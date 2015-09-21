package edu.upenn.cis.cis455.webserver;

import java.net.Socket;
import java.util.LinkedList;

public class RequestBlockingQueue {
	private static boolean shutdown;
	private LinkedList <Socket> list;
	private static final int QUEUE_SIZE = 100000;
	
	public RequestBlockingQueue(){
		list = new LinkedList<Socket>();
		shutdown = false;
	}
	
	public void add(Socket socket){
		list.add(socket);
	}
	
	public Socket remove(){
		return list.remove();
	}
	
	public boolean isEmpty(){
		return list.isEmpty();
	}
	
	public boolean isShutdown(){
		return shutdown;
	}
	
	public void setShutdown(boolean value){
		shutdown = value;
	}
}
