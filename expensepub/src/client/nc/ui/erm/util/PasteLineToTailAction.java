package nc.ui.erm.util;


import java.awt.event.ActionEvent;

import javax.swing.KeyStroke;

import nc.bs.logging.Logger;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.vo.pub.BusinessException;
import nc.vo.uif2.LoginContext;

@SuppressWarnings("serial")
public class PasteLineToTailAction extends AbstractBillTableLineAction {
	private String pkField;
	private LoginContext context;
	public PasteLineToTailAction(BillScrollPane bsp, LoginContext context,String pkField) {
		super(bsp);
		this.pkField = pkField;
		this.context = context;
	}

	@Override
	public void doAction(ActionEvent e) {
		try {
			LineClearPKUtil.doPasteLineToTail(getBillScrollPane(), pkField);
		} catch (BusinessException e1) {
			ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011000_0","02011000-0017")/*@res "´íÎó"*/,e1.getMessage(), context);
			Logger.error(e1.getMessage(), e1);
		}
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public KeyStroke getKeyStroke() {
		return null;
	}

	@Override
	public String getName() {
		return nc.ui.ml.NCLangRes.getInstance().getStrByID("_Bill", "UPP_Bill-000011")/*
	     * @res
	     * "Õ³ÌùÐÐµ½±íÎ²"
	     */;
	}
}