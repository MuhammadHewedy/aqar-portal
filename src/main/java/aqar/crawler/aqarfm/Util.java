package aqar.crawler.aqarfm;

import static aqar.util.XPathUtils.*;

import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;

import aqar.models.Apartment;
import aqar.util.XPathUtils;

class Util {

	static final XPathExpression PAGE;
	static final XPathExpression DETAILS_URLS;
	static final XPathExpression TITLE;
	static final XPathExpression PRICE;
	static final XPathExpression GLAT;
	static final XPathExpression GLONG;
	static final XPathExpression AD_NUMBER;
	
	
	static void setAddress(Document doc, Apartment apartment) {
		String string = get(doc, TITLE, String.class);
		List<String> address = Arrays.asList(string.split("،"));
		if (address.size() > 0) {
			apartment.setCity(address.get(address.size() - 1));
		}
		if (address.size() > 1) {
			apartment.setDistrict(address.get(address.size() - 2));
		}
	}

	static {
		try {

			PAGE 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[7]/ul/li/a");
			DETAILS_URLS 	= 	XPathUtils.factory.newXPath().compile("/html/body/div[6]/div/div[1]/h3/a");
			TITLE 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/div/div/h3/small[1]/a[3]");
			PRICE 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/div[2]/div[1]/div");
			GLAT 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/span/meta[1]/@content");
			GLONG 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/span/meta[2]/@content");
			AD_NUMBER 		= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/div[2]/table/tbody/tr[1]/td/kbd");

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
