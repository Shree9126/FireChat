package com.mindnotix.mnxchats.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommonMethods {
    private static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static DateFormat timeFormat = new SimpleDateFormat("K:mma");

    public static String getCurrentTime() {

        Date today = Calendar.getInstance().getTime();
        return timeFormat.format(today);
    }

    public static String getCurrentDate() {

     /*   Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
*/

        Date today;
        String strDate;
        DateFormat dateFormat;
        dateFormat = DateFormat
                .getDateInstance(DateFormat.MEDIUM);
        today = new Date();
        strDate = dateFormat.format(today);
        System.out.println("dateformat_mm_dd_yyyy_today"+today);
        System.out.println("dateformat_mm_dd_yyyy"+strDate);

        return strDate;
    }

}
