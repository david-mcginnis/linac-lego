package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;


public class TestGskelVerticalPanel extends GskelVerticalPanel
{
	Button testButton = new Button("Test");

	public TestGskelVerticalPanel(String tabTitle, GskelSetupApp setupApp) 
	{
		super(tabTitle, setupApp);
		add(testButton);
		testButton.addClickHandler(new TestButtonClickHandler(this));
	}

	@Override
	public void tabLayoutPanelInterfaceAction(String message) 
	{
		getStatusTextArea().addStatus("Tab " + this.getTabIndex() + " " + message);
		getMessageDialog().setMessage("Tab", this.getTabIndex() + " " + message, true);
	}
	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) 
	{
		getStatusTextArea().addStatus("You chose " + choiceButtonText);
		getMessageDialog().setMessage("Choice", "You chose " + choiceButtonText, true);
	}

	static class TestButtonClickHandler implements ClickHandler
	{
		TestGskelVerticalPanel testGskelVerticalPanel;
		TestButtonClickHandler(TestGskelVerticalPanel testGskelVerticalPanel)
		{
			this.testGskelVerticalPanel = testGskelVerticalPanel;
		}
		@Override
		public void onClick(ClickEvent event) 
		{
			String[] debugResponse = {"Yes", "No"};
			testGskelVerticalPanel.testButton.setEnabled(false);
			testGskelVerticalPanel.getEntryPointAppService().gskelServerTest("Hi There", testGskelVerticalPanel.isDebug(), debugResponse, new GskelServerTestAsyncCallback(testGskelVerticalPanel));
		}
	}
	public static class GskelServerTestAsyncCallback implements AsyncCallback<String[]>
	{
		TestGskelVerticalPanel testGskelVerticalPanel;
		GskelServerTestAsyncCallback(TestGskelVerticalPanel testGskelVerticalPanel)
		{
			this.testGskelVerticalPanel = testGskelVerticalPanel;
		}
		@Override
		public void onFailure(Throwable caught) 
		{
			testGskelVerticalPanel.testButton.setEnabled(true);
			testGskelVerticalPanel.getMessageDialog().setMessage("Error on Call back", caught.getMessage(), true);
			
		}
		@Override
		public void onSuccess(String[] result) 
		{
			testGskelVerticalPanel.testButton.setEnabled(true);
			testGskelVerticalPanel.getOptionDialog().setOption("Server Call back ", "Result", result[0], result[1], testGskelVerticalPanel);
			
		}
	}
}
