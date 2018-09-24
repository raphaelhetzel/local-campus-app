package de.tum.in.postcreater.Generator;

import android.content.Context;
import java.util.Random;
import de.tum.in.postcreater.R;


public class ColorGenerator {

    public ColorGenerator(){}

    public static int getColor(Context context){
        int[] appColors = context.getResources().getIntArray(R.array.CampusAppColors);
        int randomColor = appColors[new Random().nextInt(appColors.length)];
        return randomColor;
    }

}