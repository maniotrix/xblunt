package start;

import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

// The Download Manager.
public class DownloadManager extends JFrame implements Observer {// Add download
																	// text
																	// field.
	private JTextField addTextField;
	// Download table's data model.
	private DownloadsTableModel tableModel;
	// Table listing downloads.
	private JTable table;
	// These are the buttons for managing the selected download.
	private JButton pauseButton, resumeButton;
	private JButton cancelButton, clearButton;
	// Currently selected download.
	private Download selectedDownload;
	// Flag for whether or not table selection is being cleared.
	private boolean clearing;

	// Constructor for Download Manager.
	public DownloadManager() {
		// Set application title.
		setTitle("Download Manager");
		// Set window size.
		setSize(800, 480);
		// Handle window closing events.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				actionExit();
			}
		});
		// Set up file menu.
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		JMenuItem fileExitMenuItem = new JMenuItem("Exit", KeyEvent.VK_X);
		fileExitMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit();
			}
		});
		fileMenu.add(fileExitMenuItem);
		menuBar.add(fileMenu);
		setJMenuBar(menuBar);
		
		JMenu mnInternet = new JMenu("Proxy");
		menuBar.add(mnInternet);
		
		JMenuItem mntmDirect = new JMenuItem("Direct");
		mntmDirect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new Proxy().removeProxy();
			}
		});
		mnInternet.add(mntmDirect);
		
		
		JMenuItem mntmManual = new JMenuItem("Manual");
		mntmManual.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			new proxy_GUI().frmProxysettings.setVisible(true);
			}
		});
		
		JMenuItem mntmSystem = new JMenuItem("System");
		mntmSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.setProperty("java.net.useSystemProxies", "true");
			}
		});
		mnInternet.add(mntmSystem);
		mnInternet.add(mntmManual);
		// Set up add panel.
		JPanel addPanel = new JPanel();
		addTextField = new JTextField(30);
		addPanel.add(addTextField);
		JButton addButton = new JButton("Add Download");
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionAdd();
			}
		});
		addPanel.add(addButton);
		// Set up Downloads table.
		tableModel = new DownloadsTableModel();
		table = new JTable(tableModel);
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						tableSelectionChanged();
					}
				});
		// Allow only one row at a time to be selected.
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		// Set up ProgressBar as renderer for progress column.
		ProgressRenderer renderer = new ProgressRenderer(0, 100);
		renderer.setStringPainted(true); // show progress text
		table.setDefaultRenderer(JProgressBar.class, renderer);
		// Set table's row height large enough to fit JProgressBar.
		table.setRowHeight((int) renderer.getPreferredSize().getHeight());
		// Set up downloads panel.
		JPanel downloadsPanel = new JPanel();
		downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
		downloadsPanel.setLayout(new BorderLayout());
		downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
		// Set up buttons panel.
		JPanel buttonsPanel = new JPanel();
		pauseButton = new JButton("Pause");
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPause();
			}
		});
		pauseButton.setEnabled(false);

		buttonsPanel.add(pauseButton);
		resumeButton = new JButton("Resume");
		resumeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionResume();
			}
		});
		resumeButton.setEnabled(false);
		buttonsPanel.add(resumeButton);
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionCancel();
			}
		});
		cancelButton.setEnabled(false);
		buttonsPanel.add(cancelButton);
		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClear();
			}
		});
		clearButton.setEnabled(false);
		buttonsPanel.add(clearButton);
		// Add panels to display.
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(addPanel, BorderLayout.NORTH);
		getContentPane().add(downloadsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	}

	// Exit this program.
	private void actionExit() {
		System.exit(0);
	}

	// Add a new download.
	private void actionAdd() {
		int urltype = set_url_type(addTextField.getText());
		URL verifiedUrl = verifyUrl(addTextField.getText());
		if (verifiedUrl != null) {
			tableModel.addDownload(new Download(8, verifiedUrl, urltype));
			addTextField.setText(""); // reset add text field
		} else {
			JOptionPane.showMessageDialog(this, "Invalid Download URL",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// set url type
	public static int set_url_type(String url) {
		int temp = 0;
		if (url.toLowerCase().startsWith("https://"))
			temp = Download.https;
		else
			temp = Download.http;
		return temp;

	}

	// Verify download URL.

	public static URL verifyUrl(String url) {
		URL verifiedUrl = null;
		if (!url.toLowerCase().startsWith("https://")
				|| !url.toLowerCase().startsWith("http://"))
			verifiedUrl = null;
		// Only allow HTTP URLs.
		if (url.toLowerCase().startsWith("https://")
				|| url.toLowerCase().startsWith("http://")) {

			// Verify format of URL.
			// use proxy if behind a proxy
			try {
				verifiedUrl = new URL(url);
			} catch (Exception e) {
				return null;
			}
		}
		// Make sure URL specifies a file.
		if (verifiedUrl.getFile().length() < 2)
			return null;
		return verifiedUrl;
	}

	// Called when table row selection changes.
	private void tableSelectionChanged() {
		/*
		 * Unregister from receiving notifications from the last selected
		 * download.
		 */
		if (selectedDownload != null)
			selectedDownload.deleteObserver(DownloadManager.this);
		/*
		 * If not in the middle of clearing a download, set the selected
		 * download and register to receive notifications from it.
		 */
		if (!clearing && table.getSelectedRow() > -1) {
			selectedDownload = tableModel.getDownload(table.getSelectedRow());
			selectedDownload.addObserver(DownloadManager.this);
			updateButtons();
		}
	}

	// Pause the selected download.
	private void actionPause() {
		selectedDownload.pause();
		updateButtons();
	}

	// Resume the selected download.
	private void actionResume() {
		selectedDownload.resume();
		updateButtons();
	}

	// Cancel the selected download.
	private void actionCancel() {
		selectedDownload.cancel();
		updateButtons();
	}

	// Clear the selected download.
	private void actionClear() {
		clearing = true;
		tableModel.clearDownload(table.getSelectedRow());
		clearing = false;
		selectedDownload = null;
		updateButtons();
	}

	/*
	 * Update each button's state based off of the currently selected download's
	 * status.
	 */
	private void updateButtons() {
		if (selectedDownload != null) {
			int status = selectedDownload.getstatus();
			switch (status) {
			case Download.Downloading:
				pauseButton.setEnabled(true);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				break;
			case Download.Paused:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				break;
			case Download.Errors:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
				break;
			default: // COMPLETE or CANCELLED
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
			}
		} else {
			// No download is selected in table.
			pauseButton.setEnabled(false);
			resumeButton.setEnabled(false);
			cancelButton.setEnabled(false);
			clearButton.setEnabled(false);
		}
	}

	/*
	 * Update is called when a Download notifies its observers of any changes.
	 */
	public void update(Observable o, Object arg) {
		// Update buttons if the selected download has changed.
		if (selectedDownload != null && selectedDownload.equals(o))
			updateButtons();
	}

	// Run the Download Manager.
	public static void main(String[] args) {
		// System.setProperty("java.net.useSystemProxies", "true");
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DownloadManager manager = new DownloadManager();
				manager.setVisible(true);
			}
		});
	}
}