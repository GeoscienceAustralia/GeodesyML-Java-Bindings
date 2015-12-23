package au.gov.ga.geodesy.support.codegen.spoon;

import java.util.Arrays;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;

import au.gov.ga.geodesy.interfaces.geodesyml.dto.gml.AbstractCRSType;
import au.gov.ga.geodesy.interfaces.geodesyml.dto.igssitelog.BasePossibleProblemSourcesType;

/**
 * Remove misplaced package annotations from Java files.
 *
 * Sometimes spoon outputs classes with package annotations, which, according to
 * the compiler, are not allowed.
 */
/*
  TODO: The package-info.java files that Spoon outputs, lack the
  namespace prefix information placed there by the jaxb2-namespace-prefix
  plugin. We deal with this in the pom file by keeping the original
  package-info.java files instead of those output by Spoon. Is this a bug in
  Spoon? Is there a better way of dealing with this problem?
*/
public class RemoveMisplacedPackageAnnotations extends AbstractProcessor<CtClass<?>> {

    public static final Class<?>[] affectedClasses = {
        AbstractCRSType.class,
        BasePossibleProblemSourcesType.class,
    };

    @Override
    public boolean isToBeProcessed(CtClass<?> element) {
        return Arrays.asList(affectedClasses).contains(element.getActualClass());
    }

    /**
     * Given a class, remove all annotations from its package.
     */
    public void process(CtClass<?> element) {
        CtPackage p = element.getPackage();
        List<CtAnnotation<?>> as = p.getAnnotations();
        int n = as.size();
        for (int i = 0; i < n; i++) {
            p.removeAnnotation(as.get(0));
        }
    }
}
