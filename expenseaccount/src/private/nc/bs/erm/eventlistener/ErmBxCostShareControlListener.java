package nc.bs.erm.eventlistener;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.costshare.ErmCostShareBO;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.erm.event.ErmEventType;
import nc.impl.erm.costshare.ErmCSBillMgePrivateImpl;
import nc.impl.erm.costshare.ErmCostShareBillManageImpl;
import nc.itf.erm.costshare.IErmCostShareBillManagePrivate;
import nc.md.persist.framework.IMDPersistenceQueryService;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.erm.costshare.IErmCostShareBillManage;
import nc.util.erm.costshare.ErmForCShareUtil;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;

/**
 * 报销单操作时，对费用结转单操作的业务插件
 * @author chenshuaia
 *
 */
public class ErmBxCostShareControlListener implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;
		String eventType = erEvent.getEventType();
		
		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		ErmCostShareBO bo = new ErmCostShareBO();
		IErmCostShareBillManage costBillManager = new ErmCostShareBillManageImpl();
		IErmCostShareBillManagePrivate costPrivateManager = new ErmCSBillMgePrivateImpl();
		
		for (int i = 0; i < vos.length; i++) {
			JKBXVO vo = vos[i];

			AggCostShareVO shareVo = ErmForCShareUtil.convertFromBXVO(vo);
			AggCostShareVO oldShareVo = ErmForCShareUtil.convertFromBXVO(vo.getBxoldvo());

			// 修改后无分摊情况，则删除以前存在分摊
			if (shareVo == null && oldShareVo == null) {
				continue;
			} 

			if (ErmEventType.TYPE_INSERT_AFTER.equals(eventType)) {// 保存
				costBillManager.insertVO(shareVo);
			} else if (ErmEventType.TYPE_TEMPSAVE_AFTER.equals(eventType)) {// 暂存
				shareVo.getParentVO().setStatus(VOStatus.NEW);
				costPrivateManager.tempSaveVO(shareVo);
			}else if (ErmEventType.TYPE_UPDATE_AFTER.equals(eventType)) {// 更新
				if (shareVo == null && oldShareVo != null) {
					// 修改前有分摊，修改后无分摊，则进行删除结转单操作
					bo.doDeleteVOs(new AggCostShareVO[] { oldShareVo });
				} else if (oldShareVo == null
						&& shareVo != null) {
					// 修改前无分摊，修改后有分摊，则进行新增结转单操作
					costBillManager.insertVO(shareVo);
				} else if(shareVo != null){
					shareVo.setOldvo(oldShareVo);
					// 查询修改前的vo
					IMDPersistenceQueryService qryservice = MDPersistenceService.lookupPersistenceQueryService();
					AggCostShareVO oldvo = qryservice.queryBillOfVOByPK(AggCostShareVO.class, ((CostShareVO)shareVo.getParentVO()).getPrimaryKey(), false);
					vo.setCostOldVo(oldvo);//用于预算控制
					costBillManager.updateVO(shareVo);
				}
				
			} else if (ErmEventType.TYPE_DELETE_AFTER.equals(eventType)) {// 删除
				bo.doDeleteVOs(new AggCostShareVO[] { shareVo });
			} else if (ErmEventType.TYPE_SIGN_AFTER.equals(eventType)) {// 签字
				bo.doApproveVOs(new AggCostShareVO[] { shareVo }, vo.getParentVO().getJsrq());
			} else if (ErmEventType.TYPE_UNSIGN_AFTER.equals(eventType)) {// 反签字
				bo.doUnapproveVOs(new AggCostShareVO[] { shareVo });
			} else if (ErmEventType.TYPE_TEMPUPDATE_AFTER.equals(eventType)){
				if(oldShareVo != null){
					shareVo.getParentVO().setStatus(VOStatus.UPDATED);
				}else{
					shareVo.getParentVO().setStatus(VOStatus.NEW);
				}
				costPrivateManager.tempSaveVO(shareVo);
			}
		}
	}
}
