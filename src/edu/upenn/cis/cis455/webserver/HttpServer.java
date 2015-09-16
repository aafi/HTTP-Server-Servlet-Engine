package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import org.apache.log4j.Logger;

class HttpServer {
	
  static final Logger logger = Logger.getLogger(HttpServer.class);
  private static final int QUEUE_SIZE = 100;
  private static final int MAX_THREADS = 10;
  
  public static void main(String args[]) throws IOException
  {
	  // Get the input arguments
	  int port = Integer.parseInt(args[0]);
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
	  Queue <Socket> queue = new LinkedList <Socket>();
	  
	  //Create a threadpool
	  ArrayList<Thread> threadPool = new ArrayList<Thread>();
	  
	  for(int i=0;i<MAX_THREADS;i++){
		  Worker worker = new Worker(queue, baseDir);
		  Thread new_worker = new Thread(worker);
		  new_worker.start();
		  threadPool.add(new_worker);
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
		  
	  }
	  
  }
  
}
