package nc.ui.erm.billpub.remote;

import java.util.Collection;
import java.util.Map;

import nc.ui.erm.billpub.model.ErmBillBillManageModel;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.engine.IConfigVO;
import nc.vo.arap.service.ServiceVO;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;

public class ReimRuleDefCall extends AbstractCall implements IRemoteCallItem {

	private BillForm panel = null;
	public ReimRuleDefCall(BillForm panel) {
		this.panel = panel;
	}

	public ServiceVO getServcallVO() {
		Collection<DjLXVO> values = ((ErmBillBillManageModel)panel.getModel()).getBillTypeMapCache().values();
		String[] billTypeCodes = VOUtils.getAttributeValues(values.toArray(new DjLXVO[0]), "djlxbm");
		
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.pub.IConfigurationServicePublic");
		callvo.setMethodname("getCommonVOs");
		callvo.setParamtype(new Class[] { String.class, String[].class });
		callvo.setParam(new Object[] { ReimRuleDefVO.key,billTypeCodes });
		return callvo;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public void handleResult(Map<String, Object> datas) throws BusinessException {
		Map<String, IConfigVO> vos = (Map<String, IConfigVO>) datas.get(callvo.getCode());

		if(vos!=null){
			for(String key:vos.keySet()){
				IConfigVO configVO = vos.get(key);
				((ConfigAgent)ConfigAgent.getInstance()).getCache().putCommonVO(ReimRuleDefVO.key, configVO);
			}
		}
	}

}
