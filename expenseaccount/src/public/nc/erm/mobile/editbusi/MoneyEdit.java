package nc.erm.mobile.editbusi;


import java.util.Arrays;

import nc.erm.mobile.eventhandler.AbstractEditeventListener;
import nc.erm.mobile.eventhandler.EditItemInfoVO;
import nc.erm.mobile.eventhandler.InterfaceEditeventListener;
import nc.erm.mobile.eventhandler.JsonVoTransform;
import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.vo.pub.BusinessException;

public class MoneyEdit extends AbstractEditeventListener implements InterfaceEditeventListener{
	
	private static String[] keys=new String[]{
		IBillFieldGet.LOCAL_TAX_CR,IBillFieldGet.MONEY_CR,IBillFieldGet.LOCAL_NOTAX_CR,IBillFieldGet.NOTAX_CR,IBillFieldGet.LOCAL_MONEY_CR,
		IBillFieldGet.LOCAL_TAX_DE,IBillFieldGet.MONEY_DE,IBillFieldGet.LOCAL_NOTAX_DE,IBillFieldGet.NOTAX_DE,IBillFieldGet.LOCAL_MONEY_DE,
		IBillFieldGet.RATE,IBillFieldGet.TAXRATE,IBillFieldGet.PRICE,IBillFieldGet.TAXPRICE};
	
	public void process(JsonVoTransform vo) throws BusinessException {
		EditItemInfoVO  editinfo = vo.getEditItemInfoVO();
		int row = editinfo.getSelectrow();
		String key = editinfo.getId();
		String tabcode = editinfo.getClassname();
		
		if(editinfo.isBody()){
			if(!Arrays.asList(keys).contains(key)){
				return;
			}
		}else{
			return;
		}
		
//		RelationItemForCal_Credit item = new RelationItemForCal_Credit();
//		IDataSetForCal data = new LightDataSet(vo, row, item);
//		ScaleUtils scale = new ScaleUtils(InvocationInfoProxy.getInstance().getGroupId());
//		Calculator tool = new Calculator(data, scale);
//		Condition cond = new Condition();
//		String currType = (String) getBodyValueAt(vo, row, IBillFieldGet.PK_CURRTYPE);
//		String pk_org = (String) getHeadValue(vo, IBillFieldGet.PK_ORG);
//		String pk_group = (String) getHeadValue(vo, IBillFieldGet.PK_GROUP);
//		CurrtypeVO currTypeVo = nc.pubitf.uapbd.DefaultCurrtypeQryUtil.getInstance().getDefaultCurrtypeByOrgID(pk_org);
//		String destCurrType = currTypeVo.getPk_currtype();
//		cond.setCalOrigCurr(true);
//		if ((IBillFieldGet.LOCAL_MONEY_CR.equals(key) || IBillFieldGet.LOCAL_MONEY_DE.equals(key))
//				&& !destCurrType.equals(currType)) {// 组织当前币种为组织本币
//			cond.setCalOrigCurr(false);
//			vo.getEnabledItemInfoVO().setBodyItemEnabled(tabcode, key, UFBoolean.FALSE);
//		}
//		cond.setIsCalLocalCurr(true);
//		cond.setIsChgPriceOrDiscount(false);
//		cond.setIsFixNchangerate(false);
//		cond.setIsFixNqtunitrate(false);
//		boolean isInternational = 
//				   BillEnumCollection.BuySellType.OUT_BUY.VALUE.equals(getBodyValueAt(vo, row, IBillFieldGet.BUYSELLFLAG))
//				|| BillEnumCollection.BuySellType.OUT_SELL.VALUE.equals(getBodyValueAt(vo, row, IBillFieldGet.BUYSELLFLAG));
//		cond.setInternational(isInternational);
//
//		String billclass = (String) getHeadValue(vo, IBillFieldGet.BILLCLASS);
//		if (IBillFieldGet.ZS.equals(billclass) || IBillFieldGet.YS.equals(billclass)
//				|| IBillFieldGet.SK.equals(billclass)) {
//			String AR21 = SysInit.getParaString(pk_org, SysinitConst.AR21);
//			cond.setIsTaxOrNet(SysinitConst.ARAP21_TAX.equals(AR21));
//		} else {
//			String AP21 = SysInit.getParaString(pk_org, SysinitConst.AP21);
//			cond.setIsTaxOrNet(SysinitConst.ARAP21_TAX.equals(AP21));
//		}
//		cond.setGroupLocalCurrencyEnable(ArapBillCalUtil.isUseGroupMoney(pk_group));
//		cond.setGlobalLocalCurrencyEnable(ArapBillCalUtil.isUseGlobalMoney());
//		cond.setOrigCurToGlobalMoney(ArapBillCalUtil.isOrigCurToGlobalMoney());
//		cond.setOrigCurToGroupMoney(ArapBillCalUtil.isOrigCurToGroupMoney(pk_group));
//		tool.calculate(cond, key);
//		
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
