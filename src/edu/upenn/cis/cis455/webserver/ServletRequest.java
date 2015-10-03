package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

/**
 * @author Todd J. Green
 */
class ServletRequest implements HttpServletRequest {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	private Properties m_params = new Properties();
	private Properties m_props = new Properties();
	private Session m_session = null;
	private String char_encoding = "ISO-8859-1";
	
	private String m_method;
	private String uri;
	private String version = "1.1";
	private HashMap<String, String> headers;
	private Socket clientSock;
	private ArrayList<Cookie> cookies;
	private String path_info;
	private String servlet_path;
	
	ServletRequest() {
	}

	ServletRequest(HttpRequest request, Socket clientSock, String url_match) {
		this.m_method = request.getMethod();
		this.uri = request.getUri();
		this.version = request.getVersion();
		this.headers = request.getHeaders();
		this.clientSock = clientSock;
		parseResource(url_match);

	}

	ServletRequest(Session session) {
		m_session = session;
	}
	
	private void parseResource(String url_match){
		URL requestUrl;
		String path;
		
		try {
			requestUrl = new URL(uri);
			path = requestUrl.getPath();
		} catch (MalformedURLException e) {
			if(uri.contains("?"))
				path = uri.split("?")[0];
			else
				path = uri;
		}
		
		boolean isExact = true;
		if(url_match.endsWith("/*")){
			url_match = url_match.replace("/*", "");
			isExact = false;
		}
			
		if(!path.startsWith("/")){
			path = "/"+path;
		}
		
		if(path.startsWith(url_match)){
			servlet_path = url_match;
				
		if(!isExact){
			path_info = path.replace(url_match, "");
		}else
			path_info = "";
				
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getAuthType()
	 */
	public String getAuthType() {
		return "BASIC";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getCookies()
	 */
	public Cookie[] getCookies() {
		if(cookies.toArray().length!=0)
			return (Cookie[]) cookies.toArray();
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletRequest#getDateHeader(java.lang.String)
	 */
	public long getDateHeader(String header) {
		if(headers.containsKey(header.toLowerCase())){
			String date = headers.get(header.toLowerCase()).split("\t")[0];
			
			ArrayList <SimpleDateFormat> possibleFormats = new ArrayList<SimpleDateFormat>();
			possibleFormats.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.ENGLISH));
			possibleFormats.add(new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss z",Locale.ENGLISH));
			possibleFormats.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.ENGLISH));
			
			SimpleDateFormat dateFormat = null;
			Date dateValue = null;
			for(SimpleDateFormat sdf : possibleFormats){
				dateFormat = sdf;
				sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
				try {
					dateValue = sdf.parse(date);
				} catch (ParseException e) {
					logger.error("Could not parse date");
				}

				if(dateValue!=null){
					break;
				}
			}
			
			try{
				if(dateValue != null)
					return dateValue.getTime();
			}catch(IllegalArgumentException e){
				logger.error("Header value cannot be converted to date");
			}

		}
		
		return -1;
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String header) {
		if(headers.containsKey(header.toLowerCase())){
			return headers.get(header.toLowerCase()).split("\t")[0];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getHeaders(java.lang.String)
	 */
	public Enumeration getHeaders(String arg0) {
		String header = arg0.toLowerCase();
		
		if(headers.containsKey(header)){
			ArrayList<String> values = new ArrayList<String>();
			for(String value: headers.get(header).split("\t")){
				values.add(value);
			}
			return Collections.enumeration(values);
		}else{
			return Collections.emptyEnumeration();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getHeaderNames()
	 */
	public Enumeration getHeaderNames() {
		if(!headers.isEmpty())
			return Collections.enumeration(headers.keySet());
		else
			return Collections.emptyEnumeration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getIntHeader(java.lang.String)
	 */
	public int getIntHeader(String header) {
		if(headers.containsKey(header.toLowerCase())){
			String value = headers.get(header.toLowerCase()).split("\t")[0];
			try{
				int val = Integer.parseInt(value);
				return val;
			}catch(NumberFormatException e){
				logger.error("Could not parse header value to int");
			}
		}
		
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getMethod()
	 */
	public String getMethod() {
		return m_method;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getPathInfo()
	 */
	public String getPathInfo() {
		return path_info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getPathTranslated()
	 */
	public String getPathTranslated() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getContextPath()
	 */
	public String getContextPath() {
		return "";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getQueryString()
	 */
	public String getQueryString() {
		if(m_method.equals("GET")){
			return uri.split("?")[1];
		}
		logger.info("Request not of type GET");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRemoteUser()
	 */
	public String getRemoteUser() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isUserInRole(java.lang.String)
	 */
	public boolean isUserInRole(String arg0) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getUserPrincipal()
	 */
	public Principal getUserPrincipal() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestedSessionId()
	 */
	public String getRequestedSessionId() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestURI()
	 */
	public String getRequestURI() {
		try {
			URL resourceUrl = new URL(uri);
			return resourceUrl.getPath();
		} catch (MalformedURLException e) {
			return uri;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getRequestURL()
	 */
	public StringBuffer getRequestURL() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getServletPath()
	 */
	public String getServletPath() {
		return servlet_path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getSession(boolean)
	 */
	public HttpSession getSession(boolean arg0) {
		if (arg0) {
			if (!hasSession()) {
				m_session = new Session();
			}
		} else {
			if (!hasSession()) {
				m_session = null;
			}
		}
		return m_session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#getSession()
	 */
	public HttpSession getSession() {
		return getSession(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdValid()
	 */
	public boolean isRequestedSessionIdValid() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromCookie()
	 */
	public boolean isRequestedSessionIdFromCookie() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromURL()
	 */
	public boolean isRequestedSessionIdFromURL() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.http.HttpServletRequest#isRequestedSessionIdFromUrl()
	 */
	public boolean isRequestedSessionIdFromUrl() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) {
		if(m_props.containsKey(arg0))
			return m_props.get(arg0);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		if(!m_props.isEmpty())
			return Collections.enumeration(m_props.keySet());
		else
			return Collections.emptyEnumeration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return char_encoding;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String arg0)
			throws UnsupportedEncodingException {
		char_encoding = arg0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getContentLength()
	 */
	public int getContentLength() {
		if(this.headers.containsKey("content-length")){
			return Integer.parseInt(this.headers.get("content-length").split("\t")[0]);
		}
		
		return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getContentType()
	 */
	public String getContentType() {
		if(this.headers.containsKey("content-type")){
			return this.headers.get("content-type").split("\t")[0];
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getInputStream()
	 */
	public ServletInputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameter(java.lang.String)
	 */
	public String getParameter(String arg0) {
		if(m_params.containsKey(arg0))
			return m_params.getProperty(arg0);
		else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterNames()
	 */
	public Enumeration getParameterNames() {
		if(m_params.isEmpty())
			return Collections.emptyEnumeration();
		return Collections.enumeration(m_params.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterValues(java.lang.String)
	 */
	
	//TODO
	public String[] getParameterValues(String arg0) {
		if(!m_params.containsKey(arg0))
			return null;
		else{
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getParameterMap()
	 */
	public Map getParameterMap() {
		Map <String, String []> map = new HashMap <String,String[]>();
		for(Object key : m_params.keySet()){
			map.put(key.toString(), getParameterValues(key.toString()));
		}
		return map;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getProtocol()
	 */
	public String getProtocol() {
		String protocol = "HTTP/"+this.version;
		return protocol;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getScheme()
	 */
	public String getScheme() {
		return "http";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getServerName()
	 */
	public String getServerName() {
		if(headers.containsKey("host")){
			String value = headers.get("host").split("\t")[0];
			if(value.contains(":"))
				return value.split(":")[0];
			else
				return value;
		}else
			return HttpServer.serverSock.getInetAddress().getHostAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getServerPort()
	 */
	public int getServerPort() {
		if(headers.containsKey("host")){
			String value = headers.get("host").split("\t")[0];
			if(value.contains(":")){
				return Integer.parseInt(value.split(":")[1]);
			}else
				return HttpServer.getPort();
		}else{
			return HttpServer.getPort();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getReader()
	 */
	public BufferedReader getReader() throws IOException {
		StringReader sr = new StringReader(HttpRequest.response_body);
		BufferedReader br = new BufferedReader(sr);
		return br;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRemoteAddr()
	 */
	public String getRemoteAddr() {
		return clientSock.getInetAddress().getHostAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRemoteHost()
	 */
	public String getRemoteHost() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#setAttribute(java.lang.String,
	 * java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		m_props.put(arg0, arg1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		m_props.remove(arg0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocale()
	 */
	public Locale getLocale() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocales()
	 */
	public Enumeration getLocales() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#isSecure()
	 */
	public boolean isSecure() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRequestDispatcher(java.lang.String)
	 */
	public RequestDispatcher getRequestDispatcher(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRealPath(java.lang.String)
	 */
	public String getRealPath(String arg0) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getRemotePort()
	 */
	public int getRemotePort() {
		return clientSock.getPort();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocalName()
	 */
	public String getLocalName() {
		return HttpServer.serverSock.getInetAddress().getHostName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocalAddr()
	 */
	public String getLocalAddr() {
		return HttpServer.serverSock.getInetAddress().getHostAddress();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.ServletRequest#getLocalPort()
	 */
	public int getLocalPort() {
		return HttpServer.getPort();
	}

	void setMethod(String method) {
		m_method = method;
	}

	void setParameter(String key, String value) {
		m_params.setProperty(key, value);
	}

	void clearParameters() {
		m_params.clear();
	}

	boolean hasSession() {
		return ((m_session != null) && m_session.isValid());
	}

	
}