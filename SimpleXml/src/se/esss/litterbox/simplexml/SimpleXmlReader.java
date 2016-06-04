package se.esss.litterbox.simplexml;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Vector;







import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.CharacterData;

/**
 * @author mcginnis
 *
 */
public class SimpleXmlReader 
{
	protected Node xmlNode;
	protected Vector<Node> xmlNodeList = new Vector<Node>();
	protected int length = 0;
	public SimpleXmlReader()
	{

	}
	public SimpleXmlReader(Node xmlNode)
	{
		this.xmlNode = xmlNode;
		NodeList nodeList =  xmlNode.getChildNodes();
		int nodeListLength = nodeList.getLength();
		if (nodeListLength > 0)
		{
			for (int ii = 0; ii < nodeList.getLength(); ++ii)
			{
				if (nodeList.item(ii).getNodeType() == Node.ELEMENT_NODE)
				{
					xmlNodeList.add(nodeList.item(ii));
				}
			}
		}
		length = xmlNodeList.size();
	}
	private SimpleXmlReader(Node xmlNode, String tagNameFilter)
	{
		this.xmlNode = xmlNode;
		NodeList nodeList =  xmlNode.getChildNodes();
		int nodeListLength = nodeList.getLength();
		if (nodeListLength > 0)
		{
			for (int ii = 0; ii < nodeList.getLength(); ++ii)
			{
				if (nodeList.item(ii).getNodeType() == Node.ELEMENT_NODE)
				{
					if (nodeList.item(ii).getNodeName().equals(tagNameFilter))
						xmlNodeList.add(nodeList.item(ii));
				}
			}
		}
		length = xmlNodeList.size();
	}
	public  SimpleXmlReader getTagMatchingAttribute(String attributeName, String attributeValue)
	{
		NodeList nodeList =  xmlNode.getChildNodes();
		int nodeListLength = nodeList.getLength();
		if (nodeListLength > 0)
		{
			for (int ii = 0; ii < nodeList.getLength(); ++ii)
			{
				Node testNode = nodeList.item(ii);
				if (testNode.getNodeType() == Node.ELEMENT_NODE)
				{
					if (testNode.hasAttributes())
					{
						if (testNode.getAttributes().getNamedItem(attributeName).getNodeValue().equals(attributeValue))
						{
							return new SimpleXmlReader(nodeList.item(ii));
						}
					}
				}
			}
		}
		return null;
	}
	public  SimpleXmlReader getTagMatchingAttribute(String tagName, String attributeName, String attributeValue)
	{
		NodeList nodeList =  xmlNode.getChildNodes();
		int nodeListLength = nodeList.getLength();
		if (nodeListLength > 0)
		{
			for (int ii = 0; ii < nodeList.getLength(); ++ii)
			{
				Node testNode = nodeList.item(ii);
				if (testNode.getNodeType() == Node.ELEMENT_NODE)
				{
					if (testNode.getNodeName().equals(tagName))
					{
						if (testNode.hasAttributes())
						{
							if (testNode.getAttributes().getNamedItem(attributeName).getNodeValue().equals(attributeValue))
							{
									return new SimpleXmlReader(nodeList.item(ii));
							}
						}
					}
				}
			}
		}
		return null;
	}
	public SimpleXmlReader(SimpleXmlDoc simpleXmlDoc) throws SimpleXmlException
	{
		Document xmlDoc = simpleXmlDoc.getXmlDoc();
        String bodyTagName = "";
		NodeList listOfTags = xmlDoc.getChildNodes();
		int noOfTags = listOfTags.getLength();
		for (int ii =0; ii < noOfTags; ++ii)
		{
			if (listOfTags.item(ii).getNodeType() == Node.ELEMENT_NODE)
			{
				bodyTagName = listOfTags.item(ii).getNodeName();
			}
		}
		this.xmlNode = xmlDoc.getElementsByTagName(bodyTagName).item(0);
		xmlNodeList.add(this.xmlNode);
		length = 1;
	}
	public int numChildTags()
	{
		return length;
	}
	public SimpleXmlReader tag(int itag) throws SimpleXmlException
	{
		if (length == 0) 
		{
			throw new SimpleXmlException("Tag does not exist");
		}
		return new SimpleXmlReader(xmlNodeList.get(itag));
	}
	public String tagName()
	{
		return xmlNode.getNodeName();
	}
	public SimpleXmlReader tagsByName(String tagName)
	{
		return new SimpleXmlReader(xmlNode, tagName);
	}
	public String attribute(String attributeName) throws SimpleXmlException
	{
		Node subnode = xmlNode.getAttributes().getNamedItem(attributeName);
		if (subnode == null) throw new SimpleXmlException("Attribute \"" + attributeName + "\" does not exist");
		return subnode.getNodeValue();
	}
	public ArrayList<String[]> getAttributes()
	{
		NamedNodeMap map =  xmlNode.getAttributes();
		int numAtts = map.getLength();
		if (numAtts < 1) return null;
		ArrayList<String[]> list = new  ArrayList<String[]>();
		for (int ii = 0; ii < numAtts; ++ii)
		{
			String[] info = new String[2];
			info[0] = map.item(ii).getNodeName();
			info[1] = map.item(ii).getNodeValue();
			list.add(info);
		}
		return list;
	}
	public void setAttribute(String attributeName, String attributeValue) throws SimpleXmlException
	{
		Node subnode = xmlNode.getAttributes().getNamedItem(attributeName);
		if (subnode == null) throw new SimpleXmlException("Attribute does not exist");
		subnode.setNodeValue(attributeValue);
	}
	public String getCharacterData()
	{
		Node child =  xmlNode.getChildNodes().item(0);
		if (child instanceof CharacterData) 
		{
		    CharacterData cd = (CharacterData) child;
		    return cd.getData();
		}
		return null;
	}
	public void setCharacterData(String cdata) throws SimpleXmlException
	{
		Node child =  xmlNode.getChildNodes().item(0);
		if (child instanceof CharacterData) 
		{
		    CharacterData cd = (CharacterData) child;
		    cd.setData(cdata);
		}
		else
		{
			throw new SimpleXmlException("No Character data to change");
		}
	}
	public Node getXmlNode() {return xmlNode;}
	public Vector<Node> getXmlNodeList() {return xmlNodeList;}

	public static void main(String[] args) throws MalformedURLException 
	{

		try 
		{
			SimpleXmlDoc xdoc = new SimpleXmlDoc(new File("exampleFiles/test.xml").toURI().toURL());
			SimpleXmlReader xr = new SimpleXmlReader(xdoc);
			System.out.println(xr.tag(0).tagName());

			SimpleXmlReader x1 = xr.tag(0);
//			SimpleXmlReader x2 = x1.getTagMatchingAttribute("name", "smalltag02");
//			System.out.println(x2.tagName() + " " + "att1" + " = " + x2.attribute("att1"));
//			x2.setAttribute("att1", "booger");
//			System.out.println(x2.tagName() + " " + "att1" + " = " + x2.attribute("att1"));
			
			x1 = xr.tagsByName("SmallTagX");
			String sattName = "att3";
			String sval = x1.tag(0).attribute(sattName);
			System.out.println(x1.tag(0).tagName() + " " + sattName + " = " + sval);
			
			x1 = x1.tag(0).tagsByName("TinyTag");
			sattName = "ctt1";
			sval = x1.tag(0).attribute(sattName);
			System.out.println(x1.tag(0).tagName() + " " + sattName + " = " + sval);
			
			x1 = x1.tag(0).tagsByName("TinyTinyTag");
			sattName = "dtt1";
			sval = x1.tag(0).attribute(sattName);
			System.out.println(x1.tag(0).tagName() + " " + sattName + " = " + sval);
			
			x1 = xr.tagsByName("SmallTag").tag(0);
			System.out.println(x1.tagName());

			x1 = xr.tagsByName("SmallTagX").tag(0);
			System.out.println(x1.tagName());
			x1 = xr.tagsByName("SmallTagX").tag(0).tagsByName("TinyTag").tag(0).tagsByName("TinyTinyTag").tag(0);
			System.out.println(x1.tagName());
			
			x1 = xr.tagsByName("myTag").tag(0);
			System.out.println(x1.getCharacterData());
			SimpleXmlReader x2 = x1.tagsByName("TinyTinyTag").tag(0);
			System.out.println(x2.attribute("dtt1"));
			System.out.println(x2.getCharacterData());
			xdoc.saveXmlDocument("exampleFiles\\test2.xml");
			
		} 
		catch (SimpleXmlException e) 
		{
			e.printErrorMessage();
		} 
		
	}
}
