package nc.ui.erm.billpub.view.eventhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
/**
 * ����༭�¼�������
 * @author wangled
 *
 */
public class BodyEventHandleUtil {

	private ErmBillBillForm editor = null;

	public BodyEventHandleUtil(ErmBillBillForm editor) {
		this.editor = editor;
	}

	/**
	 * �������е�ԭ�ҽ�����������֧������ĸ�ֵ�е�ĳ��ֵ�����仯ʱ ���ø÷������¼���������ֵ
	 * 
	 * @param key
	 *            �����仯��ֵ
	 * @param row
	 *            �����к�
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

		// ���ԭ�ҽ����������仯
		if (key.equals(BXBusItemVO.YBJE) || key.equals(BXBusItemVO.CJKYBJE)) {
			// ���ԭ�ҽ����ڳ�����
			if (ybje.getDouble() > cjkybje.getDouble()) {
				panel.setBodyValueAt(ybje.sub(cjkybje), row,BXBusItemVO.ZFYBJE,currpage);// ֧�����=ԭ�ҽ��-������
				panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.HKYBJE,currpage);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE,currpage);
			} else {
				panel.setBodyValueAt(cjkybje.sub(ybje), row,BXBusItemVO.HKYBJE,currpage);// ������=������-ԭ�ҽ��
				panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.ZFYBJE,currpage);
				panel.setBodyValueAt(cjkybje, row, BXBusItemVO.CJKYBJE,currpage);
			}
		} else if (key.equals(BXBusItemVO.ZFYBJE)) {// �����֧�������仯
			if (zfybje.toDouble() > ybje.toDouble()) {// ֧�����ܴ���ԭ�ҽ�����֧������ֵ��Ϊԭ�ҽ���ֵ
				zfybje = ybje;
				panel.setBodyValueAt(zfybje, row, BXBusItemVO.ZFYBJE,currpage);
			}
			panel.setBodyValueAt(ybje.sub(zfybje), row, BXBusItemVO.CJKYBJE,currpage);// ������=ԭ�ҽ��-֧�����
			panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.HKYBJE,currpage);
		} else if (key.equals(BXBusItemVO.HKYBJE)) {// ����ǻ�������仯
			panel.setBodyValueAt(ybje.add(hkybje), row, BXBusItemVO.CJKYBJE,currpage);// ������=ԭ�ҽ��+������
			panel.setBodyValueAt(UFDouble.ZERO_DBL, row, BXBusItemVO.ZFYBJE,currpage);
		}
		
		panel.setBodyValueAt(ybje, row, BXBusItemVO.YBYE,currpage);// ԭ�����=ԭ�ҽ��

		String bzbm = null;
		if (getHeadValue(JKBXHeaderVO.BZBM) != null) {
			bzbm = getHeadValue(JKBXHeaderVO.BZBM).toString();
		}
		// ������֯��Ϊ��ʱ����ȥ����ԭ�Ҽ��㱾�ң����򱾱Ҳ����㡣
		if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
			transFinYbjeToBbje(row, bzbm,currpage);
		}
	}

	/**
	 * ���ر�����ֵ���գ�����0
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
	 * �������ҳǩ������ԭ�ҽ��㱾�ҽ��
	 * 
	 * @param row
	 *            �����к�
	 * @param bzbm
	 *            ���ֱ���
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
			 * ����ȫ�ּ��ű�λ��
			 * 
			 * @param amout: ԭ�ҽ�� localAmount: ���ҽ�� currtype: ���� data:����  pk_org����֯
			 * @return ȫ�ֻ��߼��ŵı��� money
			 */
			// ��Ҫ�����ű��Һ�ȫ�ֱ������õ�������
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
			
			// ��Ҫ������֧�����Һ�ȫ��֧���������õ�������
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, zfybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currpage).setValueAt(money[0], row, JKBXHeaderVO.GROUPZFBBJE);
			panel.getBillModel(currpage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALZFBBJE);
			// ��Ҫ�����ų���Һ�ȫ�ֳ�������õ�������
			je = Currency.computeYFB(getPk_org(), Currency.Change_YBCurr, bzbm, cjkybje, null, null, null, hl,
					BXUiUtil.getSysdate());
			money = Currency.computeGroupGlobalAmount(je[0], je[2], bzbm, BXUiUtil.getSysdate(),
					getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG).getValueObject().toString(), getBillCardPanel()
							.getHeadItem(JKBXHeaderVO.PK_GROUP).getValueObject().toString(), globalhl, grouphl);

			panel.getBillModel(currpage).setValueAt(money[0], row, JKBXHeaderVO.GROUPCJKBBJE);
			panel.getBillModel(currpage).setValueAt(money[1], row, JKBXHeaderVO.GLOBALCJKBBJE);
			// ��Ҫ�����Ż���Һ�ȫ�ֻ�������õ�������
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
	 * ����ʱ����дҵ���н��ʱ��������ڣ����¼���������Ȳ���
	 * @param bodyItem
	 * @throws ValidationException
	 * @throws BusinessException
	 */
	public void doContract(BillItem bodyItem, BillEditEvent eve) throws ValidationException, BusinessException {
		BillModel billModel = getBillCardPanel().getBillModel(BXConstans.CONST_PAGE);
		if(billModel == null){
			// �����ڳ���ҳǩ�����������
			return ;
		}
		UFDouble ybje = (UFDouble)getBillCardPanel().getBodyValueAt(eve.getRow(), BXBusItemVO.YBJE);
		
		if(ybje != null && !ybje.equals(UFDouble.ZERO_DBL) //ԭ��Ϊ0ʱ������������
				&& BXConstans.BX_DJDL.equals(((JKBXVO)editor.getValue()).getParentVO().getDjdl())//����ʱ���г�������
				&& (bodyItem.getDataType() == IBillItem.DECIMAL //Ϊ��ֵ����ʱ���б仯
						|| bodyItem.getDataType() == IBillItem.INTEGER)){
			BxcontrastVO[] bxcontrastVO = (BxcontrastVO[]) billModel.getBodyValueVOs(BxcontrastVO.class.getName());

			if (bxcontrastVO != null && bxcontrastVO.length > 0) {
				ContrastAction.doContrastToUI(getBillCardPanel(), ((JKBXVO) editor.getJKBXVO()),
						Arrays.asList(bxcontrastVO), editor);
			}
		}
	}
	
	/**
	 * ���屨������
	 * TODO:shiwenli������
	 */
	public void doBodyReimAction() {
		editor.doBodyReimAction();
	}
	
	
	/**
	 * �û��Զ���
	 * 
	 * @param pos
	 * @param key
	 * @param def
	 * @return
	 */
	public String getUserdefine(int pos, String key, int def) {
		return BXUiUtil.getUserdefine(pos, key, def, getBillCardPanel());
	}
	
	/**
	 * �༭��ʽ2ִ��<br>
	 * ��ʽ������ģ���Զ���2λ��
	 * @param formula ��ʽ
	 * @param skey �ֶ�key
	 * @param srow ��
	 * @param stable tablecode
	 * @param svalue ���ֶε�ֵ
	 */
	public void doFormulaAction(String formula, String skey, int srow,
			String stable, Object svalue) {
		if (formula == null)
			return;
		try {
			/**
			 * toHead(headKey,sum(%row%,%key%,%table%)) --- ��
			 * sum(%row%,%key%,%table%) ��ʽ��ֵ��ֵ����ͷheadKey;
			 * 
			 * toBody(%row%,%key%,%table%,sum(%row%,%key%,%table%)) --- ��
			 * sum(%row%,%key%,%table%) ��ʽ��ֵ��ֵ������ tableҳǩ��key�ֶε�row����
			 * 
			 * ���Ի���ʽ��֧�ֱ�ͷ�����壬�����ҳǩ֮��������ݵĴ���
			 * 
			 * sum() �ϼ� min() ���ֵ max() ��Сֵ %key% Ĭ���Ǵ�����ʽ���ֶΣ� ����ֱ��ָ�� %row%
			 * Ĭ���Ǵ�����ʽ���У�����ָ���� ��ͷֱ����-1, �����ж����и�ֵ��ȡֵ����: %all% %table% Ĭ���Ǵ�����ʽ��ҳǩ��
			 * ����ֱ��ָ��
			 * 
			 * ��ʽ�����ڵ���ģ������Զ���2�ϣ����ֶα༭ʱִ��
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

				setHeadValue(headKey, resultvalue);
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
			Object obj = getBillCardPanel().getBillModel(table).getValueObjectAt(row, key);
			value = new Object[] { obj };
		} else if (Integer.valueOf(prow) == -1) { // head
			value = new Object[] { getHeadValue(key) };
		} else {
			row = Integer.parseInt(prow);
			value = new Object[] { getBillCardPanel().getBillModel(table).getValueAt(row, key) };
		}
		if (value == null || value.length == 0)
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
	
	/*
	 * ִ���Զ�����2�Ĺ�ʽ
	 * ���ɾ�С�ճ����
	 * @param pos
	 * @param key
	 * @param def
	 * @return
	 */
	public void exeBodyUserdefine2() {
		if (getBillCardPanel().getBillData().getBillTempletVO() == null
				|| getBillCardPanel().getBillData().getBillTempletVO().getChildrenVO() == null) {
			return;
		}
		
		BillTempletBodyVO[] tbodyvos = (BillTempletBodyVO[]) getBillCardPanel()
				.getBillData().getBillTempletVO().getChildrenVO();
		
		Map<String,String> define2Map = new HashMap<String,String>();
		for (BillTempletBodyVO bodyvo : tbodyvos) {
			if (bodyvo.getPos() == 1) {
				String userDefine2 = bodyvo.getUserdefine2();
				if(userDefine2 != null && userDefine2.length() > 0){
					define2Map.put(bodyvo.getTable_code() + "##" + bodyvo.getItemkey(), userDefine2);
				}
			}
		}
		
		for(Map.Entry<String, String> entry: define2Map.entrySet()){
			String[] names = entry.getKey().split("##");
			doFormulaAction(entry.getValue(), names[1], -1, names[0], null);
		}
	}

	private BillCardPanel getBillCardPanel() {
		return editor.getBillCardPanel();
	}

	public String getPk_org() {
		if (!editor.isVisible()) {
			return null;
		} else if (editor.isVisible()) {
			// ��Ƭ����ȡ��ͷ��֯
			return (String) getBillCardPanel().getHeadItem(JKBXHeaderVO.PK_ORG)
					.getValueObject();
		} else {
			// ��ȡ������ȡĬ�ϵ�ҵ��Ԫ
			return BXUiUtil.getBXDefaultOrgUnit();
		}
	}
	
	/**
	 * ��ȡ����ֵ
	 * 
	 * @param row �к�
	 * @param key �ֶ�key
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
	 * ��ñ�ͷֵ
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
	 * �������wherepart
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
