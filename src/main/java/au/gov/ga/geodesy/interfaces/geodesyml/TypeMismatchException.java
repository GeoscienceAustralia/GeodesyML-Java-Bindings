package au.gov.ga.geodesy.interfaces.geodesyml;

/**
 * Thrown when a <code>GeodesyMLMarshaller</code>, given a GeodesyML document
 * containing a root element of some <code>actualType</code>, is asked to
 * unmarshal it to an object of some <code>expectedType</code> that is
 * unassignable from the <code>actualType</code>.
 */
@SuppressWarnings("serial")
public class TypeMismatchException extends Exception {

    public <T extends Marshallable> TypeMismatchException(Class<T> expectedType, Class<?> actualType) {
        super("Expected " + expectedType.getName() + ", but encountered " + actualType.getName());
    }
}

