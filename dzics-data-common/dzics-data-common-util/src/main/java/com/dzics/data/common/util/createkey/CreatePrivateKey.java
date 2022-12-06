package com.dzics.data.common.util.createkey;

import java.util.Random;

/**
 * @author ZhangChengJun
 * Date 2020/5/25.
 */
public class CreatePrivateKey {
    public static String privateKey(int keyLen) {
        String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%&amp;*:_+&lt;&gt;?~#$@";
        Random random = new Random();
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < keyLen; i++) {
            str.append(base.charAt(random.nextInt(base.length())));
        }
        return str.toString();
    }
}
