package se.esss.litterbox.linaclego.v2.data.legosets;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSets    implements Serializable
{
	private static final long serialVersionUID = 7359884757460783495L;
	private String id;
	private ArrayList<LegoSetsBlock> legoSetsBlockList = new ArrayList<LegoSetsBlock>();

	public String getId() {return id;}
	public ArrayList<LegoSetsBlock> getLegoSetsBlockList() {return legoSetsBlockList;}

	public LegoSets(URL fileUrl) throws LinacLegoException
	{
		try 
		{
			SimpleXmlDoc sxd = new SimpleXmlDoc(fileUrl);
			SimpleXmlReader legoSetsTag = new SimpleXmlReader(sxd);
			id = legoSetsTag.attribute("id");
			SimpleXmlReader legoSetsBlockListTag = legoSetsTag.tagsByName("legoSetsBlock");
			for (int itag = 0; itag < legoSetsBlockListTag.numChildTags(); ++itag)
			{
				legoSetsBlockList.add(new LegoSetsBlock(legoSetsBlockListTag.tag(itag)));
			}
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public LegoSets(String filePath) throws LinacLegoException, MalformedURLException
	{
		this(new File(filePath).toURI().toURL());
	}
	public  void writeLegoSetsFile(String filePath) throws LinacLegoException
	{
		try 
		{
			SimpleXmlWriter xw = new SimpleXmlWriter("legoSets","dtdFiles/LegoSets.dtd");
			File file = new File(filePath);
			id = file.getName().substring(0,file.getName().lastIndexOf(".xml"));
			xw.setAttribute("id",   id);
			for (int iset = 0; iset < legoSetsBlockList.size(); ++iset)
				legoSetsBlockList.get(iset).writeXml(xw);
			xw.closeDocument();
			xw.saveXmlDocument(filePath);
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}

	}
	public void printLegoSetsCsvFile(String filePath) throws LinacLegoException
	{
		PrintWriter pw;
		try 
		{
			pw = new PrintWriter(filePath);
			pw.println("Section,Cell,Slot,Lattice Parameter,Value,Unit,Device,Setting,Unit," + LegoSet.getTfCsvDataHeader());
			for (int iset = 0; iset < legoSetsBlockList.size(); ++iset)
			{
				legoSetsBlockList.get(iset).printLegoSetsCsvFile(pw);
			}
			pw.close();
		} catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		
	}
	public void setSettingFromLattice(LegoLinac legoLinac) throws LinacLegoException
	{
		for (int iset = 0; iset < legoSetsBlockList.size(); ++iset)
		{
			legoSetsBlockList.get(iset).setSettingFromLattice(legoLinac);
		}
	}
	public void setLatticeFromSetting(LegoLinac legoLinac) throws LinacLegoException
	{
		for (int iset = 0; iset < legoSetsBlockList.size(); ++iset)
			legoSetsBlockList.get(iset).setLatticeFromSetting(legoLinac);
	}
	public static void main(String[] args) throws MalformedURLException, LinacLegoException, SimpleXmlException 
	{
		Lego lego = new Lego("/home/dmcginnis427/Dropbox/TB18LatticeImport/5.0_Spoke.xml", null, true);
		lego.triggerUpdate("/home/dmcginnis427/Dropbox/TB18LatticeImport/5.0_Spoke.xml");
		lego.setSettingsFromLattice("/home/dmcginnis427/Dropbox/TB18LatticeImport/5.0_SpokeSets.xml", "/home/dmcginnis427/Dropbox/TB18LatticeImport/booger.xml");
	}

}
