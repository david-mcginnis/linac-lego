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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.linaclego.structures.slot.SlotModel;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class SlotSearcher 
{
	private LinacLego linacLego;
	private ArrayList<SlotMatch> slotMatches;
	
	public LinacLego getLinacLego() {return linacLego;}
	public ArrayList<SlotMatch> getSlotMatches() {return slotMatches;}
	
	public SlotSearcher(LinacLego linacLego) throws LinacLegoException
	{
		this.linacLego = linacLego;
		int numSlotModels = linacLego.getSlotModelList().size();
		if (numSlotModels < 1) throw new LinacLegoException("No slot models defined in header tag");
		slotMatches = new ArrayList<SlotMatch>();
		for (int imodel = 0; imodel < numSlotModels; ++imodel)
		{
			searchLinacForSlots(linacLego.getSlotModelList().get(imodel));
		}
	}
	public void replaceSlotsWithMatches() throws LinacLegoException
	{
		for (int imatch = 0; imatch < slotMatches.size(); ++imatch)
		{
			slotMatches.get(imatch).replaceBleListWithSlot("xx");
		}
		try 
		{
			SimpleXmlReader linacTag = linacLego.getLinacTag();
			SimpleXmlReader sectionTags = linacTag.tagsByName("section");
			if (sectionTags != null )
			{
				for (int isection = 0; isection < sectionTags.numChildTags(); ++isection)
				{
					SimpleXmlReader cellTags = sectionTags.tag(isection).tagsByName("cell");
					if (cellTags != null )
					{
						for (int icell = 0; icell < cellTags.numChildTags(); ++icell)
						{
							String cellModelId = null;
							try {cellModelId = cellTags.tag(icell).attribute("model");} catch (SimpleXmlException e1) {}
							if (cellModelId == null)
							{
								DocumentFragment cellFragment = linacLego.getSimpleXmlDoc().getXmlDoc().createDocumentFragment();
								SimpleXmlReader slotTags = cellTags.tag(icell).tagsByName("slot");
								int slotCounter = 10;
								if (slotTags != null )
								{
									for (int islot = 0; islot < slotTags.numChildTags(); ++islot)
									{
										String slotModelId = null;
										try {slotModelId = slotTags.tag(islot).attribute("model");} catch (SimpleXmlException e1) {}
										if (slotModelId == null)
										{
											NodeList childNodes  = slotTags.tag(islot).getXmlNode().getChildNodes();
											ArrayList<Node> elementNodes = new ArrayList<Node>();
											for (int ic = 0; ic < childNodes.getLength(); ++ic)
											{
												if (childNodes.item(ic).getNodeType() == Node.ELEMENT_NODE)
												{
													elementNodes.add(childNodes.item(ic));
												}
											}
											int ielementCount = 0;
											boolean openNewSlotElementTag = false;
											boolean closeNewSlotElementTag = false;
											boolean addBleTagToBleList = false;
											boolean addSlotToNewSlotList = false;
											String previousElement = "";
											String currentElement = "";
											ArrayList<Node> bleList = new ArrayList<Node>();
											Element slot;
											while (ielementCount < elementNodes.size())
											{
												previousElement = currentElement;
												currentElement = elementNodes.get(ielementCount).getNodeName();
												if (currentElement.equals("ble") && !previousElement.equals("ble"))
													openNewSlotElementTag = true;
												else
													openNewSlotElementTag = false;
												if (!currentElement.equals("ble") && previousElement.equals("ble")) 
													closeNewSlotElementTag = true;
												else
													closeNewSlotElementTag = false;
												if (currentElement.equals("ble") && (ielementCount == (elementNodes.size() - 1))) 
													closeNewSlotElementTag = true;
												if (currentElement.equals("ble") ) 
													addBleTagToBleList = true;
												else
													addBleTagToBleList = false;
												if (currentElement.equals("slot") ) 
													addSlotToNewSlotList = true;
												else
													addSlotToNewSlotList = false;
												if (openNewSlotElementTag) bleList = new ArrayList<Node>();
												if (addBleTagToBleList) bleList.add(elementNodes.get(ielementCount));
												if (closeNewSlotElementTag)
												{
													slot = linacLego.getSimpleXmlDoc().getXmlDoc().createElement("slot");
													slot.setAttribute("id",  "slot" + addLeadingZeros(slotCounter, 3));
													for (int ible  = 0; ible < bleList.size(); ++ible)
													{
														slot.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t\t\t"));
														slot.appendChild(bleList.get(ible));
													}
													slot.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t\t"));
													cellFragment.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t\t"));
													cellFragment.appendChild(slot);
													slotCounter = slotCounter + 10;
												}
												if (addSlotToNewSlotList)
												{
													elementNodes.get(ielementCount).getAttributes().getNamedItem("id").setNodeValue("slot" + addLeadingZeros(slotCounter, 3));
													cellFragment.appendChild(linacLego.getSimpleXmlDoc().getXmlDoc().createTextNode("\n\t\t\t\t"));
													cellFragment.appendChild(elementNodes.get(ielementCount));
													slotCounter = slotCounter + 10;
												}
												ielementCount = ielementCount + 1;
											}
											cellTags.tag(icell).getXmlNode().replaceChild(cellFragment, slotTags.tag(islot).getXmlNode());
										}
										else
										{
											slotTags.tag(islot).setAttribute("id", "slot" + addLeadingZeros(slotCounter, 3));
											slotCounter = slotCounter + 10;
										}
									}
									NodeList childNodes = cellTags.tag(icell).getXmlNode().getChildNodes();
									int inode = childNodes.getLength() - 1;
									while (inode > 0)
									{
										if (childNodes.item(inode).getNodeType() == Node.TEXT_NODE)
										{
											if (childNodes.item(inode - 1).getNodeType() == Node.TEXT_NODE)
											{
												cellTags.tag(icell).getXmlNode().removeChild(childNodes.item(inode - 1));
											}
										}
										inode = inode - 1;
									}
								}
							}
						}
					}
				}
			}
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	private void searchLinacForSlots(SlotModel slotModel) throws LinacLegoException
	{
		try 
		{
			SimpleXmlReader linacTag = linacLego.getLinacTag();
			SimpleXmlReader sectionTags = linacTag.tagsByName("section");
			SimpleXmlReader slotModelBles = slotModel.getTag().tagsByName("ble");
			int numBleElements = slotModelBles.numChildTags();
			getLinacLego().writeStatus("Searching for slot matches...");
			if (sectionTags != null )
			{
				for (int isection = 0; isection < sectionTags.numChildTags(); ++isection)
				{
					SimpleXmlReader cellTags = sectionTags.tag(isection).tagsByName("cell");
					if (cellTags != null )
					{
						for (int icell = 0; icell < cellTags.numChildTags(); ++icell)
						{
							String cellModelId = null;
							try {cellModelId = cellTags.tag(icell).attribute("model");} catch (SimpleXmlException e1) {}
							if (cellModelId == null)
							{
								SimpleXmlReader slotTags = cellTags.tag(icell).tagsByName("slot");
								if (slotTags != null )
								{
									for (int islot = 0; islot < slotTags.numChildTags(); ++islot)
									{
										SimpleXmlReader bleTagList = slotTags.tag(islot).tagsByName("ble");
										if (bleTagList != null) 
										{
											int startTag = 0;
											int numTagsLeft = bleTagList.numChildTags() - startTag;
											int islotPos = 0;
											while(numTagsLeft >= numBleElements)
											{
												boolean tagsMatch 
													= beamLineElementTagsMatch(slotModelBles.tag(islotPos), bleTagList.tag(islotPos + startTag));
												islotPos = islotPos + 1;
												if (tagsMatch)
												{
													if (islotPos == numBleElements)
													{
														ArrayList<SimpleXmlReader> matchingBleElements = new ArrayList<SimpleXmlReader>();
														for (int im = 0; im < numBleElements; ++im) matchingBleElements.add(bleTagList.tag(startTag + im));
														SlotMatch blel = new SlotMatch(slotModel,  matchingBleElements);
														slotMatches.add(blel);
														getLinacLego().writeStatus("     Found slotModel match " + blel.getSlotModelId() + " at " + blel.getId());
														startTag = islotPos + startTag;
														numTagsLeft = bleTagList.numChildTags() - startTag;
														islotPos = 0;
													}
												}
												else
												{
//	old way											startTag = islotPos + startTag;
													startTag = startTag + 1;
													numTagsLeft = bleTagList.numChildTags() - startTag;
													islotPos = 0;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			getLinacLego().writeStatus("Finished searching for slot matches.");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public boolean beamLineElementTagsMatch(SimpleXmlReader bleTag1, SimpleXmlReader bleTag2) throws LinacLegoException
	{
		boolean matches = true;
		try 
		{
			if (!bleTag1.attribute("type").equals(bleTag2.attribute("type"))) return false;
			SimpleXmlReader dataListTag1 = bleTag1.tagsByName("d");
			SimpleXmlReader dataListTag2 = bleTag2.tagsByName("d");
			if ((dataListTag1 == null) && (dataListTag2 == null)) return true;
			if (dataListTag1 == null) return false;
			if (dataListTag2 == null) return false;
			if (dataListTag1.numChildTags() != dataListTag2.numChildTags()) return false;
			for (int i1 = 0; i1 < dataListTag1.numChildTags(); ++i1)
			{
				LegoData data1 = new LegoData(dataListTag1.tag(i1));
				int i2 = 0;
				boolean dataMatch = false;
				while ((i2 < dataListTag2.numChildTags()) && !dataMatch)
				{
					LegoData data2 = new LegoData(dataListTag2.tag(i2));
					if (data2.matchesDataElementModel(data1)) dataMatch = true;
					i2 = i2 + 1;
				}
				if (!dataMatch) return false;
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}

		return matches;
		
	}
	String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
	}
	public void saveXmlFile(String filePath) throws LinacLegoException
	{
		try {linacLego.getSimpleXmlDoc().saveXmlDocument(filePath);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public static void main(String[] args) throws LinacLegoException, SimpleXmlException, MalformedURLException 
	{
//		String xmlFileDir = "C:\\Users\\davidmcginnis\\gitRepositories\\linac-lego\\LinacLego\\test\\xmlFiles";
		String xmlFileDir = "/Users/davidmcginnis/Documents/gitRepositories/linac-lego/LinacLego/test/xmlFiles";
		URL inputFileUrl = new File(xmlFileDir + File.separator + "dtl.xml").toURI().toURL();
		
		SimpleXmlDoc sxd = new SimpleXmlDoc(inputFileUrl);
		LinacLego linacLego = new LinacLego(sxd);
		linacLego.setStatusPanel(null);
		linacLego.readHeader();
		SlotSearcher slotSearcher = new SlotSearcher(linacLego);
		slotSearcher.replaceSlotsWithMatches();
		slotSearcher.saveXmlFile(xmlFileDir  + File.separator +  "dtlTest.xml");
	}

}
