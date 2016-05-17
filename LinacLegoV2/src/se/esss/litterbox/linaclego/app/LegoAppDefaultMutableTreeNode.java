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
package se.esss.litterbox.linaclego.app;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.data.LegoData;
import se.esss.litterbox.linaclego.data.LegoInfo;
import se.esss.litterbox.linaclego.structures.LegoCell;
import se.esss.litterbox.linaclego.structures.LegoLinac;
import se.esss.litterbox.linaclego.structures.LegoSection;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.linaclego.structures.beam.LegoBeam;


@SuppressWarnings("serial")
public class LegoAppDefaultMutableTreeNode extends DefaultMutableTreeNode
{
	
	public LegoAppDefaultMutableTreeNode() 
	{
		super();
	}
	public LegoAppDefaultMutableTreeNode(Lego lego) 
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linacLego" + "</font>";
		html =  html + "<font color=\"FF0000\"> title</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getTitle() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev No." + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getRevNo() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev Date" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getRevDate() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev Comment" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getRevComment() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LegoAppDefaultMutableTreeNode(LegoData dataElement) throws LinacLegoException
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
	public LegoAppDefaultMutableTreeNode(LegoInfo info) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "info" + "</font>";
		if (info.getId() != null)
			html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + info.getId() + "\"</font>";
		if (info.getType() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + info.getType() + "\"</font>";
		if (info.getValue() != null)
			html =  html + "<font color=\"000000\">" + " " + info.getValue() + "</font>";
		html = html + "</html>";
		setUserObject(html); 
	}
	public LegoAppDefaultMutableTreeNode(String string) throws LinacLegoException
	{
		this();
		setUserObject(string); 
	}
	public LegoAppDefaultMutableTreeNode(LegoLinac linac) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linac" + "</font>";
		html = html + "</html>";
		setUserObject(html); 
		addInfoFolder(linac.getLegoInfoList());
		addDataFolder(linac.getLegoDataList());
		
	}
	public LegoAppDefaultMutableTreeNode(LegoSection section) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "section" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rfHarmonic" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getRfHarmonic() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		addInfoFolder(section.getLegoInfoList());
	}
	public LegoAppDefaultMutableTreeNode(LegoCell cell) throws LinacLegoException
	{		
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "cell" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cell.getId() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		addInfoFolder(cell.getLegoInfoList());
		
	}
	public LegoAppDefaultMutableTreeNode(LegoSlot slot) throws LinacLegoException
	{
		this();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "slot" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getId() + "\"</font>";
		if (slot.getTemplate() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getTemplate() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		addInfoFolder(slot.getLegoInfoList());
	}
	public LegoAppDefaultMutableTreeNode(LegoBeam beam) throws LinacLegoException
	{
		this();

		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "ble" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getType() + "\"</font>";
		if (beam.getModel() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getModel() + "\"</font>";
		if (beam.getDisc() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "disc" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getDisc() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		addInfoFolder(beam.getLegoInfoList());
		addDataFolder(beam.getLegoDataList());
	}
	private void addInfoFolder(ArrayList<LegoInfo> legoInfoList) throws LinacLegoException
	{
		if (legoInfoList.size() > 0)
		{
			LegoAppDefaultMutableTreeNode infoFolder = new LegoAppDefaultMutableTreeNode("info");
			add(infoFolder);
			for (int iinfo = 0; iinfo < legoInfoList.size(); ++iinfo)
				infoFolder.add(new LegoAppDefaultMutableTreeNode(legoInfoList.get(iinfo)));
		}
	}
	private void addDataFolder(ArrayList<LegoData> legoDataList) throws LinacLegoException
	{
		if (legoDataList.size() > 0)
		{
			LegoAppDefaultMutableTreeNode dataFolder = new LegoAppDefaultMutableTreeNode("data");
			add(dataFolder);
			for (int iinfo = 0; iinfo < legoDataList.size(); ++iinfo)
				dataFolder.add(new LegoAppDefaultMutableTreeNode(legoDataList.get(iinfo)));
		}
	}
	
}
