package nc.vo.erm.termendtransact;

/**
 * 汇兑损益VO。
 * 主要承担汇兑损益币种选择界面的显示和其他情况的过滤条件
 * 创建日期：(2001-8-7 20:18:50)
 * 最后修改日期：(2001-8-7 20:18:50)
 * @author：wyan
 */
import java.util.Hashtable;
import java.util.Vector;

import nc.bs.framework.common.NCLocator;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.pubitf.bbd.CurrtypeQuery;
import nc.pubitf.org.IOrgUnitPubService;
import nc.pubitf.uapbd.DefaultCurrtypeQryUtil;
import nc.vo.bd.currtype.CurrtypeVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.org.OrgVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;

public class AgiotageVO extends nc.vo.pub.ValueObject {

	/**
	 *
	 */
	private static final long serialVersionUID = -3441494149924364782L;
	private Boolean m_bXzbz; /*选择标志*/
	private int mode;/*损益模式(月末还是外币结清) 0,1,2 */
	private boolean m_HsMode;/*是否主辅币核算*/
	private String m_sDwbm; /*单位编码*/
	private String m_sBzmc; /*币种名称*/
	private String m_sBzbm; /*币种编码*/
	private String m_sLocal;/*本币币种编码*/
	private String m_sClbh;/*处理编号*/
	private UFDate m_uLastTime; /*上次计算时间*/
	private UFDate m_sCalDate; /*本次计算日期*/
	private String m_sCalQj; /*本次计算日期所在期间*/
	private String m_sCalNd;/*本次计算日期所在年度*/
	private String m_sQjBeg; /*本次计算日期所在期间起始日*/
	private String m_sQjEnd; /*本次计算日期所在期间结束日*/
	private String m_sSfbz; /*収付标志*/
	private Vector m_sSelBzbm; /*用户选中的币种编码组*/
	private Hashtable m_pkAccids;/*币种对应的账户*/
	private String m_sDjlx;/*单据类型*/
	private String m_sDjbhBeg;/*单据编号开始*/
	private String m_sDjbhEnd;/*单据编号结束*/
	private String m_sDateBeg;/*单据日期开始*/
	private String m_sDateEnd;/*单据日期结束*/
	private String m_sCurrency;/*币种*/
	private String m_sMinje;/*最小金额*/
	private String m_sMaxje;/*最大金额*/
	private Je m_CalCe;/*计算汇兑损益的差额*/
	private String m_sUser;/*当前用户*/
	private String m_sSign;/*区分标志*/
	private Hashtable<String, String> allBz; //除本位币外的所有币种信息(bzbm-bzmc).

	private int queryType; //查询类型  0 汇总， 1 明晰
	private int agiotageType; //汇兑损益类型 0 全部 1 计提 2 回冲
	private String queryStr ; //查询字符串
	private boolean iszgAgiotage; //暂估单据是否计算汇兑损益
    
	private String queryCond ; //查询字符串
	private String busiType ; // 组织单元
	private String currType ; // 币种类型

	public static final int AGIOTAGE_SX = 2;   //已实现时计算
	public static final int AGIOTAGE_END = 1;  //月末计算
	public static final int AGIOTAGE_WB = 0;   //外币结清时计算
	
	public String getCurrType() {
		return currType;
	}

	public void setCurrType(String currType) {
		this.currType = currType;
	}
	public String getBusiType() {
		return busiType;
	}

	public void setBusiType(String busiType) {
		this.busiType = busiType;
	}

	public String getQueryCond() {
		return queryCond;
	}

	public void setQueryCond(String queryCond) {
		this.queryCond = queryCond;
	}

	public boolean isIszgAgiotage() {
		return iszgAgiotage;
	}

	public void setIszgAgiotage(boolean iszgAgiotage) {
		this.iszgAgiotage = iszgAgiotage;
	}

	public String getQueryStr() {
		return queryStr;
	}

	public void setQueryStr(String queryStr) {
		this.queryStr = queryStr;
	}

	public int getAgiotageType() {
		return agiotageType;
	}

	public void setAgiotageType(int agiotageType) {
		this.agiotageType = agiotageType;
	}

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public Hashtable<String, String> getAllBz() throws BusinessException {
		if(allBz==null && m_sDwbm!=null){
			try {
				getAllBzExceptLocal();
			} catch (BusinessException e) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v55-000195")/*@res "查询币种信息出错:"*/+e.getMessage(),e);
			}
		}
		return allBz;
	}

	public void setAllBz(Hashtable<String, String> allBz) {
		this.allBz = allBz;
	}

	public void getAllBzExceptLocal() throws BusinessException {
//      组织本为币
		String bzbm=Currency.getOrgLocalCurrPK(getDwbm());
		
		String[] fieldnames = new String[]{OrgVO.PK_ORG,OrgVO.PK_GROUP};
		OrgVO[] orgVO =NCLocator.getInstance().lookup(IOrgUnitPubService.class).getOrgs(new String[]{getDwbm()}, fieldnames);
		String pkGroup = orgVO[0].getPk_group();
		CurrtypeVO glb = DefaultCurrtypeQryUtil.getInstance().getGlobeDefaultCurrtype();
		CurrtypeVO grp = DefaultCurrtypeQryUtil.getInstance().getDefaultCurrtypeByOrgID(pkGroup);
		//集团本位币
		String groupcurr = grp.getPk_currtype();
//		全局本位币
		String globalcurr = glb.getPk_currtype();
		Hashtable<String, String> allBz=new Hashtable<String, String>();
		CurrtypeVO[] allCurrtypeVOs = CurrtypeQuery.getInstance().getAllCurrtypeVOs();
		for (int i = 0; i < allCurrtypeVOs.length; i++) {
			if(bzbm.equals(allCurrtypeVOs[i].getPk_currtype())&& groupcurr.equals(allCurrtypeVOs[i].getPk_currtype())
					&& globalcurr.equals(allCurrtypeVOs[i].getPk_currtype())){
			}else{
				allBz.put(allCurrtypeVOs[i].getPk_currtype(),allCurrtypeVOs[i].getName());
			}
		}

		setAllBz(allBz);
	}

/**
 * AgiotageVO 构造子注解。
 */
public AgiotageVO() {
	super();
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:02:51)
 * 最后修改日期：(2001-8-8 11:02:51)
 * @author：wyan
 * @return java.lang.String
 */
public String getBzbm() {
	return m_sBzbm;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:13:29)
 * 最后修改日期：(2001-8-8 11:13:29)
 * @author：wyan
 * @return java.lang.String
 */
public String getBzmc() {
	return m_sBzmc;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-29 13:28:43)
 * @return nc.vo.arap.transaction.Je
 */
public Je getCalCe() {
	return m_CalCe;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 10:26:57)
 * 最后修改日期：(2001-8-8 10:26:57)
 * @author：wyan
 * @return java.lang.String
 */
public UFDate getCalDate() {
	return m_sCalDate;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:03:03)
 * 最后修改日期：(2001-8-10 14:03:03)
 * @author：wyan
 * @return java.lang.String
 */
public String getCalNd() {
	return m_sCalNd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:03:03)
 * 最后修改日期：(2001-8-10 14:03:03)
 * @author：wyan
 * @return java.lang.String
 */
public String getCalQj() {
	return m_sCalQj;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 15:33:03)
 * 最后修改日期：(2001-8-10 15:33:03)
 * @author：wyan
 * @return java.lang.String
 */
public String getClbh() {
	return m_sClbh;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:10:46)
 * 最后修改日期：(2001-8-10 14:10:46)
 * @author：wyan
 * @return java.lang.String
 */
public String getCurrency() {
	return m_sCurrency;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:09:31)
 * 最后修改日期：(2001-8-10 14:09:31)
 * @author：wyan
 * @return java.lang.String
 */
public String getDateBeg() {
	return m_sDateBeg;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:10:08)
 * 最后修改日期：(2001-8-10 14:10:08)
 * @author：wyan
 * @return java.lang.String
 */
public String getDateEnd() {
	return m_sDateEnd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:09:31)
 * 最后修改日期：(2001-8-10 14:09:31)
 * @author：wyan
 * @return java.lang.String
 */
public String getDjbhBeg() {
	return m_sDjbhBeg;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:10:08)
 * 最后修改日期：(2001-8-10 14:10:08)
 * @author：wyan
 * @return java.lang.String
 */
public String getDjbhEnd() {
	return m_sDjbhEnd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:05:49)
 * 最后修改日期：(2001-8-10 14:05:49)
 * @author：wyan
 * @return java.lang.String
 */
public String getDjlx() {
    return m_sDjlx;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:01:26)
 * 最后修改日期：(2001-8-8 11:01:26)
 * @author：wyan
 * @return java.lang.String
 */
public String getDwbm() {
	return m_sDwbm;
}
/**
 * 返回数值对象的显示名称。
 *
 * 创建日期：(2001-2-15 14:18:08)
 * @return java.lang.String 返回数值对象的显示名称。
 */
public String getEntityName() {
	return null;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-6 16:52:40)
 * @return boolean
 */
public boolean getHsMode() {
	return m_HsMode;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 16:22:31)
 * 最后修改日期：(2001-8-10 16:22:31)
 * @author：wyan
 * @return java.lang.String
 */
public int getModeType() {
	return mode;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 10:26:11)
 * 最后修改日期：(2001-8-8 10:26:11)
 * @author：wyan
 * @return nc.vo.pub.lang.UFDate
 */
public UFDate getLastTime() {
	return m_uLastTime;
}
/**
 * a功能:
 * 作者：wyan
 * 创建时间：(2002-5-14 11:28:21)
 * 参数：<|>
 * 返回值：
 * 算法：
 * @return java.lang.String
 */
public String getLocal() {
	return m_sLocal;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:12:22)
 * 最后修改日期：(2001-8-10 14:12:22)
 * @author：wyan
 * @return java.lang.String
 */
public String getMaxJe() {
	return m_sMaxje;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:12:07)
 * 最后修改日期：(2001-8-10 14:12:07)
 * @author：wyan
 * @return java.lang.String
 */
public String getMinJe() {
	return m_sMinje;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-28 13:49:39)
 * @return java.util.Hashtable
 */
public Hashtable getPkAccids() {
	return m_pkAccids;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:45:58)
 * 最后修改日期：(2001-8-8 11:45:58)
 * @author：wyan
 * @return java.lang.String
 */
public String getQjBeg() {
	return m_sQjBeg;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:46:28)
 * 最后修改日期：(2001-8-8 11:46:28)
 * @author：wyan
 * @return java.lang.String
 */
public String getQjEnd() {
	return m_sQjEnd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 10:25:02)
 * 最后修改日期：(2001-8-8 10:25:02)
 * @author：wyan
 * @return java.lang.String[]
 */
public Vector getSelBzbm() {
	return m_sSelBzbm;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 13:55:24)
 * 最后修改日期：(2001-8-10 13:55:24)
 * @author：wyan
 * @return java.lang.String
 */
public String getSfbz() {
	return m_sSfbz;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-7-3 15:33:19)
 * @return java.lang.String
 */
public String getSign() {
	return m_sSign;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 18:42:43)
 * 最后修改日期：(2001-8-10 18:42:43)
 * @author：wyan
 * @return java.lang.String
 */
public String getUser() {
	return m_sUser;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:52:50)
 * 最后修改日期：(2001-8-8 11:52:50)
 * @author：wyan
 * @return java.lang.Boolean
 */
public Boolean getXzbz() {
	return m_bXzbz;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:02:09)
 * 最后修改日期：(2001-8-8 11:02:09)
 * @author：wyan
 * @param bzbm java.lang.String
 */
public void setBzbm(String bzbm) {
    m_sBzbm = bzbm;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:13:09)
 * 最后修改日期：(2001-8-8 11:13:09)
 * @author：wyan
 * @param bzmc java.lang.String
 */
public void setBzmc(String bzmc) {
    m_sBzmc = bzmc;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-29 13:29:07)
 * @param ce nc.vo.arap.transaction.Je
 */
public void setCalCe(Je ce) {
    m_CalCe = ce;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 10:26:41)
 * 最后修改日期：(2001-8-8 10:26:41)
 * @author：wyan
 * @param date java.lang.String
 */
public void setCalDate(UFDate calDate) {
	m_sCalDate = calDate;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:02:47)
 * 最后修改日期：(2001-8-10 14:02:47)
 * @author：wyan
 * @param qj java.lang.String
 */
public void setCalNd(String calNd) {
	m_sCalNd = calNd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:02:47)
 * 最后修改日期：(2001-8-10 14:02:47)
 * @author：wyan
 * @param qj java.lang.String
 */
public void setCalQj(String calQj) {
    m_sCalQj = calQj;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 15:33:22)
 * 最后修改日期：(2001-8-10 15:33:22)
 * @author：wyan
 * @param clbh java.lang.String
 */
public void setClbh(String clbh) {
    m_sClbh = clbh;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:10:30)
 * 最后修改日期：(2001-8-10 14:10:30)
 * @author：wyan
 * @param currency java.lang.String
 */
public void setCurrency(String currency) {
    m_sCurrency = currency;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:09:15)
 * 最后修改日期：(2001-8-10 14:09:15)
 * @author：wyan
 * @param datebeg java.lang.String
 */
public void setDateBeg(String dateBeg) {
	m_sDateBeg = dateBeg;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:09:53)
 * 最后修改日期：(2001-8-10 14:09:53)
 * @author：wyan
 * @param dateEnd java.lang.String
 */
public void setDateEnd(String dateEnd) {
	m_sDateEnd = dateEnd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:09:15)
 * 最后修改日期：(2001-8-10 14:09:15)
 * @author：wyan
 * @param datebeg java.lang.String
 */
public void setDjbhBeg(String djbhBeg) {
	m_sDjbhBeg = djbhBeg;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:09:53)
 * 最后修改日期：(2001-8-10 14:09:53)
 * @author：wyan
 * @param dateEnd java.lang.String
 */
public void setDjbhEnd(String djbhEnd) {
	m_sDjbhEnd = djbhEnd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:05:33)
 * 最后修改日期：(2001-8-10 14:05:33)
 * @author：wyan
 * @param custcode java.lang.String
 */
public void setDjlx(String djlx) {
	m_sDjlx = djlx;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:01:05)
 * 最后修改日期：(2001-8-8 11:01:05)
 * @author：wyan
 * @param dwbm java.lang.String
 */
public void setDwbm(String dwbm) {
    m_sDwbm = dwbm;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2001-11-6 16:52:01)
 * @param mode boolean
 */
public void setHsMode(boolean mode) {
    m_HsMode = mode;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 10:25:53)
 * 最后修改日期：(2001-8-8 10:25:53)
 * @author：wyan
 * @param date nc.vo.pub.lang.UFDate
 */
public void setLastTime(UFDate date) {
	m_uLastTime = date;
}
/**
 * a功能:
 * 作者：wyan
 * 创建时间：(2002-5-14 11:26:46)
 * 参数：<|>
 * 返回值：
 * 算法：
 * @param Bbpk java.lang.String
 */
public void setLocal(String Bbpk) {
    m_sLocal = Bbpk;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:11:52)
 * 最后修改日期：(2001-8-10 14:11:52)
 * @author：wyan
 * @param maxje java.lang.String
 */
public void setMaxJe(String maxje) {
    m_sMaxje = maxje;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 14:11:29)
 * 最后修改日期：(2001-8-10 14:11:29)
 * @author：wyan
 * @param minje java.lang.String
 */
public void setMinJe(String minje) {
    m_sMinje = minje;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 16:23:00)
 * 最后修改日期：(2001-8-10 16:23:00)
 * @author：wyan
 * @param mode java.lang.String
 */
public void setMode(int mode) {
	this.mode = mode;
}
/**
 * 此处插入方法描述。
 * 创建日期：(2003-9-28 13:50:39)
 * @param accids java.util.Hashtable
 */
public void setPkAccids(Hashtable accids) {
    m_pkAccids = accids;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:47:47)
 * 最后修改日期：(2001-8-8 11:47:47)
 * @author：wyan
 * @param begDate java.lang.String
 */
public void setQjBeg(String qjBeg) {
    m_sQjBeg = qjBeg;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:47:22)
 * 最后修改日期：(2001-8-8 11:47:22)
 * @author：wyan
 * @param endDate java.lang.String
 */
public void setQjEnd(String qjEnd) {
	m_sQjEnd = qjEnd;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 10:24:44)
 * 最后修改日期：(2001-8-8 10:24:44)
 * @author：wyan
 * @param bzbms java.lang.String[]
 */
public void setSelBzbm(Vector bzbms) {
	m_sSelBzbm = bzbms;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 13:55:06)
 * 最后修改日期：(2001-8-10 13:55:06)
 * @author：wyan
 * @param sfbz java.lang.String
 */
public void setSfbz(String sfbz) {
    m_sSfbz = sfbz;
}
/**
 * 此处插入方法说明。
 * 创建日期：(2002-7-3 15:32:56)
 * @param sign java.lang.String
 */
public void setSign(String sign) {
    m_sSign = sign;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-10 18:43:08)
 * 最后修改日期：(2001-8-10 18:43:08)
 * @author：wyan
 * @param user java.lang.String
 */
public void setUser(String user) {
    m_sUser = user;
}
/**
 * 主要功能：
 * 主要算法：
 * 异常描述：
 * 创建日期：(2001-8-8 11:52:30)
 * 最后修改日期：(2001-8-8 11:52:30)
 * @author：wyan
 * @param xzbz java.lang.Boolean
 */
public void setXzbz(Boolean xzbz) {
	m_bXzbz = xzbz;
}
/**
 * 验证对象各属性之间的数据逻辑正确性。
 *
 * 创建日期：(2001-2-15 11:47:35)
 * @exception nc.vo.pub.ValidationException 如果验证失败，抛出
 *     ValidationException，对错误进行解释。
 */
public void validate() throws nc.vo.pub.ValidationException {}

public static int getMode(String dwbm,Integer system) throws BusinessException{

	String paraString=system==0?"AR5":"AP3";
	int smode = AgiotageVO.AGIOTAGE_WB;
	String mode=nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2006","UPP2006-v55-000194")/*@res "月末计算"*/;
	try {
		mode = SysInit.getParaString(dwbm, paraString);
		
		if (mode.equals("月末计算"))	/*-=notranslate=-*/
			smode = AgiotageVO.AGIOTAGE_END;
		else if (mode.equals("计算已实现汇兑损益"))	/*-=notranslate=-*/
			smode = AgiotageVO.AGIOTAGE_SX;
	} catch (BusinessException e) {
		ExceptionHandler.handleException(e);
	}


	return smode;
}
}