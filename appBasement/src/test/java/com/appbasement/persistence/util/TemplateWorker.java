package com.appbasement.persistence.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * An implementation of the template pattern.
 * 
 * @author qinghai
 * 
 */
public abstract class TemplateWorker<T> {

	protected EntityManager em;

	protected T result;

	/**
	 * Construct the TemplateWorker and <b>immediately</b> execute doIt() in a
	 * transaction.
	 * 
	 * @param em
	 */
	public TemplateWorker(EntityManager em) {
		this(em, true);
	}

	public TemplateWorker(EntityManager em, boolean immediateExecute) {
		this.em = em;
		if (immediateExecute)
			this.executeInTransaction();
	}

	protected abstract void doIt();

	public void executeInTransaction() {
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			// logic
			doIt();
			tx.commit();
		} catch (Exception e) {
			throw e;
		} finally {
			if (tx != null && tx.isActive()) {
				tx.rollback();
			}
		}
	}

	public T getResult() {
		return result;
	}

	protected void setResult(T result) {
		this.result = result;
	}
}
