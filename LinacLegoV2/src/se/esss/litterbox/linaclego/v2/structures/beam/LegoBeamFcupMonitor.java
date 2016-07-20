package se.esss.litterbox.linaclego.v2.structures.beam;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.simplexml.SimpleXmlReader;

public class LegoBeamFcupMonitor extends LegoBeam 
{
	private static final long serialVersionUID = -8717805986390419636L;
	String data = "";
	double charge = 0.0;
	
	public LegoBeamFcupMonitor() throws LinacLegoException 
	{
		super();
	}
	public LegoBeamFcupMonitor(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, id, disc, model);
	}
	public LegoBeamFcupMonitor(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException 
	{
		super(legoSlot, beamListIndex, beamTag);
	}
	@Override
	protected double[] getLocalTranslationVector() throws LinacLegoException 
	{
		double[] localInputVec = {0.0, 0.0, 0.0};
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
		addDataElement("data", "", "string", "unit");
		addDataElement("charge", "0.0", "double", "C");
	}
	@Override
	protected void calcParameters() throws LinacLegoException 
	{
		data = getDataValue("data");
		charge = Double.parseDouble(getDataValue("charge"));
	}
	@Override
	protected String latticeCommand(String latticeType) throws LinacLegoException 
	{
		String latticeCommand = "";
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			latticeCommand = "DIAG_POSITION";
			latticeCommand = latticeCommand + Lego.space + getDataValue("data");
			latticeCommand = ";lego <beam ";
			latticeCommand = latticeCommand + "disc=\"" + getDisc() + "\" id=\"" + getId() + "\" data=\"DIAG_FC " +  data + "\">";
		}
		return latticeCommand;
	}
	@Override
	protected double reportEnergyChange() throws LinacLegoException {return 0;}
	@Override
	protected double reportSynchronousPhaseDegrees() throws LinacLegoException {return 0;}
	@Override
	protected double reportQuadGradientTpm() throws LinacLegoException {return 0;}
	@Override
	protected double reportDipoleBendDegrees() throws LinacLegoException {return 0;}
	@Override
	protected void setType() {type = "fcup";}
	@Override
	public String getLatticeFileKeyWord(String latticeType) 
	{
		if (latticeType.equalsIgnoreCase("tracewin")) return "DIAG_FC";
		return null;
	}
	@Override
	public void addLatticeData(String latticeType, String[] sdata) 
	{
		if (latticeType.equalsIgnoreCase("tracewin"))
		{
			data = "";
			for (int ii = 0; ii < sdata.length; ++ii) data = data + " " + sdata[ii];
			setDataValue("data", data.trim());
		}
	}
	@Override
	public String getPreferredIdLabelHeader() {return "FC-";}
	@Override
	public String getPreferredDiscipline() {return "PBI";}
	@Override
	public double characteristicValue() {return charge;}
	@Override
	public String characteristicValueUnit() {return "C";}

}
