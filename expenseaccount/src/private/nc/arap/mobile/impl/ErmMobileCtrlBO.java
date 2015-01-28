package nc.arap.mobile.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.erm.util.ErUtil;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.pf.pub.PfDataCache;
import nc.erm.mobile.util.AuditListUtil;
import nc.erm.mobile.util.BillTypeUtil;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IBXBillPublic;
import nc.itf.uap.pf.IPFWorkflowQry;
import nc.itf.uap.pf.IWorkflowDefine;
import nc.itf.uap.pf.IplatFormEntry;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.processor.BaseProcessor;
import nc.jdbc.framework.processor.ResultSetProcessor;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.bd.inoutbusiclass.InoutBusiClassVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.reimtype.ReimTypeVO;
import nc.vo.erm.accruedexpense.AccruedVO;
import nc.vo.erm.accruedexpense.AggAccruedBillVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.matterapp.AggMatterAppVO;
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
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.pub.workflownote.WorkflownoteVO;
import nc.vo.pub.workflowqry.FlowAdminVO;
import nc.vo.trade.pub.IBillStatus;
import nc.vo.uap.pf.PfProcessBatchRetObject;
import nc.vo.uap.wfmonitor.ProcessRouteRes;
import nc.vo.util.AuditInfoUtil;
import nc.vo.wfengine.core.parser.XPDLNames;
import nc.vo.wfengine.definition.WorkflowTypeEnum;
import nc.vo.wfengine.pub.WfTaskOrInstanceStatus;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import uap.pub.bap.fs.FileStorageClient;
import uap.pub.bap.fs.IFileTransfer;
import uap.pub.bap.fs.domain.FileHeader;
import uap.pub.bap.fs.domain.ext.BaFileStoreExt;


public class ErmMobileCtrlBO extends AbstractErmMobileCtrlBO{
	
	/** 
	 * Ĭ�ϵ�������
	 */
	
	public ErmMobileCtrlBO() {
		super();
	}
	
	public Map<String, Map<String, String>> queryBXHeads(String userid,String querydate)
	throws BusinessException {
		// Ĭ�ϲ�ѯ��ǰ�û�querydate��0 ���գ�1���ܣ�2���£�����д�ĵ���
		UFDate currentdate = new UFDate();
		String sqlWhere = "";
		if("0".equals(querydate)){
			sqlWhere = "(djrq >= '"+currentdate.asBegin()+"' and djrq <= '"+currentdate.asEnd()+"')";
		}else if("1".equals(querydate)){
			sqlWhere = getWeekDay();
		}else if("2".equals(querydate)){
			sqlWhere = getMonthDate();
		}else if("3".equals(querydate)){
			// ��ȡCalendar
			Calendar calendar = Calendar.getInstance();
			// ����������������Ϊ�����������
			calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
			UFDate enddate = new UFDate(calendar.getTime());
			// ��ʼ���������·�-2,��������Ϊ1
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
		String sqlWhere = "djlxbm = '2641' and "+sql;
		initEvn(userid);
		//����ѯ���ֶ�
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
//						String spztshow = getSpztShow(vo.getSpzt());
//						fieldvalueMap.put("spztshow", spztshow);
					}
					
				}
				resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
			}
		} 		
		return resultmap;
	}
	
	/**
	 * �ҵ�--δ���
	 * ��ѯ��ǰ�û�ȫ��δ����ͨ���ı�����
	 * 
	 * @param userid
	 * @param flag ����״̬��־ 0-δ��� 1-����� 
	 * @return �����������Ʒ���ı���������ֵ
	 * @throws BusinessException
	 */
	public Map<String, List<Map<String, String>>> getBXHeadsByUser(String userid,String flag,String billtype,String startline,String pagesize,String pkars)
	throws BusinessException {
		initEvn(userid);
		MobileBo bo = new MobileBo();
		if("1".equals(startline) && pkars == null){
			//�״β�ѯ����pk
			pkars = bo.queryPksByUser(userid,flag,billtype);
		}
		return bo.getResultMapByDate(billtype,startline,pagesize,pkars);
	}
	
	
	//��ѯ��ǰ�û�����������,map���ձ������������������뵥��Ԥ�ᵥ���з���
	public Map<String,List<Map<String, String>>> getBXApprovingBillByUser(String userid,String billtype)
			throws BusinessException {
		initEvn(userid);
		Map<String,List<Map<String, String>>> resultlistmap = new LinkedHashMap<String,List<Map<String, String>>>();
		AuditListUtil auditlist = new AuditListUtil();
		//��ѯ�������ı�����
		List<Map<String, String>> bxlist = auditlist.getAuditBillListByUser(BillTypeUtil.BX, false);
		if(bxlist != null && bxlist.size()>0)
			resultlistmap.put("������", bxlist);
		//��ѯ�������ı�����
		List<Map<String, String>> jklist = auditlist.getAuditBillListByUser(BillTypeUtil.JK, false);
		if(jklist != null && jklist.size()>0)
			resultlistmap.put("��", jklist);
		//��ѯ�������ı�����
		List<Map<String, String>> shengqinglist = auditlist.getAuditBillListByUser(BillTypeUtil.MA, false);
		if(shengqinglist != null && shengqinglist.size()>0)
			resultlistmap.put("�������뵥", shengqinglist);
		//��ѯ�������ı�����
		List<Map<String, String>> yutilist = auditlist.getAuditBillListByUser(BillTypeUtil.AC, false);
		if(yutilist != null && yutilist.size()>0)
			resultlistmap.put("Ԥ�ᵥ", yutilist);
		
	    return resultlistmap;
	}
	
	//��ѯ��ǰ�û�����������,map���ձ��ܡ����ܡ�������з���
	public Map<String,List<Map<String, String>>> getBXApprovedBillByUser(String userid,String billtype,String startline,String pagesize,String pks)
			throws BusinessException {
		initEvn(userid);
		MobileBo bo = new MobileBo();
		if("1".equals(startline) && pks == null){
			//�״β�ѯ����pk
			AuditListUtil util = new AuditListUtil();
			String[] pkars = util.queryAuditPksByUser(userid,billtype,true);
			StringBuffer pkstrs = new StringBuffer();
			if(pkars != null){
				for(int i=0;i<pkars.length;i++){
					pkstrs.append(pkars[i]).append(",");
				}
			}
			pks = pkstrs.toString();
		}
		return bo.getResultMapByDate(billtype,startline,pagesize,pks);
	}
	
	
	public String deleteJkbx(String headpk,String userid) throws BusinessException {
		initEvn(userid);
		try{ 
			// ��ѯ
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
				throw new BusinessException("�����ѱ�ɾ��������"); 
			}
			JKBXVO vo = vos.get(0);
			if(vo.getParentVO() != null ){
				JKBXHeaderVO head = vo.getParentVO();
				if (head.getSpzt() != null && (head.getSpzt().equals(IPfRetCheckInfo.GOINGON) )) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000398"))/*
																										 * @
																										 * res
																										 * "������������У�����ɾ����"
																										 */;
				}
				if(head.getSpzt() != null && head.getSpzt().equals(IPfRetCheckInfo.COMMIT)){
					//�ջ��ύ
					initEvn(head.getOperator());
					//�ջ��ύ
					String actionType = getActionCode(IPFActionName.UNSAVE,PK_ORG);
//					String checkNote = approveInfo.getChecknote();
//					switch(approveInfo.getOperationType()){
//					case 1:
//						//��׼
//						besideContext.setApproveResult("Y");
//						if (StringUtil.isEmpty(checkNote)) {
//					      checkNote = "��׼";
//					    }
//						besideContext.setCheckNote(checkNote);
//						break;
//					case 2:
//						//����
//						besideContext.setApproveResult("R");
//						if (StringUtil.isEmpty(checkNote)) {
//					      checkNote = "����";
//					    }
//						besideContext.setCheckNote(checkNote);
//						besideContext.setBackToFirstActivity(true);
//						besideContext.setTaskType(WfTaskType.Backward.getIntValue());
//						Activity rejectActivity = null;
//					    
//					    if (rejectActivity == null) {
//					    	besideContext.setBackToFirstActivity(true);
//					    } else {
//					    	besideContext.setBackToFirstActivity(false);
//					    }
//					    besideContext.setJumpToActivity(rejectActivity == null ? null : rejectActivity.getId());
//						break;
//					case 3:
//						//��ǩ
//						besideContext.setAddAssign(true);
//						break;
//					}
					PfUtilPrivate.runAction(actionType, head.getDjlxbm(), vo, null,
							  null, null, null);
//					PfUtilPrivate.runAction(null, IPFActionName.UNSAVE
//							+ InvocationInfoProxy.getInstance().getUserId(), head.getDjlxbm(), vo, null,
//							null, null, null); 
				}
				if (head.getSpzt() != null && head.getSpzt().equals(IPfRetCheckInfo.NOPASS)) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000914"))/*
																										 * @
																										 * res
																										 * "����δͨ���ĵ��ݲ�����ɾ����"
																										 */;
				}
				//ehp2�� �����ݹ��ĵ����ǲ�����ɾ����
				if(head.getVouchertag()!= null && head.getVouchertag()==BXStatusConst.ZGDeal){
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000953"))/*
					 * @
					 * res
					 * "�����ݹ�ƾ֤�ĵ��ݣ�������ɾ����"
					 */;
	
				}
			}
			// ִ��ɾ��
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
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY); // ��ȡ����һ������
		cal.set(Calendar.DAY_OF_MONTH, Calendar.DATE); // ��ȡ����һ������

		UFDate begindate = new UFDate(cal.getTime());
		// ������������ϸ��������յ����ڣ���Ϊ�����Ǳ߰����յ��ɵ�һ��
		cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		// ����һ�����ڣ����������й������ı����յ�����
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		UFDate enddate = new UFDate(cal.getTime());
		return "(djrq >= '"+begindate.asBegin()+"' and djrq <= '"+enddate.asEnd()+"')";
	}

	public static String getMonthDate() {
		// ��ȡCalendar
		Calendar calendar = Calendar.getInstance();
		// ����ʱ��,��ǰʱ�䲻������
		// calendar.setTime(new Date());
		calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
		UFDate begindate = new UFDate(calendar.getTime());
		// ��������Ϊ�����������
		calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
		UFDate enddate = new UFDate(calendar.getTime());
		
		return "(djrq >= '"+begindate.asBegin()+"' and djrq <= '"+enddate.asEnd()+"')";
	}

	
	@SuppressWarnings("unchecked")
	public Map<String, Map<String, String>> queryReimType(String userid) throws BusinessException {
		// Ĭ�ϲ�ѯ��ǰ���ţ�δͣ�õı�������
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
		// Ĭ�ϲ�ѯ��ǰ������֧��Ŀ
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
		  // ��ѯ
		  List<JKBXVO> vos = NCLocator.getInstance().
		    lookup(IBXBillPrivate.class).queryVOsByPrimaryKeys(new String[]{headpk}, null);
		  if(vos == null || vos.isEmpty()){
			  throw new BusinessException("�����ѱ�ɾ��������");
		  }
		  JKBXVO jkbxvo = vos.get(0);
		  // ִ���ύ
		  String actionType = getActionCode(IPFActionName.SAVE,PK_ORG);
		  PfUtilPrivate.runAction(actionType, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
					null, null, null);
//		  PfUtilPrivate.runAction(null, IPFActionName.SAVE+ userid, jkbxvo.getParentVO().getDjlxbm(), jkbxvo, null,
//					null, null, null);
		  return "true";
	}
	 
	
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
	
	//��������//����ɹ�����success����������򷵻س�����ʾ
	public String auditBXBillByPKs(String[] pks,String userid,String checknote,String ischeck) throws Exception {
		//��ʼ������
		initEvn(userid);
		
		// ����pk������ݣ����Ե��ݽ�����˽�����Ϣ
		List<JKBXVO> vos = NCLocator.getInstance().lookup(IBXBillPrivate.class)
	  	.queryVOsByPrimaryKeysForNewNode(pks, BXConstans.BX_DJDL,false,null);
		if(vos == null || vos.size()==0)
			return "������ɾ��";
		int count = vos.size();
		MessageVO[]	msgs = new MessageVO[count];
		List<AggregatedValueObject> auditVOs = new ArrayList<AggregatedValueObject>();
		JKBXVO[] jkbxVos = vos.toArray(new JKBXVO[0]);
		for (int i = 0; i < jkbxVos.length; i++) {
			JKBXVO vo = jkbxVos[i];
			// ���������ȫ�����������������֧��״̬Ӧ������Ϊ��ȫ������
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
			// ���������������
			if (auditVOs.size() > 1) {// ���������������
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
				MessageVO[] returnMsgs = new MessageVO[] { approveSingle(auditVOs.get(0),checknote) };
				return getBatchResults(returnMsgs);
			}
		} else {
			//���ݶ�û��ͨ��checkVo�����
			return getBatchResults(msgs);
		}
	}
	
	protected MessageVO[] executeBatchAudit(List<AggregatedValueObject> auditVOs) throws Exception, BusinessException {
		// ��˶�������
		List<MessageVO> result = null;
		Map<String, List<AggregatedValueObject>> typeMap = getTradeTypeAggVoMap(auditVOs);
		result = executeBatchManagerNodeAudit(typeMap);
		return result.toArray(new MessageVO[0]);
	}
	
	/**
	 * ����ڵ��������� ����ڵ���ڶཻ�����͵��������Ҫ���⴦��
	 * 
	 * @param auditVOs
	 * @param result
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	protected List<MessageVO> executeBatchManagerNodeAudit(Map<String, List<AggregatedValueObject>> typeMap) throws BusinessException {
		List<AggregatedValueObject> auditVOs = new ArrayList<AggregatedValueObject>();//���е���
		for (Map.Entry<String, List<AggregatedValueObject>> entry : typeMap.entrySet()) {
			auditVOs.addAll(entry.getValue());
		}
		
		List<MessageVO> result = new ArrayList<MessageVO>();//���
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

		// ����������, ����WorkflownoteVO��
		WorkflownoteVO noteVO = new WorkflownoteVO();
//		try {// CAǩ��
//			boolean isNeedCASign = NCLocator.getInstance().lookup(IPFWorkflowQry.class)
//					.isNeedCASign4Batch(InvocationInfoProxy.getInstance().getUserId(), typeMap.keySet().toArray(new String[0]), billIds);
//			if (isNeedCASign) {
//				noteVO.getRelaProperties().put(XPDLNames.ELECSIGNATURE, "true");
//			}
//		} catch (BusinessException e2) {
//			ExceptionHandler.handleException(e2);
//		}

//		BatchApproveWorkitemAcceptDlg dlg = new BatchApproveWorkitemAcceptDlg(null, noteVO);
//		dlg.setBachApproveMode(batchApproveMode);
		// Ĭ��Ϊ��׼
		noteVO.setChecknote("��׼");
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

				String actionType = ErUtil.getApproveActionCode(PK_ORG);
				String actionName = actionType + InvocationInfoProxy.getInstance().getUserId();
				IplatFormEntry platFormService = NCLocator.getInstance().lookup(IplatFormEntry.class);

				retObject = (PfProcessBatchRetObject) platFormService.processBatch(actionName, billType, currNote,
						aggVosTmepList.toArray(new AggregatedValueObject[0]), null, currParam);// ��������
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
	 * ���������������
	 * @param auditVOs
	 * @param retObject
	 * @return
	 */
	protected List<MessageVO> dealRetObject(List<AggregatedValueObject> auditVOs, PfProcessBatchRetObject retObject) {
		List<MessageVO> result = new ArrayList<MessageVO>();// ���

		// �������
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
	 * ���ݽ������ͽ�VO����
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
	
	// У�������Ϣ
	private MessageVO checkVo(JKBXVO bxvo) {
		JKBXHeaderVO head = null;
		head = (bxvo.getParentVO());
		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.AUDIT);
		UFDate djrq = head.getDjrq();
		// begin-- modified by chendya@ufida.com.cn ���Ƚ�ʱ���룬ֻ�Ƚ�Y-M-D
		if (djrq.afterDate(getBusiDate())) {
			// --end
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000000")/*
																												 * @
																												 * res
																												 * "������ڲ������ڵ���¼������,�������"
																												 */);
			return msgVO;
		}
		if (!(head.getSpzt().equals(IBillStatus.CHECKGOING) || head.getSpzt().equals(IBillStatus.COMMIT))) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0008")/*@res "�õ��ݵ�ǰ״̬���ܽ�����ˣ�"*/);
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
            return "��������";
        
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
					//"02011000-0039", null, new String[] {operationName});/** @* res* "�ɹ���"*/;

			//title = operationName + nc.ui.ml.NCLangRes.getInstance().getStrByID("2011000_0", "02011000-0039")
		}
        if (!bHasFail && bHasSuccess) {//ȫ���ɹ�
            return title;
        } else {
            return sbValue.toString();
        }
    }
	
	
	protected MessageVO approveSingle(AggregatedValueObject bxvo,String checknote) throws BusinessException {
		JKBXHeaderVO head = (JKBXHeaderVO)bxvo.getParentVO();
		MessageVO result = null;
		// ��˶�������
		try{
			String actionType = ErUtil.getApproveActionCode(PK_ORG);
			BesideApproveContext besideContext = new BesideApproveContext();
			besideContext.setApproveResult("Y");
			besideContext.setCheckNote(checknote);
			Object msgReturn = PfUtilPrivate.runAction(actionType, head.getDjlxbm(), bxvo, null,
					besideContext, null, null);
//			Object msgReturn = PfUtilPrivate.runAction(null, IPFActionName.APPROVE
//					+ InvocationInfoProxy.getInstance().getUserId(), head.getDjlxbm(), bxvo, null,
//					null, null, null);
			if (msgReturn instanceof MessageVO[]) {
				MessageVO[] msgVos = (MessageVO[]) msgReturn;
				JKBXVO jkbxvo = (JKBXVO) msgVos[0].getSuccessVO();
				jkbxvo.setWarningMsg(null);
				result = msgVos[0];
			} else if (msgReturn instanceof JKBXVO) {// ��ǩ�ͼ�ǩ������»���ַ���AggVo
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
		 //���巵��ֵmap
		 Map<String,Map<String,String>> resultmap = new LinkedHashMap<String,Map<String,String>>();
		 //���ҵ���
		 IBXBillPrivate queryservice = NCLocator.getInstance().lookup(
  				IBXBillPrivate.class);
  		 List<JKBXHeaderVO> list = queryservice.queryHeadersByPrimaryKeys(new String[]{billId},BXConstans.BX_DJDL);
  		if(list == null || list.size() == 0){
  			Map<String, String> fieldvalueMap = new HashMap<String, String>();
	  		fieldvalueMap.put("flowstatus","�õ����ѱ�ɾ��");
		    resultmap.put("111", fieldvalueMap);
		    return resultmap;
  		}
  		 // �ҵ�WFTask���������̶��弰�丸���̶���
  		 int m_iWorkflowtype = ErUtil.getWorkFlowType(list.get(0).getPk_org());
  		 
  		  //����ѯ���ֶ�
		  String[] queryFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "sendername","senddate","checkman"};//"messagenote",
		  //��ת�����ֶ�
		  String[] fromFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "checkname","dealdate","checkman"};
		  //ת������ֶ�
		  String[] toFields = new String[] {"actiontype","checknote","checkname",
					"checkname", "dealdate", "sendername","senddate","checkman"};
			
		  // �ҵ�WFTask���������̶��弰�丸���̶���
	      ProcessRouteRes processRoute = null;
	      IWorkflowDefine wfDefine = (IWorkflowDefine) NCLocator
	            .getInstance().lookup(IWorkflowDefine.class.getName());
	      processRoute = wfDefine.queryProcessRoute(billId,
	            billType, null, m_iWorkflowtype);
	      if (processRoute == null || processRoute.getXpdlString() == null){
	         // WARN::˵���õ��ݾ�û������ʵ��,ֱ�Ӳ鿴�õ��ݵ�״̬
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

	      // ��ȡ������״̬
	      String  m_iMainFlowStatus = String.valueOf(processRoute.getProcStatus());
	      // ����һ����ʱ��
//	      XpdlPackage pkg = new XpdlPackage("unknown", "unknown", null);
//	      pkg.getExtendedAttributes().put(XPDLNames.MADE_BY, "UFW");
//	      String def_xpdl = null;
//	      ProcessRouteRes currentRoute = processRoute;
//	      if (currentRoute.getXpdlString() != null)
//	         def_xpdl = currentRoute.getXpdlString().toString();
//	      WorkflowProcess wp = null;
//	      try {
//	         // ǰ̨����XML��Ϊ����
//	         wp = UfXPDLParser.getInstance().parseProcess(def_xpdl);
//	      } catch (XPDLParserException e) {
//	         Logger.error(e.getMessage(), e);
//	         return resultmap;
//	      }
//	      wp.setPackage(pkg);
//	      List<BasicTransitionEx> transition = wp.getTransitions();
//	      Map<String,String> transitionMap = new LinkedHashMap<String, String>();
//	      for(int i=0;i<transition.size();i++){
//	    	  transitionMap.put(transition.get(i).getTo(), transition.get(i).getFrom());
//	      }
//	      ActivityInstance[] allActInstances = currentRoute.getActivityInstance();
//	      String[] startedActivityDefIds = new String[allActInstances.length];
//	      // ��ǰ�����еĻ
//	      HashSet hsRunningActs = new HashSet();
//	      for (int i = 0; i < allActInstances.length; i++) {
//		      //�ų������ϵ�������
//		      if(allActInstances[i].getStatus()==WfTaskOrInstanceStatus.Inefficient.getIntValue())
//		            continue;
//		         startedActivityDefIds[i] = allActInstances[i].getActivityID();
//		         if (allActInstances[i].getStatus() == WfTaskOrInstanceStatus.Started.getIntValue())
//		            hsRunningActs.add(startedActivityDefIds[i]);
//	      }
//	      if (startedActivityDefIds == null || startedActivityDefIds.length == 0)
//	         return resultmap;

	      //��ȡ������Ϣ
	      FlowAdminVO adminVO = NCLocator.getInstance().lookup(IPFWorkflowQry.class)
			.queryWorkitemForAdmin(billType, billId, WorkflowTypeEnum.Approveflow.getIntValue());
	      WorkflownoteVO[] noteVOs = adminVO.getWorkflowNotes();
		  if (noteVOs == null || noteVOs.length == 0) {
				return resultmap;
		  }
		  
		  for(int j=0;j<noteVOs.length;j++){
			  if(noteVOs[j].getMessagenote().contains("commitBill") || noteVOs[j].getMessagenote().contains("startworkflow")){
				  //���Ƶ��п���������ֻ
				  //�Ƶ��
				  resultmap.put("commitBill", getApproveFieldMap(m_iMainFlowStatus,queryFields,fromFields,noteVOs[j],true));
				  //�����
				  resultmap.put(j+"Bill", getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
			  }
			  else{
				  //����ֻ�����һ��
				  resultmap.put(j+"Bill", getApproveFieldMap(m_iMainFlowStatus,queryFields,toFields,noteVOs[j],false));
			  }
		 }
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
						attributeValue="�ύ";
						fieldvalueMap.put("actionman", "�ύ��");
					}
					else{
						if(vo.getApprovestatus().intValue()==1)
							attributeValue="ͨ��";
						else if(vo.getApprovestatus().intValue()==0)
							attributeValue="������";
						else if(vo.getApprovestatus().intValue()==4)
							attributeValue="����";
						fieldvalueMap.put("actionman", "������");
					}
				}
				
				if(field.equals("checkman")){
					attributeValue = vo.getAttributeValue(targetFields[k]) == null ? ""
							: vo.getAttributeValue(targetFields[k]).toString();
					String telno = psndocMap.get(attributeValue);
					fieldvalueMap.put("telno", telno);
				}
				
				fieldvalueMap.put(targetFields[k], attributeValue);
				
				if(field.equals("dealdate") && fieldvalueMap.get("actiontype").equals("����")){
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
	 * ����������ѯ����
	 * 
	 * @param spzt 0-ȫ��,1-������,2-������,3-������
	 * @param jefw 0-ȫ��,1-0��500,2-500��1000,3-1000����
	 * @param djlxbm 0-ȫ��,1-���÷�,2-��ͨ��,3-ͨѶ��
	 * @param startdate ��ʼ���� enddate ��������
	 * @param fyxm ������Ŀ
	 * @return �������͵���
	 * @throws BusinessException
	 */
	 public Map<String,Map<String,String>> queryBxdByCond(Map condMap,String userid) throws BusinessException{
		 initEvn(userid);
		 Map<String,List<String>> sqlMap = new HashMap<String,List<String>>();
			StringBuffer sqlWhereSB = new StringBuffer("");
			//����״̬
			sqlWhereSB.append("");
			if("1".equals(condMap.get("spzt"))){
				sqlWhereSB.append( JKBXHeaderVO.SPZT +" in " +"("+IBillStatus.COMMIT + "," + IBillStatus.FREE + ")");
			}else if("2".equals(condMap.get("spzt"))){
				sqlWhereSB.append( JKBXHeaderVO.SPZT +" in " +"("+IBillStatus.CHECKGOING + ")");
			}else if("3".equals(condMap.get("spzt"))){
				sqlWhereSB.append( JKBXHeaderVO.SPZT +" in " +"("+IBillStatus.CHECKPASS + "," + IBillStatus.NOPASS + ")");
			}
			//��Χ 
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
			
			
			//��������
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

			//��֧��Ŀ
			if(!StringUtil.isEmpty(condMap.get("pk_inoutbusiclass").toString())){
				sqlWhereSB.append(" and " + JKBXHeaderVO.SZXMID + " = '" + condMap.get("pk_inoutbusiclass").toString() + "' ");
			}  
			
			//����ѯ���ֶ�
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
							String sprshow = AuditListUtil.getSprShow(vo.getSpzt());
							String spztshow = AuditListUtil.getSpztShow(vo.getSpzt());
							fieldvalueMap.put("sprshow", sprshow);
							fieldvalueMap.put("spztshow", spztshow);
						}if(JKBXHeaderVO.ZY.equals(field) && attributeValue.length()>10){
							attributeValue = attributeValue.substring(0,5)+"...";
							fieldvalueMap.put(field, attributeValue);
						} 
						
					}
					String djlxmc = PfDataCache.getBillTypeNameByCode(vo.getDjlxbm());
					if(djlxmc.endsWith("������"))
						djlxmc = djlxmc.substring(0, djlxmc.length()-3);
					fieldvalueMap.put("djlxmc", djlxmc);
					resultmap.put(vo.getPrimaryKey(), fieldvalueMap);
				}
			} 
			
			// �����ѯ���������еı������ĵ�ǰ������
//			if(!resultmap.isEmpty()){
//				Map<String, String> checkmanMap = getBillCheckman(resultmap.keySet().toArray(new String[0]));
//				for (Entry<String, String> checkmans : checkmanMap.entrySet()) {
//					Map<String, String> map = resultmap.get(checkmans.getKey());
//					// ������״̬�滻Ϊ��ǰ������
//					String spr = checkmans.getValue();
//					if(spr!=null && !spr.equals("") && spr.contains(","))
//						spr = spr.split(",")[0];
//					map.put("sprshow", "��("+spr+")����");
//				}
//			}
			
			return resultmap;
	 }

	//����ɹ�����success����������򷵻س�����ʾ
	public String unAuditbxd(String pkJkbx,String userid) throws BusinessException {
		initEvn(userid);
		List<JKBXVO> auditVOs = NCLocator.getInstance().lookup(IBXBillPrivate.class)
	  	.queryVOsByPrimaryKeysForNewNode(new String[]{pkJkbx}, BXConstans.BX_DJDL,false,null);
		if(auditVOs==null || auditVOs.size()==0)
			return "������ɾ��";
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
			Object msgReturn = PfUtilPrivate.runAction(actionType, head.getDjlxbm(), bxvo, null,
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
	 *         У�鷴�����Ϣ
	 */
	MessageVO checkUnShenhe(JKBXVO bxvo) {

		// ���ݱ�ͷVO
		JKBXHeaderVO head = bxvo.getParentVO();

		// ���鷵�ص���Ϣ
		MessageVO msgVO = new MessageVO(bxvo, ActionUtils.UNAUDIT);

		if (null != head.getQcbz() && head.getQcbz().booleanValue()) {
			msgVO.setSuccess(false);
			msgVO.setErrorMessage(NCLangRes4VoTransl.getNCLangRes().getStrByID("2006030102", "UPP2006030102-000091"))/*
																													 * @
																													 * res
																													 * "�ڳ����ݲ��ܷ����"
																													 */;
			return msgVO;
		}

		return msgVO;
	}
	
	public String get_unapproved_bxdcount(String userid) throws BusinessException {
		  initEvn(userid); 
		  AuditListUtil util = new AuditListUtil();
		  String[] billtypearr = new String[]{BillTypeUtil.BX,BillTypeUtil.JK,BillTypeUtil.MA,BillTypeUtil.AC};
		  int count = 0;
		  for(int i=0;i<billtypearr.length;i++){
			  String[] pkars = util.queryAuditPksByUser(userid,billtypearr[i],false);
			  if(pkars != null && pkars.length>0){
				  count += pkars.length;
			  }
		  }
		  JSONObject ret = new JSONObject();
		  try {
			ret.put("unapprovedbxdno", String.valueOf(count));
		} catch (JSONException e) {
			return ret.toString();
		}
		return ret.toString();
	}

	
	public String[] queryApprovedWFBillPksByCondition(String pk_user, String[] tradeTypes, boolean isApproved)
			throws BusinessException {
		StringBuffer sqlBuf = new StringBuffer(
				"select distinct billid from pub_workflownote where checkman = ?  and  actiontype = ? ");

		SQLParameter sqlparams = new SQLParameter();
		if (pk_user == null) {// �û�
			pk_user = AuditInfoUtil.getCurrentUser();
		}
		sqlparams.addParam(pk_user);
		sqlparams.addParam("Z");//actiontypeΪZʱ����ʾΪ����

		if (isApproved) {//�Ƿ�������
			sqlBuf.append(" and approvestatus = 1 ");
		} else {//��������
			sqlBuf.append(" and ischeck = 'N' ");
		}

		if (tradeTypes != null && tradeTypes.length > 0) {//��������
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

