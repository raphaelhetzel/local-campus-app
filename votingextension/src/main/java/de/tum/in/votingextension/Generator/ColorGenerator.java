package de.tum.in.votingextension.Generator;

import android.content.Context;
import java.util.Random;

import de.tum.in.votingextension.R;


public class ColorGenerator {

    public ColorGenerator(){}

    public static int getColor(Context context){
        int[] appColors = context.getResources().getIntArray(R.array.CampusAppColors);
        int randomColor = appColors[new Random().nextInt(appColors.length)];
        return randomColor;
    }


}