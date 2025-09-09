/**
 * Facility Transactions JavaScript
 * Handles displaying transaction history for a specific facility
 */


// DOM Elements
document.addEventListener('DOMContentLoaded', function() {
  // Get elements
  const facilityNameElement = document.getElementById('facility-name');
  const facilityIdElement = document.getElementById('facility-id');
  const facilityPageSubtitle = document.getElementById('facility-page-subtitle');
  const loadingElement = document.getElementById('loading');
  const errorAlert = document.getElementById('error-alert');
  const errorMessageElement = document.getElementById('error-message');
  const emptyStateElement = document.getElementById('empty-state');
  const transactionsContainer = document.getElementById('transactions-container');
  const transactionsList = document.getElementById('transactions-list');
  const statusMessage = document.getElementById('status-message');
  
  // Summary elements
  const totalCountElement = document.getElementById('total-count');
  const totalAmountElement = document.getElementById('total-amount');
  // const firstTransactionDateElement = document.getElementById('first-transaction-date');
  // const latestTransactionDateElement = document.getElementById('latest-transaction-date');
  
  // Get facility ID and name from URL parameters
  const urlParams = new URLSearchParams(window.location.search);
  const facilityId = urlParams.get('facilityId');
  const facilityName = urlParams.get('facilityName');
  
  // Check if facilityId exists
  if (!facilityId) {
    showError('No facility ID provided. Please go back and try again.');
    return;
  }
  
  // Update page with facility info
  facilityNameElement.textContent = facilityName || 'Facility';
  facilityIdElement.textContent = `ID: ${facilityId}`;
  facilityPageSubtitle.textContent = `Transaction history for ${facilityName || 'this facility'}.`;
  
  // Initialize by fetching transactions
  fetchTransactions(facilityId);
  
  /**
   * Show loading state
   */
  function showLoading() {
    loadingElement.classList.remove('d-none');
    transactionsContainer.classList.add('d-none');
    errorAlert.classList.add('d-none');
    emptyStateElement.classList.add('d-none');
    statusMessage.style.display = 'none';
  }
  
  /**
   * Show error message
   */
  function showError(message) {
    loadingElement.classList.add('d-none');
    transactionsContainer.classList.add('d-none');
    emptyStateElement.classList.add('d-none');
    
    errorAlert.classList.remove('d-none');
    errorMessageElement.textContent = message;
  }
  
  /**
   * Show status message
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
   * Format date to localized string
   */
  function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  }
  
  /**
   * Format currency amount
   */
  function formatCurrency(amount) {
    return '$' + parseFloat(amount).toFixed(2);
  }
  
  /**
   * Get CSS class for transaction status
   */
  function getStatusClass(status) {
    switch (status.toLowerCase()) {
      case 'completed':
        return 'status-completed';
      case 'pending':
        return 'status-pending';
      case 'failed':
        return 'status-failed';
      case 'refunded':
        return 'status-refunded';
      default:
        return '';
    }
  }
  
  /**
   * Fetch transactions for the facility
   */
  async function fetchTransactions(facilityId) {
    try {
      console.log(`Fetching transactions for facility ID: ${facilityId}`);
      showLoading();
      
      // Construct the API URL
      const apiUrl = `/api/payments/facility/${facilityId}`;
      
      // Make the API request
      const response = await fetch(apiUrl);
      
      // Handle error responses
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}: ${response.statusText}`);
      }
      
      // Parse the JSON response
      const transactions = await response.json();
      console.log('Received transactions:', transactions);
      
      // Display the transactions
      displayTransactions(transactions);
    } catch (error) {
      console.error('Error fetching transactions:', error);
      showError(error.message);
    }
  }
  
  /**
   * Display transactions in the list
   */
  function displayTransactions(payments) {
    // Hide loading indicator
    loadingElement.classList.add('d-none');
    
    // Check if there are any transactions
    if (!payments || payments.length === 0) {
      emptyStateElement.classList.remove('d-none');
      return;
    }
    
    // Sort transactions by date (newest first)
    payments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
    
    // Show transactions container
    transactionsContainer.classList.remove('d-none');
    transactionsList.innerHTML = ''; // Clear existing transactions
    
    // Calculate summary data
    const totalAmount = payments.reduce((sum, payment) => sum + parseFloat(payment.amount), 0);
    const firstTransaction = payments[payments.length - 1]; // After sorting, last item is oldest
    const latestTransaction = payments[0]; // After sorting, first item is newest
    
    // Update summary elements
    totalCountElement.textContent = payments.length;
    totalAmountElement.textContent = formatCurrency(totalAmount);
    // firstTransactionDateElement.textContent = formatDate(firstTransaction.createdAt);
    // latestTransactionDateElement.textContent = formatDate(latestTransaction.createdAt);
    
    // Generate HTML for each transaction
    payments.forEach(payment => {
      const transactionCard = document.createElement('div');
      transactionCard.className = 'transaction-card';
      transactionCard.innerHTML = `
        <div class="transaction-header">
          <div>
            <span class="transaction-amount">${formatCurrency(payment.amount)}</span>
          </div>
          <div>
            <span class="transaction-status ${getStatusClass(payment.paymentStatus)}">${payment.paymentStatus}</span>
          </div>
        </div>
        <div class="transaction-body">
          <div class="transaction-meta">
            <div class="transaction-meta-item">
              <div class="meta-label">Transaction ID</div>
              <div>${payment.transactionId || 'N/A'}</div>
            </div>
            <div class="transaction-meta-item">
              <div class="meta-label">Payment Method</div>
              <div>${payment.paymentMethod || 'N/A'}</div>
            </div>
          </div>
        </div>
      `;
      transactionsList.appendChild(transactionCard);
    });
  }
 
  /**
   * Handle back button functionality
   */
  document.querySelector('.back-button a').addEventListener('click', function(e) {
    e.preventDefault();
    window.location.href = "myOfferings.html";
  });
});