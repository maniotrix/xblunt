package maniotrix.xblunt.transporter.model.download;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Paths;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import maniotrix.xblunt.transporter.MainApp;
import maniotrix.xblunt.transporter.util.UrlUtility;
import maniotrix.xblunt.transporter.view.DownloadOverviewController;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a download.
 *
 * @author maniotrix
 */
@XmlRootElement()
@XmlAccessorType(XmlAccessType.PROPERTY)
@Entity
public class DownloadModel implements Serializable {

	/**
	 * 
	 */
	@Id
	private static final long serialVersionUID = 1L;
	@OneToOne(mappedBy = "observer", cascade = { CascadeType.ALL })
	public Download download;
	public StringProperty filename;
	public StringProperty size;
	public StringProperty progress;
	public StringProperty speed;
	public StringProperty status;
	public DownloadOverviewController overviewcontroller;

	/**
	 * Constructor with some initial data.
	 * 
	 * 
	 * @param url
	 */

	public DownloadModel() {
		this.filename = new SimpleStringProperty();
		this.size = new SimpleStringProperty();
		this.progress = new SimpleStringProperty();
		this.speed = new SimpleStringProperty();
		this.status = new SimpleStringProperty();
		/*
		 * status.addListener(observable -> { MainApp.savedownloadDataToFile();
		 * });
		 */
	}

	/**
	 * 
	 * @param url
	 * @param threads
	 */
	public DownloadModel(String url, int threads, String path) {
		this.download = new Download(threads, UrlUtility.verifyUrl(url), this,
				path);
			this.filename = new SimpleStringProperty(path);
		this.size = new SimpleStringProperty(download.getsize().toString());
		this.progress = new SimpleStringProperty(download.getprogress()
				.toString());
		this.speed = new SimpleStringProperty(UrlUtility.humanReadableByteCount(download.getspeed(), true)+"/s");
		this.status = new SimpleStringProperty(download.getstatus().toString());
		status.addListener(observable -> {
			MainApp.savedownloadDataToFile();
		});

	}

	public void updateModel() {
		//setSize(download.getsize().toString());
		setProgress(download.getprogress().toString());
		setSpeed(UrlUtility.humanReadableByteCount(download.getspeed(), true)+"/s");
		setStatus(download.getstatus().toString());
	}
	
	public void cancelDownload(){
		this.download=null;
	}
	
	public void resetDownload() {
		URL url = this.download.getUrl();
		this.download.cancel();
		this.download = null;
		this.download = new Download(8, url, this, getFilename());
	}

	public String getFilename() {
		return filename.get();
	}

	public void setFilename(String urlname) {
		this.filename.set(urlname);
	}

	public String getSize() {
		return size.get();
	}

	public void setSize(String size) {
		this.size.set(size);
	}

	public String getProgress() {
		return progress.get();
	}

	public void setProgress(String progress) {
		this.progress.set(progress);
	}

	public String getSpeed() {
		return speed.get();
	}

	public void setSpeed(String speed) {
		this.speed.set(speed);
	}

	public String getStatus() {
		return status.get();
	}

	public void setStatus(String status) {
		this.status.set(status);
	}

	public StringProperty FilenameProperty() {
		return filename;
	}

	public StringProperty SizeProperty() {
		return size;
	}

	public StringProperty ProgressProperty() {
		return progress;
	}

	public StringProperty SpeedProperty() {
		return speed;
	}

	public StringProperty StatusProperty() {
		return status;
	}

	public Download getDownload() {
		return download;
	}

	public void setDownload(Download download) {
		this.download = download;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
