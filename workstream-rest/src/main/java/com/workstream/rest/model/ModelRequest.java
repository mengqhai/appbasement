package com.workstream.rest.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.workstream.rest.RestConstants;
import com.workstream.rest.validation.NotRemovable;
import com.workstream.rest.validation.ValidateOnCreate;
import com.workstream.rest.validation.ValidateOnUpdate;

@NotRemovable(value = MapPropObj.NAME, groups = ValidateOnUpdate.class)
public class ModelRequest extends MapPropObj {

	public void setName(String name) {
		props.put(NAME, name);
	}

	@NotNull(groups = ValidateOnCreate.class)
	@Size(min = 1, max = RestConstants.VALID_NAME_SIZE)
	public String getName() {
		return getProp(NAME);
	}

}
