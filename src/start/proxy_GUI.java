package start;


import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class proxy_GUI {

	JFrame frmProxysettings;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the application.
	 */
	public proxy_GUI() {
		initialize();
	}

	public void kill(proxy_GUI gui) {
		gui = null;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmProxysettings = new JFrame();
		frmProxysettings.setTitle("ProxySettings");
		frmProxysettings.setResizable(false);
		frmProxysettings.setBounds(100, 100, 450, 300);
		frmProxysettings.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmProxysettings.getContentPane().setLayout(null);
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 446, 273);
		tabbedPane.add("Http",
				new Http_Proxy(tabbedPane, this.frmProxysettings));
		tabbedPane.add("Https",  new Https_Proxy(tabbedPane, this.frmProxysettings));
		tabbedPane.add("SocksV4", new SocksV4_Proxy(tabbedPane, this.frmProxysettings));
		tabbedPane.add("SocksV5", new SocksV5_Proxy(tabbedPane, this.frmProxysettings));
		frmProxysettings.getContentPane().add(tabbedPane);

	}

}
