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
package se.esss.litterbox.linaclego.tracewinreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.utilities.StatusPanel;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class TraceWinReader 
{
	private String fileLocationPath;
	private double ekinMeV;
	private double beamFrequencyMHz;
	private SimpleXmlWriter xw;
	private StatusPanel statusPanel = null;
	private ArrayList<TraceWinCommandReader> traceWinCommandList;
	private int traceWinCommandListIndex = 0;
	
	public SimpleXmlWriter getXw() {return xw;}
	public StatusPanel getStatusPanel() {return statusPanel;}
	public ArrayList<TraceWinCommandReader> getTraceWinCommandList() {return traceWinCommandList;}

	public TraceWinReader(String fileLocationPath, double ekinMeV, double beamFrequencyMHz, StatusPanel statusPanel)
	{
		this.fileLocationPath = fileLocationPath;
		this.ekinMeV = ekinMeV;
		this.beamFrequencyMHz = beamFrequencyMHz;
		this.statusPanel = statusPanel;
	}
	public void readTraceWinFile() throws LinacLegoException 
	{
		BufferedReader br;
		ArrayList<String> outputBuffer = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(fileLocationPath));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
			br.close();
			String title = new File(fileLocationPath).getName().substring(0, new File(fileLocationPath).getName().lastIndexOf("."));
			xw = new SimpleXmlWriter("linacLego", "../dtdFiles/LinacLego.dtd");
			xw.setAttribute("title", title);
			int ilineMarker = 0;
			xw.openXmlTag("slotModels");
			xw.setAttribute("id", title + "SlotModels");
			xw.closeXmlTag("slotModels");
			xw.openXmlTag("cellModels");
			xw.setAttribute("id", title + "CellModels");
			xw.closeXmlTag("cellModels");
			xw.openXmlTag("legoSets");
			xw.setAttribute("id", title + "LegoSets");
			xw.closeXmlTag("legoSets");
			xw.openXmlTag("legoMonitors");
			xw.setAttribute("id", title + "LegoMonitors");
			xw.closeXmlTag("legoMonitors");
			xw.closeXmlTag("header");
			xw.openXmlTag("linac");
			xw.openXmlTag("linacData");

			xw.openXmlTag("d");
			xw.setAttribute("id", "ekin");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "MeV");
			xw.writeCharacterData(Double.toString(ekinMeV));
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "beamFrequency");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "MHz");
			xw.writeCharacterData(Double.toString(beamFrequencyMHz));
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "xSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "m");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "ySurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "m");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "zSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "m");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "pitchSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "rollSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "yawSurvey");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "deg");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "alphaX");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "unit");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "alphaY");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "unit");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "alphaZ");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "unit");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "betaX");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm/pi.mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "betaY");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm/pi.mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "betaZ");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mm/pi.mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "emitX");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "pi.mm.mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "emitY");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "pi.mm.mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "emitZ");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "pi.mm.mrad");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.openXmlTag("d");
			xw.setAttribute("id", "beamCurrent");
			xw.setAttribute("type", "double");
			xw.setAttribute("unit", "mA");
			xw.writeCharacterData("0.0");
			xw.closeXmlTag("d");

			xw.closeXmlTag("linacData");
			traceWinCommandList = new ArrayList<TraceWinCommandReader>();
			traceWinCommandListIndex = 0;
			writeStatus("Reading TraceWin file...");
			while (ilineMarker < outputBuffer.size())
			{
				line = outputBuffer.get(ilineMarker).trim();
				int isc = line.indexOf(";>");
				if (isc >= 0)
				{
					line = line.substring(isc + 2).trim();
					if(line.toUpperCase().indexOf("SECTION_BEGIN") >= 0)
					{
						int nameIndex = line.toUpperCase().indexOf("SECTION_BEGIN") + 13;
						ilineMarker = readTraceWinSection(ilineMarker, outputBuffer, line.substring(nameIndex).trim());
					}
				}
				ilineMarker = ilineMarker + 1;
			}
			xw.closeXmlTag("linac");
			xw.closeDocument();
			String xmlFilePath = new File(fileLocationPath).getPath().substring(0, new File(fileLocationPath).getPath().lastIndexOf(".")) + ".xml";
			xw.getSimpleXmlDoc().setXmlSourceUrl(new File(xmlFilePath).toURI().toURL());
			writeStatus("Finished reading TraceWin File.");
		} 
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
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
	private int readTraceWinSection(int ilineMarker, ArrayList<String> outputBuffer, String sectionName) throws LinacLegoException
	{
		try
		{
			xw.openXmlTag("section");
			xw.setAttribute("rfHarmonic", "1");
			xw.setAttribute("id", sectionName);
			ilineMarker = ilineMarker + 1;
			while (ilineMarker < outputBuffer.size())
			{
				String line = outputBuffer.get(ilineMarker).trim();
				int isc = line.indexOf(";>");
				if (isc >= 0)
				{
					line = line.substring(isc + 2).trim();
					if(line.toUpperCase().indexOf("SECTION_END") >= 0)
					{
						xw.closeXmlTag("section");
						return ilineMarker;
					}
					if(line.toUpperCase().indexOf("CELL_BEGIN") >= 0)
					{
						int nameIndex = line.toUpperCase().indexOf("CELL_BEGIN") + 10;
						String cellName = line.substring(nameIndex).trim();
						if (cellName.equals("")) throw new LinacLegoException("No Cell Name at line: " + Integer.toString(ilineMarker + 1));
						ilineMarker = readTraceWinCell(ilineMarker, outputBuffer, cellName);
					}
					if(line.toUpperCase().indexOf("INFO_BEGIN") >= 0)
					{
						int nameIndex = line.toUpperCase().indexOf("INFO_BEGIN") + 10;
						String infoName = line.substring(nameIndex).trim();
						if (infoName.equals("")) infoName = sectionName + "Info";
						ilineMarker = readTraceWinInfo(ilineMarker, outputBuffer, infoName);
					}
				}
				ilineMarker = ilineMarker + 1;
			}

			xw.closeXmlTag("section");
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		return ilineMarker;
	}
	private int readTraceWinInfo(int ilineMarker, ArrayList<String> outputBuffer, String infoName) throws LinacLegoException
	{
		try
		{
			xw.openXmlTag("info");
			xw.setAttribute("id", infoName);
			ilineMarker = ilineMarker + 1;
			while (ilineMarker < outputBuffer.size())
			{
				String line = outputBuffer.get(ilineMarker).trim();
				int isc = line.indexOf(";>");
				if (isc >= 0)
				{
					line = line.substring(isc + 2).trim();
					if(line.toUpperCase().indexOf("INFO_END") >= 0)
					{
						xw.closeXmlTag("info");
						return ilineMarker;
					}
					else 
					{
						throw new LinacLegoException("Did not encounter INFO_END at line: " + Integer.toString(ilineMarker + 1));
					}
				}
				else
				{
					isc = line.indexOf(";");
					if (isc >= 0 && line.length() > 1)
					{
						xw.openXmlTag("comment");
						xw.writeCharacterData(line.substring(1));
						xw.closeXmlTag("comment");
					}
				}
				
				ilineMarker = ilineMarker + 1;
			}

			xw.closeXmlTag("info");
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		return ilineMarker;
	}
	private int readTraceWinCell(int ilineMarker, ArrayList<String> outputBuffer, String cellName) throws LinacLegoException
	{
		try
		{
			xw.openXmlTag("cell");
			xw.setAttribute("id", cellName);
			ilineMarker = ilineMarker + 1;
			while (ilineMarker < outputBuffer.size())
			{
				String line = outputBuffer.get(ilineMarker).trim();
				int isc = line.indexOf(";>");
				if (isc >= 0)
				{
					line = line.substring(isc + 2).trim();
					if(line.toUpperCase().indexOf("CELL_END") >= 0)
					{
						xw.closeXmlTag("cell");
						return ilineMarker;
					}
					if(line.toUpperCase().indexOf("SLOT_BEGIN") >= 0)
					{
						int nameIndex = line.toUpperCase().indexOf("SLOT_BEGIN") + 10;
						String slotName = line.substring(nameIndex).trim();
						if (slotName.equals("")) throw new LinacLegoException("No Slot Name at line: " + Integer.toString(ilineMarker + 1));
						ilineMarker = readTraceWinSlot(ilineMarker, outputBuffer, slotName);
					}
					if(line.toUpperCase().indexOf("INFO_BEGIN") >= 0)
					{
						int nameIndex = line.toUpperCase().indexOf("INFO_BEGIN") + 10;
						String infoName = line.substring(nameIndex).trim();
						if (infoName.equals("")) infoName = cellName + "Info";
						ilineMarker = readTraceWinInfo(ilineMarker, outputBuffer, infoName);
					}
				}
				ilineMarker = ilineMarker + 1;
			}

			xw.closeXmlTag("cell");
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		return ilineMarker;
	}
	private int readTraceWinSlot(int ilineMarker, ArrayList<String> outputBuffer, String slotName) throws LinacLegoException
	{
		try
		{
			xw.openXmlTag("slot");
			xw.setAttribute("id", slotName);
			ilineMarker = ilineMarker + 1;
			int elementCount = 0;
			while (ilineMarker < outputBuffer.size())
			{
				String line = outputBuffer.get(ilineMarker).trim();
				int isc = line.indexOf(";>");
				if (isc >= 0)
				{
					line = line.substring(isc + 2).trim();
					if(line.toUpperCase().indexOf("SLOT_END") >= 0)
					{
						xw.closeXmlTag("slot");
						return ilineMarker;
					}
				}
				else
				{
					TraceWinCommandReader traceWinCommand = new TraceWinCommandReader(ilineMarker, outputBuffer.get(ilineMarker), this);
					if (traceWinCommand.getTraceWinType().length() > 0)
					{
						traceWinCommandList.add(traceWinCommand);
						traceWinCommand.setTraceWinCommandListIndex(traceWinCommandListIndex);
						if (traceWinCommandList.get(traceWinCommandListIndex).getTraceWinBleData() != null)
						{
							elementCount = elementCount + 10;
							String ecstring = addLeadingZeros(elementCount, 4);
							traceWinCommandList.get(traceWinCommandListIndex).getTraceWinBleData().createBleTag(xw, ecstring);

						}
						traceWinCommandListIndex = traceWinCommandListIndex + 1;
					}
				}
				ilineMarker = ilineMarker + 1;
			}			


			xw.closeXmlTag("slot");
		}
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		return ilineMarker;
	}
	public void saveXmlFile(String filePath) throws LinacLegoException 
	{
		try {getSimpleXmlDoc().saveXmlDocument(filePath);} catch (SimpleXmlException e) {throw new LinacLegoException();}
	}
	public SimpleXmlDoc getSimpleXmlDoc() {return xw.getSimpleXmlDoc();}
	public static void main(String[] args) throws LinacLegoException 
	{
		String path = "/home/dmcginnis427/Dropbox/gitRepositories/lattice-repository/LatticeRepository/WebContent/data/test2Xml/TraceWinData/5.0_Spoke.dat";
		TraceWinReader twr = new TraceWinReader(path, 90, 352.21, null);
		twr.readTraceWinFile();
		twr.saveXmlFile("/home/dmcginnis427/Dropbox/gitRepositories/lattice-repository/LatticeRepository/WebContent/data/test2Xml/TraceWinData/5.0_Spoke.xml");
	}

}
