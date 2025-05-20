<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="rw.auca.hr.util.ServerStats" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>

<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Admin Panel</title>
    <link rel="stylesheet" href="css/style.css" />
    <link rel="preconnect" href="https://fonts.googleapis.com" />
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin />
    <link
      href="https://fonts.googleapis.com/css2?family=Fira+Sans:wght@400;700&family=Outfit:wght@100..900&display=swap"
      rel="stylesheet"
    />
    <style>
      .profile-image-container {
        position: relative;
        cursor: pointer;
      }
      
      .profile-image-container:hover::after {
        content: "Change Profile";
        position: absolute;
        bottom: 0;
        left: 0;
        right: 0;
        background-color: rgba(0, 0, 0, 0.6);
        color: white;
        padding: 4px;
        font-size: 10px;
        text-align: center;
        border-radius: 0 0 50% 50%;
      }
      
      .profile img {
        width: 50px;
        height: 50px;
        border-radius: 50%;
        object-fit: cover;
        border: 2px solid #fff;
        box-shadow: 0 2px 4px rgba(0,0,0,0.1);
      }
      
      .admin-stats {
        display: flex;
        flex-wrap: wrap;
        gap: 20px;
        margin-bottom: 30px;
      }
      
      .stat-card {
        flex: 1;
        min-width: 200px;
        background: white;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        transition: transform 0.2s, box-shadow 0.2s;
      }
      
      .stat-card:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
      }
      
      .stat-card h3 {
        margin-top: 0;
        color: #333;
        font-size: 16px;
      }
      
      .stat-card p {
        font-size: 28px;
        font-weight: 600;
        margin: 10px 0 0;
        color: #2563eb;
      }
      
      .user-table {
        background: white;
        border-radius: 8px;
        padding: 20px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.05);
        margin-bottom: 30px;
      }
      
      .user-table h2 {
        margin-top: 0;
        margin-bottom: 20px;
        color: #333;
        font-size: 18px;
      }
      
      .role-badge {
        display: inline-block;
        padding: 4px 8px;
        border-radius: 4px;
        font-size: 12px;
        font-weight: 600;
        color: white;
        margin-right: 5px;
        margin-bottom: 3px;
      }
      
      .role-ADMIN {
        background-color: #dc2626;
      }
      
      .role-HR {
        background-color: #2563eb;
      }
      
      .role-EMPLOYEE {
        background-color: #10b981;
      }
      
      .select2-container {
        width: 100% !important;
      }
      
      #userTable {
        width: 100%;
        border-collapse: collapse;
      }
      
      #userTable th, #userTable td {
        padding: 12px;
        text-align: left;
        border-bottom: 1px solid #e5e7eb;
      }
      
      #userTable th {
        background-color: #f9fafb;
        font-weight: 600;
      }
      
      #userTable tr:hover {
        background-color: #f9fafb;
      }
      
      .action-buttons {
        display: flex;
        gap: 8px;
      }
    </style>
    <!--<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />-->
  </head>
  <body>
    <div>
      <main>
        <aside class="sidebar">
          <nav>
            <h2 class="abnes">EMS Admin</h2>
            <div class="profile">
              <div class="profile-image-container upload-picture-open">
                <img
                  src="assets/dp 1.png"
                  alt="profile"
                  class="profile-img"
                  title="Click to change profile picture"
                />
              </div>
              <div>
                <h3>Admin</h3>
                <h5>admin</h5>
              </div>
            </div>
            <div>
              <ul class="gap-2">
                <li class="colored">Administration</li>
                <li class="button selected">
                  <a href="${pageContext.request.contextPath}/admin">
                    <img src="${pageContext.request.contextPath}/assets/dashboard.svg" alt="dashboard" />Admin Dashboard
                  </a>
                </li>
                <li class="button">
                  <a href="${pageContext.request.contextPath}/dashboard">
                    <img src="${pageContext.request.contextPath}/assets/dashboard.svg" alt="dashboard" />User Dashboard
                  </a>
                </li>
                <li class="button">
                  <a href="#">
                    <img src="${pageContext.request.contextPath}/assets/tool.svg" alt="settings" />System Settings
                  </a>
                </li>
              </ul>
            </div>
            <ul class="gap-2">
              <li class="colored">User Management</li>
              <li class="button">
                <a href="#"><img src="${pageContext.request.contextPath}/assets/user-alt.svg" alt="users" />User Accounts</a>
              </li>
              <li class="button">
                <a href="#"><img src="${pageContext.request.contextPath}/assets/shield.svg" alt="permissions" />Permissions</a>
              </li>
              <li class="button">
                <a href="#"><img src="${pageContext.request.contextPath}/assets/log.svg" alt="logs" />Activity Logs</a>
              </li>
            </ul>
            <ul class="gap-2">
              <li class="colored">System</li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/database.svg" alt="database" />Database
                </a>
              </li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/backup.svg" alt="backup" />Backup & Restore
                </a>
              </li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/status.svg" alt="status" />System Status
                </a>
              </li>
              <form action="${pageContext.request.contextPath}/auth" method="post" id="logoutForm">
                <input type="hidden" name="action" value="logout">
                <button type="button" class="logout">
                  <img src="${pageContext.request.contextPath}/assets/logout.svg" alt="logout" /> Logout
                </button>
              </form>
            </ul>
          </nav>
        </aside>
        <section class="main-display">
          <div class="dashboard">
            <div>
              <h1 class="abnes">Admin Dashboard</h1>
            </div>

            <div class="admin-stats">
              <div class="stat-card">
                <h3>Total Requests</h3>
                <p id="totalRequests">${ServerStats.getTotalRequests()}</p>
              </div>
              <div class="stat-card">
                <h3>Active Sessions</h3>
                <p id="activeSessions">${ServerStats.getActiveSessions()}</p>
              </div>
              <div class="stat-card">
                <h3>Total Users</h3>
                <p>${users.size()}</p>
              </div>
            </div>

            <div class="search">
              <div>
                <input type="text" placeholder="Search for users" id="searchInput" />
                <button id="searchButton">Search</button>
              </div>
            </div>

            <div class="actions">
              <ul>
                <li id="refreshBtn">Refresh</li>
              </ul>
            </div>

            <div class="table">
              <div data-table="icons">
                <p>User Management</p>
                <form action="${pageContext.request.contextPath}/admin/export-users" method="get">
                  <button type="submit" class="export">
                    Export <img src="${pageContext.request.contextPath}/assets/export.svg" alt="" />
                  </button>
                </form>
              </div>
              <table id="userTable">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Roles</th>
                    <th>Status</th>
                    <th>Actions</th>
                  </tr>
                </thead>
                <tbody>
                <% 
                List<Map<String, Object>> users = (List<Map<String, Object>>)request.getAttribute("users");
                if(users != null && !users.isEmpty()) {
                    for(Map<String, Object> user : users) { 
                        List<String> roles = (List<String>)user.get("roles");
                %>
                  <tr data-user-id="<%= user.get("id") %>" class="user-row">
                    <td><%= user.get("id") %></td>
                    <td><%= user.get("username") %></td>
                    <td>
                      <% if(roles != null) { for(String role : roles) { %>
                        <span class="role-badge role-<%= role %>"><%= role %></span>
                      <% } } %>
                    </td>
                    <td>Active</td>
                    <td>
                      <button class="select edit-user-open" data-id="<%= user.get("id") %>">Edit</button>
                      <% if (!(boolean)user.getOrDefault("isCurrentUser", false)) { %>
                        <button class="delete-user" data-id="<%= user.get("id") %>" 
                                style="background-color: #dc2626;">Delete</button>
                      <% } %>
                    </td>
                  </tr>
                <% 
                    }
                } else {
                %>
                  <tr>
                    <td colspan="5">No users found</td>
                  </tr>
                <% } %>
                </tbody>
              </table>
            </div>
          </div>
        </section>

        <!-- Edit User Modal -->
        <section class="modal edit-user" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Edit User</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/update-user" method="post">
              <input type="hidden" name="user-id" id="edit-user-id">
              <div class="form-container">
                <div class="inner-modal">
                  <div>
                    <label for="edit-username">Username</label>
                    <input type="text" name="username" id="edit-username" readonly />
                  </div>
                  <div>
                    <label for="edit-roles">Roles</label>
                    <select name="roles" id="edit-roles" multiple class="select2">
                      <option value="ADMIN">Administrator</option>
                      <option value="HR">HR Manager</option>
                      <option value="EMPLOYEE">Employee</option>
                    </select>
                  </div>
                </div>
                <div class="inner-modal">
                  <div>
                    <label for="edit-reset-password">Reset Password</label>
                    <input type="checkbox" name="reset-password" id="edit-reset-password" />
                  </div>
                  <div id="password-container" style="display:none;">
                    <label for="edit-new-password">New Password</label>
                    <input type="password" name="new-password" id="edit-new-password" placeholder="Enter new password" />
                  </div>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit">Update User</button>
              </div>
            </form>
          </div>
        </section>

        <!-- Add User Modal -->
        <section class="modal add-user" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Add New User</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/add-user" method="post" id="add-user-form">
              <div class="form-container">
                <div class="inner-modal">
                  <div>
                    <label for="add-username">Username</label>
                    <input type="text" name="username" id="add-username" required />
                  </div>
                  <div>
                    <label for="add-password">Password</label>
                    <input type="password" name="password" id="add-password" required />
                  </div>
                </div>
                <div class="inner-modal">
                  <div>
                    <label for="add-roles">Roles</label>
                    <select name="roles" id="add-roles" multiple class="select2">
                      <option value="ADMIN">Administrator</option>
                      <option value="HR">HR Manager</option>
                      <option value="EMPLOYEE">Employee</option>
                    </select>
                  </div>
                  <div>
                    <label for="add-confirm-password">Confirm Password</label>
                    <input type="password" id="add-confirm-password" required />
                  </div>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit" id="add-user-submit">Add User</button>
              </div>
            </form>
          </div>
        </section>

        <!-- Delete User Confirmation Modal -->
        <section class="modal delete-user-confirm" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Delete User</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/admin/delete-user" method="post">
              <input type="hidden" name="user-id" id="delete-user-id">
              <div class="form-container">
                <div class="inner-modal">
                  <p>Are you sure you want to delete this user? This action cannot be undone.</p>
                  <p><strong>Username: </strong><span id="delete-username"></span></p>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit" style="background-color: #dc2626;">Delete User</button>
                <button type="button" class="modal-close" style="background-color: #6b7280;">Cancel</button>
              </div>
            </form>
          </div>
        </section>
      </main>
    </div>
    <script src="${pageContext.request.contextPath}/js/modal.js"></script>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
    <script>
      document.addEventListener('DOMContentLoaded', function() {
        // Initialize Select2
        $(document).ready(function() {
          $('.select2').select2();
        });
        
        // Show success or error messages
        <% if (request.getSession().getAttribute("message") != null) { %>
          alert('<%= request.getSession().getAttribute("message") %>');
          <% request.getSession().removeAttribute("message"); %>
        <% } %>
        
        <% if (request.getSession().getAttribute("error") != null) { %>
          alert('<%= request.getSession().getAttribute("error") %>');
          <% request.getSession().removeAttribute("error"); %>
        <% } %>
      
        // Update date and time every second
        function updateDateTime() {
          const now = new Date();
          const year = now.getUTCFullYear();
          const month = String(now.getUTCMonth() + 1).padStart(2, '0');
          const day = String(now.getUTCDate()).padStart(2, '0');
          const hours = String(now.getUTCHours()).padStart(2, '0');
          const minutes = String(now.getUTCMinutes()).padStart(2, '0');
          const seconds = String(now.getUTCSeconds()).padStart(2, '0');
          
          document.getElementById('currentDateTime').textContent = 
            `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
        }
        
        // Update time every minute to conserve resources
        setInterval(updateDateTime, 60000);
        
        // Refresh button with optimized AJAX
        document.getElementById('refreshBtn').addEventListener('click', function() {
          fetch('${pageContext.request.contextPath}/admin/stats', {
            headers: { 'X-Requested-With': 'XMLHttpRequest' },
            cache: 'no-store'
          })
            .then(response => response.json())
            .then(stats => {
              document.getElementById('totalRequests').textContent = stats.totalRequests;
              document.getElementById('activeSessions').textContent = stats.activeUsers;
              updateDateTime();
              
              // Visual feedback for refresh
              ['totalRequests', 'activeSessions', 'currentDateTime'].forEach(id => {
                const element = document.getElementById(id);
                element.style.transition = 'background-color 0.5s';
                element.style.backgroundColor = '#e0f2fe';
                setTimeout(() => element.style.backgroundColor = '', 500);
              });
            })
            .catch(() => window.location.reload());
        });
        
        // Logout functionality
        document.querySelector('.logout').addEventListener('click', function() {
          document.getElementById('logoutForm').submit();
        });
        
        // Edit user with optimized fetch
        document.querySelectorAll('.edit-user-open').forEach(button => {
          button.addEventListener('click', function() {
            const userId = this.getAttribute('data-id');
            const modal = document.querySelector('.edit-user');
            
            fetch('${pageContext.request.contextPath}/admin/get-user?id=' + userId)
              .then(response => response.ok ? response.json() : Promise.reject('Failed to fetch user data'))
              .then(user => {
                document.getElementById('edit-user-id').value = user.id;
                document.getElementById('edit-username').value = user.username;
                
                // Set selected roles
                const select2Element = $('#edit-roles');
                select2Element.val(user.roles || ['EMPLOYEE']);
                select2Element.trigger('change');
                
                // Show modal
                modal.removeAttribute('aria-hidden');
                modal.classList.add('show-modal');
              })
              .catch(error => console.error(error));
          });
        });
        
        // Toggle password reset field
        document.getElementById('edit-reset-password').addEventListener('change', function() {
          document.getElementById('password-container').style.display = this.checked ? 'block' : 'none';
          document.getElementById('edit-new-password').required = this.checked;
        });
        
        // Delete user functionality
        document.querySelectorAll('.delete-user').forEach(button => {
          button.addEventListener('click', function() {
            const userId = this.getAttribute('data-id');
            const username = this.closest('tr').querySelector('td:nth-child(2)').textContent;
            
            document.getElementById('delete-user-id').value = userId;
            document.getElementById('delete-username').textContent = username;
            
            const modal = document.querySelector('.delete-user-confirm');
            modal.removeAttribute('aria-hidden');
            modal.classList.add('show-modal');
          });
        });
        
        // Form validation for new user
        document.getElementById('add-user-form').addEventListener('submit', function(e) {
          const password = document.getElementById('add-password').value;
          const confirmPassword = document.getElementById('add-confirm-password').value;
          
          if (password !== confirmPassword) {
            e.preventDefault();
            alert('Passwords do not match');
          }
        });
        
        // Optimized search with debouncing
        const searchInput = document.getElementById('searchInput');
        const searchButton = document.getElementById('searchButton');
        const userRows = document.querySelectorAll('.user-row');
        let searchTimeout;
        
        function performSearch() {
          const searchTerm = searchInput.value.toLowerCase().trim();
          let foundResults = false;
          
          userRows.forEach(row => {
            const username = row.cells[1].textContent.toLowerCase();
            const roles = row.cells[2].textContent.toLowerCase();
            
            if (username.includes(searchTerm) || roles.includes(searchTerm)) {
              row.style.display = '';
              foundResults = true;
            } else {
              row.style.display = 'none';
            }
          });
          
          // Update results display
          const noResultsRow = document.getElementById('no-results-row');
          if (!foundResults) {
            if (!noResultsRow) {
              const tableBody = document.querySelector('#userTable tbody');
              const newRow = document.createElement('tr');
              newRow.id = 'no-results-row';
              const cell = document.createElement('td');
              cell.colSpan = 5;
              cell.textContent = 'No matching users found';
              cell.style.textAlign = 'center';
              cell.style.padding = '10px';
              newRow.appendChild(cell);
              tableBody.appendChild(newRow);
            } else {
              noResultsRow.style.display = '';
            }
          } else if (noResultsRow) {
            noResultsRow.style.display = 'none';
          }
        }
        
        // Debounced search
        searchInput.addEventListener('input', function() {
          clearTimeout(searchTimeout);
          searchTimeout = setTimeout(performSearch, 250);
        });
        
        searchButton.addEventListener('click', performSearch);
        
        searchInput.addEventListener('keydown', function(event) {
          if (event.key === 'Enter') {
            event.preventDefault();
            performSearch();
          }
        });
      });
    </script>
  </body>
</html>
