package au.gov.ga.geodesy.interfaces.geodesyml;

@SuppressWarnings("serial")
public class TypeMismatchException extends Exception {

    public <T extends Marshallable> TypeMismatchException(Class<T> expectedType, Class<?> actualType) {
        super("Expected " + expectedType.getName() + ", but encountered " + actualType.getName());
    }
}

