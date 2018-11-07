package com.flcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergiu on 08.11.2015.
 */
public class Grammar {
  List<String> terminals;
  List<String> nonTerminals;
  String startingSymbol;
  Map<String, List<String>> productions;

  public final static String EMPTY = "empty";
  public final static String FINAL_STATE = "F";

  public Grammar() {
    this.terminals = new ArrayList<>();
    this.nonTerminals = new ArrayList<>();
    this.productions = new HashMap<>();
  }

  public void addTerminal(String terminal) {
    if (!terminals.contains(terminal)) {
      terminals.add(terminal);
    }
  }

  public void addNonTerminal(String nonTerminal) {
    if (!nonTerminals.contains(nonTerminal)) {
      nonTerminals.add(nonTerminal);
    }
  }

  public void setStartingSymbol(String startingSymbol) {
    this.startingSymbol = startingSymbol;
  }

  public void addProduction(String left, String right) {
    List<String> rightList = productions.get(left);
    if (rightList == null) {
      rightList = new ArrayList<>();
      productions.put(left, rightList);
    }
    rightList.add(right);
  }

  public boolean isRegular() {
    boolean isRegular = true;
    for (String left : productions.keySet()) {
      if (left.length() > 1) {
        isRegular = false;
      }
      for (String right : productions.get(left)) {
        if (right.length() > 2) {
          if (right.equals(EMPTY)) {
            if (left.charAt(0) != startingSymbol.charAt(0) || (left.charAt(0) == startingSymbol.charAt(0) &&
                    containsSymbol(left.charAt(0), getAllRightHandSide()))) {
              isRegular = false;
            }
          } else {
            isRegular = false;
          }
        } else if (right.length() == 2) {
          if (!(containsSymbol(right.charAt(1), nonTerminals) && containsSymbol(right.charAt(0), terminals))) {
            isRegular = false;
          }
        } else if (right.length() == 1) {
          if (!containsSymbol(right.charAt(0), terminals)) {
            isRegular = false;
          }
        }
      }
    }
    return isRegular;
  }

  private boolean containsSymbol(char symbol, List<String> symbols) {
    boolean contains = false;
    for (String symbolFromList : symbols) {
//      char charSymbol = symbolFromList.charAt(0);
      for (char c : symbolFromList.toCharArray()) {
        if (c == symbol) {
          contains = true;
        }
      }
    }
    return contains;
  }

  public List<String> getAllRightHandSide() {
    List<String> rhs = new ArrayList<>();
    for (String left : productions.keySet()) {
      for (String right : productions.get(left)) {
        if (!rhs.contains(right)) {
          rhs.add(right);
        }
      }
    }
    return rhs;
  }

  public String toStringTerminals() {
    StringBuilder sb = new StringBuilder();
    sb.append("Terminals: ");
    for (String terminal : terminals) {
      sb.append(terminal);
      sb.append(" ");
    }
    return sb.toString();
  }

  public String toStringNonTerminals() {
    StringBuilder sb = new StringBuilder();
    sb.append("Non Terminals: ");
    for (String nonTerminal : nonTerminals) {
      sb.append(nonTerminal);
      sb.append(" ");
    }
    return sb.toString();
  }

  public String toStringProductions() {
    StringBuilder sb = new StringBuilder();
    sb.append("Productions: ");
    for (String left : productions.keySet()) {
      for (String right : productions.get(left)) {
        sb.append(createProduction(left, right));
      }
    }
    return sb.toString();
  }

  public String productionsForSymbol(char symbol) {
    StringBuilder sb = new StringBuilder();
    for (String left : productions.keySet()) {
      for (char c : left.toCharArray()) {
        if (symbol == c) {
          for (String right : productions.get(left)) {
            sb.append(createProduction(left, right));
          }
        }
      }
    }
    return sb.toString();
  }

  private boolean containsString(String string, List<String> list) {
    for (String element : list) {
      if (element.equals(string)) {
        return true;
      }
    }
    return false;
  }

  public Automata getCorrespondingAutomata() {
    if (!isRegular()) {
      throw new UnsupportedOperationException("Grammar is not regular");
    }
    Automata automata = new Automata();
    for (String nonTerminal : nonTerminals) {
      automata.addState(nonTerminal);
    }
    automata.addState(FINAL_STATE);
    automata.setStartState(startingSymbol);
    automata.addAcceptingState(FINAL_STATE);
    for (String terminal : terminals) {
      automata.addInputSymbol(terminal);
    }
    for (String left : productions.keySet()) {
      for (String right : productions.get(left)) {
        if (right.equals(EMPTY)) {
          automata.addAcceptingState(startingSymbol);
        } else if (right.length() == 2) {
          automata.addTransition(left, right.substring(0, 1), right.substring(1, 2));
        } else if (right.length() == 1) {
          automata.addTransition(left, right.substring(0, 1), FINAL_STATE);
        }
      }
    }
    return automata;
  }

  private String createProduction(String left, String right) {
    StringBuilder sb = new StringBuilder();
    sb.append(left);
    sb.append("->");
    sb.append(right);
    sb.append(" ");
    return sb.toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Start symbol: ");
    sb.append(startingSymbol);
    sb.append("\n");

    sb.append(toStringTerminals());
    sb.append("\n");

    sb.append(toStringNonTerminals());
    sb.append("\n");

    sb.append(toStringProductions());
    return sb.toString();
  }
}
