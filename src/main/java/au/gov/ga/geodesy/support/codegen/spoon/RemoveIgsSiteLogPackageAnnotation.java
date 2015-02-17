package au.gov.ga.geodesy.support.codegen.spoon;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtClass;

import au.gov.ga.geodesy.interfaces.geodesyml.dto.igssitelog.BasePossibleProblemSourcesType;

public class RemoveIgsSiteLogPackageAnnotation extends AbstractProcessor<CtClass<?>> {

    @Override
    public boolean isToBeProcessed(CtClass<?> element) {
        return element.getActualClass().equals(BasePossibleProblemSourcesType.class);
    }

    public void process(CtClass<?> element) {
        element.getPackage().removeAnnotation(element.getPackage().getAnnotations().get(0));
    }
}
