package nc.erm.mobile.editbusi;


import nc.erm.mobile.eventhandler.AbstractEditeventListener;
import nc.erm.mobile.eventhandler.EditItemInfoVO;
import nc.erm.mobile.eventhandler.InterfaceEditeventListener;
import nc.erm.mobile.eventhandler.JsonVoTransform;
import nc.erm.mobile.util.NumberFormatUtil;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDouble;

public class MoneyEdit extends AbstractEditeventListener implements InterfaceEditeventListener{
	
	private boolean isAmoutField(String editFormulas) {
		if (editFormulas == null) {
			return false;
		}
		if (editFormulas.indexOf(JKBXHeaderVO.AMOUNT) != -1) {
			return true;
		}
		return false;
	}
	public void finBodyYbjeEdit() {
		UFDouble newHeadYbje = null;// 表头金额

		String defaultMetaDataPath = BXConstans.ER_BUSITEM;// 元数据路径
//		DjLXVO currentDjlx = ((ErmBillBillManageModel) editor.getModel()).getCurrentDjLXVO();
//
//		if ((BXConstans.JK_DJDL.equals(currentDjlx.getDjdl()))) {
//			defaultMetaDataPath = BXConstans.JK_BUSITEM;
//		}
//
//		BillTabVO[] billTabVOs = getBillCardPanel().getBillData().getBillTabVOs(IBillItem.BODY);
//		if (billTabVOs != null && billTabVOs.length > 0) {
//			for (BillTabVO billTabVO : billTabVOs) {
//				String metaDataPath = billTabVO.getMetadatapath();// metaDataPath
//																	// 为null的时候，说明是自定义页签，默认为业务行
//				if (metaDataPath != null && !defaultMetaDataPath.equals(metaDataPath)) {
//					continue;
//				}
//
//				BillModel billModel = getBillCardPanel().getBillModel(billTabVO.getTabcode());
//				BXBusItemVO[] details = (BXBusItemVO[]) billModel.getBodyValueVOs(BXBusItemVO.class.getName());
//
//				int length = details.length;
//				for (int i = 0; i < length; i++) {
//					if (details[i].getYbje() != null) {// 当表体中存在空行时，原币金额为空，所以在这里判空
//						if (newHeadYbje == null) {
//							newHeadYbje = details[i].getYbje();
//						} else {
//							newHeadYbje = newHeadYbje.add(details[i].getYbje());
//						}
//					}
//				}
//			}
//		}
//
//		getBillCardPanel().setHeadItem(JKBXHeaderVO.YBJE, newHeadYbje);
//		if (getHeadValue(JKBXHeaderVO.PK_ORG) != null) {
//			setHeadYfbByHead();
//		}
	}
	public void process(JsonVoTransform vo) throws BusinessException {
		EditItemInfoVO  editinfo = vo.getEditItemInfoVO();
		int row = editinfo.getSelectrow();
		String key = editinfo.getId();
		String formula = editinfo.getFormula();
		String tabcode = editinfo.getClassname();
		
		if(editinfo.isBody()){
			if (key.equals(BXBusItemVO.AMOUNT) || isAmoutField(formula)) {
				Object amount = editinfo.getValue();
				vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, BXBusItemVO.YBJE, NumberFormatUtil.formatDouble(new UFDouble(amount.toString())));
				// 改amount触发ybje事件
				finBodyYbjeEdit();
//				e.setKey(BXBusItemVO.YBJE);
//				bodyEventHandleUtil.modifyFinValues(e.getKey(), e.getRow(), e);
//				e.setKey(BXBusItemVO.AMOUNT);
//				try {
//					editor.getHelper().calculateFinitemAndHeadTotal(editor);
//					eventUtil.setHeadYFB();
//				} catch (BusinessException e1) {
//					ExceptionHandler.handleExceptionRuntime(e1);
//				}
			}
		}else{
			return;
		}
		
		
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNorigmnyKey(), NumberFormatUtil.formatDouble(data.getNorigmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNorigtaxKey(), NumberFormatUtil.formatDouble(data.getNorigtax()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNnumKey(), NumberFormatUtil.formatDouble(data.getNnum()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNtaxrateKey(), NumberFormatUtil.formatDouble(data.getNtaxrate()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNorigtaxnetpriceKey(), NumberFormatUtil.formatDouble(data.getNorigtaxnetprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNorignetpriceKey(), NumberFormatUtil.formatDouble(data.getNorignetprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNtaxnetpriceKey(), NumberFormatUtil.formatDouble(data.getNtaxnetprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNnetpriceKey(), NumberFormatUtil.formatDouble(data.getNnetprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNqtunitnumKey(), NumberFormatUtil.formatDouble(data.getNqtunitnum()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNqtorigpriceKey(), NumberFormatUtil.formatDouble(data.getNqtorigprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNqtorigtaxpriceKey(), NumberFormatUtil.formatDouble(data.getNqtorigtaxprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNtaxmnyKey(), NumberFormatUtil.formatDouble(data.getNtaxmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNmnyKey(), NumberFormatUtil.formatDouble(data.getNmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNtaxKey(), NumberFormatUtil.formatDouble(data.getNtax()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNexchangerateKey(), NumberFormatUtil.formatDouble(data.getNexchangerate()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNcaltaxmnyKey(), NumberFormatUtil.formatDouble(data.getNcaltaxmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNdeductibletaxKey(), NumberFormatUtil.formatDouble(data.getNdeductibletax()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNdeductibleTaxrateKey(), NumberFormatUtil.formatDouble(data.getNdeductibleTaxrate()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNorigtaxmnyKey(), NumberFormatUtil.formatDouble(data.getNorigtaxmny()));
//		
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNtaxpriceKey(), NumberFormatUtil.formatDouble(data.getNtaxprice()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNpriceKey(), NumberFormatUtil.formatDouble(data.getNprice()));
//		
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNgroupmnyKey(), NumberFormatUtil.formatDouble(data.getNgroupmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNgrouptaxmnyKey(), NumberFormatUtil.formatDouble(data.getNgrouptaxmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNglobalmnyKey(), NumberFormatUtil.formatDouble(data.getNglobalmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, item.getNglobaltaxmnyKey(), NumberFormatUtil.formatDouble(data.getNglobaltaxmny()));
//		
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, IBillFieldGet.MONEY_BAL, NumberFormatUtil.formatDouble(data.getNorigtaxmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, IBillFieldGet.LOCAL_MONEY_BAL, NumberFormatUtil.formatDouble(data.getNtaxmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, IBillFieldGet.GROUPBALANCE, NumberFormatUtil.formatDouble(data.getNgrouptaxmny()));
//		vo.getItemValueInfoVO().setBodyItemValue(tabcode, row, IBillFieldGet.GLOBALBALANCE, NumberFormatUtil.formatDouble(data.getNglobaltaxmny()));
	}
}
