package nc.ui.arap.bx;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.logging.Logger;
import nc.bs.uif2.BusinessExceptionAdapter;
import nc.itf.bd.userdefitem.IUserdefitemQryService;
import nc.md.util.BizMDModelUtil;
import nc.ui.er.util.BXUiUtil;
import nc.ui.uif2.userdefitem.QueryParam;
import nc.vo.bd.userdefrule.UserdefitemVO;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.util.remotecallcombination.IRemoteCallCombinatorUser;
import nc.vo.util.remotecallcombination.RemoteCallCombinatorEx;
import nc.vo.util.remotecallcombination.Token;

import org.apache.commons.lang.StringUtils;

/**
 * 合并查询多个用户自定义属性（自定义项）规则的工具类。
 * 
 * 通过QueryParam注入查询的参数，即规则编码（rulecode）或元数据实体全名（spacename.entityname），
 * 以及LoginContext中的组织（全局或者集团） 查询用户自定属性规则。
 * 
 * 
 * @author liujian
 * 
 */
public class BXUserDefItemContainer implements IRemoteCallCombinatorUser {

	private boolean initialized = false;

	private Map<String, UserdefitemVO[]> key_vos_map = new HashMap<String, UserdefitemVO[]>();

	private List<QueryParam> params;

	private boolean prepared = false;
	private Token token = null;

	private void doInvoke(RemoteCallCombinatorEx rcc) {
		IUserdefitemQryService service = rcc
				.getService(IUserdefitemQryService.class);

		List<String> userdefrulecodes = new ArrayList<String>();
		List<String> mdfullnames = new ArrayList<String>();
		if (getParams() != null) {
			for (QueryParam param : getParams()) {
				if (StringUtils.isNotEmpty(param.getRulecode())) {
					userdefrulecodes.add(param.getRulecode());
				} else if (StringUtils.isNotEmpty(param.getMdfullname())) {
					mdfullnames.add(param.getMdfullname());
				}
			}
			try {
				service.queryUserdefitemVOsByRuleCodesOrMDFullNames(
						userdefrulecodes, mdfullnames, getPk_org());
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	public List<QueryParam> getParams() {
		return params;
	}

	private String getPk_org() {
		return BXUiUtil.getPK_group();
	}

	public UserdefitemVO[] getUserdefitemVOsByMDClassFullName(String mdFullName) {
		if (!initialized)
			init();
		return key_vos_map.get(mdFullName);
	}

	public UserdefitemVO[] getUserdefitemVOsByUserdefruleCode(
			String userdefruleCode) {
		if (!initialized)
			init();
		return key_vos_map.get(userdefruleCode);
	}

	@SuppressWarnings("unchecked")
	private void init() {
		if (!this.prepared)
			this.prepare();
		if (getParams() != null) {
			try {
				Map<String, List<UserdefitemVO>> rulecodeOrMdfullname_userdefitems_map = (Map<String, List<UserdefitemVO>>) RemoteCallCombinatorEx
						.getInstance().getResult(token);
				List<UserdefitemVO> allItems = new ArrayList<UserdefitemVO>();
				for (String key : rulecodeOrMdfullname_userdefitems_map
						.keySet()) {
					List<UserdefitemVO> voList = rulecodeOrMdfullname_userdefitems_map
							.get(key);
					if (voList != null && voList.size() > 0) {
						key_vos_map.put(key, voList
								.toArray(new UserdefitemVO[0]));
						allItems.addAll(voList);
					}

				}
				if (allItems.size() > 0) {
					BizMDModelUtil.preloadMetaInfosByUserdefitems(allItems
							.toArray(new UserdefitemVO[0]));
				}
			} catch (BusinessException e) {
				throw new BusinessExceptionAdapter(e);
			}
		}
		initialized = true;
	}

	@Override
	public void prepare() {
		RemoteCallCombinatorEx rcc = RemoteCallCombinatorEx.getInstance();
		if (token != null) {
			rcc.update(token);
			doInvoke(rcc);
		} else {
			doInvoke(rcc);
			token = rcc.getToken();
		}
		prepared = true;
	}

	public void setParams(List<QueryParam> params) {
		this.params = params;
	}
}

