package com.haoxin.emctest.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.haoxin.emctest.model.Lun;
import com.haoxin.emctest.model.LunCreationReq;
import com.haoxin.emctest.model.ValidList;
import com.haoxin.emctest.service.LunService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * REST controller for resource LUN.
 *
 */
@Api(value = "LUNs", tags="LUN")
@RestController
@RequestMapping("/lun")
public class LunController {

	@Autowired
	private LunService lunService;

	@ApiOperation(value = "Lists all the LUNs", notes = "Returns the list of all the LUNs")
	@RequestMapping(path = "_list", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Lun> getLunList() {
		return getLunService().findAllLuns();
	}

	@ApiOperation(value = "Creates one or multiple LUNs with given size", notes = "Returns the created LUNs with id generated.")
	@RequestMapping(value = "/", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Lun> createLun(@RequestBody @Validated ValidList<LunCreationReq> lunReqs) {
		
		List<Lun> resultList = new ArrayList<Lun>();
		for (LunCreationReq lun: lunReqs) {
			resultList.add(lunService.createLun(lun.getSize()));
		}
		return resultList;
	}

	@ApiOperation(value = "Retrieves a LUN with the given id", notes="Retrieve the information (size) of a LUN")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public Lun getOneLun(@PathVariable("id") Long id) {
		return lunService.findOneLun(id);
	}

	@ApiOperation(value = "Delete a LUN with the given id")
	@RequestMapping(value = "/{id:\\d+}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public void deleteOnLun(@PathVariable("id") Long id) {
		lunService.deleteLun(id);
	}

	@ApiOperation(value = "Update a LUN with given size", notes= "Resize a LUN")
	@RequestMapping(value = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Lun updateLun(@RequestBody @Validated Lun lun) {
		return lunService.upateLun(lun);
	}

	/**
	 * @return the lunService
	 */
	public LunService getLunService() {
		return lunService;
	}

	/**
	 * @param lunService
	 *            the lunService to set
	 */
	public void setLunService(LunService lunService) {
		this.lunService = lunService;
	}

}
