package edu.upenn.cis.cis455.webserver;



import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * A test servlet to dump all the request information.
 *
 */
public class CookieServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		PrintWriter out = response.getWriter();
		response.setContentType("text/html");
		
		out.write("<html><head><title>Cookies Test Servlet</title></head><body>");
		out.write("<h1>Cookie Test</h1><p>Please Enter a name and a value for your cookie that the server should send back to the client</p>");
		out.write("<form method=\"post\" action=\"./cookies\">");
		out.write("Cookie Name: <input type=\"text\" size=\"10\" name=\"cookiename\">");
		out.write("Cookie Value: <input type=\"text\" size=\"20\" name=\"cookievalue\">");
		out.write("<input type=\"submit\" value=\"Set Cookie\"></form>");
		out.write("<h2>Currently set cookies:</h2>");	
		
		Cookie[] cookies = request.getCookies();
		if(cookies == null) {
			out.write("there are no cookies set currently.");
		} else {
			for (Cookie c : cookies) {
				out.write(String.format("<div>Cookie: %s %s</div>", c.getName(), c.getValue()));
			}
		}
		out.write("<p><a href=\"index.html\">Go Back.</a></p></body></html>");
	}
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String name = request.getParameter("cookiename");
		String value = request.getParameter("cookievalue");
		
		PrintWriter out = response.getWriter();
		if(name == null || value == null) {
			response.sendError(400, "you did not set the cookie correctly in the webform.");
		} else {
			response.addCookie(new Cookie(name, value));
			response.sendRedirect("./cookies");
		}
	}
}
