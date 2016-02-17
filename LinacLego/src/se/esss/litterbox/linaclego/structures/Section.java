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

import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.cell.Cell;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;


public class Section 
{
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final double cvel = 299792458.0;
	public static final String space = "\t";
	public static final String newline = System.getProperty("line.separator");
	SimpleXmlReader tag;
	Linac linac;
	
	ArrayList<Cell> cellList = new ArrayList<Cell>();
	private double rfFreqMHz = 0.0;
	private int rfHarmonic = 1;
	private double lamda = 0.0;
	private double length = 0.0;
	private int index = -1;
	private boolean periodicLatticeSection = false;
	
	public Section(SimpleXmlReader tag, Linac linac, int index) throws SimpleXmlException, LinacLegoException
	{
		this.tag = tag;
		this.linac = linac;
		this.index = index;
		rfHarmonic = Integer.parseInt(tag.attribute("rfHarmonic"));
		rfFreqMHz = ((double) rfHarmonic) * linac.getBeamFrequencyMHz();
		lamda = cvel / (rfFreqMHz * 1.0e+06);

		length = 0.0;
		if (getType() != null)
		{
			if (getType().equals("periodic"))
			{
				periodicLatticeSection = true;
			}
		}
		for (int icell = 0; icell < tag.numChildTags(); ++icell)
		{
			SimpleXmlReader cellTag = tag.tag(icell);
			
			if (cellTag.tagName().equals("cell")) 
			{
				Cell cell  = new Cell(cellTag, this, icell);
				cellList.add(cell);
				setLength(getLength() + cell.getLength());
			}
			else
			{
				throw new SimpleXmlException("Only cell tags allowed inside latticeSection tag");
			}
		}
	}
	public double geteVin()
	{
		return cellList.get(0).geteVin();
	}
	public double geteVout()
	{
		return cellList.get(cellList.size() - 1).geteVout();
	}
	public double getLocalBeginZ() 
	{
		return cellList.get(0).getLocalBeginZ();
	}
	public double getLocalEndZ() 
	{
		return cellList.get(cellList.size() - 1).getLocalEndZ();
	}
	public String traceWinCommand() 
	{
		String command = "";
		if (!periodicLatticeSection)
		{
			if (index > 0)
			{
				if (linac.getSectionList().get(index - 1).isPeriodicLatticeSection())
				{
					command = "LATTICE_END" + newline;
				}
			}
		}
		command = command + "FREQ";
		command = command + space + Double.toString(getRfFreqMHz());
		if (periodicLatticeSection)
		{
			command = command + newline + "LATTICE";
			command = command + space + Integer.toString(cellList.get(0).getNumBeamLineElements()) + space + "0";
		}
		command = command + newline;
		return command;
	}
	public void printLegoSets(PrintWriter pw) throws LinacLegoException 
	{
		for (int icell = 0; icell < cellList.size(); ++icell)
		{
			cellList.get(icell).printLegoSets(pw);
		}
	}
	public void printSectionTable(PrintWriter pw) throws LinacLegoException
	{
		double[] surveyCoords = getLinac().getSurveyCoords(getEndPosVec());
		pw.print(getId());
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
	public String getType() 
	{
		try {return tag.attribute("type");} catch (SimpleXmlException e) {return null;}
	}
	public String getId() throws LinacLegoException
	{
		try {return tag.attribute("id");} 
		catch (SimpleXmlException e) { throw new LinacLegoException("Section: " + e.getMessage());}
	}
	public double getRfFreqMHz() {return rfFreqMHz;}
	public double getLamda() {return lamda;}
	public ArrayList<Cell> getCellList() {return cellList;}
	public double getLength() {return length;}
	public int getIndex() {return index;}
	public int getNumOfCells() {return cellList.size();}
	public int getRfHarmonic() {return rfHarmonic;}
	public boolean isPeriodicLatticeSection() {return periodicLatticeSection;}
	
	public Cell getCell(String cellId) throws LinacLegoException 
	{
		Cell matchingCell = null;
		for (int icell = 0; icell < cellList.size(); ++icell)
		{
			if (cellList.get(icell).getId().equals(cellId)) 
				matchingCell = cellList.get(icell);
		}
		return matchingCell;
	}
	public String getAddress() 
	{
		String id = "";
		try 
		{
			id = getId();
		} 
		catch (LinacLegoException e) {id = "";} 
		return id;
	}

	public Linac getLinac() {return linac;}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	public double[] getEndPosVec() {return getCellList().get(getNumOfCells() - 1).getEndPosVec();}
	public double getBetaOut() {return BeamLineElement.beta(geteVout());}
	
	public void setLength(double length) {this.length = length;}
	public void setRfFreqMHz(double rfFreqMHz) {this.rfFreqMHz = rfFreqMHz;}
}
