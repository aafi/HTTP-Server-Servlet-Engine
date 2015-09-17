package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class HttpRequest {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);

	private String method;
	private String uri;
	private String version;
	private HashMap <String, String> headers;
	
	public void parseRequest(BufferedReader request) throws IOException{
		
		headers = new HashMap<String, String>();
		String line, prevHeader = null;
		int lineNumber = 1;
		
		while((line = request.readLine())!=null && !line.equals("")){
			//Get request line
			if(lineNumber == 1){
				String [] parts = line.split("[ \t]+");
				
				//TODO check for well formed request
				if(parts.length !=3){
					//throw bad format exception
				}
				
				method = parts[0];
				uri = parts[1];
				
				if(!parts[2].split("/")[0].equals("HTTP")){
					//throw bad format exception
				}
				version = parts[2].split("/")[1];
				
				if(!version.equals("1.1") && !version.equals("1.0")){
					//throw bad format exception
				}
				
				lineNumber++;
			}else{ //Get request headers
				if(line.contains(":\t") || line.contains(": ")){ //The line contains a header
					String [] parts = line.split(":[ \t]");
					headers.put(parts[0].toLowerCase(), parts[1].trim());
					lineNumber++;
					prevHeader = parts[0].toLowerCase();
				}else if(line.startsWith(" ") || line.startsWith("\t")){ //In case a line is continuation of previous header
					String value = headers.get(prevHeader);
					String newValue = value+" "+line.trim();
					headers.put(prevHeader, newValue);
				}else{
					//Throw bad format exception
				}
			}
		}	
	}

	public String getMethod() {
		return method;
	}

	public String getUri() {
		return uri;
	}

	public String getVersion() {
		return version;
	}

	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	
	
	

}
