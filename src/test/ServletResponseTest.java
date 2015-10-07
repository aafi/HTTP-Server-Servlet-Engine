package test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.*;

import edu.upenn.cis.cis455.webserver.ServletResponse;

import javax.servlet.http.Cookie;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class ServletResponseTest {

	ServletResponse response;
	
	/**
	 * Sets up the tests by creating a ServletResponse object
	 */
	@Before
	public void setUp(){
		response = new ServletResponse();
	}
	
	/**
	 * Checks if cookies are added properly
	 */
	
	@Test
	public void addCookieTest(){
		Cookie cookie = new Cookie("Cookie","Test");
		response.addCookie(cookie);
		assertEquals(true,response.containsHeader("Set-Cookie"));
	}
	
	/**
	 * Checks if the date header is added 
	 */
	@Test
	public void addDateHeaderTest(){
		response.addDateHeader("Date", 789456);
		assertEquals(true,response.containsHeader("Date"));
	}
	
	/**
	 * Checks if the passed header is added
	 */
	@Test
	public void setHeaderTest(){
		response.setHeader("foo", "bar");
		assertEquals(true,response.containsHeader("foo"));
	}
	
	/**
	 * Checks if the int header is added properly
	 */
	@Test
	public void addIntHeader(){
		HashMap <String, String> response_headers = new HashMap<String,String>();
		response_headers.put("foo", "1");
		response.setResponse_headers(response_headers);
		response.addIntHeader("foo", 2);
		
		assertEquals("1,2",response.getResponse_headers().get("foo"));
	}
	
	/**
	 * Checks if the correct character encoding is returned
	 */
	@Test
	public void encodingTest(){
		assertEquals("ISO-8859-1",response.getCharacterEncoding());
		response.setCharacterEncoding("UTF-8");
		assertEquals("UTF-8",response.getCharacterEncoding());
	}
	
	/**
	 * Checks if the correct content type is returned
	 */
	
	@Test
	public void contentTypeTest(){
		response.setContentType("text/html;charset=UTF-8");
		assertEquals("UTF-8",response.getCharacterEncoding());
		assertEquals("text/html;charset=UTF-8",response.getContentType());
	}
	
	/**
	 * Checks if the buffer size is set properly
	 */
	@Test
	public void bufferSizeTest(){
		response.setBufferSize(50);
		assertEquals(50,response.getBufferSize());
	}
	
	/**
	 * Checks if IllegalStateException is thrown if resetBuffer is called after response is committed
	 */
	@Test(expected = IllegalStateException.class)  
	public void resetTest() {  
		response.setCommitted(true);
		response.resetBuffer();
	}  
	
	/**
	 * Checks if IllegalStateException is thrown if sendError is called after response is committed
	 */
	@Test(expected = IllegalStateException.class)  
	public void sendErrorTest() {  
		response.setCommitted(true);
		try {
			response.sendError(404,"Not Found");
		} catch (IOException e) {
			
		}
	}  
	
}
