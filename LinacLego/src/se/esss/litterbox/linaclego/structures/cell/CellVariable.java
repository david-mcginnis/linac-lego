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
package se.esss.litterbox.linaclego.structures.cell;

import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.matcher.CellMatch;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class CellVariable 
{
	private SimpleXmlReader variableTag;
	private CellModel cellModel;
	private  ArrayList<CellVariableLocation> variableLocations = new ArrayList<CellVariableLocation>();

	public SimpleXmlReader getVariableTag() {return variableTag;}
	public CellModel getCellModel() {return cellModel;}
	public ArrayList<CellVariableLocation> getVariableLocations() {return variableLocations;}

	public CellVariable(SimpleXmlReader variableTag, CellModel cellModel) throws LinacLegoException
	{
		this.variableTag = variableTag;
		this.cellModel = cellModel;
		findVariableLocations();
	}
	String getVariableValueFromSlotMatch(CellMatch cellMatch) throws LinacLegoException
	{
		try 
		{
			String dataValue = "";
			if (variableLocations.size() < 1) return dataValue;
			int islot = variableLocations.get(0).getNslot();
			String dataId = variableLocations.get(0).getDataId();
			SimpleXmlReader dataTags = cellMatch.getMatchingSlotElements().get(islot).tagsByName("d");
			if (dataTags.numChildTags() > 0)
			{
				int idata = 0;
				boolean valueFound = false;
				while (!valueFound && (idata < dataTags.numChildTags()))
				{
					String id = dataTags.tag(idata).attribute("id");
					if (dataId.equals(id))
					{
						valueFound = true;
						dataValue = dataTags.tag(idata).getCharacterData();
					}
					else
					{
						idata = idata + 1;
					}
				}
			}
			return dataValue;
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public Element getVariableElement(CellMatch cellMatch, Document xdoc) throws LinacLegoException
	{
		Element data = xdoc.createElement("d");
		try 
		{
			data.setAttribute("id",variableTag.attribute("id"));
			data.setAttribute("type",variableTag.attribute("type"));
			data.setTextContent(getVariableValueFromSlotMatch(cellMatch));
			return data;
		} catch (DOMException e) {throw new LinacLegoException(e);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	private void findVariableLocations() throws LinacLegoException
	{
		try 
		{
			SimpleXmlReader cellModelSlots = cellModel.getTag().tagsByName("slot");
			if (cellModelSlots.numChildTags() > 0)
			{
				for (int islot = 0; islot < cellModelSlots.numChildTags(); ++islot)
				{
					SimpleXmlReader dataTags = cellModelSlots.tag(islot).tagsByName("d");
					if (dataTags.numChildTags() > 0)
					{
						for (int idata = 0; idata < dataTags.numChildTags(); ++idata)
						{
							if (variableTag.attribute("id").equals(dataTags.tag(idata).getCharacterData()))
							{
								variableLocations.add(new CellVariableLocation(islot, dataTags.tag(idata).attribute("id"), this));
//								cellModel.getLinacLego().writeStatus(cellModel.getCellModelTag().attribute("id") + " " + variableTag.attribute("id") + " Variable found at slot " + islot + " dataElement " + dataTags.tag(idata).attribute("id"));
							}
						}
					}
				}
			}
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}

}
