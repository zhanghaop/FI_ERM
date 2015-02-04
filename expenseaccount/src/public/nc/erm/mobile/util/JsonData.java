package nc.erm.mobile.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nc.bs.logging.Logger;
import nc.erm.mobile.pub.formula.WebFormulaParser;
import nc.md.MDBaseQueryFacade;
import nc.md.data.access.DASFacade;
import nc.md.data.access.NCObject;
import nc.md.model.IBusinessEntity;
import nc.md.model.MetaDataException;
import nc.md.model.access.javamap.AggVOStyle;
import nc.md.model.access.javamap.BeanStyleEnum;
import nc.md.model.access.javamap.NCBeanStyle;
import nc.pubitf.bd.accessor.GeneralAccessorFactory;
import nc.pubitf.bd.accessor.IGeneralAccessor;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bill.pub.MiscUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.ExAggregatedVO;
import nc.vo.pub.SuperVO;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.BillTempletHeadVO;
import nc.vo.pub.bill.BillTempletVO;
import nc.vo.pub.bill.MetaDataPropertyAdpter;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;



public class JsonData
  implements Serializable, IBillItem
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_BODY_TABLECODE = ExAggregatedVO.defaultTableCode;

	public final String DEFAULT_BODY_TABLENAME = "body";

	public static final String DEFAULT_HEAD_TABBEDCODE = "main";

	public final String DEFAULT_HEAD_TABBEDNAME = "主表";

	public static final String DEFAULT_TAIL_TABBEDCODE = "tail";

	public final String DEFAULT_TAIL_TABBEDNAME = "表尾";


	protected JsonItem[] m_biHeadItems = null; // 表头元素数组

	protected JsonItem[] m_biTailItems = null; // 表尾元素数组
	private String cardStyle = null;

	protected Hashtable<String, JsonModel> hBillModels = new Hashtable<String, JsonModel>(); // tableCode
	// +
	// 表体数据控制模型

	protected Hashtable<String, JsonItem> hHeadItems = new Hashtable<String, JsonItem>(); // itemKey
	protected Hashtable<String, JsonItem> hTailItems = new Hashtable<String, JsonItem>();

	protected HashtableBillTabVO hBillTabs = new HashtableBillTabVO();

	BillTempletVO billTempletVO = null;

	/**
	 * JsonData 构造子注解.
	 */
	public JsonData() {
		super();
	}

	/**
	 * BillData 构造子注解.
	 */
	public JsonData(BillTempletVO newTempletVO) {
		super();
		initTempletData(newTempletVO);
	}

  
  private void initTempletData(BillTempletVO newTempletVO)
  {
    if (newTempletVO == null) {
      Logger.info("设置模板数据为空!");
      return;
    }
    
    this.billTempletVO = newTempletVO;
    this.billTempletVO.setParentToBody();
    
    BillTempletHeadVO headVO = newTempletVO.getHeadVO();
    BillTempletBodyVO[] bodyVO = newTempletVO.getBodyVO();
    
    if (headVO != null) {
      BillStructVO btVO = hBillTabs.initByHeadVO(headVO);
      if (btVO != null) {
			setCardStyle(btVO.getCardStyle());
		}
    }
    
    if (bodyVO != null) {
      initBodyVOs(bodyVO);
      hHeadItems = new Hashtable();
      hTailItems = new Hashtable();
      ArrayList<JsonItem> list = new ArrayList();
      
      int pos = -1;
      String code = null;
      for (int i = 0; i < bodyVO.length; i++) {
	        BillTempletBodyVO bVO = bodyVO[i];
	        JsonItem item = new JsonItem(bVO, bVO.getCardflag().booleanValue());
	       
	        if (item.isCard())
	        {
	          item.setReadOrder(i);
	          //如果第一个是表头，则一直找到下一个非表头，然后将这段插入
	          if ((list.size() > 0) && (code != null) && ((pos != item.getPos()) || (!code.equals(item.getTableCode()))))
	          {
	            addToHashtable((JsonItem[])list.toArray(new JsonItem[list.size()]));
	            list.clear();
	          }
	          list.add(item);
	          pos = item.getPos();
	          code = item.getTableCode();
	        }
      }
      
      //插入最后一段
      if (list.size() > 0) {
        addToHashtable((JsonItem[])list.toArray(new JsonItem[list.size()]));
        list.clear();
      }
      
      JsonItem[] items = getHeadItems();
//      addRelationItem(items);
    }
  }
  
  public BillTempletVO getBillTempletVO() {
	return billTempletVO;
  }
  /**
	 * 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem[] getTailItems() {
		return m_biTailItems;
	}
  /**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem[] getHeadTailItems() {
		return (JsonItem[]) nc.vo.bill.pub.MiscUtil.ArraysCat(getHeadItems(),
				getTailItems());
	}
	
	/**
	 * 创建日期:(2003-7-16 10:03:50)
	 * 
	 * @param newCardStyle
	 *            java.lang.String
	 */
	public void setCardStyle(java.lang.String newCardStyle) {
		cardStyle = newCardStyle;
	}
	/**
	 * 创建日期:(2003-7-16 10:03:50)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCardStyle() {
		return cardStyle;
	}
	
	/**
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public BillTabVO[] getBillBaseTabVOsByPosition(Integer position) {
		if (position == null)
			return null;
		String style = getCardStyle();
		if (style == null || style.trim().equals(BillStructVO.DEFAULT_CARD)) {
			if (position.intValue() == IBillItem.HEAD)
				return hBillTabs.getBaseTabVos(HEAD);
			else if (position.intValue() == IBillItem.BODY)
				return hBillTabs.getBaseTabVos(BODY);
			else if (position.intValue() == IBillItem.TAIL)
				return hBillTabs.getBaseTabVos(TAIL);
			return null;
		}
		return hBillTabs.getBaseTabVos(position);
	}
	/**
	 * 设置表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 */
	public void setHeadItem(String strKey, Object Value) {
		if (hHeadItems.containsKey(strKey)) {
			JsonItem item = hHeadItems.get(strKey);
			setHeadTailItem(item, Value);
		}
	}
	
	/**
	 * 设置表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 */
	private void setHeadTailItem(JsonItem item, Object value) {
		if (item != null) {
			item.setValue(value);
		}
	}
	
	
  public JSONObject transBillValueObjectToJson(Object o) throws JSONException, Exception{
	    JSONObject resultJson = new JSONObject();
		if (o == null) {
			return resultJson;
		}
		long t = System.currentTimeMillis();
		if(o instanceof AggregatedValueObject){
			JsonItem[] items = getHeadTailItems();
			if (items != null) {
				// 设置表头数据
				resultJson.put("head", getHeadVO((SuperVO)((AggregatedValueObject)o).getParentVO()));
				
				// 设置表体数据
				IBusinessEntity be = getBillTempletVO().getHeadVO()
						.getBillMetaDataBusinessEntity();
				//获得表体页签
				BillTabVO[] tabvos = getBillBaseTabVOsByPosition(IBillItem.BODY);
				if (tabvos != null) {
					NCObject ncobject = null;
					if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD)
						ncobject = DASFacade.newInstanceWithContainedObject(be, o);
					else if (be.getBeanStyle().getStyle() == BeanStyleEnum.NCVO
							|| be.getBeanStyle().getStyle() == BeanStyleEnum.POJO) {
						if (o instanceof AggregatedValueObject) {
							o = ((AggregatedValueObject) o).getParentVO();
							ncobject = DASFacade.newInstanceWithContainedObject(be, o);
						} else {
							ncobject = DASFacade.newInstanceWithContainedObject(be, o);
						}
					}
					JSONArray itemsarray = new JSONArray();
//					JSONObject tabformulars = new JSONObject();
					int itemno = 0;
					for (int i = 0; i < tabvos.length; i++) {
						//加载公式
						BillTabVO tabVO = tabvos[i];
						String tabcode = tabVO.getTabcode();
						JsonModel model = getBillModel(tabcode);
//						JSONObject tabformular = new JSONObject();
//						tabformular.put("classname", tabVO.getMetadataclass());
//						tabformular.put("formular", model.getEditFormular());
//						tabformulars.put(tabcode, tabformular);
						//转换表体
						NCObject[] ncos = (NCObject[]) ncobject
								.getAttributeValue(tabVO.getMetadatapath());
						if(ncos != null && ncos.length > 0){
							NCObject[] nctrans = new NCObject[ncos.length];
							tabcode = tabVO.getTabcode();
							model = getBillModel(tabcode);
							for(int ncindex = 0;ncindex<ncos.length;ncindex++){
								if(!ncos[ncindex].getAttributeValue("tablecode").equals(tabcode)){
									nctrans[ncindex] = null;
								}else{
									nctrans[ncindex] = ncos[ncindex];
								}
							}
							JSONArray bodyarray = model.setBodyObjectByMetaData(nctrans);
							if(bodyarray != null){
								for(int arrayindex = 0;arrayindex < bodyarray.length();arrayindex++){
									JSONObject bodyjson = (JSONObject) bodyarray.get(arrayindex);
									bodyjson.put("itemno", itemno);
									itemno++;
									bodyjson.put("tablecode", tabcode);
									bodyjson.put("tablename", tabVO.getTabname());
									itemsarray.put(bodyjson);
								}
							}
						}
					}
//					resultJson.put("formularlist", tabformulars);
					resultJson.put("itemlist", itemsarray);
					resultJson.put("itemnum", itemsarray.length());
				}
			} else {
				// 单表体，取得表体数据，得到name并返回
				Object[] vos = ((AggregatedValueObject) o).getChildrenVO();
				JSONObject bodyJson = new JSONObject();
				//获得表体页签
				BillTabVO[] tabvos = getBillBaseTabVOsByPosition(IBillItem.BODY);
				if (vos != null && tabvos != null
						&& tabvos[0].getBillMetaDataBusinessEntity() != null) {
					NCObject[] ncos = new NCObject[vos.length];
	
					for (int i = 0; i < ncos.length; i++) {
						ncos[i] = DASFacade.newInstanceWithContainedObject(
								tabvos[0].getBillMetaDataBusinessEntity(), vos[i]);
					}
					JsonModel model = getBillModel(getDefaultBodyTableCode());
					JSONArray bodyarray = model.setBodyObjectByMetaData(ncos);
					bodyJson.put(tabvos[0].getTabcode(), bodyarray);
				}
				resultJson.put("body", bodyJson);
				return resultJson;
			}
		} else if(o instanceof SuperVO){
			// 只返回表头数据
			resultJson.put("head", getHeadVO((SuperVO)o));
			return resultJson;
		}else if(o.getClass().isArray()){
			Object[] vos = (Object[]) o;
			//只返回表体数据
			JSONObject bodyJson = new JSONObject();
			//获得表体页签
			BillTabVO[] tabvos = getBillBaseTabVOsByPosition(IBillItem.BODY);
			if (vos != null && tabvos != null
					&& tabvos[0].getBillMetaDataBusinessEntity() != null) {
				NCObject[] ncos = new NCObject[vos.length];

				for (int i = 0; i < ncos.length; i++) {
					ncos[i] = DASFacade.newInstanceWithContainedObject(
							tabvos[0].getBillMetaDataBusinessEntity(), vos[i]);
				}
				JsonModel model = getBillModel(getDefaultBodyTableCode());
				JSONArray bodyarray = model.setBodyObjectByMetaData(ncos);
				bodyJson.put(tabvos[0].getTabcode(), bodyarray);
			}
			resultJson.put("body", bodyJson);
			return resultJson;
		}
		Logger.info("ExecBatchRefSetPk taken time:"
				+ (System.currentTimeMillis() - t) + "ms.");
		return resultJson;
	}
  
  public JSONObject getHeadVO(SuperVO head) throws Exception{
	    JsonItem[] items = getHeadTailItems();
	    JSONObject headJson = new JSONObject();
	    headJson.put("classname", getBillTempletVO().getHeadVO().getMetadataclass());
	    String key = JKBXHeaderVO.TOTAL;
	    Object value = head.getAttributeValue(key);
	    if(value == null)
	    	value = UFDouble.ZERO_DBL;
	    headJson.put(key, value);
		headJson.put(key+"_name", NumberFormatUtil.formatDouble((UFDouble)value));
		key = JKBXHeaderVO.SPZT;
	    value = head.getAttributeValue(key);
	    headJson.put(key, value);
		headJson.put(key+"_name", value);
		for (int i = 0; i < items.length; i++) {
			key = items[i].getKey();
			value = head.getAttributeValue(key);
//			headJson.put(key+"_editformulas", items[i].getEditFormulas());
			if(value != null){
				JSONObject itemJson = getJsonObjectFromItem(items[i],value);
				headJson.put(key, itemJson.get("pk"));
				headJson.put(key+"_name", itemJson.get("name"));
				//加载公式
				if(items[i].getLoadFormula() != null ){
					WebFormulaParser.getInstance().processFormulasForHead(headJson,items[i].getLoadFormula(),head);
				}
			}
		}
		return headJson;
  }
  
  public JSONObject getJsonObjectFromItem(JsonItem item,Object value) throws JSONException{
		JSONObject itemJson = new JSONObject();
		if(value != null){
			if(item.getDataType() == IBillItem.UFREF && item.getMetaDataProperty() != null){
				//如果是参照，就需要取参照的值
				itemJson.put("pk", value);
				IGeneralAccessor accessor = GeneralAccessorFactory.getAccessor(item.getMetaDataProperty().getRefBusinessEntity().getID());
				if(accessor != null){
					IBDData[] data = accessor.getDocbyPks(new String[]{value.toString()});
					if(data != null && data.length>0 && data[0] != null)
						itemJson.put("name", data[0].getName());
					else
						itemJson.put("name", value);
				}else{
					itemJson.put("name", "");
				}
			}else if(item.getDataType() == IBillItem.COMBO && item.getMetaDataProperty() != null){
				//如果是参照，就需要取参照的值
				itemJson.put("pk", value);
				String reftype = item.getRefType();
				if (reftype != null
						&& (reftype = reftype.trim()).length() > 0) {
					boolean isFromMeta = reftype
							.startsWith(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN);
					String reftype1 = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
					List<DefaultConstEnum> combodata = getInitData(reftype1, isFromMeta);
					for(int index=0;index<combodata.size();index++){
						DefaultConstEnum enumvalue = combodata.get(index);
						if(value.equals(enumvalue.getValue()))
							itemJson.put("name", enumvalue.getName());
					}
				}
			}else if(item.getDataType() == IBillItem.MONEY || item.getDataType() == IBillItem.DECIMAL){
				//NumberFormat 的 innerFormat方法直接拿过来用
				itemJson.put("pk", value);
				itemJson.put("name", NumberFormatUtil.formatDouble((UFDouble)value));
			}else if(item.getDataType() == IBillItem.DATE){
				itemJson.put("pk", value);
				String str = value.toString();
				itemJson.put("name", str.substring(0, 10));
			}else{
				itemJson.put("pk", value);
				itemJson.put("name", value);
			}
		}
		return itemJson;
  }
  /**
   * 根据枚举类型的值找对应的名称
   * @param comboitems
   * @param isFromMeta
   * @return
   */
  public static List<DefaultConstEnum> getInitData(String comboitems, boolean isFromMeta) {
		boolean isSX;
		ArrayList<String> list = new ArrayList<String>();

		String[] items = MiscUtil.getStringTokens(comboitems, ",");
		if (items != null) {
			// 返回索引
			String[] strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_DBFIELD };// , COMBOTYPE_INDEX_X
			// };

			// 获得下拉项目值
			strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_X, IBillItem.COMBOTYPE_VALUE,
					IBillItem.COMBOTYPE_VALUE_X };

			isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
			boolean isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX

			if (getStringIndexOfArray(strArray, items[0]) >= 0) {
				for (int i = 1; i < items.length; i++) {
					list.add(items[i].trim());
				}
			}

			// 解析值
			if (list.size() > 0) {
				String[] ss = list.toArray(new String[list.size()]);
				if (isSX || isIX) {
					List<DefaultConstEnum> ces = new ArrayList<DefaultConstEnum>();
					Object value = null;
					for (int i = 0; i < ss.length; i++) {

						int pos = ss[i].indexOf('=');

						String name = pos >= 0 ? ss[i].substring(0, pos)
								: ss[i];
						name = getDecodeStr(name, isFromMeta);

						if (pos >= 0) {
							value = getDecodeStr(ss[i].substring(pos + 1),isFromMeta);
							if (isIX) {
								value = Integer.valueOf(value.toString());
							}
						} else {
							
							if (isSX) {
								value = getDecodeStr(ss[i], isFromMeta);
							} else {
								value = Integer.valueOf(i);
							}
						}

						ces.add(new DefaultConstEnum(value, name));
					}
					return ces;
				}
			}
		}
		return null;
	}
  
  //找s在ss数组中的位置
  private static int getStringIndexOfArray(final String[] ss, final String s) {
		if (ss != null) {
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].equals(s))
					return i;
			}
		}
		return -1;
	}
  
  //将枚举参照的值转换成中文
  private static String getDecodeStr(String str, boolean isFromMeta) {
		if (isFromMeta) {
			try {
				return URLDecoder.decode(str, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				Logger.debug(e.getMessage());
			}
		}
		return str;
	}
  
    /**
	 * getHeadTableCodes or getTableCodes. 创建日期:(2002-12-5 11:10:59)
	 */
	public String[] getTableCodes(int pos) {
		return hBillTabs.getTableCodes(pos);
	}
	
  public String[] getBodyTableCodes() {
		return getTableCodes(BODY);
	}
    /**
	 * 创建日期:(2003-4-22 16:44:08)
	 * 
	 * @return java.lang.String
	 */
	String getDefaultBodyTableCode() {
		String tableCode = DEFAULT_BODY_TABLECODE;
		String[] bodyCodes = getBodyTableCodes();
		if (bodyCodes != null && bodyCodes.length >= 1)
			tableCode = bodyCodes[0];
		return tableCode;
	}
	
	
    /**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem getHeadTailItem(String strKey) {
		JsonItem item = getHeadItem(strKey);
		if (item == null)
			return getTailItem(strKey);
		return item;
	}
	/**
	 * 得到表尾关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem getTailItem(String strKey) {
		if (strKey != null && hTailItems != null
				&& hTailItems.containsKey(strKey))
			return (JsonItem) hTailItems.get(strKey);
		else
			return null;
	}
  
  /**
	 * 得到表头关键字对应元素. 创建日期:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem getHeadItem(String strKey) {
		if (strKey != null && hHeadItems != null
				&& hHeadItems.containsKey(strKey))
			return (JsonItem) hHeadItems.get(strKey);
		else
			return null;
	}
  public JsonItem[] getHeadItems()
  {
    return m_biHeadItems;
  }
  
  private void addToHashtable(JsonItem[] items)
  {
	    if ((items == null) || (items.length == 0)) {
	      return;
	    }
	    //items中的pos都一样，判断一个就可以了
	    JsonItem item = items[0];
	    int pos = item.getPos();
	    Hashtable<String, JsonItem> hashItems = null;
	    if (pos == HEAD)
			hashItems = hHeadItems;
		else if (pos == TAIL)
			hashItems = hTailItems;
	    
	    if (hashItems != null) {
	      for (int i = 0; i < items.length; i++) {
	        hashItems.put(items[i].getKey(), items[i]);
	      }
	    }
	    
	    // 对于新的页签,增加编码和名称的对照
	    if (!hBillTabs.containsKey(pos + item.getTableCode())) {
	      String tablename = item.getTableName();
	      if (tablename == null)
	        tablename = item.getTableCode();
	      hBillTabs.add(pos, item.getTableCode(), tablename);
	    }
	    if (pos == BODY) {//BODY
	      JsonModel model = getBillModel(item.getTableCode());
	      if (model == null)
	        model = addBillModel(item.getTableCode());
	      model.setBodyItems(items);
	    }
	    else if (pos == HEAD) {
	      m_biHeadItems = ((JsonItem[])MiscUtil.ArraysCat(this.m_biHeadItems, items));
	    }
	    else if (pos == TAIL) {
	      m_biTailItems = ((JsonItem[])MiscUtil.ArraysCat(this.m_biTailItems, items));
	    }
  }
  public static String getDefaultTableCode(int pos)
  {
	    switch (pos) {
	    case 0: 
	      return "main";
	    
	
	    case 1: 
	      return ExAggregatedVO.defaultTableCode;
	    
	    case 2: 
	      return "tail";
	    }
	    return null;
  }
  
  private void initBodyVOs(BillTempletBodyVO[] bodys)
  {
	    if ((bodys == null) || (bodys.length == 0)) {
	      return;
	    }
	    
	    for (int i = 0; i < bodys.length; i++) { 
	      String code;
	      if (((code = bodys[i].getTableCode()) == null) || (code.trim().length() == 0)) {
	        int pos = bodys[i].getPos().intValue();
	        bodys[i].setTableCode(getDefaultTableCode(pos));
//	        bodys[i].setTableName(BillUtil.getDefaultTableName(pos));
	      }
	    }
	    ItemSortUtil.sortBodyVOsByProps(bodys, new String[] { "pos", "table_code", "showorder" });
  }
  
  /**
	 * 得到表体表模式. 创建日期:(01-2-23 15:23:11)
	 * 
	 * @return ufbill.BillModel
	 */
	public JsonModel getBillModel(String tableCode) {
		if (hBillModels == null)
			hBillModels = new Hashtable<String, JsonModel>();
		if (!hBillModels.containsKey(tableCode)) {
			JsonModel billModel = null;
			BillTabVO btvo = hBillTabs.get(BODY + tableCode);
			String baseTab = null;
			if (btvo != null) {
				baseTab = btvo.getBasetab();
			}
			if (baseTab != null) {
				billModel = hBillModels.get(baseTab);
			}
			return billModel;
		}
		return hBillModels.get(tableCode);
	}
	
	/**
	 * 得到表体表模式. 创建日期:(01-2-23 15:23:11)
	 * 
	 * @return ufbill.BillModel
	 */
	private JsonModel addBillModel(String tableCode) {
		if (hBillModels == null)
			hBillModels = new Hashtable<String, JsonModel>();
		if (!hBillModels.containsKey(tableCode)) {
			JsonModel billModel = null;
			BillTabVO btvo = (BillTabVO) hBillTabs.get(BODY + tableCode);
			String baseTab = null;
			if (btvo != null) {
				baseTab = btvo.getBasetab();
			}
			if (baseTab != null) {
				if (!hBillModels.containsKey(baseTab)) {
					JsonModel billmodel = new JsonModel();
					billmodel.setTabvo(btvo);
					hBillModels.put(baseTab, billModel);
				} else
					billModel = hBillModels.get(baseTab);
			} else {
				billModel = new JsonModel();
				billModel.setTabvo(btvo);
				hBillModels.put(tableCode, billModel);
			}
			return billModel;
		}
		return hBillModels.get(tableCode);
	}

	public void setVoFromItem(JsonItem item,SuperVO parent,String headvalue){
		String key = item.getKey();
		if(item != null){
            if(item != null && !StringUtil.isEmpty(headvalue)){
//            ncobject.setAttributeValue(item.getMetaDataProperty().getAttribute(), headvalue);
	            if(item.getDataType() == IBillItem.DATE){
	            	parent.setAttributeValue(key, new UFDate(headvalue));
	            }else if(item.getDataType() == IBillItem.MONEY || item.getDataType() == IBillItem.DECIMAL){
	            	parent.setAttributeValue(key, new UFDouble(headvalue));
	            }else if(item.getDataType() == IBillItem.DATETIME){
	            	parent.setAttributeValue(key, new UFDateTime(headvalue));
	            }else if(item.getDataType() == IBillItem.COMBO){
	            	String reftype = item.getRefType();
	            	reftype = reftype.replaceFirst(MetaDataPropertyAdpter.COMBOBOXMETADATATOKEN,"");
	            	boolean isSX = true;
	            	boolean isIX = false;
	        		ArrayList<String> list = new ArrayList<String>();

	        		String[] items = MiscUtil.getStringTokens(reftype, ",");
	        		if (items != null) {
		    			isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
		    			isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX
	        		}
	        		if(isSX){
	        			try{
		            		parent.setAttributeValue(key, headvalue);
		            	}catch(Exception e){
		            		return;
		            	}
	        		}else
	        			parent.setAttributeValue(key, new Integer(headvalue));
	            }else{
	            	try{
	            		parent.setAttributeValue(key, headvalue);
	            	}catch(Exception e){
	            		return;
	            	}
	            }
            }
        }
    }  
	
	public AggregatedValueObject transJsonToBillValueObject(JSONObject json) throws JSONException, BusinessException {
		//设置表头的值
		JSONObject head = (JSONObject)json.get("head");
		IBusinessEntity be = getBillTempletVO().getHeadVO()
				.getBillMetaDataBusinessEntity();
		AggregatedValueObject aggvo = null;
		if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD){
			//根据元数据类型得到应该是什么类型的vo
			AggVOStyle aggstyle = (AggVOStyle)be.getBeanStyle();
			aggvo = (AggregatedValueObject) aggstyle.newInstance(null);
//			NCObject ncobject = DASFacade.newInstanceWithContainedObject(be, aggvo);
		}
		//给表头vo赋值	
		SuperVO parent = (SuperVO) aggvo.getParentVO();
		Iterator<String> keys = head.keys();  
        JSONObject jo = null;  
        String headvalue;  
        String key;  
        while(keys.hasNext()){
            key = keys.next();  
            headvalue = (String) head.get(key);
            JsonItem item = getHeadTailItem(key);
            setVoFromItem(item,parent,headvalue);
        }  
        
        //设置表体的值
        JSONArray bodyvalue; 
        JSONObject body = (JSONObject)json.get("body");
        List<SuperVO> childrenlist = new ArrayList<SuperVO>();
        keys = body.keys();   
        while(keys.hasNext()){
            key = keys.next();  
            //表体是数组的形式
            bodyvalue = (JSONArray) body.get(key);
            JsonModel model = getBillModel(key);
            IBusinessEntity bodybean = null;
            NCBeanStyle bodystyle = null;
            
            try {
            	bodybean = MDBaseQueryFacade.getInstance().getBusinessEntityByFullName(model.getTabvo().getMetadataclass());
			} catch (MetaDataException e) {
				Logger.debug(e);
			}
            if ((bodybean.getBeanStyle().getStyle() == BeanStyleEnum.NCVO) || (bodybean.getBeanStyle().getStyle() == BeanStyleEnum.POJO)){
            	bodystyle = (NCBeanStyle) bodybean.getBeanStyle();
            }
        	for(int i=0;i<bodyvalue.length();i++){
        		SuperVO bodyvo = (SuperVO) bodystyle.newInstance(null);
        		jo = (JSONObject)bodyvalue.get(i);
        		model.translateJsonToValueObject(bodyvo,jo);
        		childrenlist.add(bodyvo);
        	}
            
        }  
        aggvo.setChildrenVO(childrenlist.toArray(new SuperVO[0]));
		return aggvo;
	}

	public static String getStringValue(Object value){
		return value == null? "":value.toString();
	}
	
	public AggregatedValueObject transMapToBillValueObject(
			Map<String, Object> valuemap) {
		//设置表头的值
		IBusinessEntity be = getBillTempletVO().getHeadVO()
				.getBillMetaDataBusinessEntity();
		AggregatedValueObject aggvo = null;
		if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD){
			//根据元数据类型得到应该是什么类型的vo
			AggVOStyle aggstyle = (AggVOStyle)be.getBeanStyle();
			aggvo = (AggregatedValueObject) aggstyle.newInstance(null);
		}
		//给表头vo赋值	
		SuperVO parent = (SuperVO) aggvo.getParentVO();
		Set<String> mapkeys = valuemap.keySet();  
		Iterator<String> keys = mapkeys.iterator();
		String headvalue;  
        String key;  
        while(keys.hasNext()){
            key = keys.next();  
            if("items".equals(key) || "attachment".equals(key)){
				continue;
			}
            headvalue = getStringValue(valuemap.get(key));
            JsonItem item = getHeadTailItem(key);
            setVoFromItem(item,parent,headvalue);
        }  
        
        //设置表体的值
        List<Map<String, Object>> items = (List<Map<String, Object>>) valuemap.get("items");
        List<SuperVO> childrenlist = new ArrayList<SuperVO>();
        if(items != null && !items.isEmpty()){ 
        	for(int i=0;i<items.size();i++){
        		Map<String, Object> itemvalue = items.get(i);
        		String amountvalue = (String) itemvalue.get("amount");
				if(StringUtil.isEmpty(amountvalue)){
					continue;
				}
				key = (String) itemvalue.get("tablecode");
				JsonModel model = getBillModel(key);
	            IBusinessEntity bodybean = null;
	            NCBeanStyle bodystyle = null;
	            try {
	            	bodybean = MDBaseQueryFacade.getInstance().getBusinessEntityByFullName(model.getTabvo().getMetadataclass());
				} catch (MetaDataException e) {
					Logger.debug(e);
				}
	            if ((bodybean.getBeanStyle().getStyle() == BeanStyleEnum.NCVO) || (bodybean.getBeanStyle().getStyle() == BeanStyleEnum.POJO)){
	            	bodystyle = (NCBeanStyle) bodybean.getBeanStyle();
	            }
	            SuperVO bodyvo = (SuperVO) bodystyle.newInstance(null);
	            for (Entry<String, Object> fieldvalues : itemvalue.entrySet()) {
					key = fieldvalues.getKey();
					Object value = fieldvalues.getValue();
					if("amount".equals(key)){
						value = new UFDouble((String)value);
					}
					bodyvo.setAttributeValue(key, value);
				} 
//	            model.translateJsonToValueObject(bodyvo,jo);
        		childrenlist.add(bodyvo);
        	}
        }
        
        aggvo.setChildrenVO(childrenlist.toArray(new SuperVO[0]));
		return aggvo;
	}

	
}