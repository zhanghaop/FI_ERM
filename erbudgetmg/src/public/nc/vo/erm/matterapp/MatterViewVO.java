package nc.vo.erm.matterapp;

import nc.vo.pubapp.pattern.model.entity.view.AbstractDataView;
import nc.vo.pubapp.pattern.model.meta.entity.view.DataViewMetaFactory;
import nc.vo.pubapp.pattern.model.meta.entity.view.IDataViewMeta;

public class MatterViewVO extends AbstractDataView{

	private static final long serialVersionUID = 1L;

	@Override
	public IDataViewMeta getMetaData() {
		return DataViewMetaFactory.getInstance().getBillViewMeta(AggMatterAppVO.class);
	}
}
