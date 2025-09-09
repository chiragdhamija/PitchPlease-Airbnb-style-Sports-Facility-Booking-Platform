async function fetchAndDisplayPayments() {
  const facilitiesContainer = document.getElementById('facilitiesContainer');
  const loadingIndicator = document.getElementById('loading');
  const errorMessage = document.getElementById('errorMessage');
  const noResults = document.getElementById('noResults');

  // Replace with logic to get actual logged-in user ID
  const userId = await getUserId();

  // Show loading
  loadingIndicator.style.display = 'block';
  facilitiesContainer.innerHTML = '';
  errorMessage.style.display = 'none';
  noResults.style.display = 'none';

  // Make API call to fetch payment data
  fetch(`/api/payments/user/${userId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      // Add Authorization header if needed:
      // 'Authorization': `Bearer ${yourAccessToken}`
    }
  })
  .then(response => {
    loadingIndicator.style.display = 'none';

    if (!response.ok) {
      throw new Error(`HTTP error! Status: ${response.status}`);
    }
    return response.json();
  })
  .then(payments => {
    if (!payments || payments.length === 0) {
      noResults.style.display = 'block';
      return;
    }

    payments.forEach(payment => {
      const paymentCard = createPaymentCard(payment);
      facilitiesContainer.appendChild(paymentCard);
    });

    if (typeof AOS !== 'undefined') {
      AOS.refresh();
    }
  })
  .catch(error => {
    console.error('Error fetching payments:', error);
    errorMessage.textContent = 'An error occurred while loading payment data. Please try again later.';
    errorMessage.style.display = 'block';
  });
}

// Function to fetch and display payment transactions
// function fetchAndDisplayPayments() {
//   const facilitiesContainer = document.getElementById('facilitiesContainer');
//   const loadingIndicator = document.getElementById('loading');
//   const errorMessage = document.getElementById('errorMessage');
//   const noResults = document.getElementById('noResults');
  
//   // Show loading indicator
//   loadingIndicator.style.display = 'block';
//   facilitiesContainer.innerHTML = '';
//   errorMessage.style.display = 'none';
//   noResults.style.display = 'none';

//   const userId = getUserId();
  
//   // This would be replaced with an actual API call in a real application
//   // For demonstration, using sample data
//   // Shailender stekked
//   // const samplePayments = [
//   //   {
//   //     paymentId: "PAY-2023-04-18-001",
//   //     userName: "John Doe",
//   //     facilityName: "Downtown Sports Complex",
//   //     amount: 149.99,
//   //     paymentMethod: "Credit Card",
//   //     paymentStatus: "Completed"
//   //   },
//   //   {
//   //     paymentId: "PAY-2023-04-17-002",
//   //     userName: "Jane Smith",
//   //     facilityName: "Riverside Tennis Court",
//   //     amount: 75.50,
//   //     paymentMethod: "PayPal",
//   //     paymentStatus: "Pending"
//   //   },
//   //   {
//   //     paymentId: "PAY-2023-04-16-003",
//   //     userName: "Robert Johnson",
//   //     facilityName: "City Stadium Field B",
//   //     amount: 320.00,
//   //     paymentMethod: "Bank Transfer",
//   //     paymentStatus: "Failed"
//   //   }
//   // ];

//   // Simulate API delay
//   setTimeout(() => {
//     loadingIndicator.style.display = 'none';
    
//     try {
//       if (samplePayments.length === 0) {
//         noResults.style.display = 'block';
//         return;
//       }
      
//       // Display payments
//       samplePayments.forEach(payment => {
//         const paymentCard = createPaymentCard(payment);
//         facilitiesContainer.appendChild(paymentCard);
//       });
      
//       // Initialize AOS animations if needed
//       if (typeof AOS !== 'undefined') {
//         AOS.refresh();
//       }
//     } catch (error) {
//       console.error('Error displaying payments:', error);
//       errorMessage.textContent = 'An error occurred while loading payment data. Please try again later.';
//       errorMessage.style.display = 'block';
//     }
//   }, 1000);
// }

// Function to create a payment card element
function createPaymentCard(payment) {
  // Determine status badge color
  // alert(payment)
  console.log(payment);
  let statusClass = '';
  let statusIcon = '';
  
  switch(payment.paymentStatus.toLowerCase()) {
    case 'completed':
      statusClass = 'bg-success';
      statusIcon = 'bi-check-circle-fill';
      break;
    case 'pending':
      statusClass = 'bg-warning';
      statusIcon = 'bi-hourglass-split';
      break;
    case 'failed':
      statusClass = 'bg-danger';
      statusIcon = 'bi-x-circle-fill';
      break;
    default:
      statusClass = 'bg-secondary';
      statusIcon = 'bi-question-circle-fill';
  }
  
  // Create column
  const col = document.createElement('div');
  col.className = 'col-lg-4 col-md-6';
  col.setAttribute('data-aos', 'fade-up');
  col.setAttribute('data-aos-delay', '100');
  
  // Create payment card HTML
  col.innerHTML = `
    <div class="card card-box">
      <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="card-title mb-0">Payment #${payment.paymentId}</h5>
        <span class="badge ${statusClass} text-white">
          <i class="bi ${statusIcon} me-1"></i>
          ${payment.paymentStatus}
        </span>
      </div>
      <div class="card-body">
        <ul class="list-group list-group-flush">
          <li class="list-group-item d-flex justify-content-between">
            <span class="fw-bold"><i class="bi bi-building me-2"></i>Facility:</span>
            <span>${payment.facilityName}</span>
          </li>
          <li class="list-group-item d-flex justify-content-between">
            <span class="fw-bold"><i class="bi bi-currency-dollar me-2"></i>Amount:</span>
            <span>$${payment.amount.toFixed(2)}</span>
          </li>
          <li class="list-group-item d-flex justify-content-between">
            <span class="fw-bold"><i class="bi bi-credit-card me-2"></i>Method:</span>
            <span>${payment.paymentMethod}</span>
          </li>
        </ul>
      </div>
      <div class="card-footer text-muted">
        <button class="btn btn-sm btn-outline-primary float-end" onclick="viewPaymentDetails('${payment.paymentId}')">
          <i class="bi bi-eye me-1"></i> View Details
        </button>
      </div>
    </div>
  `;
  
  return col;
}

// Function to view payment details (placeholder)
function viewPaymentDetails(paymentId) {
  alert(`Viewing details for payment ${paymentId}`);
  // This would navigate to a details page or open a modal in a real application
}

// Call the function when the page loads
document.addEventListener('DOMContentLoaded', function() {
  // Update page title and description
  const pageTitle = document.querySelector('.page-title h1');
  const pageDescription = document.querySelector('.page-title p.mb-0');
  
  if (pageTitle) pageTitle.textContent = 'Payment Transactions';
  if (pageDescription) pageDescription.textContent = 'View all payment transactions for facility bookings.';
  console.log("Loading payment transactions...");
  
  // Initialize the payments display
  fetchAndDisplayPayments();
});