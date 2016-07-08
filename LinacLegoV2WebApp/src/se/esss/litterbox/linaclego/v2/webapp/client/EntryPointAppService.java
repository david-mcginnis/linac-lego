package se.esss.litterbox.linaclego.v2.webapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import se.esss.litterbox.linaclego.v2.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.v2.webapp.shared.GskelException;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;


/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("entrypointapp")
public interface EntryPointAppService extends RemoteService 
{
	HtmlTextTree[] getTextTrees(String linacLegoBinaryLink) throws GskelException;
	CsvFile getCsvFile(String csvFileLink) throws GskelException;
	String[] getModelDrawingDirectories(String aigWeb, String linacLegoModelDrawingsRelLink, String drawingStructure) throws GskelException;
}
