package se.esss.litterbox.linaclego.v2.structures;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoInfo;
import se.esss.litterbox.linaclego.v2.data.LegoLatticeFileComment;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSection  implements Serializable
{
	private static final long serialVersionUID = 6504606673665119364L;
	public static final String TABLE_HEADER       = "Section,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur";
	public static final String TABLE_HEADER_UNITS = "       ,(MeV),   ,  (m) , (m), (m), (m), (m), (m), (m)";

	private String id = null;
	private int rfHarmonic = 1;
	private LegoLinac legoLinac;
	private int sectionListIndex = -1;
	private ArrayList<LegoCell> legoCellList = new ArrayList<LegoCell>();
	private ArrayList<LegoInfo> legoInfoList = new ArrayList<LegoInfo>();
	
	public String getId() {return id;}
	public int getRfHarmonic() {return rfHarmonic;}
	public ArrayList<LegoCell> getLegoCellList() {return legoCellList;}
	public ArrayList<LegoInfo> getLegoInfoList() {return legoInfoList;}
	public LegoLinac getLegoLinac() {return legoLinac;}
	public Lego getLego() {return legoLinac.getLego();}
	public int getSectionListIndex() {return sectionListIndex;}
	public LegoCell getFirstCell() {return getLegoCellList().get(0);}
	public LegoCell getLastCell() {return getLegoCellList().get(getLegoCellList().size() - 1);}
	public void setInfoList(ArrayList<LegoInfo> legoInfoList) {this.legoInfoList = legoInfoList;}
		
	public LegoSection(LegoLinac legoLinac, int sectionListIndex, String id, int rfHarmonic)
	{
		this.legoLinac = legoLinac;
		this.sectionListIndex = sectionListIndex;
		this.id = id;
		this.rfHarmonic = rfHarmonic;
	}
	public LegoSection(LegoLinac legoLinac, int sectionListIndex, SimpleXmlReader sectionTag) throws LinacLegoException
	{
		this.legoLinac = legoLinac;
		this.sectionListIndex = sectionListIndex;
		try 
		{
			SimpleXmlReader infoTag = sectionTag.tagsByName("info");
			if (infoTag.numChildTags() > 0)
			{
				for (int ii = 0; ii < infoTag.numChildTags(); ++ii)
				{
					legoInfoList.add(new LegoInfo(infoTag.tag(ii)));
				}
			}
			this.id = sectionTag.attribute("id");	
			this.rfHarmonic = Integer.parseInt(sectionTag.attribute("rfHarmonic"));			
			SimpleXmlReader cellTags = sectionTag.tagsByName("cell");
			if (cellTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < cellTags.numChildTags(); ++ii)
				{
					getLego().writeStatus("          Adding cell " + cellTags.tag(ii).attribute("id"));
					legoCellList.add(new LegoCell(this, ii, cellTags.tag(ii)));
					getLego().writeStatus("          Finished adding cell " + cellTags.tag(ii).attribute("id"));
				}
			}
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public double rfFreqMHz() throws LinacLegoException
	{
		return getLegoLinac().beamFrequencyMHz() * rfHarmonic;
	}
	public double lamda() throws LinacLegoException
	{
		return Lego.cvel / (rfFreqMHz() * 1.0e+06);
	}
	public void writeXml(SimpleXmlWriter xw, boolean expandSlotTemplate) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("section");
			xw.setAttribute("id", id);
			xw.setAttribute("rfHarmonic", Integer.toString(rfHarmonic));
			if (legoInfoList.size() > 0) for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeXml(xw);
			if (legoCellList.size() > 0)
			{
				for (int ii = 0; ii < legoCellList.size(); ++ii) legoCellList.get(ii).writeXml(xw, expandSlotTemplate);
			}
			xw.closeXmlTag("section");
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}
	public void printLatticeCommand(PrintWriter pw, String latticeType) throws LinacLegoException
	{
		if (legoInfoList.size() > 0) 
			for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeToLatticeFile(pw, "         ");;
		pw.println(";lego " + "   " + "<section id=\"" + getId() + "\" rfHarmonic=\"" +  getRfHarmonic() + "\">");
		for (int icell = 0; icell < getLegoCellList().size(); ++icell) 
		{
			getLegoCellList().get(icell).printLatticeCommand(pw, latticeType);
		}
		pw.println(";lego " + "   " + "</section>");
	}
	public LegoSection getPreviousSection()
	{
		if (sectionListIndex > 0) return getLegoLinac().getLegoSectionList().get(sectionListIndex - 1);
		return null;
	}
	public LegoSection getNextSection()
	{
		if (sectionListIndex < (getLegoLinac().getLegoSectionList().size() - 1)) return getLegoLinac().getLegoSectionList().get(sectionListIndex + 1);
		return null;
	}
	public String getAddress() 
	{
		return getId();
	}
	public int readLatticeFile(int iline, ArrayList<String> fileBuffer, String latticeType) throws LinacLegoException
	{
		int ilineMarker = iline + 1;
		int cellListIndex = 0;
		int infocounter = 10;
		ArrayList<LegoInfo> legoCellInfoList = new ArrayList<LegoInfo>();
		while (ilineMarker < fileBuffer.size())
		{
			String line = fileBuffer.get(ilineMarker).trim();
			String status = "Processing Line " + Integer.toString(ilineMarker + 1) + " ";
			if (LegoLatticeFileComment.isLegoLatticeFileComment(line))
			{
				LegoLatticeFileComment llfc = new LegoLatticeFileComment(line);
				if(llfc.getKeyword().equals("/section"))
				{
					status = status + "Finished adding section. ";
					getLego().writeStatus(status + "\t" + line);
					return ilineMarker;
				}
				if(llfc.getKeyword().equals("cell"))
				{
					LegoCell legoCell  = new LegoCell(this, cellListIndex, llfc.getAttribute("id"));
					legoCell.setInfoList(legoCellInfoList);
					legoCellInfoList = new ArrayList<LegoInfo>();
					status = status + "Adding cell " + legoCell.getId();
					getLego().writeStatus(status + "\t" + line);
					ilineMarker = legoCell.readLatticeFile(ilineMarker, fileBuffer, latticeType);
					legoCellList.add(legoCell);
					cellListIndex = cellListIndex + 1;
				}
				if(llfc.getKeyword().equals("info"))
				{
					status = status + "Adding info comment";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(llfc);
					legoInfo.setId(Lego.addLeadingZeros(infocounter, 3));
					infocounter = infocounter + 10;
					legoCellInfoList.add(legoInfo);
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
					legoCellInfoList.add(legoInfo);
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
					legoCellInfoList.add(legoInfo);
				}
			}
			ilineMarker = ilineMarker + 1;
		}
		return ilineMarker;
	}
	public double getLength()
	{
		double length = 0.0;
		for (int icell = 0; icell < legoCellList.size(); ++ icell) length = length + legoCellList.get(icell).getLength();
		return length;
	}
	public double geteVout()
	{
		return legoCellList.get(legoCellList.size() - 1).geteVout();
	}
	public double[] getEndPosVec()
	{
		return legoCellList.get(legoCellList.size() - 1).getEndPosVec();
		
	}
	public void printTable(PrintWriter pw) throws LinacLegoException
	{
		double[] surveyCoords = getLegoLinac().getSurveyCoords(getEndPosVec());
		pw.print(getId());
		pw.print(" ," + Lego.sixPlaces.format((geteVout() / 1.0e6)));
		pw.print(" ," + Lego.sixPlaces.format(LegoBeam.beta(geteVout())));
		pw.print(" ," + Lego.sixPlaces.format(getLength()));
		pw.print(" ," + Lego.sixPlaces.format(getEndPosVec()[0]));
		pw.print(" ," + Lego.sixPlaces.format(getEndPosVec()[1]));
		pw.print(" ," + Lego.sixPlaces.format(getEndPosVec()[2]));
		pw.print(" ," +Lego. sixPlaces.format(surveyCoords[0]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[1]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[2]));
		pw.println("");
	}
}
