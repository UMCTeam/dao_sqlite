package com.chendaole.dao_sqlite_annotations_processor.exception;

import javax.lang.model.element.Element;

public class DaoSqliteProcessorException extends Exception {
    private Element element;

    public DaoSqliteProcessorException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }
}
