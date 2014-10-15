package nc.ui.erm.ref;

import java.util.Arrays;
import java.util.Vector;

import nc.bs.logging.Logger;
import nc.itf.org.IOrgConst;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.ui.bd.ref.AbstractRefModel;
import nc.vo.fipub.summary.SummaryVO;
import nc.vo.pub.BusinessException;
import nc.vo.util.VisibleUtil;

/**
 * 报销专用常用摘要参照
 *
 * @author chendya
 *
 */
public class BXSummaryRefModel extends AbstractRefModel {

	public BXSummaryRefModel() {
		super();
		setRefTitle(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011ermpub0316_0","02011ermpub0316-0014")/*@res "常用摘要"*/);
		setMutilLangNameRef(false);
		reset();
	}

	@Override
	public void reset() {
		setFieldCode(new String[] { SummaryVO.CODE, SummaryVO.SUMMARYNAME });
		setFieldName(new String[] {
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
						"UC000-0003279")/* @res "编码" */,
				nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common",
						"UC000-0001155") /* @res "名称" */});
		setHiddenFieldCode(new String[] { SummaryVO.PK_SUMMARY });
		setTableName(SummaryVO.getDefaultTableName());
		setPkFieldCode(SummaryVO.PK_SUMMARY);
		setCaseSensive(true);
		resetFieldName();
	}

	@Override
	protected String getEnvWherePart() {
		StringBuffer envWherePart = new StringBuffer(" ((");
		try {
			IBean bean = MDBaseQueryFacade.getInstance()
					.getBeanByFullClassName(SummaryVO.class.getName());
			String beanID = bean.getID();
			envWherePart.append(VisibleUtil.getRefVisibleCondition(
					getPk_group(), getPk_org(), beanID));
			// 对于非公开的常用摘要，其他操作员不能参照
			// 即只能参照公开的摘要，以及操作员自己定义的私有摘要
			envWherePart.append(" AND ISPUBLIC = 'Y' ) OR (CREATOR = '");
			envWherePart.append(getPk_user());
			if (!IOrgConst.GLOBEORG.equals(getPk_org())) {
				envWherePart.append("' AND PK_ORG = '");
				envWherePart.append(getPk_org());

			}
			envWherePart.append("'))");
		} catch (BusinessException e) {
			Logger.error(e.getMessage());
			// 设置恒假条件，使界面不可以参照任何数据
			envWherePart.delete(0, envWherePart.length());
			envWherePart.append(" (1 = 2) ");
		}

		return envWherePart.toString();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Vector matchPkData(String[] strPkValues) {
		 Vector matchPkData = super.matchPkData(strPkValues);
		 if(matchPkData!=null&&matchPkData.size()>0){
			 return matchPkData;
		 }
		 Vector<Vector> vct = new Vector<Vector>();
		 Vector<String> values = new Vector<String>();
		 int index = getFieldIndex(getPkFieldCode());
		 for(int i=0;i<index;i++){
			 values.add(i, null);
		 }
		 values.add(index,strPkValues[0]);
		 vct.add(values);
		 setSelectedDataAndConvertData(vct);
		 return vct;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Vector matchPkData(String strPkValue) {
		return matchPkData(new String[]{strPkValue});
	}
	
	@Override
	public String getPkValue() {
		 String pkValue = super.getPkValue();
		 if(pkValue!=null){
			 return pkValue;
		 }
		 Vector selectedData = getSelectedData();
		 if(selectedData!=null){
			 return (String)selectedData.get(0);
		 }
		 return null;
	}
}