/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import DBAccess.ClinicDBAccess;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Appointment;
import model.Doctor;
import model.Patient;
import model.Person;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class MainScreenController implements Initializable {

    @FXML
    private Button patient_button;
    @FXML
    private Button doctor_button;
    @FXML
    private Button appointment_button;
    @FXML
    private Button add_button;
    @FXML
    private Button delete_button;
    @FXML
    private Button show_button;
    @FXML
    private Button exit_button;
    @FXML
    private ListView<String> info_view;
    
    
    private ClinicDBAccess db;
    private ObservableList<Patient> patientsObservableList;
    private ObservableList<Doctor> doctorsObservableList;
    private ObservableList<Appointment> appointmentsObservableList;
    private int tabCode;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = ClinicDBAccess.getSingletonClinicDBAccess();
        patientsObservableList = FXCollections.observableList(db.getPatients());
        doctorsObservableList = FXCollections.observableList(db.getDoctors());
        appointmentsObservableList = FXCollections.observableList(db.getAppointments());
       
        delete_button.disableProperty().bind(Bindings.equal(-1, info_view.getSelectionModel().selectedIndexProperty()));
        show_button.disableProperty().bind(Bindings.equal(-1, info_view.getSelectionModel().selectedIndexProperty())); 
        
        getPacientsList();
        
        add_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                addBttn(tabCode);
            }
        });
        
        delete_button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteBttn(tabCode);
            }
        });
    }
    
    @FXML
    public void getPacientsList() {
        tabCode = 0;
        info_view.setItems(getStringList(patientsObservableList));
    }
    @FXML
    public void getDoctorsList() {
        tabCode = 1;
        info_view.setItems(getStringList(doctorsObservableList));
    }
    @FXML
    public void getAppointmentsList() {
        tabCode = 2;
        info_view.setItems(getStringList(appointmentsObservableList));
    }
    
    public void addBttn(int code) {
        switch(code) {
            case 0:
                openAddPatient();
                break;
            case 1:
                
                break;
            case 2:
                
                break;
        }
    }
    
    public void deleteBttn(int code) {
        switch(code) {
            case 0:
                removePatient();
                break;
            case 1:
                removePatient();
                break;
            case 2:
                removePatient();
                break;
        }
    }
//    
//    public void viewBttn(int code) {
//    
//        switch(code) {
//            case 0:
//                openViewPatient();
//                break;
//            case 1:
//                openViewDoctor();
//                break;
//            case 2:
//                openViewAppointment();
//                break;
//        }
//    }
    
    private ObservableList<String> getStringList (ObservableList<?> list) {
        ArrayList<String> stringList = new ArrayList<String>();
        
        if(list.get(0) instanceof Person) {
            for(int i = 0; i < list.size(); i++) {
                Person person = (Person)list.get(i);
                String fullName = person.getSurname() + ", " + person.getName()+ "\t\t" + person.getIdentifier();
                stringList.add(fullName);
            }
        }
        else if(list.get(0) instanceof Appointment) {
            for(int i = 0; i < list.size(); i++) {
                Appointment appointment = (Appointment)list.get(i);
                String fullName = appointment.getAppointmentDateTime() + "\t\t" + appointment.getDoctor().getName() + "\t\t" + appointment.getPatient().getName();
                stringList.add(fullName);
            }
        }
        
        ObservableList<String> res = FXCollections.observableList(stringList);
        return res;
    }
    
    
    private void openAddPatient() {
        try{
            FXMLLoader miCargador = new FXMLLoader(getClass().getResource("addFrom.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            AddFromController addFormController = miCargador.<AddFromController>getController();
            addFormController.initPatientList(db, patientsObservableList);
            
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nuevo Paciente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void removePatient() {
        int index = info_view.getSelectionModel().getSelectedIndex();
        patientsObservableList.remove(index);
    }
}
