package se.esss.litterbox.linaclego.structures;

import java.io.PrintWriter;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.data.LegoData;
import se.esss.litterbox.linaclego.data.LegoInfo;
import se.esss.litterbox.linaclego.utilities.LegoLatticeFileComment;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoCell 
{
	private ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();
	private ArrayList<LegoSlot> legoSlotList = new ArrayList<LegoSlot>();
	private ArrayList<LegoInfo> legoInfoList = new ArrayList<LegoInfo>();
	private String id = null;
	private LegoSection legoSection;
	private int cellListIndex = -1;
	
	public ArrayList<LegoData> getLegoDataList() {return legoDataList;}
	public ArrayList<LegoSlot> getLegoSlotList() {return legoSlotList;}
	public String getId() {return id;}
	public int getCellListIndex() {return cellListIndex;}
	public LegoSection getLegoSection() {return legoSection;}
	public LegoLinac getLegoLinac() {return legoSection.getLegoLinac();}
	public Lego getLego() {return legoSection.getLego();}
	public LegoSlot getFirstSlot() {return getLegoSlotList().get(0);}
	public LegoSlot getLastSlot() {return getLegoSlotList().get(getLegoSlotList().size() - 1);}
	public ArrayList<LegoInfo> getLegoInfoList() {return legoInfoList;}
	public void setInfoList(ArrayList<LegoInfo> legoInfoList) {this.legoInfoList = legoInfoList;}
	
	public LegoCell(LegoSection legoSection, int cellListIndex, String id)
	{
		this.legoSection = legoSection;
		this.cellListIndex = cellListIndex;
		this.id = id;
	}
	public LegoCell(LegoSection legoSection, int cellListIndex, SimpleXmlReader cellTag) throws LinacLegoException
	{
		this.legoSection = legoSection;
		this.cellListIndex = cellListIndex;
		try 
		{
			SimpleXmlReader infoTag = cellTag.tagsByName("info");
			if (infoTag.numChildTags() > 0)
			{
				for (int ii = 0; ii < infoTag.numChildTags(); ++ii)
				{
					legoInfoList.add(new LegoInfo(infoTag.tag(ii)));
				}
			}
			this.id = cellTag.attribute("id");	
			SimpleXmlReader slotTags = cellTag.tagsByName("slot");
			if (slotTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < slotTags.numChildTags(); ++ii)
				{
					getLego().writeStatus("               Adding slot " + slotTags.tag(ii).attribute("id"));
					legoSlotList.add(new LegoSlot(this, ii, slotTags.tag(ii)));
				}
			}
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public void writeXml(SimpleXmlWriter xw, boolean expandSlotTemplate) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("cell");
			xw.setAttribute("id", id);
			if (legoInfoList.size() > 0) for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeXml(xw);
			if (legoSlotList.size() > 0)
			{
				for (int ii = 0; ii < legoSlotList.size(); ++ii) legoSlotList.get(ii).writeXml(xw, expandSlotTemplate);
			}
			xw.closeXmlTag("cell");
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}
	public void printLatticeCommand(PrintWriter pw, String latticeType) throws LinacLegoException
	{
		if (legoInfoList.size() > 0) 
			for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeToLatticeFile(pw, "            ");
		pw.println(";lego " + "      " + "<cell id=\"" + getId() + "\">");
		for (int islot = 0; islot < getLegoSlotList().size(); ++islot) 
		{
			getLegoSlotList().get(islot).printLatticeCommand(pw, latticeType);
		}
		pw.println(";lego " + "      " + "</cell>");
	}
	public LegoCell getPreviousCell()
	{
		if (cellListIndex > 0) return getLegoSection().getLegoCellList().get(cellListIndex - 1);
		LegoSection previousSection = getLegoSection().getPreviousSection();
		if (previousSection != null)  return previousSection.getLegoCellList().get(previousSection.getLegoCellList().size() - 1);
		return null;
	}
	public LegoCell getNextCell()
	{
		if (cellListIndex < (getLegoSection().getLegoCellList().size() - 1)) return getLegoSection().getLegoCellList().get(cellListIndex + 1);
		LegoSection nextSection = getLegoSection().getNextSection();
		if (nextSection != null) return nextSection.getLegoCellList().get(0);
		return null;
	}
	public String getAddress() 
	{
		return getLegoSection().getAddress() + "-" + getId();
	}
	public int readLatticeFile(int iline, ArrayList<String> fileBuffer, String latticeType) throws LinacLegoException
	{
		int ilineMarker = iline + 1;
		int slotListIndex = 0;
		int infocounter = 10;
		ArrayList<LegoInfo> legoSlotInfoList = new ArrayList<LegoInfo>();
		while (ilineMarker < fileBuffer.size())
		{
			String line = fileBuffer.get(ilineMarker).trim();
			String status = "Processing Line " + Integer.toString(ilineMarker + 1) + " ";
			if (LegoLatticeFileComment.isLegoLatticeFileComment(line))
			{
				LegoLatticeFileComment llfc = new LegoLatticeFileComment(line);
				if(llfc.getKeyword().equals("/cell"))
				{
					status = status + "Finished adding cell. ";
					getLego().writeStatus(status + "\t" + line);
					return ilineMarker;
				}
				if(llfc.getKeyword().equals("slot"))
				{
					LegoSlot legoSlot  = new LegoSlot(this, slotListIndex, llfc.getAttribute("id"));
					legoSlot.setInfoList(legoSlotInfoList);
					legoSlotInfoList = new ArrayList<LegoInfo>();
					status = status + "Adding slot " + legoSlot.getId();
					getLego().writeStatus(status + "\t" + line);
					ilineMarker = legoSlot.readLatticeFile(ilineMarker, fileBuffer, latticeType);
					legoSlotList.add(legoSlot);
					slotListIndex = slotListIndex + 1;
				}
				if(llfc.getKeyword().equals("info"))
				{
					status = status + "Adding info comment";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(llfc);
					legoInfo.setId(Lego.addLeadingZeros(infocounter, 3));
					infocounter = infocounter + 10;
					legoSlotInfoList.add(legoInfo);
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
					legoSlotInfoList.add(legoInfo);
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
					legoSlotInfoList.add(legoInfo);
				}
			}
			ilineMarker = ilineMarker + 1;
		}
		return ilineMarker;
	}

}
