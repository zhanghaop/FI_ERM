package nc.vo.erm.accruedexpense;

import java.io.Serializable;

import nc.bs.erm.common.ErmBillConst;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.er.pub.IFYControl;
import nc.vo.jcom.lang.StringUtil;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

/**
 * Ԥ�ᵥԤ�����VO��ʵ��IFYControl�ӿ�
 * @author chenshuaia
 *
 */
public class AccruedBillYsControlVO implements IFYControl, Serializable {

	private static final long serialVersionUID = 1L;

	protected AccruedVO parentvo;
	protected AccruedDetailVO detailvo;

	public AccruedBillYsControlVO(AccruedVO parentvo, AccruedDetailVO detailvo) {
		this.parentvo = (AccruedVO) parentvo.clone();
		this.detailvo = (AccruedDetailVO) detailvo.clone();
	}

	@Override
	public boolean isYSControlAble() {
		// ���ݴ�״̬���ɽ���Ԥ�����
		if (parentvo.getBillstatus().equals(BXStatusConst.DJZT_TempSaved)) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isJKControlAble() {
		return false;
	}

	@Override
	public String getPk_item() {
		return null;
	}

	@Override
	public Object getItemValue(String key) {
		if (StringUtil.isEmptyWithTrim(key)) {
			return null;
		}
		if (BXConstans.EFFECTDATE.equals(key) || BXConstans.APPROVEDATE.equals(key)) {
			key = AccruedVO.APPROVETIME;
		}
		String[] tokens = StringUtil.split(key, ".");
		if (tokens.length == 1) {
			return parentvo.getAttributeValue(key);
		}
		if (ErmBillConst.AccruedBill_DJDL.equals(tokens[0])) {
			return parentvo.getAttributeValue(tokens[1]);
		} else {
			return detailvo.getAttributeValue(tokens[1]);
		}
	}

	@Override
	public UFDouble[] getItemJe() {
		return new UFDouble[] { detailvo.getGlobal_amount(), detailvo.getGroup_amount(), detailvo.getOrg_amount(),
				detailvo.getAmount() };
	}

	@Override
	public String getBzbm() {
		return parentvo.getPk_currtype();
	}

	@Override
	public UFDouble[] getItemHl() {
		return new UFDouble[] { detailvo.getGlobal_currinfo(), detailvo.getGroup_currinfo(), detailvo.getOrg_currinfo() };
	}

	@Override
	public String getOperationUser() {
		Integer billstatus = parentvo.getBillstatus();
		if (billstatus == null) {
			return null;
		} else if (billstatus.intValue() == BXStatusConst.DJZT_Saved) {
			return parentvo.getCreator();
		} else if (billstatus.intValue() == BXStatusConst.DJZT_Verified
				|| billstatus.intValue() == BXStatusConst.DJZT_Sign) {
			return parentvo.getApprover();
		}
		return null;
	}

	@Override
	public UFDate getOperationDate() {
		Integer billstatus = parentvo.getBillstatus();
		if (billstatus == null) {
			return null;
		} else if (billstatus.intValue() == BXStatusConst.DJZT_Saved) {
			return parentvo.getBilldate();
		} else if (billstatus.intValue() == BXStatusConst.DJZT_Verified
				|| billstatus.intValue() == BXStatusConst.DJZT_Sign) {
			return parentvo.getApprovetime().getDate();
		}
		return null;
	}

	@Override
	public String getFydwbm() {
		// ���ݷ�̯����޸ģ����ر�����óе���λ
		return detailvo.getAssume_org();
	}

	@Override
	public String getPk_group() {
		return parentvo.getPk_group();
	}

	@Override
	public String getDwbm() {
		return parentvo.getPk_org();
	}

	@Override
	public UFDate getDjrq() {
		return parentvo.getBilldate();
	}

	@Override
	public String getDjlxbm() {
		return parentvo.getPk_tradetype();
	}

	@Override
	public String getPk() {
		return parentvo.getPk_accrued_bill();
	}

	@Override
	public String getDjdl() {
		return ErmBillConst.AccruedBill_DJDL;
	}

	@Override
	public String getDdlx() {
		return parentvo.getPk_billtype();
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
		return parentvo.getBillstatus();
	}

	@Override
	public String getJkbxr() {
		return parentvo.getOperator();
	}

	@Override
	public String getOperator() {
		return parentvo.getOperator();
	}

	@Override
	public String getPk_org() {
		return parentvo.getPk_org();
	}

	@Override
	public String getParentBillType() {
		return parentvo.getPk_billtype();
	}

	@Override
	public String getPk_payorg() {
		return null;
	}

	/**
	 * Ԥ��Ԥռ���
	 */
	private UFDouble[] preItemJe;

	@Override
	public UFDouble[] getPreItemJe() {
		if (preItemJe == null) {
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
