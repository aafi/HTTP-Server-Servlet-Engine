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
				
				HttpRequest request = new HttpRequest();
				try {
					request.parseRequest(input);
				} catch (IOException e) {
					logger.error("Could not parse request");
				}
				
				HttpResponse response = new HttpResponse(request,baseDir);
				response.processRequest();
				
				
				
			}
		
		}
			  
	 }
		
	}
	


