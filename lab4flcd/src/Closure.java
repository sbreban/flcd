import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sergiu on 09.01.2016.
 */
public class Closure {
  private List<String> items;

  public Closure() {
    items = new ArrayList<>();
  }

  public Closure(List<String> items) {
    this.items = items;
  }

  public List<String> getItems() {
    return items;
  }

  public void setItems(List<String> items) {
    this.items = items;
  }

  public void addItem(String item) {
    items.add(item);
  }

  public void addItems(List<String> itemsToAdd) {
    items.addAll(itemsToAdd);
  }

  public boolean contains(String item) {
    return items.contains(item);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Closure closure = (Closure) o;

    return items != null ? items.equals(closure.items) : closure.items == null;

  }

  @Override
  public int hashCode() {
    return items != null ? items.hashCode() : 0;
  }
}
