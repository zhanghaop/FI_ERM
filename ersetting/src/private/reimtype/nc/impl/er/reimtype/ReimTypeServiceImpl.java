package nc.impl.er.reimtype;


import nc.bs.bd.baseservice.BaseService;
import nc.itf.er.reimtype.IReimTypeService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.pub.BusinessException;

public class ReimTypeServiceImpl extends BaseService<ReimTypeVO> implements IReimTypeService {
	/**
	 * @author liansg
	 */
	public ReimTypeServiceImpl(String MDId) {

		super(MDId);

	}
	
	public ReimTypeServiceImpl(){
		
		super("");
	}
	@Override	
	public BatchOperateVO batchSaveReimType(BatchOperateVO batchVO)
			throws BusinessException {
//		IMDPersistenceService service = MDPersistenceService
//				.lookupPersistenceService();
//		ArrayList<ReimTypeVO> list = new ArrayList<ReimTypeVO>();
//
//		Object[] addObjs = batchVO.getAddObjs();
//		if (addObjs != null) {
//			for (int i = 0; i < addObjs.length; i++) {
//				ReimTypeVO addVO = (ReimTypeVO) addObjs[i];
//				addVO.setStatus(VOStatus.NEW);
//				list.add(addVO);
//			}
//		}
//
//		Object[] delObjs = batchVO.getDelObjs();
//		if (delObjs != null) {
//			for (int i = 0; i < delObjs.length; i++) {
//				ReimTypeVO delVO = (ReimTypeVO) delObjs[i];
//				delVO.setStatus(VOStatus.DELETED);
//				list.add(delVO);
//			}
//		}
//
//		Object[] updObjs = batchVO.getUpdObjs();
//		if (updObjs != null) {
//			for (int i = 0; i < updObjs.length; i++) {
//				ReimTypeVO updVO = (ReimTypeVO) updObjs[i];
//				updVO.setStatus(VOStatus.UPDATED);
//				list.add(updVO);
//			}
//		}
//
//		service.saveBillWithRealDelete(list.toArray(new ReimTypeVO[0]));
//
//		return new BatchOperateVO();
		return batchSave(batchVO, ReimTypeVO.class);
	}

}
