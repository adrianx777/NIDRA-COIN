/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nidra.coin;

import java.io.Serializable;

/**
 *
 * @author adria
 */
public class Transactions implements Serializable {
    String fromAddress,toAddress;
    int amount;
    public Transactions(String fromAddress,String toAddress,int amount){
        this.fromAddress = fromAddress;
        this.toAddress = toAddress;
        this.amount = amount;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
    
    

    @Override
    public String toString() {
        return "Transactions{" + "fromAddress=" + fromAddress + ", toAddress=" + toAddress + ", amount=" + amount + '}';
    }
    
}
