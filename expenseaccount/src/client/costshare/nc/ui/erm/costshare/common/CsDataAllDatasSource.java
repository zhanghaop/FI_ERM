package nc.ui.erm.costshare.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.arap.fieldmap.IBillFieldGet;
import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataAllDatasSource;
import nc.ui.erm.costshare.ui.CostShareView;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.pub.BusinessException;
/**
 * 费用结转列表界面所有行选择时打印
 * @author wangled
 *
 */
public class CsDataAllDatasSource extends MetaDataAllDatasSource{

	private static final long serialVersionUID = 1L;
	private CostShareView csShareView;
	@Override
	public Object[] getMDObjects() {
			Object[] mdObjects = super.getMDObjects();
			Map<String, String[]> fieldsMap = new HashMap<String, String[]>();
			fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY,new String[] {CostShareVO.TOTAL,CostShareVO.YBJE,CShareDetailVO.ASSUME_AMOUNT});
			fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_LOCAL,new String[] {CostShareVO.BBJE});
			fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_GLOBAL,new String[] {CostShareVO.GLOBALBBJE});
			fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_GROUP,new String[] {CostShareVO.GROUPBBJE});
			try {
				mdObjects = ERMPrintDigitUtil.getDatas(mdObjects, fieldsMap, IBillFieldGet.PK_ORG, CostShareVO.BZBM);
			} catch (NumberFormatException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (IllegalArgumentException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (IllegalAccessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			} catch (BusinessException e) {
				nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			}
			
			List<AggCostShareVO> csList = new ArrayList<AggCostShareVO>();
			for (Object data : mdObjects) {
				csList.add((AggCostShareVO) data);
			}
		 return csList.toArray();
	}
	public void setCsShareView(CostShareView csShareView) {
		this.csShareView = csShareView;
	}
	public CostShareView getCsShareView() {
		return csShareView;
	}
}
