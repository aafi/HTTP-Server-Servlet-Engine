package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import org.apache.log4j.Logger;

class HttpServer {
	
  static final Logger logger = Logger.getLogger(HttpServer.class);
  private static final int MAX_THREADS = 10;
  private static int port;
  private static ArrayList<ThreadpoolThread> threadPool;
  private static BlockingQueue queue;
  
  public static void main(String args[]) throws IOException
  {
	  if(args.length==0){
		  logger.info("Name: Anwesha Das");
		  logger.info("SEAS login: anwesha");
	  }else{
		  // Get the input arguments
		  port = Integer.parseInt(args[0]);
		  String baseDir = args[1];
		  
		  //Server code
		  ServerSocket serverSock = null;
		  try{
			  serverSock = new ServerSocket(port);
		  } catch(IOException e){
			  logger.error("Could not listen on port: "+port);
	          System.exit(1);
		  }
		  
		  //Queue for incoming requests
		  queue = new BlockingQueue();
		  
		  //Create a thread pool
		  threadPool = new ArrayList<ThreadpoolThread>();
		  
		  for(int i=0;i<MAX_THREADS;i++){
			  Worker worker = new Worker(queue, baseDir);
			  ThreadpoolThread thread = new ThreadpoolThread(worker);
			  threadPool.add(thread);
			  
		  }
		  
		  while(true){
			  
			  Socket clientSock = null;
			  		  
			  //Accept client connection
			  try{
				  clientSock = serverSock.accept();
				  logger.info("Connection accepted");
			  }catch(IOException e){
				  logger.error("Could not accept client socket");
				  System.exit(1);
			  }
			  
			  //Add request to the queue and notify all waiting threads
			  synchronized(queue){
				  queue.add(clientSock);
				  queue.notifyAll();
			  }
			  
			  logger.info("Thread reaches here");
		  }
		  
	  }
	  
  } //end of main
  
  public String threadStatus(){
	  StringBuilder status = new StringBuilder();
	  for(ThreadpoolThread t : threadPool){
		 String state = t.getThread().getState().toString();
		 logger.info("Thread state: "+state.toString());
		 if(state.equals("WAITING")){
			 status.append("<LI>"+t.getThread().getName()+" ----- Waiting");
			 status.append("<br>");
		 }else{
			 status.append("<LI>"+t.getThread().getName()+" ----- "+ t.getWorker().currentUrl());
			 status.append("<br>");
		 }
	  }
	 return status.toString(); 
  }
  
  public int getPort(){
	  return port;
  }
  
  //Set ShutDown Signal
  public void sendShutDownSignal(){
	  queue.setShutdown(true);
  }
  
}
