package edu.upenn.cis.cis455.webserver;

import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TestServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		       throws java.io.IOException
		  {
		    response.setContentType("text/html");
		    PrintWriter out = response.getWriter();
		    out.println("<html><head><title>Test</title></head><body>");
		    out.println("RequestURL: ["+request.getRequestURL()+"]<br>");
		    out.println("RequestURI: ["+request.getRequestURI()+"]<br>");
		    out.println("PathInfo: ["+request.getPathInfo()+"]<br>");
		    out.println("Context path: ["+request.getContextPath()+"]<br>");
		    out.println("Header: ["+request.getHeader("Accept-Language")+"]<br>");
		    out.println("</body></html>");
		  }

}