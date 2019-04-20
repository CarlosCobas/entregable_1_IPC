/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import model.Appointment;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class AppointmentDetailViewController implements Initializable {

    @FXML
    private Label patientName;
    @FXML
    private Label patientID;
    @FXML
    private Label patientPhone;
    @FXML
    private Label date;
    @FXML
    private Label room;
    @FXML
    private Label doctorName;
    @FXML
    private Label doctorID;
    @FXML
    private Label doctorPhone;

    private Appointment selectedAppointment;
    @FXML
    private Label time;
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
    
    
    public void initAppointment(Appointment appointment) {
        selectedAppointment = appointment;
        setAppointmentInfo();
    }
    
    private void setAppointmentInfo() {
        patientName.setText(selectedAppointment.getPatient().getName() + " " +
                selectedAppointment.getPatient().getSurname());
        patientID.setText(selectedAppointment.getPatient().getIdentifier());
        patientPhone.setText(selectedAppointment.getPatient().getTelephon());
        
        doctorName.setText(selectedAppointment.getDoctor().getName() + " " +
                selectedAppointment.getDoctor().getSurname());
        doctorID.setText(selectedAppointment.getDoctor().getIdentifier());
        doctorPhone.setText(selectedAppointment.getDoctor().getTelephon());
        
        date.setText(selectedAppointment.getAppointmentDateTime()
                .format(DateTimeFormatter.ISO_DATE));
        time.setText(selectedAppointment.getAppointmentDateTime()
                .format(DateTimeFormatter.ISO_TIME));
        room.setText(Integer.toString(selectedAppointment.getDoctor()
                .getExaminationRoom().getIdentNumber()));
    }
}
