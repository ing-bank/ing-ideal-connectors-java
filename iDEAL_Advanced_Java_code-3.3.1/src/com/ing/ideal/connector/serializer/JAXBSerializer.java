package com.ing.ideal.connector.serializer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Serializes and deserializes objects back and into XML string messages. The
 * class submitted as input must be annotated with the JAXB annotations,
 * otherwise an exception will occur on (de)serialization process.
 *
 * @author Codrin
 */
public class JAXBSerializer implements ISerializer {
    public static final String INTERFACE_CHARSET_ENCODING = "UTF-8";

    /**
     * XML message ==> JAVA object.
     */
    @Override
    public Object deserializeObject(Class clazz, String objectAsString)
            throws JAXBException {

        // get instance of JAXBContext based on root class
        JAXBContext context;

        context = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = context.createUnmarshaller();

        StringReader sr = new StringReader(objectAsString);
        return unmarshaller.unmarshal(sr);
    }

    /**
     * JAVA object ==> XML message
     */
    @Override
    public String serializeObject(Class clazz, Object object)
            throws JAXBException {

        // get instance of JAXBContext based on root class
        JAXBContext context = null;
        StringWriter sw = null;
        context = JAXBContext.newInstance(clazz);

        sw = new StringWriter();

        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, INTERFACE_CHARSET_ENCODING);
        marshaller.marshal(object, sw);

        return sw.toString();
    }

}
