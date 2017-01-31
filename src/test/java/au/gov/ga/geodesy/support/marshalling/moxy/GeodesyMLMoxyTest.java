package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.junit.Assert;
import org.junit.Test;

import au.gov.ga.geodesy.port.adapter.geodesyml.MarshallingException;
import au.gov.xml.icsm.geodesyml.v_0_4.GeodesyMLType;
import au.gov.xml.icsm.geodesyml.v_0_4.HumiditySensorType;

public class GeodesyMLMoxyTest {

    private GeodesyMLMoxy marshaller;

    public GeodesyMLMoxyTest() throws MarshallingException {
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
    public void marshal() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));

        GeodesyMLType ml = marshaller.unmarshal(input, GeodesyMLType.class).getValue();
        marshaller.marshal(ml, new PrintWriter(System.out));
    }
}
