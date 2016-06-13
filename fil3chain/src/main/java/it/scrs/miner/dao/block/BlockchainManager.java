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

		return null;
	}

	@Override
	public Block findOne(Long id) {

		return null;
	}

	@Override
	public boolean exists(Long id) {

		return false;
	}

	@Override
	public Iterable<Block> findAll() {

		return null;
	}

	@Override
	public Iterable<Block> findAll(Iterable<Long> ids) {

		return null;
	}

	@Override
	public long count() {

		return 0;
	}

	@Override
	public void delete(Long id) {

	}

	@Override
	public void delete(Block entity) {

	}

	@Override
	public void delete(Iterable<? extends Block> entities) {

	}

	@Override
	public void deleteAll() {

	}

	@Override
	public Block findByhashBlock(String hashBlock) {

		return null;
	}

	@Override
	public <S extends Block> S save(S entity) {

		return null;
	}

	@Override
	public List<Block> findBychainLevel(Integer cLevel) {

		return null;
	}

	@Override
	public List<Block> findBychainLevelGreaterThan(Integer cLevel) {

		return null;
	}

	@Override
	public List<Block> findByChainLevelGreaterThanOrderByChainLevelDesc(Integer cLevel) {

		return null;
	}

	@Override
	public List<Block> findTop10ByChainLevelGreaterThanOrderByChainLevelDesc(Integer cLevel) {

		return null;
	}

	@Override
	public Block findFirstByOrderByChainLevelDesc() {

		return null;
	}

}
