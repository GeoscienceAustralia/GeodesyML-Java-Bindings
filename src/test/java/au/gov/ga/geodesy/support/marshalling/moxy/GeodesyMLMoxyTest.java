package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.NamespaceContext;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;

import com.cedarsoftware.util.Traverser;
import com.jcabi.matchers.XhtmlMatchers;
import com.jcabi.xml.XPathContext;

import au.gov.ga.geodesy.port.adapter.geodesyml.MarshallingException;
import au.gov.xml.icsm.geodesyml.v_0_5.GeodesyMLType;
import au.gov.xml.icsm.geodesyml.v_0_5.HumiditySensorType;
import au.gov.xml.icsm.geodesyml.v_0_5.SiteType;

public class GeodesyMLMoxyTest {

    private static final NamespaceContext namespaces = new XPathContext()
        .add("geo", "urn:xml-gov-au:icsm:egeodesy:0.5")
        .add("gml", "http://www.opengis.net/gml/3.2");
    
    private GeodesyMLMoxy marshaller; public GeodesyMLMoxyTest() throws MarshallingException {
        marshaller = new GeodesyMLMoxy();
    }

    @Test
    public void unmarshal() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));

        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        List<JAXBElement<?>> els = geodesyML.getElements();
        Assert.assertNotNull(els);
        Assert.assertNotEquals(0, els.size());

        System.out.println("geodesyML elements:");
        geodesyML.getElements().forEach(x -> {
            System.out.println("  "+x.getName());
        });
    }

    @Test
    public void generateGmlId() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));

        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        SiteType siteLogType = (SiteType) geodesyML.getElements().get(0).getValue();
        siteLogType.setId(null);

        StringWriter xml = new StringWriter();
        marshaller.marshal(geodesyML, xml);
        MatcherAssert.assertThat(xml.toString(), XhtmlMatchers.hasXPath("/geo:GeodesyML/geo:Site[@gml:id]", namespaces)); 
    }

    @Test
    public void unmarshalWithNullNumericFields() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("MOBS-null-numerics.xml"));

        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        List<JAXBElement<?>> els = geodesyML.getElements();
        Assert.assertNotNull(els);
        Assert.assertNotEquals(0, els.size());

        geodesyML.getElements().forEach(x -> {
            if (x.getName().toString().indexOf("humiditySensor") != -1) {
                HumiditySensorType humiditySensorType = (HumiditySensorType)x.getValue();

                // verify that a non-null Double field has the correct value
                Double heightDiffToAntenna = humiditySensorType.getHeightDiffToAntenna();
                Assert.assertEquals((Double)2.5, (Double)heightDiffToAntenna);

                // verify that a null Double field is null in the type (not zero)
                Double accuracyPercentRelativeHumidity = humiditySensorType.getAccuracyPercentRelativeHumidity();
                Assert.assertNull(accuracyPercentRelativeHumidity);
            }
        });
    }

    @Test
    public void marshalJAXBElement() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));
        marshaller.marshal(marshaller.unmarshal(input, GeodesyMLType.class), new PrintWriter(System.out));
    }
    
    @Test
    public void marshalJAXBElementWithEmptySpaces() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS2-with-trailing-whitespace.xml"));
        GeodesyMLType geodesyML = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        List<JAXBElement<?>> els = geodesyML.getElements();
        Traverser.traverse(geodesyML, new Traverser.Visitor() {
            public void process(Object x) {
                if (x instanceof String) {
                    Assert.assertEquals(x.toString(), x.toString().trim());
                }
            }
        });
    }
    
    @Test
    public void marshal() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));

        GeodesyMLType ml = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        marshaller.marshal(ml, new PrintWriter(System.out));
    }
}
