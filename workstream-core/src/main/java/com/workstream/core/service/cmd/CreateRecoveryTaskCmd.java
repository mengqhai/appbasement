package com.workstream.core.service.cmd;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.TaskEntity;
import org.activiti.engine.task.Task;

public class CreateRecoveryTaskCmd implements Command<Object> {

	private Task task;

	public CreateRecoveryTaskCmd(Task task) {
		this.task = task;
	}

	@Override
	public Void execute(CommandContext commandContext) {
		// see SaveTaskCmd.execute() and TaskEntity.insert()

		DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
		dbSqlSession.insert((TaskEntity) task);

		if (commandContext.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			commandContext
					.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.ENTITY_CREATED, task));
			commandContext
					.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.ENTITY_INITIALIZED, task));
		}
		if (Context.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			Context.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.TASK_CREATED, task));

			if (task.getAssignee() != null) {
				// The assignment event is normally fired when calling
				// setAssignee. However, this
				// doesn't work for standalone tasks as the commandcontext is
				// not availble.
				Context.getProcessEngineConfiguration()
						.getEventDispatcher()
						.dispatchEvent(
								ActivitiEventBuilder.createEntityEvent(
										ActivitiEventType.TASK_ASSIGNED, task));
			}
		}
		return null;
	}

}
