package com.appbasement.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.appbasement.exception.EmailSendingException;

@Service
public class EmailService implements IEmailService {

	@Autowired
	protected JavaMailSender mailSender;

	protected void checkInvalidArgs(String from, String to, String subject,
			String content) throws IllegalArgumentException {
		if (from == null || "".equals(from)) {
			throw new IllegalArgumentException("From is null or empty: " + from);
		}

		if (to == null || to.equals("")) {
			throw new IllegalArgumentException("To is null or empty: " + to);
		}

		if (subject == null || subject.equals("")) {
			throw new IllegalArgumentException("Content is null or empty: "
					+ content);
		}

		if (content == null || content.equals("")) {
			throw new IllegalArgumentException("Content is null: " + content);
		}
	}

	@Override
	public void sendSimpleEmail(String from, String to, String subject,
			String content) {
		checkInvalidArgs(from, to, subject, content);

		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(content);
		mailSender.send(message);
	}

	@Override
	public void sendRichEmail(String from, String to, String subject,
			String richContent) throws EmailSendingException {
		checkInvalidArgs(from, to, subject, richContent);

		MimeMessage message = mailSender.createMimeMessage();
		MimeMessageHelper helper;
		try {
			helper = new MimeMessageHelper(message, true);
			helper.setFrom(from);
			helper.setTo(to);
			helper.setSubject(subject);
			helper.setText(richContent, true);
			mailSender.send(message);
		} catch (MessagingException e) {
			throw new EmailSendingException(e);
		}
	}
}
