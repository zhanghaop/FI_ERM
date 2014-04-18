package nc.bs.erm.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.itf.pim.budget.pub.IbudgetExe4ExpenseBill;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.exception.ProjBudgetAlarmBusinessException;
import nc.vo.pm.budget.pub.BudgetReturnMSG;
import nc.vo.pub.BusinessException;

/**
 * ��Ŀ����Ԥ����ơ���д
 * @author chenshuaia
 */
public class ErmBxBudgetListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		
		//�Ƿ�װ��ĿԤ��
		boolean isprojControlUsed = BXUtil.isProductTbbInstalled(BXConstans.PIM_FUNCODE);
		if(!isprojControlUsed){
			return;
		}
		
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		if(vos[0] instanceof JKVO){//��ĿԤ�㲻���ƽ�
			return;
		}
		
		IbudgetExe4ExpenseBill service = NCLocator.getInstance().lookup(IbudgetExe4ExpenseBill.class);
		
		List<JKBXVO> budgetBxList = new ArrayList<JKBXVO>();
		
		for (int i = 0; i < vos.length; i ++) {
			JKBXVO vo = (JKBXVO) vos[i].clone();
			JKBXHeaderVO headVo = vo.getParentVO();
			if (headVo.getDjzt().intValue() == BXStatusConst.DJZT_TempSaved) {
				continue;
			}
			if(headVo.isAdjustBxd()){
				// ��������Ϊ���õ����ĵ��ݣ���������ĿԤ��
				continue ;
			}
			
			vo.setContrastVO(null);
			vo.setcShareDetailVo(null);
			budgetBxList.add(vo);
		}
		
		if(budgetBxList.size() == 0){
			return;
		}
		
		JKBXVO[] budgetBxVos = budgetBxList.toArray(new JKBXVO[]{});
		
		BudgetReturnMSG msg = null;
		try {
			if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {//������
				msg = service.budgetExe4Save(budgetBxVos);
			}else if(ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)){//�����
				JKBXVO[] oldVos = new JKBXVO[vos.length];
				for (int i = 0; i < vos.length; i++) {
					budgetBxVos[i].getBxoldvo().getParentVO();
					int djzt = budgetBxVos[i].getBxoldvo().getParentVO().getDjzt().intValue();
					if(BXStatusConst.DJZT_TempSaved != djzt){
						oldVos[i] = (JKBXVO)budgetBxVos[i].getBxoldvo().clone();
						oldVos[i].setContrastVO(null);
						oldVos[i].setcShareDetailVo(null);
					}
				}
				if(oldVos[0] == null){
					msg = service.budgetExe4Save(budgetBxVos);//�ݴ治������
				}else{
					msg = service.budgetExe4Update(budgetBxVos, oldVos);
				}
			}else if(ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)){// ɾ������
				msg = service.budgetExe4Delete(budgetBxVos);
			}else if(ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType) ){// ��˺����
				msg = service.budgetExe4Approve(budgetBxVos);
			}else if(ErmEventType.TYPE_UNAPPROVE_AFTER.equalsIgnoreCase(eventType)){// ����˺����
				msg = service.budgetExe4UNApprove(budgetBxVos);
			}else{
				return;
			}
			
			if(vos[0].getHasProBudgetCheck()){
				vos[0].setHasProBudgetCheck(false);
				return;
			}else{
				if(msg != null && msg.getErrorMSG() != null){
					throw new ProjBudgetAlarmBusinessException(msg.getErrorMSG());
				}
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}


		
	}
}
