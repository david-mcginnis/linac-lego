package se.esss.litterbox.linaclego.v2.structures.beam;

import java.net.MalformedURLException;
import java.net.URL;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.utilities.MagCylFieldProfileBuilder;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamMagCylFieldMap extends LegoBeam
{
	private static final long serialVersionUID = -507594913033285726L;
	private double lengthmm;
	private String file;

	private MagCylFieldProfileBuilder fieldProfileBuilder = null;
	
	public LegoBeamMagCylFieldMap() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamMagCylFieldMap(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamMagCylFieldMap(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
		localInputVec[2] = lengthmm * 0.001;
		return localInputVec;
	}
	@Override
	protected double[][] getLocalRotationMatrix() throws LinacLegoException 
	{
		double[][] localRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		return localRotMat;
	}
	@Override
	public void addDataElements() throws LinacLegoException 
	{
		addDataElement("lengthmm", "0.0", "double", "mm");
		addDataElement("radiusmm", "0.0", "double", "mm");
		addDataElement("magIntensity", "0.0", "double", "unit");
		addDataElement("file", null, "string", "unit");
	}
	@Override
	protected String defaultLatticeCommand() throws LinacLegoException 
	{
		String latticeCommand = "";
		latticeCommand = "FIELD_MAP";
		latticeCommand = latticeCommand + " 50";
		latticeCommand = latticeCommand + Lego.space + getDataValue("lengthmm");
		latticeCommand = latticeCommand + Lego.space + "0";;
		latticeCommand = latticeCommand + Lego.space + getDataValue("radiusmm");
		latticeCommand = latticeCommand + Lego.space + getDataValue("magIntensity");;
		latticeCommand = latticeCommand + Lego.space + "0";
		latticeCommand = latticeCommand + Lego.space + "0";
		latticeCommand = latticeCommand + Lego.space + "0";
		latticeCommand = latticeCommand + Lego.space + getDataValue("file").split("\\.")[0];
		String traceWinFieldProfilePath = getlatticeFileOutputLocation().getParent() + Lego.delim + getDataValue("file").split("\\.")[0] + ".bsr";
		fieldProfileBuilder.writeTraceWinFile(traceWinFieldProfilePath);
		return latticeCommand;
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return defaultLatticeCommand();
		return defaultLatticeCommand();
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return 0.0;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return 0.0;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		lengthmm = Double.parseDouble(getDataValue("lengthmm"));
		file = getDataValue("file");
		fieldProfileBuilder = MagCylFieldProfileBuilder.getFieldProfileBuilderFromList(getLegoLinac().getMagCylFieldProfileBuilderList(), file);
		if (fieldProfileBuilder == null)
		{
			try 
			{
				URL fieldProfileBuilderUrl = new URL(getLego().getSourceParentUrl() + "/" + file + ".xml");
				fieldProfileBuilder = new MagCylFieldProfileBuilder(fieldProfileBuilderUrl);
				getLegoLinac().getMagCylFieldProfileBuilderList().add(fieldProfileBuilder);
			} 
			catch (MalformedURLException e) {throw new LinacLegoException(e); }
		}
		if (!(fieldProfileBuilder.getZmax() == lengthmm )) 
		{
			throw new LinacLegoException("Length does not match field profile Length" + " Zmax = " + Double.toString(fieldProfileBuilder.getZmax()) + " length mm = "+ Double.toString(lengthmm));
		}
		
	}
	@Override
	protected void setType() {type = "magCyl2dFieldMap";}
	@Override
	public String getDefaultLatticeFileKeyWord() {return "MAG_CYL_2D_FIELD_MAP";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return  getDefaultLatticeFileKeyWord();
		return getDefaultLatticeFileKeyWord();
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("lengthmm", sdata[1]);
			setDataValue("radiusmm", sdata[3]);
			setDataValue("magIntensity", sdata[4]);
			setDataValue("file", sdata[8]);
		}
		
	}
	@Override
	public String getPreferredIdLabelHeader() {return "SOL-";}
	@Override
	public String getPreferredDiscipline() {return "BMD";}
	@Override
	public double characteristicValue() {return 0.0;}
	@Override
	public String characteristicValueUnit() {return "";}

}
