package dsc.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.util.MultiValueMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dsc.dao.EntityDAO;
import dsc.dao.PaginationLogic;
import dsc.entity.DSCRegistration;
 
@Controller
@RequestMapping("UserSummary")
public class UserSummaryController {
	
	Class<User> entityClass=User.class;
	List<User> userlist=new ArrayList<>();;
	List<String> columnlist;
	@Autowired
	PaginationLogic paginationLogic;
	
	@Autowired
	private EntityDAO entityDAO;
	
	@RequestMapping("GenerateDocument")
	public ModelAndView getPdf(@RequestParam() MultiValueMap<String,String> requestParams)
	{  
		  
		  if(requestParams.isEmpty()!=true)
		  {
			  List<String> temp_column_list=new ArrayList<String>();
			   Field[] fields=User.class.getDeclaredFields();
		        for (Field field : fields) {
					
					temp_column_list.add(field.getName());
		        }
			
			  columnlist=new ArrayList<String>();
			  for(Entry<String, List<String>> e:requestParams.entrySet())
		    {
			   
				columnlist.addAll(e.getValue());
			}
			  columnlist.retainAll(temp_column_list);
		  
		 
		  
		  return null;
		  }
		  else
		  {
			  
		  
	     ModelAndView modelAndView=new ModelAndView();
	     modelAndView.addObject("column",columnlist);
	     modelAndView.addObject("customer",userlist);
	     modelAndView.addObject("class_name",entityClass);
	     
	     //modelAndView.setView(new pdfGenerator());
	     
			   return modelAndView;
		  }
		  
 
	}
	
	@RequestMapping("GenerateExcelDocument")
	public ModelAndView getExcel(@RequestParam MultiValueMap<String,String> requestParams)
	{  
 
		  if(requestParams.isEmpty()!=true)
		  {
			  List<String> temp_column_list=new ArrayList<String>();
			   Field[] fields=User.class.getDeclaredFields();
		        for (Field field : fields) {
					
					temp_column_list.add(field.getName());
		        }
			
			  columnlist=new ArrayList<String>();
			 for(Entry<String, List<String>> e:requestParams.entrySet())
		    {
			 
				columnlist.addAll(e.getValue());
			}
			  columnlist.retainAll(temp_column_list);
			  
		 
		  
		  return null;
		  }
		  else
		  {
			  
		  
	     ModelAndView modelAndView=new ModelAndView();
	     modelAndView.addObject("column",columnlist);
	     modelAndView.addObject("customer",userlist);
	     modelAndView.addObject("class_name",entityClass);
	    
 	     //modelAndView.setView(new ExcelGenerator());
  			   return modelAndView;
		  }
		  
 
	}


	
	 @RequestMapping("UserSummaryPage")
	 public String getDataWithPagination(@RequestParam(value="currentPage",defaultValue="1")int currentPage,Model theModel,HttpSession httpSession,HttpServletRequest req)
	 {
		 	int start = (currentPage-1) * paginationLogic.NO_OF_RECORDS;
			int noOfRecords = paginationLogic.NO_OF_RECORDS;
			
            userlist=entityDAO.getEntities(start, noOfRecords,entityClass);	 
			Long count=entityDAO.getCount(entityClass);
 			Map<String ,Object> paginationDetails=paginationLogic.defualtMethod(count, currentPage);

 			theModel.addAttribute("customer",userlist);
 			theModel.addAttribute("users",httpSession.getAttribute("userSession"));
 		    theModel.addAttribute("paginationDetails",paginationDetails);
 		    BaseUrlClass baseUrlClass=new BaseUrlClass();
  			String baseUrl=baseUrlClass.getBaseUrl(req);
  			theModel.addAttribute("baseUrl",baseUrl);
  			
	 	  	
			 	return "manage_users";
	 }
	 
	 	@GetMapping("UserSummaryWithSearchPage")
		public String getDataWithSearchAndPagination(@RequestParam() org.springframework.util.MultiValueMap<String, String> requestParams,Model theModel,HttpSession httpSession,HttpServletRequest req)
	 	{
		 	   List<String> tempCurrentPage =requestParams.get("currentPage");
	 		 
	 		   int currentPage=1;
			   if(tempCurrentPage!=null)
			   {
				   currentPage=Integer.parseInt(tempCurrentPage.get(0));
				   
			   }
			   requestParams.remove("currentPage");
			    //----------------start -----------search terms ---------------------------------
			   int start = (currentPage-1) * paginationLogic.NO_OF_RECORDS;
			   int noOfRecords = paginationLogic.NO_OF_RECORDS;
			   
			  // Map<String,List<String>> paramToSearch=new HashMap<String,List<String>>();
			   HashMap<String,HashMap<String,List<String>>> paramToSearch =new HashMap<>();
			   HashMap<String, List<String>> searchWithoutDate =new HashMap<>();
			   
			   HashMap<String,String> paramToSort=new HashMap<String,String>();
	 
			   	
	 		    for(Entry<String, List<String>> e:requestParams.entrySet())
			   {
	 		    	if(e.getKey().equals("sortorder") || e.getKey().equals("sortvalue"))
			    	{
			    		paramToSort.put(e.getKey(),e.getValue().get(0));
			     	}
	 		    	else if(e.getValue().isEmpty()!=true)
	 		    	{
	 		     	   searchWithoutDate.put(e.getKey(), e.getValue());   		  
			       }
			   } 
	 		    
	 		    if(!(searchWithoutDate.isEmpty()))
	 		    {
	 		    	paramToSearch.put("searchWithoutDate", searchWithoutDate);
	 		    }
	 		    
	 		    
	  	       if(paramToSort.isEmpty()!=true)
		       {
		    	   httpSession.setAttribute("paramToSearch",paramToSearch);
		    	   httpSession.setAttribute("paramToSort",paramToSort);
		       }
		       else
		       {
		            paramToSearch =  (HashMap<String, HashMap<String, List<String>>>) httpSession.getAttribute("paramToSearch");
				    paramToSort =  (HashMap<String, String>) httpSession.getAttribute("paramToSort");
		       }
	 		  
	  	       userlist=entityDAO.getEntitiesWithSearch(start, noOfRecords,entityClass,paramToSearch,paramToSort);	 
		       Long  count=entityDAO.getCountWithSearch(entityClass);
		       Map<String ,Object> paginationDetails=paginationLogic.defualtMethod(count, currentPage);
		       
		       theModel.addAttribute("customer",userlist);
		       theModel.addAttribute("users",httpSession.getAttribute("users"));
		       theModel.addAttribute("paginationDetails",paginationDetails);
		       
		       	BaseUrlClass baseUrlClass=new BaseUrlClass();
	  			String baseUrl=baseUrlClass.getBaseUrl(req);
	  			theModel.addAttribute("baseUrl",baseUrl);
	  			
	 			       
			   return "manage_users";
			
		}

}
