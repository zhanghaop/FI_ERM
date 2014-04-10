package nc.vo.erm.matterapp;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * 申请单单据状态枚举
 * @author chenshuaia
 *
 */
public class MatterAppBillStatusEnum extends MDEnum {

	public MatterAppBillStatusEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * 暂存
	 */
	public static final MatterAppBillStatusEnum TEMPSAVED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 0);

	/**
	 * 保存
	 */
	public static final MatterAppBillStatusEnum SAVED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 1);

	/**
	 * 已提交
	 */
	public static final MatterAppBillStatusEnum COMMITED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 2);

	/**
	 * 已审批
	 */
	public static final MatterAppBillStatusEnum APPROVED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 3);
}
