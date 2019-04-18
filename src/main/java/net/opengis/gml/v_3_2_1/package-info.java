@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value=StringAdapter.class, type=String.class)
})
package net.opengis.gml.v_3_2_1;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import au.gov.ga.geodesy.support.marshalling.moxy.StringAdapter;


