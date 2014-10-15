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
 * �������������������дԤ�����vo
 * 
 * ����Ԫר��
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
		boolean isbxfield = false;// �Ƿ��Ǳ����ֶ�
		String ysContrastBilltype = maysvo.getDjlxbm();//Ԥ����ս�������
		String parentBilltype = maysvo.getParentBillType();//Ԥ����յ�������
		if(BXMaYsConst.bxYsFields.contains(attr)){
			// ������ά���Ǳ���������ֶΣ��ӱ����������л�ȡ�ֶ�ֵ
			isbxfield = true;
			ysContrastBilltype = bxheadvo.getDjlxbm();
			parentBilltype = bxheadvo.getParentBillType();
		}
		String newAttr = ErmBillFieldContrastCache.getSrcField(
				ErmBillFieldContrastCache.FieldContrast_SCENE_BudGetField, ysContrastBilltype, attr);
		
		if (newAttr == null) {
			// �����ֶβ�����,�������������Ԥ����ձ���δ���ҵ���Ӧ�ֶ�
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
		// ��ÿ���ά�ȵ�ֵ
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
		// Ԥ���������֯Ĭ��Ϊ���뵥λ
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
	 * Ԥ��ִ�н��
	 */
	private UFDouble[] ItemJe;
	
	@Override
	public UFDouble[] getItemJe() {
		return ItemJe;
	}
	
	/**
	 * ����Ԥ��ִ�н��
	 * 
	 * ��ȫ�ֱ��ҽ����ű��ҽ���֯���ҽ�ԭ�ҽ�
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
	 * Ԥ��Ԥռ���
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
