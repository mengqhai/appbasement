package com.workstream.core.service;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.activiti.engine.TaskService;
import org.activiti.engine.task.Attachment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.BinaryObj;
import com.workstream.core.model.BinaryObj.BinaryObjType;
import com.workstream.core.model.BinaryObj.BinaryReposType;
import com.workstream.core.persistence.IBinaryObjDAO;
import com.workstream.core.utils.ThumbnailCreator;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class AttachmentService {

	@Autowired
	protected TaskService taskSer;

	/**
	 * My own binary DAO to store attachment content
	 */
	@Autowired
	protected IBinaryObjDAO binaryDao;

	private ThumbnailCreator thumbCreator = new ThumbnailCreator();

	private Attachment createAttachment(String taskId,
			String processInstanceId, String type, String attachmentName,
			String attachmentDescription, InputStream content, long size) {
		BinaryObj binary = BinaryObj.newBinaryObj(
				BinaryObjType.ATTACHMENT_CONTENT, null,
				BinaryReposType.FILE_SYSTEM_REPOSITORY, type, attachmentName);
		binaryDao.persistInputStreamToContent(content, binary);
		Attachment attachment = taskSer.createAttachment(type, taskId,
				processInstanceId, attachmentName, attachmentDescription,
				(String) null);
		binary.setTargetId(attachment.getId());

		if (type.startsWith("image")) {
			// create a thumbnail
			BinaryObj thumbnail = BinaryObj.newBinaryObj(
					BinaryObjType.ATTACHMENT_THUMB, attachment.getId(),
					BinaryReposType.FILE_SYSTEM_REPOSITORY, type, "thumbnail_"
							+ attachmentName);

			// have to read the original image
			InputStream originalImage = binaryDao.getContentStream(binary);
			InputStream thumb = thumbCreator.createThumbnail(originalImage);
			binaryDao.persistInputStreamToContent(thumb, thumbnail);
		}
		return attachment;
	}

	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public Attachment createTaskAttachment(String taskId, String type,
			String attachmentName, String attachmentDescription,
			InputStream content, long size) {
		return createAttachment(taskId, null, type, attachmentName,
				attachmentDescription, content, size);
	}

	/**
	 * Task id is optional
	 * 
	 * @param processId
	 * @param type
	 * @param attachmentName
	 * @param attachmentDescription
	 * @param content
	 * @param size
	 * @param taskId
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public Attachment createProcessAttachment(String processId, String type,
			String attachmentName, String attachmentDescription,
			InputStream content, long size, String taskId) {
		return createAttachment(taskId, processId, type, attachmentName,
				attachmentDescription, content, size);
	}

	public BinaryObj getAttachmentThumbBinaryObj(String attachmentId) {
		BinaryObj binary = binaryDao.getBinaryObjByTarget(
				BinaryObjType.ATTACHMENT_THUMB, attachmentId);
		return binary;
	}

	public BinaryObj getAttachmentBinaryObj(String attachmentId) {
		BinaryObj binary = binaryDao.getBinaryObjByTarget(
				BinaryObjType.ATTACHMENT_CONTENT, attachmentId);
		return binary;
	}

	public List<Attachment> filterTaskAttachment(String taskId) {
		return taskSer.getTaskAttachments(taskId);
	}

	public List<Attachment> filterProcessAttachment(String processId) {
		return taskSer.getProcessInstanceAttachments(processId);
	}

	public Attachment getAttachment(String attachmentId) {
		return taskSer.getAttachment(attachmentId);
	}

	@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
	public long outputBinaryObjContent(OutputStream os, BinaryObj binary) {
		return binaryDao.outputContent(os, binary);
	}

}
