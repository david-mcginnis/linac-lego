package se.esss.litterbox.linaclego.v2.app;

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

import se.esss.litterbox.jframeskeleton.JFrameSkeleton;
import se.esss.litterbox.jframeskeleton.StatusPanel;
import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.utilities.RfFieldProfileBuilder;
import se.esss.litterbox.simplexml.SimpleXmlException;

@SuppressWarnings("serial")
public class LegoApp extends JFrameSkeleton 
{
	private static final boolean printStackTrace = true;
	private static final String iconLocation = "se/esss/litterbox/linaclego/v2/files/lego.jpg";
	private static final String frametitle = "LinacLego";
	private static final String statusBarTitle = "Info";
	private static final int numStatusLines = 10;
	private static final String version = "v2.12";
	private static final String versionDate = "August 31, 2016";

	private Lego lego;
	private JTabbedPane mainTabbedPane; 
	private JScrollPane pbsTreeView;
	private JScrollPane xmlTreeView;
	private StatusPanel matchLogStatusPanel;
	private JTree pbsTree;
	private JTree xmlTree;
	private String suggestedFileName = "linacLego.xml";
	private File openedXmlFile = null;
	private WatchService watchService = null;
	private LegoAppWatchKeyRunnable watchKeyRunnable = null;
	private Thread watchKeyThread = null;
	private String latticeFileType = "tracewin";
	
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
		if (menu.equals("File") && menuItem.equals("Open Lattice File")) openLatticeFile();
		if (menu.equals("File") && menuItem.equals("Save Lattice File")) saveLatticeFile();
		if (menu.equals("File") && menuItem.equals("Exit")) this.quitProgram();
		if (menu.equals("PBS Level View") && menuItem.equals("Section")) expandPbsTreeTo(1, pbsTree);
		if (menu.equals("PBS Level View") && menuItem.equals("Cell")) expandPbsTreeTo(2, pbsTree);
		if (menu.equals("PBS Level View") && menuItem.equals("Slot")) expandPbsTreeTo(3, pbsTree);
		if (menu.equals("PBS Level View") && menuItem.equals("Beam")) expandPbsTreeTo(4, pbsTree);
		if (menu.equals("Actions") && menuItem.equals("Match Slot Models")) matchSlotModels();
		if (menu.equals("Actions") && menuItem.equals("Create Reports")) createReports();
		if (menu.equals("Actions") && menuItem.equals("Build XML Field File")) buildRFField();
		if (menu.equals("Actions") && menuItem.equals("Update LegoSets from Lattice")) updateLegoSetsFromLattice();
		if (menu.equals("Actions") && menuItem.equals("Update Lattice from LegoSets")) updateLatticeFromLegoSets();
		
	}

	@Override
	public void cleanupProgramBeforeExit() 
	{
	}

	@Override
	public void setupMainPanel() 
	{
        xmlTree = new JTree(new DefaultMutableTreeNode("LinacLego"));
        pbsTree = new JTree(new DefaultMutableTreeNode("LinacLego"));
        
        xmlTreeView = new JScrollPane(xmlTree);
        xmlTreeView.setPreferredSize(new Dimension(800,600));

        pbsTreeView = new JScrollPane(pbsTree);
        pbsTreeView.setPreferredSize(new Dimension(800,600));
        
        matchLogStatusPanel = new StatusPanel(20, "Slot Match Log");
        matchLogStatusPanel.setInsertAtTop(false);

		mainTabbedPane = new JTabbedPane();
		mainTabbedPane.addTab("xml Tree", xmlTreeView);
		mainTabbedPane.addTab("pbs Tree", pbsTreeView);
		mainTabbedPane.addTab("Mathing Log", matchLogStatusPanel.getScrollPane());
		getMainPane().add(mainTabbedPane);
	}

	@Override
	public void setupMenuBar() 
	{
		addMenuItem("File","Open LinacLego File");
		addMenuItem("File","Save LinacLego File");
		addMenuItem("File","Open Lattice File");
		addMenuItem("File","Save Lattice File");
		addMenuItem("File","Exit");
		addMenuItem("Actions","Match Slot Models");
		addMenuItem("Actions","Create Reports");
		addMenuItem("Actions","Update Lattice from LegoSets");
		addMenuItem("Actions","Update LegoSets from Lattice");
		addMenuItem("Actions","Build XML Field File");
		addMenuItem("PBS Level View","Section");
		addMenuItem("PBS Level View","Cell");
		addMenuItem("PBS Level View","Slot");
		addMenuItem("PBS Level View","Beam");
		addMenuItem("Help","Help");
		addMenuItem("Help","About");
		
		setEnabledMenuItem("File","Save LinacLego File",false);
		setEnabledMenuItem("File","Save Lattice File",false);
		setEnabledMenuItem("Actions","Match Slot Models",false);
		setEnabledMenuItem("Actions","Create Reports",false);
		setEnabledMenuItem("Actions","Update Lattice from LegoSets",false);
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
				boolean triggerLegoUpdate = true;
				if (extension.equals("xml"))
				{
					lego = new Lego(openedXmlFile, getStatusPanel(), true);
				}
				else
				{
					triggerLegoUpdate = false;
					lego = Lego.readSerializedLego(pathWoExt + ".bin");
					lego.setStatusPanel(getStatusPanel());
				}
				loadLinacLegoFile(openedXmlFile.getPath(), triggerLegoUpdate);
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
	protected void loadLinacLegoFile(String newXmlDocPath, boolean triggerLegoUpdate)
	{
		try 
		{
			if (triggerLegoUpdate) lego.triggerUpdate(newXmlDocPath);

			setTitle("LinacLego " + openedXmlFile.getName());
//			pbsTree.setModel(new DefaultTreeModel(LegoXmlTreeNode.buildLegoXmlTreeNodes(lego)));
//			xmlTree.setModel(new DefaultTreeModel(new LegoPbsTreeNode(lego)));
			xmlTree.setModel(new DefaultTreeModel(LegoXmlTreeNode.buildLegoXmlTreeNodes(lego)));
			pbsTree.setModel(new DefaultTreeModel(new LegoPbsTreeNode(lego)));
			expandPbsTreeTo(3, pbsTree);
			setEnabledMenu("PBS Level View", true);
			setEnabledMenuItem("File","Save LinacLego File",true);
			setEnabledMenuItem("File","Save Lattice File",true);
			setEnabledMenuItem("Actions","Match Slot Models",true);
			setEnabledMenuItem("Actions","Create Reports",true);
			setEnabledMenuItem("Actions","Update Lattice from LegoSets",true);
			setEnabledMenuItem("Actions","Update LegoSets from Lattice",true);
		} catch (LinacLegoException e) 
		{
			if (printStackTrace) e.printStackTrace();
			messageDialog("Error: " + e.getMessage());
			setEnabledMenu("PBS Level View", false);
			setEnabledMenuItem("File","Save LinacLego File",false);
			setEnabledMenuItem("File","Save Lattice File",false);
			setEnabledMenuItem("Actions","Match Slot Models",false);
			setEnabledMenuItem("Actions","Create Reports",false);
			setEnabledMenuItem("Actions","Update Lattice from LegoSets",false);
			setEnabledMenuItem("Actions","Update LegoSets from Lattice",false);
		}
	}
    private void expandPbsTreeTo(int level, JTree jtree)
    {
    	int row = jtree.getRowCount() - 1;
    	while (row >= 0) 
    	{
    		jtree.collapseRow(row);
          row--;
    	}
    	DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) jtree.getModel().getRoot();
    	if (currentNode == null) return;
    	do 
    	{
    		if (currentNode.getLevel() == level) 
    		{
    			jtree.expandPath(new TreePath(currentNode.getPath()));
    		}
    		currentNode = currentNode.getNextNode();
    	}
    	while (currentNode != null);
    }
	private void saveLinacLegoFile()
	{
		if (!changeDocInfo(false)) return;
		String[] xmlExtensions = {"xml"};
		File xmlFile = this.saveFileDialog(xmlExtensions, "Save LinacLego File", suggestedFileName);
		if (xmlFile != null)
		{
			if (!this.overwriteFileDialog(xmlFile)) return;

			try 
			{
				suggestedFileName = xmlFile.getName();
				loadLinacLegoFile(xmlFile.getPath(), true);				
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
	private void saveLatticeFile()
	{
		String latticeFileTypeNew = (String)JOptionPane.showInputDialog(this, "Enter lattice file type", "Lattice File type", JOptionPane.QUESTION_MESSAGE,null,null,latticeFileType);
		if (latticeFileTypeNew == null ) return;
		latticeFileType = latticeFileTypeNew;
		String[] datExtensions = {"dat"};
		File datFile = this.saveFileDialog(datExtensions, "Save " + latticeFileType + " File", suggestedFileName.substring(0,suggestedFileName.lastIndexOf(".")) + ".dat");
		if (datFile != null)
		{
			if (!this.overwriteFileDialog(datFile)) return;

			try 
			{
				lego.writeLatticeFile(datFile.getPath(), latticeFileType);
			} catch (LinacLegoException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
	}
	private void openLatticeFile()
	{
		String latticeFileTypeNew = (String)JOptionPane.showInputDialog(this, "Enter lattice file type", "Lattice File type", JOptionPane.QUESTION_MESSAGE,null,null,latticeFileType);
		if (latticeFileTypeNew == null ) return;
		latticeFileType = latticeFileTypeNew;
		String[] extensions = {"dat"};
		File latticeFile  = openFileDialog(extensions, "Open " + latticeFileType + " File");
		if (latticeFile != null)
		{
			String xmlFilePath = latticeFile.getPath().substring(0, latticeFile.getPath().lastIndexOf(".")) + ".xml";
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
		 		lego = new Lego(latticeFile.getName(), "1.0", "revComment", new Date().toString(), ekinMeV, beamFreqMHz, getStatusPanel(), true);
				lego.readLatticeFile(latticeFile.getPath(), latticeFileType);
				loadLinacLegoFile(xmlFilePath, true);				
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
				if (!this.overwriteFileDialog(xmlFieldFilePath)) return;
				getStatusPanel().setText("Writing RfFieldProfileBuilder " + xmlFieldFilePath);
				fpb.writeXmlFile(xmlFieldFilePath);
			} catch (LinacLegoException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
		
	}
	private void updateLegoSetsFromLattice()
	{
		try 
		{
			String[] extensions = {"xml"};
			File legoSetsSourceFile  = openFileDialog(extensions, "Choose LinacLegoSets SOURCE file");
			if (legoSetsSourceFile == null) return;
			File legoSetsDestFile  = openFileDialog(extensions, "Choose LinacLegoSets DESTINATION file");
			if (legoSetsDestFile == null) return;
			if (!this.overwriteFileDialog(legoSetsDestFile)) return;
			lego.setSettingsFromLattice(legoSetsSourceFile.getPath(), legoSetsDestFile.getPath());
		} 
		catch (LinacLegoException e) 
		{
			if (printStackTrace) e.printStackTrace();
			messageDialog("Error: " + e.getMessage());
		}
	}
	private void updateLatticeFromLegoSets()
	{
		try 
		{
			String[] extensions = {"xml"};
			File legoSetsFile  = openFileDialog(extensions, "Choose LinacLegoSets file");
			if (legoSetsFile != null)
			{
				lego.setLatticeFromSettings(legoSetsFile.getPath());;
				loadLinacLegoFile(openedXmlFile.getPath(), true);
			}

		} 
		catch (LinacLegoException e) 
		{
			if (printStackTrace) e.printStackTrace();
			messageDialog("Error: " + e.getMessage());
		}
	}
	private void matchSlotModels()
	{
		if (lego != null)
		{
			try 
			{
				matchLogStatusPanel.clearStatus();
				lego.setStatusPanel(matchLogStatusPanel);
				lego.replaceSlotsWithTemplates();
				lego.setStatusPanel(getStatusPanel());
				loadLinacLegoFile(openedXmlFile.getPath(), true);
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
			if (!changeDocInfo(true)) return;
			String reportDirectoryPath = lego.getReportDirectoryPath(openedXmlFile.getParent());
			if (!this.overwriteFileDialog(reportDirectoryPath)) return;
			try {lego.createReports(openedXmlFile.getParent());} 
			catch (LinacLegoException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
	}
	private boolean changeDocInfo(boolean triggerUpdate) 
	{
		String oldRevNo = lego.getRevNo();
		if (lego == null) return false;
		String revNo = (String)JOptionPane.showInputDialog(this, "Enter the new revision number", "LinacLego Revision No.", JOptionPane.QUESTION_MESSAGE,null,null,lego.getRevNo());
		if (revNo == null) return false;
		lego.setRevNo(revNo);
		String revComment = (String)JOptionPane.showInputDialog(this, "Enter the new revision comment", "LinacLego Revision Comment.", JOptionPane.QUESTION_MESSAGE,null,null,lego.getRevComment());
		if (revComment == null)
		{
			lego.setRevNo(oldRevNo);
			return false;
		}
		lego.setRevComment(revComment);
		lego.setRevDate(new Date().toString());
		if (triggerUpdate)
		{
			try
			{
				lego.updateXmlFile(openedXmlFile.getPath());
				
			} catch (LinacLegoException e) 
			{
				if (printStackTrace) e.printStackTrace();
				messageDialog("Error: " + e.getMessage());
			}
		}
		return true;
	}
	public static void main(String[] args) 
	{
		new LegoApp();
	}

}
