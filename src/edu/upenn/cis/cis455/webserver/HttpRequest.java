package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.util.HashMap;

public class HttpRequest {
	
	private String method;
	private String uri;
	private String version;
	private HashMap <String, String> headers;
	
	public void parseRequest(BufferedReader request) throws IOException{
		
		headers = new HashMap<String, String>();
		String line, prevHeader = null;
		int lineNumber = 1;
		
		while(!(line = request.readLine()).trim().equals("")){
			//Get request line
			if(lineNumber == 1){
				String [] parts = line.split(" ");
				method = parts[0];
				uri = parts[1];
				version = parts[2].split("/")[1];
				lineNumber++;
			}else{ //Get request headers
				if(line.contains(": ")){ //The line contains a header
					String [] parts = line.split(": ");
					headers.put(parts[0].toLowerCase(), parts[1].trim());
					lineNumber++;
					prevHeader = parts[0].toLowerCase();
				}else{ //In case a line is continuation of previous header
					String value = headers.get(prevHeader);
					String newValue = value+" "+line.trim();
					headers.put(prevHeader, newValue);			
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
