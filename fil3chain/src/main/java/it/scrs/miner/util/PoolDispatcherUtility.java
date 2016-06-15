package it.scrs.miner.util;

import it.scrs.miner.dao.transaction.Transaction;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Marco
 * Date: 12/06/2016.
 *
 * Insieme di metodi statici per le richieste di utilità
 * al Pool Dispatcher
 *
 */
public class PoolDispatcherUtility {

    /**
     * Metodo sincrono per la richiesta della complessità attuale
     * @return La complessoità attuale o -1 nel caso di errore
     */
    public static Integer getCurrentComplexity() {
        try {
            JSONObject result  = new JSONObject(HttpUtil.doPost("http://vmanager:80/sdcmgr/PD/get_complexity", "{\"date\" : \"" + new Date().getTime() + "\"}"));
            return (Integer) result.get("complexity");
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Metodo sincrono per la richiesta della complessità
     * al tempo della creazione di un blocco
     * @return La complessoità al tempo della creazione del blocco o -1 nel caso di errore
     */
    public static Integer getBlockComplexity(String blockCreationTime) {
        try {
            JSONObject result  = new JSONObject(HttpUtil.doPost("http://vmanager:80/sdcmgr/PD/get_complexity", "{\"date\" : \"" + blockCreationTime + "\"}"));
            return ((Integer) (result.get("complexity")));
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static List<Transaction> getTransactions() {
        //TODO: Mettere chiamata al server reale
        ArrayList<Transaction> transactions = new ArrayList<>();

        for(int i = 0; i < 2; i++) {
            // Transazione mock
            Transaction transaction = new Transaction();
            transaction.setFilename("Ciano's file " + new Random().nextInt());
            transaction.setHashFile(org.apache.commons.codec.digest.DigestUtils.sha256Hex(transaction.getFilename()));

            transactions.add(transaction);
        }

        long seed = System.nanoTime();
        Collections.shuffle(transactions, new Random(seed));

        return transactions;
    }

}
