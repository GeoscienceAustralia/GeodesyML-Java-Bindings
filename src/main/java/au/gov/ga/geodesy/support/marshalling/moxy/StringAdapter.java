package au.gov.ga.geodesy.support.marshalling.moxy;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class StringAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String s) throws Exception {
        if (s != null) {
            String trimmed = s.trim();
            if (!trimmed.equals("")) {
                return trimmed;
            }
        }
        return null;
    }

    @Override
    public String unmarshal(String s) throws Exception {
        return marshal(s);
    }
}