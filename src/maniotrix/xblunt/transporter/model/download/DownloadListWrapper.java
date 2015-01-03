package maniotrix.xblunt.transporter.model.download;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Helper class to wrap a list of downloads. This is used for saving the
 * list of downloads to XML.
 * 
 * @author maniotrix
 */
@XmlRootElement(name = "downloads")
public class DownloadListWrapper {

    private List<Download> downloads;

    @XmlElement(name = "Download")
    public List<Download> getPersons() {
        return downloads;
    }

    public void setPersons(List<Download> downloads) {
        this.downloads = downloads;
    }
}
