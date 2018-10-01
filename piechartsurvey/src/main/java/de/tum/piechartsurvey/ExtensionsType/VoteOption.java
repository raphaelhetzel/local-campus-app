package de.tum.piechartsurvey.ExtensionsType;

public class VoteOption {

    //All needed data to specify an option for voting

    private int id;
    private int color;
    private String text;

    //only as many different options for voting can be craated like colors defined in Colors class
    public static VoteOption getValidOption(int num, String text) {
        if (num < 0 || num >= Colors.colorsInternal.length) {
            return null;
        }
        return new VoteOption(num, text);
    }

    private VoteOption(int num, String text) {
        this.id = num;
        this.color = Colors.colorsInternal[num];    //colors are always chosen in the same order
        this.text = text;
    }


    public int getId() {
        return id;
    }

    public int getColor() {
        return color;
    }

    public String getText() {
        return text;
    }
}
