package maniotrix.xblunt.transporter.util;

import java.net.URL;

public class UrlUtility {
	public static URL verifyUrl(String url) {
		URL verifiedUrl = null;
		// Only allow HTTP URLs.
		if (url.toLowerCase().startsWith("https://")
				|| url.toLowerCase().startsWith("http://")) {

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
}
