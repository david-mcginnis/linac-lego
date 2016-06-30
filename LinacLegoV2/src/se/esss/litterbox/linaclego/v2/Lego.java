package se.esss.litterbox.linaclego.v2;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.jframeskeleton.StatusPanel;
import se.esss.litterbox.linaclego.v2.data.LegoInfo;
import se.esss.litterbox.linaclego.v2.data.LegoLatticeFileComment;
import se.esss.litterbox.linaclego.v2.data.LegoSet;
import se.esss.litterbox.linaclego.v2.structures.LegoCell;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.linaclego.v2.structures.LegoSection;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamDrift;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamFieldMap;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamMarker;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamPositionMonitor;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamQuad;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamSizeMonitor;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeamThinSteering;
import se.esss.litterbox.linaclego.v2.templates.LegoSlotTemplate;
import se.esss.litterbox.linaclego.v2.utilities.Zipper;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;


public class Lego implements Serializable
{
	private static final long serialVersionUID = -2662383493225970334L;
	public static final String newline = System.getProperty("line.separator");
	public static final String delim = System.getProperty("file.separator");
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
	private ArrayList<LegoSet> legoSetList = new ArrayList<LegoSet>();
	private LegoLinac legoLinac = null;
	private String title = "";
	private String revNo = "0";
	private String revComment = "none";
	private String revDate = "01-Jan-1970";
	private SimpleXmlDoc simpleXmlDoc = null;
	private URL sourceParentUrl = null;
	private ArrayList<LegoBeam> beamTypeList = new ArrayList<LegoBeam>();
	private StatusPanel statusPanel = null;
	private File latticeFileOutputLocation = null;
	private LegoPbsTreeNode legoPbsTreeNode = null;
	private LegoXmlTreeNode legoXmlTreeNode = null;
	
	public ArrayList<LegoSlotTemplate> getLegoSlotTempateList() {return legoSlotTemplateList;}
	public ArrayList<LegoSet> getLegoSetList() {return legoSetList;}
	public LegoLinac getLegoLinac() {return legoLinac;}
	public String getTitle() {return title;}
	public String getRevNo() {return revNo;}
	public String getRevComment() {return revComment;}
	public String getRevDate() {return revDate;}
	public SimpleXmlDoc getSimpleXmlDoc() {return simpleXmlDoc;}
	public ArrayList<LegoBeam> getBeamTypes() {return beamTypeList;}
	public URL getSourceParentUrl() {return sourceParentUrl;}
	public File getlatticeFileOutputLocation() {return latticeFileOutputLocation;}
	public void setStatusPanel(StatusPanel statusPanel) {this.statusPanel = statusPanel;}
	public LegoPbsTreeNode getLegoPbsTreeNode() {return legoPbsTreeNode;}
	public LegoXmlTreeNode getLegoXmlTreeNode() {return legoXmlTreeNode;}
	
	public Lego(String title, String revNo, String revComment, String revDate, double ekinMeV, double beamFrequencyMHz, StatusPanel statusPanel) throws LinacLegoException
	{
		addBeamTypes();
		this.title = title;
		this.revNo = revNo;
		this.revComment = revComment;
		this.revDate = revDate;
		this.statusPanel = statusPanel;
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
		this(sxd, null);
	}
	public Lego(SimpleXmlDoc sxd, StatusPanel statusPanel) throws LinacLegoException
	{
		addBeamTypes();
		simpleXmlDoc = sxd;
		this.statusPanel = statusPanel;

		try 
		{
			sourceParentUrl = simpleXmlDoc.getXmlSourceParentUrl();
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
			writeStatus("Reading LegoSets");
			legoSetList = new ArrayList<LegoSet>();
			SimpleXmlReader legoSetsListTag = legoTag.tagsByName("legoSets");
			if (legoSetsListTag.numChildTags() > 0)
			{
				for (int icol = 0; icol < legoSetsListTag.numChildTags(); ++icol)
				{
					SimpleXmlReader legoSetListTag = legoSetsListTag.tag(icol).tagsByName("legoSet");
					if (legoSetListTag.numChildTags() > 0)
					{
						for (int itag = 0; itag < legoSetListTag.numChildTags(); ++itag)
						{
							legoSetList.add(new LegoSet(legoSetListTag.tag(itag)));
							writeStatus("     Adding legoSet " + legoSetListTag.tag(itag).attribute("devName"));
						}
					}
				}
			}
			writeStatus("Finished reading LegoSets");
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
	public Lego(URL url, StatusPanel statusPanel) throws LinacLegoException, SimpleXmlException 
	{
		this(new SimpleXmlDoc(url), statusPanel);
	}
	public Lego(File file, StatusPanel statusPanel) throws LinacLegoException, MalformedURLException, SimpleXmlException
	{
		this(file.toURI().toURL(), statusPanel);
	}
	public Lego(String filePath, StatusPanel statusPanel) throws LinacLegoException, MalformedURLException, SimpleXmlException
	{
		this(new File(filePath), statusPanel);
	}
	public void writeStatus(String statusText) 
	{
		if (statusPanel != null)
		{
			statusPanel.setText(statusText);
		}
		else
		{
			System.out.println(statusText);
		}
	}
	public void updateXmlFile(String newXmlDocPath, String dtdLink, boolean expandSlotTemplate) throws LinacLegoException
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
			for (int iset = 0; iset < legoSetList.size(); ++iset) legoSetList.get(iset).writeXml(xw);
			xw.closeXmlTag("legoSets");
			legoLinac.writeXml(xw, expandSlotTemplate);
			xw.closeDocument();
			simpleXmlDoc = xw.getSimpleXmlDoc();
			simpleXmlDoc.setXmlSourceUrl(new File(newXmlDocPath).toURI().toURL());
		} catch (SimpleXmlException | MalformedURLException e) {throw new LinacLegoException(e);}		
	}
	public void writeXmlFile(String filePath) throws LinacLegoException
	{
		try {simpleXmlDoc.saveXmlDocument(filePath);} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public void readLatticeFile(String fileLocationPath, String latticeType) throws LinacLegoException
	{
		writeStatus("Reading lattice file...");
		BufferedReader br;
		ArrayList<String> fileBuffer = new ArrayList<String>();
		try 
		{
			String parentPath = new File(fileLocationPath).getParent();
			sourceParentUrl = new File(parentPath).toURI().toURL();
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
	public void triggerUpdate(String newXmlDocPath, String dtdLink, boolean expandSlotTemplate) throws LinacLegoException
	{
		getLegoLinac().triggerUpdate();
		updateXmlFile(newXmlDocPath, dtdLink, expandSlotTemplate);
		legoPbsTreeNode = new LegoPbsTreeNode(this);
		legoXmlTreeNode = LegoXmlTreeNode.buildLegoXmlTreeNodes(this);
		
	}
	public static String addLeadingZeros(int counter, int stringLength)
	{
		String scounter = Integer.toString(counter);
		while (scounter.length() < stringLength) scounter = "0" + scounter;
		return scounter;
	}
	public void writeLatticeFile(String filePath, String latticeType) throws LinacLegoException
	{
		try 
		{
			latticeFileOutputLocation = new File(filePath);
			PrintWriter pw = new PrintWriter(filePath);
			getLegoLinac().printLatticeCommand(pw, latticeType);
			pw.close();
		} catch (FileNotFoundException | LinacLegoException e) {throw new LinacLegoException(e);}
		
	}
	public void createReports(String parentDirectoryPath) throws LinacLegoException
	{
		File reportDirectory = new File(parentDirectoryPath + delim + "linacLegoOutput");
		if (reportDirectory.exists()) 
		{
			File[] fileList = reportDirectory.listFiles();
			if (fileList.length > 0) for (int ifile = 0; ifile < fileList.length; ++ifile) fileList[ifile].delete();
		}
		else
		{
			reportDirectory.mkdir();
		}
		if (reportDirectory.exists()) 
		{
			writeStatus("Report directory set to " + parentDirectoryPath + delim + "linacLegoOutput");
			String baseName = simpleXmlDoc.getXmlDocName().substring(0, simpleXmlDoc.getXmlDocName().lastIndexOf("."));
			try 
			{
				String fileName = reportDirectory.getPath() + delim +  baseName + ".dat";
				writeStatus("Printing " + fileName);
				writeLatticeFile(fileName, "tracewin");
				fileName = reportDirectory.getPath() + delim +  baseName + "BeamData.csv";
				writeStatus("Printing " + fileName);
				PrintWriter pw = new PrintWriter(fileName);
				getLegoLinac().printStructureTable(pw, LegoBeam.TABLE_HEADER, LegoBeam.TABLE_HEADER_UNITS, "beam");
				pw.close();
				fileName = reportDirectory.getPath() + delim +  baseName + "SlotData.csv";
				writeStatus("Printing " + fileName);
				pw = new PrintWriter(fileName);
				getLegoLinac().printStructureTable(pw, LegoSlot.TABLE_HEADER, LegoSlot.TABLE_HEADER_UNITS, "slot");
				pw.close();
				fileName = reportDirectory.getPath() + delim +  baseName + "CellData.csv";
				writeStatus("Printing " + fileName);
				pw = new PrintWriter(fileName);
				getLegoLinac().printStructureTable(pw, LegoCell.TABLE_HEADER, LegoCell.TABLE_HEADER_UNITS, "cell");
				pw.close();
				fileName = reportDirectory.getPath() + delim +  baseName + "SectionData.csv";
				writeStatus("Printing " + fileName);
				pw = new PrintWriter(fileName);
				getLegoLinac().printStructureTable(pw, LegoSection.TABLE_HEADER, LegoSection.TABLE_HEADER_UNITS, "section");
				pw.close();
				fileName = reportDirectory.getPath() + delim +  baseName + "Parsed.xml";
				writeStatus("Printing " + fileName);
				writeXmlFile(fileName);
				fileName = reportDirectory.getPath() + delim +  baseName;
				writeStatus("Printing Part Counts");
				getLegoLinac().printPartCounts(fileName);
				writeStatus("Printing RFFieldBuilder files");
				getLegoLinac().printRfFieldBuilder(reportDirectory.getPath());
				fileName = reportDirectory.getPath() + delim +  baseName + ".bin";
				writeStatus("Printing Serialized Lego");
				writeSerializedFile(fileName);
				
				try 
				{
					Zipper zipper = new Zipper(reportDirectory.getParent() + delim + "linacLego.zip");
					zipper.addFilesInDir(reportDirectory.getPath(), "xml");
					zipper.addFilesInDir(reportDirectory.getPath(), "csv");
					zipper.addFilesInDir(reportDirectory.getPath(), "dat");
					zipper.addFilesInDir(reportDirectory.getPath(), "edz");
					zipper.addFilesInDir(reportDirectory.getPath(), "bin");
					zipper.close();
					writeStatus("Compressed  file  created in " + reportDirectory.getParent() + delim +  "linacLego.zip");
				} catch (Exception e) 
				{
					LinacLegoException lle = new LinacLegoException(e);
					writeStatus(lle.getRootCause());
					throw lle;
				}
			} catch (FileNotFoundException e) { throw new LinacLegoException(e);}
		}
	}
	public void writeSerializedFile(String filePath) throws LinacLegoException 
	{
		
		try 
		{
			OutputStream  file = new FileOutputStream(filePath);
		    OutputStream buffer = new BufferedOutputStream(file);
		    ObjectOutput output = new ObjectOutputStream(buffer);
		    output.writeObject(this);
		    output.close();
		    buffer.close();
		    file.close();
		} catch (IOException e) {throw new LinacLegoException(e);}
		
	}
	public void setSettingsFromLattice() throws LinacLegoException
	{
		for (int iset = 0; iset < legoSetList.size(); ++iset) legoSetList.get(iset).setSettingFromLattice(legoLinac);
	}
	public void setLatticeFromSettings() throws LinacLegoException
	{
		for (int iset = 0; iset < legoSetList.size(); ++iset) legoSetList.get(iset).setLatticeFromSetting(legoLinac);;
	}
	public static Lego readSerializedLego(String filePath) throws LinacLegoException
	{
		try 
		{
			InputStream file = new FileInputStream(filePath);
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream (buffer);
			Lego serLego = (Lego) input.readObject();
			input.close();
			buffer.close();
			file.close();
			serLego.setStatusPanel(null);
			return serLego;
		} catch (IOException | ClassNotFoundException e) {throw new LinacLegoException(e);}
		
	}
	public static Lego readSerializedLegoFromWeb(String htmlLink) throws LinacLegoException
	{
		try {
			URL url = new URL(htmlLink);
			URLConnection connection = url.openConnection();
			InputStream file = connection.getInputStream();
			InputStream buffer = new BufferedInputStream(file);
			ObjectInput input = new ObjectInputStream (buffer);
			Lego serLego = (Lego) input.readObject();
			input.close();
			buffer.close();
			file.close();
			serLego.setStatusPanel(null);
			return serLego;
		} catch (IOException | ClassNotFoundException e) {throw new LinacLegoException(e);}
	}
	public static void main(String[] args) throws LinacLegoException, MalformedURLException, SimpleXmlException 
	{
//		Lego lego = new Lego("test", "revNo", "revComment", "revDate", 89.0, 352.21, null);
//		lego.readLatticeFile("testFiles/5.0_SpokeV2.dat", "tracewin");
//		lego.getLegoLinac().triggerUpdate();
// 		lego.writeXmlFile("5.0_SpokeV2.xml", "../dtdFiles/LinacLego.dtd", false);

//		Lego lego = new Lego("testFiles/5.0_SpokeV3.xml", null);
//		Lego lego = Lego.readSerializedLego("testFiles/5.0_SpokeV3.bin");
//		lego.replaceSlotsWithTemplates();
//		lego.triggerUpdate("testFiles/5.0_SpokeV3.xml","../dtdFiles/LinacLego.dtd", false);
//		lego.createReports("testFiles");
		
//		Lego lego =  readSerializedLegoFromWeb("https://aig.esss.lu.se:8443/LinacLegoV2DataWeb/data/test/linacLego.bin");
//		lego.getLegoLinac().triggerUpdate();
		Lego lego = new Lego("/home/dmcginnis427/Dropbox/TB18LatticeImport/5.0_Spoke.xml", null);
		lego.triggerUpdate("/home/dmcginnis427/Dropbox/TB18LatticeImport/5.0_Spoke.xml","../dtdFiles/LinacLego.dtd", false);
		lego.setLatticeFromSettings();
		lego.triggerUpdate("/home/dmcginnis427/Dropbox/TB18LatticeImport/5.0_Spoke.xml","../dtdFiles/LinacLego.dtd", false);
	}
}
