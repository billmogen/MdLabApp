package com.imaodou.mdlabapp.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by billmogen on 2017/3/28.
 */

public class ToolsUtil {
    public ToolsUtil() {}

    public static byte[] hex2Bytes(String src){
        byte[] res = new byte[src.length()/2];
        char[] chs = src.toCharArray();
        for(int i=0,c=0; i<chs.length; i+=2,c++){
            res[c] = (byte) (Integer.parseInt(new String(chs,i,2), 16));
        }

        return res;
    }

    public static int[] ubyteArr2IntArr(byte[] arr) {
        int[] result = new int[arr.length];
        for (int i = 0; i < arr.length; i++) {
            result[i]  = arr[i];
            if (result[i] <  0)
                result[i] += 256;
        }
        return result;
    }

    public static long ubyteArr2Long(byte[] arr, int startIndex, int size) {
        /**
         * 最高位是arr[startIndex], 最低位是arr[endIndex - 1]
         * @return result = arr[startIndex],,,,arr[endIndex-1]
         */
        long result = 0;
        for (int i = startIndex; i < startIndex + size; i++) {
            int tmp = arr[i];
            if (tmp < 0) {
                tmp += 256;
            }
            result = (result << 8) | tmp;
        }
        return result;
    }

    public static int getSomePrefToInt(Context context, String key) {
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefs.getString(key, null);
        return value == null ? -1 : Integer.valueOf(value);
    }

    public static boolean getSomePrefToBool(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean checkbox = prefs.getBoolean(key, false);
        return checkbox;
    }

}


