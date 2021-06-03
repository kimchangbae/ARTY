package com.arty.Common;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {
    public String TO_DAY = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));


}
