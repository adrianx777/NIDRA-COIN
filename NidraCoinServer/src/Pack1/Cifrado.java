/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Pack1;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 *
 * @author adria
 */
public class Cifrado {

    

    public PublicKey Publickeygen(String str){
        PublicKey key = null;
        try {
            byte[] publicKeyByteServer = Base64.getDecoder().decode(str);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKeyServer = (PublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(publicKeyByteServer));
//            System.out.println("publicKeyServer: " + publicKeyServer);
            return publicKeyServer;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            System.out.println("llave publica invalida");
//            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key;
    }
    
   public PrivateKey Privatekeygen(String str){
       PrivateKey key = null;
        try {
            byte[] PrivateKeyByteServer = Base64.getDecoder().decode(str);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey publicKeyServer = (PrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(PrivateKeyByteServer));
//            System.out.println("PrivateKeyServer: " + publicKeyServer);
            key = publicKeyServer;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeySpecException ex) {
            System.out.println("llave privada invalida");
//            Logger.getLogger(Cifrado.class.getName()).log(Level.SEVERE, null, ex);
        }
        return key;
    }
   
    public String encrypt(String message,PrivateKey privateKey) throws Exception{
        byte[] messageToBytes = message.getBytes();
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE,privateKey);
        byte[] encryptedBytes = cipher.doFinal(messageToBytes);
        return encode(encryptedBytes);
    }
    private String encode(byte[] data){
        return Base64.getEncoder().encodeToString(data);
    }

    public String decrypt(String encryptedMessage,PublicKey publicKey) throws Exception{
        byte[] encryptedBytes = decode(encryptedMessage);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE,publicKey);
        byte[] decryptedMessage = cipher.doFinal(encryptedBytes);
        return new String(decryptedMessage,"UTF8");
    }
    private byte[] decode(String data){
        return Base64.getDecoder().decode(data);
    }
}
