package nc.vo.er.settle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import nc.bs.framework.common.NCLocator;
import nc.cmp.utils.NetPayHelper;
import nc.itf.bd.psn.psndoc.IPsndocQueryService;
import nc.itf.cm.prv.CmpConst;
import nc.itf.cmp.busi.ISettlementInfoFetcher;
import nc.itf.uap.IUAPQueryBS;
import nc.jdbc.framework.processor.ArrayProcessor;
import nc.md.persist.framework.MDPersistenceService;
import nc.pubitf.uapbd.ICustomerPubService;
import nc.pubitf.uapbd.ISupplierPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.bd.cust.CustomerVO;
import nc.vo.bd.freecustom.FreeCustomVO;
import nc.vo.bd.supplier.SupplierVO;
import nc.vo.cmp.netpay.NetPayHelperVO;
import nc.vo.cmp.settlement.SettleEnumCollection;
import nc.vo.cmp.settlement.SettlementBodyVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.Paytarget;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;
/**
*
* nc.vo.er.settle.ErSettlementInfo
*
* 由业务页签向结算页签传递信息,业务涉及**对公支付**，有对公支付字段区分
*/
public class ErSettlementInfo implements ISettlementInfoFetcher {
	private JKBXVO bxvo = null;

	public SettlementBodyVO[] getSettleMetaInfo() throws BusinessException {
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		JKBXHeaderVO head =  bxvo.getParentVO();
		//是否报销单据  v631后 借款单也有收款信息
//		final boolean isBXBill = BXConstans.BX_DJDL.equals(bxvo.getParentVO().getDjdl());l boolean isBXBill = BXConstans.BX_DJDL.equals(bxvo.getParentVO().getDjdl());
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
		
		//对方类型（个人、供应商、散户）
		int traderType = 0;
		//对方名称（个人名称、供应商名称、散户名称）
		String traderName = null;
		//对方的pk（个人pk、 供应商pk、散户pk）
		String pk_trader = null;
		
		//账户银行名称
		String bankDocName = null;
		//账户银行pk（设置pk后，名称则不需要设置）
		String pk_oopBank = null;
		
		//收款银行账户(pk)
		String pk_oppAccount = head.getSkyhzh();
		//收款银行账户（String，账号）
		String oppAccount = null;
		//收款银行账户名称
		String oppAccountName = null;
		//对方银行账户性质
		Integer accountType = null;
		
		//散户
		FreeCustomVO freeCustVO = null;
		//对方信息设置
		if (head.getIscusupplier().equals(UFBoolean.TRUE)) { // 对公支付

			// 供应商与个人不能同时录入
			if (head.getFreecust() != null) {// 散户优先级最高
				freeCustVO = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK(FreeCustomVO.class,
						head.getFreecust(), false);

				if (freeCustVO != null) {
					traderType = CmpConst.TradeObjType_SanHu;
					pk_trader = freeCustVO.getPk_freecustom();
					traderName = freeCustVO.getName();
					oppAccountName = freeCustVO.getName();
					oppAccount = freeCustVO.getBankaccount();
					pk_oopBank = freeCustVO.getBank();
					accountType = freeCustVO.getAccountproperty();
				}
			} else if (head.getHbbm() != null) {// 供应商
				{
					traderType = CmpConst.TradeObjType_SUPPLIER;
					pk_trader = head.getHbbm();
					pk_oppAccount = head.getCustaccount();

					SupplierVO[] hbbmname = NCLocator.getInstance().lookup(ISupplierPubService.class).getSupplierVO(
							new String[] { head.getHbbm() }, new String[] { "name" });
					traderName = hbbmname[0].getName();
				}
			} else if (head.getCustomer() != null) { // 客户
				traderType = CmpConst.TradeObjType_CUSTOMER;
				pk_trader = head.getCustomer();
				pk_oppAccount = head.getCustaccount();
				if (pk_oppAccount != null) {
					NetPayHelperVO opphelperVO = NetPayHelper.instance.getNetPayVO(pk_oppAccount);
					bankDocName = opphelperVO.getBankdocname();
				}
				CustomerVO[] customervos = NCLocator.getInstance().lookup(ICustomerPubService.class).getCustomerVO(
						new String[] { head.getCustomer() }, new String[] { "name" });
				traderName = customervos[0].getName();

			}
		}else {// 个人
			traderType = CmpConst.TradeObjType_Person;
			// 收款人
			pk_trader = head.getReceiver();
			
			if (pk_trader != null) {
				traderName = NCLocator.getInstance().lookup(IPsndocQueryService.class)
						.queryPsndocVOsByCondition("pk_psndoc='" + pk_trader + "'")[0].getName();
			}
			pk_oppAccount = head.getSkyhzh();
		}
		
		if (pk_oopBank != null) {// 银行名称
			Object[] result = (Object[]) NCLocator
					.getInstance()
					.lookup(IUAPQueryBS.class)
					.executeQuery("select name from bd_bankdoc where pk_bankdoc='" + pk_oopBank + "'",
							new ArrayProcessor());
			if (result != null && result.length != 0 && result[0] != null) {
				bankDocName = (String) result[0];// 对方账户银行
			}
		}

		if (pk_oppAccount != null) {
			NetPayHelperVO opphelperVO = NetPayHelper.instance.getNetPayVO(pk_oppAccount);
			if (opphelperVO != null) {
				oppAccount = opphelperVO.getAccount();// 对方账户
				oppAccountName = opphelperVO.getAccname();// 对方账号名称
				if (bankDocName == null) {
					bankDocName = opphelperVO.getBankdocname();
				}
			}
		}
		
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
			metavo.setPk_org(head.getPk_payorg());
			metavo.setPk_org_v(head.getPk_payorg_v());
			metavo.setPk_group(head.getPk_group());
			metavo.setGrouppaylocal(head.getGroupzfbbje() == null ? ufZero : item.getGroupzfbbje());
			metavo.setGlobalpaylocal(head.getGlobalzfbbje() == null ? ufZero: item.getGlobalzfbbje());
			metavo.setGroupreceivelocal(head.getGrouphkbbje() == null ? ufZero: item.getGrouphkbbje());
			metavo.setGlobalreceivelocal(head.getGlobalhkbbje() == null ? ufZero: item.getGlobalhkbbje());
			metavo.setGrouprate(head.getGroupbbhl() == null ? ufZero: head.getGroupbbhl());
			metavo.setGlobalrate(head.getGlobalbbhl() == null ? ufZero: head.getGlobalbbhl());
			metavo.setPk_rescenter(head.getFydeptid());
			metavo.setNotenumber(head.getPjh());
			metavo.setPk_notetype(head.getChecktype());
			metavo.setPk_currtype(head.getBzbm());
			
			//对方信息设置（类型、名称、银行名称、银行账户）
			metavo.setTradertype(traderType);
			metavo.setPk_trader(pk_trader);
			metavo.setTradername(traderName);
			metavo.setPk_oppaccount(pk_oppAccount);
			metavo.setOppaccount(oppAccount);
			metavo.setOppaccname(oppAccountName);
			metavo.setPk_oppbank(pk_oopBank);
			metavo.setOppbank(bankDocName);
			metavo.setAccounttype(accountType);
			
			//固定为外部交易			
			metavo.setTranstype(CmpConst.TradeObjType_Department); 
			metavo.setPk_costsubj(item.getSzxmid());
			metavo.setPk_plansubj(head.getCashproj());
			metavo.setPk_balatype(head.getJsfs());
			
			//本方信息
			metavo.setPk_psndoc(jkbxr);
			metavo.setPk_account(head.getFkyhzh());		

//begin--added by chendya v6.1 新增现金帐户传结算			
			metavo.setPk_cashaccount(head.getPk_cashaccount());
//--end	
			metavo.setPk_job(item.getJobid());
			metavo.setPk_cashflow(head.getCashitem());
			metavo.setPk_deptdoc(head.getDeptid());			
			metavo.setPk_busitype(head.getBusitype());
//			metavo.setPk_cubasdoc(head.getHbbm());
			// pk_cubasdoc 客商档案
			if (head.getHbbm() != null) {
				metavo.setPk_cubasdoc(head.getHbbm());
			} else if (head.getCustomer() != null) {
				metavo.setPk_cubasdoc(head.getCustomer());
			}
			metavo.setPk_jobphase(item.getProjecttask());
			metavo.setPk_invbasdoc(null);
			metavo.setPk_invcl(null);			
			metavo.setFundsflag(metavo.getDirection());	
			metavoList.add(metavo);
		}
		return metavoList.toArray(new SettlementBodyVO[0]);
	}
	/**
	 * 对于报销单多支付对象的处理
	 * @return
	 */
	private SettlementBodyVO[] getSettleMutiMetaInfo() throws BusinessException{
		BXBusItemVO[] childrenVO = bxvo.getChildrenVO();
		JKBXHeaderVO head =  bxvo.getParentVO();
		List<SettlementBodyVO> metavoList = new ArrayList<SettlementBodyVO>();
		
		//没有表体时，根据表头生成一个业务行
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
			for(String money:money_fields){//处理表体金额字段
				bxBusItemVO.setAttributeValue(money, head.getAttributeValue(money));
			}
			bxBusItemVO.setPaytarget(head.getPaytarget());
			bxBusItemVO.setReceiver(head.getReceiver());
			bxBusItemVO.setSkyhzh(head.getSkyhzh());
			bxBusItemVO.setHbbm(head.getHbbm());
			bxBusItemVO.setCustomer(head.getCustomer());
			bxBusItemVO.setCustaccount(head.getCustaccount());
			bxBusItemVO.setFreecust(head.getFreecust());
			bxBusItemVO.setSzxmid(head.getSzxmid());
			bxBusItemVO.setJobid(head.getJobid());
			bxBusItemVO.setCashproj(head.getCashproj());
			childrenVO=new BXBusItemVO[]{bxBusItemVO};
		}
		
		//定义缓存:为了减少查询数据库的次数
		Map<String,String> cache = new LinkedHashMap<String,String>();
		
		for (int i = 0, size = childrenVO.length; i < size; i++) {
			
			//对方类型（个人、供应商、散户）
			int traderType = 0;
			//对方名称（个人名称、供应商名称、散户名称）
			String traderName = null;
			//对方的pk（个人pk、 供应商pk、散户pk）
			String pk_trader = null;
			
			//账户银行名称
			String bankDocName = null;
			//账户银行pk（设置pk后，名称则不需要设置）
			String pk_oopBank = null;
			
			//收款银行账户(pk)
			String pk_oppAccount = head.getSkyhzh();
			//收款银行账户（String，账号）
			String oppAccount = null;
			//收款银行账户名称
			String oppAccountName = null;
			//对方银行账户性质
			Integer accountType = null;
			
			//散户
			FreeCustomVO freeCustVO = null;
			
			BXBusItemVO item = childrenVO[i];
			//支付原币金额和还款原币金额都0，则不传结算
			final UFDouble ufZero =new UFDouble(0);
			if ((item.getZfybje() == null || item.getZfybje().equals(ufZero))
					&& ((item.getHkybje() == null) || item.getHkybje().equals(ufZero))) {
				continue;
			}
			SettlementBodyVO metavo = new SettlementBodyVO();
			//设置表头的字段
			metavo.setBillcode(head.getDjbh());
			metavo.setBilldate(head.getDjrq());
			metavo.setLocalrate(head.getBbhl());
			metavo.setMemo(head.getZy());
			metavo.setPk_billtype(head.getDjlxbm());
			metavo.setPk_bill(head.getPk_jkbx());
			metavo.setPk_org(head.getPk_payorg());
			metavo.setPk_org_v(head.getPk_payorg_v());
			metavo.setPk_group(head.getPk_group());
			metavo.setGrouprate(head.getGroupbbhl() == null ? ufZero: head.getGroupbbhl());
			metavo.setGlobalrate(head.getGlobalbbhl() == null ? ufZero: head.getGlobalbbhl());
			metavo.setPk_rescenter(head.getFydeptid());
			metavo.setNotenumber(head.getPjh());
			metavo.setPk_notetype(head.getChecktype());
			metavo.setPk_currtype(head.getBzbm());
			metavo.setPk_plansubj(head.getCashproj());
			metavo.setPk_balatype(head.getJsfs());
			metavo.setPk_account(head.getFkyhzh());
			metavo.setPk_cashaccount(head.getPk_cashaccount());
			metavo.setPk_busitype(head.getBusitype());
			metavo.setPk_psndoc(head.getJkbxr());
			metavo.setPk_cashflow(head.getCashitem());
			metavo.setPk_deptdoc(head.getDeptid());			
			
			//从表体带过来的值
			final Integer direction = (item.getZfybje()!=null && !item.getZfybje().equals(ufZero))? SettleEnumCollection.Direction.PAY.VALUE: SettleEnumCollection.Direction.REC.VALUE;
			metavo.setDirection(direction);
			metavo.setPay(item.getZfybje());
			metavo.setPaylocal(item.getZfbbje());
			metavo.setReceive(item.getHkybje());
			metavo.setReceivelocal(item.getHkbbje());
			metavo.setSystemcode(BXConstans.ERM_MD_NAMESPACE);
			metavo.setPk_billdetail(item.getPrimaryKey());	
			metavo.setGrouppaylocal(item.getGroupzfbbje() == null ? ufZero : item.getGroupzfbbje());
			metavo.setGlobalpaylocal(item.getGlobalzfbbje() == null ? ufZero: item.getGlobalzfbbje());
			metavo.setGroupreceivelocal(item.getGrouphkbbje() == null ? ufZero: item.getGrouphkbbje());
			metavo.setGlobalreceivelocal(item.getGlobalhkbbje() == null ? ufZero: item.getGlobalhkbbje());
			metavo.setTranstype(CmpConst.TradeObjType_Department);
			metavo.setPk_costsubj(item.getSzxmid());
			metavo.setPk_job(item.getJobid());
			metavo.setPk_jobphase(item.getProjecttask());
			metavo.setPk_invbasdoc(null);
			metavo.setPk_invcl(null);			
			metavo.setFundsflag(metavo.getDirection());	
			if (item.getHbbm() != null) {
				metavo.setPk_cubasdoc(item.getHbbm());
			} else if (item.getCustomer() != null) {
				metavo.setPk_cubasdoc(item.getCustomer());
			}
			
			//对方信息设置（类型、名称、银行名称、银行账户）:
			if(item.getPaytarget().compareTo(Paytarget.EMPLOYEE)==0){//收款对象是员工，按收款人
				traderType = CmpConst.TradeObjType_Person;
				// 收款人
				pk_trader = item.getReceiver();
				
				if (pk_trader != null) {
					if(cache.get(pk_trader)!=null){//先从缓存中取，再查数据库
						traderName = cache.get(pk_trader);
					}else{
						traderName = NCLocator.getInstance().lookup(IPsndocQueryService.class)
						.queryPsndocVOsByCondition("pk_psndoc='" + pk_trader + "'")[0].getName();
						cache.put(pk_trader, traderName);
					}
				}
				pk_oppAccount = item.getSkyhzh();
			}
			else if(item.getPaytarget().compareTo(Paytarget.HBBM)==0 || item.getPaytarget().compareTo(Paytarget.CUSTOMER)==0){
				// 供应商与个人不能同时录入
				if (item.getFreecust() != null) {// 散户优先级最高
					freeCustVO = MDPersistenceService.lookupPersistenceQueryService().queryBillOfVOByPK(FreeCustomVO.class,
							item.getFreecust(), false);
					if (freeCustVO != null) {
						traderType = CmpConst.TradeObjType_SanHu;
						pk_trader = freeCustVO.getPk_freecustom();
						traderName = freeCustVO.getName();
						oppAccountName = freeCustVO.getName();
						oppAccount = freeCustVO.getBankaccount();
						pk_oopBank = freeCustVO.getBank();
						accountType = freeCustVO.getAccountproperty();
					}
				} else if (item.getPaytarget().compareTo(Paytarget.HBBM)==0 && item.getHbbm() != null) {// 供应商
					
						traderType = CmpConst.TradeObjType_SUPPLIER;
						pk_trader = item.getHbbm();
						pk_oppAccount = item.getCustaccount();
						if(cache.get(pk_trader)!=null){
							traderName =  cache.get(pk_trader);
						}else{
							SupplierVO[] hbbmname = NCLocator.getInstance().lookup(ISupplierPubService.class).getSupplierVO(
									new String[] { item.getHbbm() }, new String[] { "name" });
							traderName = hbbmname[0].getName();
							cache.put(pk_trader, traderName);
						}
					
				} else if (item.getPaytarget().compareTo(Paytarget.CUSTOMER)==0 && item.getCustomer() != null) { // 客户
						traderType = CmpConst.TradeObjType_CUSTOMER;
						pk_trader = item.getCustomer();
						pk_oppAccount = item.getCustaccount();
						if(cache.get(pk_trader)!=null){
							traderName =  cache.get(pk_trader);
						}else{
							CustomerVO[] customervos = NCLocator.getInstance().lookup(ICustomerPubService.class).getCustomerVO(
									new String[] { item.getCustomer() }, new String[] { "name" });
							traderName = customervos[0].getName();
							cache.put(pk_trader, traderName);
						}
				}
			}else{
				//收款对象是外部人员:
				//自定义项39 是外部人员：自定义项38是收款户名 ： 自定义项37是对方账户： 自定义项36是对方银行
				traderType = CmpConst.TradeObjType_Person;
//				pk_trader = (String) item.getDefitem39();
//				traderName = (String) item.getDefitem39();
				oppAccount = (String) item.getDefitem38();
				oppAccountName = (String) item.getDefitem37();
				bankDocName = (String) item.getDefitem36();
			}
			//不是外部用户时
			if(item.getPaytarget().compareTo(Paytarget.OUTSIDEPERSON)!=0){
				//散户
				if (pk_oopBank != null) {// 银行名称
					Object[] result = (Object[]) NCLocator.getInstance().lookup(IUAPQueryBS.class)
					.executeQuery("select name from bd_bankdoc where pk_bankdoc='" + pk_oopBank + "'",
							new ArrayProcessor());
					if (result != null && result.length != 0 && result[0] != null) {
						bankDocName = (String) result[0];// 对方账户银行
					}
				}
				
				//个人、供应商、客户
				if (pk_oppAccount != null && pk_oopBank == null) {
					NetPayHelperVO opphelperVO = NetPayHelper.instance.getNetPayVO(pk_oppAccount);
					if (opphelperVO != null) {
						oppAccount = opphelperVO.getAccount();// 对方账户
						oppAccountName = opphelperVO.getAccname();// 对方账号名称
						bankDocName = opphelperVO.getBankdocname();//对方银行
					}
				}
			}
			
			metavo.setTradertype(traderType);
			metavo.setPk_trader(pk_trader);
			metavo.setTradername(traderName);
			metavo.setOppaccount(oppAccount);
			metavo.setOppaccname(oppAccountName);
			metavo.setOppbank(bankDocName);
			metavo.setAccounttype(accountType);
			if(item.getPaytarget().compareTo(Paytarget.OUTSIDEPERSON)!=0){
				if (pk_oopBank != null) {// 两个值只设置一个且散户优先
					metavo.setPk_oppbank(pk_oopBank);
				} else {
					metavo.setPk_oppaccount(pk_oppAccount);
				}
			}
			metavoList.add(metavo);	
		}
		return metavoList.toArray(new SettlementBodyVO[0]);
	}
	
	
	
	public void setBillInfo(AggregatedValueObject billinfo) {
		bxvo =  (JKBXVO) billinfo;
	}
	
	@Override
	public SettlementBodyVO[] getSettleMetaInfo(Object arg0) throws BusinessException{
		bxvo =  (JKBXVO) arg0;
		//借款单和还款单还是按原来的逻辑处理、报销单按多支付对象处理
		if (BXConstans.JK_DJDL.equals(bxvo.getParentVO().getDjdl())
				|| BXConstans.BILLTYPECODE_RETURNBILL.equals(bxvo.getParentVO()
						.getDjlxbm())) {
			return getSettleMetaInfo();
		}
		return getSettleMutiMetaInfo();
	}


}
