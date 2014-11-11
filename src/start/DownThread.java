package start;

import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

import javax.net.ssl.HttpsURLConnection;

public class DownThread extends Thread {
	private Download mainthread;
	private long downloaded, start, size;
	private int threadno;
	public int Numthreads;
	private URL url;
	private byte[] buffer;
	private long end;
	private RandomAccessFile file;
	HttpsURLConnection connectionhttps ;
	HttpURLConnection connectionhttp;

	public DownThread(URL url, int numthreads, int threadNo, long l, long m,
			Download MainThread) {
		this.url = url;
		this.start = l;
		this.end = m;
		this.threadno = threadNo;
		this.Numthreads = numthreads;
		this.size = -1;
		this.mainthread = MainThread;
		// file = mainthread.thread_file[threadno];
		// System.out.println(size);

	}

	private void dataexch() {
		mainthread.dataexch();

	}

	// return thread progress
	public long getprogress() {
		return  downloaded;
	}

	// Get file name portion of URL.
	private String getFileName(URL url) {
		String fileName = url.getFile();
		return (fileName.substring(fileName.lastIndexOf('/') + 1) + threadno);
	}

	@Override
	public void run() {
		InputStream stream = null;
		this.setPriority(Thread.MAX_PRIORITY);

		try {
			// Open connection to URL.
			if (mainthread.type == 1) {
				 connectionhttps = (HttpsURLConnection) url
						.openConnection();
				System.out.println("trying to connect(Thread)" + "" + threadno);
				// Specify what portion of file to download.
				if (threadno + 1 == Numthreads)
					connectionhttps.setRequestProperty("Range", "bytes=" + start
							+ "-");
				else
					connectionhttps.setRequestProperty("Range", "bytes=" + start
							+ "-" + end);
				// Connect to server.
				connectionhttps.connect();
				if(mainthread.ifresumed==false)
					{
					System.out.println(" thread "+threadno+" died");
					return;
					}

				// make sure reponse code is in the 200 range
				if (connectionhttps.getResponseCode() / 100 != 2) {
					// error();
				}
				// check for valid content length
				int contentlength = connectionhttps.getContentLength();
				System.out
						.println(contentlength + "of thread" + " " + threadno);
				if (contentlength < 1)
					mainthread.error();

				/*
				 * set the length of download if it is not already set
				 */
				if (size == -1)
					size = contentlength;


				if (mainthread.ifactive) {
					file = mainthread.thread_file[threadno];
					file.seek(file.getFilePointer());
					System.out.println("file seeked at filepointer");
				} else {
					file = new RandomAccessFile(getFileName(url), "rw");
					mainthread.thread_file[threadno] = file;
					file.seek(downloaded);
					System.out.println("file seeked at 0");
				}
				stream = connectionhttps.getInputStream();
			}
			if (mainthread.type == 0) {
				 connectionhttp = (HttpURLConnection) url
						.openConnection();
				System.out.println("trying to connect(Thread)" + "" + threadno);
				// Specify what portion of file to download.
				if (threadno + 1 == Numthreads)
					connectionhttp.setRequestProperty("Range", "bytes=" + start
							+ "-");
				else
					connectionhttp.setRequestProperty("Range", "bytes=" + start
							+ "-" + end);
				// Connect to server.
				connectionhttp.connect();
				if(mainthread.ifresumed==false)
					{
					System.out.println(" thread "+threadno+" died");
					return;
					}

				// make sure reponse code is in the 200 range
				if (connectionhttp.getResponseCode() / 100 != 2) {
					// error();
				}
				// check for valid content length
				int contentlength = connectionhttp.getContentLength();
				System.out
						.println(contentlength + "of thread" + " " + threadno);
				if (contentlength < 1)
					mainthread.error();

				/*
				 * set the length of download if it is not already set
				 */
				if (size == -1)
					size = contentlength;

				if (mainthread.ifactive) {
					file = mainthread.thread_file[threadno];
					file.seek(file.getFilePointer());
					System.out.println("file seeked at filepointer");
				} else {
					file = new RandomAccessFile(getFileName(url), "rw");
					mainthread.thread_file[threadno] = file;
					file.seek(downloaded);
					System.out.println("file seeked at 0");
				}
				stream = connectionhttp.getInputStream();
			}

			System.out.println("downloading for" + "" + threadno);
			while (mainthread.getstatus() == Download.Downloading) {

				if (downloaded == size) {

					break;
				}
				/*
				 * set buffer as size of file to be downloaded
				 */
				byte buffer[];
				if (size - downloaded > Download.MAX_BUFFER_SIZE) {
					buffer = new byte[Download.MAX_BUFFER_SIZE];

				} else {
					buffer = new byte[(int) (size - downloaded)];

				}

				// read from server into buffer
				int read = stream.read(buffer);
				if (read == -1) {

					break;

				}

				file.write(buffer, 0, read);
				downloaded += read;
				mainthread.thread_data[threadno] = mainthread.thread_temp[threadno]+
						downloaded;

				dataexch();

			}
			mainthread.thread_start[threadno] = downloaded + start;
			mainthread.thread_temp[threadno]+=downloaded;
			

		} catch (Exception e) {
			System.out
					.println("Unhandled exception encountered during threaded HTTP exchange.");
			e.printStackTrace();
			//mainthread.error();

		}
		// close connection to server
		if (stream != null) {
			try {
				stream.close();
				System.out.println("stream closed and status="
						+ mainthread.getstatus());

			} catch (Exception e) {
				System.out.println("stream closed");
			}
		}
	}

	public RandomAccessFile ReturnBuffer() throws IOException {
		file.seek(0);
		if (file.length() == size)
			return file;
		else
			return null;
	}
}

