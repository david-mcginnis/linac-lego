package se.esss.litterbox.linaclego.v2.data.legosets;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSetsBlock   implements Serializable
{
	private static final long serialVersionUID = -7411313388059975663L;
	private String id;
	private ArrayList<LegoSet> legoSetList = new ArrayList<LegoSet>();

	public String getId() {return id;}
	public ArrayList<LegoSet> getLegoSetList() {return legoSetList;}
	
	public LegoSetsBlock(SimpleXmlReader legoBlockTag) throws LinacLegoException
	{
		try
		{
			id = legoBlockTag.attribute("id");
			SimpleXmlReader legoSetListTag = legoBlockTag.tagsByName("legoSet");
			for (int iset =  0; iset < legoSetListTag.numChildTags(); ++iset)
			{
				legoSetList.add(new LegoSet(legoSetListTag.tag(iset)));
			}
		} 
		catch (Exception e) {throw new LinacLegoException("e");}
		
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("legoSetsBlock");
			xw.setAttribute("id",   id);
			for (int iset = 0; iset < legoSetList.size(); ++iset)
				legoSetList.get(iset).writeXml(xw);
			xw.closeXmlTag("legoSetsBlock");
		} 
		catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public void setSettingFromLattice(LegoLinac legoLinac) throws LinacLegoException
	{
		for (int iset = 0; iset < legoSetList.size(); ++iset)
		{
			legoSetList.get(iset).setSettingFromLattice(legoLinac);
		}
	}
	public void setLatticeFromSetting(LegoLinac legoLinac) throws LinacLegoException
	{
		for (int iset = 0; iset < legoSetList.size(); ++iset)
			legoSetList.get(iset).setLatticeFromSetting(legoLinac);
	}
	public void printLegoSetsCsvFile(PrintWriter pw) throws LinacLegoException 
	{
		for (int iset = 0; iset < legoSetList.size(); ++iset)
			legoSetList.get(iset).printLegoSetsCsvFile(pw);
		
	}
}
