package nc.erm.mobile.util;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JComponent;

import nc.bs.logging.Logger;
import nc.ui.bd.ref.costomize.RefCustomizedUtil;
import nc.ui.pub.bill.IBillItem;
import nc.vo.bd.ref.RefCustomizedVO;
import nc.vo.pub.bill.BillItemVO;
import nc.vo.pub.bill.BillTempletBodyVO;
import nc.vo.pub.bill.IMetaDataProperty;
import nc.vo.pub.bill.MetaDataPropertyFactory;
import nc.vo.pub.lang.UFBoolean;

/**
 * 单据基本元素. 创建日期:(01-2-21 9:29:06)
 *
 */
public class JsonItem implements IBillItem {

	private int m_iDataType = STRING; // 数据类型
	private int m_iItemType = STRING; // 数据类型

	private int m_iPos = HEAD; // 位置

	private int m_iShowOrder = 0; // 显示顺序

	private String m_strName = ""; // 显示名称

	private String m_strKey = ""; // 关键字

	private String m_strRefType = ""; // 参照类型

	private String m_strIDColName = ""; // 关键字名
	
	private String[] m_strLoadFormulas = null; // 加载时处理公式

	private String[] m_strEditFormulas = null; // 编辑时处理公式

	private String[] m_strValidateFormulas = null; // 编辑时处理公式

	private String m_strTableCode = null; // 所属页签/子表的编码

	private String m_strTableName = null; // 所属页签/子表的中文名称

	private int m_iForeground = 0; // 前景色(表头)

	private String m_strDefaultValue = null; // 默认值

	private boolean m_bShow = true; // 显示

	private boolean m_bEdit = false; // 编辑

	private boolean m_bLock = false; // 锁定

	private boolean m_bTotal = false; // 合计

	private boolean m_bNull = true; // 可空

	private boolean m_bNewLineFlag = false; // 对于卡片是否在新的一行显式

	private boolean m_bReviseFlag = false; // 是否可修订

	private boolean m_bWithIndex = false; // 参照类型是否返回索引

	protected JComponent m_compContent = null; // 组件

	private boolean m_bEnabled = true; // 可编辑

	private boolean m_bIsCard = false; // 是否属于卡片模板

	private int m_nReadOrder; // 读取顺序

	private boolean isDef = false; // 自定义项

	private String shareTableCode; // 共享页签编码

	private HashMap<String, BillItemVO> hashShareTable = null; // 在共享页签下显示属性配置

	private boolean m_bList = false; // 列表

	// store imagepath of local when datatype is image and image changed
	private String imagePath = null;

	private boolean m_StringAutoTrim = false; // 字符类型获取值自动截空格

	private String nodecode = null; // 多语目录

	private Comparator<Object> itemComparator = null; // 比较器

	private IMetaDataProperty metaDataProperty = null; // 元数据
	private boolean fillEnabled = true; // 是否可填充

	private String metaDataAccessPath = null; // 元数据访问路径

	private String metaDataRelation = null; // 元数据关联项

	private RefCustomizedVO set = null; // 参照类型格式描述

	private boolean useUIset = true;

	private boolean m_isHyperlink = false; // 是否卡片超链接
	
	private boolean m_isListHyperlink = false; // 是否列表超链接
	
	private String pk_org = null;
	
	private ArrayList<JsonItem> relationItem = null; // 关联ITEM
	
	public JsonItem() {
		super();
	}

	/**
	 * BillItem 构造子注解.
	 */
	public JsonItem(BillTempletBodyVO bVO) {
		super();
		initItem(bVO);
	}

	/**
	 * BillItem 构造子注解.
	 */
	public JsonItem(BillTempletBodyVO bVO, boolean isCard) {
		super();
		m_bIsCard = isCard;
		initItem(bVO);
	}

	protected void addRelationItem(JsonItem item) {
		if (relationItem == null)
			relationItem = new ArrayList<JsonItem>();

		relationItem.add(item);
	}
	
	/**
	 * BillItem 构造子注解.
	 */
	public JsonItem(boolean isCard) {
		super();
		m_bIsCard = isCard;
	}

	public String getLabelName() {
		return LABLE_PRESTR + getKey();
	}

	public String getComponentName() {
		return COMPONENT_PRESTR + getKey();
	}

	static String formulasToString(String[] fs) {
		if (fs == null || fs.length == 0)
			return null;
		StringBuffer sb = new StringBuffer();
		for (int i = 0, j = fs.length; i < j; i++) {
			if (i != 0)
				sb.append(";");
			sb.append(fs[i]);
		}
		return sb.toString();
	}
	
	/**
	 * 获得数据类型. 创建日期:(01-2-21 9:46:08)
	 * 
	 * @return int
	 */
	public int getDataType() {
		if (getMetaDataProperty() != null)
			return getMetaDataProperty().getDataType();
		return m_iDataType;
	}
	/**
	 * 获得数据类型. 创建日期:(01-2-21 9:46:08)
	 * 
	 * @return int
	 */
	public int getItemType() {
		return m_iItemType;
	}
	/**
	 * 创建日期:(2003-3-19 8:56:09)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getDefaultValue() {

		if (m_strDefaultValue == null && getMetaDataProperty() != null)
			return getMetaDataProperty().getDefaultValue();

		if (m_strDefaultValue != null && m_strDefaultValue.trim().length() == 0)
			m_strDefaultValue = null;

		return m_strDefaultValue;
	}
	
	/**
	 * 创建日期:(2003-3-19 8:56:09)
	 * 
	 * @return int
	 */
	public int getForeground() {
		return m_iForeground;
	}

	public static String[] parseFormulas(String formula)
	{
	    if ((formula != null) && ((formula = formula.trim()).length() > 0)) {
	      formula = formula.replaceAll("\r", "");
	      formula = formula.replaceAll("\n", "");
	      ArrayList<String> list = new ArrayList();
	      StringTokenizer st = new StringTokenizer(formula, ";");
	      
	      while (st.hasMoreTokens()) {
	        String s = st.nextToken().trim();
	        if (s.length() > 0)
	          list.add(s);
	      }
	      if (list.size() > 0)
	        return (String[])list.toArray(new String[list.size()]);
	    }
	    return null;
	}
	
	public static void prepareFormulas(String key, String[] formulas)
	{
	    if (formulas != null) {
	      String[] sf = { "$Enabled", "$Editable", "$ForeGround" };
	      
	      for (int i = 0; i < formulas.length; i++) {
	        int pos = formulas[i].indexOf("->");
	        if (pos < 0) {
	          formulas[i] = (key + "->" + formulas[i]);
	        } else {
	          String ss = formulas[i].substring(0, pos);
	          ss = ss.replaceAll(" ", "");
	          for (int j = 0; j < sf.length; j++) {
	            if (sf[j].equals(ss)) {
	              ss = ss + "[" + key + "]";
	              formulas[i] = (ss + formulas[i].substring(pos));
	            }
	          }
	        }
	      }
	    }
	}
	/**
	 * 将多条表达式语句拆分成表达式数组. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @return int
	 */
	protected String[] getFormulas(String formula) {
		if (formula == null)
			return null;

		String[] formulas = parseFormulas(formula);
		if (formulas != null) {
			prepareFormulas(getKey(), formulas);
		}

		return formulas;
	}

	private HashMap<String, BillItemVO> getHashShareTable() {
		if (hashShareTable == null) {
			hashShareTable = new HashMap<String, BillItemVO>();
			// if (options != null) {
			// final BillItemVO[] vos = BillXMLUtil.xmlToBillItemVO(options);
			// if (vos != null) {
			// for (int i = 0; i < vos.length; i++) {
			// String code = vos[i].getTablecode();
			// if (code != null && (code = code.trim()).length() > 0) {
			// if (code.equals(m_strTableCode))
			// options = null;
			// else
			// hashShareTable.put(code, vos[i]);
			// }
			// }
			// }
			// }
		}
		return hashShareTable;
	}

	/**
	 * 获得ID列名. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @return int
	 */
	public String getIDColName() {
		return m_strIDColName;
	}

	public String getImagePath() {
		return imagePath;
	}

	/**
	 * 返回图片所处位置0:left, 1:right. 创建日期:(2002-09-09 16:19:50)
	 * 
	 * @return java.awt.Dimension
	 */
	public int getImagePos() {
		int pos = 1;
		if ((getDataType() == IMAGE)) {
			try {
				String str = getRefType();
				if ((str != null) && (str.length() > 0))
					if (str.indexOf(")") >= 0) {
						String s = str.substring(str.indexOf(")") + 1);
						pos = s.equalsIgnoreCase("L") ? 0 : 1;
					}
			} catch (Exception e) {
			}
		}
		return pos;
	}

	/**
	 * 返回图片和大文本类型的预置大小. 创建日期:(2002-09-09 16:19:50)
	 * 
	 * @return java.awt.Dimension
	 */
	public Dimension getImageSize() {
		int width = 1500;
		int height = 50;
		if ((getDataType() == IMAGE) || (getDataType() == TEXTAREA)) {
			try {
				String str = getRefType();
				if ((str != null) && (str.length() > 0))
					if (str.indexOf(",") >= 0) {
						String ww = str.substring(1, str.indexOf(",")).trim();
						String hh;
						if (str.indexOf(")") > 0)
							hh = str.substring(str.indexOf(",") + 1,
									str.indexOf(")")).trim();
						else
							hh = str.substring(str.indexOf(",")).trim();
						width = Integer.parseInt(ww);
						height = Integer.parseInt(hh);
					}
			} catch (Exception e) {
				Logger.debug(e);
			}
		}
		return new Dimension(width, height);
	}

	/**
	 * 获得关键字. 创建日期:(01-2-21 9:50:17)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getKey() {
		return m_strKey;
	}

	public String[] getLoadFormula() {
		return m_strLoadFormulas;
	}

	/**
	 * 获得名称. 创建日期:(01-2-21 9:47:57)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getName() {
		if (m_strName == null) {
			if (getMetaDataProperty() != null) {
				return getMetaDataProperty().getShowName();
			}
		}

		return m_strName;
	}


	/**
	 * 获得位置. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @return int
	 */
	public int getPos() {
		return m_iPos;
	}

	/**
	 * 返回读取位置
	 * 
	 * @return int
	 */
	public int getReadOrder() {
		return m_nReadOrder;
	}

	/**
	 * 获得参照类型. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @return int
	 */
	public String getRefType() {

		if (getMetaDataProperty() != null && m_strRefType == null) {
			return getMetaDataProperty().getRefType();
		}

		return m_strRefType;
	}

	private String getShareTableCode() {
		return shareTableCode;
	}

	/**
	 * 获得控件显示顺序. 创建日期:(01-2-21 9:44:18)
	 * 
	 * @return int
	 */
	public int getShowOrder() {
		return m_iShowOrder;
	}

	/**
	 * 创建日期:(2002-09-09 10:44:36)
	 * 
	 * @return java.lang.String
	 */
	public String getTableCode() {
		return m_strTableCode;
	}

	/**
	 * 创建日期:(2002-09-09 10:44:36)
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {
		return m_strTableName;
	}

	/**
	 * 初始化ITEM. 创建日期:(01-2-21 9:47:57)
	 * 
	 * @return java.lang.String
	 */
	public void initItem(BillTempletBodyVO bVO) {

		bVO.setShareTableCode(null);

		m_strName = bVO.getDefaultshowname();
		m_strKey = bVO.getItemkey();
		m_bLock = bVO.getLockflag() != null ? bVO.getLockflag().booleanValue()
				: false;
		m_bList = bVO.isList();

		// Boolean b = bVO.isList()?bVO.getListshowflag():bVO.getShowflag();
		Boolean b = m_bIsCard ? bVO.getShowflag() : bVO.getListshowflag();

		m_bShow = b == null ? true : b.booleanValue();

		m_bTotal = bVO.getTotalflag() != null ? bVO.getTotalflag()
				.booleanValue() : false;
		m_iDataType = bVO.getDatatype() != null ? bVO.getDatatype().intValue()
				: IBillItem.STRING;
		m_iItemType = bVO.getItemtype();
		m_strRefType = bVO.getReftype();
		m_iPos = bVO.getPos().intValue();
		m_iShowOrder = bVO.getShoworder().intValue();
		m_bEdit = bVO.getEditflag() != null ? bVO.getEditflag().booleanValue()
				: true;
		m_strLoadFormulas = getFormulas(bVO.getLoadformula());
		m_strEditFormulas = getFormulas(bVO.getEditformula());
		m_strValidateFormulas = getFormulas(bVO.getValidateformula());
		m_strIDColName = bVO.getIdcolname();
		m_bNull = bVO.getNullflag() != null ? bVO.getNullflag().booleanValue()
				: false;
		m_bNewLineFlag = bVO.getNewlineflag() != null ? bVO.getNewlineflag()
				.booleanValue() : false;
		m_bReviseFlag = bVO.getReviseflag() != null ? bVO.getReviseflag()
				.booleanValue() : false;
		// if (bVO.getLeafflag() != null)
		// m_bNotLeafSelectedEnabled = !bVO.getLeafflag().booleanValue();

		hashShareTable = bVO.getHashShareTable();
		m_strTableCode = bVO.getTableCode();
		m_strTableName = bVO.getTableName();
		m_strDefaultValue = bVO.getDefaultvalue();

		m_isHyperlink = bVO.getHyperlinkflag() != null ? bVO.getHyperlinkflag()
				.booleanValue() : false;
				
		m_isListHyperlink = bVO.getListHyperlinkflag() != null ? bVO.getListHyperlinkflag()
						.booleanValue() : false;

		// comboBoxData = COMBO_INIT_DATA;

		metaDataProperty = bVO.getMetaDataPropertyAdpter();

		metaDataAccessPath = bVO.getMetadatapath();

		metaDataRelation = bVO.getMetadatarelation();

		if (getDataType() == USERDEFITEM)
			isDef = true;

		if (bVO.getParentVO() != null) {
			if (bVO.getParentVO().getHeadVO() != null) {
				nodecode = bVO.getParentVO().getHeadVO().getNodecode();
			}
		}

		// 设置控件属性
		initAttribByParseReftype();

		setEnabled(m_bEdit);
		
		setPk_org(bVO.getPk_corp());
	}

	public static String[] getStringTokensWithNullToken(String str, String delim) {
		if (str == null || delim == null)
			return null;
		java.util.StringTokenizer st = new java.util.StringTokenizer(str,
				delim, true);
		int len = st.countTokens();
		String[] tokens = new String[len * 2 + 2];
		int count = 0;
		String token;
		for (int i = 0; i < len; i++) {
			token = st.nextToken().trim();
			if (delim.indexOf(token) >= 0) {
				if (count % 2 == 0)
					count++;
			} else {
				if (count % 2 != 0)
					count++;
				if ((token = token.trim()).length() > 0)
					tokens[count] = token;
			}
			count++;
		}
		String[] ts = new String[count / 2 + 1];
		for (int i = 0; i < ts.length; i++)
			ts[i] = tokens[i * 2];
		return ts;
	}
	
	/*
	 * 解析类型设置
	 */
	private void initAttribByParseReftype() {

		if (getRefType() == null || getRefType().trim().length() == 0) {
			return;
		}

		String reftype = getRefType().trim();

		// 自定义类型处理
		if (getDataType() == USERDEFITEM) {
			String[] tokens = getStringTokensWithNullToken(reftype,
					":");

			if (tokens.length > 0) {
				try {
					// 非原数据数据类型
					if (getMetaDataProperty() != null) {
						IMetaDataProperty mdp = MetaDataPropertyFactory
								.creatMetaDataUserDefPropertyByType(
										getMetaDataProperty(), tokens[0]);
						setMetaDataProperty(mdp);
					} else {
						int datatype = Integer.parseInt(tokens[0]);
						if (datatype <= USERDEFITEM)
							setDataType(datatype);
					}
					if (tokens.length > 1) {
						setRefType(tokens[1]);
					} else {
						setRefType(null);
						return;
					}
				} catch (NumberFormatException e) {
					Logger.warn("数据类型设置错误：" + tokens[0]);
					setDataType(IBillItem.STRING);
					setRefType(null);
					return;
				}
			}
		} else if (getDataType() == IBillItem.DATE) {
//			if (getRefType().equals("B")) {
//				setDateformat(IBillItem.DATE_FORMAT_BEGIN);
//			} else if (getRefType().equals("E")) {
//				setDateformat(IBillItem.DATE_FORMAT_END);
//			}
		}

		// switch (getDataType()) {
		// case INTEGER:
		// BillItemNumberFormat f = BillUtil.parseIntgerFormat(reftype);
		//            
		// setNumberFormat(f);
		// setRefType(null);
		// break;
		// case DECIMAL:
		// f = BillUtil.parseDoubleFormat(reftype);
		//            
		// setNumberFormat(f);
		// setRefType(null);
		//
		// break;
		// case UFREF:
		//        	
		// if(getPos() == BODY)
		// set = RefCustomizedUtil.deCode(reftype,true);
		// else
		// set = RefCustomizedUtil.deCode(reftype,false);
		//
		// //自定义参照
		// if(set.isSelfRef())
		// setRefType(set.getSelfRefModelClassName());
		// else
		// setRefType(set.getRefNodeName());

		// setRefReturnCode(set.isReturnCode());

		// if(isM_bNotLeafSelectedEnabled())
		// setM_bNotLeafSelectedEnabled(set.isNotLeafSelected());
		//        	
		// break;
		// default:
		// break;
		// }
	}


	/**
	 * 是否可显示. 创建日期:(01-2-21 9:56:37)
	 * 
	 * @return boolean
	 */
	public boolean isBaseTableCodeShow() {
		return m_bShow;
	}

	/**
	 * 创建日期:(2002-11-07 15:42:16)
	 * 
	 * @return boolean
	 */
	public boolean isCard() {
		return m_bIsCard;
	}

	/**
	 * 是否可编辑. 创建日期:(01-2-21 9:59:59)
	 * 
	 * @return boolean
	 */
	public boolean isEdit() {
		if (getDataType() == UNSET)
			return false;

		if (getMetaDataProperty() != null)
			return getMetaDataProperty().isEditable() && m_bEdit;
		return m_bEdit;
	}

	/**
	 * 返回元素可编辑性. 创建日期:(01-2-23 14:56:47)
	 * 
	 * @param newBLock
	 *            boolean
	 */
	public boolean isEnabled() {

		if (getDataType() == UNSET)
			return false;

		if (getMetaDataProperty() != null)
			return getMetaDataProperty().isEditable() && m_bEnabled;
		return m_bEnabled;
	}

	/**
	 * 自定义项 创建日期:(2003-8-20 10:39:41)
	 * 
	 * @return boolean
	 */
	public boolean isIsDef() {
		return isDef;
	}

	private boolean isList() {
		return m_bList;
	}

	/**
	 * 是否锁定. 创建日期:(01-2-23 14:56:47)
	 * 
	 * @return boolean
	 */
	public boolean isLock() {
		return m_bLock;
	}


	/**
	 * 创建日期:(2003-7-11 10:49:10)
	 * 
	 * @return boolean
	 */
	// public boolean isM_bDesign() {
	// return m_bDesign;
	// }
	/**
	 * 创建日期:(2003-8-4 13:17:40)
	 * 
	 * @return boolean
	 */
	public boolean isM_bNewLineFlag() {
		return m_bNewLineFlag;
	}

	/**
	 * 创建日期:(2003-6-18 16:48:07)
	 * 
	 * @return boolean
	 */
	public boolean isM_bNotLeafSelectedEnabled() {
		if (getRefTypeSet() != null)
			return getRefTypeSet().isNotLeafSelected();
		else
			return false;

		// return m_bNotLeafSelectedEnabled;
	}

	/**
	 * 创建日期:(2003-8-20 16:07:02)
	 * 
	 * @return boolean
	 */
	public boolean isM_bReviseFlag() {
		return m_bReviseFlag;
	}

	/**
	 * 是否可空. 创建日期:(01-2-21 9:59:59)
	 * 
	 * @return boolean
	 */
	public boolean isNull() {
		if (getMetaDataProperty() != null)
			return getMetaDataProperty().isNotNull() || m_bNull;
		return m_bNull;
	}

	/**
	 * 是否可显示. 获取共享业签该属性值时，需要先设置页签编码 才能返回正确值
	 * 
	 * @return boolean
	 * 
	 * @link setShareTableCode
	 */
	public boolean isShow() {
		// 共享业签处理
		Boolean b = null;
		BillItemVO vo;
		String sc = getShareTableCode();
		if (sc != null) {
			vo = getHashShareTable().get(sc);
			if (vo != null) {
				if (isList())
					b = vo.getListshowflag();
				else
					b = vo.getShowflag();

				if (b == null)
					b = getMetaDataProperty().isUIShow();
			}
		}
		if (b != null) {
			if (getMetaDataProperty() != null)
				return getMetaDataProperty().isUIShow() && b.booleanValue();
			else
				return b.booleanValue();
		}

		if (getMetaDataProperty() != null)
			return getMetaDataProperty().isUIShow() && m_bShow;

		return m_bShow;

	}

	boolean isShareShow() {
		Boolean isShow = null;

		BillItemVO vo;

		if (getMetaDataProperty() != null)
			isShow = getMetaDataProperty().isUIShow() && m_bShow;
		else
			isShow = m_bShow;

		if (!isShow && getHashShareTable() != null) {
			for (String sc : getHashShareTable().keySet()) {
				vo = getHashShareTable().get(sc);
				if (vo != null) {
					if (isList())
						isShow = vo.getListshowflag();
					else
						isShow = vo.getShowflag();

					if (isShow == null)
						isShow = getMetaDataProperty().isUIShow();

					if (isShow)
						break;
				}
			}
		}

		return isShow;
	}

	/**
	 * 是否合计. 创建日期:(01-2-23 14:56:47)
	 * 
	 * @return boolean
	 */
	public boolean isTotal() {
		return m_bTotal;
	}

	/**
	 * 是否使用索引方式. 创建日期:(01-2-23 14:56:47)
	 * 
	 * @return boolean
	 */
	public boolean isWithIndex() {
		return m_bWithIndex;
	}

	/**
	 * 设置显示属性. 创建日期:(01-2-21 9:56:37)
	 * 
	 * @param newShow
	 *            boolean
	 */
	public void setBaseTableCodeShow(boolean newShow) {
		m_bShow = newShow;
	}

	/**
	 * 设置数据类型. 创建日期:(01-2-21 9:46:08)
	 * 
	 * @param newDataType
	 *            int
	 */
	public void setDataType(int newDataType) {
		m_iDataType = newDataType;
	}

	/**
	 * 创建日期:(2003-3-19 8:56:09)
	 * 
	 * @param newM_strDefaultValue
	 *            java.lang.String
	 */
	public void setDefaultValue(java.lang.String strDefaultValue) {
		m_strDefaultValue = strDefaultValue;
		//
		// setValue(strDefaultValue);
	}

	/**
	 * 设置编辑属性(初试). 创建日期:(01-2-21 9:59:59)
	 * 
	 * @param newEdit
	 *            boolean
	 */
	public void setEdit(boolean newEdit) {
		if (getDataType() == UNSET)
			return;
		m_bEdit = newEdit;
		setEnabled(newEdit);
	}

	/**
	 * 设置编辑属性(动态). 创建日期:(01-2-23 14:56:47)
	 * 
	 * @param newBLock
	 *            boolean
	 */
	public void setEnabled(boolean newEnabled) {
		if (getDataType() == UNSET)
			return;
		m_bEnabled = newEnabled;
	}


	/**
	 * 创建日期:(2003-3-19 8:56:09)
	 * 
	 * @param newM_iForeground
	 *            int
	 */
	public void setForeground(int iForeground) {
		m_iForeground = iForeground;
	}

	// private void setHashShareTable(HashMap hashShareTable) {
	// this.hashShareTable = hashShareTable;
	// }

	/**
	 * 设置ID列名. 创建日期:(01-2-21 9:47:57)
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setIDColName(java.lang.String newIDColName) {
		m_strIDColName = newIDColName;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}

	/**
	 * 创建日期:(2003-8-20 10:39:41)
	 * 
	 * @param newIsDef
	 *            boolean
	 */
	public void setIsDef(boolean newIsDef) {
		isDef = newIsDef;
	}

	/**
	 * 创建日期:(01-2-21 9:50:17)
	 * 
	 * @param newKey
	 *            java.lang.String
	 */
	public void setKey(java.lang.String newKey) {
		m_strKey = newKey;
	}

	public void setList(boolean list) {
		this.m_bList = list;
	}

	/**
	 * 设置锁定属性. 创建日期:(01-2-23 14:56:47)
	 * 
	 * @param newBLock
	 *            boolean
	 */
	public void setLock(boolean newBLock) {
		m_bLock = newBLock;
	}

	public void setLoadFormula(String[] formulas) {
		m_strLoadFormulas = formulas;
		if (formulas != null) {
			prepareFormulas(getKey(), formulas);
		}
	}
	
	/**
	 * 创建日期:(2003-7-11 10:49:10)
	 * 
	 * @param newM_bDesign
	 *            boolean
	 */
	// public void setM_bDesign(boolean newM_bDesign) {
	// m_bDesign = newM_bDesign;
	// }
	/**
	 * 创建日期:(2003-8-4 13:17:40)
	 * 
	 * @param newM_bNewLineFlag
	 *            boolean
	 */
	public void setM_bNewLineFlag(boolean newM_bNewLineFlag) {
		m_bNewLineFlag = newM_bNewLineFlag;
	}

	/**
	 * 创建日期:(2003-6-18 16:48:07)
	 * 
	 * @param newM_bNotLeafSelectedEnabled
	 *            boolean
	 */
	public void setM_bNotLeafSelectedEnabled(
			boolean newM_bNotLeafSelectedEnabled) {
		// m_bNotLeafSelectedEnabled = newM_bNotLeafSelectedEnabled;

		getRefTypeSet().setNotLeafSelected(newM_bNotLeafSelectedEnabled);
	}

	/**
	 * 创建日期:(2003-8-20 16:07:02)
	 * 
	 * @param newM_bReviseFlag
	 *            boolean
	 */
	public void setM_bReviseFlag(boolean newM_bReviseFlag) {
		m_bReviseFlag = newM_bReviseFlag;
	}

	/**
	 * 设置名称. 创建日期:(01-2-21 9:47:57)
	 * 
	 * @param newName
	 *            java.lang.String
	 */
	public void setName(java.lang.String newName) {
		m_strName = newName;
	}

	protected void setNotLeafSelectedEnabled(boolean flag) {
		getRefTypeSet().setNotLeafSelected(flag);
	}

	/**
	 * 设置可空属性. 创建日期:(01-2-21 9:56:37)
	 * 
	 * @param newShow
	 *            boolean
	 */
	public void setNull(boolean newNull) {
		m_bNull = newNull;
	}

	/**
	 * 设置显示位置. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @param newPos
	 *            int
	 */
	public void setPos(int newPos) {
		m_iPos = newPos;
	}

	/**
	 * 创建日期:(2002-11-19 21:15:43)
	 * 
	 * @param newM_nReadOrder
	 *            int
	 */
	public void setReadOrder(int newM_nReadOrder) {
		m_nReadOrder = newM_nReadOrder;
	}

	/**
	 * 设置参照类型. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @param newPos
	 *            int
	 */
	public void setRefType(String newRefType) {
		m_strRefType = newRefType;
	}

	public void setShareTableCode(String shareTableCode) {
		if (shareTableCode == null || shareTableCode.trim().length() == 0) {
			this.shareTableCode = null;
			return;
		}
		if (shareTableCode.equals(m_strTableCode)) {
			this.shareTableCode = null;
			return;
		}
		if (getHashShareTable().containsKey(shareTableCode))
			this.shareTableCode = shareTableCode;
	}

	/**
	 * 设置显示属性. 创建日期:(01-2-21 9:56:37) 设置共享业签该属性值时，需要先设置页签编码 才能设置正确值
	 * setShareTableCode
	 * 
	 * @param newShow
	 *            boolean
	 * 
	 * @link setShareTableCode
	 */
	public void setShow(boolean newShow) {
		Boolean b = Boolean.valueOf(newShow);
		BillItemVO vo;
		String sc = getShareTableCode();
		if (sc != null) {
			vo = (BillItemVO) getHashShareTable().get(sc);
			if (vo != null) {
				if (isList())
					vo.setListshowflag(b);
				else
					vo.setShowflag(b);
			}
		} else
			m_bShow = newShow;// showflag = b;
	}

	/**
	 * 设置显示顺序. 创建日期:(01-2-21 9:44:18)
	 * 
	 * @param newLength
	 *            int
	 */
	public void setShowOrder(int newShowOrder) {
		m_iShowOrder = newShowOrder;
	}

	// 设置是否显示千分位
	void setShowThMark(boolean newValue) {
		if (getPos() == BODY)
			return;
	}

	/**
	 * 创建日期:(2002-09-09 10:51:40)
	 * 
	 * @param newTableCode
	 *            java.lang.String
	 */
	public void setTableCode(String newTableCode) {
		m_strTableCode = newTableCode;
	}

	/**
	 * 创建日期:(2002-09-09 10:51:40)
	 * 
	 * @param newTableCode
	 *            java.lang.String
	 */
	public void setTableName(String newTableName) {
		m_strTableName = newTableName;
	}

	/**
	 * 设置元素是否可合计. 创建日期:(01-2-23 14:56:47)
	 * 
	 * @param newBLock
	 *            boolean
	 */
	public void setTatol(boolean newTatol) {
		if (getDataType() == INTEGER || getDataType() == DECIMAL
				|| getDataType() == MONEY)
			m_bTotal = newTatol;
	}

	/**
	 * 设置下拉框返回属性. 创建日期:(01-2-21 9:56:37)
	 * 
	 * @param newShow
	 *            boolean
	 */
	public void setWithIndex(boolean newWithIndex) {
		m_bWithIndex = newWithIndex;
	}

	/**
	 * 初始化ITEM. 创建日期:(01-2-21 9:47:57)
	 * 
	 * @return java.lang.String
	 */
	public BillTempletBodyVO toBillTempletBodyVO() {
		BillTempletBodyVO bVO = new BillTempletBodyVO();
		bVO.setDefaultshowname(getName());
		bVO.setItemkey(getKey());
		bVO.setLockflag(Boolean.valueOf(isLock()));
		bVO.setShowflag(Boolean.valueOf(isShow()));
		bVO.setListshowflag(null);
		bVO.setTotalflag(Boolean.valueOf(isTotal()));
		bVO.setDatatype(Integer.valueOf(getDataType()));
		bVO.setReftype(getRefType());
		bVO.setPos(Integer.valueOf(getPos()));
		bVO.setShoworder(Integer.valueOf(getShowOrder()));
		bVO.setEditflag(Boolean.valueOf(isEdit()));
		bVO.setIdcolname(getIDColName());
		bVO.setNullflag(Boolean.valueOf(isNull()));
		bVO.setTableCode(getTableCode());
		bVO.setTableName(getTableName());
		bVO.setReviseflag(UFBoolean.valueOf(isM_bReviseFlag()));
		bVO.setNewlineflag(UFBoolean.valueOf(isM_bNewLineFlag()));
		if (getRefTypeSet()!=null){
		bVO.setLeafflag(UFBoolean.valueOf(getRefTypeSet().isNotLeafSelected()));
		}
		bVO.setHashShareTable(getHashShareTable());
		// bVO.setOptions(getOptions());
		return bVO;
	}
	
	public boolean isStringAutoTrim() {
		return m_StringAutoTrim;
	}

	public void setStringAutoTrim(boolean stringAutoTrim) {
		m_StringAutoTrim = stringAutoTrim;
	}

	public Comparator<Object> getItemComparator() {
		return itemComparator;
	}

	public void setItemComparator(Comparator<Object> itemComparator) {
		this.itemComparator = itemComparator;
	}

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
	}

	public IMetaDataProperty getMetaDataProperty() {
		return metaDataProperty;
	}

	public void setMetaDataProperty(IMetaDataProperty metaDataProperty) {
		this.metaDataProperty = metaDataProperty;
		setRefType(null);
		set = null;
	}


	protected boolean isRefReturnCode() {

		return getRefTypeSet().isReturnCode();

		// return refReturnCode;
	}

	public String getMetaDataAccessPath() {
		return metaDataAccessPath;
	}

	protected String getMetaDataRelation() {
		return metaDataRelation;
	}

	public RefCustomizedVO getRefTypeSet() {

		if (getDataType() != IBillItem.UFREF)
			return null;

		if (set == null) {
			if (getRefType() != null) {
				if (getPos() == BODY)
					set = RefCustomizedUtil.deCode(getRefType(), true);
				else
				set = RefCustomizedUtil.deCode(getRefType(), false);

			} else
				set = new RefCustomizedVO();
		}

		if (getMetaDataProperty() != null && !isIsDef()) {
			set.setDataPowerOperation_Code(getMetaDataProperty().getAttribute()
					.getAccessPowerGroup());
		}

		return set;
	}

	public boolean isFillEnabled() {
		return fillEnabled;
	}

	public void setFillEnabled(boolean fillEnabled) {
		this.fillEnabled = fillEnabled;
	}


	public boolean isUseUIset() {
		return useUIset;
	}

	public void setUseUIset(boolean useUIset) {
		this.useUIset = useUIset;
	}

	public boolean isHyperlink() {
		return m_isHyperlink&&isCard();
	}

	public void setHyperlink(boolean hyperlink) {
		m_isHyperlink = hyperlink;
	}

	//单据模板数据库是否显示属性
	public boolean isShowFlag(){
		
		return m_bShow;
	}

	public boolean isListHyperlink() {
		return m_isListHyperlink&&!isCard();
	}

	public void setListHyperlink(boolean listHyperlink) {
		m_isListHyperlink = listHyperlink;
	}


	public String getPk_org() {
		return pk_org;
	}

	public void setPk_org(String pk_org) {
		this.pk_org = pk_org;
	}

//	public IGetBillRelationItemValue getGetBillRelationItemValue() {
//		// 设置关联项取值器
//		if (getMetaDataProperty() != null && getBillRelationItemValue == null)
//			getBillRelationItemValue = new MetaDataGetBillRelationItemValue(
//					getMetaDataProperty().getRefBusinessEntity());
//
//		return getBillRelationItemValue;
//	}
	/**
	 * 设置值. 创建日期:(01-2-21 9:51:24)
	 * 
	 * @param newPos
	 *            int
	 */
	Object value;
	public void setValue(Object newValue) {
		value = newValue;
	}
	public Object getValueObject() {
		return value;
	}
	
	public ArrayList<JsonItem> getRelationItem() {
		return relationItem;
	}
	
	public String[] getEditFormulas() {
		return m_strEditFormulas;
	}
	public void setEditFormula(String[] formulas) {
		m_strEditFormulas = formulas;
		if (formulas != null) {
			prepareFormulas(getKey(), formulas);
		}
	}
	
	public String[] getValidateFormulas() {
		return m_strValidateFormulas;
	}
	
	public void setValidateFormulas(String[] formulas) {
		this.m_strValidateFormulas = formulas;
		if (formulas != null) {
			prepareFormulas(getKey(), formulas);
		}
	}
}
