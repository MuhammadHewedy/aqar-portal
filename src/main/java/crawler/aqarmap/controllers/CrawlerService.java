package crawler.aqarmap.controllers;

import static crawler.aqarmap.util.XPathUtils.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.xml.xpath.XPathConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import crawler.aqarmap.models.Apartment;
import crawler.aqarmap.models.ApartmentRepo;
import crawler.aqarmap.models.QApartment;
import crawler.aqarmap.util.Util;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrawlerService {

	@Autowired
	private ApartmentBuilder builder;
	@Autowired
	private ApartmentRepo apartmentRepo;

	@Async
	public void start(String city, String url, Integer to) {
		if (!Util.LOAD_INFO.isLocked()) {
			Util.LOAD_INFO.setLocked(true);

			List<ListenableFuture<Apartment>> futures = IntStream.range(1, to + 1).parallel()
					.mapToObj(page -> url + "&" + Util.PAGE_PARAM + "=" + page)
					.flatMap(pUrl -> detailsUrlsFromPageUrl(pUrl))
					.map(dUrl -> builder.apartmentFromDetailsUrl(dUrl, city)).collect(Collectors.toList());

			Util.LOAD_INFO.setTotalCount(futures.size());

			futures.forEach(f -> f.addCallback(t -> {
				if (apartmentRepo.exists(QApartment.apartment.adNumber.eq(t.getAdNumber()))) {
					log.info("ad {} already exists", t.getAdNumber());
				} else {
					apartmentRepo.save(t);
				}
				Util.LOAD_INFO.incrementSucc();
			}, e -> {
				e.printStackTrace();
				Util.LOAD_INFO.incrementFail();
			}));

			log.info("start method returned successfully.");
		} else {
			log.info("start method still running...., status object is {} ", Util.LOAD_INFO);
		}
	}

	private Stream<String> detailsUrlsFromPageUrl(String pageUrl) {
		try {
			Document doc = Util.fromUrl(pageUrl);

			NodeList list;
			try {
				list = (NodeList) DETAILS_URLS.evaluate(doc, XPathConstants.NODESET);
				log.info("found {} details urls, in page URL: {}", list.getLength(), pageUrl);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Builder<String> builder = Stream.builder();
			for (int i = 0; i < list.getLength(); i++) {
				Node node = list.item(i);
				builder.add(node.getAttributes().getNamedItem("href").getNodeValue());
			}
			return builder.build();
		} catch (Exception ex) {
			log.error("error during get details page for: " + pageUrl, ex);
		}
		return Stream.empty();
	}

}
