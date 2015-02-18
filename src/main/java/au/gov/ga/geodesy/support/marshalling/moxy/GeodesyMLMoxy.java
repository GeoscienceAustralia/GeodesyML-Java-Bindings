package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.Reader;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import au.gov.ga.geodesy.interfaces.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.interfaces.geodesyml.Marshallable;
import au.gov.ga.geodesy.interfaces.geodesyml.TypeMismatchException;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.GeodesyMLType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.gml.AbstractFeatureType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.igssitelog.IgsSiteLogType;

/**
 * GeodesyML marshaller backed by the EclipseLink/MOXy implementation
 * of JAXB API.
 */
public class GeodesyMLMoxy implements GeodesyMLMarshaller {

    private JAXBContext jaxbContext;

    public GeodesyMLMoxy() {
        try {
            jaxbContext = JAXBContext.newInstance(
                    GeodesyMLType.class.getPackage().getName() + ":" +
                    IgsSiteLogType.class.getPackage().getName() + ":" +
                    AbstractFeatureType.class.getPackage().getName());

        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public <T extends Marshallable> void marshal(T t, Writer out) {
        try {
            createMarshaller().marshal(t, out);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Marshallable unmarshal(Reader in) throws TypeMismatchException {
        return unmarshal(in, Marshallable.class);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public <T extends Marshallable> T unmarshal(Reader in, Class<T> type) throws TypeMismatchException {
        try {
            Object x = createUnmarshaller().unmarshal(new StreamSource(in));
            if (x instanceof JAXBElement) {
                x = ((JAXBElement<?>) x).getValue();
            }
            if (type.isAssignableFrom(x.getClass())) {
                return (T) x;
            } else {
                throw new TypeMismatchException(type, x.getClass());
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    private Marshaller createMarshaller() {
        try {
            Marshaller m = jaxbContext.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            return m;
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

   private Unmarshaller createUnmarshaller() {
      try {
         return jaxbContext.createUnmarshaller();
      } catch (JAXBException e) {
          throw new RuntimeException(e);
      }
   }
}
