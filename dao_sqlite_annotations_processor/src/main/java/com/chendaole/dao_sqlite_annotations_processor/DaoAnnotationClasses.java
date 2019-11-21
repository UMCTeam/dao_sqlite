package com.chendaole.dao_sqlite_annotations_processor;

import com.chendaole.dao_sqlite_annotations_processor.beanclass.EntityClass;
import com.chendaole.dao_sqlite_annotations_processor.exception.DaoSqlTableRepeatException;
import com.squareup.javapoet.ClassName;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.util.Elements;

public class DaoAnnotationClasses {
    private Map<String, EntityClass> itemsMap =
            new LinkedHashMap<>();

    public DaoAnnotationClasses() {}

    public void add(EntityClass toInsert) throws DaoSqlTableRepeatException {
        EntityClass entityClass = itemsMap.get(toInsert.getTableName());
        if (entityClass != null) {
            throw new DaoSqlTableRepeatException(toInsert.getTypeElement(),
                    "@%s in class repeat class",
                    toInsert.getTypeElement().getSimpleName());
        }

        itemsMap.put(toInsert.getTableName(), toInsert);
    }

    public void clear() {
        itemsMap.clear();
    }

    public void generateCode(Elements elements, Filer filer) {
        ClassName listKlass = ClassName.get(List.class);
        for (Map.Entry<String, EntityClass> item : itemsMap.entrySet()) {
            new DaoCodeGenerator(item.getValue()).generate(elements, filer);
        }
    }
}
