package se.esss.litterbox.linaclego.v2.webapp.server;

import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoData;
import se.esss.litterbox.linaclego.v2.structures.LegoCell;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.linaclego.v2.structures.LegoSection;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;

public class LegoPbsHtmlTextTree 
{
	private HtmlTextTree htmlTextTree;
	
	public static final DecimalFormat threePlaces = new DecimalFormat("###.###");
	
	public HtmlTextTree getHtmlTextTree() {return htmlTextTree;}
	public LegoPbsHtmlTextTree()
	{
		htmlTextTree = new HtmlTextTree();
	}
	public void add(LegoPbsHtmlTextTree linacLegoPbsHtmlTextTree)
	{
		htmlTextTree.add(linacLegoPbsHtmlTextTree.getHtmlTextTree());
	}
	public LegoPbsHtmlTextTree(Lego lego, String tagStyle, String attLabelStyle, String attValueStyle, String attWhiteSpaceStyle) 
	{
		this();
		htmlTextTree.setTagStyle(tagStyle);
		htmlTextTree.setAttLabelStyle(attLabelStyle);
		htmlTextTree.setAttValueStyle(attValueStyle);
		htmlTextTree.setAttWhiteSpaceStyle(attWhiteSpaceStyle);
		
		htmlTextTree.setIconImageLocation("images/lego.jpg");
		htmlTextTree.setTag("linacLego", 9);
		htmlTextTree.addAttribute("title", lego.getTitle(), 1);
		htmlTextTree.addAttribute("revNo", lego.getRevNo(), 1);
		htmlTextTree.addAttribute("rev Date", lego.getRevDate(), 1);
		htmlTextTree.addAttribute("rev Comment", lego.getRevComment(), 1);
		
	}
	public void inheritStyles(LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree)
	{
		htmlTextTree.inheritStyles(parentLinacLegoPbsHtmlTextTree.getHtmlTextTree());
	}
	public LegoPbsHtmlTextTree(LegoData dataElement, LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree) throws LinacLegoException 
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation(null);
		htmlTextTree.setTag("data", 4);
		htmlTextTree.addAttribute("id", dataElement.getId(), 15);
		if (dataElement.getType() != null)
		{
			htmlTextTree.addAttribute("type", dataElement.getType(), 10);
		}
		if (dataElement.getUnit() != null)
		{
			htmlTextTree.addAttribute("unit", dataElement.getUnit(), 10);
		}
		if (dataElement.getValue() != null)
		{
			htmlTextTree.addAttribute("value", dataElement.getValue(), 15);
		}
	}
	public LegoPbsHtmlTextTree(LegoLinac linac, LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree, String linacLegoDataLink) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/linac.jpg");
		htmlTextTree.setTag("linac", 5);
		htmlTextTree.addAttribute("id", linac.getLego().getTitle(), 25);
		htmlTextTree.addAttribute("address", linac.getLego().getTitle(), 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(linac.getLastSection().getLastCell().getLastSlot().getLastBeam().geteVout() / 1.0e+06)  + " MeV", 1);
		htmlTextTree.addAttribute("length", threePlaces.format(linac.getLastSection().getLastCell().getLastSlot().getLastBeam().getEndPosVec()[2])  + " m"   + " MeV", 1);
		
		makeDataFolder(linac.getLegoDataList());
	}
	private void makeDataFolder(ArrayList<LegoData> dataElementList) throws LinacLegoException
	{
		HtmlTextTree dataFolderHtmlTextTree = new HtmlTextTree();
		dataFolderHtmlTextTree.inheritStyles(getHtmlTextTree());
		dataFolderHtmlTextTree.setIconImageLocation("images/data.png");
		dataFolderHtmlTextTree.setTag("data", 4);
		htmlTextTree.setDataFolder(dataFolderHtmlTextTree);

		for (int idata = 0; idata < dataElementList.size(); ++idata)
		{
			LegoPbsHtmlTextTree dataElement = new LegoPbsHtmlTextTree(dataElementList.get(idata), this);
			dataFolderHtmlTextTree.add(dataElement.htmlTextTree);
		}
	}
	public LegoPbsHtmlTextTree(LegoSection section, LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree, String linacLegoDataLink) throws LinacLegoException
	{

		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/section.jpg");
		htmlTextTree.setTag("section", 7);
		htmlTextTree.addAttribute("id", section.getId(), 15);
		htmlTextTree.addAttribute("address", section.getAddress(), 25);
		htmlTextTree.addAttribute("rfHarmonic", Integer.toString(section.getRfHarmonic()), 1);
		htmlTextTree.addAttribute("energy", threePlaces.format(section.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(section.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(section.getEndPosVec()[2]) + "m", 9);
		
	}
	public LegoPbsHtmlTextTree(LegoCell cell, LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree, String linacLegoDataLink) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/cell.jpg");
		htmlTextTree.setTag("cell", 4);
		htmlTextTree.addAttribute("id", cell.getId(), 15);
		htmlTextTree.addAttribute("address", cell.getAddress(), 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(cell.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(cell.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(cell.getEndPosVec()[2]) + "m", 9);
		
	}
	public LegoPbsHtmlTextTree(LegoSlot slot, LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree, String linacLegoDataLink) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("images/slots.jpg");
		htmlTextTree.setTag("slot", 4);
		htmlTextTree.addAttribute("id", slot.getId(), 15);
		htmlTextTree.addAttribute("address", slot.getAddress(), 25);
		String slotModelId = slot.getTemplate();
		if (slotModelId == null) slotModelId = "none";
		htmlTextTree.addAttribute("model", slotModelId, 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(slot.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(slot.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-end", threePlaces.format(slot.getEndPosVec()[2]) + "m", 9);
		if (slot.getDrawingLocation() != null) htmlTextTree.setInfoLink(linacLegoDataLink + "/" + slot.getDrawingLocation());
		if (slot.getDrawingLocation() != null) htmlTextTree.setInfoLinkTitle("Drawing");
		
	}
	public LegoPbsHtmlTextTree(LegoBeam beam, LegoPbsHtmlTextTree parentLinacLegoPbsHtmlTextTree, String linacLegoDataLink) throws LinacLegoException
	{
		this();
		inheritStyles(parentLinacLegoPbsHtmlTextTree);
		htmlTextTree.setIconImageLocation("");
		if (beam.getType().equals("quad")) htmlTextTree.setIconImageLocation("images/quad.jpg");
		if (beam.getType().equals("drift")) htmlTextTree.setIconImageLocation("images/drift.png");
		if (beam.getType().equals("rfGap")) htmlTextTree.setIconImageLocation("images/buncher.jpg");
		if (beam.getType().equals("fieldMap")) htmlTextTree.setIconImageLocation("images/elliptical.jpg");
		if (beam.getType().equals("bend")) htmlTextTree.setIconImageLocation("images/bend.jpg");
		if (beam.getType().equals("thinSteering")) htmlTextTree.setIconImageLocation("images/triangle.jpg");
		if (beam.getType().equals("dtlDriftTube")) htmlTextTree.setIconImageLocation("images/driftTube.jpg");
		if (beam.getType().equals("dtlRfGap")) htmlTextTree.setIconImageLocation("images/buncher.jpg");
		if (beam.getType().equals("marker")) htmlTextTree.setIconImageLocation("images/greenMarker.png");
		if (beam.getType().equals("beamPosition")) htmlTextTree.setIconImageLocation("images/bpm.jpg");
		if (beam.getType().equals("beamSize")) htmlTextTree.setIconImageLocation("images/wsc.jpg");
		if (beam.getType().equals("beamLoss")) htmlTextTree.setIconImageLocation("images/blm.jpg");
		if (beam.getType().equals("fcup")) htmlTextTree.setIconImageLocation("images/fcup.jpg");
		if (beam.getType().equals("beamCurrent")) htmlTextTree.setIconImageLocation("images/bcm.jpg");
		if (beam.getType().equals("edge")) htmlTextTree.setIconImageLocation("images/triangle.jpg");
		
		htmlTextTree.setTag(beam.getType(), 12);
		htmlTextTree.addAttribute("id", beam.getId(), 15);
		htmlTextTree.addAttribute("address", beam.getAddress(), 25);
		String modelName = "none";
		if (beam.getModel() != null) modelName = beam.getModel();
		htmlTextTree.addAttribute("model", modelName, 25);
		htmlTextTree.addAttribute("energy", threePlaces.format(beam.geteVout() / 1.0e+06) + "MeV", 11);
		htmlTextTree.addAttribute("length", threePlaces.format(beam.getLength()) + "m", 9);
		htmlTextTree.addAttribute("s-cent", threePlaces.format(beam.getEndPosVec()[2] - 0.5 * beam.getLength()) + "m", 9);
		makeDataFolder(beam.getLegoDataList());
	}

}
