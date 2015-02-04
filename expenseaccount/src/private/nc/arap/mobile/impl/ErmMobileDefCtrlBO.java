package nc.arap.mobile.impl;

import java.util.List;
import java.util.Map;

import nc.bs.erm.util.ErmDjlxCache;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.framework.exception.ComponentException;
import nc.bs.logging.Logger;
import nc.erm.mobile.billaction.BillAddAction;
import nc.erm.mobile.billaction.BillSaveAction;
import nc.erm.mobile.eventhandler.ErmEditeventHandler;
import nc.erm.mobile.pub.template.MobileTemplateUtils;
import nc.erm.mobile.util.QueryWorkFlowUtil;
import nc.erm.mobile.util.RefUtil;
import nc.erm.mobile.util.TranslateJsonToValueObject;
import nc.erm.mobile.util.TranslateValueObjectToJson;
import nc.erm.mobile.view.BillItemTranslate;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.IErmBillUIPublic;
import nc.itf.uap.billtemplate.IBillTemplateQry;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillQuery;
import nc.pubitf.erm.matterapp.IErmMatterAppBillQuery;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.templet.translator.BillTranslator;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ErmMobileDefCtrlBO extends AbstractErmMobileCtrlBO{
	
	public String addJkbx(Map<String, Object> map, String djlxbm,String userid) throws BusinessException{
		initEvn(userid);
		try{
			String billpk = null;
			TranslateJsonToValueObject trans = new TranslateJsonToValueObject();
			BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm); 
			AggregatedValueObject vo = trans.translateMapToAggvo(billTempletVO, map);
			BillSaveAction saveaction = new BillSaveAction(PK_ORG);
			if(map.get("pk_jkbx") != null && !"".equals(map.get("pk_jkbx"))){
				// ���������ɣ��޸�
				billpk = saveaction.updateJkbx(vo,userid); 
				// ������ɾ���
				deleteAttachmentList(billpk, userid); 
				saveAttachment(billpk, map);
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
				saveAttachment(billpk,map);
			}
			return "pk_jkbx"+billpk;
		}catch(BusinessException e){
			String msg = e.getMessage();
			return msg; 
		}
	}
    
    /**
     * ����Ĭ��ģ��. ��������:(01-3-6 11:18:13)
     * 
     * @param strBillType
     *            java.lang.String
     * @throws BusinessException 
     * @throws ComponentException 
     */
    private BillTempletVO getDefaultTempletStatics(String djlxbm) throws BusinessException {
            //��ѯ�汾�ҷ��ظ�������
    	BillTempletVO cardListVO = findBillTempletDatas(djlxbm);
	        //cacheBillTempletVO(cardListVO, ceKeys[i]);
        if (cardListVO != null ) {
            	BillTranslator.translate(cardListVO);
            	Logger.info("ģ����سɹ�!");
            	return cardListVO; 
        }else{
            throw new BusinessException("δ�ҵ�����ģ��!");
        }
    }
    
	public BillTempletVO findBillTempletDatas(String djlxbm)
		throws BusinessException, ComponentException {
		String pk_billtemplet = MobileTemplateUtils.getTemplatePK(djlxbm);
		if(pk_billtemplet == null){
			return null;
		}
		String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			return getIBillTemplateQry().findTempletData(pk_billtemplet,pk_group);
	}
    private IBillTemplateQry iBillTemplateQry = null;
    private IBillTemplateQry getIBillTemplateQry()
    		throws ComponentException {
		if (iBillTemplateQry == null)
		    iBillTemplateQry = (IBillTemplateQry) NCLocator.getInstance().lookup(IBillTemplateQry.class.getName());
		return iBillTemplateQry;
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

        BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm ); 
        billTempletVO.setParentToBody();
        //��ͷҪ�����ֶΣ��༭��
        BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
        try {
        	BillItemTranslate tra  = new BillItemTranslate();
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
	public String getRefList(String userid, String reftype,Map<String, Object> map) throws BusinessException {
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
		return RefUtil.getRefList(userid, reftype, map);
	}
	
	//���ر������͹�ʽ		
	public String getItemDslFile(String userid, String djlxbm, String nodecode,
			String tablecode, String flag) throws BusinessException {
		initEvn(userid);
        BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm);
        billTempletVO.setParentToBody();
		BillTempletBodyVO[] bodyVO = billTempletVO.getBodyVO();
		JSONObject jsonObj = new JSONObject();
		try {
			BillItemTranslate tra  = new BillItemTranslate();
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
		if(StringUtil.isEmpty(pk_jkbx)){
			//��������  ����Ĭ��ֵ
			String pk_group = InvocationInfoProxy.getInstance().getGroupId();
			DjLXVO djlxVO = ErmDjlxCache.getInstance().getDjlxVO(pk_group, djlxbm);
			// ��ʼ����ͷ����
			if(djlxbm.startsWith("263") || djlxbm.startsWith("264")){ 
				//��������ʼ��
				IErmBillUIPublic initservice = NCLocator.getInstance().lookup(IErmBillUIPublic.class);
				billvo = initservice.setBillVOtoUI(djlxVO, "", null);
				BillAddAction add = new BillAddAction();
				add.setJKBXValue(null, billvo);
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
		BillTempletVO billTempletVO =  getDefaultTempletStatics(djlxbm);
		TranslateValueObjectToJson trans = new TranslateValueObjectToJson();
		try {
			retJson = trans.transValueObjectToJSON(billTempletVO,billvo);
//			retJson.put("djbh", jkbxvo.getParentVO().getDjbh());
			retJson.put("total", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total"));
			retJson.put("total_name", ((org.codehaus.jettison.json.JSONObject)retJson.get("head")).get("total_name"));
			retJson.put("pk_jkbx", billvo.getParentVO().getPrimaryKey());
			retJson.put("djlxbm", djlxbm);
			retJson.put("djlxmc", djlxmc);
			retJson.put("userid", userid);
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
	
//	private static Map<String,Map<String,String>> refPkName = new HashMap<String,Map<String,String>>();
//	private String resetRefName(String key,String refval,String reftype){
//		if(key.endsWith("_name")){
//			return null;
//		}
//		if(reftype.startsWith("UFREF,")){
//			reftype = reftype.substring(6);
//			if(reftype != null && reftype.contains(","))
//				reftype = reftype.split(",")[0];
//			if (RefPubUtil.isSpecialRef(reftype)) {
//				return null;
//			}
//			if(refPkName.get(reftype) == null){
//				AbstractRefModel refModel = RefPubUtil.getRefModel(reftype);
//				String pkFieldCode = refModel.getPkFieldCode();
//				RefcolumnVO[] RefcolumnVOs = RefPubUtil.getColumnSequences(refModel);
//				Vector vDataAll = refModel.getRefData();
//				Map<String,String> map = new HashMap<String,String>();
//				for(int i=0;i<vDataAll.size();i++){
//					Vector aa = (Vector) vDataAll.get(i);
//					String pk = null;
//					String value = null;
//					for(int j=0;j<RefcolumnVOs.length;j++){
//						if(RefcolumnVOs[j].getFieldname().equals("name")){
//							value = (String) aa.get(j);
//						}
//						else if(RefcolumnVOs[j].getFieldname().equals(pkFieldCode)){
//							pk = (String) aa.get(j);
//						}
//					}
//					map.put(pk, value);
//				}
//				if(map.size() > 0)
//					refPkName.put(reftype, map);
//				else
//					return null;
//			}
//		}else if(reftype.startsWith("COMBO,")){
//			//�������������������ȡֵ
//			reftype = reftype.substring(6);
//			if(refPkName.get(reftype) == null){
//				if (reftype != null
//						&& (reftype = reftype.trim()).length() > 0) {
//					boolean isFromMeta = reftype
//							.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
//					String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
//					List<DefaultConstEnum> combodata = ComboBoxUtil.getInitData(reftype1, isFromMeta);
//					Map<String,String> map = new HashMap<String,String>();
//					for(int i=0;i<combodata.size();i++){
//						map.put(combodata.get(i).getValue().toString(), combodata.get(i).getName());
//					}
//					if(map.size() > 0)
//						refPkName.put(reftype, map);
//					else
//						return null;
//				}
//			}
//		}
//		return refPkName.get(reftype).get(refval);
//	}
}

