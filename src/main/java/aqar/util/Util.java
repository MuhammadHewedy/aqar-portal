package aqar.util;

import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

public class Util {

	// TODO convert to Bean
	public static final LoadInfo loadInfo = new LoadInfo();

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
		private int success = 0;
		private int failed = 0;
		private int nullObj = 0;
		private int dup = 0;
		private long timeInMills = 0;
		@JsonIgnore
		private long startMillis;
		
		public void calcTimeInMills() {
			this.timeInMills = System.currentTimeMillis() - startMillis;
		}

		public void incrementSucc() {
			this.success += 1;
		}

		public void incrementFail() {
			this.failed += 1;
		}

		public void incrementNullObj() {
			this.nullObj += 1;
		}

		public void incrementDup() {
			this.dup += 1;
		}

		public void reset() {
			locked = false;
		}
	}
}
