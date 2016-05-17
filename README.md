# Java Bindings for GeodesyML v0.3

#### Background

<!-- ##### eGeodesy-->

The [eGeodesy Logical Model](http://icsm.govspace.gov.au/egeodesy/), developed by the
Permanent Committee on Geodesy (PCG) of the Australian and New Zealand
Intergovernmental Committee on Surveying and Mapping (ICSM), defines a
technology-independent language to model the core business processes, entities,
and relationships within the geodetic domain.

<!-- ##### GeodesyML-->

[GeodesyML](http://github.com/GeoscienceAustralia/GeodesyML), an XML
implementation of the eGeodesy model, is a Geography Markup Language
([GML](http://www.opengeospatial.org/standards/gml))
application schema for transfer of geodetic information. For more information
about eGeodesy and GeodesyML, see http://www.geodesyml.org.

<!-- ##### Geoscience Australia-->

Geoscience Australia ([GA](http://www.ga.gov.au)) is adopting GeodesyML as the standard for
exchange of geodetic information.

This library is part of GA's codebase, released as open-source. It is a
work-in-progress; collaborators, users, and reviewers are more than welcome.

#### Implementation

Since GeodesyML is in active development, currently in beta release,
the implementation relies heavily on code generation to minimise manual
intervention following schema updates. Generation of JAXB binding classes is handled
in a fork of [JAXB for OGC Project](http://www.ogcnetwork.net/jaxb4ogc) at
https://github.com/GeoscienceAustralia/ogc-schemas/tree/geodesyml.

GeodesyML binding classes are generated in an almost one-to-one correspondence
with GeodesyML schema element definitions. The generated code is often not
idiomatic Java, rather it follows in structure more closely the conventions of
GML, an XML-based language. It is unlikely that these classes will be
particularly suitable for any non-trivial amount of processing. Their job is to
load XML data in and out of memory, where it can be manipulated into structures
suitable to specific tasks.

##### About JAXB
Java Architecture for XML Bindings (JAXB) allows Java programs to specify a
static, type-driven, mapping from Java classes to XML element definitions.
JAXB's binding compiler (XJC) can be used to automatically generate Java
binding classes from XML schema files, which is useful particulary in the early
stages of XML schema development.

At runtime, Java programs can use the JAXB API to unmarshal XML documents into Java object and to marshal
Java objects back into conformant XML documents.

See unit tests for examples of usage.

#### Contact Information

Contributions and bug reports are welcome!

Please feel free to contact us through GitHub or at geodesy@ga.gov.au.

-Lazar Bodor (lazar.bodor@ga.gov.au)





