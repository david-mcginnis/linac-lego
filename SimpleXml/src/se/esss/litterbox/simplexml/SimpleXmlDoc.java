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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSOutput;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class SimpleXmlDoc implements Serializable
{
	private static final long serialVersionUID = -8792621009854746872L;
	private Document xmlDoc;
	private  DocumentBuilderFactory docFactory;
    private DocumentBuilder documentBuilder;
    private URL xmlSourceUrl;
    private boolean documentValidated = false;
    
	public SimpleXmlDoc(URL url) throws SimpleXmlException 
	{
		xmlSourceUrl = url;
		xmlDoc = getXmlDocFromUrl();
	}
	public SimpleXmlDoc() throws SimpleXmlException
	{
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = null;
		try 
		{
			documentBuilder = docFactory.newDocumentBuilder();
		} 
		catch (ParserConfigurationException e) 
		{
			throw new SimpleXmlException(e);
		}
		xmlDoc = documentBuilder.newDocument();

	}
    private void initializeDocFactory(boolean validate) throws SimpleXmlException
    {
    	docFactory = DocumentBuilderFactory.newInstance();
        docFactory.setXIncludeAware(true);
        docFactory.setNamespaceAware(true);
        docFactory.setValidating(validate);
		try {documentBuilder = docFactory.newDocumentBuilder();} 
		catch (ParserConfigurationException e) {throw new SimpleXmlException(e);}
		documentBuilder.setErrorHandler(new SimpleXmlErrorHandler());
	   	if (!documentBuilder.isXIncludeAware()) 
	   	{
    		throw new IllegalStateException();
    	}
//Does not look for a .dtd document if it cannot find the one specified
	   	if (!validate) documentBuilder.setEntityResolver( new BlankingResolver() );
    }
    private InputSource getInputSource() throws SimpleXmlException
    {
		try 
		{
// did this to handle relative refererences in Xinclude
			InputStream is = xmlSourceUrl.openStream();
//			FileInputStream fis = new FileInputStream(xmlSourceFile);
			InputSource inputSource = new InputSource(is);
//			inputSource.setSystemId(xmlSourceFile.toURI().toString());
			inputSource.setSystemId(xmlSourceUrl.toURI().toString());
			return inputSource;
		} 
		catch (FileNotFoundException e) {throw new SimpleXmlException(e);} 
		catch (IOException e) {throw new SimpleXmlException(e);} 
		catch (URISyntaxException e) {throw new SimpleXmlException(e);} 
    }
	private Document getXmlDocFromUrl() throws SimpleXmlException
	{
		try 
		{
			documentValidated = false;
			initializeDocFactory(true);
			Document docy = documentBuilder.parse(getInputSource());
			documentValidated = true;
			return docy;
		}
		catch (SAXException e) 
		{
			if (e.getCause().getMessage().indexOf("no grammar found") >= 0)
			{
				return documentWithoutDtdValidation();
			}
			else
			{throw new SimpleXmlException(e);}
		} 
		catch (FileNotFoundException e) 
		{
			if (e.getMessage().indexOf(".dtd") >= 0)
			{
				return documentWithoutDtdValidation();
			}
			else
			{throw new SimpleXmlException(e);}
		} catch (IOException e) {throw new SimpleXmlException(e);}
	}
	private Document documentWithoutDtdValidation() throws SimpleXmlException
	{
		try 
		{
			documentValidated = false;
			initializeDocFactory(false);
			return documentBuilder.parse(getInputSource());
		}
		catch (SAXException e1) {throw new SimpleXmlException(e1);} 
		catch (IOException e1) {throw new SimpleXmlException(e1);}
	}
	public void saveXmlDocument(File xmlSourceFile) throws SimpleXmlException
	{
		if (xmlSourceFile == null) throw new SimpleXmlException("XML source file is null.");
		saveXmlDocument(xmlSourceFile.getPath());
	}
	public void saveXmlDocument(String filePath) throws SimpleXmlException
	{
		DOMImplementation impl = xmlDoc.getImplementation();
		DOMImplementationLS implLS = (DOMImplementationLS) impl.getFeature("LS", "3.0");
		LSSerializer lsSerializer = implLS.createLSSerializer();
		lsSerializer.getDomConfig().setParameter("format-pretty-print", true);
		 
		LSOutput lsOutput = implLS.createLSOutput();
		lsOutput.setEncoding("UTF-8");
		Writer stringWriter = new StringWriter();
		lsOutput.setCharacterStream(stringWriter);
		lsSerializer.write(xmlDoc, lsOutput);
		 
		String docString = stringWriter.toString();		
		
		try 
		{
			PrintWriter pw = new PrintWriter(filePath);
			pw.println(docString);
			pw.close();
		} 
		catch (FileNotFoundException e1) 
		{
			throw new SimpleXmlException(e1);
		}

	}
	public Document getXmlDoc() {return xmlDoc;}
	public URL getXmlSourceUrl() {return xmlSourceUrl;}
	public URL getXmlSourceParentUrl() throws SimpleXmlException 
	{
		try {return new URL(xmlSourceUrl.toString().substring(0, xmlSourceUrl.toString().lastIndexOf("/")));} 
		catch (MalformedURLException e) {throw new SimpleXmlException(e);}
	}
	public String getXmlDocName()
	{
		return xmlSourceUrl.toString().substring(xmlSourceUrl.toString().lastIndexOf("/") + 1);
	}
	public void setXmlSourceUrl(URL xmlSourceUrl) {this.xmlSourceUrl = xmlSourceUrl;}
	public boolean isDocumentValidated() {return documentValidated;}
	public class SimpleXmlErrorHandler implements ErrorHandler
	{
		@Override
		public void error(SAXParseException arg0) throws SAXException {
			throw new SAXException("SAXParseException Error", arg0);
		}
		@Override
		public void fatalError(SAXParseException arg0) throws SAXException {
			throw new SAXException("SAXParseException Fatal Error", arg0);
		}
		@Override
		public void warning(SAXParseException arg0) throws SAXException {
//			throw new SAXException("SAXParseException Warning", arg0);
		}
		
	}
	public static class BlankingResolver implements EntityResolver
	{
	    public InputSource resolveEntity( String arg0, String arg1 ) throws SAXException,
	            IOException
	    {
	        return new InputSource( new ByteArrayInputStream( "".getBytes() ) );
	    }
	}
	public static void main(String[] args) throws SimpleXmlException, MalformedURLException, URISyntaxException 
	{
		String linacLegoWebSite = "https://1dd61ea372616aae15dcd04cd29d320453f0cb60.googledrive.com/host/0B3Hieedgs_7FNXg3OEJIREFuUUE";
		URL inputFileUrl = new URL(linacLegoWebSite + "/public/linacLego.xml");
		System.out.println(inputFileUrl.toString());
		
		SimpleXmlDoc sxd = new SimpleXmlDoc(inputFileUrl);
		System.out.println(sxd.isDocumentValidated());
		System.out.println(sxd.getXmlDocName());

	}


}
