package de.tum.votingextension.Generator;
import java.util.Random;


public class ColorGenerator {

    public ColorGenerator(){}

    public static int getColor(){
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