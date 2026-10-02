const API_URL = "http://localhost:8080/api/properties";

async function loadProperties() {

    const propertyList = document.getElementById("property-list");

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to load properties");
        }

        const properties = await response.json();

        if (properties.length === 0) {
            propertyList.innerHTML = "<p>No properties available.</p>";
            return;
        }

        propertyList.innerHTML = properties.map(property => `
            <div class="property-card">

                <h3>${property.title}</h3>

                <p>${property.description}</p>

                <p>
                    <strong>Location:</strong>
                    ${property.location}
                </p>

                <p>
                    <strong>Type:</strong>
                    ${property.type}
                </p>

                <p>
                    <strong>Bedrooms:</strong>
                    ${property.bedrooms}
                </p>

                <p>
                    <strong>Area:</strong>
                    ${property.area}
                </p>

                <p class="price">
                    Price: ₹${property.price}
                </p>

                <p>
                    <strong>Owner / Agent:</strong>
                    ${property.ownerAgent || "N/A"}
                </p>

                <p>
                    <strong>Status:</strong>
                    ${property.status}
                </p>

            </div>
        `).join("");

    } catch (error) {

        console.error(error);

        propertyList.innerHTML =
            "<p>Unable to load properties. Please make sure the backend is running.</p>";
    }
}


document
    .getElementById("property-form")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const formMessage = document.getElementById("form-message");

        const property = {
            title: document.getElementById("title").value,
            description: document.getElementById("description").value,
            location: document.getElementById("location").value,
            type: document.getElementById("type").value,
            price: Number(document.getElementById("price").value),
            bedrooms: Number(document.getElementById("bedrooms").value),
            area: Number(document.getElementById("area").value),
            ownerAgent: document.getElementById("ownerAgent").value,
            status: document.getElementById("status").value
        };

        try {

            const response = await fetch(API_URL, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(property)
            });

            if (!response.ok) {

                const errorText = await response.text();

                throw new Error(errorText || "Failed to create property");
            }

            formMessage.textContent = "Property added successfully.";

            document.getElementById("property-form").reset();

            document.getElementById("status").value = "AVAILABLE";

            await loadProperties();

        } catch (error) {

            console.error(error);

            formMessage.textContent =
                "Unable to add property. Please check the entered information.";
        }
    });


loadProperties();