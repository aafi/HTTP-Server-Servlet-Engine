package edu.upenn.cis.cis455.webserver;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

/**
 * @author Todd J. Green
 */
public class Session implements HttpSession {

	private long creationTime;
	private String id;
	private long lastAccessedTime;
	private int interval=-1;
	private Properties m_props = new Properties();
	private boolean m_valid = true;
	
	
	public Session(String id){
		creationTime = System.currentTimeMillis();
		this.id = id;
		updateAccessedTime();
	}
	
	public void updateAccessedTime(){
		lastAccessedTime = System.currentTimeMillis();
	}
	
	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getCreationTime()
	 */
	public long getCreationTime() {
		return this.creationTime;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getId()
	 */
	public String getId() {
		return this.id;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getLastAccessedTime()
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getServletContext()
	 */
	public ServletContext getServletContext() {
		return ParseWebXml.context;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setMaxInactiveInterval(int)
	 */
	public void setMaxInactiveInterval(int arg0) {
		this.interval = arg0;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getMaxInactiveInterval()
	 */ 
	public int getMaxInactiveInterval() {
		return this.interval;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getSessionContext()
	 */
	public HttpSessionContext getSessionContext() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttribute(java.lang.String)
	 */
	public Object getAttribute(String arg0) {
		if(!this.m_valid)
			throw new IllegalStateException();
		
		if(m_props.containsKey(arg0))
			return m_props.get(arg0);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValue(java.lang.String)
	 */
	public Object getValue(String arg0) {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getAttributeNames()
	 */
	public Enumeration getAttributeNames() {
		if(!this.m_valid)
			throw new IllegalStateException();
		
		return m_props.keys();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#getValueNames()
	 */
	public String[] getValueNames() {
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#setAttribute(java.lang.String, java.lang.Object)
	 */
	public void setAttribute(String arg0, Object arg1) {
		if(!this.m_valid)
			throw new IllegalStateException();
		
		if(arg1 == null)
			this.removeAttribute(arg0);
		else
			m_props.put(arg0, arg1);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#putValue(java.lang.String, java.lang.Object)
	 */
	public void putValue(String arg0, Object arg1) {
		return;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeAttribute(java.lang.String)
	 */
	public void removeAttribute(String arg0) {
		if(!this.m_valid)
			throw new IllegalStateException();
		
		if(m_props.containsKey(arg0))
			m_props.remove(arg0);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#removeValue(java.lang.String)
	 */
	public void removeValue(String arg0) {
		return;
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#invalidate()
	 */
	public void invalidate() {
		if(!this.m_valid)
			throw new IllegalStateException();
		
		m_valid = false;
		this.m_props.clear();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpSession#isNew()
	 */
	public boolean isNew() {
		if(this.lastAccessedTime > this.creationTime)
			return false;
		
		return true;
	}

	public boolean isValid() {
		return m_valid;
	}


	public void setM_valid(boolean m_valid) {
		this.m_valid = m_valid;
	}
	
	
}
