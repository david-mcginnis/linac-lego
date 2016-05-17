package se.esss.litterbox.linaclego.templates;

import java.util.ArrayList;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.data.LegoVariable;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.linaclego.structures.beam.LegoBeam;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSlotTemplate 
{
	private ArrayList<LegoVariable> legoVariableList = new ArrayList<LegoVariable>();
	private ArrayList<LegoBeamTemplate> legoBeamTemplateList = new ArrayList<LegoBeamTemplate>();
	private String id = null;
	private Lego lego;
	
	public ArrayList<LegoVariable> getLegoVariableList() {return legoVariableList;}
	public ArrayList<LegoBeamTemplate> getLegoBeamTemplateList() {return legoBeamTemplateList;}
	public String getId() {return id;}
	public Lego getLego() {return lego;}
	
	public LegoSlotTemplate(Lego lego, String id)
	{
		this.lego = lego;
		this.id = id;
	}
	public LegoSlotTemplate(Lego lego, SimpleXmlReader slotTemplateTag) throws LinacLegoException
	{
		this.lego = lego;
		try {this.id = slotTemplateTag.attribute("id");} catch (SimpleXmlException e1) {throw new LinacLegoException(e1);}
		SimpleXmlReader variableTags = slotTemplateTag.tagsByName("var");
		if (variableTags.numChildTags() > 0)
		{
			for (int ii = 0; ii < variableTags.numChildTags(); ++ii)
			{
				try 
				{
					getLego().writeStatus("          Adding variable " + variableTags.tag(ii).attribute("id"));
					legoVariableList.add(new LegoVariable(variableTags.tag(ii)));
				} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
		}
		SimpleXmlReader beamTags = slotTemplateTag.tagsByName("beam");
		if (beamTags.numChildTags() > 0)
		{
			for (int ii = 0; ii < beamTags.numChildTags(); ++ii)
			{
				try 
				{
					getLego().writeStatus("          Adding beam " + beamTags.tag(ii).attribute("id"));
					legoBeamTemplateList.add(new LegoBeamTemplate(beamTags.tag(ii)));
				} 
				catch (SimpleXmlException e) {throw new LinacLegoException(e);}
			}
		}
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("slotTemplate");
			xw.setAttribute("id",id);
			if (legoVariableList.size() > 0)
			{
				for (int ii = 0; ii < legoVariableList.size(); ++ii) legoVariableList.get(ii).writeXml(xw);
			}
			if (legoBeamTemplateList.size() > 0)
			{
				for (int ii = 0; ii < legoBeamTemplateList.size(); ++ii) legoBeamTemplateList.get(ii).writeXml(xw);
			}
			xw.closeXmlTag("slotTemplate");
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}
	public static LegoSlotTemplate findLegoTemplateById(ArrayList<LegoSlotTemplate> legoSlotTemplateList, String id)
	{
		int icount = 0; 
		while (icount < legoSlotTemplateList.size())
		{
			if (id.equals(legoSlotTemplateList.get(icount).getId()))
			{
				return legoSlotTemplateList.get(icount);
			}
			icount = icount + 1;
		}
		return null;
	}
	public void createBeamList(LegoSlot legoSlot) throws LinacLegoException
	{
		if (legoSlot.getLegoDataList().size() > 0)
		{
			for (int ii = 0; ii < legoSlot.getLegoDataList().size(); ++ii) 
			{
				LegoVariable lv = LegoVariable.findLegoDataById(legoVariableList, legoSlot.getLegoDataList().get(ii).getId());
				lv.setValue(legoSlot.getLegoDataList().get(ii).getValue());
			}
		}
		if (legoBeamTemplateList.size() > 0)
		{
			for (int ii = 0; ii < legoBeamTemplateList.size(); ++ii) 
			{
				LegoBeam lb = legoBeamTemplateList.get(ii).createLegoBeamFromTemplate(legoSlot, this, ii);
				legoSlot.getLegoBeamList().add(lb);
			}
		}
	}

}
