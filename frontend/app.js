const API_URL = "http://localhost:8080/api/properties";

const propertyForm = document.getElementById("property-form");
const propertyList = document.getElementById("property-list");

const locationFilter = document.getElementById("search-location");
const typeFilter = document.getElementById("search-type");
const statusFilter = document.getElementById("search-status");
const bedroomsFilter = document.getElementById("search-bedrooms");

const searchButton = document.getElementById("search-button");
const clearFiltersButton = document.getElementById("clear-button");

const propertyDetailsContainer = document.getElementById(
    "property-details-container"
);

const propertyDetails = document.getElementById("property-details");

const closeDetailsButton = document.getElementById(
    "close-details-button"
);


// =====================================================
// DASHBOARD ELEMENTS
// =====================================================

const totalPropertiesElement =
    document.getElementById("total-properties");

const availablePropertiesElement =
    document.getElementById("available-properties");

const soldPropertiesElement =
    document.getElementById("sold-properties");

const rentedPropertiesElement =
    document.getElementById("rented-properties");


// =====================================================
// LOAD ALL PROPERTIES
// =====================================================

async function loadProperties() {

    try {

        const response = await fetch(API_URL);

        if (!response.ok) {
            throw new Error("Failed to load properties");
        }

        const properties = await response.json();

        displayProperties(properties);

        updateDashboard(properties);

    } catch (error) {

        console.error(error);

        propertyList.innerHTML = `
            <p class="error-message">
                Unable to load properties.
                Make sure the backend is running.
            </p>
        `;
    }
}


// =====================================================
// UPDATE DASHBOARD
// =====================================================

function updateDashboard(properties) {

    const total =
        properties.length;


    const available =
        properties.filter(
            property =>
                property.status &&
                property.status.toUpperCase() === "AVAILABLE"
        ).length;


    const sold =
        properties.filter(
            property =>
                property.status &&
                property.status.toUpperCase() === "SOLD"
        ).length;


    const rented =
        properties.filter(
            property =>
                property.status &&
                property.status.toUpperCase() === "RENTED"
        ).length;


    totalPropertiesElement.textContent =
        total;

    availablePropertiesElement.textContent =
        available;

    soldPropertiesElement.textContent =
        sold;

    rentedPropertiesElement.textContent =
        rented;
}


// =====================================================
// DISPLAY PROPERTIES
// =====================================================

function displayProperties(properties) {

    propertyList.innerHTML = "";

    if (properties.length === 0) {

        propertyList.innerHTML = `
            <p>No properties found.</p>
        `;

        return;
    }


    properties.forEach(property => {

        const card = document.createElement("div");

        card.className = "property-card";


        card.innerHTML = `

            <h3>
                ${escapeHtml(property.title)}
            </h3>

            <p>
                <strong>Location:</strong>
                ${escapeHtml(property.location)}
            </p>

            <p>
                <strong>Type:</strong>
                ${escapeHtml(property.type)}
            </p>

            <p>
                <strong>Price:</strong>
                ₹${Number(property.price).toLocaleString("en-IN")}
            </p>

            <p>
                <strong>Bedrooms:</strong>
                ${property.bedrooms ?? "N/A"}
            </p>

            <p>
                <strong>Area:</strong>
                ${property.area ?? "N/A"} sq.ft
            </p>

            <p>
                <strong>Status:</strong>
                ${escapeHtml(property.status)}
            </p>

            <div class="property-actions">

                <button
                    type="button"
                    class="view-details-button"
                    data-id="${property.id}">
                    View Details
                </button>

                <button
                    type="button"
                    class="edit-button"
                    data-id="${property.id}">
                    Edit
                </button>

                <button
                    type="button"
                    class="delete-button"
                    data-id="${property.id}">
                    Delete
                </button>

            </div>
        `;


        propertyList.appendChild(card);
    });


    addPropertyButtonListeners();
}


// =====================================================
// PROPERTY BUTTON LISTENERS
// =====================================================

function addPropertyButtonListeners() {


    document
        .querySelectorAll(".view-details-button")
        .forEach(button => {

            button.addEventListener("click", () => {

                const propertyId =
                    button.dataset.id;

                viewPropertyDetails(propertyId);
            });
        });


    document
        .querySelectorAll(".edit-button")
        .forEach(button => {

            button.addEventListener("click", () => {

                const propertyId =
                    button.dataset.id;

                editProperty(propertyId);
            });
        });


    document
        .querySelectorAll(".delete-button")
        .forEach(button => {

            button.addEventListener("click", () => {

                const propertyId =
                    button.dataset.id;

                deleteProperty(propertyId);
            });
        });
}


// =====================================================
// VIEW PROPERTY DETAILS
// =====================================================

async function viewPropertyDetails(propertyId) {

    try {

        const response =
            await fetch(
                `${API_URL}/${propertyId}`
            );


        if (!response.ok) {

            throw new Error(
                "Property not found"
            );
        }


        const property =
            await response.json();


        propertyDetails.innerHTML = `

            <div class="details-card">

                <h3>
                    ${escapeHtml(property.title)}
                </h3>

                <p>
                    <strong>Property ID:</strong>
                    ${property.id}
                </p>

                <p>
                    <strong>Description:</strong>
                    ${escapeHtml(property.description)}
                </p>

                <p>
                    <strong>Location:</strong>
                    ${escapeHtml(property.location)}
                </p>

                <p>
                    <strong>Type:</strong>
                    ${escapeHtml(property.type)}
                </p>

                <p>
                    <strong>Price:</strong>
                    ₹${Number(property.price).toLocaleString("en-IN")}
                </p>

                <p>
                    <strong>Bedrooms:</strong>
                    ${property.bedrooms ?? "N/A"}
                </p>

                <p>
                    <strong>Area:</strong>
                    ${property.area ?? "N/A"} sq.ft
                </p>

                <p>
                    <strong>Owner / Agent:</strong>
                    ${escapeHtml(
                        property.ownerAgent || "N/A"
                    )}
                </p>

                <p>
                    <strong>Status:</strong>
                    ${escapeHtml(property.status)}
                </p>

                <p>
                    <strong>Created:</strong>
                    ${formatDate(property.createdAt)}
                </p>

                <p>
                    <strong>Last Updated:</strong>
                    ${formatDate(property.updatedAt)}
                </p>

            </div>
        `;


        propertyDetailsContainer.style.display =
            "block";


        propertyDetailsContainer.scrollIntoView({
            behavior: "smooth"
        });


    } catch (error) {

        console.error(error);

        alert(
            "Unable to load property details."
        );
    }
}


// =====================================================
// CLOSE PROPERTY DETAILS
// =====================================================

closeDetailsButton.addEventListener(
    "click",
    () => {

        propertyDetailsContainer.style.display =
            "none";

        propertyDetails.innerHTML = "";
    }
);


// =====================================================
// ADD PROPERTY
// =====================================================

propertyForm.addEventListener(
    "submit",
    async function(event) {

        event.preventDefault();


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
                Number(
                    document.getElementById("price").value
                ),

            bedrooms:
                Number(
                    document.getElementById("bedrooms").value
                ),

            area:
                Number(
                    document.getElementById("area").value
                ),

            ownerAgent:
                document.getElementById("ownerAgent").value,

            status:
                document.getElementById("status").value
        };


        try {

            const response =
                await fetch(
                    API_URL,
                    {
                        method: "POST",

                        headers: {
                            "Content-Type":
                                "application/json"
                        },

                        body:
                            JSON.stringify(property)
                    }
                );


            if (!response.ok) {

                const errorText =
                    await response.text();

                throw new Error(errorText);
            }


            alert(
                "Property added successfully!"
            );


            propertyForm.reset();


            await loadProperties();


        } catch (error) {

            console.error(error);

            alert(
                "Failed to add property."
            );
        }
    }
);


// =====================================================
// SEARCH / FILTER
// =====================================================

searchButton.addEventListener(
    "click",
    async function() {

        const location =
            locationFilter.value.trim();

        const type =
            typeFilter.value;

        const status =
            statusFilter.value;

        const bedrooms =
            bedroomsFilter.value;


        const params =
            new URLSearchParams();


        if (location) {

            params.append(
                "location",
                location
            );
        }


        if (type) {

            params.append(
                "type",
                type
            );
        }


        if (status) {

            params.append(
                "status",
                status
            );
        }


        if (bedrooms) {

            params.append(
                "bedrooms",
                bedrooms
            );
        }


        try {

            const response =
                await fetch(
                    `${API_URL}/search?${params.toString()}`
                );


            if (!response.ok) {

                throw new Error(
                    "Search failed"
                );
            }


            const properties =
                await response.json();


            displayProperties(properties);


        } catch (error) {

            console.error(error);

            alert(
                "Search failed."
            );
        }
    }
);


// =====================================================
// CLEAR FILTERS
// =====================================================

clearFiltersButton.addEventListener(
    "click",
    function() {

        locationFilter.value = "";

        typeFilter.value = "";

        statusFilter.value = "";

        bedroomsFilter.value = "";

        loadProperties();
    }
);


// =====================================================
// EDIT PROPERTY
// =====================================================

async function editProperty(propertyId) {

    try {

        const response =
            await fetch(
                `${API_URL}/${propertyId}`
            );


        if (!response.ok) {

            throw new Error(
                "Property not found"
            );
        }


        const property =
            await response.json();


        const newTitle =
            prompt(
                "Enter property title:",
                property.title
            );


        if (newTitle === null) {
            return;
        }


        const newPrice =
            prompt(
                "Enter property price:",
                property.price
            );


        if (newPrice === null) {
            return;
        }


        const newStatus =
            prompt(
                "Enter status (AVAILABLE, SOLD, RENTED):",
                property.status
            );


        if (newStatus === null) {
            return;
        }


        const updatedProperty = {

            title:
                newTitle,

            description:
                property.description,

            location:
                property.location,

            type:
                property.type,

            price:
                Number(newPrice),

            bedrooms:
                property.bedrooms,

            area:
                property.area,

            ownerAgent:
                property.ownerAgent,

            status:
                newStatus.toUpperCase()
        };


        const updateResponse =
            await fetch(
                `${API_URL}/${propertyId}`,
                {
                    method: "PUT",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body:
                        JSON.stringify(
                            updatedProperty
                        )
                }
            );


        if (!updateResponse.ok) {

            throw new Error(
                "Update failed"
            );
        }


        alert(
            "Property updated successfully!"
        );


        await loadProperties();


    } catch (error) {

        console.error(error);

        alert(
            "Failed to update property."
        );
    }
}


// =====================================================
// DELETE PROPERTY
// =====================================================

async function deleteProperty(propertyId) {

    const confirmed =
        confirm(
            "Are you sure you want to delete this property?"
        );


    if (!confirmed) {
        return;
    }


    try {

        const response =
            await fetch(
                `${API_URL}/${propertyId}`,
                {
                    method: "DELETE"
                }
            );


        if (!response.ok) {

            throw new Error(
                "Delete failed"
            );
        }


        alert(
            "Property deleted successfully!"
        );


        await loadProperties();


    } catch (error) {

        console.error(error);

        alert(
            "Failed to delete property."
        );
    }
}


// =====================================================
// HELPER FUNCTIONS
// =====================================================

function formatDate(dateValue) {

    if (!dateValue) {
        return "N/A";
    }


    return new Date(
        dateValue
    ).toLocaleString("en-IN");
}


function escapeHtml(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }


    return String(value)

        .replace(
            /&/g,
            "&amp;"
        )

        .replace(
            /</g,
            "&lt;"
        )

        .replace(
            />/g,
            "&gt;"
        )

        .replace(
            /"/g,
            "&quot;"
        )

        .replace(
            /'/g,
            "&#039;"
        );
}


// =====================================================
// INITIAL LOAD
// =====================================================

loadProperties();