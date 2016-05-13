package se.esss.litterbox.linaclego.data;

import java.io.PrintWriter;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.utilities.LegoLatticeFileComment;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoInfo 
{
	private String id = null;
	private String type = null;
	private String value = "";
	public String getType() {return type;}
	public String getId() {return id;}
	public String getValue() {return value;}
	public void setId(String id) {this.id = id;}
	
	public LegoInfo(LegoLatticeFileComment llfc)
	{
		if (llfc.attributeExists("id")) this.id = llfc.getAttribute("id");
		this.value = llfc.getAttribute("info");
		this.type = "comment";
		if (llfc.attributeExists("type")) this.type = llfc.getAttribute("type");
	}
	public LegoInfo(String id, String value, String type)
	{
		this.id = id;
		this.value = value;
		this.type = type;
	}
	public LegoInfo(SimpleXmlReader infoTag) throws LinacLegoException 
	{
		try {this.id = infoTag.attribute("id");} catch (SimpleXmlException e) {this.id = null;}
		this.value = infoTag.getCharacterData();
		try {this.type = infoTag.attribute("type");} catch (SimpleXmlException e) {this.type = "comment";}
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
}
