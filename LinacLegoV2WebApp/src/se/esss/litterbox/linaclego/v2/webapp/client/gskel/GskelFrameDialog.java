package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GskelFrameDialog extends DialogBox
{
	private Label informationtext = new Label();
	private double defaultWidthEM = 25.0;
	private double defaultHeightEM = 2.0;
	private Button okButton = new Button("Ok");
	private Frame frame;
	private GskelSetupApp setupApp;

	public GskelFrameDialog(GskelSetupApp setupApp)
	{
		super();
		this.setupApp = setupApp;
		setText("Information");
//		setAnimationEnabled(true);
		okButton.addClickHandler(new ClickHandler() {public void onClick(ClickEvent event) {hide();}});
		VerticalPanel dialogVPanel = new VerticalPanel();
//		dialogVPanel.addStyleName("dialogVPanel");
		setSize(defaultWidthEM, defaultHeightEM);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		frame = new Frame();
		dialogVPanel.add(frame);
		dialogVPanel.add(informationtext);
		dialogVPanel.add(okButton);
		setWidget(dialogVPanel);

	}
	public void setFrameUrl(String frameUrl)
	{
		frame.setUrl(frameUrl);
	}
	public void setSize(double widthEM, double heightEM)
	{
		informationtext.setSize(Double.toString(widthEM) + "em", Double.toString(heightEM) + "em");
	}
	public void setMessage(String title, String message, boolean showOkButton)
	{
		okButton.setVisible(showOkButton);
		setText(title);
		informationtext.setText(message);
		setPixelSize(setupApp.getGskelTabLayoutPanelWidth(), setupApp.getGskelTabLayoutPanelHeight() - 130);
        frame.setPixelSize(setupApp.getGskelTabLayoutPanelWidth() - 20, setupApp.getGskelTabLayoutPanelHeight() - 150);
		setPopupPosition(0, 30);
        show();
	}


}
