package eurocris.cerif.mapping;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eurocris.oaipmh.XmlUtil;


/**
 * @author Nuno
 *
 *  This class is adapted to the case of University of Navarra
 */
public class ModsMetsMappingItImt extends ModsMetsMapping {

	public ModsMetsMappingItImt() {
		defaultLanguage="it";
		topMdElementXPath="//*[local-name()='mdWrap' and @MDTYPE='MODS']/*[local-name()='xmlData']";
	}
	
	@Override
	public Element convert(String cfResPublId, Element sourceRoot) {
		cerifCfResPub=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfResPubl");
		XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfResPublId", cfResPublId);
		
		Element modsEl;
		try {
//			<mets:mdWrap MDTYPE="MODS"><mets:xmlData>
			modsEl = XmlUtil.getElementByXpath(sourceRoot, topMdElementXPath);
//			modsEl = XmlUtil.getElementByXpath(sourceRoot, "//*[local-name()='mods']");
//			modsEl = XmlUtil.getElementByXpath(sourceRoot, "//*[local-name()='mods'][*[local-name()='aCode']='aaa']dmdSec/mdWrap/xmlData/mods");
//			modsEl = XmlUtil.getElementByXpath(sourceRoot, "dmdSec/mdWrap/xmlData/mods");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
//		Element modsEl = XmlUtil.getElementByTagNameWithParents(sourceRoot, "dmdSec", "mdWrap", "xmlData", "mods");
		if(modsEl==null) 
			return null;
		
		for(Element subEl : XmlUtil.elements(modsEl)) {
			if(subEl.getLocalName().equals("name")) {
				String role=null;
				String name=null;
				String nameGiven=null;
				String nameFamily=null;
				Element roleTermEl = XmlUtil.getElementByTagNameWithParents(subEl, "role", "roleTerm");
				if(roleTermEl!=null) {
					role=roleTermEl.getTextContent();
				}
				List<Element> nameParts = XmlUtil.elements(subEl, "namePart");

				for(Element namePartEl: nameParts) {
					String type = namePartEl.getAttribute("type");
					if(type==null) {
						name=namePartEl.getTextContent();
					} else if(type.equals("family")) {
						nameFamily=namePartEl.getTextContent();
					} else if(type.equals("given")) {
						nameGiven=namePartEl.getTextContent();
					} else {
						name=namePartEl.getTextContent();
					}
				}
				if(role!=null && (role.equals("affiliation") || role.equals("department")))
					createCerifCfOrgUnit(cfResPublId, name, role);
				else
					createCerifCfPers(cfResPublId, name, nameGiven, nameFamily, role);
			} else  if(subEl.getLocalName().equals("identifier")) {
				String idType = subEl.getAttribute("type");
				String idVal = subEl.getTextContent();
				if( StringUtils.isEmpty(idType)) {
					System.out.println("Untyped id (ignoring it): "+idVal);
				} else if(idType.equals("isbn")) {
					XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfISBN", idVal);
				} else if(idType.equals("issn")) {
					XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfISSN", idVal);
				} else if(idType.equalsIgnoreCase("URI")) {
					XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfURI", idVal);
				} else if(idType.equals("doi")) {
					Element cfFedIdEl = XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfFedId");
					String idId = idGenerator.generateString();
					XmlUtil.createElementNsIn(cfFedIdEl, CERIF_NS, "cfFedIdId", idId);
					XmlUtil.createElementNsIn(cfFedIdEl, CERIF_NS, "cfFedId", idVal);
					XmlUtil.createElementNsIn(cfFedIdEl, CERIF_NS, "cfClassId", "31d222b4-11e0-434b-b5ae-088119c51189");
					XmlUtil.createElementNsIn(cfFedIdEl, CERIF_NS, "cfClassSchemeId", "bccb3266-689d-4740-a039-c96594b4d916");
				} else if(idType.equals("tid") || idType.equals("other")) {
					Element cfFedIdEl = XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfFedId");
					String idId = idGenerator.generateString();
					XmlUtil.createElementNsIn(cfFedIdEl, CERIF_NS, "cfFedIdId", idId);
					XmlUtil.createElementNsIn(cfFedIdEl, CERIF_NS, "cfFedId", idVal);
				} else if(idType.equals("citation")) {
					Element cfCiteEl=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfCite");
					Element cfCiteId=XmlUtil.createElementNsIn(cfCiteEl, CERIF_NS, "cfCiteId", String.valueOf(idGenerator.generate()));
					if(URL_PATTERN.matcher(idVal.trim()).matches()) {
						XmlUtil.createElementNsIn(cfCiteEl, CERIF_NS, "cfURI", idVal.trim());
					} else
						XmlUtil.createElementNsIn(cfCiteEl, CERIF_NS, "cfDescr", idVal);
					
					Element cfResPubCiteEl=XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfResPubl_Cite");
					XmlUtil.createElementNsIn(cfResPubCiteEl, CERIF_NS, "cfCiteId", cfCiteId.getTextContent());
				} else {
					System.out.println("Unknown typed id (ignoring it): "+idType);
				}
			} else  if(subEl.getLocalName().equals("originInfo")) {
				Element dateIssuedEl = XmlUtil.getElementByTagName(subEl, "dateIssued");
				if(dateIssuedEl!=null)
					XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfResPublDate", dateIssuedEl.getTextContent());

			} else  if(subEl.getLocalName().equals("titleInfo")) {
				StringBuilder titleBuilder=new StringBuilder();
				for(Element tiSubEl : XmlUtil.elements(subEl)) {
					if(tiSubEl.getLocalName().equals("title")
							|| tiSubEl.getLocalName().equals("nonSort")
							|| tiSubEl.getLocalName().equals("partNumber")
							|| tiSubEl.getLocalName().equals("partName")
							) {
						if(titleBuilder.length() > 0) titleBuilder.append(" ");
						titleBuilder.append(tiSubEl.getTextContent());
					} else if(tiSubEl.getLocalName().equals("subtitle")) {
						
					}
				}
				String typeOfTitle=subEl.getAttribute("type");
				String langTag=subEl.getAttribute("lang");
				boolean translated=typeOfTitle!=null && typeOfTitle.equals("translated");
				if(titleBuilder.length() > 0) 
					createCfMLangStringType("cfResPublTitle", "cfTitle", titleBuilder.toString(), cfResPublId, translated, langTag);
				Element subTitleEl = XmlUtil.getElementByTagName(subEl, "subTitle");
				if(subTitleEl!=null) 
					createCfMLangStringType("cfResPublSubtitle", "cfSubtitle", subTitleEl.getTextContent(), cfResPublId, translated, langTag);
			} else  if(subEl.getLocalName().equals("abstract")) {
				createCfMLangStringType("cfResPublAbstract", "cfAbstract", subEl.getTextContent(), cfResPublId, false, subEl.getAttribute("lang"));
			} else  if(subEl.getLocalName().equals("subject")) {
				Element keywdEl = XmlUtil.getElementByTagName(subEl, "topic");
				if(keywdEl!=null) 
					createCfMLangStringType("cfResPublKeyw", "cfKeyw", keywdEl.getTextContent(), cfResPublId, false, subEl.getAttribute("lang"));
			}
		}

		Element fileLocatEl;
		try {
			fileLocatEl = XmlUtil.getElementByXpath(sourceRoot, "//*[local-name()='fileSec']"
					+ "/*[local-name()='fileGrp' and @USE='ORIGINAL']"
					+ "/*[local-name()='file']"
					+ "/*[local-name()='FLocat']");
			if(fileLocatEl!=null) {
				String uri=fileLocatEl.getAttributeNS("http://www.w3.org/1999/xlink", "href");
				if(!StringUtils.isEmpty(uri))
					XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfURI", uri.trim());
			}
			fileLocatEl = XmlUtil.getElementByXpath(sourceRoot, "//*[local-name()='fileSec']"
					+ "/*[local-name()='fileGrp' and @USE='TEXT']"
					+ "/*[local-name()='file']"
					+ "/*[local-name()='FLocat']");
			if(fileLocatEl!=null) {
				String uri=fileLocatEl.getAttributeNS("http://www.w3.org/1999/xlink", "href");
				if(!StringUtils.isEmpty(uri))
					XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfURI", uri.trim());
			}	
//			modsEl = XmlUtil.getElementByXpath(sourceRoot, "//*[local-name()='mods'][*[local-name()='aCode']='aaa']dmdSec/mdWrap/xmlData/mods");
//			modsEl = XmlUtil.getElementByXpath(sourceRoot, "dmdSec/mdWrap/xmlData/mods");
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
//		mets:fileSec/mets:fileGrp[@USE=''ORIGINAL]/mets:file/mets:fileLocat[@xlink:href]

		
		//sort values in cfResPubl according to schema
		ArrayList<Node> childs=new ArrayList<>(cerifCfResPub.getChildNodes().getLength()-1);
		for(int i=1 ; i<cerifCfResPub.getChildNodes().getLength() ; i++) {
			childs.add(cerifCfResPub.removeChild(cerifCfResPub.getChildNodes().item(i)));
		}
		for(String elNames : CF_RES_PUBL_ELEMENTS_ORDER) {
			for(int i=0 ; i<childs.size() ; i++) {
				if(childs.get(i).getLocalName().equals(elNames)) {
					cerifCfResPub.appendChild(childs.remove(i));
					i--;
				}
			}
			if(childs.isEmpty())
				break;
		}
		for(int i=0 ; i<childs.size() ; i++) 
			cerifCfResPub.appendChild(childs.get(i));
		return cerifCfResPub;
	}
}
