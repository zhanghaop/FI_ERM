package nc.ui.erm.billpub.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.bd.userdefitem.IUserdefitemQryService;
import nc.ui.querytemplate.DefQCVOProcessor;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.userdefrule.UserdefitemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.query.QueryConditionVO;
import nc.vo.pub.query.QueryTempletTotalVO;
import nc.vo.querytemplate.TemplateInfo;

public class ErmBxDefQCVOProcessor extends DefQCVOProcessor {
	TemplateInfo info = null;
	private Map<String, UserdefitemVO> attrpathDefvoMap;

	public ErmBxDefQCVOProcessor(TemplateInfo tempInfo) {
		info = tempInfo;
	}

	/**
	 * 处理自定义项查询条件，将自定义项真正的信息设回QCVO中
	 */
	public void processDefQCVOs(QueryTempletTotalVO totalVO) {
		String beanID = BXConstans.ERM_MDID_BX;
		QueryConditionVO[] vos = totalVO.getConditionVOs();
		if (vos == null || vos.length == 0) {
			return;
		}
		List<String> attrpaths = extractAttrpaths(vos);
		String pk_org = info.getPk_Org();
		try {
			if (attrpathDefvoMap == null) {
				attrpathDefvoMap = NCLocator.getInstance().lookup(IUserdefitemQryService.class).queryUserdefitemVOsByPropertyNames(beanID, attrpaths, pk_org);
			}
			super.setAttrpath_Defvo_Map(attrpathDefvoMap);
		} catch (BusinessException e) {
			ExceptionHandler.handleRuntimeException(e);
		}
		String mdid = totalVO.getTempletVO().getMetaclass();
		totalVO.getTempletVO().setMetaclass(BXConstans.ERM_MDID_BX);
		super.processDefQCVOs(totalVO);
		totalVO.getTempletVO().setMetaclass(mdid);
	};

	private List<String> extractAttrpaths(QueryConditionVO[] vos) {
		List<String> attrpaths = new ArrayList<String>();
		for (QueryConditionVO vo : vos) {
			attrpaths.add(vo.getFieldCode());
		}
		return attrpaths;
	}

}
