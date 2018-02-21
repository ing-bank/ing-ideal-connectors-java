package com.ing.ideal.connector.crypto;

import com.ing.ideal.connector.IdealException;

/**
 * Signs and verifies an XML document.
 * 
 * @author Codrin
 * 
 */
public interface IDigitalSignatureHelper {

	/**
	 * Returns the signed XML message.
	 * 
	 * @param xml xml message
	 * @return signed xml message
	 */
	String createDigitalSignedMessage(String xml) throws IdealException;

	/**
	 * Verifies if the XML is correctly signed. <br />
	 * The verification mechanism is implemented according to create signed XLM
	 * message method.
	 * 
	 * @param xml signed xml message
	 * @return true if signature is valid
	 */
	boolean verifyDigitalSignedMessage(String xml) throws IdealException;

}
