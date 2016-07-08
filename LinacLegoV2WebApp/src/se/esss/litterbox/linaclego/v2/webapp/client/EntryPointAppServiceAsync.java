package se.esss.litterbox.linaclego.v2.webapp.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

import se.esss.litterbox.linaclego.v2.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;

public interface EntryPointAppServiceAsync 
{
	void getCsvFile(String csvFileLink, AsyncCallback<CsvFile> callback);
	void getModelDrawingDirectories(String aigWeb, String linacLegoModelDrawingsRelLink, String drawingStructure,AsyncCallback<String[]> callback);
	void getTextTrees(String linacLegoBinaryLink, AsyncCallback<HtmlTextTree[]> callback);
}
