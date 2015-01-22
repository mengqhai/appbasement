package com.workstream.core.service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import org.activiti.engine.repository.ProcessDefinitionQuery;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversion;
import org.activiti.workflow.simple.converter.WorkflowDefinitionConversionFactory;
import org.activiti.workflow.simple.definition.WorkflowDefinition;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.workstream.core.CoreConstants;
import com.workstream.core.exception.BeanPropertyException;
import com.workstream.core.exception.DataPersistException;
import com.workstream.core.exception.ResourceNotFoundException;
import com.workstream.core.model.Revision;
import com.workstream.core.persistence.IRevisionDAO;
import com.workstream.core.service.cmd.DeleteEditorSourceWithExtraForModelCmd;
import com.workstream.core.service.components.WsSimpleWorkflowJsonConverter;
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

	@Autowired
	private WsSimpleWorkflowJsonConverter jsonCon;

	@Autowired
	private ProcessDiagramGenerator diagramGenerator;

	@Autowired
	private ProcessEngineConfiguration peCfg;

	@Autowired
	private ManagementService mgmtService;

	@Autowired
	private IRevisionDAO revDao;

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

	public Deployment getDeploymentByModelId(String modelId) {
		Model model = getModel(modelId);
		String deploymentId = model.getDeploymentId();
		if (deploymentId == null) {
			return null;
		} else {
			return getDeployment(deploymentId);
		}
	}

	/**
	 * non-cascading deletion
	 * 
	 * @param deploymentId
	 */
	public void removeDeployment(String deploymentId) {
		repoSer.deleteDeployment(deploymentId);
	}

	public ProcessDefinition getProcessTemplate(String processDefinitionId) {
		return repoSer.createProcessDefinitionQuery()
				.processDefinitionId(processDefinitionId).singleResult();
	}

	public List<ProcessDefinition> filterProcessTemplate(Long orgId,
			boolean onlyLatest) {
		ProcessDefinitionQuery q = repoSer.createProcessDefinitionQuery()
				.processDefinitionTenantId(String.valueOf(orgId));
		if (onlyLatest) {
			q.latestVersion(); // it's OK to combine with the
								// processDefinitionTenantId()
		}
		return q.orderByProcessDefinitionId().asc().list();
	}

	public List<ProcessDefinition> filterProcessTemplate(String deploymentId) {
		// q.latestVersion() is not be able to used with .deploymentId()
		return repoSer.createProcessDefinitionQuery()
				.deploymentId(deploymentId).list();
	}

	/**
	 * The last deployment is on the list head
	 * 
	 * @param modelId
	 * @return
	 */
	public List<Deployment> filterDeploymentByModelId(String modelId) {
		// the deployments' category id field is set to be the its model id
		// method deployModel()
		List<Deployment> deployments = repoSer.createDeploymentQuery()
				.deploymentCategory(modelId).orderByDeploymenTime().desc()
				.list();
		return deployments;
	}

	/**
	 * 
	 * @param modelId
	 * @param onlyLatest
	 *            only list latest version?
	 * @return
	 */
	public List<ProcessDefinition> filterProcessTemplateByModelId(
			String modelId, boolean onlyLatest) {
		List<Deployment> deployments = filterDeploymentByModelId(modelId);
		// the deployments' category id field is set to be the its model id
		// method deployModel()
		if (onlyLatest) {
			if (!deployments.isEmpty()) {
				Deployment lastDeployment = deployments.get(0);
				return filterProcessTemplate(lastDeployment.getId());
			} else {
				return Collections.emptyList();
			}
		} else {
			List<ProcessDefinition> result = new ArrayList<ProcessDefinition>();
			for (Deployment deploy : deployments) {
				List<ProcessDefinition> defList = this
						.filterProcessTemplate(deploy.getId());
				result.addAll(defList);
			}
			return result;
		}
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

	public InputStream getProcessTemplateDiagram(String templateId) {
		return repoSer.getProcessDiagram(templateId);
	}

	public InputStream getProcessTemplateBpmn(String templateId) {
		return repoSer.getProcessModel(templateId);
	}

	public Model createModel(Long orgId, String name) {
		Model model = repoSer.newModel();
		model.setName(name);
		model.setTenantId(String.valueOf(orgId));
		model.setCategory("table-editor"); // for explorer table editing
		repoSer.saveModel(model);
		WorkflowDefinition empty = new WorkflowDefinition();
		empty.setName(name);
		saveWorkflowToModel(model.getId(), empty); // place holder json for
													// explore table editing
		addRevision(model.getId(), null, Revision.TYPE_CREATE);
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
		removeRevisions(modelId);
	}

	/**
	 * After deployment, this id will be the key of the process definition. So
	 * in this way, we can make all the process definitions that deployed from
	 * the same model share the same key, so the version increment will be based
	 * on the model id, which is the expected behavior.
	 * 
	 * @param modelId
	 * @param flow
	 */
	private void setWorkflowId(String modelId, WorkflowDefinition flow) {
		// https://www.java.net/node/660184
		// NCName must starts with letter or _
		flow.setId("model_" + modelId);
	}

	private void saveWorkflowToModel(String modelId, WorkflowDefinition flow) {
		setWorkflowId(modelId, flow);
		try {
			WorkflowDefinitionConversion con = conFactory
					.createWorkflowDefinitionConversion(flow);
			con.convert();
			// See SimpleTableEditor.save()
			// Write JSON to byte-array and set as editor-source
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			jsonCon.writeWorkflowDefinition(flow, new OutputStreamWriter(baos,
					"utf-8")); // must explicitly specify utf-8, here
			// otherwise, the default(like GBK) will be used,
			// and jackson will report error(Invalid UTF-8 middle byte 0xd0)
			// when deserializing the byte[] back to object

			// create and save the process diagram
			InputStream diaIn = diagramGenerator.generateDiagram(
					con.getBpmnModel(), "png", peCfg.getActivityFontName(),
					peCfg.getLabelFontName(), peCfg.getClassLoader());
			// InputStream diaIn = diagramGenerator.generateDiagram(
			// con.getBpmnModel(), "png", "sansserif", "sansserif",
			// peCfg.getClassLoader());
			// to support chinese
			// after this the ModelEntity's sourceId and sourceExtraId are set
			// to null

			// source saved in byte array and linked to the model entity
			repoSer.addModelEditorSource(modelId, baos.toByteArray());
			repoSer.addModelEditorSourceExtra(modelId,
					IOUtils.toByteArray(diaIn));

		} catch (Exception e) {
			log.error("Failed to save model source and extra.", e);
			throw new DataPersistException(
					"Failed to save model source and extra.", e);
		}
	}

	/**
	 * Convert a simple WorkflowDefinition to a Model and save it.
	 * 
	 * @return
	 */
	public Model saveToModel(Long orgId, WorkflowDefinition flow)
			throws DataPersistException {
		Model model = repoSer.newModel();
		model.setName(flow.getName());
		model.setTenantId(String.valueOf(orgId));
		model.setCategory("table-editor"); // for explorer table editing
		repoSer.saveModel(model);

		saveWorkflowToModel(model.getId(), flow);
		addRevision(model.getId(), null, Revision.TYPE_CREATE);

		return model;
	}

	protected void removeRevisions(String modelId) {
		revDao.deleteFor(Model.class.getSimpleName(), modelId);
	}

	protected void addRevision(String modelId, String comment, String type) {
		String authorId = Authentication.getAuthenticatedUserId();
		Revision rev = new Revision(authorId, Model.class.getSimpleName(),
				modelId, comment);
		rev.setAction(type);
		revDao.persist(rev);
	}

	public Collection<Revision> filterModelRevision(String modelId) {
		return revDao.filterFor(Model.class.getSimpleName(), modelId);
	}

	public Model updateModel(String modelId, Map<String, Object> props) {
		Model model = getModel(modelId);
		try {
			BeanUtils.populate(model, props);
		} catch (Exception e) {
			log.error("Failed to populate the props to model: {}", props, e);
			throw new BeanPropertyException(e);
		}
		repoSer.saveModel(model);
		return model;
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
	public Model updateModel(String modelId, WorkflowDefinition flow) {
		return updateModel(modelId, flow, null);
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
	public Model updateModel(String modelId, WorkflowDefinition flow,
			String comment) throws DataPersistException {
		WorkflowDefinitionConversion con = conFactory
				.createWorkflowDefinitionConversion(flow);
		con.convert();
		Model model = repoSer.getModel(modelId);
		model.setName(flow.getName());
		model.setCategory("table-editor");
		repoSer.saveModel(model);
		// must delete the old source and extra first
		mgmtService.executeCommand(new DeleteEditorSourceWithExtraForModelCmd(
				model));
		saveWorkflowToModel(modelId, flow);

		addRevision(model.getId(), comment, Revision.ACTION_EDIT);
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
		byte[] bytes = getModelJsonBytes(modelId);
		if (bytes == null) {
			throw new ResourceNotFoundException(
					"No workflow definition for the model");
		}
		WorkflowDefinition def = jsonCon.readWorkflowDefinition(bytes);
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
				.name(def.getName()).tenantId(model.getTenantId())
				.category(model.getId()).deploy();
		// Has to set deployment category to model id
		// because although a model can have multiple deployments
		// model obj can only save the latest model id
		// so it's hard to find out all the deployments that
		// belongs to the same model.
		// Here we set the category field as the model id
		model.setDeploymentId(deploy.getId());
		repoSer.saveModel(model);
		// must update the deployment Id
		// see DeploymentEntityManager.deleteDeployment()
		return deploy;
	}

}
