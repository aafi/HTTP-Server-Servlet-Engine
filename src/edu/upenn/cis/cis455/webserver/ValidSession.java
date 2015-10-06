package edu.upenn.cis.cis455.webserver;

import java.util.HashMap;

import org.apache.log4j.Logger;

public class ValidSession implements Runnable{
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	public static HashMap<String,Session> session_mappings;
	private static long millis = 6000;
	
	public ValidSession(){
		//Initialize session mappings
		  session_mappings = new HashMap<String,Session>();
	}
	
	public void run(){
		while(true){
			synchronized(session_mappings){
				for(String id : this.session_mappings.keySet()){
					Session session = this.session_mappings.get(id);
					long current = System.currentTimeMillis();
					if((current - session.getLastAccessedTime()) > session.getMaxInactiveInterval()*1000){
						logger.info("Session has expired");
						session_mappings.remove(id);
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
