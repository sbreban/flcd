import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sergiu on 05.01.2016.
 */
public class ParseTable {

  private Map<String, String> actionTable;
  private Map<String, Map<String, String>> goToTable;

  public ParseTable() {
    actionTable = new HashMap<>();
    goToTable = new HashMap<>();
  }

  public void addAction(String state, String action) {
    actionTable.put(state, action);
  }

  public void addGoTo(String startState, String symbol, String endState) {
    Map<String, String> startStateMap = goToTable.get(startState);
    if (startStateMap == null) {
      startStateMap = new HashMap<>();
      goToTable.put(startState, startStateMap);
    }
    startStateMap.put(symbol, endState);
  }

  public String getAction(String state) {
    return actionTable.get(state);
  }

  public String getGoTo(String state, String symbol) {
    return goToTable.get(state) != null ? goToTable.get(state).get(symbol) : null;
  }
}
