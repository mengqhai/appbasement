package com.appbasement.email;

public interface IEmailService {

	public abstract void sendSimpleEmail(String from, String to,
			String subject, String content);

}
