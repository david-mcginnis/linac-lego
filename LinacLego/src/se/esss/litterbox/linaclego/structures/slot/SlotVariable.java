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
package se.esss.litterbox.linaclego.structures.slot;

import java.util.ArrayList;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.matcher.SlotMatch;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class SlotVariable 
{
	private SimpleXmlReader variableTag;
	private SlotModel slotModel;
	private  ArrayList<SlotVariableLocation> variableLocations = new ArrayList<SlotVariableLocation>();

	public SimpleXmlReader getVariableTag() {return variableTag;}
	public SlotModel getSlotModel() {return slotModel;}
	public ArrayList<SlotVariableLocation> getVariableLocations() {return variableLocations;}

	public SlotVariable(SimpleXmlReader variableTag, SlotModel slotModel) throws LinacLegoException
	{
		this.variableTag = variableTag;
		this.slotModel = slotModel;
		findVariableLocations();
	}
	String getVariableValueFromSlotMatch(SlotMatch slotMatch) throws LinacLegoException
	{
		try 
		{
			String dataValue = "";
			if (variableLocations.size() < 1) return dataValue;
			int ible = variableLocations.get(0).getNble();
			String dataId = variableLocations.get(0).getDataId();
			SimpleXmlReader dataTags = slotMatch.getMatchingBleElements().get(ible).tagsByName("d");
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
	public Element getVariableElement(SlotMatch slotMatch, Document xdoc) throws LinacLegoException
	{
		Element data = xdoc.createElement("d");
		try 
		{
			data.setAttribute("id",variableTag.attribute("id"));
			data.setAttribute("type",variableTag.attribute("type"));
			data.setTextContent(getVariableValueFromSlotMatch(slotMatch));
			return data;
		} catch (DOMException e) {throw new LinacLegoException(e);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	private void findVariableLocations() throws LinacLegoException
	{
		try 
		{
			SimpleXmlReader slotModelBleTag = slotModel.getTag().tagsByName("ble");
			if (slotModelBleTag.numChildTags() > 0)
			{
				for (int ible = 0; ible < slotModelBleTag.numChildTags(); ++ible)
				{
					SimpleXmlReader dataTags = slotModelBleTag.tag(ible).tagsByName("d");
					if (dataTags.numChildTags() > 0)
					{
						for (int idata = 0; idata < dataTags.numChildTags(); ++idata)
						{
							if (variableTag.attribute("id").equals(dataTags.tag(idata).getCharacterData()))
							{
								variableLocations.add(new SlotVariableLocation(ible, dataTags.tag(idata).attribute("id"), this));
//								slotModel.getLinacLego().writeStatus(slotModel.getSlotModelTag().attribute("id") + " " + variableTag.attribute("id") + " Variable found at ble " + ible + " dataElement " + dataTags.tag(idata).attribute("id"));
							}
						}
					}
				}
			}
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}

}
