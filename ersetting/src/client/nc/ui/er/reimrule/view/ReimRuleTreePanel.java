package nc.ui.er.reimrule.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErmDjlxConst;
import nc.ui.erm.util.ErUiUtil;
import nc.ui.uif2.components.TreePanel;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.ml.MultiLangContext;
import nc.vo.pub.BusinessException;

public class ReimRuleTreePanel extends TreePanel {

	private static final long serialVersionUID = 1L;

	@Override
	public void init() {
		super.init();
		// 交易类型树展现
		List<DjLXVO> list = new ArrayList<DjLXVO>();
		DjLXVO[] vos = null;
		try {
			vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
					"pk_group = '"+ErUiUtil.getPK_group()+"' and djdl in('jk','bx')");
			for(DjLXVO vo:vos){
				if(vo.getAttributeValue(getMultiFieldName("djlxmc")) == null){
					//当billtypename多语字段值为空时，手动赋值为~的值（防止自定义交易类型未录制多语时，构建数报空指针）
					vo.setAttributeValue(getMultiFieldName("djlxmc"), vo.getDjlxmc());
				}
				if(vo.getBxtype()==null || vo.getBxtype() != ErmDjlxConst.BXTYPE_ADJUST)
					list.add(vo);
			}
			DjLXVO[] toArray = list.toArray(new DjLXVO[] {});
			Arrays.sort(toArray, new Comparator<DjLXVO>() {
				public int compare(DjLXVO o1, DjLXVO o2) {
					return o1.getDjlxbm().compareTo(o2.getDjlxbm());
				}
			});
			getModel().initModel(toArray);
		} catch (BusinessException e) {
			getModel().initModel(null);
		}
	}
	
	/**
	 * 获取多语字段名称（name2,name,name3）
	 * @param namefield
	 * @return
	 */
	private String getMultiFieldName(String namefield) {
		int intValue = MultiLangContext.getInstance().getCurrentLangSeq().intValue();
		if(intValue>1){
			namefield=namefield+intValue;
		}
		return namefield;
	}
}
