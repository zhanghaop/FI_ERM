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
	 * ��ǰ�������ͱ���
	 */
	private String currentTradeTypeCode;
	
	/**
	 * �������Ͱ�ťѡ��ĵ������ͱ���
	 */
	private String selectTradeTypeCode = null;  
	
	/**
	 * ���еĵ�������
	 */
	private Map<String, DjLXVO> tradeTypeMapCache = new HashMap<String, DjLXVO>();

	/**
	 * ����djlxvo
	 * @param billTypeCode
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	public DjLXVO getTradeTypeVo(String billTypeCode) {
		initTradeTypeMapCache();// ͨ��ϵͳ�����ʼ���ڵ㻺��billTypeMapCache
		return tradeTypeMapCache.get(billTypeCode);
	}

	/**
	 * ��ʼ����������VO����
	 * 
	 * @author: wangyhh@ufida.com.cn
	 */
	private void initTradeTypeMapCache() {
		try {
			DjLXVO[] djLXVOs = getAllDJLXVOs();
			if (ArrayUtils.isEmpty(djLXVOs)) {
				throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0",
						"0201212-0025")/* @res "�������ͱ�ɾ��" */);
			}

			for (DjLXVO djLXVO : djLXVOs) {
				tradeTypeMapCache.put(djLXVO.getDjlxbm(), djLXVO);
			}
		} catch (Exception e) {
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}
	
	/**
	 * ��ȡ���н�������
	 * �����ѷ�浥������
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
