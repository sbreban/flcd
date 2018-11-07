package com.flcd;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class GrammarAndAutomata {

  private final String TERMINAL_TYPE = "T";
  private final String NONTERMINAL_TYPE = "NT";
  private final String STARTING = "S";
  private final String PRODUCTION = "P";

  private final String STATE = "S";
  private final String INPUT_SYMBOL = "IS";
  private final String TRANSITION  = "T";
  private final String START_STATE = "SS";
  private final String ACCEPTING_STATE = "AS";

  private final Path grammarFilePath;
  private final Path automataFilePath;
  private Grammar grammar;
  private Automata automata;

  public GrammarAndAutomata(String grammarFileName, String automataFileName) {
    this.grammarFilePath = Paths.get(grammarFileName);
    this.automataFilePath = Paths.get(automataFileName);
  }

  public static void main(String[] args) {
    GrammarAndAutomata grammarAndAutomata = new GrammarAndAutomata("C:\\Users\\breba\\OneDrive\\Documente\\Academic\\flcd\\lab2flcd\\grammar", "C:\\Users\\breba\\OneDrive\\Documente\\Academic\\flcd\\lab2flcd\\automata");
    try {
      grammarAndAutomata.readGrammar();
      grammarAndAutomata.readAutomata();
      boolean goOn = true;
      while (goOn) {
        System.out.println("1 - Print terminals");
        System.out.println("2 - Print non terminals");
        System.out.println("3 - Print productions");
        System.out.println("4 - Print production for symbol");
        System.out.println("5 - Check grammar regularity");
        System.out.println("6 - Print the set of states");
        System.out.println("7 - Print the set of input symbols");
        System.out.println("8 - Print all the transitions");
        System.out.println("9 - Print the set of final states");
        System.out.println("10 - Print the corresponding automata for grammar");
        System.out.println("11 - Print the corresponding grammar for automata");
        System.out.println("0 - Exit");
        Scanner keyboard = new Scanner(System.in);
        int option = keyboard.nextInt();
        if (option == 1) {
          System.out.println(grammarAndAutomata.getGrammar().toStringTerminals());
        } else if (option == 2) {
          System.out.println(grammarAndAutomata.getGrammar().toStringNonTerminals());
        } else if (option == 3) {
          System.out.println(grammarAndAutomata.getGrammar().toStringProductions());
        } else if (option == 4) {
          System.out.print("Give symbol: ");
          char symbol = keyboard.next().charAt(0);
          System.out.println(grammarAndAutomata.getGrammar().productionsForSymbol(symbol));
        } else if (option == 5) {
          boolean isRegular = grammarAndAutomata.getGrammar().isRegular();
          if (isRegular) {
            System.out.println("Grammar is regular");
          } else {
            System.out.println("Grammar is not regular");
          }
        } else if (option == 6) {
          System.out.println(grammarAndAutomata.getAutomata().toStringStates());
        } else if (option == 7) {
          System.out.println(grammarAndAutomata.getAutomata().toStringInputSymbols());
        } else if (option == 8) {
          System.out.println(grammarAndAutomata.getAutomata().toStringTransitions());
        } else if (option == 9) {
          System.out.println(grammarAndAutomata.getAutomata().toStringAcceptingStates());
        } else if (option == 10) {
          try {
            System.out.println(grammarAndAutomata.getGrammar().getCorrespondingAutomata());
          } catch (UnsupportedOperationException ex) {
            System.out.println("Cannot transform: " + ex.getMessage());
          }
        } else if (option == 11) {
          System.out.println(grammarAndAutomata.getAutomata().getCorrespondingGrammar());
        } else if (option == 0) {
          goOn = false;
        }
      }
    } catch (IOException ex) {
      System.out.println(ex);
    }
  }

  public void readGrammar() throws IOException {
    grammar = new Grammar();
    boolean goOn = true;
    try (Scanner scanner =  new Scanner(grammarFilePath)) {
      while (scanner.hasNextLine() && goOn){
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        if (lineScanner.hasNext()) {
          String type = lineScanner.next();
          if (type.equals(TERMINAL_TYPE)) {
            String terminal = lineScanner.next();
            grammar.addTerminal(terminal);
          } else if (type.equals(NONTERMINAL_TYPE)) {
            String nonTerminal = lineScanner.next();
            grammar.addNonTerminal(nonTerminal);
          } else if (type.equals(STARTING)) {
            String starting = lineScanner.next();
            grammar.setStartingSymbol(starting);
          } else if (type.equals(PRODUCTION)) {
            String production = lineScanner.next();
            String[] sides = production.split("->");
            String left = sides[0];
            String right = sides[1];
            grammar.addProduction(left, right);
          } else if (type.equals("end")) {
            goOn = false;
          }
        }
      }
    }
  }

  public void readAutomata() throws IOException {
    automata = new Automata();
    boolean goOn = true;
    try (Scanner scanner =  new Scanner(automataFilePath)) {
      while (scanner.hasNextLine() && goOn){
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        if (lineScanner.hasNext()) {
          String type = lineScanner.next();
          if (type.equals(STATE)) {
            String state = lineScanner.next();
            automata.addState(state);
          } else if (type.equals(INPUT_SYMBOL)) {
            String inputSymbol = lineScanner.next();
            automata.addInputSymbol(inputSymbol);
          } else if (type.equals(TRANSITION)) {
            String transition = lineScanner.next();
            String[] transitionElements = transition.split("-");
            automata.addTransition(transitionElements[0], transitionElements[1], transitionElements[2]);
          } else if (type.equals(START_STATE)) {
            String startState = lineScanner.next();
            automata.setStartState(startState);
          } else if (type.equals(ACCEPTING_STATE)) {
            String acceptingState = lineScanner.next();
            automata.addAcceptingState(acceptingState);
          } else if (type.equals("end")) {
            goOn = false;
          }
        }
      }
    }
  }

  public Grammar getGrammar() {
    return grammar;
  }

  public Automata getAutomata() {
    return automata;
  }

  @Override
  public String toString() {
    return automata.toString();
  }
}
