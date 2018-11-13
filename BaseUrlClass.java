package dsc.controller;

import javax.servlet.http.HttpServletRequest;

	
	{
		
		String scheme = request.getScheme();
	 	String serverName =request.getServerName();
	 	int serverPort = request.getServerPort();
		String contextPath=request.getContextPath();
		
		
		asafdsf
		 
	 	String requestUrl=scheme + "://" + serverName + ":" + serverPort + contextPath;
	 	
		return requestUrl;
		
	}
	
	

}
