package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.apache.log4j.Logger;

public class Worker implements Runnable{
	/**
	 * Private fields of the class
	 */
	static final Logger logger = Logger.getLogger(HttpServer.class);
	private final Queue<Socket> requestQueue;
	private final String baseDir;
	private BufferedReader input = null;
	
	/**
	 * Constructor
	 * @param Request queue
	 * @param Base Directory
	 */
	public Worker(Queue<Socket> queue, String baseDir){
		this.requestQueue = queue;
		this.baseDir = baseDir;
	}
	
	/**
	 * Run method for threads
	 */
	public void run(){
		while(true){
			Socket clientSock = null;
			synchronized(requestQueue){
				if(!requestQueue.isEmpty()){
					clientSock = requestQueue.remove();
				}else{
					logger.info("Queue is currently empty ");
					try {
						requestQueue.wait();
					} catch (InterruptedException e) {
						logger.error("Interrupted exception");
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
				
				
				//Parse request
				Boolean isGoodRequest = true; 
				HttpRequest request = new HttpRequest();
				try {
					isGoodRequest = request.parseRequest(input);
				} catch (IOException e) {
					logger.error("Could not parse request");
				}
				//Process and send response
				HttpResponse response = new HttpResponse(request,baseDir,isGoodRequest);
				OutputStream output = null;
				try {
					output = clientSock.getOutputStream();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					logger.error("Could not get output stream");
				}
				
				if(response.getResponseCode() == 0){ 
					
					//Process the requests
					if(request.getVersion().equals("1.1")){
						//Send 100 continue response
						String reply = "HTTP/1.1 100 Continue";
						//sendResponse()
						response.processRequest11();
					}else{ 
						response.processRequest10();
					}
					
					//Send error responses
					if(response.getResponseCode()!=200){
						try {
							//Send the status
							response.sendStatus(output);
							output.write("\r\n".getBytes());
							
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Could not send status");
						}
						
					}else{ //send response in case file can be accessed
						try {
							response.sendResponse(output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							logger.error("Could not send response");
						}
					}
				}else{
					//Send status
					try {
						response.sendStatus(output);
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
				
			} //end of if(clientSock!=null)
		
		}
			  
	 }
	
		
}
	


