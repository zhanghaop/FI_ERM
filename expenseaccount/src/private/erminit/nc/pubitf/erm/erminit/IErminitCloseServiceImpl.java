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
		//У�������֯������н��˵�������Ͳ�����ȡ���ر�
		checkAcc(pkOrg);
		return new ErminitCloseBO().unclose(pkOrg);
	}

	private void checkAcc(String pkOrg) throws BusinessException {
		StringBuffer msg = new StringBuffer();
		List<CloseAccBookVO> accvos = getEndAcc(pkOrg);
		if (accvos == null || accvos.size() == 0) {
			return;
		}
		//ֻҪһ���½����ˣ��Ͳ�����ȡ���ر���
		for (CloseAccBookVO vo : accvos) {
			if (vo.getIsendacc().equals(UFBoolean.TRUE)) {
				if(msg.length()==0){
					msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0101")/*@res "����֯���ù�����ˣ�������ȡ���ر�"*/);
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