package scanner.utils;

/**
 * Created by Sergiu on 25.10.2015.
 */
public class StringWrapper {

  private String stringValue;
  private int hash = 0;

  public StringWrapper(String stringValue) {
    this.stringValue = stringValue;
  }

  public String getStringValue() {
    return stringValue;
  }

  public void setStringValue(String stringValue) {
    this.stringValue = stringValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    StringWrapper that = (StringWrapper) o;

    return !(stringValue != null ? !stringValue.equals(that.stringValue) : that.stringValue != null);

  }

  @Override
  public int hashCode() {
    int h = hash;
    char[] value = stringValue.toCharArray();
    if (h == 0 && value.length > 0) {
      char val[] = value;
      for (int i = 0; i < value.length; i++) {
        h = 31 * h + val[i];
      }
      hash = h;
    }
    return h;
  }
}
