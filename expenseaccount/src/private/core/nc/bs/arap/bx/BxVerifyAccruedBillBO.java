package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import nc.bs.dao.BaseDAO;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.erm.accruedexpense.IErmAccruedBillVerifyService;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.accruedexpense.AccruedVerifyVO;
import nc.vo.erm.util.VOUtils;
import nc.vo.fi.pub.SqlUtils;
import nc.vo.pub.BusinessException;

/**
 * 报销单核销预提业务类
 * 
 * @author lvhj
 *
 */
public class BxVerifyAccruedBillBO {
	
	/**
	 * 根据报销单，处理核销预提明细生效
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void effectAccruedVerifyVOs(JKBXVO... bxvos) throws BusinessException{
		// 调用预提单核销服务进行保存
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		service.effectAccruedVerifyVOs(bxvos);
	}
	
	
	/**
	 * 根据报销单，处理核销预提明细取消生效
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void uneffectAccruedVerifyVOs(JKBXVO... bxvos) throws BusinessException{
		// 调用预提单核销服务进行保存
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		service.uneffectAccruedVerifyVOs(bxvos);
	}
	
	
	/**
	 * 保存报销单核销预提明细
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	public void saveVerifyVOs(JKBXVO... bxvos) throws BusinessException {
		if (bxvos == null || bxvos.length == 0 ) {
			return;
		}
		BaseDAO dao = new BaseDAO();
		String[] bxpks = VOUtils.getAttributeValues(bxvos, JKBXHeaderVO.PK_JKBX);
		// 查询报销单原核销明细，并分组
		Collection<AccruedVerifyVO> oldAccVerifyVOs = dao.retrieveByClause(AccruedVerifyVO.class, SqlUtils.getInStr(
				AccruedVerifyVO.PK_BXD, bxpks, true));
		Map<String, AccruedVerifyVO> oldmap = new HashMap<String, AccruedVerifyVO>();
		for (AccruedVerifyVO vo : oldAccVerifyVOs) {
			if (oldmap.get(vo.getPk_accrued_verify()) == null) {
				oldmap.put(vo.getPk_accrued_verify(), vo);
			}
		}
		List<AccruedVerifyVO> list = new ArrayList<AccruedVerifyVO>();
		List<String> temping = new ArrayList<String>();// 暂存
		List<String> temped = new ArrayList<String>();// 暂存到保存
		List<String> other = new ArrayList<String>();// 其他单据
		List<AccruedVerifyVO> delvos = new ArrayList<AccruedVerifyVO>();
		for (int i = 0; i < bxvos.length; i++) {
			JKBXVO bxvo = bxvos[i];
			if (bxvo.getAccruedVerifyVO() != null && bxvo.getAccruedVerifyVO().length > 0) {
				if (bxvo.getParentVO().getDjzt() != null
						&& bxvo.getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
					temping.add(bxvo.getParentVO().getPrimaryKey());
				} else if (bxvo.getParentVO().getDjzt() != null
						&& bxvo.getParentVO().getDjzt() == BXStatusConst.DJZT_Saved) {
					if (oldmap.get(bxvo.getAccruedVerifyVO()[0].getPk_accrued_verify()) != null
							&& oldmap.get(bxvo.getAccruedVerifyVO()[0].getPk_accrued_verify()).getEffectstatus() == BXStatusConst.SXBZ_TEMP) {
						temped.add(bxvo.getParentVO().getPk_jkbx());
						// 如果是从暂存-保存，则需要删除暂存时保留的核销明细
						if (bxvo.getAccruedVerifyVO() != null && bxvo.getAccruedVerifyVO().length > 0) {
							for (AccruedVerifyVO vo : bxvo.getAccruedVerifyVO()) {
								delvos.add(vo);
							}
						}
					} else {
						if (!bxvo.isVerifyAccruedUpdate()) {
							// 只处理标记更新核销明细的报销单
							continue;
						}
						other.add(bxvo.getParentVO().getPk_jkbx());
					}
				}
			}else{
				if (!bxvo.isVerifyAccruedUpdate()) {
					// 只处理标记更新核销明细的报销单
					continue;
				}
				other.add(bxvo.getParentVO().getPk_jkbx());
			}
			AccruedVerifyVO[] vos = prepare(bxvo);
			if (vos.length > 0) {
				list.addAll(Arrays.asList(vos));
			}
		}
		// 调用预提单核销服务进行保存
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		// 暂存
		service.tempVerifyAccruedVOs(list.toArray(new AccruedVerifyVO[list.size()]), temping.toArray(new String[0]));
		// 暂存到保存
		dao.deleteVOList(delvos);
		service.verifyAccruedVOs(list.toArray(new AccruedVerifyVO[list.size()]), temped.toArray(new String[0]));
		// 其他
		service.verifyAccruedVOs(list.toArray(new AccruedVerifyVO[list.size()]), other.toArray(new String[0]));
	}
	
	/**
	 * 根据报销单pks删除核销预提明细
	 * 
	 * @param bxdpks
	 * @throws BusinessException
	 */
	public void deleteByBxdPks(String... bxdpks) throws BusinessException{
		// 调用预提单核销服务进行保存
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		service.verifyAccruedVOs(null, bxdpks);
	}
	
	/**
	 * 过滤不需要处理核销明细的报销单，且为需要处理的核销明细补充外键、币种折算
	 * 
	 * @param vo
	 */
	private AccruedVerifyVO[] prepare(JKBXVO vo) {
		
		JKBXHeaderVO parentVO = vo.getParentVO();
		AccruedVerifyVO[] accruedVerifyVOs = vo.getAccruedVerifyVO();
		if(accruedVerifyVOs==null || accruedVerifyVOs.length==0 )
			return new AccruedVerifyVO[0];		
//		UFDouble bbhl = parentVO.getBbhl();
//		UFDouble globalbbhl = parentVO.getGlobalbbhl();
//		UFDouble groupbbhl = parentVO.getGroupbbhl();
//		String bzbm = parentVO.getBzbm();
//		UFDate djrq = parentVO.getDjrq();
//		String zfdwbm = parentVO.getPk_org();
//		String pk_group = parentVO.getPk_group();

		String pk_bxd = parentVO.getPk_jkbx();
		
		for(AccruedVerifyVO cvo:accruedVerifyVOs){
			// 补充外键、单据编号
			cvo.setPk_bxd(pk_bxd);
			cvo.setBxd_billno(parentVO.getDjbh());
//			try{
//				// 计算、设置报销核销预提金额的本位币
//				UFDouble verify_amount = cvo.getVerify_amount();
//				UFDouble cjkbbje = Currency.computeYFB(zfdwbm,Currency.Change_YBJE, bzbm, verify_amount, null, null, null, bbhl, djrq)[2];
//				cvo.setOrg_verify_amount(cjkbbje);
//				UFDouble[] money = Currency.computeGroupGlobalAmount(verify_amount, cjkbbje, bzbm, djrq, cvo.getPk_org(), pk_group, globalbbhl, groupbbhl);
//				cvo.setGroup_verify_amount(money[0]);
//				cvo.setGlobal_verify_amount(money[1]);
//			}catch (Exception e) {
//				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000387")/*@res "按照汇率计算金额失败!"*/);
//			}
		}
		return accruedVerifyVOs;
	}
}