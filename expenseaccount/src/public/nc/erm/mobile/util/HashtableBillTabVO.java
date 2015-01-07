package nc.erm.mobile.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;

import nc.ui.pub.bill.IBillItem;
import nc.vo.pub.bill.BillStructVO;
import nc.vo.pub.bill.BillTabVO;
import nc.vo.pub.bill.BillTempletHeadVO;

/**
 * key=BillItem.pos + tablecode, value = BillTabVO.
 * 创建日期:(2003-7-2 9:23:51)
 */
@SuppressWarnings("serial")
public class HashtableBillTabVO extends java.util.Hashtable<String,BillTabVO> implements IBillItem {
	/**
	 * HashtableBillTabVO 构造子注解.
	 */
	public HashtableBillTabVO() {
		super();
	}
	/**
	 * 
	 * 创建日期:(2003-7-2 9:26:36)
	 * @param pos       int
	 * @param tableCode java.lang.String
	 * @param tableName java.lang.String
	 */
	void add(int pos, String tableCode, String tableName) {
		BillTabVO vo = get(pos + tableCode);
		if (vo == null){
			vo = createBillTabVO(pos, tableCode);
			BillTabVO[] vos = getTabVos(pos);
			if(vos != null)
				vo.setTabindex(vos.length);
		}
		if (tableName != null && (tableName = tableName.trim()).length() > 0)
			vo.setTabname(tableName);
		else
			vo.setTabname(tableCode);
		put(pos + tableCode, vo);
	}
	/**
	 * 
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	public static BillTabVO createBillTabVO(int pos, String tableCode) {
		BillTabVO btvo = new BillTabVO();
		btvo.setPos(Integer.valueOf(pos));
		btvo.setTabcode(tableCode);
		return btvo;
	}
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	BillTabVO[] getAllTabVos() {
		if (size() == 0)
			return null;
		Iterator it = this.values().iterator(); 
		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
		while (it.hasNext()) {
			list.add((BillTabVO)it.next());
		}
		if (list.size() == 0)
			return null;
		BillTabVO[] vos = list.toArray(new BillTabVO[list.size()]);
		ItemSortUtil.sortBillTabVO(vos);
		return vos;
	}
	
	
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String getBodyBaseTableCode(String shareTableCode) {
//		if (shareTableCode == null || (shareTableCode = shareTableCode.trim()).length() == 0)
//			return null;
//		BillTabVO btvo = (BillTabVO) get(BODY + shareTableCode);
//		if (btvo == null)
//			return shareTableCode;
//		String baseCode = btvo.getBasetab();
//		if (baseCode == null || (baseCode = baseCode.trim()).length() == 0)
//			return shareTableCode;
//		btvo = (BillTabVO) get(BODY + baseCode);
//		if (btvo != null)
//			return baseCode;
//		return shareTableCode;
		return getBaseTableCode(BODY,shareTableCode);
	}
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String getBaseTableCode(int pos,String shareTableCode) {
		if (shareTableCode == null || (shareTableCode = shareTableCode.trim()).length() == 0)
			return null;
		BillTabVO btvo = (BillTabVO) get(pos + shareTableCode);
		if (btvo == null)
			return shareTableCode;
		String baseCode = btvo.getBasetab();
		if (baseCode == null || (baseCode = baseCode.trim()).length() == 0)
			return shareTableCode;
		btvo = (BillTabVO) get(pos + baseCode);
		if (btvo != null)
			return baseCode;
		return shareTableCode;
	}
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String[] getBodyBaseTableCodes() {
		if (size() == 0)
			return null;
		String key = null;
		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
		BillTabVO btvo;
		String basecode;
		Iterator it = keySet().iterator(); 
		while (it.hasNext()) {
			key = (String) it.next();
			if (key.startsWith(BODY + "")) {
				btvo = (BillTabVO) get(key);
				basecode = (basecode = btvo.getBasetab()) != null ? basecode.trim() : null;
				if (basecode == null || basecode.length() == 0)
					list.add(btvo);
			}
		}
		if (list.size() > 0) {
			BillTabVO[] vos = list.toArray(new BillTabVO[list.size()]);
			ItemSortUtil.sortBillTabVO(vos);
			String[] codes = new String[vos.length];
			for (int i = 0; i < vos.length; i++)
				codes[i] = vos[i].getTabcode();
			return codes;
		}
		return null;
	}
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String[] getBodyShareTableCodes(String basecode) {
		return getShareTableCodes(BODY,basecode);
	}

	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String[] getShareTableCodes(int pos,String basecode) {
		
		if (basecode == null || (basecode = basecode.trim()).length() == 0)
			return null;
		
		BillTabVO[] vos = getTabVos(pos);
		if (vos == null)
			return null;
		ArrayList<String> list = new ArrayList<String>();
		
		for (int i = 0; i < vos.length; i++) {
			if (basecode.equals(vos[i].getBasetab()) && !basecode.equals(vos[i].getTabcode()))
				list.add(vos[i].getTabcode());
		}
		if (list.size() > 0) {
			return list.toArray(new String[list.size()]);
		}
		return null;
	}

	/**
	 * key = pos + tableCode.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String[] getKeys() {
		int size = size();
		if (size == 0)
			return null;
		Iterator it = keySet().iterator();
		ArrayList<String> list = new ArrayList<String>();
		while (it.hasNext()) {
			list.add((String)it.next());
		}
		return list.toArray(new String[list.size()]);
	}
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	String[] getTableCodes(int pos) {
		BillTabVO[] vos = getTabVos(pos);
		if (vos == null)
			return null;
		String[] tableCodes = new String[vos.length];
		for (int i = 0; i < vos.length; i++)
			tableCodes[i] = vos[i].getTabcode();
		return tableCodes;
	}
	/**
	 * 
	 * 创建日期:(2002-09-12 13:20:13)
	 * @param tablecode java.lang.String
	 * @return java.lang.String
	 */
	public String getTableName(int pos, String tablecode) {
		if (validate(pos, tablecode) != null)
			return null;
		BillTabVO vo = (BillTabVO) get(pos + tablecode);
		if (vo == null)
			return null;
		return vo.getTabname();
	}
	public static String validate(int pos, String tableCode)
	{
	    String msg = validatePos(pos);
	    if (msg != null)
	      return msg;
	    return validateTableCode(tableCode);
	}
	
	public static String validatePos(int pos)
	{
	    if ((pos < 0) || (pos > 2)) {
	      return "位置错误";
	    }
	    

	    return null;
	}
	
	public static String validateTableCode(String tableCode)
	{
	    if ((tableCode == null) || (tableCode.trim().length() == 0)) {
	      return "位置错误";
	    }
	    return null;
	}
	/**
	 * 
	 * 创建日期:(2003-7-2 9:40:44)
	 * @param pos       int
	 * @param tableCode java.lang.String
	 * @return nc.vo.pub.bill.BillTabVO
	 */
	public BillTabVO getTabVO(int pos, String tableCode) {
		return (BillTabVO) get(pos + tableCode);
	}
	/**
	 * 返回业签 基础+共享.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	BillTabVO[] getTabVos(int pos) {
//		if (size() == 0)
//			return null;
////		Enumeration enum = keys();
//		Iterator it = keySet().iterator();
//		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
//		while (it.hasNext()) {
//			String key = (String) it.next();
//			if (key.startsWith(pos + ""))
//				list.add(get(key));
//		}
//		if (list.size() == 0)
//			return null;
//		BillTabVO[] vos = list.toArray(new BillTabVO[list.size()]);
//		BillUtil.sortBillTabVO(vos);
//		if (vos != null) {
//			for (int i = 0; i < vos.length; i++) {
//				vos[i].setTabindex(new Integer(i));
//			}
//		}
		BillTabVO[] vos = getBaseTabVos(pos);
		
		if(vos != null){
			ArrayList<BillTabVO> alllist = new ArrayList<BillTabVO>();
			for (int i = 0; i < vos.length; i++) {
				alllist.add(vos[i]);
				BillTabVO[] sharetab = getShareTabVOs(pos, vos[i].getTabcode());
				if(sharetab != null){
					for (int j = 0; j < sharetab.length; j++) {
						alllist.add(sharetab[j]);
					}
				}
			}
			vos = alllist.toArray(new BillTabVO[alllist.size()]);
	
			if (vos != null) {
				for (int i = 0; i < vos.length; i++) {
					vos[i].setTabindex(Integer.valueOf(i));
				}
			}
		}
		return vos;
	}
	
	/**
	 * 返回基础业签.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	BillTabVO[] getBaseTabVos(int pos) {
		if (this.size() == 0)
			return null;

		Iterator it = this.values().iterator(); 
		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
		while (it.hasNext()) {
			BillTabVO vo = (BillTabVO) it.next();
			if (vo.getPos() == pos && vo.getBasetab() == null)
				list.add(vo);
		}
		if (list.size() == 0)
			return null;
		BillTabVO[] vos = list.toArray(new BillTabVO[list.size()]);
		ItemSortUtil.sortBillTabVO(vos);
		
		return vos;
	}

	/**
	 * 返回共享业签.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
     BillTabVO[] getShareTabVOs(int pos,String basecode) {
		
		if (basecode == null || (basecode = basecode.trim()).length() == 0)
			return null;
		
		Iterator it = this.values().iterator(); 
		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
		while (it.hasNext()) {
			BillTabVO vo = (BillTabVO)it.next();
			if (vo.getPos() == pos && basecode.equals(vo.getBasetab()) && !basecode.equals(vo.getTabcode()))
				list.add(vo);
		}
		if (list.size() > 0) {
			BillTabVO[] tabvos = list.toArray(new BillTabVO[list.size()]);
			ItemSortUtil.sortBillTabVO(tabvos);
			return tabvos;
		}
		return null;
	}

     /**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	BillTabVO[] getTabVos(Integer position) {
//		if (position == null)
//			return null;
//		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
//		BillTabVO btvo = null;
//		Iterator it = values().iterator();
//		while (it.hasNext()) {
//			btvo = (BillTabVO) it.next();
//			if (btvo.getPosition() == null)
//				btvo.setPosition(btvo.getPos());
//			if (position.intValue() == btvo.getPosition().intValue())
//				list.add(btvo);
//		}
//		if (list.size() == 0)
//			return null;
//		BillTabVO[] vos = (BillTabVO[]) list.toArray(new BillTabVO[list.size()]);
//		if (vos != null) {
//			for (int i = 0; i < vos.length; i++) {
//				if (vos[i].getMixindex() == null)
//					vos[i].setMixindex(vos[i].getTabindex());
//			}
//		}
//		BillUtil.sortBillTabVOByMixIndex(vos);
//		if (vos != null) {
//			for (int i = 0; i < vos.length; i++) {
//				vos[i].setMixindex(new Integer(i));
//			}
//		}
		BillTabVO[] vos = getTabVos(position, false);
		return vos;
	}

	BillTabVO[] getBaseTabVos(Integer position) {
		BillTabVO[] vos = getTabVos(position, true);
		return vos;
	}
	/**
	 * getHeadTableCodes or getTableCodes.
	 * 创建日期:(2002-12-5 11:10:59)
	 */
	private BillTabVO[] getTabVos(Integer position,boolean isbase) {
		if (position == null)
			return null;
		ArrayList<BillTabVO> list = new ArrayList<BillTabVO>();
		BillTabVO btvo = null;
		Iterator it = values().iterator();
		while (it.hasNext()) {
			btvo = (BillTabVO) it.next();
			if (btvo.getPosition() == null)
				btvo.setPosition(btvo.getPos());
			if (position.intValue() == btvo.getPosition().intValue())
				if(isbase && btvo.getBasetab() == null)
					list.add(btvo);
				else
					list.add(btvo);
		}
		if (list.size() == 0)
			return null;
		
		BillTabVO[] vos = (BillTabVO[]) list.toArray(new BillTabVO[list.size()]);
		if (vos != null) {
			for (int i = 0; i < vos.length; i++) {
				if (vos[i].getMixindex() == null)
					vos[i].setMixindex(vos[i].getTabindex());
			}
		}
		sortBillTabVOByMixIndex(vos);
		if (vos != null) {
			for (int i = 0; i < vos.length; i++) {
				vos[i].setMixindex(Integer.valueOf(i));
			}
		}
		return vos;
	}

	 public static void sortBillTabVOByMixIndex(BillTabVO[] vos)
	  {
	    if ((vos == null) || (vos.length == 0)) {
	      return;
	    }
	    Comparator<BillTabVO> c = new Comparator() {
	      public int compare(BillTabVO o1, BillTabVO o2) {
	        Integer i1 = o1.getMixindex();
	        Integer i2 = o2.getMixindex();
	        return (i1 == null ? 0 : i1.intValue()) - (i2 == null ? 0 : i2.intValue());
	      }

		@Override
		public int compare(Object o1, Object o2) {
			// TODO Auto-generated method stub
			return 0;
		}
	      
	    };
	    Arrays.sort(vos, c);
	  }
	/**
	 * 
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	void initByBillTabVOs(BillTabVO[] btvos) {
		clear();
		if (btvos == null || btvos.length == 0)
			return;
		int pos;
		String tableCode;
		for (int i = 0; i < btvos.length; i++) {
			pos = btvos[i].getPos().intValue();
			tableCode = btvos[i].getTabcode();
			if (validatePos(pos) == null && tableCode != null) {
				put(pos + tableCode, btvos[i]);
			}
		}
	}
	/**
	 * 
	 * 创建日期:(2003-6-30 14:46:50)
	 */
	BillStructVO initByHeadVO(BillTempletHeadVO headvo) {
		clear();
		if (headvo == null)
			return null;
		BillStructVO btVO = headvo.getStructvo();
//		String xml = headvo.getOptions();
//		if (xml == null || (xml = xml.trim()).length() == 0)
//			return null;
//		BillStructVO btVO = BillUtil.xmlToBillStructVO(xml);
		if(btVO == null)
		    return null;
		BillTabVO[] btvos = btVO.getBillTabVOs(); //BillUtil.xmlToBillTab(xml);
		if (btvos != null) {
			int pos;
			String tableCode;
			for (int i = 0; i < btvos.length; i++) {
				pos = btvos[i].getPos().intValue();
				tableCode = btvos[i].getTabcode();
				if (validatePos(pos) == null && tableCode != null) {
					put(pos + tableCode, btvos[i]);
				}
			}
		}
		return btVO;
	}
	
	/**
	 * 
	 * 创建日期:(2003-7-3 14:46:55)
	 * @param tableCode java.lang.String
	 */
	boolean isBodyBaseCode(String tableCode) {
		return isBaseCode(BODY,tableCode);
	}

	/**
	 * 
	 * 创建日期:(2003-7-3 14:46:55)
	 * @param tableCode java.lang.String
	 */
	boolean isBaseCode(int pos,String tableCode) {
		if (tableCode == null || (tableCode = tableCode.trim()).length() == 0)
			return false;
		String baseCode = getBaseTableCode(pos,tableCode);
		if (baseCode == null || baseCode.equals(tableCode))
			return true;
		return false;
	}
	/**
	 * 
	 * 创建日期:(2003-7-9 14:52:49)
	 */
	void newMethod() {
	}
	/**
	 * 
	 * 创建日期:(2003-7-2 9:30:29)
	 * @param pos       int
	 * @param tableCode java.lang.String
	 */
	void remove(int pos, String tableCode) {
		if (pos == BODY) {
			String[] sharecodes = getBodyShareTableCodes(tableCode);
			if (sharecodes != null) {
				for (int i = 0; i < sharecodes.length; i++)
					remove(pos + sharecodes[i]);
			}
		}
		remove(pos + tableCode);
	}
	/*
	*mapPosKey (pos + tablecode,"")
	*/
	void filterInvalidTab(HashSet mapPosKey) {
		String[] keys = getKeys();
		BillTabVO vo;
		String bc;
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				if (!mapPosKey.contains(keys[i])) {
					vo = (BillTabVO) get(keys[i]);
					bc = vo.getBasetab();
					if (bc == null || bc.length() == 0 || !containsKey(vo.getPos() + bc))
						remove(keys[i]);
				}
			}
		}
	}
	/*
	* keys   --- pos+code
	*/
	void filterInvalidTab(String[] keys) {
        HashSet<String> map = new HashSet<String>();
		if (keys != null) {
			for (int i = 0; i < keys.length; i++) {
				map.add(keys[i]);
			}
		}
		filterInvalidTab(map);
	}
}
