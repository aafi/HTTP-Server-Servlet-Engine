package test;

import static org.junit.Assert.*;

import java.util.*;
import edu.upenn.cis.cis455.webserver.Context;
import org.junit.Before;
import org.junit.Test;

public class ContextTest {
	
	Context context;
	
	/**
	 * Creates a new context object
	 * Creates the attribute and parameter hashmaps and populates them with initial values
	 */
	@Before
	public void setUp(){
		context = new Context();
		HashMap<String,Object> attributes = new HashMap<String,Object>();
		HashMap<String,String> initParams = new HashMap<String,String>();
		attributes.put("foo", "bar");
		initParams.put("foo", "bar");
		context.setAttributes(attributes);
		context.setInitParams(initParams);
	}
	
	/**
	 * Checks if the correct attribute value is returned
	 */

	@Test
	public void getAttributeTest(){
		assertEquals("bar",context.getAttribute("foo"));
	}
	
	/**
	 * Checks if the right context is returned
	 */
	
	@Test
	public void getContextTest(){
		assertEquals(context,context.getContext("foo"));
	}
}
