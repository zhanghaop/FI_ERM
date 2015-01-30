package nc.ui.erm.accruedexpense.actions;

import java.util.HashMap;
import java.util.Map;

import nc.ui.arap.bx.print.ERMPrintDigitUtil;
import nc.ui.bd.pub.actions.print.MetaDataSingleSelectDataSource;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.pub.BusinessException;

public class AccruedPrintMetaDataSingleDataSource extends MetaDataSingleSelectDataSource {
	private static final long serialVersionUID = 1L;
	
	private AggAccruedBillVO aggAccVo = null;
	
	public AccruedPrintMetaDataSingleDataSource() {
	}

	public AccruedPrintMetaDataSingleDataSource(AggAccruedBillVO aggAccVo) {
		this.aggAccVo = aggAccVo;
	}
	
	@Override
	public Object[] getMDObjects() {
		Object[] result = null;
		if (getAggAccVo() != null) {
			result = new Object[] { getAggAccVo() };
		} else {
			result = super.getMDObjects();
		}

		Map<String, String[]> fieldsMap = new HashMap<String, String[]>();

		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_MONEY, new String[] { AccruedVO.AMOUNT,
				AccruedVO.REST_AMOUNT, AccruedVO.VERIFY_AMOUNT, AccruedVO.PREDICT_REST_AMOUNT });
		fieldsMap.put(ERMPrintDigitUtil.ErmPrintDigitConst.FIELD_LOCAL, new String[] { AccruedVO.ORG_AMOUNT,
				AccruedVO.ORG_REST_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT });

		try {
			result = ERMPrintDigitUtil.getDatas(result, fieldsMap, AccruedVO.PK_ORG, AccruedVO.PK_CURRTYPE);
		} catch (NumberFormatException e) {
			ExceptionHandler.consume(e);
		} catch (IllegalArgumentException e) {
			ExceptionHandler.consume(e);
		} catch (IllegalAccessException e) {
			ExceptionHandler.consume(e);
		} catch (BusinessException e) {
			ExceptionHandler.consume(e);
		}
		return result;
	}

	public AggAccruedBillVO getAggAccVo() {
		return aggAccVo;
	}

	public void setAggAccVo(AggAccruedBillVO aggAccVo) {
		this.aggAccVo = aggAccVo;
	}
}
