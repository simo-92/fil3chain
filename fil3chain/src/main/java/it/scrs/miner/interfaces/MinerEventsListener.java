package it.scrs.miner.interfaces;

import it.scrs.miner.dao.block.Block;

/**
 * Created by Marco
 * Date: 12/06/2016.
 */
public interface MinerEventsListener {
    void onNewBlockArrived(Block block);
}
