package se.esss.litterbox.linaclego.v2.data;

import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoVariable  implements Serializable
{
	private static final long serialVersionUID = -2374776355595329459L;
	private String id = null;
	private String type = null;
	private String value = null;
	private String unit = null;
	public String getId() {return id;}
	public String getType() {return type;}
	public String getValue() {return value;}
	public String getUnit() {return unit;}
	public void setValue(String value) {this.value = value;}
	
	public LegoVariable(String id, String type, String unit)
	{
		this.id = id;
		this.type = type;
		this.unit = unit;
	}
	public LegoVariable(SimpleXmlReader variableTag) throws LinacLegoException
	{
		try 
		{
			this.id = variableTag.attribute("id");
			this.type = variableTag.attribute("type");
			this.unit = variableTag.attribute("unit");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("var");
			xw.setAttribute("id", id);
			xw.setAttribute("type", type);
			xw.setAttribute("unit", unit);
			xw.closeXmlTag("var");

		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public static LegoVariable findLegoDataById(ArrayList<LegoVariable> legoVariableList, String id)
	{
		if (id.indexOf("#") == 0) id = id.substring(1); // takes care of string variables
		int icount = 0; 
		while (icount < legoVariableList.size())
		{
			if (id.equals(legoVariableList.get(icount).getId()))
			{
				return legoVariableList.get(icount);
			}
			icount = icount + 1;
		}
		return null;
	}
	

}
