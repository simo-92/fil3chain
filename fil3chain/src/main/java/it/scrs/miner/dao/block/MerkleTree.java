
package it.scrs.miner.dao.block;
import it.scrs.miner.dao.transaction.Transaction;

import java.util.List;
import java.util.ArrayList;
/**
 * classe per la costruzione di un merkletree
 * @author giordanocristini
 */
public class MerkleTree {
    /**
     * metodo che costruisce il merkleTree a partire da una lista di hash di transazioni
     * @param hashTransactions lista degli hash delle transazioni del blocco
     * @return <b>merkleRoot</b> la radice del merkleTree
     */
    public static String buildMerkleTree(List<String> hashTransactions){
        String merkleRoot="";
        List<String> aux=new ArrayList<String>();
        aux.addAll(hashTransactions);
        //finche' non rimane solo un nodo radice
        while(aux.size()!=1){
            for(int j=0;j<aux.size();j++){
                
                if(j+1==(aux.size())){ //se dispari accoppia con se stesso
                    String n1=aux.get(j);
                    aux.remove(j);
                    aux.add(j, MerkleTree.joinNodes(n1, n1));
                }else { //accoppia due node
                    String n1=aux.get(j);
                    String n2=aux.get(j+1);
                    aux.remove(j); //rimuovi il nodo j
                    aux.remove(j); //rimuovi il nodo j+1 che ora è il nodo j!!!
                    aux.add(j, MerkleTree.joinNodes(n1, n2));
                }
            }
            
        }
        merkleRoot=aux.get(0);
        return merkleRoot;
    }

    /**
     * metodo che fonde due nodi
     * @param n1 prima nodo
     * @param n2 secondo nodo (puo' essere uguale a <b>n1</b> se le transazioni sono dispari
     * @return fusione tra n1 e n2 (hash)
     */
    private static String joinNodes(String n1, String n2){
        return org.apache.commons.codec.digest.DigestUtils.sha256Hex(n1+n2);
    }
    //test
//    public static void main(String[] args){
//        List<String> trans=new ArrayList();
//        trans.add(org.apache.commons.codec.digest.DigestUtils.sha256Hex("A"));
//        trans.add(org.apache.commons.codec.digest.DigestUtils.sha256Hex("B"));
//        trans.add(org.apache.commons.codec.digest.DigestUtils.sha256Hex("C"));
//        trans.add(org.apache.commons.codec.digest.DigestUtils.sha256Hex("D"));
//        trans.add(org.apache.commons.codec.digest.DigestUtils.sha256Hex("E"));
//        System.out.println(MerkleTree.buildMerkleTree(trans));
//        
//        
//    }
}
