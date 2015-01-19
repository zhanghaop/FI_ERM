package nc.vo.erm.accruedexpense;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
 * 单子表/单表头/单表体聚合VO
 *
 * 创建日期:
 *
 * @author
 * @version NCPrj ??
 */
@SuppressWarnings("serial")
@nc.vo.annotation.AggVoInfo(parentVO = "nc.vo.erm.accruedexpense.AccruedVO")
public class AggAccruedBillVO extends HYBillVO implements Cloneable, IBill, IExAggVO {

	// 用于装载多子表数据的HashMap
	private HashMap hmChildVOs = new HashMap();

	private AggAccruedBillVO oldvo;

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

	private static List<String> bodyAssumeOrgBodyIterms;

	private static List<String> notRepeatFields;

	private static String[] parentNotCopyFields;
	private static String[] bodyNotCopyFields;

	private static String[] redbackClearParentFields;
	private static String[] redbackClearBodyFields;

	static {

		headAmounts = new String[] { AccruedVO.AMOUNT, AccruedVO.ORG_AMOUNT, AccruedVO.GROUP_AMOUNT,
				AccruedVO.GLOBAL_AMOUNT, AccruedVO.VERIFY_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT,
				AccruedVO.GROUP_VERIFY_AMOUNT, AccruedVO.GLOBAL_VERIFY_AMOUNT, AccruedVO.REST_AMOUNT,
				AccruedVO.ORG_REST_AMOUNT, AccruedVO.GROUP_REST_AMOUNT, AccruedVO.GLOBAL_REST_AMOUNT,
				AccruedVO.PREDICT_REST_AMOUNT,AccruedVO.RED_AMOUNT };

		bodyAmounts = new String[] { AccruedDetailVO.AMOUNT, AccruedDetailVO.ORG_AMOUNT, AccruedDetailVO.GROUP_AMOUNT,
				AccruedDetailVO.GLOBAL_AMOUNT, AccruedDetailVO.VERIFY_AMOUNT, AccruedDetailVO.ORG_VERIFY_AMOUNT,
				AccruedDetailVO.GROUP_VERIFY_AMOUNT, AccruedDetailVO.GLOBAL_VERIFY_AMOUNT, AccruedDetailVO.REST_AMOUNT,
				AccruedDetailVO.ORG_REST_AMOUNT, AccruedDetailVO.GROUP_REST_AMOUNT, AccruedDetailVO.GLOBAL_REST_AMOUNT,
				AccruedDetailVO.PREDICT_REST_AMOUNT,AccruedDetailVO.RED_AMOUNT};

		headYbAmounts = new String[] { AccruedVO.AMOUNT, AccruedVO.VERIFY_AMOUNT, AccruedVO.REST_AMOUNT,
				AccruedVO.PREDICT_REST_AMOUNT,AccruedVO.RED_AMOUNT };

		headOrgAmounts = new String[] { AccruedVO.ORG_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT, AccruedVO.ORG_REST_AMOUNT };

		headGroupAmounts = new String[] { AccruedVO.GROUP_AMOUNT, AccruedVO.GROUP_VERIFY_AMOUNT,
				AccruedVO.GROUP_REST_AMOUNT };

		headGlobalAmounts = new String[] { AccruedVO.GLOBAL_AMOUNT, AccruedVO.GLOBAL_VERIFY_AMOUNT,
				AccruedVO.GLOBAL_REST_AMOUNT };

		bodyYbAmounts = new String[] { AccruedDetailVO.AMOUNT, AccruedDetailVO.VERIFY_AMOUNT,
				AccruedDetailVO.REST_AMOUNT, AccruedDetailVO.PREDICT_REST_AMOUNT,AccruedDetailVO.RED_AMOUNT };

		bodyOrgAmounts = new String[] { AccruedDetailVO.ORG_AMOUNT, AccruedDetailVO.ORG_VERIFY_AMOUNT,
				AccruedDetailVO.ORG_REST_AMOUNT};

		bodyGroupAmounts = new String[] { AccruedDetailVO.GROUP_AMOUNT, AccruedDetailVO.GROUP_VERIFY_AMOUNT,
				AccruedDetailVO.GROUP_REST_AMOUNT };

		bodyGlobalAmounts = new String[] { AccruedDetailVO.GLOBAL_AMOUNT, AccruedDetailVO.GLOBAL_VERIFY_AMOUNT,
				AccruedDetailVO.GLOBAL_REST_AMOUNT };

		bodyAssumeOrgBodyIterms = new ArrayList<String>();
		bodyAssumeOrgBodyIterms.add(AccruedDetailVO.ASSUME_DEPT);
		bodyAssumeOrgBodyIterms.add(AccruedDetailVO.PK_IOBSCLASS);
		bodyAssumeOrgBodyIterms.add(AccruedDetailVO.PK_PROJECT);
		bodyAssumeOrgBodyIterms.add(AccruedDetailVO.PK_WBS);
		bodyAssumeOrgBodyIterms.add(AccruedDetailVO.PK_CUSTOMER);
		bodyAssumeOrgBodyIterms.add(AccruedDetailVO.PK_SUPPLIER);

		notRepeatFields = new ArrayList<String>();
		notRepeatFields.add(AccruedDetailVO.PK_PCORG);
		notRepeatFields.add(AccruedDetailVO.PK_RESACOSTCENTER);
		notRepeatFields.add(AccruedDetailVO.PK_IOBSCLASS);
		notRepeatFields.add(AccruedDetailVO.PK_PROJECT);
		notRepeatFields.add(AccruedDetailVO.PK_WBS);
		notRepeatFields.add(AccruedDetailVO.PK_CUSTOMER);
		notRepeatFields.add(AccruedDetailVO.PK_CHECKELE);
		notRepeatFields.add(AccruedDetailVO.PK_SUPPLIER);
		notRepeatFields.add(AccruedDetailVO.ASSUME_ORG);
		notRepeatFields.add(AccruedDetailVO.ASSUME_DEPT);
		notRepeatFields.add(AccruedDetailVO.PK_PROLINE);
		notRepeatFields.add(AccruedDetailVO.PK_BRAND);

		parentNotCopyFields = new String[] { AccruedVO.APPROVER, AccruedVO.APPROVETIME, AccruedVO.PK_ACCRUED_BILL,
				AccruedVO.BILLNO, AccruedVO.BILLSTATUS, AccruedVO.EFFECTSTATUS, AccruedVO.APPRSTATUS,
				AccruedVO.MODIFIER, AccruedVO.MODIFIEDTIME, AccruedVO.PRINTER, AccruedVO.PRINTDATE, AccruedVO.CREATOR,
				AccruedVO.CREATIONTIME, AccruedVO.VERIFY_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT,
				AccruedVO.GROUP_VERIFY_AMOUNT, AccruedVO.GLOBAL_VERIFY_AMOUNT, AccruedVO.REST_AMOUNT,
				AccruedVO.ORG_REST_AMOUNT, AccruedVO.GROUP_REST_AMOUNT, AccruedVO.GLOBAL_REST_AMOUNT,
				AccruedVO.PREDICT_REST_AMOUNT, AccruedVO.REDFLAG,AccruedVO.RED_AMOUNT, "ts" };
		bodyNotCopyFields = new String[] { AccruedDetailVO.PK_ACCRUED_DETAIL, AccruedDetailVO.PK_ACCRUED_BILL,
				AccruedDetailVO.VERIFY_AMOUNT, AccruedDetailVO.ORG_VERIFY_AMOUNT, AccruedDetailVO.GROUP_VERIFY_AMOUNT,
				AccruedDetailVO.GLOBAL_VERIFY_AMOUNT, AccruedDetailVO.REST_AMOUNT, AccruedDetailVO.ORG_REST_AMOUNT,
				AccruedDetailVO.GROUP_REST_AMOUNT, AccruedDetailVO.GLOBAL_REST_AMOUNT,
				AccruedDetailVO.PREDICT_REST_AMOUNT, AccruedDetailVO.ROWNO, AccruedDetailVO.SRC_ACCRUEDPK,
				AccruedDetailVO.SRCTYPE,AccruedDetailVO.SRC_DETAILPK,AccruedDetailVO.RED_AMOUNT, "ts" };

		redbackClearParentFields = new String[] { AccruedVO.APPROVER, AccruedVO.APPROVETIME, AccruedVO.PK_ACCRUED_BILL,
				AccruedVO.BILLNO, AccruedVO.BILLSTATUS, AccruedVO.EFFECTSTATUS, AccruedVO.APPRSTATUS,
				AccruedVO.MODIFIER, AccruedVO.MODIFIEDTIME, AccruedVO.PRINTER, AccruedVO.PRINTDATE, AccruedVO.CREATOR,
				AccruedVO.CREATIONTIME, AccruedVO.VERIFY_AMOUNT, AccruedVO.ORG_VERIFY_AMOUNT,
				AccruedVO.GROUP_VERIFY_AMOUNT, AccruedVO.GLOBAL_VERIFY_AMOUNT, AccruedVO.PREDICT_REST_AMOUNT,
				AccruedVO.REDFLAG,AccruedVO.RED_AMOUNT, "ts" };
		redbackClearBodyFields = new String[] { AccruedDetailVO.PK_ACCRUED_DETAIL, AccruedDetailVO.PK_ACCRUED_BILL,
				AccruedDetailVO.VERIFY_AMOUNT, AccruedDetailVO.ORG_VERIFY_AMOUNT, AccruedDetailVO.GROUP_VERIFY_AMOUNT,
				AccruedDetailVO.GLOBAL_VERIFY_AMOUNT, AccruedDetailVO.PREDICT_REST_AMOUNT, AccruedDetailVO.ROWNO,
				AccruedDetailVO.SRC_ACCRUEDPK, AccruedDetailVO.SRCTYPE,AccruedDetailVO.SRC_DETAILPK,AccruedDetailVO.RED_AMOUNT, "ts" };

	}

	/**
	 * 初始化表体页签中的字段为多选 费用承担单位， 费用承担部门，收支项目，利润中心，成本中心，核算要素，项目，项目任务，客户，供应商，品牌，产品线
	 *
	 * @return
	 */
	public static String[] getBodyMultiSelectedItems() {
		return new String[] { AccruedDetailVO.ASSUME_ORG, AccruedDetailVO.ASSUME_DEPT, AccruedDetailVO.PK_IOBSCLASS,
				AccruedDetailVO.PK_PCORG, AccruedDetailVO.PK_RESACOSTCENTER, AccruedDetailVO.PK_CHECKELE,
				AccruedDetailVO.PK_PROJECT, AccruedDetailVO.PK_WBS, AccruedDetailVO.PK_CUSTOMER,
				AccruedDetailVO.PK_SUPPLIER, AccruedDetailVO.PK_BRAND, AccruedDetailVO.PK_PROLINE };
	}

	/**
	 * 返回ParentVo,避免强制转型
	 */
	public AccruedVO getParentVO() {
		return (AccruedVO) super.getParentVO();
	}

	public AccruedDetailVO[] getChildrenVO() {
		CircularlyAccessibleValueObject[] tableVO = getTableVO(getTableCodes()[0]);
		if (tableVO == null || tableVO.length == 0) {
			return null;
		}
		AccruedDetailVO[] childs = new AccruedDetailVO[tableVO.length];
		System.arraycopy(tableVO, 0, childs, 0, tableVO.length);
		return childs;
	}

	/**
	 * 返回核销明细子表vo数组
	 * @return
	 */
	public AccruedVerifyVO[] getAccruedVerifyVO() {
		CircularlyAccessibleValueObject[] tableVO = getTableVO(getTableCodes()[1]);
		if (tableVO == null || tableVO.length == 0) {
			return null;
		}
		AccruedVerifyVO[] childs = new AccruedVerifyVO[tableVO.length];
		System.arraycopy(tableVO, 0, childs, 0, tableVO.length);
		return childs;
	}

	public void setAccruedVerifyVO(AccruedVerifyVO[] accruedVerifyVO) {
		setTableVO(getTableCodes()[1], accruedVerifyVO);
	}

	/**
	 * 返回每个子表的VO数组 创建日期：
	 *
	 * @return CircularlyAccessibleValueObject[]
	 */
	public CircularlyAccessibleValueObject[] getTableVO(String tableCode) {

		return (CircularlyAccessibleValueObject[]) hmChildVOs.get(tableCode);
	}

	/**
	 * 返回多个子表的编码 必须与单据模版的页签编码对应 创建日期：
	 *
	 * @return String[]
	 */
	public String[] getTableCodes() {

		return new String[] { "accrued_detail", "accrued_verify" };

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
	public IBillMeta getMetaData() {
		IBillMeta billMeta = BillMetaFactory.getInstance().getBillMeta("erm.accrued");
		return billMeta;
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
	public void setChildren(Class<? extends ISuperVO> clazz, ISuperVO[] children) {

	}

	@Override
	public void setChildrenVO(CircularlyAccessibleValueObject[] children) {
		setTableVO(getTableCodes()[0], children);
	}

	@Override
	public void setChildren(IVOMeta childMeta, ISuperVO[] children) {

	}

	@Override
	public void setParent(ISuperVO parent) {

	}

	@Override
	public CircularlyAccessibleValueObject[] getAllChildrenVO() {

		ArrayList<CircularlyAccessibleValueObject> al = new ArrayList<CircularlyAccessibleValueObject>();
		String[] allTabcodes = getAllTabcodes();
		for (int i = 0; i < allTabcodes.length; i++) {
			CircularlyAccessibleValueObject[] cvos = getTableVO(getTableCodes()[i]);
			if (cvos != null)
				al.addAll(Arrays.asList(cvos));
		}

		return (SuperVO[]) al.toArray(new SuperVO[0]);
	}

	@Override
	public SuperVO[] getChildVOsByParentId(String tableCode, String parentid) {
		return null;
	}

	@Override
	public String getDefaultTableCode() {
		return getTableCodes()[0];
	}

	@SuppressWarnings("rawtypes")
	@Override
	public HashMap getHmEditingVOs() throws Exception {
		return null;
	}

	@Override
	public String getParentId(SuperVO item) {
		return null;
	}

	@Override
	public String[] getTableNames() {
		return new String[] {  "预提明细"/* -=notranslate=- */, "预提核销明细"/* -=notranslate=- */,  };
	}

	@Override
	public void setParentId(SuperVO item, String id) {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void setTableVO(String tableCode, CircularlyAccessibleValueObject[] vos) {
		hmChildVOs.put(tableCode, vos);
	}

	/**
	 * 获取全部子表页签信息，包括动态扩展的子表
	 *
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String[] getAllTabcodes() {
		return (String[]) hmChildVOs.keySet().toArray(new String[hmChildVOs.keySet().size()]);
	}

	@Override
	public Object clone() {
		AggAccruedBillVO aggVo = new AggAccruedBillVO();

		if (getParentVO() != null) {
			aggVo.setParentVO((AccruedVO) getParentVO().clone());
		}

		String[] allTabcodes = getAllTabcodes();
		for (int i = 0; i < allTabcodes.length; i++) {
			CircularlyAccessibleValueObject[] cvos = getTableVO(allTabcodes[i]);
			if (cvos != null) {
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

	public static String[] getHeadYbAmounts() {
		return headYbAmounts;
	}

	public static String[] getHeadOrgAmounts() {
		return headOrgAmounts;
	}

	public static String[] getHeadGroupAmounts() {
		return headGroupAmounts;
	}

	public static String[] getHeadGlobalAmounts() {
		return headGlobalAmounts;
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

	public static List<String> getBodyAssumeOrgBodyIterms() {
		return bodyAssumeOrgBodyIterms;
	}

	public AggAccruedBillVO getOldvo() {
		return oldvo;
	}

	public void setOldvo(AggAccruedBillVO oldvo) {
		this.oldvo = oldvo;
	}

	public static List<String> getNotRepeatFields() {
		return notRepeatFields;
	}

	public static void setNotRepeatFields(List<String> notRepeatFields) {
		AggAccruedBillVO.notRepeatFields = notRepeatFields;
	}

	public static String[] getParentNotCopyFields() {
		return parentNotCopyFields;
	}

	public static String[] getBodyNotCopyFields() {
		return bodyNotCopyFields;
	}

	public static String[] getRedbackClearParentFields() {
		return redbackClearParentFields;
	}

	public static String[] getRedbackClearBodyFields() {
		return redbackClearBodyFields;
	}



}