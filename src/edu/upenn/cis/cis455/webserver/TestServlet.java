package edu.upenn.cis.cis455.webserver;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

public class TestServlet extends HttpServlet {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		       throws java.io.IOException
		  {
		    response.setContentType("text/html");
		    
//		    out.println("<html><head><title>Test</title></head><body>");
//		    out.println("RequestURL: ["+request.getRequestURL()+"]<br>");
//		    out.println("RequestURI: ["+request.getRequestURI()+"]<br>");
//		    out.println("PathInfo: ["+request.getPathInfo()+"]<br>");
//		    out.println("Context path: ["+request.getContextPath()+"]<br>");
//		    out.println("Header: ["+request.getHeader("Accept-Language")+"]<br>");
//		    out.println("Body: ["+request.getReader().read()+"]<br>");
//		    out.println("</body></html>");
		    
//		    String body = "<html><head><title>Test</title></head><body>Body: [SUCCCCESSSSS!!!!] ["+request.getReader().readLine()+"]<br>";
//		    response.setContentLength(body.length());
		    
		    request.getSession(true);
		    PrintWriter out = response.getWriter();
		    request.getSession(true).setMaxInactiveInterval(10);
//		    response.sendRedirect("/");
		    logger.info("Reconstructed url: "+request.getRequestURL().toString());
		    out.write("HI!");
		    logger.info("PARAMETERS: "+request.getParameter("name1"));
		    response.flushBuffer();
		  }

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		       throws java.io.IOException
		  {
		    response.setContentType("text/html");
		    
//		    out.println("<html><head><title>Test</title></head><body>");
//		    out.println("RequestURL: ["+request.getRequestURL()+"]<br>");
//		    out.println("RequestURI: ["+request.getRequestURI()+"]<br>");
//		    out.println("PathInfo: ["+request.getPathInfo()+"]<br>");
//		    out.println("Context path: ["+request.getContextPath()+"]<br>");
//		    out.println("Header: ["+request.getHeader("Accept-Language")+"]<br>");
//		    out.println("Body: ["+request.getReader().read()+"]<br>");
//		    out.println("</body></html>");
		    
//		    String body = "<html><head><title>Test</title></head><body>Body: [SUCCCCESSSSS!!!!] ["+request.getReader().readLine()+"]<br>";
//		    response.setContentLength(body.length());
		    
		    request.getSession(true);
		    PrintWriter out = response.getWriter();
		    request.getSession(true).setMaxInactiveInterval(10);
//		    response.sendRedirect("cookies/");
		    logger.info("Reconstructed url: "+request.getRequestURL().toString());
		    out.write("HI!");
		    logger.info("PARAMETERS: "+request.getParameter("name1"));
		    response.flushBuffer();
		  }

}
