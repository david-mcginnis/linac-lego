package se.esss.litterbox.linaclego.v2.webapp.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.utilities.LegoUtilities;
import se.esss.litterbox.linaclego.v2.webapp.client.EntryPointAppService;
import se.esss.litterbox.linaclego.v2.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.v2.webapp.shared.GskelException;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;
import se.esss.litterbox.simplexml.SimpleXmlException;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EntryPointAppServiceImpl extends RemoteServiceServlet implements EntryPointAppService 
{
	@Override
	public HtmlTextTree[] getTextTrees(String linacLegoDataLink) throws GskelException 
	{
		try 
		{
			String logFilePath = getServletContext().getRealPath("log/log.txt");
			String ip = getThreadLocalRequest().getRemoteAddr();
			LegoUtilities.appendTextToFile(logFilePath, new Date().toString() + " " + ip + "\n");
			
			Lego lego = new Lego(new URL(linacLegoDataLink + "/linacLego.xml"), null, false);
			lego.setLatticeFromSettings(new URL(linacLegoDataLink + "/linacLegoSets.xml"));
			lego.triggerUpdate(linacLegoDataLink);
			HtmlTextTree[] trees = new HtmlTextTree[2];
			trees[0] = EntryPointAppServiceImplStaticMethods.createPbsViewHtmlTextTree(lego, linacLegoDataLink);
			trees[1] = EntryPointAppServiceImplStaticMethods.createXmlView(lego);
			return  trees;
		} catch (LinacLegoException | MalformedURLException | SimpleXmlException e) {throw new GskelException(e);}
	}
	@Override
	public CsvFile getCsvFile(String csvFileLink) throws GskelException 
	{
		try {return EntryPointAppServiceImplStaticMethods.readCsvFile(new URL(csvFileLink));
		} catch (IOException e) {throw new GskelException(e);}
	}
	@Override
	public String[] getModelDrawingDirectories(String aigWeb, String linacLegoModelDrawingsRelLink, String drawingStructure) throws GskelException 
	{
		return EntryPointAppServiceImplStaticMethods.getModelDrawingDirectories(aigWeb, linacLegoModelDrawingsRelLink, drawingStructure);
	}
}
