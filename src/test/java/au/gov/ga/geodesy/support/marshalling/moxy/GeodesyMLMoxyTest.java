package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.List;

import javax.xml.bind.JAXBElement;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import au.gov.ga.geodesy.interfaces.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.support.spring.AppConfig;
import au.gov.xml.icsm.geodesyml.v_0_2_2.GeodesyMLType;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
    classes = {AppConfig.class},
    loader  = AnnotationConfigContextLoader.class
)
public class GeodesyMLMoxyTest {

    @Autowired
    private GeodesyMLMarshaller marshaller;

    @Test
    public void unmarshal() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));

        GeodesyMLType geodesyML = marshaller.unmarshal(input).getValue();
        List<JAXBElement<?>> els = geodesyML.getNodeOrAbstractPositionOrPositionPairCovariance();
        Assert.assertNotNull(els);
        Assert.assertNotEquals(0, els.size());

        System.out.println("geodesyML.getNodeOrAbstractPositionOrPositionPairCovariance elements:");
        geodesyML.getNodeOrAbstractPositionOrPositionPairCovariance().forEach(x -> {
            System.out.println("  "+x.getName());
        });
    }

    @Test
    public void marshal() throws Exception {
        Reader input = new InputStreamReader(Thread.currentThread()
            .getContextClassLoader()
            .getResourceAsStream("MOBS.xml"));

        marshaller.marshal(marshaller.unmarshal(input), new PrintWriter(System.out));
    }
}
