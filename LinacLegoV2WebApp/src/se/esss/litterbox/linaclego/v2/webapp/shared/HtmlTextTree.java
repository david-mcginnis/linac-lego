package se.esss.litterbox.linaclego.v2.webapp.shared;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class HtmlTextTree implements Serializable
{
	private String tag;
	private ArrayList<Attribute> attributeList;
	HtmlTextTree dataFolder  = null;
	private String tagStyle;
	private String iconImageLocation;
	private Attribute attributeStyle  = new Attribute();
	private String infoLink = null;
	private String infoLinkTitle = null;
	
	private ArrayList<HtmlTextTree> htmlTextTreeArrayList = null;
	
	public String getTagStyle() {return tagStyle;}
	public ArrayList<HtmlTextTree> getTextTreeArrayList() {return htmlTextTreeArrayList;}
	public String getTag() {return tag;}
	public HtmlTextTree getDataFolder() {return dataFolder;}
	public String getIconImageLocation() {return iconImageLocation;}
	public String getInfoLink() {return infoLink;}
	public String getInfoLinkTitle() {return infoLinkTitle;}
	

	public void setTagStyle(String tagStyle) {this.tagStyle = tagStyle;}
	public void setAttLabelStyle(String attLabelStyle) {attributeStyle.setAttLabelStyle(attLabelStyle);}
	public void setAttValueStyle(String attValueStyle) {attributeStyle.setAttValueStyle(attValueStyle);}
	public void setAttWhiteSpaceStyle(String attWhiteSpaceStyle) {attributeStyle.setAttWhiteSpaceStyle(attWhiteSpaceStyle);}
	public void setDataFolder(HtmlTextTree dataFolder) {this.dataFolder = dataFolder;}
	public void setIconImageLocation(String iconImageLocation) {this.iconImageLocation = iconImageLocation;}
	public void setInfoLink(String infoLink) {this.infoLink = infoLink;}
	public void setInfoLinkTitle(String infoLinkTitle) {this.infoLinkTitle = infoLinkTitle;}

	public HtmlTextTree()
	{
		htmlTextTreeArrayList = new ArrayList<HtmlTextTree>();
		attributeList = new ArrayList<Attribute>();
		iconImageLocation = null;
	}
	public void inheritStyles(HtmlTextTree parentHtmlTextTree)
	{
		setTagStyle(parentHtmlTextTree.getTagStyle());
		attributeStyle.setAttLabelStyle(parentHtmlTextTree.attributeStyle.getAttLabelStyle());
		attributeStyle.setAttValueStyle(parentHtmlTextTree.attributeStyle.getAttValueStyle());
		attributeStyle.setAttWhiteSpaceStyle(parentHtmlTextTree.attributeStyle.getAttWhiteSpaceStyle());
	}
	public void add(HtmlTextTree textTree)
	{
		htmlTextTreeArrayList.add(textTree);
	}
	public boolean hasChildren()
	{
		if (htmlTextTreeArrayList.size() > 0) return true;
		return false;
	}
	public boolean hasDataFolder()
	{
		if (dataFolder != null) return true;
		return false;
	}
	public void setTag(String tagName) 
	{
		tag = textSpan(tagName, tagStyle);
	}
	public int numChildren() {return htmlTextTreeArrayList.size();}
	public void addAttribute(Attribute attribute)
	{
		if (attribute.getAttributeName().equals("xmlns:xi")) return;
		if (attribute.getAttributeName().equals("xml:base")) return;
		attributeList.add(attribute);
	}
	public void addAttribute(String attributeName, String attributeValue, int valueWidth)
	{
		if (attributeName.equals("xmlns:xi")) return;
		if (attributeName.equals("xml:base")) return;
		attributeList.add(new Attribute(attributeName, attributeValue, valueWidth, attributeStyle));
	}
	public boolean hasAttributes()
	{
		if (attributeList.size() > 0) return true;
		return false;
	}
	public int numAttributes() {return attributeList.size();}
	public Attribute getAttribute(int index) {return attributeList.get(index);}
	private String textSpan(String text, String style)
	{
		return "<span class=\"" + style + "\">" + text + "</span>";
	}
	public String getIconImageHtml(int widthPx, int heightPx)
	{
		String html = "<img src=\"" + iconImageLocation + "\" width=\"" + Integer.toString(widthPx) + "\" height=\"" + Integer.toString(heightPx) + "\">";
		return html;
		
	}
	public String getInlineHtmlString(int iconWidthPx, int iconHeightPx, boolean addPadding, boolean addParagraph)
	{
		String html = "<html>";
		if (iconImageLocation != null) html = html + getIconImageHtml(iconWidthPx, iconWidthPx);
		html = html + getTag();
		if (hasAttributes())
		{
			for (int ic = 0; ic < numAttributes(); ++ic) html = html + attributeList.get(ic).getHtml(addPadding, addParagraph);
		}
		html = html + "</html>";
		return html;
	}
	public String getInlineHtmlString(boolean addPadding, boolean addParagraph)
	{
		String html = "<html>";
		html = html + getTag();
		if (hasAttributes())
		{
			for (int ic = 0; ic < numAttributes(); ++ic) html = html + attributeList.get(ic).getHtml(addPadding, addParagraph);
		}
		html = html + "</html>";
		return html;
	}


}
