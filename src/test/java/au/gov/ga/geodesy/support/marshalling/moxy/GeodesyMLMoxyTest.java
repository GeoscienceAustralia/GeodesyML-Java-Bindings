package au.gov.ga.geodesy.support.marshalling.moxy;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import au.gov.ga.geodesy.interfaces.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.interfaces.geodesyml.Marshallable;
import au.gov.ga.geodesy.interfaces.geodesyml.TypeMismatchException;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.AdjustmentType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.GeodesyMLType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.NodeType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.PositionType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.gml.AbstractFeatureType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.igssitelog.IgsSiteLogType;

public class GeodesyMLMoxyTest {

    private static final String examplesDir = "geodesyml-examples-v0.1.2";

    private GeodesyMLMarshaller marshaller = new GeodesyMLMoxy();

    private ClassLoader classLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private Reader getResource(String resource) {
        return new InputStreamReader(classLoader().getResourceAsStream(resource));
    }

    private Reader getExample(String resource) {
        return getResource(examplesDir + "/" + resource);
    }

    private void printSuccess(String resource, Class<? extends Marshallable> x) {
        System.out.println("Unmarshalled " + resource + " to " + x.getName());
    }

    @Test
    public void unmarshalGeodesyMLExamples() throws IOException {
        List<String> examples = IOUtils.readLines(classLoader().getResourceAsStream(examplesDir));

        boolean failed = false;

        for (String example : examples) {
            // TODO: what about extensions?
            if (example.startsWith("ExtensionExampleB.x") || example.startsWith("ExtensionExampleC.x")) {
                continue;
            }
            try {
                printSuccess(example, marshaller.unmarshal(getExample(example)).getClass());
            } catch (Exception e) {
                System.err.println("Failed to unmarshal " + example);
                e.printStackTrace();
                failed = true;
            }
        }
        if (failed) {
            Assert.fail("Some examples failed to unmarshal, see above error messages.");
        }
    }

    @Test
    public void unmarshalNode() throws Exception {
        @SuppressWarnings("unused")
        NodeType type = marshaller.unmarshal(getExample("IGSSiteLogExample.xml"), NodeType.class);
        printSuccess("IGSSiteLogExample.xml", NodeType.class);
    }

    @Test(expected = TypeMismatchException.class)
    public void testTypeMismatch() throws Exception {
        marshaller.unmarshal(getExample("IGSSiteLogExample.xml"), PositionType.class);
    }

    @Test
    public void testAdjustmentExample() throws Exception {
        GeodesyMLType ml = marshaller.unmarshal(getExample("AdjustmentExampleA.xml"), GeodesyMLType.class);
        @SuppressWarnings("unused")
        AdjustmentType a = (AdjustmentType) ml.getAbstractEstimation().get(0);
    }

    @Test
    public void unmarshalSiteLog() throws Exception {
        @SuppressWarnings("unused")
        IgsSiteLogType siteLog = marshaller.unmarshal(getResource("TID2.xml"), IgsSiteLogType.class);
        printSuccess("TID2.xml", IgsSiteLogType.class);
    }

    @Test
    public void unmarshalAbstractFeature() throws Exception {
        @SuppressWarnings("unused")
        AbstractFeatureType feature = marshaller.unmarshal(getExample("IGSSiteLogExample.xml"), AbstractFeatureType.class);
        printSuccess("IGSSiteLogExample.xml", AbstractFeatureType.class);
    }

    @Test
    public void unmarshalAny() throws Exception {
        marshaller.unmarshal(getExample("IGSSiteLogExample.xml"));
        printSuccess("IGSSiteLogExample.xml", Marshallable.class);
    }
}
