package clients;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.json.JSONObject;

/**
 * Client de emulador de sensores.
 *
 * @author Allan Capistrano
 */
public class SensorsClient {

    /**
     * Envia para o servidor uma requisição.
     *
     * @param httpMethod String - Método HTTP da requisição que será feita.
     * @param route String - Rota para a qual a requisição será feita.
     * @param conn Socket - Conexão que é realizada com o servidor.
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
    public static void sendToServer(
            String httpMethod,
            String route,
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
        json.put("method", httpMethod); // Método HTTP
        json.put("route", route + deviceId); // Rota

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

            /* Enviando a requisição para o servidor. */
            output.writeObject(json);

            output.close();
        } catch (IOException ioe) {
            System.err.println("Erro ao tentar enviar os dados dos sensores "
                    + "para o servidor.");
            System.out.println(ioe);
        }
    }
}
