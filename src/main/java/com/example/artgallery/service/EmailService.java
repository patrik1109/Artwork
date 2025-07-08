package com.example.artgallery.service;

import com.example.artgallery.entity.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendOrderNotification(OrderRequest order) {
        try {
            // Email to administrator
            SimpleMailMessage adminMessage = new SimpleMailMessage();
            String fromEmail = "patrikeevegor@gmail.com".trim();
            String toEmail = "patrikeevegor@gmail.com".trim();
            adminMessage.setFrom(fromEmail);
            adminMessage.setTo(toEmail);
            adminMessage.setSubject("New Order #" + order.getId());
            String adminText = String.format("""
                New order from customer:
                
                Order number: %d
                Customer name: %s
                Customer email: %s
                Phone: %s
                
                Order details:
                %s
                
                Total amount: %s
                """,
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getPhoneNumber(),
                formatOrderDetails(order),
                order.getTotalPrice()
            );
            adminMessage.setText(adminText);
            emailSender.send(adminMessage);

            // Email to customer
            if (order.getCustomerEmail() != null && !order.getCustomerEmail().isBlank()) {
                SimpleMailMessage clientMessage = new SimpleMailMessage();
                clientMessage.setFrom(fromEmail);
                clientMessage.setTo(order.getCustomerEmail());
                clientMessage.setSubject("Your order has been received!");
                clientMessage.setText("Thank you for your order! We will contact you soon.");
                emailSender.send(clientMessage);
            }
            System.out.println("Email sent successfully!");
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method for sending purchase confirmation
    public void sendPurchaseConfirmation(String customerEmail, String photoTitle, String transactionId) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String fromEmail = "patrikeevegor@gmail.com".trim();
            message.setFrom(fromEmail);
            message.setTo(customerEmail);
            message.setSubject("Photo Purchase Confirmation");
            
            String text = String.format("""
                Thank you for your purchase!
                
                Photo: %s
                Transaction ID: %s
                
                Your purchase has been created. Please wait for payment confirmation.
                
                Best regards,
                Art Gallery Team
                """, photoTitle, transactionId);
            
            message.setText(text);
            emailSender.send(message);
            System.out.println("Purchase confirmation email sent to: " + customerEmail);
        } catch (Exception e) {
            System.err.println("Error sending purchase confirmation email: " + e.getMessage());
        }
    }
    
    // Method for sending download link
    public void sendDownloadLink(String customerEmail, String photoTitle, String downloadToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String fromEmail = "patrikeevegor@gmail.com".trim();
            message.setFrom(fromEmail);
            message.setTo(customerEmail);
            message.setSubject("Photo Download Link");
            
            String downloadUrl = "http://localhost:3000/download?token=" + downloadToken;
            
            String text = String.format("""
                Your purchase has been confirmed!
                
                Photo: %s
                Download link: %s
                
                This link is valid for 7 days.
                
                Best regards,
                Art Gallery Team
                """, photoTitle, downloadUrl);
            
            message.setText(text);
            emailSender.send(message);
            System.out.println("Download link email sent to: " + customerEmail);
        } catch (Exception e) {
            System.err.println("Error sending download link email: " + e.getMessage());
        }
    }

    private String formatOrderDetails(OrderRequest order) {
        // TODO: Add order details formatting (list of artworks)
        return "List of artworks will be added later";
    }
    
    // Method for sending order status update notification
    public void sendOrderStatusUpdate(OrderRequest order) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            String fromEmail = "patrikeevegor@gmail.com".trim();
            message.setFrom(fromEmail);
            message.setTo(order.getCustomerEmail());
            message.setSubject("Order Status Update - Order #" + order.getId());
            
            String text = String.format("""
                Dear %s,
                
                Your order status has been updated.
                
                Order number: %d
                New status: %s
                
                We will contact you soon with further details.
                
                Best regards,
                Art Gallery Team
                """, 
                order.getCustomerName(),
                order.getId(),
                order.getStatus()
            );
            
            message.setText(text);
            emailSender.send(message);
            System.out.println("Order status update email sent to: " + order.getCustomerEmail());
        } catch (Exception e) {
            System.err.println("Error sending order status update email: " + e.getMessage());
        }
    }
} 