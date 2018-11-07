import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Parsing {

  private final static String TERMINAL_TYPE = "T";
  private final static String NONTERMINAL_TYPE = "NT";
  private final static String STARTING = "S";
  private final static String PRODUCTION = "P";
  private final static String SHIFT = "SHIFT";
  private final static String ACCEPT = "ACCEPT";

  private final static String STATE = "S";
  private final String INPUT_SYMBOL = "IS";
  private final String TRANSITION  = "T";
  private final String START_STATE = "SS";
  private final String ACCEPTING_STATE = "AS";

  public static void main(String[] args) {
    try {
      Grammar grammar = readGrammar(Paths.get("C:\\Users\\Sergiu\\IdeaProjects\\lab4flcd\\grammar"));

      Parser parser = new Parser();

      populateParser(grammar, parser);

      parse(parser);
    } catch (IOException ex) {
      System.out.println(ex);
    }
  }

  public static void parse(Parser parser) {
    String input = "int a ; { read ( a ) } $";
    String[] inputSymbols = input.split(" ");
    Stack<String> inputStack = new Stack<>();
    for (int i = inputSymbols.length-1; i > -1; i--) {
      inputStack.push(inputSymbols[i]);
    }
    Stack<String> workStack = new Stack<>();
    workStack.push(parser.getAutomata().getStartState());
    Stack<Integer> productions = new Stack<>();
    boolean accept = false;

    while (!inputStack.isEmpty()) {
      String inputSymbol = inputStack.pop();
      String state = workStack.peek();
      if (parser.getParseTable().getAction(state).equals(SHIFT)) {
        workStack.push(inputSymbol);
        String goTo = parser.getParseTable().getGoTo(state, inputSymbol);
        if (goTo != null)
          workStack.push(goTo);
        else {
          System.out.println("ERROR");
          break;
        }
      } else if (parser.getParseTable().getAction(state).equals(ACCEPT)) {
        if (workStack.isEmpty()) {
          accept = true;
          break;
        }
      } else {
        try {
          int indexOfProduction = Integer.parseInt(parser.getParseTable().getAction(state));
          productions.push(indexOfProduction);
          String production = parser.getGrammar().getProductionsString().get(indexOfProduction);
          String lhs = production.split("->")[0];
          String rhs = production.split("->")[1];
          for (int i = 0; i < noOfSymbols(parser.getGrammar(), rhs); i++) {
            workStack.pop();
            workStack.pop();
          }
          String prevState = workStack.peek();
          workStack.push(lhs);
          workStack.push(parser.getParseTable().getGoTo(prevState, lhs));
          inputStack.push(inputSymbol);
        } catch (NumberFormatException e) {
          System.out.println(e);
        }
      }
    }
    if (!accept) {
      System.out.println("The sequence is not accepted!");
    } else {
      String derivations = null;
      while (!productions.isEmpty()) {
        String production = parser.getGrammar().getProductionsString().get(productions.pop());
        if (derivations == null) {
          derivations = production;
        } else {
          String lastDerivation = derivations.split("=>")[derivations.split("=>").length-1];
          String rhs = production.split("->")[1];
          String lastNonTerminal = getLastSymbol(parser.getGrammar(), lastDerivation);
          if (lastNonTerminal != null)
            derivations = derivations + "=>" + lastDerivation.substring(0, lastDerivation.lastIndexOf(lastNonTerminal))+rhs+
                lastDerivation.substring(lastDerivation.lastIndexOf(lastNonTerminal)+lastNonTerminal.length(), lastDerivation.length());
        }
      }
      System.out.println(derivations);
    }
  }

  public static String getLastSymbol(Grammar grammar, String production) {
    int lastIndex = 0;
    String symbol = null;
    for (String nonTerminal : grammar.getNonTerminals()) {
      int index = production.lastIndexOf(nonTerminal);
      if (index > lastIndex) {
        lastIndex = index;
        symbol = nonTerminal;
      }
    }
    return symbol;
  }

  public static int noOfSymbols(Grammar grammar, String rhs) {
    List<String> symbols = new ArrayList<>();
    symbols.addAll(grammar.getNonTerminals());
    symbols.addAll(grammar.getTerminals());
    int noSymbols = 0;
    while (!rhs.isEmpty()) {
      for (String symbol : symbols) {
        if (rhs.indexOf(symbol) == 0) {
          noSymbols++;
          rhs = rhs.substring(symbol.length());
        }
      }
    }
    return noSymbols;
  }

  public static Closure clojure(List<String> items, Grammar grammar) {
    Closure closure = new Closure();
    closure.addItems(items);
    LinkedList<String> processingQueue = new LinkedList<>();
    processingQueue.addAll(items);
    while (!processingQueue.isEmpty()) {
      String currentItem = processingQueue.removeFirst();
      String lhs = currentItem.split("->")[0];
      String rhs = currentItem.split("->")[1];
      int dotIndex = rhs.indexOf('.');
      if (dotIndex > -1 && dotIndex+1 < rhs.length()) {
        String nextSymbol = getNextSymbol(currentItem, grammar);
        if (grammar.isNonTerminal(nextSymbol)) {
          for (String production : grammar.getProductionsForSymbol(nextSymbol)) {
            String newItem = nextSymbol+"->."+production;
            if (!closure.contains(newItem)) {
              processingQueue.addLast(newItem);
              closure.addItem(newItem);
            }
          }
        }
      }
    }
    return closure;
  }

  public static Closure goTo(Closure currentClosure, String symbol, Grammar grammar) {
    Closure newClosure = new Closure();
    for (String clojureItem : currentClosure.getItems()) {
      int dotIndex = clojureItem.indexOf('.');
      if (dotIndex < clojureItem.length()) {
        String nextSymbol = getNextSymbol(clojureItem, grammar);
        if (nextSymbol != null && nextSymbol.equals(symbol)) {
          String before = clojureItem.split("\\.")[0] + nextSymbol;
          String after = clojureItem.substring(clojureItem.indexOf(".") + nextSymbol.length() + 1);
          String newClojureItem = before + "." + after;
          newClosure.addItem(newClojureItem);
        }
      }
    }
    return clojure(newClosure.getItems(), grammar);
  }

  public static void populateParser(Grammar grammar, Parser parser) {
    String newNonTerminal = grammar.getStartingSymbol()+"'";
    grammar.addNonTerminal(newNonTerminal);
    grammar.addProduction(newNonTerminal, grammar.getStartingSymbol());
    String firstItem = newNonTerminal+"->."+grammar.getStartingSymbol();
    String acceptItem = newNonTerminal+"->"+grammar.getStartingSymbol()+".";

    int stateIndex = 0;
    String firstState = STATE+stateIndex;
    Map<String, Closure> clojures = new HashMap<>();
    clojures.put(firstState, clojure(Collections.singletonList(firstItem), grammar));
    LinkedList<String> processingQueue = new LinkedList<>();
    processingQueue.add(firstState);
    Automata automata = new Automata();
    ParseTable parseTable = new ParseTable();
    automata.addState(firstState);
    automata.setStartState(firstState);
    while (!processingQueue.isEmpty()) {
      String currentState = processingQueue.removeFirst();
      Closure currentClosure = clojures.get(currentState);
      for (String clojureItem : currentClosure.getItems()) {
        String nextSymbol = getNextSymbol(clojureItem, grammar);
        if (nextSymbol != null) {
          Closure newClosure = goTo(currentClosure, nextSymbol, grammar);
          String newState = null;
          if (!clojures.containsValue(newClosure)) {
            stateIndex++;
            newState = STATE+stateIndex;
            automata.addState(newState);
            clojures.put(newState, newClosure);
            processingQueue.addLast(newState);
          } else {
            for (String state : clojures.keySet()) {
              if (clojures.get(state).equals(newClosure)) {
                newState = state;
              }
            }
          }
          automata.addTransition(currentState, nextSymbol, newState);
          automata.addInputSymbol(nextSymbol);
          parseTable.addGoTo(currentState, nextSymbol, newState);
        }
      }
    }

    for (String state : clojures.keySet()) {
      boolean foundSomething = false;
      for (String clojureItem : clojures.get(state).getItems()) {
        if (clojureItem.equals(acceptItem)) {
          parseTable.addAction(state, ACCEPT);
          foundSomething = true;
        } else {
          int dotIndex = clojureItem.indexOf('.');
          if (dotIndex == clojureItem.length() - 1) {
            String production = clojureItem.substring(0, dotIndex);
            parseTable.addAction(state, grammar.getProductionsString().indexOf(production) + "");
            foundSomething = true;
          }
        }
      }
      if (!foundSomething) {
        parseTable.addAction(state, SHIFT);
      }
    }

    parser.setParseTable(parseTable);
    parser.setAutomata(automata);
    parser.setClojures(clojures);
    parser.setGrammar(grammar);
  }

  public static String getNextSymbol(String item, Grammar grammar) {
    String rhs = item.split("->")[1];
    int dotIndex = rhs.indexOf('.');
    String result = null;
    if (dotIndex > -1) {
      for (String terminal : grammar.getTerminals()) {
        if (rhs.indexOf(terminal, dotIndex) == dotIndex+1) {
          result = terminal;
        }
      }
      for (String nonTerminal : grammar.getNonTerminals()) {
        if (rhs.indexOf(nonTerminal, dotIndex) == dotIndex+1) {
          result = nonTerminal;
        }
      }
    }
    return result;
  }

  public static Grammar readGrammar(Path grammarFilePath) throws IOException {
    Grammar grammar = new Grammar();
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
    return grammar;
  }
}
