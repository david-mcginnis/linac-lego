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

public class Rf1DFieldProfileBuilder  implements Serializable
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

	private int npts;
	private double zmax;
	private double[] fieldProfile;
	private String fieldUnit = null;
	private String title = "";
	private String lengthUnit = "mm";
	private String storedEnergyUnit = "Joules";
	private double storedEnergy;
	public Rf1DFieldProfileBuilder() 
	{
	}
	public Rf1DFieldProfileBuilder(URL xmlFileUrl) throws LinacLegoException 
	{
		readXmlFile(xmlFileUrl);
	}
	public Rf1DFieldProfileBuilder(String xmlFilePath) throws LinacLegoException 
	{
		readXmlFile(xmlFilePath);
	}
	public void writeTraceWinFile(File traceWinFile) throws LinacLegoException
	{
		try {
			PrintWriter pw = new PrintWriter(traceWinFile);
			pw.println(Integer.toString(npts) + " " + Double.toString(zmax * 0.001));
			double scaleFactor = 1.0;
			pw.println(Double.toString(scaleFactor));
			for (int ii = 0; ii <= npts; ++ii)
			{
				pw.println(Double.toString(fieldProfile[ii]));
			}
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			throw new LinacLegoException(e);
		}
	}	
	public  static Rf1DFieldProfileBuilder readTraceWinFieldProfile(double storedEnergy, String traceWinFilePath) throws LinacLegoException
	{
		Rf1DFieldProfileBuilder fpb = new Rf1DFieldProfileBuilder();
		fpb.storedEnergy = storedEnergy;
		BufferedReader br;
		ArrayList<String> outputBuffer = new ArrayList<String>();
		try {
			br = new BufferedReader(new FileReader(traceWinFilePath));
			String line;
			while ((line = br.readLine()) != null) 
			{  
				outputBuffer.add(line);
			}
			br.close();
		} 
		catch (FileNotFoundException e) {throw new LinacLegoException(e);}
		catch (IOException e) {throw new LinacLegoException(e);}
		fpb.title = new File(traceWinFilePath).getName();
		fpb.title = fpb.title.substring(0, fpb.title.lastIndexOf("."));
		String delims = "[ ,\t]+";
		fpb.npts = Integer.parseInt(outputBuffer.get(0).split(delims)[0]);
		fpb.zmax = Double.parseDouble(outputBuffer.get(0).split(delims)[1]);
// Read scaleFactor but do not use it.
		double twScaleFactor = Double.parseDouble(outputBuffer.get(1).split(delims)[0]);
		if (twScaleFactor != 1.0 ) throw new LinacLegoException("edz file scale factor not equal to 1.0!");
		fpb.fieldUnit = "Volt/m";
// Convert zmax from meters to mm
		fpb.zmax  = fpb.zmax * 1000;
		fpb.fieldProfile = new double[fpb.npts + 1];
		for (int ii = 0; ii <= fpb.npts; ++ii)
		{
			fpb.fieldProfile[ii] = Double.parseDouble(outputBuffer.get(ii + 2).split(delims)[0]);
		}
		return fpb;
	}
	public void writeXmlFile(String xmlFilePath) throws LinacLegoException
	{
		try {
			PrintWriter pw = new PrintWriter(xmlFilePath);
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			pw.println("<!DOCTYPE fieldProfile SYSTEM \"dtdFiles/FieldProfile.dtd\">");
			pw.println(
					"<fieldProfile title=\"" 
							+ title 
							+ "\" storedEnergy=\"" 
							+ Double.toString(storedEnergy) + "\"" 
							+ " length=\"" + Double.toString(zmax) + "\""
							+ " lengthUnit=\"" + lengthUnit + "\""
							+ " storedEnergyUnit=\"" + storedEnergyUnit + "\""
							+ " fieldUnit=\"" + fieldUnit + "\">");
			for (int ii = 0; ii <= npts; ++ii)
			{
				pw.print("\t<d id=\"" + Integer.toString(ii) + "\">" + Double.toString(fieldProfile[ii]) + "</d>\n");
			}
			pw.println("</fieldProfile>");
			pw.close();
			
		} catch (FileNotFoundException e) 
		{
			throw new LinacLegoException(e);
		}
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
			SimpleXmlDoc xdoc = new SimpleXmlDoc(xmlFileUrl);
			SimpleXmlReader fieldProfileTag = new SimpleXmlReader(xdoc);
			this.zmax = Double.parseDouble(fieldProfileTag.attribute("length"));
			this.storedEnergy = Double.parseDouble(fieldProfileTag.attribute("storedEnergy"));
			this.fieldUnit = fieldProfileTag.attribute("fieldUnit");
			this.title = fieldProfileTag.attribute("title");
			this.lengthUnit = fieldProfileTag.attribute("lengthUnit");
			this.storedEnergyUnit = fieldProfileTag.attribute("storedEnergyUnit");
			SimpleXmlReader dataTags = fieldProfileTag.tagsByName("d");
			this.npts = dataTags.numChildTags() - 1;
			this.fieldProfile = new double[this.npts + 1];
			for (int ii = 0; ii <= this.npts; ++ii)
			{
				this.fieldProfile[ii] = Double.parseDouble(dataTags.tag(ii).getCharacterData());
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
	}
	public static Rf1DFieldProfileBuilder getFieldProfileBuilderFromList(ArrayList<Rf1DFieldProfileBuilder> fieldProfileBuilderList, String title)
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

	public int getNpts() {return npts;}
	public double getZmax() {return zmax;}
	public double getStoredEnergy() {return storedEnergy;}
	public double[] getFieldProfile() {return fieldProfile;}
	public String getFieldUnit() {return fieldUnit;}
	public String getLengthUnit() {return lengthUnit;}
	public String getTitle() {return title;}
	
	public static void main(String[] args) throws LinacLegoException 
	{
		Rf1DFieldProfileBuilder fpb = Rf1DFieldProfileBuilder.readTraceWinFieldProfile(1.0, "testFiles/Spoke_W_coupler.edz");
		fpb.writeXmlFile("testFiles/Spoke_W_coupler.xml");
	}

}
