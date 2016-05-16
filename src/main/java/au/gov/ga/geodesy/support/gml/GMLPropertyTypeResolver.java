package au.gov.ga.geodesy.support.gml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Optional;

import javax.xml.bind.JAXBElement;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.Traverser;

import au.gov.ga.geodesy.support.gml.GMLPropertyType;

import net.opengis.gml.v_3_2_1.AbstractGMLType;

/**
 * Resolve GML property type references. GML property types either contain
 * their target elements nested inside them, or they link to their target
 * elements using xlink references.
 *
 * <pre>
 * {@code
 * <siteLog>
 *     ...
 *     <gnssReceiverProperty>
 *         <GnssReceiver>
 *             ...
 *         </GnssReceier>
 *     </gnssReceiverProperty>
 *     ...
 * </siteLog>
 * }
 * </pre>
 * or
 * <pre>
 * {@code
 * <GnssReceiver gml:id="R1">
 *     ...
 * </GnssReceier>
 * <siteLog>
 *     ...
 *     <gnssReceiverProperty href="#R1">
 *     ...
 * </siteLog>
 * }
 * </pre>
 *
 * XML elements nest into strict tree structures (every element, but the root,
 * has exactly one parent). To encode graph structures, including recursive
 * dependencies, XML documents use xlink hrefs. When unmarshalled into Java
 * objects, this referencing is awkward and unnecessary, because Java objects
 * natively form graph structures. This class traverses all GML property type
 * elements under a given root, and resolves all hrefs by nesting their target
 * elements under their respective parents.
 *
 * For now (TODO), we only support resolution of local references.
 */
public class GMLPropertyTypeResolver {

    private static final Logger log = LoggerFactory.getLogger(GMLPropertyTypeResolver.class);

    private Object rootElement;

    @SuppressWarnings("serial")
    private class ElementMap extends HashMap<String, AbstractGMLType> {}

    private ElementMap elementMap = new ElementMap();

    /**
     * All linked properties must be present under the given root element.
     */
    public GMLPropertyTypeResolver(Object rootElement) {
        this.rootElement = rootElement;
    }

    /**
     * Resolve all GML property types under the given root element. The
     * modifications to the argument are eager and destructive.
     */
    public static void resolveAllProperties(Object rootElement) {
        GMLPropertyTypeResolver resolver = new GMLPropertyTypeResolver(rootElement);
        resolver.mapAllElements();
        resolver.resolveAllProperties();
    }

    /**
     * Traverse the root element and map all descendent GML elements by their
     * gml:id attribute.
     */
    private void mapAllElements() {
        Traverser.traverse(rootElement, new Traverser.Visitor() {
            public void process(Object x) {
                if (x instanceof AbstractGMLType) {
                    AbstractGMLType element = (AbstractGMLType) x;
                    elementMap.put(element.getId(), element);
                }
            }
        });
    }

    /**
     * Traverse the root element and resolve all property type hrefs.
     */
    private void resolveAllProperties() {
        Traverser.traverse(rootElement, new Traverser.Visitor() {
            public void process(Object x) {
                if (x instanceof GMLPropertyType) {
                    resolveProperty((GMLPropertyType) x);
                }
            }
        });
    }


    /**
     * Return a GML element given a local reference to its gml:id.
     */
    private Optional<AbstractGMLType> findElementByHref(String href) {
        if (!href.startsWith("#")) {
            log.warn("Failed to resolve href " + href + ", we can only resolve local references");
            return Optional.empty();
        }
        String id = StringUtils.removeStart(href, "#");
        Optional<AbstractGMLType> element = Optional.ofNullable(elementMap.get(id));
        if (!element.isPresent()) {
            log.warn("Failed to resolve href " + href + ", there is no local element with gml:id " + id);
        }
        return element;
    }

    /**
     * Resolve and modify the given property type.
     */
    private void resolveProperty(GMLPropertyType propertyType) {
        getPropertyElement(propertyType); // discard the result
    }

    /**
     * If possible, resolve the given GML property type to its target element
     * either by following the xlink href, or by returning the nested element.
     * The argument is modified and the result of resolution thus cached.
     */
    @SuppressWarnings("unchecked")
    // TODO - is this used?
    public <T extends AbstractGMLType> Optional<T> getPropertyElement(GMLPropertyType propertyType) {
        try {
            Object element = propertyType.getTargetElement();
            if (element instanceof JAXBElement) {
                return Optional.of(((JAXBElement<T>) element).getValue());
            }
            if (element != null) {
                return Optional.of((T) element);
            }
            if (StringUtils.isNotEmpty(propertyType.getHref())) {
                Optional<T> resolvedElement = (Optional<T>) findElementByHref(propertyType.getHref());
                if (resolvedElement.isPresent()) {
                    Class<?> type = propertyType.getTargetElementType();
                    if (propertyType.getTargetElementType() == JAXBElement.class) {
                        // TODO move this wrapping into GMLPropertyType
                        JAXBElement<T> wrapped = wrapInJAXBElement(resolvedElement.get());
                        PropertyUtils.setProperty(wrapped, "value", resolvedElement.get());
                        propertyType.setTargetElement(wrapped);
                    } else {
                        propertyType.setTargetElement(resolvedElement.get());
                    }
                }
                return resolvedElement;
            }
            return Optional.empty();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> JAXBElement<T> wrapInJAXBElement(T x) {
        String typeName = x.getClass().getSimpleName();
        String factoryName = x.getClass().getPackage().getName() + ".ObjectFactory";
        String factoryMethodName = "create" + typeName.substring(0, typeName.length() - "Type".length());
        try {
            Class<?> factoryClass = Class.forName(factoryName);
            Object factory = factoryClass.newInstance();
            Method factoryMethod = factoryClass.getMethod(factoryMethodName, new Class<?>[]{x.getClass()});
            return (JAXBElement<T>) factoryMethod.invoke(factory, new Object[]{x});
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
