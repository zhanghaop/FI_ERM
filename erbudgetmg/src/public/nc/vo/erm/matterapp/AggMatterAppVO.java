package nc.vo.erm.matterapp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import nc.bs.erm.matterapp.common.ErmMatterAppConst;
import nc.vo.ml.NCLangRes4VoTransl;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ISuperVO;
import nc.vo.pub.IVOMeta;
import nc.vo.pub.SuperVO;
import nc.vo.pubapp.pattern.model.entity.bill.IBill;
import nc.vo.pubapp.pattern.model.meta.entity.bill.BillMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.bill.IBillMeta;
import nc.vo.trade.pub.HYBillVO;
import nc.vo.trade.pub.IExAggVO;

/**
 * 
 * ���ӱ�/����ͷ/������ۺ�VO
 * 
 * ��������:
 * 
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.erm.matterapp.MatterAppVO")
public class AggMatterAppVO extends HYBillVO implements Cloneable, IExAggVO , IBill{
	
	private AggMatterAppVO oldvo;
	
	private static String[] headYbAmounts;

	private static String[] headOrgAmounts;

	private static String[] headGroupAmounts;

	private static String[] headGlobalAmounts;

	private static String[] bodyYbAmounts;

	private static String[] bodyOrgAmounts;

	private static String[] bodyGroupAmounts;

	private static String[] bodyGlobalAmounts;

	private static String[] headAmounts;

	private static String[] bodyAmounts;

	private static List<String> applyOrgHeadIterms;

	private static List<String> applyOrgBodyIterms;
	
	private static List<String> bodyAssumeOrgBodyIterms;
	
	private static List<String> notRepeatFields;
	
	public static String[] excelInputHeadItems = new String[] { MatterAppVO.PK_TRADETYPE, MatterAppVO.BILLDATE,
			MatterAppVO.PK_ORG, MatterAppVO.PK_CURRTYPE, MatterAppVO.ORIG_AMOUNT, MatterAppVO.APPLY_ORG,
			MatterAppVO.APPLY_ORG, MatterAppVO.APPLY_DEPT, MatterAppVO.BILLNO };

	public static String[] excelInputBodyItems = new String[] { MtAppDetailVO.ASSUME_ORG,
			MtAppDetailVO.ASSUME_DEPT, MtAppDetailVO.ORIG_AMOUNT };

	static {

		headAmounts = new String[] { MatterAppVO.ORIG_AMOUNT, MatterAppVO.REST_AMOUNT, MatterAppVO.EXE_AMOUNT,
				MatterAppVO.ORG_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT, MatterAppVO.ORG_REST_AMOUNT,
				MatterAppVO.GROUP_AMOUNT, MatterAppVO.GROUP_REST_AMOUNT, MatterAppVO.GROUP_EXE_AMOUNT,
				MatterAppVO.GLOBAL_AMOUNT, MatterAppVO.GLOBAL_REST_AMOUNT, MatterAppVO.GLOBAL_EXE_AMOUNT };

		bodyAmounts = new String[] { MtAppDetailVO.ORIG_AMOUNT, MtAppDetailVO.REST_AMOUNT, MtAppDetailVO.EXE_AMOUNT,
				MtAppDetailVO.ORG_AMOUNT, MtAppDetailVO.ORG_REST_AMOUNT, MtAppDetailVO.ORG_EXE_AMOUNT,
				MtAppDetailVO.GROUP_AMOUNT, MtAppDetailVO.GROUP_EXE_AMOUNT, MtAppDetailVO.GROUP_REST_AMOUNT,
				MtAppDetailVO.GLOBAL_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT, MtAppDetailVO.GLOBAL_REST_AMOUNT ,MtAppDetailVO.USABLE_AMOUT };

		headYbAmounts = new String[] { MatterAppVO.ORIG_AMOUNT, MatterAppVO.REST_AMOUNT, MatterAppVO.EXE_AMOUNT,
				MatterAppVO.PRE_AMOUNT,MatterAppVO.MAX_AMOUNT };

		headOrgAmounts = new String[] { MatterAppVO.ORG_AMOUNT, MatterAppVO.ORG_EXE_AMOUNT,
				MatterAppVO.ORG_REST_AMOUNT, MatterAppVO.ORG_PRE_AMOUNT };

		headGroupAmounts = new String[] { MatterAppVO.GROUP_AMOUNT, MatterAppVO.GROUP_REST_AMOUNT,
				MatterAppVO.GROUP_EXE_AMOUNT, MatterAppVO.GROUP_PRE_AMOUNT };

		headGlobalAmounts = new String[] { MatterAppVO.GLOBAL_AMOUNT, MatterAppVO.GLOBAL_REST_AMOUNT,
				MatterAppVO.GLOBAL_EXE_AMOUNT, MatterAppVO.GLOBAL_PRE_AMOUNT };

		bodyYbAmounts = new String[] { MtAppDetailVO.ORIG_AMOUNT, MtAppDetailVO.REST_AMOUNT, MtAppDetailVO.EXE_AMOUNT,
				MtAppDetailVO.PRE_AMOUNT, MtAppDetailVO.USABLE_AMOUT , MtAppDetailVO.MAX_AMOUNT,
				MtAppDetailVO.APPLY_AMOUNT};

		bodyOrgAmounts = new String[] { MtAppDetailVO.ORG_AMOUNT, MtAppDetailVO.ORG_REST_AMOUNT,
				MtAppDetailVO.ORG_EXE_AMOUNT, MtAppDetailVO.ORG_PRE_AMOUNT };

		bodyGroupAmounts = new String[] { MtAppDetailVO.GROUP_AMOUNT, MtAppDetailVO.GROUP_EXE_AMOUNT,
				MtAppDetailVO.GROUP_REST_AMOUNT, MtAppDetailVO.GROUP_PRE_AMOUNT };

		bodyGlobalAmounts = new String[] { MtAppDetailVO.GLOBAL_AMOUNT, MtAppDetailVO.GLOBAL_EXE_AMOUNT,
				MtAppDetailVO.GLOBAL_REST_AMOUNT, MtAppDetailVO.GLOBAL_PRE_AMOUNT };

		applyOrgHeadIterms = new ArrayList<String>();
//		applyOrgHeadIterms.add(MatterAppVO.APPLY_DEPT);
//		applyOrgHeadIterms.add(MatterAppVO.BILLMAKER);
		applyOrgHeadIterms.add(MatterAppVO.REASON);
		applyOrgHeadIterms.add(MatterAppVO.PK_CUSTOMER);
		applyOrgHeadIterms.add(MatterAppVO.ASSUME_DEPT);

		applyOrgBodyIterms = new ArrayList<String>();
		applyOrgBodyIterms.add(MtAppDetailVO.REASON);
		
		bodyAssumeOrgBodyIterms = new ArrayList<String>();
		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.ASSUME_DEPT);

		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.PK_SALESMAN);
		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.PK_IOBSCLASS);
		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.PK_PROJECT);
		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.PK_WBS);
		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.PK_CUSTOMER);
		bodyAssumeOrgBodyIterms.add(MtAppDetailVO.PK_SUPPLIER);
		
		
		notRepeatFields = new ArrayList<String>();
		notRepeatFields.add(MtAppDetailVO.PK_PCORG);
		notRepeatFields.add(MtAppDetailVO.PK_RESACOSTCENTER);
		notRepeatFields.add(MtAppDetailVO.PK_IOBSCLASS);
		notRepeatFields.add(MtAppDetailVO.PK_PROJECT);
		notRepeatFields.add(MtAppDetailVO.PK_WBS);

		notRepeatFields.add(MtAppDetailVO.PK_CUSTOMER);
		notRepeatFields.add(MtAppDetailVO.PK_CHECKELE);
		notRepeatFields.add(MtAppDetailVO.PK_SUPPLIER);
		notRepeatFields.add(MtAppDetailVO.PK_SALESMAN);
		
		notRepeatFields.add(MtAppDetailVO.ASSUME_ORG);
		notRepeatFields.add(MtAppDetailVO.ASSUME_DEPT);
		
		notRepeatFields.add(MtAppDetailVO.PK_PROLINE);
		notRepeatFields.add(MtAppDetailVO.PK_BRAND);
	}
	
	/**
	 * ��ʼ������ҳǩ�е��ֶ�Ϊ��ѡ
	 * @return
	 */
	public static String[] getBodyMultiSelectedItems(){
		return new String[]{MtAppDetailVO.ASSUME_ORG,MtAppDetailVO.ASSUME_DEPT,
				MtAppDetailVO.PK_IOBSCLASS,MtAppDetailVO.PK_PCORG,MtAppDetailVO.PK_RESACOSTCENTER,
				MtAppDetailVO.PK_CHECKELE,MtAppDetailVO.PK_PROJECT,MtAppDetailVO.PK_CUSTOMER,
				MtAppDetailVO.PK_SUPPLIER,MtAppDetailVO.PK_WBS,MtAppDetailVO.PK_SALESMAN,
				MtAppDetailVO.PK_BRAND,MtAppDetailVO.PK_PROLINE};
	}
	

	@Override
	public Object clone() {
		AggMatterAppVO aggVo = new AggMatterAppVO();

		if (getParentVO() != null) {
			aggVo.setParentVO((MatterAppVO) getParentVO().clone());
		}

		String[] allTabcodes = getAllTabcodes();
		for(int i = 0; i < allTabcodes.length; i++){
			CircularlyAccessibleValueObject[] cvos
			        = getTableVO(allTabcodes[i]);
			if(cvos != null){
				CircularlyAccessibleValueObject[] clonevos = new CircularlyAccessibleValueObject[cvos.length];
				for (int j = 0; j < cvos.length; j++) {
					if (cvos[j] != null) {
						clonevos[j] = (CircularlyAccessibleValueObject) cvos[j].clone();
					}
				}
				aggVo.setTableVO(allTabcodes[i], clonevos);
			}
		}

		return aggVo;
	}

	
	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		setTableVO(getTableCodes()[0], children);
	}
	
	
	/**
	 * ����ParentVo,����ǿ��ת��
	 */
	public MatterAppVO getParentVO() {
		return (MatterAppVO) super.getParentVO();
	}

	public MtAppDetailVO[] getChildrenVO() {
		CircularlyAccessibleValueObject[] tableVO = getTableVO(getTableCodes()[0]);
		if(tableVO == null || tableVO.length == 0){
			return null;
		}
		MtAppDetailVO[] childs = new MtAppDetailVO[tableVO.length];
		System.arraycopy(tableVO, 0, childs, 0, tableVO.length);
		return childs;
	}

	public static String[] getHeadYbAmounts() {
		return headYbAmounts;
	}

	public static String[] getHeadGroupAmounts() {
		return headGroupAmounts;
	}

	public static String[] getHeadGlobalAmounts() {
		return headGlobalAmounts;
	}

	public static String[] getHeadOrgAmounts() {
		return headOrgAmounts;
	}

	public static String[] getBodyYbAmounts() {
		return bodyYbAmounts;
	}

	public static String[] getBodyOrgAmounts() {
		return bodyOrgAmounts;
	}

	public static String[] getBodyGroupAmounts() {
		return bodyGroupAmounts;
	}

	public static String[] getBodyGlobalAmounts() {
		return bodyGlobalAmounts;
	}

	public static String[] getHeadAmounts() {
		return headAmounts;
	}

	public static String[] getBodyAmounts() {
		return bodyAmounts;
	}

	public static List<String> getApplyOrgHeadIterms() {
		return applyOrgHeadIterms;
	}

	public static List<String> getApplyOrgBodyIterms() {
		return applyOrgBodyIterms;
	}
	
	public static List<String> getNotRepeatFields() {
		return notRepeatFields;
	}
	
	//����װ�ض��ӱ����ݵ�HashMap
	@SuppressWarnings("rawtypes")
	private HashMap hmChildVOs = new HashMap();
	
	
	/**
	 * ���ض���ӱ�ı���
	 * �����뵥��ģ���ҳǩ�����Ӧ
	 * �������ڣ�
	 * @return String[]
	 */
	public String[] getTableCodes(){
		          
		return new String[]{
		 		 		   		"mtapp_detail"
		   		    };
		          
	}
	
	
	/**
	 * ���ض���ӱ����������
	 * �������ڣ�
	 * @return String[]
	 */
	public String[] getTableNames(){
		
		return new String[] { NCLangRes4VoTransl.getNCLangRes().getStrByID("201212_0","0201212-0094")/*@res "�������뵥��ϸ"*/ };
	}
	
	/**
	 * ��ȡȫ���ӱ�ҳǩ��Ϣ��������̬��չ���ӱ�
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getAllTabcodes(){
		return (String[]) hmChildVOs.keySet().toArray(new String[hmChildVOs.keySet().size()]);
	}
	
	/**
	 * ��ȡȫ����̬��չ��ҳǩ
	 * 
	 * @return
	 */
	public String[] getAllExtendTabcodes(){
		String[] alltabcodes = getAllTabcodes();
		List<String> list = new ArrayList<String>();
		list.addAll(Arrays.asList(alltabcodes));
		
		String[] tableCodes = getTableCodes();
		for (int i = 0; i < tableCodes.length; i++) {
			list.remove(tableCodes[i]);
		}
		return (String[]) list.toArray(new String[list.size()]);
	}
	
	/**
	 * ȡ�������ӱ������VO���󣬰�����̬��չ���ӱ�
	 * �������ڣ�
	 * @return CircularlyAccessibleValueObject[]
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CircularlyAccessibleValueObject[] getAllChildrenVO(){
		
		ArrayList al = new ArrayList();
		String[] allTabcodes = getAllTabcodes();
		for(int i = 0; i < allTabcodes.length; i++){
			CircularlyAccessibleValueObject[] cvos
			        = getTableVO(getTableCodes()[i]);
			if(cvos != null)
				al.addAll(Arrays.asList(cvos));
		}
		
		return (SuperVO[]) al.toArray(new SuperVO[0]);
	}
	
	
	/**
	 * ����ÿ���ӱ��VO����
	 * �������ڣ�
	 * @return CircularlyAccessibleValueObject[]
	 */
	public CircularlyAccessibleValueObject[] getTableVO(String tableCode){
		
		return (CircularlyAccessibleValueObject[])
		            hmChildVOs.get(tableCode);
	}
	
	
	/**
	 * 
	 * �������ڣ�
	 * @param SuperVO item
	 * @param String id
	 */
	public void setParentId(SuperVO item,String id){}
	
	/**
	 * Ϊ�ض��ӱ�����VO����
	 * �������ڣ�
	 * @param String tableCode
	 * @para CircularlyAccessibleValueObject[] vos
	 */
	@SuppressWarnings("unchecked")
	public void setTableVO(String tableCode,CircularlyAccessibleValueObject[] vos){
		
		hmChildVOs.put(tableCode,vos);
	}
	
	/**
	 * ȱʡ��ҳǩ����
	 * �������ڣ�
	 * @return String 
	 */
	public String getDefaultTableCode(){
		
		return getTableCodes()[0];
	}
	
	/**
	 * 
	 * �������ڣ�
	 * @param String tableCode
	 * @param String parentId
	 * @return SuperVO[]
	 */
	public SuperVO[] getChildVOsByParentId(String tableCode,String parentId){
		
		return null;
	}
	
	
	/**
	 * 
	 * �������ڣ�
	 * @return HashMap
	 */
	@SuppressWarnings("rawtypes")
	public HashMap getHmEditingVOs() throws Exception{
		
		return null;
	}
	
	/**
	 * 
	 * ��������:
	 * @param SuperVO item
	 * @return String
	 */
	public String getParentId(SuperVO item){
		
		return null;
	}


	@Override
	public ISuperVO[] getChildren(Class<? extends ISuperVO> clazz) {
		return getChildrenVO();
	}

	@Override
	public ISuperVO[] getChildren(IVOMeta childMeta) {
		return getChildrenVO();
	}

	@Override
	public ISuperVO getParent() {
		return getParentVO();
	}

	@Override
	public String getPrimaryKey() {
		return getParentVO().getPrimaryKey();
	}

	@Override
	public void setChildren(Class<? extends ISuperVO> clazz, ISuperVO[] vos) {
	}

	@Override
	public void setChildren(IVOMeta childMeta, ISuperVO[] items) {

	}

	@Override
	public void setParent(ISuperVO parent) {
		// this.parent = getParentVO();
	}

	@Override
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta("erm.mtapp_bill");
		return billMeta;
	}
	
	public static List<String> getBodyAssumeOrgBodyIterms() {
		return bodyAssumeOrgBodyIterms;
	}


	public static void setBodyAssumeOrgBodyIterms(List<String> bodyAssumeOrgBodyIterms) {
		AggMatterAppVO.bodyAssumeOrgBodyIterms = bodyAssumeOrgBodyIterms;
	}


	/**
	 * ����ƾ֤��keyֵ
	 * 
	 * ����+��������+����(�����ǰ���׼ʱ�����)
	 * 
	 * @return
	 */
	public String getVoucherKey(){
		MatterAppVO vo = getParentVO();
		MtAppDetailVO[] detailvos = getChildrenVO();
		String pk_pcorg = null;
		if(detailvos != null && detailvos.length >0){
			pk_pcorg = detailvos[0].getPk_pcorg();
		}
		if(vo.getClose_status() == ErmMatterAppConst.CLOSESTATUS_N){
			return  vo.getPk_mtapp_bill() + pk_pcorg+vo.getBilldate().toStdString();
		}else{
			// �رյ��ݲ��ؼ�������Ϊkey
			return  vo.getPk_mtapp_bill() + pk_pcorg;
		}
	}


	public AggMatterAppVO getOldvo() {
		return oldvo;
	}


	public void setOldvo(AggMatterAppVO oldvo) {
		this.oldvo = oldvo;
	}

}
