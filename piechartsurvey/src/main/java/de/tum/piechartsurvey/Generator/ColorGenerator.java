package de.tum.piechartsurvey.Generator;

import android.content.Context;

import java.util.Random;

import de.tum.piechartsurvey.R;


public class ColorGenerator {

    public ColorGenerator() {
    }

    public static int getColor(Context context) {
        int[] appColors = new int[]{
                -14898782,
                -221181,
                -457957,
                -6355842,
                -3563613,
                -1917434,
                -10037756,
                -5828335,
                -10042222,
                -5144962
        };
        int randomColor = appColors[new Random().nextInt(appColors.length)];
        return randomColor;
    }

}