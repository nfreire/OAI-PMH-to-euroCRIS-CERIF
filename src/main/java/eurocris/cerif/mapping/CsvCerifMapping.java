package eurocris.cerif.mapping;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eurocris.oaipmh.XmlUtil;

/**
 * @author Nuno
 *
 *	Base implementation of a mapping to CERIF
 */
public abstract class CsvCerifMapping extends CerifMapping {
	
	public CsvCerifMapping() {
		super();
	}
	
	public abstract Element convert(CSVRecord sourceRec);
	
}
