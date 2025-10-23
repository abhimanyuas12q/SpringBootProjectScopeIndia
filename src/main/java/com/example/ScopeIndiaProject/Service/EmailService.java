package com.example.ScopeIndiaProject.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;
    public void regMail(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Registered Successfully");
        message.setText("Your details have submitted successfully!!");
        mailSender.send(message);
    }

    public void sendUser(String toEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Confirmation Mail");
        message.setText("Thank you for your enquiry.\n" +
                        "We have received your request and our team will get back to you shortly. If you have any urgent queries in the meantime, please feel free to contact us.");
        mailSender.send(message);
    }

     public void sendToAdmin(String name,String email,String subject, String message) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo("abhimanyuas12q@gmail.com");
        msg.setSubject("New contact us Submission");
        msg.setText("You have received a new contact form submission:\n\n" +"Name: "+ name +"\n" +"Email: "+email+"\n"+"Subject: " + subject+"\n"+"Message:\n"+message );
        mailSender.send(msg);
    }

    public void otpMailSend(String to, String otp) {
        System.out.println("Sending OTP mail to: " + to + " with OTP: " + otp);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Your OTP code");
        message.setText("Your OTP is : " + otp);
        mailSender.send(message);
    }
    public void otpCodeMailSend(String to, String otp) {
    System.out.println("Sending OTP mail to: " + to + " with OTP: " + otp);
    SimpleMailMessage message = new SimpleMailMessage();
    message.setTo(to);
    message.setSubject("Password Reset OTP");
    message.setText("Your OTP is : " + otp);
    mailSender.send(message);
}
}
