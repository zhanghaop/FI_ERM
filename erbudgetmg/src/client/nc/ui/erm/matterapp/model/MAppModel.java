package nc.ui.erm.matterapp.model;

import java.util.HashMap;
import java.util.Map;

import nc.bs.erm.util.CacheUtil;
import nc.ui.erm.model.ERMBillManageModel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.exception.ExceptionHandler;

import org.apache.commons.lang.ArrayUtils;

public class MAppModel extends ERMBillManageModel{
	/**
	 * 当前交易类型编码
	 */
	private String djlxbm;
	
	/**
	 * 交易类型按钮选择的单据类型编码
	 */
	private String selectBillTypeCode = null;  
	
	/**
	 * 所有的单据类型
	 */
	private Map<String, DjLXVO> billTypeMapCache = new HashMap<String, DjLXVO>();

	/**
	 * 返回单据类型vo
	 * @param billTypeCode
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public DjLXVO getTradeTypeVo(String billTypeCode) {
		initBillTypeMapCache();// 通过系统缓存初始化节点缓存billTypeMapCache
		return billTypeMapCache.get(billTypeCode);
	}

	/**
	 * 初始化交易类型VO缓存
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void initBillTypeMapCache() {
		try {
			DjLXVO[] djLXVOs = getAllDJLXVOs();
			if (ArrayUtils.isEmpty(djLXVOs)) {
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0025")/* @res "交易类型被删除" */);
			}

			for (DjLXVO djLXVO : djLXVOs) {
				billTypeMapCache.put(djLXVO.getDjlxbm(), djLXVO);
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
			return CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, " djdl='ma' and pk_group='" + group
					+ "' order by djlxbm ");
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
		
		return null;
	}

	public String getDjlxbm() {
		return djlxbm;
	}

	public void setDjlxbm(String djlxbm) {
		this.djlxbm = djlxbm;
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
		return selectBillTypeCode;
	}

	public void setSelectBillTypeCode(String selectBillTypeCode) {
		this.selectBillTypeCode = selectBillTypeCode;
	}

	public Map<String, DjLXVO> getBillTypeMapCache() {
		return billTypeMapCache;
	}

	public void setBillTypeMapCache(Map<String, DjLXVO> billTypeMapCache) {
		this.billTypeMapCache = billTypeMapCache;
	}
}