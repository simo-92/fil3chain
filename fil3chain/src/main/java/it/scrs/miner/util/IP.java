/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.scrs.miner.util;

/**
 *
 * @author giordanocristini
 */
public class IP implements Cloneable, Comparable{
    private String user_ip;

    public String getIp() {
        return user_ip;
    }

    public void setIp(String ip) {
        this.user_ip = ip;
    }

    public IP(String ip) {
        this.user_ip = ip;
    }
    
    @Override
    protected Object clone(){
        return new IP(this.toString());
    }
    
    @Override
    public String toString() {
        return user_ip;
    }

    @Override
    public int compareTo(Object t) {
        return user_ip.compareTo(((IP)t).getIp());
    }
}
