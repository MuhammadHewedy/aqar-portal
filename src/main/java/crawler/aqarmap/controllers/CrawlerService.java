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

			List<ListenableFuture<Apartment>> futures = IntStream.range(1, to + 3).parallel()
					.mapToObj(page -> url + "&" + Util.PAGE_PARAM + "=" + page)
					.flatMap(pUrl -> detailsUrlsFromPageUrl(pUrl))
					.map(dUrl -> builder.apartmentFromDetailsUrl(dUrl, city)).collect(Collectors.toList());

			log.info("setting total count: {}", futures.size());
			Util.LOAD_INFO.setTotalCount(futures.size());

			log.info("register callbacks for each future...");
			futures.forEach(f -> f.addCallback(t -> {
				Long count = apartmentRepo.countByAdNumber(t.getAdNumber());
				if (count == 0) {
					apartmentRepo.save(t);
				} else {
					log.info("add {} already exists", t.getAdNumber());
				}
				Util.LOAD_INFO.incrementCurrent();
			}, e -> {
				e.printStackTrace();
				Util.LOAD_INFO.incrementCurrent();
			}));

			log.info("start return successfully");
		} else {
			log.info("already running");
		}
	}

	private Stream<String> detailsUrlsFromPageUrl(String pageUrl) {
		log.info("start getting details url for {} ", pageUrl);
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
		log.info("done creating stream of details urls of page {}", pageUrl);
		return builder.build();
	}

}
