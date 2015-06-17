package com.workstream.core.service;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.ProcessEngineConfiguration;
import org.activiti.image.ProcessDiagramGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiagramService {

	public static final String FORMAT_PNG = "png";

	@Autowired
	private ProcessDiagramGenerator diagramGenerator;

	@Autowired
	private ProcessEngineConfiguration peCfg;

	public InputStream generateProcessDiagram(BpmnModel bpmnModel,
			List<String> highlightActivityIds) {
		// return diagramGenerator.generateDiagram(bpmnModel, "png",
		// highlightActivityIds);
		List<String> flows = Collections.emptyList();
		return diagramGenerator.generateDiagram(bpmnModel, FORMAT_PNG,
				highlightActivityIds, flows, peCfg.getActivityFontName(),
				peCfg.getLabelFontName(), peCfg.getClassLoader(), 1.0f);
	}

	public InputStream generateProcessDiagram(BpmnModel bpmnModel) {
		// to support Chinese characters
		return diagramGenerator.generateDiagram(bpmnModel, "png",
				peCfg.getActivityFontName(), peCfg.getLabelFontName(),
				peCfg.getClassLoader());
	}

}
