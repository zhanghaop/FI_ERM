package nc.bs.erm.eventlistener;

import java.util.ArrayList;
import java.util.List;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.NCLocator;
import nc.erm.pub.conversion.ErmBillCostConver;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountApproveService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountManageService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountQueryService;
import nc.pubitf.erm.expenseaccount.IErmExpenseaccountWriteoffService;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * 费用结转单动作-费用明细账业务实现插件
 * 
 * @author luolch
 * 
 */
public class ErmExpBXListener implements IBusinessListener {
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		nc.vo.ep.bx.JKBXVO[] vos = (nc.vo.ep.bx.JKBXVO[]) obj.getNewObjects();
		if (vos.length > 0 && vos[0] instanceof JKVO) {
			return;
		}
		if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
			for (int i = 0; i < vos.length; i++) {
				JKBXVO vo = vos[i];
				ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVO(vo);
				//插入汇总明细账
				NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).
				insertVOs(expaccvo);
				// 设置待摊或者核销预提情况，保存后直接进行冲销
				if(vo.getParentVO().getIsexpamt()==UFBoolean.TRUE
						||(vo.getAccruedVerifyVO() != null &&vo.getAccruedVerifyVO().length >0)){
					if(vo.getParentVO().getIscostshare()==UFBoolean.TRUE){
						//不用冲销，因为费用结转会冲销
						continue;
					}
					//冲销报销单费用账
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).
					writeoffVOs(expaccvo);
				}
			}
		} else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				JKBXVO vo = vos[i];
				srcIDS[i] = vo.getParentVO().getPrimaryKey();
				//查询出旧的VO
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
				lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
				//费用明细账
				ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
				if (oldaccountVOs==null) {
					NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expaccvo);
				}else {
					NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).
					updateVOs(expaccvo,oldaccountVOs);
				}
				// 设置待摊或者核销预提情况，保存后直接进行冲销
				if(vo.getParentVO().getIsexpamt()==UFBoolean.TRUE
						||(vo.getAccruedVerifyVO() != null &&vo.getAccruedVerifyVO().length >0)){
					if(vo.getParentVO().getIscostshare()==UFBoolean.TRUE){
						//不用冲销，因为费用结转会冲销
						continue;
					}
					// 新增操作
					//插入汇总明细账
					//冲销报销单费用账
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).
					writeoffVOs(expaccvo);
				}
				
			}
			
		}else if(ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)){
			List<String> pksList = new ArrayList<String>();
			for (int j = 0; j < vos.length; j++) {
				pksList.add(vos[j].getParentVO().getPk_jkbx());
			}
			//查询
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).
			queryBySrcID(pksList.toArray(new String[0]));
			
			//删除
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).
			deleteVOs(accountVOs);
		}else if( ErmEventType.TYPE_SIGN_AFTER.equalsIgnoreCase(eventType)){
			//查询
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			//账表生效状态更新
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			signVOs(accountVOs);
		}else if( ErmEventType.TYPE_UNSIGN_AFTER.equalsIgnoreCase(eventType)){
			//查询
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			//账表生效状态更新
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
			if(accountVOs == null){
				return;
			}
			for (int i = 0; i < accountVOs.length; i++) {
				accountVOs[i].setBillstatus(BXStatusConst.DJZT_Verified);
			}
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			unsignVOs(accountVOs);
		}else if( ErmEventType.TYPE_APPROVE_BEFORE.equalsIgnoreCase(eventType)){
			//查询
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			//账表生效状态更新
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
			if(accountVOs == null){
				return;
			}
			for (int i = 0; i < accountVOs.length; i++) {
				accountVOs[i].setBillstatus(BXStatusConst.DJZT_Verified);
			}
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			approveVOs(accountVOs);
		}else if( ErmEventType.TYPE_UNAPPROVE_BEFORE.equalsIgnoreCase(eventType)){
			//查询
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS[i] = vos[i].getParentVO().getPrimaryKey();
			}
			//账表生效状态更新
			ExpenseAccountVO[] accountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
			if(accountVOs == null){
				return;
			}
			for (int i = 0; i < accountVOs.length; i++) {
				accountVOs[i].setBillstatus(BXStatusConst.DJZT_Saved);
			}
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			unApproveVOs(accountVOs);
		}else if (ErmEventType.TYPE_TEMPSAVE_AFTER.equals(eventType)) {// 暂存
			// 新增操作
			//插入汇总明细账
			ExpenseAccountVO[] expaccvo = ErmBillCostConver.getExpAccVOS(vos);
			NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).
			insertVOs(expaccvo);
		}
		
	}

}
