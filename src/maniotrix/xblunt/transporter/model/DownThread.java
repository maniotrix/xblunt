package maniotrix.xblunt.transporter.model;

import java.io.*;
import java.net.*;
import java.util.Observer;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.*;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import maniotrix.xblunt.transporter.MainApp;
import maniotrix.xblunt.transporter.util.FileUtility;
//@XmlRootElement()
//@XmlAccessorType(XmlAccessType.FIELD)
//@Entity
public class DownThread extends Thread{
	/**
	 * 
	 */
	//@Id private static final long serialVersionUID = 3419935766823909577L;
	/*@OneToOne
    @JoinColumn(name="ID")
    @MapsId
	@XmlInverseReference(mappedBy = "threads")*/
	private Download mainthread;
	private long downloaded, start, size;
	private int threadno;
	public int Numthreads;
	private URL url;
	private long end;
	private RandomAccessFile file;
	HttpURLConnection connectionhttp;

	public DownThread(URL url, int numthreads, int threadNo, long l, long m,
			Download MainThread,RandomAccessFile file) {
		this.url = url;
		this.start = l;
		this.end = m;
		this.threadno = threadNo;
		this.Numthreads = numthreads;
		this.size = -1;
		this.mainthread = MainThread;
		this.file=file;
		// file = mainthread.thread_file[threadno];
		// System.out.println(size);

	}
	public DownThread(){
		
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
				connectionhttp = (HttpURLConnection) url.openConnection();
				System.out.println("trying to connect(Thread)" + "" + threadno);
				if(end-start <=0)return;
				System.out.println("requesting bytes");
				// Specify what portion of file to download.
				connectionhttp.setRequestProperty("Range", "bytes=" + start
						+ "-" + end);
				// Connect to server.
				try {
					connectionhttp.connect();
				} catch (IOException e) {
					System.out.println("connection error");
					mainthread.error();
					return;
					
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
				if(contentlength > (end-start+1) &&  threadno>0 ){
					return;
				}*/
				/*
				 * set the length of download if it is not already set
				 */
				if (size == -1){
					size = contentlength;
				}
				
				
				
				
			/*	if (mainthread.ifactive && mainthread.thread_file[threadno] != null) {
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
				}*/
				
				//file=mainthread.thread_file[threadno];
				if(file==null){
					System.out.println("File is null");
				}
				file.seek(file.getFilePointer());
				System.out.println("Thread file seeked at"+file.getFilePointer());
				
				
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
		}
		// close connection to server
		if (stream != null) {
			try {
				stream.close();
				connectionhttp.disconnect();
				System.out.println("stream closed and status="
						+ mainthread.getstatus());

			} catch (Exception e) {
				System.out.println("stream closed with error");
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
	public Download getMainthread() {
		return mainthread;
	}
	public void setMainthread(Download mainthread) {
		this.mainthread = mainthread;
	}
	public long getDownloaded() {
		return downloaded;
	}
	public void setDownloaded(long downloaded) {
		this.downloaded = downloaded;
	}
	public long getStart() {
		return start;
	}
	public void setStart(long start) {
		this.start = start;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public int getThreadno() {
		return threadno;
	}
	public void setThreadno(int threadno) {
		this.threadno = threadno;
	}
	public int getNumthreads() {
		return Numthreads;
	}
	public void setNumthreads(int numthreads) {
		Numthreads = numthreads;
	}
	public URL getUrl() {
		return url;
	}
	public void setUrl(URL url) {
		this.url = url;
	}
	public long getEnd() {
		return end;
	}
	public void setEnd(long end) {
		this.end = end;
	}
	public RandomAccessFile getFile() {
		return file;
	}
	public void setFile(RandomAccessFile file) {
		this.file = file;
	}
	public HttpURLConnection getConnectionhttp() {
		return connectionhttp;
	}
	public void setConnectionhttp(HttpURLConnection connectionhttp) {
		this.connectionhttp = connectionhttp;
	}
}