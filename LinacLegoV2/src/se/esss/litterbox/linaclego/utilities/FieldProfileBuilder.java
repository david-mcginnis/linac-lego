package se.esss.litterbox.linaclego.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.simplexml.SimpleXmlDoc;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class FieldProfileBuilder 
{
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
	public FieldProfileBuilder() 
	{
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
	public static FieldProfileBuilder readTraceWinFieldProfile(double storedEnergy, File traceWinFile) throws LinacLegoException
	{
		FieldProfileBuilder fieldProfileBuilder = new FieldProfileBuilder();
		fieldProfileBuilder.storedEnergy = storedEnergy;
		String traceWinFilePath = traceWinFile.getPath();
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
		String delims = "[ ,\t]+";
		fieldProfileBuilder.npts = Integer.parseInt(outputBuffer.get(0).split(delims)[0]);
		fieldProfileBuilder.zmax = Double.parseDouble(outputBuffer.get(0).split(delims)[1]);
// Read scaleFactor but do not use it.
		double twScaleFactor = Double.parseDouble(outputBuffer.get(1).split(delims)[0]);
		if (twScaleFactor != 1.0 ) throw new LinacLegoException("edz file scale factor not equal to 1.0!");
		fieldProfileBuilder.fieldUnit = "Volt/m";
// Convert zmax from meters to mm
		fieldProfileBuilder.zmax  = fieldProfileBuilder.zmax * 1000;
		fieldProfileBuilder.fieldProfile = new double[fieldProfileBuilder.npts + 1];
		for (int ii = 0; ii <= fieldProfileBuilder.npts; ++ii)
		{
			fieldProfileBuilder.fieldProfile[ii] = Double.parseDouble(outputBuffer.get(ii + 2).split(delims)[0]);
		}
		return fieldProfileBuilder;
	}
	public void writeXmlFile(File xmlFile, String title) throws LinacLegoException
	{
		try {
			PrintWriter pw = new PrintWriter(xmlFile);
			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
			pw.println("<!DOCTYPE fieldProfile SYSTEM \"FieldProfile.dtd\" >");
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
	public static FieldProfileBuilder readXmlFile(URL xmlFileUrl) throws LinacLegoException
	{
		FieldProfileBuilder fieldProfileBuilder = new FieldProfileBuilder();
		try 
		{
			SimpleXmlDoc xdoc = new SimpleXmlDoc(xmlFileUrl);
			SimpleXmlReader fieldProfileTag = new SimpleXmlReader(xdoc);
			fieldProfileBuilder.zmax = Double.parseDouble(fieldProfileTag.attribute("length"));
			fieldProfileBuilder.storedEnergy = Double.parseDouble(fieldProfileTag.attribute("storedEnergy"));
			fieldProfileBuilder.fieldUnit = fieldProfileTag.attribute("fieldUnit");
			fieldProfileBuilder.title = fieldProfileTag.attribute("title");
			fieldProfileBuilder.lengthUnit = fieldProfileTag.attribute("lengthUnit");
			fieldProfileBuilder.storedEnergyUnit = fieldProfileTag.attribute("storedEnergyUnit");
			SimpleXmlReader dataTags = fieldProfileTag.tagsByName("d");
			fieldProfileBuilder.npts = dataTags.numChildTags() - 1;
			fieldProfileBuilder.fieldProfile = new double[fieldProfileBuilder.npts + 1];
			for (int ii = 0; ii <= fieldProfileBuilder.npts; ++ii)
			{
				fieldProfileBuilder.fieldProfile[ii] = Double.parseDouble(dataTags.tag(ii).getCharacterData());
			}
		} 
		catch (SimpleXmlException e) 
		{
			throw new LinacLegoException(e);
		}
		return fieldProfileBuilder;
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
	
	public static void main(String[] args) throws LinacLegoException, MalformedURLException 
	{
		File inputFile = new File("C:\\Users\\davidmcginnis\\Google Drive\\ESS\\linacLego\\public\\medBetaFieldMap.xml");
		String linacLegoWebSite = "https://cba4504235597b04fef2d0b4e6294cb45a84179e.googledrive.com/host/0B3Hieedgs_7FWlpGRHBXNVA2Rmc";
		URL inputFileUrl = new URL(linacLegoWebSite + "/public/medBetaFieldMap.xml");
		inputFileUrl = inputFile.toURI().toURL();
		FieldProfileBuilder fpb = FieldProfileBuilder.readXmlFile(inputFileUrl);
		File outputTraceWinFile = new File("C:\\Users\\davidmcginnis\\Google Drive\\ESS\\linacLego\\public\\testTraceWinFieldMap.edz");
		fpb.writeTraceWinFile(outputTraceWinFile);
		fpb = FieldProfileBuilder.readTraceWinFieldProfile(1.0, outputTraceWinFile);
		File outputXmlFile = new File("C:\\Users\\davidmcginnis\\Google Drive\\ESS\\linacLego\\public\\testXml.xml");
		fpb.writeXmlFile(outputXmlFile, "testXml");
	}

}
