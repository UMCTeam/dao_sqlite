package com.chendaole.support;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public class Allog {
    private static Messager sMessager;

    public static void initialize(Messager messager) {
        synchronized (Allog.class) {
            if (messager != null) {
                sMessager = messager;
            }
        }
    }

    public static void error (Element e, String msg) {
        sMessager.printMessage(Diagnostic.Kind.ERROR, msg, e);
    }

    public static void note (Element e, String msg) {
        sMessager.printMessage(Diagnostic.Kind.NOTE, msg, e);
    }

    public static void note (String msg) {
        sMessager.printMessage(Diagnostic.Kind.NOTE, msg);
    }
}
