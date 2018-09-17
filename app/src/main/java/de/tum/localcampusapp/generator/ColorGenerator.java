package de.tum.localcampusapp.generator;

import android.content.Context;

import java.util.HashMap;
import java.util.Random;
import de.tum.localcampusapp.R;

public class ColorGenerator {

    private HashMap<Long, Integer> postsColor = new HashMap<Long, Integer>();

    private static ColorGenerator instance = new ColorGenerator();

    private ColorGenerator() {
    }

    public static ColorGenerator getInstance(){
        return instance;
    }


    public void setColor(Context context, long postId){
        int[] appColors = context.getResources().getIntArray(R.array.CampusAppColors);
        int randomColor = appColors[new Random().nextInt(appColors.length)];
        postsColor.put(postId, randomColor);;
    }

    public int getSetColor(Context context, long postId){
        if(postsColor.containsKey(postId)){
            return postsColor.get(postId);
        }
        setColor(context, postId);
        return getSetColor(context, postId);
    }

    public int getColor(long postId){
        return postsColor.get(postId);
    }

    public int getColor(Context context){
        int[] appColors = context.getResources().getIntArray(R.array.CampusAppColors);
        int randomColor = appColors[new Random().nextInt(appColors.length)];
        return randomColor;
    }


}