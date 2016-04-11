# Java Bindings for GeodesyML v0.3

[![Build Status](https://travis-ci.org/GeoscienceAustralia/geodesyml-java-bindings.svg?branch=master)](https://travis-ci.org/GeoscienceAustralia/geodesyml-java-bindings)


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

Since the GeodesyML schema is in active development, currently in beta release,
the implementation relies heavily on code generation to minimise manual
intervention following schema updates. Generation of JAXB model classes is handled
in a fork of [JAXB for OGC Project](http://www.ogcnetwork.net/jaxb4ogc) at
https://github.com/GeoscienceAustralia/ogc-schemas/tree/geodesyml.

See unit test-cases for usage.

#### Contact Information

Contributions and bug reports are welcome!

Please feel free to contact us through GitHub or at geodesy@ga.gov.au.

-Lazar Bodor (lazar.bodor@ga.gov.au)





