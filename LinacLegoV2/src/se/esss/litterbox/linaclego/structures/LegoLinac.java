package se.esss.litterbox.linaclego.structures;

import java.io.PrintWriter;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.data.LegoData;
import se.esss.litterbox.linaclego.data.LegoInfo;
import se.esss.litterbox.linaclego.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.utilities.LegoLatticeFileComment;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoLinac 
{
	private ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();
	private ArrayList<LegoSection> legoSectionList = new ArrayList<LegoSection>();
	private ArrayList<LegoInfo> legoInfoList = new ArrayList<LegoInfo>();
	private Lego lego = null;
	private double[][] eulerMatrix   = {{0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}, {0.0, 0.0, 0.0}};
	private double[] transVec   = {0.0, 0.0, 0.0}; 
	
	public ArrayList<LegoData> getLegoDataList() {return legoDataList;}
	public ArrayList<LegoSection> getLegoSectionList() {return legoSectionList;}
	public ArrayList<LegoInfo> getLegoInfoList() {return legoInfoList;}
	public LegoSection getFirstSection() {return getLegoSectionList().get(0);}
	public LegoSection getLastSection() {return getLegoSectionList().get(getLegoSectionList().size() - 1);}
	public Lego getLego() {return lego;}
	public void setInfoList(ArrayList<LegoInfo> legoInfoList) {this.legoInfoList = legoInfoList;}
	
	public LegoLinac(Lego lego)
	{
		legoDataList.add(new LegoData("ekin", "0.0", "double", "MeV"));
		legoDataList.add(new LegoData("beamFrequency", "0.0", "double", "MHz"));
		legoDataList.add(new LegoData("xSurvey", "0.0", "double", "m"));
		legoDataList.add(new LegoData("ySurvey", "0.0", "double", "m"));
		legoDataList.add(new LegoData("zSurvey", "0.0", "double", "m"));
		legoDataList.add(new LegoData("pitchSurvey", "0.0", "double", "deg"));
		legoDataList.add(new LegoData("rollSurvey", "0.0", "double", "deg"));
		legoDataList.add(new LegoData("yawSurvey", "0.0", "double", "deg"));
		legoDataList.add(new LegoData("alphaX", "0.0", "double", "unit"));
		legoDataList.add(new LegoData("alphaY", "0.0", "double", "unit"));
		legoDataList.add(new LegoData("alphaZ", "0.0", "double", "unit"));
		legoDataList.add(new LegoData("betaX", "0.0", "double", "mm/pi.mrad"));
		legoDataList.add(new LegoData("betaY", "0.0", "double", "mm/pi.mrad"));
		legoDataList.add(new LegoData("betaZ", "0.0", "double", "mm/pi.mrad"));
		legoDataList.add(new LegoData("emitX", "0.0", "double", "pi.mm.mrad"));
		legoDataList.add(new LegoData("emitY", "0.0", "double", "pi.mm.mrad"));
		legoDataList.add(new LegoData("emitZ", "0.0", "double", "pi.mm.mrad"));
		legoDataList.add(new LegoData("beamCurrent", "0.0", "double", "mA"));	
		this.lego = lego;
	}
	public LegoLinac(Lego lego, double ekinMeV, double beamFrequencyMHz)
	{
		this(lego);
		LegoData.findLegoDataById(legoDataList, "ekin").setValue(Double.toString(ekinMeV));
		LegoData.findLegoDataById(legoDataList, "beamFrequency").setValue(Double.toString(beamFrequencyMHz));
	}
	public double ekinMeVIn() throws LinacLegoException
	{
		return Double.parseDouble(LegoData.findLegoDataById(legoDataList, "ekin").getValue());
	}
	public double beamFrequencyMHz() throws LinacLegoException
	{
		return Double.parseDouble(LegoData.findLegoDataById(legoDataList, "beamFrequency").getValue());
	}
	public LegoLinac(Lego lego, SimpleXmlReader linacTag) throws LinacLegoException
	{
		this(lego);
		try 
		{
			getLego().writeStatus("     Adding linacInfo");
			SimpleXmlReader infoTag = linacTag.tagsByName("info");
			if (infoTag.numChildTags() > 0)
			{
				for (int ii = 0; ii < infoTag.numChildTags(); ++ii)
				{
					legoInfoList.add(new LegoInfo(infoTag.tag(ii)));
				}
			}

			SimpleXmlReader dataTags = linacTag.tagsByName("linacData").tag(0).tagsByName("d");
			if (dataTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < dataTags.numChildTags(); ++ii)
				{
					getLego().writeStatus("          Adding linacData " + dataTags.tag(ii).attribute("id"));
					LegoData ld  = LegoData.findLegoDataById(legoDataList, dataTags.tag(ii).attribute("id"));
					ld.setType(dataTags.tag(ii).attribute("type"));
					ld.setUnit(dataTags.tag(ii).attribute("unit"));
					ld.setValue(dataTags.tag(ii).getCharacterData());
				}
			}
			getLego().writeStatus("     Finished Adding linacData");
			SimpleXmlReader sectionTags = linacTag.tagsByName("section");
			if (sectionTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < sectionTags.numChildTags(); ++ii)
				{
					getLego().writeStatus("     Adding section " + sectionTags.tag(ii).attribute("id"));
					legoSectionList.add(new LegoSection(this, ii, sectionTags.tag(ii)));
					getLego().writeStatus("     Finished adding section " + sectionTags.tag(ii).attribute("id"));
				}
			}
			
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public void writeXml(SimpleXmlWriter xw, boolean expandSlotTemplate) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("linac");
			if (legoInfoList.size() > 0) for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeXml(xw);
			xw.openXmlTag("linacData");
			if (legoDataList.size() > 0) for (int ii = 0; ii < legoDataList.size(); ++ii) legoDataList.get(ii).writeXml(xw);
			xw.closeXmlTag("linacData");
			if (legoSectionList.size() > 0) for (int ii = 0; ii < legoSectionList.size(); ++ii) legoSectionList.get(ii).writeXml(xw, expandSlotTemplate);
			xw.closeXmlTag("linac");
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}
	private void calculateSurveyMatrices() throws LinacLegoException
	{
		transVec[0] = Double.parseDouble(LegoData.findLegoDataById(legoDataList, "xSurvey").getValue());
		transVec[1] = Double.parseDouble(LegoData.findLegoDataById(legoDataList, "ySurvey").getValue());
		transVec[2] = Double.parseDouble(LegoData.findLegoDataById(legoDataList, "zSurvey").getValue());

		double pitch = Double.parseDouble(LegoData.findLegoDataById(legoDataList, "pitchSurvey").getValue()) * Lego.degToRad;
		double roll = Double.parseDouble(LegoData.findLegoDataById(legoDataList, "rollSurvey").getValue()) * Lego.degToRad;
		double yaw = Double.parseDouble(LegoData.findLegoDataById(legoDataList, "yawSurvey").getValue()) * Lego.degToRad;

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
	public void triggerUpdate() throws LinacLegoException
	{
		getLego().writeStatus("Triggering Update");
		getLego().writeStatus("     Calculating Survey Matrices");
		calculateSurveyMatrices();
		for (int isec = 0; isec < getLegoSectionList().size(); ++isec) 
		{
			LegoSection legoSection = getLegoSectionList().get(isec);
			getLego().writeStatus("     Updating section " + legoSection.getId());
			for (int icell = 0; icell < legoSection.getLegoCellList().size(); ++icell) 
			{
				LegoCell legoCell = legoSection.getLegoCellList().get(icell);
				getLego().writeStatus("          Updating cell " + legoCell.getId());
				for (int islot = 0; islot < legoCell.getLegoSlotList().size(); ++islot) 
				{
					LegoSlot legoSlot = legoCell.getLegoSlotList().get(islot);
					getLego().writeStatus("               Updating slot " + legoSlot.getId());
					for (int ibeam = 0; ibeam < legoSlot.getLegoBeamList().size(); ++ibeam) 
					{
						LegoBeam legoBeam = legoSlot.getLegoBeamList().get(ibeam);
						getLego().writeStatus("                    Updating beam " + legoBeam.getId());
						legoBeam.triggerUpdate();
					}
				}
			}
		}
		getLego().writeStatus("Finished Triggering Update");
		getLego().writeStatus("Starting Energy: " + Lego.twoPlaces.format(getFirstSection().getFirstCell().getFirstSlot().getFirstBeam().geteVin() * 1.0e-06) + " MeV");
		getLego().writeStatus("Ending   Energy: " + Lego.twoPlaces.format(getLastSection().getLastCell().getLastSlot().getLastBeam().geteVout() * 1.0e-06) + " MeV");
		getLego().writeStatus("x:               " + Lego.twoPlaces.format(getLastSection().getLastCell().getLastSlot().getLastBeam().getEndPosVec()[0]) + " m");
		getLego().writeStatus("y:               " + Lego.twoPlaces.format(getLastSection().getLastCell().getLastSlot().getLastBeam().getEndPosVec()[1]) + " m");
		getLego().writeStatus("z:               " + Lego.twoPlaces.format(getLastSection().getLastCell().getLastSlot().getLastBeam().getEndPosVec()[2]) + " m");
	}
	public void printLatticeCommand(PrintWriter pw, String latticeType) throws LinacLegoException
	{
		if (legoInfoList.size() > 0) 
			for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeToLatticeFile(pw, "");
		pw.println(";lego " + "" + "<linac>");
		for (int isec = 0; isec < getLegoSectionList().size(); ++isec) 
		{
			getLegoSectionList().get(isec).printLatticeCommand(pw, latticeType);
		}
		pw.println(";lego " + "" + "</linac>");
	}
	public void printBeamTable(PrintWriter pw) throws LinacLegoException 
	{
		pw.println(LegoBeam.BEAM_TABLE_HEADER);
		pw.println(LegoBeam.BEAM_TABLE_HEADER_UNITS);
		for (int isec = 0; isec < getLegoSectionList().size(); ++isec) 
		{
			LegoSection legoSection = getLegoSectionList().get(isec);
			for (int icell = 0; icell < legoSection.getLegoCellList().size(); ++icell) 
			{
				LegoCell legoCell = legoSection.getLegoCellList().get(icell);
				for (int islot = 0; islot < legoCell.getLegoSlotList().size(); ++islot) 
				{
					LegoSlot legoSlot = legoCell.getLegoSlotList().get(islot);
					for (int ibeam = 0; ibeam < legoSlot.getLegoBeamList().size(); ++ibeam) 
					{
						LegoBeam legoBeam = legoSlot.getLegoBeamList().get(ibeam);
						legoBeam.printBeamTable(pw);
					}
				}
			}
		}
	}
	public int readLatticeFile(int iline, ArrayList<String> fileBuffer, String latticeType) throws LinacLegoException
	{
		int ilineMarker = iline + 1;
		String line;
		int sectionListIndex = 0;
		int infocounter = 10;
		ArrayList<LegoInfo> legoSectionInfoList = new ArrayList<LegoInfo>();

		while (ilineMarker < fileBuffer.size())
		{
			line = fileBuffer.get(ilineMarker).trim();
			String status = "Processing Line " + Integer.toString(ilineMarker + 1) + " ";
			if (LegoLatticeFileComment.isLegoLatticeFileComment(line))
			{
				LegoLatticeFileComment llfc = new LegoLatticeFileComment(line);
				if(llfc.getKeyword().equals("/linac"))
				{
					status = status + "Finished adding linac. ";
					getLego().writeStatus(status + "\t" + line);
					return ilineMarker;
				}
				if(llfc.getKeyword().equals("section"))
				{
					int rfHarmonic = Integer.parseInt(llfc.getAttribute("rfHarmonic"));
					LegoSection legoSection  = new LegoSection(this, sectionListIndex, llfc.getAttribute("id"), rfHarmonic);
					legoSection.setInfoList(legoSectionInfoList);
					legoSectionInfoList = new ArrayList<LegoInfo>();
					status = status + "Adding section " + legoSection.getId();
					getLego().writeStatus(status + "\t" + line);
					ilineMarker = legoSection.readLatticeFile(ilineMarker, fileBuffer, latticeType);
					legoSectionList.add(legoSection);
					sectionListIndex = sectionListIndex + 1;
				}
				if(llfc.getKeyword().equals("info"))
				{
					status = status + "Adding info comment";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(llfc);
					legoInfo.setId(Lego.addLeadingZeros(infocounter, 3));
					infocounter = infocounter + 10;
					legoSectionInfoList.add(legoInfo);
				}
			}
			if ((line.indexOf(";") == 0) && (line.indexOf(";lego") < 0))
			{
				if (line.length() > 1)
				{
					status = status + "Adding info comment";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(Lego.addLeadingZeros(infocounter, 3), line.substring(1), "comment");
					infocounter = infocounter + 10;
					legoSectionInfoList.add(legoInfo);
				}
			}
			if (line.indexOf(";") < 0)
			{
				if (line.length() > 1)
				{
					status = status + "Adding info tune";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(Lego.addLeadingZeros(infocounter, 3), line, "tune");
					infocounter = infocounter + 10;
					legoSectionInfoList.add(legoInfo);
				}
			}
			ilineMarker = ilineMarker + 1;
		}
		return ilineMarker;
	}
}
