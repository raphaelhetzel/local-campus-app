package de.tum.localcampusapp.generator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTransformer {
    //Generates a normal looking date for a user
    public static String getTimeDate(Date date){
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm");
        return DATE_FORMAT.format(date);
    }
    
}
