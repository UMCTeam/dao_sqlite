package com.chendaole.dao_sqlite_annotations_processor.beanclass;

import com.chendaole.dao_sqlite_annotations_processor.property.DaoName;
import com.chendaole.dao_sqlite_annotations_processor.property.DaoType;
import com.squareup.javapoet.TypeName;

import java.lang.annotation.Annotation;

import javax.lang.model.element.VariableElement;

public class FieldClass {
    private VariableElement variableElement;

    public FieldClass(VariableElement variableElement) {
        this.variableElement = variableElement;
    }

    public VariableElement getVariableElement() {
        return variableElement;
    }

    public DaoName getName() {
        return DaoName.valueOf(variableElement);
    }

    public DaoType getType() {
        return DaoType.valueOf(variableElement.asType());
    }

    public <A extends Annotation> A getAnnotation(Class<A> aClass) {
        return variableElement.getAnnotation(aClass);
    }

    public TypeName getTypeName() {
        return TypeName.get(variableElement.asType());
    }
}
