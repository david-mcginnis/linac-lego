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
package se.esss.litterbox.linaclego.webapp.server;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.cell.Cell;
import se.esss.litterbox.linaclego.structures.legomonitor.LegoMonitor;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.linaclego.webapp.shared.HtmlTextTree;

public class LinacLegoPbsHtmlTextTree 
{
	private HtmlTextTree htmlTextTree;
	
	public static final DecimalFormat threePlaces = new DecimalFormat("###.###");
	
	public HtmlTextTree getHtmlTextTree() {return htmlTextTree;}
	public LinacLegoPbsHtmlTextTree()
	{
		htmlTextTree = new HtmlTextTree();
	}
	public void add(LinacLegoPbsHtmlTextTree linacLegoPbsHtmlTextTree)
	{
		htmlTextTree.add(linacLegoPbsHtmlTextTree.getHtmlTextTree());
	}
	public LinacLegoPbsHtmlTextTree(LinacLego linacLego, String tagStyle, String attLabelStyle, String attValueStyle, String attWhiteSpaceStyle) 
	{
		this();
		htmlTextTree.setTagStyle(tagStyle);
		htmlTextTree.setAttLabelStyle(attLabelStyle);
		htmlTextTree.setAttValueStyle(attValueStyle);
		htmlTextTree.setAttWhiteSpaceStyle(attWhiteSpaceStyle);
		
		htmlTextTree.setIconImageLocation("images/lego.jpg");
		htmlTextTree.setTag("linacLego");
		htmlTextTree.addAttribute("title", linacLego.getLinacLegoTitle(), 1);
		htmlTextTree.addAttribute("revNo", linacLego.getLinacLegoRevNo(), 1);
		htmlTextTree.addAttribute("rev Date", linacLego.getLinacLegoRevDate(), 1);
		htmlTextTree.addAttribute("rev Comment", linacLego.getLinacLegoRevComment(), 1);
		
	}
	public void inheritStyles(LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree)
	{
		htmlTextTree.inheritStyles(parentLinacLegoPbsHtmlTextTree.getHtmlTextTree());
	}
	public LinacLegoPbsHtmlTextTree(LegoData dataElement, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation(null);
		htmlTextTree.setTag("data");
		htmlTextTree.addAttribute("id", dataElement.getId(), 15);
		if (dataElement.getType() != null)
		{
			htmlTextTree.addAttribute("type", dataElement.getType(), 10);
		}
		if (dataElement.getUnit() != null)
		{
			htmlTextTree.addAttribute("unit", dataElement.getUnit(), 10);
		}
		if (dataElement.getValue() != null)
		{
			htmlTextTree.addAttribute("value", dataElement.getValue(), 15);
		}
		if (dataElement.getLegoSet() != null)
		{
			htmlTextTree.addAttribute("LegoSet", dataElement.getLegoSet().getDevName(), 25);
		}
	}
	public LinacLegoPbsHtmlTextTree(Linac linac, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/linac.jpg");
		htmlTextTree.setTag("linac");
		htmlTextTree.addAttribute("id", linac.getLinacLego().getLinacLegoTitle(), 25);
		htmlTextTree.addAttribute("address", linac.getLinacLego().getLinacLegoTitle(), 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(linac.geteVout() / 1.0e+06)  + " MeV", 1);
		htmlTextTree.addAttribute("length", threePlaces.format(linac.getLength())  + " m"   + " MeV", 1);
		
		makeDataFolder(linac.getDataElementList());
	}
	private void makeDataFolder(ArrayList<LegoData> dataElementList) throws LinacLegoException
	{
		HtmlTextTree dataFolderHtmlTextTree = new HtmlTextTree();
		dataFolderHtmlTextTree.inheritStyles(getHtmlTextTree());
		dataFolderHtmlTextTree.setIconImageLocation("images/data.png");
		dataFolderHtmlTextTree.setTag("data");
		htmlTextTree.setDataFolder(dataFolderHtmlTextTree);

		for (int idata = 0; idata < dataElementList.size(); ++idata)
		{
			LinacLegoPbsHtmlTextTree dataElement = new LinacLegoPbsHtmlTextTree(dataElementList.get(idata), this);
			dataFolderHtmlTextTree.add(dataElement.htmlTextTree);
		}
	}
	public LinacLegoPbsHtmlTextTree(Section section, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{

		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/section.jpg");
		htmlTextTree.setTag("section");
		htmlTextTree.addAttribute("id", section.getId(), 15);
		htmlTextTree.addAttribute("address", section.getAddress(), 25);
		htmlTextTree.addAttribute("rfHarmonic", Integer.toString(section.getRfHarmonic()), 1);
		htmlTextTree.addAttribute("energy", threePlaces.format(section.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(section.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(section.getLocalEndZ()) + "m", 9);
		
	}
	public LinacLegoPbsHtmlTextTree(Cell cell, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/cell.jpg");
		htmlTextTree.setTag("cell");
		htmlTextTree.addAttribute("id", cell.getId(), 15);
		htmlTextTree.addAttribute("address", cell.getAddress(), 25);
		String cellModelId = cell.getModelId();
		if (cellModelId == null) cellModelId = "none";
		htmlTextTree.addAttribute("model", cellModelId, 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(cell.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(cell.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(cell.getLocalEndZ()) + "m", 9);
		
	}
	public LinacLegoPbsHtmlTextTree(Slot slot, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/slots.jpg");
		htmlTextTree.setTag("slot");
		htmlTextTree.addAttribute("id", slot.getId(), 15);
		htmlTextTree.addAttribute("address", slot.getAddress(), 25);
		String slotModelId = slot.getModelId();
		if (slotModelId == null) slotModelId = "none";
		htmlTextTree.addAttribute("model", slotModelId, 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(slot.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(slot.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(slot.getLocalEndZ()) + "m", 9);
		
	}
	public LinacLegoPbsHtmlTextTree(BeamLineElement ble, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("");
		if (ble.getType().equals("quad")) htmlTextTree.setIconImageLocation("images/quad.jpg");
		if (ble.getType().equals("drift")) htmlTextTree.setIconImageLocation("images/drift.png");
		if (ble.getType().equals("rfGap")) htmlTextTree.setIconImageLocation("images/buncher.jpg");
		if (ble.getType().equals("fieldMap")) htmlTextTree.setIconImageLocation("images/elliptical.jpg");
		if (ble.getType().equals("bend")) htmlTextTree.setIconImageLocation("images/bend.jpg");
		if (ble.getType().equals("thinSteering")) htmlTextTree.setIconImageLocation("images/triangle.jpg");
		if (ble.getType().equals("dtlDriftTube")) htmlTextTree.setIconImageLocation("images/driftTube.jpg");
		if (ble.getType().equals("dtlRfGap")) htmlTextTree.setIconImageLocation("images/buncher.jpg");
		if (ble.getType().equals("monitor"))
		{
			String monitorType = ble.getDataElement("monitorType").getValue().toUpperCase();
			if(monitorType.equals("BPM")) htmlTextTree.setIconImageLocation("images/bpm.jpg");
			if(monitorType.equals("BLM")) htmlTextTree.setIconImageLocation("images/blm.jpg");
			if(monitorType.equals("BCM")) htmlTextTree.setIconImageLocation("images/bcm.jpg");
			if(monitorType.equals("BSM")) htmlTextTree.setIconImageLocation("images/bsm.jpg");
			if(monitorType.equals("NPM")) htmlTextTree.setIconImageLocation("images/npm.jpg");
			if(monitorType.equals("WSC")) htmlTextTree.setIconImageLocation("images/wsc.jpg");
			if(monitorType.equals("FCUP")) htmlTextTree.setIconImageLocation("images/fcup.jpg");
			if(monitorType.equals("GTV")) htmlTextTree.setIconImageLocation("images/gtv.jpg");
			if(monitorType.equals("COL")) htmlTextTree.setIconImageLocation("images/col.jpg");
			if(monitorType.equals("GRD")) htmlTextTree.setIconImageLocation("images/grd.jpg");
			if(monitorType.equals("SLIT")) htmlTextTree.setIconImageLocation("images/slit.jpg");
		}
		
		htmlTextTree.setTag("ble");
		htmlTextTree.addAttribute("id", ble.getId(), 15);
		htmlTextTree.addAttribute("address", ble.getAddress(), 25);
		String devName = "none";
		if (ble.deviceName().length() > 2) devName = ble.deviceName();
		htmlTextTree.addAttribute("name",  devName, 25);
		htmlTextTree.addAttribute("model", ble.getModel(), 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(ble.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(ble.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-cent", threePlaces.format(ble.getLocalCenterZ()) + "m", 9);
		makeDataFolder(ble.getDataElementList());
	}
	public LinacLegoPbsHtmlTextTree(LegoMonitor lmon, LinacLegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/slots.jpg");
		String monitorType = lmon.getType().toUpperCase();
		if(monitorType.equals("BPM")) htmlTextTree.setIconImageLocation("images/bpm.jpg");
		if(monitorType.equals("BLM")) htmlTextTree.setIconImageLocation("images/blm.jpg");
		if(monitorType.equals("BCM")) htmlTextTree.setIconImageLocation("images/bcm.jpg");
		if(monitorType.equals("BSM")) htmlTextTree.setIconImageLocation("images/bsm.jpg");
		if(monitorType.equals("NPM")) htmlTextTree.setIconImageLocation("images/npm.jpg");
		if(monitorType.equals("WSC")) htmlTextTree.setIconImageLocation("images/wsc.jpg");
		if(monitorType.equals("FCUP")) htmlTextTree.setIconImageLocation("images/fcup.jpg");
		if(monitorType.equals("GTV")) htmlTextTree.setIconImageLocation("images/gtv.jpg");
		if(monitorType.equals("COL")) htmlTextTree.setIconImageLocation("images/col.jpg");
		if(monitorType.equals("GRD")) htmlTextTree.setIconImageLocation("images/grd.jpg");
		if(monitorType.equals("SLIT")) htmlTextTree.setIconImageLocation("images/slit.jpg");
		
		htmlTextTree.setTag("mon");
		htmlTextTree.addAttribute("id", lmon.getId(), 15);
		htmlTextTree.addAttribute("address", lmon.getAddress(), 25);
		String devName = "none";
		if (lmon.deviceName().length() > 2) devName = lmon.deviceName();
		htmlTextTree.addAttribute("name",  devName, 25);
		makeDataFolder(lmon.getDataElementList());
	}

}
