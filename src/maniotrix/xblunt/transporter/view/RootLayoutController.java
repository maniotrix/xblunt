package maniotrix.xblunt.transporter.view;

import java.net.MalformedURLException;
import java.net.URL;

import javafx.fxml.FXML;
import maniotrix.xblunt.transporter.MainApp;
import maniotrix.xblunt.transporter.util.UrlUtility;

public class RootLayoutController {

	@FXML
	public void handleUpdateButton(){
		String urlname="https://github.com/maniotrix/xblunt";
		URL url;
		try {
			url = new URL(urlname);
			UrlUtility.openWebpage(url);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
