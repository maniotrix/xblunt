package start;

import java.io.*;
import java.util.*;
import java.util.concurrent.Exchanger;
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
	public  int type;
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
	private long filesize;// size of file
	private int status;// status of download
	private long downloaded, progress;// no of bytes downloaded
	private HttpsURLConnection connectionhttps;
	private HttpURLConnection connectionhttp;
	Thread thread;

	// constructor for download
	public Download(int numthread, URL url,int Urltype) {
		this.url = url;
		filesize = -1;
		downloaded = 0;
		status = Downloading;
		thread_start = new long[numthread];
		thread_data = new long[numthread];
		thread_size = new long[numthread];
		thread_temp = new long[numthread];
		thread_file = new RandomAccessFile[numthread];
		this.type=Urltype;
		// start download
		download(numthread);

	}

	public String geturl() {
		return url.toString();
	}

	public void dataexch() {

		this.progress = thread_data[0] + thread_data[1] + thread_data[2]
				+ thread_data[3] + thread_data[4];

		this.statechanged();
		// System.out.println("downloaded = " + downloaded);

		// System.out.println("downloaded = " + downloaded);
	}

	public long getsize() {
		return filesize;
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
		for (int i = 0; i < numthreads; i++) {
			while (threads[i] != null && threads[i].isAlive()) {
				if (this.type == 0) {
					threads[i].connectionhttp.disconnect();
				} else
					threads[i].connectionhttps.disconnect();

			}
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
		if (ifactive == true)
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

	}

	// Get file name portion of URL.
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	@Override
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
			if (this.type == 0) {
				connectionhttp = (HttpURLConnection) url.openConnection();

				System.out.println("trying to connect and status= "
						+ this.getstatus());
				// Specify what portion of file to download.
				connectionhttp.setRequestProperty("Range", "bytes=" + 0 + "-");
				// Connect to server.
				connectionhttp.connect();
				if (ifresumed == false) {
					System.out.println(" mainthread died");
					return;
				}
				// make sure reponse code is in the 200 range
				if (connectionhttp.getResponseCode() / 100 != 2) {
					error();
				}
				// check for valid content length
				this.contentlength = connectionhttp.getContentLength();
				if (contentlength < 1)
					error();
			}
			if (this.type == 1) {
				connectionhttps = (HttpsURLConnection) url.openConnection();

				System.out.println("trying to connect and status= "
						+ this.getstatus());
				// Specify what portion of file to download.
				System.out.println("Specify what portion of file to download.");
				connectionhttps.setRequestProperty("Range", "bytes=" + 0 + "-");
				// Connect to server.
				System.out.println("Connect to server.");
				connectionhttps.connect();
				if (ifresumed == false) {
					System.out.println(" mainthread died");
					return;
				}
				// make sure reponse code is in the 200 range
				if (connectionhttps.getResponseCode() / 100 != 2) {
					error();
				}
				// check for valid content length
				this.contentlength = connectionhttps.getContentLength();
				if (contentlength < 1)
					error();
			}

			/*
			 * set the length of download if it is not already set
			 */
			if (filesize == -1 || filesize!=contentlength) {
				filesize = contentlength;
				statechanged();
				System.out.println("connected" + filesize + "status= "
						+ this.getstatus());
			}
			System.out.println("connected" + " and filesize= "+filesize );
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
				file = new RandomAccessFile(getFileName(url), "rw");
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
					System.out.println("mainthreaded exited");
				} catch (Exception e) {
				}
				

			}
			else
				System.out.println("mainthreaded exited");
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
