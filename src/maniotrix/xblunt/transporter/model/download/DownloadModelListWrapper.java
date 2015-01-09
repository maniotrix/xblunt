package maniotrix.xblunt.transporter.model.download;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of downloads. This is used for saving the list of
 * downloads to XML.
 * 
 * @author maniotrix
 */
@XmlRootElement(name = "DownloadModelWrapper")
public class DownloadModelListWrapper {

	private List<DownloadModel> downloadModels;

	public List<DownloadModel> getDownloads() {
		return downloadModels;
	}

	public void setDownloads(List<DownloadModel> downloads) {
		this.downloadModels = downloads;
	}

	
}
