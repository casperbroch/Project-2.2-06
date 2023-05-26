module group6 {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.api.client;
    requires com.google.api.client.auth;
    requires com.google.api.services.calendar;
    requires com.google.api.client.extensions.java6.auth;
    requires com.google.api.client.extensions.jetty.auth;
    requires com.google.api.client.http.apache.v2;
    requires com.google.api.client.json.gson;
    requires com.google.common;
    requires com.google.errorprone.annotations;

    opens group6 to javafx.fxml;
    exports group6;
}
