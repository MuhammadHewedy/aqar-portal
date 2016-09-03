package aqar.services.crawler;

import static aqar.util.Util.*;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
			loadInfo.setStartMillis(System.currentTimeMillis());
			aqarServices.stream().filter(aq -> aq.enabled()).forEach(aq -> getApartements(aq));
		}
	}

	private void getApartements(AqarService aqarService) {
		IntStream.rangeClosed(1, aqarService.getPagesNumber())
				.parallel()
				.mapToObj(page -> aqarService.getSearchUrl(page))
				.map(sUrl -> aqarService.getDetailsUrls(sUrl))
				.forEach(dUrlsFuture -> dUrlsFuture.whenComplete((dUrls, e) -> {
					if (e == null) {
						dUrls.forEach(dUrl -> {
							aqarService.buildApartement(dUrl).whenComplete((apart, ex) -> {
								loadInfo.calcTimeInMills();
								if (ex == null) {
									saveAparetmentToDb(apart);
								} else {
									log.error(ex.getMessage());
									loadInfo.incrementNullObj();
								}
							});
						});
					} else {
						loadInfo.incrementFail();
						log.error(e.getMessage());
					}
				}));
	}

	private void saveAparetmentToDb(Apartment apart) {
		if (apartmentRepo.exists(QApartment.apartment.adNumber.eq(apart.getAdNumber()))) {
			loadInfo.incrementDup();
		} else {
			apartmentRepo.save(apart);
			loadInfo.incrementSucc();
		}
	}
}
