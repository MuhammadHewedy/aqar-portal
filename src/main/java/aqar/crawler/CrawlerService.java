package aqar.crawler;

import static aqar.util.Util.*;

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
		if (!loadInfo.isLocked()) {
			loadInfo.setLocked(true);

			List<ListenableFuture<Apartment>> apartements = aqarServices.stream().filter(aq -> aq.enabled())
					.flatMap(aq -> getApartements(aq)).collect(Collectors.toList());
			loadInfo.setTotalCount(apartements.size());

			apartements.forEach(f -> f.addCallback(t -> {
				if (t == null) {
					loadInfo.incrementFail();
				} else {
					if (apartmentRepo.exists(QApartment.apartment.adNumber.eq(t.getAdNumber()))) {
						log.info("ad {} already exists", t.getAdNumber());
					} else {
						apartmentRepo.save(t);
					}
					loadInfo.incrementSucc();
				}
			}, e -> {
				log.error(e.getMessage(), e);
				loadInfo.incrementFail();
			}));

			log.info("start method returned successfully.");
		} else {
			log.info("start method still running...., status object is {} ", loadInfo);
		}
	}

	private Stream<ListenableFuture<Apartment>> getApartements(AqarService aqarService) {
		return IntStream.rangeClosed(1, aqarService.getPagesNumber()).parallel()
				.mapToObj(page -> aqarService.getSearchUrl(page)).flatMap(sUrl -> aqarService.getDetailsUrls(sUrl))
				.map(dUrl -> aqarService.buildApartement(dUrl));
	}
}
