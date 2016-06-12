package it.scrs.miner.controllers;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.scrs.miner.IPManager;
import java.util.ArrayList;
import java.util.List;

import it.scrs.miner.MinersListenerRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import it.scrs.miner.dao.block.Block;
import it.scrs.miner.dao.block.BlockRepository;
import it.scrs.miner.dao.transaction.Transaction;
import it.scrs.miner.dao.transaction.TransactionRepository;
import it.scrs.miner.dao.user.UserRepository;
import it.scrs.miner.util.IP;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;




@Component
@RestController
public class ControllerBlockRequest {

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BlockRepository blockRepository;


	/*
	 * @RequestMapping(value="/provaJson", method= RequestMethod.GET) public @ResponseBody
	 * requestBlocks(@RequestParam("chainLevel")Integer chainLevel){ Block block=new
	 * Block("adas","sadsa","dsadsa",12,23,chainLevel); return "redirect:/fi3chain/newBlock"+block; }
	 */

	@RequestMapping(value = "/provaJson", method = RequestMethod.GET)
	public Block requestBlocks() {

		Block block = new Block("adas", "dsadsa", "12", 23, 3);

		return block;
	}

	//	http://localhost:8080/addJsonBlock?hashBlock=22213&merkleRoot=cad&minerPublicKey=12&nonce=1&chainLevel=1
	@RequestMapping(value = "/addJsonBlock", method = RequestMethod.GET)
	public Block addJsonBlock(String hashBlock, String merkleRoot, String minerPublicKey, Integer nonce, Integer chainLevel) {

		Block block = new Block(hashBlock,merkleRoot , minerPublicKey, nonce, chainLevel);
		blockRepository.save(block);
		return block;
	}

	// Controller che intercetta arrivo di un nuovo blocco
	@RequestMapping(value = "/fil3chain/newBlock", method = RequestMethod.POST)
	@ResponseBody
	public Block newBlock(@RequestBody Block block) {
		Block newBlock = block;
		System.out.println("il blocco che mi è stato mandato è " + newBlock);
		//TODO dobbiamo verificare il blocco appena arrivato se è valido
		//blocco il thread di mining e lo riavvio sulla fil3chain aggiornata

        MinersListenerRegister.getInstance().notifyListenersNewBlock(newBlock);

		return newBlock;
	}

	// Controller che intercetta la connessione di un utente
	@RequestMapping(value = "/user_connect", method = RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void newUserConnection(@RequestBody  MultiValueMap<String,String> ip) {
		String json=ip.keySet().iterator().next();
		JsonObject jobj = new Gson().fromJson(json,JsonObject.class);
		String ipHost = jobj.get("user_ip").getAsString();
		IPManager.getManager().addIP(new IP(ipHost));
		IPManager.getManager().getIPList().forEach(i -> System.out.println(i));
	}

	// Controller che intercetta la disconnessione di un utente
	@RequestMapping(value = "/user_disconnect", method = RequestMethod.POST, consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
	public void newUserDisconnection(@RequestBody  MultiValueMap<String,String> ip) {
		String json=ip.keySet().iterator().next();
		JsonObject jobj = new Gson().fromJson(json,JsonObject.class);
		String ipHost = jobj.get("user_ip").getAsString();
		IPManager.getManager().removeIP(new IP(ipHost));
		IPManager.getManager().getIPList().forEach(i -> System.out.println(i));
	}

	@RequestMapping(value = "/fil3chain/getBlock", method = RequestMethod.GET)
	@ResponseBody
	public List<Block> getBlock(Integer chainLevel) {
		return blockRepository.findBychainLevel(chainLevel);
	}

	// TODO Cambiare e mettere specifiche professore
	@RequestMapping(value = "/poolDispatcher", method = RequestMethod.GET)
	public Integer getDifficult() {
		return 1;
	}

	//Aggiungo delle transazioni di prova
	@RequestMapping(value = "/JsonTransaction", method = RequestMethod.GET)
	@ResponseBody
	public List<Transaction> JsonTransaction(Integer nTrans) {
		String s="";
		Double x ;
		List<Transaction> trans=new ArrayList<Transaction>();
		for(int i=0;i<nTrans;i++){
			x = Math.random() * nTrans;
			s=org.apache.commons.codec.digest.DigestUtils.sha256Hex(x.toString());
			Transaction transaction=new Transaction(s, "file prova numero: "+i);
			trans.add(transaction);
		}
		return trans;
	}



	//Mappiamo la richiesta di invio di blocchi ad un Peer che la richiede
	@RequestMapping(value = "/fil3chain/updateAtMaxLevel", method = RequestMethod.GET)
	public Integer updateAtMaxLevel() {
		//Inutile che ritorno si/no con accodato il chain level basta che torno il chain level e
		//il ricevente sa a chi chiedere tutti i blocchi di cui ha bisogno
		return blockRepository.findFirstByOrderByChainLevelDesc().getChainLevel();
	}
		
		
		/*
		@RequestMapping(value = "/generateValidateBlock", method = RequestMethod.GET)
		public Block generateBlock() {

			Block block = new Block("sadsa", "dsadsa", 12, 23, 3);
			block.generateHashBlock();
			return block;
		}
		*/
		
		/*	
		@RequestMapping(value = "/fil3chain/newBlock", method = RequestMethod.GET)
		@ResponseBody
		public Block requestBlock(String hashBlock, String merkleRoot, String creationTime, Integer minerPublicKey, Integer nonce, Integer chainLevel) {

			
			Block block = new Block(hashBlock, merkleRoot, creationTime, minerPublicKey, nonce, chainLevel);
			if(block.verify()){
			//aggiungerei al db
			//
			
			}
			*/

}
