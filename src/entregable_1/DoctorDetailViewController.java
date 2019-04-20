/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
import model.Days;
import model.Doctor;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class DoctorDetailViewController implements Initializable {

    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label roomLabel;
    @FXML
    private Label StartTimeLabel;
    @FXML
    private Label EndTimeLabel;
    @FXML
    private Label Dayslabel;
    
    private Doctor selectedDoctor;
    @FXML
    private ImageView avatar;
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
    
    public void initSelectedDoctor(Doctor doctor) {
        selectedDoctor = doctor;
        setDoctorInfo();
    }
    
    
    private void setDoctorInfo() {
        nameLabel.setText(selectedDoctor.getName());
        surnameLabel.setText(selectedDoctor.getSurname());
        idLabel.setText(selectedDoctor.getIdentifier());
        phoneLabel.setText(selectedDoctor.getTelephon());
        roomLabel.setText(Integer.toString(selectedDoctor.
                getExaminationRoom().getIdentNumber()));
        StartTimeLabel.setText(selectedDoctor.getVisitStartTime() + "");
        EndTimeLabel.setText(selectedDoctor.getVisitEndTime() + "");

        StringBuilder visitDaysString = new StringBuilder();
        List<Days> visitDays = selectedDoctor.getVisitDays();
        for(Days visitDay : visitDays) {
            visitDaysString.append(visitDay);
        }
        Dayslabel.setText(visitDaysString.toString());
        Image image = selectedDoctor.getPhoto();
        avatar.setImage(image);
    }
    
}
