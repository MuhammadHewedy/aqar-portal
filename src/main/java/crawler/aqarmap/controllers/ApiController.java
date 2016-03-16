package crawler.aqarmap.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysema.query.types.Predicate;

import crawler.aqarmap.models.Apartment;
import crawler.aqarmap.models.ApartmentRepo;
import crawler.aqarmap.services.CrawlerService;
import crawler.aqarmap.util.Util;

@RestController
public class ApiController {

	@Autowired
	private CrawlerService crawlerService;
	@Autowired
	private ApartmentRepo apartmentRepo;
	
	@Value("${search.url}")
	private String searchUrl;
	
	@Value("${search.numPages}")
	private Integer searchNumPages;

	@RequestMapping("/api/load")
	public void load() {
		crawlerService.start("Riyadh", Util.BASE_URL + searchUrl, searchNumPages);
	}

	@RequestMapping("/api/load/status")
	public ResponseEntity<?> loadStatus() {
		return ResponseEntity.ok(Util.LOAD_INFO);
	}
	
	@RequestMapping("/api/load/reset")
	public void loadReset() {
		Util.LOAD_INFO.reset();
	}

	@RequestMapping("/api")
	public ResponseEntity<Page<Apartment>> get(Predicate predicate, Pageable pageable) {

		Page<Apartment> findAll = apartmentRepo.findAll(predicate, pageable);
		findAll.forEach(o -> o.setImageUrls(null));
		return ResponseEntity.ok(findAll);
	}
}
