package it.scrs.miner;



public class ManageMine implements Runnable
	{
	 private boolean stopThread = false;
	 
	 public void run()
	 {
	  while (! stopThread)
	  {
	   // Esegue qualcosa fino a quando la
	   // variabile stopThread è false
	  }
	  // esecuzione di eventuali operazioni di “pulizia”
	//…
	 }

	 public void stopRunning()
	 {
	  stopThread = true;
	 }
	}

