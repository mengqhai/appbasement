package com.workstream.core.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.engine.RepositoryService;
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

	private WorkflowDefinitionConversionFactory conFactory = new WorkflowDefinitionConversionFactory();

	private SimpleWorkflowJsonConverter jsonCon = new SimpleWorkflowJsonConverter();

	@Autowired
	private ProcessDiagramGenerator diagramGenerator;

	@Autowired
	private ProcessEngineConfiguration peCfg;

	public Deployment createDeployment(Long orgId, String fileName,
			InputStream in) {
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

	public Model createModel(Long orgId, String name) {
		Model model = repoSer.newModel();
		model.setName(name);
		model.setTenantId(String.valueOf(orgId));
		repoSer.saveModel(model);
		return model;
	}

	public Model getModel(String modelId) {
		return repoSer.createModelQuery().modelId(modelId).singleResult();
	}

	public List<Model> filterModel(Long orgId) {
		return repoSer.createModelQuery().modelTenantId(String.valueOf(orgId))
				.list();
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

			repoSer.addModelEditorSourceExtra(model.getId(),
					IOUtils.toByteArray(diaIn));
		} catch (Exception e) {
			log.error("Failed to save model source and extra.", e);
			throw new RuntimeException(
					"Failed to save model source and extra.", e);
		}
		return model;
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

}
