package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
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
	private File file;
	private final OutputStream output;
	private boolean isSpecial = false;
	
	//Response Fields
	private String statusLine;
	private int responseCode = 0;
	private String message;
	private String version;
	private byte [] contents;
	private String special;
	private HashMap <String, String> headers;
	private Socket clientSock;
	
	
	
	/** 
	 * Constructor
	 * Checks whether the request has the appropriate Method
	 * Checks whether the request is a bad request
	 * Sets appropriate response code and message
	 * @param HttpRequest to be handled
	 * @param Base Directory
	 * @param HttpRequest is a good request or not
	 * @param Output Stream
	 * @param Client Socket
	 **/
	public HttpResponse(HttpRequest request,String baseDir, Boolean isGoodRequest, OutputStream output, Socket clientSock){
		this.baseDir = baseDir; 
		this.request = request;
		this.output = output;
		this.clientSock = clientSock;
		
		if (!isGoodRequest) {
			version = "HTTP/" + request.getVersion();
			responseCode = 400;
			message = "Bad Request";
		} else if (!request.getMethod().equals("GET")
				&& !request.getMethod().equals("HEAD")) {
			version = "HTTP/" + request.getVersion();
			responseCode = 501;
			message = "Not Implemented";
		}
		
	}
	
	/** 
	 * Performs checks to see if requested resource is forbidden, not found or special URL
	 **/
	
	private void checkResource(){
		logger.info("Requested Uri: "+request.getUri());
		
		String requestedResource;
		
		//Check for absolute path
		try {
			URL resourceUrl = new URL(request.getUri());
			requestedResource = resourceUrl.getPath();
		} catch (MalformedURLException e) {
			logger.info("Passed URI is not an absolute path");
			requestedResource = request.getUri();
		}
		
		Path resourcePath = Paths.get(baseDir,requestedResource).normalize();
		logger.info("Requested resource: "+resourcePath);
		Path rootPath = Paths.get(baseDir);
		logger.info("Root path: "+rootPath);
		file = resourcePath.toFile();
		
		if(!resourcePath.startsWith(rootPath)){
			logger.info("403");
			responseCode = 403;
			message = "Forbidden";
		}else if(!file.exists()){
			logger.info("404");
			responseCode = 404;
			message = "Not Found";
		}else if(!Files.isReadable(resourcePath)){
			responseCode = 403;
			message = "Forbidden";
		}
		
		special = request.getUri();
		logger.info("special = "+special);
		if(special.equals("/control") || special.equals("/shutdown") || responseCode == 0){
			// Check if file or directory
			if (file.isFile()) {
				serveFile();
			} else if (file.isDirectory()) {
				serveDirectory();
			} else if (special.equals("/control")) {
				control();
				isSpecial = true;
			} else if (special.equals("/shutdown")) {
				shutdown();
				isSpecial = true;

			}
		}
	}
	
	/**
	 * Handling Special URL control
	 */
	private void control(){
		String name = "Name: Anwesha Das";
		String seasId = "SEAS ID: anwesha";
		StringBuilder body = new StringBuilder();
		body.append(name);
		body.append("<br>");
		body.append(seasId);
		body.append("<br><br><br><b> THREAD STATUS </b><UL>");
		body.append(HttpServer.threadStatus());
		body.append("</UL><br><br><br>");
		String shutdown = "http://localhost:"+HttpServer.getPort()+"/shutdown";
		String button = "<a href = \""+shutdown+"\"><button>Shut Down</button></a>";
		body.append(button);
		contents = util.createHTML("Control Panel", body.toString()).getBytes();
		
		headers.put("Content-Type: ","text/html \r\n");
		Integer length = contents.length;
		headers.put("Content-Length",length.toString()+"\r\n");
		responseCode = 200;
		message = "OK";
		
		try {
			sendResponse();
		} catch (IOException e) {
			logger.error("Could not send control page");
		}
	}
	
	/**
	 * Handling special URL shutdown
	 */
	private void shutdown(){
		logger.info("Hadling shutdown");
		responseCode = 200;
		message = "OK";
		try {
			sendStatus();
			output.write("\r\n".getBytes());
			output.write("Server Shut Down\r\n\r\n".getBytes());
			clientSock.close();
			logger.info("Closed client socket requesting shutdown");
		} catch (IOException e) {
			logger.error("Could not send status");
		}
		HttpServer.exitFlag = true;
		HttpServer.sendShutDownSignal();
	}
	
	/**
	 * For sending back the file
	 */
	private void serveFile(){
		//Check extension
		String type = "Not Supported";
		
		try {
			type = Files.probeContentType(file.toPath());
		} catch (IOException e1) {
			logger.error("Could not get content type");
		}
		
		//Check if resource type is supported
		if(type.equals("Not Supported")){
			responseCode = 501;
			message = "Not Implemented";
		}else{ 
			responseCode = 200;
			message = "OK";
			boolean check = checkLastModified();
			logger.info("Value returned by check: "+check);
			if(check){
				logger.info("return file");
				//Assign content-type and content-length
				headers.put("Content-Type", type+"\r\n");
				
				try {
					contents = util.readFile(file);
				} catch (IOException e) {
					logger.error("File not found");
				}
				
				Integer length = contents.length;
				headers.put("Content-Length",length.toString()+"\r\n");
			}
		}
	}
	
	/**
	 * For sending back directory listing
	 */
	
	private void serveDirectory(){
		responseCode = 200;
		message = "OK";
		boolean check = checkLastModified();
		if(check){
			//Dynamically construct HTML page with directory listing
			headers.put("Content-Type: ","text/html \r\n");
			StringBuilder listing = new StringBuilder();
			File [] listFiles = file.listFiles();
			for(File name : listFiles){
				String link = "<a href=\""+Paths.get(request.getUri(),name.toPath().getFileName().toString())+"\">"+name.toString()+"</a>";
				listing.append(link);
				listing.append("<br>");
			}
			
			contents = util.createHTML(file.toString()+" Listing", listing.toString()).getBytes();
			Integer length = contents.length;
			headers.put("Content-Length",length.toString()+"\r\n");
		}
	}
	
	/**
	 * 
	 * @return whether the file was modified since the given date
	 */
	private boolean checkLastModified(){
		//Check if file has been modified
		if(request.getMethod().equals("GET")){
			if(request.getHeaders().containsKey("if-modified-since")){
				String date = request.getHeaders().get("if-modified-since").split("\t")[0];
				long lastMod = file.lastModified();
				int ret = util.compareDates(date, lastMod);
				if(ret == 0){
					logger.info("Setting 304 code");
					responseCode = 304;
					message  = "Not Modified";
					headers.put("Date", util.getDate()+"\r\n");
					return false;
				}
			}
		}else if(request.getHeaders().containsKey("if-unmodified-since")){
			String date = request.getHeaders().get("if-unmodified-since").split("\t")[0];
			logger.info("Date passed: "+date);
			long lastMod = file.lastModified();
			int ret = util.compareDates(date, lastMod);
			if(ret == 1){
				logger.info("Setting 412 code");
				responseCode = 412;
				message = "Precondition Failed";
				if(headers.containsKey("Date"))
					headers.remove("Date");
					return false;
			}
		}
		
		return true;
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
	
	public void sendStatus() throws IOException{
		statusLine = version+" "+responseCode+" "+message+"\r\n";
		output.write(statusLine.getBytes());
	}
	
	/**
	 * Sends the body of HTTP Response in case of GET request
	 * @param OutputStream
	 * @throws IOException 
	 */
	private void sendBody() throws IOException{
		
		output.write(contents);
	}
	
	/**
	 * Sends the HTTP response back to the client
	 * @param Client Socket
	 * @throws IOException 
	 */
	public void sendResponse() throws IOException{
		sendStatus();
		for(String key: headers.keySet()){
			output.write((key+": "+headers.get(key)).getBytes());
		}
		output.write(("\r\n").getBytes());
		
		//Send actual content in case it is GET request
		if(request.getMethod().equals("GET")){
			sendBody();
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
	
	public boolean isSpecial(){
		return isSpecial;
	}
	
}
