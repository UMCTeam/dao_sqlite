package com.chendaole.dao_sqlite_annotations_processor;

import com.chendaole.dao_sqlite_annotations.Entity;
import com.chendaole.support.Allog;
import com.chendaole.dao_sqlite_annotations_processor.beanclass.EntityClass;
import com.chendaole.dao_sqlite_annotations_processor.exception.DaoSqlTableRepeatException;
import com.chendaole.dao_sqlite_annotations_processor.exception.DaoSqliteProcessorException;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class DaoSqliteAnnotationsProcessor extends AbstractProcessor {
    private Elements elements;
    private Filer filer;
    private DaoAnnotationClasses annotationClasses =
            new DaoAnnotationClasses();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elements = processingEnvironment.getElementUtils();
        filer = processingEnvironment.getFiler();
        Allog.initialize(processingEnvironment.getMessager());
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(Entity.class.getCanonicalName());
        return annotations;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        for (Element annotationElement : roundEnvironment.getElementsAnnotatedWith(Entity.class)) {
            if (annotationElement.getKind() == ElementKind.CLASS)  {
                TypeElement typeElement = (TypeElement) annotationElement;
                try {
                    EntityClass entityClass = new EntityClass(typeElement);
                    checkValidClass(entityClass);
                    annotationClasses.add(entityClass);
                } catch (IllegalArgumentException e) {
                    Allog.error(typeElement, e.getMessage());
                    return true;
                } catch (DaoSqliteProcessorException e) {
                    Allog.error(e.getElement(), e.getMessage());
                    return true;
                } catch (DaoSqlTableRepeatException e) {
                    Allog.error(e.getRepeatElement(), e.getMessage());
                    return true;
                }
            }
        }
        annotationClasses.generateCode(elements, filer);
        annotationClasses.clear();

        return true;
    }

    private boolean checkValidClass(EntityClass entityClass) throws DaoSqliteProcessorException {
        TypeElement typeElement = entityClass.getTypeElement();
        if (typeElement.getModifiers().contains(Modifier.ABSTRACT)) {
            throw new DaoSqliteProcessorException(typeElement,
                    "The class %s is abstract. You can't annotate abstract classes with @%",
                    typeElement.getQualifiedName().toString(), Entity.class.getSimpleName());
        }

        return true;
    }
}
