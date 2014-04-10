package nc.vo.ep.bx;

import java.util.ArrayList;
import java.util.List;

import nc.vo.arap.bx.util.BXConstans;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.ExtendedAggregatedValueObject;

/**
 * @author twei
 *
 * nc.vo.ep.bx.BxAggregatedVO
 *
 * 借款报销聚合VO
 *
 * 处理多表体数据
 *
 * @see ExtendedAggregatedValueObject
 */
public class BxAggregatedVO extends ExtendedAggregatedValueObject{

	private static final long serialVersionUID = 2342582912153969691L;

	private BXBusItemVO[] childrenVO;

	private JKBXHeaderVO parentVO;

	private BxcontrastVO[] contrastVO;

	public BxAggregatedVO(String djdl) {
		setParentVO(VOFactory.createHeadVO(djdl));
	}

	@Override
	public JKBXHeaderVO getParentVO() {
		return parentVO;
	}

	@Override
	public String[] getTableCodes() {

		return new String[] { new BXBusItemVO().getTableName() };
	}

	@Override
	public String[] getTableNames() {

		return new String[] { nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000279")/*@res "业务信息"*/ };
	}

	@Override
	public void setParentVO(CircularlyAccessibleValueObject parentVO) {
		this.parentVO = (JKBXHeaderVO) parentVO;
	}

	@Override
	public CircularlyAccessibleValueObject[] getTableVO(String tableCode) {
		List<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();
		if(BXConstans.CONST_PAGE.equals(tableCode)){
			return contrastVO;
		}else{
			if (childrenVO != null && childrenVO.length != 0) {
				for (int i = 0; i < childrenVO.length; i++) {
					if (childrenVO[i].getTablecode().equals(tableCode)) {
						list.add(childrenVO[i]);
					}
				}
			}

			return list.toArray(new CircularlyAccessibleValueObject[] {});
		}
	}

	@Override
	public void setTableVO(String tableCode, CircularlyAccessibleValueObject[] values) {
		List<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();
		if(BXConstans.CONST_PAGE.equals(tableCode)){
			//冲销VO
			contrastVO = (BxcontrastVO[])values;
		}else{
			if (childrenVO != null && childrenVO.length != 0) {
				for (int i = 0; i < childrenVO.length; i++) {
					if(childrenVO[i].getTablecode()==null)
						continue;
					if (!childrenVO[i].getTablecode().equals(tableCode)) {
						list.add(childrenVO[i]);
					}
				}
			}
			for (int i = 0; i < values.length; i++) {
				String code = tableCode;
				if(BXConstans.JK_DJDL.equals(parentVO.getDjdl())){
					code = BXConstans.BUS_PAGE_JK;
				}
				values[i].setAttributeValue(BXBusItemVO.TABLECODE, code);
				list.add(values[i]);
			}
			childrenVO = list.toArray(new BXBusItemVO[] {});
		}
	}

	public CircularlyAccessibleValueObject[] getChildrenVO() {
		return childrenVO;
	}

	public BxcontrastVO[] getContrastVO() {
		return contrastVO;
	}

	public void setContrastVO(BxcontrastVO[] contrastVO) {
		this.contrastVO = contrastVO;
	}


}