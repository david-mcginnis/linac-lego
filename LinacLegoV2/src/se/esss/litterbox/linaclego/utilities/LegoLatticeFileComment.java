package se.esss.litterbox.linaclego.utilities;

import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLegoException;

public class LegoLatticeFileComment 
{
	private String keyword = "";
	private ArrayList<String> attributeName = new ArrayList<String>();
	private ArrayList<String> attributeValue = new ArrayList<String>();
	
	public LegoLatticeFileComment(String inputLine) throws LinacLegoException
	{
		if (!isLegoLatticeFileComment(inputLine)) throw new LinacLegoException("Not a valid LegoLatticeFileComment");
		String line = inputLine.trim();
		int ib = line.indexOf("<");
		if (ib < 0) throw new LinacLegoException("No \"<\" termination in LegoLatticeFileComment");
		int ie = line.indexOf(">");
		if (ie < ib) throw new LinacLegoException("No \">\" termination in LegoLatticeFileComment");
		line = line.substring(ib + 1, ie).trim();
		char[] lineChar = line.toCharArray();
		int ic = 0;
		String attName = "";
		String attVal = "";
		boolean keywordFound = false;
		while (ic < lineChar.length)
		{
			while(!keywordFound)
			{
				if (ic == lineChar.length)
				{
					keywordFound = true;
				}
				else 
				{
					if ((lineChar[ic] == ' ') || (lineChar[ic] == '\t'))
					{
						keywordFound = true;
					}
					else 
					{
						keyword = keyword + lineChar[ic];
						ic = ic + 1;
					}
				}
			}
			if (ic < lineChar.length)
			{
				if ((lineChar[ic] == ' ') || (lineChar[ic] == '\t'))
				{
					ic = ic + 1;
				}
				else
				{
					while (lineChar[ic] != '=')
					{
						attName = attName + lineChar[ic];
						ic = ic + 1;
					}
					attributeName.add(new String(attName));
					attName = "";
					while (lineChar[ic] != '\"') ic = ic + 1;
					ic = ic + 1;
					while (lineChar[ic] != '\"') 
					{
						attVal = attVal + lineChar[ic];
						ic = ic + 1;
					}
					attributeValue.add(new String(attVal));
					attVal = "";
					ic = ic + 1;	
				}
			}
		}
		
	}
	public boolean keywordExists(String keywordCheck)
	{
		if (keyword.equals(keywordCheck)) return true;
		return false;
	}
	public boolean attributeExists(String attribute)
	{
		if (attributeName.size() > 0)
		{
			for (int ii = 0; ii < attributeName.size(); ++ii)
			{
				if (attributeName.get(ii).equals(attribute)) return true;
			}
		}
		return 	false;
	}
	public String getKeyword() {return keyword;}
	public String getAttribute(String attribute) 
	{
		if (attributeName.size() == 0) return  null;
		if (!attributeExists(attribute)) return  null;
		for (int ii = 0; ii < attributeName.size(); ++ii)
		{
			if (attributeName.get(ii).equals(attribute)) return attributeValue.get(ii);
		}
		return null;
	}
	public static boolean isLegoLatticeFileComment(String inputLine)
	{
		boolean isLegoLatticeFileComment = false;
		String line = inputLine.trim();
		if (line.indexOf(";lego") == 0) isLegoLatticeFileComment = true;
		return isLegoLatticeFileComment;
	}

	public static void main(String[] args) throws LinacLegoException 
	{
		LegoLatticeFileComment llfc = new LegoLatticeFileComment(";lego <section id=\"SPK\" rfHarmonic=\"1\" info=\"for the love of God\">");
		System.out.println(llfc.getKeyword());
		System.out.println(llfc.getAttribute("id"));
		System.out.println(llfc.getAttribute("rfHarmonic"));
		System.out.println(llfc.getAttribute("info"));
		llfc = new LegoLatticeFileComment(";lego < /section >");
		System.out.println(llfc.getKeyword());
	}

}
