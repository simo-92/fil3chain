/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.scrs.miner.util;

import java.util.Objects;

/**
 *
 * @author giordanocristini
 */
public class IP implements Cloneable{
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
    public boolean equals(Object o){
        return user_ip.equals(((IP)o).getIp());
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + Objects.hashCode(this.user_ip);
        return hash;
    }
}
