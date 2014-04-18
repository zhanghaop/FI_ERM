package nc.bs.erm.jkbx.eventlistener.ext;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.ext.common.ErmConstExt;
import nc.bs.erm.util.ErAccperiodUtil;
import nc.vo.bd.period2.AccperiodmonthVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * �����̵渶�����������ݼ��
 * 
 * ����Ԫר��
 * 
 * @author lvhj
 *
 */
public class Cxx1BxCheckListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		JKBXVO[] vos = (JKBXVO[]) ((ErmCommonUserObj) erEvent.getUserObject()).getNewObjects();
		if(ArrayUtils.isEmpty(vos)){
			return;
		}
		StringBuffer msgs = new StringBuffer();
		for (JKBXVO jkbxvo : vos) {
			JKBXHeaderVO headvo = jkbxvo.getParentVO();
			String djlxbm = headvo.getDjlxbm();
			if(ErmConstExt.Distributor_BX_Tradetype.equals(djlxbm)){
				
				String djbh = jkbxvo.getParentVO().getDjbh();

				String beginMonthpk = headvo.getZyx1();
				String endMonthpk = headvo.getZyx2();
				
				if(StringUtil.isEmpty(beginMonthpk )||StringUtil.isEmpty(endMonthpk )){
					msgs.append("����д�����̵渶�������Ŀ�ʼ�ڼ䡢�����ڼ�");
					if(!StringUtil.isEmpty(djbh)){
						msgs.append(":"+djbh);
					}
					msgs.append("\n");
				}else{
					AccperiodmonthVO startperiodmonthVO = ErAccperiodUtil
					.getAccperiodmonthByPk(beginMonthpk);
					
					AccperiodmonthVO accperiodmonthVO = ErAccperiodUtil.getAccperiodmonthByUFDate(headvo.getPk_org(), headvo
		                        .getDjrq());
		           if (startperiodmonthVO.getYearmth().compareTo(accperiodmonthVO.getYearmth()) < 0) {
		        	   msgs.append("�����̵渶�������Ŀ�ʼ�ڼ�Ӧ�����Ƶ�����");
						if(!StringUtil.isEmpty(djbh)){
							msgs.append(":"+djbh);
						}
						msgs.append("\n");
		           }
				}
				
				if(jkbxvo.getcShareDetailVo() == null || jkbxvo.getcShareDetailVo().length ==0){
					msgs.append("����д�����̵渶�������ķ�̯��ϸ");
					if(!StringUtil.isEmpty(djbh)){
						msgs.append(":"+djbh);
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
