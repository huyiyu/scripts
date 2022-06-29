package com.huyiyu.excel.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {
    public static String toString(Exception exception) {
        StringWriter strw = new StringWriter();
        try (
            PrintWriter printWriter = new PrintWriter(strw)
        ) {
            exception.printStackTrace(printWriter);
            return strw.toString();
        }
    }
}
