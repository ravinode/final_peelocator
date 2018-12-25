package com.peelocator.kira.streak.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


public class FlushUtil {

    public static String getID()
    {
        UUID id = UUID.randomUUID();
        String date = new SimpleDateFormat("yyMMddHHmmssZ").format(new Date());
        return id+"-"+date;
    }
}
