
package dsc.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import dsc.entity.LocationMaster;
import dsc.entity.Roles;
import dsc.entity.UserCredential;
import dsc.entity.User;
import dsc.entity.ChangePassword;
import dsc.service.UserService;

@Controller
@SessionAttributes("Roles")
@RequestMapping("DSCReg")
public class UserController {

	@Autowired
	private UserService userService;
	
	
	@Autowired
	ConversionService conversionService; 
    
	@Autowired
	private ServletContext servletContext;
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	 
	@RequestMapping(value="/dscLogin",method=RequestMethod.GET)
	public ModelAndView dscLogin(Model model)
	{
		UserCredential userCredential=new UserCredential();
		ModelAndView modelAndView=new ModelAndView("dscLogin");
		model.addAttribute("userCredential", userCredential);
		return modelAndView;
		
	}
	
	@RequestMapping(value="/usersummary",method=RequestMethod.POST)
	public ModelAndView loginSuccess(@ModelAttribute("userCredential") UserCredential userCredential,BindingResult result,HttpSession session,HttpServletRequest request,Model model)
	{
		if(result.hasErrors())
		{
			System.out.println("hasserrors");
			return new ModelAndView("dscLogin");
		}
		HttpSession ses = request.getSession();
		User loginUser = userService.validateUserEmailandPassword(userCredential.getEmail_id(),userCredential.getPassword());
		ModelAndView modelAndView=new ModelAndView();
		if(loginUser!=null && loginUser.getStatus()=='E')
		{
			System.out.println("Login");
			modelAndView=new ModelAndView("redirect:/DSCRegistration/DSCRegistrationSummaryPage");
			modelAndView.addObject("user", loginUser);
		}
		else
		{
			System.out.println("hasserrors fail last");
        	model.addAttribute("errorMsg","Login failure.. Please retry");
			return new ModelAndView("dscLogin");
		}
		
		ses.setAttribute("userCredential", userCredential);
		ses.setAttribute("userSession", loginUser);
		ses.setAttribute("userName", loginUser.getUser_name());
		return modelAndView;
	}
	
	@RequestMapping(value="/newuser",method=RequestMethod.GET)
	public ModelAndView newUser(Model model)
	{
	
		User appuser=new User();
		ModelAndView modelAndView=new ModelAndView("newUser");
		model.addAttribute("user", appuser);
		
		return modelAndView;
		
	}
	
	@RequestMapping(value="/submitnewuser",method=RequestMethod.POST)
	public ModelAndView registerUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult,HttpSession session,Model model)
	{
		if(bindingResult.hasErrors())
		{
			return new ModelAndView("newUser");
			
		}
		userService.registerUser(user);
		ModelAndView modelAndView=new ModelAndView("redirect:/UserSummary/UserSummaryPage");
		modelAndView.addObject("user", user);
		return modelAndView;
		
	}
	
	@RequestMapping(value="/view",method=RequestMethod.GET)
	public ModelAndView viewUser(@RequestParam("id")int id,Model model)
	{
		
		User modifyuser = userService.getUsers(id);
		
		List<Roles> viewRoles = modifyuser.getRoles();
		for (Roles roles : viewRoles) {
			System.out.println("inside controller modify location:"+roles.getLocation_name());
		}
		
		ModelAndView modelAndView=new ModelAndView("viewUser");
		model.addAttribute("user", modifyuser);
		return modelAndView;
	}

	@RequestMapping(value="/modify",method=RequestMethod.GET)
	public ModelAndView modify(@RequestParam("id")int id,Model model)
	{
		
		User modifyuser = userService.getUsers(id);
		List<Roles> modifyRoles = modifyuser.getRoles();
		for (Roles roles2 : modifyRoles) {
			System.out.println("inside controller modify location:"+roles2.getLocation_name());
		}
		System.out.println(modifyuser.getId()+""+modifyuser.getRoles());
		ModelAndView modelAndView=new ModelAndView("modify");  
		System.out.println("<----------------->");
		model.addAttribute("user", modifyuser);
		return modelAndView;
	}
	
	@RequestMapping(value="/update",method=RequestMethod.POST)
	public ModelAndView updateUser(@Valid @ModelAttribute("user") User user,BindingResult bindingResult)
	{
		if(bindingResult.hasErrors())
		{
			return new ModelAndView("modify");
		}
		
		
		List<Roles> roles = user.getRoles();
		for (Roles roles2 : roles) {
			System.out.println("location in update user!!!!:"+roles2.getLocation_name());
		}
		userService.updateUser(user);
		ModelAndView modelAndView=new ModelAndView("redirect:/UserSummary/UserSummaryPage");
		modelAndView.addObject("user", user);
		
		return modelAndView;
		
	}
	
	@RequestMapping(value="/change",method=RequestMethod.GET)
	 public ModelAndView changePassword(Model model,HttpSession session)
	 {
		System.out.println("inside change password method");
		ChangePassword changePassword=new ChangePassword();
		model.addAttribute("changePassword", changePassword);
		User c = (User) session.getAttribute("userSession");
		ModelAndView modelAndView=new ModelAndView("changepassword");
		model.addAttribute("username", c.getUser_name());
		model.addAttribute("users",c);
		
		return modelAndView;
		 
	 }
	
	@RequestMapping(value="/changepass",method=RequestMethod.POST)
	public ModelAndView updatePassword(@ModelAttribute("changePassword") ChangePassword changePassword,BindingResult bindingResult,HttpSession session,Model model)
	{
		if(bindingResult.hasErrors())
		{
			System.out.println("binding error");
			return new ModelAndView("changepassword");
		}
		
		User cps = (User) session.getAttribute("userSession");
		int id = cps.getId();
		ModelAndView modelAndView=new ModelAndView();
		Boolean updateChangePassword = userService.updateChangePassword(id, changePassword.getOld_password(),changePassword.getNew_password(),changePassword.getRe_type_password());
	    if(updateChangePassword == true)
	    {
	    	
	    	modelAndView=new ModelAndView("redirect:/DSCReg/dscLogin");
	    }
	    else
	    {
	    	
        	model.addAttribute("errorMsg","Invalid old password...Please re-enter");
			return new ModelAndView("changepassword");
	    }
		return modelAndView;
		
	}
	
	@RequestMapping(value="/logout")
	public ModelAndView logout(HttpSession session,HttpServletRequest request)
	{
		HttpSession session2 = request.getSession();
		session2.invalidate();
		
		ModelAndView modelAndView =new ModelAndView("login");
		return modelAndView;
		
	}
	
	
	// Uniqueness check for Login Id
		@RequestMapping(value = "/existCheckForEmailId")
		public @ResponseBody void isexistCheckForEmailId(@RequestParam(required = false, value = "") String emailId,
				@ModelAttribute("user") User user, BindingResult result, ModelMap model,
				HttpServletResponse response) {
			
			boolean isEmailId = userService.searchEmailId(emailId);
			PrintWriter out = null;
			if (isEmailId == false) {
				try {
					out = response.getWriter();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out.print("F");
			} else {
				try {
					out = response.getWriter();
				} catch (IOException e) {
					e.printStackTrace();
				}
				out.print("T");
			}
		}
	
	@ModelAttribute
    public void header(Model model,HttpServletRequest request) {
	@SuppressWarnings("unchecked")
	List<Roles> roles = (List<Roles>) servletContext.getAttribute("Roles");
	
	@SuppressWarnings({ "unchecked"})
	Map<Character,String> UserType=(Map<Character,String>) servletContext.getAttribute("USERTYPE");
	
	@SuppressWarnings("unchecked")
	Map<Character,String> status=(Map<Character,String>) servletContext.getAttribute("STATUS");
	
	model.addAttribute("Roles",roles );
	model.addAttribute("USERTYPE", UserType);
	model.addAttribute("STATUS", status);
	
	BaseUrlClass baseUrlClass = new BaseUrlClass();
	String baseUrl = baseUrlClass.getBaseUrl(request);
	model.addAttribute("baseUrl", baseUrl);
	
}
	

}
