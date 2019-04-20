/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import DBAccess.ClinicDBAccess;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    private ListView<Patient> patients_info_view;
    @FXML
    private ListView<Doctor> doctors_info_view;
    @FXML
    private ListView<Appointment> appointments_info_view;

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
    @FXML
    private TextField patientSearchText;
    @FXML
    private Button patientSearchButton;
    @FXML
    private TextField doctorSearchText;
    @FXML
    private Button doctorSearchButton;
    @FXML
    private TextField appointmentSearchText;
    @FXML
    private Button appointmentSearchButton;
    @FXML
    private Button patientClearSearch;
    @FXML
    private Button doctorsClearSearch;
    @FXML
    private Button appointmentClearSearch;
    
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = ClinicDBAccess.getSingletonClinicDBAccess();
        patientsObservableList = FXCollections.observableList(db.getPatients());
        doctorsObservableList = FXCollections.observableList(db.getDoctors());
        appointmentsObservableList = FXCollections.
                observableList(db.getAppointments());
        
        initData();
        initButtons();    
    }    
    
    /** Init helper methods **/
    
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
        appointmentsObservableList.addListener( (ListChangeListener<Appointment>) 
                c -> {
            setAppointmentsList();
        });
    }
    
    private void initButtons() {
        //Patient List Buttons
        patients_delete.disableProperty().bind(Bindings.equal(-1, 
                patients_info_view.getSelectionModel()
                        .selectedIndexProperty()));
        patients_show.disableProperty().bind(Bindings.equal(-1, 
                patients_info_view.getSelectionModel()
                        .selectedIndexProperty()));
        patients_add.setOnAction((ActionEvent event) 
                -> { openAddPatient(); });
        patients_show.setOnAction((ActionEvent event) 
                -> { openPatientDetail(); });
        patients_delete.setOnAction((ActionEvent event) 
                -> { removePatient(); });
        patientSearchButton.setOnAction(((ActionEvent event) 
                -> { patientSearch(); }));
        patientClearSearch.setOnAction((ActionEvent event)
                -> { patientClearFilter(); });
        
        //Doctor List Buttons
        doctors_delete.disableProperty().bind(Bindings.equal(-1, 
                doctors_info_view.getSelectionModel()
                        .selectedIndexProperty()));
        doctors_show.disableProperty().bind(Bindings.equal(-1, 
                doctors_info_view.getSelectionModel()
                        .selectedIndexProperty()));
        doctors_add.setOnAction((ActionEvent event) 
                -> { openAddDoctor(); });
        doctors_show.setOnAction((ActionEvent event) 
                -> { openDoctorDetail(); });
        doctors_delete.setOnAction((ActionEvent event)
                -> { removeDoctor(); });
        doctorSearchButton.setOnAction((ActionEvent event)
                -> { doctorSearch(); });
        doctorsClearSearch.setOnAction((ActionEvent event)
                -> { doctorClearFilter(); });
        
        //Appointment List Buttons
        appointments_delete.disableProperty().bind(Bindings.equal(-1, 
                appointments_info_view.getSelectionModel()
                        .selectedIndexProperty()));
        appointments_show.disableProperty().bind(Bindings.equal(-1, 
                appointments_info_view.getSelectionModel()
                        .selectedIndexProperty()));
        appointments_show.setOnAction((ActionEvent event) 
                -> {openAppointmentDetail(); });
        appointments_add.setOnAction((ActionEvent event) 
                -> { openAddAppointment(); });
        appointments_delete.setOnAction((ActionEvent event) 
                -> { removeAppointment(); });
        appointmentSearchButton.setOnAction((ActionEvent event)
                -> { appointmentSearch(); });
        appointmentClearSearch.setOnAction((ActionEvent event)
                -> { appointmentClearFilter(); });
       
    }
    
    /** ListView creation methods **/
    
    public void setPacientsList() {
        patients_info_view.setItems(patientsObservableList);
        patients_info_view.setCellFactory((ListView<Patient> list) -> {
            ListCell<Patient> cell = new ListCell<Patient>(){
                @Override
                public void updateItem(Patient item, boolean empty) {
                    super.updateItem(item, empty);
                    setItem(item);
                    if(item != null) { 
                        StringBuilder displayText = new StringBuilder();
                        displayText.append(item.getIdentifier()).append("\t\t");
                        displayText.append(item.getSurname()).append(", ");
                        displayText.append(item.getName());
                        setText(displayText.toString());
                    }
                    else {
                        setText("");
                    }
                }
            };
            
            return cell;
        });
    }
    
    public void setDoctorsList() {
        doctors_info_view.setItems(doctorsObservableList);
        doctors_info_view.setCellFactory((ListView<Doctor> list) -> {
            ListCell<Doctor> cell = new ListCell<Doctor>(){
                @Override
                public void updateItem(Doctor item, boolean empty) {
                    super.updateItem(item, empty);
                    setItem(item);
                    if(item != null) { 
                        StringBuilder displayText = new StringBuilder();
                        displayText.append(item.getIdentifier()).append("\t\t");
                        displayText.append(item.getSurname()).append(", ");
                        displayText.append(item.getName());
                        setText(displayText.toString());
                    }
                    else {
                        setText("");
                    }
                }
            };
            
            return cell;
        });
    }
    
    public void setAppointmentsList() {
        appointments_info_view.setItems(appointmentsObservableList);
        appointments_info_view.setCellFactory((ListView<Appointment> list) -> {
            ListCell<Appointment> cell = new ListCell<Appointment>(){
                @Override
                public void updateItem(Appointment item, boolean empty) {
                    super.updateItem(item, empty);
                    setItem(item);
                    if(item != null) { 
                        StringBuilder displayText = new StringBuilder();
                        displayText.append("Paciente: ");
                        displayText.append(item.getPatient().getSurname());
                        displayText.append(", ");
                        displayText.append(item.getPatient().getName());
                        displayText.append("\t\t");

                        displayText.append("Doctor: ");
                        displayText.append(item.getDoctor().getSurname());
                        displayText.append(", ");
                        displayText.append(item.getDoctor().getName());
                        displayText.append("\t\t");
                        setText(displayText.toString());
                        
                        displayText.append("Fecha: ");
                        displayText.append(item.getAppointmentDateTime()
                                .format(DateTimeFormatter.ISO_DATE));
                        displayText.append(" ");
                        displayText.append(item.getAppointmentDateTime()
                                .format(DateTimeFormatter.ISO_TIME));
                        setText(displayText.toString());
                    } else {
                        setText("");
                    }
                }
            };
            
            return cell;
        });
    }
    
    /** Patient Methods **/
    
    private void openAddPatient() {
        try{
            FXMLLoader miCargador = new FXMLLoader(getClass().getResource("addPatientForm.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            AddPatientFormController addPatientFormController = miCargador.<AddPatientFormController>getController();
            addPatientFormController.initPatientList(patientsObservableList);
            
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

    private void openPatientDetail() {
        try{
            Patient patient = patients_info_view.getSelectionModel()
                    .getSelectedItem();
            
            FXMLLoader miCargador = new FXMLLoader(getClass().getResource("PatientDetailView.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            PatientDetailViewController patientDetailViewController = miCargador.<PatientDetailViewController>getController();
            patientDetailViewController.initSelectedPatient(patient);
            
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ficha Paciente");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void removePatient() {
        int index = patients_info_view.getSelectionModel()
                .getSelectedIndex();
        Patient patient = patients_info_view.getSelectionModel()
                .getSelectedItem();
        
        ArrayList<Appointment> appointments = new ArrayList<>();
        appointmentsObservableList.stream().filter((appointment) -> 
                (appointment.getPatient().getIdentifier()
                .equals(patient.getIdentifier()))).forEachOrdered((appointment) 
                    -> { appointments.add(appointment); });
        
        if(appointments.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al eliminar Paciente");
                alert.setContentText("No se pueden eliminar pacientes "
                        + "con citas pendientes");
                alert.showAndWait();
        } else {
            patientsObservableList.remove(index);
        } 
    }
    
    private void patientSearch() {
        
        FilteredList<Patient> filteredPatientList = 
                new FilteredList<>(patientsObservableList, p -> true);
        
        filteredPatientList.setPredicate(patient -> {
            if(patientSearchText.getText() == null || 
                    patientSearchText.getText().isEmpty()) {
               return true;
            }
            String query = patientSearchText.getText().toLowerCase();
            query = query.trim().replace(",", "").replace(" ","");

            String patientID = patient.getIdentifier()
                    .toLowerCase().replace(",", "").replace(" ","");
            String patientName = patient.getName()
                    .toLowerCase().replace(",", "").replace(" ","");
            String patientSurname = patient.getSurname()
                    .toLowerCase().replace(",", "").replace(" ","");

            return query.contains(patientID) 
                    || query.contains(patientName) 
                    || query.contains(patientSurname);
        });
        
        SortedList<Patient> sortedPatientList = 
                new SortedList<>(filteredPatientList);
        patients_info_view.setItems(sortedPatientList);
        patientClearSearch.setDisable(false);
    }
    
    private void patientClearFilter() {
        patients_info_view.setItems(patientsObservableList);
        patientClearSearch.setDisable(true);
    }
    
    /** Doctor Methods **/
    
    private void openAddDoctor() {
        try{
            FXMLLoader miCargador = new FXMLLoader(getClass().getResource("addDoctorForm.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            AddDoctorFormController addDoctorFormController = miCargador.<AddDoctorFormController>getController();
            addDoctorFormController.initDoctorList(doctorsObservableList);
            
            Scene scene = new Scene(root, 600, 600);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nuevo Doctor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void openDoctorDetail() {
         try{     
            Doctor doctor = doctors_info_view.getSelectionModel()
                    .getSelectedItem();
            
            FXMLLoader miCargador = new FXMLLoader(getClass().
                    getResource("DoctorDetailView.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            DoctorDetailViewController doctorDetailViewController = 
                    miCargador.<DoctorDetailViewController>getController();
            doctorDetailViewController.initSelectedDoctor(doctor);
            
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ficha Doctor");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void removeDoctor() {
        
        
        int index = doctors_info_view.getSelectionModel()
                .getSelectedIndex();
        Doctor doctor = doctors_info_view.getSelectionModel()
                .getSelectedItem();
        
        ArrayList<Appointment> appointments = new ArrayList<>();
        appointmentsObservableList.stream().filter((appointment) -> 
                (appointment.getDoctor().getIdentifier()
                .equals(doctor.getIdentifier()))).forEachOrdered((appointment) 
                    -> { appointments.add(appointment); });
        
        if(appointments.size() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al eliminar Doctor");
                alert.setContentText("No se pueden eliminar doctores "
                        + "con citas pendientes");
                alert.showAndWait();
        } else {
            doctorsObservableList.remove(index);
        } 
    }
    
    private void doctorSearch() {
        
        FilteredList<Doctor> filteredDoctorList = 
                new FilteredList<>(doctorsObservableList, p -> true);
        
        filteredDoctorList.setPredicate(doctor -> {
            if(doctorSearchText.getText() == null || 
                    doctorSearchText.getText().isEmpty()) {
               return true;
            }
            String query = doctorSearchText.getText().toLowerCase();
            query = query.trim().replace(",", "").replace(" ","");

            String doctorID = doctor.getIdentifier()
                    .toLowerCase().replace(",", "").replace(" ","");
            String doctorName = doctor.getName()
                    .toLowerCase().replace(",", "").replace(" ","");
            String doctorSurname = doctor.getSurname()
                    .toLowerCase().replace(",", "").replace(" ","");

            return query.contains(doctorID) 
                    || query.contains(doctorName) 
                    || query.contains(doctorSurname);
        });
        
        SortedList<Doctor> sortedDoctorList = 
                new SortedList<>(filteredDoctorList);
        doctors_info_view.setItems(sortedDoctorList);
        doctorsClearSearch.setDisable(false);
    }
    
    private void doctorClearFilter() {
        doctors_info_view.setItems(doctorsObservableList);
        doctorsClearSearch.setDisable(true);
    }

    /** Appointment Methods **/
    
    private void openAddAppointment() {
        try{
            FXMLLoader miCargador = new FXMLLoader(getClass()
                    .getResource("addAppointmentForm.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            AddAppointmentFormController addAppointmentFormController = miCargador.
                    <AddAppointmentFormController>getController();
            addAppointmentFormController.initAppointmentList(
                    appointmentsObservableList);
            
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Nueva Cita");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void openAppointmentDetail() {
        try{     
            Appointment appointment = appointments_info_view.getSelectionModel()
                    .getSelectedItem();
            
            FXMLLoader miCargador = new FXMLLoader(getClass().
                    getResource("AppointmentDetailView.fxml"));
            BorderPane root = (BorderPane) miCargador.load();
            
            AppointmentDetailViewController appointmentDetailViewController = 
                    miCargador.<AppointmentDetailViewController>getController();
            appointmentDetailViewController.initAppointment(appointment);
            
            Scene scene = new Scene(root, 600, 400);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Ficha Cita");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
    private void removeAppointment() {
        int index = appointments_info_view.getSelectionModel()
                .getSelectedIndex();
        LocalDateTime now = LocalDateTime.now();
        Appointment appointment = appointments_info_view.getSelectionModel()
                .getSelectedItem();
        
        if(now.compareTo(appointment.getAppointmentDateTime()) > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error al eliminar Cita");
                alert.setContentText("No se pueden eliminar citas "
                        + "que ya han pasado");
                alert.showAndWait();
        } else {
            appointmentsObservableList.remove(index);
        } 
    }
    
    private void appointmentSearch() {
         FilteredList<Appointment> filteredAppointmentList = 
                new FilteredList<>(appointmentsObservableList, p -> true);
        
        filteredAppointmentList.setPredicate(appointment -> {
            if(appointmentSearchText.getText() == null || 
                    appointmentSearchText.getText().isEmpty()) {
               return true;
            }
            String query = appointmentSearchText.getText().toLowerCase();
            query = query.trim().replace(",", "").replace(" ","");

            String doctorID = appointment.getDoctor().getIdentifier()
                    .toLowerCase().replace(",", "").replace(" ","");
            String doctorName = appointment.getDoctor().getName()
                    .toLowerCase().replace(",", "").replace(" ","");
            String doctorSurname = appointment.getDoctor().getSurname()
                    .toLowerCase().replace(",", "").replace(" ","");
            
            String patientID = appointment.getPatient().getIdentifier()
                    .toLowerCase().replace(",", "").replace(" ","");
            String patientName = appointment.getPatient().getName()
                    .toLowerCase().replace(",", "").replace(" ","");
            String patientSurname = appointment.getPatient().getSurname()
                    .toLowerCase().replace(",", "").replace(" ","");
            
            return query.contains(doctorID) 
                    || query.contains(doctorName) 
                    || query.contains(doctorSurname)
                    || query.contains(patientID) 
                    || query.contains(patientName) 
                    || query.contains(patientSurname);
        });
        
        SortedList<Appointment> sortedAppointmentList = 
                new SortedList<>(filteredAppointmentList);
        appointments_info_view.setItems(sortedAppointmentList);
        
        appointmentClearSearch.setDisable(true);
    }
    
    private void appointmentClearFilter() {
        appointments_info_view.setItems(appointmentsObservableList);
        appointmentClearSearch.setDisable(true);
    }
}
