package com.jakeapp.johannes.spuren.tray;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

import javax.swing.AbstractListModel;

@SuppressWarnings("serial")
public class ResultListModel extends AbstractListModel {
	public static final int DB_ENTRYNR_SIZE = 0;
	public static final int DB_ENTRYNR_BASEFOLDER = 1;
	public static final int DB_ENTRYNR_FILE = 2;

	private List<String[]> results = new ArrayList<String[]>();
	private ResultWorkerThread worker;
	private String currentquery = "";
	private Pattern pattern = Pattern.compile("([^/]{3})[^/]*/");
	private String replacement = "$1/";

	@Override
	public Object getElementAt(int index) {
		return getShortForm(results.get(index)[DB_ENTRYNR_FILE]);
	}

	private Object getShortForm(String string) {
		if (string.length() > 20) {
			return pattern.matcher(string).replaceAll(replacement);
		} else {
			return string;
		}
	}

	@Override
	public int getSize() {
		return results.size();
	}

	public void searchFor(String text) {
		if (worker != null) {
			if (currentquery.equals(text.trim())) {
				System.out.println("I'm too lazy to do another search");
				return;
			} else {
				worker.halt();
			}
		}
		currentquery = text.trim();
		int n = results.size();
		if (n != 0) {
			results.clear();
			this.fireIntervalRemoved(this, 0, n - 1);
		}
		System.out.println("cleared.");
		if (text.trim().isEmpty())
			return;
		worker = new ResultWorkerThread(text, results, this);
		worker.start();

		DisplayUpdater displayUpdater = new DisplayUpdater(this, results,
				worker);
		// run in this (gui) thread
		displayUpdater.start();
	}

	public void add(String[] dbentry) {
		this.results.add(dbentry);
	}

	public void refreshDisplay(int i) {
		System.out.println("#entries: " + this.results.size() + " (from " + i
				+ ")");
		if (results.size() == 0) {
			this.fireContentsChanged(this, 0, 0);
		} else {
			this.fireContentsChanged(this, 0, results.size() - 1);
		}
	}

	public boolean launch(int selected, boolean launchFolder)
			throws IOException {
		String basefolder = results.get(selected)[DB_ENTRYNR_BASEFOLDER];
		String file = results.get(selected)[DB_ENTRYNR_FILE];
		File f = new File(basefolder, file);
		if (launchFolder)
			f = f.getParentFile();

		Desktop d = Desktop.getDesktop();
		try {
			d.open(f);
		} catch (IOException e) {
			d.edit(f);
		}
		return true;
	}

	public void rebuildDatabase(Callable<Void> callable) {
		Thread t = new RebuildWorkerThread(callable);
		t.start();
	}

	public File getFile(int index) {
		String[] dbentry = results.get(index);
		return new File(dbentry[DB_ENTRYNR_BASEFOLDER],
				dbentry[DB_ENTRYNR_FILE]);
	}
}
