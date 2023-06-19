package com.jalloft.eventmaster.utils;

import android.graphics.Color;

public class ColorUtils {
    public static int getColor(String text) {
        int hashCode = Math.abs(text.hashCode());
        float factor = 0.9f;
        var color =  Color.rgb(Color.red(hashCode), Color.green(hashCode), Color.blue(hashCode));
        return Color.rgb((int) (Color.red(color) * factor),
                (int) (Color.green(color) * factor),
                (int) (Color.blue(color) * factor));
    }


}
