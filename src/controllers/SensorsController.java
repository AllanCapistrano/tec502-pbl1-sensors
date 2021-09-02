package controllers;

import clients.SensorsClient;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import utils.RandomNumbers;

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

    private static final float BODY_TEMPERATURE_VALUE = (float) 0.1;
    private static final float BLOOD_OXIGENATION_VALUE = (float) 0.5;
    private static final int FIELDS_VALUE = 1;

    private static Socket connection; // Conexão para o envio dos dados iniciais.

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /* Preenche os campos com valores aleatórios. */
        setInitialValues();

        /* Habilita os botões */
        enableButtons();

        try {
            connection = SensorsClient.startDevice(
                    txtName.getText(),
                    Float.parseFloat(txtBodyTemperature.getText()),
                    Integer.parseInt(txtRespiratoryFrequency.getText()),
                    Float.parseFloat(txtBloodOxygenation.getText()),
                    Integer.parseInt(txtBloodPressure.getText()),
                    Integer.parseInt(txtHeartRate.getText())
            );
        } catch (IOException ex) {
            System.out.println("Erro ao tentar iniciar o Client de emulação de "
                    + "sensores");
        }

        btnUpdate.setOnMouseClicked((MouseEvent e) -> {
            /* Fecha a primeia conexão com o servidor, caso ainda esteja aberta */
            if (connection.isConnected()) {
                try {
                    connection.close();
                } catch (IOException ex) {
                    System.out.println("Erro ao tentar fechar a conexão com o"
                            + "servidor.");
                }
            }
            
            if (hasEmptyFields()) {
                callAlert("Erro", "É necessário preencher todos os campos", AlertType.ERROR);
            } else {
                try {
                    Socket updateConnection = SensorsClient.startConnection();

                    SensorsClient.updateSensorsValues(
                            updateConnection,
                            txtName.getText(),
                            Float.parseFloat(txtBodyTemperature.getText()),
                            Integer.parseInt(txtRespiratoryFrequency.getText()),
                            Float.parseFloat(txtBloodOxygenation.getText()),
                            Integer.parseInt(txtBloodPressure.getText()),
                            Integer.parseInt(txtHeartRate.getText())
                    );

                    updateConnection.close();
                    
                    /* Desabilita o campo de digitar o nome do paciente. */
                    if (!txtName.isDisabled()) {
                        txtName.setDisable(true);
                    }
                } catch (UnknownHostException uhe) {
                    System.out.println("Servidor não encontrado ou está fora do ar.");
                } catch (IOException ex) {
                    System.out.println("Erro ao tentar alterar os valores dos "
                            + "sensores.");
                }
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
     * @param alertType AlertType - Tipo do alerta que será enviado.
     */
    public void callAlert(String title, String text, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(text);
        alert.show();
    }

    /**
     * Define os valores iniciais dos sensores de maneira aleatória.
     */
    public void setInitialValues() {
        float bodyTemperature = RandomNumbers.generateFloat((float) 35.99, (float) 40); // °C
        int respiratoryFrequency = RandomNumbers.generateInt(8, 29); // movimentos/minuto
        float bloodOxygenation = RandomNumbers.generateFloat((float) 0, (float) 100); // %
        int bloodPressure = RandomNumbers.generateInt(70, 100); // mmHg
        int heartRate = RandomNumbers.generateInt(51, 129); // batimentos/minuto

        /* Colocando os valores nos campos. */
        txtBodyTemperature.setText(String.format("%.1f", bodyTemperature).replace(",", "."));
        txtRespiratoryFrequency.setText(String.valueOf(respiratoryFrequency));
        txtBloodOxygenation.setText(String.format("%.1f", bloodOxygenation).replace(",", "."));
        txtBloodPressure.setText(String.valueOf(bloodPressure));
        txtHeartRate.setText(String.valueOf(heartRate));
    }

    /**
     * Habilita os botões da interface gráfica.
     */
    public void enableButtons() {
        /* Aumenta a temperatura corporal em 0.1 a cada clique. */
        btnPlusBodyTemperature.setOnMouseClicked((MouseEvent e) -> {
            float value = Float.parseFloat(txtBodyTemperature.getText());
            value += BODY_TEMPERATURE_VALUE;

            txtBodyTemperature.setText(String.format("%.1f", value).replace(",", "."));
        });

        /* Diminui a temperatura corporal em 0.1 a cada clique. */
        btnMinusBodyTemperature.setOnMouseClicked((MouseEvent e) -> {
            float value = Float.parseFloat(txtBodyTemperature.getText());
            value -= BODY_TEMPERATURE_VALUE;

            txtBodyTemperature.setText(String.format("%.1f", value).replace(",", "."));
        });

        /* Aumenta a frequência respiratória em 1 a cada clique. */
        btnPlusRespiratoryFrequency.setOnMouseClicked((MouseEvent e) -> {
            int value = Integer.parseInt(txtRespiratoryFrequency.getText());
            value += FIELDS_VALUE;

            txtRespiratoryFrequency.setText(String.valueOf(value));
        });

        /* Diminui a frequência respiratória em 1 a cada clique. */
        btnMinusRespiratoryFrequency.setOnMouseClicked((MouseEvent e) -> {
            int value = Integer.parseInt(txtRespiratoryFrequency.getText());
            value -= FIELDS_VALUE;

            txtRespiratoryFrequency.setText(String.valueOf(value));
        });

        /* Aumenta a oxigenação do sangue em 0.5 a cada clique. */
        btnPlusBloodOxygenation.setOnMouseClicked((MouseEvent e) -> {
            float value = Float.parseFloat(txtBloodOxygenation.getText());
            value += BLOOD_OXIGENATION_VALUE;

            txtBloodOxygenation.setText(String.format("%.1f", value).replace(",", "."));
        });

        /* Diminui a oxigenação do sangue em 0.5 a cada clique. */
        btnMinusBloodOxygenation.setOnMouseClicked((MouseEvent e) -> {
            float value = Float.parseFloat(txtBloodOxygenation.getText());
            value -= BLOOD_OXIGENATION_VALUE;

            txtBloodOxygenation.setText(String.format("%.1f", value).replace(",", "."));
        });

        /* Aumenta a pressão arterial em 1 a cada clique. */
        btnPlusBloodPressure.setOnMouseClicked((MouseEvent e) -> {
            int value = Integer.parseInt(txtBloodPressure.getText());
            value += FIELDS_VALUE;

            txtBloodPressure.setText(String.valueOf(value));
        });

        /* Diminui a pressão arterial em 1 a cada clique. */
        btnMinusBloodPressure.setOnMouseClicked((MouseEvent e) -> {
            int value = Integer.parseInt(txtBloodPressure.getText());
            value -= FIELDS_VALUE;

            txtBloodPressure.setText(String.valueOf(value));
        });

        /* Aumenta a frequência cardíaca em 1 a cada clique. */
        btnPlusHeartRate.setOnMouseClicked((MouseEvent e) -> {
            int value = Integer.parseInt(txtHeartRate.getText());
            value += FIELDS_VALUE;

            txtHeartRate.setText(String.valueOf(value));
        });

        /* Diminui a frequência cardíaca em 1 a cada clique. */
        btnMinusHeartRate.setOnMouseClicked((MouseEvent e) -> {
            int value = Integer.parseInt(txtHeartRate.getText());
            value -= FIELDS_VALUE;

            txtHeartRate.setText(String.valueOf(value));
        });
    }

}
