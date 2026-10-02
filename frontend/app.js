const API_URL = "http://localhost:8080/api/properties";


// ==========================================
// Load Properties
// ==========================================

async function loadProperties(url = API_URL) {

    const propertyList = document.getElementById("property-list");

    propertyList.innerHTML = "<p>Loading properties...</p>";

    try {

        const response = await fetch(url);

        if (!response.ok) {
            throw new Error("Failed to load properties");
        }

        const properties = await response.json();

        displayProperties(properties);

    } catch (error) {

        console.error(error);

        propertyList.innerHTML =
            "<p>Unable to load properties. Make sure the backend is running.</p>";
    }
}


// ==========================================
// Display Properties
// ==========================================

function displayProperties(properties) {

    const propertyList = document.getElementById("property-list");

    propertyList.innerHTML = "";

    if (properties.length === 0) {

        propertyList.innerHTML =
            "<p>No properties found.</p>";

        return;
    }

    properties.forEach(property => {

        const propertyCard = document.createElement("div");

        propertyCard.className = "property-card";

        propertyCard.innerHTML = `
            <h3>${property.title}</h3>

            <p>
                <strong>Description:</strong>
                ${property.description}
            </p>

            <p>
                <strong>Location:</strong>
                ${property.location}
            </p>

            <p>
                <strong>Type:</strong>
                ${property.type}
            </p>

            <p>
                <strong>Price:</strong>
                ₹${Number(property.price).toLocaleString("en-IN")}
            </p>

            <p>
                <strong>Bedrooms:</strong>
                ${property.bedrooms}
            </p>

            <p>
                <strong>Area:</strong>
                ${property.area}
            </p>

            <p>
                <strong>Owner / Agent:</strong>
                ${property.ownerAgent || "N/A"}
            </p>

            <p>
                <strong>Status:</strong>
                ${property.status}
            </p>
        `;

        propertyList.appendChild(propertyCard);

    });
}


// ==========================================
// Add Property
// ==========================================

document
    .getElementById("property-form")
    .addEventListener("submit", async function(event) {

        event.preventDefault();

        const formMessage =
            document.getElementById("form-message");

        const property = {

            title:
                document.getElementById("title").value,

            description:
                document.getElementById("description").value,

            location:
                document.getElementById("location").value,

            type:
                document.getElementById("type").value,

            price:
                Number(document.getElementById("price").value),

            bedrooms:
                Number(document.getElementById("bedrooms").value),

            area:
                Number(document.getElementById("area").value),

            ownerAgent:
                document.getElementById("ownerAgent").value,

            status:
                document.getElementById("status").value
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

                const errorText =
                    await response.text();

                throw new Error(errorText);
            }


            const savedProperty =
                await response.json();

            console.log("Property created:", savedProperty);


            formMessage.textContent =
                "Property added successfully!";


            formMessage.style.color = "green";


            document
                .getElementById("property-form")
                .reset();


            document.getElementById("status").value =
                "AVAILABLE";


            await loadProperties();


        } catch (error) {

            console.error(error);

            formMessage.textContent =
                "Failed to add property.";

            formMessage.style.color = "red";
        }

    });


// ==========================================
// Search & Filter
// ==========================================

document
    .getElementById("search-button")
    .addEventListener("click", async function() {

        const location =
            document.getElementById("search-location").value.trim();

        const type =
            document.getElementById("search-type").value;

        const status =
            document.getElementById("search-status").value;

        const bedrooms =
            document.getElementById("search-bedrooms").value;


        const params = new URLSearchParams();


        if (location) {
            params.append("location", location);
        }

        if (type) {
            params.append("type", type);
        }

        if (status) {
            params.append("status", status);
        }

        if (bedrooms) {
            params.append("bedrooms", bedrooms);
        }


        const queryString =
            params.toString();


        const searchURL =
            queryString
                ? `${API_URL}/search?${queryString}`
                : API_URL;


        await loadProperties(searchURL);

    });


// ==========================================
// Clear Filters
// ==========================================

document
    .getElementById("clear-button")
    .addEventListener("click", async function() {

        document.getElementById("search-location").value = "";

        document.getElementById("search-type").value = "";

        document.getElementById("search-status").value = "";

        document.getElementById("search-bedrooms").value = "";


        await loadProperties(API_URL);

    });


// ==========================================
// Initial Page Load
// ==========================================

loadProperties();