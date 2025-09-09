/**
 * Manage Facilities JavaScript
 * Handles facility creation, viewing, editing, and deletion
 */

// Configuration
 // Hard-coded user ID for demo
const API_BASE_URL = '/api/facilities';

// DOM Elements
document.addEventListener('DOMContentLoaded', async function() {
  // Tab switching helper
  window.switchToCreateTab = function() {
    document.getElementById('create-tab').click();
  };
  const userId = await getUserId();
  
  // ===== CREATE FACILITY FUNCTIONALITY =====
  const facilityForm = document.getElementById('facility-form');
  const submitBtn = document.getElementById('submit-btn');
  const loader = document.getElementById('loader');
  const successAlert = document.getElementById('success-alert');
  const createErrorAlert = document.getElementById('create-error-alert');
  const createErrorMessage = document.getElementById('create-error-message');
  
  // ===== VIEW FACILITIES FUNCTIONALITY =====
  const facilitiesContainer = document.getElementById('facilities-container');
  const loadingElement = document.getElementById('loading');
  const errorAlert = document.getElementById('error-alert');
  const errorMessageElement = document.getElementById('error-message');
  const emptyStateElement = document.getElementById('empty-state');
  const statusMessage = document.getElementById('status-message');
  
  // ===== EDIT FACILITY FUNCTIONALITY =====
  const editForm = document.getElementById('edit-form');
  const saveEditBtn = document.getElementById('save-edit-btn');
  const editFacilityModal = new bootstrap.Modal(document.getElementById('editFacilityModal'));
  
  // ===== DELETE FACILITY FUNCTIONALITY =====
  const deleteFacilityModal = new bootstrap.Modal(document.getElementById('deleteFacilityModal'));
  const confirmDeleteBtn = document.getElementById('confirm-delete-btn');
  const facilityToDeleteName = document.getElementById('facility-to-delete-name');
  
  // Store facilities data and currently selected facility
  let facilitiesData = [];
  let currentFacilityId = null;
  
  // Initialize by fetching facilities
  fetchFacilities();
  
  /**
   * Navigate to facility transactions page
   */
  window.viewTransactions = function(facilityId, facilityName) {
    // Encode the facility name for the URL
    const encodedName = encodeURIComponent(facilityName);
    // Redirect to the transactions page with facility ID and name as query parameters
    window.location.href = `facility-transactions.html?facilityId=${facilityId}&facilityName=${encodedName}`;
  };
  
  /**
   * Get CSRF token from cookie
   */
  function getCsrfToken() {
    const tokenCookie = document.cookie
      .split('; ')
      .find(cookie => cookie.startsWith('XSRF-TOKEN='));
        
    if (tokenCookie) {
      return decodeURIComponent(tokenCookie.split('=')[1]);
    }
    
    const csrfMeta = document.querySelector('meta[name="csrf-token"]');
    if (csrfMeta) {
      return csrfMeta.getAttribute('content');
    }
    
    return null;
  }
  
  /**
   * Show status message in the view facilities tab
   */
  function showStatusMessage(message, isSuccess = true) {
    statusMessage.className = isSuccess ? 'alert alert-success' : 'alert alert-danger';
    statusMessage.innerHTML = isSuccess ? 
      `<i class="bi bi-check-circle-fill me-2"></i>${message}` : 
      `<i class="bi bi-exclamation-triangle-fill me-2"></i>${message}`;
    statusMessage.style.display = 'block';
    
    // Auto-hide after 5 seconds
    setTimeout(() => {
      statusMessage.style.display = 'none';
    }, 5000);
  }
  
  /**
   * Reset create form
   */
  function resetCreateForm() {
    facilityForm.reset();
    submitBtn.disabled = false;
    loader.style.display = 'none';
    successAlert.style.display = 'none';
    createErrorAlert.style.display = 'none';
  }
  
  /**
   * Show loading state
   */
  function showLoading() {
    loadingElement.classList.remove('d-none');
    facilitiesContainer.classList.add('d-none');
    errorAlert.classList.add('d-none');
    emptyStateElement.classList.add('d-none');
    statusMessage.style.display = 'none';
  }
  
  /**
   * Show error in the view facilities tab
   */
  function showError(message) {
    loadingElement.classList.add('d-none');
    facilitiesContainer.classList.add('d-none');
    emptyStateElement.classList.add('d-none');
    
    errorAlert.classList.remove('d-none');
    errorMessageElement.textContent = message;
  }
  
  /**
   * Handle facility creation form submission
   */
  facilityForm.addEventListener('submit', async function(event) {
    event.preventDefault();
    
    // Show loading state
    submitBtn.disabled = true;
    loader.style.display = 'inline-block';
    successAlert.style.display = 'none';
    createErrorAlert.style.display = 'none';
    
    try {
      // Get form data
      const formData = new FormData(facilityForm);
      
      // Convert to object and ensure numeric fields are properly typed
      const facilityData = {
        name: formData.get('name'),
        description: formData.get('description'),
        address: formData.get('address'),
        city: formData.get('city'),
        facilityType: formData.get('facilityType'),
        hourlyRate: parseFloat(formData.get('hourlyRate')),
        ownerId: userId
      };
      
      // Get CSRF token
      const csrfToken = getCsrfToken();
      
      // Prepare headers
      const headers = {
        'Content-Type': 'application/json'
      };
      
      // Add CSRF token to headers if available
      if (csrfToken) {
        headers['X-XSRF-TOKEN'] = csrfToken;
        headers['X-CSRF-TOKEN'] = csrfToken;
      }
      
      // Send request to API
      const response = await fetch(`${API_BASE_URL}/create`, {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(facilityData),
        credentials: 'include'
      });
      
      // Handle response
      if (response.ok) {
        const result = await response.json();
        console.log('Facility created:', result);
        
        // Show success message
        successAlert.style.display = 'block';
        
        // Reset form
        facilityForm.reset();
        
        // Refresh facilities list if we're coming back to it
        fetchFacilities();
        
        // Switch to the view tab after a short delay
        setTimeout(() => {
          document.getElementById('view-tab').click();
          successAlert.style.display = 'none';
        }, 2000);
      } else {
        let errorData;
        try {
          errorData = await response.text();
        } catch (e) {
          errorData = `HTTP error! Status: ${response.status}`;
        }
        
        console.error('Error creating facility:', errorData);
        
        if (response.status === 403) {
          throw new Error('Access denied. CSRF protection might be blocking the request.');
        } else {
          throw new Error(errorData || 'Failed to create facility. Please try again.');
        }
      }
    } catch (error) {
      console.error('Submission error:', error);
      createErrorAlert.style.display = 'block';
      createErrorMessage.textContent = error.message || 'An unexpected error occurred. Please try again.';
    } finally {
      submitBtn.disabled = false;
      loader.style.display = 'none';
    }
  });
  
  /**
   * Format currency on blur for hourly rate
   */
  const hourlyRateInput = document.getElementById('hourlyRate');
  hourlyRateInput.addEventListener('blur', function() {
    if (this.value) {
      this.value = parseFloat(this.value).toFixed(2);
    }
  });
  
  /**
   * Fetch all facilities for the current user
   */
  async function fetchFacilities() {
    try {
      console.log(`Fetching facilities for user ID: ${userId}`);
      showLoading();
      
      // Construct the API URL
      const apiUrl = `${API_BASE_URL}/user_facilities?userId=${userId}`;
      
      // Make the API request
      const response = await fetch(apiUrl);
      
      // Handle error responses
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}: ${response.statusText}`);
      }
      
      // Parse the JSON response
      const facilities = await response.json();
      console.log('Received facilities:', facilities);
      
      // Store facilities data
      facilitiesData = facilities;
      
      // Display the facilities
      displayFacilities(facilities);
    } catch (error) {
      console.error('Error fetching facilities:', error);
      showError(error.message);
    }
  }
  
  /**
   * Display facilities in the view tab
   */
  function displayFacilities(facilities) {
    // Hide loading indicator
    loadingElement.classList.add('d-none');
    
    // Check if there are any facilities
    if (!facilities || facilities.length === 0) {
      emptyStateElement.classList.remove('d-none');
      return;
    }
    
    // Show facilities container
    facilitiesContainer.classList.remove('d-none');
    facilitiesContainer.innerHTML = ''; // Clear existing facilities
    
    // Generate HTML for each facility
    facilities.forEach(facility => {
      const facilityCard = document.createElement('div');
      facilityCard.className = 'col-lg-6 col-xl-4 mb-4';
      facilityCard.setAttribute('data-aos', 'fade-up');
      facilityCard.setAttribute('data-aos-delay', '100');
      
      facilityCard.innerHTML = `
        <div class="facility-card">
          <div class="facility-header">
            <div class="d-flex justify-content-between align-items-start">
              <div>
                <h3 class="fs-5 mb-1">${facility.name}</h3>
                <p class="mb-0 text-muted">${facility.facilityType}</p>
              </div>
              <span class="price-badge">${parseFloat(facility.hourlyRate).toFixed(2)}/hr</span>
            </div>
          </div>
          
          <div class="facility-content">
            <div class="mb-3">
              <div class="meta-label">Description</div>
              <p>${facility.description}</p>
            </div>
            
            <div class="facility-meta">
              <div class="facility-meta-item">
                <div class="meta-label">Address</div>
                <div>${facility.address}</div>
              </div>
              
              <div class="facility-meta-item">
                <div class="meta-label">City</div>
                <div>${facility.city}</div>
              </div>
            </div>
            
            <div class="facility-actions">
              <button class="btn btn-outline-primary btn-sm me-2" onclick="viewTransactions(${facility.facilityId}, '${facility.name.replace(/'/g, "\\'")}')">
                <i class="bi bi-cash-coin me-1"></i>Transactions
              </button>
              <button class="btn btn-outline-primary btn-sm me-2" onclick="openEditModal(${facility.facilityId})">
                <i class="bi bi-pencil me-1"></i>Edit
              </button>
              <button class="btn btn-outline-danger btn-sm" onclick="openDeleteModal(${facility.facilityId}, '${facility.name.replace(/'/g, "\\'")}')">
                <i class="bi bi-trash me-1"></i>Delete
              </button>
            </div>
          </div>
        </div>
      `;
      
      facilitiesContainer.appendChild(facilityCard);
    });
  }
  
  /**
   * Open edit modal with facility data
   */
  window.openEditModal = function(facilityId) {
    const facility = facilitiesData.find(f => f.facilityId === facilityId);
    if (!facility) return;
    
    currentFacilityId = facilityId;
    
    // Populate form fields
    document.getElementById('edit-facility-id').value = facility.facilityId;
    document.getElementById('edit-name').value = facility.name;
    document.getElementById('edit-facility-type').value = facility.facilityType;
    document.getElementById('edit-description').value = facility.description;
    document.getElementById('edit-address').value = facility.address;
    document.getElementById('edit-city').value = facility.city;
    document.getElementById('edit-hourly-rate').value = facility.hourlyRate;
    
    // Show modal
    editFacilityModal.show();
  };
  
  /**
   * Open delete confirmation modal
   */
  window.openDeleteModal = function(facilityId, facilityName) {
    currentFacilityId = facilityId;
    facilityToDeleteName.textContent = facilityName;
    deleteFacilityModal.show();
  };
  
  /**
   * Handle edit form submission
   */
  saveEditBtn.addEventListener('click', async function() {
    try {
      // Disable button and show loading state
      saveEditBtn.disabled = true;
      saveEditBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Saving...';
      
      // Get updated facility data
      const updatedFacility = {
        facilityId: parseInt(document.getElementById('edit-facility-id').value),
        name: document.getElementById('edit-name').value,
        facilityType: document.getElementById('edit-facility-type').value,
        description: document.getElementById('edit-description').value,
        address: document.getElementById('edit-address').value,
        city: document.getElementById('edit-city').value,
        hourlyRate: parseFloat(document.getElementById('edit-hourly-rate').value),
        ownerId: userId
      };
      
      // Get CSRF token
      const csrfToken = getCsrfToken();
      
      // Prepare headers
      const headers = {
        'Content-Type': 'application/json'
      };
      
      // Add CSRF token to headers if available
      if (csrfToken) {
        headers['X-XSRF-TOKEN'] = csrfToken;
        headers['X-CSRF-TOKEN'] = csrfToken;
      }
      
      // Send update request
      const response = await fetch(`${API_BASE_URL}/update?facilityId=${updatedFacility.facilityId}`, {
        method: 'PUT',
        headers: headers,
        body: JSON.stringify(updatedFacility),
        credentials: 'include'
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to update facility: ${errorText}`);
      }
      
      // Close modal
      editFacilityModal.hide();
      
      // Show success message
      showStatusMessage(`Facility "${updatedFacility.name}" has been updated successfully.`);
      
      // Refresh facilities list
      fetchFacilities();
    } catch (error) {
      console.error('Error updating facility:', error);
      
      // Show error in the view tab
      editFacilityModal.hide();
      showStatusMessage(`Error: ${error.message}`, false);
    } finally {
      // Reset button state
      saveEditBtn.disabled = false;
      saveEditBtn.innerHTML = 'Save Changes';
    }
  });
  
  /**
   * Handle facility deletion
   */
  confirmDeleteBtn.addEventListener('click', async function() {
    try {
      // Disable button and show loading state
      confirmDeleteBtn.disabled = true;
      confirmDeleteBtn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Deleting...';
      
      // Get CSRF token
      const csrfToken = getCsrfToken();
      
      // Prepare headers
      const headers = {};
      
      // Add CSRF token to headers if available
      if (csrfToken) {
        headers['X-XSRF-TOKEN'] = csrfToken;
        headers['X-CSRF-TOKEN'] = csrfToken;
      }
      
      // Send delete request
      const response = await fetch(`${API_BASE_URL}/delete?facilityId=${currentFacilityId}`, {
        method: 'DELETE',
        headers: headers,
        credentials: 'include'
      });
      
      if (!response.ok) {
        const errorText = await response.text();
        throw new Error(`Failed to delete facility: ${errorText}`);
      }
      
      // Close modal
      deleteFacilityModal.hide();
      
      // Show success message
      showStatusMessage('Facility has been deleted successfully.');
      
      // Refresh facilities list
      fetchFacilities();
    } catch (error) {
      console.error('Error deleting facility:', error);
      
      // Show error in the view tab
      deleteFacilityModal.hide();
      showStatusMessage(`Error: ${error.message}`, false);
    } finally {
      // Reset button state
      confirmDeleteBtn.disabled = false;
      confirmDeleteBtn.innerHTML = 'Delete Facility';
    }
  });
  
  // Format currency on edit hourly rate as well
  const editHourlyRate = document.getElementById('edit-hourly-rate');
  editHourlyRate.addEventListener('blur', function() {
    if (this.value) {
      this.value = parseFloat(this.value).toFixed(2);
    }
  });
});