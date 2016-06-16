package it.scrs.miner;

import org.springframework.stereotype.Service;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Marco
 * Date: 16/06/2016.
 */
@Service
public class VerifyBlockService extends Thread implements Runnable {

    Runnable verifyRunnable;

    public VerifyBlockService() {}

    public VerifyBlockService(Runnable verify) {
        verifyRunnable = verify;
    }

    @Override
    public void run() {
        try {
            if(verifyRunnable != null) verifyRunnable.run();
        } catch (Exception ex) {
            Logger.getLogger(MiningService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
