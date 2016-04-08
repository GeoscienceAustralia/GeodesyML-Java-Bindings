package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.sessions.SessionEventListener;

import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.port.adapter.geodesyml.MarshallingException;
import au.gov.xml.icsm.geodesyml.v_0_3.GeodesyMLType;

public class GeodesyMLMoxy implements GeodesyMLMarshaller {

    private JAXBContext jaxbContext;

    public GeodesyMLMoxy() throws MarshallingException {
        try {
            Properties properties = new Properties();
            SessionEventListener sessionEventListener = new NullPolicySessionEventListener();
            properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
            jaxbContext = JAXBContextFactory.createContext(new Class[] {GeodesyMLType.class}, properties);
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to initialise JAXBContext", e);
        }
    }

    private void configureNamespacePrefixMapping(Marshaller marshaller) throws PropertyException {
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {

            @SuppressWarnings("serial")
            private Map<String, String> namespacePrefixMap = new HashMap<String, String>() {{
                put("http://www.opengis.net/gml/3.2", "gml");
                put("http://www.isotc211.org/2005/gco", "gco");
                put("http://www.isotc211.org/2005/gmd", "gmd");
                put("http://www.isotc211.org/2005/gmx", "gmx");
                put("http://www.opengis.net/om/2.0", "om");
                put("urn:xml-gov-au:icsm:egeodesy:0.3", "geo");
            }};

            public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
                return namespacePrefixMap.getOrDefault(namespaceUri, suggestion);
            }
        });
    }

    private Marshaller createMarshaller() throws MarshallingException {
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "ISO-8859-1");
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:xml-gov-au:icsm:egeodesy:0.3");
            configureNamespacePrefixMapping(marshaller);
            return marshaller;
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to create marshaller", e);
        }
    }

    private Unmarshaller createUnmarshaller() throws MarshallingException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            return unmarshaller;
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to create unmarshaller", e);
        }
    }

    public void marshal(JAXBElement<?> site, Writer writer) throws MarshallingException {
        marshalJAXBElement(site, writer);
    }

    private void marshalJAXBElement(JAXBElement<?> x, Writer writer) throws MarshallingException {
        try {
            createMarshaller().marshal(x, writer);
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to marshal a site log", e);
        }
    }

    public void marshal(Object x, Writer writer) throws MarshallingException {
        if (x instanceof JAXBElement) {
            marshalJAXBElement((JAXBElement<?>) x, writer);
        } else {
            // TODO: how can we restrict x?
            String typeName = x.getClass().getSimpleName();
            String factoryName = x.getClass().getPackage().getName() + ".ObjectFactory";
            String factoryMethodName = "create" + typeName.substring(0, typeName.length() - "Type".length());
            try {
                Class<?> factoryClass = Class.forName(factoryName);
                Object factory = factoryClass.newInstance();
                Method factoryMethod = factoryClass.getMethod(factoryMethodName, new Class<?>[]{x.getClass()});
                JAXBElement<?> element = (JAXBElement<?>) factoryMethod.invoke(factory, new Object[]{x});
                marshal(element, writer);
            } catch (MarshallingException e) {
                throw e;
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public <T> JAXBElement<T> unmarshal(Reader reader, Class<T> type) throws MarshallingException {
        try {
            JAXBElement<?> element = (JAXBElement<?>) createUnmarshaller().unmarshal(reader);
            Class<?> actualType = element.getDeclaredType();
            if (type.isAssignableFrom(actualType)) {
                return (JAXBElement<T>) element;
            } else {
                throw new MarshallingException("Type mismatch: expected " + type.getClass() + ", but got " + actualType);
            }
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to unmarshal a site log", e);
        }
    }
}
