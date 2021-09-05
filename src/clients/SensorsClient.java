package clients;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.json.JSONObject;

/**
 *
 * @author Allan Capistrano
 */
public class SensorsClient {

    /**
     * Envia para o servidor o id do dispositivo.
     *
     * @param conn Socket - Conexão que é realizada com o Server.
     * @param name String - Nome do paciente.
     * @param bodyTemperature float - Temperatura corporal registrada pelo
     * sensor.
     * @param respiratoryFrequency int - Frequência respiratória registrada pelo
     * sensor.
     * @param bloodOxygenation float - Nível de oxigenação do sangue registrado
     * pelo sensor.
     * @param bloodPressure int - Pressão arterial registrada pelo sensor.
     * @param heartRate int - Frequência cardíaca registrada pelo sensor.
     */
    public static void sendInitialValues(
            Socket conn,
            String name,
            float bodyTemperature,
            int respiratoryFrequency,
            float bloodOxygenation,
            int bloodPressure,
            int heartRate,
            String deviceId
    ) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        /* Definindo os dados que serão enviadas para o Server. */
        json.put("method", "POST"); // Método HTTP
        json.put("route", "patients/create/" + deviceId); // Rota

        /* Corpo da requisição */
        body.put("name", name); // Nome do paciente
        body.put("bodyTemperatureSensor", bodyTemperature); // Temperatura corporal
        body.put("respiratoryFrequencySensor", respiratoryFrequency); // Frequência respiratória
        body.put("bloodOxygenationSensor", bloodOxygenation); // Oxigenação do sangue
        body.put("bloodPressureSensor", bloodPressure); // Pressão arterial
        body.put("heartRateSensor", heartRate); // Frequência cardíaca

        json.put("body", body); // Adicionando o Array no JSON que será enviado

        try {
            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject(json);

            output.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao tentar enviar o ID do dispositivo "
                    + "para o servidor.");
            System.out.println(ioe);
        }
    }

    /**
     * Altera os valores medidos pelos sensores.
     *
     * @param conn Socket - Conexão que é realizada com o Server.
     * @param name String - Nome do paciente.
     * @param bodyTemperature float - Temperatura corporal registrada pelo
     * sensor.
     * @param respiratoryFrequency int - Frequência respiratória registrada pelo
     * sensor.
     * @param bloodOxygenation float - Nível de oxigenação do sangue registrado
     * pelo sensor.
     * @param bloodPressure int - Pressão arterial registrada pelo sensor.
     * @param heartRate int - Frequência cardíaca registrada pelo sensor.
     * @param deviceId string - Identificador do dispositivo.
     */
    public static void updateSensorsValues(
            Socket conn,
            String name,
            float bodyTemperature,
            int respiratoryFrequency,
            float bloodOxygenation,
            int bloodPressure,
            int heartRate,
            String deviceId
    ) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        /* Definindo os dados que serão alterados. */
        json.put("method", "PUT"); // Método HTTP
        json.put("route", "patients/edit/" + deviceId); // Rota

        /* Corpo da requisição */
        body.put("name", name); // Nome do paciente
        body.put("bodyTemperatureSensor", bodyTemperature); // Temperatura corporal
        body.put("respiratoryFrequencySensor", respiratoryFrequency); // Frequência respiratória
        body.put("bloodOxygenationSensor", bloodOxygenation); // Oxigenação do sangue
        body.put("bloodPressureSensor", bloodPressure); // Pressão arterial
        body.put("heartRateSensor", heartRate); // Frequência cardíaca

        json.put("body", body); // Adicionando o Array no JSON que será enviado

        try {
            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject(json);

            output.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao tentar editar os dados dos sensores.");
            System.out.println(ioe);
        }
    }
}
