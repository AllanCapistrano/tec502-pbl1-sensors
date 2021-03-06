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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    
    @FXML
    private ImageView imgCloud;
    
    private static final float BODY_TEMPERATURE_RANGE = (float) 0.1;
    private static final float BLOOD_OXIGENATION_RANGE = (float) 0.5;
    private static final int FIELDS_VALUE = 1;
    private final String IP_ADDRESS = "localhost";
    private final int PORT = 12244;
    private final int SLEEP = 5000;

    public static String deviceId = new IdGenerate(12, ":").generate("XX.XX");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        /* Preenche os campos com valores aleat??rios. */
        setInitialValues();

        /* Habilita os bot??es */
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
            
            /* Indica que est?? conectado com o servidor. */
            imgCloud.setImage(new Image("/images/cloud-check.png"));
            
            conn.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao tentar iniciar o Client de emula????o de "
                    + "sensores");
            System.out.println(ioe);
            
            /* Indica que n??o est?? conectado com o servidor. */
            imgCloud.setImage(new Image("/images/cloud-slash.png"));
        }

        /* Thread respons??vel por enviar os valores dos sensores de tempos em
        tempos.*/
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Runnable dispatcher = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket conn = new Socket(IP_ADDRESS, PORT);

                            /* Caso o campo esteja em vazio, enviar 0 para o 
                            servidor. */
                            float bodyTemperature
                                    = (txtBodyTemperature.getText().equals(""))
                                    ? 0
                                    : Float.parseFloat(txtBodyTemperature.getText());

                            int respiratporyFrequency
                                    = (txtRespiratoryFrequency.getText().equals(""))
                                    ? 0
                                    : Integer.parseInt(txtRespiratoryFrequency.getText());

                            float bloodOxygenation
                                    = (txtBloodOxygenation.getText().equals(""))
                                    ? 0
                                    : Float.parseFloat(txtBloodOxygenation.getText());

                            int bloodPressure
                                    = (txtBloodPressure.getText().equals(""))
                                    ? 0
                                    : Integer.parseInt(txtBloodPressure.getText());

                            int heartRate
                                    = (txtHeartRate.getText().equals(""))
                                    ? 0
                                    : Integer.parseInt(txtHeartRate.getText());

                            SensorsClient.sendToServer(
                                    "PUT",
                                    "patients/edit/",
                                    conn,
                                    txtName.getText(),
                                    bodyTemperature,
                                    respiratporyFrequency,
                                    bloodOxygenation,
                                    bloodPressure,
                                    heartRate,
                                    deviceId
                            );
                            
                            /* Indica que est?? conectado com o servidor. */
                            imgCloud.setImage(new Image("/images/cloud-check.png"));

                            conn.close();
                        } catch (UnknownHostException uhe) {
                            System.err.println("Servidor n??o encontrado ou "
                                    + "est?? fora do ar.");
                            System.out.println(uhe);
                            
                            /* Indica que n??o est?? conectado com o servidor. */
                            imgCloud.setImage(new Image("/images/cloud-slash.png"));
                        } catch (IOException ioe) {
                            System.err.println("Erro ao tentar alterar os "
                                    + "valores dos sensores.");
                            System.out.println(ioe);
                            
                            /* Indica que n??o est?? conectado com o servidor. */
                            imgCloud.setImage(new Image("/images/cloud-slash.png"));
                        }
                    }
                };

                while (true) {
                    try {
                        Thread.sleep(SLEEP);
                    } catch (InterruptedException ie) {
                        System.err.println("N??o foi poss??vel parar a Thread");
                        System.out.println(ie);
                    }

                    /* Atualizar as informa????es na Thread principal. */
                    Platform.runLater(dispatcher);
                }
            }

        });

        /* Finalizar a thread de requisi????o quando fechar o programa. */
        thread.setDaemon(true);
        /* Iniciar a thread de requisi????es. */
        thread.start();
    }

    /**
     * Define os valores iniciais dos sensores de maneira aleat??ria.
     */
    public void setInitialValues() {
        float bodyTemperature
                = RandomNumbers.generateFloat((float) 35.99, (float) 43); // ??C
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
     * Habilita os bot??es da interface gr??fica.
     */
    public void enableButtons() {
        /* Aumenta a temperatura corporal em 0.1 a cada clique. */
        btnPlusBodyTemperature.setOnMouseClicked((MouseEvent e) -> {
            float value = (txtBodyTemperature.getText().equals(""))
                    ? 0
                    : Float.parseFloat(txtBodyTemperature.getText());

            value += BODY_TEMPERATURE_RANGE;

            txtBodyTemperature.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
        });

        /* Diminui a temperatura corporal em 0.1 a cada clique. */
        btnMinusBodyTemperature.setOnMouseClicked((MouseEvent e) -> {
            float value = (txtBodyTemperature.getText().equals(""))
                    ? 0
                    : Float.parseFloat(txtBodyTemperature.getText());

            value -= BODY_TEMPERATURE_RANGE;
            value = (value < 0) ? 0 : value;

            txtBodyTemperature.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
        });

        /* Aumenta a frequ??ncia respirat??ria em 1 a cada clique. */
        btnPlusRespiratoryFrequency.setOnMouseClicked((MouseEvent e) -> {
            int value = (txtRespiratoryFrequency.getText().equals(""))
                    ? 0
                    : Integer.parseInt(txtRespiratoryFrequency.getText());

            value += FIELDS_VALUE;

            txtRespiratoryFrequency.setText(String.valueOf(value));
        });

        /* Diminui a frequ??ncia respirat??ria em 1 a cada clique. */
        btnMinusRespiratoryFrequency.setOnMouseClicked((MouseEvent e) -> {
            int value = (txtRespiratoryFrequency.getText().equals(""))
                    ? 0
                    : Integer.parseInt(txtRespiratoryFrequency.getText());

            value -= FIELDS_VALUE;
            value = (value < 0) ? 0 : value;

            txtRespiratoryFrequency.setText(String.valueOf(value));
        });

        /* Aumenta a oxigena????o do sangue em 0.5 a cada clique. */
        btnPlusBloodOxygenation.setOnMouseClicked((MouseEvent e) -> {
            float value = (txtBloodOxygenation.getText().equals(""))
                    ? 0
                    : Float.parseFloat(txtBloodOxygenation.getText());

            value += BLOOD_OXIGENATION_RANGE;

            txtBloodOxygenation.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
        });

        /* Diminui a oxigena????o do sangue em 0.5 a cada clique. */
        btnMinusBloodOxygenation.setOnMouseClicked((MouseEvent e) -> {
            float value = (txtBloodOxygenation.getText().equals(""))
                    ? 0
                    : Float.parseFloat(txtBloodOxygenation.getText());

            value -= BLOOD_OXIGENATION_RANGE;
            value = (value < 0) ? 0 : value;

            txtBloodOxygenation.setText(
                    String.format("%.1f", value).replace(",", ".")
            );
        });

        /* Aumenta a press??o arterial em 1 a cada clique. */
        btnPlusBloodPressure.setOnMouseClicked((MouseEvent e) -> {
            int value = (txtBloodPressure.getText().equals(""))
                    ? 0
                    : Integer.parseInt(txtBloodPressure.getText());

            value += FIELDS_VALUE;

            txtBloodPressure.setText(String.valueOf(value));
        });

        /* Diminui a press??o arterial em 1 a cada clique. */
        btnMinusBloodPressure.setOnMouseClicked((MouseEvent e) -> {
            int value = (txtBloodPressure.getText().equals(""))
                    ? 0
                    : Integer.parseInt(txtBloodPressure.getText());

            value -= FIELDS_VALUE;
            value = (value < 0) ? 0 : value;

            txtBloodPressure.setText(String.valueOf(value));
        });

        /* Aumenta a frequ??ncia card??aca em 1 a cada clique. */
        btnPlusHeartRate.setOnMouseClicked((MouseEvent e) -> {
            int value = (txtHeartRate.getText().equals(""))
                    ? 0
                    : Integer.parseInt(txtHeartRate.getText());

            value += FIELDS_VALUE;

            txtHeartRate.setText(String.valueOf(value));
        });

        /* Diminui a frequ??ncia card??aca em 1 a cada clique. */
        btnMinusHeartRate.setOnMouseClicked((MouseEvent e) -> {
            int value = (txtHeartRate.getText().equals(""))
                    ? 0
                    : Integer.parseInt(txtHeartRate.getText());
            
            value -= FIELDS_VALUE;
            value = (value < 0) ? 0 : value;

            txtHeartRate.setText(String.valueOf(value));
        });
    }

    /**
     * Verifica se o valor inserido ?? um n??mero inteiro.
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
     * Verifica se o valor inserido ?? um n??mero de ponto flutuante.
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
     * Verifica se o nome digitado ?? composto somente por letras.
     */
    private void verifyNameInput() {
        txtName.textProperty().addListener(
                new ChangeListener<String>() {
            @Override
            public void changed(
                    ObservableValue<? extends String> observable,
                    String oldValue, String newValue
            ) {
                if (!newValue.matches("\\sa-zA-Z-z??????????????????????????????????????????????????????????????*")) {
                    txtName.setText(
                            newValue.replaceAll("[^\\sa-zA-Z-z??????????????????????????????????????????????????????????????]", "")
                    );
                }
            }
        });
    }
}
