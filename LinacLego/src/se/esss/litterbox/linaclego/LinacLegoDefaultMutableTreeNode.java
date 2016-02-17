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
package se.esss.litterbox.linaclego;

import javax.swing.tree.DefaultMutableTreeNode;

import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.cell.Cell;
import se.esss.litterbox.linaclego.structures.legomonitor.LegoMonitor;
import se.esss.litterbox.linaclego.structures.slot.Slot;

@SuppressWarnings("serial")
public class LinacLegoDefaultMutableTreeNode extends DefaultMutableTreeNode
{
	
	public LinacLegoDefaultMutableTreeNode() 
	{
		super();
	}
	public LinacLegoDefaultMutableTreeNode(LinacLego linacLego) 
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linacLego" + "</font>";
		html =  html + "<font color=\"FF0000\"> title</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + linacLego.getLinacLegoTitle() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev No." + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + linacLego.getLinacLegoRevNo() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev Date" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + linacLego.getLinacLegoRevDate() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev Comment" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + linacLego.getLinacLegoRevComment() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(LegoData dataElement) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "data" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getId() + "\"</font>";
		if (dataElement.getType() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getType() + "\"</font>";
		if (dataElement.getUnit() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "unit" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getUnit() + "\"</font>";
		if (dataElement.getValue() != null)
			html =  html + "<font color=\"000000\">" + " " + dataElement.getValue() + "</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(String string) throws LinacLegoException
	{
		this();
		setUserObject(string); 
	}
	public LinacLegoDefaultMutableTreeNode(Linac linac) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linac" + "</font>";
		html = html + "</html>";
		setUserObject(html); 
		LinacLegoDefaultMutableTreeNode dataFolder = new LinacLegoDefaultMutableTreeNode("data");
		add(dataFolder);
		for (int idata = 0; idata < linac.getDataElementList().size(); ++idata)
			dataFolder.add(new LinacLegoDefaultMutableTreeNode(linac.getDataElementList().get(idata)));
		
	}
	public LinacLegoDefaultMutableTreeNode(Section section) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "section" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rfHarmonic" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getRfHarmonic() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(Cell cell) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "cell" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cell.getId() + "\"</font>";
		if (cell.getModelId() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cell.getModelId() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		
	}
	public LinacLegoDefaultMutableTreeNode(Slot slot) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "slot" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getId() + "\"</font>";
		if (slot.getModelId() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getModelId() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LinacLegoDefaultMutableTreeNode(BeamLineElement ble) throws LinacLegoException
	{
		this();

		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "ble" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getType() + "\"</font>";
		if (!ble.getModel().equals("none"))
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getModel() + "\"</font>";
		if (!ble.getDiscipline().equals("none"))
			html =  html + "<font color=\"FF0000\">" + " " + "disc" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + ble.getDiscipline() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		LinacLegoDefaultMutableTreeNode dataFolder = new LinacLegoDefaultMutableTreeNode("data");
		add(dataFolder);
		for (int idata = 0; idata < ble.getDataElementList().size(); ++idata)
			dataFolder.add(new LinacLegoDefaultMutableTreeNode(ble.getDataElementList().get(idata)));
	}
	public LinacLegoDefaultMutableTreeNode(LegoMonitor lmon) throws LinacLegoException
	{
		this();

		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "mon" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lmon.getId() + "\"</font>";
		if (!lmon.getModel().equals("none"))
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lmon.getModel() + "\"</font>";
		if (!lmon.getDiscipline().equals("none"))
			html =  html + "<font color=\"FF0000\">" + " " + "disc" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lmon.getDiscipline() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		LinacLegoDefaultMutableTreeNode dataFolder = new LinacLegoDefaultMutableTreeNode("data");
		add(dataFolder);
		for (int idata = 0; idata < lmon.getDataElementList().size(); ++idata)
			dataFolder.add(new LinacLegoDefaultMutableTreeNode(lmon.getDataElementList().get(idata)));
	}
	
}
