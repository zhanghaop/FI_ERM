package nc.ui.er.djlx;

import java.awt.Component;

import nc.bs.framework.common.NCLocator;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.ui.dbcache.DBCacheFacade;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.ButtonObject;
import nc.ui.pub.transtype.EditorContext;
import nc.ui.pub.transtype.ITranstypeEditor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

public class BxITranstypeEditorImp implements ITranstypeEditor {
	private DjlxCardPanel template = null;
	final static int TYPE_BROWSE = 0;
	final static int TYPE_NEW = 1;
	final static int TYPE_EDIT = 2;
	final static int TYPE_CLEAR = 3;
	private int lasttype = TYPE_BROWSE;

	// private DjlxPanel djlxpanel = null;

	public void doAction(EditorContext context) throws BusinessException {
		lasttype = context.getEventtype();
		// setButtonEnable(false);
		switch (context.getEventtype()) {
		case TYPE_BROWSE:
			seteditable(false);
			BillTypeVO vo = queryvoBycontext(context);
			if (vo != null) {
				((DjLXVO) vo.getParentVO()).setFcbz(context.getTranstype().getIsLock());
				getcardpanel().setBillValueVO(vo);
			}
			setButtonEnable(true);
			break;
		case TYPE_EDIT:
			vo = queryvoBycontext(context);
			if (vo != null) {
				getcardpanel().setBillValueVO(vo);
			}
			seteditable(true);
			break;
		case TYPE_NEW:
			getcardpanel().setNull();
			seteditable(true);
			if (context.getTranstype() != null) {
				getcardpanel().setBillValueVO(converttoBilltype(context.getTranstype()));
			} else {
				getcardpanel().setBillValueVO(newBilltypevo());
			}
			break;
		case TYPE_CLEAR:
			seteditable(false);
			getcardpanel().setNull();
			break;
		default:
			seteditable(false);
			break;
		}
		DBCacheFacade.refreshTable(DjLXVO.getDefaultTableName());
	}

	public void doButtonAction(ButtonObject bo) throws BusinessException {
		// getDjlxpanel().onButtonClicked(bo);
	}

	public Component getEditorPane() {
		return getcardpanel();
	}

	public ButtonObject[] getExtButtonObjects() {
		// if
		// (BXUiUtil.getDefaultOrgUnit().equalsIgnoreCase(BXConstans.GROUP_CODE))
		// {
		//			
		// return new ButtonObject[] {((DefaultListenerController)
		// getDjlxpanel().getListenerController()).getBtnMap().get(
		// "sysinit_distribute")};
		// }

		return null;
	}

	public Object getTransTypeExtObj(EditorContext context) throws BusinessException {
		BillTypeVO vo = null;
		DjLXVO head = null;
		BilltypeVO transtype = context.getTranstype();
		String pk_group = ErUiUtil.getPK_group();
		;
		switch (lasttype) {
		case TYPE_NEW:
			vo = queryvoBycontext(context);
			if (vo != null) {
				getcardpanel().getBillCardPanelDj().getHeadItem("djlxoid").setValue(vo.getDjlxoid());
			}
			getcardpanel().getBillCardPanelDj().getHeadItem("djlxbm").setValue(transtype.getPk_billtypecode());
			getcardpanel().getBillCardPanelDj().getHeadItem("djlxmc").setValue(transtype.getBilltypename());
			getcardpanel().getBillCardPanelDj().getHeadItem("djlxjc").setValue(transtype.getBilltypename());
			getcardpanel().getBillCardPanelDj().getHeadItem("djlxmc_remark").setValue(transtype.getBilltypename());
			getcardpanel().getBillCardPanelDj().getHeadItem("djlxmc_remark").setValue(transtype.getBilltypename());
			// getcardpanel().getBillCardPanelDj()
			// .getHeadItem("fcbz").setValue(transtype.getIsLock());
			getcardpanel().getBillCardPanelDj().dataNotNullValidate();
			vo = getcardpanel().getBilltypevo();
			head = (DjLXVO) vo.getParentVO();
			head.setDjlxbm(transtype.getPk_billtypecode());
			head.setDjlxmc(transtype.getBilltypename());
			head.setDjlxjc(transtype.getBilltypename());
			head.setDjlxmc_remark(transtype.getBilltypename());
			head.setDjlxjc_remark(transtype.getBilltypename());
			// head.setPk_group("@@@@");
			head.setPk_group(pk_group);
			head.setFcbz(transtype.getIsLock());
			break;
		case TYPE_EDIT:
			getcardpanel().getBillCardPanelDj().dataNotNullValidate();
			vo = getcardpanel().getBilltypevo();
			head = (DjLXVO) vo.getParentVO();
			head.setDjlxbm(transtype.getPk_billtypecode());
			head.setDjlxmc(transtype.getBilltypename());
			head.setDjlxjc(transtype.getBilltypename());
			head.setDjlxmc_remark(transtype.getBilltypename());
			head.setDjlxjc_remark(transtype.getBilltypename());
			// head.setPk_group("@@@@");
			head.setPk_group(pk_group);
			head.setFcbz(transtype.getIsLock());
			break;
		default:
			vo = getcardpanel().getBilltypevo();
			head = (DjLXVO) vo.getParentVO();
			head.setDjlxbm(transtype.getPk_billtypecode());
			head.setDjlxmc(transtype.getBilltypename());
			head.setDjlxjc(transtype.getBilltypename());
			head.setDjlxmc_remark(transtype.getBilltypename());
			head.setDjlxjc_remark(transtype.getBilltypename());
			// head.setPk_group("@@@@");
			head.setPk_group(pk_group);
			head.setFcbz(transtype.getIsLock());
		}
		return vo;
	}

	private BillTypeVO newBilltypevo() {
		BillTypeVO vo = new BillTypeVO();
		DjLXVO head = new DjLXVO();
		vo.setParentVO(head);
		head.setDwbm(BXConstans.GLOBAL_CODE);
		head.setDjdl(getDjdl());
		initdefalutvalue(head);
		return vo;
	}

	protected String getDjdl() {
		return BXConstans.BX_DJDL;
	}

	public String getNodecode() {
		return "20110005";
	}

	protected void updateitemenable() {
		// TODO：需要修改，根据不同的单据类型设置可见数据以及可用数据。
	}

	private void seteditable(boolean b) {
		getcardpanel().setEnable(b);
		updateitemenable();
	}

	protected void initdefalutvalue(DjLXVO head) {
		head.setUsesystem(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000369")/*
																										 * @res
																										 * "报销管理"
																										 */);
	}

	private BillTypeVO queryvoBycontext(EditorContext context) {
		try {
			BillTypeVO[] vos = getdjlxiterface().queryBillTypeByBillTypeCode(context.getTranstype().getPk_billtypecode(),
					context.getTranstype().getPk_group());
			if (vos != null && vos.length > 0) {
				return vos[0];
			}
		} catch (Exception e) {
		}
		return null;
	}

	protected DjlxCardPanel getcardpanel() {
		if (template == null) {
			template = new DjlxCardPanel();
			// template = getDjlxpanel().getDjlxCardPanel();// new
			// DjlxCardPanel(getNodecode());
			inittemplate();
			getcardpanel().getBillCardPanelDj().setBillData(getcardpanel().getBillCardPanelDj().getBillData());
		}
		return template;
	}

	protected void inittemplate() {
		getcardpanel().getBillCardPanelDj().getHeadItem("djlxmc").setShow(false);
		getcardpanel().getBillCardPanelDj().getHeadItem("djlxjc").setShow(false);
		getcardpanel().getBillCardPanelDj().getHeadItem("djlxbm").setShow(false);
	}

	private IArapBillTypePrivate getdjlxiterface() {
		return NCLocator.getInstance().lookup(IArapBillTypePrivate.class);
	}

	private void setButtonEnable(boolean b) {
		if (getExtButtonObjects() != null) {
			for (ButtonObject btn : getExtButtonObjects()) {
				if (btn != null) {
					btn.setEnabled(b);
				}
			}
		}
	}

	// private String getSyscode() {
	// if (getNodecode().startsWith("2011")) {
	// return BilltypeSystemenum.ER.getSyscode();
	// }
	// return null;
	// }
	// private DjlxPanel getDjlxpanel() {
	// if (djlxpanel == null) {
	// try {
	// djlxpanel = (DjlxPanel) Class.forName(getDjlxPanelClassName())
	// .newInstance();
	// djlxpanel.setEditor(this);
	// djlxpanel.postInit();
	// } catch (InstantiationException e) {
	// Logger.error(e.getMessage(), e);
	// } catch (IllegalAccessException e) {
	// Logger.error(e.getMessage(), e);
	// } catch (ClassNotFoundException e) {
	// Logger.error(e.getMessage(), e);
	// }
	// }
	// return djlxpanel;
	// }
	private BillTypeVO converttoBilltype(BilltypeVO transtype) {
		BillTypeVO vo = new BillTypeVO();
		DjLXVO head = new DjLXVO();
		vo.setParentVO(head);
		// head.setDwbm(BXConstans.GROUP_CODE);
		head.setDwbm(BXConstans.GLOBAL_CODE);
		head.setDjdl(getDjdl());
		head.setDjlxbm(transtype.getPk_billtypecode());
		head.setDjlxmc(transtype.getBilltypename());
		head.setDjlxjc(transtype.getBilltypename());
		head.setDjlxmc_remark(transtype.getBilltypename());
		head.setDjlxjc_remark(transtype.getBilltypename());
		initdefalutvalue(head);
		return vo;
	}

	protected String getDjlxPanelClassName() throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		return "nc.ui.er.djlx.DjlxCardPanel";
	}
}