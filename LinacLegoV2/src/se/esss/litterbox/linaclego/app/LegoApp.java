package se.esss.litterbox.linaclego.app;

import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.esss.litterbox.jframeskeleton.JFrameSkeleton;
import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoCell;
import se.esss.litterbox.linaclego.structures.LegoSection;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.linaclego.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.utilities.RfFieldProfileBuilder;
import se.esss.litterbox.simplexml.SimpleXmlException;

@SuppressWarnings("serial")
public class LegoApp extends JFrameSkeleton 
{
	private static final boolean printStackTrace = true;
	private static final String iconLocation = "se/esss/litterbox/linaclego/files/lego.jpg";
	private static final String frametitle = "LinacLego";
	private static final String statusBarTitle = "Info";
	private static final int numStatusLines = 10;
	private static final String version = "v2.0";
	private static final String versionDate = "May 17, 2016";

	private Lego lego;
	private JTabbedPane mainTabbedPane; 
	private JScrollPane pbsTreeView;
	private JScrollPane xmlTreeView;
	private JTree pbsTree;
	private JTree xmlTree;
	private DefaultMutableTreeNode pbsTreeNode;
	private String suggestedFileName = "linacLego.xml";
	private File openedXmlFile = null;
	private WatchService watchService = null;
	private LegoAppWatchKeyRunnable watchKeyRunnable = null;
	private Thread watchKeyThread = null;
	
	protected WatchService getWatchService() {return watchService;}
	protected File getOpenedXmlFile() {return openedXmlFile;}
	
	public LegoApp() 
	{
		super(iconLocation, frametitle, statusBarTitle, numStatusLines, version, versionDate);
		getStatusPanel().setText(frametitle);
		getStatusPanel().setText("Version " + version);
		getStatusPanel().setText("Last Updated " + versionDate);

		try {watchService = FileSystems.getDefault().newWatchService();} catch (IOException e) {};
		watchKeyRunnable = new LegoAppWatchKeyRunnable(this);
		watchKeyThread  = new Thread(watchKeyRunnable);
		watchKeyThread.start();
	}

	@Override
	public void actOnAction(String arg0) 
	{
		
	}

	@Override
	public void actOnMenu(String menu, String menuItem) 
	{
		if (menu.equals("Help") && menuItem.equals("About")) about();
		if (menu.equals("File") && menuItem.equals("Open LinacLego File")) openLinacLegoFile();
		if (menu.equals("File") && menuItem.equals("Save LinacLego File")) saveLinacLegoFile();
		if (menu.equals("File") && menuItem.equals("Open TraceWin File")) openTraceWinFile();
		if (menu.equals("File") && menuItem.equals("Exit")) this.quitProgram();
		if (menu.equals("PBS Level View") && menuItem.equals("Section")) expandPbsTreeTo(1);
		if (menu.equals("PBS Level View") && menuItem.equals("Cell")) expandPbsTreeTo(2);
		if (menu.equals("PBS Level View") && menuItem.equals("Slot")) expandPbsTreeTo(3);
		if (menu.equals("PBS Level View") && menuItem.equals("Beam")) expandPbsTreeTo(4);
		if (menu.equals("Actions") && menuItem.equals("Match Slot Models")) matchSlotModels();
		if (menu.equals("Actions") && menuItem.equals("Create Reports")) createReports();
		if (menu.equals("Actions") && menuItem.equals("Build XML Field File")) buildRFField();
		
	}

	@Override
	public void cleanupProgramBeforeExit() 
	{
	}

	@Override
	public void setupMainPanel() 
	{
        xmlTree = new JTree(new DefaultMutableTreeNode("LinacLego"));
        pbsTreeNode = new DefaultMutableTreeNode("LinacLego");
        pbsTree = new JTree(pbsTreeNode);
        
        xmlTreeView = new JScrollPane(xmlTree);
        xmlTreeView.setPreferredSize(new Dimension(800,600));

        pbsTreeView = new JScrollPane(pbsTree);
        pbsTreeView.setPreferredSize(new Dimension(800,600));

		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addTab("xml Tree", xmlTreeView);
		mainTabbedPane.addTab("pbs Tree", pbsTreeView);
		getMainPane().add(mainTabbedPane);
	}

	@Override
	public void setupMenuBar() 
	{
		addMenuItem("File","Open LinacLego File");
		addMenuItem("File","Save LinacLego File");
		addMenuItem("File","Open TraceWin File");
		addMenuItem("File","Exit");
		addMenuItem("Actions","Match Slot Models");
		addMenuItem("Actions","Create Reports");
		addMenuItem("Actions","Update Lattice Settings from LegoSets");
		addMenuItem("Actions","Update LegoSets from Lattice");
		addMenuItem("Actions","Build XML Field File");
		addMenuItem("PBS Level View","Section");
		addMenuItem("PBS Level View","Cell");
		addMenuItem("PBS Level View","Slot");
		addMenuItem("PBS Level View","Beam");
		addMenuItem("Help","Help");
		addMenuItem("Help","About");
		
		setEnabledMenuItem("File","Save LinacLego File",false);
		setEnabledMenuItem("Actions","Match Slot Models",false);
		setEnabledMenuItem("Actions","Create Reports",false);
		setEnabledMenuItem("Actions","Update Lattice Settings from LegoSets",false);
		setEnabledMenuItem("Actions","Update LegoSets from Lattice",false);
		setEnabledMenu("PBS Level View", false);
		setEnabledMenuItem("Help","Help",false);
	}
	private void about()
	{
		messageDialog(frametitle + " " + version + "\n" + "Last Updated " + versionDate);
	}
	private void openLinacLegoFile()
	{
		String[] xmlExtensions = {"xml", "bin"};
		
		File xmlFile  = openFileDialog(xmlExtensions, "Open LinacLego File");
		if (xmlFile != null)
		{
			String extension = xmlFile.getName().substring(xmlFile.getName().lastIndexOf(".") + 1);
			String pathWoExt = xmlFile.getPath().substring(0, xmlFile.getPath().lastIndexOf("."));
			if (extension.equals("xml"))
			{
				openedXmlFile = new File(xmlFile.getPath());
			}
			else
			{
				openedXmlFile = new File(pathWoExt + ".xml");
			}
			suggestedFileName = openedXmlFile.getName();
			
			try 
			{
				if (extension.equals("xml"))
				{
					lego = new Lego(openedXmlFile, getStatusPanel());
				}
				else
				{
					lego = Lego.readSerializedLego(pathWoExt + ".bin");
					lego.setStatusPanel(getStatusPanel());
				}
				loadLinacLegoFile(openedXmlFile.getPath());
				Path path = Paths.get(this.getLastFileDirectory());	// Get the directory to be monitored
				path.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.ENTRY_DELETE);	// Register the directory
				
				
			} catch (IOException | LinacLegoException | SimpleXmlException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
	}
	protected void loadLinacLegoFile(String newXmlDocPath)
	{
		try 
		{
			lego.triggerUpdate(newXmlDocPath, "../dtdFiles/LinacLego.dtd", false);
			setTitle("LinacLego " + openedXmlFile.getName());
			xmlTree.setModel(new DefaultTreeModel(buildTreeNode((Node) lego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
			buildPbsTreeNew(lego);
			setEnabledMenu("PBS Level View", true);
			setEnabledMenuItem("File","Save LinacLego File",true);
			setEnabledMenuItem("Actions","Match Slot Models",true);
			setEnabledMenuItem("Actions","Create Reports",true);
		} catch (LinacLegoException e) 
		{
			if (printStackTrace) e.printStackTrace();
			messageDialog("Error: " + e.getMessage());
			setEnabledMenu("PBS Level View", false);
			setEnabledMenuItem("File","Save LinacLego File",false);
			setEnabledMenuItem("Actions","Match Slot Models",false);
			setEnabledMenuItem("Actions","Create Reports",false);
		}
	}
    private void buildPbsTreeNew(Lego lego) throws LinacLegoException
    {
		pbsTreeNode = new LegoAppDefaultMutableTreeNode(lego);
		pbsTree.setModel(new DefaultTreeModel(pbsTreeNode));
    	LegoAppDefaultMutableTreeNode linacNode = new LegoAppDefaultMutableTreeNode(lego.getLegoLinac());
     	pbsTreeNode.add(linacNode);
		for (int isec = 0; isec < lego.getLegoLinac().getLegoSectionList().size(); ++isec)
		{
			LegoSection section = lego.getLegoLinac().getLegoSectionList().get(isec);
			LegoAppDefaultMutableTreeNode sectionNode = new LegoAppDefaultMutableTreeNode(section);
			linacNode.add(sectionNode);
			for (int icell = 0; icell < section.getLegoCellList().size(); ++icell)
			{
				LegoCell cell = section.getLegoCellList().get(icell);
				LegoAppDefaultMutableTreeNode cellNode = new LegoAppDefaultMutableTreeNode(cell);
				sectionNode.add(cellNode);
				for (int islot = 0; islot < cell.getLegoSlotList().size(); ++islot)
				{
					LegoSlot slot = cell.getLegoSlotList().get(islot);
					LegoAppDefaultMutableTreeNode slotNode = new LegoAppDefaultMutableTreeNode(slot);
					cellNode.add(slotNode);
					for (int ibeam = 0; ibeam < slot.getLegoBeamList().size(); ++ibeam)
					{
						LegoBeam beam = slot.getLegoBeamList().get(ibeam);
						LegoAppDefaultMutableTreeNode beamNode = new LegoAppDefaultMutableTreeNode(beam);
						slotNode.add(beamNode);
					}
				}
			}
		}
    }
    private LegoAppDefaultMutableTreeNodeWrapper buildTreeNode(Node root){
    	LegoAppDefaultMutableTreeNodeWrapper dmtNode;

        dmtNode = new LegoAppDefaultMutableTreeNodeWrapper(root);
        NodeList nodeList = root.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) 
        {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
            {
                if (tempNode.hasChildNodes()) 
                {
                    // loop again if has child nodes
                    dmtNode.add(buildTreeNode(tempNode));
                }
                else
                {
                	dmtNode.add(new LegoAppDefaultMutableTreeNodeWrapper(tempNode));
                }
            }
        }
        return dmtNode;
    }
    private void expandPbsTreeTo(int level)
    {
    	int row = pbsTree.getRowCount() - 1;
    	while (row >= 0) 
    	{
    		pbsTree.collapseRow(row);
          row--;
    	}
    	DefaultMutableTreeNode currentNode = pbsTreeNode.getNextNode();
    	if (currentNode == null) return;
    	do 
    	{
    		if (currentNode.getLevel() == level) 
    		{
    			pbsTree.expandPath(new TreePath(currentNode.getPath()));
    		}
    		currentNode = currentNode.getNextNode();
    	}
    	while (currentNode != null);
    }
	private void saveLinacLegoFile()
	{
		String[] xmlExtensions = {"xml"};
		File xmlFile = this.saveFileDialog(xmlExtensions, "Save LinacLego File", suggestedFileName);
		if (xmlFile != null)
		{
			try 
			{
				suggestedFileName = xmlFile.getName();
				getStatusPanel().setText("Saving "+ xmlFile.getPath());
				lego.writeXmlFile(xmlFile.getPath());
				String serPath = xmlFile.getPath().substring(0, xmlFile.getPath().lastIndexOf(".")) + ".bin";
				getStatusPanel().setText("Saving "+ serPath);
				lego.writeSerializedFile(serPath);
				openedXmlFile = new File(xmlFile.getPath());
				Path path = Paths.get(this.getLastFileDirectory());	// Get the directory to be monitored
				path.register(watchService,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.ENTRY_DELETE);	// Register the directory
				this.setTitle("LinacLego " + openedXmlFile.getName());
			} catch (LinacLegoException | IOException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
	}
	private void openTraceWinFile()
	{
		String[] extensions = {"dat"};
		File traceWinFile  = openFileDialog(extensions, "Open TraceWin File");
		if (traceWinFile != null)
		{
			String xmlFilePath = traceWinFile.getPath().substring(0, traceWinFile.getPath().lastIndexOf(".")) + ".xml";
			suggestedFileName = new File(xmlFilePath).getName();
			openedXmlFile = new File(xmlFilePath);
			
			try 
			{
				double ekinMeV = 0.0;
				double beamFreqMHz = 0.0;
				String ekinMeVString = JOptionPane.showInputDialog("Enter Starting Energy in MeV: ");
				if (ekinMeVString != null) ekinMeV = Double.parseDouble(ekinMeVString);
				String beamFreqMHzString = JOptionPane.showInputDialog("Enter Bunch Frequency in MHz: ");
				if (beamFreqMHzString != null) beamFreqMHz = Double.parseDouble(beamFreqMHzString);
		 		lego = new Lego(traceWinFile.getName(), "1.0", "revComment", new Date().toString(), ekinMeV, beamFreqMHz, getStatusPanel());
				lego.readLatticeFile(traceWinFile.getPath(), "tracewin");
				loadLinacLegoFile(xmlFilePath);				
			} catch (LinacLegoException e)
			{
				messageDialog("Error: " + e.getRootCause());
			} 
			catch (RuntimeException e)
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			} 
		}
		
	}
	private void buildRFField()
	{
		String[] extensions = {"edz"};
		File traceWinFile  = openFileDialog(extensions, "Open TraceWin FieldMap File");
		if (traceWinFile != null)
		{
			double storedEnergy = 1.0;
			String storedEnergyString = JOptionPane.showInputDialog("Enter Stored Energy (J): ");
			if (storedEnergyString != null) storedEnergy = Double.parseDouble(storedEnergyString);
			try {
				RfFieldProfileBuilder fpb = RfFieldProfileBuilder.readTraceWinFieldProfile(storedEnergy, traceWinFile.getPath());
				String xmlFieldFilePath = traceWinFile.getPath().substring(0, traceWinFile.getPath().lastIndexOf(".")) + ".xml";
				getStatusPanel().setText("Writing RfFieldProfileBuilder " + xmlFieldFilePath);
				fpb.writeXmlFile(xmlFieldFilePath);
			} catch (LinacLegoException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
		
	}
	private void matchSlotModels()
	{
		if (lego != null)
		{
			try 
			{
				lego.replaceSlotsWithTemplates();
				loadLinacLegoFile(openedXmlFile.getPath());
			} catch (LinacLegoException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
	}
	private void createReports()
	{
		if (lego != null)
		{
			File reportDirectoryParent  = chooseDirectoryDialog("Select Parent Directory...");
			if (reportDirectoryParent != null)
			{
				try {lego.createReports(reportDirectoryParent.getPath());} 
				catch (LinacLegoException e) 
				{
					if (printStackTrace) e.printStackTrace();
					messageDialog("Error: " + e.getMessage());
				}
			} 
		}
	}
	public static void main(String[] args) 
	{
		new LegoApp();
	}

}