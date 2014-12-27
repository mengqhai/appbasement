package com.workstream.core.service;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ManagementService;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.CommentEntity;
import org.activiti.engine.impl.persistence.entity.CommentEntityManager;
import org.activiti.engine.task.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Works as a supplement for the DefaultHistoryManager to create event comments
 * for more properties in Task.
 * 
 * @author qinghai
 * 
 */
@Component
public class TaskEventHelper {

	private static final Set<String> monitorProps;
	
	@Autowired
	private ManagementService mgmtService;

	public static class TaskEventCommand implements Command<Event> {

		private String taskId, authenticatedUserId, action;
		private String[] messageParts;

		public TaskEventCommand(String taskId, String authenticatedUserId,
				String[] messageParts, String action) {
			this.taskId = taskId;
			this.authenticatedUserId = authenticatedUserId;
			this.messageParts = messageParts;
			this.action = action;
		}

		@Override
		public Event execute(CommandContext commandContext) {
			CommentEntity comment = new CommentEntity();
			comment.setUserId(authenticatedUserId);
			comment.setType(CommentEntity.TYPE_EVENT);
			comment.setTime(Context.getProcessEngineConfiguration().getClock()
					.getCurrentTime());
			comment.setTaskId(taskId);
			comment.setMessage(messageParts);
			comment.setAction(action);
			// persist the comment
			Context.getCommandContext().getSession(CommentEntityManager.class)
					.insert(comment);
			return comment;
		}

	}

	static {
		Set<String> keys = new HashSet<String>();
		keys.add("dueDate");
		keys.add("name");
		keys.add("description");

		monitorProps = Collections.unmodifiableSet(keys);
	}

	public void createEventCommentIfNeeded(String taskId,
			Map<String, ? extends Object> props, String authenticatedUserId) {
		for (String key : props.keySet()) {
			if (monitorProps.contains(key)) {
				Object value = props.get(key);
				String valueStr = null;
				if (value != null) {
					if (value instanceof Date) {
						valueStr = String.valueOf(((Date) value).getTime());
					} else {
						valueStr = value.toString();
					}
				}

				TaskEventCommand cmd = new TaskEventCommand(taskId,
						authenticatedUserId, new String[] { valueStr, key },
						new StringBuilder("Set_").append(key).toString());
				mgmtService.executeCommand(cmd);
			}
		}
	}

	public void createEventCommentIfNeeded(String taskId,
			Map<String, ? extends Object> props) {
		String authenticatedUserId = Authentication.getAuthenticatedUserId();
		createEventCommentIfNeeded(taskId, props, authenticatedUserId);
	}

}
