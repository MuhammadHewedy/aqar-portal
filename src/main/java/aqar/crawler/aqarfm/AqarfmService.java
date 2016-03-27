package aqar.crawler.aqarfm;

import static aqar.crawler.aqarfm.Helper.*;
import static aqar.util.XPathUtils.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import aqar.crawler.AqarService;
import aqar.models.Apartment;
import aqar.services.UrlService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class AqarfmService implements AqarService {

	@Value("${services.aqarfm.baseUrl}")
	private String baseUrl;
	@Value("${services.aqarfm.searchUrl}")
	private String searchUrl;
	@Value("${services.aqarfm.enabled:false}")
	private boolean enabled;

	@Autowired
	private UrlService urlService;

	@Override
	public String getSearchUrl(int page) {
		return baseUrl + searchUrl + page;
	}

	@SuppressWarnings("unchecked")
	@Async
	@Override
	public CompletableFuture<Stream<String>> getDetailsUrls(String searchUrl) {
		try {
			Document doc = urlService.fromUrl(searchUrl);
			List<Node> list = get(doc, DETAILS_URLS, List.class);
			log.info("found {} details urls, in page URL: {}", list.size(), searchUrl);
			return CompletableFuture.completedFuture(list.stream().map(n -> ((Element) n).getAttribute("href")));
		} catch (Exception ex) {
			throw new RuntimeException("error during get details page for: " + searchUrl + " >> " + ex.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public int getPagesNumber() {
		Document doc = urlService.fromUrl(baseUrl + searchUrl + "1");
		List<Node> list = get(doc, PAGE, List.class);
		String href = ((Element) list.get(list.size() - 1)).getAttribute("href");
		int page = Integer.parseInt(href.substring(href.lastIndexOf('/') + 1));
		log.info("page numbers: {} ", page);
		return page;
	}

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Async
	@Override
	public CompletableFuture<Apartment> buildApartement(String detailsUrl) {

		Document doc = urlService.fromUrl(baseUrl + detailsUrl);

		Apartment apartment = new Apartment();
		List<String> imageUrls = getImages(doc);

		if (imageUrls != null && !imageUrls.isEmpty()) {
			apartment.setImageUrls(imageUrls);
			setAddress(doc, apartment);
			Long price = get(doc, PRICE, Long.class);
			if (price != null) {
				apartment.setPrice(price * 1000);
			}
			apartment.setLatitude(get(doc, GLAT, Double.class));
			apartment.setLongitude(get(doc, GLONG, Double.class));
			apartment.setAdNumber("aqarfm-" + get(doc, AD_NUMBER, String.class));
			apartment.setTitle(get(doc, TITLE, String.class));
			apartment.setRefUrl(get(doc, REF_URL, String.class));
			apartment.setFloorNumber(get(doc, FLOOR, Long.class));
			apartment.setNumOfRooms(get(doc, ROOM, Long.class));
			apartment.setWcNumber(get(doc, PATH_ROOMS, Long.class));
			Long buildYear = get(doc, BUILD_YEAR, Long.class);
			if (buildYear != null) {
				apartment.setBuildYear(LocalDate.now().getYear() - buildYear);
			}
			setDesc(doc, apartment);

			// TODO
			// set ad date
			// set area
			// contact person

			return CompletableFuture.completedFuture(apartment);
		} else {
			log.info("skipping {}, no images found", detailsUrl);
			throw new RuntimeException(String.format("skipping %s, no images found", detailsUrl));
		}
	}
}
