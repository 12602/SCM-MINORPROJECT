package com.example.demo.repositoty;

import java.util.List;

import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;

public interface ContactRepo extends JpaRepository<Contact, Integer>
{
	
	//pagination
	@Query("from Contact as c where c.user.id=:userid ")
	public Page<Contact> findContactsByUser(@Param("userid")int userid,PageRequest pageable);
	//search
     public List<Contact> findByNameContainingAndUser(String name,User user);



}
