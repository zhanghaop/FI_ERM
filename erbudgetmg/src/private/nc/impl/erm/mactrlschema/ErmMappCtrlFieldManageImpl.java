package nc.impl.erm.mactrlschema;

import nc.bs.bd.baseservice.md.BatchBaseService;
import nc.bs.businessevent.EventDispatcher;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.itf.erm.mactrlschema.IErmMappCtrlFieldManage;
import nc.vo.erm.mactrlschema.MtappCtrlfieldVO;
import nc.vo.pub.BusinessException;

public class ErmMappCtrlFieldManageImpl extends BatchBaseService<MtappCtrlfieldVO> implements IErmMappCtrlFieldManage {

	private static final String MDId = "e779296b-cd8e-4efa-935d-e87ee2432071";

	public ErmMappCtrlFieldManageImpl() {
		super(MDId);
	}

	@Override
	protected void fireAfterDeleteEvent(MtappCtrlfieldVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_DELETE_AFTER, vos));
	}

	@Override
	protected void fireAfterUpdateEvent(MtappCtrlfieldVO[] oldVOs, MtappCtrlfieldVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_UPDATE_AFTER, vos,
				oldVOs));
	}

	@Override
	protected void fireBeforeDeleteEvent(MtappCtrlfieldVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_DELETE_BEFORE, vos));
	}

	@Override
	protected void fireBeforeUpdateEvent(MtappCtrlfieldVO[] oldVOs, MtappCtrlfieldVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_UPDATE_BEFORE, vos,
				oldVOs));
	}

	@Override
	protected void fireAfterInsertEvent(MtappCtrlfieldVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_INSERT_AFTER, vos));
	}

	@Override
	protected void fireBeforeInsertEvent(MtappCtrlfieldVO... vos) throws BusinessException {
		EventDispatcher.fireEvent(new ErmBusinessEvent(MDId, ErmEventType.TYPE_INSERT_BEFORE, vos));
	}
	
	

	// private IMDPersistenceService service = null;
	//
	// private IMDPersistenceQueryService queryService = null;
	//
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
	// MtappCtrlfieldVO[] updateVos = updateCtrlFieldVos(batchVO.getUpdObjs());
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
	// private MtappCtrlfieldVO[] updateCtrlFieldVos(Object[] updObjs) throws
	// BusinessException {
	// MtappCtrlfieldVO[] updatedVos = new MtappCtrlfieldVO[updObjs.length];
	// try {
	// for (int i = 0; i < updObjs.length; i++) {
	// checkSave(updObjs[i]);
	// String pk = getMDPersistenceService().saveBill(updObjs[i]);
	// updatedVos[i] =
	// (MtappCtrlfieldVO)getMDQueryService().queryBillOfVOByPK(MtappCtrlfieldVO.class,
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
}
