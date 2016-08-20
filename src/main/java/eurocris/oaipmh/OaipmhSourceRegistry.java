package eurocris.oaipmh;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eurocris.cerif.mapping.ModsMetsMappingEsUnivNavarra;
import eurocris.cerif.mapping.ModsMetsMappingItImt;
import eurocris.cerif.mapping.ModsMetsMappingPtRcaap;

/**
 * @author Nuno
 *
 * Real instances of the OAI-PMH metadata sources. 
 * These sources were analysed during the course of the HolaCloud project
 */
public class OaipmhSourceRegistry {
	public static OaipmhSource PT_RCAAP=new OaipmhSource("https://comum.rcaap.pt/oaiextended/driver", "mets", new ModsMetsMappingPtRcaap());
	public static OaipmhSource PT_RCAAP_DATA=new OaipmhSource("http://dados.rcaap.pt/oaiextended/driver", "mets", new ModsMetsMappingPtRcaap());
	public static OaipmhSource ES_UNAVARRA=new OaipmhSource("http://academica-e.unavarra.es/oai/request", "mets", new ModsMetsMappingEsUnivNavarra());
	public static OaipmhSource IT_IMT=new OaipmhSource("http://eprints.imtlucca.it/cgi/oai2", "mets", new ModsMetsMappingItImt());
	public static OaipmhSource PT_IPL=new OaipmhSource("http://repositorio.ipl.pt/oaiextended/request", "mets", new ModsMetsMappingPtRcaap());
	public static OaipmhSource PT_IPP=new OaipmhSource("http://recipp.ipp.pt/oaiextended/request", "mets", new ModsMetsMappingPtRcaap());
	public static OaipmhSource PT_ISMT=new OaipmhSource("http://repositorio.ismt.pt/oai/request", "mets", new ModsMetsMappingPtRcaap());
	public static OaipmhSource PT_ISCTE=new OaipmhSource("https://repositorio.iscte-iul.pt/oai/driver", "mets", new ModsMetsMappingPtRcaap());
	
	

	
	public static OaipmhSource[] ALL=new  OaipmhSource[] {
//			PT_RCAAP
//			,
//			PT_RCAAP_DATA
//			, 
//			ES_UNAVARRA
//			, 
//			IT_IMT
//			, 
//			PT_IPL
//			, 
//			PT_IPP
//			, 
//			PT_ISMT
//			, 
			PT_ISCTE
			};

}
