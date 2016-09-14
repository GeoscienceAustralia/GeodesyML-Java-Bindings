package au.gov.ga.geodesy.support.marshalling.moxy;

import java.lang.reflect.Method;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.oxm.mappings.nullpolicy.AbstractNullPolicy;
import org.eclipse.persistence.oxm.mappings.nullpolicy.XMLNullRepresentationType;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.SessionEvent;
import org.eclipse.persistence.sessions.SessionEventAdapter;

/**
 * Ensure null values are marshalled as absent, rather than empty, XML elements.
 * Empty XML elements will often break schema validation.
 */
public class NullPolicySessionEventListener extends SessionEventAdapter {

    @Override
    public void preLogin(SessionEvent event) {
        Project project = event.getSession().getProject();
        for (ClassDescriptor descriptor : project.getOrderedDescriptors()) {
            for (DatabaseMapping mapping : descriptor.getMappings()) {
                // TODO: keep this around for a while
                /* if (mapping.isAbstractDirectMapping()) { */
                /*     XMLDirectMapping m = (XMLDirectMapping) mapping; */
                /*     m.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE); */
                /* } */
                /* else if (mapping.isAbstractCompositeObjectMapping()) { */
                /*     XMLCompositeObjectMapping m = (XMLCompositeObjectMapping) mapping; */
                /*     m.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE); */
                /* } */
                /* else if (mapping.isAbstractCompositeCollectionMapping()) { */
                /*     XMLCompositeCollectionMapping m = (XMLCompositeCollectionMapping) mapping; */
                /*     m.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE); */
                /* } */
                /* else if (mapping.isAbstractCompositeDirectCollectionMapping()) { */
                /*     XMLCompositeDirectCollectionMapping m = (XMLCompositeDirectCollectionMapping) mapping; */
                /*     m.getNullPolicy().setMarshalNullRepresentation(XMLNullRepresentationType.ABSENT_NODE); */
                /* } */
                try {
                    Method getNullPolicy = mapping.getClass().getMethod("getNullPolicy");
                    AbstractNullPolicy nullPolicy = (AbstractNullPolicy) getNullPolicy.invoke(mapping);
                    nullPolicy.setMarshalNullRepresentation(XMLNullRepresentationType.XSI_NIL);
                }
                catch (NoSuchMethodException ok) {
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
     }
}
