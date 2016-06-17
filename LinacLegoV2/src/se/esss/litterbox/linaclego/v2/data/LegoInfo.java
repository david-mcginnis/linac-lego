package se.esss.litterbox.linaclego.v2.data;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoInfo  implements Serializable
{
	private static final long serialVersionUID = -673811424089237558L;
	private String id = null;
	private String type = null;
	private String value = "";
	public String getType() {return type;}
	public String getId() {return id;}
	public String getValue() {return value;}
	public void setId(String id) {this.id = id;}
	public void setValue(String value) {this.value = value;}
	
	public LegoInfo(LegoLatticeFileComment llfc)
	{
		if (llfc.attributeExists("id")) this.id = llfc.getAttribute("id");
		this.value = llfc.getAttribute("info");
		this.type = "comment";
		if (llfc.attributeExists("type")) this.type = llfc.getAttribute("type");
	}
	public LegoInfo(LegoInfo legoInfo)
	{
		this.id = legoInfo.id;
		this.value = legoInfo.value;
		this.type = legoInfo.type;
	}
	public LegoInfo(String id, String value, String type)
	{
		this.id = id;
		this.value = value;
		this.type = type;
	}
	public LegoInfo(SimpleXmlReader infoTag) throws LinacLegoException 
	{
		try {this.id = infoTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException("LegoInfo does not have an id.");}
		this.value = infoTag.getCharacterData();
		try {this.type = infoTag.attribute("type");} catch (SimpleXmlException e) {throw new LinacLegoException("LegoInfo does not have an type.");}
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("info");
			if (id != null) xw.setAttribute("id", id);
			xw.setAttribute("type", type);
			xw.writeCharacterData(value);
			xw.closeXmlTag("info");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public void writeToLatticeFile(PrintWriter pw, String tab)
	{
		if (type.equals("comment")) pw.println(";" + tab + value);
		if (type.equals("tune")) pw.println(tab + " " + value);
	}
	public boolean valueMatchsType() throws LinacLegoException
	{
		if (value != null) 
		{
			char test = ' ';
			try {test = value.charAt(0);} catch (java.lang.StringIndexOutOfBoundsException e){return true;}
			if (test == '#') return false;
			return true;
		}
		return false;
	}
	public boolean matchesLegoInfoTemplate(LegoInfo legoInfoTemplate) throws LinacLegoException
	{
		boolean matches = true;
		if (!legoInfoTemplate.getId().equals(getId())) return false;
		if (!legoInfoTemplate.getType().equals(getType())) return false;
		if (legoInfoTemplate.valueMatchsType())
		{
			if (!legoInfoTemplate.getValue().equals(getValue())) return false;
		}
		return matches;
	}
	public boolean matchesIdType(LegoInfo LegoInfoTemplate) throws LinacLegoException
	{
		boolean matches = true;
		if (!LegoInfoTemplate.getId().equals(getId())) return false;
		if (!LegoInfoTemplate.getType().equals(getType())) return false;
		return matches;
	}
	public static LegoInfo findLegoInfoById(ArrayList<LegoInfo> legoInfoList, String id)
	{
		int icount = 0; 
		while (icount < legoInfoList.size())
		{
			if (id.equals(legoInfoList.get(icount).getId()))
			{
				return legoInfoList.get(icount);
			}
			icount = icount + 1;
		}
		return null;
		
	}
}