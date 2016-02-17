/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.linaclego.webapp.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import se.esss.litterbox.linaclego.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.webapp.shared.HtmlTextTree;
import se.esss.litterbox.linaclego.webapp.shared.LinacLegoWebAppException;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("linacLego")
public interface LinacLegoService extends RemoteService 
{
	HtmlTextTree getPbsViewHtmlTextTree(String linacLegoXmlLink) throws LinacLegoWebAppException;
	HtmlTextTree getXmlViewHtmlTextTree(String linacLegoXmlLink) throws LinacLegoWebAppException;
	CsvFile getCsvFile(String csvFileLink) throws LinacLegoWebAppException;
	String[] getModelDrawingDirectories(String aigWeb, 
			String linacLegoModelDrawingsRelLink, String drawingStructure) throws LinacLegoWebAppException;
}
