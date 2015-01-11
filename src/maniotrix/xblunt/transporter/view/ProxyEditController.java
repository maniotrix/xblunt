package maniotrix.xblunt.transporter.view;

import java.net.MalformedURLException;
import java.net.URL;

import org.controlsfx.dialog.Dialog;
import org.controlsfx.dialog.Dialogs;

import maniotrix.xblunt.transporter.MainApp;
import maniotrix.xblunt.transporter.util.Proxy;
import maniotrix.xblunt.transporter.util.UrlUtility;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ProxyEditController {
	@FXML
	private TextField host;
	@FXML
	private TextField port;
	@FXML
	private TextField username;
	@FXML
	private PasswordField password;

	private Stage dialogStage;
	public static boolean okClicked = false;

	public static String proxytype = "";

	public static StringProperty hostproperty = new SimpleStringProperty();
	public static StringProperty portproperty = new SimpleStringProperty();
	public static StringProperty usernameproperty = new SimpleStringProperty();
	public static StringProperty passwordproperty = new SimpleStringProperty();

	/**
	 * Initializes the controller class. This method is automatically called
	 * after the fxml file has been loaded.
	 */
	@FXML
	private void initialize() {
	}

	/**
	 * Sets the stage of this dialog.
	 * 
	 * @param dialogStage
	 */
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

	/**
	 * Returns true if the user clicked OK, false otherwise.
	 * 
	 * @return
	 */
	public boolean isOkClicked() {
		return okClicked;
	}

	/**
	 * Called when the user clicks cancel.
	 */
	@FXML
	private void handleCancel() {
		dialogStage.close();
	}

	@FXML
	private void handleOk() {
		hostproperty.set(host.getText());
		portproperty.set(port.getText());
		usernameproperty.set(username.getText());
		passwordproperty.set(password.getText());
		okClicked = true;
		dialogStage.close();
	}

	@FXML
	public void handleDirect() {
		Proxy.removeProxy();
	}

	@FXML
	public void handleSystem() {
		System.setProperty("java.net.useSystemProxies", "true");
	}

	@FXML
	public void handleHttp() {
		ProxyEditController.proxytype = "http";
		boolean okClicked = MainApp.showProxyEditor();
		if (okClicked) {
			Proxy.http_Host = hostproperty.get();
			Proxy.http_Port = portproperty.get();
			Proxy.http_Username = usernameproperty.get();
			Proxy.http_Password = passwordproperty.get();
			Proxy.removeProxy();
			Proxy.setHttp(Proxy.http_Host, Proxy.http_Port,
					Proxy.http_Password, Proxy.http_Username);
			Proxy.setHttps(Proxy.http_Host, Proxy.http_Port,
					Proxy.http_Password, Proxy.http_Username);
			ProxyEditController.createProxyDialog(Proxy.http_Host,
					Proxy.http_Port, Proxy.http_Username, Proxy.http_Password);
		}
	}

	@SuppressWarnings("deprecation")
	public static void createProxyDialog(String host, String port, String user,
			String pass) {
		Dialogs.create()
				.title("Proxy")
				.masthead("Proxy  Saved")
				.message(
						"host= " + host + "\n" + "Port= " + port + "\n"
								+ "Username= " + user + "\n" + "Passowrd= "
								+ pass).showInformation();
	}

	/*
	 * @FXML public void handleHttps() { ProxyEditController.proxytype =
	 * "https"; boolean okClicked = MainApp.showProxyEditor(); if (okClicked) {
	 * Proxy.https_Host = hostproperty.get(); Proxy.https_Port =
	 * portproperty.get(); Proxy.https_Username = usernameproperty.get();
	 * Proxy.https_Password = passwordproperty.get(); try {
	 * Proxy.setHttps(Proxy.https_Host, Proxy.https_Port, Proxy.https_Password,
	 * Proxy.https_Username);
	 * ProxyEditController.createProxyDialog(Proxy.https_Host, Proxy.https_Port,
	 * Proxy.https_Username, Proxy.https_Password); } catch (Exception e) {
	 * e.getMessage(); } } }
	 */

	@FXML
	public void handleSocks() {
		ProxyEditController.proxytype = "socks";
		boolean okClicked = MainApp.showProxyEditor();
		if (okClicked) {
			Proxy.socksHost = hostproperty.get();
			Proxy.socksPort = portproperty.get();
			Proxy.socksUsername = usernameproperty.get();
			Proxy.socksPassword = passwordproperty.get();
			Proxy.removeProxy();
			Proxy.setProxySocks_v5(Proxy.socksHost, Proxy.socksPort,
					Proxy.socksPassword, Proxy.socksUsername);
			ProxyEditController.createProxyDialog(Proxy.socksHost,
					Proxy.socksPort, Proxy.socksUsername, Proxy.socksPassword);
		}
	}

	@FXML
	public void handleUpdateButton() {
		String urlname = "github.com/maniotrix/xblunt";
		// ProxyEditController.openlink(urlname);
		ProxyEditController.createHelpDialogs("Update/Homepage Link",
				"Visit github homepage:\n" + urlname);
	}

	@FXML
	public void handleReportButton() {
		String urlname = "github.com/maniotrix/xblunt/issues/new";
		ProxyEditController.createHelpDialogs("Help/Report Link",
				"Visit github issue page:\n" + urlname);
	}

	@FXML
	public void handleLicenseButton() {
		String urlname = "github.com/maniotrix/xblunt/license.txt";
		ProxyEditController.createHelpDialogs("License Link",
				"License on github:\n" + urlname);

	}
	
	@FXML
	public void handleAboutButton() {
		String urlname = "github.com/maniotrix";
		ProxyEditController.createHelpDialogs("About",
				"Authoe:Prince\nGithub Page:" + urlname);

	}
	/*
	 * public static void openlink(String urlname){ URL url; try { url = new
	 * URL(urlname); UrlUtility.openWebpage(url); } catch (MalformedURLException
	 * e) { // TODO Auto-generated catch block e.printStackTrace(); } }
	 */

	@SuppressWarnings("deprecation")
	public static void createHelpDialogs(String masthead, String message) {
		Dialogs.create().title("Checkout").masthead(masthead).message(message)
				.showInformation();
	}
}
