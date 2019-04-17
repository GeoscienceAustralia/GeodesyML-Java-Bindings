package au.gov.ga.geodesy.port.adapter.geodesyml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Attempt to filter out certain values like "N/A", "(enter antenna type here)", and "".
 */
public class StringAdapter extends XmlAdapter<String, String> {

    @Override
    public String marshal(String s) throws Exception {
        if (s != null) {
            String trimmed = s.trim();
            if (!isEmpty(trimmed)) {
                return trimmed;
            }
        }
        return null;
    }

    private boolean isEmpty(String s) {
        return s.equals("") || s.equalsIgnoreCase("N/A") || (s.startsWith("(") && s.endsWith(")"));
    }

    @Override
    public String unmarshal(String s) throws Exception {
        return marshal(s);
    }
}