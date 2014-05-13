package com.angularjsplay.service.beanprocessor;

import org.springframework.stereotype.Component;

import com.angularjsplay.exception.ScrumValidationException;
import com.angularjsplay.model.Task;
import com.appbasement.component.beanprocessor.GenericBeanProcessor;

@Component
public class TaskProcessor extends GenericBeanProcessor<Task> {

	public void validateTask(Task task) {
		if (task.getEstimation() == null || task.getRemaining() == null) {
			return;
		}

		Short est = task.getEstimation();
		Short rem = task.getRemaining();
		if (rem > est) {
			throw new ScrumValidationException(
					"Remaining is bigger than estimation.");
		}
	}

	@Override
	public void doBeforeCreate(Task bean) {
		validateTask(bean);
	}

	@Override
	public void doBeforeUpdateWithPatch(Task bean, Task patch) {
		validateTask(bean);
	}

}
