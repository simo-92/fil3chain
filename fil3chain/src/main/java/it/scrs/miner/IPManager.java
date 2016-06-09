/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.scrs.miner;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giordanocristini
 */
public class IPManager {
    private List<String> ipList;
    private static IPManager ipm;
    private IPManager() {
        ipList = new ArrayList<>();
    }
    
    public static IPManager getManager() {
        if(ipm == null){
            ipm = new IPManager();
            return ipm;
        }
        return ipm;
            
    }
    public List<String> getIPList() {
        return ipList;
    }
    public synchronized static void setAllIp(List<String> ips){
       ipm.getIPList().clear();
       ipm.getIPList().addAll(ips);
    }
    
    public synchronized static void addIP(String ip){
        if(!ipm.getIPList().contains(ip))
            ipm.getIPList().add(ip);
    }
    
    public synchronized static void removeIP(String ip){
        ipm.getIPList().remove(ip);
    }
}
