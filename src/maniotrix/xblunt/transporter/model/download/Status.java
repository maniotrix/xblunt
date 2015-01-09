package maniotrix.xblunt.transporter.model.download;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Enumeration Type for tracking status of each controller instance.
 * 
 * @author maniotrix
 *
 */
@XmlEnum
public enum Status {
	Downloading(0), Completed(1), Paused(2), Cancelled(4), Error(5);
	private int status;

	Status(int s) {
		status = s;
	}

	/***
	 * get value of each status.
	 * <p>
	 * To get the value of specific Status Enumeration use method as
	 * illustrated: <blockquote>
	 * 
	 * <pre>
	 * {@code
	 * myStatus = Status.Paused.getstatus();
	 * }
	 * </pre>
	 * 
	 * </blockquote>
	 * 
	 * @return int
	 */
	int getstatus() {
		return status;
	}

}
