package nc.ui.erm.expamortize.model;

import java.util.HashMap;
import java.util.Map;

import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataAllDatasSource;
import nc.vo.erm.expamortize.ExpamtinfoVO;
import nc.vo.pub.BusinessException;
/**
 * 摊销列表界面所有行选择时打印
 * @author wangled
 *
 */
public class ERMMetaDataAllDatasSource extends MetaDataAllDatasSource{
	private static final long serialVersionUID = 1L;
	
	@Override
	public Object[] getMDObjects() {
		 Object[] mdObjects = super.getMDObjects();
		 Map<String, String[]> fieldsMap = new HashMap<String, String[]>();
			fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY,new String[] {ExpamtinfoVO.TOTAL_AMOUNT,ExpamtinfoVO.RES_AMOUNT});
			try {
				mdObjects = ERMPrintDigitUtil.getDatas(mdObjects, fieldsMap, IBillFieldGet.PK_ORG, ExpamtinfoVO.BZBM);
			} catch (NumberFormatException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (IllegalArgumentException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (IllegalAccessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (BusinessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			}
		 return mdObjects;
	}
}
