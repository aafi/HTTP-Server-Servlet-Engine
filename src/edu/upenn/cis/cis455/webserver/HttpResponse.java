package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.log4j.Logger;

public class HttpResponse {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	static final Utils util = new Utils();
	private final String baseDir;
	private final HttpRequest request;
	private String statusLine;
	private int responseCode = 0;
	private String message;
	private String version;
	private byte [] contents;
	private HashMap <String, String> headers;
	
	/** 
	 * Constructor
	 * Checks whether the request has the appropriate Method
	 * Checks whether the request is a bad request
	 * Sets appropriate response code and message
	 * @param HttpRequest to be handled
	 * @param Base Directory
	 * @param HttpRequest is a bad request or not
	 **/
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
	
	/** 
	 * Performs appropriate checks on the requested resource
	 **/
	
	private void checkResource(){
		
		String resource = Paths.get(baseDir,request.getUri()).normalize().toString();
		File file = new File(resource);
		
		//Check if resource is a file or directory
		if(!file.exists()){
			responseCode = 404;
			message = "Not found";
		}else if(!file.canRead()){
			responseCode = 403;
			message = "Forbidden";
		}else if(file.isFile()){
			//Check extension
			String type = null;
			if(file.toString().lastIndexOf(".") != -1 && file.toString().lastIndexOf(".") != 0)
		        type = util.getType(file.toString().substring(file.toString().lastIndexOf(".")+1));
			
			//Check if resource type is supported
			if(type.equals("Not Supported")){
				responseCode = 501;
				message = "Not Implemented";
			}else{ //Assign content-type and content-length
				headers.put("Content-Type", type+"\r\n");
				responseCode = 200;
				message = "OK";
				try {
					contents = util.readResource(file);
				} catch (IOException e) {
					logger.error("File not found");
				}
				Integer length = contents.length;
				headers.put("Content-Length",length.toString());
			}
		}
		
		
		
		
	}
	
	/**
	 * Process 1.1 client requests 
	 **/
	public void processRequest11(){
		headers = new HashMap<String, String>();
		//Inserting appropriate headers in the response
		headers.put("Date", util.getDate()+"\r\n");
		headers.put("Connection", "close\r\n");
		
		version = "HTTP/1.1";
		if(!request.getHeaders().containsKey("host")){
			responseCode = 400;
			message = "Bad Request";
		}else{ //Check the requested resource
			checkResource();
		}
		
		buildResponse();
	}
	
	
	/** 
	 * Process 1.0 client requests 
	 **/
	public void processRequest10(){
		headers = new HashMap<String, String>();
		version = "HTTP/1.0";
		checkResource();
		
	}
	
	/** 
	 * Builds the response to be sent back to the client 
	 **/
	private void buildResponse(){
		statusLine = version+" "+responseCode+" "+message+"\r\n";
		
	}
	
	
	/**
	 * Sends the actual response back to the client
	 * @param Client Socket
	 * @throws IOException 
	 */
	public void sendResponse(Socket clientSock) throws IOException{
			
		OutputStream output = clientSock.getOutputStream();
			
			
	}
	
	
	
	
	/**
	 * GETTER METHODS
	 */
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
