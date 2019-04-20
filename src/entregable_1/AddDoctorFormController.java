/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entregable_1;

import DBAccess.ClinicDBAccess;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.Days;
import model.Doctor;
import model.ExaminationRoom;
import model.Patient;

/**
 * FXML Controller class
 *
 * @author carlo
 */
public class AddDoctorFormController implements Initializable {

    @FXML
    private Button createDoctor;
    @FXML
    private TextField identifier;
    @FXML
    private TextField name;
    @FXML
    private TextField surname;
    @FXML
    private TextField phone;
    @FXML
    private TextField photo;
    @FXML
    private Button cancelDoctorCreation;

    private ClinicDBAccess db;
    private ObservableList<Doctor> doctorsObservableList;
    @FXML
    private Spinner<Integer> startHourSpinner;
    @FXML
    private Spinner<Integer> startMinuteSpinner;
    @FXML
    private Spinner<Integer> finishHourSpinner;
    @FXML
    private Spinner<Integer> finishMinuteSpinner;
    
    
    private List<Integer> validHours;
    private List<Integer> validMinutes;
    private List<ExaminationRoom> examRooms;
    @FXML
    private ChoiceBox<ExaminationRoom> examRoomSelector;
    @FXML
    private CheckBox Monday;
    @FXML
    private CheckBox Tuesday;
    @FXML
    private CheckBox Wednesday;
    @FXML
    private CheckBox Thursday;
    @FXML
    private CheckBox Friday;
    @FXML
    private CheckBox Saturday;
    @FXML
    private CheckBox Sunday;
    @FXML
    private AnchorPane consult_days_holder;
    
     /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db = ClinicDBAccess.getSingletonClinicDBAccess();
        intTimeSpinners();
        initRoomSpinner();
        
        cancelDoctorCreation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancelar creación de doctor");
                alert.setHeaderText("Importante:");
                alert.setContentText("Si cancela en este momento, toda "
                        + "la información aportada en el formulario"
                        + " se perderá");
                Optional<ButtonType> result = alert.showAndWait();
                
                if(result.isPresent() && result.get() == ButtonType.OK) {
                    Stage stage = (Stage) cancelDoctorCreation.getScene()
                            .getWindow();
                    stage.close();
                }
            }
        });
        
        createDoctor.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
               createDoctor();
            }
        });
        
    }    
    public void initDoctorList(ObservableList<Doctor> list) {
        doctorsObservableList = list;
    }
    
    private void createDoctor(){
        String id = identifier.getText();
        String firstName = name.getText();
        String lastName = surname.getText();
        String phoneNum = phone.getText();
        
        int startHour = startHourSpinner.getValue();
        int startMinutes = startMinuteSpinner.getValue();
        
        int finishHour = finishHourSpinner.getValue();
        int finishMinutes = finishMinuteSpinner.getValue();

        LocalTime startTime = LocalTime.of(startHour, startMinutes);
        LocalTime finishTime = LocalTime.of(finishHour, finishMinutes);
        
        ArrayList<Days> days = getDoctorDays();
        
        ExaminationRoom selectedRoom = examRoomSelector.getValue();
        
        if(id.isEmpty() || firstName.isEmpty() || lastName.isEmpty() 
                || phoneNum.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error de validación");
            alert.setContentText("No has rellenado un campo obligatorio");
            alert.showAndWait();
        }
        else if(finishTime.compareTo(startTime) <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error de validación");
            alert.setContentText("La hora de fin de consulta no puede ser "
                    + "mayor que la hora de inicio");
            alert.showAndWait();
        }
        else {
            try {
                String url = System.getProperty("user.dir")+File.separator+"src"
                        +File.separator+ "entregable_1" +File.separator+
                        "imgs"+File.separator+"blank-profile-picture.png";
                Image avatar = new Image(new FileInputStream(url));
                Doctor doctor = new Doctor(selectedRoom,Days.Monday,startTime,
                        finishTime, id, firstName, lastName, phoneNum, avatar);
                doctor.setVisitDays(days);
                System.out.println(url);
                doctorsObservableList.add(doctor);
               
                Stage stage = (Stage) createDoctor.getScene().getWindow();
                stage.close();
                
            } catch ( Exception e) {
                System.out.print(e.getMessage());
            }
        }
    }
    
    
    private void initTimeLists() {
        validHours = new ArrayList<>();
        for(int i = 1; i < 24; i++){
            validHours.add(i);
        }
        validMinutes = new ArrayList<>();
        validMinutes.add(0);
        validMinutes.add(15);
        validMinutes.add(30);
        validMinutes.add(45);
    }
    
    private void intTimeSpinners() {
        initTimeLists(); 
        startHourSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(validHours)));
        finishHourSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(validHours)));
        startMinuteSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(validMinutes)));
        finishMinuteSpinner.setValueFactory(
                new SpinnerValueFactory.ListSpinnerValueFactory<>(
                        FXCollections.observableArrayList(validMinutes)));
    }
    
    private void initRoomSpinner() {
        examRooms = db.getExaminationRooms();
        examRoomSelector.setItems(FXCollections.observableArrayList(examRooms));
        examRoomSelector.setValue(examRooms.get(0));
        examRoomSelector.setConverter(new StringConverter<ExaminationRoom>() {
            @Override
            public String toString(ExaminationRoom object) {
                return object.getIdentNumber() + " " 
                        + object.getEquipmentDescription();
            }

            @Override
            public ExaminationRoom fromString(String string) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });
        examRoomSelector.setTooltip(
                new Tooltip("Selecciona la sala correspondiente a este doctor"));
    }
    
    private ArrayList<Days> getDoctorDays() {
        ArrayList<Days> res = new ArrayList();
        
        if(Monday.isSelected()) res.add(Days.Monday);
        if(Tuesday.isSelected()) res.add(Days.Tuesday);
        if(Wednesday.isSelected()) res.add(Days.Wednesday);
        if(Thursday.isSelected()) res.add(Days.Thursday);
        if(Friday.isSelected()) res.add(Days.Friday);
        if(Saturday.isSelected()) res.add(Days.Saturday);
        if(Sunday.isSelected()) res.add(Days.Sunday);
 
        return res;
    }
}
