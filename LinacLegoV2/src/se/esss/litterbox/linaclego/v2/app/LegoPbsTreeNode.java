package se.esss.litterbox.linaclego.v2.app;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoData;
import se.esss.litterbox.linaclego.v2.data.LegoInfo;
import se.esss.litterbox.linaclego.v2.structures.LegoCell;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.linaclego.v2.structures.LegoSection;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;


public class LegoPbsTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = -1947606434614685025L;
	public LegoPbsTreeNode(String title)  
	{
		super(title);
	}
	public LegoPbsTreeNode(Lego lego) throws LinacLegoException 
	{
		super();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linacLego" + "</font>";
		html =  html + "<font color=\"FF0000\"> title</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getTitle() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev No." + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getRevNo() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev Date" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getRevDate() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rev Comment" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + lego.getRevComment() + "\"</font>";
		html = html + "</html>";
		setUserObject(html); 
		DefaultMutableTreeNode linacNode = legoPbsLinacTreeNode(lego.getLegoLinac());
     	add(linacNode);
		for (int isec = 0; isec < lego.getLegoLinac().getLegoSectionList().size(); ++isec)
		{
			LegoSection section = lego.getLegoLinac().getLegoSectionList().get(isec);
			DefaultMutableTreeNode sectionNode = legoPbsSectionTreeNode(section);
			linacNode.add(sectionNode);
			for (int icell = 0; icell < section.getLegoCellList().size(); ++icell)
			{
				LegoCell cell = section.getLegoCellList().get(icell);
				DefaultMutableTreeNode cellNode = legoPbsCellTreeNode(cell);
				sectionNode.add(cellNode);
				for (int islot = 0; islot < cell.getLegoSlotList().size(); ++islot)
				{
					LegoSlot slot = cell.getLegoSlotList().get(islot);
					DefaultMutableTreeNode slotNode = legoPbsSlotTreeNode(slot);
					cellNode.add(slotNode);
					for (int ibeam = 0; ibeam < slot.getLegoBeamList().size(); ++ibeam)
					{
						LegoBeam beam = slot.getLegoBeamList().get(ibeam);
						DefaultMutableTreeNode beamNode = legoPbsBeamTreeNode(beam);
						slotNode.add(beamNode);
					}
				}
			}
		}
	}
	public DefaultMutableTreeNode legoPbsDataTreeNode(LegoData dataElement) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "data" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getId() + "\"</font>";
		if (dataElement.getType() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getType() + "\"</font>";
		if (dataElement.getUnit() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "unit" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + dataElement.getUnit() + "\"</font>";
		if (dataElement.getValue() != null)
			html =  html + "<font color=\"000000\">" + " " + dataElement.getValue() + "</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		return fmtn;
	}
	public DefaultMutableTreeNode legoPbsInfoTreeNode(LegoInfo info) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "info" + "</font>";
		if (info.getId() != null)
			html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + info.getId() + "\"</font>";
		if (info.getType() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + info.getType() + "\"</font>";
		if (info.getValue() != null)
			html =  html + "<font color=\"000000\">" + " " + info.getValue() + "</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		return fmtn;
	}
	public DefaultMutableTreeNode legoPbsStringTreeNode(String string) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		fmtn.setUserObject(string); 
		return fmtn;
	}
	public DefaultMutableTreeNode legoPbsLinacTreeNode(LegoLinac linac) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "linac" + "</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		if (linac.getLegoInfoList().size() > 0) fmtn.add(infoFolder(linac.getLegoInfoList()));
		if (linac.getLegoDataList().size() > 0) fmtn.add(dataFolder(linac.getLegoDataList()));
		return fmtn;
		
	}
	public DefaultMutableTreeNode legoPbsSectionTreeNode(LegoSection section) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "section" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "rfHarmonic" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + section.getRfHarmonic() + "\"</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		if (section.getLegoInfoList().size() > 0) fmtn.add(infoFolder(section.getLegoInfoList()));
		return fmtn;
	}
	public DefaultMutableTreeNode legoPbsCellTreeNode(LegoCell cell) throws LinacLegoException
	{		
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "cell" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + cell.getId() + "\"</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		if (cell.getLegoInfoList().size() > 0) fmtn.add(infoFolder(cell.getLegoInfoList()));
		return fmtn;
		
	}
	public DefaultMutableTreeNode legoPbsSlotTreeNode(LegoSlot slot) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "slot" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getId() + "\"</font>";
		if (slot.getTemplate() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + slot.getTemplate() + "\"</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		if (slot.getLegoInfoList().size() > 0) fmtn.add(infoFolder(slot.getLegoInfoList()));
		return fmtn;
	}
	public DefaultMutableTreeNode legoPbsBeamTreeNode(LegoBeam beam) throws LinacLegoException
	{
		DefaultMutableTreeNode fmtn = new DefaultMutableTreeNode();

		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + "ble" + "</font>";
		html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getId() + "\"</font>";
		html =  html + "<font color=\"FF0000\">" + " " + "type" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getType() + "\"</font>";
		if (beam.getModel() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "model" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getModel() + "\"</font>";
		if (beam.getDisc() != null)
			html =  html + "<font color=\"FF0000\">" + " " + "disc" + "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + beam.getDisc() + "\"</font>";
		html = html + "</html>";
		fmtn.setUserObject(html); 
		if (beam.getLegoInfoList().size() > 0) fmtn.add(infoFolder(beam.getLegoInfoList()));
		if (beam.getLegoDataList().size() > 0) fmtn.add(dataFolder(beam.getLegoDataList()));
		return fmtn;
	}
	private DefaultMutableTreeNode infoFolder(ArrayList<LegoInfo> legoInfoList) throws LinacLegoException
	{
		DefaultMutableTreeNode infoFolder = null;
		if (legoInfoList.size() > 0)
		{
			infoFolder = legoPbsStringTreeNode("info");
			for (int iinfo = 0; iinfo < legoInfoList.size(); ++iinfo)
				infoFolder.add(legoPbsInfoTreeNode(legoInfoList.get(iinfo)));
		}
		return infoFolder;
	}
	private DefaultMutableTreeNode dataFolder(ArrayList<LegoData> legoDataList) throws LinacLegoException
	{
		DefaultMutableTreeNode dataFolder = null;
		if (legoDataList.size() > 0)
		{
			dataFolder = legoPbsStringTreeNode("data");
			for (int iinfo = 0; iinfo < legoDataList.size(); ++iinfo)
				dataFolder.add(legoPbsDataTreeNode(legoDataList.get(iinfo)));
		}
		return dataFolder;
	}
	
}
