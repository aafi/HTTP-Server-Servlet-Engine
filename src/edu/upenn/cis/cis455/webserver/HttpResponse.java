package edu.upenn.cis.cis455.webserver;

import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import org.apache.log4j.Logger;

public class HttpResponse {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	private final String baseDir;
	private final HttpRequest request;
	private int responseCode = 0;
	private String message;
	private String version;
	private HashMap <String, String> headers;
		
	public HttpResponse(HttpRequest request,String baseDir, Boolean isBadRequest){
		this.baseDir = baseDir; 
		this.request = request;
		//Check for the method
		if(!request.getMethod().equals("GET") && !request.getMethod().equals("HEAD")){
			version = "HTTP/"+request.getVersion();
			responseCode = 501;
			message = "Not Implemented";
		}else if(isBadRequest){
			version = "HTTP/"+request.getVersion();
			responseCode = 400;
			message = "Bad Request";
		}
	}
	
	private void checkResource(){
		String resource = Paths.get(baseDir,request.getUri()).normalize().toString();
		
		//Check if resource is a file or directory
		
		
		
		
	}
	
	//Process 1.1 client requests
	public void processRequest11(){
		headers = new HashMap<String, String>();
		
		//Inserting appropriate headers in the response
		headers.put("Date", getDate());
		headers.put("Connection", "close");
		
		version = "HTTP/1.1";
		if(!request.getHeaders().containsKey("host")){
			responseCode = 400;
			message = "Bad Request";
		}else{ //Check the requested resource
			checkResource();
		}
		
		buildResponse();
	}
	
	
	//Process 1.0 client requests
	public void processRequest10(){
		headers = new HashMap<String, String>();
		version = "HTTP/1.0";
		checkResource();
		
	}
	
	//Builds the response to be sent back to the client
	private void buildResponse(){
		
	}
	
	private String getDate(){
		String currentDate = null;
		SimpleDateFormat dateFormatGMT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		dateFormatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		currentDate = dateFormatGMT.format(new Date());
		
		return currentDate;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getMessage() {
		return message;
	}

	public String getVersion() {
		return version;
	}
	
}
