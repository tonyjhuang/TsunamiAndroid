package com.tonyjhuang.tsunami.api.parsers;

import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Created by tony on 12/26/14.
 */
public class TsunamiDouble {
    public static DecimalFormat formatter = new DecimalFormat("#.##");
    static { formatter.setRoundingMode(RoundingMode.HALF_UP); }

    public static double format(double original) {
        return Double.valueOf(formatter.format(original));
    }
}
