package au.gov.ga.geodesy.interfaces.geodesyml;

import java.io.Reader;
import java.io.Writer;

/**
 * GeodesyML marshaller service contract.
 *
 */
public interface GeodesyMLMarshaller {

    /**
     * Serialise any marshallable object to a GeodesyML document.
     *
     * @param t instance of <code>Marshallable</code> to serialise 
     * @param out XML output writer
     */
    <T extends Marshallable> void marshal(T t, Writer out);

    /**
     * Deserialise a GeodesyML document to a specified marshallable instance.
     *
     * @param in XML input reader
     * @param t target type
     * @return unmarshalled DTO
     * @throws TypeMismatchException if the root of the suppliced document is
     * not mapped to a DTO that is an instance of the requested type <code>t</code>.
     */
    <T extends Marshallable> T unmarshal(Reader in, Class<T> t) throws TypeMismatchException;

    /**
     * Deserialise a GeodesyML document.
     *
     * @param in XML input reader
     * @return unmarshalled DTO
     * @throws TypeMismatchException if the root of the supplied document is not
     * mapped to a <code>Marshallable</code> DTO
     */
    Marshallable unmarshal(Reader in) throws TypeMismatchException;
}
