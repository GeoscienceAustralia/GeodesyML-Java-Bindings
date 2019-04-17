package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.oxm.NamespacePrefixMapper;
import org.eclipse.persistence.sessions.SessionEventListener;

import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.port.adapter.geodesyml.MarshallingException;
import au.gov.ga.geodesy.support.gml.GMLPropertyTypeResolver;
import au.gov.xml.icsm.geodesyml.v_0_4.GeodesyMLType;
import net.bramp.objectgraph.ObjectGraph;
import net.opengis.gml.v_3_2_1.AbstractGMLType;

public class GeodesyMLMoxy implements GeodesyMLMarshaller {

    private JAXBContext jaxbContext;
    
    public GeodesyMLMoxy() {
        try {
            Properties properties = new Properties();
            SessionEventListener sessionEventListener = new NullPolicySessionEventListener();
            properties.put(JAXBContextProperties.SESSION_EVENT_LISTENER, sessionEventListener);
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            
            String mappingFilename = "moxy.xml";
            Source mapping_geodesyMl = new StreamSource(classLoader.getResourceAsStream(mappingFilename));
            Map<String, Source> metadata = new HashMap<String, Source>();
            metadata.put("au.gov.xml.icsm.geodesyml.v_0_4", mapping_geodesyMl);
            properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadata);
            
            jaxbContext = JAXBContextFactory.createContext(new Class[] {GeodesyMLType.class}, properties);
        } catch (JAXBException e) {
            throw new RuntimeException("Failed to initialise JAXBContext", e);
        }
    }

    private void configureNamespacePrefixMapping(Marshaller marshaller) throws PropertyException {
        marshaller.setProperty("com.sun.xml.bind.namespacePrefixMapper", new NamespacePrefixMapper() {

            @SuppressWarnings("serial")
            private Map<String, String> namespacePrefixMap = new HashMap<String, String>() {{
                put("http://www.w3.org/1999/xlink", "xlink");
                put("http://www.opengis.net/gml/3.2", "gml");
                put("http://www.isotc211.org/2005/gco", "gco");
                put("http://www.isotc211.org/2005/gmd", "gmd");
                put("http://www.isotc211.org/2005/gmx", "gmx");
                put("http://www.opengis.net/om/2.0", "om");
                put("urn:xml-gov-au:icsm:egeodesy:0.4", "geo");
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
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "urn:xml-gov-au:icsm:egeodesy:0.4");
            this.configureNamespacePrefixMapping(marshaller);
            return marshaller;
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to create marshaller", e);
        }
    }
    
    private Unmarshaller createUnmarshaller() throws MarshallingException {
        try {
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            JAXBUnmarshaller f = (JAXBUnmarshaller) unmarshaller;
            return unmarshaller;
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to create unmarshaller", e);
        }
    }
    
    public void marshal(JAXBElement<?> site, Writer writer) throws MarshallingException {
        marshalJAXBElement(site, writer);
    }

    private void assignGmlIds(Object x) {
        ObjectGraph
            .visitor(new ObjectGraph.Visitor() {
                @Override
                public boolean visit(Object object, Class<?> clazz) {
                    if (AbstractGMLType.class.isAssignableFrom(object.getClass())) {
                        AbstractGMLType gmlType = (AbstractGMLType) object;
                        if (StringUtils.isEmpty(gmlType.getId())) {
                            String typeName = object.getClass().getSimpleName();
                            String idPrefix = typeName.substring(0, typeName.length() - 4) + '.';
                            gmlType.setId(idPrefix + UUID.randomUUID());
                        }
                    }
                    if (object instanceof Collection) {
                        ((Collection<?>) object).forEach(element -> assignGmlIds(element));
                    }
                    return false;
                }
            })
            .excludeStatic()
            .traverse(x);
    }

    private void marshalJAXBElement(JAXBElement<?> x, Writer writer) throws MarshallingException {
        try {
            assignGmlIds(x);
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
            GMLPropertyTypeResolver.resolveAllProperties(element.getValue());
            Class<?> actualType = element.getDeclaredType();
            if (type.isAssignableFrom(actualType)) {
                return (JAXBElement<T>) element;
            } else {
                throw new MarshallingException("Type mismatch: expected " + type.getName() + ", but got " + actualType);
            }
        } catch (JAXBException e) {
            throw new MarshallingException("Failed to unmarshal a site log", e);
        }
    }
}
