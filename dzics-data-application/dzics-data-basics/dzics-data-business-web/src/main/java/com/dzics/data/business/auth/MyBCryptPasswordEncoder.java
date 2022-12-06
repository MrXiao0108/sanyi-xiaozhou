package com.dzics.data.business.auth;

import com.dzics.data.common.util.md5.Md5Util;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author ZhangChengJun
 * Date 2020/5/18.
 */
@Component
public class MyBCryptPasswordEncoder extends BCryptPasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return Md5Util.md5((String) rawPassword);
    }

    /**
     *
     * @param rawPassword 传递进来的密码
     * @param encodedPassword 加密后密码
     * @return
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        String s = Md5Util.md5((String) rawPassword);
        return encodedPassword.equals(s);
    }



}
