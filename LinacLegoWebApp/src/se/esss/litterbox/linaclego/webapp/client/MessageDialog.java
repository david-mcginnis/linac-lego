package se.esss.litterbox.linaclego.webapp.client;


import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MessageDialog extends DialogBox
{
	private VerticalPanel messagePanel;
	private Image messageImage;
	private Label messageLabel;
	private boolean isShowing = false;

	public VerticalPanel getMessagePanel() {return messagePanel;}
	public Image getMessageImage() {return messageImage;}
	public Label getMessageLabel() {return messageLabel;}
	public boolean  getIsShowing() {return isShowing;}

	public MessageDialog(String title)
	{
		setText(title);
		setAnimationEnabled(true);
		messagePanel = new VerticalPanel();
		messageImage = new Image("/images/Scarecrow.jpg");
		messageLabel = new Label("Loading data from the server...");
		messagePanel.add(messageImage);
		messagePanel.add(messageLabel);
		setWidget(messagePanel);
		hide();
		
	}
	public void showMe(boolean show)
	{
		isShowing = show;
		if (show)
		{
			show();
			center();
		}
		if (!show) hide();
	}

}
