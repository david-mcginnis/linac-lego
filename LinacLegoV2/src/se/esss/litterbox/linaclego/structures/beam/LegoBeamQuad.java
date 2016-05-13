package se.esss.litterbox.linaclego.structures.beam;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamQuad extends LegoBeam
{
	private double lengthmm;
	private double gradient;
	@SuppressWarnings("unused")
	private double radius;

	public LegoBeamQuad() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamQuad(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	public LegoBeamQuad(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
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
	protected void addDataElements() throws LinacLegoException 
	{
		addDataElement("l", "0.0", "double","mm");
		addDataElement("g", "0.0", "double","T/m");
		addDataElement("r", "0.0", "double","mm");
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "QUAD";
			latticeCommand = latticeCommand + Lego.space + getDataValue("l");
			latticeCommand = latticeCommand + Lego.space + getDataValue("g");
			latticeCommand = latticeCommand + Lego.space + getDataValue("r");
		}
		return latticeCommand;
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return 0;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return 0;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return gradient;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		lengthmm = Double.parseDouble(getDataValue("l"));
		gradient = Double.parseDouble(getDataValue("g"));
		radius = Double.parseDouble(getDataValue("r"));
	}
	@Override
	protected void setType() {type = "quad";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "QUAD";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			setDataValue("l", sdata[0]);
			setDataValue("g", sdata[1]);
			setDataValue("r", sdata[2]);
		}
		
	}
	@Override
	public String getPreferredIdLabelHeader() 
	{
		String labelHeader = "QUA-";
		try {
			double gradient = Double.parseDouble(getDataValue("g"));
			if (gradient >= 0) labelHeader = "QH-";
			if (gradient <  0) labelHeader = "QV-";
		} catch (NumberFormatException | LinacLegoException e) {}
		return labelHeader;
	}

	@Override
	public String getPreferredDiscipline() {return "BMD";}

}
