package start;

import java.io.*;
import java.util.*;
import java.net.*;

import javax.net.ssl.HttpsURLConnection;

//this class downloads a file from a url

public class Download extends java.util.Observable implements
		java.lang.Runnable {

	// max size of download buffer
	public static final int MAX_BUFFER_SIZE = 1024 * 8;

	// these are statuses names
	public static final String STATUSES[] = { "Downloading", "Paused",
			"Completed", "Error", "Cancelled" };
	// THESE ARE STATUS CODES
	public static final int Downloading = 0;
	public static final int Paused = 1;
	public static final int Completed = 2;
	public static final int Cancelled = 3;
	public static final int Errors = 4;
	public static final int http = 0;
	public static final int https = 1;
	public int type;
	boolean ifresumed = true;
	boolean ifactive = false;
	private int numthreads, contentlength;
	public DownThread[] threads;
	long[] thread_start;
	long[] thread_data;
	long[] thread_size;
	long[] thread_temp;
	RandomAccessFile[] thread_file;

	private URL url;// url of file
	private long filesize, downloadstartTime;// size of file
	private int status;// status of download
	private long downloaded, progress, downprogress, timeprogress;// no of bytes
																	// downloaded
	private HttpsURLConnection connectionhttps;
	private HttpURLConnection connectionhttp;
	Thread thread;

	// constructor for download
	public Download(int numthread, URL url, int Urltype) {
		this.url = url;
		filesize = -1;
		downloaded = 0;
		status = Downloading;
		thread_start = new long[numthread];
		thread_data = new long[numthread];
		thread_size = new long[numthread];
		thread_temp = new long[numthread];
		thread_file = new RandomAccessFile[numthread];
		this.type = Urltype;
		// start download
		download(numthread);

	}

	public String geturl() {
		return url.toString();
	}

	public void dataexch() {
		long temp = 0;
		for (int i = 0; i < numthreads; i++) {
			temp += thread_data[i];
		}
		this.progress = temp;
		if (System.nanoTime() - this.downloadstartTime >= 1000000000) {
			this.downprogress = progress - this.timeprogress;
			downloadstartTime = System.nanoTime();
			this.timeprogress=progress;
		}
		

		this.statechanged();
		// System.out.println("downloaded = " + downloaded);

		// System.out.println("downloaded = " + downloaded);
	}

	public long getsize() {
		return filesize;
	}

	public float getspeed() {
		if(status==Downloading)
		return (float)(this.downprogress/1024);
		else 
			return 0;
	}

	public float getprogress() {
		return ((float) this.progress / filesize) * 100;
	}

	public int getstatus() {
		return status;
	}

	// pause this download
	public void pause() {
		ifactive = false;
		ifresumed = false;
		status = Paused;
		dataexch();
		statechanged();

		try {
			thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// interrupting threads if alive
		if (thread != null && thread.isAlive()) {
			if (this.type == 0)
				connectionhttp.disconnect();
			else
				connectionhttps.disconnect();
			System.out.println("disconnected threads ");
		}
		try {
			for (int i = 0; i < numthreads; i++) {
				while (threads[i] != null && threads[i].isAlive()) {
					if (this.type == 0) {
						threads[i].connectionhttp.disconnect();
					} else
						threads[i].connectionhttps.disconnect();

				}
			}
		} catch (Exception e) {
		}

	}

	// resume this download
	public void resume() {

		status = this.Downloading;
		ifactive = true;
		ifresumed = true;
		download(numthreads);

		statechanged();
	}

	// cancel download
	public void cancel() {
		status = Cancelled;
		statechanged();

	}

	public long get_thread_data(int i) {
		System.out.println("creating data for thread " + i);
		if (ifactive == true && thread_start[i] > 0)
			return thread_start[i];

		return ((filesize * i) / numthreads);
	}

	// mark download an error
	public void error() {
		status = this.Errors;
		statechanged();
	}

	private String getFileName(URL url, int i) {
		String fileName = url.getFile();
		return (fileName.substring(fileName.lastIndexOf('/') + 1) + i);
	}

	// start or resume download
	public void download(int numthread) {
		this.numthreads = numthread;
		// System.out.println(filesize);

		thread = new Thread(this);
		thread.setPriority(Thread.NORM_PRIORITY);
		thread.start();
		this.timeprogress = 0;

	}

	// Get file name portion of URL.
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
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

	@Override
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
			if (this.type == http) {
				connectionhttp = (HttpURLConnection) url.openConnection();

				System.out.println("trying to connect and status= "
						+ this.getstatus());
				// Specify what portion of file to download.
				connectionhttp.setRequestProperty("Range", "bytes=" + 0 + "-");
				// Connect to server.
				try {
					connectionhttp.connect();
				} catch (IOException e) {
					System.out.println("connection error");
				}
				if (ifresumed == false) {
					System.out.println(" mainthread died");
					return;
				}
				// make sure reponse code is in the 200 range
				int ResponseCode = connectionhttp.getResponseCode();
				if (ResponseCode / 100 != 2) {

					System.out.println("unwanted response code= "
							+ ResponseCode);
					return;
				}
				// check for valid content length
				this.contentlength = connectionhttp.getContentLength();
				if (contentlength < 1)
					return;
			}
			if (this.type == https) {
				connectionhttps = (HttpsURLConnection) url.openConnection();

				System.out.println("trying to connect and status= "
						+ this.getstatus());
				// Specify what portion of file to download.
				System.out.println("Specify what portion of file to download.");
				connectionhttps.setRequestProperty("Range", "bytes=" + 0 + "-");
				// Connect to server.
				System.out.println("Connect to server.");
				try {
					connectionhttps.connect();
				} catch (Exception e) {
					System.out.println("connection error");
				}
				if (ifresumed == false) {
					System.out.println(" mainthread died");
					return;
				}
				// make sure reponse code is in the 200 range
				int ResponseCode = connectionhttps.getResponseCode();
				if (ResponseCode / 100 != 2) {

					System.out.println("unwanted response code= "
							+ ResponseCode);
					return;
				}
				// check for valid content length
				this.contentlength = connectionhttps.getContentLength();
				if (contentlength < 1) {
					System.out.println("incorrect contentlength= ");
					return;
				}

			}

			/*
			 * set the length of download if it is not already set
			 */
			if (filesize == -1 || filesize != contentlength) {
				filesize = contentlength;
				statechanged();
				System.out.println("connected" + filesize + "status= "
						+ this.getstatus());
			}
			System.out.println("connected" + " and filesize= " + filesize);
			if (ifresumed == false) {
				System.out.println(" mainthread died");
				return;
			}
			// initialize DownloadThread and byte arrays
			this.threads = new DownThread[numthreads];

			// instantiate and start() threads
			for (int i = 0; i < numthreads; i++) {

				threads[i] = new DownThread(url, numthreads, i,
						get_thread_data(i),
						(((filesize * (i + 1)) / numthreads) - 1), this);
				threads[i].start();
			}

			System.out.println("Downloading...status= " + this.getstatus());
			// wait for threads to complete
			for (int i = 0, j = 0; i < numthreads; i++) {
				System.out.println(" data error occured" + "status ="
						+ this.getstatus());
				// while (status == this.Downloading) {
				while (threads[i].isAlive()) {
				}

			}
			System.out.println("opening file");

			// open file and seek to the end of it
			if (this.status == Downloading) {
				if (this.type == http)
					file = new RandomAccessFile(
							Download.getFileName(connectionhttp), "rw");
				else
					file = new RandomAccessFile(
							Download.getFileName(connectionhttps), "rw");
				file.seek(0);
				for (int i = 0; i < numthreads; i++) {
					thread_file[i].seek(0);
					long threadsize = thread_file[i].length();
					long temp = 0l;
					System.out.println("writing for" + "" + i + "  "
							+ threadsize);
					while (status == Downloading) {
						if (temp == 0)
							System.out.println("entered loop " + " " + i);

						if (temp == threadsize) {
							System.out.println("loop broke at if-condition"
									+ " " + i);
							break;
						}
						/*
						 * set buffer as size of file to be copied
						 */
						byte buffer[];
						if (threadsize - temp > Download.MAX_BUFFER_SIZE) {
							buffer = new byte[Download.MAX_BUFFER_SIZE];

						} else {
							buffer = new byte[(int) (threadsize - temp)];

						}

						// read from thread-file into main-file
						int read = thread_file[i].read(buffer);
						if (read == -1) {
							System.out.println("loop broke at read==-1");
							break;

						}
						file.write(buffer, 0, read);
						temp += read;
						statechanged();
					}
					System.out.println("bytes wriiten "
							+ thread_file[i].length() + " "
							+ file.getFilePointer());
					thread_file[i].close();
					if (this.type == http)
						new File(Download.getFileName(connectionhttp) + "_" + i)
								.delete();
					else
						new File(Download.getFileName(connectionhttps) + "_"
								+ i).delete();

				}
			}
			/*
			 * change status to finished if this point was reached because
			 * downloading was finished
			 */
			if (status == Downloading) {
				status = Completed;
				System.out.println("error occured" + "status ="
						+ this.getstatus());
				statechanged();

			}

		} catch (Exception e) {
			System.out.println("error occured");
			error();
			e.printStackTrace();

		} finally {
			// close file
			if (file != null) {
				try {
					file.close();
					System.out.println("mainthread exited file!=null");
				} catch (Exception e) {
				}

			} else
				System.out.println("mainthread exited");
			// close connection to server
			if (stream != null) {
				try {
					stream.close();

				} catch (Exception e) {
				}
			}
		}

	}

	// notify observers that its state has changed
	public void statechanged() {
		this.setChanged();
		this.notifyObservers();
	}

}
