package se.esss.litterbox.linaclego.v2.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class MagCylFieldProfileBuilder  implements Serializable
{
	private static final long serialVersionUID = -4621367822785570283L;
	public static final String newline = System.getProperty("line.separator");
	public static final String space = "\t";
	public static final DecimalFormat onePlaces = new DecimalFormat("###.#");
	public static final DecimalFormat twoPlaces = new DecimalFormat("###.##");
	public static final DecimalFormat fourPlaces = new DecimalFormat("###.####");
	public static final DecimalFormat sixPlaces = new DecimalFormat("###.######");
	public static final DecimalFormat eightPlaces = new DecimalFormat("###.########");
	public static final DecimalFormat zeroPlaces = new DecimalFormat("###");

	private int nZpts;
	private double zmax;
	private int nRpts;
	private double rmax;
	private BCylindrical[][] fieldProfile;
	private String fieldUnit = null;
	private String title = "";
	private String lengthUnit = "mm";
	private double scaleFactor = 1.0;
	public MagCylFieldProfileBuilder() 
	{
	}
	public MagCylFieldProfileBuilder(URL xmlFileUrl) throws LinacLegoException 
	{
		readXmlFile(xmlFileUrl);
	}
	public MagCylFieldProfileBuilder(String xmlFilePath) throws LinacLegoException 
	{
		readXmlFile(xmlFilePath);
	}
	public void writeTraceWinFile(String traceWinFilePath) throws LinacLegoException
	{
		String traceWinFileName = traceWinFilePath.substring(0,  traceWinFilePath.lastIndexOf("."));
		try 
		{
			PrintWriter pw = new PrintWriter(traceWinFileName + ".bsr");
			pw.println(Integer.toString(nZpts) + " " + Double.toString(zmax * 0.001));
			pw.println(Integer.toString(nRpts) + " " + Double.toString(rmax * 0.001));
			pw.println(Double.toString(scaleFactor));
			for (int ii = 0; ii <= nZpts; ++ii)
			{
				for (int ij = 0; ij <= nRpts; ++ij)
				{
					pw.println(Double.toString(fieldProfile[ii][ij].getBr()));
				}
			}
			pw.close();
			
			pw = new PrintWriter(traceWinFileName + ".bsz");
			pw.println(Integer.toString(nZpts) + " " + Double.toString(zmax * 0.001));
			pw.println(Integer.toString(nRpts) + " " + Double.toString(rmax * 0.001));
			pw.println(Double.toString(scaleFactor));
			for (int ii = 0; ii <= nZpts; ++ii)
			{
				for (int ij = 0; ij <= nRpts; ++ij)
				{
					pw.println(Double.toString(fieldProfile[ii][ij].getBz()));
				}
			}
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			throw new LinacLegoException(e);
		}
	}	
	public  void readTraceWinFieldProfile(String traceWinFilePath) throws LinacLegoException
	{
		String traceWinFileName = traceWinFilePath.substring(0,  traceWinFilePath.lastIndexOf("."));
		BufferedReader br;
		ArrayList<String> bsr = new ArrayList<String>();
		try 
		{
			br = new BufferedReader(new FileReader(traceWinFileName + ".bsr"));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				bsr.add(line);
			}
			br.close();
		} 
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);}
		ArrayList<String> bsz = new ArrayList<String>();
		try 
		{
			br = new BufferedReader(new FileReader(traceWinFileName + ".bsz"));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				bsz.add(line);
			}
			br.close();
		} 
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);}
		title = new File(traceWinFileName).getName();
		String delims = "[ ,\t]+";
		if (!bsr.get(0).split(delims)[0].equals(bsz.get(0).split(delims)[0])) throw new LinacLegoException("Number of Z points do not match");
		if (!bsr.get(0).split(delims)[1].equals(bsz.get(0).split(delims)[1])) throw new LinacLegoException("Z length does not match");
		if (!bsr.get(1).split(delims)[0].equals(bsz.get(1).split(delims)[0])) throw new LinacLegoException("Number of R points do not match");
		if (!bsr.get(1).split(delims)[1].equals(bsz.get(1).split(delims)[1])) throw new LinacLegoException("R length does not match");
		if (!bsr.get(2).split(delims)[0].equals(bsz.get(2).split(delims)[0])) throw new LinacLegoException("Scale factor does not match");
		nZpts = Integer.parseInt(bsr.get(0).split(delims)[0]);
		zmax = Double.parseDouble(bsr.get(0).split(delims)[1]);
		nRpts = Integer.parseInt(bsr.get(1).split(delims)[0]);
		rmax = Double.parseDouble(bsr.get(1).split(delims)[1]);
		scaleFactor = Double.parseDouble(bsr.get(2).split(delims)[0]);
// Convert zmax from meters to mm
		zmax  = zmax * 1000;
		rmax  = rmax * 1000;
		fieldProfile = new BCylindrical[nZpts + 1][nRpts + 1];
		int icount = 3;
		for (int ii = 0; ii <= nZpts; ++ii)
		{
			for (int ij = 0; ij <= nRpts; ++ij)
			{
				fieldProfile[ii][ij] = new BCylindrical();
				fieldProfile[ii][ij].setBr(bsr.get(icount));
				fieldProfile[ii][ij].setBz(bsz.get(icount));
				icount = icount + 1;
			}
		}
	}
	public void writeXmlFile(String xmlFilePath) throws LinacLegoException
	{
		try 
		{
			SimpleXmlWriter xw = new SimpleXmlWriter("TwoDfieldProfile","dtdFiles/TwoDfieldProfile.dtd");
			xw.setAttribute("title", title);
			xw.setAttribute("nZpts", Integer.toString(nZpts));
			xw.setAttribute("nRpts", Integer.toString(nRpts));
			xw.setAttribute("zmax", Double.toString(zmax));
			xw.setAttribute("rmax", Double.toString(rmax));
			xw.setAttribute("scaleFactor", Double.toString(scaleFactor));
			xw.setAttribute("order", "Br_Bz");
			for (int ii = 0; ii <= nZpts; ++ii)
			{
				for (int ij = 0; ij <= nRpts; ++ij)
				{
					xw.openXmlTag("d");
					xw.setAttribute("id", Integer.toString(ii) + "_" + Integer.toString(ij));
					xw.writeCharacterData(fieldProfile[ii][ij].getB());
					xw.closeXmlTag("d");
				}
			}
			xw.closeDocument();
			xw.saveXmlDocument(xmlFilePath);
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
	}
	public  void readXmlFile(String xmlFilePath) throws LinacLegoException
	{
		File xmlFile = new File(xmlFilePath);
		try {readXmlFile(xmlFile.toURI().toURL());} catch (MalformedURLException e) {throw new LinacLegoException(e);}
	}
	public  void readXmlFile(URL xmlFileUrl) throws LinacLegoException
	{
		try 
		{
			SimpleXmlDoc sxd = new SimpleXmlDoc(xmlFileUrl);
			SimpleXmlReader twoDfieldProfileTag = new SimpleXmlReader(sxd);
			title = twoDfieldProfileTag.attribute("title");
			nZpts = Integer.parseInt(twoDfieldProfileTag.attribute("nZpts"));
			nRpts = Integer.parseInt(twoDfieldProfileTag.attribute("nRpts"));
			zmax = Double.parseDouble(twoDfieldProfileTag.attribute("zmax"));
			rmax =  Double.parseDouble(twoDfieldProfileTag.attribute("rmax"));
			scaleFactor =  Double.parseDouble(twoDfieldProfileTag.attribute("scaleFactor"));
			SimpleXmlReader dataTags = twoDfieldProfileTag.tagsByName("d");
			int icount = 0;
			fieldProfile = new BCylindrical[nZpts + 1][nRpts + 1];
			for (int ii = 0; ii <= nZpts; ++ii)
			{
				for (int ij = 0; ij <= nRpts; ++ij)
				{
					fieldProfile[ii][ij] = new BCylindrical();
					fieldProfile[ii][ij].setB(dataTags.tag(icount).getCharacterData());
					icount = icount + 1;
				}
			}
		} catch (SimpleXmlException e) {new LinacLegoException(e);}
	}
	public static MagCylFieldProfileBuilder getFieldProfileBuilderFromList(ArrayList<MagCylFieldProfileBuilder> fieldProfileBuilderList, String title)
	{
		int icount = 0;
		while (icount < fieldProfileBuilderList.size())
		{
			if (fieldProfileBuilderList.get(icount).getTitle().equals(title)) return fieldProfileBuilderList.get(icount);
			icount = icount + 1;
		}
		return null;
	}
	public static boolean fileExists(String path) {return new File(path).exists(); }
	public static boolean  removeFile(String path) 
	{
		if (!fileExists(path)) return true;
		File fileToBeRemoved = new File(path);
		return fileToBeRemoved.delete();
	}

	public int getNpts() {return nZpts;}
	public double getZmax() {return zmax;}
	public BCylindrical[][] getFieldProfile() {return fieldProfile;}
	public String getFieldUnit() {return fieldUnit;}
	public String getLengthUnit() {return lengthUnit;}
	public String getTitle() {return title;}
	
	public static class BCylindrical   implements Serializable
	{
		private static final long serialVersionUID = -5807960036139433700L;
		public double getBr() {return Br;}
		public void setBr(double br) {Br = br;}
		public void setBr(String br) {Br = Double.parseDouble(br);}
		public double getBz() {return Bz;}
		public void setBz(double bz) {Bz = bz;}
		public void setBz(String bz) {Bz = Double.parseDouble(bz);}
		public String getB() {return Double.toString(Br) + "," + Double.toString(Bz);}
		public void setB(String b) 
		{
			String delims = ",";

			setBr(b.split(delims)[0]);
			setBz(b.split(delims)[1]);
		}
		private double Br;
		private double Bz;
		
	}
	
	public static void main(String[] args) throws LinacLegoException 
	{
		MagCylFieldProfileBuilder fpb = new MagCylFieldProfileBuilder();
		fpb.readTraceWinFieldProfile("/home/dmcginnis427/Dropbox/TB18LatticeImport/sef2_545_4mm.bsr");
		fpb.writeXmlFile("/home/dmcginnis427/Dropbox/TB18LatticeImport/sef2_545_4mm.xml");
		MagCylFieldProfileBuilder fpb2 = new MagCylFieldProfileBuilder("/home/dmcginnis427/Dropbox/TB18LatticeImport/sef2_545_4mm.xml");
		fpb2.writeXmlFile("/home/dmcginnis427/Dropbox/TB18LatticeImport/sef2_545_4mm2.xml");
		fpb2.writeTraceWinFile("/home/dmcginnis427/Dropbox/TB18LatticeImport/sef2_545_4mm2.bsr");
	}

}
