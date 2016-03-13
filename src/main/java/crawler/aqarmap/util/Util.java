package crawler.aqarmap.util;

import java.io.StringWriter;
import java.util.concurrent.Future;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.w3c.dom.Document;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;

public class Util {

	public static final String PAGE_PARAM = "page";
	public static final String BASE_URL = "https://ksa.aqarmap.com";
	public static final String SEARCH_URL = "/en/for-rent/apartment/riyadh/?minPrice=0&maxPrice=999999&photos=1";

	public static Document fromUrl(String url) {
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

	public static String getStringFromDocument(Document doc) {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			throw new RuntimeException(ex);
		}
	}
}
