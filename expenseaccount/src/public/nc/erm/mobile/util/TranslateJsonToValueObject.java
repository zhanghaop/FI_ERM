package nc.erm.mobile.util;

import java.util.Map;

import nc.bs.framework.exception.ComponentException;
import nc.erm.mobile.environment.ErmTemplateQueryUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.bill.BillOperaterEnvVO;
import nc.vo.pub.bill.BillTempletVO;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class TranslateJsonToValueObject {

	/**
	 * 将前台传来的json格式字符串转为vo
	 * @param str
	 * @return
	 * @throws JSONException
	 * @throws BusinessException 
	 * @throws ComponentException 
	 */
	public AggregatedValueObject translateJsonToAggvo(String str) throws JSONException, BusinessException{
		JSONObject json = new JSONObject(str);
		JSONObject head = (JSONObject)json.get("head");
		BillOperaterEnvVO envvo=new BillOperaterEnvVO();
		envvo.setBilltype("20080PBR");
		envvo.setNodekey("D1");
		envvo.setOperator(head.getString("billmaker"));
		envvo.setCorp(head.getString("pk_group"));
		BillTempletVO billTempletVO = ErmTemplateQueryUtil.findBillTempletDatas(envvo);
		JsonData jsonData = new JsonData(billTempletVO);
		return jsonData.transJsonToBillValueObject(json);
	}
	
	public AggregatedValueObject translateMapToAggvo(BillTempletVO billTempletVO,Map<String, Object> valuemap) throws BusinessException{
		JsonData jsonData = new JsonData(billTempletVO);
		return jsonData.transMapToBillValueObject(valuemap);
	}
	
}
