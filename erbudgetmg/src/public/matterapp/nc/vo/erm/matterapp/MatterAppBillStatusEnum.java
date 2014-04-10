package nc.vo.erm.matterapp;

import nc.md.model.IEnumValue;
import nc.md.model.impl.MDEnum;

/**
 * ���뵥����״̬ö��
 * @author chenshuaia
 *
 */
public class MatterAppBillStatusEnum extends MDEnum {

	public MatterAppBillStatusEnum(IEnumValue enumValue) {
		super(enumValue);
	}

	/**
	 * �ݴ�
	 */
	public static final MatterAppBillStatusEnum TEMPSAVED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 0);

	/**
	 * ����
	 */
	public static final MatterAppBillStatusEnum SAVED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 1);

	/**
	 * ���ύ
	 */
	public static final MatterAppBillStatusEnum COMMITED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 2);

	/**
	 * ������
	 */
	public static final MatterAppBillStatusEnum APPROVED = MDEnum.valueOf(MatterAppBillStatusEnum.class, 3);
}
