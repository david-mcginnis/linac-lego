package se.esss.litterbox.linaclego.v2.webapp.client.contentpanels;


import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ScrollPanel;

import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelSetupApp;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.linaclego.v2.webapp.shared.CsvFile;

import com.google.gwt.user.client.ui.Grid;

public class CsvLinkFilePanel extends GskelVerticalPanel
{
	private String tabTitle = "";
	private Grid dataGrid;
	private Grid headerGrid;
	private int numHeaderRows;
	private int headerLineHeight = 25;
	private ScrollPanel dataGridScrollPane;
	private String sourceFileLink = "";
	private int numDataRows;
	private CsvFile csvFile;
	private boolean oddDataRow = false;
	private String headerStyle = null;
	private boolean[] islinkColumn;
	private String linacLegoDataLink = "";
	
	public int getNumHeaderRows() {return numHeaderRows;}
	public Grid getDataGrid() {return dataGrid;}
	public int getNumDataRows() {return numDataRows;}
	public CsvFile getCsvFile() {return csvFile;}
	public String getTabTitle() {return tabTitle;}
	public String getSourceFileLink() {return sourceFileLink;}
	public String getHeaderStyle() {return headerStyle;}

	public void setSourceFileLink(String sourceFileLink) {this.sourceFileLink = sourceFileLink;}
	public void setLinacLegoDataLink(String linacLegoDataLink) {this.linacLegoDataLink = linacLegoDataLink;}

	public CsvLinkFilePanel(String tabTitle, int numHeaderRows, String headerStyle, GskelSetupApp setupApp) 
	{
		super(tabTitle, headerStyle, setupApp);
		
		this.numHeaderRows = numHeaderRows;
		this.tabTitle = tabTitle;
		this.headerStyle = headerStyle;
	}
	public void setCsvFile(CsvFile csvFile)
	{
		if (getWidgetCount() > 0) clear();
		this.csvFile = csvFile;
		Anchor sourceFileAnchor = new Anchor("Source File");
		sourceFileAnchor.addClickHandler(new DownLoadClickHandler(sourceFileLink));
		add(sourceFileAnchor);
		numDataRows = csvFile.numOfRows() - numHeaderRows;
		int numDataCols = 0;
		islinkColumn = new boolean[csvFile.numOfCols()];
		for (int icell = 0; icell < csvFile.numOfCols(); ++icell)
		{
			if (csvFile.getLine(0).getCell(icell).indexOf("#") == 0)
			{
				islinkColumn[icell] = true;
			}
			else
			{
				islinkColumn[icell] = false;
				numDataCols = numDataCols + 1;
			}
		}
		
		dataGrid = new Grid(numDataRows, numDataCols);
		headerGrid = new Grid(numHeaderRows, numDataCols);
		dataGridScrollPane = new ScrollPanel();
		dataGridScrollPane.add(dataGrid);
		add(headerGrid);
		add(dataGridScrollPane);

		for (int irow = 0; irow < numHeaderRows; ++irow)
		{
			int icell = 0;
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				if (!islinkColumn[icol])
				{
					headerGrid.setText(irow, icell, csvFile.getLine(irow).getCell(icol));
					icell = icell + 1;
				}
			}
		}
		for (int irow = 0; irow < numDataRows; ++irow)
		{
			int icell = 0;
			for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
			{
				if (!islinkColumn[icol])
				{
					boolean anchorMade = false;
					if (icol < (csvFile.numOfCols() - 1))
					{
						if (islinkColumn[icol + 1])
						{
							if (csvFile.getLine(irow + numHeaderRows).getCell(icol + 1).indexOf("null") < 0)
							{
								Anchor cellLinkAnchor = new Anchor(csvFile.getLine(irow + numHeaderRows).getCell(icol));
								cellLinkAnchor.addClickHandler(new CellLinkAnchorClickHandler(linacLegoDataLink + "/" + csvFile.getLine(irow + numHeaderRows).getCell(icol + 1).substring(1)));
								dataGrid.setWidget(irow, icell, cellLinkAnchor);
								anchorMade = true;
							}
						}
					}
					if (!anchorMade) dataGrid.setText(irow, icell, csvFile.getLine(irow + numHeaderRows).getCell(icol));
					icell = icell + 1;
				}
			}
			if (oddDataRow) dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
			oddDataRow = !oddDataRow;
		}
		resizeMe(csvFile);
		Window.addResizeHandler(new MyResizeHandler(csvFile));
		for (int ih = 0; ih < numHeaderRows; ++ih)
			headerGrid.getRowFormatter().setStyleName(ih, headerStyle);
		headerGrid.setBorderWidth(0);
		headerGrid.setCellSpacing(0);
		headerGrid.setCellPadding(0);
		dataGrid.setBorderWidth(0);
		dataGrid.setCellSpacing(0);
		dataGrid.setCellPadding(0);
		dataGrid.addClickHandler(new DataGridClickHandler(dataGrid));

		getStatusTextArea().addStatus("Finished building " + tabTitle + " Spreadsheet.");
	}
	private void resizeMe(CsvFile csvFile)
	{
		int parentWidth = getGskelTabLayoutScrollPanel().getPanelWidth();
		headerGrid.setWidth(parentWidth - 50 + "px");
		dataGrid.setWidth(parentWidth - 50  + "px");
		dataGridScrollPane.setHeight(getGskelTabLayoutScrollPanel().getPanelHeight() - headerLineHeight * numHeaderRows - 20 + "px");
		dataGridScrollPane.setWidth(getGskelTabLayoutScrollPanel().getPanelWidth()   - 20 + "px");
		headerGrid.setHeight(headerLineHeight * numHeaderRows + "px");

		int icell = 0;
		for ( int icol = 0; icol < csvFile.numOfCols(); ++icol)
		{
			if (!islinkColumn[icol])
			{
				double colPer = 100.0 * ((double) csvFile.getColWidth(icol) ) / ((double) csvFile.getTableWidth());
				headerGrid.getColumnFormatter().setWidth(icell, NumberFormat.getFormat("0.0").format(colPer) + "%");
				dataGrid.getColumnFormatter().setWidth(icell, NumberFormat.getFormat("0.0").format(colPer) + "%");
				icell = icell + 1;
			}
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
		Grid dataGrid;
		DataGridClickHandler(Grid dataGrid)
		{
			this.dataGrid = dataGrid;
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			int irow = dataGrid.getCellForEvent(event).getRowIndex();
			String styleName = dataGrid.getRowFormatter().getStyleName(irow);
			if (styleName.equals("csvFilePanelSelectedRow"))
			{
				if (irow == 2 * (irow / 2) ) 
				{
					dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelEvenRow");
				}
				else
				{
					dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelOddRow");
				}
			}
			else
			{
				dataGrid.getRowFormatter().setStyleName(irow, "csvFilePanelSelectedRow");
			}
			
		}
		
	}
	@Override
	public void tabLayoutPanelInterfaceAction(String message) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) {
		// TODO Auto-generated method stub
		
	}
	class CellLinkAnchorClickHandler implements ClickHandler
	{
		private String link = "";
		CellLinkAnchorClickHandler(String link)
		{
			this.link = link;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
//			Window.open(link, "_blank", "");
			getSetupApp().getFrameDialog().setFrameUrl(link);
			getSetupApp().getFrameDialog().setMessage(link, "", true);
		}
		
	}
}
