package com.project.demo.services.MailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;


import java.io.File;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    /** Send a simple text email. */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
    }

    /** Send an HTML email. */
    public void sendHtmlEmail(String to, String subject, String htmlBody) throws MessagingException, MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlBody, true);  // <true> = isHtml
        mailSender.send(mime);
    }

    /** Send email with an attachment. */
    public void sendEmailWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, false);
        FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
        helper.addAttachment(file.getFilename(), file);
        mailSender.send(mime);
    }

}
