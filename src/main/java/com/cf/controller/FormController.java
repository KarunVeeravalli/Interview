package com.cf.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@CrossOrigin
@RestController
@RequestMapping("/candidateApplicationGForm")
public class FormController {
	@PostMapping
	public String getGoogleFormData(@RequestBody Map<String,String> formData) {
		System.out.println("Map Data: "+formData);
		return null;
	}
}
