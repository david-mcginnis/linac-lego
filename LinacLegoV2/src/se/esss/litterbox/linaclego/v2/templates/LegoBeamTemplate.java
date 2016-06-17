package se.esss.litterbox.linaclego.v2.templates;

import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoData;
import se.esss.litterbox.linaclego.v2.data.LegoInfo;
import se.esss.litterbox.linaclego.v2.data.LegoVariable;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoBeamTemplate  implements Serializable
{
	private static final long serialVersionUID = -2226904536763451428L;
	private ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();
	private ArrayList<LegoInfo> legoInfoList = new ArrayList<LegoInfo>();
	private String id = null;
	private String type = null;
	private String disc = null;
	private String model = null;
	private LegoSlotTemplate legoSlotTemplate;

	public ArrayList<LegoData> getLegoDataList() {return legoDataList;}
	public ArrayList<LegoInfo> getLegoInfoList() {return legoInfoList;}
	public String getId() {return id;}
	public String getType() {return type;}
	public String getDisc() {return disc;}
	public String getModel() {return model;}
	public LegoSlotTemplate getLegoSlotTemplate() {return legoSlotTemplate;}
	
	public LegoBeamTemplate(SimpleXmlReader beamTemplateTag) throws LinacLegoException
	{
		try {id = beamTemplateTag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		try {type = beamTemplateTag.attribute("type");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		try {disc = beamTemplateTag.attribute("disc");} catch (SimpleXmlException e) {disc = null;}
		try {model = beamTemplateTag.attribute("model");} catch (SimpleXmlException e) {model = null;}
		SimpleXmlReader infoTags = beamTemplateTag.tagsByName("info");
		if (infoTags.numChildTags() > 0)
		{
			for (int iinfo = 0; iinfo < infoTags.numChildTags(); ++iinfo)
			{
				try {legoInfoList.add(new LegoInfo(infoTags.tag(iinfo)));} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
		}
		SimpleXmlReader dataTags = beamTemplateTag.tagsByName("d");
		if (dataTags.numChildTags() > 0)
		{
			for (int idata = 0; idata < dataTags.numChildTags(); ++idata)
			{
				try {legoDataList.add(new LegoData(dataTags.tag(idata)));} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
		}
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException 
	{
		try 
		{
			xw.openXmlTag("beam");
			xw.setAttribute("id",id);
			xw.setAttribute("type",type);
			if (disc != null) xw.setAttribute("disc",disc);
			if (model != null) xw.setAttribute("model",model);
			if (legoInfoList.size() > 0)
			{
				for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeXml(xw);
			}
			if (legoDataList.size() > 0)
			{
				for (int ii = 0; ii < legoDataList.size(); ++ii) legoDataList.get(ii).writeXml(xw);
			}
			xw.closeXmlTag("beam");
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		
	}
	public LegoBeam createLegoBeamFromTemplate(LegoSlot legoSlot, LegoSlotTemplate lst, int beamListIndex) throws LinacLegoException
	{
		legoSlot.getLego().writeStatus("                    Adding beam " + id);
		LegoBeam lb;
		int ibeamType = 0;
		LegoBeam beamType = null;
		while (ibeamType < legoSlot.getLego().getBeamTypes().size())
		{
			if (legoSlot.getLego().getBeamTypes().get(ibeamType).getType().equals(type))
			{
				beamType = legoSlot.getLego().getBeamTypes().get(ibeamType);
				ibeamType = legoSlot.getLego().getBeamTypes().size();
			}
			ibeamType = ibeamType + 1;
		}
		if (beamType != null) 
		{
			try {lb  = beamType.getClass().getConstructor(LegoSlot.class, int.class, String.class, String.class, String.class).newInstance(legoSlot, beamListIndex, id, disc, model);} 
			catch (Exception e) {throw new LinacLegoException(e);}		
		}
		else
		{
			throw new LinacLegoException("No such type " + type + " found");
		}
		if (legoInfoList.size() > 0)
		{
			for (int ii = 0; ii < legoInfoList.size(); ++ii)
			{
				LegoInfo legoInfo = new LegoInfo(legoInfoList.get(ii));
				if (!legoInfo.valueMatchsType())
				{
					String infoVal = LegoVariable.findLegoInfoById(lst.getLegoVariableList(),legoInfo.getValue()).getValue();
					legoInfo.setValue(infoVal);
				}
				lb.getLegoInfoList().add(legoInfo);
			}
		}
		if (legoDataList.size() > 0)
		{
			for (int ii = 0; ii < legoDataList.size(); ++ii)
			{
				LegoData legoData = new LegoData(legoDataList.get(ii));
				if (!legoData.valueMatchsType())
				{
					String datVal = LegoVariable.findLegoDataById(lst.getLegoVariableList(),legoData.getValue()).getValue();
					legoData.setValue(datVal);
				}
				lb.getLegoDataList().add(legoData);
			}
		}
		return lb;
	}

}
