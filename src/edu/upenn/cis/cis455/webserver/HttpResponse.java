package edu.upenn.cis.cis455.webserver;

public class HttpResponse {
	
	private final String baseDir;
	private final HttpRequest request;
	private int responseCode;
	private String message;
	private String version;
		
	public HttpResponse(HttpRequest request,String baseDir){
		this.baseDir = baseDir; 
		this.request = request;
	}
	
	public void processRequest(){
		//Check HTTP/1.1 request compliance
		if(request.getVersion().equals("1.1")){
			version = "HTTP/1.1";
			if(!request.getHeaders().containsKey("host")){
				responseCode = 400;
				message = "Bad Request";
			}
			else if(!request.getMethod().equals("GET") || !request.getMethod().equals("HEAD")){
				responseCode = 501;
				message = "Not Implemented";
			}
		}
		
		System.out.println(version+" "+responseCode+" "+message);
		System.out.println(request.getHeaders().get("content-type"));
		
	}
	
}
