package net.shad.rmqrx.utils;

import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;

/**
 * @author shad
 */
public class Jndis {
    public static Object jndiFor(String name) throws NamingException {
        JndiObjectFactoryBean fb = new JndiObjectFactoryBean();
        fb.setResourceRef(true);
        fb.setLookupOnStartup(true);
        fb.setJndiName(name);
        fb.afterPropertiesSet();
        return fb.getObject();
    }

    public static String jndiStrFor(String name) throws Exception{
        return (String) jndiFor(name);
    }
}
