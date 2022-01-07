/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



import Pack1.Transactions;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adria
 */
public class Block implements Serializable {
    String timestamp;
    BigInteger index;
    ArrayList<Transactions> transactions;
    String previousHash;
    String hash;
    BigInteger nonce;
    public Block(String timestamp,ArrayList<Transactions> transactions,String previousHash){
        this.timestamp = timestamp;
        this.transactions = transactions;
        this.previousHash = previousHash;
        nonce = BigInteger.ZERO;
        this.hash = calchash();
        
    }

    public void setPreviousHash(String previousHash) {
        this.previousHash = previousHash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
    
    
    public String calchash(){
        String nr = "";
        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance("SHA-256");
            String str = this.previousHash+this.timestamp+transactions.toString()+this.nonce;
            byte[] hash = digest.digest(str.toString().getBytes(StandardCharsets.UTF_8));
            String actualhash = bytesToHex(hash);
            nr = actualhash;
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Block.class.getName()).log(Level.SEVERE, null, ex);
        }
      return nr;
    }
    
    public void Mine(int diff){
        String eq = previousHash.substring(this.previousHash.length()-diff, this.previousHash.length());
        while(!this.hash.substring(0, diff).equals(eq)){
            nonce = nonce.add(new BigInteger("1"));
            this.hash = calchash();
            //System.out.println(this.hash);
        }
        System.out.println("mined="+this.hash);
    }
    
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public BigInteger getIndex() {
        return index;
    }

    public void setIndex(BigInteger index) {
        this.index = index;
    }

    public ArrayList<Transactions> getTransactions() {
        return transactions;
    }

    public void setTransactions(ArrayList<Transactions> transactions) {
        this.transactions = transactions;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    
    
    
    @Override
    public String toString() {
        return "Block{" + "timestamp=" + timestamp + ", index=" + index + ", transactions=" + transactions + ", previousHash=" + previousHash + ", hash=" + hash + ", nonce=" + nonce + '}';
    }
    
    
    
}
