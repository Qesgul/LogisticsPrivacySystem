package com.example.rsa_encrypt;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;

import javax.crypto.Cipher;
import sun.misc.BASE64Decoder;
public class EncAndDec {
    public static final String KEY_ALGORITHM="RSA";
    public static final String SIGNATURE_ALGORITHM="MD5withRSA";
    public static String str_pubK = "";
    public static String str_priK = "";


      public static PublicKey getPublicKey(String pubKey) throws Exception {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(pubKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
      }


      public static PrivateKey getPrivateKey(String priKey) throws Exception {
            byte[] keyBytes;
            keyBytes = (new BASE64Decoder()).decodeBuffer(priKey);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
      }

/*      //***************************ǩ�����֤*******************************
      public static byte[] sign(byte[] data) throws Exception{
        PrivateKey priK = getPrivateKey(str_priK);
          Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);        
          sig.initSign(priK);
          sig.update(data);
          return sig.sign();
      }
      
      public static boolean verify(byte[] data,byte[] sign) throws Exception{
          PublicKey pubK = getPublicKey(str_pubK);
          Signature sig = Signature.getInstance(SIGNATURE_ALGORITHM);
          sig.initVerify(pubK);
          sig.update(data);
          return sig.verify(sign);
      }*/
      
      //************************���ܽ���**************************
      public static byte[] encrypt(byte[] bt_plaintext)throws Exception{
          PublicKey publicKey = getPublicKey(str_pubK);
          Cipher cipher = Cipher.getInstance("RSA");
          cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] bt_encrypted = cipher.doFinal(bt_plaintext);
        return bt_encrypted;
      }
      
      public static byte[] decrypt(byte[] bt_encrypted)throws Exception{
        PrivateKey privateKey = getPrivateKey(str_priK);
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] bt_original = cipher.doFinal(bt_encrypted);
        return bt_original;
      }
      //********************main������ܽ��ܺ�ǩ����֤*********************
      public String startEncryption(String pubKey,String text){
    	  Map<String, Object> keyMap;
    	  String miwen1="";
    	  try {
    		  str_pubK=  pubKey;
    		  } catch (Exception e) { 
    			  e.printStackTrace();
    		  }
            
            byte[] bt_cipher;
			try {
				bt_cipher = encrypt(text.getBytes());
	            miwen1=new String(bt_cipher,"ISO-8859-1");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return miwen1;
/*            System.out.println("���ܺ�"+miwen1);
            
            byte[] miwen2=miwen1.getBytes("ISO-8859-1");
            byte[] bt_original = decrypt(miwen2);
            String str_original = new String(bt_original);
            System.out.println("���ܽ��:"+str_original);*/
      }
      public String startDecrypt(String priKey,String text){
    	  Map<String, Object> keyMap;
    	  String str_original="";
    	  try {
    		  str_priK =priKey;
    		  } catch (Exception e) { 
    			  e.printStackTrace();
    		  }
            byte[] miwen2;
			try {
				miwen2 = text.getBytes("ISO-8859-1");
	            byte[] bt_original = decrypt(miwen2);
	            str_original = new String(bt_original);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            return str_original;
      }
}



 
 
