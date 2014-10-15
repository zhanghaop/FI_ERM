package nc.bs.erm.prealarm;

import java.util.List;

import nc.ui.pub.print.IDataSource;

import nc.vo.fipub.exception.ExceptionHandler;
import nc.vo.pub.BeanHelper;

public class ErmPrealarmDataSource implements IDataSource {
	private static final long serialVersionUID = 8232914260850904079L;

	private List<ErmPrealarmBaseVO> datas = null;

	private Class<? extends ErmPrealarmBaseVO> clzz = null;

	public ErmPrealarmDataSource(List<ErmPrealarmBaseVO> datas,
			Class<? extends ErmPrealarmBaseVO> clzz) {
		this.datas = datas;
		this.clzz = clzz;
	}

	@Override
	public String[] getAllDataItemExpress() {
		try {
			return BeanHelper.getInstance().getPropertiesAry(clzz.newInstance());
		} catch (Exception e) {
			ExceptionHandler.consume(this.getClass(), e);
			return new String[0];
		}
	}

	@Override
	public String[] getItemValuesByExpress(String itemExpress) {
		if (datas == null || datas.size() == 0) {
			return null;
		}
		String[] colValues = new String[datas.size()];
		Object propVal = null;
		for (int i = 0; i < datas.size(); i++) {
			propVal = BeanHelper.getProperty(datas.get(i), itemExpress);
			colValues[i] = propVal == null ? null : propVal.toString();
		}
		return colValues;
	}

	@Override
	public String[] getAllDataItemNames() {
		return null;
	}

	@Override
	public String[] getDependentItemExpressByExpress(String itemExpress) {
		return null;
	}

	@Override
	public String getModuleName() {
		return null;
	}

	@Override
	public boolean isNumber(String itemExpress) {
		return false;
	}

	public List<ErmPrealarmBaseVO> getDatas() {
		return datas;
	}

	public void setDatas(List<ErmPrealarmBaseVO> datas) {
		this.datas = datas;
	}

	public Class<? extends ErmPrealarmBaseVO> getClzz() {
		return clzz;
	}

	public void setClzz(Class<? extends ErmPrealarmBaseVO> clzz) {
		this.clzz = clzz;
	}
}
