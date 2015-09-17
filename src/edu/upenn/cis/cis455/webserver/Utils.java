package edu.upenn.cis.cis455.webserver;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.*;

public class Utils {

	/** 
	 * Gets the current date and time 
	 **/
	public String getDate(){
		String currentDate = null;
		SimpleDateFormat dateFormatGMT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
		dateFormatGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		currentDate = dateFormatGMT.format(new Date());
		
		return currentDate;
	}
	
	/**
	 * Returns the MIME type based on file extension
	 * @param extension
	 * @return MIME type
	 */
	public String getType(String extension){
		
		String type;
		if(extension.equals("jpg")){
			type = "image/jpeg";
		}else if(extension.equals("png") || extension.equals("gif")){
			type = "image/"+extension;
		}else if(extension.equals("html")){
			type = "text/"+extension;
		}else if(extension.equals("txt")){
			type = "text/plain";
		}else{
			type = "Not Supported";
		}
		
		return type;
	}
	
	
	public byte [] readResource(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		byte [] contents = new byte[(int)file.length()];
		fis.read(contents);
		
		return contents;
		
	}
	
}
