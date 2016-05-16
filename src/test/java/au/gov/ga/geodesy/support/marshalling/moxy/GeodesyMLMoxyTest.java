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
