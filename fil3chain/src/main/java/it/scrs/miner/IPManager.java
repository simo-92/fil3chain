/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.scrs.miner;

import it.scrs.miner.util.IP;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author giordanocristini
 */
public class IPManager {
    private final List<IP> ipList;
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
    public synchronized List<IP> getIPList() {
        return ipList;
        
    }
    public synchronized  void setAllIp(List<IP> ips){
       ipm.getIPList().clear();
       ipm.getIPList().addAll(ips);
       
    }
    
    public synchronized int indexOf(String ip){
        for(int i=0;i<ipList.size();i++)
            if(ipList.get(i).getIp().equals(ip))
                return i;
        return -1;
    }
    
    public synchronized  void addIP(IP ip){
        if(ipm.indexOf(ip.getIp())<0)
            ipm.getIPList().add(ip);
    }
    
    public synchronized  void removeIP(IP ip){
        int index=indexOf(ip.getIp());
        if(index>=0)
            ipm.getIPList().remove(index);
    }
}
