package nc.ui.arap.bx.remote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.ui.arap.bx.BXBillMainPanel;
import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.engine.IConfigVO;
import nc.vo.arap.service.ServiceVO;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.ReimRuleDefVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

public class ReimRuleDefCall extends AbstractCall implements IRemoteCallItem {

	public ReimRuleDefCall(BXBillMainPanel panel) {
		super(panel);
	}

	public ServiceVO getServcallVO() {
		List<String> djlxbms=new ArrayList<String>();
		DjLXVO[] djlxs = getParent().getCache().getDjlxVOS();
		for(DjLXVO vo:djlxs){
			djlxbms.add(vo.getDjlxbm());
		}
		djlxbms.add("");
		
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.pub.IConfigurationServicePublic");
		callvo.setMethodname("getCommonVOs");
		callvo.setParamtype(new Class[] { String.class, String[].class });
		callvo.setParam(new Object[] { ReimRuleDefVO.key,djlxbms.toArray(new String[]{}) });
		return callvo;
	}

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
