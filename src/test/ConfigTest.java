package test;

import static org.junit.Assert.*;
import edu.upenn.cis.cis455.webserver.Config;

import org.junit.Before;
import org.junit.Test;

public class ConfigTest {
	
	Config config;
	
	/**
	 * Sets up by creating a new config object
	 */
	
	@Before
	public void setUp(){
		config = new Config("New Config",null);
	}
	
	/**
	 * Checks if parameters are set properly 
	 */
	
	@Test
	public void initParamTest(){
		config.setInitParam("foo", "bar");
		assertEquals("bar",config.getInitParameter("foo"));
	}
	
	/**
	 * Checks if the config returns the right servlet name 
	 */
	
	@Test
	public void getNameTest(){
		assertEquals("New Config",config.getServletName());
	}
}
