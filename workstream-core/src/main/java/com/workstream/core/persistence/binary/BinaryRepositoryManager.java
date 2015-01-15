package com.workstream.core.persistence.binary;

import java.util.HashMap;
import java.util.Map;

import com.workstream.core.model.BinaryObj.BinaryReposType;

public class BinaryRepositoryManager {

	Map<BinaryReposType, IBinaryRepository> repoMap = new HashMap<BinaryReposType, IBinaryRepository>(
			1);

	public void addRepository(BinaryReposType type, IBinaryRepository repo) {
		repoMap.put(type, repo);
	}

	public IBinaryRepository getRepository(BinaryReposType type) {
		return repoMap.get(type);
	}

}
