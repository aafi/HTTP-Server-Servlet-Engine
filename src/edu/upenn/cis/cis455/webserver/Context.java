package edu.upenn.cis.cis455.webserver;
import javax.servlet.*;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Nick Taylor
 */
public class Context implements ServletContext {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	private HashMap<String,Object> attributes;
	private HashMap<String,String> initParams;
	
	public Context() {
		attributes = new HashMap<String,Object>();
		initParams = new HashMap<String,String>();
	}
	
	public Object getAttribute(String name) {
		if(!attributes.containsKey(name))
			return null;
		return attributes.get(name);
	}
	
	public Enumeration getAttributeNames() {
		Set<String> keys = attributes.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}
	
	public ServletContext getContext(String name) {
		return this;
	}
	
	public String getInitParameter(String name) {
		if(!initParams.containsKey(name))
			return null;
		return initParams.get(name);
	}
	
	public Enumeration getInitParameterNames() {
		if(initParams.isEmpty())
			return Collections.emptyEnumeration();

		Set<String> keys = initParams.keySet();
		Vector<String> atts = new Vector<String>(keys);
		return atts.elements();
	}
	
	public int getMajorVersion() {
		return 2;
	}
	
	public String getMimeType(String file) {
		return null;
	}
	
	public int getMinorVersion() {
		return 4;
	}
	
	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}
	
	//TODO
	public String getRealPath(String path) {
		return null;
	}
	
	public RequestDispatcher getRequestDispatcher(String name) {
		return null;
	}
	
	public java.net.URL getResource(String path) {
		return null;
	}
	
	public java.io.InputStream getResourceAsStream(String path) {
		return null;
	}
	
	public java.util.Set getResourcePaths(String path) {
		return null;
	}
	
	//TODO
	public String getServerInfo() {
		return "Test Harness";
	}
	
	public Servlet getServlet(String name) {
		return null;
	}
	
	public String getServletContextName() {
		return ParseWebXml.display_name;
	}
	
	public Enumeration getServletNames() {
		return null;
	}
	
	public Enumeration getServlets() {
		return null;
	}
	
	public void log(Exception exception, String msg) {
		return;
	}
	
	public void log(String msg) {
		return;
	}
	
	public void log(String message, Throwable throwable) {
		return;
	}
	
	public void removeAttribute(String name) {
		attributes.remove(name);
	}
	
	public void setAttribute(String name, Object object) {
		attributes.put(name, object);
	}
	
	void setInitParam(String name, String value) {
		initParams.put(name, value);
	}

	public HashMap<String, Object> getAttributes() {
		return attributes;
	}

	public void setAttributes(HashMap<String, Object> attributes) {
		this.attributes = attributes;
	}

	public HashMap<String, String> getInitParams() {
		return initParams;
	}

	public void setInitParams(HashMap<String, String> initParams) {
		this.initParams = initParams;
	}
}
