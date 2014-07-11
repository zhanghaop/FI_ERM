package nc.ui.erm.billquery.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.ui.uif2.NCAction;
import nc.ui.uif2.ShowStatusBarMsgUtil;
import nc.ui.uif2.model.BillManageModel;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.settle.SettleUtil;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
/**
**
* 只针对结算管理生成凭证环节是结算成功
* 处理已生效末结算的单据
 * @author wangled
 *
 */
public class DealVoucherAction extends NCAction {
	private static final long serialVersionUID = 1L;

	private BillManageModel model;

	public DealVoucherAction() {
		setCode("DealVoucher");
		this.setBtnName(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("201107_0","0201107-0164")/*@res "生成凭证"*/);
	}
	@Override
	public void doAction(ActionEvent e) throws Exception {
		Object[] vos = (Object[]) getModel().getSelectedOperaDatas();
		
		boolean existSuccess = true;
		
		boolean exist = false;
        
		if (vos==null || vos.length < 1) {
			throw new BusinessException(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0165")/* @res "没有可生成凭证的单据,操作失败"*/);
		}
		
		JKBXVO[] jkbxVos = Arrays.asList(vos).toArray(new JKBXVO[0]);
		StringBuffer msg = new StringBuffer();
		List<JKBXVO> dealVo = new ArrayList<JKBXVO>();

		//使用于通版
		existSuccess = dealBillBaseVersion(existSuccess, jkbxVos, msg, dealVo);
		
		
		if (dealVo != null && dealVo.size() != 0) {
			try {
				// 传会计平台
				List<JKBXVO> returnVo = NCLocator.getInstance().lookup(IBXBillPrivate.class).effectToFip(dealVo);
				
				exist = true;//有成功的
				//
				getModel().directlyUpdate(returnVo.toArray(new AggregatedValueObject[returnVo.size()]));

			} catch (BusinessException e1) {
				existSuccess = false;
				msg.append(e1.getMessage());
			}
		}else if((dealVo==null ||dealVo.size()==0) && existSuccess){
			existSuccess = false;
			msg.append(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("201107_0", "0201107-0170"));//不符合条件，只处理生成凭证环节是结算成功的单据
		}
		
		if (existSuccess) {
			ShowStatusBarMsgUtil.showStatusBarMsg(nc.ui.ml.NCLangRes
					.getInstance().getStrByID("201107_0", "0201107-0169"),
					getModel().getContext());
		} else if(!existSuccess && exist){//有成功也有失败
			ShowStatusBarMsgUtil.showErrorMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0172"), msg.toString(),
					getModel().getContext());
		} else if(!existSuccess && !exist){
			ShowStatusBarMsgUtil.showErrorMsg(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0", "0201107-0173"), msg.toString(),
					getModel().getContext());
		}
	}
	/**
	 * ehp2 : 用来判断单据是否被删除
	 * @param jkbxVos
	 * @throws BusinessException
	 */
	private Map<String,JKBXHeaderVO> checkBillExist(JKBXVO[] jkbxVos) throws BusinessException {
		Map<String,JKBXHeaderVO> map = new LinkedHashMap<String,JKBXHeaderVO>();
		//并发校验：如果单据被删除了，提示信息
		String[] pk_jkbx = nc.vo.fipub.utils.VOUtil.getAttributeValues(jkbxVos, JKBXHeaderVO.PK_JKBX);
		List<JKBXHeaderVO> queryHeaders = NCLocator.getInstance().
		lookup(IBXBillPrivate.class).queryHeadersByPrimaryKeys(pk_jkbx, null);
		if(queryHeaders!=null){
			for (JKBXHeaderVO jkbxHeaderVO : queryHeaders) {
				map.put(jkbxHeaderVO.getPk_jkbx(), jkbxHeaderVO);
			}
		}
		return map;
	}

	/**
	 * ehp2 : 适用通版
	 * @param existSuccess
	 * @param jkbxVos
	 * @param msg
	 * @param dealVo
	 * @return
	 * @throws BusinessException
	 */
	private boolean dealBillBaseVersion(boolean existSuccess, JKBXVO[] jkbxVos,
			StringBuffer msg, List<JKBXVO> dealVo) throws BusinessException {
		
		Map<String, JKBXHeaderVO> ExistBill = checkBillExist(jkbxVos);
		
		for (int i = 0; i < jkbxVos.length; i++) {
			if(SettleUtil.isJsToFip(jkbxVos[i].getParentVO())){
				//对于单据的并发校验
				if(!ExistBill.containsKey(jkbxVos[i].getParentVO().getPk_jkbx())){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("2011",
					"UPP2011-000954")).append("\r\n");
					continue;
				}
				//对于暂存的单据不能生成凭证
				if(jkbxVos[i].getParentVO().getSxbz()!=null && BXStatusConst.DJZT_TempSaved == jkbxVos[i].getParentVO().getDjzt()){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0174")).append("\r\n");
					continue;
				}
		
				//对于作废的单据不能月末处理:ehp3
				if(jkbxVos[i].getParentVO().getDjzt()!=null && BXStatusConst.DJZT_Invalid == jkbxVos[i].getParentVO().getDjzt()){
					existSuccess = false;
					msg.append(nc.ui.ml.NCLangRes.getInstance().getStrByID("201107_0",
					"0201107-0166") + jkbxVos[i].getParentVO().getDjbh() + " "+nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("common", "UPP2011-000955")).append("\r\n");
					continue;
				}

				dealVo.add(jkbxVos[i]);//需要传会计平台的单据
			}else{
				continue;
			}
		}
		return existSuccess;
	}
	
	public BillManageModel getModel() {
		return model;
	}

	public void setModel(BillManageModel model) {
		this.model = model;
		this.model.addAppEventListener(this);
	}

	@Override
	protected boolean isActionEnable() {
		boolean flag = BXUtil.isProductInstalled(getModel().getContext().getPk_group(), BXConstans.TM_CMP_FUNCODE);
		if(BXConstans.MONTHEND_DEAL.equals(getModel().getContext().getNodeCode())){
			if(!flag){
				ShowStatusBarMsgUtil.showErrorMsg(nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("201107_0",
						"0201107-0035"), nc.vo.ml.NCLangRes4VoTransl
						.getNCLangRes().getStrByID("201107_0",
						"0201107-0171"),//未安全现金管理模块，该按钮、节点不可用
						getModel().getContext());
			}
		}
		return flag;
	}
	
	
}
