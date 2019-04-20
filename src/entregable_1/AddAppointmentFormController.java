/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import DBAccess.ClinicDBAccess;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateTimeStringConverter;
import model.Appointment;
import model.Days;
import model.Doctor;
import model.Patient;
import utils.SlotAppointmentsWeek;
import utils.SlotWeek;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class AddAppointmentFormController implements Initializable {

    @FXML
    private Button createAppointment;
    @FXML
    private Button cancelAppointmentCreation;
    private TextField patient;
    
    private ClinicDBAccess db;
    private ObservableList<Patient> patientsList;
    private ObservableList<Doctor> doctorsList;
    private ObservableList<Appointment> appointmentsList;
    @FXML
    private ComboBox<Doctor> doctorSelector;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ChoiceBox<LocalTime> timePicker;
    @FXML
    private ComboBox<Patient> patientSelector;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = ClinicDBAccess.getSingletonClinicDBAccess();
        patientsList = FXCollections.observableList(db.getPatients());
        doctorsList = FXCollections.observableList(db.getDoctors());
        
        cancelAppointmentCreation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancelar creación de cita");
                alert.setHeaderText("Importante:");
                alert.setContentText("Si cancela en este momento, "
                        + "toda la información aportada en el formualrio "
                        + "se perderá");
                Optional<ButtonType> result = alert.showAndWait();
                
                if(result.isPresent() && result.get() == ButtonType.OK) {
                    Stage stage = (Stage) cancelAppointmentCreation.getScene()
                            .getWindow();
                    stage.close();
                }
            }
        });
        initPatientSelection(patientsList);
        initDoctorSelection(doctorsList);
                
        createAppointment.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Doctor doctor = doctorSelector.getValue();
                Patient patient = patientSelector.getValue();
                LocalDate date = datePicker.getValue();
                LocalTime time = timePicker.getValue();
                
                LocalDateTime dateTime = LocalDateTime.of(date, time);
                
                Appointment appointment = new Appointment(dateTime, doctor, 
                        patient);
                
                appointmentsList.add(appointment);
                
                Stage stage = (Stage) createAppointment.getScene().getWindow();
                stage.close();
            }
        });
        
    }    
    
    public void initAppointmentList(ObservableList<Appointment> list) {
        appointmentsList = list;
    }
    
    private void initPatientSelection(ObservableList<Patient> list) {
        patientSelector.setItems(list);
       
        
        patientSelector.setTooltip(new Tooltip("Escriba el nombre o ID del"
                + " paciente y presione Intro para busqueda rápida"));
        
        patientSelector.setConverter(new StringConverter<Patient>() {
            @Override
            public String toString(Patient object) {
                if(object != null) {
                    return object.getSurname() + ", " + object.getName() 
                            + " " + object.getIdentifier();
                }
                return "";
            }

            @Override
            public Patient fromString(String string) {
                return patientSelector.getItems().stream().filter(patient -> {
                    String input = string.replace(",", "");
                    input = input.replace(" ", "").toLowerCase();
    
                    String idValue = patient.getIdentifier().toLowerCase()
                            .replace(" ", "");
                    String nameValue = patient.getName().toLowerCase()
                            .replace(" ", "");
                    String surnameValue = patient.getSurname().toLowerCase()
                            .replace(" ", "");

                    return input.contains(idValue) || input.contains(nameValue) 
                            || input.contains(surnameValue);
                }).findFirst()
                .orElse(null);
            }
        });
    }
    
    private void initDoctorSelection(ObservableList<Doctor> list) {
        doctorSelector.setItems(list);
        doctorSelector.setTooltip(new Tooltip("Escriba el nombre o ID del"
                + " doctor y presione Intro para busqueda rápida"));
        
        doctorSelector.setConverter(new StringConverter<Doctor>() {
            @Override
            public String toString(Doctor object) {
                if(object != null) {
                    return object.getSurname() + ", " + object.getName() 
                            + " " + object.getIdentifier();
                }
                return "";
            }

            @Override
            public Doctor fromString(String string) {
                return doctorSelector.getItems().stream().filter(doctor -> {
                    String input = string.replace(",", "");
                    input = input.replace(" ", "").toLowerCase();
    
                    String idValue = doctor.getIdentifier().toLowerCase()
                            .replace(" ", "");
                    String nameValue = doctor.getName().toLowerCase()
                            .replace(" ", "");
                    String surnameValue = doctor.getSurname().toLowerCase()
                            .replace(" ", "");

                    return input.contains(idValue) || input.contains(nameValue) 
                            || input.contains(surnameValue);
                }).findFirst()
                .orElse(null);
            }
        });
    
        doctorSelector.valueProperty().addListener((obs, oldValue, newValue) -> 
        {
            Doctor doctor = newValue;
            initDateSelector(doctor);
            datePicker.setDisable(false);
        });
    }
    
    private void initDateSelector(Doctor doctor) {
               
        ArrayList<Days> visitDays = doctor.getVisitDays();
        LocalTime startTime = doctor.getVisitStartTime();
        LocalTime endTime = doctor.getVisitEndTime();
        ArrayList<Appointment> appointments = db.getAppointments();
        ArrayList<Appointment> doctorAppointments = new ArrayList<>();
        
        appointments.stream().filter((appointment) -> (appointment.getDoctor()
                .getIdentifier().equals(doctor.getIdentifier())))
                .forEachOrdered((appointment) -> {
            doctorAppointments.add(appointment);
        });
        
        datePicker.setDayCellFactory((final DatePicker datePicker1) -> 
            new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    LocalDate now = LocalDate.now();
                    if(now.compareTo(item) < 0) {
                        WeekFields weekFields = WeekFields.ISO;
                        int dayOfWeek = item.getDayOfWeek().getValue();
                        int weekNumber = item.get(weekFields.weekBasedYear());
                        ArrayList<SlotWeek> appointmentsWeek = 
                                SlotAppointmentsWeek.getAppointmentsWeek(
                                        weekNumber, visitDays, startTime, 
                                        endTime, doctorAppointments);
                        boolean isAvailable = false;

                        switch(dayOfWeek){
                            case(1):
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getMondayAvailability()
                                            .equals("Free");
                                }
                                break;
                            case(2):
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getTuesdayAvailability()
                                            .equals("Free");
                                }
                                break;
                            case(3):
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getWednesdayAvailability()
                                            .equals("Free");
                                }
                                break;
                            case(4):
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getThursdayAvailability()
                                            .equals("Free");
                                }
                                break;  
                            case(5):
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getFridayAvailability()
                                            .equals("Free");
                                }
                                break;
                            case(6):
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getSaturdayAvailability()
                                            .equals("Free");
                                }
                                break;
                            default:
                                for(SlotWeek slot : appointmentsWeek) {
                                    isAvailable = isAvailable || 
                                            slot.getSundayAvailability().equals("Free");
                                }
                                break;
                        }

                        setDisable(!isAvailable);
                    } else {
                        setDisable(true);
                    }
                }
            }
        );
        
        datePicker.valueProperty().addListener((obs, oldValue, newValue) -> {
            initHourSelector(doctor, newValue);
            timePicker.setDisable(false);
        });
    }
    
    private void initHourSelector(Doctor doctor, LocalDate date) {
        ArrayList<Days> visitDays = doctor.getVisitDays();
        LocalTime startTime = doctor.getVisitStartTime();
        LocalTime endTime = doctor.getVisitEndTime();
        ArrayList<Appointment> appointments = db.getAppointments();
        ArrayList<Appointment> doctorAppointments = new ArrayList<>();
        
        appointments.stream().filter((appointment) -> (appointment.getDoctor()
                .getIdentifier().equals(doctor.getIdentifier())))
                .forEachOrdered((appointment) -> {
            doctorAppointments.add(appointment);
        });
        
        WeekFields weekFields = WeekFields.ISO;
        int dayOfWeek = date.getDayOfWeek().getValue();
        int weekNumber = date.get(weekFields.weekBasedYear());
        ArrayList<SlotWeek> appointmentsWeek = 
            SlotAppointmentsWeek.getAppointmentsWeek(
                    weekNumber, visitDays, startTime, 
                    endTime, doctorAppointments);
        ArrayList<LocalTime> availableHours = new ArrayList<>();
        
        switch(dayOfWeek){
            case(1):
                for(SlotWeek slot : appointmentsWeek) {
                    if(slot.getMondayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;
            case(2):
                for(SlotWeek slot : appointmentsWeek) {
                    if(slot.getTuesdayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;
            case(3):
                for(SlotWeek slot : appointmentsWeek) {
                   if(slot.getWednesdayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;
            case(4):
                for(SlotWeek slot : appointmentsWeek) {
                    if(slot.getThursdayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;  
            case(5):
                for(SlotWeek slot : appointmentsWeek) {
                    if(slot.getFridayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;
            case(6):
                for(SlotWeek slot : appointmentsWeek) {
                    if(slot.getSaturdayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;
            default:
                for(SlotWeek slot : appointmentsWeek) {
                    if(slot.getSundayAvailability().equals("Free")) {
                        availableHours.add(slot.getSlot());
                    }
                }
                break;
        }
        
        timePicker.setItems(FXCollections.observableList(availableHours));
        timePicker.setValue(availableHours.get(0));
        timePicker.setConverter(new StringConverter<LocalTime>() {
            @Override
            public String toString(LocalTime object) {
                return object.format(DateTimeFormatter.ISO_TIME);
            }

            @Override
            public LocalTime fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
        createAppointment.setDisable(false);
    }
}
