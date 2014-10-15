package nc.bs.arap.bx;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.bs.pub.pf.PfUtilTools;
import nc.bs.uap.oid.OidGenerator;
import nc.itf.fi.pub.Currency;
import nc.itf.fi.pub.SysInit;
import nc.pubitf.arap.payable.IArapPayableBillPubService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubService;
import nc.pubitf.uapbd.ICustsupPubService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.payable.PayableBillItemVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.arap.receivable.ReceivableBillItemVO;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDouble;

/**
 * @author twei
 *
 * nc.bs.arap.bx.ErPFUtil
 *
 * 转往来操作
 *
 * 生成条件1									生成条件2				生成单据				生成单据金额
 * 冲借款-还款金额≠0（即承担金额≠0）				费用承担单位≠借款单位	借款单位 应收单
 *																费用承担单位 应付单		冲借款-还款金额（承担金额）
 * 支付金额≠0									报销单位≠费用承担单位	报销单位 应收单
 *																费用承担单位 应付单		支付金额
 * 还款金额≠0									报销单位≠借款单位		报销单位应付单
 *																借款单位应收单			还款金额
 */
public class ErPFUtil {

	/**
	 * 应收单F0
	 */
	public static String AR = "F0";

	/**
	 * 应付单F1
	 */
	public static String AP = "F1";

	public ErPFUtil() {
	}

	private void pubTransInfo(Map<String,TransFerInfo> map,TransFerInfo vo) throws BusinessException{
		String key=vo.getFromCorp()+vo.getToCorp();
		if(map.containsKey(key)){
			TransFerInfo info = map.get(key);
			if(info.getType()!=vo.getType())
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000388")/*@res "转往来单据数据错误，不能同时对同一个公司进行收款和付款!"*/);
			info.setYb(info.getYb().add(vo.getYb()));
			info.setBb(info.getBb().add(vo.getBb()));
			map.put(key, info);
		}else{
			map.put(key, vo);
		}
	}

	@SuppressWarnings("unchecked")
	public void doTransferArap(JKBXVO vo) throws BusinessException {

		Map<String,TransFerInfo> map=new HashMap<String, TransFerInfo>();

		try {
			JKBXHeaderVO head = vo.getParentVO();

			if(head.getDjdl().equals(BXConstans.JK_DJDL)){ //只有报销单才需要进行转往来的操作
				return;
			}
			/**
			 * @ 读取“是否生成往来单据”参数，如果生成往来单据进行传收付操作
			 */
			UFBoolean isTransFer = SysInit.getParaBoolean(head.getPk_group(), BXParamConstant.PARAM_IS_TRANSTOARAP); //是否生成往来单据
			if(isTransFer!=null && !isTransFer.booleanValue())
				return;

			if(head.getZfybje().compareTo(UFDouble.ZERO_DBL)!=0){//支付金额大于0
				if(!head.getPk_payorg().equals(head.getPk_org())){
					// 支付金额大于0，且支付单位不等于主组织
					// 生成支付单位与主组织之间的往来，往来金额为支付金额
					pubTransInfo(map,new TransFerInfo(head.getPk_payorg_v(),head.getPk_payorg(),head.getPk_org(),head.getZfybje(),head.getZfbbje(),head.getGlobalzfbbje(),head.getGroupzfbbje(),vo,1));
					pubTransInfo(map,new TransFerInfo(head.getPk_org_v(),head.getPk_org(),head.getPk_payorg(),head.getZfybje(),head.getZfbbje(),head.getGlobalzfbbje(),head.getGroupzfbbje(),vo,2));
				}
			}
			
			if(head.getHkybje().compareTo(UFDouble.ZERO_DBL)!=0){//还款金额大于0
				Collection<BxcontrastVO> constrasts = new BaseDAO().retrieveByClause(BxcontrastVO.class, BxcontrastVO.PK_BXD+"='"+head.getPrimaryKey()+"'");
				for(BxcontrastVO constrst:constrasts){
					String jkPayOrg = constrst.getPk_payorg();
					if(!jkPayOrg.equals(head.getPk_payorg())){
						//还款金额
						UFDouble ybje = constrst.getYbje().sub(constrst.getFyybje());
						UFDouble bbje = constrst.getBbje().sub(constrst.getFybbje());
						UFDouble globalbbje = constrst.getGlobalbbje().sub(constrst.getGlobalfybbje());
						UFDouble groupbbje = constrst.getGroupbbje().sub(constrst.getGroupfybbje());

						if(ybje.compareTo(new UFDouble(0))!=0){
							// 报销单总还款金额不等于0，且借款支付单位不等于报销单位，
							// 且冲销行冲借款原币不等于费用原币金额(还款金额不等于0)（冲销行的费用原币 = 冲借款原币 - 还款原币）
							// 生成两个主组织之间的往来，往来金额为还款金额
							//借款单收钱、报销单支付单位付钱  借款单支付单位生成应收单
							pubTransInfo(map,new TransFerInfo(null,jkPayOrg,head.getPk_payorg(),ybje,bbje,globalbbje,groupbbje,vo,1));
							//借款单收钱、报销单支付单位付钱  报销单支付单位生成应付单
							pubTransInfo(map,new TransFerInfo(head.getPk_payorg_v(),head.getPk_payorg(),jkPayOrg,ybje,bbje,globalbbje,groupbbje,vo,2));
						}
					}
					
					
				}
			}
			
			if(head.getCjkybje().sub(head.getHkybje()).compareTo(UFDouble.ZERO_DBL)!=0){
				Collection<BxcontrastVO> constrasts = new BaseDAO().retrieveByClause(BxcontrastVO.class, BxcontrastVO.PK_BXD+"='"+head.getPrimaryKey()+"'");
				for(BxcontrastVO constrst:constrasts){
					String jkPayOrg = constrst.getPk_payorg();
					if(!jkPayOrg.equals(head.getPk_org())){
						UFDouble ybje = constrst.getFyybje();
						UFDouble bbje = constrst.getFybbje();
						UFDouble globalbbje = constrst.getGlobalfybbje();
						UFDouble groupbbje = constrst.getGroupfybbje();
						if(ybje.compareTo(new UFDouble(0))!=0){
							// 冲借款金额不等于还款金额（及费用承担金额不等于0），且借款单位不等于报销单的支付单位
							// 生成借款单主组织与报销单支付单位的之间的往来，往来金额为费用承担金额fyybje
							//借款单收钱、报销单位付钱  借款单支付单位生成应收单
							pubTransInfo(map,new TransFerInfo(null,jkPayOrg,head.getPk_org(),ybje,bbje,globalbbje,groupbbje,vo,1));
							//借款单收钱、报销单位付钱  借款单报销单位生成应付单
							pubTransInfo(map,new TransFerInfo(head.getPk_org_v(),head.getPk_org(),jkPayOrg,ybje,bbje,globalbbje,groupbbje, vo,2));
						}
					}
				}
			}

			for(TransFerInfo info:map.values()){
				doTransfer(info);
			}

		} catch (Exception ex) {
			throw ExceptionHandler.handleException(ex);
		}
	}

	private void doTransfer(TransFerInfo info) throws Exception {
		String dwbm=info.getFromCorp();
		//v6.1新增
		String dwbm_v = info.getFromCorp_v();
		String innerCorp=info.getToCorp();
		UFDouble ybje=info.getYb();
		UFDouble bbje=info.getBb();
		UFDouble globalbbje=info.getGlobalbb();
		UFDouble groupbbje=info.getGroupbb();

		JKBXVO vo=(JKBXVO) info.getVo().clone();
		int type=info.getType();

//		if(!BXUtil.isProductInstalled(dwbm,type==1?"AR":"AP")){
//			return;
//		}

		String[] trans = getTargetTranstypeBusitype(dwbm, type);

		String ysdjlx=trans[0];
		
		JKBXHeaderVO head=(JKBXHeaderVO) vo.getParentVO();
		String billCurrPk = Currency.getOrgLocalCurrPK(head.getPk_org());
		String arapCurrPk = Currency.getOrgLocalCurrPK(info.getFromCorp());
		
		if(billCurrPk != null && arapCurrPk != null && !billCurrPk.equals(arapCurrPk)){
			head.setBbhl(null);
			head.setGroupbbhl(null);
			head.setGlobalbbhl(null);
			head.setBbje(null);
			head.setGlobalbbje(null);
			head.setGroupbbje(null);
		}else{
			head.setBbje(bbje);
			head.setGlobalbbje(globalbbje);
			head.setGroupbbje(groupbbje);
		}
		head.setPk_org(dwbm);
		head.setYbje(ybje);
		vo.setParentVO(head);
		vo.setChildrenVO(new BXBusItemVO[]{new BXBusItemVO()});

		//处理收付VO

		AggPayableBillVO payablebill =null;
		AggReceivableBillVO recbill = null;
		String srcTranstype = BXConstans.BX_DJDL.equals(head.getDjdl())?BXConstans.BX_DJLXBM:BXConstans.JK_DJLXBM;
		if(ysdjlx==null){
			return;
		}else if(AP.equals(ysdjlx)){
			try{
				payablebill = (AggPayableBillVO) PfUtilTools.runChangeData(srcTranstype,ysdjlx,vo);
			}catch (BusinessException e) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0068")/*@res "报销单到应付单字段交换出现错误,"*/+e.getMessage());
			}
			dealDjzbvo(dwbm_v,dwbm, innerCorp, head,null, payablebill,payablebill.getParentVO().getAttributeValue("billclass").toString());
			payablebill.getParentVO().setStatus(VOStatus.NEW);
			//v6.1新增
			payablebill.getParentVO().setAttributeValue("pk_org_v", dwbm_v);
			payablebill.getParentVO().setAttributeValue("billstatus", -99);
			CircularlyAccessibleValueObject[] childrenVO = payablebill.getChildrenVO();
			for(CircularlyAccessibleValueObject s:childrenVO){
				s.setStatus(VOStatus.NEW);
			}
			NCLocator.getInstance().lookup(IArapPayableBillPubService.class).saveTemp(new AggPayableBillVO[]{payablebill});

		} else if(AR.equals(ysdjlx)){
			try{
				recbill = (AggReceivableBillVO) PfUtilTools.runChangeData(srcTranstype,ysdjlx,vo);
			}catch (BusinessException e) {
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0","02011002-0069")/*@res "报销单到应收单字段交换出现错误,"*/+e.getMessage());
			}
			dealDjzbvo(dwbm_v,dwbm, innerCorp, head, recbill,null,recbill.getParentVO().getAttributeValue("billclass").toString());
			recbill.getParentVO().setStatus(VOStatus.NEW);
			//v6.1新增
			recbill.getParentVO().setAttributeValue("pk_org_v", dwbm_v);
			recbill.getParentVO().setAttributeValue("billstatus", -99);
			CircularlyAccessibleValueObject[] childrenVO = recbill.getChildrenVO();
			for(CircularlyAccessibleValueObject s:childrenVO){
				s.setStatus(VOStatus.NEW);
			}
			NCLocator.getInstance().lookup(IArapReceivableBillPubService.class).saveTemp(new AggReceivableBillVO[]{recbill});

		}

		JsConstrasVO jsConstrasVO = new JsConstrasVO();
		jsConstrasVO.setJsh(head.getJsh());

		if(AP.equals(ysdjlx)){
			jsConstrasVO.setPk_bxd(head.getPk_jkbx());
			jsConstrasVO.setPk_jsd(payablebill.getParentVO().getPrimaryKey());
			jsConstrasVO.setPk_org(payablebill.getParentVO().getAttributeValue("pk_org").toString());
			jsConstrasVO.setBillflag(1);
		} else if(AR.equals(ysdjlx)){
			jsConstrasVO.setPk_bxd(head.getPk_jkbx());
			jsConstrasVO.setPk_jsd(recbill.getParent().getPrimaryKey());
			jsConstrasVO.setPk_org(recbill.getParentVO().getAttributeValue("pk_org").toString());
			jsConstrasVO.setBillflag(0);
		}else{

		}
		//FIXME 20110128注销，记得找结算信息处理
		jsConstrasVO.setJshpk(getJshpk(head));

		new BaseDAO().insertVO(jsConstrasVO);
	}

	private String[] getTargetTranstypeBusitype(String dwbm, int type)
			throws BusinessException {
		if(type==1)
			return new String[]{AR,""};
		else
			return new String[]{AP,""};
	}

	private void dealDjzbvo(String dwbm_v,String dwbm, String innerCorp, JKBXHeaderVO head, AggReceivableBillVO recbill,AggPayableBillVO payablebill, String busitype) throws BusinessException {
		//设置内部客商
		ICustsupPubService docqry = NCLocator.getInstance().lookup(ICustsupPubService.class);

		PayableBillItemVO[] payableitems = null;
		ReceivableBillItemVO[] recivableitems = null;
		if(busitype==null){
			return ;

		}else if(busitype.equalsIgnoreCase("yf")){
			payableitems = (PayableBillItemVO[]) payablebill.getChildrenVO();
			String custsup = docqry.queryCustsupPkByOrgPk(innerCorp,true);
			for(PayableBillItemVO item:payableitems){
				item.setSupplier(custsup);
				item.setPk_org(dwbm);
				//v6.1新增
				item.setPk_org_v(dwbm_v);
				item.setConfernum(head.getDjbh());
				item.setPk_group(head.getPk_group());
				break;
			}
		}else if(busitype.equalsIgnoreCase("ys")){
			recivableitems = (ReceivableBillItemVO[]) recbill.getChildrenVO();
			String custsup = docqry.queryCustsupPkByOrgPk(innerCorp,false);
			for(ReceivableBillItemVO item:recivableitems){
				item.setCustomer(custsup);
				item.setPk_org(dwbm);
				//v6.1新增
				item.setPk_org_v(dwbm_v);
				item.setConfernum(head.getDjbh());
				item.setPk_group(head.getPk_group());
				break;
			}
		}
	}

	/**
	 * FIXME 目前暂时用0001代替,更换正对应的财务组织
	 * @param headerVO
	 * @return 取结算号的唯一pk
	 */
	public String getJshpk(JKBXHeaderVO headerVO) {
		//FIXME 暂时使用
		return OidGenerator.getInstance().nextOid("0001");
	}

	static class TransFerInfo{
		private String fromCorp;
		//v6.1新增
		private String fromCorp_v;
		private String toCorp;
		private UFDouble yb;
		private UFDouble bb;
		private UFDouble globalbb;
		private UFDouble groupbb;
		private int type;// 1 表示 应收 AR； 2表示 应付AP
		private JKBXVO vo;
		public TransFerInfo(String fromCorp_v,String fromCorp, String toCorp, UFDouble zfybje, UFDouble zfbbje, UFDouble globalzfbbje,UFDouble groupzfbbje, JKBXVO vo, int busitype) {
			this.fromCorp = fromCorp;
			this.fromCorp_v = fromCorp_v;
			this.toCorp=toCorp;
			this.yb=zfybje;
			this.bb=zfbbje;
			this.globalbb=globalzfbbje;
			this.groupbb =groupzfbbje;
			this.vo=vo;
			this.type=busitype;
		}
		//begin--v6.1新增
		public void setFromCorp_v(String fromCorpV) {
			fromCorp_v = fromCorpV;
		}
		
		public String getFromCorp_v() {
			return fromCorp_v;
		}
		//--end
		public UFDouble getBb() {
			return bb;
		}
		public void setBb(UFDouble bb) {
			this.bb = bb;
		}
		public UFDouble getGlobalbb() {
			return globalbb;
		}
		public void setGlobalbb(UFDouble globalbb) {
			this.globalbb = globalbb;
		}
		public UFDouble getGroupbb() {
			return groupbb;
		}
		public void setGroupbb(UFDouble groupbb) {
			this.groupbb = groupbb;
		}
		public String getFromCorp() {
			return fromCorp;
		}
		public void setFromCorp(String fromCorp) {
			this.fromCorp = fromCorp;
		}
		public String getToCorp() {
			return toCorp;
		}
		public void setToCorp(String toCorp) {
			this.toCorp = toCorp;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public UFDouble getYb() {
			return yb;
		}
		public void setYb(UFDouble yb) {
			this.yb = yb;
		}
		public JKBXVO getVo() {
			return vo;
		}
		public void setVo(JKBXVO vo) {
			this.vo = vo;
		}
	}
}