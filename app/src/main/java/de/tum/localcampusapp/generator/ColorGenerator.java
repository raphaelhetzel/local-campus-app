package de.tum.localcampusapp.generator;

import android.content.Context;

import java.util.HashMap;
import java.util.Random;
import de.tum.localcampusapp.R;

public class ColorGenerator {

    public ColorGenerator(){}

    //chooses only colors which look good in combination with the other components used in recyclerViews and other views
    public static int getColor(Context context){
        int[] appColors = context.getResources().getIntArray(R.array.CampusAppColors);
        int randomColor = appColors[new Random().nextInt(appColors.length)];
        return randomColor;
    }


}