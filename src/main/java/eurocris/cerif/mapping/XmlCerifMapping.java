package eurocris.cerif.mapping;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eurocris.oaipmh.XmlUtil;

/**
 * @author Nuno
 *
 *	Base implementation of a mapping to CERIF
 */
public abstract class XmlCerifMapping extends CerifMapping {
	
	public XmlCerifMapping() {
		super();
	}
	
	public abstract Element convert(String cfResPublId, Element sourceRoot);
	
}
