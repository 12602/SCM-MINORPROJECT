package com.example.demo.repositoty;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.entities.User;
public interface userRepo extends JpaRepository<User, Integer> {
 
	
	 @Query("select u from user u where u.email=:email")
	public User  getUserByUserName(@Param("email")String email);




}
