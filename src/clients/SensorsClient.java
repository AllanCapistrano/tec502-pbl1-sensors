package clients;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import org.json.JSONObject;
import utils.IdGenerate;

/**
 *
 * @author Allan Capistrano
 */
public class SensorsClient {

    private static final String IPADDRESS = "localhost";
    private static final int PORT = 12244;
    private static final String ID_GENERATED = new IdGenerate(12, ".").generate();

    public static void main(String[] args) {

        try {
            /* Criando a conexão com o servidor. */
            Socket conn = SensorsClient.startDevice();
            Socket conn1 = SensorsClient.startConnection();

            SensorsClient.updateSensorsValues(conn1);

            /* Encerrar servidor.. */
            SensorsClient.shutDownServer();

            /* Fechando as conexões. */
            conn.close();
        } catch (UnknownHostException e) {
            System.out.println("Servidor não encontrado ou está fora do ar.");
        } catch (IOException e) {
            System.out.println("Erro de Entrada/Saída.");
        }
    }

    /**
     * Inicia uma nova conexão com o servidor.
     * 
     * @return Socket
     * @throws IOException 
     */
    private static Socket startConnection() throws IOException {
        return new Socket(IPADDRESS, PORT);
    }

    /**
     * Inicia o dispositivo. Primeiro realiza a conexão com o servidor, e em
     * seguida envia o id do dispositivo para o servidor
     *
     * @return Socket - Conexão que foi realizada com o servidor.
     * @throws IOException
     */
    private static Socket startDevice() throws IOException {
        Socket conn = SensorsClient.startConnection();

        SensorsClient.sendDeviceId(conn);

        return conn;
    }

    /**
     * Envia para o servidor o id do dispositivo.
     *
     * @param conn Socket - Conexão que é realizada com o Server.
     */
    private static void sendDeviceId(Socket conn) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        /* Definindo os dados que serão enviadas para o Server. */
        json.put("method", "POST"); // Método HTTP
        json.put("route", "patients/create/" + ID_GENERATED); // Rota

        /* Corpo da requisição */
        body.put("name", "Random name"); // Nome do paciente
        body.put("bodyTemperatureSensor", (float) 36.5); // Temperatura corporal
        body.put("respiratoryFrequencySensor", (float) 20); // Frequência respiratória
        body.put("bloodOxygenationSensor", (float) 96); // Oxigenação do sangue
        body.put("bloodPressureSensor", (float) 120); // Pressão arterial
        body.put("heartRateSensor", (float) 80); // Frequência cardíaca

        json.put("body", body); // Adicionando o Array no JSON que será enviado

        try {
            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject(json);

            output.close();
        } catch (IOException e) {
            System.out.println("Erro ao tentar enviar o ID do dispositivo "
                    + "para o servidor.");
        }
    }

    /**
     * Encerrar servidor.
     */
    private static void shutDownServer() {
        JSONObject json = new JSONObject();

        json.put("exit", true);

        try {
            Socket conn = new Socket(IPADDRESS, PORT);

            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject(json);

            output.close();
        } catch (IOException e) {
            System.out.println("Erro ao tentar encerrar o servidor.");
        }
    }
    
    /**
     * Altera os valores medidos pelos sensores.
     * 
     * @param conn Socket - Conexão que é realizada com o Server.
     */
    private static void updateSensorsValues(Socket conn) {
        JSONObject json = new JSONObject();
        JSONObject body = new JSONObject();

        /* Definindo os dados que serão alterados. */
        json.put("method", "PUT"); // Método HTTP
        json.put("route", "patients/edit/" + ID_GENERATED); // Rota

        /* Corpo da requisição */
        body.put("bodyTemperatureSensor", (float) 36.5); // Temperatura corporal
        body.put("respiratoryFrequencySensor", (float) 20); // Frequência respiratória
        body.put("bloodOxygenationSensor", (float) 94); // Oxigenação do sangue
        body.put("bloodPressureSensor", (float) 130); // Pressão arterial
        body.put("heartRateSensor", (float) 80); // Frequência cardíaca

        json.put("body", body); // Adicionando o Array no JSON que será enviado

        try {
            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject(json);

            output.close();
        } catch (IOException e) {
            System.out.println("Erro ao tentar editar os dados dos sensores.");
        }
    }
}
