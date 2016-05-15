package au.gov.ga.geodesy.port.adapter.geodesyml;

import java.io.Writer;

import javax.xml.bind.JAXBElement;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.Reader;

public interface GeodesyMLMarshaller {
    void marshal(Object x, Writer writer) throws MarshallingException;

    // TODO: remove
    void marshal(JAXBElement<?> doc, Writer writer) throws MarshallingException;


    // TODO: unmarshal to plain DTO instead, why not?
    <T extends Object> JAXBElement<T> unmarshal(Reader reader, Class<T> type) throws MarshallingException;

}
