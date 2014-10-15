package nc.vo.erm.jkbx.ext;

import java.io.Serializable;

import nc.bs.erm.cache.ErmBillFieldContrastCache;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.pub.IFYControl;
import nc.vo.erm.matterapp.MatterAppVO;
import nc.vo.erm.matterapp.MtAppDetailVO;
import nc.vo.erm.matterapp.ext.MatterAppYsControlVOExt;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * 报销单拉单，超申请回写预算控制vo
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class BXMaFYControlVOExt implements IFYControl,Serializable {
	private static final long serialVersionUID = 1L;
	
	protected JKBXHeaderVO bxheadvo;
	protected MatterAppYsControlVOExt maysvo;
	
	public BXMaFYControlVOExt(JKBXHeaderVO bxheadvo,MatterAppVO parentvo,MtAppDetailVO detailvo) {
		this.bxheadvo = (JKBXHeaderVO) bxheadvo.clone();
		this.maysvo = new MatterAppYsControlVOExt(parentvo,detailvo);
	}

	@Override
	public boolean isYSControlAble() {
		return bxheadvo.isYSControlAble();
	}

	@Override
	public boolean isJKControlAble() {
		return bxheadvo.isJKControlAble();
	}

	@Override
	public String getPk_item() {
		return bxheadvo.getPk_item();
	}

	@Override
	public Object getItemValue(String attr) {
		boolean isbxfield = false;// 是否是报销字段
		String ysContrastBilltype = maysvo.getDjlxbm();//预算对照交易类型
		String parentBilltype = maysvo.getParentBillType();//预算对照单据类型
		if(BXMaYsConst.bxYsFields.contains(attr)){
			// 若控制维度是报销本身的字段，从报销单数据中获取字段值
			isbxfield = true;
			ysContrastBilltype = bxheadvo.getDjlxbm();
			parentBilltype = bxheadvo.getParentBillType();
		}
		String newAttr = ErmBillFieldContrastCache.getSrcField(
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, ysContrastBilltype, attr);
		
		if (newAttr == null) {
			// 对照字段不存在,该中情况在于在预算对照表中未查找到对应字段
			if (attr.startsWith(BXConstans.BUDGET_DEFITEM_BODY_PREFIX)) {
				if(BXConstans.BX_DJLXBM.equals(parentBilltype) || BXConstans.JK_DJLXBM.equals(parentBilltype)){
					newAttr = BXConstans.BODY_USERDEF_PREFIX
					+ attr.substring(BXConstans.BUDGET_DEFITEM_BODY_PREFIX.length());
				}else{
					newAttr = "fb." + BXConstans.BODY_USERDEF_PREFIX
					+ attr.substring(BXConstans.BUDGET_DEFITEM_BODY_PREFIX.length());
				}
			} else if (attr.startsWith(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX)) {
				if (BXConstans.BX_DJLXBM.equals(parentBilltype) || BXConstans.JK_DJLXBM.equals(parentBilltype)) {
					newAttr = BXConstans.HEAD_USERDEF_PREFIX
							+ attr.substring(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX.length());
				} else {
					newAttr = BXConstans.BODY_USERDEF_PREFIX
							+ attr.substring(BXConstans.BUDGET_DEFITEM_HEAD_PREFIX.length());
				}
			} else {
				newAttr = attr;
			}
		}
		Object value = null;
		// 获得控制维度的值
		if(isbxfield){
			value = bxheadvo.getItemValue(newAttr);
		}else{
			value =  maysvo.getItemValue(newAttr);
		}
		return value;
	}
	
	@Override
	public String getBzbm() {
		return bxheadvo.getBzbm();
	}

	@Override
	public UFDouble[] getItemHl() {
		return bxheadvo.getItemHl();
	}

	@Override
	public String getOperationUser() {
		return bxheadvo.getOperationUser();
	}

	@Override
	public UFDate getOperationDate() {
		return bxheadvo.getOperationDate();
	}

	@Override
	public String getFydwbm() {
		return maysvo.getFydwbm();
	}

	@Override
	public String getPk_group() {
		return maysvo.getPk_group();
	}

	@Override
	public String getDwbm() {
		return maysvo.getDwbm();
	}

	@Override
	public UFDate getDjrq() {
		return bxheadvo.getDjrq();
	}

	@Override
	public String getDjlxbm() {
		return bxheadvo.getDjlxbm();
	}

	@Override
	public String getPk() {
		return bxheadvo.getPk();
	}

	@Override
	public String getDjdl() {
		return bxheadvo.getDjdl();
	}

	@Override
	public String getDdlx() {
		return bxheadvo.getDdlx();
	}

	@Override
	public Integer getFx() {
		return 1;
	}

	@Override
	public boolean isSaveControl() {
		return false;
	}

	@Override
	public String getJsfs() {
		return null;
	}

	@Override
	public Integer getDjzt() {
		return bxheadvo.getDjzt();
	}

	@Override
	public String getJkbxr() {
		return bxheadvo.getJkbxr();
	}

	@Override
	public String getOperator() {
		return bxheadvo.getOperator();
	}

	@Override
	public String getPk_org() {
		// 预算控制主组织默认为申请单位
		return maysvo.getPk_org();
	}

	@Override
	public String getParentBillType() {
		return bxheadvo.getParentBillType();
	}

	@Override
	public String getPk_payorg() {
		return bxheadvo.getPk_payorg();
	}
	
	/**
	 * 预算执行金额
	 */
	private UFDouble[] ItemJe;
	
	@Override
	public UFDouble[] getItemJe() {
		return ItemJe;
	}
	
	/**
	 * 设置预算执行金额
	 * 
	 * 【全局本币金额、集团本币金额、组织本币金额、原币金额】
	 * 
	 * @param amount
	 */
	public void setItemJe(UFDouble[] amount){
		if(amount == null || amount.length != 4){
			return ;
		}
		ItemJe = amount;
	}
	
	/**
	 * 预算预占金额
	 */
	private UFDouble[] preItemJe;

	@Override
	public UFDouble[] getPreItemJe() {
		if(preItemJe == null){
			return getItemJe();
		}
		return preItemJe;
	}

	public void setPreItemJe(UFDouble[] preItemJe) {
		this.preItemJe = preItemJe;
	}

	@Override
	public String getWorkFlowBillPk() {
		return getPk();
	}

	@Override
	public String getWorkFolwBillType() {
		return getDjlxbm();
	}
}
