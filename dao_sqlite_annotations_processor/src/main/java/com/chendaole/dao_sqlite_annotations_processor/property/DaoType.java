package com.chendaole.dao_sqlite_annotations_processor.property;

import javax.lang.model.type.TypeMirror;

public class DaoType {
    public String canonicalName;

    public DaoType(String canonicalName) {
        this.canonicalName = canonicalName;
    }


    public static DaoType valueOf(TypeMirror typeMirror) {
        return new DaoType(typeMirror.toString());
    }

    public String toSqliteType() {
        if (canonicalName.equals(String.class.getCanonicalName())) return "TEXT";
        if (canonicalName.toLowerCase().matches("byte|short|int|long")) return "INTEGER";
        if ((canonicalName.toLowerCase().matches("double|float"))) return "REAL";
        return "NULL";
    }

    public String toJavaType() {
        return canonicalName;
    }

    public String getJavaSimpleName() {
        if (canonicalName.contains(".")) {
            return canonicalName.substring(canonicalName.lastIndexOf(".") + 1);
        }
        return canonicalName;
    }

    public boolean isNumber() {
        return canonicalName.toLowerCase().matches("byte|short|int|long")
                || (canonicalName.toLowerCase().matches("double|float"));
    }

    public boolean isString() {
        return canonicalName.equals(String.class.getCanonicalName());
    }

    public boolean isBlob() {
        //TODO:
        return false;
    }
}
