package nc.erm.mobile.eventhandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import nc.arap.mobile.itf.IWebPubService;
import nc.bs.framework.common.NCLocator;
import nc.erm.mobile.function.ExecuteQueryFunction;
import nc.erm.mobile.function.FunctionResultVO;
import nc.erm.mobile.function.InterfaceFunction;
import nc.erm.mobile.function.MainJobDeptFunction;
import nc.erm.mobile.pub.formula.WebFormulaParser;
import nc.vo.pub.BusinessException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pubapp.pattern.exception.ExceptionUtils;

import org.codehaus.jettison.json.JSONObject;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 
 * @author gaotn
 *
 */

public abstract class AbstractEditeventHandler {
	
	public JSONObject process(String jSONString) throws BusinessException{
		JSONObject jSONObject = new JSONObject();
		JsonVoTransform jsonVoTransform = new JsonVoTransform(jSONString);
		handleEditFormulas(jsonVoTransform);
		handleEditeventConfig(jsonVoTransform);
		handleEditevent(jsonVoTransform);
		return jsonVoTransform.getjSONObject();
	}
	
	public abstract void handleEditevent(JsonVoTransform jsonVoTransform) throws BusinessException;
	
	/*
	 * 处理编辑公式
	 */
	private void handleEditFormulas(JsonVoTransform jsonVoTransform){
		String formula = jsonVoTransform.getEditItemInfoVO().getFormula();    //元素编辑公式
		int selectrow = jsonVoTransform.getEditItemInfoVO().getSelectrow();    //行信息
		String classname = jsonVoTransform.getEditItemInfoVO().getClassname();    //元数据对象
		try {
			if(formula != null && !"".equals(formula)){
				List<String> paramnameList = WebFormulaParser.getInstance().getFormulasParamnames(formula);
				Map<String, Object> paramvalueMap = new HashMap<String,Object>();
				SuperVO superVO;
				if(selectrow < 0){
					superVO = jsonVoTransform.getHeadVO();
				} else {
					superVO = jsonVoTransform.getBodysMap().get(classname).get(selectrow);
				}
				if(paramnameList.size() > 0){
					int count = paramnameList.size();
					for(int n = 0; n < count; n++){
						paramvalueMap.put(paramnameList.get(n), 
								superVO.getAttributeValue(paramnameList.get(n)));
					}
				}
				HashMap<String,Object> resultMap = WebFormulaParser.getInstance().processEditFormulas(formula,paramvalueMap);
				if(resultMap != null && resultMap.size() > 0){
					Iterator resultMapI = resultMap.keySet().iterator();
					while(resultMapI.hasNext()){
						String itemId = (String)resultMapI.next();
						Object itemValue = resultMap.get(itemId);
						if(selectrow < 0){
							jsonVoTransform.getItemValueInfoVO().setHeadItemValue(itemId, itemValue);
						} else {
							jsonVoTransform.getItemValueInfoVO().setBodyItemValue(classname, selectrow, itemId, itemValue);
						}
					}
				}
			}
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("处理编辑公式异常：" + e.getMessage());
		}
	}
	
	/*
	 * 处理配置文件
	 */
	private void handleEditeventConfig(JsonVoTransform jsonVoTransform){
		String editItemid = "";
		if(jsonVoTransform.getEditItemInfoVO().isHead()){
			editItemid = jsonVoTransform.getEditItemInfoVO().getId();
		} else {
			editItemid = jsonVoTransform.getEditItemInfoVO().getClassname() + EditeventConst.TOKEN + jsonVoTransform.getEditItemInfoVO().getId();
		}
		String fileName="";
		try {
			fileName = this.getClass().getResource("")
					+ "editevent_config.xml";
			SAXBuilder builder = new SAXBuilder(false);    //指定解析器
            Document doc = builder.build(fileName);    //得到Document
            Element items = doc.getRootElement();    //得到根元素
            List itemList = items.getChildren("item");    //获取所有item
            for (Iterator itemIte = itemList.iterator(); itemIte.hasNext();){
            	Element item = (Element)itemIte.next();
            	String itemkey = item.getChild("itemkey").getText();
            	if(!editItemid.equals(itemkey)){
            		continue;
            	}
            	String listener = item.getChild("listener").getText();
            	if(listener != null && !"".equals(listener)){
            		InterfaceEditeventListener interfaceEditeventListener = (InterfaceEditeventListener)
            				Class.forName(listener).newInstance();
            		interfaceEditeventListener.process(jsonVoTransform);
            	}
            	handleValueinfo(jsonVoTransform,item);
            	handleEnabledinfo(jsonVoTransform,item);
            	handleFilterinfo(jsonVoTransform,item);
            }
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("处理配置文件异常：" + e.getMessage());
		}
	}
	
	/*
	 * 处理配置文件的值变化信息
	 */
	private void handleValueinfo(JsonVoTransform jsonVoTransform,Element item){
		try {
			Object editItemValue = jsonVoTransform.getEditItemInfoVO().getValue();
			int selectrow = jsonVoTransform.getEditItemInfoVO().getSelectrow();
			String editItemClassname = jsonVoTransform.getEditItemInfoVO().getClassname();
			String editItemId = jsonVoTransform.getEditItemInfoVO().getId();
			List<InterfaceFunction> interfaceFunctions = new ArrayList<InterfaceFunction>();
			interfaceFunctions.add(new MainJobDeptFunction());
			interfaceFunctions.add(new ExecuteQueryFunction());
			Element valueinfo = item.getChild("valueinfo");    //获取valueinfo
			if(valueinfo == null){
				return;
			}
	    	List valueitemList = valueinfo.getChildren("valueitem");    //获取所有valueitem
	    	for(Iterator valueitemIte = valueitemList.iterator();valueitemIte.hasNext();){
	    		Element valueitem = (Element)valueitemIte.next();
	    		String valueitemid = valueitem.getChild("valueitemid").getText();
	    		String valuetype = valueitem.getChild("valuetype").getText();
	    		String value = valueitem.getChild("value").getText();
	    		if("function".equals(valuetype)){
	    			for(InterfaceFunction function : interfaceFunctions){
	    				String functionCode = "";
	    				Object parameter = null;
	    				if(value.contains(EditeventConst.SPLIT_TOKEN)){
	    					String[] codeparameter = value.split("\\"+EditeventConst.SPLIT_TOKEN);
	    					if(codeparameter.length == 2){
	    						functionCode = codeparameter[0];
	    						parameter = getParameter(jsonVoTransform, codeparameter[1]);
	    					}
	    				} else {
	    					functionCode = value;
	    					parameter = editItemValue;
	    				}
	    				if(function.getCode().equals(functionCode)){
	    					FunctionResultVO functionResultVO = function.value(parameter);
	    					ValueDetailInfoVO valueDetailInfoVO = new ValueDetailInfoVO();
	    					valueDetailInfoVO.setValue(functionResultVO.getRefPK());
	    					valueDetailInfoVO.setCode(functionResultVO.getRefCode());
	    					valueDetailInfoVO.setName(functionResultVO.getRefName());
	    					if(valueitemid.contains(EditeventConst.TOKEN)){
	    						String[] classnameid = valueitemid.split(EditeventConst.TOKEN);
	    						if(classnameid.length == 2){
	    							String classname = classnameid[0];
		    						String id = classnameid[1];
		    						jsonVoTransform.getItemValueInfoVO().setBodyItemValue(classname, selectrow, id, valueDetailInfoVO);
	    						}
	    					} else {
	    						jsonVoTransform.getItemValueInfoVO().setHeadItemValue(valueitemid, valueDetailInfoVO);
	    					}
	    				}
	    			}
	    		} else if("metadata".equals(valuetype)){
	    			IWebPubService iWebPubService = (IWebPubService) (NCLocator.getInstance()
	    					.lookup(IWebPubService.class));
	    			Object relationItemValue = iWebPubService.getRelationItemValue(editItemClassname,editItemId,value,editItemValue);
	    			if(valueitemid.contains(EditeventConst.TOKEN)){
						String[] classnameid = valueitemid.split(EditeventConst.TOKEN);
						if(classnameid.length == 2){
							String classname = classnameid[0];
    						String id = classnameid[1];
    						jsonVoTransform.getItemValueInfoVO().setBodyItemValue(classname, selectrow, id, relationItemValue);
						}
					} else {
						jsonVoTransform.getItemValueInfoVO().setHeadItemValue(valueitemid, relationItemValue);
					}
	    			
	    		}
	    	}
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("处理配置文件的值变化信息异常：" + e.getMessage());
		}
	}
	
	private String getParameter(JsonVoTransform jsonVoTransform, String parameter){
		int selectrow = jsonVoTransform.getEditItemInfoVO().getSelectrow();
		while(parameter.contains(EditeventConst.PARAMETER_TOKEN_BEGIN)){
			String itemkey = parameter.substring(parameter.indexOf(EditeventConst.PARAMETER_TOKEN_BEGIN)+1, 
					parameter.indexOf(EditeventConst.PARAMETER_TOKEN_END));
			Object value = null;
			if(itemkey.contains(EditeventConst.TOKEN)){
				String[] classnameid = itemkey.split(EditeventConst.TOKEN);
				if(classnameid.length == 2){
					String classname = classnameid[0];
					String id = classnameid[1];
					value = jsonVoTransform.getBodyValueAt(classname, selectrow, id);
				}
			} else {
				value = jsonVoTransform.getHeadValue(itemkey);
			}
			parameter = parameter.replaceAll("\\" + EditeventConst.PARAMETER_TOKEN_BEGIN + itemkey + 
					"\\" + EditeventConst.PARAMETER_TOKEN_END, value == null ? "" : value.toString());
		}
		return parameter;
	}
	
	/*
	 * 处理配置文件的可编辑性信息
	 */
	private void handleEnabledinfo(JsonVoTransform jsonVoTransform,Element item){
		try {
			Element enabledinfo = item.getChild("enabledinfo");    //获取enabledinfo
			if(enabledinfo == null){
				return;
			}
	    	List enableditemList = enabledinfo.getChildren("enableditem");    //获取所有enableditem
	    	for(Iterator enableditemIte = enableditemList.iterator();enableditemIte.hasNext();){
	    		Element enableditem = (Element)enableditemIte.next();
	    		String enableditemid = enableditem.getChild("enableditemid").getText();
	    		String requirement = enableditem.getChild("requirement").getText();
	    		String enabled = enableditem.getChild("enabled").getText();
	    		if(handleRequirement(jsonVoTransform,requirement)){
	    			if(enableditemid.contains(EditeventConst.TOKEN)){
						String[] classnameid = enableditemid.split(EditeventConst.TOKEN);
						if(classnameid.length == 2){
							String classname = classnameid[0];
    						String id = classnameid[1];
    						jsonVoTransform.getEnabledItemInfoVO().setBodyItemEnabled(classname, id, new UFBoolean(enabled));
						}
					} else {
						jsonVoTransform.getEnabledItemInfoVO().setHeadItemEnabled(enableditemid, new UFBoolean(enabled));
					}
	    		} else {
	    			if(enableditemid.contains(EditeventConst.TOKEN)){
						String[] classnameid = enableditemid.split(EditeventConst.TOKEN);
						if(classnameid.length == 2){
							String classname = classnameid[0];
    						String id = classnameid[1];
    						if("true".equals(enabled)){
    							jsonVoTransform.getEnabledItemInfoVO().setBodyItemEnabled(classname, id, UFBoolean.FALSE);
    		    			} else {
    		    				jsonVoTransform.getEnabledItemInfoVO().setBodyItemEnabled(classname, id, UFBoolean.TRUE);
    		    			}
						}
					} else {
						if("true".equals(enabled)){
		    				jsonVoTransform.getEnabledItemInfoVO().setHeadItemEnabled(enableditemid, UFBoolean.FALSE);
		    			} else {
		    				jsonVoTransform.getEnabledItemInfoVO().setHeadItemEnabled(enableditemid, UFBoolean.TRUE);
		    			}
					}
	    		}
	    	}
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("处理配置文件的可编辑性信息异常：" + e.getMessage());
		}
	}
	/*
	 * 处理条件判断信息
	 */
	private boolean handleRequirement(JsonVoTransform jsonVoTransform,String requirement){
		int selectrow = jsonVoTransform.getEditItemInfoVO().getSelectrow();
		String[] args = null;
		String itemId = null;
		String itemValue = null;
		if(requirement.contains("!=")){
			args = requirement.split("!=");
			if(args.length != 2){
				return false;
			}
			itemId = args[0];
			itemValue = args[1];
			if(itemId.contains(EditeventConst.TOKEN)){
				String[] classnameid = itemId.split(EditeventConst.TOKEN);
				if(classnameid.length == 2){
					String classname = classnameid[0];
					String id = classnameid[1];
					if(!(jsonVoTransform.getBodyValueAt(classname, selectrow, id).toString()).equals(itemValue)){
						return true;
					}
				}
			} else {
				if(!(jsonVoTransform.getHeadValue(itemId).toString()).equals(itemValue)){
					return true;
				}
			}
		} else if(requirement.contains("=")){
			args = requirement.split("=");
			if(args.length != 2){
				return false;
			}
			itemId = args[0];
			itemValue = args[1];
			if(itemId.contains(EditeventConst.TOKEN)){
				String[] classnameid = itemId.split(EditeventConst.TOKEN);
				if(classnameid.length == 2){
					String classname = classnameid[0];
					String id = classnameid[1];
					if((jsonVoTransform.getBodyValueAt(classname, selectrow, id).toString()).equals(itemValue)){
						return true;
					}
				}
			} else {
				if((jsonVoTransform.getHeadValue(itemId).toString()).equals(itemValue)){
					return true;
				}
			}
		}
		return false;
	}
	
	
	/*
	 * 处理配置文件的过滤信息
	 */
	private void handleFilterinfo(JsonVoTransform jsonVoTransform,Element item){
		try {
			Element filterinfo = item.getChild("filterinfo");    //获取filterinfo
			if(filterinfo == null){
				return;
			}
	    	List filteritemList = filterinfo.getChildren("filteritem");    //获取所有filteritem
	    	for(Iterator filteritemIte = filteritemList.iterator();filteritemIte.hasNext();){
	    		Element filteritem = (Element)filteritemIte.next();
	    		String filteritemid = filteritem.getChild("filteritemid").getText();
	    		if(filteritemid == null || "".equals(filteritemid)){
	    			continue;
	    		}
	    		String columnname = filteritem.getChild("columnname").getText();
//	    		String filtersql = filteritem.getChild("filtersql").getText();
//	    		if(filtersql == null){
//	    			filtersql = "";
//	    		} else if(!"".equals(filtersql)){
//	    			filtersql = getParameter(jsonVoTransform,filtersql);
//	    		}
	    		JSONObject filteritemJ = new JSONObject();
	    		filteritemJ.put(columnname, jsonVoTransform.getEditItemInfoVO().getValue());
//	    		filteritemJ.put("filtersql", filtersql);
	    		if(filteritemid.contains(EditeventConst.TOKEN)){
					String[] classnameid = filteritemid.split(EditeventConst.TOKEN);
					if(classnameid.length == 2){
						String classname = classnameid[0];
						String id = classnameid[1];
						jsonVoTransform.getFilterItemInfoVO().setBodyItemFilter(classname, id, filteritemJ);
					}
				} else {
					jsonVoTransform.getFilterItemInfoVO().setHeadItemFilter(filteritemid, filteritemJ);
				}
	    	}
		} catch (Exception e) {
			ExceptionUtils.wrappBusinessException("处理配置文件的过滤信息异常：" + e.getMessage());
		}
	}
}
