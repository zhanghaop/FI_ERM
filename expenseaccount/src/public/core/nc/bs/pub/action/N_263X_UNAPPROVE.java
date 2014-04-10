package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.change.PublicHeadVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.uap.pf.PFBusinessException;

/**
 * ��ע��������� ���ݶ���ִ���еĶ�ִ̬����Ķ�ִ̬���ࡣ
 * 
 * �������ڣ�(2007-7-9)
 * 
 * @author ƽ̨�ű�����
 */
public class N_263X_UNAPPROVE extends AbstractCompiler2 {


	public N_263X_UNAPPROVE() {
		super();
//		m_methodReturnHas = new Hashtable<String, Object>();
		m_keyHas = null;
	}

	/*
	 * ��ע��ƽ̨��д������ �ӿ�ִ����
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			super.m_tmpVo = vo;
			// ####���ű����뺬�з���ֵ,����DLG��PNL������������з���ֵ####
			Object retObj = null;
			// ####��Ҫ˵�������ɵ�ҵ���������������Ҫ�����޸�####
			// ####�����Ϊ�������������������...���ܽ����޸�####
			// ����˵��:null
			

			List<JKBXVO> auditVOs=new ArrayList<JKBXVO>();
			
			List<MessageVO> fMsgs=new ArrayList<MessageVO>();
			
			JKVO bxvo = (JKVO) vo.m_preValueVo;
			
//begin--added by chendya@ufida.com.cn ������˲���Ȩ��
			
			//�Ƿ��ڳ�����
			boolean isQc = bxvo.getParentVO().getQcbz().booleanValue();
			
			//�Ƿ��õ���
			boolean isInit = bxvo.getParentVO().isInit();
			//�Ƿ��NC�ͻ���
			boolean isNCClient=bxvo.isNCClient();
			
			//���ڳ������õ���У������Ȩ��
			if(isNCClient){
				if(!isQc&&!isInit){
					BXDataPermissionChkUtil.process(bxvo, BXConstans.ERMLOANRESOURCECODE, BXConstans.LOANUNAPPROVECODE,vo.m_operator);
				}
			}
//--end				
			
			boolean bflag = procUnApproveFlow(vo);
			
			if (bflag) {
				auditVOs.add(bxvo);
			}else{
				fMsgs.add(new MessageVO(bxvo,ActionUtils.UNAUDIT));
			}
			
			setParameter("billVO", auditVOs.toArray(new JKBXVO[] {}));

			retObj = runClass("nc.bs.arap.bx.BXZbBO", "unAudit", "&billVO:nc.vo.ep.bx.JKBXVO[]", vo, m_keyHas);

			if (retObj != null){
//				m_methodReturnHas.put("unAuditBill", retObj);
			
				MessageVO[] msgs=(MessageVO[]) retObj;
				
				for (int i = 0; i < msgs.length; i++) {
					fMsgs.add(msgs[i]);
				}
			}
			
			return fMsgs.toArray(new MessageVO[]{});
			
		} catch (Exception ex) {
			if (ex instanceof BusinessException)
				throw (BusinessException) ex;
			else
				throw new PFBusinessException(ex.getMessage(), ex);
		}
	}


	@Override
	public String getCodeRemark() {
		return "erm action script not allowed modify, all rights reserved!;";
	}

	/*
	 * ��ע�����ýű�������HAS
	 */
	protected void setParameter(String key, Object val) {
		if (m_keyHas == null) {
			m_keyHas = new Hashtable<String, Object>();
		}
		if (val != null) {
			m_keyHas.put(key, val);
		}
	}
	
//	protected java.util.Hashtable<String, Object> m_methodReturnHas = new java.util.Hashtable<String, Object>();

	protected Hashtable<String, Object> m_keyHas = null;
	

	protected void getPfVO(PfParameterVO paraVo, JKBXVO bxvo) {
		
		PublicHeadVO standHeadVo = new PublicHeadVO();
		
		getHeadInfo(standHeadVo, bxvo, bxvo.getParentVO().getDjlxbm());

		paraVo.m_billType=standHeadVo.billType;
		paraVo.m_billNo = standHeadVo.billNo;
		paraVo.m_billId = standHeadVo.pkBillId;
		paraVo.m_pkOrg = standHeadVo.pkOrg;
		paraVo.m_makeBillOperator = standHeadVo.operatorId;
		paraVo.m_preValueVo = bxvo;
		paraVo.m_preValueVos = new JKBXVO[]{bxvo};
		paraVo.m_standHeadVo = standHeadVo;
	}
	
	private void getHeadInfo(nc.vo.pub.change.PublicHeadVO headvo, AggregatedValueObject vo,
			String billtype) {
		headvo.billType = billtype;
	}
}
