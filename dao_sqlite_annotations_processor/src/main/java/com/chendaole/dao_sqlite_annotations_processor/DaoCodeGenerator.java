package com.chendaole.dao_sqlite_annotations_processor;

import com.chendaole.dao_sqlite_annotations.Id;
import com.chendaole.support.AlText;
import com.chendaole.dao_sqlite_annotations_processor.beanclass.EntityClass;
import com.chendaole.dao_sqlite_annotations_processor.beanclass.FieldClass;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;
import javax.lang.model.util.Elements;

public class DaoCodeGenerator {
    //依赖对象
    private ClassName sSQLTraitClassName
            = ClassName.bestGuess("com.chendaole.dao_sqlite.SQLTrait");
    private ClassName sCursorClassName = ClassName.bestGuess("android.database.Cursor");
    private static ClassName sListKlass = ClassName.get(List.class);
    private static ClassName sArrayListKlass = ClassName.get(ArrayList.class);

    private EntityClass entityClass;
    private ClassName entityClassName;
    private TypeName listOfEntityTypeName;


    public DaoCodeGenerator(EntityClass entityClass) {
        this.entityClass = entityClass;
        this.entityClassName = ClassName.get(entityClass.getTypeElement());
        this.listOfEntityTypeName = ParameterizedTypeName.get(sListKlass, this.entityClassName);
    }

    private MethodSpec create() {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS " + this.entityClass.getTableName()
                + " (");
        Iterator<FieldClass> fieldClassIterator = this.entityClass.getVariables().iterator();
        if (!fieldClassIterator.hasNext()) {
            sql.append(")");
        } else {
            while (fieldClassIterator.hasNext()) {
                FieldClass fieldClass = fieldClassIterator.next();
                sql.append(fieldClass.getName().toLine() + " ");
                sql.append(fieldClass.getType().toSqliteType());

                if (fieldClass.getAnnotation(Id.class) != null) {
                    sql.append(" PRIMARY KEY");
                }

                if (fieldClassIterator.hasNext()) {
                    sql.append(",");
                } else {
                    sql.append(")");
                }
            }
        }

        return MethodSpec.methodBuilder("create")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addStatement(String.format("String sql =\"%s\"", sql.toString()))
                .addStatement("$T sqlTrait = $T.getInstance()",
                        sSQLTraitClassName,
                        sSQLTraitClassName)
                .addStatement("sqlTrait.execSQL(sql)")
                .build();
    }

    private MethodSpec addOne() {
        MethodSpec.Builder addOneBuilder = MethodSpec.methodBuilder("addOne")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(this.entityClassName, "entity")
                .addStatement("create()");


        StringBuffer insertSql = new StringBuffer();
        insertSql.append("\"INSERT INTO " + this.entityClass.getTableName() + "(");
        StringBuffer valueSql = new StringBuffer();
        valueSql.append(" VALUES(\" + ");

        Iterator<FieldClass> fieldClassIterator = this.entityClass.getVariables().iterator();
        while (fieldClassIterator.hasNext()) {
            FieldClass fieldClass = fieldClassIterator.next();
            insertSql.append(fieldClass.getName().toLine()+ " ");

            boolean varIsNumber = fieldClass.getType().isNumber();
            if (varIsNumber) {
                valueSql.append("entity." + fieldClass.getName().toHump());
            } else {
                valueSql.append("\"\\\"\" + entity." + fieldClass.getName().toHump() + "+ \"\\\"\"");
            }

            if (fieldClassIterator.hasNext()) {
                insertSql.append(",");
                valueSql.append(" + \",\" + ");
            } else {
                valueSql.append(" + ");
            }
        }

        insertSql.append(")");
        valueSql.append("\")\"");

        return addOneBuilder.addStatement(String.format("String sql = %s",
                insertSql.append(valueSql).toString()))
                .addStatement("$T sqlTrait = $T.getInstance()",
                        sSQLTraitClassName,
                        sSQLTraitClassName)
                .addStatement("sqlTrait.execSQL(sql)")
                .build();
    }

    private MethodSpec addAll() {
        return MethodSpec.methodBuilder("addAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(this.listOfEntityTypeName, "entities")
                .addStatement("$T<$T> iter = entities.iterator()",
                        ClassName.get(Iterator.class),
                        this.entityClassName)
                .beginControlFlow("while(iter.hasNext())")
                .addStatement("$T entity = iter.next()", this.entityClassName)
                .addStatement("addOne(entity)")
                .endControlFlow()
                .build();
    }

    private MethodSpec updateByCondition() {
        StringBuilder sql = new StringBuilder();
        sql.append("\"UPDATE " + this.entityClass.getTableName() + " SET \" ");

        Iterator<FieldClass> fieldClassIterator = this.entityClass.getVariables().iterator();
        while (fieldClassIterator.hasNext()) {
            FieldClass fieldClass = fieldClassIterator.next();
            sql.append( "+\"" + fieldClass.getName().toLine() + "= \"+");

            boolean varIsNumber = fieldClass.getType().isNumber();
            if (varIsNumber) {
                sql.append("entity." + fieldClass.getName().toHump());
            } else {
                sql.append("\"\\\"\" + entity." + fieldClass.getName().toHump() + "+ \"\\\"\"");
            }

            if (fieldClassIterator.hasNext()) {
                sql.append(" + \",\" ");
            } else {
                sql.append(" + \")\"");
            }
        }

        return MethodSpec.methodBuilder("updateByCondition")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(this.entityClassName, "entity")
                .addParameter(ClassName.get(String.class), "condition")
                .addStatement(String.format("String sql = %s",
                        sql.toString()))
                .addStatement("$T sqlTrait = $T.getInstance()",
                        sSQLTraitClassName,
                        sSQLTraitClassName)
                .addStatement("sqlTrait.execSQL(sql)")
                .build();
    }

    private MethodSpec updateById() {
        StringBuilder condition = new StringBuilder();
        FieldClass idAnnotationFieldClass = this.entityClass.getIdAnnotationFieldClass();
        condition.append( "\""+ idAnnotationFieldClass.getName().toLine() + "=");
        if (idAnnotationFieldClass.getType().isNumber()) {
            condition.append("\" + entity." + idAnnotationFieldClass.getName().toHump());
        } else {
            condition.append("\\\"\" + entity."
                    + idAnnotationFieldClass.getName().toHump()
                    + "+ \"\\\"\"");
        }
        return MethodSpec.methodBuilder("updateByCondition")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(this.entityClassName, "entity")
                .addStatement(String.format("updateByCondition(entity, %s)", condition.toString()))
                .build();
    }

    private MethodSpec deleteById() {
        FieldClass idAnnotationFieldClass = this.entityClass.getIdAnnotationFieldClass();
        boolean IdTypeNameIsNumber = idAnnotationFieldClass.getType().isNumber();
        String condition =  "\"" + idAnnotationFieldClass.getName().toLine() + "=\" + "
                + (IdTypeNameIsNumber ? "String.valueOf(id)" : "\"\\\"\" + id +  \"\\\"\"");
        return MethodSpec.methodBuilder("deleteById")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(idAnnotationFieldClass.getTypeName(), "id")
                .addStatement(String.format("deleteByCondition(%s)", condition))
                .build();
    }

    private MethodSpec deleteByCondition() {
        return MethodSpec.methodBuilder("deleteByCondition")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addParameter(ClassName.get(String.class), "condition")
                .addStatement(String.format("String sql =\"DELETE FROM %s \"", this.entityClass.getTableName()))
                .beginControlFlow("if(condition != null && condition.length() > 0)")
                .addStatement("sql += \"WHERE \" + condition")
                .endControlFlow()
                .addStatement("$T sqlTrait = $T.getInstance()",
                        sSQLTraitClassName,
                        sSQLTraitClassName)
                .addStatement("sqlTrait.execSQL(sql)")
                .addStatement("")
                .build();
    }

    private MethodSpec deleteAll() {
        return MethodSpec.methodBuilder("deleteAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                .addStatement("deleteByCondition(null)")
                .build();
    }


    private MethodSpec findById() {
        FieldClass idAnnotationFieldClass = this.entityClass.getIdAnnotationFieldClass();
        boolean IdTypeNameIsNumber = idAnnotationFieldClass.getType().isNumber();
        String condition =  "\"" + idAnnotationFieldClass.getName().toLine() + "=\" + "
                + (IdTypeNameIsNumber ? "String.valueOf(id)" : "\"\\\"\" + id +  \"\\\"\"");

        return MethodSpec.methodBuilder("findById")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(idAnnotationFieldClass.getTypeName(), "id")
                .returns(this.entityClassName)
                .addStatement("$T ret = " + String.format("findByCondition(%s)", condition),
                        this.listOfEntityTypeName)
                .addStatement("if(ret.size() <= 0) return null")
                .addStatement("return ret.get(0)")
                .build();
    }

    private MethodSpec findAll() {
        TypeName listOfEntityTypeName = ParameterizedTypeName.get(sListKlass, this.entityClassName);
        return MethodSpec.methodBuilder("findAll")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(listOfEntityTypeName)
                .addStatement("return findByCondition(null)")
                .build();
    }

    private MethodSpec findByCondition() {
        TypeName listOfEntityTypeName = ParameterizedTypeName.get(sListKlass, this.entityClassName);
        MethodSpec.Builder findBuilder = MethodSpec.methodBuilder("findByCondition")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addParameter(ClassName.get(String.class), "condition")
                .returns(listOfEntityTypeName)
                .addStatement("$T ret = new $T()",
                        listOfEntityTypeName,
                        ParameterizedTypeName.get(sArrayListKlass, this.entityClassName))
                .addStatement(String.format("String sql =\"SELECT * FROM %s\"", this.entityClass.getTableName()))
                .beginControlFlow("if (condition != null && condition.length() > 0)")
                .addStatement(String.format("sql +=\" WHERE \" + condition",
                        this.entityClass.getTableName()))
                .endControlFlow()
                .addStatement("$T sqlTrait = $T.getInstance()",
                        sSQLTraitClassName,
                        sSQLTraitClassName)
                .addStatement("$T cursor = sqlTrait.rawQuery(sql)",
                        sCursorClassName)
                .addStatement("cursor.moveToFirst()")
                .addStatement("if (cursor.isAfterLast()) return ret")
                .beginControlFlow("do")
                .addStatement("$T temp = new $T()", this.entityClassName, this.entityClassName);

        Iterator<FieldClass> fieldClassIterator = entityClass.getVariables().iterator();
        while (fieldClassIterator.hasNext()) {
            FieldClass fieldClass = fieldClassIterator.next();
            findBuilder.addStatement("temp.$N= cursor.$N(cursor.getColumnIndex($S))",
                    fieldClass.getName().toHump(),
                    generateJavaGetMethodName(fieldClass),
                    fieldClass.getName().toLine());
        }

        return findBuilder.addStatement("ret.add(temp)")
                .endControlFlow("while(cursor.moveToNext())")
                .addStatement("return ret")
                .build();
    }

    public void generate(Elements elements, Filer filer) {
        FieldSpec tableName = FieldSpec.builder(String.class, "tableName")
                .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                .initializer("$S", this.entityClass.getTableName())
                .build();
        //组装Dao类
        TypeSpec dao = TypeSpec.classBuilder(this.entityClass.getSingleName() + "Dao")
                .addModifiers(Modifier.PUBLIC)
                .addField(tableName)
                .addMethod(create())
                .addMethod(addOne())
                .addMethod(addAll())
                .addMethod(deleteById())
                .addMethod(deleteByCondition())
                .addMethod(deleteAll())
                .addMethod(findById())
                .addMethod(findAll())
                .addMethod(findByCondition())
                .addMethod(updateById())
                .addMethod(updateByCondition())
                .build();

        JavaFile javaFile = JavaFile.builder(this.entityClassName.packageName(), dao)
                .build();
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //构建get方法名称
    private static String generateJavaGetMethodName(FieldClass fieldClass) {
        String canonicalName = fieldClass.getType()
                .toJavaType();
        String javaGetMethodName = canonicalName;
        if (javaGetMethodName.contains(".")) {
            javaGetMethodName = canonicalName.substring(canonicalName.lastIndexOf(".") + 1);
        }
        return "get" + AlText.captureName(javaGetMethodName);
    }
}
