package aqar.crawler;

import java.util.stream.Stream;

import org.springframework.scheduling.annotation.Async;
import org.springframework.util.concurrent.ListenableFuture;

import aqar.models.Apartment;

public interface AqarService {

	/**
	 * used to compose the page url that contains details urls
	 * 
	 * @param page
	 * @return
	 */
	String getSearchUrl(int page);

	/**
	 * get details Urls from the search url
	 * 
	 * @param searchUrl
	 * @return
	 */
	Stream<String> getDetailsUrls(String searchUrl);

	/**
	 * get number of pages available to be iterated over it
	 * 
	 * @return
	 */
	int getPagesNumber();

	/**
	 * enable and disable the aqar service
	 * 
	 * @return
	 */
	boolean enabled();

	/**
	 * The implementation methods should be annotated by {@link Async}
	 * annotation <br />
	 * Used to build the {@link Apartment} object from the details url
	 * 
	 * @param detailsUrl
	 * @return a {@link ListenableFuture} as a promise of the {@link Apartment}
	 *         object
	 */
	ListenableFuture<Apartment> buildApartement(String detailsUrl);
}
