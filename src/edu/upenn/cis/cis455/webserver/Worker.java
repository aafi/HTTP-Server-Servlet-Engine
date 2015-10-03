package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

public class Worker implements Runnable{
	/**
	 * Private fields of the class
	 */
	static final Logger logger = Logger.getLogger(HttpServer.class);
	private final RequestBlockingQueue requestQueue;
	private final String baseDir;
	private BufferedReader input = null;
	private String currentUrl = null;
	public static boolean shutdownFlag = false;
	
	/**
	 * Constructor
	 * @param Request queue
	 * @param Base Directory
	 */
	public Worker(RequestBlockingQueue queue, String baseDir){
		this.requestQueue = queue;
		this.baseDir = baseDir;
	}
	
	
	/**
	 * Run method for threads
	 */
	public void run(){
		while(!shutdownFlag){
			Socket clientSock = null;
			
			//Wait until there is a request in the Queue
			synchronized(requestQueue){
				if(!requestQueue.isEmpty() && !requestQueue.isShutdown()){
					logger.info("Serving request");
					clientSock = requestQueue.remove();
				}else if(requestQueue.isEmpty()){
					logger.info("Queue is currently empty. Thread is waiting");
					try {
						requestQueue.wait();
					} catch (InterruptedException e) {
						logger.error("Interrupted exception");
					}
				}else{
					if(requestQueue.isShutdown()){
						logger.info("ShutDown has been signalled");
						break;
					}
				}
			}
			
			//Check to see if a socket was actually removed from the queue
			if(clientSock!=null){
				//Setup input stream from client
				try {
					input = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				} catch (IOException e) {
					logger.error("Could not read input");
				}
				
				//Create output stream for the client
				OutputStream output = null;
				try {
					output = clientSock.getOutputStream();
				} catch (IOException e1) {
					logger.error("Could not get output stream");
				}
				
				//Parse request
				HttpRequest request = new HttpRequest();
				Boolean isGoodRequest = request.parseRequest(input, output);
				
				String requestedResource;
				
				//Check for absolute path
				try {
					URL resourceUrl = new URL(request.getUri());
					requestedResource = resourceUrl.getPath();
				} catch (MalformedURLException e) {
					logger.info("Passed URI is not an absolute path");
					requestedResource = request.getUri();
				}
				
				/** CHECK URL PATTERN WITH REQUEST PATH ***/
				boolean isServletRequest = false;
				String url_match = null;
				int max_len = 0;
				
				for(String url : ParseWebXml.urls.keySet()){
					logger.info(url);
					String temp_url = null;
					if(url.endsWith("/*")){
						temp_url = url;
						url = url.replace("/*", "");
					}
					
					logger.info("RR: "+requestedResource);
					if(!requestedResource.startsWith("/")){
						requestedResource = "/"+requestedResource;
					}
					if(requestedResource.startsWith(url)){
						if(url.length() > max_len){
							isServletRequest = true;
							url_match = temp_url;
							max_len = url.length();
						}
					}
				}
				
				
				logger.info(isServletRequest);
				//Check if servlet request
				if(isServletRequest){
					HttpServlet servlet;
					servlet = ParseWebXml.servlets.get(ParseWebXml.urls.get(url_match));
					
					ServletRequest servletRequest = new ServletRequest(request,clientSock, url_match);
					ServletResponse servletResponse = new ServletResponse(clientSock);
					try {
						servlet.service(servletRequest, servletResponse);
					} catch (ServletException | IOException e) {
						logger.error("could not service");
					}
					
				}else{ //Normal Static Request
					HttpResponse response = new HttpResponse(request,baseDir,isGoodRequest,output,clientSock);
					if(isGoodRequest)
						currentUrl = request.getUri();
					else
						currentUrl = "Bad Request";
					
					// Send 100 Continue Response
					if (isGoodRequest && request.getVersion().equals("1.1")) {
						// Send 100 continue response
						if (request.getHeaders().containsKey("expect")) {
							String reply = "HTTP/1.1 100 Continue \r\n";

							try {
								output.write(reply.getBytes());
								output.write("\r\n".getBytes());
							} catch (IOException e) {
								logger.error("Could not send 100 Continue response");
							}

						}
					}

					// Process and send response
					if (response.getResponseCode() == 0) {

						// Process the requests
						if (request.getVersion().equals("1.1")) {
							response.processRequest11();
						} else {
							response.processRequest10();
						}
						
						if(!response.isSpecial()){
							// Send error responses
							if (response.getResponseCode() != 200) { //In case of 404 NOT FOUND
								try {
									// Send the status
									response.sendStatus();
									output.write("\r\n".getBytes());
								} catch (IOException e) {
									logger.error("Could not send status");
								}

							} else { // send response in case file can be accessed
								try {
									response.sendResponse();
								} catch (IOException e) {
									logger.error("Could not send response");
								}
							}
						} //end of if(!isSpecial())
						
					} else {
						// Send status
						try {
							response.sendStatus();
							output.write("\r\n".getBytes());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Could not send status");
						}
						
					}
					
					try {
						clientSock.close();
						output.close();
					} catch (IOException e) {
						logger.error("Could not close client socket");
					}
				} // End of processing normal static request
			} //end of if(clientSock!=null)
		} // end of while
	 } // end of run
	
	/**
	 * @return Current URl the thread is working on
	 */
	public String currentUrl(){
		return currentUrl;
	}
}
	


