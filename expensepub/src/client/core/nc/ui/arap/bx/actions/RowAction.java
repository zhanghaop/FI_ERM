package nc.ui.arap.bx.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillScrollPane;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDouble;

/**
 * nc.ui.arap.bx.actions.RowAction
 * 
 * 单据行操作类　
 * 
 * @author twei
 * @modified by liansg
 * 
 */
public class RowAction extends BXDefaultTabAction {

	private boolean isshow;

	public RowAction() {
		super();
		setEnabled(false);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try {
			addRow();
		} catch (BusinessException e1) {
			ExceptionHandler.handleExceptionRuntime(e1);
		}
	}

	public void addRow() throws BusinessException {

		validateAddRow();

		// 还款单节点
		boolean isRtnNode = getMainPanel().getNodeCode().equals(BXConstans.BXRB_CODE);

		if (isRtnNode) {
			return;
		}

		getBxBillCardPanel().addLine();

		setItemDefaultValue(getBillCardPanel().getBillData().getBodyItemsForTable(getCardPanel().getCurrentBodyTableCode()));

		String temppk = getTempPk();
		int rownum = getBillCardPanel().getRowCount();
		rownum--;

		getBxBillCardPanel().setBodyValueAt(temppk, getBxBillCardPanel().getRowCount() - 1, BXBusItemVO.PK_BUSITEM);

		// 将数据从表头联动到表体
		Map<String, String> keyMap = new HashMap<String, String>();
		keyMap.put(JKBXHeaderVO.SZXMID, "szxmmc");
		keyMap.put(JKBXHeaderVO.JKBXR, "jkbxrmc");
		keyMap.put(JKBXHeaderVO.JOBID, "jobmc");
		keyMap.put(JKBXHeaderVO.CASHPROJ, "cashproj_mc");
		doCoresp(rownum, keyMap, getCardPanel().getCurrentBodyTableCode());

		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.YBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.CJKYBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.ZFYBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.HKYBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.BBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.CJKBBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.ZFBBJE);
		getBillCardPanel().setBodyValueAt(new UFDouble(0), rownum, JKBXHeaderVO.HKBBJE);

		// 带出报销标准
		doBodyReimAction();

	}

	private void doCoresp(int rownum, Map<String, String> keyMap, String tablecode) {

		for (String key : keyMap.keySet()) {
			String value = null;
			if (getBillCardPanel().getHeadItem(key) != null && getBillCardPanel().getHeadItem(key).getValueObject() != null) {
				value = getBillCardPanel().getHeadItem(key).getValueObject().toString();
			}

			String bodyvalue = (String) getBillCardPanel().getBodyValueAt(rownum, key);
			if (value != null) {
				getBillCardPanel().setBodyValueAt(value, rownum, key);
			} else if (bodyvalue != null) {
				getBillCardPanel().setBodyValueAt(bodyvalue, rownum, key);
			}
			if (value != null) {
				execBodyFormula(rownum, keyMap.get(key), tablecode);
			}
			// begin--added by chendya 加载元数据相关项，避免显示pk
			getBillCardPanel().getBillModel().loadLoadRelationItemValue(rownum);
			// --end
		}
	}

	private boolean validateAddRow() throws BusinessException {
		
		//冲销行页签，还款单业务页签不允许操作
		if (getCardPanel().getCurrentBodyTableCode().equals(BXConstans.CONST_PAGE) 
				|| BXConstans.BILLTYPECODE_RETURNBILL.equals(getBillValueVO().getParentVO().getDjlxbm())) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000404")/*
																														 * @res
																														 * "当前页签不允许进行增,删，复制,粘贴 行的操作!"
																														 */);
		}
		return true;
	}

	private void setItemDefaultValue(BillItem[] items) {
		if (items != null) {
			for (int i = 0; i < items.length; i++) {
				BillItem item = items[i];
				Object value = item.getDefaultValueObject();
				if (value != null)
					item.setValue(value);
			}
		}
	}

	private void execBodyFormula(int rownum, String key, String tablecode) {
		BillItem item = getBillCardPanel().getBillData().getBodyItem(tablecode, key);
		if (item == null)
			return;
		String[] formulas = item.getEditFormulas();
		BillModel bm = getBillCardPanel().getBillModel();
		if (bm != null)
			bm.execFormulas(rownum, formulas);
	}

	public void delRow() throws BusinessException {
		if (getBillCardPanel().getBillModel().getRowCount() != 0) {

			validateAddRow();
			
			boolean isNeed = isNeedContrast();
			
			getBxBillCardPanel().delLine();
			
			if(isNeed){
				doContract();
			}
			
			// 删除，粘贴行后进行重设表头金额的操作.
			resetJeAfterModifyRow();
		}
	}
	
	/**
	 * 是否需要冲销
	 * @return
	 */
	private boolean isNeedContrast() {
		String tableCode = getCardPanel().getCurrentBodyTableCode();
		BillScrollPane bsp = getCardPanel().getBodyPanel(tableCode);
		int selectedRow = bsp.getTable().getSelectedRow();
		
		UFDouble cjkybje = (UFDouble)getBillCardPanel().getBodyValueAt(selectedRow, JKBXHeaderVO.CJKYBJE);
		
		if(!tableCode.equals(BXConstans.CONST_PAGE)){
			if(cjkybje != null && cjkybje.compareTo(UFDouble.ZERO_DBL) > 0){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 冲销 add by chenshuaia
	 * @throws ValidationException
	 * @throws BusinessException
	 */
	private void doContract() throws ValidationException, BusinessException {
		
		if(getBxBillCardPanel().getContrasts() == null && getBillValueVO().getContrastVO() != null){
			List<BxcontrastVO> contrastVosList = new ArrayList<BxcontrastVO>();
			for (int i = 0; i < getBillValueVO().getContrastVO().length; i++) {
				contrastVosList.add(getBillValueVO().getContrastVO()[i]);
			}
			getBxBillCardPanel().setContrasts(contrastVosList);//需要设置报销cardpanel的 contrasts
		}
		
		ContrastAction.doContrastToUI(getBxBillCardPanel(), getBillValueVO(),
			getBxBillCardPanel().getContrasts(), getBxBillCardPanel().isContrast());
	}

	private void resetJeAfterModifyRow() throws BusinessException {
		BXDefaultAction.calculateFinitemAndHeadTotal(getMainPanel());
		setHeadYFB();
		resetBodyFinYFB();
	}

	public void insertLine() throws BusinessException {

		validateAddRow();

		BillScrollPane bsp = getCardPanel().getBodyPanel(getCardPanel().getCurrentBodyTableCode());

		int selectedRow = bsp.getTable().getSelectedRow();

		getBillCardPanel().insertLine();

		getBxBillCardPanel().setBodyValueAt(getTempPk(), selectedRow, BXBusItemVO.PK_BUSITEM);
	}

	public void copyLine() throws BusinessException {

		validateAddRow();

		getBillCardPanel().copyLine();
	}

	public void pasteLine() throws BusinessException {

		validateAddRow();

		BillScrollPane bsp = getCardPanel().getBodyPanel(getCardPanel().getCurrentBodyTableCode());
		int rownum = bsp.getTable().getSelectedRow();

		getBillCardPanel().pasteLine();

		int pasteLineNumer = bsp.getTableModel().getPasteLineNumer();

		for (int i = 0; i < pasteLineNumer; i++) {
			getBxBillCardPanel().setBodyValueAt(getTempPk(), rownum + i, BXBusItemVO.PK_BUSITEM);
			getBxBillCardPanel().setBodyValueAt(null, rownum + i, BXBusItemVO.PK_BUSITEM);
		}

		// 删除，粘贴行后进行重设表头金额的操作.
		resetJeAfterModifyRow();
	}

	public void pasteLineToTail() throws BusinessException {

		validateAddRow();

		int rownum = getBillCardPanel().getRowCount(getCardPanel().getCurrentBodyTableCode());

		getBillCardPanel().pasteLineToTail();

		BillScrollPane bsp = getCardPanel().getBodyPanel(getCardPanel().getCurrentBodyTableCode());
		int pasteLineNumer = bsp.getTableModel().getPasteLineNumer();

		for (int i = 0; i < pasteLineNumer; i++) {
			getBxBillCardPanel().setBodyValueAt(getTempPk(), rownum + i, BXBusItemVO.PK_BUSITEM);
			getBxBillCardPanel().setBodyValueAt(null, rownum + i, BXBusItemVO.PK_BUSITEM);
		}

		// 删除，粘贴行后进行重设表头金额的操作.
		resetJeAfterModifyRow();
	}
}