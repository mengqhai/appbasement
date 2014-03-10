package com.angularjsplay.mvc;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.angularjsplay.model.TitledMessage;

@Controller
@RequestMapping("titledMessage")
public class TitledMessageController {

	// @Autowired(required=true)
	// @Autowired won't work here because Spring will find beans match
	// TitledMessage type and construct a collection.
	@Value("#{allMessages}")
	private List<TitledMessage> allMessages;

	public List<TitledMessage> getAllMessages() {
		return allMessages;
	}

	public void setAllMessages(List<TitledMessage> allMessages) {
		this.allMessages = allMessages;
	}

	@RequestMapping(method = RequestMethod.GET, headers = "Accept=application/json")
	public @ResponseBody List<TitledMessage> listMessages(
			@RequestParam(required = false, value = "offset") Integer offset,
			@RequestParam(required = false, value = "limit") Integer limit) {
		if (offset == null) {
			offset = 0;
		}

		int toIndex = allMessages.size();
		if (limit != null) {
			toIndex = Math.min(offset + limit, allMessages.size());
		}

		return allMessages.subList(offset, toIndex);
	}

}
