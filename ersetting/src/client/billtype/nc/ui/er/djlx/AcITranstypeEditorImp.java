package nc.ui.er.djlx;

import java.awt.Component;

import nc.bs.erm.common.ErmBillConst;
import nc.bs.framework.common.NCLocator;
import nc.itf.er.prv.IArapBillTypePrivate;
import nc.ui.dbcache.DBCacheFacade;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.transtype.AbstractTranstypeEditor;
import nc.ui.pub.transtype.EditorContext;
import nc.ui.pub.transtype.ITranstypeEditor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.billtype.BilltypeVO;

/**
 * 预提单 交易类型扩展面板
 * 注册节点：单据类型管理节点，单据类型字表 ：类型扩展编辑器类	nc.ui.er.djlx.AcITranstypeEditorImp	
 * @author chenshuaia
 *
 */
public class AcITranstypeEditorImp extends AbstractTranstypeEditor implements ITranstypeEditor {
	private BillCardPanel csBillCard;

	@Override
	protected void clearEditorPane() {
		getCsBillCard().setBillValueVO(new BillTypeVO());
		getCsBillCard().setEnabled(false);
	}

	@Override
	protected void newTranstypeExtProp() throws BusinessException {
		setEditable(true);
		getCsBillCard().setBillValueVO(new BillTypeVO());

	}

	/**
	 * 根据context信息，更新界面状态
	 */
	public void doAction(EditorContext ec) throws BusinessException {
		switch (ec.getEventtype()) {
		case EditorContext.TYPE_CANCEL:
			qryTransobj(ec);
			break;
		case EditorContext.TYPE_BROWSE:
			qryTransobj(ec);
			break;
		case EditorContext.TYPE_NEW:
			// 新增时将编辑器界面清空，状态为可编辑
			newTranstypeExtProp();
			break;
		case EditorContext.TYPE_EDIT:
			// 状态置为可编辑
			setEditable(true);
			// 其他业务处理
			break;
		case EditorContext.TYPE_CLEAR:
			clearEditorPane();
			setEditable(false);
		default:
			break;
		}
		
		DBCacheFacade.refreshTable(DjLXVO.getDefaultTableName());
	}

	private void qryTransobj(EditorContext ec) throws BusinessException {
		transTypeExtObject = queryTranstypeExtProp(ec);
		showTranstypeExtObj(transTypeExtObject);
		// 不可编辑
		setEditable(false);
	}

	@Override
	protected Object queryTranstypeExtProp(EditorContext ec) throws BusinessException {
		try {
			BillTypeVO[] vos = getdjlxiterface().queryBillTypeByBillTypeCode(ec.getTranstype().getPk_billtypecode(),
					ec.getTranstype().getPk_group());
			if (vos != null && vos.length > 0) {
				return vos[0];
			}
		} catch (Exception e) {
		}
		return null;
	}

	private IArapBillTypePrivate getdjlxiterface() {
		return NCLocator.getInstance().lookup(IArapBillTypePrivate.class);
	}

	@Override
	protected void setEditable(boolean isEdit) {
		getCsBillCard().setEnabled(isEdit);
	}

	@Override
	protected void showTranstypeExtObj(Object obj) throws BusinessException {
		BillTypeVO vo = (BillTypeVO) obj;
		getCsBillCard().setBillValueVO(vo == null ? new BillTypeVO() : vo);
	}

	@Override
	public Component getEditorPane() {
		getCsBillCard().setEnabled(false);
		return getCsBillCard();
	}

	@Override
	public Object getTransTypeExtObj(EditorContext context) throws BusinessException {
		BillTypeVO vo = null;
		DjLXVO head = null;
		BilltypeVO transtype = context.getTranstype();
		String pk_group = ErUiUtil.getPK_group();
		switch (context.getEventtype()) {
		case EditorContext.TYPE_NEW:
			getCsBillCard().getHeadItem("djlxbm").setValue(transtype.getPk_billtypecode());
			getCsBillCard().getHeadItem("djlxmc").setValue(transtype.getBilltypename());
			getCsBillCard().getHeadItem("djlxjc").setValue(transtype.getBilltypename());
			getCsBillCard().getHeadItem("djlxmc_remark").setValue(transtype.getBilltypename());
			
			getCsBillCard().dataNotNullValidate();
			getCsBillCard().stopEditing();
			
			vo = (BillTypeVO) getCsBillCard().getBillValueVO("nc.vo.er.djlx.BillTypeVO", "nc.vo.er.djlx.DjLXVO",
					"nc.vo.er.djlx.DjLXVO");
			head = (DjLXVO) vo.getParentVO();
			head.setDjlxbm(transtype.getPk_billtypecode());
			head.setDjlxmc(transtype.getBilltypename());
			head.setDjlxjc(transtype.getBilltypename());
			head.setDjlxmc_remark(transtype.getBilltypename());
			head.setDjlxjc_remark(transtype.getBilltypename());
			
			head.setPk_group(pk_group);
			head.setFcbz(transtype.getIsLock());

			head.setDjdl(ErmBillConst.AccruedBill_DJDL);
			head.setDwbm(BXConstans.GLOBAL_CODE);
			break;
		case EditorContext.TYPE_EDIT:
			vo = (BillTypeVO) queryTranstypeExtProp(context);
			if (vo != null) {
				getCsBillCard().getHeadItem("djlxoid").setValue(vo.getDjlxoid());
			}
			
			getCsBillCard().dataNotNullValidate();
			getCsBillCard().stopEditing();
			vo = (BillTypeVO) getCsBillCard().getBillValueVO("nc.vo.er.djlx.BillTypeVO", "nc.vo.er.djlx.DjLXVO",
					"nc.vo.er.djlx.DjLXVO");
			head = (DjLXVO) vo.getParentVO();
			head.setDjlxbm(transtype.getPk_billtypecode());
			head.setDjlxmc(transtype.getBilltypename());
			head.setDjlxjc(transtype.getBilltypename());
			head.setDjlxmc_remark(transtype.getBilltypename());
			head.setDjlxjc_remark(transtype.getBilltypename());
			head.setPk_group(pk_group);
			head.setFcbz(transtype.getIsLock());
			head.setDjdl(ErmBillConst.AccruedBill_DJDL);
			break;
		default:
			vo = (BillTypeVO) getCsBillCard().getBillValueVO("nc.vo.er.djlx.BillTypeVO", "nc.vo.er.djlx.DjLXVO",
					"nc.vo.er.djlx.DjLXVO");
			head = (DjLXVO) vo.getParentVO();
			head.setDjlxbm(transtype.getPk_billtypecode());
			head.setDjlxmc(transtype.getBilltypename());
			head.setDjlxjc(transtype.getBilltypename());
			head.setDjlxmc_remark(transtype.getBilltypename());
			head.setDjlxjc_remark(transtype.getBilltypename());
			
			head.setPk_group(pk_group);
			head.setFcbz(transtype.getIsLock());
			head.setDjdl(ErmBillConst.AccruedBill_DJDL);
		}
		setEditable(false);
		return vo;
	}

	public BillCardPanel getCsBillCard() {
		if (csBillCard == null) {
			csBillCard = new BillCardPanel();
			csBillCard.loadTemplet("1001Z31000000001I9PX");
		}
		return csBillCard;
	}

}
