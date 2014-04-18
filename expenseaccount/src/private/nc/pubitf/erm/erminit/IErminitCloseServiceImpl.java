package nc.pubitf.erm.erminit;

import java.util.List;

import nc.bs.erm.erminit.ErminitCloseBO;
import nc.bs.framework.common.NCLocator;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;

public class IErminitCloseServiceImpl implements IErminitCloseService {

	@Override
	public boolean close(String pkOrg) throws BusinessException {
		return new ErminitCloseBO().close(pkOrg);
	}

	@Override
	public boolean unclose(String pkOrg) throws BusinessException {
		//校验这个组织，如果有结账的情况，就不可以取消关闭
		checkAcc(pkOrg);
		return new ErminitCloseBO().unclose(pkOrg);
	}

	private void checkAcc(String pkOrg) throws BusinessException {
		StringBuffer msg = new StringBuffer();
		List<CloseAccBookVO> accvos = getEndAcc(pkOrg);
		if (accvos == null || accvos.size() == 0) {
			return;
		}
		//只要一个月结账了，就不可以取消关闭了
		for (CloseAccBookVO vo : accvos) {
			if (vo.getIsendacc().equals(UFBoolean.TRUE)) {
				if(msg.length()==0){
					msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0101")/*@res "该组织费用管理结账，不可以取消关闭"*/);
				}
			}
		}

		if (msg.length() != 0) {
			throw new BusinessException(msg.toString());
		}

	}

	private List<CloseAccBookVO> getEndAcc(String pk_org)
			throws BusinessException {
		List<CloseAccBookVO> vos= getIErminitQueryService().queryAccStatusByOrg(pk_org);
		return vos;
	}

	public IErminitQueryService getIErminitQueryService() {
		return NCLocator.getInstance().lookup(
				IErminitQueryService.class);
	}



}