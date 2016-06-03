package se.esss.litterbox.linaclego.data;

import java.io.Serializable;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.Lego;
import se.esss.litterbox.linaclego.structures.LegoSection;
import se.esss.litterbox.linaclego.structures.LegoSlot;
import se.esss.litterbox.linaclego.structures.beam.LegoBeam;

public class LegoModel  implements Serializable
{
	private static final long serialVersionUID = -5672672767466394689L;
	private String modelName = null;
	private String modelType = null;
	private String structureType = null;	
	private ArrayList<LegoSlot> slotList = new ArrayList<LegoSlot>();
	private ArrayList<LegoBeam> beamList = new ArrayList<LegoBeam>();
	
	public String getModelName() {return modelName;}
	public String getStructureType() {return structureType;}
	public String getModelType() {return modelType;}
	public ArrayList<LegoSlot> getSlotList() {return slotList;}
	public ArrayList<LegoBeam> getBeamList() {return beamList;}
	
	public LegoModel(LegoSlot legoSlot)
	{
		slotList = new ArrayList<LegoSlot>();
		beamList = null;
		slotList.add(legoSlot);
		structureType = "slot";
		if (legoSlot.getTemplate() == null)
		{
			modelName = null;
		}
		else
		{
			modelName = legoSlot.getTemplate();
		}
		modelType = "slot";
	}
	public LegoModel(LegoBeam legoBeam)
	{
		beamList = new ArrayList<LegoBeam>();
		slotList = null;
		beamList.add(legoBeam);
		structureType = "beam";
		if (legoBeam.getModel() == null)
		{
			modelName = null;
		}
		else
		{
			modelName = legoBeam.getModel();
		}
		modelType = legoBeam.getType();
	}
	public static void addLegoBeamToModelList(ArrayList<LegoModel> legoModelList, LegoBeam legoBeam)
	{
		if (legoModelList.size() < 1)
		{
			legoModelList.add(new LegoModel(legoBeam));
			return;
		}
		int imodel = 0;
		while (imodel < legoModelList.size())
		{
			if (legoModelList.get(imodel).getStructureType().equals("beam"))
			{
				if (legoModelList.get(imodel).getModelType().equals(legoBeam.getType()))
				{
					if (legoModelList.get(imodel).getModelName() == null)
					{
						if (legoBeam.getModel() == null)
						{
							legoModelList.get(imodel).getBeamList().add(legoBeam);
							return;
						}
					}
					else
					{
						if (legoBeam.getModel() != null)
						{
							if (legoModelList.get(imodel).getModelName().equals(legoBeam.getModel()))
							{
								legoModelList.get(imodel).getBeamList().add(legoBeam);
								return;
							}
						}
					}
				}
			}
			imodel = imodel + 1;
		}
		legoModelList.add(new LegoModel(legoBeam));
		return;
	}
	public static void addLegoSlotToModelList(ArrayList<LegoModel> legoModelList, LegoSlot legoSlot)
	{
		if (legoModelList.size() < 1)
		{
			legoModelList.add(new LegoModel(legoSlot));
			return;
		}
		int imodel = 0;
		while (imodel < legoModelList.size())
		{
			if (legoModelList.get(imodel).getStructureType().equals("slot"))
			{
				if (legoModelList.get(imodel).getModelName() == null)
				{
					if (legoSlot.getTemplate() == null)
					{
						legoModelList.get(imodel).getSlotList().add(legoSlot);
						return;
					}
				}
				else
				{
					if (legoSlot.getTemplate() != null)
					{
						if (legoModelList.get(imodel).getModelName().equals(legoSlot.getTemplate()))
						{
							legoModelList.get(imodel).getSlotList().add(legoSlot);
							return;
						}
					}
				}
			}
			imodel = imodel + 1;
		}
		legoModelList.add(new LegoModel(legoSlot));
		return;
	}
	public double[] minAvgMaxCharacteristicValues()
	{
		if (structureType.equals("slot")) return null;
		double minValue = 1.0e+33;
		double maxValue = -1.0e+33;
		double averageValue = 0.0;
		double numElements = 0.0;
		for (int ij = 0; ij < beamList.size(); ++ij)
		{
			double cv = beamList.get(ij).characteristicValue();
			if (minValue > cv) minValue = cv;
			if (maxValue < cv) maxValue = cv;
			averageValue = averageValue + cv;
			numElements = numElements + 1;
		}
		averageValue = averageValue / numElements;
		double[] minAvgMax = {minValue, averageValue, maxValue};
		return minAvgMax;
	}
	public int modelCountForSection(String sectionId)
	{
		int icount = 0;
		if (structureType.equals("slot"))
		{
			for (int islot = 0; islot < slotList.size(); ++islot)
			{
				if (slotList.get(islot).getLegoSection().getId().equals(sectionId)) icount = icount + 1;
			}
		}
		if (structureType.equals("beam"))
		{
			for (int ibeam = 0; ibeam < beamList.size(); ++ibeam)
			{
				if (beamList.get(ibeam).getLegoSection().getId().equals(sectionId)) icount = icount + 1;
			}
		}
		return icount;
	}
	public String printRowOfPartCounts()
	{
		if (structureType.equals("slot"))
		{
			String rowString = "slot" + "," + modelName;
			ArrayList<LegoSection> legoSectionList = slotList.get(0).getLegoLinac().getLegoSectionList();
			for (int isection = 0; isection < legoSectionList.size(); ++isection)
			{
				rowString = rowString + "," + modelCountForSection(legoSectionList.get(isection).getId());
			}
			rowString = rowString + "," + slotList.size();
			return rowString;
		}
		String rowString = modelType + "," + modelName;
		ArrayList<LegoSection> legoSectionList = beamList.get(0).getLegoLinac().getLegoSectionList();
		for (int isection = 0; isection < legoSectionList.size(); ++isection)
		{
			rowString = rowString + "," + modelCountForSection(legoSectionList.get(isection).getId());
		}
		rowString = rowString + "," + beamList.size();

		double[] minAvgMax = minAvgMaxCharacteristicValues();
		rowString = rowString + "," + Lego.fourPlaces.format(minAvgMax[0]) + "," + Lego.fourPlaces.format(minAvgMax[1]) + "," + Lego.fourPlaces.format(minAvgMax[2]);
		rowString = rowString + "," + beamList.get(0).characteristicValueUnit();
		return rowString;
	}

}
