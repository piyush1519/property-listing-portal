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

            <div class="property-actions">

                <button
                    type="button"
                    onclick="editProperty(${property.id})"
                >
                    Edit
                </button>

                <button
                    type="button"
                    onclick="deleteProperty(${property.id})"
                >
                    Delete
                </button>

            </div>
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
                document.getElementById("title").value.trim(),

            description:
                document.getElementById("description").value.trim(),

            location:
                document.getElementById("location").value.trim(),

            type:
                document.getElementById("type").value.trim(),

            price:
                Number(document.getElementById("price").value),

            bedrooms:
                Number(document.getElementById("bedrooms").value),

            area:
                Number(document.getElementById("area").value),

            ownerAgent:
                document.getElementById("ownerAgent").value.trim(),

            status:
                document.getElementById("status").value.trim()
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
// Edit Property
// ==========================================

async function editProperty(id) {

    try {

        const response =
            await fetch(`${API_URL}/${id}`);


        if (!response.ok) {
            throw new Error("Failed to load property");
        }


        const property =
            await response.json();


        showEditForm(property);


    } catch (error) {

        console.error(error);

        alert("Unable to load property for editing.");

    }
}


// ==========================================
// Create Edit Form
// ==========================================

function showEditForm(property) {

    let editContainer =
        document.getElementById("edit-property-container");


    if (!editContainer) {

        editContainer =
            document.createElement("section");

        editContainer.id =
            "edit-property-container";

        editContainer.className =
            "form-section";


        const main =
            document.querySelector("main");

        main.insertBefore(
            editContainer,
            document.querySelector(".property-section")
        );
    }


    editContainer.innerHTML = `

        <h2>Edit Property</h2>

        <form id="edit-property-form">

            <input
                type="text"
                id="edit-title"
                placeholder="Property title"
                value="${escapeHtml(property.title)}"
                required
            >

            <textarea
                id="edit-description"
                placeholder="Description"
                required
            >${escapeHtml(property.description)}</textarea>

            <input
                type="text"
                id="edit-location"
                placeholder="Location"
                value="${escapeHtml(property.location)}"
                required
            >

            <input
                type="text"
                id="edit-type"
                placeholder="Property type"
                value="${escapeHtml(property.type)}"
                required
            >

            <input
                type="number"
                id="edit-price"
                placeholder="Price"
                min="1"
                value="${property.price}"
                required
            >

            <input
                type="number"
                id="edit-bedrooms"
                placeholder="Bedrooms"
                min="0"
                value="${property.bedrooms}"
                required
            >

            <input
                type="number"
                id="edit-area"
                placeholder="Area"
                min="1"
                value="${property.area}"
                required
            >

            <input
                type="text"
                id="edit-ownerAgent"
                placeholder="Owner / Agent"
                value="${escapeHtml(property.ownerAgent || "")}"
            >

            <select
                id="edit-status"
                required
            >
                <option value="AVAILABLE"
                    ${property.status === "AVAILABLE" ? "selected" : ""}>
                    AVAILABLE
                </option>

                <option value="SOLD"
                    ${property.status === "SOLD" ? "selected" : ""}>
                    SOLD
                </option>

                <option value="RENTED"
                    ${property.status === "RENTED" ? "selected" : ""}>
                    RENTED
                </option>
            </select>


            <div class="filter-buttons">

                <button type="submit">
                    Update Property
                </button>

                <button
                    type="button"
                    id="cancel-edit-button"
                >
                    Cancel
                </button>

            </div>

        </form>

        <p id="edit-form-message"></p>
    `;


    document
        .getElementById("edit-property-form")
        .addEventListener(
            "submit",
            async function(event) {

                event.preventDefault();

                await updateProperty(property.id);
            }
        );


    document
        .getElementById("cancel-edit-button")
        .addEventListener(
            "click",
            function() {

                editContainer.remove();

            }
        );


    editContainer.scrollIntoView({
        behavior: "smooth"
    });
}


// ==========================================
// Update Property
// ==========================================

async function updateProperty(id) {

    const message =
        document.getElementById("edit-form-message");


    const updatedProperty = {

        title:
            document.getElementById("edit-title").value.trim(),

        description:
            document.getElementById("edit-description").value.trim(),

        location:
            document.getElementById("edit-location").value.trim(),

        type:
            document.getElementById("edit-type").value.trim(),

        price:
            Number(document.getElementById("edit-price").value),

        bedrooms:
            Number(document.getElementById("edit-bedrooms").value),

        area:
            Number(document.getElementById("edit-area").value),

        ownerAgent:
            document.getElementById("edit-ownerAgent").value.trim(),

        status:
            document.getElementById("edit-status").value
    };


    try {

        const response =
            await fetch(`${API_URL}/${id}`, {

                method: "PUT",

                headers: {
                    "Content-Type": "application/json"
                },

                body: JSON.stringify(updatedProperty)
            });


        if (!response.ok) {

            const errorText =
                await response.text();

            throw new Error(errorText);
        }


        const updated =
            await response.json();


        console.log(
            "Property updated:",
            updated
        );


        message.textContent =
            "Property updated successfully!";

        message.style.color =
            "green";


        setTimeout(async function() {

            const editContainer =
                document.getElementById(
                    "edit-property-container"
                );

            if (editContainer) {
                editContainer.remove();
            }

            await loadProperties();

        }, 700);


    } catch (error) {

        console.error(error);

        message.textContent =
            "Failed to update property.";

        message.style.color =
            "red";
    }
}


// ==========================================
// Delete Property
// ==========================================

async function deleteProperty(id) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this property?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await fetch(`${API_URL}/${id}`, {

                method: "DELETE"
            });


        if (!response.ok) {

            throw new Error(
                "Failed to delete property"
            );
        }


        alert(
            "Property deleted successfully."
        );


        await loadProperties();


    } catch (error) {

        console.error(error);

        alert(
            "Unable to delete property."
        );
    }
}


// ==========================================
// Escape HTML
// ==========================================

function escapeHtml(value) {

    if (value === null || value === undefined) {
        return "";
    }

    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


// ==========================================
// Search & Filter
// ==========================================

document
    .getElementById("search-button")
    .addEventListener("click", async function() {

        const location =
            document
                .getElementById("search-location")
                .value
                .trim();

        const type =
            document.getElementById("search-type").value;

        const status =
            document.getElementById("search-status").value;

        const bedrooms =
            document.getElementById("search-bedrooms").value;


        const params =
            new URLSearchParams();


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