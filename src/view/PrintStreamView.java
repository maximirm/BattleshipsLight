package view;

import java.io.IOException;
import java.io.PrintStream;

public interface PrintStreamView {

    void printOwnBoard(PrintStream printStream) throws IOException;
    void printEnemyBoard(PrintStream printStream) throws IOException;

}
