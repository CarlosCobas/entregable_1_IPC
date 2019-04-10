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
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
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
    private Tab pacients_tab;
    @FXML
    private Tab doctors_tab;
    @FXML
    private Tab appointments_tab;
    
    @FXML
    private ListView<String> patients_info_view;
    @FXML
    private ListView<String> doctors_info_view;
    @FXML
    private ListView<String> appointments_info_view;

    @FXML
    private Button patients_add;
    @FXML
    private Button patients_delete;
    @FXML
    private Button patients_show;
    @FXML
    private Button doctors_add;
    @FXML
    private Button doctors_delete;
    @FXML
    private Button doctors_show;
    @FXML
    private Button appointments_add;
    @FXML
    private Button appointments_delete;
    @FXML
    private Button appointments_show;
    
    private ClinicDBAccess db;
    private ObservableList<Patient> patientsObservableList;
    private ObservableList<Doctor> doctorsObservableList;
    private ObservableList<Appointment> appointmentsObservableList;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = ClinicDBAccess.getSingletonClinicDBAccess();
        patientsObservableList = FXCollections.observableList(db.getPatients());
        doctorsObservableList = FXCollections.observableList(db.getDoctors());
        appointmentsObservableList = FXCollections.observableList(db.getAppointments());
        
        initData();
        initButtons();
        
       
    }    
    
    
    private void initData() {
        
        setPacientsList();
        setDoctorsList();
        setAppointmentsList();
        
        patientsObservableList.addListener( (ListChangeListener<Patient>) c -> {
            setPacientsList();
        });
        doctorsObservableList.addListener( (ListChangeListener<Doctor>) c -> {
            setDoctorsList();
        });
        appointmentsObservableList.addListener( (ListChangeListener<Appointment>) c -> {
            setAppointmentsList();
        });
    }
    
    private void initButtons() {
        //Patient List Buttons
        
        patients_delete.disableProperty().bind(Bindings.equal(-1, 
                patients_info_view.getSelectionModel().selectedIndexProperty()));
        patients_show.disableProperty().bind(Bindings.equal(-1, 
                patients_info_view.getSelectionModel().selectedIndexProperty()));
        patients_add.setOnAction((ActionEvent event) -> { openAddPatient(); });
        patients_delete.setOnAction((ActionEvent event) -> { removePatient(); });
        
        //Doctor List Buttons
        doctors_delete.disableProperty().bind(Bindings.equal(-1, 
                doctors_info_view.getSelectionModel().selectedIndexProperty()));
        doctors_show.disableProperty().bind(Bindings.equal(-1, 
                doctors_info_view.getSelectionModel().selectedIndexProperty()));
        
        
        //Appointment List Buttons
        appointments_delete.disableProperty().bind(Bindings.equal(-1, 
                appointments_info_view.getSelectionModel().selectedIndexProperty()));
        appointments_show.disableProperty().bind(Bindings.equal(-1, 
                appointments_info_view.getSelectionModel().selectedIndexProperty()));
        
        
    }
    
    public void setPacientsList() {
        patients_info_view.setItems(getStringList(patientsObservableList));
    }
    
    public void setDoctorsList() {
        doctors_info_view.setItems(getStringList(doctorsObservableList));
    }
    
    public void setAppointmentsList() {
        appointments_info_view.setItems(getStringList(appointmentsObservableList));
    }
    
    private ObservableList<String> getStringList (ObservableList<?> list) {
        ArrayList<String> stringList = new ArrayList<>();
        
        if(list.get(0) instanceof Person) {
            for(int i = 0; i < list.size(); i++) {
                Person person = (Person)list.get(i);
                String fullName = person.getSurname() + ", " + person.getName()
                        + "\t\t" + person.getIdentifier();
                stringList.add(fullName);
            }
        }
        else if(list.get(0) instanceof Appointment) {
            for(int i = 0; i < list.size(); i++) {
                Appointment appointment = (Appointment)list.get(i);
                String fullName = appointment.getAppointmentDateTime() 
                        + "\t\t" + appointment.getDoctor().getName() + "\t\t" 
                        + appointment.getPatient().getName();
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
        int index = patients_info_view.getSelectionModel().getSelectedIndex();
        System.out.println(index);
        patientsObservableList.remove(index);
        System.out.println(patientsObservableList.size());
    }
    
    
}
