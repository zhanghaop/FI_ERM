package nc.bs.erm.matterapp.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.matterapp.ext.ErmMatterAppConstExt;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 申请单，数据检查
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class ErmMatterappCheckListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		AggMatterAppVO[] vos = (AggMatterAppVO[]) ((ErmCommonUserObj) erEvent.getUserObject()).getNewObjects();
		if(ArrayUtils.isEmpty(vos)){
			return;
		}
		StringBuffer msgs = new StringBuffer();
		for (AggMatterAppVO aggvo : vos) {
			MatterAppVO headvo = aggvo.getParentVO();
			String billno = headvo.getBillno();

			String beginMonthpk = (String) headvo.getAttributeValue(ErmMatterAppConstExt.STARTPERIOD_FIELD);
			String endMonthpk = (String) headvo.getAttributeValue(ErmMatterAppConstExt.ENDPERIOD_FIELD);
				
			if (StringUtil.isEmpty(beginMonthpk)
					|| StringUtil.isEmpty(endMonthpk)) {
				msgs.append("请填写开始期间、结束期间");
				if (!StringUtil.isEmpty(billno)) {
					msgs.append(":" + billno);
				}
				msgs.append("\n");
			} else {
				AccperiodmonthVO startperiodmonthVO = ErAccperiodUtil
				.getAccperiodmonthByPk(beginMonthpk);
				
				AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(headvo.getPk_org(), headvo
	                        .getBilldate());
	           if (startperiodmonthVO.getYearmth().compareTo(accperiodmonthVO.getYearmth()) < 0) {
	        	   msgs.append("开始期间应大于制单日期");
					if(!StringUtil.isEmpty(billno)){
						msgs.append(":"+billno);
					}
					msgs.append("\n");
	           }
			}

		}
		if(msgs.length() > 0){
			throw new BusinessException(msgs.toString());
		}
	}

}
