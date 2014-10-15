package nc.bs.erm.jkbx.eventlistener.ext;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.ext.common.BXFromMaHelper;
import nc.impl.arap.bx.ErmBxReflectorServiceImpl;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * 报销单凭证重算，包括超申请差额凭证
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class ErmBxReflectorServiceImplExt extends ErmBxReflectorServiceImpl {

	@Override
	protected List<JKBXVO> getBusiBill(String[] keys) throws BusinessException {
		List<JKBXVO> busiBill = super.getBusiBill(keys);
		if(busiBill != null && !busiBill.isEmpty()){
			List<JKBXVO> allbill = new ArrayList<JKBXVO>();
			allbill.addAll(busiBill);
			for (JKBXVO jkbxvo : busiBill) {
				// 获得超申请报销单，申请单转换到分摊页签的数据
				JKBXVO newbxvo = BXFromMaHelper.getMaBalanceBxVOForFip(jkbxvo);
				if(newbxvo != null){
					allbill.add(newbxvo);
				}
			}
			busiBill = allbill;
		}
		return busiBill;
	}
}
