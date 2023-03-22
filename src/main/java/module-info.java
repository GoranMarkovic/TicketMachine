module com.ticketmachine.ticketmachine {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.fasterxml.jackson.annotation;
//    requires jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    opens com.ticketmachine.ticketmachine to javafx.fxml;
    exports com.ticketmachine.ticketmachine;
}