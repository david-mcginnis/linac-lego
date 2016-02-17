package se.esss.litterbox.linaclego.structures.legoset;

import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.simplexml.SimpleXmlException;

public class TransferFunction 
{
	private final static int numCoeff = 5;
	double[] coeff;
	
	public TransferFunction(LegoSet legoSet) throws LinacLegoException 
	{
		coeff = new double[numCoeff];
		for (int itf = 0; itf < numCoeff; ++itf)
		{
			try 
			{
				String coefString = legoSet.getLegoSetTag().attribute("tf" + Integer.toString(itf));
				try {coeff[itf] = Double.parseDouble(coefString);}
				catch (NumberFormatException nfe) {throw new LinacLegoException("Transfer function coeff not a number " + legoSet.getDevName());} 
			} catch (SimpleXmlException e) {coeff[itf] = 0.0;}
		}
	}
	public double get(double devSetting) 
	{
		double tfPow = 1.0;
		double tf = 0.0;
		for (int itf = 0; itf < numCoeff; ++itf)
		{
			tf = tf + coeff[itf] * tfPow;
			tfPow = tfPow * devSetting;
		}
		return tf;
	}
	public double invert(double tfDesired, int ntry, double tol)
	{
		double xguess = 0.0;
		if (Math.abs(coeff[1]) > 0.0)
		{
			xguess = (tfDesired - coeff[0]) / coeff[1];
		}
		double tfDeriv = getDerivative(xguess);
		double tf = get(xguess);
		double tferror = 1.0 + tol;
		int itry = 0;
		while ((itry < ntry) && (Math.abs(tfDeriv) > 0) && (tferror > tol))
		{
			xguess = xguess + 1.0 * (tfDesired - tf) / tfDeriv;
			tfDeriv = getDerivative(xguess);
			tf = get(xguess);
			tferror = (tf - tfDesired);
			if (tfDesired != 0) tferror = tferror / tfDesired;
			tferror = Math.abs(tferror);
			itry = itry + 1;
		}
		return xguess;
	}
	private double getDerivative(double devSetting)
	{
		double tfPow = 1.0;
		double tf = 0.0;
		for (int itf = 1; itf < numCoeff; ++itf)
		{
			tf = tf + ((double) itf) * coeff[itf] * tfPow;
			tfPow = tfPow * devSetting;
		}
		return tf;
		
	}
	public boolean matches(TransferFunction tfCheck)
	{
		for (int itf = 0; itf < numCoeff; ++itf)
		{
			if (coeff[itf] != tfCheck.coeff[itf]) return false;
		}
		return true;
	}
	public String getTfCsvData()
	{
		String csvData = "";
		for (int itf = 0; itf < numCoeff - 1; ++itf)
		{
			csvData = csvData + Double.toString(coeff[itf]) + ",";
		}
		csvData = csvData + Double.toString(coeff[numCoeff - 1]);
		return csvData;
	}
	public static String getTfCsvDataHeader()
	{
		String csvData = "";
		for (int itf = 0; itf < numCoeff - 1; ++itf)
		{
			csvData = csvData + "TF " + itf + ",";
		}
		csvData = csvData + "TF "+ (numCoeff - 1);
		return csvData;
	}

}
