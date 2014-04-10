package nc.bs.erm.matterappctrl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.erm.matterapp.common.MatterAppUtils;
import nc.bs.framework.common.NCLocator;
import nc.itf.erm.mactrlschema.IErmMappCtrlFieldQuery;
import nc.itf.uap.pf.IPfExchangeService;
import nc.pubitf.erm.matterappctrl.IMatterAppCtrlService;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
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
			return null;
		}
		
		return new MatterAppCtrlBO(false).matterappControl(vos);
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
	
	public List<String> getMtCtrlBusiFieldList(String des_billtype, String ma_tradetype, String pk_org)
			throws BusinessException {
		// 查询申请单的控制维度
		IErmMappCtrlFieldQuery ctrlfieldQryService = NCLocator.getInstance().lookup(IErmMappCtrlFieldQuery.class);
		Map<String, List<String>> ctrlFieldMap = ctrlfieldQryService.queryCtrlFields(pk_org,
				new String[] { ma_tradetype });

		// 根据交易类型
		List<ExchangeRuleVO> ruleVos = MatterAppUtils.findExchangeRule(ma_tradetype, des_billtype);

		Map<String, String> fieldMap = new HashMap<String, String>();
		for (ExchangeRuleVO ruleVo : ruleVos) {
			fieldMap.put(ruleVo.getRuleData(), ruleVo.getDest_attr());
		}

		List<String> destFieldList = new ArrayList<String>();
		if(ctrlFieldMap.get(ma_tradetype) != null){
			for (String srcField : ctrlFieldMap.get(ma_tradetype)) {
				if (fieldMap.get(srcField) != null) {
					destFieldList.add(fieldMap.get(srcField));
				}
			}
		}

		return destFieldList;
	}
}
