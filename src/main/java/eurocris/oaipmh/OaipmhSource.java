package eurocris.oaipmh;

import java.util.ArrayList;
import java.util.List;

import eurocris.cerif.mapping.CerifMapping;

/**
 * @author Nuno
 *
 *	Data about an OAI-PMH metadata source
 *
 */
public class OaipmhSource {

    String baseUrl;
    List<String> sets=new ArrayList<String>();
    List<String> recordIdentifiers=new ArrayList<String>();
    String metadataPrefix;
    CerifMapping mapping;
    
	public OaipmhSource(String baseUrl, String metadataPrefix, CerifMapping mapping) {
		super();
		this.baseUrl = baseUrl;
		this.metadataPrefix = metadataPrefix;
		this.mapping = mapping;
	}
	
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public List<String> getSets() {
		return sets;
	}
	public void setSets(List<String> sets) {
		this.sets = sets;
	}
	public List<String> getRecordIdentifiers() {
		return recordIdentifiers;
	}
	public void setRecordIdentifiers(List<String> recordIdentifiers) {
		this.recordIdentifiers = recordIdentifiers;
	}
	public String getMetadataPrefix() {
		return metadataPrefix;
	}
	public void setMetadataPrefix(String metadataPrefix) {
		this.metadataPrefix = metadataPrefix;
	}

	public CerifMapping getMapping() {
		return mapping;
	}

	public void setMapping(CerifMapping mapping) {
		this.mapping = mapping;
	}
    
    
}
