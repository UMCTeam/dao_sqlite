package com.chendaole.dao_sqlite_annotations_processor.beanclass;


import com.chendaole.dao_sqlite_annotations.Entity;
import com.chendaole.dao_sqlite_annotations.Id;
import com.chendaole.support.AlText;
import com.chendaole.dao_sqlite_annotations_processor.exception.DaoSqliteProcessorException;

import java.lang.annotation.Annotation;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

public class EntityClass {
    private TypeElement annotatedClassElement;
    private String singleName;
    private String tableName;
    private FieldClass idAnnotationFieldClass;
    private List<FieldClass> variables = new LinkedList<>();

    public EntityClass(TypeElement classElement) throws IllegalArgumentException, DaoSqliteProcessorException {
        this.annotatedClassElement = classElement;
        this.singleName = classElement.getSimpleName().toString();
        Entity annotation = classElement.getAnnotation(Entity.class);
        this.tableName = annotation.table();

        if (AlText.isEmpty(this.tableName)) {
            throw new IllegalArgumentException(
                    String.format("table() in @%s for class %s is null or empty! that's not allowed",
                            Entity.class.getSimpleName(), classElement.getQualifiedName().toString())
            );
        }

        List<? extends Element> elements = classElement.getEnclosedElements();
        for (Element element : elements) {
            if (element.getKind() == ElementKind.FIELD) {
                FieldClass fieldClass = new FieldClass((VariableElement) element);
                Annotation ann = element.getAnnotation(Id.class);
                if (ann != null) {
                    if (idAnnotationFieldClass != null) {
                        throw new DaoSqliteProcessorException(element,
                                "id() in @%s for class primary key repeats",
                                classElement.getSimpleName());
                    }
                    idAnnotationFieldClass = fieldClass;
                }
                variables.add(fieldClass);
            }
        }
    }

    public String getSingleName() {
        return this.singleName;
    }

    public String getTableName() {
        return tableName;
    }

    public TypeElement getTypeElement() {
        return this.annotatedClassElement;
    }

    public FieldClass getIdAnnotationFieldClass() {
        return idAnnotationFieldClass;
    }

    public List<FieldClass> getVariables() {
        return this.variables;
    }
}
