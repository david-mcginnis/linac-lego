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

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.FieldProfileBuilder;
import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElementModelReporter;
import se.esss.litterbox.linaclego.structures.cell.CellModelReporter;
import se.esss.litterbox.linaclego.structures.legomonitor.LegoMonitor;
import se.esss.litterbox.linaclego.structures.legomonitor.LegoMonitorModelReporter;
import se.esss.litterbox.linaclego.structures.legoset.TransferFunction;
import se.esss.litterbox.linaclego.structures.slot.SlotModelReporter;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;


public class Linac 
{
	public static final double PI = Math.PI;
	public static final double degToRad = PI / 180.0;
	public static final DecimalFormat onePlaces = new DecimalFormat("###.#");
	public static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final DecimalFormat eightPlaces = new DecimalFormat("###.########");
	public static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	private ArrayList<Section> sectionList = new ArrayList<Section>();
	private double eVin = 0.0;
	private double eVout = 0.0;
	private double beamFrequencyMHz  = -1.0;
	private LinacLego linacLego = null;
	private double length = 0.0;
	private ArrayList<BeamLineElement> beamLineElementList =  new ArrayList<BeamLineElement>();
	private double[][] eulerMatrix   = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
	private double[] transVec   = {0.0, 0.0, 0.0}; 
	private FieldProfileBuilder fieldProfileBuilder = null;
	private URL fieldProfileBuilderUrl = new URL("http://test.com");

	private ArrayList<LegoData> dataElementList = new ArrayList<LegoData>();


	private LegoData[][] twissData = new LegoData[3][3];
	private LegoData[] surveyTranslationData = new LegoData[3];
	private LegoData[] surveyAngleData = new LegoData[3];
	private LegoData beamCurrent =  new LegoData("beamCurrent",  "beamCurrent",  "double", "mA");;
	
	public Linac(LinacLego linacLego) throws Exception
	{
		this.linacLego = linacLego;
		LegoData ekinDataElement  = new LegoData("ekin", "ekin", "double", "MeV");
		LegoData beamFrequencyDataElement  = new LegoData("beamFrequency", "beamFrequency", "double", "MHz");
		readDataElementValue(ekinDataElement);
		readDataElementValue(beamFrequencyDataElement);

		eVin = Double.parseDouble(ekinDataElement.getValue()) * 1.0e+06;
		beamFrequencyMHz = Double.parseDouble(beamFrequencyDataElement.getValue());
		eVout = eVin;
		length = 0.0;
		readInputTwissData();
		readSurveyData();
		SimpleXmlReader sectionTags = linacLego.getLinacTag().tagsByName("section");
		for (int isec = 0; isec < sectionTags.numChildTags(); ++isec)
		{
			Section newSection = new Section(sectionTags.tag(isec), this, isec);
			sectionList.add(newSection);
			length = length + newSection.getLength();
		}
		eVout = beamLineElementList.get(beamLineElementList.size() - 1).geteVout();
		addLegoMonitorsToLattice();
	}
	public void addLegoMonitorsToLattice() throws SimpleXmlException, LinacLegoException
	{
		linacLego.writeStatus("Reading LegoMonitors...");
		String sectionName  = null;
		String cellName = null;
		String slotName = null;
		String bleName = null;
		boolean bleFound;
		int numBle = beamLineElementList.size();
		int ible;
		BeamLineElement ble = null;
		
		SimpleXmlReader legoMonitorsListTag = linacLego.getLinacLegoTag().tagsByName("header").tag(0).tagsByName("legoMonitors");
		if (legoMonitorsListTag.numChildTags() > 0)
		{
			for (int icol = 0; icol < legoMonitorsListTag.numChildTags(); ++icol)
			{
				SimpleXmlReader legoMonitorTags = legoMonitorsListTag.tag(icol).tagsByName("legoMonitor");
				if (legoMonitorTags.numChildTags() > 0)
				{
					for (int itag = 0; itag < legoMonitorTags.numChildTags(); ++itag)
					{
						sectionName = legoMonitorTags.tag(itag).attribute("section");
						cellName = legoMonitorTags.tag(itag).attribute("cell");
						slotName = legoMonitorTags.tag(itag).attribute("slot");
						bleName = legoMonitorTags.tag(itag).attribute("ble");
						bleFound = false;
						ible = 0;
						while ((ible < numBle) && !bleFound)
						{
							ble = beamLineElementList.get(ible);
							if (ble.getId().equals(bleName))
								if (ble.getSlot().getId().equals(slotName))
									if (ble.getSlot().getCell().getId().equals(cellName))
										if (ble.getSlot().getCell().getSection().getId().equals(sectionName))
										{
											bleFound = true;
											ble.getLegoMonitorList().add(new LegoMonitor(legoMonitorTags.tag(itag), ble, ble.getLegoMonitorList().size()));
										}
							ible = ible + 1;
						}
						if (!bleFound)
						{
							throw new LinacLegoException("Cannot find LegoMonitor: " + sectionName + "-" + cellName + "-" + slotName + "-" + bleName);
						}
					}
				}
			}
		}
		linacLego.writeStatus("Finished reading LegoMonitors");
	}
	public void printLegoSets(String fileName) throws FileNotFoundException, LinacLegoException 
	{
		PrintWriter pw = new PrintWriter(fileName);
		pw.println("BLE devName,BLE data,BLE value,BLE unit,LinacSet devName,LinacSet Value,LinacSet Unit," + TransferFunction.getTfCsvDataHeader());
		for (int isec = 0; isec < getNumOfSections(); ++isec)
		{
			sectionList.get(isec).printLegoSets(pw);
		}
		pw.close();
	}
	public void printPartCounts(String fileName) throws FileNotFoundException, LinacLegoException
	{
		PrintWriter pwCells = new PrintWriter(fileName + "CellParts.csv");
		PrintWriter pwSlots = new PrintWriter(fileName + "SlotParts.csv");
		PrintWriter pwBles = new PrintWriter(fileName + "BleParts.csv");
		PrintWriter pwMons = new PrintWriter(fileName + "MonitorParts.csv");
		LegoMonitorModelReporter legoMonitorModelReporter = new LegoMonitorModelReporter(this);
		BeamLineElementModelReporter beamLineElementModelReporter = new BeamLineElementModelReporter(this);
		SlotModelReporter slotModelReporter = new SlotModelReporter(this);
		CellModelReporter cellModelReporter = new CellModelReporter(this);
		pwCells.print("type" +"," + "model");
		pwSlots.print("type" +"," + "model");
		pwBles.print("type" +"," + "model");
		pwMons.print("type" +"," + "model");
		for (int isection = 0; isection < getNumOfSections(); ++isection)
		{
			pwCells.print("," + getSectionList().get(isection).getId());
			pwSlots.print("," + getSectionList().get(isection).getId());
			pwBles.print("," + getSectionList().get(isection).getId());
			pwMons.print("," + getSectionList().get(isection).getId());
		}	
		pwCells.println(",Total");
		pwSlots.println(",Total");
		pwBles.println(",Total,minValue, avgValue,maxValue,Unit");
		pwMons.println(",Total");
		cellModelReporter.printModels(pwCells, this);
		slotModelReporter.printModels(pwSlots, this);
		beamLineElementModelReporter.printModels(pwBles, this);
		legoMonitorModelReporter.printModels(pwMons, this);
		pwCells.close();
		pwSlots.close();
		pwBles.close();
		pwMons.close();
	}
	public Section getLatticeSection(String sectionId) throws LinacLegoException 
	{
		Section matchingSection = null;
		for (int isec = 0; isec < sectionList.size(); ++isec)
		{
			if (sectionList.get(isec).getId().equals(sectionId)) matchingSection = sectionList.get(isec);
		}
		return matchingSection;
	}
	public double[] getSurveyCoords(double[] linacCoords)
	{
		double[] surveyCoords = {0.0, 0.0, 0.0};
		for (int ir = 0; ir < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)
			{
				surveyCoords[ir] = surveyCoords[ir] + eulerMatrix[ir][ic] * linacCoords[ic];
			}
			surveyCoords[ir] = surveyCoords[ir] + transVec[ir];
		}
		return surveyCoords;
	}
	private void readInputTwissData() throws  LinacLegoException
	{
		twissData[0][0] = new LegoData("alphaX", "alphaX", "double", "unit");
		twissData[0][1] = new LegoData("betaX",  "betaX",  "double", "mm/pi.mrad");
		twissData[0][2] = new LegoData("emitX",  "emitX",  "double", "pi.mm.mrad");
		twissData[1][0] = new LegoData("alphaY", "alphaY", "double", "unit");
		twissData[1][1] = new LegoData("betaY",  "betaY",  "double", "mm/pi.mrad");
		twissData[1][2] = new LegoData("emitY",  "emitY",  "double", "pi.mm.mrad");
		twissData[2][0] = new LegoData("alphaZ", "alphaZ", "double", "unit");
		twissData[2][1] = new LegoData("betaZ",  "betaZ",  "double", "mm/pi.mrad");
		twissData[2][2] = new LegoData("emitZ",  "emitZ",  "double", "pi.mm.mrad");
		for (int ip = 0; ip < 3; ++ip)
		{
			for (int ii = 0; ii < 3; ++ii)
			{
				readDataElementValue(twissData[ip][ii]);
			}
		}
		readDataElementValue(beamCurrent);
		return;
		
	}
	private void readDataElementValue(LegoData dataElement) throws LinacLegoException
	{
		SimpleXmlReader twissTag = null;
		try 
		{
			twissTag = linacLego.getLinacTag().tagsByName("linacData").tag(0).tagsByName("d").getTagMatchingAttribute("id", dataElement.getId());
			dataElementList.add(new LegoData(twissTag));
		} catch (SimpleXmlException e1) 
		{
			throw new LinacLegoException("Linac tag " + dataElement.getId() + ": " + e1.getMessage());
		}
		if (twissTag != null) 
		{
			try 
			{
				if (!twissTag.attribute("unit").equals(dataElement.getUnit())) 
					throw new LinacLegoException("Linac tag " +  dataElement.getId() + " unit does not match required unit of " + dataElement.getUnit());
			} 
			catch (SimpleXmlException e) 
			{
				throw new LinacLegoException("Linac tag " + dataElement.getId() + ": " + e.getMessage());
			}
			dataElement.setValue(twissTag.getCharacterData()); 
		}
	}
	public String makeDynacHeader() throws LinacLegoException
	{
		String command = getLinacLego().getLinacLegoTitle() + "\nGEBEAM\n2\t1\n";
		command = command + zeroPlaces.format(beamFrequencyMHz * 1e-06) + "\t1000\n";
		command = command + "0.0\t0.0\t0.0\t0.0\t0.0\t0.0\n";
		for (int ip = 0; ip < 3; ++ip)
		{
			for (int ii = 0; ii < 3; ++ii) command = command + twissData[ip][ii].getValue() + "\t";
			command = command + "\n";
		}
		command = command + "INPUT\n938.2796\t1.0\t1.0\n";
		command = command + fourPlaces.format(eVin * 1.0e-06) + "\t0.0\n";
		command = command + "REFCOG\n0\n";
//TODO finish this		
		return command;
	}
	private void readSurveyData() throws LinacLegoException
	{
		double pitch = 0.0;
		double roll = 0.0;
		double yaw = 0.0;
		for (int ir = 0; ir < 3; ++ir) transVec[ir] = 0;

		surveyTranslationData[0] = new LegoData("xSurvey", "xSurvey", "double", "m");
		surveyTranslationData[1] = new LegoData("ySurvey", "ySurvey", "double", "m");
		surveyTranslationData[2] = new LegoData("zSurvey", "zSurvey", "double", "m");
		surveyAngleData[0]  = new LegoData("pitchSurvey", "pitchSurvey", "double", "deg");
		surveyAngleData[1]  = new LegoData("rollSurvey", "rollSurvey", "double", "deg");
		surveyAngleData[2]  = new LegoData("yawSurvey", "yawSurvey", "double", "deg");
		for (int ii = 0; ii < 3; ++ii)
		{
			readDataElementValue(surveyTranslationData[ii]);
			if (surveyTranslationData[ii].valueMatchsType()) transVec[ii] = Double.parseDouble(surveyTranslationData[ii].getValue());
			readDataElementValue(surveyAngleData[ii]);
		}
		if (surveyAngleData[0].valueMatchsType()) pitch = Double.parseDouble(surveyAngleData[0].getValue()) * degToRad;
		if (surveyAngleData[1].valueMatchsType()) roll = Double.parseDouble(surveyAngleData[0].getValue()) * degToRad;
		if (surveyAngleData[2].valueMatchsType()) yaw = Double.parseDouble(surveyAngleData[0].getValue()) * degToRad;


		
		double[][] pitchMatrix = {{1.0, 0.0, 0.0}, {0.0, Math.cos(pitch), Math.sin(pitch)}, {0.0, -Math.sin(pitch), Math.cos(pitch)}};
		double[][] yawMatrix  = {{Math.cos(yaw), 0.0, -Math.sin(yaw)}, {0.0, 1.0, 0.0}, {Math.sin(yaw), 0.0, Math.cos(yaw)}};
		double[][] rollMatrix   = {{Math.cos(roll), Math.sin(roll), 0.0}, {-Math.sin(roll), Math.cos(roll), 0.0}, {0.0, 0.0, 1.0}};
		double[][] pyMatrix   = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
		
		for (int ir = 0; ir < 3; ++ir)
			for (int ic = 0; ic < 3; ++ic)
			{
				eulerMatrix[ir][ic] = 0.0;
				for (int ik = 0; ik < 3; ++ik)
					pyMatrix[ir][ic] = pyMatrix[ir][ic] + pitchMatrix[ir][ik] * yawMatrix[ik][ic];
			}
		for (int ir = 0; ir < 3; ++ir)
			for (int ic = 0; ic < 3; ++ic)
				for (int ik = 0; ik < 3; ++ik)
					eulerMatrix[ir][ic] = eulerMatrix[ir][ic] + rollMatrix[ir][ik] * pyMatrix[ik][ic];
	}
	public ArrayList<Section> getSectionList() {return sectionList;}
	public double geteVin() {return eVin;}
	public double geteVout() {return eVout;}
	public LinacLego getLinacLego() {return linacLego;}
	public double getLength() {return length;}
	public int getNumOfSections() {return sectionList.size();}
	public ArrayList<BeamLineElement> getBeamLineElements() {return beamLineElementList;}
	public int getNumOfBeamLineElements() {return beamLineElementList.size();}
	public double getBeamFrequencyMHz() {return beamFrequencyMHz;}
	public FieldProfileBuilder getFieldProfileBuilder() {return fieldProfileBuilder;}
	public LegoData[][] getTwissData() {return twissData;}
	public LegoData getBeamCurrent() {return beamCurrent;}
	public ArrayList<LegoData> getDataElementList() {return dataElementList;}
	public URL getFieldProfileBuilderUrl() {return fieldProfileBuilderUrl;}

	public void setFieldProfileBuilder(FieldProfileBuilder fieldProfileBuilder, URL fieldProfileBuilderUrl) 
	{
		this.fieldProfileBuilder = fieldProfileBuilder;
		this.fieldProfileBuilderUrl = fieldProfileBuilderUrl;
	}


}
