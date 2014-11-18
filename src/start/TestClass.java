package start;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.concurrent.*;

public class TestClass {

	public static void main(String[] args) {
		// System.setProperty("java.net.useSystemProxies", "true");
		// System.setProperty("http.proxyHost", "127.0.0.1");
		// System.setProperty("http.proxyPort", "9050");
		
		String s="http://download.oracle.com/otn-pub/java/jdk/8u25-b17/jdk-8u25-linux-x64.tar.gz?AuthParam=1416018901_434cd221a1821006be92992be32ce29e";
		Download test = new Download(
				8,
				verifyUrl(s),DownloadManager.set_url_type(s));

	}

	// Verify download URL.
	public static URL verifyUrl(String url) {
		URL verifiedUrl = null;
		if (!url.toLowerCase().startsWith("https://")
				|| !url.toLowerCase().startsWith("http://"))
			verifiedUrl =null;
		// Only allow HTTP URLs.
		if (url.toLowerCase().startsWith("https://")
				|| url.toLowerCase().startsWith("http://")){
			
		// Verify format of URL.
		
		try {
			verifiedUrl = new URL(url);
			if (url.toLowerCase().startsWith("https://")) {
				useProxyhttps16();
			}
			if (url.toLowerCase().startsWith("http://")) {
				useProxyhttp16();
			}

		} catch (Exception e) {
			return null;
		}}
		// Make sure URL specifies a file.
		if (verifiedUrl.getFile().length() < 2)
			return null;
		return verifiedUrl;
	}

	public static void useProxyhttps() {

		String host = "10.1.1.18";
		String port = "80";
		System.out.println("Using proxy: " + host + ":" + port);
		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", port);
		System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
		final String authUser = "506.13135079";
		final String authPassword = "iitcc2013";
		Authenticator.setDefault(new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword
						.toCharArray());
			}
		});

		System.setProperty("https.proxyUser", authUser);
		System.setProperty("https.proxyPassword", authPassword);
	}

	public static void useProxyhttp() {

		String host = "10.1.1.19";
		String port = "80";
		System.out.println("Using proxy: " + host + ":" + port);
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", port);
		System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
		final String authUser = "506.13135079";
		final String authPassword = "iitcc2013";
		Authenticator.setDefault(new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword
						.toCharArray());
			}
		});

		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);
	}

	public static void useProxyhttps16() {

		String host = "10.1.1.16";
		String port = "80";
		System.out.println("Using proxy: " + host + ":" + port);
		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", port);
		System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
		final String authUser = "067.9721097213";
		final String authPassword = "kshitizkmr091";
		Authenticator.setDefault(new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword
						.toCharArray());
			}
		});

		System.setProperty("https.proxyUser", authUser);
		System.setProperty("https.proxyPassword", authPassword);
	}

	public static void useProxyhttp16() {

		String host = "10.1.1.16";
		String port = "80";
		System.out.println("Using proxy: " + host + ":" + port);
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", port);
		System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
		final String authUser = "067.9721097213";
		final String authPassword = "kshitizkmr091";
		Authenticator.setDefault(new Authenticator() {
			public PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(authUser, authPassword
						.toCharArray());
			}
		});

		System.setProperty("http.proxyUser", authUser);
		System.setProperty("http.proxyPassword", authPassword);
	}

}