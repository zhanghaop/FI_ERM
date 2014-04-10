package nc.vo.erm.matterapp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import nc.vo.pub.AggregatedValueObject;

/**
 * �������뵥����ת����ķ��ؽ��
 * 
 * @author lvhj
 * 
 */
public class MatterAppConvResVO implements Serializable {

	private static final long serialVersionUID = 1;

	/**
	 * ת�����ҵ������
	 */
	private AggregatedValueObject busiobj;

	/**
	 * �����������뵥���Ƶ�ҵ�񵥾��ֶ�
	 */
	private Map<String, List<String>> mtCtrlBusiFieldMap;

	public AggregatedValueObject getBusiobj() {
		return busiobj;
	}

	public void setBusiobj(AggregatedValueObject busiobj) {
		this.busiobj = busiobj;
	}

	public Map<String, List<String>> getMtCtrlBusiFieldMap() {
		return mtCtrlBusiFieldMap;
	}

	public void setMtCtrlBusiFieldMap(Map<String, List<String>> mtCtrlBusiFieldMap) {
		this.mtCtrlBusiFieldMap = mtCtrlBusiFieldMap;
	}

}
