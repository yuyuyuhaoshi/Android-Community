package com.yhslib.android.util;

import java.text.DecimalFormat;

public class CoinType {
    public static String formatCoinNumber(String num) {
        Double number = Double.parseDouble(num);
        DecimalFormat fmt = new DecimalFormat("##0.0");
        if (number > 1000000000.0) {
            return fmt.format(number / 1000000000) + "B";
        }
        if (number > 1000000.0) {
            return fmt.format(number / 1000000.0) + "M";
        }
        if (number > 1000) {
            return fmt.format(number / 1000.0) + "M";
        }
        return num;
    }
}
