module com.ticketmachine.ticketmachene {
    requires javafx.controls;
    requires javafx.fxml;
    requires okhttp3;
    requires com.fasterxml.jackson.annotation;
    requires jackson.databind;
    requires com.fasterxml.jackson.core;
    requires java.desktop;
    requires java.sql;


    opens com.ticketmachine.ticketmachine to javafx.fxml;
    exports com.ticketmachine.ticketmachine;
}