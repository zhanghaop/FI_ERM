package nc.bs.erm.costshare.actimpl;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.costshare.IErmCostShareConst;
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
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.expenseaccount.ExpenseAccountVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

/**
 * 费用结转单动作-费用明细账业务实现插件
 * 
 * @author luolch
 * 
 */
public class ErmExpCostListener implements IBusinessListener {
	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		
		ErmBusinessEvent erevent = (ErmBusinessEvent) event;
		String eventType = erevent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erevent.getUserObject();
		AggCostShareVO[] vos = (AggCostShareVO[]) obj.getNewObjects();
		if (ErmEventType.TYPE_INSERT_AFTER.equalsIgnoreCase(eventType)) {
			// 新增操作
			AggCostShareVO[] newvos = (AggCostShareVO[]) obj.getNewObjects();
			for (int i = 0; i < newvos.length; i++) {
				ExpenseAccountVO[] expvos = ErmBillCostConver.getExpAccVO(newvos[i]);
				for (int j = 0; j < expvos.length; j++) {
					if (IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO) newvos[i].getParentVO())
							.getSrc_type()) {
						expvos[j].setBillstatus(BXStatusConst.DJZT_TempSaved);// 费用明细账暂存态
					}
				}
				// 插入费用
				NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).insertVOs(expvos);

				// 事前分摊冲销报销 费用处理
				if (IErmCostShareConst.CostShare_Bill_SCRTYPE_BX == ((CostShareVO) newvos[i].getParentVO())
						.getSrc_type()) {

					// 查出报销单费用账
					String src_id = ((CostShareVO) newvos[i].getParentVO()).getSrc_id();
					ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance()
							.lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[] { src_id });
					// 冲销报销单费用账
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(oldaccountVOs);

				}
			}
		} else if (ErmEventType.TYPE_UPDATE_AFTER.equalsIgnoreCase(eventType)) {

			// 如果是卡片修改暂存不做费用的任何处理
			CostShareVO cs = (CostShareVO) vos[0].getParentVO();
			if (vos.length == 1 && BXStatusConst.DJZT_TempSaved == cs.getBillstatus()) {
				return;
			}

			for (int i = 0; i < vos.length; i++) {
				// 修改操作
				ExpenseAccountVO[] expvo = ErmBillCostConver.getExpAccVO(vos[i]);
				// 查询旧费用账
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().lookup(IErmExpenseaccountQueryService.class)
						.queryBySrcID(new String[] { vos[i].getParentVO().getPrimaryKey() });

				for (int j = 0; j < expvo.length; j++) {
					if (IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO) vos[i].getParentVO())
							.getSrc_type()) {
						// 费用明细账暂存态
						expvo[j].setBillstatus(BXStatusConst.DJZT_TempSaved);
					}
				}
				if (oldaccountVOs != null) {
					NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class)
							.updateVOs(expvo, oldaccountVOs);
				}

				// 更新完，如果是事前需要再冲销报销单
				if (IErmCostShareConst.CostShare_Bill_SCRTYPE_BX == ((CostShareVO) vos[i].getParentVO()).getSrc_type()) {
					// 查询报销
					ExpenseAccountVO[] bxAccountVOs = NCLocator.getInstance()
							.lookup(IErmExpenseaccountQueryService.class)
							.queryBySrcID(new String[] { ((CostShareVO) vos[i].getParentVO()).getSrc_id() });
					// 事前报销的冲销处理
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(bxAccountVOs);

					// 摊销情况下 ，冲销结转单
					UFBoolean isexpamt = ((CostShareVO) vos[i].getParentVO()).getIsexpamt();
					if (isexpamt != null && isexpamt.equals(UFBoolean.TRUE)) {
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(expvo);
					}
				}
			}
		}else if(ErmEventType.TYPE_DELETE_AFTER.equalsIgnoreCase(eventType)
				|| ErmEventType.TYPE_INVALID_AFTER.equalsIgnoreCase(eventType)){
			//更新完，如果是事前需要再冲销报销单
			for (int i = 0; i < vos.length; i++) {
				//查询旧费用账
				String[] srcIDS = new String[vos.length];
				for (int j = 0; j < vos.length; j++) {
					srcIDS[j] = vos[j].getParentVO().getPrimaryKey();
				}
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
				lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
				// 删除费用账操作
				NCLocator.getInstance().lookup(IErmExpenseaccountManageService.class).deleteVOs(oldaccountVOs);
				//如果是事前的报销单(报销单取消分摊的情况)，需要重启用报销费用FIXME
				if(IErmCostShareConst.CostShare_Bill_SCRTYPE_BX == ((CostShareVO)vos[i].getParentVO()).getSrc_type()){
					//查询报销费用
					ExpenseAccountVO[] bxoldaccountVOs = NCLocator.getInstance().
					lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[]{ ((CostShareVO)vos[i].getParentVO()).getSrc_id()});
					//事前报销的冲销处理
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).unWriteoffVOs(bxoldaccountVOs);
				}
			}
			
		}else if( ErmEventType.TYPE_APPROVE_AFTER.equalsIgnoreCase(eventType)){
			AggCostShareVO[] newvos = (AggCostShareVO[]) obj.getNewObjects();
			
			String[] pks = new String[vos.length];
			String[] srcIDS = new String[vos.length];
			for (int i = 0; i < vos.length; i++) {
				srcIDS [i] = ((CostShareVO)vos[i].getParentVO()).getSrc_id();
				pks[i] = ((CostShareVO)vos[i].getParentVO()).getPrimaryKey();
			}
			//查询费用
			ExpenseAccountVO[] oldcostAccountVOs = NCLocator.getInstance().
			lookup(IErmExpenseaccountQueryService.class).queryBySrcID(pks);
			// 确认操作
			NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
			signVOs(oldcostAccountVOs);
			for (int j = 0; j < newvos.length; j++) {
				if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO)newvos[j].getParentVO()).getSrc_type()){
					//查询报销
					ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
					lookup(IErmExpenseaccountQueryService.class).queryBySrcID(srcIDS);
					//事后报销的冲销处理
					NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).
					writeoffVOs(oldaccountVOs);
				}else{
					// 冲销结转单
					UFBoolean isexpamt = ((CostShareVO) newvos[j].getParentVO()).getIsexpamt();
					if (isexpamt != null && isexpamt.equals(UFBoolean.TRUE)) {
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).writeoffVOs(oldcostAccountVOs);
					}
				}
			}
			
		}else if( ErmEventType.TYPE_UNAPPROVE_AFTER.equalsIgnoreCase(eventType)){
			// 取消确认操作
			
			for (int i = 0; i < vos.length; i++) {
				ExpenseAccountVO[] oldaccountVOs = NCLocator.getInstance().
				lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[]{((CostShareVO)vos[i].getParentVO()).getPrimaryKey()});
				//更新状态（费用回到保存态或暂存态）
				if (oldaccountVOs!=null) {
					for (int j = 0; j < oldaccountVOs.length; j++) {
						if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO)vos[i].getParentVO()).getSrc_type()){
							oldaccountVOs[j].setBillstatus(BXStatusConst.DJZT_TempSaved);
						}else {
							oldaccountVOs[j].setBillstatus(BXStatusConst.DJZT_Saved);
						}
						
					}
					//查出旧vo反审核
					NCLocator.getInstance().lookup(IErmExpenseaccountApproveService.class).
					unsignVOs(oldaccountVOs);
				}
				//报销反冲销处理
				if(IErmCostShareConst.CostShare_Bill_SCRTYPE_SEL == ((CostShareVO)vos[i].getParentVO()).getSrc_type()){
					//查出事后报销的反冲销处理FIXME
					ExpenseAccountVO[] bxOldaccountVOs = NCLocator.getInstance().
					lookup(IErmExpenseaccountQueryService.class).queryBySrcID(new String[]{((CostShareVO)vos[i].getParentVO()).getSrc_id()});
					
					if(bxOldaccountVOs!=null){
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class).
						unWriteoffVOs(bxOldaccountVOs);
					}
				}else{
					// 冲销结转单
					UFBoolean isexpamt = ((CostShareVO) vos[i].getParentVO()).getIsexpamt();
					if (isexpamt != null && isexpamt.equals(UFBoolean.TRUE)) {
						NCLocator.getInstance().lookup(IErmExpenseaccountWriteoffService.class)
								.unWriteoffVOs(oldaccountVOs);
					}
				}
			}
		}
		
	}

}
