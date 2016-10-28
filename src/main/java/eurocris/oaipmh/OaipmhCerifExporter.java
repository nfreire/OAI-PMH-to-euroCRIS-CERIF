package eurocris.oaipmh;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eurocris.cerif.AbstractCerifExporter;
import eurocris.cerif.mapping.IdGenerator;
/**
 * @author Nuno
 *
 * Exports an OAI-PMH data source into a file based XML repository
 */
public class OaipmhCerifExporter extends AbstractCerifExporter {
	
	public static void main(String[] args) {
		HttpsUtil.initSslTrustingHostVerifier();
		File repository=new File(args==null || args.length<1 ? "repository" : args[0]);
		if(!repository.exists())
			repository.mkdirs();

		
		IdGenerator idGenerator=new IdGenerator();
		
		for(OaipmhSource oaipmhSource: OaipmhSourceRegistry.ALL) {
			oaipmhSource.getMapping().setIdGenerator(idGenerator);
			try {
				File reposotoryOfSource=new File(repository, URLEncoder.encode(oaipmhSource.getBaseUrl(), "UTF-8"));
				List<String> sets = oaipmhSource.getSets();
				if(sets.isEmpty()){
					sets= new ArrayList<>();
					sets.add(null);
				}
				for(String setspec: sets) {
					File repositoryOfSet=new File(reposotoryOfSource, setspec==null? "__ALL__" : URLEncoder.encode(setspec, "UTF-8"));
//					if(!repositoryOfSet.exists())
//						repositoryOfSet.mkdirs();
					File repositoryOfSetInFormat = new File(repositoryOfSet, oaipmhSource.getMetadataPrefix());
					File repositoryOfSetInFormatCerif = new File(repositoryOfSet, "cerif");
//					if(!repositoryOfSetInFormat.exists())
//						repositoryOfSetInFormat.mkdirs();
					OaipmhHarvest harvest=new OaipmhHarvest(oaipmhSource.getBaseUrl(), oaipmhSource.getMetadataPrefix(), setspec);
					while( harvest.hasNext() ) {
						OaiPmhRecord rec = (OaiPmhRecord) harvest.next();
//						File recFile=new File(repositoryOfSetInFormat, URLEncoder.encode(rec.getIdentifier(),"UTF-8")+".xml");
						File recFile=getRepositoryFile(repositoryOfSetInFormat, rec.getIdentifier());
						File recFileCerif=getRepositoryFile(repositoryOfSetInFormatCerif, rec.getIdentifier());
						if(rec.isDeleted()) {
							if(recFile.exists())
								recFile.delete();
							if(recFileCerif.exists())
								recFileCerif.delete();
						} else {
							Element metadata = rec.getMetadata();
							XmlUtil.writeDomToFile(metadata, recFile);
							oaipmhSource.getMapping().convert(rec.getIdentifier(), metadata);
							Document cerifDom = oaipmhSource.getMapping().getCerifDom();
							XmlUtil.writeDomToFile(cerifDom, recFileCerif, XML_OUTPUT_OPTIONS);
							oaipmhSource.getMapping().reset();
						}
					}
				}
			} catch (UnsupportedEncodingException | HarvestException e) {
				System.err.println("Harvest failed:" + oaipmhSource);
				e.printStackTrace();
			}
		}
	}
	
}
