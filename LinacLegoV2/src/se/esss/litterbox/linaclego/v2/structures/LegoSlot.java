package se.esss.litterbox.linaclego.v2.structures;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoData;
import se.esss.litterbox.linaclego.v2.data.LegoInfo;
import se.esss.litterbox.linaclego.v2.data.LegoLatticeFileComment;
import se.esss.litterbox.linaclego.v2.data.LegoModel;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.v2.templates.LegoBeamTemplate;
import se.esss.litterbox.linaclego.v2.templates.LegoSlotTemplate;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSlot  implements Serializable
{
	private static final long serialVersionUID = -2766328744732348452L;
	public static final String TABLE_HEADER       = "Section,Cell,Slot,Model,#drawing,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur";
	public static final String TABLE_HEADER_UNITS = "       ,    ,    ,     ,#       ,(MeV),   ,  (m) , (m), (m), (m), (m), (m), (m)";

	private ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();
	private ArrayList<LegoBeam> legoBeamList = new ArrayList<LegoBeam>();
	private ArrayList<LegoInfo> legoInfoList = new ArrayList<LegoInfo>();
	private String id = null;
	private String template = null;
	private int slotListIndex = -1;
	private LegoCell legoCell;
	private String drawingLocation = null;

	public ArrayList<LegoData> getLegoDataList() {return legoDataList;}
	public ArrayList<LegoBeam> getLegoBeamList() {return legoBeamList;}
	public String getId() {return id;}
	public String getTemplate() {return template;}
	public int getSlotListIndex() {return slotListIndex;}
	public LegoCell getLegoCell() {return legoCell;}
	public LegoSection getLegoSection() {return legoCell.getLegoSection();}
	public LegoLinac getLegoLinac() {return getLegoSection().getLegoLinac();}
	public Lego getLego() {return getLegoSection().getLego();}
	public LegoBeam getFirstBeam() {return getLegoBeamList().get(0);}
	public LegoBeam getLastBeam() {return getLegoBeamList().get(getLegoBeamList().size() - 1);}
	public ArrayList<LegoInfo> getLegoInfoList() {return legoInfoList;}
	public String getDrawingLocation() {return drawingLocation;}
	public void setInfoList(ArrayList<LegoInfo> legoInfoList) {this.legoInfoList = legoInfoList;}

	public LegoSlot(LegoCell legoCell, int slotListIndex, String id)
	{
		this.legoCell = legoCell;
		this.slotListIndex = slotListIndex;
		this.id = id;
	}
	public LegoSlot(LegoCell legoCell, int slotListIndex, SimpleXmlReader slotTag) throws LinacLegoException
	{
		this.legoCell = legoCell;
		this.slotListIndex = slotListIndex;
		try {this.id = slotTag.attribute("id");	} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		try {this.template = slotTag.attribute("template");	} catch (SimpleXmlException e)  {template = null;}
		if (template == null)
		{
			SimpleXmlReader infoTag = slotTag.tagsByName("info");
			if (infoTag.numChildTags() > 0)
			{
				for (int ii = 0; ii < infoTag.numChildTags(); ++ii)
				{
					try {legoInfoList.add(new LegoInfo(infoTag.tag(ii)));} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
				}
			}
			SimpleXmlReader beamTags = slotTag.tagsByName("beam");
			if (beamTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < beamTags.numChildTags(); ++ii)
				{
					try 
					{
						getLego().writeStatus("                    Adding beam " + beamTags.tag(ii).attribute("id"));
						String type = beamTags.tag(ii).attribute("type");
						int ibeamType = 0;
						LegoBeam beamType = null;
						while (ibeamType < getLego().getBeamTypes().size())
						{
							if (getLego().getBeamTypes().get(ibeamType).getType().equals(type))
							{
								beamType = getLego().getBeamTypes().get(ibeamType);
								ibeamType = getLego().getBeamTypes().size();
							}
							ibeamType = ibeamType + 1;
						}
						if (beamType != null) 
						{
							legoBeamList.add(beamType.getClass().getConstructor(LegoSlot.class, int.class, SimpleXmlReader.class).newInstance(this, ii, beamTags.tag(ii)));
						}
						else
						{
							throw new LinacLegoException("No such type " + type + " found");
						}
					} catch (Exception e)  {throw new LinacLegoException(e);}
				}
			}
			getLego().writeStatus("               Finished adding slot " + id);

		}
		else
		{
			SimpleXmlReader dataTags = slotTag.tagsByName("d");
			if (dataTags.numChildTags() > 0)
			{
				for (int ii = 0; ii < dataTags.numChildTags(); ++ii)
				{
					try {legoDataList.add(new LegoData(dataTags.tag(ii)));} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
				}
			}
			LegoSlotTemplate legoSlotTemplate =  LegoSlotTemplate.findLegoTemplateById(getLego().getLegoSlotTempateList(), template);
			if (legoSlotTemplate == null) throw new LinacLegoException("Slot Template " + template + " not found");
			drawingLocation = legoSlotTemplate.getDrawingLocation();
			legoSlotTemplate.createBeamList(this);
		}
	}
	public LegoBeam getLegoBeamById(String beamId)
	{
		int ibeam = 0;
		while (ibeam < legoBeamList.size())
		{
			if (legoBeamList.get(ibeam).getId().equals(beamId)) return legoBeamList.get(ibeam);
			ibeam = ibeam + 1;
		}
		return null;
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("slot");
			xw.setAttribute("id", id);
			if (template != null)
			{
				xw.setAttribute("template", template);
				if (legoDataList.size() > 0)
				{
					for (int ii = 0; ii < legoDataList.size(); ++ii) legoDataList.get(ii).writeXml(xw);
				}
			}
			if (template == null)
			{
				if (legoInfoList.size() > 0) for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeXml(xw);
				if (legoBeamList.size() > 0)
				{
					for (int ii = 0; ii < legoBeamList.size(); ++ii) legoBeamList.get(ii).writeXml(xw);
				}
			}
			xw.closeXmlTag("slot");
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public void printLatticeCommand(PrintWriter pw, String latticeType) throws LinacLegoException
	{
		if (legoInfoList.size() > 0) 
			for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeToLatticeFile(pw, "               ");
		pw.println(";lego " + "         " + "<slot id=\"" + getId() + "\">");
		for (int ibeam = 0; ibeam < getLegoBeamList().size(); ++ibeam) 
		{
			getLegoBeamList().get(ibeam).printLatticeCommand(pw, latticeType);
		}
		pw.println(";lego " + "         " + "</slot>");
	}
	public LegoSlot getPreviousSlot()
	{
		if (slotListIndex > 0) return getLegoCell().getLegoSlotList().get(slotListIndex - 1);
		LegoCell previousCell = getLegoCell().getPreviousCell();
		if (previousCell != null) 
		{
			return previousCell.getLegoSlotList().get(previousCell.getLegoSlotList().size() - 1);
		}
		return null;
	}
	public LegoSlot getNextSlot()
	{
		if (slotListIndex < (getLegoCell().getLegoSlotList().size() - 1)) return getLegoCell().getLegoSlotList().get(slotListIndex + 1);
		LegoCell nextCell = getLegoCell().getNextCell();
		if (nextCell != null) return nextCell.getLegoSlotList().get(0);
		return null;
	}
	public String getAddress() 
	{
		return getLegoCell().getAddress() + "-" + getId();
	}
	public int readLatticeFile(int iline, ArrayList<String> fileBuffer, String latticeType) throws LinacLegoException
	{
		int ilineMarker = iline + 1;
		int beamListIndex = 0;
		int infocounter = 10;
		ArrayList<LegoInfo> legoBeamInfoList = new ArrayList<LegoInfo>();
		while (ilineMarker < fileBuffer.size())
		{
			String line = fileBuffer.get(ilineMarker).trim();
			String status = "Processing Line " + Integer.toString(ilineMarker + 1) + " ";
			if (LegoLatticeFileComment.isLegoLatticeFileComment(line))
			{
				LegoLatticeFileComment llfc = new LegoLatticeFileComment(line);
				if(llfc.getKeyword().equals("/slot"))
				{
					status = status + "Finished adding slot. ";
					getLego().writeStatus(status + "\t" + line);
					return ilineMarker;
				}
				if(llfc.getKeyword().equals("info"))
				{
					status = status + "Adding info comment";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(llfc);
					legoInfo.setId(Lego.addLeadingZeros(infocounter, 3));
					infocounter = infocounter + 10;
					legoBeamInfoList.add(legoInfo);
				}
			}
			if ((line.indexOf(";") == 0) && (line.indexOf(";lego") < 0))
			{
				if (line.length() > 1)
				{
					status = status + "Adding info comment";
					getLego().writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(Lego.addLeadingZeros(infocounter, 3), line.substring(1).trim(), "comment");
					infocounter = infocounter + 10;
					legoBeamInfoList.add(legoInfo);
				}
			}
			int isclego = line.indexOf(";lego");
			int isc = line.indexOf(";");
			if (isc != 0 || (isclego == 0) )
			{
				LegoLatticeFileComment llfc = null;
				String delims = "[ ,\t]+";
				String[] splitResponse = null;
				if (isc > 0)
				{
					if (LegoLatticeFileComment.isLegoLatticeFileComment(line.substring(isc).trim()))
					{
						llfc = new LegoLatticeFileComment(line.substring(isc).trim());
						if (!llfc.getKeyword().equals("beam")) llfc = null;
					}
					line = line.substring(0, isc);
				}
				if (isclego != 0)
				{
					splitResponse = line.split(delims);
					
				}
				else
				{
					llfc = new LegoLatticeFileComment(line.substring(isc).trim());
					if (!llfc.getKeyword().equals("beam")) llfc = null;
					if (llfc.attributeExists("data"))
					{
						splitResponse = llfc.getAttribute("data").split(delims);
					}
				}
				if (splitResponse.length > 0)
				{
					String latticeKeyWord = splitResponse[0];
					String[] latticeData = new String[splitResponse.length - 1];
					for (int idata = 0; idata < (splitResponse.length - 1); ++idata)
					{
						latticeData[idata] = splitResponse[idata + 1];
					}
					int ibeamType = 0;
					LegoBeam beamType = null;
					while (ibeamType < getLego().getBeamTypes().size())
					{
						if (getLego().getBeamTypes().get(ibeamType).getLatticeFileKeyWord(latticeType).equals(latticeKeyWord))
						{
							beamType = getLego().getBeamTypes().get(ibeamType);
							ibeamType = getLego().getBeamTypes().size();
						}
						ibeamType = ibeamType + 1;
					}
					if (beamType != null) 
					{
						String beamId = addLeadingZeros((beamListIndex + 1) * 10, 4);
						LegoBeam lb = null;
						try 
						{
							lb = beamType.getClass().getConstructor(LegoSlot.class, int.class, String.class, String.class, String.class).newInstance(this, beamListIndex, beamId, null, null);
						} catch (Exception e) {throw new LinacLegoException(e);}
						lb.addDataElements();
						lb.addLatticeData(latticeType, latticeData);
						lb.setId(lb.getPreferredIdLabelHeader() + beamId);
						lb.setDisc(lb.getPreferredDiscipline());
						lb.setInfoList(legoBeamInfoList);
						legoBeamInfoList =  new ArrayList<LegoInfo>();
						status = status + "Adding beam element";
						getLego().writeStatus(status + "\t" + line);
						if (llfc != null)
						{
							String disc = llfc.getAttribute("disc");
							if (disc != null) lb.setDisc(disc);
							String id = llfc.getAttribute("id");
							if (id != null) lb.setId(id);
							String model = llfc.getAttribute("model");
							if (model != null) lb.setModel(model);
						}
						legoBeamList.add(lb);
						beamListIndex = beamListIndex + 1;
					}
					else
					{
						if (line.length() > 1)
						{
							status = status + "Adding info tune";
							getLego().writeStatus(status + "\t" + line);
							legoBeamInfoList.add(new LegoInfo(Lego.addLeadingZeros(infocounter, 3), line, "tune"));
							infocounter = infocounter + 10;
						}
					}
				}
			}
			ilineMarker = ilineMarker + 1;
		}
		return ilineMarker;
	}
	public static String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
	}
	public boolean matchesSlotTemplate(LegoSlotTemplate slotTemplate) throws LinacLegoException
	{
		if (legoBeamList.size() != slotTemplate.getLegoBeamTemplateList().size())
		{
			getLego().writeStatus("               Beam List size = " + legoBeamList.size() + " Template List size = " + slotTemplate.getLegoBeamTemplateList().size());
			return false;
		}
		for (int ibeam = 0; ibeam < legoBeamList.size(); ++ ibeam)
		{
			if (!legoBeamList.get(ibeam).matchesTemplate( slotTemplate.getLegoBeamTemplateList().get(ibeam))) 
			{
				getLego().writeStatus("               Mismatch at " + legoBeamList.get(ibeam).getId() + " with Template " + slotTemplate.getLegoBeamTemplateList().get(ibeam).getId());
				return false;
			}
			
		}
		return true;
	}
	public void convertToTemplateType(LegoSlotTemplate slotTemplate) throws LinacLegoException
	{
		template = slotTemplate.getId();
		legoDataList = new ArrayList<LegoData>();
		legoInfoList = new ArrayList<LegoInfo>();
		for (int iinfo = 0; iinfo < slotTemplate.getLegoInfoList().size(); ++iinfo)
		{
			legoInfoList.add(slotTemplate.getLegoInfoList().get(iinfo));
		}
		for (int idata = 0; idata < slotTemplate.getLegoVariableList().size(); ++idata)
		{
			legoDataList.add(new LegoData(
					slotTemplate.getLegoVariableList().get(idata).getId(), 
					slotTemplate.getLegoVariableList().get(idata).getValue(), 
					slotTemplate.getLegoVariableList().get(idata).getType(), 
					slotTemplate.getLegoVariableList().get(idata).getUnit()));
		}
		for (int ib = 0; ib < slotTemplate.getLegoBeamTemplateList().size(); ++ib)
		{
			LegoBeamTemplate lbt = slotTemplate.getLegoBeamTemplateList().get(ib);
			for (int id = 0; id < lbt.getLegoDataList().size(); ++id)
			{
				if (!lbt.getLegoDataList().get(id).valueMatchsType()) 
				{
					String value  = LegoData.findLegoDataById(legoBeamList.get(ib).getLegoDataList(), lbt.getLegoDataList().get(id).getId()).getValue();
					String varName = lbt.getLegoDataList().get(id).getValue();
					if (lbt.getLegoDataList().get(id).getType().equals("string")) varName = varName.substring(1);
					LegoData.findLegoDataById(legoDataList, varName).setValue(value);					
				}
			}
			for (int iinfo = 0; iinfo < lbt.getLegoInfoList().size(); ++iinfo)
			{
				if (!lbt.getLegoInfoList().get(iinfo).valueMatchsType()) 
				{
					String value  = LegoInfo.findLegoInfoById(legoBeamList.get(ib).getLegoInfoList(), lbt.getLegoInfoList().get(iinfo).getId()).getValue();
					String varName = lbt.getLegoInfoList().get(iinfo).getValue().substring(1);
					LegoData.findLegoDataById(legoDataList, varName).setValue(value);					
				}
			}
		}
	}
	public void triggerUpdate(ArrayList<LegoModel> legoBeamModelList) throws LinacLegoException
	{
		getLego().writeStatus("               Updating slot " + getId());
		if (template != null)
		{
			legoBeamList = new ArrayList<LegoBeam>();
			LegoSlotTemplate.findLegoTemplateById(getLego().getLegoSlotTempateList(), template).createBeamList(this);
		}
		for (int ibeam = 0; ibeam < getLegoBeamList().size(); ++ibeam) 
		{
			LegoBeam legoBeam = getLegoBeamList().get(ibeam);
			getLego().writeStatus("                    Updating beam " + legoBeam.getId());
			legoBeam.triggerUpdate();
			LegoModel.addLegoBeamToModelList(legoBeamModelList, legoBeam);
		}
		
	}
	public double getLength()
	{
		double length = 0.0;
		for (int ibeam = 0; ibeam < legoBeamList.size(); ++ ibeam) length = length + legoBeamList.get(ibeam).getLength();
		return length;
	}
	public double geteVout()
	{
		return legoBeamList.get(legoBeamList.size() - 1).geteVout();
	}
	public double[] getEndPosVec()
	{
		return legoBeamList.get(legoBeamList.size() - 1).getEndPosVec();
		
	}
	public void printTable(PrintWriter pw) throws LinacLegoException
	{
		double[] surveyCoords = getLegoLinac().getSurveyCoords(getEndPosVec());
		pw.print(getLegoSection().getId());
		pw.print("," + getLegoCell().getId());
		pw.print("," + getId());
		pw.print(" ," + template);
		pw.print(" ,#" + drawingLocation);
		pw.print(" ," + Lego.sixPlaces.format((geteVout() / 1.0e6)));
		pw.print(" ," + Lego.sixPlaces.format(LegoBeam.beta(geteVout())));
		pw.print(" ," + Lego.sixPlaces.format(getLength()));
		pw.print(" ," + Lego.sixPlaces.format(getEndPosVec()[0]));
		pw.print(" ," + Lego.sixPlaces.format(getEndPosVec()[1]));
		pw.print(" ," + Lego.sixPlaces.format(getEndPosVec()[2]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[0]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[1]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[2]));
		pw.println("");
	}
				
}
