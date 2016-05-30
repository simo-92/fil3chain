package it.scrs.miner.dao.block;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.scrs.miner.dao.transaction.TransactionRepository;



public class BlockchainManager implements BlockRepository {

	@Autowired
	BlockRepository rBlock;
	@Autowired
	TransactionRepository rTransaction;


	@Override
	public <S extends Block> Iterable<S> save(Iterable<S> entities) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block findOne(Long id) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean exists(Long id) {

		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Iterable<Block> findAll() {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Block> findAll(Iterable<Long> ids) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long count() {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(Long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Block entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Iterable<? extends Block> entities) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAll() {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Block> findByhashBlock(String hashBlock) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <S extends Block> S save(S entity) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Block> findBychainLevel(Integer cLevel) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Block> findBychainLevelGreaterThan(Integer cLevel) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Block> findByChainLevelGreaterThanOrderByChainLevelDesc(Integer cLevel) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Block> findTop10ByChainLevelGreaterThanOrderByChainLevelDesc(Integer cLevel) {

		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Block findFirstByOrderByChainLevelDesc() {

		// TODO Auto-generated method stub
		return null;
	}

}
