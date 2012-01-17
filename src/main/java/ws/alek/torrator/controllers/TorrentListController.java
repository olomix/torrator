package ws.alek.torrator.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TorrentListController {
	@RequestMapping("/")
	public String list(Model model) {
		return "TorrentList";
	}
}
