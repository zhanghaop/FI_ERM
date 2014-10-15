package nc.ui.erm.util.report;

import java.awt.Container;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.desktop.ui.WorkbenchEnvironment;
import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.ui.iufo.freereport.extend.DefaultQueryAction;
import nc.ui.ml.NCLangRes;
import nc.ui.pub.beans.MessageDialog;
import nc.ui.pub.beans.UIDialog;
import nc.ui.querytemplate.QueryConditionDLG;
import nc.ui.querytemplate.filter.DefaultFilter;
import nc.ui.querytemplate.filter.IFilter;
import nc.ui.querytemplate.querytree.IQueryScheme;
import nc.ui.querytemplate.querytree.QueryScheme;
import nc.ui.querytemplate.value.DefaultFieldValueElement;
import nc.ui.querytemplate.value.IFieldValueElement;
import nc.ui.querytemplate.value.RefValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.query.IQueryConstants;
import nc.vo.querytemplate.TemplateInfo;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.model.AbsAnaReportModel;
import com.ufida.report.free.plugin.param.IReportVarService;
import com.ufida.report.free.plugin.param.ReportVariable;
import com.ufida.report.free.plugin.param.ReportVariables;

@SuppressWarnings("restriction")
public abstract class  ErmDefaultQryAction extends DefaultQueryAction implements IReportVarService,Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String,String[]> varMap = new HashMap<String, String[]>();
	public void repeatMapKey(){
		varMap.clear();
		for (int i = 0; i < getVars().size(); i++) {
			varMap.put(getVars().get(i).getKey(), null);
		}
	}

	public String getValPKString(String key){
		if(varMap.get(key)!=null){
			return ((String[])varMap.get(key))[1];
		}
		return null;
	}

	public String getValShowString(String key){
		if(varMap.get(key)!=null){
			return ((String[])varMap.get(key))[0];
		}
		return null;
	}

	@Override
	public abstract List<ReportVariable> getVars();
	/*
	 * 点查询之后调用此方法
	 */
	public void deforeShowDialog(IContext context,String sqlWhere){}

	@Override
	public Object getVarsValues(IContext context, String paraKey) {
		Object obj = context.getAttribute(this.getClass().getName());
		if (obj == null || !(obj instanceof ReportVariables)) {
			return null;
		}

		ReportVariables varPool = (ReportVariables) obj;

		return varPool.getValue(paraKey);
	}

	@Override
	protected IQueryCondition showQueryDialog(Container parent, IContext context, AbsAnaReportModel reportModel,
			TemplateInfo tempinfo, IQueryCondition oldCondition) {
		QueryConditionDLG queryDlg = getQueryConditionDlg(parent, context, reportModel, oldCondition);
		queryDlg = beforeShowModal(queryDlg, parent, context, tempinfo, oldCondition);
		if (queryDlg.showModal() == UIDialog.ID_OK) {
			deforeShowDialog(context,queryDlg.getWhereSQL());
			IQueryCondition condition = setQryExpTrendQryVO(context,reportModel, queryDlg.getWhereSQL());
			// 设置报表变量池中变量的值
			try {
				getQuerySchemeMap((QueryScheme) queryDlg.getQueryScheme());
			} catch (BusinessException e) {
				MessageDialog.showErrorDlg(parent,NCLangRes.getInstance().getStrByID("uif2", "ExceptionHandlerWithDLG-000000")/*错误*/, e.getMessage());
				return null;
			}
			setReportHeadVas(context, reportModel);
			return condition;
		} else {
			return new BaseQueryCondition(false);
		}

	}

	private void setReportHeadVas(IContext context,
			AbsAnaReportModel reportModel) {
		ReportVariables varPool = ReportVariables.getInstance(reportModel.getFormatModel());
		ReportVariable var;
		for (int i = 0; i < getVars().size(); i++) {
			var = varPool.getVariable(getVars().get(i).getKey());
			var.setValue(getValShowString(getVars().get(i).getKey()));
		}
		context.setAttribute(this.getClass().getName(), varPool);
	}

	public abstract IQueryCondition setQryExpTrendQryVO(IContext context,AbsAnaReportModel reportModel,
			String sqlWhere);

	/**
	 * 根据指定查询方案进行查询 （IQueryAction接口的方法的实现）。
	 *
	 * @param parent
	 * @param context
	 * @param reportModel
	 * @param queryScheme
	 * @return
	 */
	@Override
	public IQueryCondition doQueryByScheme(Container parent, IContext context, AbsAnaReportModel reportModel,
			IQueryScheme queryScheme) {
		if (queryScheme == null)
			return new BaseQueryCondition(false);
		String whereSql = queryScheme.getWhereSQLOnly();
		whereSql = resetWhereSql(whereSql);
		try {
			getQuerySchemeMap((QueryScheme) queryScheme);
		} catch (BusinessException e) {
			MessageDialog.showErrorDlg(parent,NCLangRes.getInstance().getStrByID("uif2", "ExceptionHandlerWithDLG-000000")/*错误*/, e.getMessage());
			return null;
		}
		deforeShowDialog(context,whereSql);
		IQueryCondition condition = setQryExpTrendQryVO(context,reportModel, whereSql);
		setReportHeadVas(context, reportModel);
		return condition;
	}
	
	private String resetWhereSql(String whereSql) {
	    int nPos = whereSql.indexOf("accyear");
	    StringBuilder sbWhere = new StringBuilder();
	    sbWhere.append(whereSql.substring(0, nPos + 11));
        UFDate currBusiDate = WorkbenchEnvironment.getInstance().getBusiDate();
        sbWhere.append(currBusiDate.getYear());
        sbWhere.append(whereSql.substring(nPos + 15, whereSql.length()));
	    return sbWhere.toString();
	}

	/**
	 *
	 * 方法说明：
	 * <p>修改记录：</p>
	 * @param editor
	 * @param code
	 * @param type
	 * @return
	 * @throws BusinessException
	 * @see
	 * @since V6.0
	 */
	private Map<String,String[]> getQuerySchemeMap(QueryScheme queryScheme) throws BusinessException {
		//清除旧值
		repeatMapKey();
		IFilter[] filter = (IFilter[]) queryScheme.get(IQueryScheme.KEY_FILTERS);
		for (int i = 0; i < filter.length; i++) {
			DefaultFilter ftv= (DefaultFilter) filter[i];
			nc.ui.querytemplate.meta.FilterMeta  fmeta = (nc.ui.querytemplate.meta.FilterMeta ) ftv.getFilterMeta();
			if (!varMap.containsKey(fmeta.getFieldCode())) {
				continue;
			}
			if(IQueryConstants.UFREF==fmeta.getDataType()){
				if (varMap.get(fmeta.getFieldCode())!=null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0",null,"02011000-0016",null,new String[]{ftv.getFilterMeta().getFieldName()})/*@res ":查询条件太复杂,有两个相同字段的查询条件,报表表头无法展示！"*/);
				}
				List<IFieldValueElement> valList = (ftv.getFieldValue()).getFieldValues();
				if (!valList.isEmpty()) {
					if(valList.size()>1){
						StringBuffer sbuf = new StringBuffer();
						StringBuffer pkbuf = new StringBuffer();
						for (int j = 0; j < valList.size(); j++) {
							DefaultFieldValueElement defment = ((DefaultFieldValueElement)valList.get(j));
							sbuf.append(defment.getShowString());
							sbuf.append(",");
							pkbuf.append(((RefValueObject)defment.getValueObject()).getPk());
							pkbuf.append(",");
						}
						varMap.put(fmeta.getFieldCode(), new String[]{sbuf.substring(0, sbuf.length()-1),pkbuf.substring(0, pkbuf.length()-1)});
					}else {
						DefaultFieldValueElement defment = ((DefaultFieldValueElement)valList.get(0));
						RefValueObject refvaO = ((RefValueObject)defment.getValueObject());
						StringBuffer pkbuf = new StringBuffer();
						if (((RefValueObject)defment.getValueObject()).isSubIncluded()) {
							String[] pkvalue = refvaO.getRefIncludeSubInfo().getSelectedTopLevelNodesPKs();
							for (int j = 0; j < pkvalue.length; j++) {
								pkbuf.append(pkvalue[j]);
								pkbuf.append(",");
							}
							varMap.put(fmeta.getFieldCode(), new String[]{defment.getShowString(),pkbuf.substring(0, pkbuf.length()-1)});
						}else {
							varMap.put(fmeta.getFieldCode(), new String[]{defment.getShowString(),((RefValueObject)defment.getValueObject()).getPk()});
						}
					}
				}
			}else if(IQueryConstants.STRING==fmeta.getDataType()){
				if (varMap.get(fmeta.getFieldCode())!=null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0",null,"02011000-0016",null,new String[]{ftv.getFilterMeta().getFieldName()})/*@res ":查询条件太复杂,有两个相同字段的查询条件,报表表头无法展示！"*/);
				}
				List<IFieldValueElement> valList = (ftv.getFieldValue()).getFieldValues();
				for (int j = 0; j < valList.size(); j++) {
					DefaultFieldValueElement defment = ((DefaultFieldValueElement)valList.get(j));
					varMap.put(fmeta.getFieldCode(), new String[]{defment.getShowString(),defment.getShowString()});
				}
			}
		}
		return null;
	}

}