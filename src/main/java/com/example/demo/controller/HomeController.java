package com.example.demo.controller;



import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.hibernate.loader.plan.exec.spi.LockModeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;
import com.example.demo.repositoty.userRepo;



@Controller
public class HomeController {
	@Autowired
	private userRepo userRep;
	
	@Autowired
	 private BCryptPasswordEncoder passwordEncoder;
	
	
	@GetMapping("/home")
	public String home(Model m)
	{
		m.addAttribute("title","smartContactManager");
		return "home";
	}
	
	
	@GetMapping("/about")
	public String about(Model m)
	{
		m.addAttribute("title","this is about page");
		return "about";
	}
	@GetMapping("/signup")
	public String signup(Model m)
	{
		m.addAttribute("user",new User());
		return "signup";
	}
//	this is handler for registering student
	@PostMapping("/do_register")
	 public String resgisterUser(@Valid @ModelAttribute User user,@RequestParam ("profileImage") MultipartFile file,BindingResult res,@RequestParam(value="agreement",defaultValue="false" )boolean agreement,Model model,HttpSession session)
	 {
		
	      try
	      {
	    	  
	    	  //setting the image of user 
	    	  if(file.isEmpty())
	     	 {
	     		 //if the file is empty
	     		 //setting the default image
	     		 user.setImageUrl("banner.jpg");
	     	 }
	    	  else
	     	 {
	     		 //upload the file to folder and update the name to contact
	     		user.setImageUrl(file.getOriginalFilename());
	     		 
	     	File save= new ClassPathResource("/static/image").getFile();
	     	Path path= Paths.get(save.getAbsolutePath()+File.separator+file.getOriginalFilename());
	     	
	     	Files.copy( file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING );
	     	
	     	 }
	    	  //if aggrement is not aceepted  so throw error
	    	  if(agreement==false)
		       {
		    	 
               throw new Exception("You are not agreee for terms and condtions");
		    	  
		       }
	    	  if(res.hasErrors())
	    	  {
	    		  System.out.print("An error occured!!"+res.toString());
	    		  
	    		  model.addAttribute("user",user);
	    		  return "login";
	    	  }
	    	  
	    	  
	    	  //else set the user
		       user.setRole("ROLE_USER");
		       user.setImageUrl("default.png");
		       user.setEnabled(true);
		       user.setPassword(passwordEncoder.encode(user.getPassword()));
		    	  model.addAttribute("user",new User());
		    	  session.setAttribute("message", new Message("Succesfully registered!","alter-success"));
		    	 User save=userRep.save(user);
		    	 System.out.println(user);
		    	 return "login";
	      }
	      catch(Exception e)
	      {
	    	  e.printStackTrace();
	    	  model.addAttribute("user",user);
	    	  session.setAttribute("message", new Message("Something want wrong !  Server Error ,"+e.getMessage(),"alert-danger"));
	    	  return "signup";
	      
	      }
		
	 }
	  //handler for custom login
	 @GetMapping("/signin")
	  public String customLogin(Model m)
	  {
		 m.addAttribute("title","Login Page");
		 return "login";
	  }
	 @GetMapping("/loginfail")
	 public String loginFail(Model m)
	 {
		 
		 return "loginfail";
	 }
	 
	
}
