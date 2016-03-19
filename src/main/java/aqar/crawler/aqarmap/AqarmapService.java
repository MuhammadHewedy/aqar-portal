package aqar.crawler.aqarmap;

import static aqar.crawler.aqarmap.Helper.*;
import static aqar.util.XPathUtils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import aqar.crawler.AqarService;
import aqar.models.Apartment;
import aqar.services.UrlService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
class AqarmapService implements AqarService {

	@Value("${services.aqarmap.baseUrl}")
	private String baseUrl;
	@Value("${services.aqarmap.searchUrl}")
	private String searchUrl;
	@Value("${services.aqarmap.enabled:false}")
	private boolean enabled;

	@Autowired
	private UrlService urlService;

	@Override
	public String getSearchUrl(int page) {
		return baseUrl + searchUrl + "&page=" + page;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<String> getDetailsUrls(String searchUrl) {
		try {
			Document doc = urlService.fromUrl(searchUrl);
			List<Node> list = get(doc, DETAILS_URLS, List.class);
			log.info("found {} details urls, in page URL: {}", list.size(), searchUrl);
			return list.stream().map(n -> ((Element) n).getAttribute("href"));
		} catch (Exception ex) {
			log.error("error during get details page for: " + searchUrl + " >> " + ex.getMessage());
			return Stream.empty();
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public int getPagesNumber() {
		log.debug("calculating page numbers for...");
		Document doc = urlService.fromUrl(baseUrl + searchUrl + "&page=" + 1);
		List<Node> list = get(doc, PAGE, List.class);
		String href = ((Element) list.get(list.size() - 1)).getAttribute("href");
		int pages = Integer.parseInt(href.substring(href.lastIndexOf('=') + 1));
		log.info("pages number: {}", pages);
		return pages;
	}

	@Async
	@Override
	public ListenableFuture<Apartment> buildApartement(String detailsUrl) {
		Document doc = urlService.fromUrl(baseUrl + detailsUrl);

		Apartment apartment = new Apartment();

		String[] split = detailsUrl.split("\\/");
		if (split != null && split.length > 1) {
			apartment.setCityRegion(split[split.length - 2]);
		}
		apartment.setRefUrl(baseUrl + detailsUrl);
		apartment.setAdNumber("aqarmap-" + get(doc, AD_NUMBER, String.class));
		apartment.setPrice(get(doc, PRICE, Long.class));
		apartment
				.setAdDate(LocalDate.parse(get(doc, AD_DATE, String.class), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		apartment.setAdMobile(getMobile(doc));
		apartment.setPropertyType(get(doc, PROPERTY_TYPE, String.class));
		apartment.setArea(Integer.valueOf(get(doc, AREA, String.class).replace(" M&sup2;", "")));
		apartment.setBuildYear(get(doc, BUILD_YEAR, Long.class));
		apartment.setCategoryOfFinishes(get(doc, FINISHES, String.class));
		apartment.setDescription(get(doc, DESC, String.class));
		apartment.setFloorNumber(get(doc, FLOOR_NUMBER, Long.class));
		apartment.setNumOfRooms(get(doc, NUM_OF_ROOMS, Long.class));
		apartment.setAdvertiser(get(doc, ADVERTISER, String.class));
		apartment.setCity("");
		apartment.setPayMethod(get(doc, PAY_METHOD, String.class));
		apartment.setTitle(get(doc, TITLE, String.class));
		apartment.setWcNumber(get(doc, WC_NUMBER, Long.class));
		apartment.setDistrict(get(doc, DISTRICT, String.class));
		apartment.setImageUrls(getImageUrls(doc));
		setLatAndLong(doc, apartment);

		return new AsyncResult<Apartment>(apartment);
	}

	@Override
	public boolean enabled() {
		return enabled;
	}
}
