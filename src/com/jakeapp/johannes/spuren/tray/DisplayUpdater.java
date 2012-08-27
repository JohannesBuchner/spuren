package com.jakeapp.johannes.spuren.tray;

import java.util.List;

public class DisplayUpdater extends Thread {
	private List<?> list;
	private ResultListModel model;
	private boolean stopped = false;
	private int lastLen;
	private Thread worker;

	public DisplayUpdater(ResultListModel model, List<?> list, Thread worker) {
		this.list = list;
		this.model = model;
		lastLen = list.size();
		this.worker = worker;
	}

	public void halt() {
		stopped = true;
	}

	@Override
	public void run() {
		while (!stopped && worker.isAlive()) {
			int nextLen = list.size();
			if (lastLen != nextLen) {
				model.refreshDisplay(lastLen);
				lastLen = nextLen;
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		model.refreshDisplay(-1);
	}
}
