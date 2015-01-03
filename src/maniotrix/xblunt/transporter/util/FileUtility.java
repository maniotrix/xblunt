package maniotrix.xblunt.transporter.util;

import java.io.IOException;
import java.net.URLConnection;
import java.util.StringTokenizer;

public class FileUtility {
	/**
	 * Returns the file name associated to an url connection.<br />
	 * The result is not a path but just a file name.
	 * 
	 * @param urlC
	 *            - the url connection
	 * @return the file name
	 * 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static final String getFileName(URLConnection urlC)
			throws IOException {
		String fileName = null;

		String contentDisposition = urlC.getHeaderField("content-disposition");

		if (contentDisposition != null) {
			fileName = extractFileNameFromContentDisposition(contentDisposition);
		}

		// if the file name cannot be extracted from the content-disposition
		// header, using the url.getFilename() method
		if (fileName == null) {
			StringTokenizer st = new StringTokenizer(urlC.getURL().getFile(),
					"/");
			while (st.hasMoreTokens())
				fileName = st.nextToken();
		}

		return fileName;
	}

	/**
	 * Extract the file name from the content disposition header.
	 * <p>
	 * See <a
	 * href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec19.html">http:
	 * //www.w3.org/Protocols/rfc2616/rfc2616-sec19.html</a> for detailled
	 * information regarding the headers in HTML.
	 * 
	 * @param contentDisposition
	 *            - the content-disposition header. Cannot be <code>null>/code>.
	 * @return the file name, or <code>null</code> if the content-disposition
	 *         header does not contain the filename attribute.
	 */
	public static final String extractFileNameFromContentDisposition(
			String contentDisposition) {
		System.out.println("content disposition = " + contentDisposition);
		String[] attributes = contentDisposition.split(";");

		for (String a : attributes) {
			if (a.toLowerCase().contains("filename")) {
				// The attribute is the file name. The filename is between
				// quotes.
				try {
					return a.substring(a.indexOf('\"') + 1, a.lastIndexOf('\"'));
				} catch (StringIndexOutOfBoundsException e) {
					return a.substring(a.indexOf('=') + 1, a.length());
				}
			}
		}

		// not found
		return null;
	}
}
