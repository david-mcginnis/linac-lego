package se.esss.litterbox.linaclego.structures.beam;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.linaclego.utilities.RfFieldProfileBuilder;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamFieldMap extends LegoBeam
{
	private static final long serialVersionUID = -507594913033285726L;
	private double rfpdeg;
	private double xelmax;
	@SuppressWarnings("unused")
	private double radiusmm;
	private double lengthmm;
	private String file;
	private double scaleFactor;

	private RfFieldProfileBuilder fieldProfileBuilder = null;
	private double[] phiZprofile;
	private double phisdeg = -361.0;
	private double energyGain = 0.0;
	
	public LegoBeamFieldMap() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamFieldMap(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamFieldMap(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
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
		addDataElement("rfpdeg", "0.0", "double", "deg");
		addDataElement("xelmax", "0.0", "double", "unit");
		addDataElement("radiusmm", "0.0", "double", "mm");
		addDataElement("lengthmm", "0.0", "double", "mm");
		addDataElement("file", null, "string", "unit");
		addDataElement("scaleFactor", "1.0", "double", "unit");
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "FIELD_MAP 100";
			latticeCommand = latticeCommand + Lego.space + getDataValue("lengthmm");
			latticeCommand = latticeCommand + Lego.space + getDataValue("rfpdeg");
			latticeCommand = latticeCommand + Lego.space + getDataValue("radiusmm");
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + Lego.space + getDataValue("xelmax");
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + Lego.space + "0";
			latticeCommand = latticeCommand + Lego.space + getDataValue("file").split("\\.")[0];
			String traceWinFieldProfilePath = getlatticeFileOutputLocation().getParent() + Lego.delim + getDataValue("file").split("\\.")[0] + ".edz";
			fieldProfileBuilder.writeTraceWinFile(new File(traceWinFieldProfilePath));
		}
		return latticeCommand;
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException 
	{
		return energyGain;
	}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException 
	{
		return phisdeg;
	}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		rfpdeg = Double.parseDouble(getDataValue("rfpdeg"));
		xelmax = Double.parseDouble(getDataValue("xelmax"));
		radiusmm = Double.parseDouble(getDataValue("radiusmm"));
		lengthmm = Double.parseDouble(getDataValue("lengthmm"));
		scaleFactor = Double.parseDouble(getDataValue("scaleFactor"));
		file = getDataValue("file");
		fieldProfileBuilder = RfFieldProfileBuilder.getFieldProfileBuilderFromList(getLegoLinac().getRfFieldProfileBuilderList(), file);
		if (fieldProfileBuilder == null)
		{
			try 
			{
				URL fieldProfileBuilderUrl = new URL(getLego().getSourceParentUrl() + "/" + file + ".xml");
				fieldProfileBuilder = new RfFieldProfileBuilder(fieldProfileBuilderUrl);
				getLegoLinac().getRfFieldProfileBuilderList().add(fieldProfileBuilder);
			} 
			catch (MalformedURLException e) {throw new LinacLegoException(e); }
		}
		if (!(fieldProfileBuilder.getZmax() == lengthmm )) 
		{
			throw new LinacLegoException("Length does not match field profile Length");
		}
		phiZprofile = new double[fieldProfileBuilder.getNpts()];
		updateEnergyGain();
		
	}
	private void updateEnergyGain() throws LinacLegoException
	{
		phiZprofile[0] = 0.0;
		energyGain = 0.0;
		double dz =  lengthmm * 0.001 / ((double) (fieldProfileBuilder.getNpts() - 1));
		double k0 = Lego.TWOPI / getLegoSection().lamda();
		for (int ii = 0; ii < fieldProfileBuilder.getNpts(); ++ii)
		{
			energyGain = energyGain + 1.0e+06 * xelmax  * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] 
					* Math.cos(rfpdeg * Lego.degToRad + phiZprofile[ii]) * dz;
			if (ii < (fieldProfileBuilder.getNpts() - 1) )
			{
				phiZprofile[ii + 1] = phiZprofile[ii] + k0 * dz / beta(geteVin() + energyGain);
			}
		}
		double dWcos = 0.0;
		double dWsin = 0.0;
		for (int ii = 0; ii < fieldProfileBuilder.getNpts(); ++ii)
		{
			dWcos = dWcos + xelmax * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] * Math.cos(rfpdeg * Lego.degToRad + phiZprofile[ii]) * dz;
			dWsin = dWsin +  xelmax * scaleFactor * fieldProfileBuilder.getFieldProfile()[ii] * Math.sin(rfpdeg * Lego.degToRad + phiZprofile[ii]) * dz;
		}
		phisdeg = Math.atan(dWsin / dWcos ) / Lego.degToRad;
	}
	@Override
	protected void setType() {type = "fieldMap";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "FIELD_MAP";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("lengthmm", sdata[1]);
			setDataValue("rfpdeg", sdata[2]);
			setDataValue("radiusmm", sdata[3]);
			setDataValue("xelmax", sdata[5]);
			setDataValue("file", sdata[8]);
		}
		
	}
	@Override
	public String getPreferredIdLabelHeader() {return "CAV-";}
	@Override
	public String getPreferredDiscipline() {return "EMR";}
	@Override
	public double characteristicValue() {return xelmax;}
	@Override
	public String characteristicValueUnit() {return "field ratio";}

}
