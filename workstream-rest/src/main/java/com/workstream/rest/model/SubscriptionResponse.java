package com.workstream.rest.model;

import java.util.Date;

import com.workstream.core.model.Subscription;

public class SubscriptionResponse extends InnerWrapperObj<Subscription> {

	private Subscription subscription;

	public SubscriptionResponse(Subscription subscription) {
		super(subscription);
		this.subscription = subscription;
	}

	public String getUserId() {
		return subscription.getUserId();
	}

	public String getTargetType() {
		return subscription.getTargetType();
	}

	public String getTargetId() {
		return subscription.getTargetId();
	}

	public Date getCreatedAt() {
		return subscription.getCreatedAt();
	}

}
