/*
Copyright (c) 2014 European Spallation Source

This file is part of SimpleXml.
SimpleXml is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.simplexml;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;

public class SimpleXmlWriter 
{
	private final int MAXELEMENTS = 200;
	private SimpleXmlDoc simpleXmlDoc;
	//	private Document xmlDoc = null;
	Element masterElement = null;
	private Element[] xmlTagElement = new Element[MAXELEMENTS];
	private int numTagsOpen = 0;
	private String startTag = "";
	private boolean documentOpen = false;

	public SimpleXmlWriter(String startTag, String dtdName) throws SimpleXmlException
	{
		this.startTag = startTag;
		clearXmlElementArray(0);
		simpleXmlDoc = new SimpleXmlDoc();
		DOMImplementation domImpl = simpleXmlDoc.getXmlDoc().getImplementation();
		DocumentType doctype = domImpl.createDocumentType(startTag, null, dtdName);
		simpleXmlDoc.getXmlDoc().appendChild(doctype);
		numTagsOpen = 0;
		documentOpen = true;
		openXmlTag(startTag);
	}
	private void clearXmlElementArray(int istart)
	{
		if (istart >= MAXELEMENTS) return;
		for (int ii = istart; ii < MAXELEMENTS; ++ii) xmlTagElement[ii] = null;
	}
	public void openXmlTag(String tagName) throws SimpleXmlException
	{
		if (!documentOpen) throw new SimpleXmlException(" XML Document closed already.");
		if (numTagsOpen > 0)
		{
			xmlTagElement[numTagsOpen - 1].appendChild(simpleXmlDoc.getXmlDoc().createTextNode("\n"));
			for (int ii = 0; ii < numTagsOpen; ++ii)
			{
				xmlTagElement[numTagsOpen - 1].appendChild(simpleXmlDoc.getXmlDoc().createTextNode("\t"));
			}
		}
		xmlTagElement[numTagsOpen] = simpleXmlDoc.getXmlDoc().createElement(tagName);
		numTagsOpen = numTagsOpen + 1;
	}
	public void writeCharacterData(String info) throws SimpleXmlException
	{
		if (!documentOpen) throw new SimpleXmlException(" XML Document closed already.");
		xmlTagElement[numTagsOpen - 1].setTextContent(info);
	}
	public void closeXmlTag(String tagName) throws SimpleXmlException
	{
		if (!documentOpen) throw new SimpleXmlException(" XML Document closed already.");
		if (numTagsOpen == 0) return;
		if (numTagsOpen > 1)
		{
			if (xmlTagElement[numTagsOpen] != null)
			{
				xmlTagElement[numTagsOpen - 1].appendChild(simpleXmlDoc.getXmlDoc().createTextNode("\n"));
				for (int ii = 0; ii < numTagsOpen - 1; ++ii)
				{
					xmlTagElement[numTagsOpen - 1].appendChild(simpleXmlDoc.getXmlDoc().createTextNode("\t"));
				}
			}
			xmlTagElement[numTagsOpen - 2].appendChild(xmlTagElement[numTagsOpen - 1]);
			xmlTagElement[numTagsOpen] = null;
			numTagsOpen = numTagsOpen - 1;
		}
	}
	public void setAttribute(String attributeName, String attributeValue) throws SimpleXmlException
	{
		if (!documentOpen) throw new SimpleXmlException(" XML Document closed already.");
		xmlTagElement[numTagsOpen - 1].setAttribute(attributeName, attributeValue);
	}
	public void closeDocument() throws SimpleXmlException
	{
		if (!documentOpen) throw new SimpleXmlException(" XML Document closed already.");
		closeXmlTag(startTag);
		xmlTagElement[0].appendChild(simpleXmlDoc.getXmlDoc().createTextNode("\n"));
		simpleXmlDoc.getXmlDoc().appendChild(xmlTagElement[0]);
		documentOpen = false;
		
	}
	public void saveXmlDocument(String fileName) throws SimpleXmlException
	{
		simpleXmlDoc.saveXmlDocument(fileName);
	}
	public void saveXmlDocumentOld(String fileName) throws SimpleXmlException
	{
		if (documentOpen) closeDocument();
		
		DOMImplementation impl = simpleXmlDoc.getXmlDoc().getImplementation();
		DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
		LSSerializer lsSerializer = implLS.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("format-pretty-print", true);
		 
		LSOutput lsOutput = implLS.createLSOutput();
		lsOutput.setEncoding("UTF-8");
		Writer stringWriter = new StringWriter();
		lsOutput.setCharacterStream(stringWriter);
		lsSerializer.write(simpleXmlDoc.getXmlDoc(), lsOutput);
		 
		String docString = stringWriter.toString();		
		
		try 
		{
			PrintWriter pw = new PrintWriter(fileName);
			pw.println(docString);
			pw.close();
		} 
		catch (FileNotFoundException e1) 
		{
			throw new SimpleXmlException("FileNotFoundException", e1);
		}
	}
	public SimpleXmlDoc getSimpleXmlDoc() {return simpleXmlDoc;}
	public static void main(String[] args) 
	{
		SimpleXmlWriter xw;
		try 
		{
			xw = new SimpleXmlWriter("BigTag", "park.dtd");
			xw.setAttribute("att1", "10.0");
			xw.openXmlTag("SmallTagX");
				xw.openXmlTag("TinyTag");
					xw.setAttribute("ctt1", "1.0");
						xw.openXmlTag("TinyTinyTag");
							xw.setAttribute("dtt1", "2.0");
						xw.closeXmlTag("TinyTinyTag");
				xw.closeXmlTag("TinyTag");
				xw.setAttribute("att1", "3.0");
				xw.setAttribute("att2", "4.0");
				xw.setAttribute("att3", "5.0");
			xw.closeXmlTag("SmallTagX");
			xw.openXmlTag("SmallTag");
				xw.setAttribute("att1", "6.0");
				xw.setAttribute("att2", "7.0");
				xw.setAttribute("att3", "8.0");
				xw.setAttribute("att4", "9.0");
			xw.closeXmlTag("SmallTag");
			xw.openXmlTag("SmallTag");
				xw.setAttribute("att1", "10.0");
				xw.setAttribute("att2", "11.0");
			xw.closeXmlTag("SmallTag");
			xw.closeDocument();
			xw.saveXmlDocument("test.xml");
		} 
		catch (SimpleXmlException e) 
		{
			e.printErrorMessage();
		}
	}

}
