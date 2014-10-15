package nc.impl.er.indauthorize;

import nc.bs.bd.baseservice.BaseService;
import nc.itf.er.indauthorize.IIndAuthorizeService;
import nc.vo.bd.meta.BatchOperateVO;
import nc.vo.er.indauthorize.IndAuthorizeVO;
import nc.vo.pub.BusinessException;

/**
 * @author liansg
 */
public class IndAuthorizeServiceImpl extends BaseService<IndAuthorizeVO> implements IIndAuthorizeService {
	
	public IndAuthorizeServiceImpl(String MDId) {
		super(MDId);
		
	}

	public IndAuthorizeServiceImpl(){
		
		super("");
	}
	public BatchOperateVO batchSaveIndAuthorize(BatchOperateVO batchVO)
			throws BusinessException {

		return batchSave(batchVO, IndAuthorizeVO.class);
	}

}
