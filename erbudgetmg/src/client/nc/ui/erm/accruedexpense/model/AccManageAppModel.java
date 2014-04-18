package nc.ui.erm.accruedexpense.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;

import nc.bs.erm.util.CacheUtil;
import nc.ui.erm.model.ERMBillManageModel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;

public class AccManageAppModel extends ERMBillManageModel {
	/**
	 * 当前交易类型编码
	 */
	private String currentTradeTypeCode;
	
	/**
	 * 交易类型按钮选择的单据类型编码
	 */
	private String selectTradeTypeCode = null;  
	
	/**
	 * 所有的单据类型
	 */
	private Map<String, DjLXVO> tradeTypeMapCache = new HashMap<String, DjLXVO>();

	/**
	 * 返回djlxvo
	 * @param billTypeCode
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public DjLXVO getTradeTypeVo(String billTypeCode) {
		initTradeTypeMapCache();// 通过系统缓存初始化节点缓存billTypeMapCache
		return tradeTypeMapCache.get(billTypeCode);
	}

	/**
	 * 初始化交易类型VO缓存
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void initTradeTypeMapCache() {
		try {
			DjLXVO[] djLXVOs = getAllDJLXVOs();
			if (ArrayUtils.isEmpty(djLXVOs)) {
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0025")/* @res "交易类型被删除" */);
			}

			for (DjLXVO djLXVO : djLXVOs) {
				tradeTypeMapCache.put(djLXVO.getDjlxbm(), djLXVO);
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	/**
	 * 获取所有交易类型
	 * 包含已封存单据类型
	 * @return
	 */
	public DjLXVO[] getAllDJLXVOs(){
		String group = getContext().getPk_group();
		try {
			return CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " djdl='ac' and pk_group='" + group
					+ "' order by djlxbm ");
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		
		return null;
	}
	
	public String getCurrentTradeTypeCode() {
		return currentTradeTypeCode;
	}

	public void setCurrentTradeTypeCode(String currentTradeTypeCode) {
		this.currentTradeTypeCode = currentTradeTypeCode;
	}

	public String getSelectTradeTypeCode() {
		return selectTradeTypeCode;
	}

	public void setSelectTradeTypeCode(String selectTradeTypeCode) {
		this.selectTradeTypeCode = selectTradeTypeCode;
	}

	@SuppressWarnings("unchecked")
	public void directlyUpdateWithoutFireEvent(Object obj) throws Exception {
		if (obj == null)
			return;
		if (getSelectedRow() >= 0) {
			getData().set(getSelectedRow(), obj);
		}
	}

	public String getSelectBillTypeCode() {
		return selectTradeTypeCode;
	}

	public void setSelectBillTypeCode(String selectBillTypeCode) {
		this.selectTradeTypeCode = selectBillTypeCode;
	}

	public Map<String, DjLXVO> getBillTypeMapCache() {
		return tradeTypeMapCache;
	}

	public void setBillTypeMapCache(Map<String, DjLXVO> billTypeMapCache) {
		this.tradeTypeMapCache = billTypeMapCache;
	}
}
