package nc.bs.erm.erminit;

//import java.util.Map;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.businessevent.bd.BDCommonEvent;
import nc.bs.businessevent.bd.BDCommonEvent.BDCommonUserObj;
import nc.bs.erm.annotation.ErmBusinessDef;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.erminit.IErminitQueryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.erm.annotation.CloseAccBiz;
import nc.vo.fipub.annotation.Business;
import nc.vo.fipub.annotation.BusinessType;
import nc.vo.org.CloseAccBookVO;
import nc.vo.pub.BusinessException;

/**
 * 结账前校验期初是否关闭
 *
 * @author wangled
 *
 */
public class ExminitCloseListener implements IBusinessListener {

	@Override
	@Business(business=ErmBusinessDef.CloseAcc,subBusiness=CloseAccBiz.InitCloseBeforeCloseAcc, description = "结账前校验期初是否关闭" /*-=notranslate=-*/,type=BusinessType.CORE)
	public void doAction(IBusinessEvent event) throws BusinessException {
		BDCommonEvent erEvent = (BDCommonEvent) event;
		BDCommonUserObj obj = (BDCommonUserObj) erEvent.getUserObject();
		Object vos[] = (Object[]) obj.getNewObjects();
		// 只校验费用管理模块
		CloseAccBookVO vo = ((CloseAccBookVO) vos[0]);
		if (!BXConstans.ERM_MODULEID.equals(vo.getModuleid())) {
			return;
		}
        StringBuffer msg = new StringBuffer();
		// 校验组织是否期初关闭
        String acc = checkCloseAcc(vo);
        if (acc != null) {
            msg.append(acc);
            if (msg.charAt(msg.length() - 1) == '\n')
                msg.deleteCharAt(msg.length() - 1);
        }
		if (msg.length() != 0) {
			throw new BusinessException(msg.toString());
		}
	}

	@Business(business=ErmBusinessDef.CloseAcc,subBusiness=CloseAccBiz.InitCloseBeforeCloseAcc, description = "结账前校验期初是否关闭" /*-=notranslate=-*/,type=BusinessType.CORE)
	private String checkCloseAcc(CloseAccBookVO vo) throws BusinessException {
		String pk_org = vo.getPk_org();
		StringBuffer msg = new StringBuffer();
		boolean flag = getService().queryStatusByOrg(pk_org);
		if (flag == false) {
			msg.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0100")/*@res "组织期初未关闭，不可以进行结账 !\n"*/);
		}
		if (msg.length() != 0) {
			return msg.toString();
		} else {
			return null;
		}

	}

	private IErminitQueryService getService() {
		return NCLocator.getInstance().lookup(IErminitQueryService.class);
	}

}