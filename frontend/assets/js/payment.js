document.addEventListener('DOMContentLoaded', function() {
    // Load booking data from localStorage
    loadBookingData();
    
    // Set up payment methods
    setupPaymentMethods();
    
    // Set up payment form
    setupPaymentForm();

    // Initialize retry counter
    window.paymentRetryCount = 0;
    window.maxRetries = 2;
});

// Generate a booking reference number
function generateBookingReference(facilityId) {
    const timestamp = new Date().getTime().toString().slice(-6);
    const random = Math.floor(Math.random() * 10000).toString().padStart(4, '0');
    return `${facilityId}-${timestamp}-${random}`;
}

// Load booking data from localStorage
function loadBookingData() {
    const bookingData = JSON.parse(localStorage.getItem('bookingData'));
    
    if (!bookingData) {
        showAlert('No booking data found. Please return to the booking page.', 'danger');
        return;
    }
    
    // Update the facility page link
    const facilityPageLink = document.getElementById('facility-page-link');
    facilityPageLink.textContent = bookingData.facilityName;
    facilityPageLink.href = `property-single.html?id=${bookingData.facilityId}`;
    
    // Update the booking page link
    const bookingPageLink = document.getElementById('booking-page-link');
    bookingPageLink.textContent = 'Booking';
    bookingPageLink.href = `bookings.html?id=${bookingData.facilityId}`;
    
    // Update payment summary with booking details
    document.getElementById('summary-facility').textContent = bookingData.facilityName;
    document.getElementById('summary-date').textContent = bookingData.formattedDate;
    document.getElementById('summary-hourly-rate').textContent = bookingData.hourlyRate.toFixed(2);
    document.getElementById('summary-hours').textContent = bookingData.hours;
    document.getElementById('summary-total').textContent = bookingData.totalAmount;
    
    // Generate a booking reference for bank transfer
    const bookingReference = generateBookingReference(bookingData.facilityId);
    const bookingReferenceElement = document.getElementById('booking-reference');
    if (bookingReferenceElement) {
        bookingReferenceElement.textContent = bookingReference;
    }
    
    // Create list of time slots
    const timeSlotsList = document.getElementById('summary-time-slots');
    if (timeSlotsList) {
        timeSlotsList.innerHTML = '';
        
        bookingData.timeSlots.forEach(slot => {
            const startTime = formatTime(slot.startHour);
            const endTime = formatTime(slot.endHour);
            
            const listItem = document.createElement('li');
            listItem.textContent = `${startTime} - ${endTime}`;
            timeSlotsList.appendChild(listItem);
        });
    }
}

// Format hour to 12-hour time format with AM/PM
function formatTime(hour) {
    const ampm = hour >= 12 ? 'PM' : 'AM';
    const displayHour = hour % 12 || 12; // Convert 0 to 12 for 12 AM
    return `${displayHour}:00 ${ampm}`;
}

// Set up payment method selection
function setupPaymentMethods() {
    const paymentMethods = document.querySelectorAll('.payment-method');
    
    paymentMethods.forEach(method => {
        method.addEventListener('click', function() {
            // Remove selected class from all methods
            paymentMethods.forEach(m => m.classList.remove('selected'));
            
            // Add selected class to clicked method
            this.classList.add('selected');
            
            // Find the radio input and check it
            const radio = this.querySelector('input[type="radio"]');
            if (radio) {
                radio.checked = true;
            }
            
            // Show appropriate payment form based on selection
            showPaymentForm(radio.id);
        });
    });
}

// Show the appropriate payment form based on selection
function showPaymentForm(paymentMethod) {
    // Get all payment form containers
    const creditCardForm = document.getElementById('credit-card-form');
    const paypalForm = document.getElementById('paypal-form');
    const bankTransferForm = document.getElementById('bank-transfer-form');
    
    // Hide all forms first
    if (creditCardForm) creditCardForm.style.display = 'none';
    if (paypalForm) paypalForm.style.display = 'none';
    if (bankTransferForm) bankTransferForm.style.display = 'none';
    
    // Show the selected form
    switch(paymentMethod) {
        case 'credit-card':
            if (creditCardForm) creditCardForm.style.display = 'block';
            break;
        case 'paypal':
            if (paypalForm) paypalForm.style.display = 'block';
            break;
        case 'bank-transfer':
            if (bankTransferForm) bankTransferForm.style.display = 'block';
            break;
    }
}

// Set up payment form submission
function setupPaymentForm() {
    // Set up credit card form
    const creditCardForm = document.getElementById('payment-form');
    if (creditCardForm) {
        creditCardForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Validate form
            if (!validatePaymentForm()) {
                return;
            }
            
            // Process payment
            processPayment('Credit Card');
        });
    }
    
    // Set up PayPal form
    const paypalForm = document.getElementById('paypal-payment-form');
    if (paypalForm) {
        paypalForm.addEventListener('submit', function(e) {
            e.preventDefault();
            processPayment('PayPal');
        });
    }
    
    // Set up bank transfer form
    const bankTransferForm = document.getElementById('bank-transfer-payment-form');
    if (bankTransferForm) {
        bankTransferForm.addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Check if the confirmation checkbox is checked
            const confirmCheckbox = document.getElementById('bank-transfer-confirm');
            if (confirmCheckbox && !confirmCheckbox.checked) {
                showAlert('Please confirm that you have initiated the bank transfer', 'warning');
                return;
            }
            
            processPayment('Bank Transfer');
        });
    }

    // Add Fail Payment buttons to each payment form
    addFailPaymentButtons();
}

// Add Fail Payment buttons to the payment forms
function addFailPaymentButtons() {
    // For Credit Card form
    const creditCardBtnContainer = document.querySelector('#payment-form .d-grid');
    if (creditCardBtnContainer) {
        const failBtn = document.createElement('button');
        failBtn.type = 'button';
        failBtn.id = 'fail-payment-btn';
        failBtn.className = 'btn btn-outline-danger mt-2';
        failBtn.textContent = 'Fail Payment (For Testing)';
        failBtn.addEventListener('click', function() {
            failPayment('Credit Card');
        });
        creditCardBtnContainer.appendChild(failBtn);
    }
    
    // For PayPal form
    const paypalBtnContainer = document.querySelector('#paypal-payment-form .d-grid');
    if (paypalBtnContainer) {
        const failBtn = document.createElement('button');
        failBtn.type = 'button';
        failBtn.id = 'paypal-fail-btn';
        failBtn.className = 'btn btn-outline-danger mt-2';
        failBtn.textContent = 'Fail Payment (For Testing)';
        failBtn.addEventListener('click', function() {
            failPayment('PayPal');
        });
        paypalBtnContainer.appendChild(failBtn);
    }
    
    // For Bank Transfer form
    const bankBtnContainer = document.querySelector('#bank-transfer-payment-form .d-grid');
    if (bankBtnContainer) {
        const failBtn = document.createElement('button');
        failBtn.type = 'button';
        failBtn.id = 'bank-fail-btn';
        failBtn.className = 'btn btn-outline-danger mt-2';
        failBtn.textContent = 'Fail Payment (For Testing)';
        failBtn.addEventListener('click', function() {
            failPayment('Bank Transfer');
        });
        bankBtnContainer.appendChild(failBtn);
    }
}

// Process payment for any payment method
async function processPayment(paymentMethod) {
    // Show loading state
    let submitBtn;

    switch (paymentMethod) {
        case 'Credit Card':
            submitBtn = document.getElementById('complete-payment-btn');
            break;
        case 'PayPal':
            submitBtn = document.getElementById('paypal-payment-btn');
            break;
        case 'Bank Transfer':
            submitBtn = document.getElementById('bank-transfer-payment-btn');
            break;
    }

    if (!submitBtn) {
        console.error('Submit button not found for', paymentMethod);
        return;
    }

    submitBtn.disabled = true;
    submitBtn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';

    // Retrieve booking data from localStorage
    const bookingData = JSON.parse(localStorage.getItem('bookingData'));

    if (!bookingData) {
        showAlert('Error: Booking data not found', 'danger');
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Try Again';
        return;
    }

    // Add user ID and payment method to the booking data
    const bookingPayload = {
        userId: await getUserId(), // Hardcoded user ID
        userName: bookingData.facilityName,
        facilityId: bookingData.facilityId,
        facilityName: "abc",
        addonsString: "abc",

        date: bookingData.date,
        timeSlots: bookingData.timeSlots,
        totalAmount: parseFloat(bookingData.totalAmount),
        hourlyRate: bookingData.hourlyRate,
        hours: bookingData.hours,
        paymentMethod: paymentMethod,
        paymentStatus: 'COMPLETED'
    };

    console.log(bookingPayload.userId);

    // Send booking data to the backend
    try {
        const response = await fetch('/api/payments/create', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(bookingPayload)
        });

        if (!response.ok) {
            throw new Error('Network response was not ok');
        }

        const data = await response.json();
        console.log('Booking created successfully:', data);

        // Show success message
        showAlert(`${paymentMethod} payment successful! Your booking is confirmed.`, 'success');

        // Update button
        submitBtn.innerHTML = 'Payment Successful <i class="bi bi-check-circle-fill"></i>';
        submitBtn.classList.remove('btn-primary');
        submitBtn.classList.add('btn-success');

        // Disable the fail payment button
        disableFailPaymentButtons();

        // Redirect to confirmation page after 2 seconds
        setTimeout(() => {
            if (data && data.id) {
                window.location.href = `booking-confirmation.html?id=${data.id}`;
            } else {
                alert('Booking confirmed!');
                window.location.href = 'index.html';
            }
        }, 2000);

    } catch (error) {
        console.error('Error creating booking:', error);
        showAlert('Error creating booking. Please try again.', 'danger');

        // Reset button
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Try Again';
    }
}


// Function to handle failed payments
function failPayment(paymentMethod) {
    // Check if we've reached the maximum number of retries
    if (window.paymentRetryCount >= window.maxRetries) {
        showAlert('Payment failed. Maximum retry attempts reached. Redirecting to booking page...', 'danger');
        
        // Disable all payment buttons after max retries
        document.getElementById('complete-payment-btn').disabled = true;
        document.getElementById('paypal-payment-btn').disabled = true;
        document.getElementById('bank-transfer-payment-btn').disabled = true;
        disableFailPaymentButtons();
        
        // Get the facility ID from localStorage to redirect back to the booking page
        let facilityId = '1'; // Default fallback
        try {
            const bookingData = JSON.parse(localStorage.getItem('bookingData'));
            if (bookingData && bookingData.facilityId) {
                facilityId = bookingData.facilityId;
            }
        } catch (error) {
            console.error('Error retrieving booking data:', error);
        }
        
        // Redirect to the booking page after a short delay
        setTimeout(() => {
            window.location.href = `booking.html?id=${facilityId}`;
        }, 2000);
        
        return;
    }
    
    // Increment retry counter
    window.paymentRetryCount++;
    const retriesRemaining = window.maxRetries - window.paymentRetryCount;
    
    // Show failure message with retry count
    showAlert(`${paymentMethod} payment failed. Retries remaining: ${retriesRemaining}`, 'danger');
    
    // Reset form state
    let submitBtn;
    
    switch(paymentMethod) {
        case 'Credit Card':
            submitBtn = document.getElementById('complete-payment-btn');
            break;
        case 'PayPal':
            submitBtn = document.getElementById('paypal-payment-btn');
            break;
        case 'Bank Transfer':
            submitBtn = document.getElementById('bank-transfer-payment-btn');
            break;
    }
    
    if (submitBtn) {
        submitBtn.disabled = false;
        submitBtn.innerHTML = 'Try Again';
    }
}
// Disable all fail payment buttons
function disableFailPaymentButtons() {
    const failButtons = [
        document.getElementById('fail-payment-btn'),
        document.getElementById('paypal-fail-btn'),
        document.getElementById('bank-fail-btn')
    ];
    
    failButtons.forEach(btn => {
        if (btn) {
            btn.disabled = true;
            btn.textContent = 'Payment Failed';
        }
    });
}

// Validate payment form fields
function validatePaymentForm() {
    return true;
    const cardHolder = document.getElementById('card-holder')?.value.trim();
    const cardNumber = document.getElementById('card-number')?.value.trim();
    const expiryDate = document.getElementById('expiry-date')?.value.trim();
    const cvv = document.getElementById('cvv')?.value.trim();
    
    // Simple validation checks
    if (!cardHolder) {
        showAlert('Please enter cardholder name', 'warning');
        return false;
    }
    
    if (!cardNumber || cardNumber.length < 16) {
        showAlert('Please enter a valid card number', 'warning');
        return false;
    }
    
    if (!expiryDate || !expiryDate.includes('/')) {
        showAlert('Please enter a valid expiry date (MM/YY)', 'warning');
        return false;
    }
    
    if (!cvv || cvv.length < 3) {
        showAlert('Please enter a valid CVV', 'warning');
        return false;
    }
    
    return true;
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