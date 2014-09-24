package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.cmp.busi.ISettleinfoCarrier;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.cmp.busi.IGetTargetVO;
import nc.vo.cmp.settlement.SettlementAggVO;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.costshare.AggCostShareVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.SuperVO;
import nc.vo.pub.lang.UFDouble;
import nc.vo.trade.pub.IExAggVO;

/**
 * 借款报销聚合VO v6.1修改将借款和报销单vo拆开提取此公共类为abstract，借款和报销分别继承
 * 
 * @author rocking
 * @modify twei nc.vo.ep.bx.BXVO
 */
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.ep.bx.JKBXHeaderVO")
public abstract class JKBXVO extends AggregatedValueObject implements ISettleinfoCarrier, IGetTargetVO, IExAggVO {

	private static final long serialVersionUID = 2574120515388447427L;

	protected BXBusItemVO[] childrenVO;

	protected BxcontrastVO[] contrastVO;

	protected CShareDetailVO[] cShareDetailVo;// 分摊信息
	
	protected AccruedVerifyVO[] accruedVerifyVO;// 核销预提明细

	protected JKBXHeaderVO parentVO;

	/**
	 * 拉单时拉到费用申请单表头vo
	 */
	protected MatterAppVO maheadvo;

	protected JKBXVO bxoldvo;

	protected AggCostShareVO costOldVo;

	protected JKHeaderVO[] jkHeadVOs = null;// 报销单冲借款单所关联的借款单集合

	protected Map<String, List<SettlementBodyVO>> settlementMap;

	protected Map<String, JKBXHeaderVO> jkdMap;

	protected boolean isChildrenFetched = false;
	
	//是否从NC客户端
	private boolean isNCClient=false;
	
	public boolean isNCClient() {
		return isNCClient;
	}

	public void setNCClient(boolean isNCClient) {
		this.isNCClient = isNCClient;
	}
	
	/**
	 * 借款报销单自己关联的申请记录vo集合
	 */
	protected MtapppfVO[] maPfVos;
	
	/**
	 * 报销单冲借款关联的申请记录vo集合
	 */
	protected MtapppfVO[] contrastMaPfVos;

	/**
	 * 构造方法
	 * 
	 * @param djlx
	 */
	public JKBXVO(String djlx) {
		parentVO = VOFactory.createHeadVO(djlx);
		childrenVO = new BXBusItemVO[] {};
		settlementMap = new HashMap<String, List<SettlementBodyVO>>();
		jkdMap = new HashMap<String, JKBXHeaderVO>();
	}

	public JKBXVO(CircularlyAccessibleValueObject head, CircularlyAccessibleValueObject[] bXBusItemVOs) {
		this.setParentVO(head);
		this.setBxBusItemVOS((BXBusItemVO[]) bXBusItemVOs);
	}

	public JKBXVO(SuperVO head) {
		this.setParentVO(head);
		childrenVO = new BXBusItemVO[] {};
	}

	public boolean isChildrenFetched() {
		return isChildrenFetched;
	}

	public void setChildrenFetched(boolean isChildrenFetched) {
		this.isChildrenFetched = isChildrenFetched;
	}

	public BXBusItemVO[] getBxBusItemVOS() {
		return childrenVO;
	}

	public void setBxBusItemVOS(BXBusItemVO[] bxBusItemVOS) {
		this.childrenVO = bxBusItemVOS;
	}

	@Override
	public BXBusItemVO[] getChildrenVO() {
		return childrenVO;
	}

	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] childrenVO) {
		this.childrenVO = (BXBusItemVO[]) childrenVO;
	}

	@Override
	public JKBXHeaderVO getParentVO() {
		return parentVO;
	}

	@Override
	public void setParentVO(CircularlyAccessibleValueObject parentVO) {
		this.parentVO = (JKBXHeaderVO) parentVO;
	}

	@Override
	public Object clone() {

		if (parentVO == null) {
			throw new IllegalArgumentException();
		}

		JKBXVO bxVO = VOFactory.createVO(parentVO.getDjdl());
		JKBXHeaderVO voHeader = (JKBXHeaderVO) parentVO.clone();

		BXBusItemVO[] voItem = null;

		if (childrenVO != null) {
			voItem = new BXBusItemVO[childrenVO.length];
			for (int i = 0; i < childrenVO.length; i++)
				if (childrenVO[i] != null)
					voItem[i] = (BXBusItemVO) childrenVO[i].clone();
		}

		CShareDetailVO[] voCShareVo = null;
		if (cShareDetailVo != null) {
			voCShareVo = new CShareDetailVO[cShareDetailVo.length];
			for (int i = 0; i < cShareDetailVo.length; i++)
				if (cShareDetailVo[i] != null)
					voCShareVo[i] = (CShareDetailVO) cShareDetailVo[i].clone();
		}
		AccruedVerifyVO[] accVerifyVO = null;
		if (accruedVerifyVO != null) {
			accVerifyVO = new AccruedVerifyVO[accruedVerifyVO.length];
			for (int i = 0; i < accruedVerifyVO.length; i++)
				if (accruedVerifyVO[i] != null)
					accVerifyVO[i] = (AccruedVerifyVO) accruedVerifyVO[i].clone();
		}
		// 冲销信息复制
		BxcontrastVO[] voContrastVo = null;
		if (contrastVO != null) {
			voContrastVo = new BxcontrastVO[contrastVO.length];
			for (int i = 0; i < contrastVO.length; i++) {
				if (contrastVO[i] != null)
					voContrastVo[i] = (BxcontrastVO) contrastVO[i].clone();
			}
		}

		// //预算占用期间:以后可能需要
		// ERMTbbDetailVO[] vobxtbbDetailVO =null;
		// if(bxtbbDetailVO!=null){
		// vobxtbbDetailVO = new ERMTbbDetailVO[bxtbbDetailVO.length];
		// for(int i = 0; i < bxtbbDetailVO.length; i++){
		// if(bxtbbDetailVO[i]!=null){
		// vobxtbbDetailVO[i] = (ERMTbbDetailVO)bxtbbDetailVO[i].clone();
		// }
		// }
		// }

		// 拉单信息复制
		MtapppfVO[] voMaPfVos = null;
		if (this.maPfVos != null) {
			voMaPfVos = new MtapppfVO[this.maPfVos.length];
			for (int i = 0; i < maPfVos.length; i++) {
				if (this.maPfVos[i] != null) {
					voMaPfVos[i] = (MtapppfVO) this.maPfVos[i].clone();
				}
			}
		}

		bxVO.setParentVO(voHeader);
		bxVO.setBxBusItemVOS(voItem);
		bxVO.setcShareDetailVo(voCShareVo);
		bxVO.setContrastVO(voContrastVo);
		bxVO.setSettlementMap(getSettlementMap());
		bxVO.setJkdMap(getJkdMap());
		bxVO.setMaPfVos(voMaPfVos);
		bxVO.setAccruedVerifyVO(accVerifyVO);

		if (this.getBxoldvo() != null) {
			bxVO.setBxoldvo((JKBXVO) this.getBxoldvo().clone());
		}

		return bxVO;
	}

	public String[] getTableCodes() {
		if (getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			return new String[] { new BXBusItemVO().getTableName(), new BxcontrastVO().getTableName(),
					"costsharedetail",BXConstans.AccruedVerify_Metadatapath};
		} else {
			return new String[] { BXConstans.BUS_PAGE_JK, BXConstans.CONST_PAGE_JK };
		}
	}

	/**
	 * 是否有分摊信息
	 * 
	 * @return
	 */
	public boolean isHasCShareDetail() {
		if (cShareDetailVo != null && cShareDetailVo.length > 0) {
			return true;
		}
		return false;
	}

	public String[] getTableNames() {

		if (getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000279") /*
																												 * @
																												 * res
																												 * "业务信息"
																												 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000963") /*
																									 * @
																									 * res
																									 * "报销单冲销对照行"
																									 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000964") /*
																									 * @
																									 * res
																									 * "费用分摊明细"
																									 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000965") /*
																									 * @
																									 * res
																									 * "报销核销预提明细"
																									 */};
		} else {
			return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000279") /*
																												 * @
																												 * res
																												 * "业务信息"
																												 */,
					nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000963") /*
																									 * @
																									 * res
																									 * "报销单冲销对照行"
																									 */};
		}
		
	}

	public CircularlyAccessibleValueObject[] getTableVO(String tableCode) {
		List<SuperVO> list = new ArrayList<SuperVO>();
		if (BXConstans.CONST_PAGE.equals(tableCode) || BXConstans.CONST_PAGE_JK.equals(tableCode)) {
			return contrastVO;
		} else if (BXConstans.CSHARE_PAGE.equals(tableCode) || "costsharedetail".equals(tableCode)) {
			return cShareDetailVo;
		} else if (BXConstans.AccruedVerify_TABLECODE.equals(tableCode) || BXConstans.AccruedVerify_PAGE.equals(tableCode)) {
			return accruedVerifyVO;
		}else if (childrenVO != null && childrenVO.length != 0) {
			if (BXBusItemVO.getDefaultTableName().equals(tableCode)) {
				// 适用于会计平台凭证模版取值
				return childrenVO;
			}
			// 适用于单据模版取值
			for (int i = 0; i < childrenVO.length; i++) {
				if (childrenVO[i].getTablecode().equals(tableCode)) {
					list.add(childrenVO[i]);
				}
			}
		}

		return list.toArray(new SuperVO[] {});
	}

	// @Override
	public void setTableVO(String tableCode, CircularlyAccessibleValueObject[] values) {

		if (tableCode == null || values == null)
			return;
		if (BXConstans.CONST_PAGE.equals(tableCode) || BXConstans.CONST_PAGE_JK.equals(tableCode)) {
			// 修改以支持web报销调用时传入values的值为空的SuperVO数组 modified by kongxl
			contrastVO = new BxcontrastVO[values.length];
			for (int i = 0; i < values.length; i++) {
				contrastVO[i] = (BxcontrastVO) values[i];
			}
		} else if (BXConstans.CSHARE_PAGE.equals(tableCode) || "costsharedetail".equals(tableCode)) {
			cShareDetailVo = new CShareDetailVO[values.length];
			for (int i = 0; i < values.length; i++) {
				cShareDetailVo[i] = (CShareDetailVO) values[i];
			}

		} else if (BXConstans.AccruedVerify_TABLECODE.equals(tableCode) || BXConstans.AccruedVerify_PAGE.equals(tableCode)) {
			accruedVerifyVO = new AccruedVerifyVO[values.length];
			for (int i = 0; i < values.length; i++) {
				accruedVerifyVO[i] = (AccruedVerifyVO) values[i];
			}
		}
		else {
			List<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();

			if (childrenVO != null && childrenVO.length != 0) {
				for (int i = 0; i < childrenVO.length; i++) {
					if (childrenVO[i].getTablecode() == null)
						continue;
					if (!childrenVO[i].getTablecode().equals(tableCode)) {
						list.add(childrenVO[i]);
					}
				}
			}
			for (int i = 0; i < values.length; i++) {
				if (values[i].getAttributeValue(BXBusItemVO.TABLECODE) == null) {
					values[i].setAttributeValue(BXBusItemVO.TABLECODE, tableCode);
				}
				list.add(values[i]);
			}

			childrenVO = list.toArray(new BXBusItemVO[] {});
		}
	}

	public void setParentVO(JKBXHeaderVO parentVO) {
		this.parentVO = parentVO;
	}

	public void setChildrenVO(BXBusItemVO[] childrenVO) {
		this.childrenVO = childrenVO;
	}

	public BxcontrastVO[] getContrastVO() {
		return contrastVO;
	}

	public void setContrastVO(BxcontrastVO[] contrastVO) {
		this.contrastVO = contrastVO;
	}

	public MatterAppVO getMaheadvo() {
		return maheadvo;
	}

	public void setMaheadvo(MatterAppVO maheadvo) {
		this.maheadvo = maheadvo;
	}

	/**
	 * 审批流程使用方法
	 * 
	 * IGetBusiDataForFlow IPfBackCheck2 IPfRetBackCheckInfo
	 */
	protected int checkState = 0;
	protected String checkNote = null;
	protected String checkMan = null;

	public UFDouble getPfAssMoney() {
		return null;
	}

	public String getPfCurrency() {
		return getParentVO().getBzbm();
	}

	public UFDouble getPfLocalMoney() {
		return getParentVO().getBbje();
	}

	public UFDouble getPfMoney() {
		return getParentVO().getYbje();
	}

	public void setCheckMan(String approveid) {
		checkMan = approveid;
	}

	public String getCheckMan() {
		return checkMan;
	}

	public void setCheckNote(java.lang.String strCheckNote) {
		this.checkNote = strCheckNote;
	}

	public String getCheckNote() {
		return checkNote;
	}

	public void setCheckState(int icheckState) {
		checkState = icheckState;
		getParentVO().setSpzt(icheckState);
	}

	public int getCheckState() {
		return checkState;
	}

	protected Object targetVO = null;
	protected SettlementAggVO settlevo = null; // 结算VO
	protected Map<String, String> cmpIdMap; // 结算ID对照
	protected boolean hasZjjhCheck; // 是否已经通过资金计划校验
	protected boolean hasNtbCheck; // 是否已经通过预算校验

	protected boolean hasJkCheck; // 是否已经通过借款校验
	protected boolean hasCrossCheck; // 是否已经通过交叉校验
	protected boolean hasProBudgetCheck; // 是否已经通过项目预算校验

	public List<String> authList;
	protected String warningMsg;
	protected boolean isContrastUpdate = false; // 是否更新冲借款信息
	protected boolean isVerifyAccruedUpdate = false; // 是否更新报销核销明细

	public boolean getHasProBudgetCheck() {
		return hasProBudgetCheck;
	}

	public void setHasProBudgetCheck(boolean hasProBudgetCheck) {
		this.hasProBudgetCheck = hasProBudgetCheck;
	}

	public boolean getHasCrossCheck() {
		return hasCrossCheck;
	}

	public void setHasCrossCheck(boolean hasCrossCheck) {
		this.hasCrossCheck = hasCrossCheck;
	}

	public boolean getHasJkCheck() {
		return hasJkCheck;
	}

	public void setHasJkCheck(boolean hasJkCheck) {
		this.hasJkCheck = hasJkCheck;
	}

	public boolean getHasNtbCheck() {
		return hasNtbCheck;
	}

	public void setHasNtbCheck(boolean hasNtbCheck) {
		this.hasNtbCheck = hasNtbCheck;
	}

	public boolean isContrastUpdate() {
		return isContrastUpdate;
	}

	public void setContrastUpdate(boolean isContrastUpdate) {
		this.isContrastUpdate = isContrastUpdate;
	}

	public List<String> getAuthList() {
		return authList;
	}

	public void setAuthList(List<String> authList) {
		this.authList = authList;
	}

	public boolean getHasZjjhCheck() {
		return hasZjjhCheck;
	}

	public void setHasZjjhCheck(boolean hasZjjhCheck) {
		this.hasZjjhCheck = hasZjjhCheck;
	}

	public String getWarningMsg() {
		return warningMsg;
	}

	public void setWarningMsg(String warningMsg) {
		this.warningMsg = warningMsg;
	}

	public SettlementAggVO getSettlementInfo() {
		return settlevo;
	}

	public void setSettlementInfo(SettlementAggVO settlementInfo) {
		this.settlevo = settlementInfo;
	}

	@SuppressWarnings("unchecked")
	public <T extends AggregatedValueObject> T getTargetVO() {
		return (T) targetVO;
	}

	@SuppressWarnings("unchecked")
	public <T extends AggregatedValueObject> T[] getTargetVOs() {
		return (T[]) targetVO;
	}

	public boolean isUseTargetVO() {
		return true;
	}

	public <T extends AggregatedValueObject> void setTargetVO(T vo) {
		targetVO = vo;
	}

	public <T extends AggregatedValueObject> void setTargetVOs(T[] vos) {
		targetVO = vos;
	}

	public Map<String, String> getCmpIdMap() {
		return cmpIdMap;
	}

	public void setCmpIdMap(Map<String, String> cmpIdMap) {
		this.cmpIdMap = cmpIdMap;
	}

	public JKBXVO getBxoldvo() {
		return bxoldvo;
	}

	public void setBxoldvo(JKBXVO bxoldvo) {
		this.bxoldvo = bxoldvo;
	}

	public SettlementAggVO getSettlevo() {
		return settlevo;
	}

	public void setSettlevo(SettlementAggVO settlevo) {
		this.settlevo = settlevo;
	}

	public void setTargetVO(Object targetVO) {
		this.targetVO = targetVO;
	}

	public CircularlyAccessibleValueObject[] getAllChildrenVO() {

		List<CircularlyAccessibleValueObject> allVOList = new ArrayList<CircularlyAccessibleValueObject>();

		String[] tableCodes = getTableCodes();

		if (tableCodes == null || tableCodes.length == 0) {
			throw new RuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011v61013_0",
					"02011v61013-0059")/* @res "实现IExAggVO必须返回页签编码" */);
		}
		for (int i = 0; i < tableCodes.length; i++) {
			CircularlyAccessibleValueObject[] tableVOs = getTableVO(tableCodes[i]);
			if (tableVOs == null || tableVOs.length == 0) {
				// 避免空指针异常
				continue;
			}
			List<CircularlyAccessibleValueObject> voList = Arrays.asList(tableVOs);
			allVOList.addAll(voList);
		}
		return allVOList.toArray(new CircularlyAccessibleValueObject[0]);
	}

	public SuperVO[] getChildVOsByParentId(String tableCode, String parentid) {
		return null;
	}

	public String getDefaultTableCode() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public HashMap getHmEditingVOs() throws Exception {
		return null;
	}

	public String getParentId(SuperVO item) {
		return null;
	}

	public void setParentId(SuperVO item, String id) {

	}

	public Map<String, List<SettlementBodyVO>> getSettlementMap() {
		return settlementMap;
	}

	public void setSettlementMap(Map<String, List<SettlementBodyVO>> settlementMap) {
		this.settlementMap = settlementMap;
	}

	public Map<String, JKBXHeaderVO> getJkdMap() {
		return jkdMap;
	}

	public void setJkdMap(Map<String, JKBXHeaderVO> jkdMap) {
		this.jkdMap = jkdMap;
	}

	public CShareDetailVO[] getcShareDetailVo() {
		return cShareDetailVo;
	}

	public void setcShareDetailVo(CShareDetailVO[] cShareDetailVo) {
		this.cShareDetailVo = cShareDetailVo;
	}

	public AggCostShareVO getCostOldVo() {
		return costOldVo;
	}

	public void setCostOldVo(AggCostShareVO costOldVo) {
		this.costOldVo = costOldVo;
	}

	public JKHeaderVO[] getJkHeadVOs() {
		return jkHeadVOs;
	}

	public void setJkHeadVOs(JKHeaderVO[] jkHeadVOs) {
		this.jkHeadVOs = jkHeadVOs;
	}

	public MtapppfVO[] getMaPfVos() {
		return maPfVos;
	}

	public void setMaPfVos(MtapppfVO[] maPfVos) {
		this.maPfVos = maPfVos;
	}

	public MtapppfVO[] getContrastMaPfVos() {
		return contrastMaPfVos;
	}

	public void setContrastMaPfVos(MtapppfVO[] contrastMaPfVos) {
		this.contrastMaPfVos = contrastMaPfVos;
	}

	public AccruedVerifyVO[] getAccruedVerifyVO() {
		return accruedVerifyVO;
	}

	public void setAccruedVerifyVO(AccruedVerifyVO[] accruedVerifyVO) {
		this.accruedVerifyVO = accruedVerifyVO;
	}

	public boolean isVerifyAccruedUpdate() {
		return isVerifyAccruedUpdate;
	}

	public void setVerifyAccruedUpdate(boolean isVerifyAccruedUpdate) {
		this.isVerifyAccruedUpdate = isVerifyAccruedUpdate;
	}
}