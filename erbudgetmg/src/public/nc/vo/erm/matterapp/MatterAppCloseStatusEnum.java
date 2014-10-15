package nc.vo.erm.matterapp;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * ���뵥�ر�״̬
 * @author chenshuaia
 *
 */
public class MatterAppCloseStatusEnum extends MDEnum {

	public MatterAppCloseStatusEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * �ѹر�
	 */
	public static final MatterAppCloseStatusEnum CLOSED = MDEnum.valueOf(MatterAppCloseStatusEnum.class, 1);

	/**
	 * δ�ر�
	 */
	public static final MatterAppCloseStatusEnum UNCLOSED = MDEnum.valueOf(MatterAppCloseStatusEnum.class, 2);
}
