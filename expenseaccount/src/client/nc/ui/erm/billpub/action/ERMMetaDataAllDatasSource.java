package nc.ui.erm.billpub.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataAllDatasSource;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.pub.BusinessException;

public class ERMMetaDataAllDatasSource extends MetaDataAllDatasSource{
	
	private static final long serialVersionUID = 1L;

	@Override
	public Object[] getMDObjects() {
		 Object[] mdObjects = super.getMDObjects();
		 Map<String, String[]> fieldsMap = new HashMap<String, String[]>();
			fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY,new String[] {JKBXHeaderVO.TOTAL,JKBXHeaderVO.YBJE,JKBXHeaderVO.YBYE,JKBXHeaderVO.AMOUNT});
			try {
				mdObjects = ERMPrintDigitUtil.getDatas(mdObjects, fieldsMap, IBillFieldGet.PK_ORG, JKBXHeaderVO.BZBM);
			} catch (NumberFormatException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (IllegalArgumentException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (IllegalAccessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (BusinessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			}
			
			List<JKBXHeaderVO> headList = new ArrayList<JKBXHeaderVO>();
			for (Object data : mdObjects) {
				headList.add(((JKBXVO)data).getParentVO());
			}
			return headList.toArray();
	}
}