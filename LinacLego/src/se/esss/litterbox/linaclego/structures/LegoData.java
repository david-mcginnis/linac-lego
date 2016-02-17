/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.linaclego.structures;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.legoset.LegoSet;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoData 
{
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
	public void writeTag(SimpleXmlWriter xw) throws LinacLegoException
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
	public boolean matchesDataElementModel(LegoData dataElementTemplate) throws LinacLegoException
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
}
