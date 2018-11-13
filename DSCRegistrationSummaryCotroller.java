package dsc.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dsc.dao.EntityDAO;
import dsc.dao.PaginationLogic;
import dsc.entity.DSCRegistration;
import dsc.entity.User;

//import dsc.pdfGen.pdfGenerator;


@Controller
@RequestMapping("DSCRegistration")
public class DSCRegistrationSummaryCotroller {

	Class<DSCRegistration> entityClass = DSCRegistration.class;
	List<DSCRegistration> userlist = new ArrayList<>();

	List<String> columnlist;
	@Autowired
	PaginationLogic paginationLogic;

	@Autowired
	private EntityDAO entityDAO;

	@RequestMapping("GeneratePDFDocument")
	public ModelAndView getPdf(@RequestParam MultiValueMap<String, String> requestParams) {

		if (requestParams.isEmpty() != true) {
			List<String> temp_column_list = new ArrayList<String>();
			Field[] fields = DSCRegistration.class.getDeclaredFields();
			for (Field field : fields) {
				System.out.println("valuessss:" + field.getName());
				temp_column_list.add(field.getName());
			}

			columnlist = new ArrayList<String>();
			for (Entry<String, List<String>> e : requestParams.entrySet()) {
				System.out.println(e.getValue());
				columnlist.addAll(e.getValue());
			}
			columnlist.retainAll(temp_column_list);

			return null;
		} else {

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("column", columnlist);
			modelAndView.addObject("customer", userlist);
			modelAndView.addObject("class_name", entityClass);
			System.out.println("us:::" + userlist);
			//modelAndView.setView(new pdfGenerator());
			return modelAndView;
		}

	}

	@RequestMapping("GenerateExcelDocument")
	public ModelAndView getExcel(@RequestParam MultiValueMap<String, String> requestParams) {

		if (requestParams.isEmpty() != true) {
			List<String> temp_column_list = new ArrayList<String>();
			Field[] fields = DSCRegistration.class.getDeclaredFields();
			for (Field field : fields) {
				System.out.println("valuessss:" + field.getName());
				temp_column_list.add(field.getName());
			}

			columnlist = new ArrayList<String>();
			for (Entry<String, List<String>> e : requestParams.entrySet()) {
				System.out.println(e.getValue());
				columnlist.addAll(e.getValue());
			}
			columnlist.retainAll(temp_column_list);

			return null;
		} else {

			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject("column", columnlist);
			modelAndView.addObject("customer", userlist);
			modelAndView.addObject("class_name", entityClass);
			System.out.println("us:::" + userlist);
			//modelAndView.setView(new ExcelGenerator());
			return modelAndView;
		}

	}

	@RequestMapping("DSCData")
	public String DscData() {

		return "Dsc_details_capture";

	}

	@RequestMapping("DSCRegistrationSummaryPage")
	public ModelAndView getDataWithPagination(@RequestParam(value = "currentPage", defaultValue = "1") int currentPage,
			ModelAndView theModel, HttpSession httpSession, HttpServletRequest req) {
		httpSession.removeAttribute("paramToSearch");
		int start = (currentPage - 1) * paginationLogic.NO_OF_RECORDS;
		int noOfRecords = paginationLogic.NO_OF_RECORDS;
		userlist = entityDAO.getEntities(start, noOfRecords, entityClass);
		Long count = entityDAO.getCount(entityClass);
		Map<String, Object> paginationDetails = paginationLogic.defualtMethod(count, currentPage);
		theModel.addObject("customers", userlist);
		httpSession.setAttribute("customerforpdf", userlist);
		User s = (User) httpSession.getAttribute("userSession");

		theModel.addObject("users", httpSession.getAttribute("userSession"));
		theModel.addObject("paginationDetails", paginationDetails);
		theModel.addObject("status_enum", status_enum.values());
		theModel.setViewName("DSC_registration_users_view");

		BaseUrlClass baseUrlClass = new BaseUrlClass();
		String baseUrl = baseUrlClass.getBaseUrl(req);
		theModel.addObject("baseUrl", baseUrl);

		return theModel;
	}

	@RequestMapping(value = "DSCRegistrationSummaryWithSearchPage", method = RequestMethod.GET)
	public ModelAndView getDataWithSearchAndPagination(@RequestParam() MultiValueMap<String, String> requestParams,
			ModelAndView theModel, HttpSession httpSession, HttpServletRequest req) {
		String dateType = "registered_date";
		String dateValueToSearch = null;
		List<String> dateValue = new ArrayList<>();

		List<String> tempCurrentPage = requestParams.get("currentPage");

		int currentPage = 1;
		if (tempCurrentPage != null) {
			currentPage = Integer.parseInt(tempCurrentPage.get(0));

		}
		requestParams.remove("currentPage");
		// ----------------start -----------search terms
		// ---------------------------------
		int start = (currentPage - 1) * paginationLogic.NO_OF_RECORDS;
		int noOfRecords = paginationLogic.NO_OF_RECORDS;

		// Map<String,String> paramToSearch=new HashMap<String,String>();
		// Map<String,String> paramToSort=new HashMap<String,String>();

		HashMap<String, List<String>> dateToSearch = new HashMap<String, List<String>>();
		HashMap<String, List<String>> searchWithoutDate = new HashMap<String, List<String>>();
		HashMap<String, String> paramToSort = new HashMap<String, String>();
		HashMap<String, HashMap<String, List<String>>> paramToSearch = new HashMap<>();

		// Map<String, List<String>> paramToSearch = new
		// HashMap<String,List<String>>();
		// Map<String, String> paramToSort= new HashMap<String,String>();

		for (Entry<String, List<String>> e : requestParams.entrySet()) {

			switch (e.getKey()) {

			case "sortorder":
				paramToSort.put(e.getKey(), e.getValue().get(0));

				break;

			case "sortvalue":
				paramToSort.put(e.getKey(), e.getValue().get(0));
				break;

			case "fromDate":
				System.out.println("From date" + e.getValue().get(0));
				if (e.getValue().get(0).isEmpty() != true) {
					dateValueToSearch = "fromDateToSearch";
					dateValue.add(e.getValue().get(0));
					dateToSearch.put(dateType, dateValue);
				}
				break;

			case "toDate":
				System.out.println("To Date:" + e.getValue().get(0));
				if (e.getValue().get(0).isEmpty() != true) {
					dateValue.add(e.getValue().get(0));

					// if both dates are enetred add key as both date otherwise
					// add key as only toDate
					if (dateValue.size() > 1) {
						dateValueToSearch = "bothDateToSearch";
					} else {
						dateValueToSearch = "toDateToSearch";
					}

					dateToSearch.put(dateType, dateValue);

				}
				break;

			default:
				if (!(e.getValue().get(0).isEmpty())) {
					searchWithoutDate.put(e.getKey(), e.getValue());
				}

				break;

			}

			/*
			 * if(e.getKey().equals("sortorder") ||
			 * e.getKey().equals("sortvalue")) {
			 * paramToSort.put(e.getKey(),e.getValue().get(0));
			 * 
			 * } else if (e.getKey().equals("registered_date")) {
			 * System.out.println("Date-----"+e.getKey()+","+e.getValue());
			 * dateToSearch.put(e.getKey(), e.getValue());
			 * System.out.println("DatetoSearch---"+dateToSearch); } else
			 * if(!(e.getValue().isEmpty())) {
			 * System.out.println("e.getValue.isempty: "+e.getValue().isEmpty())
			 * ;
			 * System.out.println(" before MMMMMMMMMMMMMMMMMMM"+e.getKey()+","+e
			 * .getValue()); paramToSearch.put(e.getKey(),e.getValue());
			 * 
			 * 
			 * System.out.println("MMMMMMMMMMMMMMMMMMM"+e.getKey()+","+e.
			 * getValue()); }
			 */
		}

		 if(dateToSearch.isEmpty()!=true)
		    {
		    	paramToSearch.put(dateValueToSearch, dateToSearch);
		    }
		   
		if (!(searchWithoutDate.isEmpty())) {
			paramToSearch.put("searchWithoutDate", searchWithoutDate);
		}

		System.out.println("paramtosearh after switch: " + paramToSearch);
		System.out.println("paramtosort after switch: " + paramToSort);

		if (paramToSort.isEmpty() != true) {
			System.out.println("sssss:" + paramToSearch);
			httpSession.setAttribute("paramToSearch", paramToSearch);
			httpSession.setAttribute("paramToSort", paramToSort);
		} else {
			System.out.println("nnnnn:" + paramToSearch);
			paramToSearch = (HashMap<String, HashMap<String, List<String>>>) httpSession.getAttribute("paramToSearch");
			paramToSort = (HashMap<String, String>) httpSession.getAttribute("paramToSort");
		}
		userlist = entityDAO.getEntitiesWithSearch(start, noOfRecords, entityClass, paramToSearch, paramToSort);
		Long count = entityDAO.getCountWithSearch(entityClass);
		Map<String, Object> paginationDetails = paginationLogic.defualtMethod(count, currentPage);
		theModel.addObject("customers", userlist);
		theModel.addObject("users", httpSession.getAttribute("userSession"));
		theModel.addObject("paginationDetails", paginationDetails);
		theModel.addObject("status_enum", status_enum.values());
		theModel.setViewName("DSC_registration_users_view");

		BaseUrlClass baseUrlClass = new BaseUrlClass();
		String baseUrl = baseUrlClass.getBaseUrl(req);
		theModel.addObject("baseUrl", baseUrl);

		return theModel;

	}

}
