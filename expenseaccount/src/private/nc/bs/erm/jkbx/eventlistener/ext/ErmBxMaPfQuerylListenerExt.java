package nc.bs.erm.jkbx.eventlistener.ext;

import java.util.List;
import java.util.Map;

import nc.bs.businessevent.IBusinessEvent;
import nc.bs.businessevent.IBusinessListener;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmBusinessEvent.ErmCommonUserObj;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.matterappctrl.IMtapppfVOQryService;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.pub.BusinessException;
import nc.vo.trade.summarize.Hashlize;
import nc.vo.trade.summarize.IHashKey;

/**
 * 借款报销，各个动作事件前，申请单执行记录查询补充插件
 * 
 * 合生元专用
 * 
 * @author lvhj
 *
 */
public class ErmBxMaPfQuerylListenerExt implements IBusinessListener {

	@Override
	public void doAction(IBusinessEvent event) throws BusinessException {
		ErmBusinessEvent erEvent = (ErmBusinessEvent) event;

		ErmCommonUserObj obj = (ErmCommonUserObj) erEvent.getUserObject();
		JKBXVO[] vos = (JKBXVO[]) obj.getNewObjects();
		
		if(vos == null || vos.length == 0){
			return ;
		}
		
		String[] bxpks = VOUtils.getAttributeValues(vos, null);
		MtapppfVO[] pfVos = NCLocator.getInstance().lookup(IMtapppfVOQryService.class).queryMtapppfVoByBusiDetailPk(bxpks);
		if(pfVos != null && pfVos.length > 0){
			// 按报销单分组执行记录
			@SuppressWarnings("unchecked")
			Map<String, List<MtapppfVO>> bxPfMap = Hashlize.hashlizeObjects(pfVos, new IHashKey() {
				@Override
				public String getKey(Object o) {
					return ((MtapppfVO)o).getBusi_detail_pk();
				}
			});
			// 将执行记录信息补充到各个报销单中
			for (int i = 0; i < vos.length; i++) {
				String pk_jkbx = vos[i].getParentVO().getPrimaryKey();
				List<MtapppfVO> list = bxPfMap.get(pk_jkbx);
				vos[i].setMaPfVos(list==null?null:list.toArray(new MtapppfVO[0]));
			}
		}
		
	}
}
