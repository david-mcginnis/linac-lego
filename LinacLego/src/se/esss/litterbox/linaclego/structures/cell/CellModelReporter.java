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
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;

public class CellModelReporter 
{
	ArrayList<ModelList> modelListList = new ArrayList<ModelList>();
	public CellModelReporter(Linac linac) throws LinacLegoException
	{
		for (int isec = 0; isec < linac.getSectionList().size(); ++isec)
		{
			for (int icell = 0; icell < linac.getSectionList().get(isec).getCellList().size(); ++icell)
			{
				addModel(linac.getSectionList().get(isec).getCellList().get(icell));
			}
		}
	}
	public ModelList getModel(Cell element) throws LinacLegoException
	{
		int icollection = 0;
		while (icollection < modelListList.size())
		{
			if (modelListList.get(icollection).matchesModelAndType(element)) return modelListList.get(icollection);
			icollection = icollection + 1;
		}
		return null;
	}
	private void addModel(Cell element) throws LinacLegoException
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
		String typeId = "cell";
		ArrayList<Cell> elementList = new ArrayList<Cell>();
		ModelList(Cell element) throws LinacLegoException
		{
			String elementModelId = element.getModelId();
			if (elementModelId == null) elementModelId = "-";
			this.modelId = elementModelId;
			elementList = new ArrayList<Cell>();
			elementList.add(element);
		}
		private boolean matchesModelAndType(Cell element) throws LinacLegoException
		{
			String elementModelId = element.getModelId();
			if (elementModelId == null) elementModelId = "-";
			if (!this.modelId.equals(elementModelId)) return false;
			if (!this.typeId.equals("cell")) return false;
			return true;
		}
		private ArrayList<Cell> sortBySection(Section section)
		{
			ArrayList<Cell> elementListForSection = new ArrayList<Cell>();
			for (int ii = 0; ii < elementList.size(); ++ii)
			{
				if (elementList.get(ii).getSection().equals(section)) elementListForSection.add(elementList.get(ii));
			}
			return elementListForSection;
		}
		private  ArrayList<ArrayList<Cell>> sortByLinac(Linac linac)
		{
			ArrayList<ArrayList<Cell>> elementListForLinac = new ArrayList<ArrayList<Cell>>();
			for (int isection = 0; isection < linac.getNumOfSections(); ++isection)
			{
				elementListForLinac.add(sortBySection(linac.getSectionList().get(isection)));
			}
			elementListForLinac.add(elementList);
			return elementListForLinac;
		}
		private String printRowOfPartCounts(ArrayList<ArrayList<Cell>> elementListForLinac)
		{
			String rowString = "cell" + "," + modelId;
			for (int isection = 0; isection < elementListForLinac.size(); ++isection)
			{
				rowString = rowString + "," + elementListForLinac.get(isection).size();
			}
			return rowString;
		}
		String getModelId() {return modelId;}
		String getTypeId() {return typeId;}
		ArrayList<Cell> getElementList() {return elementList;}
	}
}
