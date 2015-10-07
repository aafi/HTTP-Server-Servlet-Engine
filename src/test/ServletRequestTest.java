package test;

import static org.junit.Assert.*;

import java.util.*;

import edu.upenn.cis.cis455.webserver.ServletRequest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class ServletRequestTest {
	
	@Rule
    public ExpectedException thrown= ExpectedException.none();
	
	ServletRequest request;
	
	/**
	 * Sets up the test by creating a new ServletRequest object
	 */
	
	@Before
	public void setUp(){
		request = new ServletRequest();
	}
	
	/**
	 * Checks if the correct Auth type is returned
	 */
	@Test
	public void getAuthTest(){
		assertEquals("BASIC",request.getAuthType());
	}
	
	/**
	 * Populates the headers maps with values and checks if the appropriate values are returned
	 */
	
	@Test
	public void getHeaderTest(){
		HashMap<String,String> headers = new HashMap<String,String>();
		headers.put("host", "localhost");
		headers.put("port", "8080");
		request.setRequestHeaders(headers);
		
		assertEquals("localhost",request.getHeader("host"));
		assertEquals(null,request.getHeader("location"));
		assertEquals(8080,request.getIntHeader("port"));
		
	}
	
	/**
	 * Checks if the right request method is returned
	 */
	
	@Test
	public void getMethodTest(){
		request.setM_method("GET");
		assertEquals("GET",request.getMethod());
	}
	
	/**
	 * Checks if the correct query string is returned from the URI if any
	 */
	@Test
	public void getQueryTest(){
		request.setUri("test/foo?param1=value1");
		assertEquals("param1=value1",request.getQueryString());
		request.setUri("foo/bar");
		assertEquals(null,request.getQueryString());
		
	}
	
	/**
	 * Checks if the right URI is returned
	 */
	
	@Test
	public void getUriTest(){
		request.setUri("/test/foo?param1=value1");
		assertEquals("/test/foo",request.getRequestURI());
		request.setUri("http://foo.bar/a.html?a1=b1&a2=b2");
		assertEquals("/a.html",request.getRequestURI());
		
	}
	
	/**
	 * Checks if getSession(false) doesn't create a session and returns null
	 */
	@Test
	public void getSessionTest(){
		assertEquals(null,request.getSession(false));
	}
	
}
