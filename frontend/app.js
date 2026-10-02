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

loadProperties();