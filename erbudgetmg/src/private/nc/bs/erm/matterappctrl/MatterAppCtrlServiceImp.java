package nc.bs.erm.matterappctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.matterapp.common.MatterAppUtils;
import nc.bs.erm.matterappctrl.ext.MatterAppCtrlBOExt;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillQuery;
import nc.itf.uap.pf.IPfExchangeService;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppConvResVO;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.pf.change.ExchangeRuleVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

public class MatterAppCtrlServiceImp implements IMatterAppCtrlService {

	@Override
	@Business(business=ErmBusinessDef.MATTAPP_CTROL,subBusiness="", description = "借款报销单回写费用申请单"/* -=notranslate=- */,type=BusinessType.CORE)
	public MtappCtrlInfoVO matterappControl(IMtappCtrlBusiVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBO(true).matterappControl(vos);
	}

	@Override
	@Business(business=ErmBusinessDef.MATTAPP_CTROL,subBusiness="", description = "借款报销单回写费用申请单"/* -=notranslate=- */,type=BusinessType.CORE)
	public MtappCtrlInfoVO matterappValidate(IMtappCtrlBusiVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return new MtappCtrlInfoVO();
		}
		
		return new MatterAppCtrlBO(false).matterappControl(vos);
	}
	

	@Override
	public MtappCtrlInfoVO matterappControlByAllAdjust(IMtappCtrlBusiVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return new MtappCtrlInfoVO();
		}
		
		return new MatterAppCtrlBO(true,true).matterappControl(vos);
	}

	@Override
	public MtappCtrlInfoVO matterappValidateByAllAdjust(IMtappCtrlBusiVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return new MtappCtrlInfoVO();
		}
		
		return new MatterAppCtrlBO(false,true).matterappControl(vos);
	}
	
	
	@Override
	public MtappCtrlInfoVO matterappControlByDetail(IMtappCtrlBusiVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBOExt(true,true).matterappControlByDetail(vos);
	}

	@Override
	public MtappCtrlInfoVO matterappValidateByDetail(IMtappCtrlBusiVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBOExt(false,true).matterappControlByDetail(vos);
	}

	@Override
	public MatterAppConvResVO getConvertBusiVOs(String des_billtype, String pk_org, AggMatterAppVO retvo)
			throws BusinessException {
		IPfExchangeService exchangeservice = NCLocator.getInstance().lookup(IPfExchangeService.class);
		String ma_tradetype = retvo.getParentVO().getPk_tradetype();
		AggregatedValueObject newbusivo = exchangeservice.runChangeData(ma_tradetype, des_billtype, retvo, null);
		
		if(newbusivo != null){
			JKBXVO jkbxvo = (JKBXVO)newbusivo;
			JKBXHeaderVO parentVO = jkbxvo.getParentVO();
			parentVO.setPk_item(retvo.getParentVO().getPk_mtapp_bill());
			
			String[] fields = new String[] { JKBXHeaderVO.SZXMID, JKBXHeaderVO.JOBID, JKBXHeaderVO.CASHPROJ,
					JKBXHeaderVO.PK_RESACOSTCENTER, JKBXHeaderVO.PK_CHECKELE, JKBXHeaderVO.PK_PCORG,
					JKBXHeaderVO.PROJECTTASK };
			
			if (jkbxvo.getChildrenVO() != null) {
				for (BXBusItemVO item : jkbxvo.getChildrenVO()) {
					item.setPk_item(retvo.getParentVO().getPk_mtapp_bill());
					item.setSrcbilltype(retvo.getParentVO().getPk_tradetype());
					item.setSrctype(retvo.getParentVO().getPk_billtype());

					for (String field : fields) {
						if (parentVO.getAttributeValue(field) != null) {
							if (item.getAttributeValue(field) == null) {
								item.setAttributeValue(field, parentVO.getAttributeValue(field));
							}
						}
					}
					
					if(des_billtype.startsWith(BXConstans.BX_PREFIX)){
						if (item.getAttributeValue(BXBusItemVO.PAYTARGET) == null) {
							item.setAttributeValue(BXBusItemVO.PAYTARGET, parentVO.getAttributeValue(BXBusItemVO.PAYTARGET));
						}
						
						if (item.getAttributeValue(BXBusItemVO.RECEIVER) == null) {
							item.setAttributeValue(BXBusItemVO.RECEIVER, parentVO.getAttributeValue(BXBusItemVO.RECEIVER));
						}
						
						if (item.getAttributeValue(BXBusItemVO.CUSTOMER) == null) {
							item.setAttributeValue(BXBusItemVO.CUSTOMER, parentVO.getAttributeValue(BXBusItemVO.CUSTOMER));
						}
						
						if (item.getAttributeValue(BXBusItemVO.HBBM) == null) {
							item.setAttributeValue(BXBusItemVO.HBBM, parentVO.getAttributeValue(BXBusItemVO.HBBM));
						}
						
						if (item.getAttributeValue(BXBusItemVO.FREECUST) == null) {
							item.setAttributeValue(BXBusItemVO.FREECUST, parentVO.getAttributeValue(BXBusItemVO.FREECUST));
						}
					}
				}
			}
		}

		Map<String, List<String>> destControlFielMap = new HashMap<String, List<String>>();
		List<String> controlFieldList = getMtCtrlBusiFieldList(des_billtype, ma_tradetype, pk_org);

		destControlFielMap.put(retvo.getParentVO().getPk_mtapp_bill(), controlFieldList);

		MatterAppConvResVO res = new MatterAppConvResVO();
		res.setBusiobj(newbusivo);
		res.setMtCtrlBusiFieldMap(destControlFielMap);

		return res;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getMtCtrlBusiFieldList(String des_billtype, String ma_tradetype, String pk_org)
			throws BusinessException {
		// 查询申请单的控制维度
//		IErmMappCtrlFieldQuery ctrlfieldQryService = NCLocator.getInstance().lookup(IErmMappCtrlFieldQuery.class);
//		Map<String, List<String>> ctrlFieldMap = ctrlfieldQryService.queryCtrlFields(pk_org,
//				new String[] { ma_tradetype });
		
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		List<String[]> paramList = new ArrayList<String[]>();
		paramList.add(new String[]{pk_org, ma_tradetype});
		@SuppressWarnings("rawtypes")
		Map[] ctrlmap = NCLocator.getInstance().lookup(IErmMappCtrlBillQuery.class).queryCtrlShema(paramList, pk_group);
		List<MtappCtrlfieldVO> ctrlfieldvos = (List<MtappCtrlfieldVO>) ctrlmap[1].get(pk_org+ma_tradetype);
		if(ctrlfieldvos == null || ctrlfieldvos.size() < 0){
			return null;
		}
		List<String> srcFields = new ArrayList<String>();
		for(MtappCtrlfieldVO vo : ctrlfieldvos){
			srcFields.add(vo.getFieldcode());
		}
		
		// 根据交易类型
		List<ExchangeRuleVO> ruleVos = MatterAppUtils.findExchangeRule(ma_tradetype, des_billtype);

		Map<String, List<String>> fieldMap = new HashMap<String, List<String>>();
		for (ExchangeRuleVO ruleVo : ruleVos) {
			String ruleData = ruleVo.getRuleData();
			List<String> list = fieldMap.get(ruleData);
			if(list == null){
				list = new ArrayList<String>();
				fieldMap.put(ruleData, list);
			}
			list.add(ruleVo.getDest_attr());
		}

		List<String> destFieldList = new ArrayList<String>();
		for (String srcField : srcFields) {
			if (fieldMap.get(srcField) != null) {
				destFieldList.addAll(fieldMap.get(srcField));
			}
		}

		return destFieldList;
	}

}
