package nc.vo.erm.common;

import java.util.ArrayList;

import nc.vo.pub.FieldObject;
import nc.vo.pub.NullFieldException;
import nc.vo.pub.SuperVO;
import nc.vo.pub.ValidationException;

public abstract class CommonSuperVO extends SuperVO {

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2007-9-21
	 *
	 * @return java.lang.String
	 */
	public java.lang.String getParentPKFieldName() {

		return null;

	}

	protected void validateNotNullField() throws NullFieldException {

		ArrayList<String> errFields = new ArrayList<String>(); // errFields record those null
		ArrayList<String> notNullFields = getNotNullFields();

		if(notNullFields==null || notNullFields.size()==0)
			return;

		for (String field : notNullFields) {
			if (getAttributeValue(field) == null || getAttributeValue(field).toString().trim().length()==0)
				errFields.add(getFieldName(field));
		}
		StringBuffer message = new StringBuffer();
		message.append(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000286")/*@res "表头下列字段不能为空:\n"*/);
		if (errFields.size() > 0) {
			String[] temp = errFields.toArray(new String[0]);
			message.append(temp[0]);
			for (int i = 1; i < temp.length; i++) {
				message.append(",");
				message.append(temp[i]);
			}
			throw new NullFieldException(message.toString());
		}
	}

	public void validate() throws ValidationException {

		validateNotNullField();

	}

	/**
	 * @return 非空字段列表
	 */
	protected abstract ArrayList<String> getNotNullFields();

	/**
	 * @param field
	 * @return 字段中文名
	 */
	public abstract String getFieldName(String field);

	/**
	 * @return VO检查类名 (主要是做vo之外的一些约束检查,例如编码不能重复.)
	 * @see VOCheck
	 */
	public abstract String getCheckClass();


	/**
	 * @return
	 */
	public FieldObject[] getFields() {
		return new FieldObject[]{};
	}

}