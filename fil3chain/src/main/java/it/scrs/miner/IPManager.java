/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.scrs.miner;

import it.scrs.miner.util.IP;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author giordanocristini
 */
public class IPManager {
	private final List<IP> ipList;
	private static IPManager ipm;

	private IPManager() {
		ipList = Collections.synchronizedList(new ArrayList<>());
	}

	public static IPManager getManager() {
		if (ipm == null) {
			ipm = new IPManager();
		}
		return ipm;

	}

	public List<IP> getIPList() {
        synchronized (ipList) {
		    return new ArrayList<>(ipList);
        }
	}

	public void setAllIp(List<IP> ips) {
		synchronized (ipList) {
            ipList.clear();
            ipList.addAll(ips);
        }

	}

	public int indexOf(String ip) {
        synchronized (ipList) {
            for (int i = 0; i < ipList.size(); i++)
                if (ipList.get(i).getIp().equals(ip))
                    return i;
            return -1;
        }
	}

	public void addIP(IP ip) {
        synchronized (ipList) {
            if (ipList.indexOf(ip.getIp()) < 0)
                ipList.add(ip);
        }
	}

	public void removeIP(IP ip) {
        synchronized (ipList) {
            int index = indexOf(ip.getIp());
            if (index >= 0)
                ipList.remove(index);
        }
	}
}
