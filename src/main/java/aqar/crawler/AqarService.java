package aqar.crawler;

import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import aqar.models.Apartment;

public interface AqarService {

	String getSearchUrl(int page);

	Stream<String> getDetailsUrls(String searchUrl);

	int getPagesNumber();

	boolean enabled();

	/**
	 * The implementation methods should be annotated by {@link Async}
	 * annotation
	 * 
	 * @param detailsUrl
	 * @return
	 */
	ListenableFuture<Apartment> buildApartement(String detailsUrl);
}
