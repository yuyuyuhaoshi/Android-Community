package com.yhslib.android.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Email {
    public static boolean isEmail(String email) {
        String strPattern = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
