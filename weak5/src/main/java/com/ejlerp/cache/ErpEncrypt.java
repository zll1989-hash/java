//package com.ejlerp.cache;
//
//import org.jasypt.util.text.BasicTextEncryptor;
//
//public class ErpEncrypt {
//    public static void main(String[] args) {
//        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
//        //加密所需的salt(盐)
//        textEncryptor.setPassword("G0CvDz7oJn6");
//        //要加密的数据（数据库的用户名或密码）
//        String url = textEncryptor.encrypt("jdbc:mysql://rm-bp18t5la5w2b68nt6.mysql.rds.aliyuncs.com:3306/egenie_dev");
//        String username = textEncryptor.encrypt("egenie_dev");
//        String password = textEncryptor.encrypt("Ejldev1806");
//        System.out.println("url:" + url);
//        System.out.println("username:" + username);
//        System.out.println("password:" + password);
//    }
//}
