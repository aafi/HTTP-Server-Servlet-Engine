package edu.upenn.cis.cis455.webserver;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

public class ValidSession implements Runnable{
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	public static HashMap<String,Session> session_mappings;
	private static long millis = 6000;
	
	/**
	 * Contructor 
	 */
	public ValidSession(){
		//Initialize session mappings
		  session_mappings = new HashMap<String,Session>();
	}
	
	/**
	 * Keeps checking if the sessions have expired 
	 */
	
	public void run(){
		while(true){
			synchronized(session_mappings){
				Iterator iterator = this.session_mappings.entrySet().iterator();
				while(iterator.hasNext()){
					Map.Entry<String, Session> next = (Map.Entry<String, Session>) iterator.next();
					long current = System.currentTimeMillis();
					if((current - next.getValue().getLastAccessedTime()) > next.getValue().getMaxInactiveInterval() * 1000){
						logger.info("Session has expired: "+next.getValue().getId());
						next.getValue().invalidate();
						iterator.remove();
						logger.info(session_mappings.containsKey(next.getKey()));
					}
				}
			} // End of synchronized block
			
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
				logger.info("Thread interrupt received");
				break;
			}
		}
	}

}
