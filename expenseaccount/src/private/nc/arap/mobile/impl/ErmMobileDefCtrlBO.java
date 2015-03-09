package nc.arap.mobile.impl;

import java.util.HashMap;
import java.util.List;

import nc.bs.er.util.BXPubUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.billaction.BillSaveAction;
import nc.erm.mobile.billaction.InitBillDataAction;
import nc.erm.mobile.billaction.JKBXBillAddAction;
import nc.erm.mobile.environment.ErmTemplateQueryUtil;
import nc.erm.mobile.eventhandler.ErmEditeventHandler;
import nc.erm.mobile.eventhandler.JsonVoTransform;
import nc.erm.mobile.pub.template.MobileTemplateUtils;
import nc.erm.mobile.util.JsonData;
import nc.erm.mobile.util.QueryWorkFlowUtil;
import nc.erm.mobile.util.RefController;
import nc.erm.mobile.view.MobileTemplateFactory;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ErmMobileDefCtrlBO extends AbstractErmMobileCtrlBO{
	
	public String addJkbx(String bxdcxt, String djlxbm,String userid) throws BusinessException{
		initEvn(userid);
		try{
			String billpk = null;
			JsonData jsonData = new JsonData(ErmTemplateQueryUtil.getDefaultTempletStatics(djlxbm));
			JSONObject context = new JSONObject(bxdcxt);
			Object[] resultObject = jsonData.transJsonToBillValueObject(context);
//			if(resultObject[1] != null && !"".equals(resultObject[1])){
//	    		 json.put("message", resultObject[1]);
//	    	 }
			AggregatedValueObject vo = (AggregatedValueObject)(resultObject[0]);
			JSONObject head = (JSONObject) context.get("head");
			BillSaveAction saveaction = new BillSaveAction(PK_ORG);
			if(vo.getParentVO().getPrimaryKey() != null && !"".equals(vo.getParentVO().getPrimaryKey())){
				// ���������ɣ��޸�
				if(head.getBoolean("isContrast")){
					//����
					BXPubUtil.autoContrast((JKBXVO)vo, true);
				}
				if(head.getBoolean("isVerifyAccrued")){
					//����Ԥ��
					BXPubUtil.autoContrast((JKBXVO)vo, true);
				}
				billpk = saveaction.updateJkbx(vo,userid); 
				// ������ɾ���
//				deleteAttachmentList(billpk, userid); 
//				saveAttachment(billpk, map);
			}else{
				//��������
				saveaction.fillAmount((JKBXVO)vo,djlxbm,userid);
				if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){
					if(head.getBoolean("isContrast")){
						//����
						BXPubUtil.autoContrast((JKBXVO)vo, false);
					}
					if(head.getBoolean("isVerifyAccrued")){
						//����Ԥ��
						BXPubUtil.autoContrast((JKBXVO)vo, false);
					}
					billpk = saveaction.insertJkbx(vo,djlxbm,userid);
				}else if(djlxbm.startsWith("262")){
					billpk = saveaction.insertAcc(vo,djlxbm,userid);
				}else if(djlxbm.startsWith("261")){
					billpk = saveaction.insertMa(vo,djlxbm,userid);
				}
				//���渽��
//				saveAttachment(billpk,map);
			}
			return "pk_jkbx"+billpk;
		}catch(Exception e){
			String msg = e.getMessage();
			return msg; 
		}
	}
    
	
	//�õ�����ģ��
	public String getBxdTemplate(String userid,
			String djlxbm, String nodecode,String flag) throws BusinessException {
		initEvn(userid);
		MobileTemplateFactory tra  = new MobileTemplateFactory(djlxbm);
		return tra.getheadcarddsl(flag);
	}
	
	//��ȡ�����������Ͳ����б�
	public String getRefList(String userid,String query,String pk_org, String reftype,String filterCondition) throws BusinessException {
		initEvn(userid);
		if(reftype.startsWith("workflow,")){
			JSONObject jsonObj = new JSONObject();
 			String[] str = reftype.split(",");
 			try {
				return QueryWorkFlowUtil.getWorkFlowGraph(str[1],str[2]).toString();
			} catch (JSONException e) {
				return jsonObj.toString();
			}
 		}
		try {
			RefController ref = new RefController();
			return ref.getRefList(userid, query,pk_org,reftype, filterCondition);
		} catch (Exception e) {
			throw new BusinessException("���մ����쳣��" + e.getMessage());
		}
	}
	
	//���ر������͹�ʽ		
	public String getItemDslFile(String userid, String djlxbm, String nodecode,
			String tablecode, String flag) throws BusinessException {
		initEvn(userid);
		MobileTemplateFactory tra  = new MobileTemplateFactory(djlxbm);
		return tra.getbodycarddsl(flag,tablecode);
//        BillTempletVO billTempletVO =  ErmTemplateQueryUtil.getDefaultTempletStatics(djlxbm);
//        billTempletVO.setParentToBody();
//		BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
//		JSONObject jsonObj = new JSONObject();
//		try {
//			MobileTemplateFactory tra  = new MobileTemplateFactory(djlxbm);
//			jsonObj.put("dsl", tra.getbodydsl(bodyVO,tablecode,flag,djlxbm));
//			jsonObj.put(tablecode + "_order", tra.getbodyorder(bodyVO,tablecode,flag,djlxbm));
//			jsonObj.put(tablecode + "_formula", tra.getbodyformula(bodyVO,tablecode));
//			jsonObj.put("ts", "\""+billTempletVO.getHeadVO().getTs().toString()+"\"");
//		} catch (JSONException e) {
//		}
//		return jsonObj.toString();
	}
	
	//У��ģ���ǲ������µģ��õ�ģ������޸�ʱ��
	public String validateTs(String userid, String djlxbm, String nodecode,
			String tsflag) throws BusinessException {
		initEvn(userid);
		if(tsflag.equals("head")){
			return MobileTemplateUtils.getTemplateTS(djlxbm);
		}
		else
			return null;
	}
	
	
	public String getJkbxCard(String pk_jkbx,String userid,String djlxbm,String djlxmc,String getbillflag) throws BusinessException {
		initEvn(userid);
		org.codehaus.jettison.json.JSONObject retJson = new org.codehaus.jettison.json.JSONObject();
		AggregatedValueObject billvo = null;
		BillTempletVO billTempletVO =  ErmTemplateQueryUtil.getDefaultTempletStatics(djlxbm);
		JsonData jsonData = new JsonData(billTempletVO);
		try {
			if(StringUtil.isEmpty(pk_jkbx)){
				billvo = InitBillDataAction.getBillData(djlxbm, jsonData);
				retJson = jsonData.transBillValueObjectToJson(billvo);
				//����ʱ��itemlist��ֵ��Ĭ��ֵitem��
				JSONArray itemsarray = (JSONArray) retJson.get("itemlist");
				if(itemsarray.length() > 0){
					retJson.put("item", itemsarray.get(0));
				}
				retJson.remove("itemlist");
				retJson.put("itemnum", 0);
			}else{
				List<JKBXVO> vos = NCLocator.getInstance()
						.lookup(IBXBillPrivate.class).queryVOsByPrimaryKeysForNewNode(
								new String[]{pk_jkbx}, null,false,null);
				if(vos == null || vos.isEmpty()){ 
					throw new BusinessException("�����ѱ�ɾ��������"); 
				}
				billvo = vos.get(0);
				retJson = jsonData.transBillValueObjectToJson(billvo);
			}
			//
			HashMap authMap = null;
			if("audit".equals(getbillflag) || getbillflag == null){
				//��������õ�����ֵ֮��Ҫ�����Ƿ��������/����/��ǩȨ��
				String actionType = ErUtil.getApproveActionCode(PK_ORG);
				BesideApproveContext besideContext = new BesideApproveContext();
				besideContext.setApproveResult("Y");
				besideContext.setCheckNote("��׼");
				authMap = PfUtilPrivate.getOperateAuthorization(actionType, djlxbm, billvo, null,
						besideContext, null, null);
			}
			retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total"));
			retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total_name"));
			retJson.put("pk_jkbx", billvo.getParentVO().getPrimaryKey());
			retJson.put("djlxbm", djlxbm);
			retJson.put("djlxmc", djlxmc);
			retJson.put("userid", userid);
			if(authMap != null){
				retJson.put("canReject", authMap.get("canReject"));
				retJson.put("canTransefer", authMap.get("canTransefer"));
				retJson.put("canAddApprover", authMap.get("canAddApprover"));
	
			}
			
			// ��ȡ��������
			String fileNum = getFileNum(pk_jkbx, userid);
			if(fileNum != null){
				retJson.put("attachnum", fileNum);
			}else{
				retJson.put("attachnum", "0");
			}
		}catch (Exception e) {
			ExceptionUtils.wrappBusinessException("��̨��Ϣת���쳣��" + e.getMessage());
		}
		return retJson.toString();
	}

	public String getAttachFile(String pk_jkbx, String userid) throws BusinessException {
		initEvn(userid);
		org.codehaus.jettison.json.JSONObject retJson = new org.codehaus.jettison.json.JSONObject();
		// ��ȡ�����б�
		try {
			org.codehaus.jettison.json.JSONArray attatchmapList = getFileList(pk_jkbx, userid);
			retJson.put("contentlist", attatchmapList);
		} catch (JSONException e) {
			Logger.debug(e.getMessage());
		}
		return retJson.toString();
	}

	public String doAfterEdit(String editinfo, String userid) throws BusinessException {
		initEvn(userid);
		ErmEditeventHandler ermEditeventHandler = new ErmEditeventHandler();
		JSONObject jSONObject = ermEditeventHandler.process(editinfo);
		if(jSONObject != null){
			return jSONObject.toString();
		}
		return null;
	}
	
	public String getItemInfo(String userid, String djlxbm,String head,String tablecode,String itemnum,String classname)
			throws BusinessException {
		initEvn(userid);
		JsonVoTransform votra = new JsonVoTransform();
		SuperVO parentVO;
		try {
			parentVO = votra.transformHead(new JSONObject(head));
			SuperVO bodyVO = new JKBXBillAddAction().setBodyDefaultValue(parentVO,tablecode,classname);
			JSONObject retjson = votra.getVOJSONObject(Integer.parseInt(itemnum),djlxbm,bodyVO,tablecode,classname);
			return retjson.toString();
		} catch (Exception e) {
			return null;
		}
	}
}

