package org.fordes.subview.util.SettingUtil;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SettingUtil {


        public static boolean CheckTiming(int high,int low){
            int date = Integer.valueOf(new SimpleDateFormat("HH").format(new Date()));
            return ((high>low&&(date>=high&&date<=24||date<low))||(high<low)&&(date>=high&&date<low))?true:false;
        }

    public static void main(String[] args) {

    }

}
