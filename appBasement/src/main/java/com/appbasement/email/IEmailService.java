package com.appbasement.email;

import java.util.Map;

import com.appbasement.exception.EmailSendingException;

public interface IEmailService {

	public abstract void sendSimpleEmail(String from, String to,
			String subject, String content);

	public abstract void sendRichEmail(String from, String to,
			String subject, String richContent) throws EmailSendingException;

	public abstract void sendTemplateRichEmail(String from, String to,
			String subject, String templateName, Map<String, Object> model, String encoding);

}
