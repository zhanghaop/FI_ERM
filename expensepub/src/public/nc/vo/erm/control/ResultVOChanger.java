package nc.vo.erm.control;

/**
 * 此处插入类型描述。
 * 创建日期：(2004-4-1 14:33:50)
 * @author：钟悦
 */
 import java.util.List;

import nc.vo.pub.lang.UFDouble;
import nc.vo.tb.obj.NtbParamVO;

public class ResultVOChanger {
/**
 * ResultVOChanger 构造子注解。
 */
public ResultVOChanger() {
	super();
}
	public static UFDouble[][] getUFDoubleArray(FuncResultVO[] resultVOs){
		UFDouble[][] values = new UFDouble[resultVOs.length][];
		for(int i=0; i<resultVOs.length; i++){
	 		List valueArr =  (List) resultVOs[i].getResult();
           nc.vo.pub.lang.UFDouble[] ufdvalues = (UFDouble[])valueArr.toArray(new UFDouble[0]);
          	NtbParamVO ntbvo = (NtbParamVO)resultVOs[i].getSourceVO();
        	int index = ntbvo.getIndex();
       		values[index]= ufdvalues;
		}
		return values;
	}
}
