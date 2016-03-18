package aqar.crawler.aqarfm;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import aqar.util.XPathUtils;

class Util {

	static final XPathExpression PAGE;
	static final XPathExpression DETAILS_URLS;

	static {
		try {

			PAGE = 			XPathUtils.factory.newXPath().compile("/html/body/div[7]/ul/li/a");
			DETAILS_URLS = 	XPathUtils.factory.newXPath().compile("/html/body/div[6]/div/div[1]/h3/a");

		} catch (XPathExpressionException e) {
			throw new RuntimeException(e);
		}
	}
}
