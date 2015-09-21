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
  private static RequestBlockingQueue queue;
  private static ServerSocket serverSock;
  public static boolean exitFlag = false;
  
  public static void main(String args[])
  {
	  if(args.length==0){
		  logger.info("Name: Anwesha Das");
		  logger.info("SEAS login: anwesha");
	  }else if(args.length == 2){
		  // Get the input arguments
		  
		  try{
			  port = Integer.parseInt(args[0]);
		  }catch(NumberFormatException e){
			  logger.info("Invalid Port Number. Could not start Server");
			  System.exit(1);
		  }
		  
		  String baseDir = args[1];
		  File rootDir = new File(baseDir);
		  if(!rootDir.exists() && !rootDir.isDirectory()){
			  logger.info("Not valid root directory. Could not start Server");
			  System.exit(1);
		  }
		  
		  serverSock = null;
		  //Server code
		  try{
			  serverSock = new ServerSocket(port);
		  } catch(IOException e){
			  logger.error("Could not listen on port: "+port);
	          System.exit(1);
		  }
		  
		  //Queue for incoming requests
		  queue = new RequestBlockingQueue();
		  
		  //Create a thread pool
		  threadPool = new ArrayList<ThreadpoolThread>();
		  
		  for(int i=0;i<MAX_THREADS;i++){
			  Worker worker = new Worker(queue, baseDir);
			  ThreadpoolThread thread = new ThreadpoolThread(worker);
			  threadPool.add(thread);
			  
		  }
		  
		  while(!exitFlag){
			  
			  Socket clientSock = null;
			  		  
			  //Accept client connection
			  try{
				  clientSock = serverSock.accept();
				  logger.info("Connection accepted");
			  }catch(IOException e){
				  logger.error("Could not accept client socket");
				  break;
			  }
			  
			  //Add request to the queue and notify all waiting threads
			  synchronized(queue){
				  queue.add(clientSock);
				  queue.notifyAll();
			  }
			  
		  }
		  
		  for(ThreadpoolThread t : threadPool){
				logger.info(t.getThread().getName()+" is serving "+t.getWorker().currentUrl());
				try{
					t.getThread().join();
					logger.info(t.getThread().getName()+"exited");
				} catch (InterruptedException e) {
					logger.error("Thread could not join");
				}
			}
		  
		 }else{
			 logger.info("Could not start server. Invalid number of arguments");
		 }
	  
  } //end of main
  
  /**
   * Checks the status/URL of every thread
   */
  public static String threadStatus(){
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
  
  /**
   * Returns the port
   */
  public static int getPort(){
	  return port;
  }
  
  /**
   * Sets the shut down signal
   */
  public static void sendShutDownSignal(){
	  try {
			serverSock.close();
		}catch (IOException e) {
			logger.info("Could not close server");
	  }
	  
	  logger.info("Closed server socket");
	  queue.setShutdown(true);
	  logger.info("Sent shutdown signal");
	  shutDown();
  }
  
  /**
   * Implements server shut down
   */
private static void shutDown(){
	  //Set shutdown flag in worker
	  Worker.shutdownFlag = true;
	  logger.info("Set worker shutdown flag to true");
	  
	  synchronized(queue){
		queue.notifyAll();
	  }
	  logger.info("Notified all waiting threads");
	  
  }
  
}
