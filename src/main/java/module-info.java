module com.ticketmachine.ticketmachine {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.annotation;
//    requires jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.desktop;
    requires java.sql;
    requires com.fasterxml.jackson.databind;
    requires jdk.crypto.ec;

    opens com.ticketmachine.ticketmachine to javafx.fxml;
    exports com.ticketmachine.ticketmachine;
}