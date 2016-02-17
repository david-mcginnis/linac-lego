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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.webapp.client.LinacLegoWebApp;
import se.esss.litterbox.linaclego.webapp.shared.CsvFile;

import com.google.gwt.user.client.ui.Grid;

public class DrawingListPanel extends VerticalPanel
{
	private Grid dataGrid;
	private Grid headerGrid;
	private int numHeaderRows = 1;
	private int headerLineHeight = 25;
	private ScrollPanel dataGridScrollPane;
	private String sourceFileLink = "";
	private int numDataRows;
	private CsvFile csvFile;
	private boolean oddDataRow = false;
	boolean rowSelected = false;
	LinacLegoWebApp linacLegoWebApp;

	
	public int getNumHeaderRows() {return numHeaderRows;}
	public Grid getDataGrid() {return dataGrid;}
	public int getNumDataRows() {return numDataRows;}
	public CsvFile getCsvFile() {return csvFile;}
	public String getSourceFileLink() {return sourceFileLink;}
	public void setSourceFileLink(String sourceFileLink) {this.sourceFileLink = sourceFileLink;}

	public DrawingListPanel(LinacLegoWebApp linacLegoWebApp) 
	{
		super();
		this.linacLegoWebApp = linacLegoWebApp;
	}
	public void setDrawingListFile(CsvFile csvFile)
	{
		if (getWidgetCount() > 0) clear();
		this.csvFile = csvFile;
		numDataRows = csvFile.numOfRows() - numHeaderRows;
		
		dataGrid = new Grid(numDataRows, csvFile.numOfCols() - 1);
		headerGrid = new Grid(numHeaderRows, csvFile.numOfCols()  - 1);
		dataGridScrollPane = new ScrollPanel();
		dataGridScrollPane.add(dataGrid);
		add(headerGrid);
		add(dataGridScrollPane);

		for (int irow = 0; irow < numHeaderRows; ++irow)
		{
			for ( int icol = 0; icol < csvFile.numOfCols() - 1; ++icol)
			{
				headerGrid.setText(irow, icol, csvFile.getLine(irow).getCell(icol));
			}
		}
		for (int irow = 0; irow < numDataRows; ++irow)
		{
			dataGrid.setText(irow, 0, csvFile.getLine(irow + numHeaderRows).getCell(0));
			Anchor sourceFileAnchor = new Anchor(csvFile.getLine(irow + numHeaderRows).getCell(1));
			sourceFileAnchor.addClickHandler(new DownLoadClickHandler(csvFile.getLine(irow + numHeaderRows).getCell(2)));
			dataGrid.setWidget(irow, 1, sourceFileAnchor);
			
			if (oddDataRow) dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
			oddDataRow = !oddDataRow;
		}
		for (int ih = 0; ih < numHeaderRows; ++ih)
			headerGrid.getRowFormatter().setStyleName(ih, "partsFilePanelHeader");
		headerGrid.setBorderWidth(0);
		headerGrid.setCellSpacing(0);
		headerGrid.setCellPadding(0);
		dataGrid.setBorderWidth(0);
		dataGrid.setCellSpacing(0);
		dataGrid.setCellPadding(0);
		
		headerGrid.setWidth("400px");
		dataGrid.setWidth("400px");
		dataGridScrollPane.setWidth("420px");
		headerGrid.setHeight(headerLineHeight * numHeaderRows + "px");
		headerGrid.getColumnFormatter().setWidth(0, "30%");
		headerGrid.getColumnFormatter().setWidth(1, "65%");
		dataGrid.getColumnFormatter().setWidth(0, "30%");
		dataGrid.getColumnFormatter().setWidth(1, "65%");
		
//		dataGridScrollPane.setHeight("400px");


		linacLegoWebApp.getStatusTextArea().addStatus("Finished building Drawing List Spreadsheet.");
		linacLegoWebApp.getInfoPanel().setDrawingListCaptionPanelWidget(this);
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

}