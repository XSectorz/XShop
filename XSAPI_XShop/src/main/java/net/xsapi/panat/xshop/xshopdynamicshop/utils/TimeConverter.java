package net.xsapi.panat.xshop.xshopdynamicshop.utils;

import net.xsapi.panat.xshop.xshopdynamicshop.configuration.messages;

public class TimeConverter {

    public String getTime(long millis) {
        String time = "";

        int minutes = (int) ((millis / (1000*60)) % 60);
        int hours   = (int) ((millis / (1000*60*60)) % 24);
        int days = (int) (millis / (1000*60*60*24));

        String str_day = messages.customConfig.getString("time_days");
        String str_hour = messages.customConfig.getString("time_hours");
        String str_minute = messages.customConfig.getString("time_minutes");

        if(days > 0) {
            if(days <= 1) {
                str_day = messages.customConfig.getString("time_day");
            }
            time += days + str_day + " ";
        }
        if(hours > 0) {
            if(hours <= 1) {
                str_hour = messages.customConfig.getString("time_hour");
            }
            time += hours + str_hour + " ";
        }
        if(minutes > 0) {
            if(minutes <= 1) {
                str_minute = messages.customConfig.getString("time_minute");
            }
            time += minutes + str_minute + " ";
        }

        if(days == 0 && hours == 0 && minutes == 0) {
            return messages.customConfig.getString("time_soon");
        }

        if(millis < 0) {
            return messages.customConfig.getString("time_soon");
        }

        return time;
    }

}
