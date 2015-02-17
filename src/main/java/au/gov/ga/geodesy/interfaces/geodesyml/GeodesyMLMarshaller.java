package au.gov.ga.geodesy.interfaces.geodesyml;

import java.io.Reader;
import java.io.Writer;

public interface GeodesyMLMarshaller {
    <T extends Marshallable> void marshal(T t, Writer out);
    <T extends Marshallable> T unmarshal(Reader in, Class<T> t) throws TypeMismatchException;
    Marshallable unmarshal(Reader in) throws TypeMismatchException;
}
