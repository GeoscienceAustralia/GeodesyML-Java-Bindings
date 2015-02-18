package au.gov.ga.geodesy.support.codegen.spoon;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtField;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtTypeReference;

import au.gov.ga.geodesy.interfaces.geodesyml.dto.AdjustmentType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.GeodesyMLType;

/**
 * JAXB-XJC considers <code>AbstractEstimation</code> entities to be of type
 * <code>AbstractFeature</code>. According to the definition of this
 * element in <code>Adjustment.xsd</code>, however, XJC's take seems to be an over
 * generalisation.
 * <p>
 * We transform the reference to abstract estimations to reflect the one
 * defined choice of estimation, <code>AdjustmentType</code>.
 * </p>
 */
/*
  TODO: Review this decision. If ever different types of
  estimations are defined, we will generilise them all with an interface.
  Also, if this the way to go, we will apply the same technique to abstract
  measurements, and potentially other elements, too.
*/
public class SpecialiseAbstractEstimation extends AbstractProcessor<CtClass<?>> {

    @Override
    public boolean isToBeProcessed(CtClass<?> element) {
        return element.getActualClass().equals(GeodesyMLType.class);
    }

    /**
     * Change the type of property <code>abstractEstimations</code> from
     * <code>List<AbstractFeature></code> to <code>List<AdjustmentType>}</code>
     * and swap its <code>XmlElement</code> annotation with an
     * <code>XmlElementRef</code> annotation.
     */
    public void process(CtClass<?> element) {
        TypeFactory types = new TypeFactory(getFactory());

        CtField<?> abstractEstimation = element.getField("abstractEstimation");
        CtTypeReference<AdjustmentType> t = types.createReference(AdjustmentType.class);
        List<CtTypeReference<?>> tt = new ArrayList<CtTypeReference<?>>();
        tt.add(t);
        abstractEstimation.getType().setActualTypeArguments(tt);
        element.getMethod("getAbstractEstimation").getType().setActualTypeArguments(tt);
        
        CtCodeSnippetStatement checkNull = getFactory().Code().createCodeSnippetStatement(
                    "if (abstractEstimation == null) { abstractEstimation = new java.util.ArrayList<AdjustmentType>(); }");

        element.getMethod("getAbstractEstimation").getBody().getStatement(0).replace(checkNull);

        abstractEstimation.removeAnnotation(abstractEstimation.getAnnotation(types.createReference(XmlElement.class)));
        getFactory().Annotation().annotate(abstractEstimation, XmlElementRef.class);
    }
}
