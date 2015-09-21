package edu.upenn.cis.cis455.webserver;

public class ThreadpoolThread {
	private Thread thread;
	private Worker worker;
	
	public ThreadpoolThread(Worker worker){
		this.thread = new Thread(worker);
		this.worker = worker;
		this.thread.start();
	}

	public Thread getThread() {
		return thread;
	}

	public Worker getWorker() {
		return worker;
	}
	
	
}
