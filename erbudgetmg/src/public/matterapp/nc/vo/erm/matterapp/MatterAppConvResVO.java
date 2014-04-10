package nc.vo.erm.matterapp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import nc.vo.pub.AggregatedValueObject;

/**
 * 费用申请单拉单转换后的返回结果
 * 
 * @author lvhj
 * 
 */
public class MatterAppConvResVO implements Serializable {

	private static final long serialVersionUID = 1;

	/**
	 * 转换后的业务数据
	 */
	private AggregatedValueObject busiobj;

	/**
	 * 各个费用申请单控制的业务单据字段
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
