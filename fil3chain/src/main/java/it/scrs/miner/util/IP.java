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
public class IP implements Cloneable{
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public IP(String ip) {
        this.ip = ip;
    }
    
    @Override
    protected Object clone(){
        return new IP(this.toString());
    }
}
