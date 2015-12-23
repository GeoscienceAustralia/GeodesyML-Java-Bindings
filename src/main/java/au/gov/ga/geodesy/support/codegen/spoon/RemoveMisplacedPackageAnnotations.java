package au.gov.ga.geodesy.support.codegen.spoon;

import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtPackage;

import au.gov.ga.geodesy.interfaces.geodesyml.dto.igssitelog.BasePossibleProblemSourcesType;

/**
 * In case of <code>BasePossibleProblemSourceType</code>, Spoon outputs a Java
 * file with a package annotation, which is not allowed according the compiler.
 */
/*
  TODO: The package-info.java files that Spoon outputs, lack the
  namespace prefix information placed there by the jaxb2-namespace-prefix
  plugin. We deal with this in the pom file by keeping the original
  package-info.java files instead of those output by Spoon. Is this a bug in
  Spoon? Is there a better way of dealing with this problem?
*/
public class RemoveMisplacedPackageAnnotations extends AbstractProcessor<CtClass<?>> {

    @Override
    public boolean isToBeProcessed(CtClass<?> element) {
        return element.getActualClass().equals(BasePossibleProblemSourcesType.class);
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
