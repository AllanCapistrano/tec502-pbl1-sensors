/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 *
 * @author Allan Capistrano
 */
public class SensorsController implements Initializable {
    
    @FXML
    private TextField txtName;

    @FXML
    private TextField txtBodyTemperature;

    @FXML
    private Button btnPlusBodyTemperature;

    @FXML
    private Button btnMinusBodyTemperature;

    @FXML
    private TextField txtRespiratoryFrequency;

    @FXML
    private Button btnPlusRespiratoryFrequency;

    @FXML
    private Button btnMinusRespiratoryFrequency;

    @FXML
    private TextField txtBloodOxygenation;

    @FXML
    private Button btnPlusBloodOxygenation;

    @FXML
    private Button btnMinusBloodOxygenation;

    @FXML
    private TextField textBloodPressure;

    @FXML
    private Button btnPlusBloodPressure;

    @FXML
    private Button btnMinusBloodPressure;

    @FXML
    private TextField txtHeartRate;

    @FXML
    private Button btnPlusHeartRate;

    @FXML
    private Button btnMinusHeartRate;

    @FXML
    private Button btnUpdate;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnUpdate.setOnMouseClicked((MouseEvent e)->{
            System.out.println("Hello World!");
        });
    }    
    
}
