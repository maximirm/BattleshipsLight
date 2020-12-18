/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package ui;

import java.util.LinkedList;

public class App {

    public static final String WELCOME = "Welcome to battleships (light)";
    public static final String ENTER_NAME = "Enter your name: ";
    public static final String SELECT_AN_OPTION = "Select an option: ";
    public static final String INVALID_OPTION = "Select a valid option between 0 and ";



    public static void main(String[] args) {
        System.out.println(WELCOME);
        String playerName = Console.readString(ENTER_NAME);

        BattleshipsUI userInterface = new BattleshipsUI(playerName);
        LinkedList<Command> commandList = userInterface.returnCommandList();

        do {
            System.out.println(buildCommandMenu(commandList));
            System.out.println(selectCommand(commandList).execute());
        } while (true);
    }

    private static String buildCommandMenu(LinkedList<Command> commands) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        builder.append(System.lineSeparator()).append(SELECT_AN_OPTION).append(System.lineSeparator());
        builder.append(System.lineSeparator());
        for (int i = 1; i < commands.size(); i++) {
            Command cmd = commands.get(i);
            builder.append(" ").append(i).append(". ").append(cmd.description()).append(System.lineSeparator());
        }
        builder.append(" " + 0 + ". ").append(commands.get(0).description()).append(System.lineSeparator());
        return builder.toString();
    }

    static private Command selectCommand(LinkedList<Command> commands) {
        do {
            int select = Console.readInteger(SELECT_AN_OPTION);
            if (select >= 0 && select < commands.size()) {
                return commands.get(select);
            }
            System.out.println( INVALID_OPTION+ (commands.size() - 1));
        }
        while (true);
    }
}
