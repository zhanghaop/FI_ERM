package nc.ui.erm.costshare.common;

import java.util.HashMap;
import java.util.Map;

import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
/**
 * 费用结转卡片打印
 * @author wangled
 *
 */
public class CSDataSingleDataSource extends MetaDataSingleSelectDataSource{

	private static final long serialVersionUID = 1L;
	
	@Override
	public Object[] getMDObjects() {
		AggCostShareVO aggVO = (AggCostShareVO) getModel().getSelectedData();
		
		Object[] obj =new Object[0];
		Map<String, String[]> fieldsMap = new HashMap<String, String[]>();
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY,new String[] {CostShareVO.TOTAL,CostShareVO.YBJE,CShareDetailVO.ASSUME_AMOUNT,CShareDetailVO.SHARE_RATIO});
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_LOCAL,new String[] {CostShareVO.BBJE});
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_GLOBAL,new String[] {CostShareVO.GLOBALBBJE});
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_GROUP,new String[] {CostShareVO.GROUPBBJE});
		try {
			obj= ERMPrintDigitUtil.getDatas(new AggCostShareVO[]{aggVO}, fieldsMap, IBillFieldGet.PK_ORG, CostShareVO.BZBM);
		} catch (NumberFormatException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} catch (IllegalArgumentException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} catch (IllegalAccessException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		} catch (BusinessException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
		}
		return obj;
	}
}
