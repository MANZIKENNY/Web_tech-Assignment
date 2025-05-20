document.addEventListener("DOMContentLoaded", function() {
    const loginForm = document.getElementById("login-form");
    const registerForm = document.getElementById("register-form");
    
    // Check for success message to show login form
    const successMessage = document.querySelector(".success-message");
    if (successMessage) {
        loginForm.setAttribute("aria-hidden", "false");
        registerForm.setAttribute("aria-hidden", "true");
    }
    
    // Toggle between login and registration forms
    document.getElementById("login").addEventListener("click", function() {
        loginForm.setAttribute("aria-hidden", "true");
        registerForm.setAttribute("aria-hidden", "false");
    });
    
    document.getElementById("register").addEventListener("click", function() {
        loginForm.setAttribute("aria-hidden", "false");
        registerForm.setAttribute("aria-hidden", "true");
    });
});
