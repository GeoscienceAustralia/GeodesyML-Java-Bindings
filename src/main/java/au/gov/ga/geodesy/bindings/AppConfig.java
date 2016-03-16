package au.gov.ga.geodesy.bindings;
 
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import au.gov.ga.geodesy.interfaces.geodesyml.GeodesyMLMarshaller;
import au.gov.ga.geodesy.interfaces.geodesyml.MarshallingException;
import au.gov.ga.geodesy.support.marshalling.moxy.GeodesyMLMoxy;
 
@Configuration
@ComponentScan(basePackages = "au.gov.ga.geodesy")
public class AppConfig {
    @Bean
    public GeodesyMLMarshaller getGeodesyMLMarshaller() throws MarshallingException {
        return new GeodesyMLMoxy();
    }
}