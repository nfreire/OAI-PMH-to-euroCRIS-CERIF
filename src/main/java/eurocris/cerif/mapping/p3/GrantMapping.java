package eurocris.cerif.mapping.p3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Element;

import eurocris.cerif.mapping.CsvCerifMapping;
import eurocris.oaipmh.XmlUtil;

public class GrantMapping extends CsvCerifMapping {
	static final DatatypeFactory dateTypeFact;
	
	static {
		try {
			dateTypeFact = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}	
	
	// "Project Number";"Project Title";"Project Title English";"Responsible
	// Applicant";"Funding Instrument";"Funding Instrument
	// Hierarchy";"Institution";"University";"Discipline Number";"Discipline
	// Name";"Discipline Name Hierarchy";"Start Date";"End Date";"Approved
	// Amount";"Keywords";"Abstract";"Lay Summary Lead (English)";"Lay Summary
	// (English)";"Lay Summary Lead (German)";"Lay Summary (German)";"Lay
	// Summary Lead (French)";"Lay Summary (French)";"Lay Summary Lead
	// (Italian)";"Lay Summary (Italian)"

	// "1";"Schlussband (Bd. VI) der Jacob Burckhardt-Biographie";"";"Kaegi
	// Werner";"Project funding (Div. I-III)";"Project funding";"";"Nicht
	// zuteilbar - NA";"10302";"Swiss history";"Human and Social
	// Sciences;Theology & religious studies, history, classical studies,
	// archaeology, prehistory and early
	// history";"01.10.1975";"30.09.1976";"11619.00";"";"";"";"";"";"";"";"";"";""


	Element cerifCfProj;
	
	@Override
	public Element convert(CSVRecord sourceRec) {
		String cfProjId=sourceRec.get(0);		
		cerifCfProj=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfProj");
		XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfProjId", cfProjId);
		
		if(!StringUtils.isEmpty(sourceRec.get(1))) 
			XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfTitle", sourceRec.get(1));
		if(!StringUtils.isEmpty(sourceRec.get(2))) {
			Element titleEl = XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfTitle", sourceRec.get(1));
			titleEl.setAttribute("cfLangCode", "EN");
		}
		if(!StringUtils.isEmpty(sourceRec.get(3))) {			
			createCerifCfPers(cfProjId, sourceRec.get(3), null, null, "33551370-1cfe-11e1-8bc2-0800200c9a66");
//			createCerifCfPers(cfProjId, sourceRec.get(3), null, null, "b0e11470-1cfd-11e1-8bc2-0800200c9a66");
		}
		if(!StringUtils.isEmpty(sourceRec.get(4))) {
//			11"Start Date";12"End Date"
//			<xs:element name="cfStartDate" type="xs:date" minOccurs="0"/>
//			<xs:element name="cfEndDate" type="xs:date" minOccurs="0"/>
			SimpleDateFormat df=new SimpleDateFormat("dd.MM.yyyy");
			Date start = null;
			Date end = null;
			if(!StringUtils.isEmpty(sourceRec.get(11))) try {
				start = df.parse(sourceRec.get(11));
			} catch (ParseException e) {
				System.out.println("WARNING: Unparsable date");
				e.printStackTrace();
			}
			if(!StringUtils.isEmpty(sourceRec.get(12))) try {
				end = df.parse(sourceRec.get(12));
			} catch (ParseException e) {
				System.out.println("WARNING: Unparsable date");
				e.printStackTrace();
			}
			
//			13"Approved Amount"
			createCerifCfFund(cfProjId, sourceRec.get(4), "125a3e36-a300-449f-267C-abfa-11178d87ba63", sourceRec.get(13), start, end);
		}
		if(!StringUtils.isEmpty(sourceRec.get(6))) {			
			createCerifCfOrgUnit(cfProjId, sourceRec.get(6), "eda2b2f4-34c5-11e1-b86c-0800200c9a66");
		}
		if(!StringUtils.isEmpty(sourceRec.get(7))) {			
			createCerifCfOrgUnit(cfProjId, sourceRec.get(7), "eda2b2ec-34c5-11e1-b86c-0800200c9a66");
		}
//		8"Discipline Number";9"Discipline Name";10"Discipline Name Hierarchy";
		if(!StringUtils.isEmpty(sourceRec.get(9))) {
			Element titleEl = XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfKeyw", sourceRec.get(9));
			if(!StringUtils.isEmpty(defaultLanguage))
				titleEl.setAttribute("cfLangCode", defaultLanguage);
			titleEl.setAttribute("cfTrans", "o");
		}	
		if(!StringUtils.isEmpty(sourceRec.get(10))) {
			Element titleEl = XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfKeyw", sourceRec.get(10));
			if(!StringUtils.isEmpty(defaultLanguage))
				titleEl.setAttribute("cfLangCode", defaultLanguage);
			titleEl.setAttribute("cfTrans", "o");
		}	
		
		
		
//		14"Keywords";15"Abstract";
		if(!StringUtils.isEmpty(sourceRec.get(14))) {
			Element titleEl = XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfKeyw", sourceRec.get(14));
			if(!StringUtils.isEmpty(defaultLanguage))
				titleEl.setAttribute("cfLangCode", defaultLanguage);
			titleEl.setAttribute("cfTrans", "o");
		}	
		if(!StringUtils.isEmpty(sourceRec.get(15))) {
			Element titleEl = XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfAbstr", sourceRec.get(15));
			if(!StringUtils.isEmpty(defaultLanguage))
				titleEl.setAttribute("cfLangCode", defaultLanguage);
			titleEl.setAttribute("cfTrans", "o");
		}		
		
//		16"Lay Summary Lead (English)";"Lay Summary
		// (English)";"Lay Summary Lead (German)";"Lay Summary (German)";"Lay
		// Summary Lead (French)";"Lay Summary (French)";"Lay Summary Lead
		// (Italian)";"Lay Summary (Italian)"
		return cerifCfProj;
	}
	
	
	protected void createCerifCfFund(String cfProjId, String name, String role, String financedAmount, Date start, Date end) {
//		<xs:complexType name="cfFund__Type">
//		<xs:sequence>
//		<xs:element name="cfFundId" type="cfId__Type"/>
//		<xs:element nam="cfStartDate" type="xs:date" minOccurs="0"/>
//		<xs:element name="cfEndDate" type="xs:date" minOccurs="0"/>
//		<xs:element name="cfAcro" type="xs:string" minOccurs="0"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		<xs:element name="cfURI" type="xs:string" minOccurs="0"/>
//		<xs:choice minOccurs="0" maxOccurs="unbounded">
//		<!--  embedded multiple-language attributes  -->
//		<xs:element name="cfName" type="cfMLangString__Type"/>
//		<xs:element name="cfDescr" type="cfMLangString__Type"/>
//		<xs:element name="cfKeyw" type="cfMLangString__Type"/>
//		<!--  embedded linking entities  -->
//		<xs:element name="cfEquip_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfEquipId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfEvent_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfEventId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfFacil_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfFacilId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfFund_Class" type="cfCoreClassWithFraction__Type"/>
//		<xs:element name="cfFund_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfFundId2" type="cfId__Type"/>
//		</xs:sequence>
//		<xs:sequence>
//		<xs:element name="cfFundId1" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfOrgUnit_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfOrgUnitId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfPers_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfPersId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfProj_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfProjId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfResProd_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfResProdId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfResPubl_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfResPublId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfResPat_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfResPatId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfSrv_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfSrvId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfMedium_Fund">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfMediumId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfFund_Indic">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfIndicId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<xs:element name="cfFund_Meas">
//		<xs:complexType>
//		<xs:sequence>
//		<xs:choice>
//		<xs:sequence>
//		<xs:element name="cfMeasId" type="cfId__Type"/>
//		</xs:sequence>
//		</xs:choice>
//		<xs:group ref="cfCoreClassWithFraction__Group"/>
//		<xs:element name="cfAmount" type="cfAmount__Type" minOccurs="0"/>
//		</xs:sequence>
//		</xs:complexType>
//		</xs:element>
//		<!--  embedded federated identifiers  -->
//		<xs:element name="cfFedId" type="cfFedId__EmbType"/>
//		</xs:choice>
//		</xs:sequence>
//		</xs:complexType>
		Element elCfFund=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfFund");
		
		
		String cfFundId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfFund, CERIF_NS, "cfFundId", cfFundId);
		if(start!=null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(start);
			XMLGregorianCalendar dateTime = dateTypeFact.newXMLGregorianCalendar(gc);
			Element startDateEl = XmlUtil.createElementNsIn(elCfFund, CERIF_NS, "cfStartDate", dateTime.toString());
		}
		if(end!=null) {
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTime(end);
			XMLGregorianCalendar dateTime = dateTypeFact.newXMLGregorianCalendar(gc);
			Element startDateEl = XmlUtil.createElementNsIn(elCfFund, CERIF_NS, "cfEndDate", dateTime.toString());
		}
		
		
		Element cfNameEl = XmlUtil.createElementNsIn(elCfFund, CERIF_NS, "cfName", name);
		cfNameEl.setAttribute("cfTrans", "o");
		if(defaultLanguage!=null)
			cfNameEl.setAttribute("cfLangCode", defaultLanguage);
		if(!StringUtils.isEmpty(financedAmount)) {
			Element cfAmountEl = XmlUtil.createElementNsIn(elCfFund, CERIF_NS, "cfAmount", financedAmount);
			cfNameEl.setAttribute("cfCurrCode", "CHF");
		}
			
		Element elCfOrgUnitResPubl=XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfProj_Fun");
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfFundId", cfFundId);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassId", role);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassSchemeId", "a620795c-7015-482e-bdba-43a761b337a1");
//		<cfClassId>125a3e36-a300-449f -267C -abfa-11178d87ba63</cfClassId>
//		<!--
//		 this is the uuid for the CERIF scheme "Activity Funding Types" 
//		-->
//		<cfClassSchemeId>a620795c-7015-482e-bdba-43a761b337a1</cfClassSchemeId>
	}
	 
	protected void createCerifCfOrgUnit(String cfProjId, String name, String role) {
//		<cfProj_OrgUnit>
//		<cfOrgUnitId>6123451</cfOrgUnitId>
//		<!--  this is the uuid for the term "Coordinator"  -->
//		<cfClassId>c31d3380-1cfd-11e1-8bc2-0800200c9a66</cfClassId>
//		<!--
//		 this is the uuid for the CERIF scheme "Organisation Project Engagements" 
//		-->
//		<cfClassSchemeId>6b2b7d25-3491-11e1-b86c-0800200c9a66</cfClassSchemeId>
//		<cfStartDate>2009-02-01T00:00:00</cfStartDate>
//		<cfEndDate>2012-01-31T24:00:00</cfEndDate>
//		</cfProj_OrgUnit>
		Element elCfOrgUnit=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfOrgUnit");
		
		String cfOrgUnitId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfOrgUnitId", cfOrgUnitId);
		Element cfNameEl = XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfName", name);
		cfNameEl.setAttribute("cfTrans", "o");
		if(defaultLanguage!=null)
			cfNameEl.setAttribute("cfLangCode", defaultLanguage);
			
		Element elCfOrgUnitResPubl=XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfProj_OrgUnit");
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfOrgUnitId", cfOrgUnitId);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassId", role);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassSchemeId", "6b2b7d25-3491-11e1-b86c-0800200c9a66");
	}
	protected void createCerifCfPers(String cfProjId, String name, String nameGiven, String nameFamily, String role) {
		Element elCfPers=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfPers");
		
		String cfPersId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfPers, CERIF_NS, "cfPersId", cfPersId);
		
		Element elCfProjPers=cerifDoc.createElementNS(CERIF_NS, "cfProj_Pers");
		cerifCfProj.appendChild(elCfProjPers);
		
		XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfPersId", cfPersId);
		XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfProjId", cfProjId);
		if (role!=null) {
				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", role);
				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "6b2b7d24-3491-11e1-b86c-0800200c9a66");
//			if (role.equals("advisor") || role.equals("Orientador/supervisor")) {
//				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", "6b2b7d22-3491-11e1-b86c-0800200c9a66");
//				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "6b2b7d24-3491-11e1-b86c-0800200c9a66");
//			} else if (role.equals("author")) {
//				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", "49815870-1cfe-11e1-8bc2-0800200c9a66");
//				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
//			} else if (role.equals("editor")) {
//				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", "708b3df0-1cfe-11e1-8bc2-0800200c9a66");
//				XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
//			} else if (role.equals("affiliation")) {
//				throw new RuntimeException("affiliation should be mapped elsewhere");
//			} else {
//				System.out.println("WARN: Role not mapped: "+role);
//				role=null;
//			}
		} 
		if(role==null) {
			XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", "e4d7b130-1cfd-11e1-8bc2-0800200c9a66");
			XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
		}
			
		Element elCfPersName=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfPersName");
		String elCfPersNameId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfPersNameId", elCfPersNameId);
		if(name!=null && name.contains(",")) {
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFamilyNames", name.substring(0, name.indexOf(',')).trim());
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFirstNames", name.substring(name.indexOf(',')+1).trim());
		} else if(name!=null)
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFirstNames", name.trim());
		else {
			if(nameFamily!=null)
				XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFamilyNames", nameFamily.trim());
			if(nameGiven!=null)
				XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFirstNames", nameGiven.trim());			
		}

		Element elCfPersNamePers=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfPersName_Pers");
		
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfPersNameId", elCfPersNameId);
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfPersId", cfPersId);
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfClassId", "bdcf213d-df3e-4af4-a2ea-69ca26e98cd4");
		XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfClassSchemeId", "7375609d-cfa6-45ce-a803-75de69abe21f");
	}


}
