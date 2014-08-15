package nc.bs.pub.action;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.pub.compiler.AbstractCompiler2;
import nc.pubitf.para.SysInitQuery;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXParamConstant;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.erm.common.MessageVO;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.change.PublicHeadVO;
import nc.vo.pub.compiler.PfParameterVO;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.pf.workflow.IPFActionName;
import nc.vo.uap.pf.PFBusinessException;
import nc.vo.wfengine.core.data.DataField;

/**
 * ��ע������������� ���ݶ���ִ���еĶ�ִ̬����Ķ�ִ̬���ࡣ
 * 
 * �������ڣ�(2007-7-9)
 * 
 * @author ƽ̨�ű�����
 */
public class N_264X_APPROVE extends AbstractCompiler2 {

	public N_264X_APPROVE() {
		super();
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
			// ##################################################
			// ####�����Ϊ����������������ʼ...���ܽ����޸�####
			
			List<JKBXVO> auditVOs=new ArrayList<JKBXVO>();
			List<MessageVO> fMsgs=new ArrayList<MessageVO>();
			
			BXVO bxvo = (BXVO) vo.m_preValueVo;
			
//begin--added by chendya@ufida.com.cn ��˲���Ȩ��	
			//�Ƿ��ڳ�����
			boolean isQc = bxvo.getParentVO().getQcbz().booleanValue();
			//�Ƿ��õ���
			boolean isInit = bxvo.getParentVO().isInit();
			//�Ƿ��NC�ͻ���
			boolean isNCClient=bxvo.isNCClient();
			
			//���ڳ������õ���У������Ȩ��
			if(isNCClient){
				if(!isQc&&!isInit){
					BXDataPermissionChkUtil.process(bxvo, BXConstans.ERMEXPRESOURCECODE, BXConstans.EXPAPPROVECODE,vo.m_operator);
				}
			}
//--end			
			Object bflag = procActionFlow(vo);
			
			if (bflag == null) {
				String paraString = SysInitQuery.getParaString(bxvo.getParentVO().getPk_org(), BXParamConstant.ER_FLOW_TYPE);
				if (paraString == null || BXParamConstant.ER_FLOW_TYPE_APPROVEFLOW.equals(paraString) || isWorkFlowFinalNode(vo)) {
					auditVOs.add(bxvo);
				} else {
					fMsgs.add(new MessageVO(bxvo, ActionUtils.AUDIT));
				}
			} else {
				fMsgs.add(new MessageVO(bxvo, ActionUtils.AUDIT));
			}

			setParameter("billVO", auditVOs.toArray(new JKBXVO[] {}));
			
			retObj = runClass("nc.bs.arap.bx.BXZbBO", "audit", "&billVO:nc.vo.ep.bx.JKBXVO[]", vo, m_keyHas);

			if (retObj != null){
				
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
	
	/**
	 * �Ƿ�������󻷽�
	 * @param vo
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private boolean isWorkFlowFinalNode(PfParameterVO vo) {
		if(vo.m_workFlow == null){
			return false;
		}
		
		List argsList = vo.m_workFlow.getApplicationArgs();
		if (argsList != null && argsList.size() > 0) {
			for (Iterator iterator = argsList.iterator(); iterator.hasNext();) {
				DataField df = (DataField) iterator.next();
				Object value = df.getInitialValue();
				if (value == null) {
					continue;
				}
				if ("isWorkFlowFinalNode".equals(df.getName()) && UFBoolean.valueOf(value.toString()).booleanValue()) {
					return true;
				}
			}
		}
		return false;
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
