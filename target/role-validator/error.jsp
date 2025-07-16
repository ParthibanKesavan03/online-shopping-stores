<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error - Role Validator</title>
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
        
        .error-container {
            background: white;
            padding: 2rem;
            border-radius: 10px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
            text-align: center;
            max-width: 500px;
            width: 100%;
        }
        
        .error-icon {
            font-size: 4rem;
            color: #dc3545;
            margin-bottom: 1rem;
        }
        
        .error-title {
            color: #333;
            margin-bottom: 1rem;
            font-size: 1.5rem;
        }
        
        .error-message {
            color: #666;
            margin-bottom: 2rem;
            line-height: 1.5;
        }
        
        .back-button {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 0.75rem 2rem;
            border: none;
            border-radius: 5px;
            font-size: 1rem;
            cursor: pointer;
            text-decoration: none;
            display: inline-block;
            transition: transform 0.2s ease;
        }
        
        .back-button:hover {
            transform: translateY(-2px);
        }
        
        .error-details {
            margin-top: 2rem;
            padding: 1rem;
            background-color: #f8f9fa;
            border-radius: 5px;
            text-align: left;
            font-family: monospace;
            font-size: 0.9rem;
            color: #495057;
        }
    </style>
</head>
<body>
    <div class="error-container">
        <div class="error-icon">⚠️</div>
        <h1 class="error-title">Oops! Something went wrong</h1>
        <p class="error-message">
            We're sorry, but an error occurred while processing your request. 
            Please try again or contact support if the problem persists.
        </p>
        
        <a href="<%=request.getContextPath()%>/" class="back-button">
            ← Back to Home
        </a>
        
        <% if (request.getAttribute("javax.servlet.error.status_code") != null) { %>
        <div class="error-details">
            <strong>Error Details:</strong><br>
            Status Code: <%= request.getAttribute("javax.servlet.error.status_code") %><br>
            <% if (request.getAttribute("javax.servlet.error.message") != null) { %>
            Message: <%= request.getAttribute("javax.servlet.error.message") %><br>
            <% } %>
            <% if (request.getAttribute("javax.servlet.error.request_uri") != null) { %>
            Request URI: <%= request.getAttribute("javax.servlet.error.request_uri") %><br>
            <% } %>
            Timestamp: <%= new java.util.Date() %>
        </div>
        <% } %>
    </div>
</body>
</html>