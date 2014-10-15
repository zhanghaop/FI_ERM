package nc.ui.erm.billpub.remote;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.ui.fipub.service.IRemoteCallItem;
import nc.ui.uif2.editor.BillForm;
import nc.vo.arap.engine.BtnInfoVO;
import nc.vo.arap.engine.FlowInfoVO;
import nc.vo.arap.engine.ListenerVO;
import nc.vo.arap.service.ServiceVO;
import nc.vo.arap.workflow.config.ConfigAgent;
import nc.vo.arap.workflow.config.ConfigCache;
import nc.vo.pub.BusinessException;

public class InitNodeCall extends AbstractCall implements IRemoteCallItem {

	private BillForm panel = null;
	public InitNodeCall(BillForm panel) {
		this.panel = panel;
	}

	@Override
	public ServiceVO getServcallVO() {

		callvo = new ServiceVO();
		callvo.setClassname("nc.itf.arap.pub.IConfigurationServicePublic");
		callvo.setMethodname("initNode");
		callvo.setParamtype(new Class[] { String.class, String.class });
		callvo.setParam(new Object[] { panel.getModel().getContext().getNodeCode(), getPk_group() });
		return callvo;
	}

	@SuppressWarnings({ "unchecked", "static-access" })
	public void handleResult(Map<String, Object> datas) throws BusinessException {
		List<List<Map<String, ?>>> values = (List<List<Map<String, ?>>>) datas.get(callvo.getCode());

		ConfigCache cache = ((ConfigAgent)ConfigAgent.getInstance()).getCache();
		List<Map<String, ?>> buttons = values.get(0);
		List<Map<String, ?>> flows = values.get(1);
		List<Map<String, ?>> lis = values.get(2);
		
		if(lis!=null && lis.size()!=0){
			Collection<ListenerVO> values2 = (Collection<ListenerVO>) lis.get(0).values();
			for(ListenerVO listener:values2){
				cache.putListenerRefFlowId(listener.getBilltypecode(),listener.getPk_corp(),listener.getTablecode(),listener.getEventtype(),listener.getMethod(),listener.getBilltypecode(),listener.getTemplatetype(),listener.getIPos(),listener.getFlowid());
			}
		}
		
		String nodeCode = panel.getModel().getContext().getNodeCode();
		
		cache.putButtons(nodeCode, getPk_group(),(Map<String, BtnInfoVO>) buttons.get(0));
		
		for (@SuppressWarnings("rawtypes")
		Iterator iter = flows.iterator(); iter.hasNext();) {
			Map<String, FlowInfoVO> flow = (Map<String, FlowInfoVO>) iter.next();
			if(flow!=null && flow.size()!=0){
				cache.putFlows(flow.values().iterator().next().getNamespace(), flow);
			}
		}
	}

}
