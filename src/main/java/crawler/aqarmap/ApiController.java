package crawler.aqarmap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysema.query.types.Predicate;

@RestController
public class ApiController {

	@Autowired
	private CrawlerService crawlerService;
	@Autowired
	private ApartmentRepo apartmentRepo;

	@RequestMapping("/api/load")
	public void load() {
		crawlerService.start("Riyadh", Util.BASE_URL + Util.SEARCH_URL, 3);
	}

	@RequestMapping("/api")
	public ResponseEntity<Page<Apartment>> get(Predicate predicate, Pageable pageable) {
		return ResponseEntity.ok(apartmentRepo.findAll(predicate, pageable));
	}
}
