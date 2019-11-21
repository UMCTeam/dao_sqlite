package com.chendaole.dao_sqlite_annotations_processor.property;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;

public class DaoName {
    private static Pattern sLinePattern = Pattern.compile("_(\\w)");
    private String name;

    private DaoName(String name) {
        this.name = name;
    }

    public static DaoName valueOf(Element element) {
        return DaoName.valueOf(element.getSimpleName().toString());
    }

    public static DaoName valueOf(String name) {
        return new DaoName(name.replaceAll("[A-Z]", "_$0").toLowerCase());
    }

    public String toLine() {
        return this.name.replaceAll("[A-Z]", "_$0").toLowerCase();
    }

    public String toHump() {
        String str = this.name.toLowerCase();
        Matcher matcher = sLinePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
