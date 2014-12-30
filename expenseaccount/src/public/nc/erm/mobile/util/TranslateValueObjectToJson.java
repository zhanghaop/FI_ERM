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
	 * 将主子表vo转为josn
	 * 只返回pk，没有取参照的名称
	 * 形式如pk_org:""
	 * @param aggvo
	 * @return
	 */
	public   JSONObject transAggvoToJSONWithoutName(AggregatedValueObject aggvo){
		JSONObject resultJson = new JSONObject();
		try{
			//转换表头
			SuperVO head = (SuperVO) aggvo.getParentVO();
			JSONObject headJson = new JSONObject();
			//获取传入对象的Class对象
			Class classType = head.getClass();
			//获取传入对象的所有属性组
			Field[] fields=classType.getDeclaredFields();
			
			//遍历属性
			for(int i=0;i<fields.length;i++){
				//获取对应属性的名字
				String fieldName=fields[i].getName().toLowerCase();
				Object value = head.getAttributeValue(fieldName);
				if(value instanceof String){
					//将值放入到JSON对象中
					headJson.put(fieldName, value);
			    }else if(value != null){
			    	headJson.put(fieldName, value.toString());
			    }
			}
			resultJson.put("head", headJson);
			
			//转换表体
			JSONArray bodyarray = new JSONArray();
			SuperVO[] bodys = (SuperVO[]) aggvo.getChildrenVO();
			for(SuperVO body : bodys){
				JSONObject jsonBody = new JSONObject();
				JSONObject jsonItem = new JSONObject();
				//获取传入对象的Class对象
				classType = body.getClass();
				//获取传入对象的所有属性组
				fields=classType.getDeclaredFields();
	
				//遍历属性
				for(int i=0;i<fields.length;i++){
					//获取对应属性的名字
					String fieldName=fields[i].getName().toLowerCase();
					Object value = head.getAttributeValue(fieldName);
					if(value instanceof String ){
						//将值放入到JSON对象中
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
	 * 将主子表vo转换为前台可识别的json
	 * 形式如pk_org:{pk:"",name:""}
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
				throw new BusinessException("加载模板失败："+envvo.getBilltype()+":"+envvo.getNodekey()+":"+envvo.getOperator()+":"+envvo.getCorp()+":");
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
