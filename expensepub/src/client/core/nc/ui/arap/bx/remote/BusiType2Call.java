package nc.ui.arap.bx.remote;

import java.util.Map;

import nc.ui.fipub.service.IRemoteCallItem;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.engine.IConfigVO;
import nc.vo.arap.service.ServiceVO;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.pub.BusinessException;


public class BusiType2Call extends AbstractCall implements IRemoteCallItem {
	public BusiType2Call() {
		super();
	}

	public ServiceVO getServcallVO() {
		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.pub.IConfigurationServicePublic");
		callvo.setMethodname("getCommonVOs");
		callvo.setParamtype(new Class[] { String.class, String[].class });
		callvo.setParam(new Object[] { BusiTypeVO.key,new String[]{BXConstans.REPORT_BUSITYPE_KEY}});
		return callvo;
	}

	public void handleResult(Map<String, Object> datas) throws BusinessException {
		Map<String, IConfigVO> vos = (Map<String, IConfigVO>) datas.get(callvo.getCode());

		if(vos!=null){
			for(String key:vos.keySet()){
				IConfigVO configVO = vos.get(key);
				((ConfigAgent)ConfigAgent.getInstance()).getCache().putCommonVO(BusiTypeVO.key, configVO);
			}
		}
	}

}
