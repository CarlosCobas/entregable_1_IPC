/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import DBAccess.ClinicDBAccess;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 *
 * @author Carlos Cobas
 */
public class Entregable_1 extends Application {
    private ClinicDBAccess db;
    
    @Override
    public void start(Stage stage) throws Exception {
        db = ClinicDBAccess.getSingletonClinicDBAccess();
        
        Parent root = FXMLLoader.load(getClass().getResource("mainScreen.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Base de Datos Clínica");
        stage.show();
         
        stage.setOnCloseRequest((WindowEvent event) ->{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(db.getClinicName());
            alert.setHeaderText("Saving data in DB");
            alert.setContentText("The application is saving the changes in the data into the database. This action can expend some minutes.");
            alert.show();
            db.saveDB();
       });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
