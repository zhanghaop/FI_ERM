package nc.erm.mobile.view;

import java.awt.Dimension;
import java.util.Comparator;
import java.util.HashMap;

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
 * ���ݻ���Ԫ��. ��������:(01-2-21 9:29:06)
 *
 */
public class MobileBillItem implements IBillItem {

	private int m_iDataType = STRING; // ��������

	private int m_iPos = HEAD; // λ��

	private int m_iShowOrder = 0; // ��ʾ˳��

	private String m_strName = ""; // ��ʾ����

	private String m_strKey = ""; // �ؼ���

	private String m_strRefType = ""; // ��������

	private String m_strIDColName = ""; // �ؼ�����

	private String m_strTableCode = null; // ����ҳǩ/�ӱ�ı���

	private String m_strTableName = null; // ����ҳǩ/�ӱ����������

	private int m_iForeground = 0; // ǰ��ɫ(��ͷ)

	private String m_strDefaultValue = null; // Ĭ��ֵ

	private boolean m_bShow = true; // ��ʾ

	private boolean m_bEdit = false; // �༭

	private boolean m_bLock = false; // ����

	private boolean m_bTotal = false; // �ϼ�

	private boolean m_bNull = true; // �ɿ�

	private boolean m_bNewLineFlag = false; // ���ڿ�Ƭ�Ƿ����µ�һ����ʽ

	private boolean m_bReviseFlag = false; // �Ƿ���޶�

	private boolean m_bWithIndex = false; // ���������Ƿ񷵻�����

	protected JComponent m_compContent = null; // ���

	private boolean m_bEnabled = true; // �ɱ༭

	private boolean m_bIsCard = false; // �Ƿ����ڿ�Ƭģ��

	private int m_nReadOrder; // ��ȡ˳��

	private boolean isDef = false; // �Զ�����

	private String shareTableCode; // ����ҳǩ����

	private HashMap<String, BillItemVO> hashShareTable = null; // �ڹ���ҳǩ����ʾ��������

	private boolean m_bList = false; // �б�

	// store imagepath of local when datatype is image and image changed
	private String imagePath = null;

	private boolean m_StringAutoTrim = false; // �ַ����ͻ�ȡֵ�Զ��ؿո�

	private String nodecode = null; // ����Ŀ¼

	private Comparator<Object> itemComparator = null; // �Ƚ���

	private IMetaDataProperty metaDataProperty = null; // Ԫ����
	private boolean fillEnabled = true; // �Ƿ�����

	private String metaDataAccessPath = null; // Ԫ���ݷ���·��

	private String metaDataRelation = null; // Ԫ���ݹ�����

	private RefCustomizedVO set = null; // �������͸�ʽ����

	private boolean useUIset = true;

	private boolean m_isHyperlink = false; // �Ƿ�Ƭ������
	
	private boolean m_isListHyperlink = false; // �Ƿ��б�����
	private String pk_org = null;
	
	public MobileBillItem() {
		super();
	}

	/**
	 * BillItem ������ע��.
	 */
	public MobileBillItem(BillTempletBodyVO bVO) {
		super();
		initItem(bVO);
	}

	/**
	 * BillItem ������ע��.
	 */
	public MobileBillItem(BillTempletBodyVO bVO, boolean isCard) {
		super();
		m_bIsCard = isCard;
		initItem(bVO);
	}

	/**
	 * BillItem ������ע��.
	 */
	public MobileBillItem(boolean isCard) {
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
	 * �����������. ��������:(01-2-21 9:46:08)
	 * 
	 * @return int
	 */
	public int getDataType() {
		if (getMetaDataProperty() != null)
			return getMetaDataProperty().getDataType();
		return m_iDataType;
	}
	/**
	 * ��������:(2003-3-19 8:56:09)
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
	 * ��������:(2003-3-19 8:56:09)
	 * 
	 * @return int
	 */
	public int getForeground() {
		return m_iForeground;
	}

	/**
	 * ���������ʽ����ֳɱ��ʽ����. ��������:(01-2-21 9:51:24)
	 * 
	 * @return int
	 */
	protected String[] getFormulas(String formula) {
		if (formula == null)
			return null;

//		String[] formulas = BillUtil.parseFormulas(formula);
//		if (formulas != null) {
//			BillFormulaReg.prepareFormulas(getKey(), formulas);
//		}

		return new String[]{};
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
	 * ���ID����. ��������:(01-2-21 9:51:24)
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
	 * ����ͼƬ����λ��0:left, 1:right. ��������:(2002-09-09 16:19:50)
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
	 * ����ͼƬ�ʹ��ı����͵�Ԥ�ô�С. ��������:(2002-09-09 16:19:50)
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
	 * ��ùؼ���. ��������:(01-2-21 9:50:17)
	 * 
	 * @return java.lang.String
	 */
	public java.lang.String getKey() {
		return m_strKey;
	}


	/**
	 * �������. ��������:(01-2-21 9:47:57)
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
	 * ���λ��. ��������:(01-2-21 9:51:24)
	 * 
	 * @return int
	 */
	public int getPos() {
		return m_iPos;
	}

	/**
	 * ���ض�ȡλ��
	 * 
	 * @return int
	 */
	public int getReadOrder() {
		return m_nReadOrder;
	}

	/**
	 * ��ò�������. ��������:(01-2-21 9:51:24)
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
	 * ��ÿؼ���ʾ˳��. ��������:(01-2-21 9:44:18)
	 * 
	 * @return int
	 */
	public int getShowOrder() {
		return m_iShowOrder;
	}

	/**
	 * ��������:(2002-09-09 10:44:36)
	 * 
	 * @return java.lang.String
	 */
	public String getTableCode() {
		return m_strTableCode;
	}

	/**
	 * ��������:(2002-09-09 10:44:36)
	 * 
	 * @return java.lang.String
	 */
	public String getTableName() {
		return m_strTableName;
	}

	/**
	 * ��ʼ��ITEM. ��������:(01-2-21 9:47:57)
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
		m_strRefType = bVO.getReftype();
		m_iPos = bVO.getPos().intValue();
		m_iShowOrder = bVO.getShoworder().intValue();
		m_bEdit = bVO.getEditflag() != null ? bVO.getEditflag().booleanValue()
				: true;
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

		// ���ÿؼ�����
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
	 * ������������
	 */
	private void initAttribByParseReftype() {

		if (getRefType() == null || getRefType().trim().length() == 0) {
			return;
		}

		String reftype = getRefType().trim();

		// �Զ������ʹ���
		if (getDataType() == USERDEFITEM) {
			String[] tokens = getStringTokensWithNullToken(reftype,
					":");

			if (tokens.length > 0) {
				try {
					// ��ԭ������������
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
					Logger.warn("�����������ô���" + tokens[0]);
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
		// //�Զ������
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
	 * �Ƿ����ʾ. ��������:(01-2-21 9:56:37)
	 * 
	 * @return boolean
	 */
	public boolean isBaseTableCodeShow() {
		return m_bShow;
	}

	/**
	 * ��������:(2002-11-07 15:42:16)
	 * 
	 * @return boolean
	 */
	public boolean isCard() {
		return m_bIsCard;
	}

	/**
	 * �Ƿ�ɱ༭. ��������:(01-2-21 9:59:59)
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
	 * ����Ԫ�ؿɱ༭��. ��������:(01-2-23 14:56:47)
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
	 * �Զ����� ��������:(2003-8-20 10:39:41)
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
	 * �Ƿ�����. ��������:(01-2-23 14:56:47)
	 * 
	 * @return boolean
	 */
	public boolean isLock() {
		return m_bLock;
	}


	/**
	 * ��������:(2003-7-11 10:49:10)
	 * 
	 * @return boolean
	 */
	// public boolean isM_bDesign() {
	// return m_bDesign;
	// }
	/**
	 * ��������:(2003-8-4 13:17:40)
	 * 
	 * @return boolean
	 */
	public boolean isM_bNewLineFlag() {
		return m_bNewLineFlag;
	}

	/**
	 * ��������:(2003-6-18 16:48:07)
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
	 * ��������:(2003-8-20 16:07:02)
	 * 
	 * @return boolean
	 */
	public boolean isM_bReviseFlag() {
		return m_bReviseFlag;
	}

	/**
	 * �Ƿ�ɿ�. ��������:(01-2-21 9:59:59)
	 * 
	 * @return boolean
	 */
	public boolean isNull() {
		if (getMetaDataProperty() != null)
			return getMetaDataProperty().isNotNull() || m_bNull;
		return m_bNull;
	}

	/**
	 * �Ƿ����ʾ. ��ȡ����ҵǩ������ֵʱ����Ҫ������ҳǩ���� ���ܷ�����ȷֵ
	 * 
	 * @return boolean
	 * 
	 * @link setShareTableCode
	 */
	public boolean isShow() {
		// ����ҵǩ����
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
	 * �Ƿ�ϼ�. ��������:(01-2-23 14:56:47)
	 * 
	 * @return boolean
	 */
	public boolean isTotal() {
		return m_bTotal;
	}

	/**
	 * �Ƿ�ʹ��������ʽ. ��������:(01-2-23 14:56:47)
	 * 
	 * @return boolean
	 */
	public boolean isWithIndex() {
		return m_bWithIndex;
	}

	/**
	 * ������ʾ����. ��������:(01-2-21 9:56:37)
	 * 
	 * @param newShow
	 *            boolean
	 */
	public void setBaseTableCodeShow(boolean newShow) {
		m_bShow = newShow;
	}

	/**
	 * ������������. ��������:(01-2-21 9:46:08)
	 * 
	 * @param newDataType
	 *            int
	 */
	public void setDataType(int newDataType) {
		m_iDataType = newDataType;
	}

	/**
	 * ��������:(2003-3-19 8:56:09)
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
	 * ���ñ༭����(����). ��������:(01-2-21 9:59:59)
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
	 * ���ñ༭����(��̬). ��������:(01-2-23 14:56:47)
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
	 * ��������:(2003-3-19 8:56:09)
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
	 * ����ID����. ��������:(01-2-21 9:47:57)
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
	 * ��������:(2003-8-20 10:39:41)
	 * 
	 * @param newIsDef
	 *            boolean
	 */
	public void setIsDef(boolean newIsDef) {
		isDef = newIsDef;
	}

	/**
	 * ��������:(01-2-21 9:50:17)
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
	 * ������������. ��������:(01-2-23 14:56:47)
	 * 
	 * @param newBLock
	 *            boolean
	 */
	public void setLock(boolean newBLock) {
		m_bLock = newBLock;
	}

	/**
	 * ��������:(2003-7-11 10:49:10)
	 * 
	 * @param newM_bDesign
	 *            boolean
	 */
	// public void setM_bDesign(boolean newM_bDesign) {
	// m_bDesign = newM_bDesign;
	// }
	/**
	 * ��������:(2003-8-4 13:17:40)
	 * 
	 * @param newM_bNewLineFlag
	 *            boolean
	 */
	public void setM_bNewLineFlag(boolean newM_bNewLineFlag) {
		m_bNewLineFlag = newM_bNewLineFlag;
	}

	/**
	 * ��������:(2003-6-18 16:48:07)
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
	 * ��������:(2003-8-20 16:07:02)
	 * 
	 * @param newM_bReviseFlag
	 *            boolean
	 */
	public void setM_bReviseFlag(boolean newM_bReviseFlag) {
		m_bReviseFlag = newM_bReviseFlag;
	}

	/**
	 * ��������. ��������:(01-2-21 9:47:57)
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
	 * ���ÿɿ�����. ��������:(01-2-21 9:56:37)
	 * 
	 * @param newShow
	 *            boolean
	 */
	public void setNull(boolean newNull) {
		m_bNull = newNull;
	}

	/**
	 * ������ʾλ��. ��������:(01-2-21 9:51:24)
	 * 
	 * @param newPos
	 *            int
	 */
	public void setPos(int newPos) {
		m_iPos = newPos;
	}

	/**
	 * ��������:(2002-11-19 21:15:43)
	 * 
	 * @param newM_nReadOrder
	 *            int
	 */
	public void setReadOrder(int newM_nReadOrder) {
		m_nReadOrder = newM_nReadOrder;
	}

	/**
	 * ���ò�������. ��������:(01-2-21 9:51:24)
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
	 * ������ʾ����. ��������:(01-2-21 9:56:37) ���ù���ҵǩ������ֵʱ����Ҫ������ҳǩ���� ����������ȷֵ
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
	 * ������ʾ˳��. ��������:(01-2-21 9:44:18)
	 * 
	 * @param newLength
	 *            int
	 */
	public void setShowOrder(int newShowOrder) {
		m_iShowOrder = newShowOrder;
	}

	// �����Ƿ���ʾǧ��λ
	void setShowThMark(boolean newValue) {
		if (getPos() == BODY)
			return;
	}

	/**
	 * ��������:(2002-09-09 10:51:40)
	 * 
	 * @param newTableCode
	 *            java.lang.String
	 */
	public void setTableCode(String newTableCode) {
		m_strTableCode = newTableCode;
	}

	/**
	 * ��������:(2002-09-09 10:51:40)
	 * 
	 * @param newTableCode
	 *            java.lang.String
	 */
	public void setTableName(String newTableName) {
		m_strTableName = newTableName;
	}

	/**
	 * ����Ԫ���Ƿ�ɺϼ�. ��������:(01-2-23 14:56:47)
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
	 * ���������򷵻�����. ��������:(01-2-21 9:56:37)
	 * 
	 * @param newShow
	 *            boolean
	 */
	public void setWithIndex(boolean newWithIndex) {
		m_bWithIndex = newWithIndex;
	}

	/**
	 * ��ʼ��ITEM. ��������:(01-2-21 9:47:57)
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

	//����ģ�����ݿ��Ƿ���ʾ����
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

}