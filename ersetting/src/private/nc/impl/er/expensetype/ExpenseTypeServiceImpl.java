package nc.impl.er.expensetype;

import nc.bs.bd.baseservice.BaseService;
import nc.itf.er.expensetype.IExpenseTypeService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.expensetype.ExpenseTypeVO;
import nc.vo.pub.BusinessException;

/**
 * @author liansg
 */


public class ExpenseTypeServiceImpl extends BaseService<ExpenseTypeVO> implements IExpenseTypeService {
	
	public ExpenseTypeServiceImpl(String MDId) {

		super(MDId);

	}
	
	public ExpenseTypeServiceImpl(){
		
		super("");
	}

	@Override
	public BatchOperateVO batchSaveExpenseType(BatchOperateVO batchVO)
			throws BusinessException {
//		IMDPersistenceService service = MDPersistenceService
//				.lookupPersistenceService();
//		ArrayList<ExpenseTypeVO> list = new ArrayList<ExpenseTypeVO>();
//
//		Object[] addObjs = batchVO.getAddObjs();
//		if (addObjs != null) {
//			for (int i = 0; i < addObjs.length; i++) {
//				ExpenseTypeVO addVO = (ExpenseTypeVO) addObjs[i];
//				addVO.setStatus(VOStatus.NEW);
//				list.add(addVO);
//			}
//		}
//
//		Object[] delObjs = batchVO.getDelObjs();
//		if (delObjs != null) {
//			for (int i = 0; i < delObjs.length; i++) {
//				ExpenseTypeVO delVO = (ExpenseTypeVO) delObjs[i];
//				delVO.setStatus(VOStatus.DELETED);
//				list.add(delVO);
//			}
//		}
//
//		Object[] updObjs = batchVO.getUpdObjs();
//		if (updObjs != null) {
//			for (int i = 0; i < updObjs.length; i++) {
//				ExpenseTypeVO updVO = (ExpenseTypeVO) updObjs[i];
//				updVO.setStatus(VOStatus.UPDATED);
//				list.add(updVO);
//			}
//		}

//		service.saveBillWithRealDelete(list.toArray(new ExpenseTypeVO[0]));
//		batchSave(vo, convertToClass)
		return batchSave(batchVO, ExpenseTypeVO.class);

//		return new BatchOperateVO();
	}

}
