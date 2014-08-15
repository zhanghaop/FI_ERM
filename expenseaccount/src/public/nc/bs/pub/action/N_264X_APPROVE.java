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
 * 备注：报销单的审核 单据动作执行中的动态执行类的动态执行类。
 * 
 * 创建日期：(2007-7-9)
 * 
 * @author 平台脚本生成
 */
public class N_264X_APPROVE extends AbstractCompiler2 {

	public N_264X_APPROVE() {
		super();
		m_keyHas = null;
	}

	/*
	 * 备注：平台编写规则类 接口执行类
	 */
	@Override
	public Object runComClass(PfParameterVO vo) throws BusinessException {
		try {
			super.m_tmpVo = vo;
			// ####本脚本必须含有返回值,返回DLG和PNL的组件不允许有返回值####
			Object retObj = null;
			// ####重要说明：生成的业务组件方法尽量不要进行修改####
			// ####该组件为单动作工作流处理结束...不能进行修改####
			// 方法说明:null
			// ##################################################
			// ####该组件为单动作工作流处理开始...不能进行修改####
			
			List<JKBXVO> auditVOs=new ArrayList<JKBXVO>();
			List<MessageVO> fMsgs=new ArrayList<MessageVO>();
			
			BXVO bxvo = (BXVO) vo.m_preValueVo;
			
//begin--added by chendya@ufida.com.cn 审核操作权限	
			//是否期初单据
			boolean isQc = bxvo.getParentVO().getQcbz().booleanValue();
			//是否常用单据
			boolean isInit = bxvo.getParentVO().isInit();
			//是否从NC客户端
			boolean isNCClient=bxvo.isNCClient();
			
			//非期初、常用单据校验数据权限
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
	 * 是否流程最后环节
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
	 * 备注：设置脚本变量的HAS
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
