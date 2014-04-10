package nc.bs.erm.matterappctrl.ext;

import nc.pubitf.erm.matterappctrl.ext.IMatterAppCtrlServiceExt;
import nc.vo.erm.matterappctrl.IMtappCtrlBusiVO;
import nc.vo.erm.matterappctrl.MtappCtrlInfoVO;
import nc.vo.pub.BusinessException;

import org.apache.commons.lang.ArrayUtils;

/**
 * 费用申请单控制回写扩展服务，实现类
 * 
 * @author lvhj
 *
 */
public class MatterAppCtrlServiceExtImp implements IMatterAppCtrlServiceExt {

	@Override
	public MtappCtrlInfoVO matterappControlByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBOExt(true).matterappControlByRatio(vos);
	}

	@Override
	public MtappCtrlInfoVO matterappValidateByRatio(IMtappCtrlBusiVO[] vos) throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBOExt(false).matterappControlByRatio(vos);
	}

	@Override
	public MtappCtrlInfoVO matterappControlByDetail(IMtappCtrlBusiVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBOExt(true).matterappControlByDetail(vos);
	}

	@Override
	public MtappCtrlInfoVO matterappValidateByDetail(IMtappCtrlBusiVO[] vos)
			throws BusinessException {
		if (ArrayUtils.isEmpty(vos)) {
			return null;
		}
		
		return new MatterAppCtrlBOExt(false).matterappControlByDetail(vos);
	}
}
