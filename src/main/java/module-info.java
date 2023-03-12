module com.danzabarr.messager {
    requires javafx.controls;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.web;
    requires mysql.connector.java;
    requires java.sql;
    requires jdk.jsobject;


    exports com.danzabarr.messager.core;
    exports com.danzabarr.messager.client;
    exports com.danzabarr.messager.server;
}