package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.NamespaceContext;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XPathContext;

import au.gov.ga.geodesy.port.adapter.geodesyml.MarshallingException;
import au.gov.xml.icsm.geodesyml.v_0_5.GeodesyMLType;
import au.gov.xml.icsm.geodesyml.v_0_5.GnssReceiverType;
import au.gov.xml.icsm.geodesyml.v_0_5.SiteType;

import net.opengis.gml.v_3_2_1.AbstractGMLType;

public class GeodesyMLMoxyTest {

    private static final NamespaceContext namespaces = new XPathContext()
        .add("geo", "urn:xml-gov-au:icsm:egeodesy:0.5")
        .add("gml", "http://www.opengis.net/gml/3.2");
    
    private GeodesyMLMoxy marshaller;

    public GeodesyMLMoxyTest() throws MarshallingException {
        marshaller = new GeodesyMLMoxy();
    }

    @Test
    public void unmarshal() throws Exception {
        Reader input = getSiteLogFile("MOBS.xml");
        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        List<JAXBElement<?>> els = geodesyML.getElements();
        Assert.assertNotNull(els);
        Assert.assertNotEquals(0, els.size());
    }

    @Test
    public void generateGmlId() throws Exception {
        Reader input = getSiteLogFile("MOBS.xml");
        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        SiteType siteLogType = (SiteType) geodesyML.getElements().get(0).getValue();
        siteLogType.setId(null);

        StringWriter xml = new StringWriter();
        marshaller.marshal(geodesyML, xml);
        MatcherAssert.assertThat(xml.toString(), XhtmlMatchers.hasXPath("/geo:GeodesyML/geo:Site[@gml:id]", namespaces)); 
    }

    @SuppressWarnings("unchecked")
    private <T> T getElementById(GeodesyMLType document, Class<T> elementType, String id) {
        List<AbstractGMLType> elements = document.getElements().stream()
            .map(JAXBElement::getValue)
            .filter(e -> e instanceof AbstractGMLType)
            .map(e -> (AbstractGMLType) e)
            .filter(e -> e.getId().equals(id))
            .collect(Collectors.toList());

        Assert.assertEquals(1, elements.size());
        Assert.assertTrue(elementType.isAssignableFrom(elements.get(0).getClass()));
        return (T) elements.get(0);
    }

    @Test
    public void unmarshalWithNullNumericFields() throws Exception {
        Reader input = getSiteLogFile("MOBS.xml");
        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();

        GnssReceiverType receiverOne = getElementById(geodesyML, GnssReceiverType.class, "GNSS_REC_1");
        Assert.assertEquals((Double) 2.5, receiverOne.getTemperatureStabilization());

        GnssReceiverType receiverTwo = getElementById(geodesyML, GnssReceiverType.class, "GNSS_REC_2");
        Assert.assertNull(receiverTwo.getTemperatureStabilization());
    }

    @Test
    public void marshalJAXBElement() throws Exception {
        Reader input = getSiteLogFile("MOBS.xml");
        JAXBElement<GeodesyMLType> ml = marshaller.unmarshal(input, GeodesyMLType.class);
        marshaller.marshal(ml, new PrintWriter(System.out));
    }

    @Test
    public void marshal() throws Exception {
        Reader input = getSiteLogFile("MOBS.xml");
        GeodesyMLType ml = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        marshaller.marshal(ml, new PrintWriter(System.out));
    }

    private Reader getSiteLogFile(String fileName) {
        return new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream(fileName));
    }
}
