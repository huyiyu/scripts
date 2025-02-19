package com.cibidf.huyiyu;

public class StringConst {

  public static final String DESC = """
      - Show last 'count' lines in file:
          tail -n count path/to/file
          
          
      - Print the last lines of a given file and keep reading file until `Ctrl + C`:
          tail -f path/to/file""";

  public static final String HEADER = """
      tail
      Display the last part of a file.
      See also: `head`.
      More information: <https://manned.org/man/freebsd-13.0/tail.1>.
      """;
  public static final String EMPTY = "";
}
