@XmlJavaTypeAdapters({
    @XmlJavaTypeAdapter(value=StringAdapter.class, type=String.class)
})
package net.opengis.iso19139.gmd.v_20070417;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;

import au.gov.ga.geodesy.port.adapter.geodesyml.StringAdapter;


