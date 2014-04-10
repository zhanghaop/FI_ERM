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
 * ����������Ԥ��ҵ����
 * 
 * @author lvhj
 *
 */
public class BxVerifyAccruedBillBO {
	
	/**
	 * ���ݱ��������������Ԥ����ϸ��Ч
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void effectAccruedVerifyVOs(JKBXVO... bxvos) throws BusinessException{
		// ����Ԥ�ᵥ����������б���
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		service.effectAccruedVerifyVOs(bxvos);
	}
	
	
	/**
	 * ���ݱ��������������Ԥ����ϸȡ����Ч
	 * 
	 * @param bxvos
	 * @throws BusinessException
	 */
	public void uneffectAccruedVerifyVOs(JKBXVO... bxvos) throws BusinessException{
		// ����Ԥ�ᵥ����������б���
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		service.uneffectAccruedVerifyVOs(bxvos);
	}
	
	
	/**
	 * ���汨��������Ԥ����ϸ
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
		// ��ѯ������ԭ������ϸ��������
		Collection<AccruedVerifyVO> oldAccVerifyVOs = dao.retrieveByClause(AccruedVerifyVO.class, SqlUtils.getInStr(
				AccruedVerifyVO.PK_BXD, bxpks, true));
		Map<String, AccruedVerifyVO> oldmap = new HashMap<String, AccruedVerifyVO>();
		for (AccruedVerifyVO vo : oldAccVerifyVOs) {
			if (oldmap.get(vo.getPk_accrued_verify()) == null) {
				oldmap.put(vo.getPk_accrued_verify(), vo);
			}
		}
		List<AccruedVerifyVO> list = new ArrayList<AccruedVerifyVO>();
		List<String> temping = new ArrayList<String>();// �ݴ�
		List<String> temped = new ArrayList<String>();// �ݴ浽����
		List<String> other = new ArrayList<String>();// ��������
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
						// ����Ǵ��ݴ�-���棬����Ҫɾ���ݴ�ʱ�����ĺ�����ϸ
						if (bxvo.getAccruedVerifyVO() != null && bxvo.getAccruedVerifyVO().length > 0) {
							for (AccruedVerifyVO vo : bxvo.getAccruedVerifyVO()) {
								delvos.add(vo);
							}
						}
					} else {
						if (!bxvo.isVerifyAccruedUpdate()) {
							// ֻ�����Ǹ��º�����ϸ�ı�����
							continue;
						}
						other.add(bxvo.getParentVO().getPk_jkbx());
					}
				}
			}else{
				if (!bxvo.isVerifyAccruedUpdate()) {
					// ֻ�����Ǹ��º�����ϸ�ı�����
					continue;
				}
				other.add(bxvo.getParentVO().getPk_jkbx());
			}
			AccruedVerifyVO[] vos = prepare(bxvo);
			if (vos.length > 0) {
				list.addAll(Arrays.asList(vos));
			}
		}
		// ����Ԥ�ᵥ����������б���
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		// �ݴ�
		service.tempVerifyAccruedVOs(list.toArray(new AccruedVerifyVO[list.size()]), temping.toArray(new String[0]));
		// �ݴ浽����
		dao.deleteVOList(delvos);
		service.verifyAccruedVOs(list.toArray(new AccruedVerifyVO[list.size()]), temped.toArray(new String[0]));
		// ����
		service.verifyAccruedVOs(list.toArray(new AccruedVerifyVO[list.size()]), other.toArray(new String[0]));
	}
	
	/**
	 * ���ݱ�����pksɾ������Ԥ����ϸ
	 * 
	 * @param bxdpks
	 * @throws BusinessException
	 */
	public void deleteByBxdPks(String... bxdpks) throws BusinessException{
		// ����Ԥ�ᵥ����������б���
		IErmAccruedBillVerifyService service = NCLocator.getInstance().lookup(IErmAccruedBillVerifyService.class);
		service.verifyAccruedVOs(null, bxdpks);
	}
	
	/**
	 * ���˲���Ҫ���������ϸ�ı���������Ϊ��Ҫ����ĺ�����ϸ�����������������
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
			// ������������ݱ��
			cvo.setPk_bxd(pk_bxd);
			cvo.setBxd_billno(parentVO.getDjbh());
//			try{
//				// ���㡢���ñ�������Ԥ����ı�λ��
//				UFDouble verify_amount = cvo.getVerify_amount();
//				UFDouble cjkbbje = Currency.computeYFB(zfdwbm,Currency.Change_YBJE, bzbm, verify_amount, null, null, null, bbhl, djrq)[2];
//				cvo.setOrg_verify_amount(cjkbbje);
//				UFDouble[] money = Currency.computeGroupGlobalAmount(verify_amount, cjkbbje, bzbm, djrq, cvo.getPk_org(), pk_group, globalbbhl, groupbbhl);
//				cvo.setGroup_verify_amount(money[0]);
//				cvo.setGlobal_verify_amount(money[1]);
//			}catch (Exception e) {
//				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000387")/*@res "���ջ��ʼ�����ʧ��!"*/);
//			}
		}
		return accruedVerifyVOs;
	}
}