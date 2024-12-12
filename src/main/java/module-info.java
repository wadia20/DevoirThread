module com.example.devoirthreads {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jackson.databind;


    opens com.example.devoirthreads to javafx.fxml;
    exports com.example.devoirthreads;
}