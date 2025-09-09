document.addEventListener('DOMContentLoaded', async function() {
    // Get facility ID from URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const facilityId = urlParams.get('id') || '1'; // Default to ID 1 if not specified
    
    // Current user ID (in a real app, this would come from auth system)
    // For demo purposes, we'll hardcode user ID 2
    // const currentUserId = 2;
    const currentUserId = await getUserId(); // Function to get the current user ID from auth system
    alert(currentUserId) // Hardcoded for demo
    // Fetch facility details
    getFacilityDetails(facilityId);
    
    // Fetch facility reviews
    getFacilityReviews(facilityId);
    
    // Set up review form submission
    setupReviewForm(facilityId, currentUserId);
  });
  
  // Fetch facility details from API
  function getFacilityDetails(facilityId) {
    fetch(`/api/facility_details/get_details?id=${facilityId}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(data => {
        // Populate the portfolio description section
        populatePortfolioDescription(data);
        
        // Populate the facility info section
        populateFacilityInfo(data);
      })
      .catch(error => {
        console.error('Error fetching facility details:', error);
        // Show error message on the page
        document.getElementById('portfolio-description').innerHTML = `
          <div class="alert alert-danger">
            <h4>Error loading facility details</h4>
            <p>Please try again later.</p>
          </div>
        `;
      });
  }
  
  // Populate the portfolio description section with facility data
  function populatePortfolioDescription(facility) {
    const portfolioDescription = document.getElementById('portfolio-description');
    if (portfolioDescription) {
      portfolioDescription.innerHTML = `
        <h2>${facility.name}</h2>
        <p>${facility.description}</p>
        
        <div class="facility-features mt-4">
          <h4>Facility Features</h4>
          <p>${facility.features || 'No additional features listed.'}</p>
        </div>
        
        <div class="facility-rules mt-4">
          <h4>Facility Rules</h4>
          <p>${facility.rules || 'Please contact for specific rules and regulations.'}</p>
        </div>
        
        <div class="facility-availability mt-4">
          <h4>Availability</h4>
          <p>${facility.availability || 'Contact for availability details.'}</p>
        </div>
        
        ${facility.agent ? `
          <div class="testimonial-item mt-4">
            <p>
              <span>${facility.agent.bio || 'Contact me for more details about this facility.'}</span>
            </p>
            <div>
              <img src="${facility.agent.photo || 'assets/img/testimonials/testimonials-2.jpg'}" class="testimonial-img" alt="${facility.agent.name}">
              <h3>${facility.agent.name}</h3>
              <h4>Facility Manager</h4>
            </div>
          </div>
        ` : ''}
      `;
    }

    // Find all elements with id 'facility_name' and update their content
    const facilityNameElements = document.querySelectorAll('#facility_name');
    facilityNameElements.forEach(element => {
        element.textContent = facility.name;
    });
  }
  
  // Populate the facility info sidebar with data
  function populateFacilityInfo(facility) {
    const portfolioInfo = document.getElementById('portfolio-info');
    if (portfolioInfo) {
      portfolioInfo.innerHTML = `
        <h3>Quick Summary</h3>
        <ul>
          <li><strong>Name:</strong> ${facility.name}</li>
          <li><strong>Description:</strong> ${facility.description?.substring(0, 50)}${facility.description?.length > 50 ? '...' : ''}</li>
          <li><strong>Address:</strong> ${facility.address}</li>
          <li><strong>City:</strong> ${facility.city}</li>
          <li><strong>Facility Type:</strong> ${facility.facilityType}</li>
          <li><strong>Hourly Rate:</strong> $${facility.hourlyRate}</li>
          <li><strong>Average Rating:</strong> <span class="facility-rating">${facility.averageRating ? facility.averageRating.toFixed(1) + '/5' : 'No ratings yet'}</span></li>
        </ul>
        <div class="mt-4">
        <a href="booking.html?id=${facility.facilityId}" class="btn btn-primary w-100">Book Now</a>
      </div>
      `;
    }
  }
  
  // Fetch facility reviews
  function getFacilityReviews(facilityId) {
    fetch(`/api/facility_details/get_reviews?facilityId=${facilityId}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(reviews => {
        displayReviews(reviews);
      })
      .catch(error => {
        console.error('Error fetching facility reviews:', error);
        document.getElementById('reviews-container').innerHTML = `
          <div class="alert alert-warning">
            <p>Unable to load reviews at this time.</p>
          </div>
        `;
      });
  }
  
  // Display reviews in the reviews section
  async function displayReviews(reviews) {
    const reviewsContainer = document.getElementById('reviews-container');
    if (!reviewsContainer) return;
    
    // Get current user ID
    const currentUserId = await getUserId(); // Function to get the current user ID from auth system
    // alert(currentUserId) // Hardcoded for demo
    
    if (reviews.length === 0) {
      reviewsContainer.innerHTML = '<p class="text-center">No reviews yet. Be the first to leave a review!</p>';
      return;
    }
    
    let reviewsHTML = '';
    
    reviews.forEach(review => {
      const reviewDate = new Date(review.createdAt).toLocaleDateString();
      const stars = '★'.repeat(review.rating) + '☆'.repeat(5 - review.rating);
      
      // Check if this review belongs to the current user
      
      const isOwnReview = (review.userId == currentUserId);
      alert(isOwnReview)
      reviewsHTML += `
        <div class="review-item" data-review-id="${review.reviewId}">
          <div class="review-header d-flex justify-content-between align-items-center">
            <div>
              <h5>${review.userName}</h5>
              <div class="review-rating">${stars} <span class="small">(${review.rating}/5)</span></div>
              <div class="review-date small text-muted">${reviewDate}</div>
            </div>
            ${isOwnReview ? `
              <button class="btn btn-sm btn-outline-danger delete-review-btn" 
                      data-review-id="${review.reviewId}">
                <i class="bi bi-trash"></i>
              </button>
            ` : ''}
          </div>
          <div class="review-content mt-2">
            <p>${review.comment}</p>
          </div>
          <hr>
        </div>
      `;
    });
    
    reviewsContainer.innerHTML = reviewsHTML;
    
    // Add event listeners to delete buttons
    document.querySelectorAll('.delete-review-btn').forEach(button => {
      button.addEventListener('click', function() {
        const reviewId = this.getAttribute('data-review-id');
        deleteReview(reviewId, currentUserId);
      });
    });
  }
  
  // Set up the review form submission
  function setupReviewForm(facilityId, userId) {
    const reviewForm = document.getElementById('review-form');
    if (!reviewForm) return;
    
    reviewForm.addEventListener('submit', function(e) {
      e.preventDefault();
      
      const rating = document.querySelector('input[name="rating"]:checked')?.value;
      const comment = document.getElementById('review-comment').value;
      
      if (!rating) {
        showAlert('Please select a rating', 'warning');
        return;
      }
      
      submitReview(facilityId, userId, parseInt(rating), comment);
    });
  }
  
  // Submit a new review to the API
  function submitReview(facilityId, userId, rating, comment) {
    // Show loading state
    const submitBtn = document.getElementById('submit-review-btn');
    const originalBtnText = submitBtn.innerHTML;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Submitting...';
    submitBtn.disabled = true;
    
    const reviewData = {
      facility_id: facilityId,
      user_id: userId,
      rating: rating,
      comment: comment
    };
    
    fetch('/api/facility_details/create_review', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(reviewData)
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // Reset the form
      document.getElementById('review-form').reset();
      
      // Show success message
      showAlert('Your review has been submitted successfully!', 'success');
      
      // Refresh the reviews
      getFacilityReviews(facilityId);
      
      // Also refresh facility details to update average rating
      getFacilityDetails(facilityId);
    })
    .catch(error => {
      console.error('Error submitting review:', error);
      showAlert('Unable to submit your review. Please try again.', 'danger');
    })
    .finally(() => {
      // Reset button
      submitBtn.innerHTML = originalBtnText;
      submitBtn.disabled = false;
    });
  }
  
  // Delete a review
  function deleteReview(reviewId, userId) {
    if (!confirm('Are you sure you want to delete your review?')) {
      return;
    }
    
    fetch(`/api/facility_details/delete_review?reviewId=${reviewId}&userId=${userId}`, {
      method: 'DELETE'
    })
    .then(response => {
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      return response.json();
    })
    .then(data => {
      // Show success message
      showAlert('Your review has been deleted successfully!', 'success');
      
      // Remove the review from DOM
      const reviewElement = document.querySelector(`[data-review-id="${reviewId}"]`);
      if (reviewElement) {
        reviewElement.remove();
      }
      
      // Get the facility ID from URL to refresh average rating
      const urlParams = new URLSearchParams(window.location.search);
      const facilityId = urlParams.get('id') || '1';
      
      // Refresh facility details to update average rating
      getFacilityDetails(facilityId);
    })
    .catch(error => {
      console.error('Error deleting review:', error);
      showAlert('Unable to delete your review. Please try again.', 'danger');
    });
  }
  
  // Utility function to show alerts
  function showAlert(message, type = 'info') {
    const alertsContainer = document.getElementById('alerts-container');
    if (!alertsContainer) return;
    
    const alertId = 'alert-' + Date.now();
    const alertHTML = `
      <div id="${alertId}" class="alert alert-${type} alert-dismissible fade show" role="alert">
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
      </div>
    `;
    
    alertsContainer.innerHTML += alertHTML;
    
    // Auto-dismiss after 5 seconds
    setTimeout(() => {
      const alertElement = document.getElementById(alertId);
      if (alertElement) {
        alertElement.classList.remove('show');
        setTimeout(() => alertElement.remove(), 150);
      }
    }, 5000);
  }