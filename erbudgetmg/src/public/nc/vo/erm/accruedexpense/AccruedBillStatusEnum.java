package nc.vo.erm.accruedexpense;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

public class AccruedBillStatusEnum extends MDEnum {

	public AccruedBillStatusEnum(IEnumValue enumValue) {
		super(enumValue);
	}
	
	/**
	 * 暂存
	 */
	public static final AccruedBillStatusEnum TEMPSAVE = MDEnum.valueOf(AccruedBillStatusEnum.class, 0);
	
	/**
	 * 保存
	 */
	public static final AccruedBillStatusEnum SAVE = MDEnum.valueOf(AccruedBillStatusEnum.class, 1);
	
	/**
	 * 已提交
	 */
	public static final AccruedBillStatusEnum COMMITED = MDEnum.valueOf(AccruedBillStatusEnum.class, 2);

	/**
	 * 已审批
	 */
	public static final AccruedBillStatusEnum APPROVED = MDEnum.valueOf(AccruedBillStatusEnum.class, 3);
}
