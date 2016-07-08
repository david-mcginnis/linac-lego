package se.esss.litterbox.linaclego.v2.webapp.client.contentpanels;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.VerticalPanel;

public class PbsInfoDialogBox extends DialogBox
{
	public PbsInfoDialogBox(String title, Grid attributeGrid, Grid dataGrid)
	{
		setText(title);
		setAnimationEnabled(true);
		Button closeButton = new Button("Close");
		// We can set the id of a widget by accessing its Element
		closeButton.getElement().setId("closeButton");
		closeButton.addClickHandler(new CloseButtonClickHandler(this));
		VerticalPanel dialogVPanelWrapper = new VerticalPanel();
		dialogVPanelWrapper.add(attributeGrid);
		if (dataGrid != null) dialogVPanelWrapper.add(dataGrid);
		dialogVPanelWrapper.add(closeButton);
		setWidget(dialogVPanelWrapper);
		hide();
		
	}
	static class CloseButtonClickHandler implements ClickHandler
	{
		private PbsInfoDialogBox infoDialogBox;
		CloseButtonClickHandler(PbsInfoDialogBox infoDialogBox)
		{
			this.infoDialogBox = infoDialogBox;
		}

		@Override
		public void onClick(ClickEvent event) 
		{
			infoDialogBox.hide();
			
		}
		
	}
}
