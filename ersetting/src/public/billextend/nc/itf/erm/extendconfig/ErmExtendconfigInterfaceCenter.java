package nc.itf.erm.extendconfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.framework.core.util.ObjectCreator;
import nc.bs.logging.Logger;
import nc.pubitf.erm.extendtab.IErmExtendtabQueryService;
import nc.vo.erm.extendconfig.ErmExtendConfigVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.utils.VOUtil;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.trade.pub.IExAggVO;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;


/**
 * er_extendconfig内注册的相关实现类管理
 * 
 * @author lvhj
 *
 */
public class ErmExtendconfigInterfaceCenter {
	
	/**
	 * 多子表聚合vo，补充查询扩展子表信息
	 * 
	 * @param vos
	 * @throws BusinessException
	 */
	public static void fillExtendTabVOs(String pk_group,final String tradetype_field,IExAggVO... vos)
			throws BusinessException {
		if (vos == null || vos.length == 0) {
			return;
		}
		CircularlyAccessibleValueObject parentVO = vos[0].getParentVO();
		if(vos.length == 1){
			String pk_tradetype = (String) parentVO.getAttributeValue(tradetype_field);
			ErmExtendConfigVO[] configs = ErmExtendconfigCache.getInstance().getErmExtendConfigVOs(pk_group, pk_tradetype);
			if(configs != null && configs.length >0){
				String pk = parentVO.getPrimaryKey();
				for (int i = 0; i < configs.length; i++) {
					IErmExtendtabQueryService queryService = ErmExtendconfigInterfaceCenter.getExtendtabQueryService(configs[i]);
					CircularlyAccessibleValueObject[] tabvos = queryService.queryByMaPK(pk);
					vos[0].setTableVO(configs[i].getBusi_tabcode(), tabvos);
				}
			}
		}else{
			@SuppressWarnings("rawtypes")
			HashMap vomap = Hashlize.hashlizeObjects(vos, new IHashKey() {
				
				@Override
				public String getKey(Object o) {
					return (String) ((IExAggVO)o).getParentVO().getAttributeValue(tradetype_field);
				}
			});
			String[] tradtypes = VOUtils.getAttributeValues(vos,
					tradetype_field);
			Map<String, List<ErmExtendConfigVO>> configvos = ErmExtendconfigCache
			.getInstance().getMaExtendxaconfigVOs(
					pk_group, tradtypes);
			for (Entry<String, List<ErmExtendConfigVO>> configentry : configvos.entrySet()) {
				List<ErmExtendConfigVO> configs = configentry.getValue();
				if(!configs.isEmpty()){
					String pk_tradetype = configentry.getKey();
					IExAggVO[] tradetype_vos = vos;
					if(!StringUtil.isEmpty(pk_tradetype)){
						// 扩展页签支持指定申请单交易类型的处理
						@SuppressWarnings("unchecked")
						ArrayList<IExAggVO> volist = (ArrayList<IExAggVO>) vomap.get(pk_tradetype);
						tradetype_vos =  volist.toArray(volist.toArray(new IExAggVO[volist.size()]));
					}
					String[] pks = VOUtil.getAttributeValues(tradetype_vos, null);
					for (int i = 0; i < configs.size(); i++) {
						ErmExtendConfigVO config = configs.get(i);
						if(StringUtil.isEmpty(config.getQueryclass())){
							continue;
						}
						IErmExtendtabQueryService queryService = ErmExtendconfigInterfaceCenter.getExtendtabQueryService(config);
						Map<String,CircularlyAccessibleValueObject[]> tabvos = queryService.queryByMaPKs(pks);
						for (IExAggVO aggvo : tradetype_vos) {
							aggvo.setTableVO(config.getBusi_tabcode(), tabvos.get(aggvo.getParentVO().getPrimaryKey()));
						}
					}
				}
			}
			
		}
		
		
	}


	public static IErmExtendtabQueryService getExtendtabQueryService(ErmExtendConfigVO config) throws BusinessException {
		IErmExtendtabQueryService rs = null;
		String modulecode = config.getBusi_sys();
		String classname = config.getQueryclass();
		if (classname != null) {
			Object o = null;
			try {
				o = ObjectCreator.newInstance(modulecode, classname);
			} catch (Exception e) {
				Logger.error("", e);
				try {
					o = ObjectCreator.newInstance(classname);
				} catch (Exception e2) {
					Logger.error("", e2);
				}
			}
			if (o == null) {
				try {
					o = Class.forName(classname).newInstance();
				} catch (Exception e) {
					Logger.error("", e);
				}
			}
			if (o != null) {
				if (o instanceof IErmExtendtabQueryService)
					rs = (IErmExtendtabQueryService) o;
				else
					throw new BusinessException(getNoInterfaceImpMessage(config.getBusi_tabcode(),"queryclass", "IErmExtendtabQueryService"));
			} else {
				throw new BusinessException(getNoInstanceCreateMessage(config.getBusi_tabcode(), "queryclass"));
			}
		}else{
			throw new BusinessException(getNoInterfaceImpMessage(config.getBusi_tabcode(),"queryclass", "IErmExtendtabQueryService"));
		}
		return rs;
	}

	public static String getNoInterfaceImpMessage(String tabcode, String classname,String iftname) {
		return  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0044",
				null,new String[]{tabcode,classname,iftname})/* @res "扩展页签{0}在er_extendconfig表里注册的{1}接口实现类没有实现{2}接口，请检查注册信息！" */;
	}

	public static String getNoInstanceCreateMessage(String tabcode, String classname) {
		return  nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201100_0","0201100-0045",
				null,new String[]{tabcode,classname})/* @res "扩展页签{0}在er_extendconfig表里注册的{1}接口实现类无法正常实例化，请检查日志信息！" */;
	}
}