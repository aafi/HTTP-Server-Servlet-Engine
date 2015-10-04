package edu.upenn.cis.cis455.webserver;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

/**
 * @author tjgreen
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ServletResponse implements HttpServletResponse {

	static final Logger logger = Logger.getLogger(HttpServer.class);
	private HashMap <String, String> response_headers = new HashMap<String,String>();
	
	private String content_type = "text/html";
	private String char_encoding = "ISO-8859-1";
	private int content_length;
	private int buffer_size = 0;
	public StringWriter sw;
	private Locale loc;
	private int statusCode = 0 ;
	private Socket clientSock;
	private boolean isCommitted = false;
	private boolean writerCalled = false;
	private boolean encodingSet = false;
	
	class ServletWriter extends PrintWriter{
		
		public ServletWriter(StringWriter sw) {
			super(sw);
		}
		
		public void flush(){
			try {
				flushBuffer();
			} catch (IOException e) {
				logger.error("Could not flush buffer");
			}
		}
		
	}
	
	public ServletResponse(Socket clientSock){
		this.clientSock = clientSock;
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addCookie(javax.servlet.http.Cookie)
	 */
	public void addCookie(Cookie cookie) {
		String key = "Set-Cookie";
		String value = cookie.getName()+"="+cookie.getValue()+"; Max-age="+cookie.getMaxAge()+"; Domain="+cookie.getDomain()+"; Path="+cookie.getPath();
		
		logger.info(key+" "+value);
		this.addHeader(key, value);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#containsHeader(java.lang.String)
	 */
	public boolean containsHeader(String arg0) {
		if(response_headers.containsKey(arg0))
			return true;
		else
			return false;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeURL(java.lang.String)
	 */
	public String encodeURL(String arg0) {
		return arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectURL(java.lang.String)
	 */
	public String encodeRedirectURL(String arg0) {
		return arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeUrl(java.lang.String)
	 */
	public String encodeUrl(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#encodeRedirectUrl(java.lang.String)
	 */
	public String encodeRedirectUrl(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int, java.lang.String)
	 */
	public void sendError(int arg0, String arg1) throws IOException {
		if(this.isCommitted())
			throw new IllegalStateException();
		
		String errorPage = Utils.createHTML("Error Page", arg0+" "+arg1);
		this.response_headers.put("Content-Type", "text/html");
		PrintWriter pw = this.getWriter();
		pw.write(errorPage);
		this.isCommitted = true;
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendError(int)
	 */
	public void sendError(int arg0) throws IOException {
		sendError(arg0,Utils.getStatusMessage(arg0));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect(java.lang.String)
	 */
	public void sendRedirect(String arg0) throws IOException {
		System.out.println("[DEBUG] redirect to " + arg0 + " requested");
		System.out.println("[DEBUG] stack trace: ");
		Exception e = new Exception();
		StackTraceElement[] frames = e.getStackTrace();
		for (int i = 0; i < frames.length; i++) {
			System.out.print("[DEBUG]   ");
			System.out.println(frames[i].toString());
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setDateHeader(java.lang.String, long)
	 */
	public void setDateHeader(String arg0, long arg1) {
		this.response_headers.put(arg0, Long.toString(arg1));

	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addDateHeader(java.lang.String, long)
	 */
	public void addDateHeader(String arg0, long arg1) {
		if(this.response_headers.containsKey(arg0)){
			String value = this.response_headers.get(arg0).toString();
			String new_value = value+","+arg1;
			this.response_headers.put(arg0, new_value);
			
		}else
			this.response_headers.put(arg0, Long.toString(arg1));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setHeader(java.lang.String, java.lang.String)
	 */
	public void setHeader(String arg0, String arg1) {
		this.response_headers.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addHeader(java.lang.String, java.lang.String)
	 */
	public void addHeader(String arg0, String arg1) {
		if(this.response_headers.containsKey(arg0)){
			String value = this.response_headers.get(arg0).toString();
			String new_value = value+","+arg1;
			this.response_headers.put(arg0, new_value);
			
		}else
			this.response_headers.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setIntHeader(java.lang.String, int)
	 */
	public void setIntHeader(String arg0, int arg1) {
		this.response_headers.put(arg0, Integer.toString(arg1));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#addIntHeader(java.lang.String, int)
	 */
	public void addIntHeader(String arg0, int arg1) {
		if(this.response_headers.containsKey(arg0)){
			String value = this.response_headers.get(arg0).toString();
			String new_value = value+","+arg1;
			this.response_headers.put(arg0, new_value);
			
		}else
			this.response_headers.put(arg0, Integer.toString(arg1));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int)
	 */
	public void setStatus(int arg0) {
		this.statusCode = arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServletResponse#setStatus(int, java.lang.String)
	 */
	public void setStatus(int arg0, String arg1) {
		return;

	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getCharacterEncoding()
	 */
	public String getCharacterEncoding() {
		return this.char_encoding;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getContentType()
	 */
	public String getContentType() {
		return this.content_type;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getOutputStream()
	 */
	public ServletOutputStream getOutputStream() throws IOException {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getWriter()
	 */
	public PrintWriter getWriter() throws IOException {
		this.writerCalled = true;
		sw = new StringWriter(buffer_size);
		return new ServletWriter(this.sw);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setCharacterEncoding(java.lang.String)
	 */
	public void setCharacterEncoding(String arg0) {
		if(Charset.isSupported(arg0)){
			this.char_encoding = arg0;
			this.encodingSet = true;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentLength(int)
	 */
	public void setContentLength(int arg0) {
		this.content_length = arg0;
		this.response_headers.put("Content-Length",Integer.toString(this.content_length));
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setContentType(java.lang.String)
	 */
	public void setContentType(String arg0) {
		if(!this.isCommitted()){
			this.content_type = arg0;
			this.response_headers.put("Content-Type", content_type);
			
			if(!this.writerCalled && this.content_type.contains(";charset=")){
				this.setCharacterEncoding(this.content_type.split("=")[1]);
			}
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setBufferSize(int)
	 */
	public void setBufferSize(int arg0) {
		this.buffer_size = arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getBufferSize()
	 */
	public int getBufferSize() {
		return this.buffer_size;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#flushBuffer()
	 */
	public void flushBuffer() throws IOException {
		if(!isCommitted()){
			
			//Throws IOException if cannot open client socket
			OutputStream output = this.clientSock.getOutputStream();
			
			if(!this.response_headers.containsKey("Content-Length"))
				this.response_headers.put("Content-Length", Integer.toString(this.sw.toString().length()));
			
			if(!this.response_headers.containsKey("Content-Type"))
				this.response_headers.put("Content-Type", this.getContentType());
			
			if(statusCode == 0)
				statusCode = 200;
			
			String statusLine = "HTTP/"+ServletRequest.version+" "+this.statusCode+" "+Utils.getStatusMessage(this.statusCode)+"\r\n";
			StringBuilder sb = new StringBuilder();
			
			for(String key : response_headers.keySet()){
				if(key.equals("Set-Cookie")){
					String [] cookieVals = response_headers.get(key).split(",");
					for(String cookie : cookieVals){
						sb.append("Set-Cookie"+": "+cookie+"\r\n");
					}
				}
				else
					sb.append(key+": "+response_headers.get(key)+"\r\n");
			}
			sb.append("\r\n");
			String headers = sb.toString();
			String body = this.sw.toString();
			
			//Sending the Response
			output.write(statusLine.getBytes());
			output.write(headers.getBytes());
			output.write(body.getBytes(this.char_encoding));
			
			output.close();
			isCommitted = true;
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#resetBuffer()
	 */
	public void resetBuffer() {
		if(isCommitted()){
			throw new IllegalStateException();
		}
		
		this.sw.getBuffer().setLength(0);
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#isCommitted()
	 */
	public boolean isCommitted() {
		return this.isCommitted ;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#reset()
	 */
	public void reset() {
		if(isCommitted()){
			throw new IllegalStateException();
		}
		
		this.resetBuffer();
		this.response_headers.clear();
		this.statusCode = 0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#setLocale(java.util.Locale)
	 */
	
	//TODO
	public void setLocale(Locale arg0) {
		if(!this.isCommitted() && !this.writerCalled && !this.encodingSet )
			this.loc = arg0;
		
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletResponse#getLocale()
	 */
	public Locale getLocale() {
		return this.loc;
	}

}
