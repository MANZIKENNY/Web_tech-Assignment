document.getElementById('login-toggle').addEventListener('click', function() {
    document.getElementById('login-form').classList.add('active');
    document.getElementById('signup-form').classList.remove('active');
});

document.getElementById('signup-toggle').addEventListener('click', function() {
    document.getElementById('signup-form').classList.add('active');
    document.getElementById('login-form').classList.remove('active');
});

document.getElementById('switch-to-signup').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('signup-form').classList.add('active');
    document.getElementById('login-form').classList.remove('active');
});

document.getElementById('switch-to-login').addEventListener('click', function(e) {
    e.preventDefault();
    document.getElementById('login-form').classList.add('active');
    document.getElementById('signup-form').classList.remove('active');
});
