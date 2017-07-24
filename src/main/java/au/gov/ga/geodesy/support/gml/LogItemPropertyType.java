package au.gov.ga.geodesy.support.gml;

import net.opengis.gml.v_3_2_1.TimePositionType;

/**
 * GMLPropertyType with change tracking attributes.
 *
 * <pre>
 * {@code
 * <siteLog>
 *     ...
 *     <gnssReceiverProperty>
 *         <GnssReceiver>
 *             ...
 *         </GnssReceier>
 *         <dateInserted>...</dataInserted>
 *         <dateDeleted>...</dataDeleted>
 *         <deletedReason>...</deletedReason>
 *     </gnssReceiverProperty>
 *     ...
 * </siteLog>
 * }
 * </pre>

 * @see resource geodesyml-v_0_4.xjb
 */
public interface LogItemPropertyType extends GMLPropertyType {

    TimePositionType getDateInserted();
    TimePositionType getDateDeleted();
    String getDeletedReason();

    void setDateInserted(TimePositionType date);
    void setDateDeleted(TimePositionType date);
    void setDeletedReason(String reason);
}
