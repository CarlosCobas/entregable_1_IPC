/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import DBAccess.ClinicDBAccess;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.Patient;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class AddFromController implements Initializable {

    private Button createPatient;
    @FXML
    private Button cancelPatientCreation;
    @FXML
    private TextField identifier;
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField phone;
    
    private ClinicDBAccess db;
    private ObservableList<Patient> patientsObservableList;
    @FXML
    private Button createDoctor;
    @FXML
    private TextField photo;
    @FXML
    private TextField visitDays;
    @FXML
    private TextField visitStartTime;
    @FXML
    private TextField visitEndTime;
    @FXML
    private TextField examinationRoom;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cancelPatientCreation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(AlertType.CONFIRMATION);
                alert.setTitle("Cancelar creación de usuario");
                alert.setHeaderText("Importante:");
                alert.setContentText("Si cancela en este momento, toda la información aportada en el formualrio se perderá");
                Optional<ButtonType> result = alert.showAndWait();
                
                if(result.isPresent() && result.get() == ButtonType.OK) {
                    Stage stage = (Stage) cancelPatientCreation.getScene().getWindow();
                    stage.close();
                }
            }
        });
        
        createPatient.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               createPatient();
            }
        });
        
    }    
    public void initPatientList(ClinicDBAccess database, ObservableList<Patient> list) {
        db = database;
        patientsObservableList = list;
    }
    
    private void createPatient(){
        String id = identifier.getText();
        String firstName = name.getText();
        String lastName = surname.getText();
        String phoneNum = phone.getText();
       
        if(id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || phoneNum.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Error de validación");
            alert.setContentText("No has rellenado un campo obligatorio");
            alert.showAndWait();
        }
        else {
            try {
                String url = System.getProperty("user.dir")+File.separator+"src"+File.separator+ "entregable_1" +File.separator+"imgs"+File.separator+"blank-profile-picture.png";
                Image avatar = new Image(new FileInputStream(url));
                Patient patient = new Patient(id, firstName, lastName, phoneNum, avatar);
                System.out.println(url);
                patientsObservableList.add(patient);
               
                Stage stage = (Stage) createPatient.getScene().getWindow();
                stage.close();
                
            } catch ( Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }
   
}
