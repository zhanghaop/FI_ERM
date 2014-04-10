package nc.ui.erm.util;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.KeyStroke;

import nc.sfbase.client.ClientToolKit;
import nc.ui.pub.bill.BillScrollPane;

@SuppressWarnings("serial")
public abstract class AbstractBillTableLineAction extends AbstractAction {

	private BillScrollPane bsp = null;
	
	public AbstractBillTableLineAction(BillScrollPane bsp) {
		super();
		
		this.bsp = bsp;
		
		putValue(Action.NAME, getName());
		Icon icon = ClientToolKit.loadImageIcon(getIconPath());
		putValue(Action.SMALL_ICON, icon);
		putValue(Action.SHORT_DESCRIPTION, getName());
		putValue(Action.ACCELERATOR_KEY,getKeyStroke());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(bsp != null){
			if (bsp.getTable().getCellEditor() != null)
				bsp.getTable().getCellEditor().stopCellEditing();
			doAction(e);
		}

	}
	
	public abstract void doAction(ActionEvent e);
	
	public abstract String getName();
	
	public abstract String getIconPath();
	
	public abstract KeyStroke getKeyStroke();
	
	public BillScrollPane getBillScrollPane(){
		return bsp;
	}
	
}
