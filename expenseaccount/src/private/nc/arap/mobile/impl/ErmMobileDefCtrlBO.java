package nc.arap.mobile.impl;

import java.util.HashMap;
import java.util.List;

import nc.bs.erm.util.ErUtil;
import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Logger;
import nc.erm.mobile.billaction.BillSaveAction;
import nc.erm.mobile.billaction.JKBXBillAddAction;
import nc.erm.mobile.environment.ErmTemplateQueryUtil;
import nc.erm.mobile.eventhandler.ErmEditeventHandler;
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
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ErmMobileDefCtrlBO extends AbstractErmMobileCtrlBO{
	
	public String addJkbx(String bxdcxt, String djlxbm,String userid) throws BusinessException{
		initEvn(userid);
		try{
			String billpk = null;
			JsonData jsonData = new JsonData();
			AggregatedValueObject vo = null;
			vo = jsonData.transJsonToBillValueObject(bxdcxt);
			BillSaveAction saveaction = new BillSaveAction(PK_ORG);
			if(vo.getParentVO().getPrimaryKey() != null && !"".equals(vo.getParentVO().getPrimaryKey())){
				// ���������ɣ��޸�
				billpk = saveaction.updateJkbx(vo,userid); 
				// ������ɾ���
				deleteAttachmentList(billpk, userid); 
//				saveAttachment(billpk, map);
			}else{
				//��������
				if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){
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
    
    
    
    
    /**
	 * ��ʼ��ģ������. ��������:(01-2-23 15:05:07)
	 */
	public static String getDefaultTableCode(int pos) {
		switch (pos) {
		case 0:
			return "main";
			// return BillUtil.getDefaultTableName(HEAD);
		case 1:
			// return BillUtil.getDefaultTableName(BODY);
			return "table";
		case 2:
			// return BillUtil.getDefaultTableName(TAIL);
			return "tail";
		}
		return null;
	}
	
	public static String getDefaultTableName(int pos) {
		switch (pos) {
		case 0:
			return "����";
			// return BillData.DEFAULT_HEAD_TABBEDCODE;
		case 1:
			return "�ӱ�";

			// return BillData.DEFAULT_BODY_TABLECODE;
		case 2:
			return "����";
			// return BillData.DEFAULT_TAIL_TABBEDCODE;
		}
		return null;
	}
	 
	
	//�õ�����context
	public String getBxdTemplate(String userid,
			String djlxbm, String nodecode,String flag) throws BusinessException {
		initEvn(userid);
		JSONObject jsonObj = new JSONObject();

        BillTempletVO billTempletVO =  ErmTemplateQueryUtil.getDefaultTempletStatics(djlxbm ); 
        billTempletVO.setParentToBody();
        //��ͷҪ�����ֶΣ��༭��
        BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
        try {
        	MobileTemplateFactory tra  = new MobileTemplateFactory();
        	//�༭��������Ӱ�ťʱ��Ҫ����ҳǩ�б�  ����ҳǩ�б�
			jsonObj.put("tablist", tra.getTableCodes(billTempletVO.getHeadVO()));
			jsonObj.put("dsl", tra.getheaddsl(flag,bodyVO,djlxbm));
			jsonObj.put("formula", tra.getheadformula(flag,bodyVO));
			jsonObj.put("ts", billTempletVO.getHeadVO().getTs().toString());
		} catch (JSONException e) {
		}
		//��ʾ������Ҫ���ر�ͷ�ͱ��壬��ΪҪ���ݱ����dsl��reftypeȷ��pk����Ӧ������
//        if(flag.equals("editcard")){
//        	for(int i=0;i<tablist.length();i++){
//        		JSONObject item = (JSONObject) tablist.get(i);
//        		String tablecode = item.getString("tablecode");
//        		jsonObj.put(tablecode+"dsl", getbodydsl(bodyVO,tablecode,flag,djlxbm));
//        	}
//        }
		return jsonObj.toString();
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
        BillTempletVO billTempletVO =  ErmTemplateQueryUtil.getDefaultTempletStatics(djlxbm);
        billTempletVO.setParentToBody();
		BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
		JSONObject jsonObj = new JSONObject();
		try {
			MobileTemplateFactory tra  = new MobileTemplateFactory();
			jsonObj.put("dsl", tra.getbodydsl(bodyVO,tablecode,flag,djlxbm));
			jsonObj.put(tablecode + "_order", tra.getbodyorder(bodyVO,tablecode,flag,djlxbm));
			jsonObj.put(tablecode + "_formula", tra.getbodyformula(bodyVO,tablecode));
			jsonObj.put("ts", "\""+billTempletVO.getHeadVO().getTs().toString()+"\"");
		} catch (JSONException e) {
		}
		return jsonObj.toString();
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
		if(StringUtil.isEmpty(pk_jkbx)){
			//��������  ����Ĭ��ֵ
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
			// ��ʼ����ͷ����
			if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){ 
				//��������ʼ��
				IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
				billvo = initservice.setBillVOtoUI(djlxVO, "", null);
				JKBXBillAddAction add = new JKBXBillAddAction();
				add.setJKBXValue(jsonData,null, billvo);
			}else if(djlxbm.startsWith("262")){
				//����Ԥ�ᵥ��ʼ��
				IErmAccruedBillQuery initservice = NCLocator.getInstance().lookup(IErmAccruedBillQuery.class);
				billvo = initservice.getAddInitAccruedBillVO(djlxVO, "", null);
			}else if(djlxbm.startsWith("261")){
				//�������뵥��ʼ��
				IErmMatterAppBillQuery initservice = NCLocator.getInstance().lookup(IErmMatterAppBillQuery.class);
				billvo = initservice.getAddInitAggMatterVo(djlxVO, "", null);
			}
		}else{
			List<JKBXVO> vos = NCLocator.getInstance()
					.lookup(IBXBillPrivate.class).queryVOsByPrimaryKeysForNewNode(
							new String[]{pk_jkbx}, null,false,null);
			billvo = vos.get(0);
		}
		//
		HashMap authMap = null;
		if("audit".equals(getbillflag)||getbillflag==null){
			String actionType = ErUtil.getApproveActionCode(PK_ORG);
			BesideApproveContext besideContext = new BesideApproveContext();
			besideContext.setApproveResult("Y");
			besideContext.setCheckNote("��׼");
			authMap = PfUtilPrivate.getOperateAuthorization(actionType, djlxbm, billvo, null,
					besideContext, null, null);
		}
		try {
			retJson = jsonData.transBillValueObjectToJson(billvo);
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
		} catch (Exception e) {
			Logger.debug(e.getMessage());
		}
		
		// ��ȡ��������
		try {
			String fileNum = getFileNum(pk_jkbx, userid);
			if(fileNum != null){
				retJson.put("attachnum", fileNum);
			}else{
				retJson.put("attachnum", "0");
			}
		} catch (JSONException e) {
			Logger.debug(e.getMessage());
		}
		return retJson.toString();
	}

	public String getAttachFile(String pk_jkbx, String userid) throws BusinessException {
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
		ErmEditeventHandler ermEditeventHandler = new ErmEditeventHandler();
		JSONObject jSONObject = ermEditeventHandler.process(editinfo);
		if(jSONObject != null){
			return jSONObject.toString();
		}
		return null;
	}
	
}

