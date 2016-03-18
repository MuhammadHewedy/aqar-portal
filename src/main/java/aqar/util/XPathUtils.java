package aqar.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XPathUtils {

	public static final XPathFactory factory = XPathFactory.newInstance();

	/**
	 * return value, if the clazz type is number, then all non-numeric values
	 * will be stripped-out.
	 * 
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
			} else if (clazz == List.class) {
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
		} else if (List.class.isAssignableFrom(clazz)) {
			NodeList nodeList = (NodeList) value;
			List<Node> list = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				list.add(nodeList.item(i));
			}
			return (T) list;
		} else {
			return (T) value;
		}
	}

}
