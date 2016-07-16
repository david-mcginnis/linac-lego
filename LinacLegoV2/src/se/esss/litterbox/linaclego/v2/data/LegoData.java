package se.esss.litterbox.linaclego.v2.data;

import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.legosets.LegoSet;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoData  implements Serializable
{
	private static final long serialVersionUID = 9089341391422767526L;
	private String id = null;
	private String value = null;
	private String type = null;
	private String unit = null;
	private LegoSet legoSet = null;
	
	public LegoData(String id, String value, String type, String unit)
	{
		this.id = id;
		this.value = value;
		this.type = type;
		this.unit = unit;
	}
	public LegoData(LegoData legoData)
	{
		this.id = legoData.id;
		this.value = legoData.value;
		this.type = legoData.type;
		this.unit = legoData.unit;
	}
	public LegoData(SimpleXmlReader dataTag) throws LinacLegoException 
	{
		try 
		{
			this.id = dataTag.attribute("id");
			this.value = dataTag.getCharacterData();
		} catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		try {this.type = dataTag.attribute("type");} catch (SimpleXmlException e) {this.type = "type";}
		try {this.unit = dataTag.attribute("unit");} catch (SimpleXmlException e) {this.unit = "unit";}
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("d");
			xw.setAttribute("id", id);
			if (type != null ) xw.setAttribute("type", type);
			if (unit != null ) xw.setAttribute("unit", unit);
			if (value != null ) xw.writeCharacterData(value);
			xw.closeXmlTag("d");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public String getId() {return id;}
	public String getValue() throws LinacLegoException 
	{
		if (value != null )
		{
			return value;
		}
		else
		{
			throw new LinacLegoException("Cannot find value of " + id);
		}
	}
	public String getType() {return type;}
	public String getUnit() {return unit;}
	public LegoSet getLegoSet() {return legoSet;}

	public void setId(String id) {this.id = id;}
	public void setValue(String value) {this.value = value;}
	public void setType(String type) {this.type = type;}
	public void setUnit(String unit) {this.unit = unit;}
	public void setLegoSet(LegoSet legoSet) {this.legoSet = legoSet;}
	
	public boolean unitMatches(String unit)
	{
		return unit.equals(this.unit);
	}
	
	public boolean valueMatchsType() throws LinacLegoException
	{

		if (type.toLowerCase().equals("double"))
		{
			boolean isDouble = true;
			try
			{
				Double.parseDouble(value);
			}catch (NumberFormatException nfe)
			{
				isDouble = false;
			}
			catch (java.lang.NullPointerException e)
			{
				throw new LinacLegoException("Null value for data value " + id);
			}
			return isDouble;
		}
		if (type.toLowerCase().equals("int"))
		{
			boolean isInt = true;
			try
			{
				Integer.parseInt(value);
			}catch (NumberFormatException nfe)
			{
				isInt = false;
			}
			return isInt;
		}
		if (type.toLowerCase().equals("string"))
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
		if (type.toLowerCase().equals("boolean"))
		{
			boolean isBoolean = false;
			if (value.toLowerCase().equals("true")) isBoolean = true;
			if (value.toLowerCase().equals("false")) isBoolean = true;
			return isBoolean;
		}
		return false;
	}
	public boolean matchesDataElementTemplate(LegoData dataElementTemplate) throws LinacLegoException
	{
		boolean matches = true;
		if (!dataElementTemplate.getId().equals(getId())) return false;
		if (dataElementTemplate.getUnit() != null)
			if (!dataElementTemplate.getUnit().equals(getUnit())) return false;
		if (dataElementTemplate.getType() != null)
			if (!dataElementTemplate.getType().equals(getType())) return false;
		if (dataElementTemplate.getType() != null)
		{
			if (dataElementTemplate.valueMatchsType())
			{
				if (!dataElementTemplate.getValue().equals(getValue())) return false;
			}
		}
		else
		{
			if (!dataElementTemplate.getValue().equals(getValue())) return false;
		}
		return matches;
	}
	public boolean matchesIdUnitType(LegoData dataElementTemplate) throws LinacLegoException
	{
		boolean matches = true;
		if (!dataElementTemplate.getId().equals(getId())) return false;
		if (dataElementTemplate.getUnit() != null)
			if (!dataElementTemplate.getUnit().equals(getUnit())) return false;
		if (dataElementTemplate.getType() != null)
			if (!dataElementTemplate.getType().equals(getType())) return false;
		return matches;
	}
	public static LegoData findLegoDataById(ArrayList<LegoData> legoDataList, String id)
	{
		int icount = 0; 
		while (icount < legoDataList.size())
		{
			if (id.equals(legoDataList.get(icount).getId()))
			{
				return legoDataList.get(icount);
			}
			icount = icount + 1;
		}
		return null;
		
	}
}
