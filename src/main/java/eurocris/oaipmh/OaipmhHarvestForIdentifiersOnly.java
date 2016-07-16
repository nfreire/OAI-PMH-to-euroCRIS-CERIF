package eurocris.oaipmh;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.oclc.oai.harvester2.verb.ListIdentifiers;
import org.w3c.dom.DOMException;
import org.xml.sax.SAXException;

/**
 * Harvests only record identifiers and not the metadata 
 * 
 * @author Nuno Freire (nfreire@gmail.com)
 */
public class OaipmhHarvestForIdentifiersOnly  extends OaipmhHarvestByListIdentifiers{

    /**
     * Creates a new instance of this class.
     * @param baseURL
     * @param from
     * @param until
     * @param metadataPrefix
     * @param setSpec
     * @throws HarvestException
     */
    public OaipmhHarvestForIdentifiersOnly(String baseURL, String from, String until,
                                           String metadataPrefix, String setSpec)
                                                                                 throws HarvestException {
        super(baseURL, from, until, metadataPrefix, setSpec);
    }

    /**
     * Creates a new instance of this class.
     * @param baseURL
     * @param metadataPrefix
     * @param setSpec
     * @throws HarvestException
     */
    public OaipmhHarvestForIdentifiersOnly(String baseURL, String metadataPrefix, String setSpec)
                                                                                                 throws HarvestException {
        super(baseURL, metadataPrefix, setSpec);
    }

    /**
     * Creates a new instance of this class.
     * @param baseURL
     * @param resumptionToken
     */
    public OaipmhHarvestForIdentifiersOnly(String baseURL, String resumptionToken) {
        super(baseURL, resumptionToken);
    }
    
    @Override
    protected OaiPmhRecord getRecord(OaiPmhRecord record) throws IOException,
            ParserConfigurationException, SAXException, TransformerException, DOMException,
            HarvestException {
        return record;
    }

}
