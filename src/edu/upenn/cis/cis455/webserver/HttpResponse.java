package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import org.apache.log4j.Logger;

public class HttpResponse {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	static final Utils util = new Utils();
	private final String baseDir;
	private final HttpRequest request;
	private String resource;
	private File file;
	
	//Response Fields
	private String statusLine;
	private int responseCode = 0;
	private String message;
	private String version;
	private byte [] contents;
	private HashMap <String, String> headers;
	
//	static final String HTML_START =
//			"<html>" +
//			"<title>HTTP Server in java</title>" +
//			"<body>";
//
//	static final String HTML_END =
//			"</body>" +
//			"</html>";
	
	/** 
	 * Constructor
	 * Checks whether the request has the appropriate Method
	 * Checks whether the request is a bad request
	 * Sets appropriate response code and message
	 * @param HttpRequest to be handled
	 * @param Base Directory
	 * @param HttpRequest is a bad request or not
	 * @param Client Socket
	 **/
	public HttpResponse(HttpRequest request,String baseDir, Boolean isGoodRequest){
		this.baseDir = baseDir; 
		this.request = request;
		
		//Check for the method
		
		if(!isGoodRequest){
			version = "HTTP/"+request.getVersion();
			responseCode = 400;
			message = "Bad Request";
		}else if(!request.getMethod().equals("GET") && !request.getMethod().equals("HEAD")){
			version = "HTTP/"+request.getVersion();
			responseCode = 501;
			message = "Not Implemented";
		}
	}
	
	/** 
	 * Performs appropriate checks on the requested resource
	 **/
	
	private void checkResource(){
		logger.info("Requested path: "+request.getUri());
		Path resourcePath = Paths.get(baseDir,request.getUri()).normalize();
		logger.info("Resource path: "+resourcePath.toString());
		Path rootPath = Paths.get(baseDir);
		logger.info("Root Path: "+rootPath.toString());
		
		resource = resourcePath.toString();
		file = resourcePath.toFile();
		
		//Check if resource is a file or directory
		if(!resourcePath.startsWith(rootPath)){
			responseCode = 403;
			message = "Forbidden";
		}else if(!file.exists()){
			responseCode = 404;
			message = "Not Found";
		}else if(Files.isReadable(file.toPath())){
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
				headers.put("Content-Length",length.toString()+"\r\n");
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
		
		
	}
	
	
	/** 
	 * Process 1.0 client requests 
	 **/
	public void processRequest10(){
		headers = new HashMap<String, String>();
		version = "HTTP/1.0";
		checkResource();
		
	}
	
	public void sendStatus(OutputStream output) throws IOException{
		statusLine = version+" "+responseCode+" "+message+"\r\n";
		output.write(statusLine.getBytes());
	}
	
	/**
	 * Sends the body of HTTP Response in case of GET request
	 * @param OutputStream
	 * @throws IOException 
	 */
	private void sendBody(OutputStream output) throws IOException{
		
		output.write(contents);
	}
	
	/**
	 * Sends the HTTP response back to the client
	 * @param Client Socket
	 * @throws IOException 
	 */
	public void sendResponse(OutputStream output) throws IOException{
		sendStatus(output);
		for(String key: headers.keySet()){
			output.write((key+": "+headers.get(key)).getBytes());
		}
		output.write(("\r\n").getBytes());
		
		//Send actual content in case it is GET request
		if(request.getMethod().equals("GET")){
			sendBody(output);
		}
		
		output.close();
		
		logger.info("Sent Output");
			
			
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
