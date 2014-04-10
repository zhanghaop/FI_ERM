package nc.vo.er.settle;
/**
 *
 * nc.vo.er.settle.ErSettlementInfo
 *
 * 由业务页签向结算页签传递信息,业务涉及**对公支付**
 */
import java.util.ArrayList;
import java.util.List;

import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.NetPayHelper;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.cm.prv.CmpConst;
import nc.itf.cmp.busi.ISettlementInfoFetcher;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.netpay.NetPayHelperVO;
import nc.vo.cmp.settlement.SettleEnumCollection;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.util.StringUtils;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFDouble;

public class ErSettlementInfo implements ISettlementInfoFetcher {
	private JKBXVO bxvo = null;

	public SettlementBodyVO[] getSettleMetaInfo() throws BusinessException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		JKBXHeaderVO head =  bxvo.getParentVO();
		//是否报销单据
		final boolean isBXBill = BXConstans.BX_DJDL.equals(bxvo.getParentVO().getDjdl());
		//借款报销人
		String jkbxr = bxvo.getParentVO().getJkbxr();
		if(childrenVO==null || childrenVO.length==0){
			BXBusItemVO bxBusItemVO = new BXBusItemVO();
			String[] money_fields = BXBusItemVO.MONEY_FIELDS;
			if(head.getPk_jkbx()!=null){
				bxBusItemVO.setPrimaryKey(head.getPk_jkbx());
				bxBusItemVO.setPk_jkbx(head.getPk_jkbx());
			}else{
				bxBusItemVO.setPrimaryKey(BXConstans.TEMP_ZB_PK);
				bxBusItemVO.setPk_jkbx(BXConstans.TEMP_ZB_PK);
			}
			for(String money:money_fields){
				bxBusItemVO.setAttributeValue(money, head.getAttributeValue(money));
			}
			bxBusItemVO.setJkbxr(head.getJkbxr());
			bxBusItemVO.setSzxmid(head.getSzxmid());
			bxBusItemVO.setJobid(head.getJobid());
			bxBusItemVO.setCashproj(head.getCashproj());
			childrenVO=new BXBusItemVO[]{bxBusItemVO};
		}
		List<SettlementBodyVO> metavoList = new ArrayList<SettlementBodyVO>();
		for (int i = 0, size = childrenVO.length; i < size; i++) {
			BXBusItemVO item = childrenVO[i];
			//支付原币金额和还款原币金额都0，则不传结算
			final UFDouble ufZero =new UFDouble(0);
			if ((item.getZfybje() == null || item.getZfybje().equals(ufZero))
					&& ((item.getHkybje() == null) || item.getHkybje().equals(ufZero))) {
				continue;
			}
			SettlementBodyVO metavo = new SettlementBodyVO();
			//收付方向(收0,付1)
			final Integer direction = (item.getZfybje()!=null && !item.getZfybje().equals(ufZero))? SettleEnumCollection.Direction.PAY.VALUE: SettleEnumCollection.Direction.REC.VALUE;
			metavo.setBillcode(head.getDjbh());
			metavo.setBilldate(head.getDjrq());
			metavo.setDirection(direction);
			metavo.setLocalrate(head.getBbhl());
			metavo.setMemo(head.getZy());
			metavo.setPay(item.getZfybje());
			metavo.setPaylocal(item.getZfbbje());
			metavo.setReceive(item.getHkybje());
			metavo.setReceivelocal(item.getHkbbje());
			
//begin--modified by chendya 报销传结算的"归属系统"字段标识
			metavo.setSystemcode(BXConstans.ERM_MD_NAMESPACE);
//--end			
			metavo.setPk_billdetail(item.getPrimaryKey());			
			metavo.setPk_billtype(head.getDjlxbm());
			metavo.setPk_bill(head.getPk_jkbx());
			metavo.setPk_org(head.getPk_org());
			metavo.setPk_org_v(head.getPk_org_v());
			metavo.setPk_group(head.getPk_group());
			metavo.setGrouppaylocal(head.getGroupbbje() == null ? ufZero : item.getGroupbbje());
			metavo.setGlobalpaylocal(head.getGlobalbbje() == null ? ufZero: item.getGlobalbbje());
			metavo.setGroupreceivelocal(head.getGroupbbje() == null ? ufZero: item.getGroupbbje());
			metavo.setGlobalrate(head.getGlobalbbhl() == null ? ufZero: head.getGlobalbbhl());
			metavo.setPk_rescenter(head.getFydeptid());
			metavo.setNotenumber(head.getPjh());
			metavo.setPk_notetype(head.getChecktype());
			metavo.setPk_currtype(head.getBzbm());
			
			//对方信息
			if(isBXBill){
				//报销单据,可能存在对公支付情况
				//收款人
				String pk_receiver = head.getReceiver();
				//供应商
				String pk_supplier = head.getHbbm();
				
				if(!StringUtils.isNullWithTrim(pk_receiver)){
					//对个人
					metavo.setTradertype(CmpConst.TradeObjType_Person ); 
					metavo.setPk_trader(pk_receiver); 
					metavo.setPk_oppaccount(head.getSkyhzh());
					try { 
						//查询报销人信息
						if(pk_receiver!=null) {
							String psnname = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocVOsByCondition("pk_psndoc='"+pk_receiver+"'")[0].getName();
							metavo.setTradername(psnname);
						}
					} catch (Exception e) {
						throw new BusinessRuntimeException(e.getMessage(),e);
					}
				} else{
					//对供应商(对公支付)
					metavo.setTradertype(CmpConst.TradeObjType_SUPPLIER); 	
					metavo.setPk_trader(pk_supplier); 
					metavo.setPk_oppaccount(head.getCustaccount());
					try { 
						//查询供应商信息
						if(pk_supplier!=null) {
							SupplierVO[] hbbmname = NCLocator.getInstance().lookup(ISupplierPubService.class).getSupplierVO(new String[]{pk_supplier}, new String[]{"name"});
							metavo.setTradername(hbbmname[0].getName());
						}
					} catch (Exception e) {
						throw new BusinessRuntimeException(e.getMessage(),e);
					}
				}
			}else{
				//借款单不存在对公支付情况
				metavo.setTradertype(CmpConst.TradeObjType_Person ); 
				metavo.setPk_trader(jkbxr); 
				metavo.setPk_oppaccount(head.getSkyhzh());
				try { //查询报销人信息
					if(jkbxr!=null) {
						String psnname = NCLocator.getInstance().lookup(IPsndocQueryService.class).queryPsndocVOsByCondition("pk_psndoc='"+jkbxr+"'")[0].getName();
						metavo.setTradername(psnname);
					}
				} catch (Exception e) {
					throw new BusinessRuntimeException(e.getMessage(),e);
				}
			}
			NetPayHelperVO opphelperVO = NetPayHelper.instance.getNetPayVO(metavo.getPk_oppaccount());
			metavo.setOppbank(opphelperVO.getBankdocname());
			
			//固定为外部交易			
			metavo.setTranstype(CmpConst.TradeObjType_Department); 
			metavo.setPk_costsubj(item.getSzxmid());
			metavo.setPk_plansubj(item.getCashproj());
			metavo.setPk_balatype(head.getJsfs());
			
			//本方信息
			metavo.setPk_psndoc(jkbxr);
			metavo.setPk_account(head.getFkyhzh());		

//begin--added by chendya v6.1 新增现金帐户传结算			
			metavo.setPk_cashaccount(head.getPk_cashaccount());
//--end	
			metavo.setPk_job(head.getJobid());
			metavo.setPk_cashflow(head.getCashitem());
			metavo.setPk_deptdoc(head.getDeptid());			
			metavo.setPk_busitype(head.getBusitype());
			metavo.setPk_cubasdoc(head.getHbbm());
			metavo.setPk_jobphase(head.getProjecttask());
			metavo.setPk_invbasdoc(null);
			metavo.setPk_invcl(null);			
			metavo.setFundsflag(metavo.getDirection());	
			metavoList.add(metavo);
		}
		return metavoList.toArray(new SettlementBodyVO[0]);
	}

	public void setBillInfo(AggregatedValueObject billinfo) {
		bxvo =  (JKBXVO) billinfo;
	}
	
	public SettlementBodyVO[] getSettleMetaInfo(Object arg0) throws BusinessException {
		bxvo =  (JKBXVO) arg0;
		return getSettleMetaInfo();
	}

}
