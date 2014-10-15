package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.busilog.util.BusinessLogServiceUtil;
import nc.bs.busilog.vo.BusinessLogContext;
import nc.bs.logging.Logger;
import nc.md.MDBaseQueryFacade;
import nc.md.model.IBean;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;

import org.apache.commons.lang.ArrayUtils;

/**
 * v6.1 业务日志工具类
 * 
 */
@Deprecated
public class ErmBusiLogUtils {

	/**
	 * 业务日志处理 新增情况下vos不为空，oldvos为空；删除情况下vos为空，oldvos不为空。
	 * 
	 * @param vos
	 * @param oldvos
	 * @param pk_operation
	 * 
	 * @author lvhj
	 */
	public static void insertSmartBusiLogs(Object[] vos, Object[] oldvos, String pk_operation) {
		if (ArrayUtils.isEmpty(vos) && ArrayUtils.isEmpty(oldvos)) {
			return;
		}

		try {
			List<BusinessLogContext> listvo = new ArrayList<BusinessLogContext>();
			SuperVO obj = vos == null ? getParentVo(oldvos[0]) : getParentVo(vos[0]);
			IBean bean = MDBaseQueryFacade.getInstance().getBeanByFullClassName(obj.getClass().getName());
			String beanname = bean.getName();
			String displayName = bean.getDisplayName();
			String beanid = bean.getID();

			if (!ArrayUtils.isEmpty(vos)) {// 新增，修改

				Map<String, Object> oldvoMap = hashlizeAggVOs(oldvos);
				for (int i = 0; i < vos.length; i++) {

					BusinessLogContext ctx = new BusinessLogContext();
					ctx.setBusiobjcode(beanname);
					ctx.setBusiobjname(displayName);

					ctx.setBusiobjvo(vos[i]);

					SuperVO parentVo = getParentVo(vos[i]);

					String key = parentVo.getPrimaryKey();
					ctx.setOldbusiobjvo(oldvoMap.get(key));

					ctx.setOrgpk_busiobj((String) parentVo.getAttributeValue("pk_org"));
					ctx.setTypepk_busiobj(beanid);
					ctx.setPk_busiobj(beanid);
					ctx.setPk_operation(pk_operation);

					listvo.add(ctx);
				}
			} else if (!ArrayUtils.isEmpty(oldvos)) {// 删除
				for (int i = 0; i < oldvos.length; i++) {
					BusinessLogContext ctx = new BusinessLogContext();
					ctx.setBusiobjcode(beanname);
					ctx.setBusiobjname(displayName);

					ctx.setBusiobjvo(null);

					ctx.setOldbusiobjvo(oldvos[i]);

					ctx.setOrgpk_busiobj((String) getParentVo(oldvos[i]).getAttributeValue("pk_org"));
					ctx.setTypepk_busiobj(beanid);
					ctx.setPk_busiobj(beanid);
					ctx.setPk_operation(pk_operation);

					listvo.add(ctx);
				}

			}
			BusinessLogServiceUtil.insertBatchBusiLogAsynch(listvo);
		} catch (Exception e) {
			Logger.error("~~~~~~~~~~~~~~~~~生成费用业务日志出错!!!~~~~~~~~~~~~~~~~~");
			ExceptionHandler.handleExceptionRuntime(e);
		}
	}

	/**
	 * 兼容聚合vo和superVO
	 * 
	 * @param vos
	 * @param i
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private static SuperVO getParentVo(Object vo) {
		if (vo instanceof AggregatedValueObject) {
			return (SuperVO) ((AggregatedValueObject) vo).getParentVO();
		}

		return (SuperVO) vo;
	}

	/**
	 * hash化聚合vo，key为主表pk
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	private static Map<String, Object> hashlizeAggVOs(Object[] vos) throws BusinessException {
		Map<String, Object> result = new HashMap<String, Object>();
		if (!ArrayUtils.isEmpty(vos)) {
			for (int i = 0; i < vos.length; i++) {
				String key = getParentVo(vos[i]).getPrimaryKey();
				result.put(key, vos[i]);
			}
		}

		return result;
	}
}