package com.workstream.core.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workstream.core.model.ProcessModelMetaInfo;
import com.workstream.core.model.RevisionEntry;

@Component
public class ProcessModelMetaInfoHelper {

	private Logger log = LoggerFactory
			.getLogger(ProcessModelMetaInfoHelper.class);

	private ObjectMapper mapper = new ObjectMapper();

	public ProcessModelMetaInfoHelper() {
		mapper.setSerializationInclusion(Include.NON_EMPTY);
	}

	public ProcessModelMetaInfo getMetaInfo(String metaStr) {
		if (metaStr.length() > 4000) {
			log.error("Meta info string too long: {}", metaStr);
			throw new RuntimeException("Meta info string too long");
		} else if (metaStr.length() > 2000) {
			log.warn("Meta info String is getting too long {}", metaStr);
		}
		try {
			return mapper.readValue(metaStr, ProcessModelMetaInfo.class);
		} catch (Exception e) {
			log.error("Failed to convert to meta info from {}", metaStr, e);
			throw new RuntimeException("Failed to convert to meta info", e);
		}
	}

	public String getStr(ProcessModelMetaInfo metaInfo) {
		try {
			return mapper.writeValueAsString(metaInfo);
		} catch (Exception e) {
			log.error("Fail to convert to meta info string, model name={}",
					metaInfo.getName(), e);
			throw new RuntimeException("Failed to convert to meta info string",
					e);
		}
	}

	public ProcessModelMetaInfo addRevision(String userId, String originalStr,
			String comment) {
		ProcessModelMetaInfo metaInfo = null;
		if (originalStr == null) {
			metaInfo = new ProcessModelMetaInfo();
		} else {
			metaInfo = getMetaInfo(originalStr);
		}
		RevisionEntry entry = new RevisionEntry(userId, comment, new Date());
		metaInfo.getRevisions().add(entry);
		return metaInfo;
	}

}
