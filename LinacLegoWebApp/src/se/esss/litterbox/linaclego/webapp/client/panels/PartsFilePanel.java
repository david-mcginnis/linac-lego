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
package se.esss.litterbox.linaclego.webapp.client.panels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.webapp.client.LinacLegoWebApp;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutPanel;
import se.esss.litterbox.linaclego.webapp.client.tablayout.MyTabLayoutScrollPanel;
import se.esss.litterbox.linaclego.webapp.shared.CsvFile;

import com.google.gwt.user.client.ui.Grid;

public class PartsFilePanel extends VerticalPanel
{
	private String partsFileType = "";
	private Grid dataGrid;
	private Grid headerGrid;
	private int numHeaderRows = 1;
	private MyTabLayoutScrollPanel myTabLayoutScrollPanel;
	private int headerLineHeight = 25;
	private ScrollPanel dataGridScrollPane;
	private String sourceFileLink = "";
	private int numDataRows;
	private CsvFile partsFile;
	private boolean fileCompletelyLoaded = false;
	private boolean oddDataRow = false;
	private int oldSelectedRow = -1;
	boolean rowSelected = false;

	
	public boolean isFileCompletelyLoaded() {return fileCompletelyLoaded;}
	public int getNumHeaderRows() {return numHeaderRows;}
	public Grid getDataGrid() {return dataGrid;}
	public int getNumDataRows() {return numDataRows;}
	public CsvFile getPartsFile() {return partsFile;}
	public String getPartsFileType() {return partsFileType;}
	public MyTabLayoutScrollPanel getMyTabLayoutScrollPanel() {return myTabLayoutScrollPanel;}
	public void setFileCompletelyLoaded(boolean fileCompletelyLoaded) {this.fileCompletelyLoaded = fileCompletelyLoaded;}
	public String getSourceFileLink() {return sourceFileLink;}
	public LinacLegoWebApp getLinacLegoWebApp() {return getMyTabLayoutScrollPanel().getMyTabLayoutPanel().getLinacLegoWebApp();}
	public void setSourceFileLink(String sourceFileLink) {this.sourceFileLink = sourceFileLink;}

	public PartsFilePanel(MyTabLayoutPanel myTabLayoutPanel, String tabTitle, String csvFileType) 
	{
		super();
		myTabLayoutScrollPanel = new MyTabLayoutScrollPanel(myTabLayoutPanel);
		myTabLayoutPanel.add(myTabLayoutScrollPanel, tabTitle);
		myTabLayoutScrollPanel.add(this);
		
		this.partsFileType = csvFileType;
	}
	public void setPartsFile(CsvFile csvFile)
	{
		if (getWidgetCount() > 0) clear();
		this.partsFile = csvFile;
		Anchor sourceFileAnchor = new Anchor("Source File");
		sourceFileAnchor.addClickHandler(new DownLoadClickHandler(sourceFileLink));
		add(sourceFileAnchor);
		numDataRows = csvFile.numOfRows() - numHeaderRows;
		
		dataGrid = new Grid(numDataRows, csvFile.numOfCols());
		headerGrid = new Grid(numHeaderRows, csvFile.numOfCols() );
		dataGridScrollPane = new ScrollPanel();
		dataGridScrollPane.add(dataGrid);
		add(headerGrid);
		add(dataGridScrollPane);

		for (int irow = 0; irow < numHeaderRows; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				headerGrid.setText(irow, icol, csvFile.getLine(irow).getCell(icol));
			}
		}
		for (int irow = 0; irow < numDataRows; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				dataGrid.setText(irow, icol, csvFile.getLine(irow + numHeaderRows).getCell(icol));
			}
			if (oddDataRow) dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
			oddDataRow = !oddDataRow;
		}
		resizeMe(csvFile);
		Window.addResizeHandler(new MyResizeHandler(csvFile));
		for (int ih = 0; ih < numHeaderRows; ++ih)
			headerGrid.getRowFormatter().setStyleName(ih, "partsFilePanelHeader");
		headerGrid.setBorderWidth(0);
		headerGrid.setCellSpacing(0);
		headerGrid.setCellPadding(0);
		dataGrid.setBorderWidth(0);
		dataGrid.setCellSpacing(0);
		dataGrid.setCellPadding(0);
		dataGrid.addClickHandler(new DataGridClickHandler(this));
		
        getLinacLegoWebApp().getStatusTextArea().addStatus("Finished building " + partsFileType + " Spreadsheet.");
	}
	private void resizeMe(CsvFile csvFile)
	{
		int parentWidth = myTabLayoutScrollPanel.getPanelWidth();
		headerGrid.setWidth(parentWidth - 50 + "px");
		dataGrid.setWidth(parentWidth - 50  + "px");
		dataGridScrollPane.setHeight(myTabLayoutScrollPanel.getPanelHeight() - headerLineHeight * numHeaderRows - 20 + "px");
		dataGridScrollPane.setWidth(myTabLayoutScrollPanel.getPanelWidth()   - 20 + "px");
		headerGrid.setHeight(headerLineHeight * numHeaderRows + "px");

		int tableWidth = 0;
		for ( int icol = 0; icol < csvFile.numOfCols(); ++icol) tableWidth = tableWidth + csvFile.getColWidth(icol);
		for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
		{
			double colPer = 100.0 * ((double) csvFile.getColWidth(icol) ) / ((double) csvFile.getTableWidth());
			headerGrid.getColumnFormatter().setWidth(icol, NumberFormat.getFormat("0.0").format(colPer) + "%");
			dataGrid.getColumnFormatter().setWidth(icol, NumberFormat.getFormat("0.0").format(colPer) + "%");
		}
	}
	public class MyResizeHandler implements ResizeHandler
	{
		private CsvFile csvFile;
		MyResizeHandler(CsvFile csvFile)
		{
			this.csvFile = csvFile;
		}
		@Override
		public void onResize(ResizeEvent event) 
		{
			resizeMe(csvFile);
		}
	}
	class DownLoadClickHandler implements ClickHandler
	{
		String link;
		DownLoadClickHandler(String link)
		{
			this.link = link;
		}
		@Override
		public void onClick(ClickEvent event) {
			
//			Window.open(link, "_blank", "enabled");
			Window.open(link, "_blank", "");
		}
		
	}
	static class DataGridClickHandler implements ClickHandler
	{
		PartsFilePanel partsFilePanel;
		DataGridClickHandler(PartsFilePanel partsFilePanel)
		{
			this.partsFilePanel = partsFilePanel;
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			int irow = partsFilePanel.dataGrid.getCellForEvent(event).getRowIndex();
			int icol = partsFilePanel.dataGrid.getCellForEvent(event).getCellIndex();
			if (partsFilePanel.rowSelected)
			{
				if (partsFilePanel.oldSelectedRow == 2 * (partsFilePanel.oldSelectedRow / 2) ) 
				{
					partsFilePanel.dataGrid.getRowFormatter().setStyleName(partsFilePanel.oldSelectedRow, "csvFilePanelEvenRow");
				}
				else
				{
					partsFilePanel.dataGrid.getRowFormatter().setStyleName(partsFilePanel.oldSelectedRow, "csvFilePanelOddRow");
				}
				partsFilePanel.rowSelected = false;
			}
			if ((partsFilePanel.oldSelectedRow != irow) || (icol == 0 ))
			{
				partsFilePanel.dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelSelectedRow");
				partsFilePanel.rowSelected = true;
			}
			partsFilePanel.oldSelectedRow = irow;
		}
	}

}