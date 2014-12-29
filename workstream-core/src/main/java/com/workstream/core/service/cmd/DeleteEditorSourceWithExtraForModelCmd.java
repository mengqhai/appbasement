package com.workstream.core.service.cmd;

import java.io.Serializable;

import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ByteArrayRef;
import org.activiti.engine.impl.persistence.entity.ModelEntity;
import org.activiti.engine.repository.Model;

public class DeleteEditorSourceWithExtraForModelCmd implements Command<Object>,
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1364752105779413503L;

	protected String modelId;

	protected Model model;

	public DeleteEditorSourceWithExtraForModelCmd(String modelId) {
		super();
		this.modelId = modelId;
	}

	public DeleteEditorSourceWithExtraForModelCmd(Model model) {
		super();
		this.model = model;
		this.modelId = model.getId();
	}

	@Override
	public Object execute(CommandContext commandContext) {
		if (model == null) {
			model = commandContext.getModelEntityManager().findModelById(
					modelId);
		}
		if (model != null) {
			ModelEntity entity = (ModelEntity) model;

			String sourceExtraId = entity.getEditorSourceExtraValueId();
			String sourceId = entity.getEditorSourceValueId();
			entity.setEditorSourceExtraValueId(null);
			entity.setEditorSourceValueId(null);
			commandContext.getModelEntityManager().updateModel(entity);
			// This removed the foreign reference in model

			if (sourceExtraId != null) {
				ByteArrayRef ref = new ByteArrayRef(sourceExtraId);
				ref.delete();
			}
			if (sourceId != null) {
				ByteArrayRef ref = new ByteArrayRef(sourceId);
				ref.delete();
			}
		}
		return null;
	}

}
