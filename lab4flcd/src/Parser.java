import java.util.Map;

/**
 * Created by Sergiu on 05.01.2016.
 */
public class Parser {

  private Grammar grammar;
  private Automata automata;
  private Map<String, Closure> clojures;
  private ParseTable parseTable;

  public Grammar getGrammar() {
    return grammar;
  }

  public void setGrammar(Grammar grammar) {
    this.grammar = grammar;
  }

  public Automata getAutomata() {
    return automata;
  }

  public void setAutomata(Automata automata) {
    this.automata = automata;
  }

  public Map<String, Closure> getClojures() {
    return clojures;
  }

  public void setClojures(Map<String, Closure> clojures) {
    this.clojures = clojures;
  }

  public ParseTable getParseTable() {
    return parseTable;
  }

  public void setParseTable(ParseTable parseTable) {
    this.parseTable = parseTable;
  }
}
