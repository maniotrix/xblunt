package start;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JButton;

public class Http_Proxy extends JPanel {
	private JTextField host;
	private JTextField port;
	private JTextField username;
	private JTextField password;

	public Http_Proxy(final JTabbedPane Pane, final JFrame frame) {
		setBounds(20, 11, 371, 239);
		setLayout(null);
		JLabel lblNewLabel_1 = new JLabel("Host:");
		lblNewLabel_1.setBounds(26, 29, 46, 14);
		add(lblNewLabel_1);

		host = new JTextField();
		host.setBounds(83, 26, 142, 20);
		add(host);
		host.setColumns(10);

		JLabel lblPort = new JLabel("Port");
		lblPort.setBounds(233, 29, 33, 14);
		add(lblPort);

		port = new JTextField();
		port.setColumns(10);
		port.setBounds(261, 26, 46, 20);
		add(port);

		JCheckBox authentication = new JCheckBox("Requires Authentication");
		authentication.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (username.isEnabled() && password.isEnabled()) {
					username.setEnabled(false);
					password.setEnabled(false);
				} else {
					username.setEnabled(true);
					password.setEnabled(true);
				}
			}
		});
		authentication.setBounds(25, 63, 142, 23);
		add(authentication);

		JLabel lblUsername = new JLabel("UserName:");
		lblUsername.setBounds(48, 93, 56, 14);
		add(lblUsername);

		username = new JTextField();
		username.setColumns(10);
		username.setBounds(124, 93, 203, 20);
		add(username);
		username.setEnabled(false);

		JLabel lblPassword = new JLabel("Password:");
		lblPassword.setBounds(48, 118, 56, 14);
		add(lblPassword);

		password = new JTextField();
		password.setColumns(10);
		password.setBounds(124, 124, 203, 20);
		add(password);
		password.setEnabled(false);

		host.setText(Proxy.http_Host);
		port.setText(Proxy.http_Port);
		password.setText(Proxy.http_Password);
		username.setText(Proxy.http_Username);

		JCheckBox UseSameForHttps = new JCheckBox("Use Same for Https");
		UseSameForHttps.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Pane.isEnabledAt(1)) {
					Pane.setEnabledAt(1, false);
				} else {
					{
						Pane.setEnabledAt(1, true);
					}
				}
				if (UseSameForHttps.isSelected())
					new Proxy().setHttps(host.getText(), port.getText(),
							password.getText(), username.getText());
			}

		});
		UseSameForHttps.setBounds(25, 166, 142, 23);
		add(UseSameForHttps);

		JCheckBox useSameForsocks = new JCheckBox("Use Same for Socks");
		useSameForsocks.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (int i = 2; i < 4; i++) {
					if (Pane.isEnabledAt(i)) {
						Pane.setEnabledAt(i, false);
					} else {
						Pane.setEnabledAt(i, true);
					}
				}
				if (useSameForsocks.isSelected()) {
					new Proxy().useProxySocks_v4(host.getText(),
							port.getText(), password.getText(),
							username.getText());
					new Proxy().useProxySocks_v5(host.getText(),
							port.getText(), password.getText(),
							username.getText());
				}
			}
		});
		useSameForsocks.setBounds(165, 166, 142, 23);
		add(useSameForsocks);

		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (Pane.isEnabledAt(0)) {
					new Proxy().setHttp(host.getText(), port.getText(),
							password.getText(), username.getText());
					Proxy.http_Host = host.getText();
					Proxy.http_Port = port.getText();
					Proxy.http_Password = password.getText();
					Proxy.http_Username = username.getText();
					frame.dispose();
				}
			}
		});
		btnOk.setBounds(136, 205, 64, 23);
		add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
		btnCancel.setBounds(233, 205, 74, 23);
		add(btnCancel);
	}
}
