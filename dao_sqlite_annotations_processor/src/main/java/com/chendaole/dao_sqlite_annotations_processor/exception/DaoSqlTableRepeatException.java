package com.chendaole.dao_sqlite_annotations_processor.exception;

import javax.lang.model.element.Element;

public class DaoSqlTableRepeatException extends Exception {
    private Element element;
    public DaoSqlTableRepeatException(Element element, String msg, Object... args) {
        super(String.format(msg, args));
        this.element = element;
    }

    public Element getRepeatElement() {
        return this.element;
    }
}
