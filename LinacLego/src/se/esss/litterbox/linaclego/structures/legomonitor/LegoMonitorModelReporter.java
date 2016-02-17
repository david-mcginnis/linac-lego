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
package se.esss.litterbox.linaclego.structures.legomonitor;

import java.io.PrintWriter;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;

public class LegoMonitorModelReporter 
{
	ArrayList<ModelList> modelListList = new ArrayList<ModelList>();
	public LegoMonitorModelReporter(Linac linac) throws LinacLegoException
	{
		for (int isec = 0; isec < linac.getSectionList().size(); ++isec)
		{
			for (int icell = 0; icell < linac.getSectionList().get(isec).getCellList().size(); ++icell)
			{
				for (int islot = 0; islot < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().size(); ++islot)
				{
					for (int ible = 0; ible < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().size(); ++ible)
					{
						for (int imon = 0; imon < linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().get(ible).getLegoMonitorList().size(); ++imon)
						{
							addModel(linac.getSectionList().get(isec).getCellList().get(icell).getSlotList().get(islot).getBeamLineElementList().get(ible).getLegoMonitorList().get(imon));
						}
					}
				}
			}
		}
	}
	public ModelList getModel(LegoMonitor element) throws LinacLegoException
	{
		int icollection = 0;
		while (icollection < modelListList.size())
		{
			if (modelListList.get(icollection).matchesModelAndType(element)) return modelListList.get(icollection);
			icollection = icollection + 1;
		}
		return null;
	}
	private void addModel(LegoMonitor element) throws LinacLegoException
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
		String modelId;
		String discId ;
		ArrayList<LegoMonitor> elementList = new ArrayList<LegoMonitor>();
		ModelList(LegoMonitor element) throws LinacLegoException
		{
			this.modelId = element.getModel();
			this.discId = element.getDiscipline();
			elementList = new ArrayList<LegoMonitor>();
			elementList.add(element);
		}
		private boolean matchesModelAndType(LegoMonitor element) throws LinacLegoException
		{
			if (!this.modelId.equals(element.getModel())) return false;
			if (!this.discId.equals(element.getDiscipline())) return false;
			return true;
		}
		private ArrayList<LegoMonitor> sortBySection(Section section)
		{
			ArrayList<LegoMonitor> elementListForSection = new ArrayList<LegoMonitor>();
			for (int ii = 0; ii < elementList.size(); ++ii)
			{
				if (elementList.get(ii).getBeamLineElement().getSlot().getCell().getSection().equals(section)) elementListForSection.add(elementList.get(ii));
			}
			return elementListForSection;
		}
		private  ArrayList<ArrayList<LegoMonitor>> sortByLinac(Linac linac)
		{
			ArrayList<ArrayList<LegoMonitor>> elementListForLinac = new ArrayList<ArrayList<LegoMonitor>>();
			for (int isection = 0; isection < linac.getNumOfSections(); ++isection)
			{
				elementListForLinac.add(sortBySection(linac.getSectionList().get(isection)));
			}
			elementListForLinac.add(elementList);
			return elementListForLinac;
		}
		private String printRowOfPartCounts(ArrayList<ArrayList<LegoMonitor>> elementListForLinac)
		{
			String rowString = discId + "," + modelId;
			for (int isection = 0; isection < elementListForLinac.size(); ++isection)
			{
				rowString = rowString + "," + elementListForLinac.get(isection).size();
			}
			return rowString;
		}
		String getModelId() {return modelId;}
		String getTypeId() {return discId;}
		ArrayList<LegoMonitor> getElementList() {return elementList;}
	}
}
