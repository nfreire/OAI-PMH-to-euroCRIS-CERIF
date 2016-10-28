package eurocris.csv;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eurocris.cerif.AbstractCerifExporter;
import eurocris.cerif.mapping.IdGenerator;
import eurocris.cerif.mapping.p3.GrantMapping;
import eurocris.cerif.mapping.p3.PersonMapping;
import eurocris.oaipmh.HarvestException;
import eurocris.oaipmh.HttpsUtil;
import eurocris.oaipmh.OaiPmhRecord;
import eurocris.oaipmh.OaipmhCerifExporter;
import eurocris.oaipmh.OaipmhHarvest;
import eurocris.oaipmh.OaipmhSource;
import eurocris.oaipmh.OaipmhSourceRegistry;
import eurocris.oaipmh.XmlUtil;

public class ConvertP3CsvDumpToCerif extends AbstractCerifExporter {
	
	public static void main(String[] args) {
		File repository=new File(args==null || args.length<1 ? "repository" : args[0]);
		if(!repository.exists())
			repository.mkdirs();
		File csvsFolder=new File(args==null || args.length<2 ? "data" : args[1]);
		if(!csvsFolder.exists())
			throw new RuntimeException(csvsFolder.getPath()+" does not exits");
		
		IdGenerator idGenerator=new IdGenerator();
		GrantMapping grantMapping=new GrantMapping();
		grantMapping.setDefaultLanguage(null);
		grantMapping.setIdGenerator(idGenerator);
		PersonMapping personMapping=new PersonMapping();
		personMapping.setDefaultLanguage(null);
		personMapping.setIdGenerator(idGenerator);
		File reposotoryOfSource=new File(repository, "p3grants");
		File repositoryOfSet=new File(reposotoryOfSource, "__ALL__");
		File repositoryOfSetInFormatCerif = new File(repositoryOfSet, "cerif");
		File inCsvFile = new File(csvsFolder, "P3_GrantExport_with_abstracts.csv");
		try {
			Reader csvReader=new FileReader(inCsvFile);
			CSVParser csvREader=new CSVParser(csvReader, CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader());
			
			Iterator<CSVRecord> harvest = csvREader.iterator();
			while( harvest.hasNext() ) {
				try {
					while( harvest.hasNext() ) {
						CSVRecord rec = harvest.next();
						
						Element converted = grantMapping.convert(rec);
	//						File recFile=new File(repositoryOfSetInFormat, URLEncoder.encode(rec.getIdentifier(),"UTF-8")+".xml");
							File recFileCerif=getRepositoryFile(repositoryOfSetInFormatCerif, "P3:Project:"+rec.get(0));
							Document cerifDom = grantMapping.getCerifDom();
							XmlUtil.writeDomToFile(cerifDom, recFileCerif, OaipmhCerifExporter.XML_OUTPUT_OPTIONS);
							grantMapping.reset();
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Resuming after error");
				}
			}
		} catch (IOException e) {
			System.err.println("Harvest failed:" + inCsvFile);
			e.printStackTrace();
		}
		 inCsvFile = new File(csvsFolder, "P3_PersonExport.csv");
		try {
			Reader csvReader=new FileReader(inCsvFile);
			CSVParser csvREader=new CSVParser(csvReader, CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader());
			
			Iterator<CSVRecord> harvest = csvREader.iterator();
			while( harvest.hasNext() ) {
				try {
					while( harvest.hasNext() ) {
						CSVRecord rec = harvest.next();
						
						Element converted = personMapping.convert(rec);
						//						File recFile=new File(repositoryOfSetInFormat, URLEncoder.encode(rec.getIdentifier(),"UTF-8")+".xml");
						File recFileCerif=getRepositoryFile(repositoryOfSetInFormatCerif, "P3:Person:"+rec.get(5));
						Document cerifDom = personMapping.getCerifDom();
						XmlUtil.writeDomToFile(cerifDom, recFileCerif, OaipmhCerifExporter.XML_OUTPUT_OPTIONS);
						personMapping.reset();
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.err.println("Resuming after error");
				}
			}
		} catch (IOException e) {
			System.err.println("Harvest failed:" + inCsvFile);
			e.printStackTrace();
		}
	}

}
