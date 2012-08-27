package com.jakeapp.johannes.spuren.tray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class RebuildWorkerThread extends Thread {

	private static final String MINECMD = "spuren-mine";
	private Callable<Void> callable;

	public RebuildWorkerThread(Callable<Void> callable) {
		this.callable = callable;
	}

	@Override
	public void run() {
		try {
			System.out.println("starting mining");
			Process p = Runtime.getRuntime().exec(MINECMD);
			p.waitFor();
			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			String line;
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();
			input = new BufferedReader(new InputStreamReader(p
					.getErrorStream()));
			while ((line = input.readLine()) != null) {
				System.err.println(line);
			}
			input.close();
			System.out.println("mining done");
		} catch (Exception err) {
			err.printStackTrace();
		}
		try {
			this.callable.call();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
