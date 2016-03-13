package crawler.aqarmap;

import static crawler.aqarmap.XPathUtils.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ApartmentBuilder {

	@Async
	public ListenableFuture<Apartment> apartmentFromDetailsUrl(String detailsUrl) {
		log.info("calling details url: {}", detailsUrl);
		Document doc = Util.fromUrl(Util.BASE_URL + detailsUrl);

		Apartment apartment = new Apartment();

		apartment.setRefUrl(detailsUrl);

		apartment.setAdNumber(get(doc, AD_NUMBER, String.class));

		apartment.setPrice(get(doc, PRICE, Long.class));
		apartment
				.setAdDate(LocalDate.parse(get(doc, AD_DATE, String.class), DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		apartment.setAdMobile(get(doc, AD_MOBILE, String.class));
		apartment.setPropertyType(get(doc, PROPERTY_TYPE, String.class));
		apartment.setArea(get(doc, AREA, Integer.class));		// BUG HERE, XXX FIX IT
		apartment.setBuildYear(get(doc, BUILD_YEAR, Long.class));
		apartment.setCategoryOfFinishes(get(doc, FINISHES, String.class));
		apartment.setDescription(get(doc, DESC, String.class));
		apartment.setDistrict("TODO");
		apartment.setFloorNumber(get(doc, FLOOR_NUMBER, Long.class));
		apartment.setNumOfRooms(get(doc, NUM_OF_ROOMS, Long.class));

		// TODO add rest of fields

		return new AsyncResult<Apartment>(apartment);
	}
}
