document.addEventListener('DOMContentLoaded', function() {
    // Fetch sports facilities for the homepage carousel
    fetchFacilitiesForCarousel();
  });
  
  // Fetch facilities from API
  function fetchFacilitiesForCarousel() {
    fetch('/api/facilities/all')
      .then(response => {
        if (!response.ok) {
          throw new Error('Network response was not ok');
        }
        return response.json();
      })
      .then(facilities => {
        // Populate the carousel with the facilities data
        populateHeroCarousel(facilities);
      })
      .catch(error => {
        console.error('Error fetching facilities:', error);
        // Show default content if facilities can't be loaded
        setDefaultCarouselContent();
      });
  }
  
  // Populate the hero carousel with facilities data
  function populateHeroCarousel(facilities) {
    const carousel = document.getElementById('hero-carousel');
    if (!carousel) return;
    
    // Clear existing carousel items except navigation controls
    const carouselInner = carousel.querySelector('.carousel-inner') || carousel;
    carouselInner.innerHTML = '';
    
    // Create carousel items for each facility
    facilities.forEach((facility, index) => {
      const isActive = index === 0 ? 'active' : '';
      const carouselItem = document.createElement('div');
      carouselItem.className = `carousel-item ${isActive}`;
      
      // Format the price display
      const priceDisplay = facility.hourlyRate ? 
        `${facility.facilityType} | $${facility.hourlyRate}/hour` : 
        `${facility.facilityType}`;
      
      carouselItem.innerHTML = `
        <img src="${facility.heroImage || `assets/img/hero-carousel/hero-carousel-${(index % 3) + 1}.jpg`}" alt="${facility.name}">
        <div class="carousel-container">
          <div>
            <p>${facility.city || 'Location'}, ${facility.state || ''}</p>
            <h2><span>${facility.facilityId}</span> ${facility.name}</h2>
            <a href="property-single.html?id=${facility.facilityId}" class="btn-get-started">${priceDisplay}</a>
          </div>
        </div>
      `;
      
      carouselInner.appendChild(carouselItem);
    });
    
    // Create/update carousel indicators
    updateCarouselIndicators(carousel, facilities.length);
    
    // Reinitialize Bootstrap carousel if needed
    if (window.bootstrap && bootstrap.Carousel) {
      new bootstrap.Carousel(carousel);
    }
  }
  
  // Update carousel indicators
  function updateCarouselIndicators(carousel, count) {
    const indicatorsContainer = carousel.querySelector('.carousel-indicators');
    if (!indicatorsContainer) return;
    
    indicatorsContainer.innerHTML = '';
    
    for (let i = 0; i < count; i++) {
      const indicator = document.createElement('li');
      indicator.setAttribute('data-bs-target', '#hero-carousel');
      indicator.setAttribute('data-bs-slide-to', i.toString());
      if (i === 0) {
        indicator.classList.add('active');
      }
      indicatorsContainer.appendChild(indicator);
    }
  }
  
  // Set default content if API fails
  function setDefaultCarouselContent() {
    const carousel = document.getElementById('hero-carousel');
    if (!carousel) return;
    
    // Keep the original demo content as fallback
    console.log('Using default carousel content due to API error');
  }