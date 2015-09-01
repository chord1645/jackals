package jackals.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;


public class EncryptConfig extends Thread {
    private Logger logger = LoggerFactory.getLogger(getClass());
    static String key;
    static String encoding = "utf-8";
    //    static String path = "D:\\work\\workspace\\crawler\\jackals\\src\\main\\resources\\jar\\config\\config-jackals.properties";
    static String path1 = "D:\\work\\workspace\\jackals\\jackals-core\\src";
    static String path2 = "D:\\work\\workspace\\jackals\\jackals-web\\src";


    public static void main(String[] args) throws IOException {
        BufferedReader strin = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("密码");
        key = strin.readLine();
        System.out.println("操作");
        String op = strin.readLine();
//        String content = "test";
//        String password = "1234l5678";
//        System.out.println(content);
//        String s1 = encrypt64(content, password);
//        System.out.println(s1);
//        String s2 = decrypt64(s1, password);
//        System.out.println(s2);
        //生成加密文件
        if ("e".equals(op)) {//加密
            encryptAll(new File(path1));
            encryptAll(new File(path2));
        } else if ("c".equals(op)) {//加密+清空
            encryptAndClean(new File(path1));
            encryptAndClean(new File(path2));
        } else if ("r".equals(op)) {//恢复
            revertAll(new File(path1));
            revertAll(new File(path2));

        }

    }

    private static void encryptAll(File file) {
        if (file.isFile()) {
            if (file.getName().endsWith(".properties")) {
                encrypt(file);
            }

        } else {
            for (File f : file.listFiles()) {
                encryptAll(f);
            }
        }
    }


    private static void encryptAndClean(File file) {
        if (file.isFile()) {
            if (file.getName().endsWith(".properties")) {
                encrypt(file);
                cleanValue(file);
            }

        } else {
            for (File f : file.listFiles()) {
                encryptAndClean(f);
            }
        }
    }

    private static void revertAll(File file) {
        if (file.isFile()) {
            if (file.getName().endsWith(".properties")) {
                revert(file);
            }

        } else {
            for (File f : file.listFiles()) {
                revertAll(f);
            }
        }
    }

    private static void revert(File file) {
        File encFile = new File(file.getParent(), file.getName() + ".enc");
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            System.out.println(file.getPath());

            bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, false)));
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(encFile), encoding));
            String s = null;
            for (; (s = br.readLine()) != null; ) {
                String line = decrypt64(s, key);
                bw.write(line + "\n");
                System.out.println(line);
            }
            System.out.println("====================================");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void cleanValue(File file) {
        File encFile = new File(file.getParent(), file.getName() + ".enc");
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            System.out.println(file.getPath());

            bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(file, false)));
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(encFile), encoding));
            String s = null;
            for (; (s = br.readLine()) != null; ) {
//                String kv = StringUtil.regxGet("(\\w+\\s*=\\s*\".+?\")", 1, s);
//                if (!StringUtils.isEmpty(kv))
//                    System.out.println(file.getName()+" "+kv);
                String line = decrypt64(s, key);
                line = line.replaceAll("=\\s*.*", "=");
                bw.write(line + "\n");
                System.out.println(line);

            }
            System.out.println("====================================");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    Pattern pattern = Pattern.compile("");

    private static void encrypt(File file) {
        File encFile = new File(file.getParent(), file.getName() + ".enc");
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            System.out.println(file.getPath());

            bw = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(encFile, false)));
            br = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), encoding));
            String s = null;
            for (; (s = br.readLine()) != null; ) {
//                String kv = StringUtil.regxGet("(\\w+\\s*=\\s*\".+?\")", 1, s);
//                if (!StringUtils.isEmpty(kv))
//                System.out.println(encrypt64(s, key));

                String line = encrypt64(s, key);
                System.out.println(s);
                System.out.println(line);
                bw.write(line + "\n");
            }
            System.out.println("====================================");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    public static byte[] encrypt(String content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            byte[] byteContent = content.getBytes(encoding);
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(byteContent);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String encrypt64(String content, String password) {
        byte[] bt = encrypt(content, password);
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(bt).replaceAll("\\s", "");
    }

    public static String decrypt64(String str64, String password) {
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            byte[] bt64 = decoder.decodeBuffer(str64);
            byte[] bt = decrypt(bt64, password);
            return new String(bt, encoding);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }


}