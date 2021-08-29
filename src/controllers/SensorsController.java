/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
    private TextField txtBloodPressure;

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
        btnUpdate.setOnMouseClicked((MouseEvent e) -> {
            if (hasEmptyFields()) {
                callAlert("Erro", "É necessário preencher todos os campos");
            } else {
                System.out.println("Hello World!");
            }
        });
    }

    /**
     * Verifica se todos os campos estão preenchidos.
     *
     * @return boolean
     */
    public boolean hasEmptyFields() {
        return txtName.getText().isEmpty()
                || txtBloodOxygenation.getText().isEmpty()
                || txtBodyTemperature.getText().isEmpty()
                || txtHeartRate.getText().isEmpty()
                || txtRespiratoryFrequency.getText().isEmpty()
                || txtBloodPressure.getText().isEmpty();
    }

    /**
     * Mostra uma mensagem de alerta na tela.
     * 
     * @param title String - Título do alerta.
     * @param text String - Mensagem que será exibida.
     */
    public void callAlert(String title, String text) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.show();
    }

}
