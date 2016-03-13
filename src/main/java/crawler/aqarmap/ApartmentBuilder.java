package crawler.aqarmap;

import static crawler.aqarmap.XPathUtils.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApartmentBuilder {

	@Async
	public ListenableFuture<Apartment> apartmentFromDetailsUrl(String detailsUrl, String city) {
		log.info("calling details url: {}", detailsUrl);
		Document doc = Util.fromUrl(Util.BASE_URL + detailsUrl);

		Apartment apartment = new Apartment();

		apartment.setRefUrl(detailsUrl);
		apartment.setAdNumber(get(doc, AD_NUMBER, String.class));
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
		apartment.setCity(city);
		apartment.setPayMethod(get(doc, PAY_METHOD, String.class));
		apartment.setTitle(get(doc, TITLE, String.class));
		apartment.setWcNumber(get(doc, WC_NUMBER, Long.class));
		apartment.setDistrict(get(doc, DISTRICT, String.class));
		apartment.setImageUrls(getImageUrls(doc));
		setLatAndLong(doc, apartment);

		return new AsyncResult<Apartment>(apartment);
	}

	private List<String> getImageUrls(Document doc) {
		List<String> list = new ArrayList<>();
		NodeList nodeList = get(doc, IMG_URLS, NodeList.class);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element e = (Element) nodeList.item(i);
			list.add(e.getAttribute("src"));
		}
		return list;
	}

	private String getMobile(Document doc) {
		Element ele = ((Element) get(doc, AD_MOBILE, Node.class));
		if (ele != null) {
			String mobile = ele.getAttribute("data-number");
			try {
				return URLDecoder.decode(mobile, "utf8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	private void setLatAndLong(Document doc, Apartment apartment) {
		Node node = get(doc, LAT_LONG, Node.class);
		if (node != null) {
			Element e = (Element) node;
			String attribute = e.getAttribute("ng-init");
			if (attribute != null && attribute.contains("initListingLocation")) {
				apartment.setLatitude(
						Double.valueOf(attribute.substring(attribute.indexOf('(') + 1, attribute.indexOf(',')).trim()));
				apartment.setLongitude(
						Double.valueOf(attribute.substring(attribute.indexOf(',') + 1, attribute.indexOf(')')).trim()));
			}
		}
	}
}
