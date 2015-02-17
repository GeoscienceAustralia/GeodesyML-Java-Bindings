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

public class SpecialiseAbstractEstimation extends AbstractProcessor<CtClass<?>> {

    @Override
    public boolean isToBeProcessed(CtClass<?> element) {
        return element.getActualClass().equals(GeodesyMLType.class);
    }

    public void process(CtClass<?> element) {
        TypeFactory types = new TypeFactory(getFactory());
        CtTypeReference<AdjustmentType> t = types.createReference(AdjustmentType.class);
        List<CtTypeReference<?>> tt = new ArrayList<CtTypeReference<?>>();

        tt.add(t);
        CtField<?> abstractEstimation = element.getField("abstractEstimation");

        abstractEstimation.getType().setActualTypeArguments(tt);
        
        abstractEstimation.removeAnnotation(abstractEstimation.getAnnotation(types.createReference(XmlElement.class)));
        getFactory().Annotation().annotate(abstractEstimation, XmlElementRef.class);

        element.getMethod("getAbstractEstimation").getType().setActualTypeArguments(tt);

        CtCodeSnippetStatement checkNull = getFactory().Code().createCodeSnippetStatement(
                    "if (abstractEstimation == null) { abstractEstimation = new java.util.ArrayList<AdjustmentType>(); }");

        element.getMethod("getAbstractEstimation").getBody().getStatement(0).replace(checkNull);
    }
}
