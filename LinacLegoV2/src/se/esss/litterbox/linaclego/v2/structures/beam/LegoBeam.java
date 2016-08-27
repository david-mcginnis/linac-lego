package se.esss.litterbox.linaclego.v2.structures.beam;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.v2.Lego;
import se.esss.litterbox.linaclego.v2.LinacLegoException;
import se.esss.litterbox.linaclego.v2.data.LegoData;
import se.esss.litterbox.linaclego.v2.data.LegoInfo;
import se.esss.litterbox.linaclego.v2.structures.LegoCell;
import se.esss.litterbox.linaclego.v2.structures.LegoLinac;
import se.esss.litterbox.linaclego.v2.structures.LegoSection;
import se.esss.litterbox.linaclego.v2.structures.LegoSlot;
import se.esss.litterbox.linaclego.v2.templates.LegoBeamTemplate;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public abstract class LegoBeam  implements Serializable
{
	private static final long serialVersionUID = -623912672912900885L;
	public static final String TABLE_HEADER       = "Section,Cell,Slot,Beam,Type,Model,Disc,Address,eVout,v/c,Length,Xend,Yend,Zend,Xsur,Ysur,Zsur,Volts,Phase,Grad,Bend";
	public static final String TABLE_HEADER_UNITS = "       ,    ,    ,    ,    ,     ,    ,       ,(MeV),   , (m)  , (m), (m), (m), (m), (m), (m), MV  , deg , T/m, deg";
	public static final double PI = Math.PI;
	public static final double degToRad = PI / 180.0;

	private ArrayList<LegoData> legoDataList = new ArrayList<LegoData>();
	private ArrayList<LegoInfo> legoInfoList = new ArrayList<LegoInfo>();
	private String id = null;
	protected String type = null;
	private String disc = null;
	private String model = null;
	private int beamListIndex = -1;
	private LegoSlot legoSlot;
	
	private double[] endPosVec = {0.0, 0.0, 0.0};
	private double[][] endRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
	private double length = 0.0;
	private double eVout = -0.0;
	private double eVin = -0.0;
	private double synchronousPhaseDegrees = 0.0;
	private double quadGradientTpm = 0.0;
	private double dipoleBendDegrees = 0.0;

	public double[] getEndPosVec() {return endPosVec;}
	public double[][] getEndRotMat() {return endRotMat;}
	public double getLength() {return length;}
	public double geteVout() {return eVout;}
	public double geteVin() {return eVin;}
	public double getVoltage() {return 	(1e-6 * (geteVout() - geteVin()) / Math.cos(getSynchronousPhaseDegrees() * Lego.degToRad));}
	public double getSynchronousPhaseDegrees() {return synchronousPhaseDegrees;}
	public double getQuadGradientTpm() {return quadGradientTpm;}
	public double getDipoleBendDegrees() {return dipoleBendDegrees;}

	public ArrayList<LegoData> getLegoDataList() {return legoDataList;}
	public String getId() {return id;}
	public String getType() {return type;}
	public String getDisc() {return disc;}
	public String getModel() {return model;}
	public void setId(String id) {this.id = id;}
	public void setDisc(String disc) {this.disc = disc;}
	public void setModel(String model) {this.model = model;}
	public void setInfoList(ArrayList<LegoInfo> legoInfoList) {this.legoInfoList = legoInfoList;}
	public int getBeamListIndex() {return beamListIndex;}
	public LegoSlot getLegoSlot() {return legoSlot;}
	public LegoCell getLegoCell() {return legoSlot.getLegoCell();}
	public LegoSection getLegoSection() {return getLegoCell().getLegoSection();}
	public LegoLinac getLegoLinac() {return getLegoCell().getLegoLinac();}
	public Lego getLego() {return getLegoCell().getLego();}
	public ArrayList<LegoInfo> getLegoInfoList() {return legoInfoList;}
	public File getlatticeFileOutputLocation() {return getLego().getlatticeFileOutputLocation();}
	
	protected abstract double[] getLocalTranslationVector() throws LinacLegoException;
	protected abstract double[][] getLocalRotationMatrix() throws LinacLegoException;
	public abstract void addDataElements() throws LinacLegoException;
	protected abstract void calcParameters() throws LinacLegoException;
	protected abstract String latticeCommand(String latticeType) throws LinacLegoException;
	protected abstract double reportEnergyChange() throws LinacLegoException;
	protected abstract double reportSynchronousPhaseDegrees() throws LinacLegoException;
	protected abstract double reportQuadGradientTpm() throws LinacLegoException;
	protected abstract double reportDipoleBendDegrees() throws LinacLegoException;
	protected abstract void setType();
	public abstract String getLatticeFileKeyWord(String latticeType);
	public abstract void addLatticeData(String latticeType, String[] sdata);
	public abstract String getPreferredIdLabelHeader();
	public abstract String getPreferredDiscipline();
	public abstract double characteristicValue();
	public abstract String characteristicValueUnit();
	
	public LegoBeam() throws LinacLegoException
	{
		setType();
	}
	public LegoBeam(LegoSlot legoSlot, int beamListIndex, String id, String disc, String model) throws LinacLegoException
	{
		setType();
		this.legoSlot = legoSlot;
		this.beamListIndex = beamListIndex;
		this.id = id;
		this.disc = disc;
		this.model = model;
	}
	public LegoBeam(LegoSlot legoSlot, int beamListIndex, SimpleXmlReader beamTag) throws LinacLegoException
	{
		setType();
		this.legoSlot = legoSlot;
		this.beamListIndex = beamListIndex;
		try {this.id = beamTag.attribute("id");	} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		try {this.disc = beamTag.attribute("disc");	} catch (SimpleXmlException e)  {disc = null;}
		try {this.model = beamTag.attribute("model");	} catch (SimpleXmlException e)  {model = null;}
		SimpleXmlReader dataTags = beamTag.tagsByName("d");
		if (dataTags.numChildTags() > 0)
		{
			for (int ii = 0; ii < dataTags.numChildTags(); ++ii)
			{
				try {legoDataList.add(new LegoData(dataTags.tag(ii)));} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
			}
		}
		SimpleXmlReader infoTag = beamTag.tagsByName("info");
		if (infoTag.numChildTags() > 0)
		{
			for (int ii = 0; ii < infoTag.numChildTags(); ++ii)
			{
				try {legoInfoList.add(new LegoInfo(infoTag.tag(ii)));} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
			}
		}
	}
	public void writeXml(SimpleXmlWriter xw) throws LinacLegoException
	{
		try 
		{
			xw.openXmlTag("beam");
			xw.setAttribute("id", id);
			xw.setAttribute("type", type);
			if (disc != null) xw.setAttribute("disc", disc);
			if (model != null) xw.setAttribute("model", model);
			if (legoInfoList.size() > 0) for (int ii = 0; ii < legoInfoList.size(); ++ii) legoInfoList.get(ii).writeXml(xw);
			if (legoDataList.size() > 0) for (int ii = 0; ii < legoDataList.size(); ++ii) legoDataList.get(ii).writeXml(xw);
			xw.closeXmlTag("beam");
		} catch (SimpleXmlException e)  {throw new LinacLegoException(e);}
		
	}
	public void addDataElement(String id, String value, String type, String unit)
	{
		legoDataList.add(new LegoData(id, value, type, unit));
	}
	public String getDataValue(String id) throws LinacLegoException
	{
		return LegoData.findLegoDataById(getLegoDataList(), id).getValue();
	}
	public void setDataValue(String id, String value) 
	{
		LegoData.findLegoDataById(getLegoDataList(), id).setValue(value);
	}
	public LegoBeam getPreviousBeam()
	{
		if (beamListIndex > 0) return getLegoSlot().getLegoBeamList().get(beamListIndex - 1);
		LegoSlot previousSlot = getLegoSlot().getPreviousSlot();
		if (previousSlot != null) 
		{
			return previousSlot.getLegoBeamList().get(previousSlot.getLegoBeamList().size() - 1);
		}
		return null;
	}
	public LegoBeam getNextBeam()
	{
		if (beamListIndex < (getLegoSlot().getLegoBeamList().size() - 1)) return getLegoSlot().getLegoBeamList().get(beamListIndex + 1);
		LegoSlot nextSlot = getLegoSlot().getNextSlot();
		if (nextSlot != null) return nextSlot.getLegoBeamList().get(0);
		return null;
	}
	public void checkDataElements() throws LinacLegoException
	{
		if (legoDataList.size() > 0)
		{
			for (int ii = 0; ii < legoDataList.size(); ++ii)
			{
				LegoData ld = LegoData.findLegoDataById(getLegoDataList(), legoDataList.get(ii).getId());
				if (ld == null) throw new LinacLegoException(legoDataList.get(ii).getId() + " not found.");
				if(!ld.matchesIdUnitType(legoDataList.get(ii)))
						 throw new LinacLegoException(legoDataList.get(ii).getId() + " does not match input data");
			}
		}
	}
	public void triggerUpdate() throws LinacLegoException
	{
		checkDataElements();
		eVin = 0.0;
		if (getPreviousBeam() != null)
		{
			eVin = getPreviousBeam().geteVout();
		}
		else
		{
			eVin = getLegoLinac().ekinMeVIn() * 1.0e+06;
		}
		calcParameters();
		updateLengthAndPosition();
		eVout = reportEnergyChange() + eVin;
		synchronousPhaseDegrees = reportSynchronousPhaseDegrees();
		quadGradientTpm = reportQuadGradientTpm();
		dipoleBendDegrees = reportDipoleBendDegrees();
	}
	private void updateLengthAndPosition() throws LinacLegoException
	{
		double[] localInputVec = getLocalTranslationVector();
		length = 0.0;
		for (int ir = 0; ir  < 3; ++ir) 
			length = length + localInputVec[ir] * localInputVec[ir];
		length = Math.sqrt(length);

		double[][] localRotMat = getLocalRotationMatrix();
		double[][] prevRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		double[] prevPosVec = {0.0, 0.0, 0.0};
		if (getPreviousBeam() != null)
		{
			for (int ir = 0; ir  < 3; ++ir)
			{
				prevPosVec[ir] = getPreviousBeam().getEndPosVec()[ir];
				for (int ic = 0; ic < 3; ++ic)	
				{
					prevRotMat[ir][ic] = getPreviousBeam().getEndRotMat()[ir][ic];
				}
			}
		}
		double[] localOutputVec = {0.0, 0.0, 0.0};
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
				localOutputVec[ir] = localOutputVec[ir] + prevRotMat[ir][ic] * localInputVec[ic];
			getEndPosVec()[ir] = getBeginPosVec()[ir] + localOutputVec[ir];
		}
		for (int ir = 0; ir  < 3; ++ir)
		{
			for (int ic = 0; ic < 3; ++ic)	
			{
				getEndRotMat()[ir][ic] = 0.0;
				for (int ik = 0; ik < 3; ++ik)	
				{
					getEndRotMat()[ir][ic] = getEndRotMat()[ir][ic] + localRotMat[ir][ik] * prevRotMat[ik][ic];
				}
			}
		}
	}
	private double[] getBeginPosVec()
	{
		double[] beginPosVec = {0.0, 0.0, 0.0};
		if (getPreviousBeam() != null)
		{
			beginPosVec = getPreviousBeam().getEndPosVec();
		}
		return beginPosVec;
	}
	public double[] centerLocation()
	{
		double[] centerPosVec = {0.0, 0.0, 0.0};
		double[] beginPosVec = {0.0, 0.0, 0.0};
		if (getPreviousBeam() != null)
		{
			for (int ii = 0; ii < 3; ++ii) beginPosVec[ii] = getPreviousBeam().getEndPosVec()[ii];
		}
		for (int ii = 0; ii < 3; ++ii) centerPosVec[ii] = 0.5 * (beginPosVec[ii] + endPosVec[ii]);
		return centerPosVec;
	}
	public double[][] centerRotMat()
	{
		double[][] centerRotMat = { {0.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 0.0}};
		double[][] beginRotMat = { {1.0, 0.0, 0.0}, {0.0, 1.0, 0.0}, {0.0, 0.0, 1.0}};
		if (getPreviousBeam() != null)
		{
			for (int ii = 0; ii < 3; ++ii) 
				for (int ij = 0; ij < 3; ++ij) beginRotMat[ii][ij] = getPreviousBeam().getEndRotMat()[ii][ij];
		}
		for (int ii = 0; ii < 3; ++ii)
			for (int ij = 0; ij < 3; ++ij) centerRotMat[ii][ij] = 0.5 * (beginRotMat[ii][ij] + endRotMat[ii][ij]);
		return centerRotMat;
	}
	public String getAddress() 
	{
		return getLegoSlot().getAddress() + "-" + getId();
	}
	public void printLatticeCommand(PrintWriter pw, String latticeType) throws LinacLegoException
	{
		if (legoInfoList.size() > 0) 
		{
			for (int ii = 0; ii < legoInfoList.size(); ++ii) 
			{
				String tab = "                 ";
				legoInfoList.get(ii).writeToLatticeFile(pw, tab);
			}
		}
		String latticeCommand = "                  ";
		latticeCommand = latticeCommand + latticeCommand(latticeType);
		int clen = latticeCommand.length();
		for (int ii = clen; ii < 100; ++ii) latticeCommand = latticeCommand + " ";
		String legoComment = " ;lego <beam id=\"" + id + "\"";
		if (disc != null) legoComment = legoComment + " disc=\"" + disc + "\"";
		if (model != null) legoComment = legoComment + " model=\"" + model + "\"";
		legoComment = legoComment + ">";
		latticeCommand = latticeCommand + legoComment;
		pw.println(latticeCommand);
	}
	public void printTable(PrintWriter pw) throws LinacLegoException 
	{
		double[] endVec = getEndPosVec();
		double[] surveyCoords = getLegoLinac().getSurveyCoords(endVec);
		pw.print(getLegoSection().getId());
		pw.print("," + getLegoCell().getId());
		pw.print("," + getLegoSlot().getId());
		pw.print("," + getId());
		pw.print("," + getType());
		if (!(getModel() == null)) pw.print(" ," + getModel());
		if (  getModel() == null ) pw.print(" ," + "");
		if (getDisc() != null) pw.print(" ," + getDisc());
		if (getDisc() == null) pw.print(" ," + "");
		pw.print(" ," + getAddress());
		pw.print(" ," + Lego.sixPlaces.format((geteVout() / 1.0e6)));
		pw.print(" ," + Lego.sixPlaces.format(beta(geteVout())));
		pw.print(" ," + Lego.sixPlaces.format(getLength()));
		pw.print(" ," + Lego.sixPlaces.format(endVec[0]));
		pw.print(" ," + Lego.sixPlaces.format(endVec[1]));
		pw.print(" ," + Lego.sixPlaces.format(endVec[2]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[0]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[1]));
		pw.print(" ," + Lego.sixPlaces.format(surveyCoords[2]));
		pw.print(" ," + Lego.sixPlaces.format(getVoltage()));
		pw.print(" ," + Lego.sixPlaces.format(getSynchronousPhaseDegrees()));
		pw.print(" ," + Lego.sixPlaces.format(getQuadGradientTpm()));
		pw.print(" ," + Lego.sixPlaces.format(getDipoleBendDegrees()));
		pw.println("");
	}
	public static double gamma(double eVkin)
	{
		double gamma = (eVkin + Lego.eVrest) / Lego.eVrest;
		return gamma;
	}
	public static double beta(double eVkin)
	{
		double beta = gamma(eVkin);
		beta = Math.sqrt(1.0 - 1.0 / (beta * beta));
		return beta;
	}
	public static double pc(double eVkin)
	{
		return beta(eVkin) * gamma(eVkin) * Lego.eVrest;
	}
	public boolean matchesTemplate(LegoBeamTemplate beamTemplate) throws LinacLegoException
	{
		boolean matches = true;
		if (!beamTemplate.getType().equals(type)) return false;
		if ((beamTemplate.getLegoDataList().size() < 1) && (legoDataList.size() < 1)) return true;
		if (beamTemplate.getLegoDataList().size() != legoDataList.size()) return false;
		for (int i1 = 0; i1 < beamTemplate.getLegoDataList().size(); ++i1)
		{
			int i2 = 0;
			boolean dataMatch = false;
			while ((i2 < legoDataList.size()) && !dataMatch)
			{
				if (legoDataList.get(i2).matchesDataElementTemplate(beamTemplate.getLegoDataList().get(i1))) dataMatch = true;
				i2 = i2 + 1;
			}
			if (!dataMatch) return false;
		}
		for (int i1 = 0; i1 < beamTemplate.getLegoInfoList().size(); ++i1)
		{
			int i2 = 0;
			boolean infoMatch = false;
			while ((i2 < legoInfoList.size()) && !infoMatch)
			{
				if (legoInfoList.get(i2).matchesLegoInfoTemplate(beamTemplate.getLegoInfoList().get(i1))) infoMatch = true;
				i2 = i2 + 1;
			}
			if (!infoMatch) return false;
		}
		return matches;
		
	}

}
