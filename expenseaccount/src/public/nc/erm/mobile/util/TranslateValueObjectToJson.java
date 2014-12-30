package nc.erm.mobile.util;

import java.lang.reflect.Field;

import nc.erm.mobile.environment.ErmTemplateQueryUtil;
import nc.erm.mobile.environment.EnvironmentInit;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;

public class TranslateValueObjectToJson{

	/**
	 * �����ӱ�voתΪjosn
	 * ֻ����pk��û��ȡ���յ�����
	 * ��ʽ��pk_org:""
	 * @param aggvo
	 * @return
	 */
	public   JSONObject transAggvoToJSONWithoutName(AggregatedValueObject aggvo){
		JSONObject resultJson = new JSONObject();
		try{
			//ת����ͷ
			SuperVO head = (SuperVO) aggvo.getParentVO();
			JSONObject headJson = new JSONObject();
			//��ȡ��������Class����
			Class classType = head.getClass();
			//��ȡ������������������
			Field[] fields=classType.getDeclaredFields();
			
			//��������
			for(int i=0;i<fields.length;i++){
				//��ȡ��Ӧ���Ե�����
				String fieldName=fields[i].getName().toLowerCase();
				Object value = head.getAttributeValue(fieldName);
				if(value instanceof String){
					//��ֵ���뵽JSON������
					headJson.put(fieldName, value);
			    }else if(value != null){
			    	headJson.put(fieldName, value.toString());
			    }
			}
			resultJson.put("head", headJson);
			
			//ת������
			JSONArray bodyarray = new JSONArray();
			SuperVO[] bodys = (SuperVO[]) aggvo.getChildrenVO();
			for(SuperVO body : bodys){
				JSONObject jsonBody = new JSONObject();
				JSONObject jsonItem = new JSONObject();
				//��ȡ��������Class����
				classType = body.getClass();
				//��ȡ������������������
				fields=classType.getDeclaredFields();
	
				//��������
				for(int i=0;i<fields.length;i++){
					//��ȡ��Ӧ���Ե�����
					String fieldName=fields[i].getName().toLowerCase();
					Object value = head.getAttributeValue(fieldName);
					if(value instanceof String ){
						//��ֵ���뵽JSON������
						jsonItem.put(fieldName, value);
				    }else if(value != null){
				    	jsonItem.put(fieldName, value.toString());
				    }
				}
				jsonBody.put(body.getTableName(),jsonItem);
				bodyarray.put(jsonBody);
			}
			resultJson.put("bodys", bodyarray);
		}catch(Exception e){
				e.printStackTrace();
		}

		return resultJson;
	}
	
	/**
	 * �����ӱ�voת��Ϊǰ̨��ʶ���json
	 * ��ʽ��pk_org:{pk:"",name:""}
	 * @param aggvo
	 * @return
	 * @throws Exception 
	 */
	public JSONObject transValueObjectToJSON(String pk_group,String pk_user,Object o) throws Exception{
		try {
			EnvironmentInit.initEvn(pk_user);
			BillOperaterEnvVO envvo=new BillOperaterEnvVO();
			envvo.setBilltype("20080PBR");
			envvo.setNodekey("D1");
			envvo.setOperator(pk_user);
			envvo.setCorp(pk_group);
			BillTempletVO billTempletVO = ErmTemplateQueryUtil.findBillTempletDatas(envvo);
			if(billTempletVO==null){
				throw new BusinessException("����ģ��ʧ�ܣ�"+envvo.getBilltype()+":"+envvo.getNodekey()+":"+envvo.getOperator()+":"+envvo.getCorp()+":");
			}
			JsonData jsonData = new JsonData(billTempletVO);
			return jsonData.transBillValueObjectToJson(o);
		}catch (BusinessException e) {
			JSONObject eo = new JSONObject();
			eo.put("errormessage", e.getMessage());
			return eo;
		}
	}
	
	
//	public   List<JSONObject> toJSONList(List listObj){
//		List<JSONObject> list=null;
//		try{
//			list=new ArrayList<JSONObject>();
//			for(int i=0;i<listObj.size();i++){
//				AggregatedValueObject obj = (AggregatedValueObject)listObj.get(i);
//				JSONObject json = VoToJSON(obj);
//				if(json!=null){
//					list.add(json);
//				}
//			}
//
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		return list;
//	}
//	
	
}
