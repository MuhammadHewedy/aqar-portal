package aqar.services.crawler.aqarfm;

import static aqar.util.XPathUtils.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import aqar.models.Apartment;
import aqar.util.XPathUtils;

class Helper {
	
	private static final String TABLE_XPATH = "/html/body/div[4]/div[2]/table/tbody/tr/td[contains(text(),'%s')]";

	static final XPathExpression PAGE;
	static final XPathExpression DETAILS_URLS;
	static final XPathExpression TITLE;
	static final XPathExpression PRICE;
	static final XPathExpression GLAT;
	static final XPathExpression GLONG;
	static final XPathExpression AD_NUMBER;
	static final XPathExpression REF_URL;
	static final XPathExpression FLOOR;
	static final XPathExpression ROOM;
	static final XPathExpression PATH_ROOMS;
	static final XPathExpression BUILD_YEAR;
	static final XPathExpression INFO_TABLE_TR;
	static final XPathExpression IMAGES;
	
	static void setAddress(Document doc, Apartment apartment) {
		String string = get(doc, TITLE, String.class);
		List<String> address = Arrays.asList(string.split(","));
		if (address.size() > 0) {
			apartment.setCity(address.get(address.size() - 1));
		}
		if (address.size() > 1) {
			apartment.setDistrict(address.get(address.size() - 2));
		}
	}
	
	@SuppressWarnings("unchecked")
	static void setDesc(Document doc, Apartment apartment) {
		List<Node> list = get(doc, INFO_TABLE_TR, List.class);
		apartment.setDescription(list.get(list.size() - 3).getNodeValue());
	}

	@SuppressWarnings("unchecked")
	static List<String> getImages(Document doc) {
		List<Node> list = get(doc, IMAGES, List.class);
		List<String> imgList = new ArrayList<>(list.size());
		list.forEach(img -> {
			imgList.add(((Element) img).getAttribute("src"));
		});
		return imgList;
	}

	static {
		try {

			PAGE 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[7]/ul/li/a");
			DETAILS_URLS 	= 	XPathUtils.factory.newXPath().compile("/html/body/div[6]/div/div[1]/h3/a");
			TITLE 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/div/div/h3/small[1]/a[3]");
			PRICE 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/div/div/h3/small[2]/span");
			GLAT 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/span/meta[1]/@content");
			GLONG 			= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/span[2]/span/meta[2]/@content");
			AD_NUMBER 		= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/div[2]/table/tbody/tr[1]/td/kbd");
			REF_URL 		= 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH + "/a", "رابط"));
			FLOOR 			= 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "دور"));
			ROOM 			= 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "غرف"));
			PATH_ROOMS		= 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "مياة"));
			BUILD_YEAR		= 	XPathUtils.factory.newXPath().compile(String.format(TABLE_XPATH, "عمر"));
			INFO_TABLE_TR	= 	XPathUtils.factory.newXPath().compile("/html/body/div[4]/div[2]/table/tbody/tr/td");
			IMAGES			= 	XPathUtils.factory.newXPath().compile("//*[@id=\"ad-images\"]/div[2]/a/img");

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
