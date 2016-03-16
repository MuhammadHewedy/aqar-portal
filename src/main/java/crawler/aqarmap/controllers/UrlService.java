package crawler.aqarmap.controllers;

import java.util.concurrent.Future;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

@Service
public class UrlService {

	@Retryable(maxAttempts = 10)
	public Document fromUrl(String url) {
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		Future<Response> future = asyncHttpClient.prepareGet(url).execute();
		Document doc;
		try {
			doc = new DomSerializer(new CleanerProperties())
					.createDOM(new HtmlCleaner().clean(future.get().getResponseBody()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			asyncHttpClient.close();
		}
		return doc;
	}
}
