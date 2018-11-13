package dsc.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import dsc.entity.User;
// dsc.pdfGen.pdfGenerator;
@Controller
@RequestMapping("FileGenerate")
public class FileGenratorController {
 
	/*@RequestMapping("PdfGen")
	public ModelAndView getPdf()
	{ 
		   return new ModelAndView(new pdfGenerator(),"customer",name);
		  
	}*/

}
