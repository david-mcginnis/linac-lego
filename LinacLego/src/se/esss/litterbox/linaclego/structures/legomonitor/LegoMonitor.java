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
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.cell.Cell;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoMonitor 
{
	public static final String newline = System.getProperty("line.separator");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final String space = "\t";
	private ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();
	private SimpleXmlReader tag;
	private int index;
	private BeamLineElement beamLineElement;
	private double[] endPosVec = {0.0, 0.0, 0.0};
	private double[] endLocalPosVec = {0.0, 0.0, 0.0};
	private String discipline = "none";
	private String id = null;
	private String model = "none";
	private String type = null;
	
	public LegoMonitor(SimpleXmlReader tag, BeamLineElement beamLineElement, int index) throws LinacLegoException
	{
		setTag(tag);
		this.beamLineElement = beamLineElement;
		this.index = index;
		checkAttributes();
		addLegoData();
		readLegoDataFromXml();
		readLegoData();
		beamLineElement.getSlot().getCell().getSection().getLinac().getLinacLego().writeStatus("     " + getEssId());
		calcLocation();	
	}
	public void checkAttributes() throws LinacLegoException
	{
		try {
			String  sectionName = tag.attribute("section");
			String cellName = tag.attribute("cell");
			String slotName = tag.attribute("slot");
			String bleName = tag.attribute("ble");
			if (!sectionName.equals(beamLineElement.getSlot().getCell().getSection().getId())) throw new LinacLegoException(getEssId() + ": Section attribute  does not match section id.");
			if (!cellName.equals(beamLineElement.getSlot().getCell().getId())) throw new LinacLegoException(getEssId() + ": Cell attribute  does not match cell id.");
			if (!slotName.equals(beamLineElement.getSlot().getId())) throw new LinacLegoException(getEssId() + ": slot attribute  does not match slot id.");
			if (!bleName.equals(beamLineElement.getId())) throw new LinacLegoException(getEssId() + ": ble attribute  does not match ble id.");
		} catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		return;
	}
	public void addLegoData() 
	{
		addLegoData("dxmm", "0.0", "double", "mm");
		addLegoData("dymm", "0.0", "double", "mm");
		addLegoData("dzmm", "0.0", "double", "mm");
	}
	public void readLegoData() throws NumberFormatException, LinacLegoException  
	{
		if (getLegoData("dxmm").getValue() != null) endLocalPosVec[0] = Double.parseDouble(getLegoData("dxmm").getValue()) * 0.001;
		if (getLegoData("dymm").getValue() != null) endLocalPosVec[1] = Double.parseDouble(getLegoData("dymm").getValue()) * 0.001;
		if (getLegoData("dzmm").getValue() != null) endLocalPosVec[2] = Double.parseDouble(getLegoData("dzmm").getValue()) * 0.001;
	}
	public void printTraceWin(PrintWriter pw) throws SimpleXmlException 
	{
		if (!beamLineElement.getSlot().getCell().getSection().getLinac().getLinacLego().isPrintLegoMonitors()) return;
		String command = ";" + getAddress()
				+ space + "dxmm=" + Double.toString(endLocalPosVec[0] * 1000.0)
				+ space + "dymm=" + Double.toString(endLocalPosVec[1] * 1000.0)
				+ space + "dzmm=" + Double.toString(endLocalPosVec[2] * 1000.0);
		pw.println(command);
	}
	public void printXmlPbs(SimpleXmlWriter xw) throws LinacLegoException   
	{
		for (int idata = 0; idata < legoDataList.size(); ++idata)
		{
			legoDataList.get(idata).writeTag(xw);
		}
	}
	public String getAddress() 
	{
		String id = "";
		id = beamLineElement.getAddress() + "-" + getId();
		return id;
	}
	public String deviceName() 
	{
		String id = "";
		if ( getDiscipline().equals("none")) return id;
		try {
			id = beamLineElement.getSlot().getCell().getSection().getId()
					+ "-" + beamLineElement.getSlot().getCell().getId()
					+       beamLineElement.getSlot().getId()
					+ ":" + getDiscipline()
					+ "-" + getId();
		} 
		catch (LinacLegoException e) {id = "";} 
		return id;
	}
	public double[] location()
	{
		double[] posVec = beamLineElement.getBeginPosVec();
		
		for (int ii = 0; ii < 3; ++ii) posVec[ii] = posVec[ii] + endLocalPosVec[ii];
		return posVec;
	}
	public void printMonitorTable(PrintWriter pw) throws LinacLegoException 
	{
		if (!beamLineElement.getSlot().getCell().getSection().getLinac().getLinacLego().isPrintLegoMonitors()) return;
		double[] surveyCoords = beamLineElement.getSlot().getCell().getSection().getLinac().getSurveyCoords(location());
		pw.print(beamLineElement.getSlot().getCell().getSection().getId());
		pw.print("," + beamLineElement.getSlot().getCell().getId());
		pw.print("," + beamLineElement.getSlot().getId());
		pw.print("," + beamLineElement.getId());
		pw.print(" ," + getId());
		pw.print(" ," + getType());
		if (!getModel().equals("none")) pw.print(" ," + getModel());
		if ( getModel().equals("none")) pw.print(" ," + "");
		if (!getDiscipline().equals("none")) pw.print(" ," + getDiscipline());
		if ( getDiscipline().equals("none")) pw.print(" ," + "");
		pw.print(" ," + deviceName());
		pw.print(" ," + sixPlaces.format((beamLineElement.geteVout() / 1.0e6)));
		pw.print(" ," + sixPlaces.format(BeamLineElement.beta(beamLineElement.geteVout())));
		pw.print(" ," + sixPlaces.format(location()[0]));
		pw.print(" ," + sixPlaces.format(location()[1]));
		pw.print(" ," + sixPlaces.format(location()[2]));
		pw.print(" ," + sixPlaces.format(surveyCoords[0]));
		pw.print(" ," + sixPlaces.format(surveyCoords[1]));
		pw.print(" ," + sixPlaces.format(surveyCoords[2]));
		pw.println(" , ");
	}
	public String getEssId() 
	{
		String id = "";
		try {
			id = beamLineElement.getSlot().getCell().getSection().getId()
					+ "-" + beamLineElement.getSlot().getCell().getId()
					+ "-" + beamLineElement.getSlot().getId()
					+ "-" + beamLineElement.getId()
					+ "-" + getId();
		} 
		catch (LinacLegoException e) {id = "";} 
		return id;
	}
	private void calcLocation() 
	{
		double[] localOutputVec = {0.0, 0.0, 0.0};
		double[][] centerRotMat = beamLineElement.centerRotMat();
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + centerRotMat[ir][ic] * endLocalPosVec[ic];
			endPosVec[ir] = beamLineElement.centerLocation()[ir] + localOutputVec[ir];
		}
	}
	public void addLegoData(String id, String value, String type, String unit)
	{
		legoDataList.add(new LegoData(id, value, type, unit));
	}
	public int numLegoData()
	{
		return legoDataList.size();
	}
	private void readLegoDataFromXml() throws LinacLegoException
	{
		try 
		{
			discipline = tag.attribute("disc");
			id = tag.attribute("id");
			type = tag.attribute("type");
			try {model = tag.attribute("model");} catch (SimpleXmlException e) { model = "none";}
			SimpleXmlReader legoDataTags = tag.tagsByName("d");
			int numDataTags = legoDataTags.numChildTags();
			if (numDataTags < 1) return;
			for (int ii = 0; ii < numLegoData(); ++ii)
			{
				LegoData a = getDataElement(ii);
				for (int ij = 0; ij < numDataTags; ++ij)
				{
					SimpleXmlReader dataTag = legoDataTags.tag(ij);
					if (a.getId().equals(dataTag.attribute("id")))
					{
						if (!a.unitMatches(dataTag.attribute("unit"))) throw new LinacLegoException(getEssId() + " " + a.getId()  + " unit does not match required unit of " + a.getUnit());
						a.setValue(dataTag.getCharacterData());
						if (!a.valueMatchsType()) throw new LinacLegoException(getEssId() + " " + a.getId()  + " data does not match type.");
					}
				}
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(getEssId() + ":" + e.getMessage());
		}
		
	}
	public LegoData getDataElement(int ii)
	{
		return legoDataList.get(ii);
	}
	public LegoData getLegoData(String id)
	{
		for (int ii = 0; ii < numLegoData(); ++ii  )
		{
			LegoData a = getDataElement(ii);
			if (a.getId().equals(id)) return a;
		}
		return null;
	}
	public BeamLineElement getBeamLineElement() {return beamLineElement;}
	public Slot getSlot() {return getBeamLineElement().getSlot();}
	public Cell getCell() {return getSlot().getCell();}
	public Section getSection() {return getCell().getSection();}
	public Linac getLinac() {return getSection().getLinac();}
	public LinacLego getLinacLego() {return getLinac().getLinacLego();}
	public SimpleXmlReader getTag() {return tag;}
	public int getIndex() {return index;}
	public double[] getEndLocalPosVec() {return endLocalPosVec;}
	public String getId() {return id;}
	public String getType() {return type;}
	public String getDiscipline() {return discipline;}
	public String getModel() {return model;}
	public double[] getEndPosVec() {return endPosVec;}
	public ArrayList<LegoData> getDataElementList() {return legoDataList;}

	public void setTag(SimpleXmlReader tag) {this.tag = tag;}

}
