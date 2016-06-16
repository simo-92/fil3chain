/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.scrs.miner.util;

import java.io.FileInputStream;
import java.io.InputStream;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author simone
 */
public class AudioUtil {
    
    public static void alert(){
        try{
            InputStream in = AudioUtil.class.getResourceAsStream("/clip/airhorn_3.au");
            AudioStream audioStream = new AudioStream(in);
            AudioPlayer.player.start(audioStream);
        }catch(Exception e){
            System.out.println("problemi con la tromba");
        }
    }
    
}
