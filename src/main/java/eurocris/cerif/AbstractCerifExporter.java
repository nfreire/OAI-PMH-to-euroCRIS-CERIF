package eurocris.cerif;

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

import eurocris.cerif.mapping.IdGenerator;
/**
 * @author Nuno
 *
 * Exports an OAI-PMH data source into a file based XML repository
 */
public abstract class AbstractCerifExporter {
	protected static final Properties XML_OUTPUT_OPTIONS=new Properties();
	static {
		XML_OUTPUT_OPTIONS.put("indent", "yes");
	}
	
	
	protected static File getRepositoryFile(File repositoryParentFolder,  String recId) {
		try {
			int hash=Math.abs(recId.hashCode());
			String midfolder=String.valueOf(hash % 100);
			File midFolderFile = new File(repositoryParentFolder, midfolder);
			if(!midFolderFile.exists())
				midFolderFile.mkdirs();
			File recFile=new File(midFolderFile, URLEncoder.encode(recId,"UTF-8")+".xml");
			return recFile;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
