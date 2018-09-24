package de.tum.in.commentsextensionmodule.Generator;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTransformer {

    public static String getTimeDate(Date date){
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yy HH:mm");
        return DATE_FORMAT.format(date);
    }
    
}
