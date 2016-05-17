package se.esss.litterbox.linaclego.app;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Node;

import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

@SuppressWarnings("serial")
public class LegoAppDefaultMutableTreeNodeWrapper extends DefaultMutableTreeNode
{
	SimpleXmlReader sxr;
	LegoAppDefaultMutableTreeNodeWrapper(Node xmlNode)
	{
		super(xmlNode);
		sxr = new SimpleXmlReader(xmlNode);
	}
	@Override
	public String toString()
	{
		return getTagLabel();
	}
	String getTagLabel()
	{
		String tagName = sxr.tagName();
		String id = "";
		try {id = sxr.attribute("id");} catch (SimpleXmlException e) {}
		String html = "<html>";
		html = html + "<font color=\"0000FF\">" + tagName + "</font>";
		if (id.length() > 0)
		{
			html =  html + "<font color=\"FF0000\"> id</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" + id + "\"</font>";
		}
		ArrayList<String[]> attributes = sxr.getAttributes();
		if (attributes != null)
		{
			for (int ii = 0; ii < attributes.size(); ++ii)
			{
				if (!attributes.get(ii)[0].equals("id"))
				{
					if (!attributes.get(ii)[0].equals("xmlns:xi"))
					{
						if (!attributes.get(ii)[0].equals("xml:base"))
						{
								html =  html + "<font color=\"FF0000\">" + " " + attributes.get(ii)[0] 
										+ "</font><font color=\"000000\">=</font><font color=\"9933FF\">\"" 
										+ attributes.get(ii)[1] + "\"</font>";
						}
					}
				}
			}
		}
		String cdata = sxr.getCharacterData();
		if (cdata != null)
		{
			html =  html + "<font color=\"000000\">" + " " + cdata + "</font>";
		}
		html = html + "</html>";
		return html;
	}
}