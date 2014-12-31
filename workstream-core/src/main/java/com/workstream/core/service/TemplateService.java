package com.workstream.core.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ManagementService;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.Model;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversionFactory;
import org.activiti.workflow.simple.converter.json.SimpleWorkflowJsonConverter;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.model.ProcessModelMetaInfo;
import com.workstream.core.service.cmd.DeleteEditorSourceWithExtraForModelCmd;
import com.workstream.core.worflow.simple.CoreWorkflowDefinitionConversionFactory;

/**
 * Process template(deployment & process definitions) related service.
 * 
 * @author qinghai
 * 
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, value = CoreConstants.TX_MANAGER)
public class TemplateService {

	private final static Logger log = LoggerFactory
			.getLogger(TemplateService.class);

	@Autowired
	private RepositoryService repoSer;

	private WorkflowDefinitionConversionFactory conFactory = new CoreWorkflowDefinitionConversionFactory();

	private SimpleWorkflowJsonConverter jsonCon = new SimpleWorkflowJsonConverter();

	@Autowired
	private ProcessDiagramGenerator diagramGenerator;

	@Autowired
	private ProcessEngineConfiguration peCfg;

	@Autowired
	private ManagementService mgmtService;

	@Autowired
	private ProcessModelMetaInfoHelper metaHelper;

	public Deployment deployFile(Long orgId, String fileName, InputStream in) {
		DeploymentBuilder deploymentBuilder = repoSer.createDeployment()
				.name(fileName).tenantId(String.valueOf(orgId));
		if (fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn")) {
			deploymentBuilder.addInputStream(fileName, in);
		} else if (fileName.endsWith(".bar") || fileName.endsWith(".zip")) {
			deploymentBuilder.addZipInputStream(new ZipInputStream(in));
		} else {
			throw new RuntimeException(
					"Unable to deside the deployment type, have you correctly set the file extension? "
							+ fileName);
		}
		return deploymentBuilder.deploy();
	}

	public Deployment getDeployment(String deploymentId) {
		return repoSer.createDeploymentQuery().deploymentId(deploymentId)
				.singleResult();
	}

	public List<Deployment> filterDeployment(Long orgId) {
		return repoSer.createDeploymentQuery()
				.deploymentTenantId(String.valueOf(orgId)).list();
	}

	/**
	 * non-cascading deletion
	 * 
	 * @param deploymentId
	 */
	public void removeDeployment(String deploymentId) {
		repoSer.deleteDeployment(deploymentId);
	}

	public List<ProcessDefinition> filterProcessTemplate(Long orgId) {
		return repoSer.createProcessDefinitionQuery()
				.processDefinitionTenantId(String.valueOf(orgId)).list();
	}

	public List<ProcessDefinition> filterProcessTemplate(String deploymentId) {
		return repoSer.createProcessDefinitionQuery()
				.deploymentId(deploymentId).list();
	}

	/**
	 * Return single ProcessDefinition for a deployment. If there are more than
	 * one ProcessDefinition for the deployment, an ActivitiException will be
	 * thrown. See AbstractQuery.executeSingleResult().
	 * 
	 * @param deploymentId
	 * @return
	 */
	public ProcessDefinition getProcessTemplateByDeployment(String deploymentId) {
		return repoSer.createProcessDefinitionQuery()
				.deploymentId(deploymentId).singleResult();
	}

	public Model createModel(Long orgId, String name) {
		Model model = repoSer.newModel();
		model.setName(name);
		model.setTenantId(String.valueOf(orgId));
		repoSer.saveModel(model);
		return model;
	}

	public Model getModel(String modelId) {
		Model model = repoSer.createModelQuery().modelId(modelId)
				.singleResult();
		if (model == null) {
			log.warn("Unable to find the model id={} ", modelId);
		}
		return model;
	}

	public List<Model> filterModel(Long orgId) {
		return repoSer.createModelQuery().modelTenantId(String.valueOf(orgId))
				.list();
	}

	public List<Model> filterModelByDeploymentId(String deploymentId) {
		return repoSer.createModelQuery().deploymentId(deploymentId).list();
	}

	public void removeModel(String modelId) {
		repoSer.deleteModel(modelId);
	}

	/**
	 * Convert a simple WorkflowDefinition to a Model and save it.
	 * 
	 * @return
	 */
	public Model saveToModel(Long orgId, WorkflowDefinition flow) {
		WorkflowDefinitionConversion con = conFactory
				.createWorkflowDefinitionConversion(flow);
		con.convert();

		Model model = repoSer.newModel();
		model.setName(flow.getName());
		model.setTenantId(String.valueOf(orgId));
		model.setCategory("table-editor");
		String authorId = Authentication.getAuthenticatedUserId();
		ProcessModelMetaInfo metaInfo = metaHelper.addRevision(authorId, null,
				null);
		metaInfo.setName(flow.getName());
		metaInfo.setDescription(flow.getDescription());
		model.setMetaInfo(metaHelper.getStr(metaInfo));
		repoSer.saveModel(model);

		try {
			// See SimpleTableEditor.save()
			// Write JSON to byte-array and set as editor-source
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			jsonCon.writeWorkflowDefinition(flow, new OutputStreamWriter(baos));
			// source saved in byte array and linked to the model entity
			repoSer.addModelEditorSource(model.getId(), baos.toByteArray());

			// create and save the process diagram
			InputStream diaIn = diagramGenerator.generateDiagram(
					con.getBpmnModel(), "png", peCfg.getActivityFontName(),
					peCfg.getLabelFontName(), peCfg.getClassLoader());
			// InputStream diaIn = diagramGenerator.generateDiagram(
			// con.getBpmnModel(), "png", "sansserif", "sansserif",
			// peCfg.getClassLoader());
			// to support chinese

			repoSer.addModelEditorSourceExtra(model.getId(),
					IOUtils.toByteArray(diaIn));
		} catch (Exception e) {
			log.error("Failed to save model source and extra.", e);
			throw new RuntimeException(
					"Failed to save model source and extra.", e);
		}
		return model;
	}

	public ProcessModelMetaInfo getModelMetaInfo(Model model) {
		return metaHelper.getMetaInfo(model.getMetaInfo());
	}

	/**
	 * Update the model with a new WorkflowDefinition. The old source &
	 * sourceExtra of the model will be deteted.
	 * 
	 * @param orgId
	 * @param modelId
	 * @param flow
	 * @return
	 */
	public Model updateModel(Long orgId, String modelId, WorkflowDefinition flow) {
		WorkflowDefinitionConversion con = conFactory
				.createWorkflowDefinitionConversion(flow);
		con.convert();
		Model model = repoSer.getModel(modelId);
		model.setName(flow.getName());
		model.setTenantId(String.valueOf(orgId));
		model.setCategory("table-editor");
		// TODO add revision meta info
		repoSer.saveModel(model);

		try {
			// See SimpleTableEditor.save()
			// Write JSON to byte-array and set as editor-source
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			jsonCon.writeWorkflowDefinition(flow, new OutputStreamWriter(baos));

			// create and save the process diagram
			InputStream diaIn = diagramGenerator.generateDiagram(
					con.getBpmnModel(), "png", peCfg.getActivityFontName(),
					peCfg.getLabelFontName(), peCfg.getClassLoader());
			// InputStream diaIn = diagramGenerator.generateDiagram(
			// con.getBpmnModel(), "png", "sansserif", "sansserif",
			// peCfg.getClassLoader());
			// to support chinese

			// must delete the old source and extra first
			mgmtService
					.executeCommand(new DeleteEditorSourceWithExtraForModelCmd(
							model));
			// after this the ModelEntity's sourceId and sourceExtraId are set
			// to null

			// source saved in byte array and linked to the model entity
			repoSer.addModelEditorSource(model.getId(), baos.toByteArray());
			repoSer.addModelEditorSourceExtra(model.getId(),
					IOUtils.toByteArray(diaIn));

		} catch (Exception e) {
			log.error("Failed to save model source and extra.", e);
			throw new RuntimeException(
					"Failed to save model source and extra.", e);
		}
		return model;
	}

	/**
	 * Duplicate a model. The newly created copy has separated source &
	 * sourceExtra from the original model.
	 * 
	 * @param modelId
	 * @return
	 */
	public Model duplicateModel(String modelId) {
		Model source = getModel(modelId);
		Long orgId = null;
		String orgIdStr = source.getTenantId();
		if (orgIdStr != null) {
			orgId = Long.valueOf(orgIdStr);
		}
		WorkflowDefinition def = getModelWorkflowDef(modelId);
		def.setName(new StringBuilder(def.getName()).append("_copy").toString());
		Model target = saveToModel(orgId, def);
		return target;
	}

	public byte[] getModelJsonBytes(String modelId) {
		return repoSer.getModelEditorSource(modelId);
	}

	public WorkflowDefinition getModelWorkflowDef(String modelId) {
		WorkflowDefinition def = jsonCon
				.readWorkflowDefinition(getModelJsonBytes(modelId));
		return def;
	}

	public byte[] getModelDiagram(String modelId) {
		return repoSer.getModelEditorSourceExtra(modelId);
	}

	public Deployment deployModel(String modelId) {
		WorkflowDefinition def = getModelWorkflowDef(modelId);
		// see EditorProcessDefinitionDetailPanel.deployModel()
		WorkflowDefinitionConversion con = conFactory
				.createWorkflowDefinitionConversion(def);
		con.convert();
		BpmnModel bpmn = con.getBpmnModel();
		Model model = getModel(modelId);
		Deployment deploy = repoSer.createDeployment()
				.addBpmnModel(def.getName() + ".bpmn", bpmn)
				.name(def.getName()).tenantId(model.getTenantId()).deploy();

		model.setDeploymentId(deploy.getId());
		repoSer.saveModel(model);
		// must update the deployment Id
		// see DeploymentEntityManager.deleteDeployment()
		return deploy;
	}

}
