package se.esss.litterbox.linaclego;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoXmlTreeNode extends DefaultMutableTreeNode
{
	private static final long serialVersionUID = 6263419567118694246L;
	Node xmlNode = null;
	public LegoXmlTreeNode(Lego lego)
	{
		super();
		add((buildTreeNode((Node) lego.getSimpleXmlDoc().getXmlDoc().getDocumentElement())));
	}
	public LegoXmlTreeNode(Node xmlNode)
	{
		super(xmlNode);
		this.xmlNode = xmlNode;
	}
	@Override
	public String toString()
	{
		return getTagLabel();
	}
	String getTagLabel()
	{
		SimpleXmlReader sxr = new SimpleXmlReader(xmlNode);
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
	public static LegoXmlTreeNode buildLegoXmlTreeNodes(Lego lego)
	{
		return LegoXmlTreeNode.buildTreeNode((Node) lego.getSimpleXmlDoc().getXmlDoc().getDocumentElement());
	}
    private static LegoXmlTreeNode buildTreeNode(Node root){
    	LegoXmlTreeNode dmtNode;

        dmtNode = new LegoXmlTreeNode(root);
        NodeList nodeList = root.getChildNodes();
        for (int count = 0; count < nodeList.getLength(); count++) 
        {
            Node tempNode = nodeList.item(count);
            // make sure it's element node.
            if (tempNode.getNodeType() == Node.ELEMENT_NODE) 
            {
                if (tempNode.hasChildNodes()) 
                {
                    // loop again if has child nodes
                    dmtNode.add(buildTreeNode(tempNode));
                }
                else
                {
                	dmtNode.add(new LegoXmlTreeNode(tempNode));
                }
            }
        }
        return dmtNode;
    }

}