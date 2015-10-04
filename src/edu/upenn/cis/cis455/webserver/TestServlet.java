package edu.upenn.cis.cis455.webserver;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

public class TestServlet extends HttpServlet {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		       throws java.io.IOException
		  {
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
//		    out.println("<html><head><title>Test</title></head><body>");
//		    out.println("RequestURL: ["+request.getRequestURL()+"]<br>");
//		    out.println("RequestURI: ["+request.getRequestURI()+"]<br>");
//		    out.println("PathInfo: ["+request.getPathInfo()+"]<br>");
//		    out.println("Context path: ["+request.getContextPath()+"]<br>");
//		    out.println("Header: ["+request.getHeader("Accept-Language")+"]<br>");
//		    out.println("Body: ["+request.getReader().read()+"]<br>");
//		    out.println("</body></html>");
		    
		    String body = "<html><head><title>Test</title></head><body>Body: ["+request.getReader().read()+"]<br>";
		    response.setContentLength(body.length());
		    response.addCookie(request.getCookies()[0]);
		    logger.info("COOKIES: "+request.getCookies()[0].getName());
		    out.write(body);
		    out.flush();
		  }

}
