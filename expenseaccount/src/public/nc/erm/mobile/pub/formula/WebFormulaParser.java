package nc.erm.mobile.pub.formula;

/**
*
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.RuntimeEnv;
import nc.erm.mobile.util.JsonItem;
import nc.md.data.access.NCObject;
import nc.ui.pub.formulaparse.FormulaParse;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.formulaset.FormulaParseFather;
import nc.vo.pub.formulaset.VarryVO;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;


public class WebFormulaParser {
	//���ڴ�����ǰ�̵߳Ĺ�ʽִ����,ʹ��ÿ���߳�ֻ����һ��ִ����,���Ч��
	private static ThreadLocal<WebFormulaParser> currentFomulaParser = new ThreadLocal<WebFormulaParser>();
	/**
	* ���췽��������һ����WebFormulaParserʵ��
	*/
	private WebFormulaParser() {
		super();
		initFormulaParse();
	}
	private FormulaParseFather parser = null;
	private boolean isOnServer = false;
	private static final String[] formulaflags = new String[] {
		"(", ")", "+", "-", "*", "/", "&", "|", "!", ">", "<", "="
	};
	/**
	* WebFormulaParser��¶�ķ�������ֻ��ͨ���÷���������WebFormulaParser�࣬��ֹ�Լ�����ʵ��
	*/
	public static WebFormulaParser getInstance() {
		WebFormulaParser parser = (WebFormulaParser) currentFomulaParser.get();
		if (parser == null) {
			parser = new WebFormulaParser();
			currentFomulaParser.set(parser);
		}
		return parser;
	}
	/**
	* ��ʼ��parser���÷�����WebFormulaParser��������ʱ����ã��Ա�֤ÿ�������õ�WebFormulaParser����һ��parser����ʹ�ã��������п�
	*/
	private void initFormulaParse() {
		if (parser == null) {
			isOnServer = RuntimeEnv.getInstance().isRunningInServer();
			if (isOnServer)
				parser = new nc.bs.pub.formulaparse.FormulaParse();
			else
				parser = new FormulaParse();
			}
		}
	public FormulaParseFather getFormulaParse() {
		return parser;
	}
	/**
	 * �����ͷ�ֶ�������ʽ
	 * 
	 */
	public void processFormulasForHead(JSONObject parentJson,String[] formulas,Object dataVO) throws Exception{
		SuperVO superVO = null;
		if(dataVO instanceof AggregatedValueObject){
			superVO = (SuperVO)((AggregatedValueObject)dataVO).getParentVO();
		} else if(dataVO instanceof SuperVO){
			superVO = (SuperVO)dataVO;
		}
		getFormulaParse().setExpressArray(formulas);
		VarryVO[] paramlist = getFormulaParse().getVarryArray();
		Object[] paramvalues = null;
		String[] paramnames = null;
		Object[][] results = null;
		if (paramlist != null) {
			for (int j = 0; j < paramlist.length; j++) {
				VarryVO vo = paramlist[j];
				if (vo != null && vo.getVarry() != null && vo.getVarry().length > 0) {
					if (dataVO == null) {
						throw new BusinessException("���빫ʽ��ʵ��VOΪ��,��ʽ�еı����޷�ȡֵ!");
					}
					paramnames = vo.getVarry();
					paramvalues = new Object[paramnames.length];
					for (int i = 0; i < paramnames.length; i++) {
						if (paramnames[i] == null){
							continue;
						}
						paramvalues[i] = superVO.getAttributeValue(paramnames[i]);
						getFormulaParse().addVariable(paramnames[i], paramvalues[i]);
					}
				}
			}
			try {
				results = getFormulaParse().getValueOArray();
				if (getFormulaParse().getError() != null){
					throw new BusinessException(getFormulaParse().getError());
				}
			} catch (Exception e) {
				throw new BusinessException("��ʽ�������,ԭ��:" + e.getMessage());
			}
			for(int n = 0; n < paramlist.length; n++){
				VarryVO varryVO = paramlist[n];
				Object[] result = results[n];
				String itemkey = varryVO.getFormulaName();
				JSONObject itemJson = new JSONObject();
				itemJson.put("pk", (String)(result[0]));
				itemJson.put("name", (String)(result[0]));
				parentJson.put(itemkey, itemJson);
			}
		}
	}
	
	/**
	 * ��������ֶ�������ʽ
	 * 
	 */
	public JSONArray processFormulasForBody(Map<Integer, JSONObject> bodyMap,JsonItem[] items,NCObject[] bodys) throws Exception{
		JSONArray bodyarray = new JSONArray();
		if(bodyMap.size() == 0){
			return null;
		}
		for(JsonItem item : items){
			if(item.getLoadFormula() != null ){
				getFormulaParse().setExpressArray(item.getLoadFormula());
				VarryVO[] paramlist = getFormulaParse().getVarryArray();
				String[] paramnames = null;
				Object[][] results = null;
				if (paramlist != null) {
					for (int i=0; i<paramlist.length; i++) {
						VarryVO vo = paramlist[i];
						paramnames = vo.getVarry();
						for (int j=0; j<paramnames.length; j++) {
							List<Object> paramvalues = new ArrayList<Object>();
							for(int m=0; m<bodys.length; m++){
								paramvalues.add(bodys[m].getAttributeValue(paramnames[j]));
							}
							getFormulaParse().addVariable(paramnames[j],paramvalues);
						}
					}
					try {
						results = getFormulaParse().getValueOArray();
						if (getFormulaParse().getError() != null){
							throw new BusinessException(getFormulaParse().getError());
						}
					} catch (Exception e) {
						throw new BusinessException("��ʽ�������,ԭ��:" + e.getMessage());
					}
					if(results.length == 0){
						continue;
					}
					for (int n=0; n<paramlist.length; n++) {
						if(n == results.length){
							break;
						}
						VarryVO varryVO = paramlist[n];
						Object[] result = results[n];
						String itemkey = varryVO.getFormulaName();
						for(int k=0; k<result.length; k++){
							JSONObject itemJson = new JSONObject();
							itemJson.put("pk", (String)(result[k]));
							itemJson.put("name", (String)(result[k]));
							bodyMap.get(k).put(itemkey, itemJson);
						}
					}
				}
			}
		}
		for(int l=0; l<bodyMap.size(); l++){
			bodyarray.put(bodyMap.get(l));
		}
		return bodyarray;
	}
	
	/**
	 * ��ȡ��ʽ����
	 * 
	 */
	public List<String> getFormulasParamnames(String formula) throws Exception{
		List<String> paramnameList = new ArrayList<String>();
		String[] eArray = formula.split(";");
		for (int i = 0; i < eArray.length; i++) {
			eArray[i] = eArray[i].trim();
		}
		getFormulaParse().setExpressArray(eArray);
		VarryVO[] paramlist = getFormulaParse().getVarryArray();
		if (paramlist != null) {
			String[] paramnames = null;
			for (int i = 0; i < paramlist.length; i++) {
				VarryVO vo = paramlist[i];
				if (vo != null && vo.getVarry() != null && vo.getVarry().length > 0) {
					paramnames = vo.getVarry();
					for(String paramname : paramnames){
						if(!paramnameList.contains(paramname)){
							paramnameList.add(paramname);
						}
					}
				}
			}
		}
		return paramnameList;
	}
	
	/**
	 * ����༭��ʽ
	 * 
	 */
	public HashMap<String,Object> processEditFormulas(String formula,Map<String, Object> paramvalueMap) throws Exception{
		String[] eArray = formula.split(";");
		for (int i = 0; i < eArray.length; i++) {
			eArray[i] = eArray[i].trim();
		}
		getFormulaParse().setExpressArray(eArray);
		VarryVO[] varryVOList = getFormulaParse().getVarryArray();
		if (varryVOList != null) {
			Object[][] results = null;
			String[] paramnames = null;
			for (int j = 0; j < varryVOList.length; j++) {
				VarryVO vo = varryVOList[j];
				if (vo != null && vo.getVarry() != null && vo.getVarry().length > 0) {
					paramnames = vo.getVarry();
					for (int i = 0; i < paramnames.length; i++) {
						if (paramnames[i] == null){
							continue;
						}
						getFormulaParse().addVariable(paramnames[i], paramvalueMap.get(paramnames[i]));
					}
				}
			}
			try {
				results = getFormulaParse().getValueOArray();
				if (getFormulaParse().getError() != null){
					throw new BusinessException(getFormulaParse().getError());
				}
			} catch (Exception e) {
				throw new BusinessException("��ʽ�������,ԭ��:" + e.getMessage());
			}
			HashMap<String,Object> resultMap = new HashMap<String,Object>();
			for(int n = 0; n < varryVOList.length; n++){
				VarryVO varryVO = varryVOList[n];
				Object[] result = results[n];
				String itemkey = varryVO.getFormulaName();
				resultMap.put(itemkey, result[0]);
			}
			return resultMap;
		}
		return null;
	}
}

