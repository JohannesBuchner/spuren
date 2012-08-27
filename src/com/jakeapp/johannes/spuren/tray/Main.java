package com.jakeapp.johannes.spuren.tray;

import java.awt.AWTException;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.batik.transcoder.Transcoder;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

/**
 * @author Johannes Buchner
 */
public class Main {

	private static ResourceBundle textBundle;

	private static SystemTray tray = SystemTray.getSystemTray();

	private static TrayIcon trayIcon;

	private static Image busyImage;

	// private static SearchForm searchForm;

	private static Frame searchFormFrame;

	private static ResultListModel model;

	private static Image normalImage;

	private static JTextPane detailsLabel;

	public static void main(String[] args) {
		System.out.println("loading strings");
		textBundle = new TolerantResourceBundle(ResourceBundle
				.getBundle("i18n"));
		// busyImage = loadImage("images/search-folder.png");
		busyImage = loadImage("images/search-folder.svg");
		System.out.println("creating tray icon");
		createTrayIcon();
		System.out.println("creating search box");
		createSearchBox();
		System.out.println("done");
		// busyImage = loadImage("images/search-folder.svg");
		setBusy(true);
		normalImage = loadImage("images/search-file.svg");
		setBusy(false);
	}

	private static void createSearchBox() {
		SearchForm searchForm = new SearchForm();
		final JTextField text = searchForm.getSearchToken();
		final JLabel textLabel = searchForm.getSearchTokenLabel();
		final JList resultList = searchForm.getResultList();
		textLabel.setText(textBundle.getString("PM_SEARCH"));

		model = new ResultListModel();
		resultList.setModel(model);
		detailsLabel = searchForm.getResultDetails();

		attachListeners(text, resultList);

		searchFormFrame = new JFrame(textBundle.getString("PM_PROJECTNAME"));
		searchFormFrame.setUndecorated(true);
		searchFormFrame.setSize(new Dimension(500, 500));
		searchFormFrame.add(searchForm);
		searchFormFrame.setVisible(false);
	}

	private static void attachListeners(final JTextField text,
			final JList resultList) {
		text.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					searchFormFrame.setVisible(false);
				} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
					resultList.requestFocusInWindow();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					launch(resultList.getSelectedIndex(), e.isShiftDown());
				} else {
					searchAgain(text.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		resultList.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					searchFormFrame.setVisible(false);
				} else if (e.getKeyCode() == KeyEvent.VK_UP
						&& resultList.getSelectedIndex() == 0) {
					text.requestFocusInWindow();
				} else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					launch(resultList.getSelectedIndex(), e.isShiftDown());
				}
			}
		});
		resultList.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					launch(resultList.getSelectedIndex(), e.isShiftDown());
				}
			}
		});
		resultList.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				File f = model.getFile(resultList.getSelectedIndex());
				detailsLabel.setText(f.getAbsolutePath() + "\n"
						+ textBundle.getString("PM_SIZE") + ": "
						+ formatFileSize(f.length()));
			}
		});
	}

	protected static String formatFileSize(double length) {
		if (length < 1024) {
			return Math.round(length) + " B";
		}
		length /= 1024;
		if (length < 1024) {
			return Math.round(length) + " KiB";
		}
		length /= 1024;
		if (length < 1024) {
			return Math.round(length) + " MiB";
		}
		length /= 1024;
		if (length < 1024) {
			return Math.round(length) + " GiB";
		}
		length /= 1024;
		if (length < 1024) {
			return Math.round(length) + " TiB";
		}
		return null;
	}

	protected static void launch(int selectedIndex, boolean shiftDown) {
		try {
			model.launch(selectedIndex, shiftDown);
		} catch (IOException e) {
			JOptionPane.showConfirmDialog(searchFormFrame, e
					.getLocalizedMessage(), textBundle
					.getString("PM_LAUNCH_PROBLEM"), JOptionPane.OK_OPTION
					| JOptionPane.ERROR_MESSAGE);
		}
	}

	protected static void searchAgain(String text) {
		// System.out.println("would search for " + text);
		model.searchFor(text);
	}

	private static void createTrayIcon() {
		ActionListener exitListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onExitClicked();
			}
		};
		ActionListener rebuildListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				rebuildClicked();
			}
		};
		ActionListener aboutListener = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane
						.showMessageDialog(null, textBundle
								.getString("PM_ABOUTTEXT"), textBundle
								.getString("PM_PROJECTNAME"),
								JOptionPane.INFORMATION_MESSAGE
										| JOptionPane.OK_OPTION);

			}
		};
		MouseListener mouseListener = new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					onTrayClick(e.getPoint());
				}
			}

			public void mouseEntered(MouseEvent e) {
			}

			public void mouseExited(MouseEvent e) {
			}

			public void mousePressed(MouseEvent e) {
			}

			public void mouseReleased(MouseEvent e) {
			}
		};
		PopupMenu popup = new PopupMenu();

		MenuItem rebuildMItem = new MenuItem(textBundle.getString("PM_REBUILD"));
		rebuildMItem.addActionListener(rebuildListener);
		popup.add(rebuildMItem);

		popup.addSeparator();

		MenuItem aboutMItem = new MenuItem(textBundle.getString("PM_ABOUT"));
		aboutMItem.addActionListener(aboutListener);
		popup.add(aboutMItem);
		MenuItem exitMItem = new MenuItem(textBundle.getString("PM_EXIT"));
		exitMItem.addActionListener(exitListener);
		popup.add(exitMItem);

		trayIcon = new TrayIcon(busyImage, textBundle
				.getString("PM_PROJECTNAME"), popup);
		trayIcon.addMouseListener(mouseListener);
		trayIcon.setImageAutoSize(true);

		try {
			tray.add(trayIcon);
		} catch (AWTException ex) {
			dieGraphically(ex);
		}
	}

	protected static void rebuildClicked() {
		setBusy(true);

		model.rebuildDatabase(new Callable<Void>() {

			@Override
			public Void call() throws Exception {
				setBusy(false);
				trayIcon.displayMessage(
						textBundle.getString("PM_REBUILD_DONE"), textBundle
								.getString("PM_PROJECTNAME"),
						TrayIcon.MessageType.INFO);
				return null;
			}
		});
	}

	protected static void setBusy(boolean b) {
		Image newImage;
		int cursor;
		if (b) {
			newImage = busyImage;
			cursor = Cursor.WAIT_CURSOR;
		} else {
			newImage = normalImage;
			cursor = Cursor.DEFAULT_CURSOR;
		}
		searchFormFrame.setCursor(Cursor.getPredefinedCursor(cursor));
		trayIcon.setImage(newImage);
	}

	protected static void onExitClicked() {
		tray.remove(trayIcon);
		searchFormFrame.setVisible(false);
		System.exit(0);
	}

	protected static void onTrayClick(Point point) {
		if (searchFormFrame.isVisible()) {
			searchFormFrame.setVisible(false);
		} else {
			searchFormFrame.setLocation(point);
			searchFormFrame.setVisible(true);
		}
	}

	private static void dieGraphically(AWTException ex) {
		JOptionPane.showMessageDialog(null, ex.getMessage(), textBundle
				.getString("PM_ERROR"), JOptionPane.ERROR_MESSAGE
				| JOptionPane.OK_OPTION);
		System.exit(-1);
	}

	private static Image loadImage(String name) {
		System.out.println("loading image " + name + " ...");
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		URL imgUrl = cl.getResource(name);

		System.out.println("converting image " + name + " ...");
		Dimension imgSize = tray.getTrayIconSize();

		Transcoder pngTransc = new PNGTranscoder();
		pngTransc.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(imgSize
				.getWidth()));
		pngTransc.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(
				imgSize.getHeight()));

		TranscoderInput tinImgChecking = new TranscoderInput(imgUrl.toString());
		ByteArrayOutputStream outImgChecking = null;

		try {
			outImgChecking = new ByteArrayOutputStream();
			TranscoderOutput toutImgChecking = new TranscoderOutput(
					outImgChecking);

			System.out.println("transcoding ...");
			pngTransc.transcode(tinImgChecking, toutImgChecking);
			System.out.println("transcoding done");
			outImgChecking.flush();
			outImgChecking.close();
		} catch (TranscoderException ex) {
			JOptionPane.showMessageDialog(null, textBundle
					.getString("E_SVGICON")
					+ ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE
					| JOptionPane.OK_OPTION);
			System.exit(-1);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, textBundle
					.getString("E_SVGICON")
					+ ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE
					| JOptionPane.OK_OPTION);
			System.exit(-1);
		}
		System.out.println("creating image " + name + " ...");

		Image img = Toolkit.getDefaultToolkit().createImage(
				outImgChecking.toByteArray());
		System.out.println("image " + name + " done");
		return img;
	}
}
