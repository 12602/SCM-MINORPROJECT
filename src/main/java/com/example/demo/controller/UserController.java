package com.example.demo.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import com.example.demo.helper.Message;
import com.example.demo.repositoty.ContactRepo;
import com.example.demo.repositoty.userRepo;


@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	 private BCryptPasswordEncoder passwordEncoder;
	@Autowired
	private userRepo userRep;
	@Autowired
	private ContactRepo contactRep;
	//this method is fire everyTime for index and all page we got the user who is log in
	//method fo adding coomon details to page
	 @ModelAttribute
	  public void addCommonData(Model m,Principal principal)
	  {
		 String username=principal.getName();
         User user=  userRep.getUserByUserName(username);
         System.out.println(user.getImageUrl());
         m.addAttribute("user",user);
		  
	  }
	 //dashboard home
    @RequestMapping("/index")
	public String dashboard(Model m,Principal principal)
	{
    	m.addAttribute("title","User Dashboard");
		return "normal/user_dashboard";
	}
    
    
     //add or open add contact form 
    @GetMapping("/add_contact")
      public String addContactForm(Model m)
      {
    	
    	m.addAttribute("title","Add Contact");
    
    	m.addAttribute("contact",new Contact());
    	  return "normal/add_contact_form";
    	  
      }
     //processing add-contact form
    @PostMapping("/process-contact")
    public String contact(@ModelAttribute Contact contact,@RequestParam ("profileImage") MultipartFile file,  Principal p ,HttpSession session ) 
    
    {
    	try 
    	
    	{
    		
    		
    	String name=p.getName();
    	//take out the user
    	User user=userRep.getUserByUserName(name);
    	
    	//processing and uploading file
    	 if(file.isEmpty())
    	 {
    		 //if the file is empty
    		 //setting the default image
    		 contact.setImageUrl("banner.jpg");
    	 }
    	 else
    	 {
    		 //upload the file to folder and update the name to contact
    		 contact.setImageUrl(file.getOriginalFilename());
    		 
    	File save= new ClassPathResource("/static/image").getFile();
    	Path path= Paths.get(save.getAbsolutePath()+File.separator+file.getOriginalFilename());
    	
    	Files.copy( file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING );
    	
    	 }
    	//add the user
        	
     
    	contact.setUser(user);
    	 user.getContact().add(contact);
    	 //save the user
    	 //message success
    	 session.setAttribute("message",new Message("Your Contact Is Added! Add More..","success"));
    	 userRep.save(user);
    	 System.out.println("Added to database");
    	 
    	 
    	 return "normal/add_contact_form";
       }
    	catch(Exception e)
    	{
        	//error message
    		 session.setAttribute("message",new Message("Something went wrong !Try Again!Email Already exist or Description limit excedded !Please Change it !!..","danger"));
    		
    		e.printStackTrace();
    	}

    	return "normal/add_contact_form";

    }

 //per page-5 contact
    //currentpage=0

  //show contact
    @GetMapping("/show-contact/{page}")     
    public String showContact(@PathVariable("page") int page,Model m,Principal p)
    {      
    	String email=p.getName();
    	
    
    	 User user= userRep.getUserByUserName(email);
    	 PageRequest pageable=PageRequest.of(page,3);
    	 
    	 
    	 Page<Contact> contacts= contactRep.findContactsByUser(user.getId(),pageable);
    	  m.addAttribute("contacts", contacts);
    	  m.addAttribute("currentpage",page);
    	  m.addAttribute("totalpages", contacts.getTotalPages());
    	  
    	return "normal/show-contact";
    	
    }
	  
	  
   //showing particular contact details
    @GetMapping("/{cid}/contact")
     public String showContactDetails(@PathVariable("cid")int cid,Model m,Principal p)
     {
    	Optional<Contact>op=contactRep.findById(cid);
    
    	if(op.isEmpty()==true)
    	{
    		System.out.println(op);
    		//if id is wrong then display login page back
    		return "/loginfail";
    	}
  
             String username=p.getName();
            User user= userRep.getUserByUserName(username);
              
    	    Contact contact=op.get();
    	    //if contact user id matches with user login id then it is correcr user
    	    if(user.getId()==contact.getUser().getId())
    	    
    	    	m.addAttribute("contact",contact);
    	    else
    	    	return "loginfail";
    	    	
        	    
        	    
    	    	return "normal/contact_details";
    	    
    	    
    		
     }
   
    
      //delete contact handler
     
     @GetMapping("/delete/{cid}")
    public String deleteContact(@PathVariable("cid")int cid,Model m,HttpSession session,Principal p)
      {
    	  
    	    Optional<Contact>op=contactRep.findById(cid);
    	   Contact contact=op.get();
    	   // changing the contact of user to null  so that we can get delete contact
    	     contact.setUser(null);
    	     //delete image
    	      User user=userRep.getUserByUserName(p.getName());
    	     //matches tht contact by list
    	      user.getContact().remove(contact);
    	     userRep.save(user);
    	     
    	    
          session.setAttribute("message", new Message("Contact Deleted Suceesfully!","success"));
    	     
    	     return "redirect:/user/show-contact/0";
      
      }
     
     //open update form handler
      @PostMapping("/update-contact/{cid}")
     public String openUpdateForm(Model m,@PathVariable("cid")int cid)
     {
    	 m.addAttribute("title","Update Contact");
    	Contact contact= contactRep.findById(cid).get();
    	   m.addAttribute("contact", contact);
    	
    	 return "normal/update_form";
     }
      //update contact handler
      @PostMapping("/process-update")
       public String updateForm(@ModelAttribute Contact contact ,@RequestParam ("profileImage") MultipartFile file,Model m,HttpSession session,Principal p)
       {
    	  try 
    	  {
			
    		  //old contact details;
    		 Contact oldContact=   contactRep.findById(contact.getcId()).get();
    		  if(!file.isEmpty())
    		  {
    		
    			  //rewrite the file
    			  //delete old photo
    			  File deleteFile= new ClassPathResource("/static/image").getFile();
    			  File file1=new File(deleteFile,oldContact.getImageUrl());
    			  file1.delete();
    			  
    			  //update new photo
    			 	File save= new ClassPathResource("/static/image").getFile();
    		    	Path path= Paths.get(save.getAbsolutePath()+File.separator+file.getOriginalFilename());
    		    	
    		    	Files.copy( file.getInputStream(), path,StandardCopyOption.REPLACE_EXISTING );
    			 
    		    	
    		    	contact.setImageUrl(file.getOriginalFilename());
    			
    			  
    		  }
    		  else
    		  {
    			  //if the image is empty then not update it
    			  contact.setImageUrl(oldContact.getImageUrl());
    		  }
    		  User user=userRep.getUserByUserName(p.getName());
    		  
    		  contact.setUser(user);
    		  contactRep.save(contact);
    		  session.setAttribute("message", new Message("Your contact is Updated","success"));
		    } 
    	  
    	  catch (Exception e) {
		e.printStackTrace();
		}
    	  
    	    return "redirect:/user/"+contact.getcId()+"/contact";
    	  
       }
      //your profile handler
      @GetMapping("/profile")
      public String yourProfileHandler(Model m)
      {
    	  m.addAttribute("title","Profile page");
    
    	  return "normal/profile";
      }
      
  
      //open setting handler
       @GetMapping("/settings")     
      public String openSettings(Model m)
      {
    	   m.addAttribute("title","Setting Page");
    	  return "normal/setting";
      }
       
       //change password handler
       @PostMapping("/change-password")
       public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,Principal p,HttpSession session )
       {
    	   
    	   String name= p.getName();
    	      User user=  userRep.getUserByUserName(name);
    	      if(passwordEncoder.matches(oldPassword,user.getPassword()))
    	      {
    	    	  //change the password by encoding
    	    	  user.setPassword(this.passwordEncoder.encode(newPassword));
    	    	  this.userRep.save(user);
    	       session.setAttribute("message",new Message("Your Password Changed Sucesfully!", "success"));
    	      
    	      }
    	      else
    	      {
    	    	  session.setAttribute("message",new Message("Your Old  Password is Wrong !", "error"));
    	    	  //error
    	      }
    	      
    	     
    	      
    	      
    	   return "redirect:/user/index";
       }
       

}
    

