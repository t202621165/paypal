package com.iwanol.paypal.third.hangtian;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ContextUtil {
	private static Properties props = new Properties();
    static {
        InputStream in = ContextUtil.class.getClassLoader().getResourceAsStream("hangtian.properties");
        try {
            props.load(in);
        } catch (IOException e1) {
            e1.printStackTrace();
        }finally {
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String getValue(String key) {
        return props.getProperty(key);
    }
}
