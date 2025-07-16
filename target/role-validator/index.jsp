<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Role Validator</title>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        
        .container {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            width: 100%;
            max-width: 500px;
        }
        
        .header {
            text-align: center;
            margin-bottom: 2rem;
        }
        
        .header h1 {
            color: #333;
            margin-bottom: 0.5rem;
        }
        
        .header p {
            color: #666;
            font-size: 0.9rem;
        }
        
        .form-group {
            margin-bottom: 1.5rem;
        }
        
        .form-group label {
            display: block;
            margin-bottom: 0.5rem;
            color: #333;
            font-weight: 500;
        }
        
        .form-group input, .form-group textarea {
            width: 100%;
            padding: 0.75rem;
            border: 2px solid #e1e5e9;
            border-radius: 5px;
            font-size: 1rem;
            transition: border-color 0.3s ease;
        }
        
        .form-group input:focus, .form-group textarea:focus {
            outline: none;
            border-color: #667eea;
        }
        
        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }
        
        .btn {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 0.75rem 2rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            transition: transform 0.2s ease;
            width: 100%;
        }
        
        .btn:hover {
            transform: translateY(-2px);
        }
        
        .btn:disabled {
            opacity: 0.6;
            cursor: not-allowed;
            transform: none;
        }
        
        .result {
            margin-top: 2rem;
            padding: 1rem;
            border-radius: 5px;
            display: none;
        }
        
        .result.success {
            background-color: #d4edda;
            border: 1px solid #c3e6cb;
            color: #155724;
        }
        
        .result.error {
            background-color: #f8d7da;
            border: 1px solid #f5c6cb;
            color: #721c24;
        }
        
        .result.info {
            background-color: #d1ecf1;
            border: 1px solid #bee5eb;
            color: #0c5460;
        }
        
        .loading {
            display: none;
            text-align: center;
            margin-top: 1rem;
        }
        
        .spinner {
            border: 3px solid #f3f3f3;
            border-top: 3px solid #667eea;
            border-radius: 50%;
            width: 30px;
            height: 30px;
            animation: spin 1s linear infinite;
            margin: 0 auto;
        }
        
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>Role Validator</h1>
            <p>Enter role details to validate using REST API</p>
        </div>
        
        <form id="roleForm">
            <div class="form-group">
                <label for="roleName">Role Name *</label>
                <input type="text" id="roleName" name="roleName" required placeholder="Enter role name">
            </div>
            
            <div class="form-group">
                <label for="roleDescription">Role Description *</label>
                <textarea id="roleDescription" name="roleDescription" required placeholder="Enter role description"></textarea>
            </div>
            
            <button type="submit" class="btn" id="validateBtn">Validate Role</button>
        </form>
        
        <div class="loading" id="loading">
            <div class="spinner"></div>
            <p>Validating role...</p>
        </div>
        
        <div class="result" id="result"></div>
    </div>

    <script>
        document.getElementById('roleForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            const roleName = document.getElementById('roleName').value.trim();
            const roleDescription = document.getElementById('roleDescription').value.trim();
            const validateBtn = document.getElementById('validateBtn');
            const loading = document.getElementById('loading');
            const result = document.getElementById('result');
            
            // Validate inputs
            if (!roleName || !roleDescription) {
                showResult('Please fill in all required fields.', 'error');
                return;
            }
            
            // Show loading state
            validateBtn.disabled = true;
            loading.style.display = 'block';
            result.style.display = 'none';
            
            // Prepare form data
            const formData = new FormData();
            formData.append('roleName', roleName);
            formData.append('roleDescription', roleDescription);
            
            // Make AJAX request to servlet
            fetch('/role-validator/validate', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json())
            .then(data => {
                hideLoading();
                if (data.success) {
                    showResult(data.message || 'Role validation successful!', 'success');
                } else {
                    showResult(data.message || 'Role validation failed.', 'error');
                }
            })
            .catch(error => {
                hideLoading();
                console.error('Error:', error);
                showResult('An error occurred while validating the role. Please try again.', 'error');
            });
        });
        
        function showResult(message, type) {
            const result = document.getElementById('result');
            result.className = 'result ' + type;
            result.innerHTML = '<strong>' + (type === 'success' ? 'Success!' : type === 'error' ? 'Error!' : 'Info!') + '</strong><br>' + message;
            result.style.display = 'block';
        }
        
        function hideLoading() {
            document.getElementById('validateBtn').disabled = false;
            document.getElementById('loading').style.display = 'none';
        }
    </script>
</body>
</html>