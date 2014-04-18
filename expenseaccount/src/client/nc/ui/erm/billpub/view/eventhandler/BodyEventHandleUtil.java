package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JComponent;

import nc.itf.fi.pub.Currency;
import nc.ui.bd.ref.AbstractRefModel;
import nc.ui.er.util.BXUiUtil;
import nc.ui.erm.billpub.action.ContrastAction;
import nc.ui.erm.billpub.view.ErmBillBillForm;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillEditEvent;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.IBillItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ValidationException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
/**
 * 表体编辑事件工具类
 * @author wangled
 *
 */
public class BodyEventHandleUtil {

	private ErmBillBillForm editor = null;

	public BodyEventHandleUtil(ErmBillBillForm editor) {
		this.editor = editor;
	}

	/**
	 * 当表体中的原币金额，冲借款金额，还款金额，支付金额四个值中的某个值发生变化时 调用该方法重新计算其他的值
	 * 
	 * @param key
	 *            发生变化的值
	 * @param row
	 *            表体行号
	 * @author zhangxiao1
	 */
	public void modifyFinValues(String key, int row,BillEditEvent e) {
		BillCardPanel panel = getBillCardPanel();
		String currpage = null;
		if(e!=null){
			currpage = e.getTableCode();
		}else{
			currpage = panel.getCurrentBodyTableCode();
		}
		
		UFDouble ybje = getAmountValue(row, panel, BXBusItemVO.YBJE,currpage);
		UFDouble cjkybje = getAmountValue(row, panel, BXBusItemVO.CJKYBJE,currpage);
		UFDouble zfybje = getAmountValue(row, panel, BXBusItemVO.ZFYBJE,currpage);
		UFDouble hkybje = getAmountValue(row, panel, BXBusItemVO.HKYBJE,currpage);

		// 如果原币金额或冲借款金额发生变化
		if (key.equals(BXBusItemVO.YBJE) || key.equals(BXBusItemVO.CJKYBJE)) {
			// 如果原币金额大于冲借款金额
			if (ybje.getDouble() > cjkybje.getDouble()) {
				panel.setBodyValueAt(ybje.sub(cjkybje), row,BXBusItemVO.ZFYBJE,currpage);// 支付金额=原币金额-冲借款金额
				panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.HKYBJE,currpage);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE,currpage);
			} else {
				panel.setBodyValueAt(cjkybje.sub(ybje), row,BXBusItemVO.HKYBJE,currpage);// 还款金额=冲借款金额-原币金额
				panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.ZFYBJE,currpage);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE,currpage);
			}
		} else if (key.equals(BXBusItemVO.ZFYBJE)) {// 如果是支付金额发生变化
			if (zfybje.toDouble() > ybje.toDouble()) {// 支付金额不能大于原币金额，否则将支付金额的值置为原币金额的值
				zfybje = ybje;
				panel.setBodyValueAt(zfybje, row, BXBusItemVO.ZFYBJE,currpage);
			}
			panel.setBodyValueAt(ybje.sub(zfybje), row, BXBusItemVO.CJKYBJE,currpage);// 冲借款金额=原币金额-支付金额
			panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.HKYBJE,currpage);
		} else if (key.equals(BXBusItemVO.HKYBJE)) {// 如果是还款金额发生变化
			panel.setBodyValueAt(ybje.add(hkybje), row, BXBusItemVO.CJKYBJE,currpage);// 冲借款金额=原币金额+还款金额
			panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.ZFYBJE,currpage);
		}
		
		panel.setBodyValueAt(ybje, row, BXBusItemVO.YBYE,currpage);// 原币余额=原币金额

		String bzbm = null;
		if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
			bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
		}
		// 财务组织不为空时，再去根据原币计算本币，否则本币不计算。
		if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
			transFinYbjeToBbje(row, bzbm,currpage);
		}
	}

	/**
	 * 返回表体金额值，空，返回0
	 * 
	 * @param row
	 * @param panel
	 * @param itemKey
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private UFDouble getAmountValue(int row, BillCardPanel panel, String itemKey,String currpage) {
		Object bodyValue = panel.getBillModel(currpage).getValueAt(row, itemKey);
		return bodyValue == null ? UFDouble.ZERO_DBL: (UFDouble) bodyValue;
	}

	/**
	 * 表体财务页签，根据原币金额换算本币金额
	 * 
	 * @param row
	 *            表体行号
	 * @param bzbm
	 *            币种编码
	 * @author zhangxiao1
	 */
	protected void transFinYbjeToBbje(int row, String bzbm,String currpage) {
		BillCardPanel panel = getBillCardPanel();
		
		UFDouble ybje = (UFDouble) panel.getBillModel(currpage).getValueAt(row,BXBusItemVO.YBJE);
		UFDouble cjkybje = (UFDouble) panel.getBillModel(currpage).getValueAt(row, BXBusItemVO.CJKYBJE);
		UFDouble hkybje = (UFDouble) panel.getBillModel(currpage).getValueAt(row, BXBusItemVO.HKYBJE);
		UFDouble zfybje = (UFDouble) panel.getBillModel(currpage).getValueAt(row, BXBusItemVO.ZFYBJE);
		UFDouble hl = null;
		UFDouble globalhl = null;
		UFDouble grouphl = null;
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject() != null) {
			hl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.BBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject() != null) {
			grouphl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GROUPBBHL).getValueObject().toString());
		}
		if (getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject() != null) {
			globalhl = new UFDouble(getBillCardPanel().getHeadItem(JKBXHeaderVO.GLOBALBBHL).getValueObject().toString());
		}
		try {
			UFDouble[] bbje = Currency.computeYFB(getPk_org(),
					Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			panel.getBillModel(currpage).setValueAt(bbje[2], row,JKBXHeaderVO.BBJE);
			panel.getBillModel(currpage).setValueAt(bbje[2], row,JKBXHeaderVO.BBYE);
			
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
					bzbm, cjkybje, null, null, null, hl, BXUiUtil.getSysdate());
			panel.getBillModel(currpage).setValueAt(bbje[2], row,JKBXHeaderVO.CJKBBJE);
			
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
					bzbm, hkybje, null, null, null, hl, BXUiUtil.getSysdate());
			panel.getBillModel(currpage).setValueAt(bbje[2], row,JKBXHeaderVO.HKBBJE);
			bbje = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr,
					
					bzbm, zfybje, null, null, null, hl, BXUiUtil.getSysdate());
			panel.getBillModel(currpage).setValueAt(bbje[2], row,JKBXHeaderVO.ZFBBJE);

			/**
			 * 计算全局集团本位币
			 * 
			 * @param amout: 原币金额 localAmount: 本币金额 currtype: 币种 data:日期  pk_org：组织
			 * @return 全局或者集团的本币 money
			 */
			// 需要将集团本币和全局本币设置到界面上
			UFDouble[] je = Currency.computeYFB(getPk_org(),Currency.Change_YBCurr, bzbm, ybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			UFDouble[] money = Currency.computeGroupGlobalAmount(je[0], je[2],bzbm, BXUiUtil.getSysdate(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel().getHeadItem(
							JKBXHeaderVO.PK_GROUP).getValueObject().toString(),
					globalhl, grouphl);
			panel.getBillModel(currpage).setValueAt(money[0], row,JKBXHeaderVO.GROUPBBJE);
			panel.getBillModel(currpage).setValueAt(money[1], row,JKBXHeaderVO.GLOBALBBJE);
			panel.getBillModel(currpage).setValueAt(money[0], row,JKBXHeaderVO.GROUPBBYE);
			panel.getBillModel(currpage).setValueAt(money[1], row,JKBXHeaderVO.GLOBALBBYE);
			
			// 需要将集团支付本币和全局支付本币设置到界面上
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currpage).setValueAt(money[0], row, JKBXHeaderVO.GROUPZFBBJE);
			panel.getBillModel(currpage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALZFBBJE);
			// 需要将集团冲借款本币和全局冲借款本币设置到界面上
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currpage).setValueAt(money[0], row, JKBXHeaderVO.GROUPCJKBBJE);
			panel.getBillModel(currpage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALCJKBBJE);
			// 需要将集团还款本币和全局还款本币设置到界面上
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, hkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currpage).setValueAt(money[0], row, JKBXHeaderVO.GROUPHKBBJE);
			panel.getBillModel(currpage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALHKBBJE);
		} catch (BusinessException e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	
	
	/**
	 * 报销时，填写业务行金额时，冲借款存在，重新计算冲借款分配等操作
	 * @param bodyItem
	 * @throws ValidationException
	 * @throws BusinessException
	 */
	public void doContract(BillItem bodyItem, BillEditEvent eve) throws ValidationException, BusinessException {
		BillModel billModel = getBillCardPanel().getBillModel(BXConstans.CONST_PAGE);
		if(billModel == null){
			// 不存在冲销页签情况，不处理
			return ;
		}
		UFDouble ybje = (UFDouble)getBillCardPanel().getBodyValueAt(eve.getRow(), BXBusItemVO.YBJE);
		
		if(ybje != null && !ybje.equals(UFDouble.ZERO_DBL) //原币为0时不做冲销动作
				&& BXConstans.BX_DJDL.equals(((JKBXVO)editor.getValue()).getParentVO().getDjdl())//报销时才有冲销动作
				&& (bodyItem.getDataType() == IBillItem.DECIMAL //为数值类型时才有变化
						|| bodyItem.getDataType() == IBillItem.INTEGER)){
			BxcontrastVO[] bxcontrastVO = (BxcontrastVO[]) billModel.getBodyValueVOs(BxcontrastVO.class.getName());

			if (bxcontrastVO != null && bxcontrastVO.length > 0) {
				ContrastAction.doContrastToUI(getBillCardPanel(), ((JKBXVO) editor.getJKBXVO()),
						Arrays.asList(bxcontrastVO), editor);
			}
		}
	}
	
	/**
	 * 表体报销规则
	 * TODO:shiwenli来处理
	 */
	public void doBodyReimAction() {
		editor.doBodyReimAction();
	}
	
	
	/**
	 * 用户自定义
	 * 
	 * @param pos
	 * @param key
	 * @param def
	 * @return
	 */
	public String getUserdefine(int pos, String key, int def) {
		return BXUiUtil.getUserdefine(pos, key, def, getBillCardPanel());
	}

	public void doFormulaAction(String formula, String skey, int srow,
			String stable, Object svalue) {
		if (formula == null)
			return;
		try {
			/**
			 * toHead(headKey,sum(%row%,%key%,%table%)) --- 将
			 * sum(%row%,%key%,%table%) 公式的值赋值给表头headKey;
			 * 
			 * toBody(%row%,%key%,%table%,sum(%row%,%key%,%table%)) --- 将
			 * sum(%row%,%key%,%table%) 公式的值赋值给表体 table页签的key字段的row行上
			 * 
			 * 个性化公式：支持表头，表体，表体各页签之间进行数据的传递
			 * 
			 * sum() 合计 min() 最大值 max() 最小值 %key% 默认是触发公式的字段， 可以直接指定 %row%
			 * 默认是触发公式的行，可以指定， 表头直接用-1, 所有行都进行赋值和取值则用: %all% %table% 默认是触发公式的页签，
			 * 可以直接指定
			 * 
			 * 公式定义在单据模板表体自定义2上，在字段编辑时执行
			 * 
			 * */
			formula = formula.replace('(', '#');
			formula = formula.replace(')', '#');
			formula = formula.replace(',', '#');
			formula = formula.trim();

			if (formula.startsWith("toHead")) {
				String[] values = formula.split("#");
				String headKey = values[1];
				String func = values[2];
				String prow = values[3];
				String pkey = values[4];
				String ptab = values[5];

				String key = pkey.equals("%key%") ? skey : pkey;
				String table = ptab.equals("%table%") ? stable : ptab;

				Object resultvalue = getResultValue(func, svalue, prow, key,
						table);

				if (resultvalue != null) {
					setHeadValue(headKey, resultvalue);
				}
			}
			if (formula.startsWith("toBody")) {
				String[] values = formula.split("#");
				String bodyRow = values[1];
				String bodyKey = values[2];
				String bodyTab = values[3];
				String func = values[4];
				String prow = values[5];
				String pkey = values[6];
				String ptab = values[7];

				String key = pkey.equals("%key%") ? skey : pkey;
				String table = ptab.equals("%table%") ? stable : ptab;

				Object resultvalue = getResultValue(func, svalue, prow, key,
						table);

				BillItem item = getBillCardPanel()
						.getBodyItem(bodyTab, bodyKey);
				BillModel bm = getBillCardPanel().getBillModel(bodyTab);

				if (resultvalue != null) {
					bodyKey = bodyKey.equals("%key%") ? skey : bodyKey;
					bodyTab = bodyTab.equals("%table%") ? stable : bodyTab;

					if (bodyRow.equals("%all%")) {
						int rowCount = getBillCardPanel().getRowCount(bodyTab);
						for (int i = 0; i < rowCount; i++) {
							getBillCardPanel().setBodyValueAt(resultvalue, i,
									bodyKey, bodyTab);

							if (bm != null)
								bm.execFormulas(i, item.getEditFormulas());
						}
					} else if (bodyRow.equals("%row%")) {
						getBillCardPanel().setBodyValueAt(resultvalue, srow,
								bodyKey, bodyTab);

						if (bm != null)
							bm.execFormulas(srow, item.getEditFormulas());
					} else {
						getBillCardPanel().setBodyValueAt(resultvalue,
								Integer.parseInt(bodyRow), bodyKey, bodyTab);

						if (bm != null)
							bm.execFormulas(Integer.parseInt(bodyRow), item
									.getEditFormulas());
					}

				}
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	private Object getResultValue(String func, Object svalue, String prow,
			String key, String table) {
		int row = 0;
		Object[] value = null;
		if (prow.equals("%all%")) {
			BillModel billModel = getBillCardPanel().getBillModel(table);
			int rowCount = getBillCardPanel().getRowCount(table);
			List<Object> arrayValue = new ArrayList<Object>();
			for (int i = 0; i < rowCount; i++) {
				Object valueAt = billModel.getValueAt(i, key);
				if (valueAt != null && !valueAt.equals("")) {
					arrayValue.add(valueAt);
				}
			}
			value = arrayValue.toArray(new Object[] {});
		} else if (prow.equals("%row%")) {
			value = new Object[] { svalue };
		} else if (Integer.valueOf(prow) == -1) { // head
			value = new Object[] { getHeadValue(key) };
		} else {
			row = Integer.parseInt(prow);
			value = new Object[] { getBillCardPanel().getBillModel(table).getValueAt(row, key) };
		}
		if (value == null)
			return null;
		if (value.length <= 1)
			return value[0];
		if (func.equals("sum")) {
			Object revalue = null;
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = ((UFDouble) revalue).add((UFDouble) sv);
				}
				if (sv instanceof Integer) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = Integer.valueOf(((Integer) revalue).intValue()
								+ (Integer.parseInt(sv.toString())));
				}
			}
			return revalue;
		}
		if (func.equals("avg")) {
			Object revalue = null;
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = ((UFDouble) revalue).add((UFDouble) sv);
				}
				if (sv instanceof Integer) {
					if (revalue == null)
						revalue = sv;
					else
						revalue = Integer.valueOf(((Integer) revalue).intValue()
                                + (Integer.parseInt(sv.toString())));
				}
			}
			if (revalue == null)
				return null;
			if (revalue instanceof UFDouble)
				return ((UFDouble) revalue).div(value.length);
			if (revalue instanceof Integer)
				return ((Integer) revalue) / (value.length);
		}
		if (func.equals("min")) {
			Object revalue = value[0];
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					UFDouble new_name = (UFDouble) sv;
					if (new_name.compareTo(revalue) < 0)
						revalue = new_name;
				}
				if (sv instanceof Integer) {
					Integer new_name = (Integer) sv;
					if (new_name.compareTo((Integer) revalue) < 0)
						revalue = new_name;
				}
				if (sv instanceof UFDate) {
					UFDate new_name = (UFDate) sv;
					// FIXME
					if (new_name.compareTo((UFDate) revalue) < 0)
						revalue = new_name;
				}
				if (sv instanceof String) {
					String new_name = (String) sv;
					if (new_name.compareTo((String) revalue) < 0)
						revalue = new_name;
				}
			}
			return revalue;
		}
		if (func.equals("max")) {
			Object revalue = value[0];
			for (Object sv : value) {
				if (sv == null)
					continue;
				if (sv instanceof UFDouble) {
					UFDouble new_name = (UFDouble) sv;
					if (new_name.compareTo(revalue) > 0)
						revalue = new_name;
				}
				if (sv instanceof Integer) {
					Integer new_name = (Integer) sv;
					if (new_name.compareTo((Integer) revalue) > 0)
						revalue = new_name;
				}
				if (sv instanceof UFDate) {
					UFDate new_name = (UFDate) sv;
					if (new_name.compareTo((UFDate) revalue) > 0)
						revalue = new_name;
				}
				if (sv instanceof String) {
					String new_name = (String) sv;
					if (new_name.compareTo((String) revalue) > 0)
						revalue = new_name;
				}
			}
			return revalue;
		}
		return value;
	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}

	public String getPk_org() {
		if (!editor.isVisible()) {
			return null;
		} else if (editor.isVisible()) {
			// 卡片界面取表头组织
			return (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
					.getValueObject();
		} else {
			// 还取不到，取默认的业务单元
			return BXUiUtil.getBXDefaultOrgUnit();
		}
	}
	
	/**
	 * 获取表体值
	 * 
	 * @param row 行号
	 * @param key 字段key
	 * @return
	 */
	public String getBodyItemStrValue(int row, String key,String currpage) {
		Object obj = getBillCardPanel().getBillModel(currpage).getValueObjectAt(row, key);

		if (obj == null) {
			return null;
		} else if (obj instanceof IConstEnum) {
			return (String) ((IConstEnum) obj).getValue();
		}

		return (String) obj;
	}
	/**
	 * 获得表头值
	 * @param itemKey
	 * @return
	 */
	public String getHeadItemStrValue(String itemKey) {
		BillItem headItem = getBillCardPanel().getHeadItem(itemKey);
		return headItem == null ? null : (String) headItem.getValueObject();
	}
	
	public UIRefPane getBodyItemUIRefPane(final String tableCode, final String key) {
		JComponent component = getBillCardPanel().getBodyItem(tableCode, key).getComponent();
		return component instanceof UIRefPane ?(UIRefPane) component : null;
	}
	
	protected void setHeadValue(String key, Object value) {
		if (getBillCardPanel().getHeadItem(key) != null) {
			getBillCardPanel().getHeadItem(key).setValue(value);
		}
	}
	
	
	public  void setPkOrg2RefModel(UIRefPane refPane, String pk_org) {
		refPane.getRefModel().setPk_org(pk_org);
	}
	/**
	 * 参照添加wherepart
	 * @param refPane
	 * @param pk_org
	 * @param addwherePart
	 */
	public void addWherePart2RefModel(UIRefPane refPane, String pk_org,
			String addwherePart) {
		filterRefModelWithWherePart(refPane, pk_org, null, addwherePart);
	}

	public void setWherePart2RefModel(UIRefPane refPane, String pk_org,
			String wherePart) {
		filterRefModelWithWherePart(refPane, pk_org, wherePart, null);
	}

	public void filterRefModelWithWherePart(UIRefPane refPane, String pk_org,
			String wherePart, String addWherePart) {
		AbstractRefModel model = refPane.getRefModel();
		model.setPk_org(pk_org);
		model.setWherePart(wherePart);
		if (addWherePart != null) {
			model.setPk_org(pk_org);
			model.addWherePart(" and " + addWherePart);
		}
	}
	protected Object getHeadValue(String key) {
		BillItem headItem = getBillCardPanel().getHeadItem(key);
		if (headItem == null) {
			headItem = getBillCardPanel().getTailItem(key);
		}
		if (headItem == null) {
			return null;
		}
		return headItem.getValueObject();
	}
	
	
}
