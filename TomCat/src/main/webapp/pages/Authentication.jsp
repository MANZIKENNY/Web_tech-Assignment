<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Authentication</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
    <style>
      .error-message { color: red; margin: 10px 0; text-align: center; }
      .success-message { color: green; margin: 10px 0; text-align: center; }
    </style>
  </head>
  <body class="login">
    <p>EMS</p>
    <main class="container">
      <section class="form" aria-hidden="true" id="login-form">
        <h1>Welcome Back</h1>
        <% if(request.getAttribute("loginError") != null) { %>
          <div class="error-message">${loginError}</div>
        <% } %>
        <form action="${pageContext.request.contextPath}/auth" method="post">
          <input type="hidden" name="action" value="login">
          <input
            type="text"
            name="username"
            id="username"
            placeholder="Username"
            required
          />
          <input
            type="password"
            name="password"
            id="password"
            placeholder="Password"
            required
          />
          <a id="login">Don't Have An account? Create One</a>
          <button type="submit">Login</button>
        </form>
      </section>
      <section class="form" aria-hidden="false" id="register-form">
        <h1>Register</h1>
        <% if(request.getAttribute("registerError") != null) { %>
          <div class="error-message">${registerError}</div>
        <% } %>
        <% if(request.getAttribute("registerSuccess") != null) { %>
          <div class="success-message">${registerSuccess}</div>
        <% } %>
        <form action="${pageContext.request.contextPath}/auth" method="post">
          <input type="hidden" name="action" value="register">
          <input
            type="text"
            name="username"
            id="reg-username"
            placeholder="Username"
            required
          />
          <input
            type="password"
            name="password"
            id="reg-password"
            placeholder="Password"
            required
          />
          <input type="email" name="email" id="email" placeholder="Email" required />
          <a id="register">Already Have account? Login</a>
          <button type="submit">Register</button>
        </form>
      </section>
    </main>
    <script src="${pageContext.request.contextPath}/js/script.js"></script>
  </body>
</html>
