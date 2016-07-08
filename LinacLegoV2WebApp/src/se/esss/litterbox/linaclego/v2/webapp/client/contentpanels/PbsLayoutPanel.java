package se.esss.litterbox.linaclego.v2.webapp.client.contentpanels;

import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.VerticalPanel;

import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelSetupApp;
import se.esss.litterbox.linaclego.v2.webapp.client.gskel.GskelVerticalPanel;
import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;


public class PbsLayoutPanel extends GskelVerticalPanel
{
	private String treeType = "";
	HtmlTextTree textTree;
	PbsLevelPanel pbsLevelPanel;
	
	public String getTreeType() {return treeType;}


	public PbsLayoutPanel(String tabTitle, String tabStyle, String treeType,  GskelSetupApp setupApp) 
	{
		super(tabTitle, tabStyle, setupApp);
		this.treeType = treeType;
		setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);

	}
	public void addTree(HtmlTextTree textTree)
	{
		if (getWidgetCount() > 0) clear();
		getStatusTextArea().addStatus("Finished building " + treeType + " layout view.");
		pbsLevelPanel = new PbsLevelPanel(0, textTree.getTextTreeArrayList().get(0), true, null, getGskelTabLayoutScrollPanel());
		VerticalPanel vertWrapperPanel = new VerticalPanel();
		vertWrapperPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		
		vertWrapperPanel.add(new InlineHTML(textTree.getInlineHtmlString(false, false)));
		vertWrapperPanel.add(pbsLevelPanel);
		add(vertWrapperPanel);
		//	      pbsLevelPanel.focusPanel.setFocus(false);
		pbsLevelPanel.expandPanel();
	}


	@Override
	public void tabLayoutPanelInterfaceAction(String message) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) {
		// TODO Auto-generated method stub
		
	}

}
