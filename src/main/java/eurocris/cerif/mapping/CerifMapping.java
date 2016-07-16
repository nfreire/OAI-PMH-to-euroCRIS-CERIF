package eurocris.cerif.mapping;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eurocris.oaipmh.XmlUtil;

/**
 * @author Nuno
 *
 *	Base implementatio of a mapping to CERIF
 */
public abstract class CerifMapping {
	public static final String CERIF_NS="urn:xmlns:org:eurocris:cerif-1.5-1";
	
	protected IdGenerator idGenerator;

	protected Document cerifDoc;
	protected Element cerifParent;

	protected String defaultLanguage="en";
	
	public CerifMapping() {
		reset();
	}
	
	public void setIdGenerator(IdGenerator idGenerator) {
		this.idGenerator = idGenerator;
	}
	
	public abstract Element convert(String cfResPublId, Element sourceRoot);
	
	public Document getCerifDom() {
		return cerifDoc;
	}
	
	public void reset() {
		cerifDoc=XmlUtil.newDocument();
		cerifParent=cerifDoc.createElementNS(CERIF_NS, "CERIF");
		cerifDoc.appendChild(cerifParent);
	}
	
	

	protected void createCfMLangStringType(String elementName, String subElementName, String value, String cfResPublId, boolean translated, String langTag) {
		Element cfResPublTitleEl=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, elementName);
		XmlUtil.createElementNsIn(cfResPublTitleEl, CERIF_NS, "cfResPublId", cfResPublId);					
		Element cfTitleEl=XmlUtil.createElementNsIn(cfResPublTitleEl, CERIF_NS, subElementName, value);
		cfTitleEl.setAttribute("cfTrans", translated ? "h" : "o");
		cfTitleEl.setAttribute("cfLangCode", StringUtils.isEmpty(langTag) ? defaultLanguage : langTag);
	}
}
