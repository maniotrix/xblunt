package maniotrix.xblunt.transporter.util;

import java.awt.Desktop;
import java.net.URI;
import java.net.URISyntaxException;
/*import java.io.InputStream;
 import java.net.HttpURLConnection;
 import java.net.MalformedURLException;*/
import java.net.URL;

public class UrlUtility {
	/*
	 * public static void checkForUpdate() { new Runnable() { public void run()
	 * { InputStream stream = null; HttpURLConnection connection; try { URL
	 * url=new URL(""); connection=(HttpURLConnection) url.openConnection();
	 * 
	 * 
	 * 
	 * } catch (Exception e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * 
	 * } }; }
	 */

	public static URL verifyUrl(String url) {
		URL verifiedUrl = null;
		// Only allow HTTP URLs.
		if (url.toLowerCase().startsWith("https://")
				|| url.toLowerCase().startsWith("http://")) {

			try {
				verifiedUrl = new URL(url);
				System.out.println(verifiedUrl.toString());

			} catch (Exception e) {
				return null;
			}
		}
		// Make sure URL specifies a file.
		if (verifiedUrl != null && verifiedUrl.getFile().length() < 2)
			return null;
		return verifiedUrl;
	}

	public static void openWebpage(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop()
				: null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
	
	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}

}
