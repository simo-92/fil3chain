/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package it.scrs.miner.models;


import com.google.gson.reflect.TypeToken;
import it.scrs.miner.IPManager;
import it.scrs.miner.Miner;
import it.scrs.miner.ServiceMiner;
import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.util.HttpUtil;
import it.scrs.miner.util.IP;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;



public class BlockChain {

	private int nBlockUpdate = 10;


	/**
	 * @return the nBlockUpdate
	 */
	public int getnBlockUpdate() {

		return nBlockUpdate;
	}

	/**
	 * @param nBlockUpdate
	 *            the nBlockUpdate to set
	 */
	public void setnBlockUpdate(int nBlockUpdate) {

		this.nBlockUpdate = nBlockUpdate;
	}


	private Miner miner;
	private BlockRepository blockRepository;
	private ServiceMiner serviceMiner;


	public BlockChain(Miner miner) {
		this.miner = miner;
		this.blockRepository = miner.getBlockRepository();
		this.serviceMiner = miner.getServiceMiner();

		Properties prop = new Properties();
		InputStream in = Miner.class.getResourceAsStream("/miner.properties");
		this.setnBlockUpdate(Integer.parseInt(prop.getProperty("nBlockUpdate", "10")));
	}

	/**
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	public Boolean updateFilechain() {

		@SuppressWarnings("unchecked")
		List<IP> ipMiners = (List<IP>) ((ArrayList<IP>) IPManager.getManager().getIPList()).clone();

		// Rimuovo il mio IP
		ipMiners.remove(miner.getIp());

		Integer myChainLevel = 0;
		while (!ipMiners.isEmpty()) {
			System.out.println("1");
			// Lista contenente le richieste asincrone ai 3 ip
			List<Future<Pairs<IP, Integer>>> minerResp = new ArrayList<>();
			// Chiedi al db il valore del mio Max chainLevel
			myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel();

			// Finche non sono aggiornato(ovvero mi rispondono con stringa
			// codificata o blocco fittizio)
			// Prendo k ip random da tutta la lista di Ip che mi sono stati inviati
			askMinerChainLvl(ipMiners, minerResp, serviceMiner);
			System.out.println("2");
			// minerResp.add(serviceMiner.findMaxChainLevel("192.168.0.107"));
			// System.out.println("1");

			// Oggetto che contiene la coppia IP,ChainLevel del Miner designato
			Pairs<IP, Integer> designedMiner = new Pairs<>();
			try {
				waitAndChooseMiner(ipMiners, myChainLevel, minerResp, designedMiner);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("3");
			try {
				killRequest(minerResp);
			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
			if (designedMiner.getValue1() != null) {
				System.out.println("Il Miner designato = " + designedMiner.getValue1() + " con ChainLevel = " + designedMiner.getValue2() + "\n");
				Integer counter = 0;
				Boolean flag = Boolean.TRUE;
				while (counter <= ServiceMiner.nReqProp && flag) {
					try {
						System.out.println("\nBranchUpdate GetBlock");
						flag = !getBlocksFromMiner(ipMiners, myChainLevel, designedMiner, blockRepository);
					} catch (IOException | ExecutionException | InterruptedException e) {
						e.printStackTrace();
					}
					counter++;
				}

			}
			// aspetta una risposta
			// verifico i blocchi e aggiungo al db

			// mi connetto al primo che rispondi si e gli chiedo 10 blocchi o meno
			// chiusi dal blocco fittizio
			System.out.println(ipMiners.toString());
		}
		return Boolean.TRUE;
	}

	/**
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	public Boolean updateBranChain(String hash) {

		@SuppressWarnings("unchecked")

		List<IP> ipMiners = (List<IP>) ((ArrayList<IP>) IPManager.getManager().getIPList()).clone();
		Boolean flag = Boolean.TRUE;
		// Rimuovo il mio IP
		// ipMiners.remove(miner.getIp());
		System.out.println("\nBranchUpdate");
		while (!ipMiners.isEmpty() && flag) {
			// Lista contenente le richieste asincrone ai 3 ip
			List<Future<Pairs<IP, Block>>> minerResp = new ArrayList<>();

			// Finche non sono aggiornato(ovvero mi rispondono con stringa
			// codificata o blocco fittizio)
			// Prendo k ip random da tutta la lista di Ip che mi sono stati inviati
			askMinerBlock(ipMiners, minerResp, serviceMiner);
			System.out.println("\nBranchUpdate REQ");

			// minerResp.add(serviceMiner.findMaxChainLevel("192.168.0.107"));
			// System.out.println("1");

			// Oggetto che contiene la coppia IP,ChainLevel del Miner designato
			Pairs<IP, Block> designedMiner = new Pairs<>();
			try {
				waitAndChooseMinerBlock(minerResp, designedMiner);
				System.out.println("\nBranchUpdate Wait and Choose");

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				killRequestBlock(minerResp);
				System.out.println("\nBranchUpdate KILL");

			} catch (InterruptedException | ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
			if (designedMiner.getValue1() != null) {
				System.out.println("Il Miner designato = " + designedMiner.getValue1() + " con ChainLevel = " + designedMiner.getValue2() + "\n");
				Integer counter = 0;
				while (counter <= ServiceMiner.nReqProp && flag) {
					try {
						System.out.println("\nBranchUpdate GetBlock");
						flag = !getBlockFromMiner(ipMiners, hash, designedMiner, blockRepository);

					} catch (IOException | ExecutionException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						counter++;
					}
				}
			}

			// aspetta una risposta
			// verifico i blocchi e aggiungo al db

			// mi connetto al primo che rispondi si e gli chiedo 10 blocchi o meno
			// chiusi dal blocco fittizio
			System.out.println("\n Hash branching: " + hash + " Miner ancora in lista" + ipMiners.toString());
		}
		return Boolean.TRUE;
	}

	/**
	 * @param minerResp
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void killRequest(List<Future<Pairs<IP, Integer>>> minerResp) throws InterruptedException, ExecutionException {

		for (Future<Pairs<IP, Integer>> f : minerResp) {
			System.out.println("\nElimino :" + f.get().getValue1().getIp());
			f.cancel(Boolean.TRUE);
		}
	}

	/**
	 * @param minerResp
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void killRequestBlock(List<Future<Pairs<IP, Block>>> minerResp) throws InterruptedException, ExecutionException {

		for (Future<Pairs<IP, Block>> f : minerResp) {
			System.out.println("\nElimino :" + f.get().getValue1().getIp());
			f.cancel(Boolean.TRUE);
		}
	}

	/**
	 * Restituisce la lista di miner che hanno risposto con il loro livello di block chain.
	 *
	 * @param ipMiners
	 * @param minerResp
	 */
	private void askMinerChainLvl(List<IP> ipMiners, List<Future<Pairs<IP, Integer>>> minerResp, ServiceMiner serviceMiner) {

		for (int i = 0; i < ipMiners.size(); i++) {
			// Double x = Math.random() * ipMiners.size();
			Future<Pairs<IP, Integer>> result = serviceMiner.findMaxChainLevel(ipMiners.get(i).getIp());
			try {
				if (result == null || result.get() == null || result.get().getValue1() == null || result.get().getValue2() == null) {
                    IP tmp = ipMiners.remove(i);
                    System.out.println("\nHo rimosso l'IP: " + tmp.getIp());
                } else {
                    minerResp.add(result);
                }
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Restituisce la lista di miner che hanno risposto con il loro livello di block chain.
	 *
	 * @param ipMiners
	 * @param minerResp
	 */
	private void askMinerBlock(List<IP> ipMiners, List<Future<Pairs<IP, Block>>> minerResp, ServiceMiner serviceMiner) {

		for (int i = 0; i < ipMiners.size(); i++) {
			// Double x = Math.random() * ipMiners.size();
			Future<Pairs<IP, Block>> result = serviceMiner.findBlockByReq(ipMiners.get(i).getIp(), "getBlockByhash?hash=0");
			try {
				if (result == null || result.get() == null || result.get().getValue1() == null || result.get().getValue2() == null) {
                    IP tmp = ipMiners.remove(i);
                    System.out.println("\nHo rimosso l'IP: " + tmp.getIp());
                } else {
                    minerResp.add(result);
                }
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param minerResp
	 * @param designedMiner
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void waitAndChooseMiner(List<IP> ipMiners, Integer myChainLevel, List<Future<Pairs<IP, Integer>>> minerResp, Pairs<IP, Integer> designedMiner) throws InterruptedException, ExecutionException {

		Boolean flag = Boolean.TRUE;
		while (flag && !minerResp.isEmpty()) {
			System.out.println("1.1");
			System.out.println("size: " + minerResp.size());
			// Controlliamo se uno dei nostri messaggi di richiesta è tornato
			// indietro con successo
			System.out.println("Il mio chain level: " + myChainLevel);
			System.out.println(minerResp.get(0).get().getValue2());
			Future<Pairs<IP, Integer>> f;
			for (Integer i = 0; i < minerResp.size(); i++) {
				System.out.println("1.5");
				f = minerResp.get(i);
				// facciamo un For per ciclare tutte richieste attive
				// all'interno del nostro array e controlliamo se
				// sono arrivate le risposte
				if (f != null && f.isDone()) {
					flag = Boolean.FALSE;
					if (f.get().getValue2() > myChainLevel) {
						// IP del miner designato da cui prendere la blockchain
						designedMiner.setValue1(f.get().getValue1());
						// ChainLevel del miner designato
						designedMiner.setValue2(f.get().getValue2());
						System.out.println("\nRisposto da: " + f.get().getValue1() + " Chain level: " + f.get().getValue2());

					} else {
						// TODO rivedere questa cosa wait/////
						ipMiners.remove(f.get().getValue1());

					}
				} else {
					// TODO rivedere questa cosa wait/////
					minerResp.remove(f);

				}

			}

		}
	}

	/**
	 * @param minerResp
	 * @param designedMiner
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */

	private void waitAndChooseMinerBlock(List<Future<Pairs<IP, Block>>> minerResp, Pairs<IP, Block> designedMiner) throws InterruptedException, ExecutionException {

		Boolean flag = Boolean.TRUE;
		while (flag && !minerResp.isEmpty()) {
			System.out.println("size: " + minerResp.size());
			// Controlliamo se uno dei nostri messaggi di richiesta è tornato
			// indietro con successo
			Thread.sleep(250L);
			for (Future<Pairs<IP, Block>> f : minerResp) {
				// facciamo un For per ciclare tutte richieste attive
				// all'interno del nostro array e controlliamo se
				// sono arrivate le risposte
				System.out.println("\nSono in attesa di branch miner");
				if (f != null && f.isDone()) {
					// IP del miner designato da cui prendere la blockchain
					designedMiner.setValue1(f.get().getValue1());
					// ChainLevel del miner designato
					designedMiner.setValue2(f.get().getValue2());
					// System.out.println("\nRisposto da: " + f.get().getValue1() + "Block Block " + f.get().getValue2());
					flag = Boolean.FALSE;

				} else {
					// TODO rivedere questa cosa wait/////
					minerResp.remove(f);
				}

			}

		}
	}

	/**
	 * @param ipMiners
	 * @param designedMiner
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Boolean getBlockFromMiner(List<IP> ipMiners, String hash, Pairs<IP, Block> designedMiner, BlockRepository blockRepository) throws IOException, ExecutionException, InterruptedException {

		// TODO cambire la uri di richiesta
		Type type = new TypeToken<Block>() {
		}.getType();

		System.out.println("Hash richiesta:" + hash + "\n");
		Block blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getValue1().getIp() + "/fil3chain/getBlockByhash?hash=" + hash, type);

		if (blockResponse != null) {
			System.out.println("\n Block response branch: " + blockResponse.getHashBlock() + "\n");

			if (miner.verifyBlock(blockResponse, blockRepository, serviceMiner)) {
				System.out.println("salvo il blocco");
				blockRepository.save(blockResponse);
				return Boolean.TRUE;
			} else {
				// Elimino il miner se il blocco non è verificato
				ipMiners.remove(designedMiner.getValue1());
				System.err.println("Il miner " + designedMiner.getValue1() + " ha inviato un blocco non corretto.");
				return Boolean.FALSE;

			}
			// System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");

		} else {
			ipMiners.remove(designedMiner.getValue1());
			return Boolean.FALSE;
		}

	}

	/**
	 * @param ipMiners
	 * @param myChainLevel
	 * @param designedMiner
	 * @throws Exception
	 */
	// TODO da rivalutare questo metodo, più in particolare la questione del while visto che è stata ereditata duratente
	// una modifica di pezzi di codice
	// TODO probabilmente il while non serve perchè le richieste vengono fatte da fuori tramite un ciclo quindi qui
	// dentro non serve anche se cmq dovrei uscire
	// TODO valutare bene
	@SuppressWarnings("unchecked")
	private Boolean getBlocksFromMiner(List<IP> ipMiners, Integer myChainLevel, Pairs<IP, Integer> designedMiner, BlockRepository blockRepository) throws IOException, ExecutionException, InterruptedException {

		// TODO cambire la uri di richiesta
		myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel() + 1;
		Type type = new TypeToken<List<Block>>() {
		}.getType();

		List<Block> blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getValue1().getIp() + "/fil3chain/getBlockByChain?chainLevel=" + myChainLevel, type);
		if (blockResponse != null) {
			System.out.println("\n Block response: " + blockResponse.size());
			System.out.println("\n Hash Block response " + blockResponse.get(0).getHashBlock() + "\n");
			for (Block b : blockResponse) {
				System.out.println(b);
				if (miner.verifyBlock(b, blockRepository, serviceMiner)) {
					for (Transaction t : b.getTransactionsContainer())
						t.setBlockContainer(b.getHashBlock());
					blockRepository.save(b);
					return Boolean.TRUE;
				} else {
					// Elimino il miner se il blocco non è verificato
					ipMiners.remove(designedMiner.getValue1());
					System.err.println("Il miner " + designedMiner.getValue1() + " ha inviato un blocco non corretto.");
				}
				// System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");
			}
			if (designedMiner.getValue2() <= blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel()) {
				ipMiners.remove(designedMiner.getValue1());
			}
		}
		return Boolean.FALSE;

		// System.out.println("2");

	}

	@SuppressWarnings("unchecked")
	private List<Transaction> getTransFromDisp(Integer nTrans) throws Exception {
		// TODO List<Transaction> trans = HttpUtil.doGetJSON("http://" + getEntryPointBaseUri() +

		Type type = new TypeToken<List<Transaction>>() {
		}.getType();
		List<Transaction> trans = HttpUtil.doGetJSON("http://" + "10.198.0.7" + ":8080/JsonTransaction?nTrans=" + nTrans, type);

		// Integer i = 0;
		// Boolean nullResponse = Boolean.FALSE;
		// while (!nullResponse && (i < nBlockUpdate) && (designedMiner.getValue2() > myChainLevel)) {
		// // TODO cambire la uri di richiesta
		// myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel() + 1;
		//
		// List<Block> blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getValue1() +
		// ":8080/fil3chain/getBlock?chainLevel=" + myChainLevel);
		//
		// if (blockResponse != null) {
		// System.out.println("\nBlock response: " + blockResponse.toString());
		// for (Block b : blockResponse) {
		// // TODO qui dentro ora posso salvare nel mio DB tutti i blocchi appena ricevuti e verificarli
		// blockRepository.save(b);
		// System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");
		// }
		// } else {
		// nullResponse = Boolean.TRUE;
		// }
		// i++;
		//
		// }
		// System.out.println("2");
		//
		// if (!nullResponse && designedMiner.getValue2() <=
		// blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel())
		// ipMiners.remove(designedMiner.getValue1());
		return trans;
	}

}
