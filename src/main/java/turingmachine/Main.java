package turingmachine;

import picocli.CommandLine;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.stream.Stream;

@CommandLine.Command(name = "turingmachine",mixinStandardHelpOptions = true, version = "checksum 4.0",description = "Turning Machine for assignment")
class Main implements Runnable {
    @CommandLine.Option(names = {"-f","--nameOfTransitionFile"}, description = "Enter the name of transition file")
    public String nameOfTransitionFile = "";

    @CommandLine.Option(names = {"-i","--nameOfInitialTapeInput"}, description = "Enter the name of initial tape input file")
    public String nameOfInitialTapeInput="";
    @CommandLine.Option(names = {"-o","--nameOfFinalOutputFile"}, description = "Enter the name of initial tape input file")
    public String nameOfFinalOutputFile = "";
    @CommandLine.Option(names = {"-n","--numberOfSteps"}, description = "Enter number of steps")
    public int numberOfSteps = 0;
    public static void main(String[] args) {
        new CommandLine(new Main()).execute(args);
    }

    String successMessage = "Program Halted within the number of steps given\n";
    String failureMessage = "Program didnt Halted within the number of steps given\n";

    @Override
    public void run() {
        TuringMachine turingMachine = new TuringMachine();
        turingMachine.addState("q1");
        turingMachine.addState("q2");
        turingMachine.addState("q3");
        turingMachine.addState("q4");
        turingMachine.addState("q5");
        turingMachine.addState("q6");
        turingMachine.addState("q7");
        turingMachine.addState("q8");
        turingMachine.addState("qa");
        turingMachine.addState("qr");
        turingMachine.setStartState("q1");
        turingMachine.setAcceptState("qa");
        turingMachine.setRejectState("qr");

        extractTransitionsFromFile(Paths.get(nameOfTransitionFile),turingMachine);

        String initialTape = extractInputTapeFromFile(Paths.get(nameOfInitialTapeInput));




//        TuringMachine TM1 = MachineLibrary.EqualBinaryWords();

        if (!initialTape.isEmpty()) {
            boolean done = turingMachine.Run(initialTape, false, numberOfSteps, Paths.get(nameOfFinalOutputFile));
            turingMachine.writeOutputToFile(Paths.get(nameOfFinalOutputFile),done,successMessage,failureMessage);
        }
    }



    public void extractTransitionsFromFile(Path fileName,TuringMachine turingMachine) {
        try(Stream<String> lines = Files.lines(fileName)) {

            lines
                    .map(s -> s.split(","))
                    .forEach(strings -> turingMachine.addTransition(strings[0], strings[1].charAt(0),strings[2],strings[3].charAt(0),Boolean.parseBoolean(strings[4])));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractInputTapeFromFile(Path fileName) {
        String inputTape = "";
        try(Stream<String> lines = Files.lines(fileName)) {
            inputTape = lines.findFirst().orElse("");
        } catch (IOException e) {
            System.err.println(e);
        }
        return inputTape;
    }
}
