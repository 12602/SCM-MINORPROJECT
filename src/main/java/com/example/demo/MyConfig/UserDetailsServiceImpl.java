package com.example.demo.MyConfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.example.demo.entities.User;
import com.example.demo.repositoty.userRepo;

public class UserDetailsServiceImpl implements UserDetailsService
{
	@Autowired
      private userRepo userRep;
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// fetching user from database
		
		User user=userRep.getUserByUserName(username);
		if(user==null)
			throw new UsernameNotFoundException("Could not found user");
		CustomUserDetails customUserDetails=new CustomUserDetails(user);
		return customUserDetails;
	}

}
