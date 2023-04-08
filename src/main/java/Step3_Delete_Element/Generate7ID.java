package Step3_Delete_Element;

import java.util.Random;
public class Generate7ID {
    private static final String LETTERS = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "0123456789";
    private static final int WORD_LENGTH = 7;


    public Generate7ID() {
    }

    public static String generate(){

        String characters = LETTERS + NUMBERS;
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < WORD_LENGTH; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
