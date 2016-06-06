package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class GskelMessageDialog extends DialogBox
{
	private Label informationtext = new Label();
	private double defaultWidthEM = 25.0;
	private double defaultHeightEM = 2.0;
	private Button okButton = new Button("Ok");
	private Image logoImage = new Image("images/essLogo.png");

	public Image getLogoImage() {return logoImage;}

	public GskelMessageDialog()
	{
		super();
		setText("Information");
//		setAnimationEnabled(true);
		okButton.addClickHandler(new ClickHandler() {public void onClick(ClickEvent event) {hide();}});
		VerticalPanel dialogVPanel = new VerticalPanel();
//		dialogVPanel.addStyleName("dialogVPanel");
		setSize(defaultWidthEM, defaultHeightEM);
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(logoImage);
		dialogVPanel.add(informationtext);
		dialogVPanel.add(okButton);
		setWidget(dialogVPanel);

	}
	public void setImageUrl(String logoImageUrl)
	{
		logoImage.setUrl(logoImageUrl);
		getLogoImage().setUrl(logoImageUrl);
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
        center();
        show();
	}
	public void setMessage(String title, String message, boolean showOkButton, double widthEM, double heightEM)
	{
		okButton.setVisible(showOkButton);
		setText(title);
		informationtext.setText(message);
		setSize(widthEM, heightEM);
        center();
        show();
	}


}
