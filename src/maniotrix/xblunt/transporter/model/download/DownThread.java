package maniotrix.xblunt.transporter.model.download;

import java.io.*;
import java.net.*;

import maniotrix.xblunt.transporter.util.FileUtility;

public class DownThread extends Thread {
	private Download mainthread;
	private long downloaded, start, size;
	private int threadno;
	public int Numthreads;
	private URL url;
	private long end;
	private RandomAccessFile file;
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
		return downloaded;
	}

	

	@Override
	public void run() {
		InputStream stream = null;
		this.setPriority(Thread.MAX_PRIORITY);

		try {
			// Open connection to URL.
<<<<<<< HEAD:src/maniotrix/xblunt/transporter/model/download/DownThread.java
=======
			if (mainthread.type == Download.https) {
				connectionhttps = (HttpsURLConnection) url.openConnection();
				System.out.println("trying to connect(Thread)" + "" + threadno);
				// Specify what portion of file to download.
				if(end-start <=0)return;
				/*
				 * if (threadno + 1 == Numthreads)
				 * connectionhttps.setRequestProperty("Range", "bytes=" + start
				 * + "-"); else
				 */
				connectionhttps.setRequestProperty("Range", "bytes=" + start
						+ "-" + end);
				// Connect to server.
				try {
					connectionhttps.connect();
				} catch (Exception e) {
					System.out.println("connection error");
				}
				if (mainthread.ifresumed == false) {
					System.out.println(" thread " + threadno + " died");
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
				int contentlength = connectionhttps.getContentLength();
				System.out
						.println(contentlength + "of thread" + " " + threadno);
				if (contentlength < 1)
					mainthread.error();
				System.out.println("start= " + start + " end= " + end
						+ " end-start= " + (end - start) + " contenlength= "
						+ contentlength);
				/*
				 * set the length of download if it is not already set
				 */
				if (size == -1)
					size = contentlength;

				if (mainthread.ifactive && mainthread.thread_file[threadno] != null) {
					file = mainthread.thread_file[threadno];
					file.seek(file.getFilePointer());
					System.out.println("file seeked at filepointer");
				} else {
					file = new RandomAccessFile(
							Download.getFileName(connectionhttps) + "_"
									+ threadno, "rw");
					mainthread.thread_file[threadno] = file;
					file.seek(downloaded);
					System.out.println("file seeked at 0");
				}
				stream = connectionhttps.getInputStream();
			}
			if (mainthread.type == Download.http) {
>>>>>>> d6dc45e1dd45f200954d757782107ab7092e49d4:src/start/DownThread.java
				connectionhttp = (HttpURLConnection) url.openConnection();
				System.out.println("trying to connect(Thread)" + "" + threadno);
				if(end-start <=0)return;
				// Specify what portion of file to download.
				connectionhttp.setRequestProperty("Range", "bytes=" + start
						+ "-" + end);
				// Connect to server.
				try {
					connectionhttp.connect();
				} catch (IOException e) {
					System.out.println("connection error");
				}
				if (mainthread.ifresumed == false) {
					System.out.println(" thread " + threadno + " died");
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
				int contentlength = connectionhttp.getContentLength();
				System.out
						.println(contentlength + "of thread" + " " + threadno);
				if (contentlength < 1)
					mainthread.error();
				System.out.println("end-start= " + (end - start)
						+ " contenlength= " + contentlength);
				/*
				 * set the length of download if it is not already set
				 */
				if (size == -1)
					size = contentlength;

				if (mainthread.ifactive && mainthread.thread_file[threadno] != null) {
					file = mainthread.thread_file[threadno];
					file.seek(file.getFilePointer());
					System.out.println("file seeked at filepointer");
				} else {
					file = new RandomAccessFile(
							FileUtility.getFileName(connectionhttp) + "_"
									+ threadno, "rw");
					mainthread.thread_file[threadno] = file;
					file.seek(downloaded);
					System.out.println("file seeked at 0");
				}
				stream = connectionhttp.getInputStream();
			

			System.out.println("downloading for" + "" + threadno);
			byte buffer[] = new byte[4096];
			while (mainthread.getstatus() == Status.Downloading && downloaded < size) {

				if (downloaded == size) {

					break;
				}
			    int read = stream.read(buffer, 0, (int) Math.min(buffer.length,size-downloaded));
			    if (read == -1) break;

			    file.write(buffer, 0, read);
				downloaded += read;
				mainthread.thread_data[threadno] = mainthread.thread_temp[threadno]
						+ downloaded;

				dataexch();
			}
			mainthread.thread_start[threadno] = downloaded + start;
			mainthread.thread_temp[threadno] += downloaded;

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