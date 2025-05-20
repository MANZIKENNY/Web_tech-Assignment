<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <link rel="stylesheet" href="asset/css/dash.css" />
    <title>Dashboard</title>
    <style>
      .header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 10px;
        background-color: #f1f1f1;
      }
      .profile {
        display: flex;
        align-items: center;
      }
      .profile img {
        border-radius: 50%;
        width: 40px;
        height: 40px;
        margin-right: 10px;
      }
    </style>
  </head>
  <body>
    <header class="header">
      <p>Request: ${sessionCount}</p>
      <div class="profile">
        <img src="asset/images/profile.png" alt="Profile Picture" />
        <span>${userName}</span>
      </div>
    </header>
    <main>
      <section class="user-connected">
        <p>Users Connected: ${totalActiveSessions}</p>
      </section>
    </main>
    <footer></footer>
  </body>
</html>
