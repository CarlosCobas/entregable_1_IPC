/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Patient;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class PatientDetailViewController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private ImageView avatar;
    
    private Patient selectedPatient;
    @FXML
    private Button closeView;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        closeView.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Stage stage = (Stage) closeView.getScene().getWindow();
                stage.close();
            }
        });  
    }    
    
    public void initSelectedPatient(Patient patient) {
        selectedPatient = patient;
        setPatientInfo();
    }
    
    
    private void setPatientInfo() {
        nameLabel.setText(selectedPatient.getName());
        surnameLabel.setText(selectedPatient.getSurname());
        idLabel.setText(selectedPatient.getIdentifier());
        phoneLabel.setText(selectedPatient.getTelephon());
        Image image = selectedPatient.getPhoto();
        avatar.setImage(image);
    }
}
