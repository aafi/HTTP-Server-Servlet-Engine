package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class HttpRequest {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	static final Utils util = new Utils();

	private String method;
	private String uri;
	private String version = "1.1";
	private HashMap <String, String> headers;
	public String response_body = null;
	public boolean hasBody = false;
	
	/**
	 * Parses the HTTP request
	 * @param request
	 * @return success/failure of parsing
	 * @throws IOException
	 */
	public boolean parseRequest(BufferedReader request, OutputStream output){
		
		headers = new HashMap<String, String>();
		String line = null, prevHeader = null;
		int lineNumber = 1;
		
		try {
			line = request.readLine();
		} catch (IOException e) {
			logger.error("Could not read request");
		}
		
		while(line!=null && !line.equals("")){
			if(lineNumber == 1){
				boolean error = parseRequestLine(line);
				if(!error){
					return false;
				}
				
				lineNumber++;
			}else{ //Get request headers
				if(line.contains(":\t") || line.contains(": ")){ //The line contains a header
					String [] parts = line.split(":[ \t]");
					
					if(!headers.containsKey(parts[0].toLowerCase())){
						headers.put(parts[0].toLowerCase(), parts[1].trim()+"\t");
					}else {
						StringBuffer sb = new StringBuffer();
						sb.append(headers.get(parts[0].toLowerCase().split("\t")[0]).trim());
						sb.append("\t");
						sb.append(parts[1].trim());
						headers.put(parts[0].toLowerCase(), sb.toString());
					}
					lineNumber++;
					prevHeader = parts[0].toLowerCase();
				}else if(line.startsWith(" ") || line.startsWith("\t")){ //In case a line is continuation of previous header
					String value = headers.get(prevHeader);
					String newValue = value+"\t"+line.trim();
					headers.put(prevHeader, newValue);
				}else{
					logger.error("Badly formed headers");
					return false;
				}
			}
			
			try {
				line = request.readLine();
			} catch (IOException e) {
				logger.info("Could not get next line of request");
			}
			
		} //end of while
		
		//Storing the request body
		
		if(this.method.equals("POST") && this.headers.containsKey("content-length") && this.headers.containsKey("content-type") && (this.headers.get("content-type").startsWith("application/x-www-form-urlencoded"))){
			int length = Integer.parseInt(this.headers.get("content-length").split("\t")[0]);
			
			char [] cbuf = new char [length];
			int bytes_read = 0;
			
			try {
				bytes_read = request.read(cbuf);
			} catch (IOException e) {
				logger.error("Could not read request");
			}
			
			if(bytes_read > 0){
				this.response_body = new String(cbuf);
				this.hasBody = true;
			}
			logger.info("RESPONSE BODY IN HTTP REQUEST: "+response_body);
		}
		return true;
	}
	
	
	/**
	 * Parses first line of request
	 * @param initial request line
	 * @return boolean
	 */
	private boolean parseRequestLine(String line) {
		
		if(line.length() == 0){
			logger.error("Blank Request");
			return false;
		}
		
		String [] parts = line.split("[ \t]+");
		if(parts.length !=3){
			logger.error("Badly formed request line");
			return false;
		}
	
		method = parts[0];
		uri = parts[1];
		if(!parts[2].contains("HTTP/") && !parts[2].split("/")[0].equals("HTTP")){
			logger.error("No HTTP version");
			return false;
		}else{
			version = parts[2].split("/")[1];
			if (!version.equals("1.1") && !version.equals("1.0")) {
				logger.error("Invalid HTTP version");
				return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Getter Methods
	 */
	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public String getVersion() {
		return version;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	
}
