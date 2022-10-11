package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.repositoty.ContactRepo;
import com.example.demo.repositoty.userRepo;

@RestController
public class SearchController 
{
	@Autowired
	private userRepo userRep;
	@Autowired
	private ContactRepo contactRep;
	
	//search handler
	@GetMapping("/user/search/{query}")
	  public ResponseEntity<?> search(@PathVariable("query")String query,Principal p)
	  {
	
		User user=userRep.getUserByUserName(p.getName());
		  
		 
		List<Contact> contacts=contactRep.findByNameContainingAndUser(query,user);
		  System.out.println("Contatcts"+contacts);
		return ResponseEntity.ok(contacts);
	  
	  
	  }

}
