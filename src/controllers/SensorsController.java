package controllers;

import clients.SensorsClient;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import utils.IdGenerate;
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

    private static final float BODY_TEMPERATURE_VALUE = (float) 0.1;
    private static final float BLOOD_OXIGENATION_VALUE = (float) 0.5;
    private static final int FIELDS_VALUE = 1;

    public static String deviceId = new IdGenerate(12, ":").generate("XX.XX");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /* Preenche os campos com valores aleatórios. */
        setInitialValues();

        /* Habilita os botões */
        enableButtons();

        /* Verifica os valores digitados nos campos */
        verifyIntInputs();
        verifyFloatInputs();
        verifyNameInput();

        try {
            Socket conn = new Socket("localhost", 12244);

            SensorsClient.sendInitialValues(
                    conn,
                    txtName.getText(),
                    Float.parseFloat(txtBodyTemperature.getText()),
                    Integer.parseInt(txtRespiratoryFrequency.getText()),
                    Float.parseFloat(txtBloodOxygenation.getText()),
                    Integer.parseInt(txtBloodPressure.getText()),
                    Integer.parseInt(txtHeartRate.getText()),
                    deviceId
            );

            conn.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao tentar iniciar o Client de emulação de "
                    + "sensores");
            System.out.println(ioe);
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable dispatcher = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket conn = new Socket("localhost", 12244);

                            SensorsClient.updateSensorsValues(
                                    conn,
                                    txtName.getText(),
                                    Float.parseFloat(txtBodyTemperature.getText()),
                                    Integer.parseInt(txtRespiratoryFrequency.getText()),
                                    Float.parseFloat(txtBloodOxygenation.getText()),
                                    Integer.parseInt(txtBloodPressure.getText()),
                                    Integer.parseInt(txtHeartRate.getText()),
                                    deviceId
                            );

                            conn.close();
                        } catch (UnknownHostException uhe) {
                            System.err.println("Servidor não encontrado ou "
                                    + "está fora do ar.");
                            System.out.println(uhe);
                        } catch (IOException ioe) {
                            System.err.println("Erro ao tentar alterar os "
                                    + "valores dos sensores.");
                            System.out.println(ioe);
                        }
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ie) {
                        System.err.println("Não foi possível parar a Thread");
                        System.out.println(ie);
                    }

                    /* Atualizar as informações na Thread principal. */
                    Platform.runLater(dispatcher);
                }
            }

        });

        /* Finalizar a thread de requisição quando fechar o programa. */
        thread.setDaemon(true);
        /* Iniciar a thread de requisições. */
        thread.start();
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

            value = (value < 0) ? 0 : value;

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

            value = (value < 0) ? 0 : value;

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

            value = (value < 0) ? 0 : value;

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

            value = (value < 0) ? 0 : value;

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

            value = (value < 0) ? 0 : value;

            txtHeartRate.setText(String.valueOf(value));
        });
    }

    /**
     * Verifica se o valor inserido é um número inteiro.
     */
    public void verifyIntInputs() {
        txtRespiratoryFrequency.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\d*")) {
                    txtRespiratoryFrequency.setText(
                            newValue.replaceAll("[^\\d]", "")
                    );
                }
            }
        });

        txtBloodPressure.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\d*")) {
                    txtBloodPressure.setText(
                            newValue.replaceAll("[^\\d]", "")
                    );
                }
            }
        });

        txtHeartRate.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\d*")) {
                    txtHeartRate.setText(
                            newValue.replaceAll("[^\\d]", "")
                    );
                }
            }
        });
    }

    /**
     * Verifica se o valor inserido é um número de ponto flutuante.
     */
    public void verifyFloatInputs() {
        txtBodyTemperature.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\d*\\.")) {
                    txtBodyTemperature.setText(
                            newValue.replaceAll("[^\\d^\\.]", "")
                    );
                }
            }
        });

        txtBloodOxygenation.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\d*\\.")) {
                    txtBloodOxygenation.setText(
                            newValue.replaceAll("[^\\d^\\.]", "")
                    );
                }
            }
        });
    }

    /**
     * Verifica se o nome digitado é composto somente por letras.
     */
    private void verifyNameInput() {
        txtName.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\sa-zA-Z-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ*")) {
                    txtName.setText(
                            newValue.replaceAll("[^\\sa-zA-Z-záàâãéèêíïóôõöúçñÁÀÂÃÉÈÍÏÓÔÕÖÚÇÑ]", "")
                    );
                }
            }
        });
    }
}
