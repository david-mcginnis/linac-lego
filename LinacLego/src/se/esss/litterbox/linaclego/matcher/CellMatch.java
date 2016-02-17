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
package se.esss.litterbox.linaclego.matcher;

import java.util.ArrayList;

import org.w3c.dom.DocumentFragment;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.cell.CellModel;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class CellMatch {

	private CellModel cellModel;
	private SimpleXmlReader linacTag;
	private SimpleXmlReader sectionTag;
	private SimpleXmlReader cellTag;
	private ArrayList<SimpleXmlReader> matchingSlots;
	
	public CellModel getCellModel() {return cellModel;}
	public SimpleXmlReader getLinacTag() {return linacTag;}
	public SimpleXmlReader getSectionTag() {return sectionTag;}
	public SimpleXmlReader getCellTag() {return cellTag;}
	public ArrayList<SimpleXmlReader> getMatchingSlotElements() {return matchingSlots;}
	
	public CellMatch(CellModel cellModel, ArrayList<SimpleXmlReader> matchingSlots)
	{
		this.cellModel = cellModel;
		this.matchingSlots = matchingSlots;
		this.cellTag = new SimpleXmlReader(matchingSlots.get(0).getXmlNode().getParentNode());
		this.sectionTag = new SimpleXmlReader(cellTag.getXmlNode().getParentNode());
		this.linacTag = new SimpleXmlReader(sectionTag.getXmlNode().getParentNode());
	}
	public void replaceSlotListWithCell(String cellName) throws LinacLegoException
	{
		DocumentFragment fraggy = createSlotFragment(cellName);
		getCellTag().getXmlNode().insertBefore(fraggy, matchingSlots.get(0).getXmlNode());
		for (int ii = 0; ii < matchingSlots.size(); ++ii)
		{
			getCellTag().getXmlNode().removeChild(matchingSlots.get(ii).getXmlNode().getNextSibling());
			getCellTag().getXmlNode().removeChild(matchingSlots.get(ii).getXmlNode());
		}
	}
	public DocumentFragment createSlotFragment(String name) throws LinacLegoException
	{
		return cellModel.createCellFragment(this, name);
	}
	String getId() throws LinacLegoException
	{
		return getSectionId() + "." + getCellId() + "." + getSlotId();
	}
	String getCellModelId() throws LinacLegoException {try {return cellModel.getTag().attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getSlotId() throws LinacLegoException {try {return matchingSlots.get(0).attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getCellId() throws LinacLegoException {try {return cellTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}
	String getSectionId() throws LinacLegoException {try {return sectionTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}}

}
