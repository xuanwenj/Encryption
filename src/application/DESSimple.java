package application;

import java.io.File;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author ahmed
 */
public class DESSimple {
    
    private SecretKey secretkey;
    private String algorithm;
    
    public DESSimple(String algorithm) throws NoSuchAlgorithmException 
    {
    	this.algorithm = algorithm;
        generateKey();
    }
    
    
    /**
	* Step 1. Generate a DES key using KeyGenerator 
    */
    
    public void generateKey() throws NoSuchAlgorithmException 
    {
        KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
        this.setSecretkey(keyGen.generateKey());        
    }
    
    public byte[] encrypt (String strDataToEncrypt) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {
        Cipher desCipher = Cipher.getInstance(algorithm); // Must specify the mode explicitly as most JCE providers default to ECB mode!!
        desCipher.init(Cipher.ENCRYPT_MODE, this.getSecretkey());
        byte[] byteDataToEncrypt = strDataToEncrypt.getBytes();
        byte[] byteCipherText = desCipher.doFinal(byteDataToEncrypt);       
        return byteCipherText;
    }
    
    public String decrypt (byte[] strCipherText) throws 
            NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, 
            InvalidAlgorithmParameterException, IllegalBlockSizeException, 
            BadPaddingException
    {        
        Cipher desCipher = Cipher.getInstance(algorithm); // Must specify the mode explicitly as most JCE providers default to ECB mode!!				
        desCipher.init(Cipher.DECRYPT_MODE, this.getSecretkey());        
        byte[] byteDecryptedText = desCipher.doFinal(strCipherText);        
        return new String(byteDecryptedText);
    }   

    /**
     * @return the secretkey
     */
    public SecretKey getSecretkey() {
        return secretkey;
    }

    /**
     * @param secretkey the secretkey to set
     */
    public void setSecretkey(SecretKey secretkey) {
        this.secretkey = secretkey;
    }
    
    /**
     * 
     * @throws FileNotFoundException
     * @throws IOException
     * save key to file
     */
      
    public void saveKeyToFile(File fileName) throws Exception
    {
    	FileOutputStream fs = new FileOutputStream(fileName);
   
    ObjectOutputStream oos = new ObjectOutputStream(fs);
    //CipherOutputStream cos = new CipherOutputStream(new FileOutputStream(fileName),**algorithem*);
    // Write the SecretKey object to the file
    oos.writeObject(this.secretkey);
    oos.close();
    }
    
    public SecretKey loadKeyFile(String fileName) throws IOException {
        byte[] keyBytes;
        try (FileInputStream fis = new FileInputStream(fileName)) {
            keyBytes = fis.readAllBytes();
        }
        return new SecretKeySpec(keyBytes, algorithm);
    }

    public SecretKey loadKeyFromFile() throws IOException {
        return loadKeyFile("src\\application\\keyFile.txt");
    }
    
    
}
