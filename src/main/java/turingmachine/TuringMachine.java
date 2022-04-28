package turingmachine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class TuringMachine
{
    private Set<String> StateSpace;
    private Set<Transition> TransitionSpace;
    private String StartState;
    private String AcceptState;
    private String RejectState;

    private String Tape;
    private String CurrentState;
    private int CurrentSymbol;

    class Transition
    {
        String readState;
        char readSymbol;
        String writeState;
        char writeSymbol;
        boolean moveDirection;	//true is right, false is left

        boolean isConflicting(String state, char symbol)
        {
            if (state.equals(readState) && symbol == readSymbol)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }


    public TuringMachine()
    {
        StateSpace = new HashSet<String>();
        TransitionSpace = new HashSet<Transition>();
        StartState = new String("");
        AcceptState = new String("");
        RejectState = new String("");
        Tape = new String("");
        CurrentState = new String("");
        CurrentSymbol = 0;

    }

    public boolean Run(String input, boolean silentmode, int numberOfSteps, Path outPutFile)
    {
        String successfulOutput = "";
        String failureOutput = "";
        CurrentState = StartState;
        Tape = input;

        try {
            Files.deleteIfExists(outPutFile);
            while (!CurrentState.equals(AcceptState) && !CurrentState.equals(RejectState)) {
                boolean foundTransition = false;
                Transition CurrentTransition = null;
                if (silentmode == false) {

                    if (CurrentSymbol > 0) {
                        successfulOutput = Tape.substring(0, CurrentSymbol) + " " + CurrentState + " " + Tape.substring(CurrentSymbol)+"\n";
                    } else {
                        successfulOutput = " " + CurrentState + " " + Tape.substring(CurrentSymbol)+"\n";
                    }
                    Files.write(outPutFile,successfulOutput.getBytes(StandardCharsets.UTF_8),StandardOpenOption.CREATE,StandardOpenOption.APPEND);
                }


                Iterator<Transition> TransitionsIterator = TransitionSpace.iterator();
                int counter = 0;
                while (TransitionsIterator.hasNext() && foundTransition == false) {
                    Transition nextTransition = TransitionsIterator.next();
                    if (nextTransition.readState.equals(CurrentState) && nextTransition.readSymbol == Tape.charAt(CurrentSymbol)) {
                        foundTransition = true;
                        CurrentTransition = nextTransition;
                    }

                    if (counter == numberOfSteps) {
                        break;
                    }
                    counter++;
                }

                if (foundTransition == false) {
                    failureOutput = "There is no valid transition for this phase! (state=" + CurrentState + ", symbol=" + Tape.charAt(CurrentSymbol) + ")\n";
                    Files.write(outPutFile,failureOutput.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
                    return false;
                } else {
                    CurrentState = CurrentTransition.writeState;
                    char[] tempTape = Tape.toCharArray();
                    tempTape[CurrentSymbol] = CurrentTransition.writeSymbol;
                    Tape = new String(tempTape);
                    if (CurrentTransition.moveDirection == true) {
                        CurrentSymbol++;
                    } else {
                        CurrentSymbol--;
                    }

                    if (CurrentSymbol < 0)
                        CurrentSymbol = 0;

                    while (Tape.length() <= CurrentSymbol) {
                        Tape = Tape.concat("_");
                    }


                }


            }
        } catch (IOException e) {

        }


        if (CurrentState.equals(AcceptState))
        {
            return true;
        }
        else
        {
            return false;
        }


    }

    public boolean addState(String newState)
    {
        if (StateSpace.contains(newState))
        {
            return false;
        }
        else
        {
            StateSpace.add(newState);
            return true;
        }
    }

    public boolean setStartState(String newStartState)
    {
        if (StateSpace.contains(newStartState))
        {
            StartState = newStartState;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean setAcceptState(String newAcceptState)
    {
        if (StateSpace.contains(newAcceptState) && !RejectState.equals(newAcceptState))
        {
            AcceptState = newAcceptState;
            return true;
        }
        else
        {
            return false;
        }

    }

    public boolean setRejectState(String newRejectState)
    {
        if (StateSpace.contains(newRejectState) && !AcceptState.equals(newRejectState))
        {
            RejectState = newRejectState;
            return true;
        }
        else
        {
            return false;
        }
    }

    public boolean addTransition(String rState, char rSymbol, String wState, char wSymbol, boolean mDirection)
    {
        if(!StateSpace.contains(rState) || !StateSpace.contains(wState))
        {
            return false;
        }

        boolean conflict = false;
        Iterator<Transition> TransitionsIterator = TransitionSpace.iterator();
        while ( TransitionsIterator.hasNext() && conflict == false)
        {
            Transition nextTransition = TransitionsIterator.next();
            if (nextTransition.isConflicting(rState, rSymbol))
            {
                conflict = true;
            }

        }
        if (conflict == true)
        {
            return false;
        }
        else
        {
            Transition newTransition = new Transition();
            newTransition.readState = rState;
            newTransition.readSymbol = rSymbol;
            newTransition.writeState = wState;
            newTransition.writeSymbol = wSymbol;
            newTransition.moveDirection = mDirection;
            TransitionSpace.add(newTransition);
            return true;
        }
    }

    public void writeOutputToFile(Path path, boolean isSuccess, String successMessage, String failureMessage) {

        // default, create, truncate and write to it.
        try (BufferedWriter writer =
                     Files.newBufferedWriter(path, StandardCharsets.UTF_8,StandardOpenOption.APPEND)) {
            if (isSuccess) {
                writer.write(successMessage);
            } else {
                writer.write(failureMessage);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
