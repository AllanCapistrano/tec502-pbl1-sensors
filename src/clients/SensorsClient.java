package clients;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import utils.IdGenerate;

/**
 *
 * @author Allan Capistrano
 */
public class SensorsClient {

    private static final String IPADDRESS = "localhost";
    private static final int PORT = 12244;
    private static final IdGenerate ID_GENERATED = new IdGenerate(12, ".");

    public static void main(String[] args) {

        try {
            /* Criando a conexão com o servidor. */
            Socket conn = SensorsClient.startDevice();

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
     * Inicia o dispositivo. Primeiro realiza a conexão com o servidor, e em
     * seguida envia o id do dispositivo para o servidor
     *
     * @return Socket - Conexão que foi realizada com o servidor.
     * @throws IOException
     */
    private static Socket startDevice() throws IOException {
        Socket conn = new Socket(IPADDRESS, PORT);

        SensorsClient.sendDeviceId(conn);

        return conn;
    }

    /**
     * Envia para o servidor o id do dispositivo.
     *
     * @param conn Socket - Conexão que é realizada com o Server.
     */
    private static void sendDeviceId(Socket conn) {
        try {
            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject("POST patients/create/" + ID_GENERATED.generate());

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
        try {
            Socket conn = new Socket(IPADDRESS, PORT);

            ObjectOutputStream output
                    = new ObjectOutputStream(conn.getOutputStream());

            output.writeObject("exit");

            output.close();
        } catch (IOException e) {
            System.out.println("Erro ao tentar encerrar o servidor.");
        }
    }
}
