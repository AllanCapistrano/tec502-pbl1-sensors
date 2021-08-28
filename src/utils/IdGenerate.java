package utils;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gera um identificador único.
 *
 * @author Allan Capistrano
 */
public final class IdGenerate {

    private final int size;
    private final String separator;
    private final ArrayList<String> generatedIds = new ArrayList<>();

    /**
     * Método construtor.
     *
     * @param size int - Quantidade de números que irão compor o identificador.
     * @param separator String - Separador que será utilizado.
     */
    public IdGenerate(int size, String separator) {
        this.size = size;
        this.separator = separator;
    }

    /**
     * Gera um identificador único.
     *
     * @return String | null.
     */
    public String generate() {
        String id;
        Pattern letter = Pattern.compile("[a-zA-z]");
        Pattern digit = Pattern.compile("[0-9]");

        Matcher isLetter = letter.matcher(this.separator);
        Matcher isDigit = digit.matcher(this.separator);

        if (!isLetter.find() && !isDigit.find()) {
            do {
                id = this.createId();
            } while (!this.verifyId(id));

            this.generatedIds.add(id);

            return id;
        }

        return null;
    }

    /**
     * Cria o separador no formato xxx.xxx.xxx... onde x é um número de 0-9.
     *
     * @return String.
     */
    private String createId() {
        String id = "";
        Random randomInt = new Random();

        for (int i = 0; i < this.size; i++) {
            if (i != 0 && i % 3 == 0) {
                id += this.separator;
            }

            id += Integer.toString(randomInt.ints(0, 9).findFirst().getAsInt());
        }

        return id;
    }

    /**
     * Verifica se o identificador já está em uso.
     *
     * @param id String - Identificador que se deseja verificar.
     * @return boolean
     */
    private boolean verifyId(String id) {
        if (!this.generatedIds.isEmpty()) {
            for (int i = 0; i < this.generatedIds.size(); i++) {
                if (this.generatedIds.get(i).equals(id)) {
                    System.out.println("Não devia ter entrado. " + id);

                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Retorna uma lista contendo o todos os identificadores criados.
     *
     * @return ArrayList<String>
     */
    public ArrayList<String> list() {
        return this.generatedIds;
    }
}
