package se.esss.litterbox.linaclego.v2.webapp.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoCell;
import se.esss.litterbox.linaclego.v2.structures.LegoSection;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.structures.beam.LegoBeam;
import se.esss.litterbox.linaclego.v2.webapp.shared.CsvFile;
import se.esss.litterbox.linaclego.v2.webapp.shared.GskelException;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class EntryPointAppServiceImplStaticMethods 
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
	public static HtmlTextTree createXmlView(Lego linacLego)    
	{
		String xmlViewTagStyle = "xmlTagLabel";
		String xmlViewAttLabelStyle = "xmlAttLabel";
		String xmlViewAttValueStyle = "xmlAttValue";
		String xmlViewAttWhiteSpaceStyle = "xmlWhiteSpace";
		SimpleXmlDoc sxd = linacLego.getSimpleXmlDoc();
		return  EntryPointAppServiceImplStaticMethods.buildXmlTextTree((Node) sxd.getXmlDoc().getDocumentElement(), xmlViewTagStyle, xmlViewAttLabelStyle, xmlViewAttValueStyle, xmlViewAttWhiteSpaceStyle);
	}
	public static HtmlTextTree createPbsViewHtmlTextTree(Lego linacLego, String linacLegoDataLink) throws GskelException   
	{
		try
		{
			String pbsTagStyle = "pbsTagLabel";
			String pbsAttLabelStyle = "pbsAttLabel";
			String pbsAttValueStyle = "pbsAttValue";
			String pbsAttWhiteSpaceStyle = "pbsWhiteSpace";
			LegoPbsHtmlTextTree pbsView = new LegoPbsHtmlTextTree(linacLego, pbsTagStyle, pbsAttLabelStyle, pbsAttValueStyle, pbsAttWhiteSpaceStyle);
			
			LegoPbsHtmlTextTree linacNode = new LegoPbsHtmlTextTree(linacLego.getLegoLinac(), pbsView, linacLegoDataLink);
			pbsView.add(linacNode);
			for (int isec = 0; isec < linacLego.getLegoLinac().getLegoSectionList().size(); ++isec)
			{
				LegoSection section = linacLego.getLegoLinac().getLegoSectionList().get(isec);
				LegoPbsHtmlTextTree sectionNode = new LegoPbsHtmlTextTree(section, linacNode, linacLegoDataLink);
				linacNode.add(sectionNode);
				for (int icell = 0; icell < section.getLegoCellList().size(); ++icell)
				{
					LegoCell cell = section.getLegoCellList().get(icell);
					LegoPbsHtmlTextTree cellNode = new LegoPbsHtmlTextTree(cell, sectionNode, linacLegoDataLink);
					sectionNode.add(cellNode);
					for (int islot = 0; islot < cell.getLegoSlotList().size(); ++islot)
					{
						LegoSlot slot = cell.getLegoSlotList().get(islot);
						LegoPbsHtmlTextTree slotNode = new LegoPbsHtmlTextTree(slot, cellNode, linacLegoDataLink);
						cellNode.add(slotNode);
						for (int ible = 0; ible < slot.getLegoBeamList().size(); ++ible)
						{
							LegoBeam ble = slot.getLegoBeamList().get(ible);
							LegoPbsHtmlTextTree bleNode = new LegoPbsHtmlTextTree(ble, slotNode, linacLegoDataLink);
							slotNode.add(bleNode);
						}
					}
				}
			}
			return pbsView.getHtmlTextTree();
		}
		catch (LinacLegoException e) {throw new GskelException(e);}
	}
	public static CsvFile readCsvFile(URL csvFileUrl) throws GskelException   
	{
		try
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
		catch (IOException e) {throw new GskelException(e);}
	}
	public static String[] getModelDrawingDirectories(String aigWeb, String linacLegoModelDrawingsRelLink, String drawingStructure) throws GskelException
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
			throw new GskelException(e);
		}
	}
	public static void main(String[] args) throws LinacLegoException 
	{
	
		Lego linacLego = Lego.readSerializedLegoFromWeb("https://aig.esss.lu.se:8443/LinacLegoV2DataWeb/data/test/linacLego.bin");
		linacLego.getLegoLinac().triggerUpdate();
	}	

}
