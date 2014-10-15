package nc.ui.erm.billpub.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JPanel;

import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.arap.pub.IBxUIControl;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.itf.fipub.report.IPubReportConstants;
import nc.ui.arap.bx.BXBillListPanel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.pub.beans.UIButton;
import nc.ui.pub.beans.UIDialog;
import nc.ui.pub.beans.UILabel;
import nc.ui.pub.beans.UIPanel;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.ValueChangedEvent;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillEditListener;
import nc.ui.pub.bill.BillEditListener2;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillModelRowStateChangeEventListener;
import nc.ui.pub.bill.RowStateChangeEvent;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.uif2.UIState;
import nc.ui.uif2.editor.BillForm;
import nc.ui.uif2.model.AbstractAppModel;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.querytemplate.TemplateInfo;

import org.apache.commons.lang.ArrayUtils;

/**
 *  报销单中冲借款对话框, 使用列表模板实现
 *  
 */
public class ContrastDialog extends UIDialog implements java.awt.event.ActionListener,
		nc.ui.pub.beans.ValueChangedListener {

	private static final long serialVersionUID = 3022537308787517645L;

	private UIPanel queryPanel;
	private UILabel loanuserlabel;
	private UILabel loanfromtimelabel;
	private UILabel loantotimelabel;
	private UIRefPane loanusertext;
	private UIRefPane loanfromtimetext;
	private UIRefPane loantotimetext;

	private UIButton loanquerybtn;
	
	private UIButton advquerybtn;

	private UIButton btnConfirm;

	private UIButton btnCancel;

	private JKBXVO bxvo;

	private String nodecode;

	private String pkCorp;

	private JPanel ivjUIDialogContentPane;

	private BXBillListPanel listPanel;

	private UIPanel buttonPanel;

	private BillForm editor;
	
	private AbstractAppModel model;

	/**
	 *  选中表体业务VO缓存
	 *  <br><借款单pk,<借款行pk，借款行VO>>
	 */
	private Map<String, Map<String, BXBusItemVO>> selectedVO = new HashMap<String, Map<String, BXBusItemVO>>();
	/**
	 * 借款单主表已经被冲销的金额
	 */
	Map<String, UFDouble> jkOldcjkMap = new HashMap<String, UFDouble>();
	/**
	 * 借款单业务行已经被冲销的金额
	 */
	private Map<String, UFDouble> jkItemOldcjkMap = new HashMap<String, UFDouble>();
	
	/**
	 * 冲借款界面的借款单信息
	 */
	private List<JKBXHeaderVO> jkds = new ArrayList<JKBXHeaderVO>();
	
	private Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
	
	/**
	 * 支持多选的操作
	 * @author chenshuaia
	 *
	 */
	private class BodyRowStateListener implements IBillModelRowStateChangeEventListener {
		@Override
		public void valueChanged(RowStateChangeEvent event) {
			for (int row = event.getRow(); row <= event.getEndRow(); row++) {
				Boolean isSelected = (Boolean)getBodyValue(row, BXBusItemVO.SELECTED);
				if(isBodyRowSelected(row)){//选择
					if(isSelected != null && isSelected.booleanValue()){
						continue;
					}
					
					UFDouble ybye = (UFDouble) getBodyValue(row, JKBXHeaderVO.YBYE);
					
					getListPanel().getBodyBillModel().setValueAt(ybye, row, JKBXHeaderVO.CJKYBJE);

					// 如果是还款单的话，要将还款金额设置为冲借款金额
					if (BXConstans.BILLTYPECODE_RETURNBILL.equals(getBxvo().getParentVO().getDjlxbm())) {
						getListPanel().getBodyBillModel().setValueAt(ybye, row, JKBXHeaderVO.HKYBJE);
					} else {
						getListPanel().getBodyBillModel().setValueAt(UFDouble.ZERO_DBL, row, JKBXHeaderVO.HKYBJE);
					}

					// 将选择的表体VO放到一个缓存中
					int selectedRow = getListPanel().getHeadTable().getSelectedRow();
					getListPanel().getHeadBillModel().setValueAt(sumCjkje(), selectedRow, JKBXHeaderVO.CJKYBJE);
					
					setBodyHkJe(row);//设置表体还款金额
					setSelectedVO(row);//更新缓存
					setHeadTotalAmount();//设置表头合计金额
					setBodyValue(Boolean.TRUE, row, BXBusItemVO.SELECTED);
				}else if(getBodyRowState(row) == BillModel.UNSTATE){//取消选择
					// 清除缓存中的数据
					String pk_jkbx = (String) getListPanel().getBodyBillModel()
							.getValueAt(row, JKBXHeaderVO.PK_JKBX);
					String pk_busitem = (String) getListPanel().getBodyBillModel().getValueAt(row,
							BXBusItemVO.PK_BUSITEM);
					Map<String, BXBusItemVO> map = selectedVO.get(pk_jkbx);
					if (map != null && map.containsKey(pk_busitem)) {
						selectedVO.get(pk_jkbx).remove(pk_busitem);
					}
					
					getListPanel().getBodyBillModel().setValueAt(UFDouble.ZERO_DBL, row, JKBXHeaderVO.CJKYBJE);
					getListPanel().getBodyBillModel().setValueAt(UFDouble.ZERO_DBL, row, JKBXHeaderVO.HKYBJE);
					setHeadTotalAmount();
					setBodyValue(Boolean.FALSE, row, BXBusItemVO.SELECTED);
				}
			}
		}
	}

	private class BodyEditListener implements BillEditListener {
		@Override
		public void bodyRowChange(BillEditEvent e) {
		}

		@Override
		public void afterEdit(BillEditEvent e) {
			if (e.getKey().equals(JKBXHeaderVO.CJKYBJE)) {// 冲借款金额的校验
				UFDouble ybye = (UFDouble) getListPanel().getBodyBillModel().getValueAt(e.getRow(), "ybye");
				UFDouble cjkje = (UFDouble) getListPanel().getBodyBillModel().getValueAt(e.getRow(),
						JKBXHeaderVO.CJKYBJE);
				// 如果是还款单的话，要将还款金额设置为冲借款金额
				if (BXConstans.BILLTYPECODE_RETURNBILL.equals(getBxvo().getParentVO().getDjlxbm())) {
					getListPanel().getBodyBillModel().setValueAt(cjkje, e.getRow(), JKBXHeaderVO.HKYBJE);
				}

				if (cjkje.compareTo(ybye) > 0) {
					BXUiUtil.showUif2DetailMessage(ContrastDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("201107_0", "0201107-0147")/*
																	 * @res
																	 * "错误信息"
																	 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("201107_0", "0201107-0022")/*
																	 * @res
																	 * "冲借款金额不应大于借款余额"
																	 */);
					getListPanel().getBodyBillModel().setValueAt(ybye, e.getRow(), JKBXHeaderVO.CJKYBJE);
				}
				getListPanel().getHeadBillModel().setValueAt(sumCjkje(),
						getListPanel().getHeadTable().getSelectedRow(), JKBXHeaderVO.CJKYBJE);

				if (BXConstans.BILLTYPECODE_RETURNBILL.equals(getBxvo().getParentVO().getDjlxbm())) {
					UFDouble totalHkje = sumHkje();
					getListPanel().getHeadBillModel().setValueAt(totalHkje,
							getListPanel().getHeadTable().getSelectedRow(), JKBXHeaderVO.HKYBJE);
				}

				setBodyHkJe(e.getRow());
				// 修改缓存中的数据
				setSelectedVO(e.getRow());
				// 修改冲借款时修改重新计算还款金额
				setHeadTotalAmount();
			} else if (e.getKey().equals(JKBXHeaderVO.HKYBJE)) {
				UFDouble totalHkje = sumHkje();
				UFDouble hkybje = (UFDouble) getListPanel().getBodyBillModel().getValueAt(e.getRow(),
						JKBXHeaderVO.HKYBJE) == null ? UFDouble.ZERO_DBL : (UFDouble) getListPanel().getBodyBillModel()
						.getValueAt(e.getRow(), JKBXHeaderVO.HKYBJE);

				if (hkybje.compareTo(UFDouble.ZERO_DBL) < 0) {
					BXUiUtil.showUif2DetailMessage(ContrastDialog.this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("201107_0", "0201107-0147")/*
																	 * @res
																	 * "错误信息"
																	 */, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
							.getStrByID("201107_0", "0201107-0156")/*
																	 * @res
																	 * "还款金额不应小于0！"
																	 */);

					getListPanel().getBodyBillModel().setValueAt(UFDouble.ZERO_DBL, e.getRow(), JKBXHeaderVO.HKYBJE);
					return;
				}

				getListPanel().getHeadBillModel().setValueAt(totalHkje, getListPanel().getHeadTable().getSelectedRow(),
						JKBXHeaderVO.HKYBJE);
				// 修改缓存中的数据
				setSelectedVO(e.getRow());
			}
		}

	}
	
	private class HeadEditListerner implements BillEditListener {
		@Override
		public void afterEdit(BillEditEvent e) {
		}

		@Override
		public void bodyRowChange(BillEditEvent e) {
			// 选择表头一行后，将表体的数据带出来
			String pk_jkbx = (String) listPanel.getHeadBillModel().getValueAt(e.getRow(), JKBXHeaderVO.PK_JKBX);
			try {
				BXBusItemVO[] bodyVos = queryByHeaders(pk_jkbx);
				getListPanel().setBodyValueVO(getCachVO(bodyVos));
				// 需要加载公式，显示参照名称
				getListPanel().getBodyBillModel().loadLoadRelationItemValue();
				getListPanel().getBodyBillModel().execLoadFormula();
				
				//设置行选中状态
				BillModel bodyBillModel = getListPanel().getBodyBillModel();
				for (int i = 0; i < bodyBillModel.getRowCount(); i++) {
					Boolean isSelected = (Boolean) getBodyValue(i, BXBusItemVO.SELECTED);
					if (isSelected != null && isSelected.booleanValue()) {
						setBodyRowState(i, BillModel.SELECTED);
					}
				}

				setHeadTotalAmount();
				
				getListPanel().getChildListPanel().updateUI();
			} catch (BusinessException e1) {
				ExceptionHandler.handleRuntimeException(e1);
			}
		}
	}

	/**
	 * 设置表头中的总金额（冲借款金额，还款金额）
	 * @param e
	 */
	public void setHeadTotalAmount() {
		UFDouble sumCjkje = sumCjkje();
		int selectedRow = getListPanel().getHeadTable().getSelectedRow();
		getListPanel().getHeadBillModel().setValueAt(sumCjkje, selectedRow, JKBXHeaderVO.CJKYBJE);

		// 得到所有表头行的值
		JKHeaderVO[] jkHeadVos = (JKHeaderVO[]) getListPanel().getHeadBillModel().getBodyValueVOs(
				nc.vo.ep.bx.JKHeaderVO.class.getName());

		int row = 0;
		for (JKHeaderVO jkHeaderVO : jkHeadVos) {
			Map<String, BXBusItemVO> map = selectedVO.get(jkHeaderVO.getAttributeValue(JKBXHeaderVO.PK_JKBX));
			UFDouble jkhkje = UFDouble.ZERO_DBL;
			if (map != null) {
				List<BXBusItemVO> list = new ArrayList<BXBusItemVO>();
				for (BXBusItemVO busItem : map.values()) {
					list.add(busItem);
				}
				
				for (BXBusItemVO item : list) {
					jkhkje = jkhkje.add(item.getHkybje() == null ? UFDouble.ZERO_DBL : item.getHkybje());
					if (selectedRow == row) {
						// 当前选择行的子表，界面更新
						setBodyColumnValue(BXBusItemVO.HKYBJE, BXBusItemVO.PK_BUSITEM, item.getPk_busitem(),
								item.getHkybje());
					}
				}
			}
			jkHeaderVO.setHkybje(jkhkje);
			getListPanel().getHeadBillModel().setValueAt(jkHeaderVO.getHkybje(), row++, JKBXHeaderVO.HKYBJE);
		}
	}

	/**
	 * 重新设置表体行的还款金额 当选择行，或修改冲借款金额时
	 * 
	 * @author chenshuaia
	 * @param bodyrow
	 */
	private void setBodyHkJe(int bodyrow) {
		if (bodyrow >= 0) {
			// 从缓存中取得金额合计
			String pk_busitem = (String) getListPanel().getBodyBillModel().getValueObjectAt(bodyrow,
					BXBusItemVO.PK_BUSITEM);
			UFDouble totalCjk = getTotalBodyJe(BXBusItemVO.CJKYBJE, pk_busitem);
			UFDouble totalHkje = getTotalBodyJe(BXBusItemVO.HKYBJE, pk_busitem);

			UFDouble bodyCjk = (UFDouble) getListPanel().getBodyBillModel().getValueAt(bodyrow, JKBXHeaderVO.CJKYBJE);
			totalCjk = totalCjk.add(bodyCjk);// 加入本行冲借款金额
			

			UFDouble hkje = totalCjk.sub(getBxje()).doubleValue() < 0 ? UFDouble.ZERO_DBL : totalCjk.sub(getBxje());

			if (hkje.compareTo(UFDouble.ZERO_DBL) > 0) {
				UFDouble bodycjkje = (UFDouble) getListPanel().getBodyBillModel().getValueObjectAt(bodyrow,
						JKBXHeaderVO.CJKYBJE);
				UFDouble otherHkje = hkje.sub(totalHkje);
				if (otherHkje.compareTo(UFDouble.ZERO_DBL) > 0) {
					if (otherHkje.compareTo(bodycjkje) >= 0) {
						getListPanel().getBodyBillModel().setValueAt(bodycjkje, bodyrow, JKBXHeaderVO.HKYBJE);
					} else {
						getListPanel().getBodyBillModel().setValueAt(otherHkje, bodyrow, JKBXHeaderVO.HKYBJE);
					}
				} else {
					getListPanel().getBodyBillModel().setValueAt(UFDouble.ZERO_DBL, bodyrow, JKBXHeaderVO.HKYBJE);
				}

			} else {
				getListPanel().getBodyBillModel().setValueAt(UFDouble.ZERO_DBL, bodyrow, JKBXHeaderVO.HKYBJE);
			}
		}
	}
	
	/**
	 * 获取选中行中金额的合计值
	 * @param key 金额的key
	 * @return
	 */
	private UFDouble getTotalBodyJe(String key, String pk_busitem) {
		if (key == null) {
			return null;
		}
		UFDouble total = UFDouble.ZERO_DBL;

		for (Map<String, BXBusItemVO> map : selectedVO.values()) {
			for (Entry<String, BXBusItemVO> entry : map.entrySet()) {
				BXBusItemVO item = entry.getValue();
				if(pk_busitem != null && pk_busitem.equals(item.getPk_busitem())){
					continue;
				}
				if (item.getAttributeValue(key) != null) {
					total = total.add((UFDouble) item.getAttributeValue(key));
				}
			}
		}

		return total;
	}

	public void setSelectedVO(int row) {
		String pk_jkbx = (String) getListPanel().getBodyBillModel().getValueAt(row, JKBXHeaderVO.PK_JKBX);
		String pk_busitem = (String) getListPanel().getBodyBillModel().getValueAt(row, BXBusItemVO.PK_BUSITEM);
		BXBusItemVO bxbusitemVO = (BXBusItemVO) getListPanel().getBodyBillModel().getBodyValueRowVO(row,
				"nc.vo.ep.bx.BXBusItemVO");
		Map<String, BXBusItemVO> map = selectedVO.get(pk_jkbx);
		if (map == null) {
			map = new HashMap<String, BXBusItemVO>();
			selectedVO.put(pk_jkbx, map);
		}
		map.put(pk_busitem, bxbusitemVO);
	}
	

	// 多个或者单个
	private UFDouble getBxje() {
		JKBXVO bxvo2 = getBxvo();
		return bxvo2.getParentVO().getYbje();
	}

	private UFDouble getContrastTotal() {
		UFDouble total = UFDouble.ZERO_DBL;
		Collection<Map<String, BXBusItemVO>> bxbusItemMapList = selectedVO.values();
		
		if(bxbusItemMapList != null){
			for(Map<String, BXBusItemVO> map :bxbusItemMapList){
				if(map != null && map.values() != null){
					for(BXBusItemVO item : map.values()){
						total = total.add(item.getCjkybje() == null ? UFDouble.ZERO_DBL : item.getCjkybje());
					}
				}
			}
		}
		
		return total;
	}

	protected UFDouble getHkTotal() {
		UFDouble total = UFDouble.ZERO_DBL;
		Collection<Map<String, BXBusItemVO>> bxbusItemMapList = selectedVO.values();

		if (bxbusItemMapList != null) {
			for (Map<String, BXBusItemVO> map : bxbusItemMapList) {
				if (map != null && map.values() != null) {
					for (BXBusItemVO item : map.values()) {
						total = total.add(item.getHkybje() == null ? UFDouble.ZERO_DBL : item.getHkybje());
					}
				}
			}
		}
		return total;
	}

	/**
	 * @see 加载冲借款模板
	 */
	private void loadBillListTemplate() {

		WorkbenchEnvironment instance = WorkbenchEnvironment.getInstance();
		getListPanel().loadTemplet(nodecode, null, instance.getLoginUser().getCuserid(),
				instance.getGroupVO().getPk_group(), "CJK63");
	}

	// 得到借款表体业务行
	public BXBusItemVO[] queryByHeaders(String pk_jkbx) throws BusinessException {
		if (pk_jkbx == null) {
			return null;
		}
		return NCLocator.getInstance().lookup(IBxUIControl.class).queryByPk(pk_jkbx, bxvo.getParentVO().getPk_jkbx());
	}

	@SuppressWarnings("deprecation")
	public BXBillListPanel getListPanel() {
		if (listPanel == null) {
			try {
				listPanel = new BXBillListPanel();
				listPanel.setName("LIST");
				loadBillListTemplate();
				listPanel.getParentListPanel().setTotalRowShow(true);
				listPanel.getHeadTable().getActionMap().clear();
				// 千分位和零值不显示的处理
				nc.vo.pub.bill.BillRendererVO voCell = new nc.vo.pub.bill.BillRendererVO();
				voCell.setShowThMark(true);
				voCell.setShowZeroLikeNull(true);
				listPanel.getChildListPanel().setShowFlags(voCell);
				listPanel.getParentListPanel().setShowFlags(voCell);
				listPanel.setEnabled(true);
				listPanel.getChildListPanel().setEnabled(true);
				listPanel.getBodyScrollPane("jk_busitem").addEditListener2(new BillEditListener2() {
					@Override
					public boolean beforeEdit(BillEditEvent e) {
						final int row = e.getRow();
						if (e.getKey().equals(JKBXHeaderVO.CJKYBJE) || e.getKey().equals(JKBXHeaderVO.HKYBJE)) {
							boolean selected = ((Boolean) (getListPanel().getBodyBillModel().getValueAt(row,
									JKBXHeaderVO.SELECTED))).booleanValue();
							return selected;
						}
						return false;//界面中除了借款款和 还款金额可编辑，其他不可编辑
					}
				});
				listPanel.getBodyScrollPane("jk_busitem").clearDefalutEditAction();
				listPanel.getBodyScrollPane("jk_busitem").clearFixAction();
				listPanel.getBodyScrollPane("jk_busitem").clearNotEditAction();
				listPanel.addHeadEditListener(new HeadEditListerner());
				listPanel.addBodyEditListener(new BodyEditListener());
				listPanel.getBodyBillModel().addRowStateChangeEventListener(new BodyRowStateListener());
				
				listPanel.getChildListPanel().setAutoAddLine(false);
				listPanel.setChildMultiSelect(true);

			} catch (Exception ex) {
				handleException(ex);
			}
		}
		return listPanel;
	}

	public BXBusItemVO[] getCachVO(BXBusItemVO[] bodyVos) {
		// 如果缓存中有业务行数据，有将缓存中的数据设置到查询出的业务行中
		List<BXBusItemVO> list = new ArrayList<BXBusItemVO>();
		if (!ArrayUtils.isEmpty(bodyVos)) {
			for (BXBusItemVO vo : bodyVos) {
				Map<String, BXBusItemVO> map = selectedVO.get(vo.getPk_jkbx());
				if (map != null) {
					BXBusItemVO item = map.get(vo.getPk_busitem());
					if (item != null) {
						vo.setCjkybje(item.getCjkybje());
						vo.setHkybje(item.getHkybje());
						vo.setSelected(UFBoolean.TRUE);
					}
				}
				if (jkItemOldcjkMap.get(vo.getPrimaryKey()) != null) {// 冲销保存
					vo.setYbye(vo.getYjye().add(jkItemOldcjkMap.get(vo.getPrimaryKey())));
				}
				
				if(vo.getYbye() != null && vo.getYbye().compareTo(UFDouble.ZERO_DBL) > 0){
					list.add(vo);
				}
			}
		}
		return list.toArray(new BXBusItemVO[list.size()]);
	}

	/**
	 * 获取被选中的借款单业务行
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public JKHeaderVO[] getSelectedJKVOs() throws BusinessException {
		Set<JKHeaderVO> headSet = new HashSet<JKHeaderVO>();
		Map<String, JKHeaderVO> jkHeadMap = new HashMap<String, JKHeaderVO>();

		JKHeaderVO[] jkHeadVos = (JKHeaderVO[]) getListPanel().getHeadBillModel().getBodyValueVOs(
				nc.vo.ep.bx.JKHeaderVO.class.getName());

		if (jkHeadVos != null) {
			for (int i = 0; i < jkHeadVos.length; i++) {
				jkHeadMap.put(jkHeadVos[i].getPk_jkbx(), jkHeadVos[i]);
			}
		}

		for (Map.Entry<String, Map<String, BXBusItemVO>> entry : selectedVO.entrySet()) {
			if (entry.getValue() != null && !entry.getValue().isEmpty()) {
				headSet.add(jkHeadMap.get(entry.getKey()));
			}
		}

		return headSet.toArray(new JKHeaderVO[] {});
	}

	/**
	 * @author chendya 需要合计的字段
	 * @return
	 */
	public String[] getTotalMoneyKey() {

		return new String[] { BxcontrastVO.YBJE, BxcontrastVO.YBYE, BxcontrastVO.BBJE, BxcontrastVO.CJKBBJE,
				BxcontrastVO.CJKYBJE, BxcontrastVO.FYYBJE, BxcontrastVO.HKYBJE, BxcontrastVO.GLOBALBBJE,
				BxcontrastVO.GLOBALCJKBBJE, BxcontrastVO.GLOBALFYBBJE };
	}

	/**
	 * @author chendya 计算合计
	 */
	public void setTotalRowMoney() {
		// 除去合计行
		int rowCount = getListPanel().getHeadBillModel().getRowCount();
		final String[] fields = getTotalMoneyKey();
		BxcontrastVO totalRowVO = new BxcontrastVO();
		for (int i = 0; i < rowCount; i++) {
			for (int j = 0; j < fields.length; j++) {
				UFDouble value = (UFDouble) getHeadColumnValue(fields[j], i);
				if (value == null || value.toString().length() == 0) {
					continue;
				}
				if (totalRowVO.getAttributeValue(fields[j]) == null) {
					int decimalDigits = getListPanel().getHeadItem(fields[j]).getDecimalDigits();
					totalRowVO.setAttributeValue(fields[j], new UFDouble(0, decimalDigits));
				}
				UFDouble sumVal = (UFDouble) totalRowVO.getAttributeValue(fields[j]);
				sumVal = sumVal.add(value);
				totalRowVO.setAttributeValue(fields[j], sumVal);
			}
		}
		BillItem[] items = getListPanel().getHeadBillModel().getBodyItems();
		for (int i = 0; i < items.length; i++) {
			if (!items[i].isShow()) {
				continue;
			}
			getListPanel().getHeadBillModel().getTotalTableModel()
					.setValueAt(totalRowVO.getAttributeValue(items[i].getKey()), 0, i);
		}
	}

	// 计算表体的冲借款金额之和
	private UFDouble sumCjkje() {
		UFDouble totalcjkje = new UFDouble(0);
		for (int row = 0; row < getListPanel().getBodyBillModel().getRowCount(); row++) {
			if (!isBodyRowSelected(row)) {
				continue;
			}

			UFDouble cjkje = (UFDouble) getListPanel().getBodyBillModel().getValueAt(row, JKBXHeaderVO.CJKYBJE) == null ? UFDouble.ZERO_DBL
					: (UFDouble) getListPanel().getBodyBillModel().getValueAt(row, JKBXHeaderVO.CJKYBJE);
			totalcjkje = totalcjkje.add(cjkje);
		}
		return totalcjkje;
	}

	// 合计表体的还款金额之和
	private UFDouble sumHkje() {
		UFDouble totalhkje = UFDouble.ZERO_DBL;
		for (int row = 0; row < getListPanel().getBodyBillModel().getRowCount(); row++) {
			if (!isBodyRowSelected(row))
				continue;
			
			UFDouble hkje = (UFDouble) getListPanel().getBodyBillModel().getValueAt(row, JKBXHeaderVO.HKYBJE) == null ? UFDouble.ZERO_DBL
					: (UFDouble) getListPanel().getBodyBillModel().getValueAt(row, JKBXHeaderVO.HKYBJE);
			totalhkje = totalhkje.add(hkje);
		}
		return totalhkje;
	}

	public ContrastDialog(BillForm editor, String nodecode, String pkCorp, JKBXVO bxvo) {
		super(((BillManageModel) editor.getModel()).getContext().getEntranceUI());
		this.editor = editor;
		this.model = editor.getModel();
		setNodecode(nodecode);
		setPkCorp(pkCorp);
		setBxvo(bxvo);
		initialize();
	}
	
	public AbstractAppModel getModel() {
		return model;
	}
	
	private IBxUIControl getBxUIControl() {
		return NCLocator.getInstance().lookup(IBxUIControl.class);
	}

	/**
	 * 加载冲销行的数据
	 */
	private void loadselectedVO(BxcontrastVO[] contrastVO) {
		for (BxcontrastVO vo : contrastVO) {
			BXBusItemVO busitemvo = new BXBusItemVO();
			busitemvo.setSelected(UFBoolean.TRUE);
			busitemvo.setSzxmid(vo.getSzxmid());
			busitemvo.setPk_jkbx(vo.getPk_jkd());
			busitemvo.setPk_busitem(vo.getPk_busitem());
			busitemvo.setCjkybje(vo.getCjkybje());
			busitemvo.setHkybje(vo.getHkybje());
			busitemvo.setPk_bxcontrast(vo.getPk_bxcontrast());
			busitemvo.setJobid(vo.getJobid());
			
			Map<String, BXBusItemVO> map = selectedVO.get(vo.getPk_jkd());
			if (map == null) {
				map = new HashMap<String, BXBusItemVO>();
				selectedVO.put(vo.getPk_jkd(), map);
			}
			// 对于冲销行和借款单业务行不一致时，处理方式
			BXBusItemVO item = map.get(vo.getPk_busitem());
			if (item != null) {
				busitemvo.setCjkybje((item.getCjkybje().add(vo.getCjkybje())));
				busitemvo.setHkybje(item.getHkybje().add(vo.getHkybje()));
			}

			map.put(vo.getPk_busitem(), busitemvo);
		}
	}

	/**
	 * @param bxvo
	 * @param oldcontrastvos
	 * @throws BusinessException
	 * 
	 *             初始化数据
	 */
	public void initData(JKBXVO bxvo, BxcontrastVO[] oldcontrastvos,String qrySql) throws BusinessException {
		// 初始化数据时先要清空缓存，再根据冲销行加载缓存
		selectedVO.clear();
		jkOldcjkMap.clear();
		jkItemOldcjkMap.clear();
		
		setBxvo(bxvo);
		//校验界面信息
		checkinitData(bxvo);

		if (oldcontrastvos != null && oldcontrastvos.length > 0) {
			for (int i = 0; i < oldcontrastvos.length; i++) {
				BxcontrastVO bxcontrastVO = oldcontrastvos[i];
				if(bxcontrastVO.getSxbz() != null && bxcontrastVO.getSxbz() == BXStatusConst.SXBZ_TEMP){
					continue;
				}
				
				UFDouble cjkybje = bxcontrastVO.getCjkybje();
				// 计算借款主表已经冲借款金额
				UFDouble headCjkamount = jkOldcjkMap.get(bxcontrastVO.getPk_jkd()) == null ? UFDouble.ZERO_DBL
						: jkOldcjkMap.get(bxcontrastVO.getPk_jkd());
				jkOldcjkMap.put(bxcontrastVO.getPk_jkd(), headCjkamount.add(cjkybje));
				// 计算借款业务行已经冲借款金额
				UFDouble itemCjkamount = jkItemOldcjkMap.get(bxcontrastVO.getPk_busitem()) == null ? UFDouble.ZERO_DBL
						: jkItemOldcjkMap.get(bxcontrastVO.getPk_busitem());
				
				jkItemOldcjkMap.put(bxcontrastVO.getPk_busitem(), itemCjkamount.add(cjkybje));
			}
		}
		
		//根据冲销信息设置借款信息缓存
		BxcontrastVO[] contrastVO = bxvo.getContrastVO();
		if (contrastVO != null && contrastVO.length != 0) {
			loadselectedVO(contrastVO);
		}
		
		//处理界面精度
		BXUiUtil.resetDecimalForContrast(getListPanel(), bxvo.getParentVO().getBzbm());
		
		try {// 查询借款单
			JKBXVO jkbxvo = (JKBXVO) bxvo.clone();
			jkbxvo.setContrastVO(oldcontrastvos);
			setJkds(getBxUIControl().getJKD(jkbxvo, BXUiUtil.getBusiDate(),
					qrySql));
		} catch (Exception e) {
			Log.getInstance(this.getClass()).error(e.getMessage(), e);
			throw new BusinessException(e.getMessage(), e);
		}
		
		initJkds(bxvo, jkds);
		
		//设置合计行的值
		setTotalRowMoney();
		
		//设置表头每一行的还款金额和冲借款金额
		setHeadTotalAmount();
		
		setHeadSelectedRow();
	}
	
	private void setHeadSelectedRow() {
		int index = -1;
		for (int row = 0; row < getListPanel().getHeadBillModel().getRowCount(); row++) {
			UFDouble cjkje = (UFDouble) getListPanel().getHeadBillModel().getValueAt(row, JKBXHeaderVO.CJKYBJE);
			if (cjkje!= null && cjkje.compareTo(UFDouble.ZERO_DBL) > 0) {
				index = row;
				break;
			}
			index = 0;
		}
		
		getListPanel().getHeadTable().getSelectionModel().setSelectionInterval(index, index);
		
		getListPanel().getChildListPanel().updateUI();
	}

	/**
	 * 打开冲销界面时，校验冲销信息
	 * @param bxvo
	 * @return
	 * @throws BusinessException
	 */
	private void checkinitData(JKBXVO bxvo) throws BusinessException {
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		if (parentVO.getReceiver() == null && parentVO.getJkbxr() == null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000179")/*
																														 * @
																														 * res
																														 * "请录入报销人,再进行冲借款操作!"
																														 */);
		}

		if (!BXConstans.BILLTYPECODE_RETURNBILL.equals(parentVO.getDjlxbm())) {
			if (parentVO.getYbje().compareTo(new UFDouble(0)) <= 0) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000911")/*
										 * @res "录入报销金额要大于0,才进行冲借款操作!"
										 */);
			}
			BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
			if (childrenVO != null && childrenVO.length != 0) {
				for (BXBusItemVO bxBusItemVO : childrenVO) {
					if (bxBusItemVO.getYbje().compareTo(new UFDouble(0)) <= 0) {
						throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
								"UPP2011-000905")/*
												 * @res
												 * "报销单业务行金额都要大于0,才进行冲借款操作！"
												 */);
					}
				}
			}
		}
	}
	
	/**
	 * 
	 * @param bxvo 报销VO
	 * @param bxvos 借款单VO集合
	 */
	private void initJkds(JKBXVO bxvo, List<JKBXHeaderVO> bxvos) {

		// 增加原被冲销的金额
		if (bxvos == null) {
			return;
		}
		
		for (JKBXHeaderVO jkHead : bxvos) {
			UFDouble ybye = jkHead.getYbye() == null ? UFDouble.ZERO_DBL : jkHead.getYbye();
			UFDouble cjkJe = jkOldcjkMap.get(jkHead.getPk_jkbx());
			if (cjkJe != null) {
				jkHead.setYbye(ybye.add(jkOldcjkMap.get(jkHead.getPk_jkbx())));
			}
			jkdMap.put(jkHead.getPk_jkbx(), jkHead);
		}
		
		setHeadValueVos(bxvos);
		
		// 如果报销单已经冲销了借款单,此处初始化.
		if (((ErmBillBillForm) getEditor()).isContrast()) {
			// 取界面冲销信息初始化冲借款对话框
			BxcontrastVO[] bxcontrastVO = (BxcontrastVO[]) getEditor().getBillCardPanel()
					.getBillModel(BXConstans.CONST_PAGE).getBodyValueVOs(BxcontrastVO.class.getName());
			List<BxcontrastVO> contrasts = Arrays.asList(bxcontrastVO);
			
			if (contrasts != null) {
				for (BxcontrastVO head : contrasts) {
					setCjkje(head); // 设置冲借款金额
				}
			}
		} else { // 取数据库信息初始化冲借款对话框
			BxcontrastVO[] contrastVO = bxvo.getContrastVO();
			if (contrastVO != null) {
				for (BxcontrastVO head : contrastVO) {
					setCjkje(head); // 设置冲借款金额
				}
			}
		}
		
	}

	/**
	 * @param pk_jkd
	 * @param cjkybje
	 * 
	 *            设置冲借款金额
	 */
	private void setCjkje(BxcontrastVO vo) {
		String pk_jkd = vo.getPk_jkd();

		if (pk_jkd != null) {
			setHeadColumnValue(JKBXHeaderVO.SELECTED, JKBXHeaderVO.PK_JKBX, pk_jkd, UFBoolean.TRUE);
			setHeadColumnValue(BxcontrastVO.PK_BXCONTRAST, JKBXHeaderVO.PK_JKBX, pk_jkd, vo.getPk_bxcontrast());

			Object cjkje = getHeadColumnValue(JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.PK_JKBX, pk_jkd);
			if (cjkje != null){
				setHeadColumnValue(JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.PK_JKBX, pk_jkd,
						vo.getCjkybje().add((UFDouble) cjkje));
			}else{
				setHeadColumnValue(JKBXHeaderVO.CJKYBJE, JKBXHeaderVO.PK_JKBX, pk_jkd, vo.getCjkybje());
			}
			
			// 设置可以编辑
			//getListPanel().getHeadItem(JKBXHeaderVO.CJKYBJE).setEnabled(true);
			//getListPanel().getHeadItem(JKBXHeaderVO.HKYBJE).setEnabled(true);
		}

//		resetHkje();
	}

	private void initialize() {
		try {
			setName("ContrastDialog");
			setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
			setSize(837, 490);
			setContentPane(getContentPanel());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
		getBtnConfirm().addActionListener(this);
		getBtnCancel().addActionListener(this);
	}

	private nc.ui.pub.beans.UIPanel getQueryPanel() {
		if (queryPanel == null) {
			try {
				queryPanel = new nc.ui.pub.beans.UIPanel();
				queryPanel.setName("queryPanel");
				queryPanel.setPreferredSize(new java.awt.Dimension(1000, 50));
				queryPanel.setBounds(0, 0, 600, 50);
				queryPanel.setLayout(null);
				queryPanel.add(getUserlabel());
				queryPanel.add(getusertext());
				queryPanel.add(getfromtimelabel(), getfromtimelabel().getName());
				queryPanel.add(getfromtimetext(), getfromtimetext().getName());
				queryPanel.add(gettotimelabel(), gettotimelabel().getName());
				queryPanel.add(gettotimetext(), gettotimetext().getName());
				queryPanel.add(getBtnQuery(), getBtnQuery().getName());
				queryPanel.add(getBtnAdvancedQuery(),getBtnAdvancedQuery().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return queryPanel;
	}

	private UIButton getBtnAdvancedQuery() {
		if (advquerybtn == null) {
			advquerybtn = new nc.ui.pub.beans.UIButton();
			advquerybtn.setName("advquerybtn");
			advquerybtn.setBounds(750, 15, 60, 20);
			advquerybtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000940")/*																								 */);
			advquerybtn.addActionListener(this);

		}
		return advquerybtn;
	}

	private UILabel getUserlabel() {
		if (loanuserlabel == null) {
			try {
				loanuserlabel = new nc.ui.pub.beans.UILabel();
				loanuserlabel
						.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000249")/*
																												 * @
																												 * res
																												 * "借款人"
																												 */);
				loanuserlabel.setBounds(40, 15, 60, 20);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loanuserlabel;
	}

	private UIRefPane getusertext() {
		if (loanusertext == null) {
			try {
				loanusertext = new nc.ui.pub.beans.UIRefPane();
				loanusertext.setName("loanuser");
				loanusertext.setRefNodeName("人员");
				loanusertext.setBounds(95, 15, 100, 20);
				loanusertext.setPk_org(getBxvo().getParentVO().getDwbm());
				loanusertext.getRefModel().setUseDataPower(true);
				loanusertext.setDataPowerOperation_code(IPubReportConstants.FI_REPORT_REF_POWER);
				// FIXME 增加授权代理之类后处理,此处相当增加过滤条件，与单据界面上的借款报销人的取得方法相同，内容应该也相同
				String wherePart = BXUiUtil.getAgentWhereString(getBxvo().getParentVO().getDjlxbm(),
						BXUiUtil.getPk_user(), BXUiUtil.getSysdate().toString(), getBxvo().getParentVO().getDwbm());
				try {
					String whereStr = loanusertext.getRefModel().getWherePart();
					
					if (null != whereStr) {
						whereStr += wherePart;
					} else {
						whereStr = " 1=1 " + wherePart;
					}
					loanusertext.setWhereString(whereStr);
				} catch (ClassCastException e) {
				}
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		loanusertext.setPk_org(getBxvo().getParentVO().getPk_org());
		return loanusertext;
	}

	private UILabel getfromtimelabel() {
		if (loanfromtimelabel == null) {
			try {
				loanfromtimelabel = new nc.ui.pub.beans.UILabel();
				loanfromtimelabel.setBounds(270, 15, 60, 20);
				loanfromtimelabel.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000374")/*
										 * @res "借款时间"
										 */);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loanfromtimelabel;
	}

	private UIRefPane getfromtimetext() {
		if (loanfromtimetext == null) {
			try {
				loanfromtimetext = new nc.ui.pub.beans.UIRefPane();
				loanfromtimetext.setName("loanfromtime");
				loanfromtimetext.setRefNodeName("日历");
				loanfromtimetext.setBounds(335, 15, 100, 20);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loanfromtimetext;
	}

	private UILabel gettotimelabel() {
		if (loantotimelabel == null) {
			try {
				loantotimelabel = new nc.ui.pub.beans.UILabel();
				loantotimelabel.setBounds(490, 15, 60, 20);
				loantotimelabel
						.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000375")/*
																												 * @
																												 * res
																												 * "至"
																												 */);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loantotimelabel;
	}

	private UIRefPane gettotimetext() {
		if (loantotimetext == null) {
			try {
				loantotimetext = new nc.ui.pub.beans.UIRefPane();
				loantotimetext.setName("loantotime");
				loantotimetext.setRefNodeName("日历");
				loantotimetext.setBounds(527, 15, 100, 20);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return loantotimetext;
	}

	protected UIButton getBtnQuery() {
		if (loanquerybtn == null) {
			loanquerybtn = new nc.ui.pub.beans.UIButton();
			loanquerybtn.setName("querybtn");
			loanquerybtn.setBounds(670, 15, 60, 20);
			loanquerybtn.setText(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000204")/*
																												 * @
																												 * res
																												 * "查询"
																												 */);
			loanquerybtn.addActionListener(this);

		}
		return loanquerybtn;
	}

	private javax.swing.JPanel getContentPanel() {
		if (ivjUIDialogContentPane == null) {
			try {
				ivjUIDialogContentPane = new javax.swing.JPanel();
				ivjUIDialogContentPane.setName("UIDialogContentPane");
				ivjUIDialogContentPane.setLayout(new java.awt.BorderLayout());
				// 增加是否允许重新他人借款参数
				boolean para = true;
				try {
					// modified by chendya@ufida.com.cn 取组织级参数
					// para =
					// SysInit.getParaBoolean(BXUiUtil.getDefaultOrgUnit(),
					// BXParamConstant.PARAM_IS_CONTRAST_OTHERS).booleanValue();
					final String pk_org = (String) getEditor().getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
							.getValueObject();
					para = SysInit.getParaBoolean(pk_org, BXParamConstant.PARAM_IS_CONTRAST_OTHERS).booleanValue();
					// --end
				} catch (java.lang.Throwable ivjExc) {
				}
				if (para) {
					getContentPanel().add(getQueryPanel(), BorderLayout.NORTH);
				}
				getContentPanel().add(getListPanel(), BorderLayout.CENTER);
				getContentPanel().add(getButtonPanel(), BorderLayout.SOUTH);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return ivjUIDialogContentPane;
	}

	private nc.ui.pub.beans.UIPanel getButtonPanel() {
		if (buttonPanel == null) {
			try {
				buttonPanel = new nc.ui.pub.beans.UIPanel();
				buttonPanel.setName("buttonPanel");
				buttonPanel.setPreferredSize(new java.awt.Dimension(660, 50));
				buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
				buttonPanel.add(getBtnConfirm(), getBtnConfirm().getName());
				buttonPanel.add(getBtnCancel(), getBtnCancel().getName());
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return buttonPanel;
	}

	/**
	 * @return　
	 * 
	 *         冲借款信息选择后，返回信息，进行后续业务处理 由表头和表体的数据组成
	 */
	public List<BxcontrastVO> getContrastData() {
		List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();
		JKBXHeaderVO parentVO = bxvo.getParentVO();
		for (Entry<String, Map<String, BXBusItemVO>> selectedvo : selectedVO.entrySet()) {
			String jkdpk = selectedvo.getKey();
			Map<String, BXBusItemVO> busiitems = selectedvo.getValue();
			JKBXHeaderVO head = jkdMap.get(jkdpk);
			String deptid = head.getDeptid();
			String djlxbm = head.getDjlxbm();
			String jkbxr = head.getJkbxr();
			String pk_org = head.getPk_org();
			String djbh = head.getDjbh();
			String pk_payorg = head.getPk_payorg();

			for (BXBusItemVO item : busiitems.values()) {
				BxcontrastVO bxcontrastVO = new BxcontrastVO();
				if(item.getCjkybje() == null || item.getCjkybje().compareTo(UFDouble.ZERO_DBL) == 0){
					continue;
				}
				
				bxcontrastVO.setYbje(item.getCjkybje());
				bxcontrastVO.setCjkybje(item.getCjkybje());
				bxcontrastVO.setCxrq(BXUiUtil.getBusiDate());
				bxcontrastVO.setDeptid(deptid);
				bxcontrastVO.setDjlxbm(djlxbm);
				bxcontrastVO.setJkbxr(jkbxr);
				bxcontrastVO.setJobid(item.getJobid());
				bxcontrastVO.setPk_payorg(pk_payorg);
				bxcontrastVO.setPk_bxd(parentVO.pk_jkbx);
				bxcontrastVO.setPk_org(pk_org);
				bxcontrastVO.setPk_jkd(item.getPk_jkbx());
				bxcontrastVO.setPk_busitem(item.getPk_busitem());
				bxcontrastVO.setSxbz(BXStatusConst.SXBZ_NO);
				bxcontrastVO.setSxrq(null);
				bxcontrastVO.setSzxmid(item.getSzxmid());
				bxcontrastVO.setHkybje(item.getHkybje() == null ? UFDouble.ZERO_DBL : item.getHkybje());
				bxcontrastVO.setFyybje(bxcontrastVO.getCjkybje().sub(bxcontrastVO.getHkybje()));
				bxcontrastVO.setBxdjbh(parentVO.getDjbh());
				bxcontrastVO.setJkdjbh(djbh);
				list.add(bxcontrastVO);
			}
		}
		return list;
	}

	public ContrastDialog(java.awt.Frame owner) {
		super(owner);
	}

	private nc.ui.pub.beans.UIButton getBtnCancel() {
		if (btnCancel == null) {
			try {
				btnCancel = new nc.ui.pub.beans.UIButton();
				btnCancel.setName("BnCancel");
				btnCancel.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000008")/*
																											 * @
																											 * res
																											 * "取消"
																											 */);
				btnCancel.setBounds(435, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnCancel;
	}

	private nc.ui.pub.beans.UIButton getBtnConfirm() {
		if (btnConfirm == null) {
			try {
				btnConfirm = new nc.ui.pub.beans.UIButton();
				btnConfirm.setName("BnConfirm");
				btnConfirm.setText(nc.ui.ml.NCLangRes.getInstance().getStrByID("2006030201", "UC001-0000044")/*
																											 * @
																											 * res
																											 * "确定"
																											 */);
				btnConfirm.setBounds(355, 15, 70, 22);
			} catch (java.lang.Throwable ivjExc) {
				handleException(ivjExc);
			}
		}
		return btnConfirm;
	}

	@Override
	public String getTitle() {
		return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000182")/*
																							 * @
																							 * res
																							 * "冲借款"
																							 */;
	}

	public void actionPerformed(ActionEvent e) {
		if (this.getOwnedWindows() != null && this.getOwnedWindows().length != 0) {// 判断如果有窗口显示，则不不响应
			for (Window w : this.getOwnedWindows()) {
				if (w instanceof UIDialog) {
					if (w.isShowing()) {
						return;
					}
				}
			}
		}
		if (e.getSource().equals(getBtnConfirm())) {

			String errormsg = validateData();
			if (errormsg != null) {
				BXUiUtil.showUif2DetailMessage(this, null, errormsg);
				return;
			}
			closeOK();
			destroy();
		} else if (e.getSource().equals(getBtnCancel())) {
			closeCancel();
			destroy();
		} else if (e.getSource().equals(getBtnQuery())) {
			onBoquery();
		} else if(e.getSource().equals(getBtnAdvancedQuery())){
			onAvdquery();
		}
	}

	private void onAvdquery() {
		if (UIDialog.ID_OK == getQryDlg().showModal()) {
			//高级查询逻辑
			BxcontrastVO[] oldcontrastvos = null;
			if (getModel().getUiState() == UIState.EDIT) {
				JKBXVO selectedData = (JKBXVO) getModel().getSelectedData();
				if (selectedData != null) {
					oldcontrastvos = selectedData.getContrastVO();
				}
			}
			try {
				initData(bxvo, oldcontrastvos, getQryDlg().getWhereSQL());
			} catch (BusinessException e) {
				ExceptionHandler.handleRuntimeException(e);
			}
		}
	}
	/**
	 * 增加查询对话框
	 */
	private QueryConditionDLG queryDialog;
	private QueryConditionDLG getQryDlg() {
			if(queryDialog==null){
				TemplateInfo tempinfo = new TemplateInfo();
				tempinfo.setPk_Org(getModel().getContext().getPk_group());
				tempinfo.setFunNode(nodecode);
				tempinfo.setUserid(getModel().getContext().getPk_loginUser());
				tempinfo.setNodekey("20110cjkq");
				queryDialog = new QueryConditionDLG(getEditor(), null,
						tempinfo,
						nc.ui.ml.NCLangRes.getInstance().getStrByID("common", "UC000-0002782"));
				queryDialog.registerCriteriaEditorListener(new CJKQueryCriteriaChangedListener(getModel()));
				
			}
		return queryDialog;
	}

	private String validateData() {
		UFDouble contrastTotal = getContrastTotal();
		UFDouble hkTotal = getHkTotal();
		UFDouble bxje = getBxje();
		// 修改设置还款金额
		if (contrastTotal.compareTo(bxje) > 0) { // 冲借款大于报销金额
			if (hkTotal.compareTo(contrastTotal.sub(bxje)) != 0) {
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000376")/*
																									 * @
																									 * res
																									 * "冲借款信息错误：合计还款金额应等于冲借款总金额-报销总金额"
																									 */;
			}
		} else {
			if (hkTotal.compareTo(new UFDouble(0)) > 0) {
				return nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000377")/*
																									 * @
																									 * res
																									 * "冲借款信息错误：冲借款金额小于报销总金额，不能进行还款"
																									 */;
			}
		}
		// 借款单和报销单的主组织本位币不一致时，不可以冲借款
		try {
			String bx_pkpog = getBxvo().getParentVO().getPk_org();
			String bxCurrPK = Currency.getLocalCurrPK(bx_pkpog);
			StringBuffer msginfo = new StringBuffer();
			for (Entry<String, Map<String, BXBusItemVO>> selectedvo : selectedVO.entrySet()) {
				String jk_pkorg = jkdMap.get(selectedvo.getKey()).getPk_org();
				String jk_djbh = jkdMap.get(selectedvo.getKey()).getDjbh();
				String jkCurrPK = Currency.getLocalCurrPK(jk_pkorg);
				if (!bxCurrPK.equals(jkCurrPK)) {
					msginfo.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000900")
							+ jk_djbh + nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000901"));
					msginfo.append("\n");
				}
			}
			
			if (msginfo.length() != 0) {
				return msginfo.toString();
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleRuntimeException(e);
		}
		return null;
	}

	private List<JKBXHeaderVO> getJkdsByQuery() throws BusinessException {
//		List<JKBXHeaderVO> jkds = new ArrayList<JKBXHeaderVO>();
	    List<JKBXHeaderVO> jkds = Collections.emptyList();
		String usertext = getusertext().getRefPK();
		UFDate fromtime = getfromtimetext().getDateBegin();
		UFDate totime = gettotimetext().getDateEnd();
		String errormsg = null;
		
		if (fromtime != null && fromtime.compareTo(totime) > 0) {
			
			errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0", "02011002-0044")/*
																											 * @
																											 * res
																											 * "开始日期不能大于结束日期，请重新输入!"
																											 */;
			BXUiUtil.showUif2DetailMessage(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0147"), errormsg);
			
			
		}
		if (usertext == null || usertext.trim().equals("")) {
			errormsg = nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000378")/*
																									 * @
																									 * res
																									 * "请先选择借款人名称"
																									 */;
			BXUiUtil.showUif2DetailMessage(this, nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0", "0201107-0147"), errormsg);
			return null;
		} else {
			String sql = " zb.jkbxr='" + usertext + "'";
			if (totime != null && !"".equals(totime.toString()))
				sql += " and zb.djrq<='" + totime + "'";
			if (fromtime != null && !"".equals(fromtime.toString()))
				sql += " and zb.djrq>='" + fromtime + "'";

			jkds = getBxUIControl().getJKD(bxvo, BXUiUtil.getSysdate(), sql);
			
		}
		
		return jkds;
	}

	private void onBoquery() {
		Collection<JKBXHeaderVO> queryContrastVO = null;
		try {
			queryContrastVO = getJkdsByQuery();
		} catch (BusinessException e) {
			throw new BusinessRuntimeException(e.getMessage(), e);
		}
		if (queryContrastVO != null ) {
			for (JKBXHeaderVO svo : queryContrastVO) {
				boolean include = false;
				String newprimaryKey = svo.getPrimaryKey();
				for (SuperVO supervo : getJkds()) {
					String primaryKey = supervo.getPrimaryKey();
					if (newprimaryKey.equals(primaryKey)) {
						include = true;
					}
				}
				if (!include) {
					getJkds().add(svo);
				}
			}
			initJkds(getBxvo(), (List<JKBXHeaderVO>) queryContrastVO);
		}
	}

	public void valueChanged(ValueChangedEvent event) {
	}

	private void handleException(java.lang.Throwable e) {
		ExceptionHandler.consume(e);
	}

	public BillForm getEditor() {
		return editor;
	}

	public JKBXVO getBxvo() {
		return bxvo;
	}

	public void setBxvo(JKBXVO bxvo) {
		this.bxvo = bxvo;
	}

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public String getPkCorp() {
		return pkCorp;
	}

	public void setPkCorp(String pkCorp) {
		this.pkCorp = pkCorp;
	}

	public List<JKBXHeaderVO> getJkds() {
		return jkds;
	}

	public void setJkds(List<JKBXHeaderVO> bxvos) {
		this.jkds = bxvos;
	}

	private Object getHeadColumnValue(String key, int row) {
		return getListPanel().getHeadBillModel().getValueAt(row, key);
	}

	protected void setHeadColumnValue(String key, int row, Object value) {
		getListPanel().getHeadBillModel().setValueAt(value, row, key);
	}

	private void setBodyColumnValue(String key, String compKey, Object compValue, Object value) {

		int row = 0;

		boolean equal = false;

		BillModel bodyBillModel = getListPanel().getBodyBillModel();
		for (; row < bodyBillModel.getRowCount(); row++) {
			if (bodyBillModel.getValueAt(row, compKey).equals(compValue)) {
				equal = true;
				break;
			}
		}

		if (equal) {
			bodyBillModel.setValueAt(value, row, key);
		}
	}

	/**
	 * 按照compKey,compValue找到对应的单据行 将key列设置为值value
	 * 
	 * @param key
	 * @param compKey
	 * @param compKey
	 * @param value
	 */

	private void setHeadColumnValue(String key, String compKey, Object compValue, Object value) {

		int row = 0;

		boolean equal = false;

		BillModel headBillModel = getListPanel().getHeadBillModel();
		for (; row < headBillModel.getRowCount(); row++) {
			if (headBillModel.getValueAt(row, compKey).equals(compValue)) {
				equal = true;
				break;
			}
		}

		if (equal) {
			headBillModel.setValueAt(value, row, key);
		}
	}

	private Object getHeadColumnValue(String key, String compKey, Object compValue) {

		int row = 0;

		boolean equal = false;

		BillModel headBillModel = getListPanel().getHeadBillModel();
		for (; row < headBillModel.getRowCount(); row++) {
			if (headBillModel.getValueAt(row, compKey).equals(compValue)) {
				equal = true;
				break;
			}
		}

		if (equal) {
			return headBillModel.getValueAt(row, key);
		}

		return null;
	}

	private void setHeadValueVos(List<JKBXHeaderVO> jkVos) {
		BillModel headBillModel = getListPanel().getHeadBillModel();
		headBillModel.clearBodyData();
		getListPanel().getBodyBillModel().clearBodyData();

		try {
			for (int i = jkVos.size() - 1; i >= 0; i--) {
				if (jkVos.get(i).getYbye() == null || jkVos.get(i).getYbye().compareTo(UFDouble.ZERO_DBL) == 0) {
					jkVos.remove(i);
				}
			}
			getListPanel().setHeaderValueVO(jkVos.toArray(new JKBXHeaderVO[] {}));
			getListPanel().getHeadBillModel().loadLoadRelationItemValue();
		} catch (Throwable e) {
			ExceptionHandler.consume(e);
		}

		// 加载公式
		try {
			headBillModel.execLoadFormula();
		} catch (Exception e) {
			ExceptionHandler.consume(e);
		}
	}
	
	private boolean isBodyRowSelected(int row){
		if(getListPanel().getBodyBillModel().getRowAttribute(row).getRowState() == BillModel.SELECTED){
			
			return true;
		}
		
		return false;
	}
	
	private int getBodyRowState(int row){
		if(getListPanel().getBodyBillModel() != null){
			return getListPanel().getBodyBillModel().getRowAttribute(row).getRowState();
		}
		
		return -1;
	}
	
	private void setBodyRowState(int row, int state){
		if(getListPanel().getBodyBillModel() != null){
			getListPanel().getBodyBillModel().getRowAttribute(row).setRowState(state);
		}
	}
	
	private Object getBodyValue(int row, String key) {
		if(row < 0){
			return null;
		}
		return getListPanel().getBodyBillModel().getValueAt(row, key);
	}
	
	private void setBodyValue(Object value, int row, String key) {
		if (row < 0) {
			return;
		}
		getListPanel().getBodyBillModel().setValueAt(value, row, key);
	}
}