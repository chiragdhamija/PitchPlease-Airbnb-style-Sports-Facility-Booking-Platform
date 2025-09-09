document.addEventListener('DOMContentLoaded', async function () {
    // Get facility ID from URL parameter
    const urlParams = new URLSearchParams(window.location.search);
    const facilityId = urlParams.get('id') || '1'; // Default to ID 1 if not specified

    // Current user ID (in a real app, this would come from auth system)
    // For demo purposes, we'll hardcode user ID 2
    const currentUserId = await getUserId();

    // Fetch facility details
    getFacilityDetails(facilityId);

    // Initialize date picker
    initDatePicker();

    // Set up event listeners
    document.getElementById('proceedToCheckout').addEventListener('click',async function () {
        await proceedToCheckout();
    });
});
var facility_name = "";
// Initialize Flatpickr date picker
function initDatePicker() {
    const today = new Date();

    flatpickr("#booking-date", {
        minDate: "today",
        maxDate: new Date().fp_incr(90), // Allow bookings up to 90 days in advance
        dateFormat: "Y-m-d",
        onChange: function (selectedDates, dateStr) {
            if (selectedDates.length > 0) {
                const selectedDate = dateStr;
                fetchAvailableTimeSlots(selectedDate);
            }
        }
    });
}

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
            facility_name = data.name;
            populateFacilityInfo(data);
            updateFacilityReferences(data);
        })
        .catch(error => {
            console.error('Error fetching facility details:', error);
            showAlert('Error loading facility details. Please try again later.', 'danger');
            document.getElementById('facility-info').innerHTML = `
                <div class="alert alert-danger">
                    <h4>Error loading facility details</h4>
                    <p>Please try again later.</p>
                </div>
            `;
        });
}

// Populate the facility info sidebar with data
function populateFacilityInfo(facility) {
    const facilityInfo = document.getElementById('facility-info');
    if (facilityInfo) {
        facilityInfo.innerHTML = `
            <h4>${facility.name}</h4>
            <p>${facility.description?.substring(0, 100)}${facility.description?.length > 100 ? '...' : ''}</p>
            <hr>
            <ul class="list-unstyled">
                <li><i class="bi bi-geo-alt me-2"></i> ${facility.address}, ${facility.city}</li>
                <li><i class="bi bi-building me-2"></i> ${facility.facilityType}</li>
                <li><i class="bi bi-currency-dollar me-2"></i> <strong>Hourly Rate:</strong> $${facility.hourlyRate}</li>
                <li><i class="bi bi-star-fill me-2"></i> <strong>Rating:</strong> <span class="facility-rating">${facility.averageRating ? facility.averageRating.toFixed(1) + '/5' : 'No ratings yet'}</span></li>
            </ul>
            ${facility.agent ? `
                <hr>
                <div class="d-flex align-items-center">
                    <img src="${facility.agent.photo || 'assets/img/testimonials/testimonials-2.jpg'}" class="rounded-circle me-2" width="40" alt="${facility.agent.name}">
                    <div>
                        <strong>${facility.agent.name}</strong><br>
                        <small>Facility Manager</small>
                    </div>
                </div>
            ` : ''}
        `;

        // Store the hourly rate as a data attribute for easy access
        document.getElementById('facility-info').setAttribute('data-hourly-rate', facility.hourlyRate);
    }
}

// Update facility references in the page
function updateFacilityReferences(facility) {
    // Update the title
    document.title = `Book ${facility.name} - EstateAgency`;

    // Update facility name in intro text
    document.getElementById('facility-name-title').textContent = facility.name;

    // Update the breadcrumbs link
    const facilityPageLink = document.getElementById('facility-page-link');
    facilityPageLink.textContent = facility.name;
    facilityPageLink.href = `property-single.html?id=${facility.id}`;
}

// Fetch available time slots for a specific date
function fetchAvailableTimeSlots(date) {
    const facilityId = new URLSearchParams(window.location.search).get('id') || '1';

    // Show loading state
    document.getElementById('time-slots-grid').innerHTML = `
        <div class="col-12 text-center py-5">
            <div class="spinner-border" role="status">
                <span class="visually-hidden">Loading available slots...</span>
            </div>
            <p class="mt-2">Loading available time slots...</p>
        </div>
    `;

    // Show the time slot container
    document.getElementById('timeSlotContainer').style.display = 'block';

    // Hide booking summary if it was shown
    document.getElementById('bookingSummary').style.display = 'none';

    // In a real app, you would fetch the booked slots from the API
    // For demo purposes, we'll simulate a response with some random slots marked as booked
    fetch(`/api/bookings/available_slots?facilityId=${facilityId}&date=${date}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            renderTimeSlots(date, data.availableSlots);
        })
        .catch(error => {
            console.error('Error fetching available time slots:', error);

            // For demo, let's simulate a response
            console.log('Using simulated available slots data');

            // Generate random booked slots
            const bookedSlots = [];
            for (let i = 0; i < 6; i++) { // Randomly book 6 slots
                const randomHour = Math.floor(Math.random() * 24);
                bookedSlots.push(randomHour);
            }

            const availableSlots = [];
            for (let hour = 0; hour < 24; hour++) {
                availableSlots.push({
                    startHour: hour,
                    endHour: hour + 1,
                    available: !bookedSlots.includes(hour)
                });
            }
            //  HARDCODED SHAILENDER STEKKD
            // A sample for available slots is given below
            // [
            //     { startHour: 0, endHour: 1, available: true },
            //     { startHour: 1, endHour: 2, available: true },
            //     { startHour: 2, endHour: 3, available: false }, // This slot is booked
            //     { startHour: 3, endHour: 4, available: true },
            //     { startHour: 4, endHour: 5, available: true },
            //     { startHour: 5, endHour: 6, available: false }, // This slot is booked
            //     { startHour: 6, endHour: 7, available: true },
            //     { startHour: 7, endHour: 8, available: true },
            //     { startHour: 8, endHour: 9, available: false }, // This slot is booked
            //     { startHour: 9, endHour: 10, available: true },
            //     { startHour: 10, endHour: 11, available: true },
            //     { startHour: 11, endHour: 12, available: false }, // This slot is booked
            //     { startHour: 12, endHour: 13, available: true },
            //     { startHour: 13, endHour: 14, available: true },
            //     { startHour: 14, endHour: 15, available: true },
            //     { startHour: 15, endHour: 16, available: false }, // This slot is booked
            //     { startHour: 16, endHour: 17, available: true },
            //     { startHour: 17, endHour: 18, available: true },
            //     { startHour: 18, endHour: 19, available: true },
            //     { startHour: 19, endHour: 20, available: false }, // This slot is booked
            //     { startHour: 20, endHour: 21, available: true },
            //     { startHour: 21, endHour: 22, available: true },
            //     { startHour: 22, endHour: 23, available: true },
            //     { startHour: 23, endHour: 24, available: true }
            //   ]
            console.log("l162 availableSlots", availableSlots);
            renderTimeSlots(date, availableSlots);
        });
}

// Render time slots grid
function renderTimeSlots(selectedDate, availableSlots) {
    const timeSlotGrid = document.getElementById('time-slots-grid');
    timeSlotGrid.innerHTML = '';

    // Create time slot elements
    for (let i = 0; i < availableSlots.length; i++) {
        const slot = availableSlots[i];
        const startHour = slot.startHour;
        const endHour = slot.endHour;

        // Format the display time (12-hour format with AM/PM)
        const startTime = formatTime(startHour);
        const endTime = formatTime(endHour);

        // Create column element
        const colElement = document.createElement('div');
        colElement.className = 'col-md-6';

        // Create the time slot element
        const slotElement = document.createElement('div');
        slotElement.className = `booking-time-slot ${slot.available ? '' : 'disabled'}`;
        slotElement.setAttribute('data-start-hour', startHour);
        slotElement.setAttribute('data-end-hour', endHour);
        slotElement.setAttribute('data-date', selectedDate);

        slotElement.innerHTML = `
            <div class="d-flex justify-content-between align-items-center">
                <span>${startTime} - ${endTime}</span>
                <span class="badge ${slot.available ? 'bg-success' : 'bg-secondary'}">
                    ${slot.available ? 'Available' : 'Booked'}
                </span>
            </div>
        `;

        // Add click handler for available slots
        if (slot.available) {
            slotElement.addEventListener('click', function () {
                toggleTimeSlotSelection(this);
                updateBookingSummary();
            });
        }

        colElement.appendChild(slotElement);
        timeSlotGrid.appendChild(colElement);
    }
}

// Format hour to 12-hour time format with AM/PM
function formatTime(hour) {
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12; // Convert 0 to 12 for 12 AM
    return `${displayHour}:00 ${ampm}`;
}

// Toggle time slot selection
function toggleTimeSlotSelection(slotElement) {
    slotElement.classList.toggle('selected');
}

// Update booking summary based on selected time slots
function updateBookingSummary() {
    const selectedSlots = document.querySelectorAll('.booking-time-slot.selected');

    // Only show summary if at least one slot is selected
    if (selectedSlots.length > 0) {
        // Get selected date from first slot
        const selectedDate = selectedSlots[0].getAttribute('data-date');
        const formattedDate = new Date(selectedDate).toLocaleDateString('en-US', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });

        // Set date in summary
        document.getElementById('summary-date').textContent = formattedDate;

        // Get facility name
        const facilityName = document.getElementById('facility-name-title').textContent;
        document.getElementById('summary-facility').textContent = facilityName;

        // Get hourly rate
        const hourlyRate = parseFloat(document.getElementById('facility-info').getAttribute('data-hourly-rate'));
        document.getElementById('summary-hourly-rate').textContent = hourlyRate.toFixed(2);

        // Calculate hours and total cost
        const hours = selectedSlots.length;
        const totalCost = hours * hourlyRate;

        document.getElementById('summary-hours').textContent = hours;
        document.getElementById('summary-total').textContent = totalCost.toFixed(2);

        // Create list of selected time slots
        const timeSlotsList = document.getElementById('summary-time-slots');
        timeSlotsList.innerHTML = '';

        selectedSlots.forEach(slot => {
            const startHour = parseInt(slot.getAttribute('data-start-hour'));
            const endHour = parseInt(slot.getAttribute('data-end-hour'));
            const startTime = formatTime(startHour);
            const endTime = formatTime(endHour);

            const listItem = document.createElement('li');
            listItem.textContent = `${startTime} - ${endTime}`;
            timeSlotsList.appendChild(listItem);
        });

        // Show booking summary
        document.getElementById('bookingSummary').style.display = 'block';
    } else {
        // Hide booking summary if no slots selected
        document.getElementById('bookingSummary').style.display = 'none';
    }
}

// SHAILENDER STEKKD - LOTS OF HARDCODED STUFF - USERID, FACILITYID
// Process checkout
async function proceedToCheckout() {
    const selectedSlots = document.querySelectorAll('.booking-time-slot.selected');

    if (selectedSlots.length === 0) {
        showAlert('Please select at least one time slot to book', 'warning');
        return;
    }

    const facilityId = new URLSearchParams(window.location.search).get('id') || '1';
    const selectedDate = selectedSlots[0].getAttribute('data-date');

    // Collect booking data
    const timeSlots = [];
    selectedSlots.forEach(slot => {
        timeSlots.push({
            startHour: parseInt(slot.getAttribute('data-start-hour')),
            endHour: parseInt(slot.getAttribute('data-end-hour')),
            date: selectedDate
        });
    });

    const hourlyRate = parseFloat(document.getElementById('facility-info').getAttribute('data-hourly-rate'));
    const hours = selectedSlots.length;
    const totalAmount = hours * hourlyRate;

    const bookingData = {
        facilityId: facilityId,
        userId: getUserId(), // Hardcoded for demo
        date: selectedDate,
        timeSlots: timeSlots,
        totalAmount: totalAmount
    };

    // In a real app, you would send this data to your API
    console.log('Booking data to be sent:', bookingData);

    // Simulate API call
    // For demo purposes, show a success message
    showAlert('Your booking has been successfully submitted! Redirecting to payment...', 'success');

    // Disable the checkout button to prevent multiple submissions
    document.getElementById('proceedToCheckout').disabled = true;
    document.getElementById('proceedToCheckout').innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';

    // Store booking data in localStorage for access on the payment page
    localStorage.setItem('bookingData', JSON.stringify({
        facilityId: facilityId,
        facilityName: facility_name,
        date: selectedDate,
        formattedDate: document.getElementById('summary-date').textContent,
        timeSlots: timeSlots,
        hourlyRate: hourlyRate,
        hours: hours,
        totalAmount: totalAmount.toFixed(2)
    }));

    // Redirect to payment page after a short delay
    setTimeout(() => {
        window.location.href = `payment.html?bookingId=temp-${Date.now()}&facilityId=${facilityId}`;
    }, 1500);
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

    // // Auto-dismiss after 5 seconds
    // setTimeout(() => {
    //     const alertElement = document.getElementById(alertId);
    //     if (alertElement) {
    //         alertElement.classList.remove('show');
    //         setTimeout(() => alertElement.remove(), 150);
    //     }
    // }, 5000);
}