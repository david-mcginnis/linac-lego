package se.esss.litterbox.linaclego.structures.legoset;


import java.text.DecimalFormat;
import java.util.ArrayList;

import se.esss.litterbox.linaclego.LinacLego;
import se.esss.litterbox.linaclego.LinacLegoException;
import se.esss.litterbox.linaclego.structures.LegoData;
import se.esss.litterbox.simplexml.SimpleXmlException;
import se.esss.litterbox.simplexml.SimpleXmlReader;
import se.esss.litterbox.simplexml.SimpleXmlWriter;

public class LegoSet 
{
	private SimpleXmlReader legoSetTag;
	private String dataId = null;
	private String devName = null;
	private String unit;
	private String type;
	private String section = null;
	private String cell = null;
	private String slot = null;
	private String ble = null;
	private LinacLego linacLego = null;
	private TransferFunction transferFunction;
	private SimpleXmlReader matchingDataTag = null;

	public SimpleXmlReader getLegoSetTag() {return legoSetTag;}
	public SimpleXmlReader getMatchingDataTag() {return matchingDataTag;}
	public String getDataId() {return dataId;}
	public String getDevName() {return devName;}
	public String getUnit() {return unit;}
	public String getType() {return type;}
	public String getSection() {return section;}
	public String getCell() {return cell;}
	public String getSlot() {return slot;}
	public String getBle() {return ble;}
	public TransferFunction getTransferFunction() {return transferFunction;}
	public LinacLego getLinacLego() {return linacLego;}
	
	public LegoSet(SimpleXmlReader legoSetTag, LinacLego linacLego) throws LinacLegoException
	{
		this.legoSetTag = legoSetTag;
		this.linacLego = linacLego;
		
		try {devName = legoSetTag.attribute("devName");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No deviceName for legoSet.");}
		try {unit = legoSetTag.attribute("unit");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No unit for legoSet for device " + devName);}
		try {type = legoSetTag.attribute("type");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No type for legoSet for device " + devName);}
		try {dataId = legoSetTag.attribute("dataId");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No dataId for legoSet for device " + devName);}
		
		section = null;
		cell = null;
		slot = null;
		ble = null;
		try {section = legoSetTag.attribute("section");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No section for legoSet for device " + devName);}
		try {cell = legoSetTag.attribute("cell");} 
		catch (SimpleXmlException e) {throw new LinacLegoException("No cell for legoSet for device " + devName);}
		try {slot = legoSetTag.attribute("slot");} 
		catch (SimpleXmlException e) {slot = null;}
		try {ble = legoSetTag.attribute("ble");} 
		catch (SimpleXmlException e) {slot = null;}
			
		transferFunction = new TransferFunction(this);
		SimpleXmlReader sectionTag = linacLego.getLinacTag().tagsByName("section").getTagMatchingAttribute("id", section);
		SimpleXmlReader cellTag = sectionTag.tagsByName("cell").getTagMatchingAttribute("id", cell);
		SimpleXmlReader dataTagParent = cellTag;
		SimpleXmlReader slotTag = null;
		SimpleXmlReader bleTag = null;
		if (slot != null)
		{
			slotTag = cellTag.tagsByName("slot").getTagMatchingAttribute("id", slot);
			dataTagParent = slotTag;
			if (ble != null) 
			{
				bleTag = slotTag.tagsByName("ble").getTagMatchingAttribute("id", ble);
				dataTagParent = bleTag;
			}
		}
		matchingDataTag = dataTagParent.tagsByName("d").getTagMatchingAttribute("id", dataId);
	}
	private String getLegoSetData(ArrayList<LegoData> legoSetDataList) throws LinacLegoException
	{
		int icount = 0;
		LegoData legoSetData;
		while (icount < legoSetDataList.size())
		{
			legoSetData = legoSetDataList.get(icount);
			if (devName.equals(legoSetData.getId()))
			{
				if (unit.equals(legoSetData.getUnit()))
				{
					if (type.equals(legoSetData.getType()))
					{
						if (type.equals("double"))
						{
							return legoSetDataList.get(icount).getValue();
						}
						else
						{
							throw new LinacLegoException("Type of " + devName + " is not a double");
						}
					}
					else
					{
						throw new LinacLegoException("Type of " + devName + " does not match setting type");
					}
				}
				else
				{
					throw new LinacLegoException("Unit of " + devName + " does not match setting unit");
				}
			}
			icount = icount + 1;
		}
		return null;
	}
	public void updateLatticeSettingsUsingLegoSetData(ArrayList<LegoData> legoSetDataList) throws LinacLegoException 
	{
		String legoSetDataString = getLegoSetData(legoSetDataList);
		double latticeValue = transferFunction.get(Double.parseDouble(legoSetDataString));
		try {matchingDataTag.setCharacterData(Double.toString(latticeValue));} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public void updateLegoSetDataUsingLatticeSettings(SimpleXmlWriter xw) throws LinacLegoException
	{
		DecimalFormat fivePlaces = new DecimalFormat("###.#####");
		double newDeviceSetting = transferFunction.invert(Double.parseDouble(matchingDataTag.getCharacterData()), 10, .01);
		try 
		{
			xw.openXmlTag("legoSetData");
			xw.setAttribute("id", devName);
			xw.setAttribute("type", type);
			xw.setAttribute("unit", unit);
			xw.writeCharacterData(fivePlaces.format(newDeviceSetting));
			xw.closeXmlTag("legoSetData");
		} 
		catch (SimpleXmlException e) {throw new LinacLegoException(e);}
	}
	public static LegoSet getMatchingLegoSet(String sectionToMatch, String cellToMatch, String slotToMatch, String bleToMatch, String dataIdToMatch, LinacLego linacLego) throws LinacLegoException
	{
		LegoSet matchingLegoSet = null;
		int iset = 0;
		while (iset < linacLego.getLegoSetList().size())
		{
			LegoSet legoSet = linacLego.getLegoSetList().get(iset);
			boolean sectionMatch = sectionToMatch.equals(legoSet.getSection());
			boolean cellMatch = cellToMatch.equals(legoSet.getCell());
			boolean dataIdMatch = dataIdToMatch.equals(legoSet.getDataId());
			boolean slotMatch = false;
			boolean bleMatch = false;
			if ((slotToMatch == null) && (legoSet.getSlot() == null))
			{
				slotMatch = true;
			}
			else
			{
				if (slotToMatch.equals(legoSet.getSlot())) slotMatch = true;
			}
			if ((bleToMatch == null) && (legoSet.getBle() == null))
			{
				bleMatch = true;
			}
			else
			{
				if (bleToMatch.equals(legoSet.getBle())) bleMatch = true;
			}
			
			if (sectionMatch && cellMatch && dataIdMatch && slotMatch && bleMatch)
			{
				matchingLegoSet = legoSet;
				iset = linacLego.getLegoSetList().size();
			}
			iset = iset + 1;
		}
		return matchingLegoSet;
	}

}
