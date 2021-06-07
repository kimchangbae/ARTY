package com.arty.Common;

import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Common {
    public String TO_DAY = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));

    final String randomKey = UUID.randomUUID().toString();

    public boolean validationEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }
}
