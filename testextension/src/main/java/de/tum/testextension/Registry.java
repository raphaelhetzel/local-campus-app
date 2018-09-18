package de.tum.testextension;

import java.util.ArrayList;

public class Registry {
    public static ArrayList<Class<?>> _classes = new ArrayList<Class<?>>();

    static{
        _classes.add(TestFragment.class);
    }
}
