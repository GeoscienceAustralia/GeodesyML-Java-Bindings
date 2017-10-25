package au.gov.ga.geodesy.support.gml;

import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.junit.Test;

import au.gov.ga.geodesy.port.adapter.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.support.marshalling.moxy.GeodesyMLMoxy;
import au.gov.xml.icsm.geodesyml.v_0_5.GeodesyMLType;
import au.gov.xml.icsm.geodesyml.v_0_5.SiteLogType;
import au.gov.xml.icsm.geodesyml.v_0_5.SitePropertyType;

public class GMLPropertyTypeResolverTest {

    private Reader resourceReader(String resource) throws FileNotFoundException {
        return new FileReader(Thread.currentThread().getContextClassLoader().getResource(resource).getFile());
    }

    @Test
    public void test() throws Exception {
        GeodesyMLMarshaller marshaller = new GeodesyMLMoxy();
        GeodesyMLType geodesyML = marshaller.unmarshal(resourceReader("MOBS.xml"), GeodesyMLType.class).getValue();

        SiteLogType siteLog = geodesyML.getElements().stream()
            .filter(e -> e.getValue() instanceof SiteLogType)
            .map(e -> (SiteLogType) e.getValue())
            .findFirst()
            .get();
        SitePropertyType siteProperty = siteLog.getAtSite();
        assertNotNull(siteProperty.getHref());
        new GMLPropertyTypeResolver(geodesyML);
        assertNotNull(siteProperty.getSite());
    }
}

