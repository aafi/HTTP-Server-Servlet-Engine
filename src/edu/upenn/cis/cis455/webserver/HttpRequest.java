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
	
	public boolean parseRequest(BufferedReader request) throws IOException{
		
		headers = new HashMap<String, String>();
		String line, prevHeader = null;
		int lineNumber = 1;
		
		while((line = request.readLine())!=null && !line.equals("")){
			//Get request line
			if(lineNumber == 1){
				String [] parts = line.split("[ \t]+");
				
				if(parts.length !=3){
					return false;
				}
				
				method = parts[0];
				uri = parts[1];
				
				if(!parts[2].split("/")[0].equals("HTTP")){
					return false;
				}
				version = parts[2].split("/")[1];
				
				if(!version.equals("1.1") && !version.equals("1.0")){
					return false; 
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
					return false;
				}
			}
		}	
		return true;
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
