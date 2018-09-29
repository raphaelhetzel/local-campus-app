package de.tum.in.piechartsurvey.ExtensionsType;

public class VoteOption {

    private int id;
    private int color;
    private String text;


    public static VoteOption getValidOption(int num, String text) {
        if (num < 0 || num >= Colors.colorsInternal.length) {
            return null;
        }
        return new VoteOption(num, text);
    }

    private VoteOption(int num, String text) {
        this.id = num;
        this.color = Colors.colorsInternal[num];
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
