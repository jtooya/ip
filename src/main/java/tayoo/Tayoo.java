package tayoo;

import tayoo.command.Command;
import tayoo.exception.TayooException;

/**
 * The Tayoo class is the main class of the Tayoo chatbot, it initialises a new Ui, Storage and Tasklist per instannce
 * of Tayoo.
 *
 * @author Jeremy
 * @version 0.1
 */
public class Tayoo {
    private static final String NAME = "tayoo";
    private Ui ui;
    private Storage storage;
    private Tasklist tasks;

    /**
     * Constructs a new Tayoo instance by initializing a new Ui, Storage and Tasklist object.
     *
     * <p>If no Tasklist.txt file is detected (either because this is the first time a user is using the bot, or because
     * the file was deleted) the chatbot automatically creates a new .txt file in which to store data. Alternatively, if
     * a .txt file is found, the storage instance will read from the .txt file to load any tasks created when the user
     * previously used the bot.</p>
     *
     * <p>Any exceptions during file creation are caught and an error message is displayed.</p>
     *
     *
     */
    public Tayoo() {
        try {
            this.storage = new Storage();
            this.ui = new Ui(NAME);
            if (storage.createTxt()) {
                ui.printText("Creating a new tasklist.txt for you.");
            }
            this.tasks = new Tasklist(storage.readTxt());
        } catch (TayooException e) {
            ui.printError(e.getMessage());
        }
    }

    /**
     * Starts the main loop of the Tayoo chatbot, handling user commands and interactions.
     *
     * <p>The method initializes the chatbot by displaying the welcome message then enters a loop where it
     * continuously reads the users input, processes commands and executes the corresponding actions. The loop continues
     * until a command that signals an exit is encountered.</p>
     *
     * <p>Exceptions that occur during the command execution are caught and handled by displaying an error message
     * to the user. The loop terminates when a command returns a signal to exit</p>
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                Command c = Parser.parseCommand(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (TayooException e) {
                ui.printError(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Tayoo tayoo = new Tayoo();
        tayoo.run();
        System.exit(0);
    }

    public String getResponse(String input) {
        try {
            Command c = Parser.parseCommand(input);
            return c.guiExecute(tasks, ui, storage);
        } catch (TayooException e) {
            return e.getMessage();
        }
    }

}
