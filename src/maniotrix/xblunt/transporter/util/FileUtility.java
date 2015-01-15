package maniotrix.xblunt.transporter.util;

//import java.io.File;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.StringTokenizer;



public class FileUtility {
	
	
	/**
	 * extract filename from url.
	 * @param string
	 * @return filename
	 */
	public static String getInitialname(String string){
		String name=null;
		try {
				StringTokenizer st = new StringTokenizer(string,
						"/");
				while (st.hasMoreTokens())
					name = st.nextToken();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return name;
	}
	
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
       //if(FileUtility.checkFileExistence(fileName))
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
				} catch (Exception e) {
					return a.substring(a.indexOf('=') + 1, a.length());
				}
			}
		}

		// not found
		return null;
	}
	/*public static boolean checkFileExistence(String tempName){
		File file=new File(tempName);
		return file.exists();
	}*/
	
	/*public static String getCountedName(String FileName){
		if(FileUtility.checkFileExistence(FileName)==true)
		
	}*/
	
	public static void openFile(File file){
		  if (!Desktop.isDesktopSupported()) {
			    return ;
			  }

			  Desktop desktop = Desktop.getDesktop();

			  try {
				  if( desktop.isSupported(Desktop.Action.OPEN)){
					 
					  System.out.println("opening file");
					   new Runnable() {
						public void run() {
							try {
								 System.out.println("opened file");
								desktop.open(file);
								return;
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					};
				  
				  
				  }
			  } catch (Exception e) {
				  e.printStackTrace();
			    // Log an error
			    return ;
			  }
			  //System.out.println("opening file");
			  return ;
	}
	
	public boolean editFile(final File file) {
		  if (!Desktop.isDesktopSupported()) {
		    return false;
		  }

		  Desktop desktop = Desktop.getDesktop();
		  if (!desktop.isSupported(Desktop.Action.EDIT)) {
		    return false;
		  }

		  try {
		    desktop.edit(file);
		  } catch (Exception e) {
		    // Log an error
		    return false;
		  }

		  return true;
		}
}
