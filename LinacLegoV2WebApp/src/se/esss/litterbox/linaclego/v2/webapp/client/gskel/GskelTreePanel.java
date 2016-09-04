package se.esss.litterbox.linaclego.v2.webapp.client.gskel;

import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import se.esss.litterbox.linaclego.v2.webapp.shared.HtmlTextTree;

public class GskelTreePanel extends GskelVerticalPanel
{
    private Tree rootTree;
    private GskelTreeItem rootTreeItem;
	private HtmlTextTree textTree;
	
	public HtmlTextTree getTextTree() {return textTree;}
	public GskelTreeItem getRootTreeItem() {return rootTreeItem;}

	public GskelTreePanel(String tabTitle, String tabStyle, GskelSetupApp setupApp) 
	{
		super(tabTitle, tabStyle, setupApp);
		rootTree = null;
	}
	public void addTree(HtmlTextTree textTree)
	{
		if (getWidgetCount() > 0) clear();
		this.textTree = textTree;
		rootTree = new Tree();
		rootTree.addSelectionHandler(new TreeSelectionHandler());
		rootTreeItem = new GskelTreeItem(textTree,  32, 32, getSetupApp());
		rootTree.addItem(rootTreeItem);
		add(rootTree);
		rootTreeItem.expand();
		int nchild = rootTreeItem.getGskelTreeItemChildrenList().size();
		if (nchild > 0)
		{
			for (int ichild = 0; ichild < nchild; ++ichild)
			{
				rootTreeItem.getGskelTreeItemChildrenList().get(ichild).expand();
			}
		}
//Expand first two levels
		rootTreeItem.setState(true);
		nchild = rootTreeItem.getChildCount();
		if (nchild > 0)
		{
			for (int ichild = 0; ichild < nchild; ++ichild)
			{
				rootTreeItem.getChild(ichild).setState(true);
			}
		}
	}
	@Override
	public void tabLayoutPanelInterfaceAction(String message) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void optionDialogInterfaceAction(String choiceButtonText) 
	{
		// TODO Auto-generated method stub
		
	}
	class TreeSelectionHandler implements SelectionHandler<TreeItem>
	{
		@Override
		public void onSelection(SelectionEvent<TreeItem> event) 
		{
			GskelTreeItem gskelTreeItem = (GskelTreeItem) event.getSelectedItem();
			gskelTreeItem.expand();
		}
	}
}
