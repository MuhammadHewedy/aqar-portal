package crawler.aqarmap;

import java.lang.reflect.Method;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class XPathUtils {

	private static final XPathFactory factory = XPathFactory.newInstance();

	public static final XPathExpression DETAILS_URLS;
	public static final XPathExpression PRICE;
	public static final XPathExpression AD_NUMBER;
	public static final XPathExpression TITLE;
	public static final XPathExpression DISTRICT;
	public static final XPathExpression AD_DATE;
	public static final XPathExpression AD_MOBILE;
	public static final XPathExpression AD_TYPE;
	public static final XPathExpression TYPE;
	public static final XPathExpression AREA;
	public static final XPathExpression PAY_METHOD;
	public static final XPathExpression NUM_OF_ROOMS;
	public static final XPathExpression FLOOR_NUMBER;
	public static final XPathExpression WC_NUMBER;
	public static final XPathExpression BUILD_YEAR;
	public static final XPathExpression DESC;
	public static final XPathExpression LAT_LONG;
	public static final XPathExpression IMG_URLS;
	public static final XPathExpression CAT_OF_FINISHES;
	
	

	@SuppressWarnings("unchecked")
	public static <T> T get(Document doc, XPathExpression exp, Class<T> clazz) {
		String value;
		try {
			value = exp.evaluate(doc);
		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}

		if (Number.class.isAssignableFrom(clazz)) {
			value = value.replaceAll("[^\\d.]", "");
			if (value.length() > 0) {
				try {
					Method method = clazz.getMethod("valueOf", String.class);
					Object o = method.invoke(null, value);
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
			DETAILS_URLS = factory.newXPath().compile("/html/body/div[3]/div[2]/section/div[2]/ul/li/a");
			
			TITLE = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[1]/h1");
			DISTRICT = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[1]/p");
			AD_NUMBER = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[1]/td[2]");
			AD_DATE = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[6]/td[2]");
			AD_MOBILE = factory.newXPath().compile("/html/body/div[3]/div[1]/div/div[2]/section/div/div[2]/div/a/text()");
			AD_TYPE = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[4]/td[2]");
			PRICE = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[2]/td[2]/span");
			TYPE = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[3]/td[2]");
			AREA = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[5]/td[2]");
			PAY_METHOD = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[7]/td[2]");
			NUM_OF_ROOMS = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[8]/td[2]");
			FLOOR_NUMBER = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[9]/td[2]");
			WC_NUMBER = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[10]/td[2]");
			BUILD_YEAR = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[11]/td[2]");
			CAT_OF_FINISHES = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/table/tbody/tr[12]/td[2]");
			DESC = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[3]/p[2]/text()");
			LAT_LONG = factory.newXPath().compile("//*[@id=\"map\"]");
			IMG_URLS = factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[2]/div[1]/div/div/div/img");

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
