package nc.arap.mobile.impl;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.CacheUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.bs.pf.pub.PfDataCache;
import nc.bs.pub.filesystem.IFileSystemService;
import nc.bs.pub.filesystem.IQueryFolderTreeNodeService;
import nc.bs.wfengine.engine.ActivityInstance;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.fi.org.IOrgVersionQueryService;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IWorkflowDefine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.itf.uap.rbac.IUserManageQuery;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.inoutbusiclass.InoutBusiClassVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.fip.pub.SqlTools;
import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pf.change.PfUtilBaseTools;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.ValidationException;
import nc.vo.pub.filesystem.NCFileNode;
import nc.vo.pub.filesystem.NCFileVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.BillStatusEnum;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pub.workflowqry.FlowAdminVO;
import nc.vo.sm.UserVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uap.wfmonitor.ProcessRouteRes;
import nc.vo.util.AuditInfoUtil;
import nc.vo.vorg.OrgVersionVO;
import nc.vo.wfengine.core.XpdlPackage;
import nc.vo.wfengine.core.parser.UfXPDLParser;
import nc.vo.wfengine.core.parser.XPDLNames;
import nc.vo.wfengine.core.parser.XPDLParserException;
import nc.vo.wfengine.core.transition.BasicTransitionEx;
import nc.vo.wfengine.core.workflow.WorkflowProcess;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;

import org.apache.commons.lang.ArrayUtils;

import sun.misc.BASE64Encoder;
import uap.pub.bap.fs.FileStorageClient;
import uap.pub.bap.fs.IFileTransfer;
import uap.pub.bap.fs.domain.FileHeader;
import uap.pub.bap.fs.domain.ext.BaFileStoreExt;


public class ErmMobileCtrlBO extends AbstractErmMobileCtrlBO{
	
	/** 
	 * 默认单据类型
	 */
	private String defaultDjlxbm="2643";
	
	public ErmMobileCtrlBO(String defaultDjlxbm) {
		super();
		this.defaultDjlxbm = defaultDjlxbm;
	}
	
	public String addJkbx(Map<String, Object> valuemap) throws BusinessException {
		String userid = (String) valuemap.get("userid");
		initEvn(userid);
	
		String pk_jkbx = "";
		try{
			if(valuemap.get("pk_jkbx") != null && !"".equals(valuemap.get("pk_jkbx"))){
				// 主键已生成，说明是修改后提交，需要先收回单据
				commitCancle((String) valuemap.get("pk_jkbx"));
				pk_jkbx =  updateJkbx(valuemap);
			}else{
				//第一次提交
				pk_jkbx = insertJkbx(valuemap);
			}
			commitJkbx(userid,pk_jkbx);
			return "pk_jkbx"+pk_jkbx;
		}catch(BusinessException e){
			String msg = e.getMessage();
			return "retmsg"+msg;
		}
	}

	private String updateJkbx(Map<String, Object> valuemap) throws BusinessException {
		String headpk = (String) valuemap.get("pk_jkbx");
		// 查询当前借款报销单
		List<JKBXVO> vos = NCLocator.getInstance().
		  lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{headpk}, null);
		if(vos == null || vos.isEmpty()){
			throw new BusinessException("单据已被删除，请检查");
		}
		JKBXVO jkbxvo = vos.get(0);
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		// 审批状态控制
		Integer spzt = parentVO.getSpzt();

		if (spzt != null && (spzt.equals(IPfRetCheckInfo.GOINGON) || spzt.equals(IPfRetCheckInfo.COMMIT))) {
			String userId = InvocationInfoProxy.getInstance().getUserId();
			String billId = headpk;
			String billType = parentVO.getDjlxbm();
			try {
				if (((IPFWorkflowQry) NCLocator.getInstance().lookup(IPFWorkflowQry.class.getName()))
						.isApproveFlowStartup(billId, billType)) {// 启动了审批流后
					if(spzt.equals(IPfRetCheckInfo.COMMIT) && userId.equals(jkbxvo.getParentVO().getCreator())){
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
					}
					
					if (!NCLocator.getInstance().lookup(IPFWorkflowQry.class).isCheckman(billId,
									billType, userId)) {
						throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0092")/*@res "请取消审批再修改！"*/);
					}
				}else{
					throw new ValidationException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0093")/*@res "请收回单据再修改！"*/);
				}
			} catch (ValidationException ex) {
				ExceptionHandler.handleException(ex);
			} catch (BusinessException e) {
				ExceptionHandler.consume(e);
			}
		}
		parentVO.setStatus(VOStatus.UPDATED);
		parentVO.setBzbm("1002Z0100000000001K1");// 默认人民币
		parentVO.setBbhl(new UFDouble(1));
		parentVO.setPaytarget(0);
		for (Entry<String, Object> value_entry : valuemap.entrySet()) {
			String key = value_entry.getKey();
			if("items".equals(key)){
				continue;
			}
			String value = getStringValue(value_entry.getValue());
			if(JKBXHeaderVO.DJRQ.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setDjrq(new UFDate(value));
				}
			}else if(JKBXHeaderVO.TOTAL.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setTotal(new UFDouble(value));
				}
			}else{
				parentVO.setAttributeValue(key, value);
			}
		}
		// 业务行先删后插，重新合计表头
		List<BXBusItemVO> itemlist = new ArrayList<BXBusItemVO>();
		BXBusItemVO[] olditems = jkbxvo.getBxBusItemVOS();
		if(olditems != null && olditems.length > 0){
			for (int i = 0; i < olditems.length; i++) {
				olditems[i].setStatus(VOStatus.DELETED);
				itemlist.add(olditems[i]);
			}
		}
		UFDouble totalAmount = UFDouble.ZERO_DBL;
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> items = (List<Map<String, Object>>) valuemap.get("items");
		if(items != null && !items.isEmpty()){
			//把表头收支项目同步到表体
			String szxmid = parentVO.getSzxmid();
			for (Map<String, Object> itemvalue : items) {
				String amountvalue = (String) itemvalue.get("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				BXBusItemVO itemvo = new BXBusItemVO();
				itemvo.setStatus(VOStatus.NEW);
				//0第一个页签   1其他页签
				String itemflag = itemvalue.get("itemflag")==null?"0":itemvalue.get("itemflag").toString();
				//同步第一个页签
				if(!StringUtil.isEmpty(szxmid) && itemflag.equals("0"))
					itemvo.setSzxmid(szxmid);
				String tabcode = "arap_bxbusitem";
				for (Entry<String, Object> fieldvalues : itemvalue.entrySet()) {
					String key = fieldvalues.getKey();
					Object value = fieldvalues.getValue();
					if("amount".equals(key)){
						value = new UFDouble((String)value);
					}
					if("itemflag".equals(key)){
						if("1".equals(value)){
							tabcode = "other";
						}
					}
					//第一个页签收支项目已经赋值了
					if(itemflag.equals("1") || !"szxmid".equals(key))
						itemvo.setAttributeValue(key, value);
				} 
				
				itemvo.setTablecode(tabcode);
				itemvo.setYbje(itemvo.getAmount()); 
				itemvo.setBbje(itemvo.getAmount());
				itemvo.setPaytarget(0);
				itemvo.setReceiver(parentVO.getReceiver());
				itemvo.setPrimaryKey(null);
				itemlist.add(itemvo);
				totalAmount = totalAmount.add(itemvo.getAmount());
			}
		}
		jkbxvo.setBxBusItemVOS(itemlist.toArray(new BXBusItemVO[itemlist.size()]));
		parentVO.setYbje(totalAmount);
		parentVO.setTotal(totalAmount);
		parentVO.setBbje(totalAmount);
		// 附件先删后插
		deleteAttachmentList(headpk, parentVO.getOperator());
		saveAttachment(headpk, valuemap);
		
		// 更新单据
		NCLocator.getInstance().lookup(IBXBillPublic.class).update(new JKBXVO[]{jkbxvo});
		
		return headpk;
	}

	/**
	 * 新增保存
	 * 
	 * @param valuemap
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private String insertJkbx(Map<String, Object> valuemap)
			throws BusinessException {
//		String userid = valuemap.get("userid").toString();
		String userid = InvocationInfoProxy.getInstance().getUserId();
		if(!StringUtil.isEmpty(userid)){
			initEvn(userid);
		}  
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
		//获得当前操作的交易类型
		String djlxbm = (String) valuemap.get(JKBXHeaderVO.DJLXBM);
		String pk_tradetype = StringUtil.isEmpty(djlxbm)?defaultDjlxbm:djlxbm.toString();
		org.jfree.util.Log.getInstance().error("wangjjm pk_group :" + pk_group);
		org.jfree.util.Log.getInstance().error("wangjjm pk_tradetype :" + pk_tradetype);
		DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, pk_tradetype);
		org.jfree.util.Log.getInstance().error("wangjjm djlxVO :" + djlxVO);
		// 初始化表头数据
		IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
		JKBXVO jkbxvo = initservice.setBillVOtoUI(djlxVO, "", null);
		JKBXHeaderVO parentVO = jkbxvo.getParentVO();
		
		for (Entry<String, Object> value_entry : valuemap.entrySet()) {
			String key = value_entry.getKey();
			if("items".equals(key) || "attachment".equals(key)){
				continue;
			}
			String value = getStringValue(value_entry.getValue());
			if(JKBXHeaderVO.DJRQ.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setDjrq(new UFDate(value));
				}
			}else if(JKBXHeaderVO.TOTAL.equals(key)){
				if(!StringUtil.isEmpty(value)){
					parentVO.setTotal(new UFDouble(value));
				}
			}else{
				parentVO.setAttributeValue(key, value);
			}
		}
		parentVO.setPk_jkbx(null);
		parentVO.setStatus(VOStatus.NEW);
		parentVO.setBzbm("1002Z0100000000001K1");// 默认人民币
		parentVO.setBbhl(new UFDouble(1));
		parentVO.setGroupbbhl(UFDouble.ZERO_DBL);
		parentVO.setGlobalbbhl(UFDouble.ZERO_DBL);
		parentVO.setPaytarget(0);
		parentVO.setDjbh(null);
		// 补充表头组织字段版本信息
		IOrgVersionQueryService orgvservice = NCLocator.getInstance().lookup(IOrgVersionQueryService.class);
		Map<String, OrgVersionVO> orgvmap = orgvservice.getOrgVersionVOsByOrgsAndDate(new String[]{parentVO.getPk_org()}, parentVO.getDjrq());
		String orgVid = orgvmap.get(parentVO.getPk_org()).getPk_vid();
		parentVO.setDwbm_v(orgVid);
		parentVO.setFydwbm_v(orgVid);
		parentVO.setPk_org_v(orgVid);
		parentVO.setPk_payorg_v(orgVid);
		
		String deptVid = getDept_vid(parentVO.getDeptid(),  parentVO.getDjrq());
		parentVO.setDeptid_v(deptVid);
		parentVO.setFydeptid_v(deptVid);
		
		// 设置表体数据
		List<Map<String, Object>> items = (List<Map<String, Object>>) valuemap.get("items");
		//把表头收支项目同步到表体
		String szxmid = parentVO.getSzxmid();
		if(items != null && !items.isEmpty()){ 
			UFDouble totalAmount = UFDouble.ZERO_DBL;   
			List<BXBusItemVO> itemvos = new ArrayList<BXBusItemVO>();
			for (Map<String, Object> itemvalue : items) {
				String amountvalue = (String) itemvalue.get("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				BXBusItemVO itemvo = new BXBusItemVO();
				if(!StringUtil.isEmpty(szxmid))
					itemvo.setSzxmid(szxmid);
				itemvo.setStatus(VOStatus.NEW);
				String tabcode = "arap_bxbusitem";
				for (Entry<String, Object> fieldvalues : itemvalue.entrySet()) {
					String key = fieldvalues.getKey();
					Object value = fieldvalues.getValue();
					if("amount".equals(key)){
						value = new UFDouble((String)value);
					}
					itemvo.setAttributeValue(key, value);
					if("itemflag".equals(key)){
						if("1".equals(fieldvalues.getValue().toString())){
							tabcode = "other";
						}
					}
				} 
				itemvo.setYbje(itemvo.getAmount());
				itemvo.setBbje(itemvo.getAmount());
				itemvo.setPaytarget(0);
				itemvo.setReceiver(parentVO.getReceiver());
//				if(StringUtils.isEmpty(itemvo.getTablecode() )){
//					itemvo.setTablecode("arap_bxbusitem");
//				}
				itemvo.setTablecode(tabcode);
				itemvos.add(itemvo);
				totalAmount = totalAmount.add(itemvo.getAmount());
			}
			jkbxvo.setBxBusItemVOS(itemvos.toArray(new BXBusItemVO[itemvos.size()]));
			parentVO.setYbje(totalAmount);
			parentVO.setTotal(totalAmount);
			parentVO.setBbje(totalAmount);
		}
		// 保存报销单数据
		IBXBillPublic service = NCLocator.getInstance().lookup(IBXBillPublic.class);
		JKBXVO[] result = service.save(new JKBXVO[] { jkbxvo });
		
		String bxpk = result[0].getParentVO().getPrimaryKey();
		
		//保存附件
		saveAttachment(bxpk,valuemap);
		return bxpk; 
	}
	
	
	
//	/**
//	 * 上传附件
//	 * 
//	 * @param groupid
//	 * @param usrid
//	 * @param attachments
//	 * @param bxpk
//	 * @throws BusinessException
//	 */
//	private void uploadAttachment(String groupid, String usrid, List attachments,String bxpk) throws BusinessException
//	{
//		if (attachments == null || attachments.isEmpty())
//		{
//			return ;
//		}
//
//		for (Object attachObj : attachments)
//		{
//			Map attachMap = (Map) attachObj;
//			String field = (String) attachMap.get("attachment"); // 文件内容
//			BASE64Decoder decoder = new BASE64Decoder();
//			InputStream in = null;
//			try
//			{
//				in = new ByteArrayInputStream(decoder.decodeBuffer(field));
//			}
//			catch (IOException e)
//			{
//				ExceptionHandler.handleException(e);
//			}
//
//			upload(in, attachMap,groupid,bxpk);
//		}
//
//	}
//	
	
	public Map<String, Map<String, String>> queryBXHeads(String userid,String querydate)
	throws BusinessException {
		// 默认查询当前用户querydate：0 本日，1本周，2本月，内填写的单据
		UFDate currentdate = new UFDate();
		String sqlWhere = "";
		if("0".equals(querydate)){
			sqlWhere = "(djrq >= '"+currentdate.asBegin()+"' and djrq <= '"+currentdate.asEnd()+"')";
		}else if("1".equals(querydate)){
			sqlWhere = getWeekDay();
		}else if("2".equals(querydate)){
			sqlWhere = getMonthDate();
		}else if("3".equals(querydate)){
			// 获取Calendar
			Calendar calendar = Calendar.getInstance();
			// 结束日期设置日期为本月最大日期
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
			UFDate enddate = new UFDate(calendar.getTime());
			// 开始日期设置月份-2,日期设置为1
			calendar.add(Calendar.MONTH, -2);
			calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
			UFDate begindate = new UFDate(calendar.getTime());
			
			sqlWhere = "(djrq >= '"+begindate.asBegin()+"' and djrq <= '"+enddate.asEnd()+"')";
		}
		return queryBxheadVOsByWhere(userid,sqlWhere);
	}
	
	public Map<String, Map<String, String>> queryBXHeadsByDate(String userid,String startdate,String enddate)
	throws BusinessException {
		String sqlWhere = "(djrq >= '"+startdate+"' and djrq <= '"+enddate+"')";
		return queryBxheadVOsByWhere(userid,sqlWhere);
	}
	

	private Map<String, Map<String, String>> queryBxheadVOsByWhere(String userid,String sql) throws BusinessException {
		String sqlWhere = "djlxbm = '"+defaultDjlxbm+"' and "+sql;
		initEvn(userid);
		//待查询的字段
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
				JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		
		String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere,InvocationInfoProxy.getInstance().getUserId()
				,BXConstans.BX_DJDL);
		
		IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
				IBXBillPrivate.class);
		List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(pks,
				BXConstans.BX_DJDL);
		

		Map<String, Map<String, String>> resultmap = new LinkedHashMap<String, Map<String, String>>();
		
		if (list != null && !list.isEmpty()) {
			for (JKBXHeaderVO vo : list) {
				Map<String, String> fieldvalueMap = new HashMap<String, String>();
				for (int i = 0; i < queryFields.length; i++) {
					String field = queryFields[i];
					String attributeValue = vo.getAttributeValue(field) == null ? ""
							: vo.getAttributeValue(field).toString();
					fieldvalueMap.put(field, attributeValue);
					if(JKBXHeaderVO.DJRQ.equals(field)){
						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
					}else if(JKBXHeaderVO.SPZT.equals(field)){
						String spztshow = getSpztShow(vo.getSpzt());
						fieldvalueMap.put("spztshow", spztshow);
					}
					
				}
				resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
			}
		} 		
		return resultmap;
	}
	
	/**
	 * 我的--未完成
	 * 查询当前用户全部未审批通过的报销单
	 * 
	 * @param userid
	 * @param flag 审批状态标志 1-已完成 0-未完成
	 * @return 交易类型名称分组的报销单属性值
	 * @throws BusinessException
	 */
	public Map<String, Map<String, String>> getBXHeadsByUser(String userid,String flag)
	throws BusinessException {

		initEvn(userid);
		String sqlWhere = SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, getDjlxbmArray(), false);
//		sqlWhere = JKBXHeaderVO.DJLXBM + " in(" + "'2641','2642','2643')";
		if("1".equals(flag)){
			sqlWhere = sqlWhere + " and " + JKBXHeaderVO.SPZT +" in (" + 
					""+IBillStatus.CHECKPASS + "," + IBillStatus.NOPASS + ")";
		}else{
			sqlWhere = sqlWhere + " and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.CHECKPASS + " and " + JKBXHeaderVO.SPZT +"<>"+IBillStatus.NOPASS;
		}
		
		
		//待查询的字段
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
				JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		
		String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere,InvocationInfoProxy.getInstance().getUserId()
				,BXConstans.BX_DJDL);
		
		IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
				IBXBillPrivate.class);
		List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(pks,
				BXConstans.BX_DJDL);
		
		
		
		Map<String, Map<String, String>> resultmap = new LinkedHashMap<String, Map<String, String>>();
		if (list != null && !list.isEmpty()) {
			for (JKBXHeaderVO vo : list) {
				Map<String, String> fieldvalueMap = new HashMap<String, String>();
				for (int i = 0; i < queryFields.length; i++) {
					String field = queryFields[i];
					String attributeValue = vo.getAttributeValue(field) == null ? ""
							: vo.getAttributeValue(field).toString();
					fieldvalueMap.put(field, attributeValue);
					if(JKBXHeaderVO.DJRQ.equals(field)){
						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
					}else if(JKBXHeaderVO.TOTAL.equals(field)){
						UFDouble total = new UFDouble(attributeValue);
						total = total.setScale(2,4);
						fieldvalueMap.put(field, total.toString());
					}if(JKBXHeaderVO.SPZT.equals(field)){
						String sprshow = getSprShow(vo.getSpzt());
						String spztshow = getSpztShow(vo.getSpzt());
						fieldvalueMap.put("sprshow", sprshow);
						fieldvalueMap.put("spztshow", spztshow);
					}if(JKBXHeaderVO.ZY.equals(field) && attributeValue.length()>10){
						attributeValue = attributeValue.substring(0,5)+"...";
						fieldvalueMap.put(field, attributeValue);
					}
					
				}
				String djlxmc = PfDataCache.getBillTypeNameByCode(vo.getDjlxbm());
				if(djlxmc.endsWith("报销单"))
					djlxmc = djlxmc.substring(0, djlxmc.length()-3);
				fieldvalueMap.put("djlxmc", djlxmc);
				resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
			}
		} 
		
		// 补充查询审批进行中的报销单的当前审批人
		if(!resultmap.isEmpty()){
			Map<String, String> checkmanMap = getBillCheckman(resultmap.keySet().toArray(new String[0]),defaultDjlxbm);
			for (Entry<String, String> checkmans : checkmanMap.entrySet()) {
				Map<String, String> map = resultmap.get(checkmans.getKey());
				// 将审批状态替换为当前审批人
				String spr = checkmans.getValue();
				if(spr!=null && !spr.equals("") && spr.contains(","))
					spr = spr.split(",")[0];
				map.put("sprshow", "待("+spr+")审批");
			}
		}
		
		return resultmap;
	}
	/**
	 * 查询当前用户全部未审批通过的报销单
	 * 
	 * @param userid
	 * @return 交易类型名称分组的报销单属性值
	 * @throws BusinessException
	 */
	public Map<String, Map<String, String>> getUnApprovedBXHeadsByUser(String userid)
	throws BusinessException {
		initEvn(userid);
		String sqlWhere = JKBXHeaderVO.SPZT +"<>"+IBillStatus.CHECKPASS;
		//待查询的字段
		String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
				JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
				JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
				JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		
		String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhere,InvocationInfoProxy.getInstance().getUserId()
				,BXConstans.BX_DJDL);
		
		IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
				IBXBillPrivate.class);
		List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(pks,
				BXConstans.BX_DJDL);
		

		
		Map<String, Map<String, String>> resultmap = new HashMap<String, Map<String, String>>();
		if (list != null && !list.isEmpty()) {
			for (JKBXHeaderVO vo : list) {
				Map<String, String> fieldvalueMap = new HashMap<String, String>();
				for (int i = 0; i < queryFields.length; i++) {
					String field = queryFields[i];
					String attributeValue = vo.getAttributeValue(field) == null ? ""
							: vo.getAttributeValue(field).toString();
					fieldvalueMap.put(field, attributeValue);
					if(JKBXHeaderVO.DJRQ.equals(field)){
						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
					}else if(JKBXHeaderVO.SPZT.equals(field)){
						String spztshow = getSpztShow(vo.getSpzt());
						fieldvalueMap.put("spztshow", spztshow);
					}
					
				}
				String djlxmc = PfDataCache.getBillTypeNameByCode(vo.getDjlxbm());
//				djlxmc = djlxmc.substring(0, djlxmc.length()-3);
				fieldvalueMap.put("djlxmc", djlxmc);
				resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
			}
		} 
		
		//分组显示待审批
//		Map<String,Map<String, Map<String, String>>> djlxresultmap = new HashMap<String,Map<String, Map<String, String>>>();
//		Map<String, Map<String, String>> resultmap = null;
//		if (list != null && !list.isEmpty()) {
//			for (JKBXHeaderVO vo : list) {
//				Map<String, String> fieldvalueMap = new HashMap<String, String>();
//				for (int i = 0; i < queryFields.length; i++) {
//					String field = queryFields[i];
//					String attributeValue = vo.getAttributeValue(field) == null ? ""
//							: vo.getAttributeValue(field).toString();
//					fieldvalueMap.put(field, attributeValue);
//					if(JKBXHeaderVO.DJRQ.equals(field)){
//						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
//					}else if(JKBXHeaderVO.SPZT.equals(field)){
//						String spztshow = getSpztShow(vo.getSpzt());
//						fieldvalueMap.put("spztshow", spztshow);
//					}
//					
//				}
//				String djlxbmc = PfDataCache.getBillTypeNameByCode(vo.getDjlxbm());
//				resultmap = djlxresultmap.get(djlxbmc);
//				if(resultmap==null){
//					resultmap = new LinkedHashMap<String, Map<String, String>>();
//					djlxresultmap.put(djlxbmc, resultmap);
//				}
//				resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
//			}
//		} 
//		return djlxresultmap;
		
		return resultmap;
	}
	
	
	//查询当前用户待审批单据,map按照本周、上周、更早进行分组
	public Map<String,Map<String, Map<String, String>>> getBXApprovingBillByUser(String userid)
			throws BusinessException {
		initEvn(userid);
		Map<String,Map<String, Map<String, String>>> resultmap = new LinkedHashMap<String,Map<String, Map<String, String>>>();
	      //待查询的字段
		
		  String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
					JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
					JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
					JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		  
		  //联合审批流查询主键信息
//		  String bxSql = "select distinct pk_jkbx FROM er_bxzb er_bxzb , pub_workflownote wf where er_bxzb.pk_jkbx=wf.billid(+)"
//            + " AND (wf.billid is null or (wf.ischeck='N' AND wf.approvestatus!=4 and wf.checkman= '"
//            + userid
//            + "' AND ( wf.dr=0 or isnull(wf.dr,0)=0 )  AND wf.actiontype = 'Z'))"
//            +" AND er_bxzb.djzt = 1 AND er_bxzb.spzt in (2,3) and er_bxzb.DR ='0'"
//            +" AND  er_bxzb.djlxbm in ('2641','2642','2643')  and er_bxzb.QCBZ='N'";
//		  BaseDAO dao = new BaseDAO();
//		  List<String> pks = new ArrayList<String>();
//		  BaseProcessor baseprocessor = new BaseProcessor(){
//	         private static final long serialVersionUID = 1L;
//			@Override
//			public Object processResultSet(ResultSet rs) throws SQLException {
//				List<String> result = new ArrayList<String>();
//                while (rs.next()) {
//                    result.add(rs.getObject(1).toString());
//                }
//                return result;
//			}
//	      };
//	      pks = (ArrayList<String>)dao.executeQuery(bxSql,baseprocessor);
		  String sqlWhere = "QCBZ='N' and DR ='0'";
		  //待查询的单据类型
		  String[] djlxbmarray = getDjlxbmArray();//{"2643","2642","2641"};
		  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbmarray, false);
		  String[] billPks = queryApprovedWFBillPksByCondition(userid, djlxbmarray, false);
		  if (billPks != null && billPks.length > 0) {
			  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, false);
		  } else {
			  sqlWhere += " and 1=0 ";
		  }
		  String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class)
			.queryPKsByWhereForBillManageNode(sqlWhere, InvocationInfoProxy.getInstance().getGroupId(), 
					InvocationInfoProxy.getInstance().getUserId());
	      //根据主键查询表头信息
	      if(pks != null && pks.length > 0){
	    	  List<JKBXHeaderVO> list = NCLocator.getInstance().lookup(IBXBillPrivate.class)
	    	  	.queryHeadersByPrimaryKeys(pks, BXConstans.BX_DJDL);
	    	  
	    	  //将查询好的数据组装成map
				Map<String, Map<String, String>> pksbzmap = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> pksszmap = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> pksgzmap = new LinkedHashMap<String, Map<String, String>>();
				
	    	  if (list != null && !list.isEmpty()) {
	    		  Set<String> userSet = new HashSet<String>();
	    		  for(int i=0;i<list.size();i++){
	    			  userSet.add(list.get(i).getCreator());//getJkbxr()
	    		  }
//	    		  PsndocVO[] docvos = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocByPks(userSet.toArray(new String[0]));
	    		  UserVO[] uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).findUserByIDs(userSet.toArray(new String[0]));
	    		  Map<String,String> userMap = new HashMap<String,String>();
	    		  for(int i=0;i<uservo.length;i++){
	    			  userMap.put(uservo[i].getCuserid(), uservo[i].getUser_name());
	    		  }
	    		  Map<String,String> djlxMap = new HashMap<String,String>();
				  DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
						"pk_group = '"+InvocationInfoProxy.getInstance().getGroupId()+"' and djdl in('jk','bx')");
				  for(int i=0;i<vos.length;i++){
					  String djlxmc = vos[i].getDjlxmc();
//					  djlxmc = djlxmc.substring(0, djlxmc.length()-3);
					  djlxMap.put(vos[i].getDjlxbm(), djlxmc);
				  }
	  			  for (JKBXHeaderVO vo : list) {
	  				Map<String, String> fieldvalueMap = new HashMap<String, String>();
	  				for (int i = 0; i < queryFields.length; i++) {
	  					String field = queryFields[i];
	  					String attributeValue = vo.getAttributeValue(field) == null ? ""
	  							: vo.getAttributeValue(field).toString();
	  					fieldvalueMap.put(field, attributeValue);
	  					if(JKBXHeaderVO.DJRQ.equals(field)){
	  						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
	  					}else if(JKBXHeaderVO.SPZT.equals(field)){
	  						String spztshow = getSpztShow(vo.getSpzt());
	  						fieldvalueMap.put("spztshow", spztshow);
	  					}else if(JKBXHeaderVO.TOTAL.equals(field)){
							UFDouble total = new UFDouble(attributeValue);
							total = total.setScale(2,4);
							fieldvalueMap.put(field, total.toString());
						}else if(JKBXHeaderVO.JKBXR.equals(field)){
	  						fieldvalueMap.put(JKBXHeaderVO.JKBXR, userMap.get(vo.getAttributeValue(JKBXHeaderVO.CREATOR)));
	  					}else if(JKBXHeaderVO.DJLXBM.equals(field)){
		  					fieldvalueMap.put("djlxmc", djlxMap.get(vo.getDjlxbm()));
	  					}if(JKBXHeaderVO.ZY.equals(field) && attributeValue.length()>10){
							attributeValue = attributeValue.substring(0,5)+"...";
							fieldvalueMap.put(field, attributeValue);
						}
	  					
	  				}
	  				UFDate djrq = vo.getDjrq();
	  				if(djrq.after(getNowWeekMonday()) || djrq.equals(getNowWeekMonday()))
	  					pksbzmap.put(vo.getPrimaryKey(), fieldvalueMap);
	  				else if(djrq.before(getNowWeekMonday()) && djrq.after(getLastWeekMonday())
	  						|| djrq.equals(getLastWeekMonday()))
	  					pksszmap.put(vo.getPrimaryKey(), fieldvalueMap);
	  				else
	  					pksgzmap.put(vo.getPrimaryKey(), fieldvalueMap);
	  			}
	  			//组装最后一层map，根据日期
	  			resultmap.put("本周", pksbzmap);
	  			resultmap.put("上周", pksszmap);
	  			resultmap.put("更早", pksgzmap);
	  		} 		
	  	}
	    return resultmap;
	}
	
	//查询当前用户已审批单据,map按照本周、上周、更早进行分组
	public Map<String,Map<String, Map<String, String>>> getBXApprovedBillByUser(String userid)
			throws BusinessException {
		initEvn(userid);
		Map<String,Map<String, Map<String, String>>> resultmap = new LinkedHashMap<String,Map<String, Map<String, String>>>();
	      //待查询的字段
		  String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
					JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
					JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
					JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
		  //待查询的单据类型
//		  String[] djlxbmarray = getDjlxbmArray();//{"2643","2642","2641"};
//		  String sqlWhere = " and " + SqlUtils.getInStr("er_bxzb." + JKBXHeaderVO.DJLXBM, djlxbmarray, false);
//		  
//		  //联合审批流查询主键信息 AND wf.approvestatus!=4
//		  String bxSql = "select distinct pk_jkbx FROM er_bxzb er_bxzb , pub_workflownote wf where er_bxzb.pk_jkbx=wf.billid(+)"
//            + " AND ((wf.billid is null and er_bxzb.approver='"
//            + userid
//            + "') or (wf.ischeck='Y' and wf.checkman= '"
//            + userid
//            + "' AND ( wf.dr=0 or isnull(wf.dr,0)=0 )  AND wf.actiontype = 'Z'))"
//            +" AND er_bxzb.spzt in (0,1,2) and er_bxzb.DR ='0'"
//            +" and er_bxzb.QCBZ='N' "
//            + sqlWhere;
//		  BaseDAO dao = new BaseDAO();
//		  List<String> pks = new ArrayList<String>();
//		  BaseProcessor baseprocessor = new BaseProcessor(){
//	         private static final long serialVersionUID = 1L;
//			@Override
//			public Object processResultSet(ResultSet rs) throws SQLException {
//				List<String> result = new ArrayList<String>();
//                while (rs.next()) {
//                    result.add(rs.getObject(1).toString());
//                }
//                return result ;
//			}
//	      };
//	      pks = (ArrayList<String>)dao.executeQuery(bxSql,baseprocessor);
		  String sqlWhere = "QCBZ='N' and DR ='0'"; 
		  String[] djlxbm = getDjlxbmArray();
		  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbm, false);
		  String[] billPks = queryApprovedWFBillPksByCondition(userid, djlxbm, true);
		  if (billPks != null && billPks.length > 0) {
			  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, true);
		  } else {
			  sqlWhere += " and 1=0 ";
		  }
		  String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class)
			.queryPKsByWhereForBillManageNode(sqlWhere, InvocationInfoProxy.getInstance().getGroupId(), 
					InvocationInfoProxy.getInstance().getUserId());
	      //根据主键查询表头信息
	      if(pks!=null && pks.length>0){
	    	  List<JKBXHeaderVO> list = NCLocator.getInstance().lookup(IBXBillPrivate.class)
	    	  	.queryHeadersByPrimaryKeys(pks, BXConstans.BX_DJDL);//pks.toArray(new String[]{})
	    	  
	    	  //将查询好的数据组装成map
				Map<String, Map<String, String>> pksbzmap = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> pksszmap = new LinkedHashMap<String, Map<String, String>>();
				Map<String, Map<String, String>> pksgzmap = new LinkedHashMap<String, Map<String, String>>();
	    	  if (list != null && !list.isEmpty()) {
	    		  Set<String> userSet = new HashSet<String>();
	    		  for(int i=0;i<list.size();i++){
	    			  userSet.add(list.get(i).getCreator());//getJkbxr()
	    		  }
//	    		  PsndocVO[] docvos = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocByPks(userSet.toArray(new String[0]));
	    		  UserVO[] uservo = NCLocator.getInstance().lookup(IUserManageQuery.class).findUserByIDs(userSet.toArray(new String[0]));
	    		  Map<String,String> userMap = new HashMap<String,String>();
	    		  for(int i=0;i<uservo.length;i++){
	    			  userMap.put(uservo[i].getCuserid(), uservo[i].getUser_name());
	    		  }
	    		  Map<String,String> djlxMap = new HashMap<String,String>();
				  DjLXVO[] vos = CacheUtil.getValueFromCacheByWherePart(DjLXVO.class, 
						"pk_group = '"+InvocationInfoProxy.getInstance().getGroupId()+"' and djdl in('jk','bx')");
				  for(int i=0;i<vos.length;i++){
					  String djlxmc = vos[i].getDjlxmc();
//					  djlxmc = djlxmc.substring(0, djlxmc.length()-3);
					  djlxMap.put(vos[i].getDjlxbm(), djlxmc);
				  }
	  			for (JKBXHeaderVO vo : list) {
	  				Map<String, String> fieldvalueMap = new HashMap<String, String>();
	  				for (int i = 0; i < queryFields.length; i++) {
	  					String field = queryFields[i];
	  					String attributeValue = vo.getAttributeValue(field) == null ? ""
	  							: vo.getAttributeValue(field).toString();
	  					fieldvalueMap.put(field, attributeValue);
	  					if(JKBXHeaderVO.DJRQ.equals(field)){
	  						fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
	  					}else if(JKBXHeaderVO.TOTAL.equals(field)){
							UFDouble total = new UFDouble(attributeValue);
							total = total.setScale(2,4);
							fieldvalueMap.put(field, total.toString());
						}else if(JKBXHeaderVO.SPZT.equals(field)){
	  						String spztshow = getSpztShow(vo.getSpzt());
	  						fieldvalueMap.put("spztshow", spztshow);
	  					}else if(JKBXHeaderVO.JKBXR.equals(field)){
	  						fieldvalueMap.put(JKBXHeaderVO.JKBXR, userMap.get(vo.getAttributeValue(JKBXHeaderVO.CREATOR)));
	  					}else if(JKBXHeaderVO.DJLXBM.equals(field)){
		  					fieldvalueMap.put("djlxmc", djlxMap.get(vo.getDjlxbm()));
	  					}if(JKBXHeaderVO.ZY.equals(field) && attributeValue.length()>10){
							attributeValue = attributeValue.substring(0,10)+"...";
							fieldvalueMap.put(field, attributeValue);
						}
	  					
	  				}
	  				UFDate djrq = vo.getDjrq();
	  				if(djrq.after(getNowWeekMonday()) || djrq.equals(getNowWeekMonday()))
	  					pksbzmap.put(vo.getPrimaryKey(), fieldvalueMap);
	  				else if(djrq.before(getNowWeekMonday()) && djrq.after(getLastWeekMonday())
	  						|| djrq.equals(getLastWeekMonday()))
	  					pksszmap.put(vo.getPrimaryKey(), fieldvalueMap);
	  				else
	  					pksgzmap.put(vo.getPrimaryKey(), fieldvalueMap);
	  			}
	  			//组装最后一层map，根据日期
	  			resultmap.put("本周", pksbzmap);
	  			resultmap.put("上周", pksszmap);
	  			resultmap.put("更早", pksgzmap);
	  		} 		
	  	}
	    return resultmap;
	}
	
	
	//获取本周一日期
	public static UFDate getNowWeekMonday() {
        Calendar cal = Calendar.getInstance();
        //解决周日会出现 并到下一周的情况 
		cal.setFirstDayOfWeek(Calendar.MONDAY);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);    
        return new UFDate(cal.getTime()); 
    }
	
	//获取上周一日期
	public static UFDate getLastWeekMonday() {    
		Calendar cal = Calendar.getInstance();
        //解决周日会出现 并到下一周的情况 
		cal.setFirstDayOfWeek(Calendar.MONDAY);
		cal.add(Calendar.WEEK_OF_MONTH, -1);
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);   
        return new UFDate(cal.getTime());    
   }
	public static String getSpztShow(Integer spzt) {
		String value = "";
		switch (spzt) {
		case -1:
			value = "待提交";
			break;
		case 0: 
			value = BillStatusEnum.NOPASS.getName();
			break;
		case 1:
			value = BillStatusEnum.APPROVED.getName();
			break;
		case 2:
			value = BillStatusEnum.APPROVING.getName();
			break;
		case 3:
			value = "已提交";
		default:
			break;
		}
		return value;
	}

	public static String getSprShow(Integer spzt) {
		String value = "";
		switch (spzt) {
		case 2:
			value = BillStatusEnum.APPROVING.getName();
			break;
		default:
			value = "";
			break;
		}
		return value;
	}
	
	public String deleteJkbx(String headpk,String userid) throws BusinessException {
		initEvn(userid);
		try{ 
			// 查询
//			List<JKBXVO> vos = NCLocator.getInstance().
//			  lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{headpk}, null);
			List<JKBXHeaderVO> list = NCLocator.getInstance().
					lookup(IBXBillPrivate.class).queryHeadersByPrimaryKeys(new String[]{headpk},"bx" );
//			BaseDAO dao = new BaseDAO();
//			JKBXHeaderVO bxheadvo = (JKBXHeaderVO) dao.retrieveByPK(JKBXHeaderVO.class, headpk);
//			List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
//			list.add(bxheadvo);
			List<JKBXVO> vos = NCLocator.getInstance().
					lookup(IBXBillPrivate.class).retriveItems(list);
			if(vos == null || vos.isEmpty()){ 
				throw new BusinessException("单据已被删除，请检查"); 
			}
			JKBXVO vo = vos.get(0);
			if(vo.getParentVO() != null ){
				JKBXHeaderVO head = vo.getParentVO();
				if (head.getSpzt() != null && (head.getSpzt().equals(IPfRetCheckInfo.GOINGON) )) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000398"))/*
																										 * @
																										 * res
																										 * "单据正在审核中，不能删除！"
																										 */;
				}
				if(head.getSpzt() != null && head.getSpzt().equals(IPfRetCheckInfo.COMMIT)){
					//收回提交
					initEvn(head.getOperator());
					//收回提交
					String actionType = getActionCode(IPFActionName.UNSAVE,PK_ORG);
					  PfUtilPrivate.runAction(null, actionType, head.getDjlxbm(), vo, null,
								null, null, null);
//					PfUtilPrivate.runAction(null, IPFActionName.UNSAVE
//							+ InvocationInfoProxy.getInstance().getUserId(), head.getDjlxbm(), vo, null,
//							null, null, null); 
				}
				if (head.getSpzt() != null && head.getSpzt().equals(IPfRetCheckInfo.NOPASS)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000914"))/*
																										 * @
																										 * res
																										 * "审批未通过的单据不可以删除！"
																										 */;
				}
				//ehp2： 对于暂估的单据是不可以删除的
				if(head.getVouchertag()!= null && head.getVouchertag()==BXStatusConst.ZGDeal){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000953"))/*
					 * @
					 * res
					 * "存在暂估凭证的单据，不可以删除！"
					 */;
	
				}
			}
			// 执行删除
			NCLocator.getInstance().
			lookup(IBXBillPublic.class).deleteBills(vos.toArray(new JKBXVO[0]));
			return "success";
		}catch(BusinessException e){
			String msg = e.getMessage();
			return msg;
		}
	}
	
	
	 
	
	public static String getWeekDay() {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // 获取本周一的日期
		cal.set(Calendar.DAY_OF_MONTH, Calendar.DATE); // 获取本周一的日期

		UFDate begindate = new UFDate(cal.getTime());
		// 这种输出的是上个星期周日的日期，因为老外那边把周日当成第一天
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		// 增加一个星期，才是我们中国人理解的本周日的日期
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		UFDate enddate = new UFDate(cal.getTime());
		return "(djrq >= '"+begindate.asBegin()+"' and djrq <= '"+enddate.asEnd()+"')";
	}

	public static String getMonthDate() {
		// 获取Calendar
		Calendar calendar = Calendar.getInstance();
		// 设置时间,当前时间不用设置
		// calendar.setTime(new Date());
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
		UFDate begindate = new UFDate(calendar.getTime());
		// 设置日期为本月最大日期
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		UFDate enddate = new UFDate(calendar.getTime());
		
		return "(djrq >= '"+begindate.asBegin()+"' and djrq <= '"+enddate.asEnd()+"')";
	}

	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> queryReimType(String userid) throws BusinessException {
		// 默认查询当前集团，未停用的报销类型
		initEvn(userid);
		String condition = " pk_group = '"+InvocationInfoProxy.getInstance().getGroupId()+"' and inuse='N'";
		BaseDAO dao = new BaseDAO();
		Collection<ReimTypeVO> c = dao.retrieveByClause(ReimTypeVO.class, condition, new String[]{ReimTypeVO.PK_REIMTYPE,ReimTypeVO.CODE,ReimTypeVO.NAME});
		Map<String, Map<String, String>> resultmap = new HashMap<String, Map<String,String>>();
		if(c != null && !c.isEmpty()){
			for (ReimTypeVO reimTypeVO : c) {
				Map<String, String> valuemap = new HashMap<String, String>();
				valuemap.put(ReimTypeVO.PK_REIMTYPE, reimTypeVO.getPk_reimtype());
				valuemap.put(ReimTypeVO.CODE, reimTypeVO.getCode());
				valuemap.put(ReimTypeVO.NAME, reimTypeVO.getName());
				resultmap.put(reimTypeVO.getPk_reimtype(), valuemap);
			}
		}
		return resultmap;
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> queryExpenseTypeInfoString(String userid) throws BusinessException {
		// 默认查询当前集团收支项目
		initEvn(userid);
		String pk_org = "select pk_org from bd_psndoc where pk_psndoc in (select pk_psndoc from sm_user where cuserid='" + userid + "') ";
		String condition = "enablestate = '2' and  ( exists (select 1 from bd_inoutuse a where a.pk_inoutbusiclass = bd_inoutbusiclass.pk_inoutbusiclass and a.pk_org in("
			+ pk_org + ")) or pk_org in ("+ pk_org + ")) order by code";
		BaseDAO dao = new BaseDAO();
		Collection<InoutBusiClassVO> c = dao.retrieveByClause(InoutBusiClassVO.class, condition, new String[]{InoutBusiClassVO.PK_INOUTBUSICLASS,InoutBusiClassVO.CODE,InoutBusiClassVO.NAME});
		Map<String, Map<String, String>> resultmap = new HashMap<String, Map<String,String>>();
		if(c != null && !c.isEmpty()){
			for (InoutBusiClassVO inoutBusiClassVO : c) {
				Map<String, String> valuemap = new HashMap<String, String>();
				valuemap.put(InoutBusiClassVO.PK_INOUTBUSICLASS, inoutBusiClassVO.getPk_inoutbusiclass());
				valuemap.put(InoutBusiClassVO.CODE, inoutBusiClassVO.getCode());
				valuemap.put(InoutBusiClassVO.NAME, inoutBusiClassVO.getName());
				resultmap.put(inoutBusiClassVO.getPk_inoutbusiclass(), valuemap);
			}
		}
		return resultmap;
	}

	
	public String commitJkbx(String userid,String headpk) throws BusinessException {
		initEvn(userid);
		  // 查询
		  List<JKBXVO> vos = NCLocator.getInstance().
		    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{headpk}, null);
		  if(vos == null || vos.isEmpty()){
			  throw new BusinessException("单据已被删除，请检查");
		  }
		  JKBXVO jkbxvo = vos.get(0);
		  // 执行提交
		  String actionType = getActionCode(IPFActionName.SAVE,PK_ORG);
		  PfUtilPrivate.runAction(null, actionType, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
					null, null, null);
//		  PfUtilPrivate.runAction(null, IPFActionName.SAVE+ userid, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
//					null, null, null);
		  return "true";
	}
	
	
//   private Map upload(InputStream in, Map map,String pk_group,String bxpk)
//       throws BusinessException
//   {
//       String fn = map.get("name").toString();
//       String moduleName = "erm";
//       
//       BaFileStoreExt bafile = new BaFileStoreExt();
//		bafile.setPk_group(pk_group);
//		bafile.setPk_billtype("bx");
//		bafile.setPk_billitem(bxpk);
//		
//       FileHeader header = FileStorageClient.getInstance().uploadFile(moduleName, fn, in, true, new IFileStorageExt[]{bafile});
//       String fileid = header.getGuid();
//       String filepath = header.getPath();
//       map.put("fileid", fileid);
//       map.put("filepath", filepath);
//       map.put("downloadpath", header.toString());
//       map.put("downloadpath2", header.toString());
//       map.put("in", in.toString());
//       return map;
//   }
//   
//	public Map download(OutputStream out, Map map) throws BusinessException {
//		Map map1;
//		try {
//			JSONObject fileJson = new JSONObject(map.get("params").toString());
//			String filePath = fileJson.getString("downloadpath");
//			if ((filePath == null || "".equalsIgnoreCase(filePath))
//					&& fileJson.has("fileid"))
//				filePath = fileJson.getString("fileid");
//			String downflag = "false";
//			if (fileJson.has("downflag"))
//				downflag = fileJson.getString("downflag");
//			String startpos = "0";
//			if (fileJson.has("startposition"))
//				startpos = fileJson.getString("startposition");
//			String endpos = "500";
//			if (fileJson.has("endposition"))
//				endpos = fileJson.getString("endposition");
//			String moduleName = "erm";
//			if (fileJson.has("modulename"))
//				moduleName = fileJson.getString("modulename");
//			IFileTransfer fileTransfer = FileStorageClient.getInstance()
//					.getFileTransfer(moduleName);
//			out = new ByteArrayOutputStream();
//			if (downflag != null
//					&& ("true".equalsIgnoreCase(downflag) || "Y"
//							.equalsIgnoreCase(downflag)))
//				fileTransfer.download(filePath, out, Long.valueOf(startpos)
//						.longValue(), Long.valueOf(endpos).longValue());
//			else
//				fileTransfer.download(filePath, out);
//			out.flush();
//			byte bytes[] = ((ByteArrayOutputStream) out).toByteArray();
//			byte bese64Bytes[] = Base64.encodeBase64(bytes);
//			map.put("fields", new String(bese64Bytes));
//			out.close();
//			out = null;
//			map1 = map;
//		} catch (JSONException e) {
//			throw new BusinessException((new StringBuilder()).append(
//					"File download params value has some wrong:").append(
//					e.getMessage()).toString());
//		} catch (IOException e) {
//			throw new BusinessException((new StringBuilder()).append(
//					"Download file some wrong:").append(e.getMessage())
//					.toString());
//		} finally {
//			if (out != null) {
//				try {
//					out.close();
//				} catch (IOException e) {
//					throw new BusinessException((new StringBuilder()).append(
//							"Download file some wrong:").append(e.getMessage())
//							.toString());
//				}
//				out = null;
//			}
//		}
//		return map1;
//	}
	
	public FileHeader[] getAttachmentList(String pk_group, String bxpk) {
		IFileTransfer fileTransfer = FileStorageClient.getInstance()
				.getFileTransfer("erm");

		BaFileStoreExt bafile = new BaFileStoreExt();
		bafile.setPk_group(pk_group);
		bafile.setPk_billtype("bx");
		bafile.setPk_billitem(bxpk);
		
		FileHeader[] heads = fileTransfer.queryHeaders(bafile);
		return heads;
	}
	
//	@SuppressWarnings("unchecked")
//	public List<Map> getFileList(String bxpk,String userid) throws BusinessException{
//		// 获取附件列表
//		List<Map> attatchmapList = new ArrayList<Map>();
//		
//		IQueryFolderTreeNodeService fileservice = NCLocator.getInstance().lookup(IQueryFolderTreeNodeService.class);
//		NCFileNode filenode = fileservice.getNCFileNodeTreeAndCreateAsNeed(bxpk, userid);
//		if(filenode != null){
//			Enumeration fileEnum = filenode.breadthFirstEnumeration();
//			while (fileEnum.hasMoreElements()) {
//				NCFileNode tempfile = (NCFileNode) fileEnum.nextElement();
//				Collection<NCFileVO> files = tempfile.getFilemap().values();
//				for (NCFileVO ncFileVO : files) {
//					LinkedHashMap<String, String> fileMap = new LinkedHashMap<String, String>();
//					attatchmapList.add(fileMap);
//
//					fileMap.put("name", ncFileVO.getName()); // 附件文件名称
//					fileMap.put("type", ncFileVO.getFiletype()); // 文件类型
//					fileMap.put("id", ncFileVO.getPk()); // 文件标识
//					fileMap.put("size", ""+ncFileVO.getFileLen()); // 文件大小
//					fileMap.put("path", ncFileVO.getFullPath()); // 文件路径
//					fileMap.put("content", getFileContent(ncFileVO)); // 文件内容
//				}
//			}
//		}
//		
//		
//		return attatchmapList;
//	}

//	private String getFileContent(NCFileVO ncFileVO) throws BusinessException {
//		IFileSystemService fileservice = NCLocator.getInstance().lookup(IFileSystemService.class);
//		
//		OutputStream out = new ByteArrayOutputStream();
//		fileservice.downLoadFile(ncFileVO.getFullPath(), out);
//		byte[] downloaded = ((ByteArrayOutputStream) out).toByteArray(); // 附件内容
//		BASE64Encoder encoder = new BASE64Encoder();
//		String content = encoder.encodeBuffer(downloaded);
//		
//		return content;
//	}
	
	
	/**
	 * 批量查询单据当前审批人
	 * 
	 * @param billIdAry
	 * @param billType
	 * @return
	 * @throws BusinessException
	 */
	private Map<String,String> getBillCheckman(String billIdAry[], String billType) throws BusinessException{
		String sql = (new StringBuilder()).append(" select billVersionPK ,checkman,user_name from pub_workflownote ,sm_user " +
				" where pub_workflownote.checkman = sm_user.cuserid " +
				" and approvestatus in(").append(WfTaskOrInstanceStatus.getUnfinishedStatusSet()).append(") " +
						"and actiontype not in('").append("BIZ").append("','").append("MAKEBILL").append("') ")
						.append(" and "+SqlTools.getInStr("billVersionPK", billIdAry, true)).toString();
		
	    
		BaseDAO dao = new BaseDAO();
		final Map<String,String> resultmap = new HashMap<String, String>();
		dao.executeQuery(sql, new BaseProcessor(){

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Object processResultSet(ResultSet resultset)
					throws SQLException {
				 while (resultset.next()) {
					 String billVersionPK = (String) resultset.getObject(1);
//					 String checkman = (String) resultset.getObject(2);
					 String user_name = (String) resultset.getObject(3);
					 
					 String rcheckman = resultmap.get(billVersionPK);
					if(rcheckman == null){
						resultmap.put(billVersionPK, user_name);
					}else{
						resultmap.put(billVersionPK, rcheckman+","+user_name);
					}
					
				}
				return null;
			}
			
		});
	
		return resultmap;
	}
	
	//审批单据//如果成功返回success，如果出错则返回出错提示
	public String auditBXBillByPKs(String[] pks,String userid) throws Exception {
		//初始化集团
		initEvn(userid);
		
		// 根据pk查出单据，并对单据进行审核较验信息
		List<JKBXVO> vos = NCLocator.getInstance().lookup(IBXBillPrivate.class)
	  	.queryVOsByPrimaryKeysForNewNode(pks, BXConstans.BX_DJDL,false,null);
		if(vos == null || vos.size()==0)
			return "单据已删除";
		int count = vos.size();
		MessageVO[]	msgs = new MessageVO[count];
		List<AggregatedValueObject> auditVOs = new ArrayList<AggregatedValueObject>();
		JKBXVO[] jkbxVos = vos.toArray(new JKBXVO[0]);
		for (int i = 0; i < jkbxVos.length; i++) {
			JKBXVO vo = jkbxVos[i];
			// 如果报销单全部用来冲借款后，审批后，支付状态应该设置为：全部冲借款
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (!parentVO.isAdjustBxd()&&parentVO.zfybje.doubleValue() == 0 && parentVO.hkybje.doubleValue() == 0) {
				parentVO.setPayflag(BXStatusConst.ALL_CONTRAST);
			}
			vo.setNCClient(true);
			msgs[i] = checkVo(vo);

			if (!msgs[i].isSuccess()) {
				continue;
			}
			auditVOs.add(vo);
		}

		if (auditVOs.size() > 0) {
			// 加入进度条的审批
			if (auditVOs.size() > 1) {// 加入进度条的审批
				MessageVO[] msgReturn = executeBatchAudit(auditVOs);
				if (msgReturn != null) {
					for (int i = 0; i < msgReturn.length; i++) {
						for (int j = 0; j < msgs.length; j++) {
							if (msgReturn[i] != null &&  msgs[j]!=null
									&& msgs[j].getSuccessVO().getParentVO().getPrimaryKey().equals(msgReturn[i].getSuccessVO().getParentVO().getPrimaryKey())) {
								msgs[j] = msgReturn[i];
							}
						}
					}
				}
				return getBatchResults(msgs);
			} else {
				MessageVO[] returnMsgs = new MessageVO[] { approveSingle(auditVOs.get(0)) };
				return getBatchResults(returnMsgs);
			}
		} else {
			//单据都没有通过checkVo的审核
			return getBatchResults(msgs);
		}
	}
	
	protected MessageVO[] executeBatchAudit(List<AggregatedValueObject> auditVOs) throws Exception, BusinessException {
		// 审核动作处理
		List<MessageVO> result = null;
		Map<String, List<AggregatedValueObject>> typeMap = getTradeTypeAggVoMap(auditVOs);
		result = executeBatchManagerNodeAudit(typeMap);
		return result.toArray(new MessageVO[0]);
	}
	
	/**
	 * 管理节点批量审批 管理节点存在多交易类型的情况，需要特殊处理
	 * 
	 * @param auditVOs
	 * @param result
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected List<MessageVO> executeBatchManagerNodeAudit(Map<String, List<AggregatedValueObject>> typeMap) throws BusinessException {
		List<AggregatedValueObject> auditVOs = new ArrayList<AggregatedValueObject>();//所有单据
		for (Map.Entry<String, List<AggregatedValueObject>> entry : typeMap.entrySet()) {
			auditVOs.addAll(entry.getValue());
		}
		
		List<MessageVO> result = new ArrayList<MessageVO>();//结果
		String[] billIds = VOUtils.getAttributeValues(auditVOs.toArray(new AggregatedValueObject[0]), null);

//		BatchApproveModel batchApproveMode = new BatchApproveModel();
//		if (typeMap.size() > 1) {
//			batchApproveMode.setSingleBillSelected(false);
//		} else {
//			batchApproveMode.setSingleBillSelected(true);
//		}
//
//		batchApproveMode.setMessageUI(true);
//		batchApproveMode.setBillItem(auditVOs.size());

		// 获得审批意见, 放在WorkflownoteVO中
		WorkflownoteVO noteVO = new WorkflownoteVO();
		try {// CA签字
			boolean isNeedCASign = NCLocator.getInstance().lookup(IPFWorkflowQry.class)
					.isNeedCASign4Batch(InvocationInfoProxy.getInstance().getUserId(), typeMap.keySet().toArray(new String[0]), billIds);
			if (isNeedCASign) {
				noteVO.getRelaProperties().put(XPDLNames.ELECSIGNATURE, "true");
			}
		} catch (BusinessException e2) {
			ExceptionHandler.handleException(e2);
		}

//		BatchApproveWorkitemAcceptDlg dlg = new BatchApproveWorkitemAcceptDlg(null, noteVO);
//		dlg.setBachApproveMode(batchApproveMode);
		// 默认为批准
		noteVO.setChecknote(UFBoolean.valueOf(true).toString());
		noteVO.setApproveresult("Y");
		for (Map.Entry<String, List<AggregatedValueObject>> entry : typeMap.entrySet()) {
			List<AggregatedValueObject> aggVosTmepList = entry.getValue();
			PfProcessBatchRetObject retObject = null;
			String billType = entry.getKey();

			try {
				@SuppressWarnings("rawtypes")
				HashMap currParam = new HashMap();
				WorkflownoteVO currNote = (WorkflownoteVO) noteVO.clone();
				currParam.put(PfUtilBaseTools.PARAM_WORKNOTE, currNote);
				currParam.put(PfUtilBaseTools.PARAM_BATCH, PfUtilBaseTools.PARAM_BATCH);

				String actionName = "APPROVE" + InvocationInfoProxy.getInstance().getUserId();
				IplatFormEntry platFormService = NCLocator.getInstance().lookup(IplatFormEntry.class);

				retObject = (PfProcessBatchRetObject) platFormService.processBatch(actionName, billType, currNote,
						aggVosTmepList.toArray(new AggregatedValueObject[0]), null, currParam);// 批量审批
			} catch (Exception ex) {
				MessageVO messageVo = new MessageVO(aggVosTmepList.get(0), ActionUtils.AUDIT);
				messageVo.setSuccess(false);
				messageVo.setErrorMessage(ex.getMessage());
				result.add(messageVo);
				continue;
			}
			
			result.addAll(dealRetObject(entry.getValue(), retObject));
		}

		return result;
	}
	/**
	 * 处理批量审批结果
	 * @param auditVOs
	 * @param retObject
	 * @return
	 */
	protected List<MessageVO> dealRetObject(List<AggregatedValueObject> auditVOs, PfProcessBatchRetObject retObject) {
		List<MessageVO> result = new ArrayList<MessageVO>();// 结果

		// 结果处理
		HashMap<Integer, String> errMap = retObject.getExceptionInfo() == null ? null : retObject
				.getExceptionInfo().getErrorMessageMap();

		MessageVO[] resultVos = (MessageVO[]) retObject.getRetObj();
		for (int i = 0; i < auditVOs.size(); i++) {
			String errorMsg = errMap == null ? null : errMap.get(Integer.valueOf(i));
			MessageVO messageVo = null;
			if (!StringUtil.isEmpty(errorMsg)) {
				messageVo = new MessageVO(auditVOs.get(i), ActionUtils.AUDIT);
				messageVo.setSuccess(false);
				messageVo.setErrorMessage(errorMsg);

			} else {
				messageVo = resultVos[i];
			}
			result.add(messageVo);
		}
		return result;
	}
	/**
	 * 根据交易类型将VO分组
	 * @param auditVos
	 * @param tradeTypeKey
	 * @return
	 */
	private Map<String, List<AggregatedValueObject>> getTradeTypeAggVoMap(List<AggregatedValueObject> auditVos) {
		if (auditVos == null || auditVos.size() == 0) {
			return null;
		}

		Map<String, List<AggregatedValueObject>> result = new HashMap<String, List<AggregatedValueObject>>();

		for (AggregatedValueObject auditVo : auditVos) {
			String tradeType = (String) auditVo.getParentVO().getAttributeValue(MatterAppVO.PK_TRADETYPE);
			if (tradeType == null) {
				tradeType = (String) auditVo.getParentVO().getAttributeValue(JKBXHeaderVO.DJLXBM);
				if (tradeType == null) {
					continue;
				}
			}

			if (result.get(tradeType) != null) {
				result.get(tradeType).add(auditVo);
			} else {
				List<AggregatedValueObject> tempAggVoList = new ArrayList<AggregatedValueObject>();
				tempAggVoList.add(auditVo);
				result.put(tradeType, tempAggVoList);
			}
		}
		return result;
	}
	
	// 校验审核信息
	private MessageVO checkVo(JKBXVO bxvo) {
		JKBXHeaderVO head = null;
		head = (bxvo.getParentVO());
		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.AUDIT);
		UFDate djrq = head.getDjrq();
		// begin-- modified by chendya@ufida.com.cn 不比较时分秒，只比较Y-M-D
		if (djrq.afterDate(getBusiDate())) {
			// --end
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000000")/*
																												 * @
																												 * res
																												 * "审核日期不能早于单据录入日期,不能审核"
																												 */);
			return msgVO;
		}
		if (!(head.getSpzt().equals(IBillStatus.CHECKGOING) || head.getSpzt().equals(IBillStatus.COMMIT))) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0008")/*@res "该单据当前状态不能进行审核！"*/);
			return msgVO;
		}
		return msgVO;
	}
	
	private UFDate getBusiDate() {
		long busitime = InvocationInfoProxy.getInstance().getBizDateTime();
		return new UFDate(busitime);
	}
	
	private static String getBatchResults(MessageVO[] messageVos) throws BusinessException {
        if (ArrayUtils.isEmpty(messageVos))
            return "审批出错";
        
        boolean bHasSuccess = false;
        boolean bHasFail = false;
        StringBuilder sbValue = new StringBuilder();
        for (MessageVO messagevo : messageVos) {
            if (messagevo == null)
                continue;
            if (messagevo.isSuccess()) {
                bHasSuccess = true;
            } else {
                bHasFail = true;
                sbValue.append(messagevo.toString()).append("\r\n");
            }
        }
        
        if(sbValue.length() > 0){
        	sbValue.delete(sbValue.length() - 2, sbValue.length());
        }
        String title = null;
		if (bHasSuccess) {
			title = "success";
				//nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getString("2011000_0", null,
					//"02011000-0039", null, new String[] {operationName});/** @* res* "成功！"*/;

			//title = operationName + nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0", "02011000-0039")
		}
        if (!bHasFail && bHasSuccess) {//全部成功
            return title;
        } else {
            return sbValue.toString();
        }
    }
	
	
	protected MessageVO approveSingle(AggregatedValueObject bxvo) throws BusinessException {
		JKBXHeaderVO head = (JKBXHeaderVO)bxvo.getParentVO();
		MessageVO result = null;
		// 审核动作处理
		try{
			String actionType = ErUtil.getApproveActionCode(PK_ORG);
			Object msgReturn = PfUtilPrivate.runAction(null, actionType, head.getDjlxbm(), bxvo, null,
						null, null, null);
//			Object msgReturn = PfUtilPrivate.runAction(null, IPFActionName.APPROVE
//					+ InvocationInfoProxy.getInstance().getUserId(), head.getDjlxbm(), bxvo, null,
//					null, null, null);
			if (msgReturn instanceof MessageVO[]) {
				MessageVO[] msgVos = (MessageVO[]) msgReturn;
				JKBXVO jkbxvo = (JKBXVO) msgVos[0].getSuccessVO();
				jkbxvo.setWarningMsg(null);
				result = msgVos[0];
			} else if (msgReturn instanceof JKBXVO) {// 改签和加签的情况下会出现返回AggVo
				((JKBXVO) msgReturn).setWarningMsg(null);
				result = new MessageVO((JKBXVO) msgReturn, ActionUtils.AUDIT);
			}
		}catch (BusinessException e) {
			ExceptionHandler.consume(e);
			String errMsg = e.getMessage();
			result = new MessageVO(bxvo, ActionUtils.AUDIT, false, errMsg);
		}
		return result;
	}

	public Map<String,Map<String,String>> queryWorkFlow(String billId,String billType) throws BusinessException {
		 //定义返回值map
		 Map<String,Map<String,String>> resultmap = new LinkedHashMap<String,Map<String,String>>();
		 //查找单据
		 IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
  				IBXBillPrivate.class);
  		 List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(new String[]{billId},BXConstans.BX_DJDL);
  		if(list == null || list.size() == 0){
  			Map<String, String> fieldvalueMap = new HashMap<String, String>();
	  		fieldvalueMap.put("flowstatus","该单据已被删除");
		    resultmap.put("111", fieldvalueMap);
		    return resultmap;
  		}
  		 // 找到WFTask所属的流程定义及其父流程定义
  		 int m_iWorkflowtype = ErUtil.getWorkFlowType(list.get(0).getPk_org());
  		 
		  //待查询的字段
		  String[] queryFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "messagenote", "sendername","senddate","checkman"};
		  
		  String[] fromFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "messagenote", "checkname","dealdate","senderman"};
		  
		  String[] toFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "messagenote", "sendername","senddate","checkman"};
			
		  // 找到WFTask所属的流程定义及其父流程定义
	      ProcessRouteRes processRoute = null;
	      IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator
	            .getInstance().lookup(IWorkflowDefine.class.getName());
	      processRoute = wfDefine.queryProcessRoute(billId,
	            billType, null, m_iWorkflowtype);
	      if (processRoute == null || processRoute.getXpdlString() == null){
	         // WARN::说明该单据就没有流程实例,直接查看该单据的状态
//	    	 IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
//	  				IBXBillPrivate.class);
//	  		 List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(new String[]{billId},BXConstans.BX_DJDL);
	  		 Map<String, String> fieldvalueMap = new HashMap<String, String>();
	  		 if(list!=null && list.size()>0){
	  			if(list.get(0).getSpzt().equals(IBillStatus.CHECKPASS))
	  				fieldvalueMap.put("flowstatus","3");
	  			else
	  				fieldvalueMap.put("flowstatus","2");
		    	resultmap.put("111", fieldvalueMap);
	  		 }
	         return resultmap;
	      }

	      // 获取主流程状态
	      String  m_iMainFlowStatus = String.valueOf(processRoute.getProcStatus());
	      // 构造一个临时包
	      XpdlPackage pkg = new XpdlPackage("unknown", "unknown", null);
	      pkg.getExtendedAttributes().put(XPDLNames.MADE_BY, "UFW");
	      String def_xpdl = null;
	      ProcessRouteRes currentRoute = processRoute;
	      if (currentRoute.getXpdlString() != null)
	         def_xpdl = currentRoute.getXpdlString().toString();
	      WorkflowProcess wp = null;
	      try {
	         // 前台解析XML串为对象
	         wp = UfXPDLParser.getInstance().parseProcess(def_xpdl);
	      } catch (XPDLParserException e) {
	         Logger.error(e.getMessage(), e);
	         return resultmap;
	      }
	      wp.setPackage(pkg);
	      List<BasicTransitionEx> transition = wp.getTransitions();
	      Map<String,String> transitionMap = new LinkedHashMap<String, String>();
	      for(int i=0;i<transition.size();i++){
	    	  transitionMap.put(transition.get(i).getTo(), transition.get(i).getFrom());
	      }
	      ActivityInstance[] allActInstances = currentRoute.getActivityInstance();
	      String[] startedActivityDefIds = new String[allActInstances.length];
	      // 当前正运行的活动
	      HashSet hsRunningActs = new HashSet();
	      for (int i = 0; i < allActInstances.length; i++) {
		      //排除掉作废的子流程
		      if(allActInstances[i].getStatus()==WfTaskOrInstanceStatus.Inefficient.getIntValue())
		            continue;
		         startedActivityDefIds[i] = allActInstances[i].getActivityID();
		         if (allActInstances[i].getStatus() == WfTaskOrInstanceStatus.Started.getIntValue())
		            hsRunningActs.add(startedActivityDefIds[i]);
	      }
	      if (startedActivityDefIds == null || startedActivityDefIds.length == 0)
	         return resultmap;

	      //获取具体信息
	      FlowAdminVO adminVO = NCLocator.getInstance().lookup(IPFWorkflowQry.class)
			.queryWorkitemForAdmin(billType, billId, WorkflowTypeEnum.Approveflow.getIntValue());
	      WorkflownoteVO[] noteVOs = adminVO.getWorkflowNotes();
		  if (noteVOs == null || noteVOs.length == 0) {
				return resultmap;
			}
		  
//		  for(int i=0;i<startedActivityDefIds.length;i++){
//			  if(startedActivityDefIds[i]==null)
//				  continue;
			  for(int j=0;j<noteVOs.length;j++){
				  String id = noteVOs[j].getActivityID();
				  String fromid = transitionMap.get(id);
//				  if(id.equals(startedActivityDefIds[i])){
					  if(noteVOs[j].getMessagenote().contains("commitBill")){
					  //从制单中可以提出两种活动
					  //制单活动
					  resultmap.put("commitBill", getApproveFieldMap(m_iMainFlowStatus,queryFields,fromFields,noteVOs[j],true));
					  //审批活动
					  resultmap.put(j+"Bill", getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
					  }
					  else{
						  //其余只能提出一种
						  resultmap.put(j+"Bill", getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
					  }
//				  }
				  }
//		  }
	      return resultmap;
	   }

	Map<String, String> getApproveFieldMap(String m_iMainFlowStatus,String[] queryFields,String[] targetFields,WorkflownoteVO vo,boolean isBillMake){
		Map<String, String> fieldvalueMap = new HashMap<String, String>();
		fieldvalueMap.put("flowstatus",m_iMainFlowStatus);
		Map<String,String> psndocMap = getTelnoByUserid();
		for (int k = 0; k < queryFields.length; k++) {
		    String field = queryFields[k];
				String attributeValue = vo.getAttributeValue(field) == null ? ""
						: vo.getAttributeValue(field).toString();
				if(field.equals("actiontype")){
					if(isBillMake){
						attributeValue="提交";
						fieldvalueMap.put("actionman", "提交人");
					}
					else{
						if(vo.getApprovestatus().intValue()==1)
							attributeValue="通过";
						else if(vo.getApprovestatus().intValue()==0)
							attributeValue="待审批";
						else if(vo.getApprovestatus().intValue()==4)
							attributeValue="作废";
						fieldvalueMap.put("actionman", "审批人");
					}
				}
				
				if(field.equals("checkman")){
					attributeValue = vo.getAttributeValue(targetFields[k]) == null ? ""
							: vo.getAttributeValue(targetFields[k]).toString();
					String telno = psndocMap.get(attributeValue);
					fieldvalueMap.put("telno", telno);
				}
				
				fieldvalueMap.put(targetFields[k], attributeValue);
				
				if(field.equals("dealdate") && fieldvalueMap.get("actiontype").equals("作废")){
					fieldvalueMap.put("dealdate", vo.getSenddate().toLocalString());
				}
		 }
		 return fieldvalueMap;
	}
	
	private Map<String,String> getTelnoByUserid() {
//		String sql = "select mobile from bd_psndoc where pk_psndoc in (select pk_psndoc from sm_user where cuserid='" + userid + "') ";
		String sql = "select cuserid,mobile from bd_psndoc,sm_user where bd_psndoc.pk_psndoc = sm_user.pk_psndoc ";
		
		BaseDAO dao = new BaseDAO();
		final Map<String,String> psndocMap = new HashMap<String, String>();
		try {
			dao.executeQuery(sql, new BaseProcessor(){
				private static final long serialVersionUID = 1L;

				@Override
				public Object processResultSet(ResultSet resultset)
						throws SQLException {
					 while (resultset.next()) {
						String mobile = (String) resultset.getObject(2);
						String cuserid = (String) resultset.getObject(1);
						psndocMap.put(cuserid, mobile);
					}
					return null;
				}
				
			});
		} catch (DAOException e) {
			e.printStackTrace();
		}
		
		return psndocMap;
	}
	
	 /**
	 * 根据条件查询单据
	 * 
	 * @param spzt 0-全部,1-待审批,2-审批中,3-已审批
	 * @param jefw 0-全部,1-0至500,2-500至1000,3-1000以上
	 * @param djlxbm 0-全部,1-差旅费,2-交通费,3-通讯费
	 * @param startdate 起始日期 enddate 结束日期
	 * @param fyxm 费用项目
	 * @return 所有类型单据
	 * @throws BusinessException
	 */
	 public Map<String,Map<String,String>> queryBxdByCond(Map condMap,String userid) throws BusinessException{
		 initEvn(userid);
		 Map<String,List<String>> sqlMap = new HashMap<String,List<String>>();
			StringBuffer sqlWhereSB = new StringBuffer("");
			//审批状态
			sqlWhereSB.append("");
			if("1".equals(condMap.get("spzt"))){
				sqlWhereSB.append( JKBXHeaderVO.SPZT +" in " +"("+IBillStatus.COMMIT + "," + IBillStatus.FREE + ")");
			}else if("2".equals(condMap.get("spzt"))){
				sqlWhereSB.append( JKBXHeaderVO.SPZT +" in " +"("+IBillStatus.CHECKGOING + ")");
			}else if("3".equals(condMap.get("spzt"))){
				sqlWhereSB.append( JKBXHeaderVO.SPZT +" in " +"("+IBillStatus.CHECKPASS + "," + IBillStatus.NOPASS + ")");
			}
			//金额范围 
			String jefw = "";
			if("1".equals(condMap.get("jefw"))){
				if(sqlWhereSB.length() > 0){
					sqlWhereSB.append( " and ");
				}
				jefw = JKBXHeaderVO.TOTAL +" < " + 500 + " ";
				sqlWhereSB.append(jefw);
			}else if("2".equals(condMap.get("jefw"))){
				if(sqlWhereSB.length() > 0){
					sqlWhereSB.append( " and ");
				}
				jefw = JKBXHeaderVO.TOTAL +" >= " + 500 + " and " + JKBXHeaderVO.TOTAL +" < " + 1000 + " ";
				sqlWhereSB.append(jefw);
			}else if("3".equals(condMap.get("jefw"))){
				if(sqlWhereSB.length() > 0){
					sqlWhereSB.append( " and ");
				}
				jefw = JKBXHeaderVO.TOTAL +" >= " + 1000 + " ";
				sqlWhereSB.append(jefw);
			}
			
			
			//单据类型
			if("1".equals(condMap.get("djlxbm"))){
				if(sqlWhereSB.length() > 0){
					sqlWhereSB.append( " and ");
				}
				sqlWhereSB.append( JKBXHeaderVO.DJLXBM +" = '" + 2641 + "' ");
			}else if("2".equals(condMap.get("djlxbm"))){
				if(sqlWhereSB.length() > 0){
					sqlWhereSB.append( " and ");
				}
				sqlWhereSB.append(JKBXHeaderVO.DJLXBM +" = '" + 2642 + "' ");
			}else if("3".equals(condMap.get("djlxbm"))){
				if(sqlWhereSB.length() > 0){
					sqlWhereSB.append( " and ");
				}
				sqlWhereSB.append(JKBXHeaderVO.DJLXBM +" = '" + 2643 + "' ");
			}
			if(sqlWhereSB.length() > 0){
				sqlWhereSB.append( " and ");
			}
			sqlWhereSB.append(JKBXHeaderVO.DJRQ + " >= '" + condMap.get("startdate") + " 00:00:00' ");
			sqlWhereSB.append(" and " + JKBXHeaderVO.DJRQ + " <= '" + condMap.get("enddate") + " 59:59:59' ");

			//收支项目
			if(!StringUtil.isEmpty(condMap.get("pk_inoutbusiclass").toString())){
				sqlWhereSB.append(" and " + JKBXHeaderVO.SZXMID + " = '" + condMap.get("pk_inoutbusiclass").toString() + "' ");
			}  
			
			//待查询的字段
			String[] queryFields = new String[] { JKBXHeaderVO.PK_JKBX,
					JKBXHeaderVO.YBJE,JKBXHeaderVO.TOTAL,
					JKBXHeaderVO.DJRQ, JKBXHeaderVO.DJBH, JKBXHeaderVO.ZY, JKBXHeaderVO.JKBXR,JKBXHeaderVO.DJLXBM,
					JKBXHeaderVO.DJDL,JKBXHeaderVO.SPZT};
			
			String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryPKsByWhereForBillNode(sqlWhereSB.toString(),InvocationInfoProxy.getInstance().getUserId()
					,BXConstans.BX_DJDL);
			
			IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
					IBXBillPrivate.class);
			List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(pks,
					BXConstans.BX_DJDL);
			
			
			
			Map<String, Map<String, String>> resultmap = new LinkedHashMap<String, Map<String, String>>();
			if (list != null && !list.isEmpty()) {
				for (JKBXHeaderVO vo : list) {
					Map<String, String> fieldvalueMap = new HashMap<String, String>();
					for (int i = 0; i < queryFields.length; i++) {
						String field = queryFields[i];
						String attributeValue = vo.getAttributeValue(field) == null ? ""
								: vo.getAttributeValue(field).toString();
						fieldvalueMap.put(field, attributeValue);
						if(JKBXHeaderVO.DJRQ.equals(field)){
							fieldvalueMap.put(field, new UFDate(attributeValue).toLocalString());
						}else if(JKBXHeaderVO.TOTAL.equals(field)){
							UFDouble total = new UFDouble(attributeValue);
							total = total.setScale(2,4);
							fieldvalueMap.put(field, total.toString());
						}if(JKBXHeaderVO.SPZT.equals(field)){
							String sprshow = getSprShow(vo.getSpzt());
							String spztshow = getSpztShow(vo.getSpzt());
							fieldvalueMap.put("sprshow", sprshow);
							fieldvalueMap.put("spztshow", spztshow);
						}if(JKBXHeaderVO.ZY.equals(field) && attributeValue.length()>10){
							attributeValue = attributeValue.substring(0,5)+"...";
							fieldvalueMap.put(field, attributeValue);
						} 
						
					}
					String djlxmc = PfDataCache.getBillTypeNameByCode(vo.getDjlxbm());
					if(djlxmc.endsWith("报销单"))
						djlxmc = djlxmc.substring(0, djlxmc.length()-3);
					fieldvalueMap.put("djlxmc", djlxmc);
					resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
				}
			} 
			
			// 补充查询审批进行中的报销单的当前审批人
			if(!resultmap.isEmpty()){
				Map<String, String> checkmanMap = getBillCheckman(resultmap.keySet().toArray(new String[0]),defaultDjlxbm);
				for (Entry<String, String> checkmans : checkmanMap.entrySet()) {
					Map<String, String> map = resultmap.get(checkmans.getKey());
					// 将审批状态替换为当前审批人
					String spr = checkmans.getValue();
					if(spr!=null && !spr.equals("") && spr.contains(","))
						spr = spr.split(",")[0];
					map.put("sprshow", "待("+spr+")审批");
				}
			}
			
			return resultmap;
	 }

	//如果成功返回success，如果出错则返回出错提示
	public String unAuditbxd(String pkJkbx,String userid) throws BusinessException {
		initEvn(userid);
		List<JKBXVO> auditVOs = NCLocator.getInstance().lookup(IBXBillPrivate.class)
	  	.queryVOsByPrimaryKeysForNewNode(new String[]{pkJkbx}, BXConstans.BX_DJDL,false,null);
		if(auditVOs==null || auditVOs.size()==0)
			return "单据已删除";
		JKBXVO bxvo = auditVOs.get(0);
		MessageVO result = checkUnShenhe(bxvo);
		if (!result.isSuccess()) {
			return result.getErrorMessage();
		}
		JKBXHeaderVO head = bxvo.getParentVO();
		boolean retflag = true;
		String errMsg  = "";
		try{
			String actionType = ErUtil.getUnApproveActionCode(PK_ORG);
			Object msgReturn = PfUtilPrivate.runAction(null, actionType, head.getDjlxbm(), bxvo, null,
						null, null, null);
//			Object msgReturn = PfUtilPrivate.runAction(null, IPFActionName.UNAPPROVE
//					+ InvocationInfoProxy.getInstance().getUserId(), head.getDjlxbm(), bxvo, null,
//					null, null, null);
			if (msgReturn instanceof MessageVO[]) {
				MessageVO[] msgVos = (MessageVO[]) msgReturn;
				result = msgVos[0];
			} else if (msgReturn instanceof JKBXVO) {
				result = new MessageVO((JKBXVO) msgReturn, ActionUtils.AUDIT);
			}
		}catch (BusinessException e) {
			ExceptionHandler.consume(e);
			errMsg = e.getMessage();
			retflag = false;
		}
		if(retflag){
			return "success";
		}else{
			return errMsg;
		}
	}

	/**
	 * @param bxvo
	 * @param background
	 * @return
	 * 
	 *         校验反审核信息
	 */
	MessageVO checkUnShenhe(JKBXVO bxvo) {

		// 单据表头VO
		JKBXHeaderVO head = bxvo.getParentVO();

		// 较验返回的信息
		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.UNAUDIT);

		if (null != head.getQcbz() && head.getQcbz().booleanValue()) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-000091"))/*
																													 * @
																													 * res
																													 * "期初单据不能反审核"
																													 */;
			return msgVO;
		}

		return msgVO;
	}
	
	public String get_unapproved_bxdcount(String userid) throws BusinessException {
		initEvn(userid); 
		//联合审批流查询主键信息 AND wf.approvestatus!=4
//		  String bxSql = "select distinct pk_jkbx FROM er_bxzb er_bxzb , pub_workflownote wf where er_bxzb.pk_jkbx=wf.billid(+)"
//          + " AND ((wf.billid is null and er_bxzb.approver='"
//		+ userid
//          + "') or (wf.ischeck='Y' and wf.checkman= '"
//          + userid
//          + "' AND ( wf.dr=0 or isnull(wf.dr,0)=0 )  AND wf.actiontype = 'Z'))"
//          +" AND er_bxzb.spzt in (2,3) and er_bxzb.DR ='0'"
//          +" AND  er_bxzb.djlxbm in ('2641','2642','2643')  and er_bxzb.QCBZ='N'";
//		 String bxSql = "select distinct pk_jkbx FROM er_bxzb er_bxzb , pub_workflownote wf where er_bxzb.pk_jkbx=wf.billid(+)"
//	            + " AND (wf.billid is null or (wf.ischeck='N' AND wf.approvestatus!=4 and wf.checkman= '"
//	            + userid
//	            + "' AND ( wf.dr=0 or isnull(wf.dr,0)=0 )  AND wf.actiontype = 'Z'))"
//	            +" AND er_bxzb.djzt = 1 AND er_bxzb.spzt in (2,3) and er_bxzb.DR ='0'"
//	            +" AND  er_bxzb.djlxbm in ('2641','2642','2643')  and er_bxzb.QCBZ='N'";
//		  BaseDAO dao = new BaseDAO();
//		  List<String> pks = new ArrayList<String>();
//		  BaseProcessor baseprocessor = new BaseProcessor(){
//	         private static final long serialVersionUID = 1L;
//			@Override
//			public Object processResultSet(ResultSet rs) throws SQLException {
//				List<String> result = new ArrayList<String>();
//              while (rs.next()) {
//                  result.add(rs.getObject(1).toString());
//              }
//              return result;
//			}
//	      };
//	    pks = (ArrayList<String>)dao.executeQuery(bxSql,baseprocessor);
		
		
//		String sqlWhere = "QCBZ='N' and DR ='0'";
//		  String[] djlxbm = {"2643","2642","2641"};
//		  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbm, false);
//		  String[] billPks = NCLocator.getInstance().lookup(IErmBsCommonService.class)
//			.queryApprovedWFBillPksByCondition(null, djlxbm, false);
//		  if (billPks != null && billPks.length > 0) {
//			  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, false);
//		  } else {
//			  sqlWhere += " and 1=0 ";
//		  }
//		  String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class)
//			.queryPKsByWhereForBillManageNode(sqlWhere, InvocationInfoProxy.getInstance().getGroupId(), 
//					InvocationInfoProxy.getInstance().getUserId());
		 String sqlWhere = "QCBZ='N' and DR ='0'"; 
		 String[] djlxbmarray = getDjlxbmArray();//{"2643","2642","2641"};
		  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.DJLXBM, djlxbmarray, false);
		  String[] billPks = queryApprovedWFBillPksByCondition(userid, djlxbmarray, false);
		  if (billPks != null && billPks.length > 0) {
			  sqlWhere += " and " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, billPks, false);
		  } else { 
			  sqlWhere += " and 1=0 ";
		  }
		  String[] pks = NCLocator.getInstance().lookup(IBXBillPrivate.class)
			.queryPKsByWhereForBillManageNode(sqlWhere, InvocationInfoProxy.getInstance().getGroupId(), 
					InvocationInfoProxy.getInstance().getUserId());
		return pks==null?"0":String.valueOf(pks.length);
	}

	
	public String[] queryApprovedWFBillPksByCondition(String pk_user, String[] tradeTypes, boolean isApproved)
			throws BusinessException {
		StringBuffer sqlBuf = new StringBuffer(
				"select distinct billid from pub_workflownote where checkman = ?  and  actiontype = ? ");

		SQLParameter sqlparams = new SQLParameter();
		if (pk_user == null) {// 用户
			pk_user = AuditInfoUtil.getCurrentUser();
		}
		sqlparams.addParam(pk_user);
		sqlparams.addParam("Z");//actiontype为Z时，表示为审批

		if (isApproved) {//是否已审批
			sqlBuf.append(" and approvestatus = 1 ");
		} else {//待我审批
			sqlBuf.append(" and ischeck = 'N' ");
		}

		if (tradeTypes != null && tradeTypes.length > 0) {//交易类型
			sqlBuf.append(" and " + SqlUtils.getInStr("pk_billtype", tradeTypes, true));
		}

		@SuppressWarnings("unchecked")
		List<String> result = (List<String>) new BaseDAO().executeQuery(sqlBuf.toString(), sqlparams,
				new ResultSetProcessor() {
					private static final long serialVersionUID = 1L;

					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {
						List<String> result = new ArrayList<String>();
						while (rs.next()) {
							result.add(rs.getString(1));
						}
						return result;
					}

				});

		if (result != null && result.size() > 0) {
			return result.toArray(new String[] {});
		}

		return null;
	}
}

