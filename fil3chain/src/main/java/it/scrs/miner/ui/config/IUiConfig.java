package it.scrs.miner.ui.config;
/**
 * Quest'interfaccia viene utilizzata per recuperare la configurazione dell'ambiente di lavoro attuale
 * @author ivan18
 *
 */
public interface IUiConfig {
	public AUiConfig getConfigFromEnvironment(String environment);
}
