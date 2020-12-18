package ui;

import java.util.Scanner;

public class Console {

    /**
     * reading user input (only int) - repeat if input is invalid
     * @param text request an Integer input from user
     * @return int
     */
    public static int readInteger(String text) {

        Scanner input = new Scanner(System.in);
        int x = -1;
        boolean isNumber;

        do {
            System.out.println(text);
            if (input.hasNextInt()) {
                x = input.nextInt();
                isNumber = true;
            } else {
                System.out.println("invalid input");
                isNumber = false;
                input.next();
            }
        } while (!(isNumber));
        return x;
    }

    public static String readString(String text) {

        Scanner input = new Scanner(System.in);
        boolean notEmpty;

        do {
            System.out.println(text);
            if (input.hasNext()) {
                notEmpty = true;
            } else {
                notEmpty = false;
                input.next();
            }
        } while (!(notEmpty));
        return input.nextLine();
    }

}
