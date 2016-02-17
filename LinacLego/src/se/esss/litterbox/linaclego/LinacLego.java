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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.linaclego.structures.Linac;
import se.esss.litterbox.linaclego.structures.Section;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.cell.Cell;
import se.esss.litterbox.linaclego.structures.cell.CellModel;
import se.esss.litterbox.linaclego.structures.legomonitor.LegoMonitor;
import se.esss.litterbox.linaclego.structures.legoset.LegoSet;
import se.esss.litterbox.linaclego.structures.legoset.TransferFunction;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.linaclego.structures.slot.SlotModel;
import se.esss.litterbox.linaclego.utilities.StatusPanel;
import se.esss.litterbox.linaclego.utilities.Zipper;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LinacLego 
{
	static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	private static final String delim = System.getProperty("file.separator");
	public static final String newline = System.getProperty("line.separator");
	private SimpleXmlDoc simpleXmlDoc;
	private SimpleXmlReader linacLegoTag;
	private ArrayList<CellModel> cellModelList;
	private ArrayList<SlotModel> slotModelList;
	private ArrayList<LegoSet> legoSetList;
	private SimpleXmlReader linacTag;
	private String linacLegoTitle;
	private double eVout = -1.0;
	private boolean printIdInTraceWin = true;
	private StatusPanel statusPanel = null;
	private String linacLegoRevNo = "0";
	private String linacLegoRevComment = "none";
	private String linacLegoRevDate = "01-Jan-1970";
	private File reportDirectory = null;
	private boolean reportDirectoryExists = false;
	private boolean printLegoMonitors = true;
	
	private Linac linac = null;
	
	public LinacLego(SimpleXmlDoc simpleXmlDoc)   
	{
		this.simpleXmlDoc = simpleXmlDoc;
		reportDirectoryExists = false;
		legoSetList = new ArrayList<LegoSet>();
	}
	public void readHeader() throws LinacLegoException
	{
		writeStatus("Reading header tag...");
		try 
		{
			linacLegoTag = new SimpleXmlReader(simpleXmlDoc);
			try {linacLegoRevNo = linacLegoTag.attribute("revNo");} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) linacLegoRevNo = "0";}
			try {linacLegoRevComment = linacLegoTag.attribute("comment");} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) linacLegoRevComment = "none";}
			try {linacLegoRevDate = linacLegoTag.attribute("date");} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) linacLegoRevComment = "01-Jan-1970";}

			linacTag =  linacLegoTag.tagsByName("linac").tag(0);
			linacLegoTitle = linacLegoTag.attribute("title");

			cellModelList = new ArrayList<CellModel>();
			SimpleXmlReader cellModelsListTag = linacLegoTag.tagsByName("header").tag(0).tagsByName("cellModels");
			if (cellModelsListTag.numChildTags() > 0)
			{
				for (int icol = 0; icol < cellModelsListTag.numChildTags(); ++icol)
				{
					SimpleXmlReader cellModelListTag = cellModelsListTag.tag(icol).tagsByName("cellModel");
					if (cellModelListTag.numChildTags() > 0)
					{
						for (int itag = 0; itag < cellModelListTag.numChildTags(); ++itag)
						{
							cellModelList.add(new CellModel(cellModelListTag.tag(itag), this));
							writeStatus("     Adding CellModel " + cellModelListTag.tag(itag).attribute("id"));
						}
					}
				}
			}
			slotModelList = new ArrayList<SlotModel>();
			SimpleXmlReader slotModelsListTag = linacLegoTag.tagsByName("header").tag(0).tagsByName("slotModels");
			if (slotModelsListTag.numChildTags() > 0)
			{
				for (int icol = 0; icol < slotModelsListTag.numChildTags(); ++icol)
				{
					SimpleXmlReader slotModelListTag = slotModelsListTag.tag(icol).tagsByName("slotModel");
					if (slotModelListTag.numChildTags() > 0)
					{
						for (int itag = 0; itag < slotModelListTag.numChildTags(); ++itag)
						{
							slotModelList.add(new SlotModel(slotModelListTag.tag(itag), this));
							writeStatus("     Adding SlotModel " + slotModelListTag.tag(itag).attribute("id"));
						}
					}
				}
			}

			writeStatus("Reading LegoSets");
			legoSetList = new ArrayList<LegoSet>();
			SimpleXmlReader legoSetsListTag = linacLegoTag.tagsByName("header").tag(0).tagsByName("legoSets");
			if (legoSetsListTag.numChildTags() > 0)
			{
				for (int icol = 0; icol < legoSetsListTag.numChildTags(); ++icol)
				{
					SimpleXmlReader legoSetListTag = legoSetsListTag.tag(icol).tagsByName("legoSet");
					if (legoSetListTag.numChildTags() > 0)
					{
						for (int itag = 0; itag < legoSetListTag.numChildTags(); ++itag)
						{
							legoSetList.add(new LegoSet(legoSetListTag.tag(itag), this));
							writeStatus("     Adding legoSet " + legoSetListTag.tag(itag).attribute("devName"));
						}
					}
				}
			}
			writeStatus("Finished reading LegoSets");
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		}
		writeStatus("Finished reading header tag.");
	}
	public void setReportDirectory(File parentDirectory)
	{
		reportDirectory = new File(parentDirectory.getPath() + delim + "linacLegoOutput");
		if (reportDirectory.exists()) 
		{
			File[] fileList = reportDirectory.listFiles();
			if (fileList.length > 0) for (int ifile = 0; ifile < fileList.length; ++ifile) fileList[ifile].delete();
		}
		else
		{
			reportDirectory.mkdir();
		}
		reportDirectoryExists = false;
		if (reportDirectory.exists()) reportDirectoryExists = true;
		writeStatus("Report directory set to " + parentDirectory.getPath() + delim + "linacLegoOutput");
	}
	public void updateLatticeSettingsUsingLegoSetData(URL legoSetDataXmlDocUrl) throws LinacLegoException
	{
		writeStatus("Updating lattice settings using LegoSet Data in " + legoSetDataXmlDocUrl.toString());
		ArrayList<LegoData> legoSetDataList = new ArrayList<LegoData>();
		try 
		{
			SimpleXmlDoc legoSetDataXmlDoc = new SimpleXmlDoc(legoSetDataXmlDocUrl);
			SimpleXmlReader legoSetDataTagList = new SimpleXmlReader(legoSetDataXmlDoc).tagsByName("legoSetData");
			if (legoSetDataTagList.numChildTags() > 0)
			{
				for (int ii = 0; ii < legoSetDataTagList.numChildTags(); ++ii)
				{
					LegoData de = new LegoData(legoSetDataTagList.tag(ii));
					int ij = 0;
					while (ij < ii)
					{
						if(de.getId().equals(legoSetDataList.get(ij).getId()))
							throw new LinacLegoException("LegoSetData " + de.getId() + " appears more than once in " + legoSetDataXmlDocUrl.toString());
						ij = ij + 1;
					}
					legoSetDataList.add(de);
					writeStatus("     Reading LegoSetData for " + legoSetDataList.get(ii).getId());
				}
			}
		}
		catch (SimpleXmlException e) { throw new LinacLegoException(e);}
		for (int ii = 0; ii < legoSetList.size(); ++ii)
		{
			legoSetList.get(ii).updateLatticeSettingsUsingLegoSetData(legoSetDataList);
		}
		writeStatus("Finished updating lattice settings using LegoSet Data in " + legoSetDataXmlDocUrl.toString());
	}
	public void updateLegoSetDataUsingLatticeSettings(String settingsFileName, String dtdFileName, boolean saveInReportDirectory) throws LinacLegoException
	{
		writeStatus("Updating LegoSets using lattice settings.");
		File filePath = new File(settingsFileName);
		if (saveInReportDirectory) filePath =  new File(getReportDirectory().getPath() + delim +  getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "LegoSets.xml");
		try 
		{
			SimpleXmlWriter xw = new SimpleXmlWriter("legoSetDataList", dtdFileName);
			xw.setAttribute("title", filePath.getName().substring(0, filePath.getName().indexOf(".xml")));
			for (int ii = 0; ii < legoSetList.size(); ++ii)
			{
				legoSetList.get(ii).updateLegoSetDataUsingLatticeSettings(xw);
				writeStatus("     Writing LegoSetData for " + legoSetList.get(ii).getDevName());
			}
			xw.closeDocument();
			xw.saveXmlDocument(filePath.getPath());
		} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		writeStatus("Finished updating LegoSets using lattice settings to " + settingsFileName);
	}
	public void updateLinac() throws LinacLegoException 
	{
		writeStatus("Updating Linac...");
		try 
		{
			linac = new Linac(this);
			eVout = linac.geteVout();
			writeStatus("     Final Energy = " + fourPlaces.format(geteVout() * 1.0e-6) + " MeV");
			writeStatus("     Length       = " + fourPlaces.format(linac.getLength() ) + " meters");
			writeStatus("     X = " + fourPlaces.format(linac.getBeamLineElements().get(linac.getBeamLineElements().size() - 1).getEndPosVec()[0]) + " meters");
			writeStatus("     Y = " + fourPlaces.format(linac.getBeamLineElements().get(linac.getBeamLineElements().size() - 1).getEndPosVec()[1]) + " meters");
			writeStatus("     Z = " + fourPlaces.format(linac.getBeamLineElements().get(linac.getBeamLineElements().size() - 1).getEndPosVec()[2]) + " meters");
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		}
		writeStatus("...Finished updating Linac.");
	}
	public void createTraceWinFile() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  getTraceWinFileName());
			pw.println(";" + getLinacLegoTitle());
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					for (int islot = 0; islot < cell.getNumOfSlots(); ++islot)
					{
						Slot  slot= cell.getSlotList().get(islot);
						for (int ielem = 0; ielem < slot.getNumBeamLineElements(); ++ielem)
						{
							BeamLineElement  elem = slot.getBeamLineElementList().get(ielem);
							elem.printTraceWin(pw);
						}
					}
				}
			}
			pw.println("END");
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		} catch (SimpleXmlException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		}
		writeStatus("TraceWin    file  created in " + getReportDirectory().getPath() + delim +  getTraceWinFileName());
	}
	public void createDynacFile() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  getDynacFileName());
			pw.println(linac.makeDynacHeader());
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					for (int islot = 0; islot < cell.getNumOfSlots(); ++islot)
					{
						Slot  slot= cell.getSlotList().get(islot);
						for (int ielem = 0; ielem < slot.getNumBeamLineElements(); ++ielem)
						{
							BeamLineElement  elem = slot.getBeamLineElementList().get(ielem);
							elem.printDynac(pw);;
						}
					}
				}
			}
			pw.println("STOP");
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getStackTraceString());
			throw lle;
		} 
		writeStatus("Dynac       file  created in " + getReportDirectory().getPath() + delim +  getDynacFileName());
	}
	public void printMonitorTable() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "MonitorData.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("Section,Cell,Slot,BLE,MON,Type,Model,Disc,Name,eVout,v/c,Xend,Yend,Zend,Xsur,Ysur,Zsur");
			pw.println("       ,    ,    ,   ,   ,    ,     ,    ,    ,(MeV),   , (m), (m), (m), (m), (m), (m)");
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					for (int islot = 0; islot < cell.getNumOfSlots(); ++islot)
					{
						Slot  slot= cell.getSlotList().get(islot);
						for (int ielem = 0; ielem < slot.getNumBeamLineElements(); ++ielem)
						{
							BeamLineElement  elem = slot.getBeamLineElementList().get(ielem);
							if (elem.getNumMonitors() > 0)
							{
								for (int imon = 0; imon < elem.getNumMonitors(); ++imon)
								{
									LegoMonitor  monitor = elem.getLegoMonitorList().get(imon);
									monitor.printMonitorTable(pw);
								}
							}
						}
					}
				}
			}
			pw.close();
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("CSV Data    file  created in " + getReportDirectory().getPath() + delim +  fileName );
	}
	public void printBleTable() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "BleData.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("Section,Cell,Slot,BLE,Type,Model,Disc,Name,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur, VT ,PhiS ,  G  ,Theta");
			pw.println("       ,    ,    ,   ,    ,     ,    ,    ,(MeV),   ,  (m) , (m), (m), (m), (m), (m), (m),(MV),(deg),(T/m),(deg)");
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					for (int islot = 0; islot < cell.getNumOfSlots(); ++islot)
					{
						Slot  slot= cell.getSlotList().get(islot);
						for (int ielem = 0; ielem < slot.getNumBeamLineElements(); ++ielem)
						{
							BeamLineElement  elem = slot.getBeamLineElementList().get(ielem);
							elem.printBleTable(pw);
						}
					}
				}
			}
			pw.close();
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("CSV Data    file  created in " + getReportDirectory().getPath() + delim +  fileName );
	}
	public void printSlotTable() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "SlotData.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("Section,Cell,Slot,Model,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur");
			pw.println("       ,    ,    ,     ,(MeV),   ,  (m) , (m), (m), (m), (m), (m), (m)");
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					for (int islot = 0; islot < cell.getNumOfSlots(); ++islot)
					{
						Slot  slot= cell.getSlotList().get(islot);
						slot.printSlotTable(pw);;
					}
				}
			}
			pw.close();
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("CSV Data    file  created in " + getReportDirectory().getPath() + delim +  fileName );
	}
	public void printCellTable() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "CellData.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("Section,Cell,Model,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur");
			pw.println("       ,    ,     ,(MeV),   ,  (m) , (m), (m), (m), (m), (m), (m)");
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					cell.printCellTable(pw);;
				}
			}
			pw.close();
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("CSV Data    file  created in " + getReportDirectory().getPath() + delim +  fileName );
	}
	public void printSectionTable() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "SectionData.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("Section,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur");
			pw.println("       ,(MeV),   ,  (m) , (m), (m), (m), (m), (m), (m)");
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				section.printSectionTable(pw);;
			}
			pw.close();
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("CSV Data    file  created in " + getReportDirectory().getPath() + delim +  fileName );
	}
	public void printPartCounts() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) ;
		try {
			linac.printPartCounts(getReportDirectory().getPath() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("Part Counts files created in " + getReportDirectory().getPath() + delim +  fileName + "Parts.csv");
	}
	public void printLegoSets() throws LinacLegoException  
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "LegoSet.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("BLE devName,BLE data,BLE value,BLE unit,LinacSet devName,LinacSet Value,LinacSet Unit," + TransferFunction.getTfCsvDataHeader());
			for (int isec = 0; isec < linac.getNumOfSections(); ++isec)
			{
				Section  section= linac.getSectionList().get(isec);
				for (int icell = 0; icell < section.getNumOfCells(); ++icell)
				{
					Cell  cell= section.getCellList().get(icell);
					for (int islot = 0; islot < cell.getNumOfSlots(); ++islot)
					{
						Slot  slot= cell.getSlotList().get(islot);
						for (int ielem = 0; ielem < slot.getNumBeamLineElements(); ++ielem)
						{
							BeamLineElement  elem = slot.getBeamLineElementList().get(ielem);
							elem.printLegoSets(pw);
						}
					}
				}
			}
			pw.close();
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("LegoSet     file  created in " + getReportDirectory().getPath() + delim +  fileName + "LegoSet.csv");
	}
	public void printModelInfoLinks() throws LinacLegoException
	{
		if (linac == null) throw new LinacLegoException("no linac data");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "InfoLinks.csv";
		try 
		{
			PrintWriter pw = new PrintWriter(getReportDirectory().getPath() + delim +  fileName);
			pw.println("Type,Id,Link");
			for ( int imodel = 0; imodel < slotModelList.size(); ++imodel)
			{
				for (int ii = 0; ii < slotModelList.get(imodel).getInfoLinkList().size(); ++ii)
				{
					String info = slotModelList.get(imodel).getInfoLinkList().get(ii).getType();
					info = info +  "," + slotModelList.get(imodel).getInfoLinkList().get(ii).getId();
					info = info +  "," + slotModelList.get(imodel).getInfoLinkList().get(ii).getData();
					pw.println(info);
				}
			}
			pw.close();
		}
		catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("InfoLink    file  created in " + getReportDirectory().getPath() + delim +  fileName + "InfoLinks.csv");
	}
	public void saveXmlDocument() throws LinacLegoException
	{
		if (simpleXmlDoc == null) throw new LinacLegoException("no xml Docxument");
		String fileName = getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + "Parsed.xml";
		
		try {
			simpleXmlDoc.saveXmlDocument(getReportDirectory().getParent() + delim +  fileName);
		} catch (Exception e) {
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		} 
		writeStatus("LinacLego   file  saved   in " + getReportDirectory().getParent() + delim +  fileName);
	}
	public void setPrintIdInTraceWin(boolean printIdInTraceWin) 
	{
		this.printIdInTraceWin = printIdInTraceWin;
		if (printIdInTraceWin)
		{
			writeStatus("Turning on  TraceWinId output");
		}
		else
		{
			writeStatus("Turning off TraceWinId output");
		}
	}
	public void createZipArchive() throws LinacLegoException
	{
		try 
		{
			Zipper zipper = new Zipper(getReportDirectory().getParent() + delim + "linacLego.zip");
			zipper.addFilesInDir(getReportDirectory().getParent(), "xml");
			zipper.addFilesInDir(getReportDirectory().getPath(), "xml");
			zipper.addFilesInDir(getReportDirectory().getPath(), "csv");
			zipper.addFilesInDir(getReportDirectory().getPath(), "in");
			zipper.addFilesInDir(getReportDirectory().getPath(), "dat");
			zipper.addFilesInDir(getReportDirectory().getPath(), "txt");
			zipper.addFilesInDir(getReportDirectory().getPath(), "edz");
			zipper.close();
			writeStatus("Compressed  file  created in " + getReportDirectory().getParent() + delim +  "linacLego.zip");
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		}
	}
	
	public File getReportDirectory() {return reportDirectory;}
	public boolean isReportDirectoryExists() {return reportDirectoryExists;}
	public String getXmlFileName() {return simpleXmlDoc.getXmlDocName();}
	public SimpleXmlDoc getSimpleXmlDoc() {return simpleXmlDoc;}
	public SimpleXmlReader getLinacLegoTag() {return linacLegoTag;}
	public ArrayList<CellModel> getCellModelList() {return cellModelList;}
	public ArrayList<SlotModel> getSlotModelList() {return slotModelList;}
	public ArrayList<LegoSet> getLegoSetList() {return legoSetList;}
	public SimpleXmlReader getLinacTag() {return linacTag;}
	public double geteVout() {return eVout;}
	public String getLinacLegoTitle() {return linacLegoTitle;}
	public boolean isPrintIdInTraceWin() {return printIdInTraceWin;}
	public String getLinacLegoRevNo() {return linacLegoRevNo;}
	public String getLinacLegoRevComment() {return linacLegoRevComment;}
	public String getLinacLegoRevDate() {return linacLegoRevDate;}
	public Linac getLinac() {return linac;}
	public boolean isPrintLegoMonitors() {return printLegoMonitors;}
	
	public void seteVout(double eVout) {this.eVout = eVout;}
	public void setStatusPanel(StatusPanel statusPanel) {this.statusPanel = statusPanel;}
	public void setPrintLegoMonitors(boolean printLegoMonitors) {this.printLegoMonitors = printLegoMonitors;}
	public String  getTraceWinFileName() 
	{
		return getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + ".dat";
	}
	public String  getDynacFileName() 
	{
		return getXmlFileName().substring(0, getXmlFileName().lastIndexOf(".")) + ".in";
	}
	public void writeStatus(String statusText) 
	{
		if (statusPanel != null)
		{
			statusPanel.setText(statusText);
		}
		else
		{
			System.out.println(statusText);
		}
	}
	@SuppressWarnings("unused")
	public static void main(String[] args) throws LinacLegoException, SimpleXmlException, MalformedURLException, URISyntaxException  
	{
		String xmlFileDir = "xmlFiles";
		String xmlFile = "spokeLego.xml";
		String legoSetDataFile = "spokeSettings.xml";
		URL inputFileUrl = new File(xmlFileDir + File.separator + xmlFile).toURI().toURL();
		URL legoSetDataFileUrl = new File(xmlFileDir + File.separator + legoSetDataFile).toURI().toURL();
		
		SimpleXmlDoc sxd = new SimpleXmlDoc(inputFileUrl);
		LinacLego linacLego = new LinacLego(sxd);
		linacLego.setStatusPanel(null);
		linacLego.readHeader();
		linacLego.setPrintIdInTraceWin(true);
		linacLego.setReportDirectory(new File(xmlFileDir));
//		linacLego.updateLatticeSettingsUsingLegoSetData(legoSetDataFileUrl);
		linacLego.updateLegoSetDataUsingLatticeSettings("", null, true);
		linacLego.updateLinac();
		linacLego.createTraceWinFile();
		linacLego.createDynacFile();
		linacLego.printMonitorTable();
		linacLego.printBleTable();
		linacLego.printSlotTable();
		linacLego.printCellTable();
		linacLego.printSectionTable();
		linacLego.printPartCounts();
		linacLego.printLegoSets();
		linacLego.printModelInfoLinks();
		linacLego.saveXmlDocument();
		linacLego.createZipArchive();
	}

}
