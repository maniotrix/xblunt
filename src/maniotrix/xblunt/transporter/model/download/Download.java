package maniotrix.xblunt.transporter.model.download;

import java.io.*;
import java.net.*;

import maniotrix.xblunt.transporter.model.DownloadModel;
import maniotrix.xblunt.transporter.util.FileUtility;

//this class downloads a file from a url

public class Download implements
		java.lang.Runnable {

	// max size of download buffer
	public static final int MAX_BUFFER_SIZE = 1024 * 8;
	DownloadModel observer;
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
	private Status status;// status of download
	private long progress, downprogress, timeprogress;// no of bytes
														// downloaded
	private HttpURLConnection connectionhttp;
	Thread thread;

	// constructor for download
	public Download(int numthread, URL url,DownloadModel model) {
		this.observer=model;
		this.url = url;
		filesize = -1;
		status = Status.Downloading;
		thread_start = new long[numthread];
		thread_data = new long[numthread];
		thread_size = new long[numthread];
		thread_temp = new long[numthread];
		thread_file = new RandomAccessFile[numthread];
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
			this.timeprogress = progress;
		}

		this.statechanged();
		// System.out.println("downloaded = " + downloaded);

		// System.out.println("downloaded = " + downloaded);
	}

	public Long getsize() {
		return filesize;
	}

	public Float getspeed() {
		if (status == Status.Downloading)
			return (float) (this.downprogress / 1024);
		else
			return (float) 0;
	}

	public Float getprogress() {
		return ((float) this.progress / filesize) * 100;
	}

	public Status getstatus() {
		return status;
	}

	// pause this download
	public void pause() {
		ifactive = false;
		ifresumed = false;
		status = Status.Paused;
		dataexch();
		statechanged();

		// interrupting threads if alive
		if (thread != null && thread.isAlive()) {
			connectionhttp.disconnect();
			System.out.println("disconnected threads ");
		}
		try {
			for (int i = 0; i < numthreads; i++) {
				while (threads[i] != null && threads[i].isAlive()) {
					threads[i].connectionhttp.disconnect();

				}
			}
		} catch (Exception e) {
		}

	}

	// resume this download
	public void resume() {

		status = Status.Downloading;
		ifactive = true;
		ifresumed = true;
		download(numthreads);

		statechanged();
	}

	// cancel download
	public void cancel() {
		status = Status.Cancelled;
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
		status = Status.Error;
		statechanged();
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



	@Override
	public void run() {
		RandomAccessFile file = null;
		InputStream stream = null;

		try {
			// Open connection to URL.
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

				System.out.println("unwanted response code= " + ResponseCode);
				return;
			}
			// check for valid content length
			this.contentlength = connectionhttp.getContentLength();
			if (contentlength < 1)
				return;

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
			for (int i = 0; i < numthreads; i++) {
				System.out.println(" data error occured" + "status ="
						+ this.getstatus());
				threads[i].join();

			}
			System.out.println("opening file");

			// open file and seek to the end of it
			if (this.status == Status.Downloading) {
				file = new RandomAccessFile(
						FileUtility.getFileName(connectionhttp), "rw");
				file.seek(0);
				for (int i = 0; i < numthreads; i++) {
					thread_file[i].seek(0);
					long threadsize = thread_file[i].length();
					long temp = 0l;
					System.out.println("writing for" + "" + i + "  "
							+ threadsize);
					while (status == Status.Downloading) {
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
					new File(FileUtility.getFileName(connectionhttp) + "_" + i)
							.delete();
				}
			}
			/*
			 * change status to finished if this point was reached because
			 * downloading was finished
			 */
			if (status == Status.Downloading) {
				status = Status.Completed;
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
		this.observer.updateModel();
	}

}
