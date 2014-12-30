package nc.erm.mobile.util;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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
import nc.ui.bd.mmpub.DataDictionaryReader;
import nc.ui.pub.beans.constenum.DefaultConstEnum;
import nc.ui.pub.bill.HashtableBillTabVO;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.accessor.IBDData;
import nc.vo.bill.pub.MiscUtil;
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

	public final String DEFAULT_HEAD_TABBEDNAME = "����";

	public static final String DEFAULT_TAIL_TABBEDCODE = "tail";

	public final String DEFAULT_TAIL_TABBEDNAME = "��β";


	protected JsonItem[] m_biHeadItems = null; // ��ͷԪ������

	protected JsonItem[] m_biTailItems = null; // ��βԪ������
	private String cardStyle = null;

	protected Hashtable<String, JsonModel> hBillModels = new Hashtable<String, JsonModel>(); // tableCode
	// +
	// �������ݿ���ģ��

	protected Hashtable<String, JsonItem> hHeadItems = new Hashtable<String, JsonItem>(); // itemKey
	protected Hashtable<String, JsonItem> hTailItems = new Hashtable<String, JsonItem>();

	protected HashtableBillTabVO hBillTabs = new HashtableBillTabVO();

	BillTempletVO billTempletVO = null;

	/**
	 * JsonData ������ע��.
	 */
	public JsonData() {
		super();
	}

	/**
	 * BillData ������ע��.
	 */
	public JsonData(BillTempletVO newTempletVO) {
		super();
		initTempletData(newTempletVO);
	}

  
  private void initTempletData(BillTempletVO newTempletVO)
  {
    if (newTempletVO == null) {
      Logger.info("����ģ������Ϊ��!");
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
	          //�����һ���Ǳ�ͷ����һֱ�ҵ���һ���Ǳ�ͷ��Ȼ����β���
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
      
      //�������һ��
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
	 * ��������:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem[] getTailItems() {
		return m_biTailItems;
	}
  /**
	 * �õ���ͷ�ؼ��ֶ�ӦԪ��. ��������:(01-2-23 14:22:07)
	 * 
	 * @return ufbill.BillItem[]
	 */
	public JsonItem[] getHeadTailItems() {
		return (JsonItem[]) nc.vo.bill.pub.MiscUtil.ArraysCat(getHeadItems(),
				getTailItems());
	}
	
	/**
	 * ��������:(2003-7-16 10:03:50)
	 * 
	 * @param newCardStyle
	 *            java.lang.String
	 */
	public void setCardStyle(java.lang.String newCardStyle) {
		cardStyle = newCardStyle;
	}
	/**
	 * ��������:(2003-7-16 10:03:50)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getCardStyle() {
		return cardStyle;
	}
	
	/**
	 * ��������:(2003-6-30 14:46:50)
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
	 * ���ñ�ͷ�ؼ��ֶ�ӦԪ��. ��������:(01-2-23 14:22:07)
	 */
	public void setHeadItem(String strKey, Object Value) {
		if (hHeadItems.containsKey(strKey)) {
			JsonItem item = hHeadItems.get(strKey);
			setHeadTailItem(item, Value);
		}
	}
	
	/**
	 * ���ñ�ͷ�ؼ��ֶ�ӦԪ��. ��������:(01-2-23 14:22:07)
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
				// ���ñ�ͷ����
				resultJson.put("head", getHeadVO((SuperVO)((AggregatedValueObject)o).getParentVO()));
				
				// ���ñ�������
				IBusinessEntity be = getBillTempletVO().getHeadVO()
						.getBillMetaDataBusinessEntity();
				//��ñ���ҳǩ
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
					JSONObject bodyJson = new JSONObject();
					for (int i = 0; i < tabvos.length; i++) {
						BillTabVO tabVO = tabvos[i];
						NCObject[] ncos = (NCObject[]) ncobject
								.getAttributeValue(tabVO.getMetadatapath());
	
						JsonModel model = getBillModel(tabVO.getTabcode());
						JSONArray bodyarray = model.setBodyObjectByMetaData(ncos);
						bodyJson.put(tabVO.getTabcode(), bodyarray);
					}
					resultJson.put("body", bodyJson);
				}
			} else {
				// �����壬ȡ�ñ������ݣ��õ�name������
				Object[] vos = ((AggregatedValueObject) o).getChildrenVO();
				JSONObject bodyJson = new JSONObject();
				//��ñ���ҳǩ
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
			// ֻ���ر�ͷ����
			resultJson.put("head", getHeadVO((SuperVO)o));
			return resultJson;
		}else if(o.getClass().isArray()){
			Object[] vos = (Object[]) o;
			//ֻ���ر�������
			JSONObject bodyJson = new JSONObject();
			//��ñ���ҳǩ
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
		for (int i = 0; i < items.length; i++) {
			String key = items[i].getKey();
			Object value = head.getAttributeValue(key);
			if(value != null){
				JSONObject itemJson = getJsonObjectFromItem(items[i],value);
				headJson.put(key, itemJson);
				//���ع�ʽ
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
				//����ǲ��գ�����Ҫȡ���յ�ֵ
				itemJson.put("pk", value);
				IGeneralAccessor accessor = GeneralAccessorFactory.getAccessor(item.getMetaDataProperty().getRefBusinessEntity().getID());
				if(accessor != null){
					IBDData[] data = accessor.getDocbyPks(new String[]{value.toString()});
					if(data != null && data.length>0 && data[0] != null)
						itemJson.put("name", data[0].getName());
				}else{
					itemJson.put("name", "");
				}
			}else if(item.getDataType() == IBillItem.COMBO && item.getMetaDataProperty() != null){
				//����ǲ��գ�����Ҫȡ���յ�ֵ
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
				//NumberFormat �� innerFormat����ֱ���ù�����
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
   * ����ö�����͵�ֵ�Ҷ�Ӧ������
   * @param comboitems
   * @param isFromMeta
   * @return
   */
  public static List<DefaultConstEnum> getInitData(String comboitems, boolean isFromMeta) {
		boolean isSX;
		ArrayList<String> list = new ArrayList<String>();

		String[] items = MiscUtil.getStringTokens(comboitems, ",");
		if (items != null) {
			// ��������
			String[] strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_DBFIELD };// , COMBOTYPE_INDEX_X
			// };

			// ���������Ŀֵ
			strArray = new String[] { IBillItem.COMBOTYPE_INDEX,
					IBillItem.COMBOTYPE_INDEX_X, IBillItem.COMBOTYPE_VALUE,
					IBillItem.COMBOTYPE_VALUE_X };

			isSX = IBillItem.COMBOTYPE_VALUE_X.equals(items[0]); // SX
			boolean isIX = IBillItem.COMBOTYPE_INDEX_X.equals(items[0]); // IX

			if (getStringIndexOfArray(strArray, items[0]) >= 0) {
				for (int i = 1; i < items.length; i++) {
					list.add(items[i].trim());
				}
			} else if (items.length == 3
					&& getStringIndexOfArray(new String[] {
							IBillItem.COMBOTYPE_INDEX_DBFIELD,
							IBillItem.COMBOTYPE_VALUE_DBFIELD }, items[0]) >= 0) {
				items = new DataDictionaryReader(items[1], items[2]).getQzsm();
				if (items != null) {
					for (int i = 0; i < items.length; i++) {
						list.add(items[i].trim());
					}
				}
			}

			// ����ֵ
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
  
  //��s��ss�����е�λ��
  private static int getStringIndexOfArray(final String[] ss, final String s) {
		if (ss != null) {
			for (int i = 0; i < ss.length; i++) {
				if (ss[i].equals(s))
					return i;
			}
		}
		return -1;
	}
  
  //��ö�ٲ��յ�ֵת��������
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
	 * getHeadTableCodes or getTableCodes. ��������:(2002-12-5 11:10:59)
	 */
	public String[] getTableCodes(int pos) {
		return hBillTabs.getTableCodes(pos);
	}
	
  public String[] getBodyTableCodes() {
		return getTableCodes(BODY);
	}
    /**
	 * ��������:(2003-4-22 16:44:08)
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
	 * �õ���ͷ�ؼ��ֶ�ӦԪ��. ��������:(01-2-23 14:22:07)
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
	 * �õ���β�ؼ��ֶ�ӦԪ��. ��������:(01-2-23 14:22:07)
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
	 * �õ���ͷ�ؼ��ֶ�ӦԪ��. ��������:(01-2-23 14:22:07)
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
	    //items�е�pos��һ�����ж�һ���Ϳ�����
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
	    
	    // �����µ�ҳǩ,���ӱ�������ƵĶ���
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
  
  private void initBodyVOs(BillTempletBodyVO[] bodys)
  {
//    if ((bodys == null) || (bodys.length == 0)) {
//      return;
//    }
//    
//    for (int i = 0; i < bodys.length; i++) { 
//      String code;
//      if (((code = bodys[i].getTableCode()) == null) || (code.trim().length() == 0)) {
//        int pos;
//        bodys[i].setTableCode(BillUtil.getDefaultTableCode(pos = bodys[i].getPos().intValue()));
//        bodys[i].setTableName(BillUtil.getDefaultTableName(pos));
//      }
//    }
//    BillUtil.sortBodyVOsByProps(bodys, new String[] { "pos", "table_code", "showorder" });
  }
  
  /**
	 * �õ������ģʽ. ��������:(01-2-23 15:23:11)
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
	 * �õ������ģʽ. ��������:(01-2-23 15:23:11)
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

	public AggregatedValueObject transJsonToBillValueObject(JSONObject json) throws JSONException, BusinessException {
		//���ñ�ͷ��ֵ
		JSONObject head = (JSONObject)json.get("head");
		IBusinessEntity be = getBillTempletVO().getHeadVO()
				.getBillMetaDataBusinessEntity();
		AggregatedValueObject aggvo = null;
		if (be.getBeanStyle().getStyle() == BeanStyleEnum.AGGVO_HEAD){
			//����Ԫ�������͵õ�Ӧ����ʲô���͵�vo
			AggVOStyle aggstyle = (AggVOStyle)be.getBeanStyle();
			aggvo = (AggregatedValueObject) aggstyle.newInstance(null);
//			NCObject ncobject = DASFacade.newInstanceWithContainedObject(be, aggvo);
		}
		//����ͷvo��ֵ	
		SuperVO parent = (SuperVO) aggvo.getParentVO();
		Iterator<String> keys = head.keys();  
        JSONObject jo = null;  
        String headvalue;  
        String key;  
        while(keys.hasNext()){
            key = keys.next();  
            headvalue = (String) head.get(key);
            JsonItem item = getHeadTailItem(key);
            if(item != null){
	            if(item!=null && !StringUtil.isEmpty(headvalue)){
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
			            		continue;
			            	}
		        		}else
		        			parent.setAttributeValue(key, new Integer(headvalue));
		            }else{
		            	try{
		            		parent.setAttributeValue(key, headvalue);
		            	}catch(Exception e){
		            		continue;
		            	}
		            }
	            }
            }
        }  
        
        //���ñ����ֵ
        JSONArray bodyvalue; 
        JSONObject body = (JSONObject)json.get("body");
        List<SuperVO> childrenlist = new ArrayList<SuperVO>();
        keys = body.keys();   
        while(keys.hasNext()){
            key = keys.next();  
            //�������������ʽ
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

	
}