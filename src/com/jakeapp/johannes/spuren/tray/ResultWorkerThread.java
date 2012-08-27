package com.jakeapp.johannes.spuren.tray;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

public class ResultWorkerThread extends Thread {

	private static final String SEARCHSPLITREGEX = "[/\\\\_. ]+";
	private static final String FILESPLITREGEX = "[/\\\\_. \\-+]+";

	private boolean stopped = false;
	private String[] tokens;
	private ResultListModel model;
	private String[] cmd;
	private Process p;

	public ResultWorkerThread(String text, List<String[]> results,
			ResultListModel model) {
		this.tokens = text.trim().toLowerCase().split(SEARCHSPLITREGEX);
		this.model = model;
	}

	public void halt() {
		stopped = true;
		if (p != null)
			p.destroy();
	}

	@Override
	public void run() {
		if (stopped || this.tokens.length == 0)
			return;
		System.out.println("executing search");
		try {
			this.cmd = getCmd(getLongest(tokens));
			String line;
			String file;
			int n = 0;
			String[] dbentry;

			p = Runtime.getRuntime().exec(cmd);
			BufferedReader input = new BufferedReader(new InputStreamReader(p
					.getInputStream()));
			while ((line = input.readLine()) != null) {
				if (stopped) {
					System.out.println("I noticed I was stopped");
					break;
				}
				dbentry = line.split("\t");
				if (dbentry.length <= ResultListModel.DB_ENTRYNR_FILE) {
					System.err.println("weird db entry " + dbentry.length
							+ ": " + line);
				} else {
					file = dbentry[ResultListModel.DB_ENTRYNR_FILE];

					if (isMatch(file, tokens)) {
						this.model.add(dbentry);
					}
					n++;
				}
			}
			System.out.println("handled " + n + " entries.");
			input.close();
			input = new BufferedReader(
					new InputStreamReader(p.getErrorStream()));
			while ((line = input.readLine()) != null) {
				System.err.println(line);
			}
			input.close();
			this.model.refreshDisplay(-1);
		} catch (Exception err) {
			err.printStackTrace();
		}
		System.out.println("search done");
	}

	private boolean isMatch(String file, String[] tokens) {
		String[] filetags = file.toLowerCase().split(FILESPLITREGEX);

		if (tokens.length > filetags.length) {
			return false;
		}
		boolean matched = false;
		for (int i = tokens.length - 1; i >= 0; i--) {
			matched = false;
			for (int j = filetags.length - 1; j >= 0; j--) {
				String token = tokens[i];
				boolean beginsWith = true;
				boolean inverse = false;

				if (token.length() < 1)
					continue;
				
				if (token.charAt(0) == '-') {
					token = token.substring(1);
					inverse = true;
				}
				if (token.length() < 1)
					continue;
				
				if (token.charAt(0) == '*') {
					token = token.substring(1);
					beginsWith = false;
				}
				if (token.length() < 1)
					continue;

				if ((!beginsWith && filetags[j].contains(token))
						|| (beginsWith && filetags[j].startsWith(token))) {
					if (inverse) {
						// inverse match is very bad
						return false;
					}
					matched = true;
					break;
				} else {
					if (inverse) {
						// inverse mismatch is good
						matched = true;
						break;
					}
					
				}
			}
			if (!matched)
				return false;
		}

		return matched;
	}

	private String getLongest(String[] tokens) {
		return tokens[0];
	}

	private String[] getCmd(String text) {
		String[] s = { "/home/user/bin/spuren/spuren-search", text };
		return s;
	}

}
