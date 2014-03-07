package com.angularjsplay.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.angularjsplay.model.UploadedFile;

@Controller
@RequestMapping("/fileUpload")
public class FileUploadController {

	@RequestMapping(method = RequestMethod.GET)
	public String showFileUploadPage() {
		return "fileUpload";
	}

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=text/html")
	public String saveFile(
			@RequestParam(value = "files", required = true) List<MultipartFile> files) {
		for (MultipartFile file : files) {
			System.out.println("File uploaded:" + file.getOriginalFilename()
					+ " type=" + file.getContentType());

		}
		return "fileUpload";
	}

	@RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
	public @ResponseBody
	List<UploadedFile> ajaxSaveFile(
			@RequestParam(value = "files", required = true) ArrayList<MultipartFile> files) {
		List<UploadedFile> uploadedFiles = new ArrayList<UploadedFile>();
		for (MultipartFile file : files) {
			System.out.println("File uploaded:" + file.getOriginalFilename()
					+ " type=" + file.getContentType());
			UploadedFile uf = new UploadedFile();
			uf.setName(file.getOriginalFilename());
			uploadedFiles.add(uf);

		}
		return uploadedFiles;
	}
}
