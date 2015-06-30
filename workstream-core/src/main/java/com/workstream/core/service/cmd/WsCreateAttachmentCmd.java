package com.workstream.core.service.cmd;

import java.io.InputStream;

import org.activiti.engine.delegate.event.ActivitiEventType;
import org.activiti.engine.delegate.event.impl.ActivitiEventBuilder;
import org.activiti.engine.impl.db.DbSqlSession;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.AttachmentEntity;
import org.activiti.engine.impl.persistence.entity.ByteArrayEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.util.IoUtil;
import org.activiti.engine.task.Attachment;

/**
 * Activiti implementation doesn't allow to add attachment to archived tasks, so
 * here we provide our own.
 * 
 * @author qinghai
 * 
 */
public class WsCreateAttachmentCmd implements Command<Attachment> {

	protected String attachmentType;
	protected String taskId;
	protected String processInstanceId;
	protected String attachmentName;
	protected String attachmentDescription;
	protected InputStream content;
	protected String url;

	public WsCreateAttachmentCmd(String attachmentType, String taskId,
			String processInstanceId, String attachmentName,
			String attachmentDescription, InputStream content, String url) {
		this.attachmentType = attachmentType;
		this.taskId = taskId;
		this.processInstanceId = processInstanceId;
		this.attachmentName = attachmentName;
		this.attachmentDescription = attachmentDescription;
		this.content = content;
		this.url = url;
	}

	public Attachment execute(CommandContext commandContext) {

		AttachmentEntity attachment = new AttachmentEntity();
		attachment.setName(attachmentName);
		attachment.setDescription(attachmentDescription);
		attachment.setType(attachmentType);
		attachment.setTaskId(taskId);
		attachment.setProcessInstanceId(processInstanceId);
		attachment.setUrl(url);
		attachment.setUserId(Authentication.getAuthenticatedUserId());

		DbSqlSession dbSqlSession = commandContext.getDbSqlSession();
		dbSqlSession.insert(attachment);

		if (content != null) {
			byte[] bytes = IoUtil.readInputStream(content, attachmentName);
			ByteArrayEntity byteArray = ByteArrayEntity.createAndInsert(bytes);
			attachment.setContentId(byteArray.getId());
		}

		commandContext.getHistoryManager().createAttachmentComment(taskId,
				processInstanceId, attachmentName, true);

		if (commandContext.getProcessEngineConfiguration().getEventDispatcher()
				.isEnabled()) {
			// Forced to fetch the process-instance to associate the right
			// process definition
			String processDefinitionId = null;
			if (attachment.getProcessInstanceId() != null) {
				ExecutionEntity process = commandContext
						.getExecutionEntityManager().findExecutionById(
								processInstanceId);
				if (process != null) {
					processDefinitionId = process.getProcessDefinitionId();
				}
			}

			commandContext
					.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.ENTITY_CREATED,
									attachment, processInstanceId,
									processInstanceId, processDefinitionId));
			commandContext
					.getProcessEngineConfiguration()
					.getEventDispatcher()
					.dispatchEvent(
							ActivitiEventBuilder.createEntityEvent(
									ActivitiEventType.ENTITY_INITIALIZED,
									attachment, processInstanceId,
									processInstanceId, processDefinitionId));
		}

		return attachment;
	}

}
