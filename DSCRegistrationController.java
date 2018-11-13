/*package dsc.controller;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import dsc.entity.CityMaster;
import dsc.entity.DSCRegistration;
import dsc.entity.StateMaster;
import dsc.service.DSCRegistrationServiceImpl;

@Controller
@SessionAttributes("dscRegistration")
public class DSCRegistrationController {

	@Autowired
	private ServletContext servletContext;

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Autowired
	private DSCRegistrationServiceImpl dscRegistrationImpl;

	public DSCRegistrationServiceImpl getDscRegistrationImpl() {
		return dscRegistrationImpl;
	}

	public void setDscRegistrationImpl(DSCRegistrationServiceImpl dscRegistrationImpl) {
		this.dscRegistrationImpl = dscRegistrationImpl;
	}
	
	@Autowired 
	ConversionService conversionService; 
	
	@RequestMapping(value = "/")
	public String homePage() {		
		return "home";
	}

	@RequestMapping(value = "/dscRegistration", method = RequestMethod.GET)
	public String dscRegistrationMethod(Model model,HttpServletRequest req) {
			
		Date date = new Date();
		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate=formatter1.format(date);		
		System.out.println("server time -------------------"+formattedDate);
		model.addAttribute("formattedDate",formattedDate);
		model.addAttribute("dscRegistration", new DSCRegistration());
		model.addAttribute("edit",true);
		
		BaseUrlClass baseUrlClass=new BaseUrlClass();
		String baseUrl=baseUrlClass.getBaseUrl(req);
		model.addAttribute("baseUrl",baseUrl);
		return "dscPurchase";
	}

	@RequestMapping(value = "/dscSuccess", method = RequestMethod.POST)
	public ModelAndView saveRegistration(@Valid @ModelAttribute("dscRegistration") DSCRegistration dscRegistration,
			BindingResult bindingResult,Model model) {
		System.out.println("inside purchase controller===============" + dscRegistration.getStatus());
		if (bindingResult.hasErrors()) {
			System.out.println("error occure" + bindingResult.getFieldErrorCount());
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError objectError : errors) {
				System.out.println("==========errors" + objectError.getDefaultMessage());			
			}
			model.addAttribute("edit",true);
			return new ModelAndView("dscPurchase");
		}		
		dscRegistrationImpl.saveDSCRegister(dscRegistration);
		ModelAndView modelAndView = new ModelAndView("paymentDetails");
		modelAndView.addObject("dscRegistration", dscRegistration);
		return modelAndView;
	}

	@RequestMapping(value = "/ViewDscDetails", method = RequestMethod.GET)
	public String viewDSCDetail(@RequestParam("id") int id, Model model) {
		DSCRegistration dscDetails = dscRegistrationImpl.viewDscDetails(id);
		model.addAttribute("dscDetails", dscDetails);
		return "ViewDscDetails";
	}

	@RequestMapping(value = "/UpdateDscDetails")
	public String getDscDetailsForUpdateDscDetails(@RequestParam("id") int id,
			@ModelAttribute("dscDetails") DSCRegistration dscDetails, Model model, HttpSession httpSession) {
		DSCRegistration dscDetails1 = dscRegistrationImpl.getDetails(id);
		model.addAttribute("dscDetails", dscDetails1);
		return "updateDscDetails";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateDSCDetails(@ModelAttribute("dscRegistration") DSCRegistration dscRegistration, Model model) {
		dscRegistrationImpl.updateDscDetails(dscRegistration);
		model.addAttribute("updateDscSuccess",true);	
		return "success";
	}

	@RequestMapping(value = "/UpdateDscAmount")
	public String getDSCDetailsForAmountUpdate(@RequestParam("id") int id,
			@ModelAttribute("dscDetails") DSCRegistration dscDetails, Model model, HttpSession httpSession) {
		DSCRegistration detailsForAmountUpdate = dscRegistrationImpl.getDSCDetailsForAmountUpdate(id);
		model.addAttribute("dscDetails", detailsForAmountUpdate);
		return "updateDscAmount";
	}

	@RequestMapping(value = "/updateAmount", method = RequestMethod.POST)
	public String updateAmountDetails(@ModelAttribute("dscDetails") DSCRegistration dscDetails, Model model) {
		dscRegistrationImpl.updateAmountDetails(dscDetails);
		return "success";
	}

	@RequestMapping(value = "/retrieveDetail", method = RequestMethod.POST)
	public String retrieveDetails(@RequestParam("email_id") String email_id, @RequestParam("password") String password,
			Model model) {
		DSCRegistration dscRegistration = dscRegistrationImpl.retrieveDscDetails(email_id,password);		
		if(dscRegistration.getStatus()==10)
		{
			model.addAttribute("edit",true);
		}
		else{
			model.addAttribute("edit",false);
		}	
		model.addAttribute("dscRegistration",dscRegistration);
		return "dscPurchase";
	}

	@ModelAttribute("dscRegistration")
	public DSCRegistration setUpOrderForm() {
		return new DSCRegistration();
	}

	@ModelAttribute
	public void headerMsg(Model model) {
		String str = (String) servletContext.getAttribute("test");
		System.out.println(str);

		@SuppressWarnings("unchecked")
		Map<Integer, String> locationMap = (Map<Integer, String>) servletContext.getAttribute("UinibrainLocation");
		model.addAttribute("UinibrainLocation", locationMap);

		@SuppressWarnings("unchecked")
		Map<Integer, String> classMap = (Map<Integer, String>) servletContext.getAttribute("ClassType");
		model.addAttribute("ClassType", classMap);

		@SuppressWarnings("unchecked")
		Map<Integer, String> validityMap = (Map<Integer, String>) servletContext.getAttribute("Validity");
		model.addAttribute("Validity", validityMap);

		@SuppressWarnings("unchecked")
		Map<Character, String> registrationTypeMap = (Map<Character, String>) servletContext
				.getAttribute("RegistrationType");
		model.addAttribute("RegistrationType", registrationTypeMap);

		@SuppressWarnings("unchecked")
		Map<Character, String> hardwareMap = (Map<Character, String>) servletContext.getAttribute("HardwareMap");
		model.addAttribute("HardwareMap", hardwareMap);

		@SuppressWarnings("unchecked")
		Map<Integer, String> caMap = (Map<Integer, String>) servletContext.getAttribute("CaMap");
		model.addAttribute("CaMap", caMap);

		@SuppressWarnings("unchecked")
		List<StateMaster> stateList = (List<StateMaster>) servletContext.getAttribute("StateList");
			
		HashMap<Integer, String> statesMap = new HashMap<>();

		for (StateMaster stateMaster : stateList) {
			Integer stateId = stateMaster.getId();
			statesMap.put(stateId, stateMaster.getState_name());
		}
		model.addAttribute("states", statesMap);
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, String> cities = (HashMap<Integer, String>) servletContext.getAttribute("city");	
		model.addAttribute("cities", cities);
		

			
	}


	@RequestMapping(value = "/getcities")
	public @ResponseBody void showCities(@RequestParam(required = false, value = "") Integer state,
			@Valid @ModelAttribute("dscRegistration") DSCRegistration dscRegistration, BindingResult result,
			ModelMap model, HttpServletResponse response) {
		
		System.out.println("getting state id--------------->" + state);
	
		@SuppressWarnings("unchecked")
		HashMap<Integer, Object> statesCities = (HashMap<Integer, Object>) servletContext.getAttribute("StateCities");
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, CityMaster> selectedStateCities = (HashMap<Integer, CityMaster>) statesCities.get(state);

		for (Entry<Integer, CityMaster> entry : selectedStateCities.entrySet()) {
			System.out.println("key:=" + entry.getKey() + "value:+" + entry.getValue().getCity_name());
		}
		
		PrintWriter out = null;
		JSONArray obj = new JSONArray();

		for (Entry<Integer, CityMaster> entry : selectedStateCities.entrySet()) {
			System.out.println("key:=" + entry.getKey() + "value:+" + entry.getValue().getCity_name());
			JSONObject ob = new JSONObject();
			try {
				out = response.getWriter();
				ob.put("ID", entry.getKey());
				ob.put("Name", entry.getValue().getCity_name());
				obj.put(ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		out.println(obj);
	}
		
	@RequestMapping(value = "/getAmount")
	public @ResponseBody void showAmount(@RequestParam("token") String token,@RequestParam("class_id")Integer class_id,@RequestParam("validity")Integer validity, DSCRegistration dscRegistration, BindingResult result,
		@Valid	ModelMap model, HttpServletResponse response) {
		
		System.out.println("token id===="+token);
		System.out.println("token id===="+class_id);
		System.out.println("token id===="+validity);
		
		BigDecimal amount;
		
		@SuppressWarnings("unchecked")
		Map<String,BigDecimal> amountMap = (Map<String, BigDecimal>) servletContext.getAttribute("amountMap");
		amount = amountMap.get(class_id+"~"+token+"~"+validity);
		
		System.out.println("amont is================="+amount);				
		try {
			PrintWriter out=response.getWriter();
			out.println(amount);
			out.flush();		
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}

*/





package dsc.controller;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

import dsc.converter.LocalDateTimeConverter;
import dsc.entity.CityMaster;
import dsc.entity.DSCRegistration;
import dsc.entity.StateMaster;
import dsc.service.DSCRegistrationServiceImpl;

@Controller
@RequestMapping("DSCPurchase")
@SessionAttributes("dscRegistration")
public class DSCRegistrationController {

	@Autowired
	private ServletContext servletContext;

	public ServletContext getServletContext() {
		return servletContext;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Autowired
	private DSCRegistrationServiceImpl dscRegistrationImpl;

	public DSCRegistrationServiceImpl getDscRegistrationImpl() {
		return dscRegistrationImpl;
	}

	public void setDscRegistrationImpl(DSCRegistrationServiceImpl dscRegistrationImpl) {
		this.dscRegistrationImpl = dscRegistrationImpl;
	}
	
	@Autowired 
	ConversionService conversionService; 
	
	 
	@RequestMapping(value = "/dscRegistration", method = RequestMethod.GET)
	public String dscRegistrationMethod(Model model,HttpServletRequest req) {
			
		Date date = new Date();
		DateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String formattedDate=formatter1.format(date);		
		model.addAttribute("formattedDate",formattedDate);
		model.addAttribute("dscRegistration", new DSCRegistration());
		model.addAttribute("edit",true);
		return "dscPurchase";
	}

	@RequestMapping(value = "/dscSuccess", method = RequestMethod.POST)
	public ModelAndView saveRegistration(@Valid @ModelAttribute("dscRegistration") DSCRegistration dscRegistration,
			BindingResult bindingResult,Model model) {
		if (bindingResult.hasErrors()) {
			System.out.println("error occure" + bindingResult.getFieldErrorCount());
			List<ObjectError> errors = bindingResult.getAllErrors();
			for (ObjectError objectError : errors) {
				System.out.println("==========errors" + objectError.getDefaultMessage());			
			}
			model.addAttribute("edit",true);
			return new ModelAndView("dscPurchase");
		}		
		Date date = new Date();
		DateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
		String stringRegisteredDate=formatter1.format(date);		
		LocalDateTimeConverter converter=new LocalDateTimeConverter();
		LocalDateTime registeredDate = converter.convert(stringRegisteredDate);
		
		dscRegistration.setRegistered_date(registeredDate);
		
		dscRegistrationImpl.saveDSCRegister(dscRegistration);
		ModelAndView modelAndView = new ModelAndView("paymentDetails");
		modelAndView.addObject("dscRegistration", dscRegistration);
		return modelAndView;
	}

	@RequestMapping(value = "/ViewDscDetails", method = RequestMethod.GET)
	public String viewDSCDetail(@RequestParam("id") int id, Model model) {
		DSCRegistration dscDetails = dscRegistrationImpl.viewDscDetails(id);
		model.addAttribute("dscDetails", dscDetails);
		return "ViewDscDetails";
	}

	@RequestMapping(value = "/UpdateDscDetails")
	public String getDscDetailsForUpdateDscDetails(@RequestParam("id") int id,
			@ModelAttribute("dscDetails") DSCRegistration dscDetails, Model model, HttpSession httpSession) {
		DSCRegistration dscDetails1 = dscRegistrationImpl.getDetails(id);
		model.addAttribute("dscDetails", dscDetails1);
		return "updateDscDetails";
	}

	@RequestMapping(value = "/update", method = RequestMethod.POST)
	public String updateDSCDetails(@ModelAttribute("dscRegistration") DSCRegistration dscRegistration, Model model) {
		dscRegistrationImpl.updateDscDetails(dscRegistration);
		model.addAttribute("updateDscSuccess",true);	
		return "success";
	}

	@RequestMapping(value = "/UpdateDscAmount")
	public String getDSCDetailsForAmountUpdate(@RequestParam("id") int id,
			@ModelAttribute("dscDetails") DSCRegistration dscDetails, Model model, HttpSession httpSession) {
		DSCRegistration detailsForAmountUpdate = dscRegistrationImpl.getDSCDetailsForAmountUpdate(id);
		model.addAttribute("dscDetails", detailsForAmountUpdate);
		return "updateDscAmount";
	}

	@RequestMapping(value = "/updateAmount", method = RequestMethod.POST)
	public String updateAmountDetails(@ModelAttribute("dscDetails") DSCRegistration dscDetails, Model model) {
		dscRegistrationImpl.updateAmountDetails(dscDetails);
		return "updateamountsuccess";
	}

	@RequestMapping(value = "/retrieveDetail", method = RequestMethod.POST)
	public String retrieveDetails(@RequestParam("email_id") String email_id, @RequestParam("password") String password,
			Model model) {
		DSCRegistration dscRegistration = dscRegistrationImpl.retrieveDscDetails(email_id,password);
		
		if(dscRegistration ==  null){
			String errorMsg="Sorry,credentials are not found...Please enter valid credentials";
			model.addAttribute("errorMsg",errorMsg);
			return "error";
		}
		
		if(dscRegistration.getApplication_status()==10)
		{
			model.addAttribute("edit",true);
		}
		else{
			model.addAttribute("edit",false);
		}	
		model.addAttribute("dscRegistration",dscRegistration);
		
		return "dscPurchase";
	}

	@ModelAttribute("dscRegistration")
	public DSCRegistration setUpOrderForm() {
		return new DSCRegistration();
	}

	@ModelAttribute
	public void headerMsg(Model model,HttpServletRequest request) {
		String str = (String) servletContext.getAttribute("test");
		

		@SuppressWarnings("unchecked")
		Map<Integer, String> locationMap = (Map<Integer, String>) servletContext.getAttribute("UnibrainLocation");
		model.addAttribute("UnibrainLocation", locationMap);

		@SuppressWarnings("unchecked")
		Map<Integer, String> classMap = (Map<Integer, String>) servletContext.getAttribute("ClassType");
		model.addAttribute("ClassType", classMap);

		@SuppressWarnings("unchecked")
		Map<Integer, String> validityMap = (Map<Integer, String>) servletContext.getAttribute("Validity");
		model.addAttribute("Validity", validityMap);

		@SuppressWarnings("unchecked")
		Map<Character, String> registrationTypeMap = (Map<Character, String>) servletContext
				.getAttribute("RegistrationType");
		model.addAttribute("RegistrationType", registrationTypeMap);

		@SuppressWarnings("unchecked")
		Map<Character, String> hardwareMap = (Map<Character, String>) servletContext.getAttribute("HardwareMap");
		model.addAttribute("HardwareMap", hardwareMap);

		@SuppressWarnings("unchecked")
		Map<Integer, String> caMap = (Map<Integer, String>) servletContext.getAttribute("CaMap");
		model.addAttribute("CaMap", caMap);

		@SuppressWarnings("unchecked")
		List<StateMaster> stateList = (List<StateMaster>) servletContext.getAttribute("StateList");
			
		HashMap<Integer, String> statesMap = new HashMap<>();

		for (StateMaster stateMaster : stateList) {
			Integer stateId = stateMaster.getId();
			statesMap.put(stateId, stateMaster.getState_name());
		}
		model.addAttribute("states", statesMap);
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, String> cities = (HashMap<Integer, String>) servletContext.getAttribute("city");	
		model.addAttribute("cities", cities);
		

		BaseUrlClass baseUrlClass = new BaseUrlClass();
		String baseUrl = baseUrlClass.getBaseUrl(request);
		model.addAttribute("baseUrl", baseUrl);
			
	}


	@RequestMapping(value = "/getcities")
	public @ResponseBody void showCities(@RequestParam(required = false, value = "") Integer state,
			@Valid @ModelAttribute("dscRegistration") DSCRegistration dscRegistration, BindingResult result,
			ModelMap model, HttpServletResponse response) {
		
		
	
		@SuppressWarnings("unchecked")
		HashMap<Integer, Object> statesCities = (HashMap<Integer, Object>) servletContext.getAttribute("StateCities");
		
		@SuppressWarnings("unchecked")
		HashMap<Integer, CityMaster> selectedStateCities = (HashMap<Integer, CityMaster>) statesCities.get(state);

		for (Entry<Integer, CityMaster> entry : selectedStateCities.entrySet()) {
			
		}
		
		PrintWriter out = null;
		JSONArray obj = new JSONArray();

		for (Entry<Integer, CityMaster> entry : selectedStateCities.entrySet()) {
			System.out.println("key:=" + entry.getKey() + "value:+" + entry.getValue().getCity_name());
			JSONObject ob = new JSONObject();
			try {
				out = response.getWriter();
				ob.put("ID", entry.getKey());
				ob.put("Name", entry.getValue().getCity_name());
				obj.put(ob);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
		out.println(obj);
	}
		
	@RequestMapping(value = "/getAmount")
	public @ResponseBody void showAmount(@RequestParam("token") String token,@RequestParam("class_id")Integer class_id,@RequestParam("validity")Integer validity, DSCRegistration dscRegistration, BindingResult result,
		@Valid	ModelMap model, HttpServletResponse response) {
		
		
		
		BigDecimal amount;
		
		@SuppressWarnings("unchecked")
		Map<String,BigDecimal> amountMap = (Map<String, BigDecimal>) servletContext.getAttribute("amountMap");
		amount = amountMap.get(class_id+"~"+token+"~"+validity);
		
		
						
		try {
			PrintWriter out=response.getWriter();
			out.println(amount);
			out.flush();		
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}