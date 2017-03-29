package com.imaodou.mdlabapp.util;

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
}


