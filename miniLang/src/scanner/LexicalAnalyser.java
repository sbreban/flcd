package scanner;

import scanner.datastructure.SeparateChainingHashST;
import scanner.utils.LexicalError;
import scanner.utils.Pair;
import scanner.utils.StringWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Sergiu on 22.10.2015.
 */
public class LexicalAnalyser {

  private final int MAX_SIZE = 100;
  private final int IDENTIFIER_MAX_LENGTH = 8;
  private final String IDENTIFIER = "identifier";
  private final String CONSTANT = "constant";

  private SeparateChainingHashST<StringWrapper, Integer> identifiers;
  private SeparateChainingHashST<StringWrapper, Integer> constants;
  private SeparateChainingHashST<StringWrapper, Integer> codification;
  private List<Pair<Integer, Integer>> pif;

  private String reservedSymbols = "()[]{}:;+*-<>=!&|";

  private final Path programFilePath;
  private final Path codificationFilePath;
  private final static Charset ENCODING = StandardCharsets.UTF_8;

  public LexicalAnalyser(String programFileName, String codificationFileName) {
    this.programFilePath = Paths.get(programFileName);
    this.codificationFilePath = Paths.get(codificationFileName);
    this.identifiers = new SeparateChainingHashST<>(MAX_SIZE);
    this.constants = new SeparateChainingHashST<>(MAX_SIZE);
    this.codification = new SeparateChainingHashST<>(MAX_SIZE);
    this.pif = new ArrayList<>();
  }

  public static void main(String[] args) throws IOException {
    LexicalAnalyser parser = new LexicalAnalyser("C:\\Users\\Sergiu\\IdeaProjects\\miniLang\\program.txt", "C:\\Users\\Sergiu\\IdeaProjects\\miniLang\\codification.txt");
    parser.createCodificationTable();
    try {
      parser.processLineByLine();
      parser.printPif();
    } catch (IOException ioex) {
      log(ioex);
    } catch (LexicalError leex) {
      log(leex);
    }
  }

  private void createCodificationTable() throws IOException {
    try (Scanner scanner =  new Scanner(codificationFilePath, ENCODING.name())){
      while (scanner.hasNextLine()){
        String line = scanner.nextLine();
        Scanner lineScanner = new Scanner(line);
        if (lineScanner.hasNext()) {
          String name = lineScanner.next();
          Integer value = Integer.parseInt(lineScanner.next());
          codification.put(new StringWrapper(name), value);
        }
      }
    }
  }

  private void printCodificationTable() {
    for (StringWrapper key : codification.keys()) {
      log(key + ":" + codification.get(key));
    }
  }

  private void printPif() {
    for (Pair pair : pif) {
      log(pair.getName() + " -> " + pair.getValue());
    }
  }

  private final void processLineByLine() throws IOException, LexicalError {
    try (Scanner scanner =  new Scanner(programFilePath, ENCODING.name())){
      while (scanner.hasNextLine()){
        processLine(scanner.nextLine());
      }
    }
  }

  private void processLine(String aLine) throws LexicalError {
    Scanner scanner = new Scanner(aLine);
    while (scanner.hasNext()) {
      String value = scanner.next();
      processWord(value);
    }
  }

  private void processWord(String aWord) throws LexicalError {
    int i = 0;
    while (i < aWord.length()) {
      char c = aWord.charAt(i);
      if (c == '_') {
        StringBuilder identifier = new StringBuilder();
        identifier.append(c);
        i++;
        c = aWord.charAt(i);
        while ((Character.isDigit(c) || Character.isLetter(c)) && i < aWord.length()) {
          identifier.append(c);
          i++;
          if (i < aWord.length()) {
            c = aWord.charAt(i);
          }
        }
        if (identifier.length() > IDENTIFIER_MAX_LENGTH) {
          throw new LexicalError("Identifier name too long");
        }
        if (identifiers.get(new StringWrapper(identifier.toString())) == null) {
          identifiers.put(new StringWrapper(identifier.toString()), identifiers.size());
        }
        pif.add(new Pair(codification.get(new StringWrapper(IDENTIFIER)), identifiers.get(new StringWrapper(identifier.toString()))));
      } else if (reservedSymbols.indexOf(c) >= 0) {
        StringBuilder symbol = new StringBuilder();
        symbol.append(c);
        i++;
        char next;
        if (i < aWord.length()) {
          next = aWord.charAt(i);
          if (c == '<') {
            if (next == '-' || next == '=') {
              symbol.append(next);
              i++;
            }
          } else if (c == '>') {
            if (next == '=') {
              symbol.append(next);
              i++;
            }
          } else if (c == '|') {
            if (next == '|') {
              symbol.append(next);
              i++;
            }
          } else if (c == '&') {
            if (next == '&') {
              symbol.append(next);
              i++;
            }
          } else if (c == '!') {
            if (next == '=') {
              symbol.append(next);
              i++;
            }
          } else if (c == '=') {
            if (next == '=') {
              symbol.append(next);
              i++;
            }
          }
        }
        if (codification.get(new StringWrapper(symbol.toString())) != null) {
          pif.add(new Pair(codification.get(new StringWrapper(symbol.toString())), -1));
        }
      } else {
        StringBuilder word = new StringBuilder();
        word.append(c);
        i++;
        if (i < aWord.length()) {
          c = aWord.charAt(i);
          while (reservedSymbols.indexOf(c) < 0 && i < aWord.length()) {
            word.append(c);
            i++;
            if (i < aWord.length()) {
              c = aWord.charAt(i);
            }
          }
        }
        if (word.length() > 0) {
          if (codification.get(new StringWrapper(word.toString())) != null) {
            pif.add(new Pair(codification.get(new StringWrapper(word.toString())), -1));
          } else if (isNumeric(word.toString()) || (word.toString().startsWith("'") && word.toString().endsWith("'"))) {
            if (constants.get(new StringWrapper(word.toString())) == null) {
              constants.put(new StringWrapper(word.toString()), constants.size());
            }
            pif.add(new Pair(codification.get(new StringWrapper(CONSTANT)), constants.get(new StringWrapper(word.toString()))));
          }
        }
      }
    }
  }

  public static boolean isNumeric(String str)
  {
    try
    {
      Double.parseDouble(str);
    }
    catch(NumberFormatException nfe)
    {
      return false;
    }
    return true;
  }

  private static void log(Object aObject){
    System.out.println(String.valueOf(aObject));
  }
}
