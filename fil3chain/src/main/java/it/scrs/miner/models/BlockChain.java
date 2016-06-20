/*
 * To change this license header, choose License Headers in Project Properties. To change this template file, choose
 * Tools | Templates and open the template in the editor.
 */
package it.scrs.miner.models;


import com.google.gson.reflect.TypeToken;
import it.scrs.miner.IPManager;
import it.scrs.miner.Miner;
import it.scrs.miner.MiningService;
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

import org.springframework.beans.factory.annotation.Autowired;



public class BlockChain {

	private int nBlockUpdate = 10;
	private Miner miner;
	private BlockRepository blockRepository;

	
	
	@Autowired
	private MiningService ms;

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




	public BlockChain(Miner miner) {
		this.miner = miner;
		this.blockRepository = miner.getBlockRepository();

		Properties prop = new Properties();
		InputStream in = Miner.class.getResourceAsStream("/miner.properties");
		this.setnBlockUpdate(Integer.parseInt(prop.getProperty("nBlockUpdate", "10")));
	}

	/**
     * Metodo di update della blockchain per chain level
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws Exception
	 */
	public Boolean updateFilechain() {

		@SuppressWarnings("unchecked")
		List<IP> ipMiners = (List<IP>) ((ArrayList<IP>) IPManager.getManager().getIPList()).clone();

		Integer myChainLevel;

		while (!ipMiners.isEmpty()) {
			System.out.println("1");
			// Lista contenente le richieste asincrone ai 3 ip
			List<Future<Pairs<IP, Integer>>> minerResp = new ArrayList<>();
			// Chiedi al db il valore del mio Max chainLevel
			myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel();

			// Finche non sono aggiornato(ovvero mi rispondono con stringa
			// codificata o blocco fittizio)
			// Prendo k ip random da tutta la lista di Ip che mi sono stati inviati
			askMinerChainLvl(ipMiners, minerResp);
			System.out.println("2");
			// minerResp.add(serviceMiner.findMaxChainLevel("192.168.0.107"));
			// System.out.println("1");

			// Oggetto che contiene la coppia IP,ChainLevel del Miner designato
			Pairs<IP, Integer> designedMiner = new Pairs<>();
			try {
				waitAndChooseMiner(ipMiners, myChainLevel, minerResp, designedMiner);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
			System.out.println("3");

			try {
				killRequest(minerResp);
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			// Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
			if (designedMiner.getValue1() != null) {
				System.out.println("Il Miner designato = " + designedMiner.getValue1() + " con ChainLevel = " + designedMiner.getValue2() + "\n");
				Integer counter = 0;
				Boolean flag = Boolean.TRUE;
				while (counter <= MiningService.nReqProp && flag) {
					try {
						System.out.println("\nBranchUpdate GetBlock");
						getBlocksFromMiner(ipMiners, designedMiner, blockRepository);
                        flag = Boolean.FALSE;
                    } catch (IOException | ExecutionException | InterruptedException e) {
						e.printStackTrace();
					    counter++;
					}

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
			List<Pairs<Future<String>, IP>> minerResp = new ArrayList<>();

			// Finche non sono aggiornato(ovvero mi rispondono con stringa
			// codificata o blocco fittizio)
			// Prendo k ip random da tutta la lista di Ip che mi sono stati inviati
			askMinerBlock(ipMiners, minerResp);
			System.out.println("\nBranchUpdate REQ");

			// minerResp.add(serviceMiner.findMaxChainLevel("192.168.0.107"));
			// System.out.println("1");

			// Oggetto che contiene la coppia IP,ChainLevel del Miner designato
			IP designedMiner = null;
			try {
				designedMiner = waitAndChooseMinerBlock(minerResp);
				System.out.println("\nBranchUpdate Wait and Choose");

			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			try {
				killRequestBlock(minerResp);
				System.out.println("\nBranchUpdate KILL");
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}

			// Aggiorno la mia blockChain con i blocchi che mi arrivano in modo incrementale
			if (designedMiner != null) {
				System.out.println("Il Miner designato = " + designedMiner.getIp() + "\n");
				Integer counter = 0;
				flag = Boolean.TRUE;
				while (counter <= MiningService.nReqProp && flag) {
					try {
						System.out.println("\nBranchUpdate GetBlock");
						getBlockFromMiner(ipMiners, hash, designedMiner, blockRepository);
                        flag = Boolean.FALSE;
					} catch (IOException | ExecutionException | InterruptedException e) {
						e.printStackTrace();
						counter++;
					}

                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
			} else {
                // Caso esplosione brutta, manca il miner
                System.err.println("Nessun miner ha risposto, spengi tutto e scappa.");
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

        System.out.println("Numero di richieste da killare: " + minerResp.size());

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
	private void killRequestBlock(List<Pairs<Future<String>, IP>> minerResp) throws InterruptedException, ExecutionException {

        System.out.println("Numero di richieste da killare: " + minerResp.size());

		for (Pairs<Future<String>, IP> pair : minerResp) {
            Future<String> future = pair.getValue1();
			System.out.println("\nElimino :" + pair.getValue2().getIp());
			future.cancel(Boolean.TRUE);
		}
	}

	/**
	 * Restituisce la lista di miner che hanno risposto con il loro livello di block chain.
	 *
	 * @param ipMiners
	 * @param minerResp
	 */
	private void askMinerChainLvl(List<IP> ipMiners, List<Future<Pairs<IP, Integer>>> minerResp) {

		for (int i = 0; i < ipMiners.size(); i++) {
			// Double x = Math.random() * ipMiners.size();
			Future<Pairs<IP, Integer>> result = ms.findMaxChainLevel(ipMiners.get(i).getIp());
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
	private void askMinerBlock(List<IP> ipMiners, List<Pairs<Future<String>, IP>> minerResp) {

		for (int i = 0; i < ipMiners.size(); i++) {
			// Double x = Math.random() * ipMiners.size();
			// Future<Pairs<IP, Block>> result = serviceMiner.pingUser(ipMiners.get(i).getIp(), "getBlockByhash?hash=0");
            Future<String> result = ms.pingUser(ipMiners.get(i).getIp());
			try {
				if (result == null || result.get() == null) {
                    IP tmp = ipMiners.remove(i);
                    System.out.println("\nHo rimosso l'IP: " + tmp.getIp());
                } else {
                    System.out.println("\nHo aggiunto l'IP: " + ipMiners.get(i).getIp());
                    minerResp.add(new Pairs<>(result, ipMiners.get(i)));
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
			System.out.println("Size del numero di risposte dei miner: " + minerResp.size());
			// Controlliamo se uno dei nostri messaggi di richiesta è tornato
			// indietro con successo

			Future<Pairs<IP, Integer>> future;

			for (Integer i = 0; i < minerResp.size(); i++) {
				System.out.println("1.5");
                System.out.println("Il mio chain level: " + myChainLevel);
                System.out.println("Il chain level dell'altro miner: " + minerResp.get(i).get().getValue2()+ " ip miner " + minerResp.get(i).get().getValue1());
                System.out.println("Size del numero di risposte dei miner: " + minerResp.size());
                future = minerResp.get(i);
				// facciamo un For per ciclare tutte richieste attive
				// all'interno del nostro array e controlliamo se
				// sono arrivate le risposte
				if (future != null) {
                    if(future.isDone()) {
                        if (future.get().getValue2() > myChainLevel) {
                            flag = Boolean.FALSE;
                            // IP del miner designato da cui prendere la blockchain
                            designedMiner.setValue1(future.get().getValue1());
                            // ChainLevel del miner designato
                            designedMiner.setValue2(future.get().getValue2());
                            System.out.println("\nRisposto da: " + future.get().getValue1() + " Chain level: " + future.get().getValue2());
                        } else {
                            // Miner ha livello minore del mio, allora elimino
                            ipMiners.remove(future.get().getValue1());
                            minerResp.remove(future);
                            System.out.println("Elimino il miner perche ha chain level minore uguale al mio " + future.get().getValue2() + " e la dimensione della lista " + minerResp.size());
                        }
                    } else {
                        System.out.println("Richiesta non pronta");
                    }
                } else {
                    // Future nulla, elimino il miner dalla lista
                    // ipMiners.remove(future.get().getValue1());
                    System.out.println("Elimino il miner perche future nulla e la dimensione della lista " + minerResp.size());
                }
			}
		}
	}

	/**
	 * @param minerResp
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */

	private IP waitAndChooseMinerBlock(List<Pairs<Future<String>, IP>> minerResp) throws InterruptedException, ExecutionException {

		// Boolean flag = Boolean.TRUE;
		while (!minerResp.isEmpty()) {
			System.out.println("size: " + minerResp.size());
			// Controlliamo se uno dei nostri messaggi di richiesta è tornato
			// indietro con successo
			for (Pairs<Future<String>, IP> pair : minerResp) {

                Future<String> future = pair.getValue1();
                IP minerIp = pair.getValue2();

				// facciamo un For per ciclare tutte richieste attive
				// all'interno del nostro array e controlliamo se
				// sono arrivate le risposte
				System.out.println("\nSono in attesa di branch miner");
				if (future != null) {
                    if(future.isDone()) {
                        System.out.println("Controllo equals Loopo non se fida de Java: " + future.get().equals("{\"response\":\"ACK\"}") + " " + future.get());

                        if(future.get().equals("{\"response\":\"ACK\"}")) {
                            // IP del miner designato da cui prendere la blockchain
                            System.out.println("\nRisposto da: " + minerIp.getIp());
                            return minerIp;
                        } else {
                            future.cancel(Boolean.TRUE);
                            minerResp.remove(pair);
                        }
                    }
				} else {
					minerResp.remove(pair);
				}

			}
            // Attesa tra una richiesta e l'altra
            Thread.sleep(250L);
		}

        return null;

	}

	/**
	 * @param ipMiners
	 * @param designedMiner
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Boolean getBlockFromMiner(List<IP> ipMiners, String hash, IP designedMiner, BlockRepository blockRepository) throws IOException, ExecutionException, InterruptedException {

		Type type = new TypeToken<Block>() {
		}.getType();

		System.out.println("Hash richiesta:" + hash + "\n");
		Block blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getIp() + "/fil3chain/getBlockByhash?hash=" + hash, type);

		if (blockResponse != null) {
			System.out.println("\n Block response branch update: " + blockResponse.getHashBlock() + "\n");

			if (miner.verifyBlock(blockResponse, blockRepository)) {
				System.out.println("Salvo il blocco");
				blockRepository.save(blockResponse);
				return Boolean.TRUE;
			} else {
				// Elimino il miner se il blocco non è verificato
				ipMiners.remove(designedMiner);
				System.err.println("Il miner " + designedMiner.getIp() + " ha inviato un blocco non corretto, lo elimino dalla lista.");
				return Boolean.FALSE;
			}
			// System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");

		} else {
            // Il miner non ha risposto, lo elimino
			ipMiners.remove(designedMiner);
			return Boolean.FALSE;
		}

	}

	/**
	 * @param ipMiners
	 * @param designedMiner
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private Boolean getBlocksFromMiner(List<IP> ipMiners, Pairs<IP, Integer> designedMiner, BlockRepository blockRepository) throws IOException, ExecutionException, InterruptedException {

		Integer myChainLevel = blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel() + 1;
		Type type = new TypeToken<List<Block>>() {
		}.getType();

		List<Block> blockResponse = HttpUtil.doGetJSON("http://" + designedMiner.getValue1().getIp() + "/fil3chain/getBlockByChain?chainLevel=" + myChainLevel, type);
		if (blockResponse != null) {
			System.out.println("\n Numero di blocchi ricevuti da block chain update from level: " + blockResponse.size());
			for (Block b : blockResponse) {
				System.out.println("Blocco ricevuto: " + b);
				if (miner.verifyBlock(b, blockRepository)) {
					for (Transaction t : b.getTransactionsContainer())
						t.setBlockContainer(b.getHashBlock());
					blockRepository.save(b);

                    // Se il miner attuale ha un livello minore o uguale al mio lo elimino
                    if (designedMiner.getValue2() <= blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel()) {
                        ipMiners.remove(designedMiner.getValue1());
                    }

					return Boolean.TRUE;

				} else {
					// Elimino il miner se il blocco non è verificato
					ipMiners.remove(designedMiner.getValue1());
					System.err.println("Il miner " + designedMiner.getValue1() + " ha inviato un blocco non corretto, lo elimino.");
				}
				// System.out.println("Ho tirato fuori il blocco con chainLevel: " + b.getChainLevel() + "\n");
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
