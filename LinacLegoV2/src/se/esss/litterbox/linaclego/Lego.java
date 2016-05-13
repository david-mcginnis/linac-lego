package se.esss.litterbox.linaclego;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.data.LegoInfo;
import se.esss.litterbox.linaclego.data.LegoSet;
import se.esss.litterbox.linaclego.structures.LegoCell;
import se.esss.litterbox.linaclego.structures.LegoLinac;
import se.esss.litterbox.linaclego.structures.LegoSection;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.linaclego.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamDrift;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamFieldMap;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamMarker;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamPositionMonitor;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamQuad;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamSizeMonitor;
import se.esss.litterbox.linaclego.structures.beam.LegoBeamThinSteering;
import se.esss.litterbox.linaclego.templates.LegoSlotTemplate;
import se.esss.litterbox.linaclego.utilities.LegoLatticeFileComment;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;


public class Lego 
{
	public static final String newline = System.getProperty("line.separator");
	public static final String space = "   ";
	public static final double eVrest = 938.272046e+06;
	public static final double PI = Math.PI;
	public static final double degToRad = PI / 180.0;
	public static final double radToDeg = 180.0 / PI;
	public static final double TWOPI = 2.0 * PI;
	public static final double cvel = 299792458.0;
	public static final DecimalFormat onePlaces = new DecimalFormat("###.#");
	public static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final DecimalFormat eightPlaces = new DecimalFormat("###.########");
	public static final DecimalFormat zeroPlaces = new DecimalFormat("###");
	
	private ArrayList<LegoSlotTemplate> legoSlotTemplateList = new ArrayList<LegoSlotTemplate>();
	private ArrayList<LegoSet> legoSetlList = new ArrayList<LegoSet>();
	private LegoLinac legoLinac = null;
	private String title = "";
	private String revNo = "0";
	private String revComment = "none";
	private String revDate = "01-Jan-1970";
	private SimpleXmlDoc simpleXmlDoc = null;
	private ArrayList<LegoBeam> beamTypeList = new ArrayList<LegoBeam>();
	
	public ArrayList<LegoSlotTemplate> getLegoSlotTempateList() {return legoSlotTemplateList;}
	public ArrayList<LegoSet> getLegoSetlList() {return legoSetlList;}
	public LegoLinac getLegoLinac() {return legoLinac;}
	public String getTitle() {return title;}
	public String getRevNo() {return revNo;}
	public String getRevComment() {return revComment;}
	public String getRevDate() {return revDate;}
	public SimpleXmlDoc getSimpleXmlDoc() {return simpleXmlDoc;}
	public ArrayList<LegoBeam> getBeamTypes() {return beamTypeList;}
	
	public Lego(String title, String revNo, String revComment, String revDate, double ekinMeV, double beamFrequencyMHz) throws LinacLegoException
	{
		addBeamTypes();
		this.title = title;
		this.revNo = revNo;
		this.revComment = revComment;
		this.revDate = revDate;
		legoLinac = new LegoLinac(this, ekinMeV, beamFrequencyMHz);
	}
	private void addBeamTypes() throws LinacLegoException
	{
		beamTypeList.add(new LegoBeamDrift());
		beamTypeList.add(new LegoBeamQuad());
		beamTypeList.add(new LegoBeamFieldMap());
		beamTypeList.add(new LegoBeamThinSteering());
		beamTypeList.add(new LegoBeamMarker());
		beamTypeList.add(new LegoBeamPositionMonitor());
		beamTypeList.add(new LegoBeamSizeMonitor());
	}
	
	public Lego(SimpleXmlDoc sxd) throws LinacLegoException
	{
		addBeamTypes();
		simpleXmlDoc = sxd;
		try 
		{
			SimpleXmlReader legoTag = new SimpleXmlReader(sxd);
			try {revNo = legoTag.attribute("revNo");} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) revNo = "0";}
			try {revComment = legoTag.attribute("comment");} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) revComment = "none";}
			try {revDate = legoTag.attribute("date");} 
			catch (SimpleXmlException e) {if (e.getMessage().equals("Attribute does not exist")) revDate = "01-Jan-1970";}
			title = legoTag.attribute("title");
	
			writeStatus("Reading slotTemplates tag...");
			SimpleXmlReader slotTemplateListTag = legoTag.tagsByName("slotTemplate");
			if (slotTemplateListTag.numChildTags() > 0)
			{
				for (int itag = 0; itag < slotTemplateListTag.numChildTags(); ++itag)
				{
					writeStatus("     Adding SlotTemplate " + slotTemplateListTag.tag(itag).attribute("id"));
					legoSlotTemplateList.add(new LegoSlotTemplate(this, slotTemplateListTag.tag(itag)));
				}
			}
			writeStatus("Finished reading slotTemplates");
			writeStatus("Reading Linac ");
			legoLinac = new LegoLinac(this, legoTag.tagsByName("linac").tag(0));
			writeStatus("Finished reading Linac");
		} catch (Exception e) 
		{
			LinacLegoException lle = new LinacLegoException(e);
			writeStatus(lle.getRootCause());
			throw lle;
		}

	}
	public Lego(URL url) throws LinacLegoException, SimpleXmlException 
	{
		this(new SimpleXmlDoc(url));
	}
	public Lego(File file) throws MalformedURLException, LinacLegoException, SimpleXmlException
	{
		this(file.toURI().toURL());
	}
	public Lego(String filePath) throws MalformedURLException, LinacLegoException, SimpleXmlException
	{
		this(new File(filePath));
	}
	public void writeStatus(String statusText) 
	{
		System.out.println(statusText);
	}
	public void writeXmlFile(String filePath, String dtdLink, boolean expandSlotTemplate) throws LinacLegoException
	{
		try 
		{
			SimpleXmlWriter xw = new SimpleXmlWriter("linacLego", dtdLink);
			xw.setAttribute("title", title);
			if (!expandSlotTemplate)
			{
				if (legoSlotTemplateList.size() > 0)
				{
					for (int ii = 0; ii < legoSlotTemplateList.size(); ++ii) legoSlotTemplateList.get(ii).writeXml(xw);
				}
			}
			xw.openXmlTag("legoSets");
			xw.setAttribute("id", title + "LegoSets");
			xw.closeXmlTag("legoSets");
			legoLinac.writeXml(xw, expandSlotTemplate);
			xw.closeDocument();
			xw.saveXmlDocument(filePath);
		} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		
	}
	public void readLatticeFile(String fileLocationPath, String latticeType) throws LinacLegoException
	{
		writeStatus("Reading lattice file...");
		BufferedReader br;
		ArrayList<String> fileBuffer = new ArrayList<String>();
		try 
		{
			br = new BufferedReader(new FileReader(fileLocationPath));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				fileBuffer.add(line);
			}
			br.close();
			title = new File(fileLocationPath).getName().substring(0, new File(fileLocationPath).getName().lastIndexOf("."));
		}
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);} 

		int ilineMarker = 0;
		String line;
		int infocounter = 10;
		ArrayList<LegoInfo> legoLinacInfoList = new ArrayList<LegoInfo>();
		while (ilineMarker < fileBuffer.size())
		{
			line = fileBuffer.get(ilineMarker).trim();
			String status = "Processing Line " + Integer.toString(ilineMarker + 1) + " ";
			if (LegoLatticeFileComment.isLegoLatticeFileComment(line))
			{
				LegoLatticeFileComment llfc = new LegoLatticeFileComment(line);
				if(llfc.getKeyword().equals("linac"))
				{
					status = status + "Adding linac ";
					writeStatus(status + "\t" + line);
					ilineMarker = legoLinac.readLatticeFile(ilineMarker, fileBuffer, latticeType);
					legoLinac.setInfoList(legoLinacInfoList);
					legoLinacInfoList = new ArrayList<LegoInfo>();
				}
				if(llfc.getKeyword().equals("info"))
				{
					status = status + "Adding info ";
					writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(llfc);
					legoInfo.setId(Lego.addLeadingZeros(infocounter, 3));
					infocounter = infocounter + 10;
					legoLinacInfoList.add(legoInfo);
				}
			}
			if ((line.indexOf(";") == 0) && (line.indexOf(";lego") < 0))
			{
				if (line.length() > 1)
				{
					status = status + "Adding info comment";
					writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(Lego.addLeadingZeros(infocounter, 3), line.substring(1), "comment");
					infocounter = infocounter + 10;
					legoLinacInfoList.add(legoInfo);
				}
			}
			if (line.indexOf(";") < 0)
			{
				if (line.length() > 1)
				{
					status = status + "Adding info tune";
					writeStatus(status + "\t" + line);
					LegoInfo legoInfo = new LegoInfo(Lego.addLeadingZeros(infocounter, 3), line, "tune");
					infocounter = infocounter + 10;
					legoLinacInfoList.add(legoInfo);
				}
			}
			ilineMarker = ilineMarker + 1;
		}
		writeStatus("Finished reading lattice file...");
	}
	public void printLatticeCommand(PrintWriter pw, String latticeType) throws LinacLegoException
	{
		legoLinac.printLatticeCommand(pw, latticeType);
	}
	public void replaceSlotsWithTemplates() throws LinacLegoException
	{
		writeStatus("Replacing Slots With Templates");
		for (int isec = 0; isec < legoLinac.getLegoSectionList().size(); ++isec) 
		{
			LegoSection legoSection = legoLinac.getLegoSectionList().get(isec);
			for (int icell = 0; icell < legoSection.getLegoCellList().size(); ++icell) 
			{
				LegoCell legoCell = legoSection.getLegoCellList().get(icell);
				for (int islot = 0; islot < legoCell.getLegoSlotList().size(); ++islot) 
				{
					LegoSlot legoSlot = legoCell.getLegoSlotList().get(islot);
					if (legoSlot.getTemplate() == null)
					{
						writeStatus("     Examing slot " + legoSlot.getAddress());
						int itemplate = 0;
						boolean templateMatched = false;
						while ((itemplate < legoSlotTemplateList.size()) && (!templateMatched))
						{
							templateMatched = legoSlot.matchesSlotTemplate(legoSlotTemplateList.get(itemplate));
							if (!templateMatched) 
							{
								itemplate = itemplate + 1;
							}
							else
							{
								writeStatus("          Slot " + legoSlot.getAddress() + " matches template " + legoSlotTemplateList.get(itemplate).getId());
								legoSlot.convertToTemplateType(legoSlotTemplateList.get(itemplate));
							}
						}
					}
					else
					{
						writeStatus("     Skipping slot " + legoSlot.getAddress() + " It is already assoicated with a template");
					}
				}
			}
		}
	}
	public static String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
	}
	public static void main(String[] args) throws LinacLegoException, MalformedURLException, SimpleXmlException, FileNotFoundException 
	{
// 		Lego lego = new Lego("test", "revNo", "revComment", "revDate", 89.0, 352.21);
//		lego.readLatticeFile("5.0_SpokeV2.dat", "tracewin");
// 		lego.writeXmlFile("5.0_SpokeV2.xml", "../dtdFiles/LinacLego.dtd", false);

		Lego lego = new Lego("5.0_SpokeV4.xml");
		lego.getLegoLinac().triggerUpdate();
		lego.replaceSlotsWithTemplates();
/*		PrintWriter beamTablePw = new PrintWriter("5.0_SpokeV2.csv");
		lego.getLegoLinac().printBeamTable(beamTablePw);
		beamTablePw.close();
		PrintWriter traceWinPw = new PrintWriter("5.0_SpokeV3.dat");
		lego.getLegoLinac().printLatticeCommand(traceWinPw, "tracewin");
		traceWinPw.close();
*/		lego.writeXmlFile("5.0_SpokeV5.xml", "../dtdFiles/LinacLego.dtd", true);
	}
}
