package com.appbasement.service.email;

import com.appbasement.exception.EmailSendingException;

public interface IEmailService {

	public abstract void sendSimpleEmail(String from, String to,
			String subject, String content);

	public abstract void sendRichEmail(String from, String to, String subject,
			String richContent) throws EmailSendingException;

}
