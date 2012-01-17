package ws.alek.torrator.torrent;

import java.net.URL;
import java.util.Date;

import ws.alek.torrator.torrent.bencoded.BinaryString;

public class Tracker {
	private URL url;
	private int interval = 60 * 60;
	private int minInteval = 30 * 60;
	private BinaryString trackerId = null;
	private Date lastConnect;

	public Tracker() {
	}
	
	public Tracker(URL url) {
		this.url = url;
	}
	
	public void setUrl(URL url) {
		this.url = url;
	}

	public URL getUrl() {
		return url;
	}

	public int getInterval() {
		return interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
	}

	public int getMinInteval() {
		return minInteval;
	}

	public void setMinInteval(int minInteval) {
		this.minInteval = minInteval;
	}

	public BinaryString getTrackerId() {
		return trackerId;
	}

	public void setTrackerId(BinaryString trackerId) {
		this.trackerId = trackerId;
	}

	public Date getLastConnect() {
		return lastConnect;
	}

	public void setLastConnect(Date lastConnect) {
		this.lastConnect = lastConnect;
	}

}
