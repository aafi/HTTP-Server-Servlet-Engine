package test;

import static org.junit.Assert.*;
import edu.upenn.cis.cis455.webserver.Session;

import org.junit.Before;
import org.junit.Test;

public class SessionTest {
	
	Session session;
	
	/**
	 * Sets up the tests by creating a session object
	 */
	@Before
	public void setUp(){
		session = new Session("FirstID");
	}
	
	/**
	 * Checks if the right session ID is returned
	 */
	
	@Test
	public void getIDTest(){
		assertEquals("FirstID",session.getId());
	}
	
	/**
	 * Checks if IllegalStateException is thrown when invalidate is called on already invalid session
	 */
	
	@Test(expected = IllegalStateException.class)  
	public void sendErrorTest() {  
		session.setM_valid(false);
		session.getAttribute("foo");
		session.getAttributeNames();
		session.invalidate();
	}  
	
	/**
	 * Checks if the current session is new
	 */
	@Test
	public void isNewTest(){
		assertEquals(true,session.isNew());
	}
	
	/**
	 * Checks if the current session gets invalidated correctly
	 */
	@Test
	public void invalidateTest(){
		session.setM_valid(true);
		session.invalidate();
		assertEquals(false,session.isValid());
	}
	

}
