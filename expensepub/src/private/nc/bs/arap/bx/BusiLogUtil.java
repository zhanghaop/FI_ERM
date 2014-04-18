package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.busilog.util.BusinessLogServiceUtil;
import nc.bs.busilog.vo.BusinessLogContext;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.sm.busilog.util.LogConfigServiceFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IAttribute;
import nc.md.model.IBean;
import nc.md.model.impl.Attribute;
import nc.md.model.type.IType;
import nc.md.model.type.impl.EnumType;
import nc.ms.tb.formula.iufoplugin.IllegalParamsException;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.ui.pub.beans.constenum.IConstEnum;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bd.meta.BDObjectAdpaterFactory;
import nc.vo.bd.meta.IBDObject;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFDouble;
import nc.vo.sm.funcreg.ModuleVO;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

/**
 * <b>Date:</b>2012-7-13<br>
 * git
 * @author：wangyhh@ufida.com.cn
 */
public class BusiLogUtil {
	private static final String NBSP_NBSP = "     ";
	/**
	 * 模块缓存
	 */
	private static Map<String, ModuleVO> modulID2VOMap = new HashMap<String, ModuleVO>();
	/**
	 * 辅助核算缓存
	 */
	private static Map<String, String> assidMap = new HashMap<String, String>();

	/**
	 * 创建并保存日志
	 * 
	 * @param operationType
	 * @param newBills
	 * @param oldBills
	 * @param type
	 * @author: wangyhh@ufida.com.cn
	 */
	public static void insertSmartBusiLog(String operationType, Object[] newBills, Object[] oldBills) {
		if (ArrayUtils.isEmpty(newBills) || newBills[0] == null) {
			Logger.error("业务日志对象为空" /*-=notranslate=-*/);
			return;
		}

		String metadataid = NCObject.newInstance(newBills[0]).getRelatedBean().getID();
		String pk_group = (String) NCObject.newInstance(newBills[0]).getAttributeValue("pk_group");
		try {
			boolean needLog = LogConfigServiceFacade.getInstance().isOperNeedLog(pk_group, metadataid, operationType);
			if (!needLog) {
				Logger.error("该操作不需要记录业务日志" /*-=notranslate=-*/);
				return;
			}

			List<BusinessLogContext> listvo = new ArrayList<BusinessLogContext>();
			for (int i = 0, j = newBills.length; i < j; i++) {
				Object newvo = newBills[i];
				if (newvo == null) {
					continue;
				}
				
				Object oldvo = oldBills == null ? null : oldBills[i];
				String logmsg = constructMsg(oldvo, newvo, pk_group);
				
				listvo.add(createLogVO(newvo, logmsg, operationType));
			}
			BusinessLogServiceUtil.insertBatchBusiLogAsynch(listvo);
		} catch (BusinessException e) {
			Logger.error("记录业务日志失败！失败原因：" /*-=notranslate=-*/ + e.getMessage());
		}
	}

	private static String constructMsg(Object oldVo, Object newVo, String pk_group) throws BusinessException {
		if (newVo == null) {
			throw new IllegalParamsException("newvo不能为空" /*-=notranslate=-*/, "自定义" /*-=notranslate=-*/);
		}

		IBean bean = NCObject.newInstance(newVo).getRelatedBean();
		List<String> list = LogConfigServiceFacade.getInstance().getAttrbuteNamePath(pk_group, bean.getID(), false);
		if (list == null || list.size() == 0) {
			return null;
		}

		NCObject newNcobject = NCObject.newInstance(newVo);
		NCObject oldNcobject = oldVo == null ? null : NCObject.newInstance(oldVo);

		StringBuffer sb = new StringBuffer();
		List<IAttribute> subEntryAttrList = constructHeadMsg(bean, list, newNcobject, oldNcobject,sb);

		// 表体vo业务日志记录
		for (IAttribute attr : subEntryAttrList) {
			NCObject[] newBodyVO = (NCObject[]) newNcobject.getAttributeValue(attr);
			if (newBodyVO == null) {
				return sb.toString();
			}
			
			StringBuffer bodysb = new StringBuffer();
			constructBodyMsg(list, oldNcobject, bodysb, attr, newBodyVO);
			if (!StringUtils.isEmpty(bodysb.toString())) {
				//因为凭证前台传到后台所有分录没有unchanged状态vo，所以需要日志自己区分是否需要记录分录
				//所以分录名放到外层记录
				sb.append(attr.getDisplayName() + ":\n");
				sb.append(bodysb.toString());
			}
		}
		
		return sb.toString();
	}
	
	/**
	 * 构建表头信息
	 * 
	 * @param bean
	 * @param list
	 * @param newNcobject
	 * @param sb
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private static List<IAttribute> constructHeadMsg(IBean bean, List<String> list, NCObject newNcobject,NCObject oldNcobject, StringBuffer sb) throws BusinessException {
		sb.append(newNcobject.getRelatedBean().getDisplayName() + ":\n");
		List<IAttribute> subEntryAttrList = getSubEntryAttribute(bean);
		
		int voStatus = newNcobject.getVOStatus();
		if(oldNcobject != null){
			voStatus = VOStatus.UPDATED;
		}
		for (String str : list) {
			boolean isSub = false;
			for (IAttribute iAttribute : subEntryAttrList) {
				if (str.startsWith(iAttribute.getName())) {
					isSub = true;
					break;
				}
			}
			if (isSub) {
				continue;
			}
			
			IAttribute attr = bean.getAttributeByPath(str);
			
			Object newValue = getValue(newNcobject, attr);
			Object oldValue = getValue(oldNcobject, attr);
			
			if (oldValue == null && newValue == null) {
				// 为空不记录日志
				continue;
			}
		
			appendStringValue(sb, attr.getDisplayName(), newValue, oldValue,voStatus);
		}

		sb.append("\n\n");
		return subEntryAttrList;
	}

	/**
	 * 构建表头信息
	 * 
	 * @param list
	 * @param oldNcobject
	 * @param sb
	 * @param subEntryAttribute
	 * @param newBodyVO
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private static void constructBodyMsg(List<String> list, NCObject oldNcobject, StringBuffer sb, IAttribute subEntryAttribute, NCObject[] newBodyVO) throws BusinessException {
		NCObject[] oldBodyVO = oldNcobject == null ? null : (NCObject[]) oldNcobject.getAttributeValue(subEntryAttribute);
		Map<String, NCObject> oldBodyVOMap = getOldBodyVO(oldBodyVO);
		
		for (int i = 0; i < newBodyVO.length; i++) {
			StringBuffer linesb = new StringBuffer();
			boolean isModify = false;
			int status = newBodyVO[i].getVOStatus();
			if(status == VOStatus.UNCHANGED && oldNcobject != null){
				status = VOStatus.UPDATED;
			}
			String vostatus = "";
			switch (status) {
			case VOStatus.UNCHANGED:
				vostatus = "未改变" /*-=notranslate=-*/;
				break;
			case VOStatus.UPDATED:
				vostatus = "修改" /*-=notranslate=-*/;
				break;
			case VOStatus.NEW:
				vostatus = "新增" /*-=notranslate=-*/;
				break;
			case VOStatus.DELETED:
				vostatus = "删除" /*-=notranslate=-*/;
				break;
			}
			linesb.append(NBSP_NBSP + "操作:[" /*-=notranslate=-*/ + vostatus + "];");

			IBean subBean = subEntryAttribute.getOwnerBean();
			for (String str : list) {
				if (!str.startsWith(subEntryAttribute.getName())) {
					continue;
				}

				IAttribute attr = subBean.getAttributeByPath(str.split("\\.")[1]);
				if (attr == null) {
					attr = subBean.getAttributeByPath(str);
				}

				BDObjectAdpaterFactory factory = new BDObjectAdpaterFactory();
				NCObject oldBodyVo = oldBodyVOMap.get((String) factory.createBDObject(newBodyVO[i].getContainmentObject()).getId());

				Object newValue = getValue(newBodyVO[i], attr);
				Object oldValue = getValue(oldBodyVo, attr);

				if (oldValue == null && newValue == null) {
					// 为空不记录日志
					continue;
				}

				if (appendStringValue(linesb, attr.getDisplayName(), newValue, oldValue,status)) {
					isModify = true;
				}
			}

			linesb.append("\n");
			
			if (status == VOStatus.UPDATED && isModify) {
				sb.append(linesb.toString());
			}else if (status != VOStatus.UPDATED){
				sb.append(linesb.toString());
			}
		}
	}

	private static BusinessLogContext createLogVO(Object newvo, String logmsg, String pk_operation) throws BusinessException {
		BDObjectAdpaterFactory factory = new BDObjectAdpaterFactory();
		NCObject mainvo = NCObject.newInstance(newvo);
		IBDObject bdobject = factory.createBDObject(mainvo.getContainmentObject());

		BusinessLogContext smartlog = new BusinessLogContext();
		smartlog.setLogmsg(logmsg);
		smartlog.setOrgpk_busiobj((String) bdobject.getPk_org());
		smartlog.setPk_busiobj((String) bdobject.getId());
		smartlog.setBusiobjcode(getBillNo(newvo));
		smartlog.setBusiobjname(mainvo.getRelatedBean().getDisplayName());
		smartlog.setPk_operation(pk_operation);
		smartlog.setTypepk_busiobj(mainvo.getRelatedBean().getID());

		return smartlog;
	}

	private static String getBillNo(Object newvo) {
		String billno = "";

		if (newvo instanceof AggregatedValueObject) {
			billno = (String) ((AggregatedValueObject) newvo).getParentVO().getAttributeValue("djbh");
			if (billno == null) {
				billno = (String) ((AggregatedValueObject) newvo).getParentVO().getAttributeValue("billno");
			}
		} else if (newvo instanceof SuperVO) {
			billno = (String) ((SuperVO) newvo).getAttributeValue("djbh");
			if (billno == null) {
				billno = (String) ((SuperVO) newvo).getAttributeValue("billno");
			}
		}
		return billno;
	}

	/**
	 * 获取子实体属性
	 * 
	 * @param bills
	 * @param bean
	 * @return
	 * @author: wangyhh@ufida.com.cn
	 */
	private static List<IAttribute> getSubEntryAttribute(IBean bean) {
		List<IAttribute>  attrList = new ArrayList<IAttribute>();
		for (IAttribute attribute : bean.getAttributes()) {
			if (attribute.getDataType().getTypeType() == IType.STYLE_LIST || attribute.getDataType().getTypeType() == IType.COLLECTION) {
				attrList.add(attribute);
			}
		}
		return attrList;
	}

	private static Map<String, NCObject> getOldBodyVO(NCObject[] oldBodyVO) {
		Map<String, NCObject> map = new HashMap<String, NCObject>();
		if (oldBodyVO == null) {
			return map;
		}

		BDObjectAdpaterFactory factory = new BDObjectAdpaterFactory();
		for (int i = 0, j = oldBodyVO.length; i < j; i++) {
			IBDObject bdobject = factory.createBDObject(oldBodyVO[i].getContainmentObject());
			map.put((String) bdobject.getId(), oldBodyVO[i]);
		}

		return map;
	}

	/**
	 * 解析value
	 * 
	 * @param vo
	 * @param attr
	 * @return
	 * @throws BusinessException
	 * @author: wangyhh@ufida.com.cn
	 */
	private static Object getValue(NCObject vo, IAttribute attr) throws BusinessException {
		if (vo == null || attr == null)
			return null;

		Object attributeValue = vo.getAttributeValue(attr.getName());

		if (attributeValue == null) {
			return null;
		}

		String docPk = attributeValue.toString();
		Object value = null;
		if (attr.getDataType().getTypeType() == IType.REF) {

			String dataTypeId = ((Attribute) attr).getDataTypeID();
			if ("ec4995b1-1881-44ef-8550-2cb0139f5d8a".equals(dataTypeId)) {
				// 来源系统无法通过IGeneralAccessor取得，没有配置管控模式
				ModuleVO module = getModulVOByID(docPk);
				if (module != null) {
					value = module.getSystypename();
				}
			} else {
				IGeneralAccessor accessor = GeneralAccessorFactory.getAccessor(dataTypeId);
				IBDData docByPk = accessor.getDocByPk(docPk);
				if (docByPk != null) {
					value = docByPk.getName().toString();
				}
			}

			if (value == null) {
				value = attributeValue;
			}

		} else if (attr.getDataType().getTypeType() == IType.ENUM) {
			// 枚举类型
			EnumType enumType = (EnumType) attr.getDataType();
			IConstEnum constEnum = enumType.getConstEnum(attributeValue);
			if (constEnum == null) {
				value = (vo.getAttributeValue(attr.getName()) == null) ? "" : vo.getAttributeValue(attr.getName()).toString();
			} else {
				value = constEnum.getName();
			}
		} else {
			if ("assid".equals(attr.getName()) && (!StringUtil.isEmpty(docPk))) {
				// 如果是辅助核算，进行特殊解析；
				value = assidMap.get(docPk);
			} else {
				value = attributeValue;
			}
		}
		return value;
	}

	/**
	 * 追加日志信息
	 * 
	 * @param sb
	 * @param displayName
	 * @param newValue
	 * @param oldValue
	 * @param voStatus
	 * @return  是否是修改
	 * @author: wangyhh@ufida.com.cn
	 */
	private static boolean appendStringValue(StringBuffer sb, String displayName, Object newValue, Object oldValue,int voStatus ) {
		int power = 0;
		if(oldValue instanceof UFDouble) {
			power = ((UFDouble) oldValue).getPower();
		}
		if (newValue instanceof UFDouble && power == 0){
			power = ((UFDouble) newValue).getPower();
		}
		if(oldValue instanceof UFDouble) {
			oldValue = ((UFDouble) oldValue).setScale(power, UFDouble.ROUND_HALF_UP).toString();
		}
		if (newValue instanceof UFDouble){
			newValue = ((UFDouble) newValue).setScale(power, UFDouble.ROUND_HALF_UP).toString();
		}

		if ((voStatus == VOStatus.UPDATED) && (newValue != null && !newValue.equals(oldValue)) || (oldValue != null && !oldValue.equals(newValue))) {
			sb.append(NBSP_NBSP + displayName + ":由[" /*-=notranslate=-*/ + oldValue + "]修改为[" /*-=notranslate=-*/ + newValue + "];");
			return true;
		} else if (voStatus != VOStatus.UNCHANGED) {
			sb.append(NBSP_NBSP + displayName + ":[" + newValue + "];");
			return false;
		}
//		if ((newValue != null && !newValue.equals(oldValue)) || (oldValue != null && !oldValue.equals(newValue))) {
//			sb.append(NBSP_NBSP + displayName + ":由[" + oldValue + "]修改为[" + newValue + "];");
//		} else {
//			sb.append(NBSP_NBSP + displayName + ":[" + newValue + "];");
//		}
		return false;
	}

	private static ModuleVO getModulVOByID(String moduleID) {
		if (!modulID2VOMap.containsKey(moduleID)) {
			initModulID2VOMap();
		}
		return modulID2VOMap.get(moduleID);
	}

	private static void initModulID2VOMap() {
		modulID2VOMap.clear();
		ModuleVO[] modules = PfDataCache.getAllModules();
		for (ModuleVO module : modules) {
			modulID2VOMap.put(module.getModuleid(), module);
		}
	}
}
