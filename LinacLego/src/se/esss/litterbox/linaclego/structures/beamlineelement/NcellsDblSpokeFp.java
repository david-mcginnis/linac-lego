/*
Copyright (c) 2014 European Spallation Source

This file is part of LinacLego.
LinacLego is free software: you can redistribute it and/or modify it under the terms of the 
GNU General Public License as published by the Free Software Foundation, either version 2 
of the License, or any newer version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program. 
If not, see https://www.gnu.org/licenses/gpl-2.0.txt
*/
package se.esss.litterbox.linaclego.structures.beamlineelement;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.slot.Slot;
import se.esss.litterbox.simplexml.SimpleXmlReader;


public class NcellsDblSpokeFp extends Ncells
{
	private double	xbetag;
	private double	ecentmax;
	private double	xesin;
	private double	xeend;
	private double	phisdeg;
	private double	xelmax;

	public NcellsDblSpokeFp(SimpleXmlReader elementTag, Slot slot, int index) throws LinacLegoException 
	{
		super(elementTag, slot, index);
	}
	@Override
	public void addDataElements() 
	{
		super.addDataElements();
		addDataElement("xbetag", "0.0", "double","unit");
		addDataElement("ecentmax", "0.0", "double","Volt/m");
		addDataElement("xesin", "0.0", "double","unit");
		addDataElement("xeend", "0.0", "double","unit");
		addDataElement("phisdeg", "0.0", "double","deg");
		addDataElement("xelmax", "0.0", "double","unit");
	}
	@Override
	public void readDataElements() throws LinacLegoException 
	{
		super.readDataElements();
		setMode(1);
		setNcells(3);
		setBetag(Double.parseDouble(getDataElement("betag").getValue()));
		setRadius(Double.parseDouble(getDataElement("radius").getValue()));

		xbetag = Double.parseDouble(getDataElement("xbetag").getValue());
		ecentmax = Double.parseDouble(getDataElement("ecentmax").getValue());
		xesin = Double.parseDouble(getDataElement("xesin").getValue());
		xeend = Double.parseDouble(getDataElement("xeend").getValue());

		phisdeg = Double.parseDouble(getDataElement("phisdeg").getValue());
		xelmax = Double.parseDouble(getDataElement("xelmax").getValue());
	}
	@Override
	public void calcParameters() throws LinacLegoException 
	{
		setTtInfo(true);
		matchSynchPhase(phisdeg, 5);
	}
	@Override
	protected double centerCellFieldProfile(double kgz)
	{
		double ec = 0;
		double kgzcEnd = xbetag * Math.acos((xeend - 1.0) / (xeend + 1.0));
		if (Math.abs(kgz) > kgzcEnd ) return ec;
		ec = xesin * (Math.cos(kgz / xbetag) - Math.cos(kgzcEnd / xbetag));
		if (ec > 1.0) ec = 1.0;
		ec = ec * xelmax * ecentmax;
		return ec;
	}
	@Override
	protected double outputCellFieldProfile(double kgz)
	{
		double kgza  = kgz + PI;
		double ee = 0;
		double kgzcEnd = xbetag * Math.acos((xeend - 1.0) / (xeend + 1.0));
		double kgzeEnd = TWOPI * xbetag - kgzcEnd;
		if (kgza < kgzcEnd) return ee;
		if (kgza > kgzeEnd) return ee;
		ee = -xesin * (Math.cos(kgza / xbetag) - Math.cos(kgzcEnd / xbetag));
		if (ee > xeend) ee = xeend;
		ee = ee * xelmax * ecentmax;
		return ee;
	}
	@Override
	protected double inputCellFieldProfile(double kgz)
	{
		return outputCellFieldProfile(-kgz);
	}

	public double getXbetag() {return xbetag;}
	public double getEcentmax() {return ecentmax;}
	public double getXesin() {return xesin;}
	public double getXeend() {return xeend;}
	public double getPhisdeg() {return phisdeg;}
	public double getXelmax() {return xelmax;}
	
	public void setXbetag(double xbetag) throws LinacLegoException 
	{
		this.xbetag = xbetag;
		getDataElement("xbetag").setValue(Double.toString(xbetag));
	}
	public void setEcentmax(double ecentmax) throws LinacLegoException 
	{
		this.ecentmax = ecentmax;
		getDataElement("ecentmax").setValue(Double.toString(ecentmax));
	}
	public void setXesin(double xesin) throws LinacLegoException 
	{
		this.xesin = xesin;
		getDataElement("xesin").setValue(Double.toString(xesin));
	}
	public void setXeend(double xeend) throws LinacLegoException 
	{
		this.xeend = xeend;
		getDataElement("xeend").setValue(Double.toString(xeend));
	}
	public void setPhisdeg(double phisdeg) throws LinacLegoException 
	{
		this.phisdeg = phisdeg;
		getDataElement("phisdeg").setValue(Double.toString(phisdeg));
	}
	public void setXelmax(double xelmax) throws LinacLegoException 
	{
		this.xelmax = xelmax;
		getDataElement("xelmax").setValue(Double.toString(xelmax));
	}
	public static void main(String[] args)  
	{
	}
	
}
