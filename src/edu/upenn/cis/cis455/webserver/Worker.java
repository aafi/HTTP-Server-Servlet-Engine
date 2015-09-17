package edu.upenn.cis.cis455.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.*;

import org.apache.log4j.Logger;

public class Worker implements Runnable{
	//private Socket clientSock;
	static final Logger logger = Logger.getLogger(HttpServer.class);
	private final Queue<Socket> requestQueue;
	private final String baseDir;
	private BufferedReader input = null;
	
	public Worker(Queue<Socket> queue, String baseDir){
		this.requestQueue = queue;
		this.baseDir = baseDir;
	}
	
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
				Boolean isBadRequest = false; 
				HttpRequest request = new HttpRequest();
				try {
					request.parseRequest(input);
				} catch (IOException e) {
					logger.error("Could not parse request");
				}//catch (BadFormedException a) {
				//logger.error("badly formed request");
				//isBadRequest = true;
				//}
				
				//Process and send response
				HttpResponse response = new HttpResponse(request,baseDir,isBadRequest);
				
				if(response.getResponseCode() == 0){ 
					if(request.getVersion().equals("1.1")){
						//Send 100 continue response
						String reply = "HTTP/1.1 100 Continue";
						//sendResponse()
						response.processRequest11();
					
				
					
					}else{ 
						
						response.processRequest10();
					
					}
				}
				
				logger.info(response.getMessage());
				
				
			} //end of if(clientSock!=null)
		
		}
			  
	 }
		
	}
	


