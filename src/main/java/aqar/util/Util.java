package aqar.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import lombok.Data;

public class Util {

	// TODO convert to Bean
	public static final LoadInfo LOAD_INFO = new LoadInfo();

	// used for debugging to print the html page
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

	@Data
	public static class LoadInfo {
		private boolean locked = false;
		private int totalCount = 0;
		private int success = 0;
		private int failed = 0;

		public void incrementSucc() {
			this.success += 1;
		}

		public void incrementFail() {
			this.failed += 1;
		}

		public void reset() {
			locked = false;
			totalCount = success = failed = 0;

		}
	}
}
