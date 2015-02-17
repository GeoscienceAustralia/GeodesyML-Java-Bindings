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
import au.gov.ga.geodesy.interfaces.geodesyml.dto.AdjustmentType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.GeodesyMLType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.gml.AbstractFeatureType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.igssitelog.IgsSiteLogType;

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

    public <T extends Marshallable> void marshal(T t, Writer out) {
        try {
            createMarshaller().marshal(t, out);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public void marshal(JAXBElement<?> element, Writer out) {
        try {
            createMarshaller().marshal(element, out);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public Marshallable unmarshal(Reader in) throws TypeMismatchException {
        return unmarshal(in, Marshallable.class);
    }

    @SuppressWarnings("unchecked")
    public <T extends Marshallable> T unmarshal(Reader in, Class<T> type) throws TypeMismatchException {
        try {
            StreamSource source = new StreamSource(in);
            /* return createUnmarshaller().unmarshal(source, type).getValue(); */
            
            Object x = createUnmarshaller().unmarshal(source);
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
