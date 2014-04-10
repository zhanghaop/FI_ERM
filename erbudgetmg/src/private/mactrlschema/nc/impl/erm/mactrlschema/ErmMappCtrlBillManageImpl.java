package nc.impl.erm.mactrlschema;

import nc.bs.bd.baseservice.md.BatchBaseService;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.itf.erm.mactrlschema.IErmMappCtrlBillManage;
import nc.vo.erm.mactrlschema.MtappCtrlbillVO;
import nc.vo.pub.BusinessException;

public class ErmMappCtrlBillManageImpl extends BatchBaseService<MtappCtrlbillVO> implements IErmMappCtrlBillManage {
	private static final String MDId = "7b51cdf3-576c-47bb-99f3-8623bb092531";

	public ErmMappCtrlBillManageImpl() {
		super(MDId);
	}

	@Override
	protected void fireAfterDeleteEvent(MtappCtrlbillVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_DELETE_AFTER, vos));
	}

	@Override
	protected void fireAfterUpdateEvent(MtappCtrlbillVO[] oldVOs, MtappCtrlbillVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_UPDATE_AFTER, vos, oldVOs));
	}

	@Override
	protected void fireBeforeDeleteEvent(MtappCtrlbillVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_DELETE_BEFORE, vos));
	}

	@Override
	protected void fireBeforeUpdateEvent(MtappCtrlbillVO[] oldVOs, MtappCtrlbillVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_UPDATE_BEFORE, vos, oldVOs));
	}

	@Override
	protected void fireAfterInsertEvent(MtappCtrlbillVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_INSERT_AFTER, vos));
	}

	@Override
	protected void fireBeforeInsertEvent(MtappCtrlbillVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_INSERT_BEFORE, vos));
	}
	
	// @Override
	// public BatchOperateVO batchSave(BatchOperateVO batchVO) throws
	// BusinessException {
	// if (batchVO == null)
	// return null;
	//
	// if (batchVO.getDelObjs() != null && batchVO.getDelObjs().length > 0) {
	// service.deleteBill(batchVO.getDelObjs());
	// }
	//
	// if (batchVO.getUpdObjs() != null && batchVO.getUpdObjs().length > 0) {
	// MtappCtrlbillVO[] updateVos = updateCtrlFieldVos(batchVO.getUpdObjs());
	// batchVO.setUpdObjs(updateVos);
	// }
	//
	// if (batchVO.getAddObjs() != null && batchVO.getAddObjs().length > 0) {
	// String[] pks = insertCtrlFieldVos(batchVO.getAddObjs());
	// Collection<MtappCtrlfieldVO> addList =
	// getMDQueryService().queryBillOfVOByPKs(MtappCtrlfieldVO.class, pks,
	// false);
	// batchVO.setAddObjs(addList.toArray(new MtappCtrlfieldVO[] {}));
	// }
	//
	// return batchVO;
	// }
	//
	// private String[] insertCtrlFieldVos(Object[] insertObjs) throws
	// BusinessException {
	// String[] pks = new String[insertObjs.length];
	// try {
	// for (int i = 0; i < insertObjs.length; i++) {
	// checkSave(insertObjs[i]);
	// pks[i] = getMDPersistenceService().saveBill(insertObjs[i]);
	// }
	// } catch (MetaDataException e) {
	// ExceptionHandler.handleException(e);
	// }
	//		
	// return pks;
	// }
	//
	// private MtappCtrlbillVO[] updateCtrlFieldVos(Object[] updObjs) throws
	// BusinessException {
	// MtappCtrlbillVO[] updatedVos = new MtappCtrlbillVO[updObjs.length];
	// try {
	// for (int i = 0; i < updObjs.length; i++) {
	// checkSave(updObjs[i]);
	// String pk = getMDPersistenceService().saveBill(updObjs[i]);
	// updatedVos[i] =
	// (MtappCtrlbillVO)getMDQueryService().queryBillOfVOByPK(MtappCtrlbillVO.class,
	// pk, false);
	// }
	// } catch (MetaDataException e) {
	// ExceptionHandler.handleException(e);
	// }
	//		
	// return updatedVos;
	// }
	//
	// private void checkSave(Object object) {
	//		
	// }
	//	
	// private IMDPersistenceQueryService getMDQueryService() {
	// if (queryService == null) {
	// queryService = MDPersistenceService.lookupPersistenceQueryService();
	// }
	//
	// return queryService;
	// }
	//
	// private IMDPersistenceService getMDPersistenceService() {
	// if (service == null) {
	// service = MDPersistenceService.lookupPersistenceService();
	// }
	// return service;
	// }
	//	
	//
	// private IMDPersistenceService service = null;
	//
	// private IMDPersistenceQueryService queryService = null;
}
