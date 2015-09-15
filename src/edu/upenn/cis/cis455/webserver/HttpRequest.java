package edu.upenn.cis.cis455.webserver;

import java.io.*;
import java.util.HashMap;

public class HttpRequest {
	
	private String method;
	private String uri;
	private String version;
	private HashMap <String, String> headers;
	
	public void parseRequest(BufferedReader request) throws IOException{
		String line;
		while((line = request.readLine())!=""){
			//Get request line
			if(line.contains("GET") || line.contains("HEAD")){
				String [] parts = line.split(" ");
				method = parts[0];
				uri = parts[1];
				version = parts[2].split("/")[1];
			}else{ //Get request headers
				String [] parts = line.split(":");
				headers.put(parts[0], parts[1]);
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
