package nc.pubimpl.fipub.subscribe;

import nc.itf.iufo.freereport.extend.IQueryCondition;
import nc.itf.iufo.freereport.extend.ISubscribeConditionConvertor;
import nc.itf.iufo.freereport.extend.ISubscribeQueryCondition;
import nc.pub.smart.model.preferences.Parameter;
import nc.ui.querytemplate.querytree.IQueryScheme;

import com.ufida.dataset.IContext;
import com.ufida.report.anareport.base.BaseQueryCondition;
import com.ufida.report.anareport.base.BaseSubscribeCondition;
import com.ufida.report.anareport.model.AbsAnaReportModel;

/**
 * @author ll
 * @created at 2011-6-17,下午03:13:56
 * 
 */
public class ExpSubConvertor implements ISubscribeConditionConvertor {

	public ExpSubConvertor() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * nc.itf.iufo.freereport.extend.ISubscribeConditionConvertor#getQueryCondition
	 * (nc.itf.iufo.freereport.extend.ISubscribeQueryCondition,
	 * com.ufida.dataset.IContext,
	 * com.ufida.report.anareport.model.AbsAnaReportModel)
	 */
	@Override
	public IQueryCondition getQueryCondition(ISubscribeQueryCondition subscribCondition, IContext context,
			AbsAnaReportModel reportModel) {
		// 默认实现只认识BaseSubscribeCondition
		if (subscribCondition == null || !(subscribCondition instanceof BaseSubscribeCondition))
			return new BaseQueryCondition(true);

		BaseSubscribeCondition subCondition = (BaseSubscribeCondition) subscribCondition;
		IQueryScheme scheme = subCondition.getScheme();
		Parameter[] params = subCondition.getParams();
		if (scheme == null && params == null)
			return null;
		BaseQueryCondition basec = (BaseQueryCondition)subCondition.getQueryResult().get(this.getClass().getName());
		return basec;
			// 根据保存的查询方案获取SQL条件
//		if(params != null){
//			// 将参数值写入变量池
//			ReportVariableHelper.addParamsValueToPool(ReportVariableHelper.getPoolInstance(context), params);
//		}
	}

}
