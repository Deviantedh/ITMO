package commands;

import commands.Command;
import managers.CollectionManager;
import utility.console.Console;

public class History extends Command {
    private final Console console;
    private final CollectionManager collectionManager;

    public History(Console console, CollectionManager collectionManager) {
        super("history", "show last 5 commands");
        this.console = console;
        this.collectionManager = collectionManager;
    }

    @Override
    public void execute(String[] args) throws Exception {
        String[] commandHistory = collectionManager.getCommandHistory().toArray(new String[0]);

        for (int i = Math.max(0, commandHistory.length - 5); i < commandHistory.length; i++) {
            console.println("- " + commandHistory[i]);
        }
    }

    @Override
    public boolean apply(String[] arguments) {
        return false;
    }
}