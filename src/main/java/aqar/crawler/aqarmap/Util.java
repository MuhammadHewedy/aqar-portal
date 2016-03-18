package aqar.crawler.aqarmap;

import static aqar.util.XPathUtils.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import aqar.models.Apartment;
import aqar.util.XPathUtils;

class Util {
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
	
	static List<String> getImageUrls(Document doc) {
		List<String> list = new ArrayList<>();
		NodeList nodeList = get(doc, IMG_URLS, NodeList.class);
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element e = (Element) nodeList.item(i);
			list.add(e.getAttribute("src"));
		}
		return list;
	}

	static String getMobile(Document doc) {
		Element ele = ((Element) get(doc, AD_MOBILE, Node.class));
		if (ele != null) {
			String mobile = ele.getAttribute("data-number");
			try {
				String decode = URLDecoder.decode(mobile, "utf8");
				decode = !decode.startsWith("0") ? "0" + decode : decode;
				return decode;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return "";
	}

	static void setLatAndLong(Document doc, Apartment apartment) {
		Node node = get(doc, LAT_LONG, Node.class);
		if (node != null) {
			Element e = (Element) node;
			String attribute = e.getAttribute("ng-init");
			if (attribute != null && attribute.contains("initListingLocation")) {
				apartment.setLatitude(
						Double.valueOf(attribute.substring(attribute.indexOf('(') + 1, attribute.indexOf(',')).trim()));
				apartment.setLongitude(
						Double.valueOf(attribute.substring(attribute.indexOf(',') + 1, attribute.indexOf(')')).trim()));
			}
		}
	}
	
	static {
		try {
			DETAILS_URLS = 	XPathUtils.factory.newXPath().compile("/html/body/div[3]/div[2]/section/div[2]/ul/li/a");
			TITLE = 		XPathUtils.factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[1]/h1");
			DISTRICT = 		XPathUtils.factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[1]/p");
			AD_NUMBER = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Listing ID"));
			AD_DATE = 		XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Publish Date"));
			PROPERTY_TYPE = XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Property Type"));
			PRICE = 		XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Price"));
			ADVERTISER = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Seller Role"));
			AREA = 			XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Size"));
			PAY_METHOD = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Payment Method"));
			NUM_OF_ROOMS = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Rooms"));
			FLOOR_NUMBER = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Floor"));
			WC_NUMBER = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Baths"));
			BUILD_YEAR = 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Year Built"));
			FINISHES = 		XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "Finish Type"));
			AD_MOBILE = 	XPathUtils.factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[5]/div[2]/div/a");
			DESC = 			XPathUtils.factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[3]/p[2]/text()");
			LAT_LONG = 		XPathUtils.factory.newXPath().compile("//*[@id=\"map\"]");
			IMG_URLS = 		XPathUtils.factory.newXPath().compile("/html/body/div[3]/div[1]/div/section/div[2]/div/img");

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
