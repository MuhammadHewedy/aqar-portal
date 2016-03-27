package aqar.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysema.query.types.Predicate;

import aqar.models.Apartment;
import aqar.models.ApartmentRepo;
import aqar.services.crawler.CrawlerService;
import aqar.util.Util;

@RestController
@RequestMapping("/api")
public class ApiController {

	@Autowired
	private CrawlerService crawlerService;
	@Autowired
	private ApartmentRepo apartmentRepo;

	@RequestMapping("load")
	public void load() {
		crawlerService.start();
	}

	@RequestMapping("load/status")
	public ResponseEntity<?> loadStatus() {
		return ResponseEntity.ok(Util.loadInfo);
	}

	@RequestMapping("load/reset")
	public void loadReset() {
		Util.loadInfo.reset();
	}

	@RequestMapping
	public ResponseEntity<Page<Apartment>> get(Predicate predicate, Pageable pageable) {

		Page<Apartment> findAll = apartmentRepo.findAll(predicate, pageable);
		findAll.forEach(o -> o.setImageUrls(null));
		return ResponseEntity.ok(findAll);
	}
}
