document.addEventListener('DOMContentLoaded', function () {
  // Get references to DOM elements
  const searchForm = document.getElementById('searchForm');
  const facilitiesContainer = document.getElementById('facilitiesContainer');
  const loadingIndicator = document.getElementById('loading');
  const errorMessageElement = document.getElementById('errorMessage');
  const noResultsElement = document.getElementById('noResults');
  const resetButton = document.getElementById('resetButton');

  // Initialize by fetching all facilities
  fetchFacilities();

  // Add event listeners
  searchForm.addEventListener('submit', function (e) {
    e.preventDefault();
    searchFacilities();
  });

  resetButton.addEventListener('click', function () {
    // Reset form fields
    searchForm.reset();

    // Fetch all facilities again
    fetchFacilities();
  });

  /**
   * Fetch all available facilities from the API
   */
  async function fetchFacilities() {
    try {
      // Show loading state
      showLoadingState();
      const token = localStorage.getItem('accessToken');

      // Fetch facilities from the API endpoint
      const response = await fetch('/api/facilities/all', {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
          ...(token && { Authorization: `Bearer ${token}` }) // add token if exists
        }
      });

      // Check if the response is successful
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      // Parse the JSON response
      const facilities = await response.json();

      // Render the facilities to the page
      renderFacilities(facilities);

      // Hide loading state
      hideLoadingState();
    } catch (error) {
      console.error('Error fetching facilities:', error);

      // Display error message to the user
      displayErrorMessage('Failed to load facilities. Please try again later.');

      // Hide loading state
      hideLoadingState();
    }
  }

  /**
   * Search facilities based on filter criteria
   */
  async function searchFacilities() {
    try {
      // Show loading state
      showLoadingState();

      // Get search parameters
      const city = document.getElementById('city').value.trim();
      const facilityType = document.getElementById('facilityType').value.trim();
      const minPrice = document.getElementById('minPrice').value;
      const maxPrice = document.getElementById('maxPrice').value;

      // Build query parameters
      let queryParams = new URLSearchParams();
      if (city) queryParams.append('city', city);
      if (facilityType) queryParams.append('facilityType', facilityType);
      if (minPrice) queryParams.append('minPrice', minPrice);
      if (maxPrice) queryParams.append('maxPrice', maxPrice);

      // Make API call
      const response = await fetch(`/api/facilities/search?${queryParams.toString()}`);

      // Check if the response is successful
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      // Parse the JSON response
      const facilities = await response.json();

      // Render the facilities to the page
      renderFacilities(facilities);

      // Hide loading state
      hideLoadingState();
    } catch (error) {
      console.error('Error searching facilities:', error);

      // Display error message to the user
      displayErrorMessage(`Error: ${error.message}`);

      // Hide loading state
      hideLoadingState();
    }
  }

  /**
   * Render facilities to the page
   * @param {Array} facilities - The array of facility objects
   */
  function renderFacilities(facilities) {
    // Clear error and no results messages
    errorMessageElement.style.display = 'none';
    noResultsElement.style.display = 'none';

    // Clear existing content
    facilitiesContainer.innerHTML = '';

    // If no facilities are available
    if (!facilities || facilities.length === 0) {
      noResultsElement.style.display = 'block';
      return;
    }

    // Loop through each facility and create HTML elements
    facilities.forEach(facility => {
      const propertyCard = createFacilityCard(facility);
      facilitiesContainer.appendChild(propertyCard);
    });
  }

  /**
   * Create a facility card element
   * @param {Object} facility - The facility object
   * @returns {HTMLElement} - The facility card element
   */
  function createFacilityCard(facility) {
    const propertyElement = document.createElement('div');
    propertyElement.className = 'col-xl-4 col-md-6';
    propertyElement.setAttribute('data-aos', 'fade-up');
    propertyElement.setAttribute('data-aos-delay', '100');

    // Determine price text based on pricing model
    const priceText = facility.pricePerHour ?
      `$${facility.pricePerHour}/hour` :
      (facility.price ? `$${facility.price}` : 'Contact for pricing');

    propertyElement.innerHTML = `
      <div class="card">
        <img src="${facility.imageUrl || 'assets/img/properties/default.jpg'}" alt="${facility.name}" class="img-fluid">
        <div class="card-body">
          <span class="sale-rent">Book | ${priceText}</span>
          <h3><a href="property-single.html?id=${facility.facilityId}" class="stretched-link">${facility.name}</a></h3>
          <div class="card-content d-flex flex-column justify-content-center text-center">
            <div class="row propery-info">
              <div class="col">Id</div>
              <div class="col">Type</div>
              <div class="col">Per Hour</div>
              <div class="col">City</div>
            </div>
            <div class="row">
              <div class="col">${facility.facilityId || 'N/A'}</div>
              <div class="col">${facility.facilityType || 'N/A'}</div>
              <div class="col">${facility.hourlyRate || 'N/A'}</div>
              <div class="col">${facility.city || 'N/A'}</div>
            </div>
          </div>
        </div>
      </div>
    `;

    return propertyElement;
  }

  /**
   * Show loading state
   */
  function showLoadingState() {
    // Hide existing elements
    facilitiesContainer.style.display = 'none';
    errorMessageElement.style.display = 'none';
    noResultsElement.style.display = 'none';

    // Show loading indicator
    loadingIndicator.style.display = 'block';
  }

  /**
   * Hide loading state
   */
  function hideLoadingState() {
    // Hide loading indicator
    loadingIndicator.style.display = 'none';

    // Show facilities container
    facilitiesContainer.style.display = '';
  }

  /**
   * Display error message
   * @param {string} message - The error message to display
   */
  function displayErrorMessage(message) {
    // Clear existing content
    facilitiesContainer.innerHTML = '';
    facilitiesContainer.style.display = 'none';
    noResultsElement.style.display = 'none';

    // Set error message and show it
    errorMessageElement.textContent = message;
    errorMessageElement.style.display = 'block';
  }
});
