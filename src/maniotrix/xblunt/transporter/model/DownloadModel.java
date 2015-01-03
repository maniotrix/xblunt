package maniotrix.xblunt.transporter.model;

import maniotrix.xblunt.transporter.model.download.Download;
import maniotrix.xblunt.transporter.util.UrlUtility;
import maniotrix.xblunt.transporter.view.DownloadOverviewController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Model class for a download.
 *
 * @author maniotrix
 */
public class DownloadModel {

	private StringProperty urlname;
	private StringProperty size;
	private StringProperty progress;
	private StringProperty speed;
	private StringProperty status;
	private Download download;
	private DownloadOverviewController overviewcontroller;

	/**
	 * Constructor with some initial data.
	 * 
	 * 
	 * @param url
	 */
	public DownloadModel(DownloadOverviewController controller, String url,
			int threads) {
		this.overviewcontroller = controller;
		this.download = new Download(threads, UrlUtility.verifyUrl(url), this);
		this.urlname = new SimpleStringProperty(url);
		this.size = new SimpleStringProperty(download.getsize().toString());
		this.progress = new SimpleStringProperty(download.getprogress()
				.toString());
		this.speed = new SimpleStringProperty(download.getspeed().toString());
		this.status = new SimpleStringProperty(download.getstatus().toString());
	}

	public StringProperty getUrlName() {
		return urlname;
	}

	public StringProperty getStatus() {
		return status;
	}

	public StringProperty getSpeed() {
		return speed;
	}

	public StringProperty getprogress() {
		return progress;
	}

	public StringProperty getsize() {
		return size;
	}

	public Download getdownloadInstance() {
		return this.download;
	}

	public void updateModel() {
		this.size.set(download.getsize().toString());
		this.progress.set(download.getprogress().toString());
		this.speed.set(download.getspeed().toString());
		this.status.set(download.getstatus().toString());
		this.overviewcontroller.update(this);
	}

}
