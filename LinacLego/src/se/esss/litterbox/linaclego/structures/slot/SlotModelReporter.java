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

import java.io.PrintWriter;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;

public class SlotModelReporter 
{
	ArrayList<ModelList> modelListList = new ArrayList<ModelList>();
	public SlotModelReporter(Linac linac) throws LinacLegoException
	{
		for (int isec = 0; isec < linac.getSectionList().size(); ++isec)
		{
			for (int icell = 0; icell < linac.getSectionList().get(isec).getCellList().size(); ++icell)
			{
				for (int islot = 0; islot < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().size(); ++islot)
				{
					addModel(linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot));
				}
			}
		}
	}
	public ModelList getModel(Slot element) throws LinacLegoException
	{
		int icollection = 0;
		while (icollection < modelListList.size())
		{
			if (modelListList.get(icollection).matchesModelAndType(element)) return modelListList.get(icollection);
			icollection = icollection + 1;
		}
		return null;
	}
	private void addModel(Slot element) throws LinacLegoException
	{
		ModelList modelList = getModel(element);
		if (modelList != null)
		{
			modelList.getElementList().add(element);
		}
		else
		{
			modelListList.add(new ModelList(element));
		}
	}
	public void printModels(PrintWriter pw, Linac linac) throws LinacLegoException
	{
		for (int imodel = 0; imodel < modelListList.size(); ++imodel)
		{
			pw.println(modelListList.get(imodel).printRowOfPartCounts(modelListList.get(imodel).sortByLinac(linac)));
		}
	}
	class ModelList 
	{
		String modelId = "-";
		String typeId = "slot";
		ArrayList<Slot> elementList = new ArrayList<Slot>();
		ModelList(Slot element) throws LinacLegoException
		{
			String elementModelId = element.getModelId();
			if (elementModelId == null) elementModelId = "-";
			this.modelId = elementModelId;
			elementList = new ArrayList<Slot>();
			elementList.add(element);
		}
		private boolean matchesModelAndType(Slot element) throws LinacLegoException
		{
			String elementModelId = element.getModelId();
			if (elementModelId == null) elementModelId = "-";
			if (!this.modelId.equals(elementModelId)) return false;
			if (!this.typeId.equals("slot")) return false;
			return true;
		}
		private ArrayList<Slot> sortBySection(Section section)
		{
			ArrayList<Slot> elementListForSection = new ArrayList<Slot>();
			for (int ii = 0; ii < elementList.size(); ++ii)
			{
				if (elementList.get(ii).getCell().getSection().equals(section)) elementListForSection.add(elementList.get(ii));
			}
			return elementListForSection;
		}
		private  ArrayList<ArrayList<Slot>> sortByLinac(Linac linac)
		{
			ArrayList<ArrayList<Slot>> elementListForLinac = new ArrayList<ArrayList<Slot>>();
			for (int isection = 0; isection < linac.getNumOfSections(); ++isection)
			{
				elementListForLinac.add(sortBySection(linac.getSectionList().get(isection)));
			}
			elementListForLinac.add(elementList);
			return elementListForLinac;
		}
		private String printRowOfPartCounts(ArrayList<ArrayList<Slot>> elementListForLinac)
		{
			String rowString = "";
			rowString = "slot" + "," + modelId;
			for (int isection = 0; isection < elementListForLinac.size(); ++isection)
			{
				rowString = rowString + "," + elementListForLinac.get(isection).size();
			}
			return rowString;
		}
		String getModelId() {return modelId;}
		String getTypeId() {return typeId;}
		ArrayList<Slot> getElementList() {return elementList;}
	}
}
