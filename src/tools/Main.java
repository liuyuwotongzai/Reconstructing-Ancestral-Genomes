package tools;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        encode.encodeAllandWrite();
        ExecutionOfcommands.doCommands();
        findAndAnalysis.allStates();
    }
}
