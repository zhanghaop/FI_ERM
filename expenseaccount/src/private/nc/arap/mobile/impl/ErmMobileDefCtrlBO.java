package nc.arap.mobile.impl;

import java.util.HashMap;
import java.util.List;

import nc.bs.er.util.BXPubUtil;
import nc.bs.erm.util.ErUtil;
import nc.bs.logging.Logger;
import nc.erm.mobile.billaction.BillInitDataAction;
import nc.erm.mobile.billaction.BillSaveAction;
import nc.erm.mobile.billaction.JKBXBillAddAction;
import nc.erm.mobile.environment.ErmTemplateQueryUtil;
import nc.erm.mobile.eventhandler.ErmEditeventHandler;
import nc.erm.mobile.eventhandler.JsonVoTransform;
import nc.erm.mobile.pub.template.MobileTemplateUtils;
import nc.erm.mobile.util.JsonData;
import nc.erm.mobile.util.QueryWorkFlowUtil;
import nc.erm.mobile.util.RefController;
import nc.erm.mobile.view.MobileTemplateFactory;
import nc.ui.pf.workitem.beside.BesideApproveContext;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

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
				// 主键已生成，修改
				if(head.getBoolean("isContrast")){
					//冲借款
					BXPubUtil.autoContrast((JKBXVO)vo, true);
				}
				if(head.getBoolean("isVerifyAccrued")){
					//核销预提
					BXPubUtil.autoContrast((JKBXVO)vo, true);
				}
				billpk = saveaction.updateJkbx(vo,userid); 
				// 附件先删后插
//				deleteAttachmentList(billpk, userid); 
//				saveAttachment(billpk, map);
			}else{
				//新增保存
				saveaction.fillAmount((JKBXVO)vo,djlxbm,userid);
				if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){
					if(head.getBoolean("isContrast")){
						//冲借款
						BXPubUtil.autoContrast((JKBXVO)vo, false);
					}
					if(head.getBoolean("isVerifyAccrued")){
						//核销预提
						BXPubUtil.autoContrast((JKBXVO)vo, false);
					}
					billpk = saveaction.insertJkbx(vo,djlxbm,userid);
				}else if(djlxbm.startsWith("262")){
					billpk = saveaction.insertAcc(vo,djlxbm,userid);
				}else if(djlxbm.startsWith("261")){
					billpk = saveaction.insertMa(vo,djlxbm,userid);
				}
				//保存附件
//				saveAttachment(billpk,map);
			}
			return "pk_jkbx"+billpk;
		}catch(Exception e){
			String msg = e.getMessage();
			return msg; 
		}
	}
    
	
	//拿到单据模板
	public String getBxdTemplate(String userid,
			String djlxbm, String nodecode,String flag) throws BusinessException {
		initEvn(userid);
		MobileTemplateFactory tra  = new MobileTemplateFactory(djlxbm);
		return tra.getheadcarddsl(flag);
	}
	
	//获取驳回审批流和参照列表
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
			throw new BusinessException("参照处理异常：" + e.getMessage());
		}
	}
	
	//加载表体界面和公式		
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
	
	//校验模板是不是最新的，得到模板最后修改时间
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
				//新增单据时需要初始化单据信息
				retJson = BillInitDataAction.getBillInitData(djlxbm, jsonData);
				retJson.put("djlxmc", djlxmc);
				retJson.put("userid", userid);
			}else{
				MobileBo bo = new MobileBo();
				List vos = bo.getBillAggVo(djlxbm, new String[]{pk_jkbx});
				if(vos == null || vos.isEmpty()){ 
					throw new BusinessException("单据已被删除，请检查"); 
				}
				billvo = (AggregatedValueObject) vos.get(0);
				//单纯查看数据
				if("new".equals(getbillflag))
					retJson = jsonData.transVOToJsonOnlyName(billvo);
				else
					retJson = jsonData.transBillValueObjectToJson(billvo);
				retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total"));
				retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total_name"));
				retJson.put("pk_jkbx", billvo.getParentVO().getPrimaryKey());
				retJson.put("djlxbm", djlxbm);
				retJson.put("djlxmc", djlxmc);
				retJson.put("userid", userid);
			}
			
			
			if("audit".equals(getbillflag) || getbillflag == null){
				//审批界面得到单据值之后还要加载是否具有审批/驳回/加签权限
				HashMap authMap = null;
				String actionType = ErUtil.getApproveActionCode(PK_ORG);
				BesideApproveContext besideContext = new BesideApproveContext();
				besideContext.setApproveResult("Y");
				besideContext.setCheckNote("批准");
				authMap = PfUtilPrivate.getOperateAuthorization(actionType, djlxbm, billvo, null,
						besideContext, null, null);
				if(authMap != null){
					retJson.put("canReject", authMap.get("canReject"));
					retJson.put("canTransefer", authMap.get("canTransefer"));
					retJson.put("canAddApprover", authMap.get("canAddApprover"));
					
				}
			}
			
			// 获取附件个数
			String fileNum = getFileNum(pk_jkbx, userid);
			if(fileNum != null){
				retJson.put("attachnum", fileNum);
			}else{
				retJson.put("attachnum", "0");
			}
		}catch (Exception e) {
			ExceptionUtils.wrappBusinessException("后台信息转换异常：" + e.getMessage());
		}
		return retJson.toString();
	}

	public String getAttachFile(String pk_jkbx, String userid) throws BusinessException {
		initEvn(userid);
		org.codehaus.jettison.json.JSONObject retJson = new org.codehaus.jettison.json.JSONObject();
		// 获取附件列表
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

