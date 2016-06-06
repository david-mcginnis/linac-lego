package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import java.util.ArrayList;
import java.util.Date;

import com.google.gwt.user.client.ui.TextArea;

public class GskelStatusTextArea extends TextArea
{
	private int panelWidth = 285;
	private int panelHeight = 130;
	private ArrayList<String> statusList = new ArrayList<String>();
	private int maxBufferSize = 100;
	public int getPanelWidth() {return panelWidth;}
	public int getPanelHeight() {return panelHeight;}

	public GskelStatusTextArea(int panelWidth, int panelHeight)
	{
		super();
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void setSize(int panelWidth, int panelHeight)
	{
		this.panelWidth = panelWidth;
		this.panelHeight = panelHeight;
		setSize(panelWidth + "px", panelHeight+ "px");
	}
	public void addStatus(String status)
	{
		statusList.add(0, new Date().toString() + ": " + status);
		int statusListSize = statusList.size();
		while (statusListSize > maxBufferSize)
		{
			statusList.remove(statusListSize - 1);
			statusListSize = statusList.size();
		}
		String text = "";
		for (int ii = 0; ii < statusListSize; ++ii)
		{
			text = text + statusList.get(ii) + "\n";
		}
		setText(text);
	}
	public int getBufferMaxSize() {return maxBufferSize;}
	public void setMaxBufferSize(int maxBufferSize) {this.maxBufferSize = maxBufferSize;}
}
