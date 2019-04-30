/*
 * Created on :2013-6-21 Author :songlin.li Change History Version Date Author
 * Reason <Ver.No> <date> <who modify> <reason>
 */
package cn.wuxia.project.security.common;

import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.MessageDigestPasswordEncoder;

public class MyPasswordEncoder {
    private static final String PREFIX = "{";

    private static final String SUFFIX = "}";

    /**
     * String salt = KeyGenerators.string().generateKey();
     */
    /**
     * 兼容旧版cas登录
     * @author songlin
     * @param rawPassword
     * @param salt
     * @return
     */
    @Deprecated
    public String encodePassword(String rawPassword, Object salt) {
        return encodeMD5Password(rawPassword);
    }

    @Deprecated
    public String encodeMD5Password(String rawPassword) {
        String passwd = new MessageDigestPasswordEncoder("MD5").encode(rawPassword);
        return passwd;
    }

    public String encodePassword(String rawPassword) {
        String passwd = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(rawPassword);
        return passwd;
    }

    public boolean isPasswordValid(String databasePassword, String inputPassword) {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder().matches(inputPassword, databasePassword);
    }

    /**
     * 兼容旧版security密码校验
     * @author songlin
     * @param databasePassword
     * @param inputPassword
     * @return
     */
    @Deprecated
    public boolean isPasswordValid(String databasePassword, String inputPassword, Object salt) {
        if (salt != null) {
            databasePassword = "{MD5}{" + salt + "}" + databasePassword;
        } else {
            databasePassword = "{MD5}" + databasePassword;
        }
        return isPasswordValid(databasePassword, inputPassword);
    }

    public static String extractSalt(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(PREFIX);
        if (start != 0) {
            return "";
        }
        int end = prefixEncodedPassword.indexOf(SUFFIX, start);
        if (end < 0) {
            return "";
        }
        return prefixEncodedPassword.substring(start + 1, end);
    }

    public static String extractPassword(String prefixEncodedPassword) {
        int start = prefixEncodedPassword.indexOf(PREFIX);
        if (start != 0) {
            return "";
        }
        int end = prefixEncodedPassword.indexOf(SUFFIX, start);
        if (end < 0) {
            return "";
        }
        return prefixEncodedPassword.substring(end + 1, prefixEncodedPassword.length());
    }

    public static void main(String[] args) {
        MyPasswordEncoder encoder = new MyPasswordEncoder();
        //String salt = encoder.getGenerateSalt();
        System.out.println("      " + encoder.encodePassword("8888"));
        String passwordk = encoder.encodePassword("8888", "");
        System.out.println("   passwordk   " + passwordk);
        System.out.println(encoder.extractSalt(passwordk));
        System.out.println(encoder.extractPassword(passwordk));

        System.out.println("" + encoder.isPasswordValid("{MD5}" + passwordk, "8888"));
        System.out.println(encoder.isPasswordValid(encoder.extractPassword(passwordk), "8888", encoder.extractSalt(passwordk)));

        System.out.println(encoder.isPasswordValid("05b4ddeb1cb978c39387e6ca07d8695a", "qwe123", "68bc592397bbbca6"));
        // System.out.println(encoder.matches("12345", "1234"));


        /**
         * 旧密码策略
         */
        String pw = "b2e423d4482c8eb3f77b436b7800eb23";
        String salt = "00fd0a874365621f";


        /**
         * 新密码策略
         */
        String pw1 = "3fe4de6a6708060f09241c6b757a327f";
        String salt1 = "UHdFfC6Zrw67WEM6gPA8jKyOqEDbSLFi5QnDILodfNU=";


        String md5password = encoder.encodeMD5Password("zmtt888");
        System.out.println(MyPasswordEncoder.extractPassword(md5password));
        System.out.println(MyPasswordEncoder.extractSalt(md5password));
    }
}
