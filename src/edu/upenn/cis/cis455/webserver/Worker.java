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
	
	public Worker(Queue queue, String baseDir){
		this.requestQueue = queue;
		this.baseDir = baseDir;
	}
	
	public void run(){
		while(true){
			if(requestQueue.isEmpty()){
				synchronized (requestQueue) {
					logger.info("Queue is currently empty ");
					try {
						requestQueue.wait();
					} catch (InterruptedException e) {
						logger.error("Interrupted exception");
					}
				}
			}else{
				Socket clientSock = requestQueue.remove();
				//Setup input stream from client
				try {
					input = new BufferedReader(new InputStreamReader(clientSock.getInputStream()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				HttpRequest request = new HttpRequest();
				try {
					request.parseRequest(input);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
			  
	 }
		
	}
	


