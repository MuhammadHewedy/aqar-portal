package aqar.crawler;

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
			aqarServices.stream().filter(aq -> aq.enabled()).forEach(aq -> getApartements(aq));
		}
	}

	private void getApartements(AqarService aqarService) {

		IntStream.rangeClosed(1, aqarService.getPagesNumber()).parallel()
				.mapToObj(page -> aqarService.getSearchUrl(page)).map(sUrl -> aqarService.getDetailsUrls(sUrl))
				.forEach(dUrlsFuture -> dUrlsFuture.addCallback(dUrls -> {
					dUrls.forEach(dUrl -> {
						Apartment apartement = aqarService.buildApartement(dUrl);
						if (apartement == null) {
							loadInfo.incrementNullObj();
						} else {
							if (apartmentRepo.exists(QApartment.apartment.adNumber.eq(apartement.getAdNumber()))) {
								loadInfo.incrementDup();
							} else {
								apartmentRepo.save(apartement);
								loadInfo.incrementSucc();
							}
						}
					});
				}, e -> {
					loadInfo.incrementFail();
					log.error(e.getMessage(), e);
				}));
	}
}
