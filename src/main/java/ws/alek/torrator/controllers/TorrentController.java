package ws.alek.torrator.controllers;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import ws.alek.torrator.services.TorrentService;
import ws.alek.torrator.torrent.TorrentExistsException;

@Controller
public class TorrentController {
	private TorrentService torrentService;
	private static final Logger LOG = LoggerFactory
			.getLogger(TorrentController.class);

	public void setTorrentService(TorrentService torrentService) {
		if (torrentService == null) {
			throw new NullPointerException("TorrentService should not be null.");
		}
		this.torrentService = torrentService;
	}

	private TorrentService getTorrentService() {
		if (torrentService == null) {
			throw new IllegalStateException("TorrentService is not set.");
		}
		return torrentService;
	}

	@RequestMapping("/")
	public String list(Model model) {
		return "TorrentList";
	}

	/**
	 * Request for form to submit new torrent file
	 * 
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String addGet() {
		return "NewTorrent";
	}

	/**
	 * Submit form with new torrent file
	 * 
	 * @param torrentFile
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String addPost(
			@RequestParam("torrentFile") MultipartFile torrentFile, Model model) {
		if (torrentFile.getSize() <= 0) {
			model.addAttribute("error", "No torrent file.");
			return "NewTorrent";
		}
		InputStream in = null;
		try {
			in = torrentFile.getInputStream();
			getTorrentService().add(in);
		} catch (IOException e) {
			LOG.error("Can't read from torrent file: " + e.getMessage());
			model.addAttribute("error", "Can't read from torrent file.");
			return "NewTorrent";
		} catch (TorrentExistsException e) {
			model.addAttribute("error", "We already have this torrent.");
			return "redirect:/add/failed";
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					LOG.warn("Can't close torrent input stream: "
							+ e.getMessage());
				}
			}
		}
		return "redirect:/add/success";
	}

	@RequestMapping("/add/success")
	public String addSuccess() {
		return "newTorrentSuccess";
	}

	@RequestMapping("/add/failed")
	public String addFailed() {
		return "newTorrentFailed";
	}
}
