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
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import utils.IdGenerate;
import utils.RandomNumbers;

/**
 * Controller do emulador de sensores.
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
    private final String IP_ADDRESS = "6.tcp.ngrok.io";
    private final int PORT = 15091;
    private final int SLEEP = 5000;

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
            Socket conn = new Socket(IP_ADDRESS, PORT);

            SensorsClient.sendToServer(
                    "POST",
                    "patients/create/",
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

        /* Thread responsável por enviar os valores dos sensores de tempos em
        tempos.*/
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable dispatcher = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket conn = new Socket(IP_ADDRESS, PORT);

                            SensorsClient.sendToServer(
                                    "PUT",
                                    "patients/edit/",
                                    conn,
                                    txtName.getText(),
                                    Float.parseFloat(
                                            txtBodyTemperature.getText()
                                    ),
                                    Integer.parseInt(
                                            txtRespiratoryFrequency.getText()
                                    ),
                                    Float.parseFloat(
                                            txtBloodOxygenation.getText()
                                    ),
                                    Integer.parseInt(
                                            txtBloodPressure.getText()
                                    ),
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
                        Thread.sleep(SLEEP);
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
     * Define os valores iniciais dos sensores de maneira aleatória.
     */
    public void setInitialValues() {
        float bodyTemperature
                = RandomNumbers.generateFloat((float) 35.99, (float) 43); // °C
        int respiratoryFrequency
                = RandomNumbers.generateInt(8, 36); // movimentos/minuto
        float bloodOxygenation
                = RandomNumbers.generateFloat((float) 0, (float) 100.1); // %
        int bloodPressure = RandomNumbers.generateInt(70, 130); // mmHg
        int heartRate = RandomNumbers.generateInt(51, 136); // batimentos/minuto

        /* Colocando os valores nos campos. */
        txtBodyTemperature.setText(
                String.format("%.1f", bodyTemperature).replace(",", ".")
        );
        txtRespiratoryFrequency.setText(String.valueOf(respiratoryFrequency));
        txtBloodOxygenation.setText(
                String.format("%.1f", bloodOxygenation).replace(",", ".")
        );
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

            txtBodyTemperature.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
        });

        /* Diminui a temperatura corporal em 0.1 a cada clique. */
        btnMinusBodyTemperature.setOnMouseClicked((MouseEvent e) -> {
            float value = Float.parseFloat(txtBodyTemperature.getText());
            value -= BODY_TEMPERATURE_VALUE;

            value = (value < 0) ? 0 : value;

            txtBodyTemperature.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
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

            txtBloodOxygenation.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
        });

        /* Diminui a oxigenação do sangue em 0.5 a cada clique. */
        btnMinusBloodOxygenation.setOnMouseClicked((MouseEvent e) -> {
            float value = Float.parseFloat(txtBloodOxygenation.getText());
            value -= BLOOD_OXIGENATION_VALUE;

            value = (value < 0) ? 0 : value;

            txtBloodOxygenation.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
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
