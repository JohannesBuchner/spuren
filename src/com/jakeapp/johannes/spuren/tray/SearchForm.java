package com.jakeapp.johannes.spuren.tray;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SearchForm extends javax.swing.JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private JList resultList;
	private JScrollPane jScrollPane1;
	private JLabel searchTokenLabel;
	private JTextField searchToken;
	private JTextPane resultDetails;

	/**
	 * Auto-generated main method to display this JPanel inside a new JFrame.
	 */

	public SearchForm() {
		super();
		initGUI();
		searchToken.setFocusable(true);
		searchToken.requestFocusInWindow();
		resultList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultDetails.setEditable(false);
		resultDetails.setEnabled(false);
	}

	private void initGUI() {
		try {
			GridBagLayout thisLayout = new GridBagLayout();
			thisLayout.rowWeights = new double[] { 0.0, 0.1, 0.0 };
			thisLayout.rowHeights = new int[] { 0, 7, 0 };
			thisLayout.columnWeights = new double[] { 0.01, 0.1 };
			thisLayout.columnWidths = new int[] { 3, 7 };
			this.setLayout(thisLayout);
			this.setOpaque(false);
			this.setPreferredSize(new java.awt.Dimension(500, 500));
			{
				{
					searchTokenLabel = new JLabel();
					this.add(searchTokenLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
				}
				{
					searchToken = new JTextField();
					this.add(searchToken, new GridBagConstraints(1, 0, 1, 1, 0.0,
							0.0, GridBagConstraints.NORTH,
								GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
								0, 0));
				}
				{
					jScrollPane1 = new JScrollPane();
					this.add(jScrollPane1, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					{
						resultList = new JList();
						jScrollPane1.setViewportView(resultList);
						//resultList.setPreferredSize(new java.awt.Dimension(9, 204));
					}
				}
			}
			{
				resultDetails = new JTextPane();
				this.add(resultDetails, new GridBagConstraints(0, 2, 2, 1, 0.0,
						0.0, GridBagConstraints.SOUTH,
						GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
						0, 0));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SearchForm sf = new SearchForm();
		sf.setVisible(true);
		JFrame w = new JFrame();
		w.add(sf);
		w.setSize(new Dimension(500, 500));
		w.setVisible(true);
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public JList getResultList() {
		return resultList;
	}

	public JLabel getSearchTokenLabel() {
		return searchTokenLabel;
	}

	public JTextField getSearchToken() {
		return searchToken;
	}

	public JTextPane getResultDetails() {
		return resultDetails;
	}
}
