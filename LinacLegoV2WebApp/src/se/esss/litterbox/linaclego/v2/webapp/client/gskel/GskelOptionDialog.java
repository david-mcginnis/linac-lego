package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GskelOptionDialog extends DialogBox
{
	private Label informationLabel = new Label();
	private Button choice1button = new Button("Choice 1");
	private Button choice2button = new Button("Choice 2");
	GskelOptionDialogInterface optionDialogInterface = null;
	VerticalPanel dialogVPanel;
	private Image logoImage = new Image("images/essLogo.png");

	public Image getLogoImage() {return logoImage;}

	public GskelOptionDialog()
	{
		super();
		setText("Choice");
		choice1button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				hide();
				optionDialogInterface.optionDialogInterfaceAction(choice1button.getText());
			}
		});
		choice2button.addClickHandler(new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				hide();
				optionDialogInterface.optionDialogInterfaceAction(choice2button.getText());
			}
		});
		dialogVPanel = new VerticalPanel();
		dialogVPanel.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);
		dialogVPanel.add(logoImage);
		dialogVPanel.add(informationLabel);

		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.add(choice1button);
		buttonPanel.add(choice2button);
		dialogVPanel.add(buttonPanel);

		setWidget(dialogVPanel);
	}
	public void setImageUrl(String logoImageUrl)
	{
		logoImage.setUrl(logoImageUrl);
		getLogoImage().setUrl(logoImageUrl);
	}
	public void setOption(String title, String message, String choice1ButtonText, String choice2ButtonText, GskelOptionDialogInterface optionDialogInterface)
	{
		setText(title);
		informationLabel.setText(message);
		choice1button.setText(choice1ButtonText);
		choice2button.setText(choice2ButtonText);
		this.optionDialogInterface = optionDialogInterface;
        center();
        show();
	}
	public void matchLocation(Widget widget)
	{
		setPopupPosition(widget.getAbsoluteLeft(),widget.getAbsoluteTop());
	}
	public void matchWidgetWidth(Widget widget)
	{
		if (widget.getOffsetWidth() > getOffsetWidth()) setWidth(Integer.toString(widget.getOffsetWidth()) + "px");
		dialogVPanel.setWidth("100%");
	}
	public void matchWidgetHeight(Widget widget)
	{
		if (widget.getOffsetHeight() > getOffsetHeight()) setHeight(Integer.toString(widget.getOffsetHeight()) + "px");
		dialogVPanel.setHeight("100%");
	}
	public void coverOverWidget(Widget widget)
	{
		 matchLocation(widget);
		 matchWidgetWidth(widget);
		 matchWidgetHeight(widget);
	}
	public interface GskelOptionDialogInterface 
	{
		void optionDialogInterfaceAction(String choiceButtonText);

	}

}
