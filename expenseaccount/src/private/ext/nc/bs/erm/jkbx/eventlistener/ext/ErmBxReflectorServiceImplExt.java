package nc.bs.erm.jkbx.eventlistener.ext;

import java.util.ArrayList;
import java.util.List;

import nc.bs.erm.ext.common.BXFromMaHelper;
import nc.impl.arap.bx.ErmBxReflectorServiceImpl;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

/**
 * ������ƾ֤���㣬������������ƾ֤
 * 
 * ����Ԫר��
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
				// ��ó����뱨���������뵥ת������̯ҳǩ������
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
