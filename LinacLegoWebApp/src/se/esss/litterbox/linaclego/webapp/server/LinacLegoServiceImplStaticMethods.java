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
package se.esss.litterbox.linaclego.webapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.Section;
import se.esss.litterbox.linaclego.structures.beamlineelement.BeamLineElement;
import se.esss.litterbox.linaclego.structures.cell.Cell;
import se.esss.litterbox.linaclego.structures.legomonitor.LegoMonitor;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.linaclego.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.webapp.shared.HtmlTextTree;
import se.esss.litterbox.linaclego.webapp.shared.LinacLegoWebAppException;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LinacLegoServiceImplStaticMethods 
{
    static HtmlTextTree buildXmlTextTree(Node root, String tagStyle, String attLabelStyle, String attValueStyle, String attWhiteSpaceStyle)
    {
    	HtmlTextTree htmlTextTree = new HtmlTextTree();
		htmlTextTree.setTagStyle(tagStyle);
		htmlTextTree.setAttLabelStyle(attLabelStyle);
		htmlTextTree.setAttValueStyle(attValueStyle);
		htmlTextTree.setAttWhiteSpaceStyle(attWhiteSpaceStyle);

		setXmlHtmlDisplay(htmlTextTree, root);
        NodeList nodeList = root.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) 
        {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
            {
            	HtmlTextTree childHtmlTextTree = new HtmlTextTree();
            	childHtmlTextTree.inheritStyles(htmlTextTree);
            	setXmlHtmlDisplay(childHtmlTextTree, tempNode);
          	
                if (tempNode.hasChildNodes()) 
                {
                    // loop again if has child nodes
                	htmlTextTree.add(buildXmlTextTree(tempNode, tagStyle, attLabelStyle, attValueStyle, attWhiteSpaceStyle));
                }
                else
                {
                	htmlTextTree.add(childHtmlTextTree);
                }
            }
        }
        return htmlTextTree;
    }
	private static void setXmlHtmlDisplay(HtmlTextTree htmlTextTree, Node xmlNode)
	{

		SimpleXmlReader sxr = new SimpleXmlReader(xmlNode);
		htmlTextTree.setTag(sxr.tagName());
		
		String id = "";
		try {id = sxr.attribute("id");} catch (SimpleXmlException e) {}
		if (id.length() > 0)
		{
			htmlTextTree.addAttribute("id", id, 0);
		}
		ArrayList<String[]> attributes = sxr.getAttributes();
		if (attributes != null)
		{
			for (int ii = 0; ii < attributes.size(); ++ii)
			{
				if (!attributes.get(ii)[0].equals("id"))
				{
					htmlTextTree.addAttribute(attributes.get(ii)[0], attributes.get(ii)[1], 0);
				}
			}
		}
		String cdata = sxr.getCharacterData();
		if (cdata != null )
		{
			if (!stripWhiteSpaces(cdata).equals(""))
				htmlTextTree.addAttribute("cdata", cdata, 0);
		}
		return;
	}
	public static String stripWhiteSpaces(String whitey)
	{
		int numChar = 0;
		for (int ii = 0; ii < whitey.length(); ++ii)
		{
			if (whitey.charAt(ii) != ' ') 
				if (whitey.charAt(ii) != '\n') 
					numChar = numChar + 1;
		}
		if (numChar == 0) return "";
		char[] slimJimArray = new char[numChar];
		int iChar = 0;
		for (int ii = 0; ii < whitey.length(); ++ii)
		{
			if (whitey.charAt(ii) != ' ') 
			{
				slimJimArray[iChar] = whitey.charAt(ii);
				iChar = iChar + 1;
			}
		}
		return new String(slimJimArray);
	}
	public static HtmlTextTree createXmlView(URL linacLegoXmlURL) throws SimpleXmlException    
	{
		String xmlViewTagStyle = "xmlTagLabel";
		String xmlViewAttLabelStyle = "xmlAttLabel";
		String xmlViewAttValueStyle = "xmlAttValue";
		String xmlViewAttWhiteSpaceStyle = "xmlWhiteSpace";
		SimpleXmlDoc sxd = new SimpleXmlDoc(linacLegoXmlURL);
		return  LinacLegoServiceImplStaticMethods.buildXmlTextTree((Node) sxd.getXmlDoc().getDocumentElement(), xmlViewTagStyle, xmlViewAttLabelStyle, xmlViewAttValueStyle, xmlViewAttWhiteSpaceStyle);
	}
	public static HtmlTextTree createPbsViewHtmlTextTree(URL linacLegoXmlURL) throws SimpleXmlException, LinacLegoException   
	{
		String pbsTagStyle = "pbsTagLabel";
		String pbsAttLabelStyle = "pbsAttLabel";
		String pbsAttValueStyle = "pbsAttValue";
		String pbsAttWhiteSpaceStyle = "pbsWhiteSpace";
		SimpleXmlDoc sxd = new SimpleXmlDoc(linacLegoXmlURL);
		LinacLego linacLego = new LinacLego(sxd);
		linacLego.setStatusPanel(null);
		linacLego.readHeader();
		linacLego.updateLinac();
		LinacLegoPbsHtmlTextTree pbsView = new LinacLegoPbsHtmlTextTree(linacLego, pbsTagStyle, pbsAttLabelStyle, pbsAttValueStyle, pbsAttWhiteSpaceStyle);
		
		LinacLegoPbsHtmlTextTree linacNode = new LinacLegoPbsHtmlTextTree(linacLego.getLinac(), pbsView);
		pbsView.add(linacNode);
		for (int isec = 0; isec < linacLego.getLinac().getSectionList().size(); ++isec)
		{
			Section section = linacLego.getLinac().getSectionList().get(isec);
			LinacLegoPbsHtmlTextTree sectionNode = new LinacLegoPbsHtmlTextTree(section, linacNode);
			linacNode.add(sectionNode);
			for (int icell = 0; icell < section.getCellList().size(); ++icell)
			{
				Cell cell = section.getCellList().get(icell);
				LinacLegoPbsHtmlTextTree cellNode = new LinacLegoPbsHtmlTextTree(cell, sectionNode);
				sectionNode.add(cellNode);
				for (int islot = 0; islot < cell.getSlotList().size(); ++islot)
				{
					Slot slot = cell.getSlotList().get(islot);
					LinacLegoPbsHtmlTextTree slotNode = new LinacLegoPbsHtmlTextTree(slot, cellNode);
					cellNode.add(slotNode);
					for (int ible = 0; ible < slot.getBeamLineElementList().size(); ++ible)
					{
						BeamLineElement ble = slot.getBeamLineElementList().get(ible);
						LinacLegoPbsHtmlTextTree bleNode = new LinacLegoPbsHtmlTextTree(ble, slotNode);
						slotNode.add(bleNode);
						for (int imon = 0; imon < ble.getLegoMonitorList().size(); ++imon)
						{
							LegoMonitor lmon = ble.getLegoMonitorList().get(imon);
							LinacLegoPbsHtmlTextTree lmonNode = new LinacLegoPbsHtmlTextTree(lmon, bleNode);
							bleNode.add(lmonNode);
						}
					}
				}
			}
		}
		return pbsView.getHtmlTextTree();
	}
	public static CsvFile readCsvFile(URL csvFileUrl) throws IOException, LinacLegoWebAppException  
	{
		CsvFile csvFile = new CsvFile();
        BufferedReader br;
        InputStreamReader inputStreamReader = new InputStreamReader(csvFileUrl.openStream());
        br = new BufferedReader(inputStreamReader);
        String line;
        while ((line = br.readLine()) != null) 
        {  
        	csvFile.addLine(line);
        }
        br.close();
        inputStreamReader.close();
        csvFile.close();
		return csvFile;
	}
	public static String[] getModelDrawingDirectories(String aigWeb, String linacLegoModelDrawingsRelLink, String drawingStructure) throws LinacLegoWebAppException
	{
		if (!drawingStructure.equals("section") && !drawingStructure.equals("cell") && !drawingStructure.equals("slot")) return null;
		String findString = linacLegoModelDrawingsRelLink + "/" + drawingStructure + "/";
		try
		{
			URL url = new URL(aigWeb +  findString);
		    HttpURLConnection huc =  (HttpURLConnection)  url.openConnection(); 
		    huc.setInstanceFollowRedirects(false);
		    huc.setRequestMethod("GET"); 
		    huc.connect(); 
			
	        InputStream inputStream = huc.getInputStream();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
	
	        String line = null;
	        ArrayList<String> modelList = new ArrayList<String>();
	        while ((line = reader.readLine()) != null) 
	        {
	        	int lastIndex = 0;
	
	        	while(lastIndex != -1)
	        	{
	        		lastIndex = line.indexOf(findString,lastIndex);
	
	       			if( lastIndex != -1)
	       			{
	       				String tempString = line.substring(lastIndex);
	       				int istart = tempString.indexOf(">") + 1;
	       				int istop = tempString.indexOf("</a>");
	       				tempString = tempString.substring(istart, istop);
	       				modelList.add(tempString);
	       				lastIndex+=findString.length();
	       			}
	        	}
	        }
	        inputStream.close();
	        String[] modelListArray = null;
	        if (modelList.size() > 0)
	        {
	        	modelListArray = new String[modelList.size()];
	        	for (int is = 0; is < modelList.size(); ++is) modelListArray[is]  = modelList.get(is);
	        }

	        return modelListArray;
		}
		catch (IOException e)
		{
			throw new LinacLegoWebAppException(e);
		}
	}
	public static void main(String[] args) throws LinacLegoWebAppException  
	{
		String aigWeb = "https://f6dea02762c7d72c87ecd77e9ac9c92f0cd2fa43.googledrive.com/host/0B_mauLIA30CDVzY4NWdXODBNU3c/";
		String linacLegoModelDrawingsLink = "/LinacLego/modelDrawings";
		String drawingStructure = "slot";
		String[] modelList = LinacLegoServiceImplStaticMethods.getModelDrawingDirectories(aigWeb, linacLegoModelDrawingsLink, drawingStructure);
        if (modelList != null)
        {
        	for (int is = 0; is < modelList.length; ++is) System.out.println(modelList[is]);
        }
	
	}	

}
