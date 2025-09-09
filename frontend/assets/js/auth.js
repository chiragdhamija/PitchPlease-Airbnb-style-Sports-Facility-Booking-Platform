// Save JWT after login
document.addEventListener("DOMContentLoaded", function () {
    const loginForm = document.getElementById("loginForm");
    const signupForm = document.getElementById("signupForm");
  
    if (loginForm) {
      loginForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const userName = document.getElementById("username").value;
        const password = document.getElementById("password").value;
  
        try {
          const res = await fetch("/api/api/auth/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ userName, password })
          });
          
          if (!res.ok) {
            const errorData = await res.json();
            throw new Error(errorData.message || "Login failed");
          }

          const tokenData = await res.json();
          const { accessToken, refreshToken, accessTokenExpiresAt } = tokenData.response;
          localStorage.setItem("accessToken", accessToken);
          localStorage.setItem("refreshToken", refreshToken);
          localStorage.setItem("accessTokenExpiresAt", accessTokenExpiresAt);
          // localStorage.setItem("jwt", token);
          window.location.href = "index.html";
        } catch (err) {
          alert("Login error: " + err.message);
        }
      });
    }
  
    if (signupForm) {
      signupForm.addEventListener("submit", async function (e) {
        e.preventDefault();
        const userName = document.getElementById("username").value;
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;
        // alert("SKIBDI signup");
        try {
            // alert("try");
          const res = await fetch("/api/api/auth/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              userName,
              email,
              password
            }),
          });
  
          const text = await res.text();
          if (!res.ok) throw new Error(text);
          alert("Signup successful! Please login.");
          window.location.href = "login.html";
        } catch (err) {
          alert("Signup error: " + err.message);
        }
      });
    }
  });
  
  // Check if logged in
  function requireAuth() {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      window.location.href = "login.html";
    }
  }
  
  // Logout
  async function logout() {
    const refreshToken = localStorage.getItem("refreshToken");
  
    try {
      await fetch("/api/api/auth/logout", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ token: refreshToken })
      });
    } catch (err) {
      console.warn("Logout failed silently:", err);
    }
  
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    localStorage.removeItem("accessTokenExpiresAt");
    window.location.href = "login.html";
  }
  
async function getUserId() {
    const token = localStorage.getItem("accessToken");
    if (!token) {
      console.warn("No access token found.");
      return null;
    }
    console.log("Accesss token:", token);
    try {
      const res = await fetch("/api/api/users/user-id", {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`
        }
      });
  
      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || "Failed to fetch user ID");
      }
  
      const userId = await res.text(); // assuming plain text response
      console.log("User ID:", userId);
      return userId;
    } catch (err) {
      console.error("Error retrieving user ID:", err.message);
      return null;
    }
  }
  window.getUserId = getUserId;
  window.requireAuth = requireAuth;
  window.logout = logout;