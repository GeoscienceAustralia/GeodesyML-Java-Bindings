package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.springframework.stereotype.Component;

import au.gov.ga.geodesy.interfaces.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.interfaces.geodesyml.MarshallingException;
import au.gov.xml.icsm.geodesyml.v_0_2_2.GeodesyMLType;

@Component
public class GeodesyMLMoxy implements GeodesyMLMarshaller {

    private JAXBContext jaxbContext;

    public GeodesyMLMoxy() throws MarshallingException {
        try {
            jaxbContext = JAXBContextFactory.createContext(new Class[] {GeodesyMLType.class}, null);
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to initialise JAXBContext", e);
        }
    }

    private void configureNamespacePrefixMapping(Marshaller marshaller) throws PropertyException {
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {

            private Map<String, String> namespacePrefixMap = new HashMap<String, String>() {{
                put("http://www.opengis.net/gml/3.2", "gml");
                put("http://www.isotc211.org/2005/gco", "gco");
                put("http://www.isotc211.org/2005/gmd", "gmd");
                put("http://www.isotc211.org/2005/gmx", "gmx");
                put("http://www.opengis.net/om/2.0", "om");
                put("urn:xml-gov-au:icsm:egeodesy:0.2", "geo");
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
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:xml-gov-au:icsm:egeodesy:0.2");
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

    public void marshal(JAXBElement<GeodesyMLType> site, Writer writer) throws MarshallingException {
        try {
            createMarshaller().marshal(site, writer);
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to marshal a site log", e);
        }
    }

    @SuppressWarnings("unchecked")
    public JAXBElement<GeodesyMLType> unmarshal(Reader reader) throws MarshallingException {
        try {
            return (JAXBElement<GeodesyMLType>) createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to unmarshal a site log", e);
        }
    }
}
