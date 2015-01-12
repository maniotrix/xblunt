package maniotrix.xblunt.transporter.model;

import java.io.*;
import java.net.*;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.oxm.annotations.XmlInverseReference;

import maniotrix.xblunt.transporter.util.FileUtility;
import maniotrix.xblunt.transporter.util.UrlUtility;

//this class downloads a file from a url
@XmlRootElement()
// @XmlAccessorType(XmlAccessType.FIELD)
@Entity
public class Download implements java.lang.Runnable, Serializable {

	public String getFilepath() {
		return filepath;
	}

	public void setFilepath(String filepath) {
		this.filepath = filepath;
	}

	/**
	 * 
	 */
	@Id
	private static final long serialVersionUID = -8692158647815575107L;
	/**
	 * 
	 */

	// max size of download buffer
	public static final int MAX_BUFFER_SIZE = 1024 * 8;
	@OneToOne
	@JoinColumn(name = "ID")
	@MapsId
	@XmlInverseReference(mappedBy = "download")
	DownloadModel observer;
	boolean ifresumed = true;
	boolean ifactive = false;
	private int numthreads, contentlength;
	@XmlTransient
	public DownThread[] threads;
	long[] thread_start;
	long[] thread_data;
	long[] thread_temp;
	// @XmlTransient
	// RandomAccessFile[] thread_file;

	private URL url;// url of file
	private long filesize, downloadstartTime;// size of file
	@XmlElement(required = true)
	private Status status;// status of download
	private long progress, downprogress, timeprogress;// no of bytes
														// downloaded
	private HttpURLConnection connectionhttp;
	Thread thread;

	String filepath;

	// constructor for download
	public Download(int numthread, URL url, DownloadModel model, String filepath) {
		this.observer = model;
		this.url = url;
		filesize = -1;
		status = Status.Downloading;
		this.filepath = filepath;
		thread_start = new long[numthread];
		thread_data = new long[numthread];
		thread_temp = new long[numthread];
		// thread_file = new RandomAccessFile[numthread];
		// start download
		download(numthread);

	}

	public Download() {
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

	public long getspeed() {
		if (status == Status.Downloading)
			return downprogress;
		else
			return  0l;
	}

	public String getprogress() {
		Float f=((float) this.progress / filesize) * 100;
		return f.toString();
	}

	public Status getstatus() {
		return status;
	}

	// pause this download
	public void pause() {
		if (status == Status.Paused|| status==Status.Cancelled ||status==Status.Completed)
			return;
		ifactive = false;
		ifresumed = false;
		status = Status.Paused;
		this.downprogress = 0l;
		// dataexch();
		statechanged();
		
		try {
			Thread.sleep(2000);
			dataexch();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// interrupting threads if alive
		if (thread != null && thread.isAlive()) {
			connectionhttp.disconnect();
			System.out.println("disconnected threads ");
		}
		try {
			for (int i = 0; i < numthreads; i++) {
				while (threads[i] != null && threads[i].isAlive()) {
					//threads[i].connectionhttp.disconnect();

				}
			}
		} catch (Exception e) {
		}
		dataexch();

	}

	// resume this download
	public void resume() {
		if (status == Status.Downloading || status == Status.Cancelled
				|| status == Status.Completed)
			return;
		status = Status.Downloading;
		ifactive = true;
		ifresumed = true;
		download(numthreads);

		statechanged();
	}

	// cancel download
	public void cancel() {
		if(status==Status.Cancelled || status==Status.Completed)
			return;
		status = Status.Cancelled;
		statechanged();
		//Thread.sleep(2000);
		dataexch();
		// interrupting threads if alive
	new Runnable() {
			
			@Override
			public void run() {
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
				
				try {
					for(int i=0;i<numthreads;i++){
						new File(filepath + "_" + i).delete();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		};
		System.out.println("download cancelled");

	}

	public long get_thread_data(int i) {
		System.out.println("creating data for thread " + i);
		if (ifactive == true && thread_start[i] > 0)
			return thread_start[i];

		return ((filesize * i) / numthreads);
	}

	// mark download an error
	public void error() {
		/*if (status != Status.Paused) {
			status = Status.Error;
		} else {*/
			status = Status.Error;
		//}
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
		// this.downprogress=0;
		// organiseThreadFiles();

	}

	/*
	 * public void organiseThreadFiles(RandomAccessFile[] thread_file) { String
	 * mypath=filepath; for (int i = 0; i < numthreads; i++) { try {
	 * if(mypath!=null ){ //mypath= mypath + "_" + i;
	 * //this.thread_file[i]=null; thread_file[i] = new RandomAccessFile(mypath+
	 * "_" + i, "rw"); thread_file[i].seek(thread_file[i].length());
	 * System.out.println("File Pointer seeked" +
	 * thread_file[i].getFilePointer());} } catch (Exception e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * } }
	 */

	// @SuppressWarnings("null")
	@Override
	public void run() {
		RandomAccessFile file = null;
		RandomAccessFile[] thread_file = new RandomAccessFile[numthreads];
		/*
		 * for(int i=0;i<numthreads;i++){ thread_file[i]=null; }
		 */
		InputStream stream = null;
		statechanged();
		this.downprogress = 0l;
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
				observer.setStatus(e.getMessage());
				error();
			}
			if (ifresumed == false) {
				System.out.println(" mainthread died");
				return;
			}
			// make sure reponse code is in the 200 range
			int ResponseCode = connectionhttp.getResponseCode();
			if (ResponseCode / 100 != 2) {

				System.out.println("unwanted response code= " + ResponseCode);
				error();
				observer.setStatus("Http Response Error");
				return;
			}
			// check for valid content length
			this.contentlength = connectionhttp.getContentLength();
			System.out.println(contentlength);
			if (contentlength < 1) {
				error();
				observer.setStatus("File is less than 1 byte");
				return;
			}

			/*
			 * set the length of download if it is not already set
			 */
			if (filesize == -1 || filesize != contentlength) {
				filesize = contentlength;
				// statechanged();
				observer.setSize(UrlUtility.humanReadableByteCount(filesize, true));
				System.out.println("connected" + filesize + "status= "
						+ this.getstatus());
			}
			/*connectionhttp.disconnect();
			connectionhttp.setRequestProperty("Range", "bytes=" + 0
					+ "-" + filesize/8);
			connectionhttp.connect();
			contentlength= connectionhttp.getContentLength();
			if(contentlength>filesize/8){
				observer.setStatus("Server discarding multiple connection");
				return;
			}*/
			
			System.out.println("connected" + " and filesize= " + filesize);
			System.out.println(filepath);

			if (filepath.toLowerCase().startsWith("http")) {
				System.out.println("filepath same as url");
				filepath = FileUtility.getFileName(connectionhttp);
				observer.setFilename(filepath);
			}
			try {

				// organiseThreadFiles(thread_file);
				for (int i = 0; i < numthreads; i++) {
					try {
						if (filepath != null) {
							// mypath= mypath + "_" + i;
							// this.thread_file[i]=null;
							thread_file[i] = new RandomAccessFile(filepath
									+ "_" + i, "rw");
							thread_file[i].seek(thread_file[i].length());
							System.out.println("File Pointer seeked"
									+ thread_file[i].getFilePointer());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			} catch (Exception e) {
				observer.setStatus("file not found");
				return;
			}

			// initialize DownloadThread and byte arrays
			this.threads = new DownThread[numthreads];

			// instantiate and start() threads
			for (int i = 0; i < numthreads; i++) {

				threads[i] = new DownThread(url, numthreads, i,
						get_thread_data(i),
						(((filesize * (i + 1)) / numthreads) - 1), this,
						thread_file[i]);
				threads[i].start();
			}
			System.out.println("Downloading...status= " + this.getstatus());
			// wait for threads to complete
			for (int i = 0; i < numthreads; i++) {
				threads[i].join();

			}
			System.out.println("opening file "+status.toString());

			// open file and seek to the end of it
			if (this.status == Status.Downloading) {
				new File(filepath).delete();
				file = new RandomAccessFile(filepath, "rw");
				file.seek(0);
				dataexch();
				observer.setStatus("Download Comlpete Merging Files");
				for (int i = 0; i < numthreads; i++) {
					thread_file[i].seek(0);
					long threadsize = thread_file[i].length();
					long temp = 0l;
					System.out.println("writing for" + filepath  + i + "  "
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
						//statechanged();
					}
					System.out.println("bytes wriiten "
							+ thread_file[i].length() + " "
							+ file.getFilePointer());
					thread_file[i].close();
					new File(filepath + "_" + i).delete();
				}
			}
			/*
			 * change status to finished if this point was reached because
			 * downloading was finished
			 */
			if (status == Status.Downloading && file.length() == filesize) {
				status = Status.Completed;
				System.out.println("error occured" + "status ="
						+ this.getstatus());
				statechanged();

			} else {
				for (int i = 0; i < numthreads; i++) {
					thread_file[i].close();
					thread_file[i] = null;
				}
				statechanged();
			}

		} catch (Exception e) {
			System.out.println("error occured" + e.getMessage());
			statechanged();
			e.printStackTrace();
			//observer.setStatus(e.getMessage());
			error();

		} finally {
			// close file
			if (file != null) {
				try {
					file.close();
					System.out.println("mainthread exited file!=null");
				} catch (Exception e) {
				}

			} else
				// error();
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

	public DownloadModel getObserver() {
		return observer;
	}

	public void setObserver(DownloadModel observer) {
		this.observer = observer;
	}

	public boolean isIfresumed() {
		return ifresumed;
	}

	public void setIfresumed(boolean ifresumed) {
		this.ifresumed = ifresumed;
	}

	public boolean isIfactive() {
		return ifactive;
	}

	public void setIfactive(boolean ifactive) {
		this.ifactive = ifactive;
	}

	public int getNumthreads() {
		return numthreads;
	}

	public void setNumthreads(int numthreads) {
		this.numthreads = numthreads;
	}

	public int getContentlength() {
		return contentlength;
	}

	public void setContentlength(int contentlength) {
		this.contentlength = contentlength;
	}

	@XmlTransient
	public DownThread[] getThreads() {
		return threads;
	}

	public void setThreads(DownThread[] threads) {
		this.threads = threads;
	}

	public long[] getThread_start() {
		return thread_start;
	}

	public void setThread_start(long[] thread_start) {
		this.thread_start = thread_start;
	}

	public long[] getThread_data() {
		return thread_data;
	}

	public void setThread_data(long[] thread_data) {
		this.thread_data = thread_data;
	}

	public long[] getThread_temp() {
		return thread_temp;
	}

	public void setThread_temp(long[] thread_temp) {
		this.thread_temp = thread_temp;
	}

	/*
	 * //@XmlTransient public RandomAccessFile[] getThread_file() { return
	 * thread_file; }
	 * 
	 * public void setThread_file(RandomAccessFile[] thread_file) {
	 * this.thread_file = thread_file; }
	 */
	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		this.url = url;
	}

	public long getFilesize() {
		return filesize;
	}

	public void setFilesize(long filesize) {
		this.filesize = filesize;
	}

	public long getDownloadstartTime() {
		return downloadstartTime;
	}

	public void setDownloadstartTime(long downloadstartTime) {
		this.downloadstartTime = downloadstartTime;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public long getProgress() {
		return progress;
	}

	public void setProgress(long progress) {
		this.progress = progress;
	}

	public long getDownprogress() {
		return downprogress;
	}

	public void setDownprogress(long downprogress) {
		this.downprogress = downprogress;
	}

	public long getTimeprogress() {
		return timeprogress;
	}

	public void setTimeprogress(long timeprogress) {
		this.timeprogress = timeprogress;
	}

	public HttpURLConnection getConnectionhttp() {
		return connectionhttp;
	}

	public void setConnectionhttp(HttpURLConnection connectionhttp) {
		this.connectionhttp = connectionhttp;
	}

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
