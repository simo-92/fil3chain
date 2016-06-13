package it.scrs.miner;

import it.scrs.miner.dao.block.Block;

import java.util.ArrayList;

/**
 * Created by Marco
 * Date: 12/06/2016.
 */
public class MinersListenerRegister {

    // Instanza del registro
    private static MinersListenerRegister minersListenerRegister;

    // Lista di miners che devono gestire gli eventi
    private static ArrayList<Miner> registeredMiners;

    private MinersListenerRegister() {
        registeredMiners = new ArrayList<>();
    }

    // Ritorna l'istanza di un registro miners
    public static MinersListenerRegister getInstance() {
        if (minersListenerRegister == null) {
            minersListenerRegister = new MinersListenerRegister();
        }
        return minersListenerRegister;
    }

    // Registra un miner
    public void registerMiner(Miner miner) {
        registeredMiners.add(miner);
    }

    // Toglie la registrazione di un miner
    public void unregisterMiner(Miner miner) {
        registeredMiners.remove(miner);
    }

    // Metodo per notificare i miners di un nuovo blocco verificato
    public void notifyListenersNewBlock(Block verifiedBlock) {
        for(Miner miner: registeredMiners) {
            miner.onNewBlockArrived(verifiedBlock);
        }
    }

}
