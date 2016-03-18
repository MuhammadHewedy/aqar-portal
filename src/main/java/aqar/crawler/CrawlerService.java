package aqar.crawler;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import aqar.models.Apartment;
import aqar.models.ApartmentRepo;
import aqar.models.QApartment;
import aqar.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrawlerService {

	@Autowired
	private ApartmentRepo apartmentRepo;
	@Autowired
	private List<AqarService> aqarServices;

	@Async
	public void start() {
		if (!Util.LOAD_INFO.isLocked()) {
			Util.LOAD_INFO.setLocked(true);

			List<ListenableFuture<Apartment>> apartements = aqarServices.stream().flatMap(aq -> getApartements(aq))
					.collect(Collectors.toList());

			Util.LOAD_INFO.setTotalCount(apartements.size());

			apartements.forEach(f -> f.addCallback(t -> {
				if (apartmentRepo.exists(QApartment.apartment.adNumber.eq(t.getAdNumber()))) {
					log.info("ad {} already exists", t.getAdNumber());
				} else {
					apartmentRepo.save(t);
				}
				Util.LOAD_INFO.incrementSucc();
			}, e -> {
				log.error(e.getMessage());
				Util.LOAD_INFO.incrementFail();
			}));

			log.info("start method returned successfully.");
		} else {
			log.info("start method still running...., status object is {} ", Util.LOAD_INFO);
		}
	}

	private Stream<ListenableFuture<Apartment>> getApartements(AqarService aqarService) {
		return IntStream.rangeClosed(1, aqarService.getPagesNumber()).parallel()
				.mapToObj(page -> aqarService.getSearchUrl(page)).flatMap(sUrl -> aqarService.getDetailsUrls(sUrl))
				.map(dUrl -> aqarService.buildApartement(dUrl));
	}
}
