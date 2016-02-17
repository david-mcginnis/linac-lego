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

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.legoset.LegoSet;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class Cell 
{
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	private SimpleXmlReader tag;
	private Section section;
	
	private ArrayList<LegoData> variableList = new ArrayList<LegoData>();
	ArrayList<Slot> slotList = new ArrayList<Slot>();
	private int numBeamLineElements = 0;
	private double length = 0.0;
	private int index  = -1;
	private SimpleXmlReader modelTag = null;
	
	public Cell(SimpleXmlReader tag, Section section, int index) throws SimpleXmlException, LinacLegoException 
	{
		this.tag = tag;
		this.section = section;
		this.index = index;
		expand();
	}
	public void expand() throws SimpleXmlException, LinacLegoException
	{
		SimpleXmlReader slotTagList = null;
		numBeamLineElements = 0;
		length = 0.0;
		if (getModelId() != null)
		{
			SimpleXmlReader dataElements = tag.tagsByName("d");
			int numDataTags = dataElements.numChildTags();
			modelTag = getMatchingModelTag(tag.attribute("model"));
			SimpleXmlReader variableDef =  modelTag.tagsByName("var");
			
			for (int ivar = 0; ivar < variableDef.numChildTags(); ++ivar)
			{
				String varId = variableDef.tag(ivar).attribute("id");
				String varType = variableDef.tag(ivar).attribute("type");
				String varValue = null;
				LegoSet matchingLegoSet = null;
				if (numDataTags > 0)
				{
					int itag = 0;
					while (itag < numDataTags)
					{
						SimpleXmlReader dataTag = dataElements.tag(itag);
						if (varId.equals(dataTag.attribute("id")))
						{
							varValue = dataTag.getCharacterData();
							matchingLegoSet = LegoSet.getMatchingLegoSet(getSection().getId(), this.getId(), null, null, varId, getLinacLego());
							itag = numDataTags;
						}
						itag = itag + 1;
					}
				}
				LegoData legoData = new LegoData(varId, varValue, varType, null);
				legoData.setLegoSet(matchingLegoSet);
				variableList.add(legoData);
			}
			slotTagList =  modelTag.tagsByName("slot");
		}
		else
		{
			slotTagList = tag.tagsByName("slot");
			if ((slotTagList.numChildTags() - tag.numChildTags()) != 0 )
			{
				throw new LinacLegoException("Only slot tags allowed inside cell tag");
			}
		}
		for (int islot = 0; islot < slotTagList.numChildTags(); ++islot)
		{
			SimpleXmlReader slotTag = slotTagList.tag(islot);
			Slot slot  = new Slot(slotTag, this, islot);
			slotList.add(slot);
			numBeamLineElements = numBeamLineElements + slot.getNumBeamLineElements();
			length = length + slot.getLength();
		}
	}
	public LegoData getVariableLegoData(String variableId) throws LinacLegoException
	{
		String tempVarId = variableId;
		if (variableId.charAt(0) == '#') tempVarId = variableId.substring(1);
		for (int ii = 0; ii < variableList.size(); ++ii  )
		{
			LegoData a =  variableList.get(ii);
			if (a.getId().equals(tempVarId)) return a;
		}
		return null;
	}
	public void printLegoSets(PrintWriter pw) throws LinacLegoException 
	{
		for (int islots = 0; islots < slotList.size(); ++islots)
		{
			slotList.get(islots).printLegoSets(pw);
		}
	}
	public void printCellTable(PrintWriter pw) throws LinacLegoException
	{
		double[] surveyCoords = getSection().getLinac().getSurveyCoords(getEndPosVec());
		pw.print(getSection().getId());
		pw.print("," + getId());
		if (!(getModelId() == null))
		{
			pw.print(" ," + getModelId());
		}
		else
		{
			pw.print(" ," + "");
		}
		pw.print(" ," + sixPlaces.format((geteVout() / 1.0e6)));
		pw.print(" ," + sixPlaces.format(getBetaOut()));
		pw.print(" ," + sixPlaces.format(getLength()));
		pw.print(" ," + sixPlaces.format(getEndPosVec()[0]));
		pw.print(" ," + sixPlaces.format(getEndPosVec()[1]));
		pw.print(" ," + sixPlaces.format(getEndPosVec()[2]));
		pw.print(" ," + sixPlaces.format(surveyCoords[0]));
		pw.print(" ," + sixPlaces.format(surveyCoords[1]));
		pw.print(" ," + sixPlaces.format(surveyCoords[2]));
		pw.println("");
	}
	private SimpleXmlReader getMatchingModelTag(String cellModelId) throws SimpleXmlException 
	{
		int numModels = section.getLinac().getLinacLego().getCellModelList().size();
		int imodel = 0;
		while (imodel < numModels)
		{
			SimpleXmlReader cellModelTag = section.getLinac().getLinacLego().getCellModelList().get(imodel).getTag();
			if (cellModelId.equals(cellModelTag.attribute("id")))
			{
				return cellModelTag;
			}
			imodel = imodel + 1;
		}
		return null;
	}
	public double geteVin()
	{
		return slotList.get(0).geteVin();
	}
	public double geteVout()
	{
		return slotList.get(slotList.size() - 1).geteVout();
	}
	public double getLocalBeginZ() 
	{
		return slotList.get(0).getLocalBeginZ();
	}
	public double getLocalEndZ() 
	{
		return slotList.get(slotList.size() - 1).getLocalEndZ();
	}
	public Slot getSlot(String slotId) throws LinacLegoException
	{
		Slot matchingSlot = null;
		for (int islot = 0; islot < slotList.size(); ++islot)
		{
			if (slotList.get(islot).getId().equals(slotId)) 
				matchingSlot = slotList.get(islot);
		}
		return matchingSlot;
	}
	public Section getSection() {return section;}
	public Linac getLinac() {return getSection().getLinac();}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	public String getModelId() 
	{
		try {return tag.attribute("model");} catch (SimpleXmlException e) {return null;}
	}
	public String getId() throws LinacLegoException
	{
		try {return tag.attribute("id");} 
		catch (SimpleXmlException e) { throw new LinacLegoException("Cell: " + e.getMessage());}
	}
	public String getAddress() 
	{
		String id = "";
		try {
			id = section.getAddress() + "-" + getId();
		} 
		catch (LinacLegoException e) {id = "";} 
		return id;
	}
	public ArrayList<LegoData> getVariableList() {return variableList;}
	public ArrayList<Slot> getSlotList() {return slotList;}
	public int getNumBeamLineElements() {return numBeamLineElements;}
	public double getLength() {return length;}
	public int getIndex() {return index;}
	public int getNumOfSlots() {return slotList.size();}
	public SimpleXmlReader getModelTag() {return modelTag;}
	public SimpleXmlReader geTag() {return tag;}
	public double[] getEndPosVec() {return slotList.get(getNumOfSlots() - 1).getEndPosVec();}
	public double getBetaOut() {return BeamLineElement.beta(geteVout());}

}
