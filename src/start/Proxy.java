package start;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class Proxy {
	static String http_Host = "";
	static String http_Port = "";
	static String http_Username = "";
	static String http_Password = "";
	static String https_Host = "";
	static String https_Port = "";
	static String https_Username = "";
	static String https_Password = "";
	static String socks4_Host = "";
	static String socks4_Port = "";
	static String socks4_Username = "";
	static String socks4_Password = "";
	static String socks5_Host = "";
	static String socks5_Port = "";
	static String socks5_Username = "";
	static String socks5_Password = "";
	static boolean samehttps=false;
	static boolean samesocks=false;

	public void setHttp(String host, String port, final String password,
			final String username) {
		System.out.println("Using Http proxy: " + host + ":" + port+" username= "+username+" password= "+password);
		System.setProperty("http.proxyHost", host);
		System.setProperty("http.proxyPort", port);
		System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
		if (password != "" && username != "") {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});

			System.setProperty("http.proxyUser", username);
			System.setProperty("http.proxyPassword", password);
		}

	}

	public void setHttps(String host, String port, final String password,
			final String username) {
		System.out.println("Using Https proxy: " + host + ":" + port+" username= "+username+" password= "+password);
		System.setProperty("https.proxyHost", host);
		System.setProperty("https.proxyPort", port);
		System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
		if (password != "" && username != "") {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});

			System.setProperty("https.proxyUser", username);
			System.setProperty("https.proxyPassword", password);
		}

	}

	public  void useProxySocks_v5(String host, String port,
			final String password, final String username) {

		System.out.println("Using Socks proxy: " + host + ":" + port+" username= "+username+" password= "+password);
		System.setProperty("socksProxyVersion", "5");
		System.setProperty("socksProxyHost", host);
		System.setProperty("socksProxyPort", port);
		System.setProperty("socks.nonProxyHosts", "localhost|127.0.0.1");
		if (password != "" && username != "") {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});
			System.setProperty("java.net.socks.username", username);
			System.setProperty("java.net.socks.password", password);
		}
	}
	public  void useProxySocks_v4(String host, String port,
			final String password, final String username) {

		System.out.println("Using Socks proxy: " + host + ":" + port+" username= "+username+" password= "+password);
		System.setProperty("socksProxyVersion", "4");
		System.setProperty("socksProxyHost", host);
		System.setProperty("socksProxyPort", port);
		System.setProperty("socks.nonProxyHosts", "localhost|127.0.0.1");
		if (password != "" && username != "") {
			Authenticator.setDefault(new Authenticator() {
				public PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(username, password
							.toCharArray());
				}
			});
			System.setProperty("java.net.socks.username", username);
			System.setProperty("java.net.socks.password", password);
		}
	}
	public void removeProxy(){
		System.clearProperty("http.proxyHost" );
		System.clearProperty("http.proxyPort" );
		System.clearProperty("http.proxyUser" );
		System.clearProperty("http.proxyPassword" );
		
		System.clearProperty("https.proxyHost" );
		System.clearProperty("https.proxyPort" );
		System.clearProperty("https.proxyUser" );
		System.clearProperty("https.proxyPassword" );
		
		System.clearProperty("socksProxyVersion" );
		System.clearProperty("socksProxyHost" );
		System.clearProperty("socksProxyPort" );
		
		System.clearProperty("java.net.socks.username" );
		System.clearProperty("java.net.socks.password" );
		
		System.clearProperty("java.net.useSystemProxies");
		
		System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
		System.setProperty("https.nonProxyHosts", "localhost|127.0.0.1");
		System.setProperty("socks.nonProxyHosts", "localhost|127.0.0.1");
	}
	
}