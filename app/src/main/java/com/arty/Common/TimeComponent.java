package com.arty.Common;

import android.util.Log;

import java.text.SimpleDateFormat;

public class TimeComponent {
    public static class TIME_MAXIMUM {
        public static final int SEC     = 60;
        public static final int MIN     = 60;
        public static final int HOUR    = 24;
        public static final int DAY     = 30;
    }

    public String switchTime(String regTime) {
        String msg = null;
        try {
            long curTime = System.currentTimeMillis();
            long diffTime = (curTime - Long.valueOf(regTime)) / 1000;

            if (diffTime < TIME_MAXIMUM.SEC) {
                msg = "방금 전";
            } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
                msg = diffTime + "분 전";
            } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
                msg = (diffTime) + "시간 전";
            } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
                msg = (diffTime) + "일 전";
            } else {
                msg = new SimpleDateFormat("yyyy/MM/dd").format(regTime);
            }

        } catch (Exception e) {
            Log.e("TimeComponent", e.getMessage());
            e.printStackTrace();
        }
        return msg;
    }
}
