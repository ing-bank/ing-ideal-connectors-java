package com.ing.ideal.connector.serializer;

/**
 * States the contract of serializing objects to or from string.
 * 
 * @author Codrin
 * 
 */
public interface ISerializer {

	Object deserializeObject(Class clazz, String objectAsString)
			throws Exception;

	String serializeObject(Class clazz, Object object) throws Exception;
}
