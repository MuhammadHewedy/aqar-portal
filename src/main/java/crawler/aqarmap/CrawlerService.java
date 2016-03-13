package crawler.aqarmap;

import static crawler.aqarmap.XPathUtils.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.Stream.Builder;

import javax.xml.xpath.XPathConstants;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CrawlerService {

	@Autowired
	private ApartmentBuilder builder;
	@Autowired
	private ApartmentRepo apartmentRepo;

	public void crawle() {
		crawle("Riyadh", Util.BASE_URL + Util.SEARCH_URL, 1);
	}

	private void crawle(String city, String url, Integer to) {
		List<ListenableFuture<Apartment>> collect = IntStream.range(1, to + 1).parallel()
				.mapToObj(page -> url + "&" + Util.PAGE_PARAM + "=" + page)
				.flatMap(pUrl -> detailsUrlsFromPageUrl(pUrl)).map(dUrl -> builder.apartmentFromDetailsUrl(dUrl, city))
				.collect(Collectors.toList());

		collect.forEach(f -> f.addCallback(t -> {
			Long count = apartmentRepo.countByAdNumber(t.getAdNumber());
			if (count == 0) {
				apartmentRepo.save(t);
			} else {
				log.info("add {} already exists", t.getAdNumber());
			}
		} , e -> {
			e.printStackTrace();
		}));
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
