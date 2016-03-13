package crawler.aqarmap;

import java.lang.reflect.Method;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathUtils {

	private static final XPathFactory factory = XPathFactory.newInstance();
	private static final String TABLE_XPATH = "/html/body/div[3]/div[1]/div/section/table/tbody/tr/td[contains(text(),'%s')]//following-sibling::td";

	public static final XPathExpression DETAILS_URLS;
	public static final XPathExpression PRICE;
	public static final XPathExpression AD_NUMBER;
	public static final XPathExpression TITLE;
	public static final XPathExpression DISTRICT;
	public static final XPathExpression AD_DATE;
	public static final XPathExpression AD_MOBILE;
	public static final XPathExpression PROPERTY_TYPE;
	public static final XPathExpression ADVERTISER;
	public static final XPathExpression AREA;
	public static final XPathExpression PAY_METHOD;
	public static final XPathExpression NUM_OF_ROOMS;
	public static final XPathExpression FLOOR_NUMBER;
	public static final XPathExpression WC_NUMBER;
	public static final XPathExpression BUILD_YEAR;
	public static final XPathExpression DESC;
	public static final XPathExpression LAT_LONG;
	public static final XPathExpression IMG_URLS;
	public static final XPathExpression FINISHES;
	
	

	/**
	 * return value, if the clazz type is number, then all non-numeric values will be stripped-out.
	 * @param doc
	 * @param exp
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Document doc, XPathExpression exp, Class<T> clazz) {
		Object value;
		try {
			if (clazz == Node.class) {
				value = exp.evaluate(doc, XPathConstants.NODE);
			} else if (clazz == NodeList.class) {
				value = exp.evaluate(doc, XPathConstants.NODESET);
			} else {
				value = exp.evaluate(doc);
			}
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		if (Number.class.isAssignableFrom(clazz)) {
			String sValue = value.toString();
			sValue = sValue.replaceAll("[^\\d.]", "");
			if (sValue.length() > 0) {
				try {
					Method method = clazz.getMethod("valueOf", String.class);
					Object o = method.invoke(null, sValue);
					return (T) o;
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			} else {
				return null;
			}
		} else {
			return (T) value;
		}
	}

	static {
		try {
			DETAILS_URLS = 	factory.newXPath().compile("/html/body/div[3]/div[2]/section/div[2]/ul/li/a");
			TITLE = 		factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[1]/h1");
			DISTRICT = 		factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[1]/p");
			AD_NUMBER = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Listing ID"));
			AD_DATE = 		factory.newXPath().compile(String.format(TABLE_XPATH, "Publish Date"));
			PROPERTY_TYPE = factory.newXPath().compile(String.format(TABLE_XPATH, "Property Type"));
			PRICE = 		factory.newXPath().compile(String.format(TABLE_XPATH, "Price"));
			ADVERTISER = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Seller Role"));
			AREA = 			factory.newXPath().compile(String.format(TABLE_XPATH, "Size"));
			PAY_METHOD = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Payment Method"));
			NUM_OF_ROOMS = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Rooms"));
			FLOOR_NUMBER = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Floor"));
			WC_NUMBER = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Baths"));
			BUILD_YEAR = 	factory.newXPath().compile(String.format(TABLE_XPATH, "Year Built"));
			FINISHES = 		factory.newXPath().compile(String.format(TABLE_XPATH, "Finish Type"));
			AD_MOBILE = 	factory.newXPath().compile("/html/body/div[3]/div[1]/div/div[2]/section/div/div[2]/div/a/text()");
			DESC = 			factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[3]/p[2]/text()");
			LAT_LONG = 		factory.newXPath().compile("//*[@id=\"map\"]");
			IMG_URLS = 		factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[2]/div[1]/div/div/div/img");

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
