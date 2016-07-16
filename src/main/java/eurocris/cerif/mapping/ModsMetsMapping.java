package eurocris.cerif.mapping;

import java.util.ArrayList;
import java.util.regex.Pattern;

import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import eurocris.oaipmh.XmlUtil;

/**
 * @author Nuno
 *
 *	General mappings to CERIF applicable to most uses of MODS.
 *  This class is adapted to the case when MODS is embedded in METS metadata
 */
public abstract class ModsMetsMapping extends CerifMapping{
	public static final Pattern URL_PATTERN=Pattern.compile("^(https?:\\/\\/)?([\\da-z\\.-]+)\\.([a-z\\.]{2,6})([\\/\\w \\.-]*)*\\/?$");
	public static final String[] CF_RES_PUBL_ELEMENTS_ORDER=new String[] {
			"cfResPublDate", "cfNum", "fVol", 
			"cfEdition", "cfSeries", "cfIssue", 
			"cfStartPage", "cfEndPage", "cfTotalPages", 
			"cfISBN", "cfISSN", "cfURI"};
//	<xs:sequence>
//	<xs:element name="cfResPublId" type="cfId__Type"/>
//	<xs:element name="cfResPublDate" type="xs:date" minOccurs="0"/>
//	<xs:element name="cfNum" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfVol" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfEdition" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfSeries" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfIssue" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfStartPage" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfEndPage" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfTotalPages" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfISBN" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfISSN" type="xs:string" minOccurs="0"/>
//	<xs:element name="cfURI" type="xs:string" minOccurs="0"/>
	
	Element cerifCfResPub;
	
	public ModsMetsMapping() {
		
	}
	
	@Override
	public Element convert(String cfResPublId, Element sourceRoot) {
		cerifCfResPub=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfResPubl");
		XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfResPublId", cfResPublId);
		
		Element modsEl;
		try {
			modsEl = XmlUtil.getElementByXpath(sourceRoot, "//*[local-name()='mods']");
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
				Element roleTermEl = XmlUtil.getElementByTagNameWithParents(subEl, "role", "roleTerm");
				if(roleTermEl!=null) {
					role=roleTermEl.getTextContent();
				}
				Element namePartEl = XmlUtil.getElementByTagName(subEl, "namePart");
				if(namePartEl!=null) {
					name=namePartEl.getTextContent();
				}
				if(role!=null && (role.equals("affiliation") || role.equals("department")))
					createCerifCfOrgUnit(cfResPublId, name, role);
				else
					createCerifCfPers(cfResPublId, name, role);
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

	 
	private void createCerifCfOrgUnit(String cfResPublId, String name, String role) {
		Element elCfOrgUnit=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfOrgUnit");
		
		String cfOrgUnitId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfOrgUnitId", cfOrgUnitId);
		Element cfNameEl = XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfName", name);
		cfNameEl.setAttribute("cfTrans", "o");
		cfNameEl.setAttribute("cfLangCode", defaultLanguage);
			
		Element elCfOrgUnitResPubl=XmlUtil.createElementNsIn(cerifCfResPub, CERIF_NS, "cfOrgUnit_ResPubl");
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfOrgUnitId", cfOrgUnitId);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassId", "49815870-1cfe-11e1-8bc2-0800200c9a66");
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
//		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassSchemeId", "6b2b7d26-3491-11e1-b86c-0800200c9a66");
	}
	private void createCerifCfPers(String cfResPublId, String name, String role) {
		Element elCfPers=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfPers");
		
		String cfPersId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfPers, CERIF_NS, "cfPersId", cfPersId);
		
		Element elCfPersResPubl=cerifDoc.createElementNS(CERIF_NS, "cfPers_ResPubl");
		cerifParent.appendChild(elCfPersResPubl);
		
		XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfPersId", cfPersId);
		XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfResPubId", cfResPublId);
		if (role!=null) {
			if (role.equals("advisor")) {
				XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassId", "6b2b7d22-3491-11e1-b86c-0800200c9a66");
				XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassSchemeId", "6b2b7d24-3491-11e1-b86c-0800200c9a66");
			} else if (role.equals("author")) {
				XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassId", "49815870-1cfe-11e1-8bc2-0800200c9a66");
				XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
			} else if (role.equals("editor")) {
				XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassId", "708b3df0-1cfe-11e1-8bc2-0800200c9a66");
				XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
			} else if (role.equals("affiliation")) {
				throw new RuntimeException("affiliation should be mapped elsewhere");
			} else {
				System.out.println("WARN: Role not mapped: "+role);
				role=null;
			}
		} 
		if(role==null) {
			XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassId", "e4d7b130-1cfd-11e1-8bc2-0800200c9a66");
			XmlUtil.createElementNsIn(elCfPersResPubl, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
		}
			
		Element elCfPersName=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfPersName");
		String elCfPersNameId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfPersNameId", elCfPersNameId);
		if(name.contains(",")) {
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFamilyNames", name.substring(0, name.indexOf(',')).trim());
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFirstNames", name.substring(name.indexOf(',')+1).trim());
		} else 
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFirstNames", name.trim());

		Element elCfPersNamePers=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfPersName_Pers");
		
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfPersNameId", elCfPersNameId);
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfPersId", cfPersId);
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfClassId", "bdcf213d-df3e-4af4-a2ea-69ca26e98cd4");
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfClassSchemeId", "7375609d-cfa6-45ce-a803-75de69abe21f");
	}

}
