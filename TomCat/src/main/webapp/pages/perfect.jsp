<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="rw.auca.hr.model.Employee" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="rw.auca.hr.model.User" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title class="abnes">EMS</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css" />
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
    </style>
  </head>
  <body>
    <div>
      <main>
        <aside class="sidebar">
          <nav>
            <h2 class="abnes">EMS</h2>
            <div class="profile">
              <div class="profile-image-container upload-picture-open">
                <img
                  src="${sessionScope.user.profilePicture != null ? pageContext.request.contextPath.concat('/assets/profile/').concat(sessionScope.user.profilePicture) : 'https://s3-alpha-sig.figma.com/img/916f/9ff0/fc94870afef9a39114bfad39f141e97b?Expires=1742169600&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=nG5W9Sd79igp5ganPF1xYbMrW-g2rPZMg1xI~42Yfswrvz1zj-7iAWYgwkm3BNRP76A8Q~DkbY3sBY6jx06unA6maPuCzohBbsyCrwW323kdLT2ydJCCsSqlNS5bFKsKMGCgICVaxD4nQnE3O9JG7yYthLKayZ59ISPDIryEnZ1VaxWXEFWqupSqW0armfLaG2lffMoSL2hxqBNOBqHczSdCF4tnBGZpINuNHNWqmIhoZQGlmgSsH2B38rAQXXLrRpz-ftuUDor04EwRK5KRAGvKgA6QNJLX5f5oM4Qeu3cE6sRd7T-oE0WqWtdvg3KM-0Gx9ZGawaItD7sb0iatmQ__'}"
                  alt="profile"
                  class="profile-img"
                  title="Click to change profile picture"
                />
              </div>
              <div>
                <h3><%= request.getAttribute("roleDisplay") %></h3>
                <h5><%= request.getAttribute("username") %></h5>
              </div>
            </div>
            <div>
              <ul class="gap-2">
                <li class="colored">Features</li>
                <li class="button selected">
                  <a href="${pageContext.request.contextPath}/dashboard">
                    <img src="${pageContext.request.contextPath}/assets/dashboard.svg" alt="dashboard" />Dashboard
                  </a>
                </li>
                <li class="button">
                  <a href="#">
                    <img src="${pageContext.request.contextPath}/assets/message.svg" alt="dashboard" />Messages
                  </a>
                </li>
              </ul>
            </div>
            <ul class="gap-2">
              <li class="colored">Recruitment</li>
              <li class="button">
                <a href="#"><img src="${pageContext.request.contextPath}/assets/tool.svg" alt="dashboard" />Jobs</a>
              </li>
              <li class="button">
                <a href="#"><img src="${pageContext.request.contextPath}/assets/group.svg" alt="dashboard" />Candidates</a>
              </li>
              <li class="button">
                <a href="#"><img src="${pageContext.request.contextPath}/assets/resume.svg" alt="dashboard" />Resume</a>
              </li>
            </ul>
            <ul class="gap-2">
              <li class="colored">Organizations</li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/user-alt.svg" alt="dashboard" />Employee Management
                </a>
              </li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/book-open.svg" alt="dashboard" />Leave Management
                </a>
              </li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/perfomance.svg" alt="dashboard" />Perfomance Management
                </a>
              </li>
              <li class="button">
                <a href="#">
                  <img src="${pageContext.request.contextPath}/assets/payroll.svg" alt="dashboard" />Payroll Management
                </a>
              </li>
              <form action="${pageContext.request.contextPath}/auth" method="post" id="logoutForm">
                <input type="hidden" name="action" value="logout">
                <button type="button" class="logout">
                  <img src="${pageContext.request.contextPath}/assets/logout.svg" alt="" /> Logout
                </button>
              </form>
            </ul>
          </nav>
        </aside>
        <section class="main-display">
          <div class="dashboard">
            <div>
              <h1 class="abnes">EMS</h1>
            </div>
            <div class="search">
              <img src="${pageContext.request.contextPath}/assets/home.svg" alt="home" />
              <div>
                <input type="text" placeholder="Search for employees" id="searchInput" />
                <button id="searchButton">Search</button>
              </div>
            </div>

            <div class="actions">
              <ul>
                <li id="refreshBtn">Refresh</li>
                <li class="add-open">Add</li>
                <li class="delete-open">Delete</li>
              </ul>
            </div>
            <div class="table">
              <div data-table="icons">
                <p>Employee Management</p>
                <form action="${pageContext.request.contextPath}/employee/export" method="get">
                  <button type="submit" class="export">
                    Export <img src="${pageContext.request.contextPath}/assets/export.svg" alt="" />
                  </button>
                </form>
              </div>
              <table id="employeeTable">
                <tr>
                  <th>Names</th>
                  <th>Position</th>
                  <th>Start Date</th>
                  <th>Gender</th>
                  <th>Qualification</th>
                  <th>Actions</th>
                </tr>
                <% 
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                List<Employee> employees = (List<Employee>)request.getAttribute("employees");
                if(employees != null) {
                    for(Employee employee : employees) { 
                %>
                  <tr data-employee-id="<%= employee.getEmpId() %>" class="employee-row">
                    <td><%= employee.getName() %></td>
                    <td><%= employee.getPositionName() %></td>
                    <td><%= employee.getStartDate() != null ? dateFormat.format(employee.getStartDate()) : "N/A" %></td>
                    <td><%= employee.getGender() %></td>
                    <td><%= employee.getQualification() != null ? employee.getQualification() : "N/A" %></td>
                    <td>
                      <button class="select view-open" data-id="<%= employee.getEmpId() %>">View</button>
                    </td>
                  </tr>
                <% 
                    }
                } else {
                %>
                  <tr>
                    <td colspan="6">No employees found</td>
                  </tr>
                <% } %>
              </table>
            </div>
          </div>
        </section>
        <!--################################################ Loading User Data Modal And Update ###############################################-->
        <section class="modal view-employee" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Employee Profile</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/employee/update" method="post">
              <input type="hidden" name="emp-id" id="view-emp-id">
              <div class="form-container">
                <div class="inner-modal">
                  <div>
                    <label for="view-emp-name">Name</label>
                    <input
                      type="text"
                      name="emp-name"
                      id="view-emp-name"
                      placeholder="ex: John Doe"
                    />
                  </div>
                  <div class="inner-modal">
                    <label for="view-emp-gender">Gender</label>
                    <select name="emp-gender" id="view-emp-gender">
                      <option value="M">Male</option>
                      <option value="F">Female</option>
                      <option value="O">Other</option>
                    </select>
                  </div>
                  <div class="inner-modal">
                    <label for="view-emp-date">Start Date</label>
                    <input type="date" name="emp-date" id="view-emp-date" />
                  </div>
                </div>
                <div>
                  <div class="inner-modal">
                    <label for="view-emp-position">Position</label>
                    <select name="emp-position" id="view-emp-position">
                      <% 
                      String[] positionCodes = {"SWE", "PM", "DBA", "UID", "NET", "BA", "QA", "HR", "DEV", "MKT"};
                      String[] positionNames = {
                          "Software Developer", "Project Manager", "Database Administrator", 
                          "UI/UX Designer", "Network Engineer", "Business Analyst",
                          "Quality Assurance Engineer", "HR Manager", "DevOps Engineer", "Marketing Specialist"
                      };
                      for(int i = 0; i < positionCodes.length; i++) {
                      %>
                        <option value="<%= positionCodes[i] %>"><%= positionNames[i] %></option>
                      <% } %>
                    </select>
                  </div>
                  <div class="inner-modal">
                    <label for="view-emp-qualification">Qualification</label>
                    <select name="emp-qualification" id="view-emp-qualification">
                      <option value="Diploma">Diploma</option>
                      <option value="Bachelor">Bachelor</option>
                      <option value="Master">Master</option>
                      <option value="PHD">PHD</option>
                      <option value="Professor">Professor</option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit">Update</button>
              </div>
            </form>
          </div>
        </section>
        <!--################################################ Loading User Data Modal ###############################################-->
        <!--################################################ Add Employee ###############################################-->
        <section class="modal add-employee" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Add Employee</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/employee/add" method="post">
              <div class="form-container">
                <div class="inner-modal">
                  <div>
                    <label for="add-emp-name">Name</label>
                    <input
                      type="text"
                      name="emp-name"
                      id="add-emp-name"
                      placeholder="ex: John Doe"
                      required
                    />
                  </div>
                  <div class="inner-modal">
                    <label for="add-emp-gender">Gender</label>
                    <select name="emp-gender" id="add-emp-gender" required>
                      <option value="M">Male</option>
                      <option value="F">Female</option>
                      <option value="O">Other</option>
                    </select>
                  </div>
                  <div class="inner-modal">
                    <label for="add-emp-date">Start Date</label>
                    <input type="date" name="emp-date" id="add-emp-date" required />
                  </div>
                </div>
                <div>
                  <div class="inner-modal">
                    <label for="add-emp-position">Position</label>
                    <select name="emp-position" id="add-emp-position" required>
                      <% for(int i = 0; i < positionCodes.length; i++) { %>
                        <option value="<%= positionCodes[i] %>"><%= positionNames[i] %></option>
                      <% } %>
                    </select>
                  </div>
                  <div class="inner-modal">
                    <label for="add-emp-qualification">Qualification</label>
                    <select name="emp-qualification" id="add-emp-qualification" required>
                      <option value="Diploma">Diploma</option>
                      <option value="Bachelor">Bachelor</option>
                      <option value="Master">Master</option>
                      <option value="PHD">PHD</option>
                      <option value="Professor">Professor</option>
                    </select>
                  </div>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit">Add</button>
              </div>
            </form>
          </div>
        </section>
        <!--################################################ Delete Employee ###############################################-->
        <section class="modal delete-employee" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Delete</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/employee/delete" method="post">
              <div class="form-container">
                <div class="inner-modal">
                  <div>
                    <label for="delete-emp-id">Select Employee</label>
                    <select name="emp-id" id="delete-emp-id" required>
                      <% if(employees != null) {
                           for(Employee employee : employees) { %>
                        <option value="<%= employee.getEmpId() %>"><%= employee.getName() %></option>
                      <% } } %>
                    </select>
                  </div>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit" class="delete-button" style="background-color: red">
                  Delete
                </button>
              </div>
            </form>
          </div>
        </section>
        <!--################################################ Delete Employee ###############################################-->
        <!--################################################ Upload Profile Picture ###############################################-->
        <section class="modal upload-picture" aria-hidden="true">
          <div class="main-modal">
            <div class="modal-header">
              <h1>Upload Profile Picture</h1>
              <button class="modal-close">Close</button>
            </div>
            <form action="${pageContext.request.contextPath}/user/profile" method="post" enctype="multipart/form-data">
              <div class="form-container">
                <div class="inner-modal">
                  <div>
                    <h1 class="colored">Select Profile Picture</h1>
                    <div class="upload">
                      <input
                        type="file"
                        name="profile-picture"
                        id="profile-picture"
                        accept="image/*"
                        required
                      />
                    </div>
                  </div>
                  <div class="image-preview" style="margin-top: 1rem">
                    <img
                      id="preview"
                      src="${sessionScope.user.profilePicture != null ? pageContext.request.contextPath.concat('/assets/profile/').concat(sessionScope.user.profilePicture) : 'https://s3-alpha-sig.figma.com/img/916f/9ff0/fc94870afef9a39114bfad39f141e97b?Expires=1742169600&Key-Pair-Id=APKAQ4GOSFWCW27IBOMQ&Signature=nG5W9Sd79igp5ganPF1xYbMrW-g2rPZMg1xI~42Yfswrvz1zj-7iAWYgwkm3BNRP76A8Q~DkbY3sBY6jx06unA6maPuCzohBbsyCrwW323kdLT2ydJCCsSqlNS5bFKsKMGCgICVaxD4nQnE3O9JG7yYthLKayZ59ISPDIryEnZ1VaxWXEFWqupSqW0armfLaG2lffMoSL2hxqBNOBqHczSdCF4tnBGZpINuNHNWqmIhoZQGlmgSsH2B38rAQXXLrRpz-ftuUDor04EwRK5KRAGvKgA6QNJLX5f5oM4Qeu3cE6sRd7T-oE0WqWtdvg3KM-0Gx9ZGawaItD7sb0iatmQ__'}"
                      alt="Preview"
                      style="max-width: 200px; display: block"
                    />
                  </div>
                </div>
              </div>
              <div class="modal-button">
                <button type="submit">Upload</button>
              </div>
            </form>
          </div>
        </section>
        <!--################################################ Upload Profile Picture ###############################################-->
      </main>
      <footer></footer>
    </div>
    <script src="${pageContext.request.contextPath}/js/modal.js" defer></script>
    <script>
      document.addEventListener('DOMContentLoaded', function() {
        // Show success or error messages
        <% if (request.getSession().getAttribute("message") != null) { %>
          alert('<%= request.getSession().getAttribute("message") %>');
          <% request.getSession().removeAttribute("message"); %>
        <% } %>
        
        <% if (request.getSession().getAttribute("error") != null) { %>
          alert('<%= request.getSession().getAttribute("error") %>');
          <% request.getSession().removeAttribute("error"); %>
        <% } %>
      
        // Refresh button functionality
        document.getElementById('refreshBtn').addEventListener('click', function() {
          window.location.reload();
        });
        
        // Logout functionality
        document.querySelector('.logout').addEventListener('click', function() {
          document.getElementById('logoutForm').submit();
        });
        
        // View employee modal functionality
        document.querySelectorAll('.view-open').forEach(button => {
          button.addEventListener('click', function() {
            const employeeId = this.getAttribute('data-id');
            fetch('${pageContext.request.contextPath}/employee/get?id=' + employeeId)
              .then(response => response.json())
              .then(employee => {
                document.getElementById('view-emp-id').value = employee.empId;
                document.getElementById('view-emp-name').value = employee.name;
                document.getElementById('view-emp-gender').value = employee.gender;
                document.getElementById('view-emp-date').value = new Date(employee.startDate).toISOString().split('T')[0];
                document.getElementById('view-emp-position').value = employee.positionName;
                document.getElementById('view-emp-qualification').value = employee.qualification;
              })
              .catch(error => console.error('Error fetching employee data:', error));
          });
        });
        
        // File preview functionality
        document.getElementById('profile-picture').addEventListener('change', function(e) {
          const preview = document.getElementById('preview');
          const file = e.target.files[0];
          if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
              preview.src = e.target.result;
              preview.style.display = 'block';
            };
            reader.readAsDataURL(file);
          }
        });
        
        // Search functionality
        const searchInput = document.getElementById('searchInput');
        const searchButton = document.getElementById('searchButton');
        const employeeRows = document.querySelectorAll('.employee-row');
        
        function performSearch() {
          const searchTerm = searchInput.value.toLowerCase().trim();
          let foundResults = false;
          
          employeeRows.forEach(row => {
            const name = row.cells[0].textContent.toLowerCase();
            const position = row.cells[1].textContent.toLowerCase();
            const qualification = row.cells[4].textContent.toLowerCase();
            
            if (name.includes(searchTerm) || 
                position.includes(searchTerm) || 
                qualification.includes(searchTerm)) {
              row.style.display = '';
              foundResults = true;
            } else {
              row.style.display = 'none';
            }
          });
          
          // Show no results message if needed
          const noResultsRow = document.getElementById('no-results-row');
          if (!foundResults) {
            if (!noResultsRow) {
              const table = document.getElementById('employeeTable');
              const newRow = table.insertRow();
              newRow.id = 'no-results-row';
              const cell = newRow.insertCell(0);
              cell.colSpan = 6;
              cell.textContent = 'No matching employees found';
              cell.style.textAlign = 'center';
              cell.style.padding = '10px';
            } else {
              noResultsRow.style.display = '';
            }
          } else if (noResultsRow) {
            noResultsRow.style.display = 'none';
          }
        }
        
        searchButton.addEventListener('click', performSearch);
        
        searchInput.addEventListener('keyup', function(event) {
          if (event.key === 'Enter') {
            performSearch();
          }
        });
        
        // Open profile picture modal when clicking on profile image
        document.querySelector('.profile-image-container').addEventListener('click', function() {
          const modal = document.querySelector('.upload-picture');
          modal.removeAttribute('aria-hidden');
          modal.classList.add('show-modal');
        });
      });
    </script>
  </body>
</html>
