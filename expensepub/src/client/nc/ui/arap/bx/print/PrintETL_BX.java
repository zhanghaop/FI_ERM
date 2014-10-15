package nc.ui.arap.bx.print;

import static nc.ui.pub.bill.IBillItem.BOOLEAN;
import static nc.ui.pub.bill.IBillItem.COMBO;
import static nc.ui.pub.bill.IBillItem.DATE;
import static nc.ui.pub.bill.IBillItem.DECIMAL;
import static nc.ui.pub.bill.IBillItem.IMAGE;
import static nc.ui.pub.bill.IBillItem.INTEGER;
import static nc.ui.pub.bill.IBillItem.OBJECT;
import static nc.ui.pub.bill.IBillItem.PASSWORDFIELD;
import static nc.ui.pub.bill.IBillItem.STRING;
import static nc.ui.pub.bill.IBillItem.TEXTAREA;
import static nc.ui.pub.bill.IBillItem.TIME;
import static nc.ui.pub.bill.IBillItem.UFREF;
import static nc.ui.pub.bill.IBillItem.USERDEF;

import java.awt.Component;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.table.TableModel;

import nc.ui.fi_print.data.IData;
import nc.ui.fi_print.data.PrintETL;
import nc.ui.pub.beans.UICheckBox;
import nc.ui.pub.beans.UIComboBox;
import nc.ui.pub.beans.UIPasswordField;
import nc.ui.pub.beans.UIRefPane;
import nc.ui.pub.beans.UITextAreaScrollPane;
import nc.ui.pub.bill.BillCardPanel;
import nc.ui.pub.bill.BillItem;
import nc.ui.pub.bill.BillModel;
import nc.ui.pub.bill.BillObjectRefPane;
import nc.ui.pub.bill.BillScrollPane;
import nc.ui.pub.report.ReportBaseClass;
import nc.vo.arap.pub.DefaultCAVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

public class PrintETL_BX extends PrintETL {
	
	public static IData ETL(BillCardPanel card) {
		return ETL_NO_TOTAL(card);
	}
	
	public static IData ETL_NO_TOTAL(BillCardPanel card) {
		if (card instanceof ReportBaseClass) {
			throw new IllegalArgumentException("the templet must be BillCardPanel,not ReportBaseClass");
		}
		DefaultCAVO headvo = null;
//		DefaultCAVO[] bodyvos = null;
		/** *对单据头部、底部的数据执行ETL操作 */
		
		Set<BillItem> htSet = makeHeadHash(card);
		htSet = makeTailHash(htSet,card);
		
		headvo = htSet.size() > 0 ? new DefaultCAVO() : null;

		for (BillItem item : htSet) {
			String key = item.getKey();
			String value = _ht_ETL(item);
			headvo.setAttributeValue(key, value);
		}
		/** 对于单据表体的数据执行ETL操作 */
		String[] bodyTableCodes = card.getBillData().getBodyTableCodes();
		
		List<DefaultCAVO> bodyvec=new ArrayList<DefaultCAVO>();
		
		for(String code:bodyTableCodes){
			
			BillModel model = card.getBillModel(code);

			int rowCount = model == null ? 0 : model.getRowCount();
			
			if(rowCount==0)
				continue;

			/** 表体行 */
			for (int i = 0; i < rowCount; i++) {
				DefaultCAVO defaultCAVO = new DefaultCAVO();
				
				BillItem[] bbs = model.getBodyItems();
				int blen = bbs == null ? 0 : bbs.length;

				for (int j = 0; j < blen; j++) {
					String key = bbs[j].getKey();
					Object rawValue = model.getValueAt(i, j);
					String value;
					if (rawValue == null) {
						value = null;
					} else {
						value = _b_ETL(bbs[j], rawValue);
					}
					defaultCAVO.setAttributeValue(key, value);
				}
				
				defaultCAVO.setAttributeValue("tablecode", code);
				
				bodyvec.add(defaultCAVO);
			}
			
		}
		
		
		return new TempletData_BX(headvo, bodyvec.toArray(new DefaultCAVO[]{}));
	}

//	public static IMetaData ETL_NO_TOTAL_META(BillCardPanel card) {
//		if (card instanceof ReportBaseClass) {
//			throw new IllegalArgumentException("the templet must be BillCardPanel,not ReportBaseClass");
//		}
//		DefaultCAVO headvo = null;
////		DefaultCAVO[] bodyvos = null;
//		/** *对单据头部、底部的数据执行ETL操作 */
//		
//		Set<BillItem> htSet = makeHeadHash(card);
//		htSet = makeTailHash(htSet,card);
//		
//		headvo = htSet.size() > 0 ? new DefaultCAVO() : null;
//
//		for (BillItem item : htSet) {
//			String key = item.getKey();
//			String value = _ht_ETL(item);
//			headvo.setAttributeValue(key, value);
//		}
//		/** 对于单据表体的数据执行ETL操作 */
//		String[] bodyTableCodes = card.getBillData().getBodyTableCodes();
//		
//		List<DefaultCAVO> bodyvec=new ArrayList<DefaultCAVO>();
//		
//		for(String code:bodyTableCodes){
//			
//			BillModel model = card.getBillModel(code);
//
//			int rowCount = model == null ? 0 : model.getRowCount();
//			
//			if(rowCount==0)
//				continue;
//
//			/** 表体行 */
//			for (int i = 0; i < rowCount; i++) {
//				DefaultCAVO defaultCAVO = new DefaultCAVO();
//				
//				BillItem[] bbs = model.getBodyItems();
//				int blen = bbs == null ? 0 : bbs.length;
//
//				for (int j = 0; j < blen; j++) {
//					String key = bbs[j].getKey();
//					Object rawValue = model.getValueAt(i, j);
//					String value;
//					if (rawValue == null) {
//						value = null;
//					} else {
//						value = _b_ETL(bbs[j], rawValue);
//					}
//					defaultCAVO.setAttributeValue(key, value);
//				}
//				
//				defaultCAVO.setAttributeValue("tablecode", code);
//				
//				bodyvec.add(defaultCAVO);
//			}
//			
//		}
//		
//		
//		return new MetaData_BX(headvo, bodyvec.toArray(new DefaultCAVO[]{}));
//	}
	private static Set<BillItem> makeTailHash(Set<BillItem> htSet, BillCardPanel card) {
		BillItem[] hbs = card.getTailItems();

		int hlen = (hbs == null ? 0 : hbs.length);

		for (int i = 0; i < hlen; i++) {
			htSet.add(hbs[i]);
		}
		return htSet;
	}

	private static Set<BillItem> makeHeadHash(BillCardPanel card) {
		BillItem[] hbs = card.getHeadItems();

		Set<BillItem> htSet = new HashSet<BillItem>();
		int hlen = (hbs == null ? 0 : hbs.length);

		for (int i = 0; i < hlen; i++) {
			htSet.add(hbs[i]);
		}
		return htSet;
	}
	
	public static IData ETL_With_Total(BillCardPanel card) {
		if (card instanceof ReportBaseClass) {
			throw new IllegalArgumentException("the templet must be BillCardPanel,not ReportBaseClass");
		}
		DefaultCAVO headvo = null;
//		DefaultCAVO[] bodyvos = null;
		/** *对单据头部、底部的数据执行ETL操作 */
		BillItem[] hbs = card.getHeadItems();
		BillItem[] tbs = card.getTailItems();

		Set<BillItem> htSet = new HashSet<BillItem>();
		int hlen = (hbs == null ? 0 : hbs.length);
		int tlen = (tbs == null ? 0 : tbs.length);

		for (int i = 0; i < hlen; i++) {
			htSet.add(hbs[i]);
		}
		for (int i = 0; i < tlen; i++) {
			htSet.add(tbs[i]);
		}
		headvo = htSet.size() > 0 ? new DefaultCAVO() : null;

		for (BillItem item : htSet) {
			String key = item.getKey();
			String value = _ht_ETL(item);
			headvo.setAttributeValue(key, value);
		}
		/** 对于单据表体的数据执行ETL操作 */

		BillItem[] bbs = card.getBodyItems();
		int blen = bbs == null ? 0 : bbs.length;
//		BillModel model = card.getBillModel();

//		int rowCount = model == null ? 0 : model.getRowCount();

//		bodyvos = (rowCount + _t_rowCount) > 0 ? new DefaultCAVO[rowCount + _t_rowCount] : null;

		String[] bodyTableCodes = card.getBillData().getBodyTableCodes();
		
		List<DefaultCAVO> bodyvec=new ArrayList<DefaultCAVO>();
		
		for(String code:bodyTableCodes){
			
			BillModel model = card.getBillModel(code);

			int rowCount = model == null ? 0 : model.getRowCount();
			
			if(rowCount==0)
				continue;
			
			/** 表体行 */
			for (int i = 0; i < rowCount; i++) {
				DefaultCAVO defaultCAVO = new DefaultCAVO();

				for (int j = 0; j < blen; j++) {
					String key = bbs[j].getKey();
					Object rawValue = model.getValueAt(i, j);
					String value;
					if (rawValue == null) {
						value = null;
					} else {
						value = _b_ETL(bbs[j], rawValue);
					}
					defaultCAVO.setAttributeValue(key, value);
				}
				
				bodyvec.add(defaultCAVO);
			}
			
			BillModel _tm = card.getBillData().getBillModel(code);

			BillScrollPane _bsp = card.getBodyPanel(code);
			
			boolean _showTotal = _bsp == null ? false : _bsp.isTatolRow();
			
			TableModel totalModel = _tm == null ? null : _tm.getTotalTableModel();
			
			int _t_rowCount = _showTotal ? totalModel == null ? 0 : totalModel.getRowCount() : 0;
			
			/** 合计行 */
			for (int i = 0; i < _t_rowCount; i++) {
				
				DefaultCAVO defaultCAVO = new DefaultCAVO();
				for (int j = 0; j < blen; j++) {
					String key = bbs[j].getKey();
					Object rawValue = totalModel.getValueAt(i - rowCount, j);
					String value;
					if (rawValue == null) {
						value = null;
					} else {
						value = _b_ETL(bbs[j], rawValue);
					}
					defaultCAVO.setAttributeValue(key, value);
				}
				
				bodyvec.add(defaultCAVO);
			}
			
		}

		return new TempletData_BX(headvo, bodyvec.toArray(new DefaultCAVO[]{}));
	}

	private static String _b_ETL(BillItem item, Object value) {
		String strValue = null;

		switch (item.getDataType()) {

		case INTEGER:
			if (!value.equals("")) {
				strValue = value.toString();
			}
			break;
		case DECIMAL:
			if (!value.equals("")) {
				if (value instanceof UFDouble) {
					if (UZero.equals(value)) {
						strValue = null;
					} else {
						strValue = ((UFDouble) value).setScale(0 - item.getDecimalDigits(), 4).toString();
					}
				} else {
					strValue = new UFDouble(value.toString(), 0 - item.getDecimalDigits()).toString();
				}
			}
			break;
		case STRING:
		case TEXTAREA:
			strValue = value.toString();
			break;
		case BOOLEAN:
			if (!value.equals("")) {
				if (value instanceof Boolean) {
					if (((Boolean) value).booleanValue()) {
						strValue = TRUE;
					} else {
						strValue = FALSE;
					}
				} else if (value instanceof UFBoolean) {
					if (((UFBoolean) value).booleanValue()) {
						strValue = TRUE;
					} else {
						strValue = FALSE;
					}
				}
			}
			break;
		case DATE:
			if (!value.equals("")) {
				strValue = value.toString().trim();
			}
			break;

		case COMBO:
			if (!value.equals("")) {
				strValue = value.toString().trim();
			}
			break;
		case TIME:
			if (!value.equals("")) {
				strValue = value.toString().trim();
			}
			break;
		case IMAGE:
			break;
		case OBJECT:
			break;
		case UFREF:
			strValue = value.toString();
			break;
		case USERDEF:
			break;
		}
		return strValue;
	}

	/**
	 * 针对表头的单据项目,进行提取
	 * 
	 * @param item
	 * @return
	 */
	private static String _ht_ETL(BillItem item) {

		String strValue = null;
		Component comp = item.getComponent();
		switch (item.getDataType()) {
		case INTEGER:
		case DECIMAL:
			if (comp instanceof UIRefPane) {
				strValue = ((UIRefPane) comp).getText();
			}
			break;
		case STRING:
		case TIME:
		case DATE:
			if (comp instanceof UIRefPane)
				strValue = ((UIRefPane) comp).getText();
			if (item.getDataType() == STRING && strValue != null && item.isStringAutoTrim())
				strValue = strValue.trim();
			break;

		case TEXTAREA:
			if (item.getComponent() instanceof UITextAreaScrollPane) {
				strValue = ((UITextAreaScrollPane) item.getComponent()).getText();
			}
			break;
		case USERDEF:
			if (comp instanceof UIRefPane) {
				if (((UIRefPane) comp).getRefModel() != null) {
					strValue = ((UIRefPane) comp).getRefModel().getRefNameValue();
				}
				if (strValue == null)
					strValue = ((UIRefPane) comp).getUITextField().getText();
			}
			break;
		case UFREF:
			if (comp instanceof UIRefPane)
				strValue = ((UIRefPane) comp).getText();
			break;

		case COMBO:
			if (comp instanceof UIComboBox)
				if (((UIComboBox) comp).getSelectedItem() != null)
					strValue = ((UIComboBox) comp).getSelectedItemName().toString();
			break;
		case BOOLEAN:
			if (comp instanceof UICheckBox)
				if (((UICheckBox) comp).isSelected())
					strValue = TRUE;
				else
					strValue = FALSE;
			break;

		case OBJECT:
			if (comp instanceof BillObjectRefPane)
				strValue = ((BillObjectRefPane) comp).getText();
			break;

		case PASSWORDFIELD: // add for 暗文
			if (comp instanceof UIPasswordField)
				strValue = String.valueOf(((UIPasswordField) comp).getPassword());
			break;
		}
		return strValue;
	}
}
