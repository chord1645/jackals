package jackals.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;  
import java.security.NoSuchAlgorithmException;  
import java.security.SecureRandom;  
  
import javax.crypto.BadPaddingException;  
import javax.crypto.Cipher;  
import javax.crypto.IllegalBlockSizeException;  
import javax.crypto.KeyGenerator;  
import javax.crypto.NoSuchPaddingException;  
import javax.crypto.SecretKey;  
import javax.crypto.spec.SecretKeySpec;  
  

public class AES {  

     public static byte[] encrypt(String content, String password) {   
             try {              
                     KeyGenerator kgen = KeyGenerator.getInstance("AES");   
                     kgen.init(128, new SecureRandom(password.getBytes()));   
                     SecretKey secretKey = kgen.generateKey();   
                     byte[] enCodeFormat = secretKey.getEncoded();   
                     SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");   
                     Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
                     byte[] byteContent = content.getBytes("utf-8");   
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
        
        

      public static void main(String[] args) {  
          String content = "test";   
          String password = "12345678";   
          //加密   
         System.out.println("加密前：" + content);   
         byte[] encryptResult = encrypt(content, password);  
//         String tt4 = Base64.encode(encryptResult);
         System.out.println(new String(encryptResult));
           
          //解密   
         byte[] decryptResult = decrypt(encryptResult,password);   
         System.out.println("解密后：" + new String(decryptResult));   
    }  
}  