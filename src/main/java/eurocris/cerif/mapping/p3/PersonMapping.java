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

public class PersonMapping extends CsvCerifMapping {
	static final DatatypeFactory dateTypeFact;
	
	static {
		try {
			dateTypeFact = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}	
	
//	"Last Name";"First Name";"Gender";"Institute Name";"Institute Place";"Person ID SNSF";"ORCID";
//	"Projects as responsible Applicant";"Projects as Applicant";"Projects as Partner";
//	"Projects as Practice Partner";"Projects as Employee";"Projects as Contact Person"


//"Äämismaa";"Päivi";"male";"Abteilung Biophysikalische Chemie Biozentrum der Universität Basel";
//"Basel";"509100";"";"";"";"";"";"58800;107793";""
//
//"Aapro";"Matti S.";"male";
//"Clinique de Genolier F.M.H. Oncologie-Hématologie Centre pluridisciplinaire de Cancerologie";
//"Genolier";"3268";"";"8532;9513";"8155";"";"";"";""


	Element cerifCfPers;
	
	@Override
	public Element convert(CSVRecord sourceRec) {
//		protected void createCerifCfPers(String cfProjId, String name, String nameGiven, String nameFamily, String role) {
			 cerifCfPers=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfPers");
			
			String cfPersId = sourceRec.get(5);
			XmlUtil.createElementNsIn(cerifCfPers, CERIF_NS, "cfPersId", cfPersId);
			
			String gender=sourceRec.get(2);
			if(gender!=null) {
				if(gender.startsWith("m") || gender.startsWith("M"))
					XmlUtil.createElementNsIn(cerifCfPers, CERIF_NS, "cfGender", "m");
				else if(gender.startsWith("f") || gender.startsWith("F"))
					XmlUtil.createElementNsIn(cerifCfPers, CERIF_NS, "cfGender", "f");
			}
			
			String nameFamily=sourceRec.get(0);
			String nameGiven=sourceRec.get(1);

			if(nameFamily==null && nameGiven==null) 
				return null;
			
			Element elCfPersName=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfPersName");
			String elCfPersNameId = String.valueOf(idGenerator.generate());
			XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfPersNameId", elCfPersNameId);
			
			if(nameFamily!=null)
				XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFamilyNames", nameFamily.trim());
			if(nameGiven!=null)
				XmlUtil.createElementNsIn(elCfPersName, CERIF_NS, "cfFirstNames", nameGiven.trim());			

			Element elCfPersNamePers=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfPersName_Pers");
			
			XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfPersNameId", elCfPersNameId);
			XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfPersId", cfPersId);
			XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfClassId", "bdcf213d-df3e-4af4-a2ea-69ca26e98cd4");
			XmlUtil.createElementNsIn(elCfPersNamePers, CERIF_NS, "cfClassSchemeId", "7375609d-cfa6-45ce-a803-75de69abe21f");
			
			if(!StringUtils.isEmpty(sourceRec.get(6))) 
				createCerifCfFedId(cfPersId, sourceRec.get(6), "716bcc9a-c9dd-4b8b-b4ab-6c140e578ec3");
			
			if(!StringUtils.isEmpty(sourceRec.get(3)))
				createCerifCfOrgUnit(cfPersId, sourceRec.get(3), sourceRec.get(4), "c302c2f0-1cd7-11e1-8bc2-0800200c9a66");
			
			if(!StringUtils.isEmpty(sourceRec.get(7))) //"Projects as responsible Applicant"
				for(String projId: sourceRec.get(7).split(";"))
					createCerifCfProj(projId, cfPersId, "33551370-1cfe-11e1-8bc2-0800200c9a66");
//			createCerifCfProj(projId, cfPersId, "b0e11470-1cfd-11e1-8bc2-0800200c9a66");
			if(!StringUtils.isEmpty(sourceRec.get(8))) //"Projects as  Applicant"
				for(String projId: sourceRec.get(8).split(";"))
					createCerifCfProj(projId, cfPersId, "33551370-1cfe-11e1-8bc2-0800200c9a66");
			if(!StringUtils.isEmpty(sourceRec.get(9))) //"Projects as Partner"
				for(String projId: sourceRec.get(9).split(";"))
					createCerifCfProj(projId, cfPersId, "cb3e0010-1cd7-11e1-8bc2-0800200c9a66");
			if(!StringUtils.isEmpty(sourceRec.get(10))) //"Projects as Practice Partner"
				for(String projId: sourceRec.get(10).split(";"))
					createCerifCfProj(projId, cfPersId, "cb3e0010-1cd7-11e1-8bc2-0800200c9a66");
			if(!StringUtils.isEmpty(sourceRec.get(11))) //"Projects as Employee"
				for(String projId: sourceRec.get(11).split(";"))
					createCerifCfProj(projId, cfPersId, "cb3e0010-1cd7-11e1-8bc2-0800200c9a66");
			if(!StringUtils.isEmpty(sourceRec.get(12))) //"Projects as Contact Person"
				for(String projId: sourceRec.get(12).split(";"))
					createCerifCfProj(projId, cfPersId, "2af3d7c0-1cfe-11e1-8bc2-0800200c9a66");
//			"Projects as responsible Applicant";"Projects as Applicant";"Projects as Partner";
//			"Projects as Practice Partner";"Projects as Employee";"Projects as Contact Person"
//			"30891;39386;45755"
			
			return cerifCfPers;
	}

	protected void createCerifCfProj(String cfProjId, String cfPersId, String role) {
		Element cerifCfProj=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "cfProj");
		XmlUtil.createElementNsIn(cerifCfProj, CERIF_NS, "cfProjId", cfProjId);
		
		Element elCfProjPers=cerifDoc.createElementNS(CERIF_NS, "cfProj_Pers");
		cerifCfProj.appendChild(elCfProjPers);
		
		XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfPersId", cfPersId);
		XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfProjId", cfProjId);
		if (role!=null) {
			XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", role);
			XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "6b2b7d24-3491-11e1-b86c-0800200c9a66");
		} 
		if(role==null) {
			XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassId", "e4d7b130-1cfd-11e1-8bc2-0800200c9a66");
			XmlUtil.createElementNsIn(elCfProjPers, CERIF_NS, "cfClassSchemeId", "b7135ad0-1d00-11e1-8bc2-0800200c9a66");
		}
	}
	
	protected void createCerifCfOrgUnit(String cfPersId, String name, String place, String role) {
		Element elCfOrgUnit=XmlUtil.createElementNsIn(cerifParent, CERIF_NS, "CfOrgUnit");
		
		String cfOrgUnitId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfOrgUnitId", cfOrgUnitId);
		Element cfNameEl = XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfName", name);
		cfNameEl.setAttribute("cfTrans", "o");
		if(defaultLanguage!=null)
			cfNameEl.setAttribute("cfLangCode", defaultLanguage);
			
		Element elCfOrgUnitResPubl=XmlUtil.createElementNsIn(cerifCfPers, CERIF_NS, "cfPers_OrgUnit");
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfOrgUnitId", cfOrgUnitId);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassId", role);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassSchemeId", "994069a0-1cd6-11e1-8bc2-0800200c9a66");
	}
	protected void createCerifCfFedId(String cfPersId, String id, String idType) {
		Element elCfOrgUnit=XmlUtil.createElementNsIn(cerifCfPers, CERIF_NS, "cfFedId");
		
		String cfOrgUnitId = String.valueOf(idGenerator.generate());
		XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfFedIdId", cfOrgUnitId);
		XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfFedId", id);
		
		Element elCfOrgUnitResPubl=XmlUtil.createElementNsIn(elCfOrgUnit, CERIF_NS, "cfFedId_Class");
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassId", idType);
		XmlUtil.createElementNsIn(elCfOrgUnitResPubl, CERIF_NS, "cfClassSchemeId", "bccb3266-689d-4740-a039-c96594b4d916");
	}

}
