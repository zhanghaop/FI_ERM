package nc.vo.erm.matterapp;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * 申请单关闭状态
 * @author chenshuaia
 *
 */
public class MatterAppCloseStatusEnum extends MDEnum {

	public MatterAppCloseStatusEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * 已关闭
	 */
	public static final MatterAppCloseStatusEnum CLOSED = MDEnum.valueOf(MatterAppCloseStatusEnum.class, 1);

	/**
	 * 未关闭
	 */
	public static final MatterAppCloseStatusEnum UNCLOSED = MDEnum.valueOf(MatterAppCloseStatusEnum.class, 2);
}
