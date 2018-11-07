package com.flcd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergiu on 08.11.2015.
 */
public class Automata {

  private List<String> states;
  private List<String> inputSymbols;
  private Map<String, Map<String, List<String>>> transitions;
  private String startState;
  private List<String> acceptingStates;

  public Automata() {
    this.states = new ArrayList<>();
    this.inputSymbols = new ArrayList<>();
    this.transitions = new HashMap<>();
    this.acceptingStates = new ArrayList<>();
  }

  public void addState(String state) {
    states.add(state);
  }

  public void addInputSymbol(String inputSymbol) {
    inputSymbols.add(inputSymbol);
  }

  public void addTransition(String state1, String inputSymbol, String state2) {
    Map<String, List<String>> transitionsFromState1 = transitions.get(state1);
    if (transitionsFromState1 == null) {
      transitionsFromState1 = new HashMap<>();
      transitions.put(state1, transitionsFromState1);
    }
    List<String> transitionsWithInput = transitionsFromState1.get(inputSymbol);
    if (transitionsWithInput == null) {
      transitionsWithInput = new ArrayList<>();
      transitionsFromState1.put(inputSymbol, transitionsWithInput);
    }
    if (!transitionsWithInput.contains(state2)) {
      transitionsWithInput.add(state2);
    }
  }

  public void setStartState(String startState) {
    this.startState = startState;
  }

  public void addAcceptingState(String acceptingState) {
    acceptingStates.add(acceptingState);
  }

  private void buildStringList(List<String> list, StringBuilder sb) {
    for (String listElement : list) {
      sb.append(listElement);
      sb.append(" ");
    }
  }

  public String toStringTransitions() {
    StringBuilder sb = new StringBuilder();
    sb.append("Transitions: ");
    for (String state1 : transitions.keySet()) {
      for (String inputSymbol: transitions.get(state1).keySet()) {
        for (String state2 : transitions.get(state1).get(inputSymbol)) {
          sb.append(state1);
          sb.append("-");
          sb.append(inputSymbol);
          sb.append("-");
          sb.append(state2);
          sb.append(" ");
        }
      }
    }
    return sb.toString();
  }

  public String toStringStates() {
    StringBuilder sb = new StringBuilder();
    sb.append("States: ");
    buildStringList(states, sb);
    return sb.toString();
  }

  public String toStringInputSymbols() {
    StringBuilder sb = new StringBuilder();
    sb.append("Input symbols: ");
    buildStringList(inputSymbols, sb);
    return sb.toString();
  }

  public String toStringAcceptingStates() {
    StringBuilder sb = new StringBuilder();
    sb.append("Accepting states: ");
    buildStringList(acceptingStates, sb);
    return sb.toString();
  }

  public Grammar getCorrespondingGrammar() {
    Grammar grammar = new Grammar();
    for (String state : states) {
      grammar.addNonTerminal(state);
    }
    for (String inputSymbol : inputSymbols) {
      grammar.addTerminal(inputSymbol);
    }
    grammar.setStartingSymbol(startState);
    for (String accepting : acceptingStates) {
      if (accepting.equals(startState)) {
        grammar.addProduction(startState, Grammar.EMPTY);
      }
    }
    for (String state1 : transitions.keySet()) {
      for (String inputSymbol : transitions.get(state1).keySet()) {
        for (String state2 : transitions.get(state1).get(inputSymbol)) {
          grammar.addProduction(state1, inputSymbol + state2);
          for (String accepting : acceptingStates) {
            if (accepting.equals(state2)) {
              grammar.addProduction(state1, inputSymbol);
            }
          }
//          for (String acceptingState : acceptingStates) {
//            if (state2.equals(acceptingState)) {
//              grammar.addProduction(state2, Grammar.EMPTY);
//            }
//          }
        }
      }
    }
    return grammar;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(toStringStates());
    sb.append("\n");
    sb.append(toStringInputSymbols());
    sb.append("\n");
    sb.append(toStringTransitions());
    sb.append("\n");
    sb.append("Start state: ");
    sb.append(startState);
    sb.append("\n");
    sb.append(toStringAcceptingStates());
    return sb.toString();
  }
}
