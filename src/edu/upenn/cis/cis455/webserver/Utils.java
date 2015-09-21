package edu.upenn.cis.cis455.webserver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

public class Utils {
	
	static final Logger logger = Logger.getLogger(HttpServer.class);
	
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
	
	public Integer compareDates(String date, long lastMod){
		ArrayList <SimpleDateFormat> possibleFormats = new ArrayList<SimpleDateFormat>();
		possibleFormats.add(new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z",Locale.ENGLISH));
		possibleFormats.add(new SimpleDateFormat("EEEEEE, dd-MMM-yy HH:mm:ss z",Locale.ENGLISH));
		possibleFormats.add(new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy",Locale.ENGLISH));
		Date header = null;
		SimpleDateFormat dateFormat = null;
		
		for(SimpleDateFormat sdf : possibleFormats){
			dateFormat = sdf;
			sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
	
			try {
				header = sdf.parse(date);
			} catch (ParseException e) {
				logger.error("Could not parse date");
			}

			if(header!=null){
				break;
			}
		}
		
		logger.info("Got date");
		Date last = new Date(lastMod);
		dateFormat.format(last);
		logger.info("Date passed is: "+header);
		logger.info("File was last modified: "+last);
		
		Date currentDate = null;
		try {
			currentDate = dateFormat.parse(dateFormat.format(new Date()));
		} catch (ParseException e) {
			logger.error("Could not parse current date");
		}
		
		logger.info("Current date is :"+currentDate);
		if(header == null || header.after(currentDate)){
			logger.info("passed date is null/date is in the future");
			return -1;
		}else if(last.after(header)){
			logger.info("File has been modified since date passed");
			return 1;
		}else{
			logger.info("File has not been modified since date passed");
			return 0;
		}
		
		
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
	
	/**
	 * Gets the contents of the file into a byte array
	 * @param file
	 * @return byte array of file contents
	 * @throws IOException
	 */
	public byte [] readFile(File file) throws IOException{
		FileInputStream fis = new FileInputStream(file);
		byte [] contents = new byte[(int)file.length()];
		fis.read(contents);
		fis.close();
		return contents;
		
	}
	
	/**
	 * Create HTML page
	 * @param title
	 * @param body
	 * @return byte array of HTML page
	 */
	public byte [] createHTML(String title, String body){
		String start =
				"<html>" +
				"<title>"+title+"</title>" +
				"<meta charset=\"utf-8\">"+
				"<body>";

		String end =
				"</body>" +
				"</html>";
		
		StringBuilder page = new StringBuilder();
		page.append(start);
		page.append(body);
		page.append(end);
		
		return page.toString().getBytes();
		
		
	}
}
