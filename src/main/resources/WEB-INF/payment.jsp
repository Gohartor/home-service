<!-- src/main/webapp/WEB-INF/jsp/payment.html -->
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Payment Page</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 20px; }
        .container { max-width: 500px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px; }
        label { display: block; margin-bottom: 5px; font-weight: bold; }
        input[type="text"], input[type="password"] { width: calc(100% - 22px); padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px; }
        button { background-color: #4CAF50; color: white; padding: 10px 15px; border: none; border-radius: 4px; cursor: pointer; }
        button:hover { background-color: #45a049; }
        #countdown { font-size: 1.2em; color: #d9534f; margin-bottom: 15px; }
        #message { margin-top: 15px; padding: 10px; border-radius: 4px; }
        .success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .error { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>
<div class="container">
    <h1>Complete Your Payment for Order: ${orderId}</h1>
    <div id="countdown">Time remaining: <span id="timer"></span></div>

    <form id="paymentForm">
        <input type="hidden" id="paymentSessionToken" value="${token}">

        <label for="cardNumber">Card Number:</label>
        <input type="text" id="cardNumber" name="cardNumber" required placeholder="16-digit card number">

        <label for="expiryDate">Expiry Date (MM/YY):</label>
        <input type="text" id="expiryDate" name="expiryDate" required placeholder="MM/YY">

        <label for="cvv">CVV:</label>
        <input type="text" id="cvv" name="cvv" required placeholder="3 or 4 digits">

        <label for="captcha">Enter Captcha:</label>
        <img src="${captchaImage}" alt="Captcha Image" style="margin-bottom: 10px; border: 1px solid #ddd;">
        <input type="text" id="captcha" name="captcha" required placeholder="Enter characters from image">

        <button type="submit">Pay Now</button>
    </form>
    <div id="message"></div>
</div>

<script>
    <%
    System.out.println("hi in jsp");
    %>
    const paymentSessionToken = document.getElementById('paymentSessionToken').value;
    const expiresAt = new Date("${expiresAt}");

    function updateCountdown() {
        const now = new Date();
        const timeLeft = expiresAt - now;

        if (timeLeft <= 0) {
            clearInterval(timerInterval);
            document.getElementById('timer').textContent = "Expired!";
            document.getElementById('paymentForm').style.pointerEvents = 'none';
            document.getElementById('message').className = 'error';
            document.getElementById('message').textContent = 'Payment session has expired.';
            return;
        }

        const minutes = Math.floor((timeLeft % (1000 * 60 * 60)) / (1000 * 60));
        const seconds = Math.floor((timeLeft % (1000 * 60)) / 1000);
        document.getElementById('timer').textContent = `${minutes}m ${seconds}s`;
    }

    const timerInterval = setInterval(updateCountdown, 1000);
    updateCountdown();

    document.getElementById('paymentForm').addEventListener('submit', async function(event) {
        event.preventDefault();

        const cardNumber = document.getElementById('cardNumber').value;
        const expiryDate = document.getElementById('expiryDate').value;
        const cvv = document.getElementById('cvv').value;
        const captcha = document.getElementById('captcha').value;
        const messageDiv = document.getElementById('message');



        try {
            const response = await fetch('/payment/process', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    token: paymentSessionToken,
                    captcha: captcha,
                    cardNumber: cardNumber,
                    expiryDate: expiryDate,
                    cvv: cvv
                })
            });

            const responseText = await response.text();

            if (response.ok) {
                messageDiv.className = 'success';
                messageDiv.textContent = responseText;
                document.getElementById('paymentForm').reset();
            } else {
                messageDiv.className = 'error';
                try {
                    const errorData = JSON.parse(responseText);
                    messageDiv.textContent = errorData.message || 'Payment failed.';
                } catch (e) {
                    messageDiv.textContent = responseText || 'Payment failed.';
                }
            }
        } catch (error) {
            console.error('Error:', error);
            messageDiv.className = 'error';
            messageDiv.textContent = 'An unexpected error occurred.';
        }
    });
</script>
</body>
</html>
