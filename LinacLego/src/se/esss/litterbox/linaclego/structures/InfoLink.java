package se.esss.litterbox.linaclego.structures;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class InfoLink 
{
	private SimpleXmlReader tag;
	private String data;
	private String id;
	private String type;
	public InfoLink(SimpleXmlReader tag) throws LinacLegoException
	{
		data = tag.getCharacterData();
		try {id = tag.attribute("id");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		try {type = tag.attribute("type");} catch (SimpleXmlException e) {throw new LinacLegoException(e);}
		this.tag = tag;
	}
	public SimpleXmlReader getTag() {return tag;}
	public String getData() {return data;}
	public String getId() {return id;}
	public String getType() {return type;}

}
