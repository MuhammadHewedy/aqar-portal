package aqar.crawler.aqarfm;

import static aqar.crawler.aqarfm.Util.*;
import static aqar.util.XPathUtils.*;

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
public class AqarfmService implements AqarService {

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

	@SuppressWarnings("unchecked")
	@Override
	public int getPagesNumber() {
		Document doc = urlService.fromUrl(baseUrl + searchUrl + "1");
		List<Node> list = get(doc, PAGE, List.class);
		String href = ((Element) list.get(list.size() - 1)).getAttribute("href");
		int page = Integer.parseInt(href.substring(href.lastIndexOf('/') + 1));
		log.info("page numbers: {} ", page);
		// return page;
		return 1;
	}

	@Override
	public boolean enabled() {
		return enabled;
	}

	@Async
	@Override
	public ListenableFuture<Apartment> buildApartement(String detailsUrl) {

		Document doc = urlService.fromUrl(baseUrl + detailsUrl);

		Apartment apartment = new Apartment();

		setAddress(doc, apartment);
		apartment.setPrice(get(doc, PRICE, Long.class));
		apartment.setLatitude(get(doc, GLAT, Double.class));
		apartment.setLongitude(get(doc, GLONG, Double.class));
		apartment.setAdNumber("aqarfm-" + get(doc, AD_NUMBER, String.class));
		apartment.setRefUrl(baseUrl + detailsUrl);
		apartment.setTitle(get(doc, TITLE, String.class));
		
		// TODO add rest of fields
		
		return new AsyncResult<Apartment>(apartment);
	}

}
