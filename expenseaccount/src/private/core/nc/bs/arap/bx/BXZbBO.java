package nc.bs.arap.bx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import nc.bs.businessevent.EventDispatcher;
import nc.bs.dao.BaseDAO;
import nc.bs.dao.DAOException;
import nc.bs.er.callouter.FipCallFacade;
import nc.bs.er.settle.ErForCmpBO;
import nc.bs.er.util.BXDataPermissionChkUtil;
import nc.bs.er.util.SqlUtils;
import nc.bs.erm.costshare.IErmCostShareConst;
import nc.bs.erm.event.ErmBusinessEvent;
import nc.bs.erm.event.ErmEventType;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.common.NCLocator;
import nc.bs.logging.Log;
import nc.bs.logging.Logger;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.arap.prv.IBXBillPrivate;
import nc.itf.arap.pub.ISqdlrKeyword;
import nc.itf.fi.pub.Currency;
import nc.itf.fipub.summary.ISummaryQueryService;
import nc.jdbc.framework.ConnectionFactory;
import nc.jdbc.framework.JdbcSession;
import nc.jdbc.framework.PersistenceManager;
import nc.jdbc.framework.SQLParameter;
import nc.jdbc.framework.crossdb.CrossDBConnection;
import nc.jdbc.framework.exception.DbException;
import nc.jdbc.framework.util.SQLHelper;
import nc.pubitf.arap.payable.IArapPayableBillPubQueryService;
import nc.pubitf.arap.payable.IArapPayableBillPubService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubQueryService;
import nc.pubitf.arap.receivable.IArapReceivableBillPubService;
import nc.pubitf.erm.costshare.IErmCostShareBillQuery;
import nc.pubitf.org.IOrgUnitPubService;
import nc.vo.arap.basebill.BaseBillVO;
import nc.vo.arap.billstatus.ARAPBillStatus;
import nc.vo.arap.bx.util.ActionUtils;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BXUtil;
import nc.vo.arap.payable.AggPayableBillVO;
import nc.vo.arap.receivable.AggReceivableBillVO;
import nc.vo.cmp.BusiStatus;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BXVO;
import nc.vo.ep.bx.BusiTypeVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JsConstrasVO;
import nc.vo.ep.bx.MtappfUtil;
import nc.vo.ep.bx.SqdlrVO;
import nc.vo.ep.bx.VOFactory;
import nc.vo.ep.dj.DjCondVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.check.VOStatusChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.er.settle.SettleUtil;
import nc.vo.er.util.StringUtils;
import nc.vo.erm.common.MessageVO;
import nc.vo.erm.costshare.CShareDetailVO;
import nc.vo.erm.costshare.CostShareVO;
import nc.vo.erm.matterappctrl.MtapppfVO;
import nc.vo.erm.termendtransact.DataValidateException;
import nc.vo.erm.util.ErVOUtils;
import nc.vo.fip.service.FipMessageVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.fipub.billcode.FinanceBillCodeInfo;
import nc.vo.fipub.billcode.FinanceBillCodeUtils;
import nc.vo.fipub.summary.SummaryVO;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.pub.AggregatedValueObject;
import nc.vo.pub.BusinessException;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.VOStatus;
import nc.vo.pub.lang.UFBoolean;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;
import nc.vo.pub.lang.UFTime;
import nc.vo.pub.pf.IPfRetCheckInfo;
import nc.vo.uap.rbac.role.RoleVO;
import nc.vo.util.AuditInfoUtil;

/**
 * @author twei
 * 
 *         nc.bs.ep.bx.BXZbBO
 * 
 *         借款报销类单据表头业务类
 */
public class BXZbBO {
	private JKBXDAO jkbxDAO;

	/**
	 * 借款报销单删除
	 * 
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public MessageVO[] delete(JKBXVO[] vos) throws BusinessException {

		MessageVO[] msgVos = new MessageVO[vos.length];

		compareTs(vos);// ts校验（包含了主键锁）

		List<JKBXHeaderVO> deleteHeaders = new ArrayList<JKBXHeaderVO>();
		try {
			for (int i = 0; i < vos.length; i++) {
				msgVos[i] = new MessageVO(vos[i], ActionUtils.DELETE);
				JKBXHeaderVO parentVO = vos[i].getParentVO();

				JKBXVO vo = retriveItems(vos[i]);// 表体需要补充，因项目预算一些业务插件中需要
				fillUpMapf(vo);

				checkDelete(vo);// 校验删除

				beforeActInf(vo, MESSAGE_DELETE);
				// 删除业务信息
				getBxBusitemBO(parentVO.getDjlxbm(), parentVO.getDjdl()).deleteByBXVOs(new JKBXVO[] { vo });

				// 删除冲借款对照信息
				new ContrastBO().deleteByPK_bxd(new String[] { parentVO.getPk_jkbx() });
				
				//批量删除表头
				deleteHeaders.add(parentVO);

				// 暂存单据没有传结算，删除暂存的单据也应该不走结算
				boolean isTempSave = BXStatusConst.DJZT_TempSaved == parentVO.getDjzt().intValue() ? true : false;

				// 判断CMP产品是否启用
				boolean isCmpInstalled = isCmpInstall(vo.getParentVO());

				// 是否 既无收款也无付款
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo.getParentVO().getZfybje()
						.equals(new UFDouble(0)))
						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje()
								.equals(new UFDouble(0)));

				if (!isTempSave && !notExistsPayOrRecv && isCmpInstalled) {
					new ErForCmpBO().invokeCmp(vo, parentVO.getDjrq(), BusiStatus.Deleted);
				}

				afterActInf(vo, MESSAGE_DELETE);

				// 非常用单据回退单据号，常用单据不生成单据号
				if (!vo.getParentVO().isInit()) {
					returnBillCode(vo);
				}

			}

			// 删除表头
			getJKBXDAO().delete(deleteHeaders.toArray(new JKBXHeaderVO[] {}));
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return msgVos;
	}

	public IBXBusItemBO getBxBusitemBO(String djlxbm, String djdl) throws BusinessException {
		try {
			BusiTypeVO busTypeVO = BXUtil.getBusTypeVO(djlxbm, djdl);
			String busiClass = busTypeVO.getInterfaces().get(BusiTypeVO.IBXBusItemBO);
			return (IBXBusItemBO) Class.forName(busiClass).newInstance();
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	/**
	 * 特殊处理摘要
	 * 
	 * @param vo
	 */
	private void dealZyName(JKBXVO vo) throws BusinessException {
		String value = vo.getParentVO().getZy();
		if (value != null && value.length() == 20) {
			ISummaryQueryService service = NCLocator.getInstance().lookup(
					nc.itf.fipub.summary.ISummaryQueryService.class);
			SummaryVO[] summaryVOs = service.querySummaryVOByCondition(SummaryVO.PK_SUMMARY + "='" + value + "'");
			if (summaryVOs != null && summaryVOs.length > 0) {
				vo.getParentVO().setZy(summaryVOs[0].getSummaryname());
			}
		}
	}

	/**
	 * 正式保存，进行业务校验，并调用需要的接口
	 * 
	 * @param vos
	 *            要保存单据的聚合VO数组
	 */
	public JKBXVO[] save(JKBXVO[] vos) throws BusinessException {
		VOChecker checker = new VOChecker();
		for (JKBXVO vo : vos) {
			// 特殊处理摘要
			dealZyName(vo);
			//只录表头，不录表体的情况下生成表体行
			BXUtil.generateJKBXRow(vo);
			
			VOChecker.prepare(vo);
			// 保存校验
			checker.checkkSaveBackground(vo);

			// 检查单据是否关帐
			VOChecker.checkErmIsCloseAcc(vo);

			if (vo.getParentVO().getQcbz() != null && vo.getParentVO().getQcbz().booleanValue()) {
				// 期初单据保存后自动审核通过
				vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
				vo.getParentVO().setShrq(new UFDateTime(vo.getParentVO().getDjrq(), new UFTime("00:00:00")));

				vo.getParentVO().setJsrq(vo.getParentVO().getDjrq());
				vo.getParentVO().setApprover(InvocationInfoProxy.getInstance().getUserId());
				vo.getParentVO().setJsr(InvocationInfoProxy.getInstance().getUserId());

			} else {
				// 保存后自动提交
				vo.getParentVO().setSpzt(IPfRetCheckInfo.COMMIT);
			}

			// 去服务器事件作为创建时间
			AuditInfoUtil.addData(vo.getParentVO());
			
			fillUpMapf(vo);
		}

		beforeActInf(vos, MESSAGE_SAVE);

		// 保存单据
		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		try {
			// 调用现金流平台结算
			ErForCmpBO erBO = new ErForCmpBO();

			for (JKBXVO vo : vos) {

				BusiStatus billStatus = SettleUtil.getBillStatus(vo.getParentVO(), false);

				// 是否安装结算
				boolean isInstallCmp = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(),
						BXConstans.TM_CMP_FUNCODE);

				// 是否 既无收款也无付款
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo.getParentVO().getZfybje()
						.equals(new UFDouble(0)))
						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje()
								.equals(new UFDouble(0)));

				if (isInstallCmp && !notExistsPayOrRecv) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_SAVE);

		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		return bxvos;
	}

	private void checkForInitBill(JKBXVO[] vos) throws BusinessException {
		for (JKBXVO vo : vos) {
			if (vo.getParentVO().isInit()) {
				JKBXHeaderVO header = vo.getParentVO();
				String djlxbm = header.getDjlxbm();
				String pk_org = header.getPk_org();
				String pk_group = header.getPk_group();
				String pk = "ER_JKBXINIT" + djlxbm + pk_org;
				String user = KeyLock.dynamicLock(pk);

				if (user != null) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0060")/*
											 * @res
											 * "并发异常，保存常用单据失败，该单据类型的常用单据在当前单位已经存在！"
											 */);
				}

				DjCondVO condVO = new DjCondVO();
				UFBoolean isGroup = vo.getParentVO().getIsinitgroup();
				if (isGroup.booleanValue()) {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
					condVO.pk_group = new String[] { pk_group };
				} else {
					condVO.defWhereSQL = " zb.djlxbm='" + djlxbm + "' and zb.dr=0 and zb.isinitgroup='" + isGroup + "'";
					condVO.pk_org = new String[] { pk_org };
				}
				condVO.isInit = true;
				condVO.isCHz = false;
				List<JKBXVO> svos = NCLocator.getInstance().lookup(IBXBillPrivate.class).queryVOs(0, 1, condVO);

				if (svos != null && svos.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("expensepub_0",
							"02011002-0060")/*
											 * @res
											 * "并发异常，保存常用单据失败，该单据类型的常用单据在当前单位已经存在！"
											 */);
				}
			}
		}
	}

	/**
	 * 暂存业务处理，不进行任何业务校验，不调用其他模块接口
	 * 
	 * @param vos
	 *            要暂存单据的聚合VO数组
	 */
	public JKBXVO[] tempSave(JKBXVO[] vos) throws BusinessException {
		// 常用单据的并发校验
		checkForInitBill(vos);

		for (JKBXVO vo : vos) {
			BXUtil.generateJKBXRow(vo);
			VOChecker.prepare(vo);
			
			// 保存后自动提交
			vo.getParentVO().setSpzt(IPfRetCheckInfo.NOSTATE);
			vo.getParentVO().setDjzt(BXStatusConst.DJZT_TempSaved);

			new VOChecker().checkkSaveBackground(vo);
			// 特殊处理摘要
			dealZyName(vo);

			// 设置审计信息
			if (vo.getParentVO().getPrimaryKey() != null) {
				AuditInfoUtil.updateData(vo.getParentVO());
			} else {
				AuditInfoUtil.addData(vo.getParentVO());
			}
		}

		beforeActInf(vos, MESSAGE_TEMP_SAVE);

		JKBXVO[] bxvos;
		try {
			bxvos = getJKBXDAO().save(vos);
		} catch (SQLException e) {
			throw ExceptionHandler.handleException(e);
		}

		afterActInf(vos, MESSAGE_TEMP_SAVE);

		return bxvos;
	}

	public JKBXVO[] update(JKBXVO[] vos) throws BusinessException {
		// 校验时间戳
		compareTs(vos);
		for (JKBXVO vo : vos) {
			// 特殊处理摘要
			dealZyName(vo);
			
			// 查询修改前的vo
			if (vo.getBxoldvo() == null) {
				List<JKBXVO> oldvo = null;
				oldvo = NCLocator.getInstance().lookup(IBXBillPrivate.class)
						.queryVOsByPrimaryKeysForNewNode(new String[] { vo.getParentVO().getPrimaryKey() },
								vo.getParentVO().getDjdl(), vo.getParentVO().isInit(), null);
				vo.setBxoldvo(oldvo.get(0));
				// 补齐children信息（因前台传过来的的只是改变的children）
				fillUpChildren(vo, oldvo.get(0));
				// 同样补齐CShareDetailVO信息
				fillUpCShareDetail(vo, oldvo.get(0));

			}
			//只录表头，不录表体的情况下生成表体行
			BXUtil.generateJKBXRow(vo);
			
			VOChecker voChecker = new VOChecker();
			VOChecker.prepare(vo);
			//后台校验
			voChecker.checkkSaveBackground(vo);

			// 补齐申请单申请记录(预算控制中用到)
			fillUpMapf(vo);
			fillUpMapf(vo.getBxoldvo());

			// 常用单据单据,暂存态单据不校验关帐
			boolean isChkCloseAcc = true;
			JKBXHeaderVO parentVO = vo.getParentVO();
			if (parentVO != null && (parentVO.isInit() || BXStatusConst.DJZT_TempSaved == parentVO.getDjzt())) {
				isChkCloseAcc = false;
			}
			if (isChkCloseAcc) {
				VOChecker.checkErmIsCloseAcc(vo);
			}
			if (vo.getParentVO().getDjzt() != BXStatusConst.DJZT_TempSaved) {
				if (vo.getParentVO().getQcbz() != null && vo.getParentVO().getQcbz().booleanValue()) {
					vo.getParentVO().setSpzt(IPfRetCheckInfo.PASSING);
					vo.getParentVO().setShrq(new UFDateTime(vo.getParentVO().getDjrq(), new UFTime("00:00:00")));
					vo.getParentVO().setJsrq(vo.getParentVO().getDjrq());
					vo.getParentVO().setApprover(InvocationInfoProxy.getInstance().getUserId());
					vo.getParentVO().setJsr(InvocationInfoProxy.getInstance().getUserId());

				}else {
					if(vo.getParentVO().getSpzt() != IPfRetCheckInfo.GOINGON){//加入这个判断，审批中修改，不做审批状态变更
						vo.getParentVO().setSpzt(IPfRetCheckInfo.COMMIT);
					}
				}
			} 

			// 取服务器事件作为修改时间
			AuditInfoUtil.updateData(vo.getParentVO());
		}

		beforeActInf(vos, MESSAGE_UPDATE);

		JKBXVO[] bxvos = null;

		try {
			bxvos = getJKBXDAO().update(vos);
		} catch (SQLException e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			ExceptionHandler.handleException(e);
		}
		
		try {
			// 调用现金流平台结算
			ErForCmpBO erBO = new ErForCmpBO();

			for (JKBXVO vo : vos) {

				// 当前结算信息状态
				BusiStatus billStatus = SettleUtil.getBillStatus(vo.getParentVO(), false);

				// 当前结算信息状态
				BusiStatus oldBillStatus = SettleUtil.getBillStatus(vo.getBxoldvo().getParentVO(), false);

				// CMP产品是否启用
				boolean isInstallCmp = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(),
						BXConstans.TM_CMP_FUNCODE);

				// 是否暂存单据修改
				boolean isTmpSave = BXStatusConst.DJZT_TempSaved == vo.getParentVO().getDjzt();

				// 是否不存在收付款
				boolean notExistsPayOrRecv = (vo.getParentVO().getZfybje() == null || vo.getParentVO().getZfybje()
						.equals(new UFDouble(0)))
						&& (vo.getParentVO().getHkybje() == null || vo.getParentVO().getHkybje()
								.equals(new UFDouble(0)));

				boolean isToSettle = false;
				if (oldBillStatus == BusiStatus.Save && billStatus == BusiStatus.Deleted) {
					isToSettle = true;
				} else {
					isToSettle = !notExistsPayOrRecv;
				}

				if (isInstallCmp && !isTmpSave && isToSettle) {
					erBO.invokeCmp(vo, vo.getParentVO().getDjrq(), billStatus);
				}
			}

			afterActInf(vos, MESSAGE_UPDATE);
			
			//清空补齐的数据
			for(JKBXVO vo : vos){
				vo.setBxoldvo(null);
			}
		} catch (Exception e) {
			for (JKBXVO bxvo : vos) {
				returnBillCode(bxvo);
			}
			throw ExceptionHandler.handleException(e);
		}

		return bxvos;
	}

	/**
	 * 补充申请记录
	 * 
	 * @param bxVo
	 * @throws BusinessException
	 */
	private void fillUpMapf(JKBXVO bxVo) throws BusinessException {
		// 申请单处理
		MtapppfVO[] pfVos = MtappfUtil.getMaPfVosByJKBXVo(new JKBXVO[] { bxVo });
		bxVo.setMaPfVos(pfVos);

		if (bxVo instanceof BXVO) {
			MtapppfVO[] contrastPfs = MtappfUtil.getContrastMaPfVos(new JKBXVO[] { bxVo });
			bxVo.setContrastMaPfVos(contrastPfs);
		}
	}

	private void fillUpCShareDetail(JKBXVO vo, JKBXVO oldvo) {
		List<CShareDetailVO> result = new ArrayList<CShareDetailVO>();
		List<String> pkList = new ArrayList<String>();
		CShareDetailVO[] changedCShareVO = vo.getcShareDetailVo();
		CShareDetailVO[] oldCShareVO = oldvo.getcShareDetailVo();
		if (changedCShareVO == null || oldCShareVO == null) {
			return;
		}
		// 处理新增和修改和删除的行，只将 新增和修改的放到结果中
		for (int i = 0; i < changedCShareVO.length; i++) {
			if (changedCShareVO[i].getStatus() != VOStatus.DELETED) {
				result.add(changedCShareVO[i]);
			}
			pkList.add(changedCShareVO[i].getPrimaryKey());
		}
		for (int i = 0; i < oldCShareVO.length; i++) {
			if (!pkList.contains(oldCShareVO[i].getPrimaryKey())) {
				oldCShareVO[i].setStatus(VOStatus.UNCHANGED);
				result.add(oldCShareVO[i]);
			}
		}
		vo.setcShareDetailVo(result.toArray(new CShareDetailVO[] {}));
	}

	/**
	 * 补充业务页签
	 * 
	 * @param vo
	 * @param oldvo
	 */
	private void fillUpChildren(JKBXVO vo, JKBXVO oldvo) {
		List<BXBusItemVO> result = new ArrayList<BXBusItemVO>();
		oldvo = (JKBXVO)oldvo.clone();
		List<String> pkList = new ArrayList<String>();
		BXBusItemVO[] changedChildren = vo.getChildrenVO();
		BXBusItemVO[] oldChildren = oldvo.getChildrenVO();

		// 处理新增和修改和删除的行，只将 新增和修改的放到结果中
		for (int i = 0; i < changedChildren.length; i++) {
			if (changedChildren[i].getStatus() != VOStatus.DELETED) {
				result.add(changedChildren[i]);
			}
			pkList.add(changedChildren[i].getPrimaryKey());
		}
		for (int i = 0; i < oldChildren.length; i++) {
			if (!pkList.contains(oldChildren[i].getPrimaryKey())) {
				oldChildren[i].setStatus(VOStatus.UNCHANGED);
				result.add(oldChildren[i]);
			}

		}
		
		Collections.sort(result, new Comparator<BXBusItemVO>(){
			@Override
			public int compare(BXBusItemVO item1, BXBusItemVO item2) {
				if(item1.getRowno() == null && item2.getRowno() == null){
					return 0;
				}else if(item1.getRowno() != null && item2.getRowno() == null){
					return -1;
				}else if(item1.getRowno() == null && item2.getRowno() != null){
					return 1;
				}
				return item1.getRowno().compareTo(item2.getRowno());
			}
		});
		
		vo.setChildrenVO(result.toArray(new BXBusItemVO[] {}));
	}

	public MessageVO[] unAudit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				try {
					unAuditBack(vos[i]);

					msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT);
				} catch (BusinessException e) {
					msgs[i] = new MessageVO(vos[i], ActionUtils.UNAUDIT, false, e.getMessage());
					ExceptionHandler.handleException(e);
				}
			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return msgs;

	}

	private void unAuditBack(JKBXVO bxvo) throws BusinessException {
		// 查表体信息
		bxvo = retriveItems(bxvo);
		JKBXHeaderVO headerVO = bxvo.getParentVO();
		VOStatusChecker.checkUnAuditStatus(headerVO);

		// 判断CMP产品是否启用
		boolean isCmpInstalled = isCmpInstall(headerVO);
		BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false, BusiStatus.Save);
		if (billStatus.equals(BusiStatus.Deleted)) {

			// 没有结算信息的单据直接反生效
			unSettle(new JKBXVO[] { bxvo });

			// 删除凭证
			effectToFip(bxvo, MESSAGE_UNSETTLE);
		} else {
			UFDate shrq = headerVO.getShrq() == null ? null : headerVO.getShrq().getDate();
			if (!isCmpInstalled) {
				unSettle(new JKBXVO[] { bxvo });
				// 删除凭证
				effectToFip(bxvo, MESSAGE_UNSETTLE);
			} else {
				// 安装了结算的反审核
				new ErForCmpBO().invokeCmp(bxvo, shrq, billStatus);
			}
		}

		headerVO.setSxbz(BXStatusConst.SXBZ_NO);
		headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Saved));

		// begin--清空结算人，结算日期
		headerVO.setJsr(null);
		headerVO.setJsrq(null);
		headerVO.shrq_show = null;

		try {
			beforeActInf(bxvo, MESSAGE_UNAUDIT);

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO },
					new String[] { JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ, JKBXHeaderVO.SPZT });

			// 重新加载冲销行表体（带出冲销行生效日期）
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
				Collection<BxcontrastVO> contrasts = queryContrasts(bxvo.getParentVO());
				bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			}

			afterActInf(bxvo, MESSAGE_UNAUDIT);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private JKBXVO retriveItems(JKBXVO bxvo) throws BusinessException {

		if ((bxvo.getChildrenVO() == null || bxvo.getChildrenVO().length == 0)) {
			JKBXVO resultBxvo = new ArapBXBillPrivateImp().retriveItems(bxvo.getParentVO());
			bxvo.setChildrenVO(resultBxvo.getChildrenVO());
			bxvo.setcShareDetailVo(resultBxvo.getcShareDetailVo());
			bxvo.setContrastVO(resultBxvo.getContrastVO());
		}

		return bxvo;
	}

	public MessageVO[] audit(JKBXVO[] vos) throws BusinessException {

		if (vos == null || vos.length < 1)
			return null;

		MessageVO[] msgs = new MessageVO[vos.length];

		compareTs(vos);

		try {
			for (int i = 0; i < vos.length; i++) {

				auditBack(vos[i]);

				msgs[i] = new MessageVO(vos[i], ActionUtils.AUDIT);
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}

		return msgs;
	}

	/**
	 * 审核，单据状态转为已审核或已签字状态
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 * @throws SQLException
	 */
	private void auditBack(JKBXVO bxvo) throws BusinessException, SQLException {

		bxvo = retriveItems(bxvo);

		JKBXHeaderVO headerVO = bxvo.getParentVO();

		// 如果报销单全部用来冲借款后，审批后，支付状态应该设置为：全部冲借款
		if (headerVO.zfybje.doubleValue() == 0 && headerVO.hkybje.doubleValue() == 0) {
			headerVO.setPayflag(BXStatusConst.ALL_CONTRAST);
		}

		VOStatusChecker.checkAuditStatus(headerVO, headerVO.getShrq());
		headerVO.setSpzt(IPfRetCheckInfo.PASSING);

		beforeActInf(bxvo, MESSAGE_AUDIT);

		// 单据是否自动签字(受参数控制)
		boolean isAutoSign = SettleUtil.isAutoSign(headerVO);

		// 判断CMP产品是否启用
		boolean isCmpInstalled = isCmpInstall(headerVO);

		// modified by chendya
		if (headerVO.getSpzt() == IPfRetCheckInfo.PASSING) {
			// 审批通过
			headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Verified));
		}

		// 需更新字段
		String[] updateFields = new String[] { JKBXHeaderVO.SPZT, JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ,
				JKBXHeaderVO.APPROVER, JKBXHeaderVO.SHRQ, JKBXHeaderVO.PAYFLAG };
		if (isAutoSign) {
			// 1.状态置为已审核，结算单据
			headerVO.setJsr(headerVO.getApprover());
			headerVO.setJsrq(headerVO.getShrq().getDate());

			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO }, updateFields);

			// 是否有结算信息(根据支付，还款金额判断)
			BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false, BusiStatus.Audit);

			// 没有结算信息的单据直接签字生效
			if (billStatus.equals(BusiStatus.Deleted)) {
				settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);

				// 传会计平台
				effectToFip(bxvo, MESSAGE_SETTLE);
			} else {
				if (isCmpInstalled) {
					new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), billStatus);
				} else {

					// 未装结算情况
					settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);

					// 传会计平台
					effectToFip(bxvo, MESSAGE_SETTLE);
				}
			}
			// 自动签字
			headerVO.setDjzt(Integer.valueOf(BXStatusConst.DJZT_Sign));
			// 生效标志
			headerVO.setSxbz(Integer.valueOf((BXStatusConst.SXBZ_VALID)));
		} else {
			BusiStatus billStatus = SettleUtil.getBillStatus(headerVO, false, BusiStatus.Audit);
			// 没有结算信息的单据直接签字生效
			if (billStatus.equals(BusiStatus.Deleted)) {
				settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);
				// 传会计平台
				effectToFip(bxvo, MESSAGE_SETTLE);
			} else {
				if (isCmpInstalled) {
					new ErForCmpBO().invokeCmp(bxvo, headerVO.getShrq().getDate(), billStatus);
				} else {
					settle(headerVO.getApprover(), headerVO.getShrq().getDate(), bxvo);
					// 传会计平台
					effectToFip(bxvo, MESSAGE_SETTLE);
				}
			}
		}
		try {
			getJKBXDAO().update(new JKBXHeaderVO[] { headerVO }, updateFields);

			// 重新加载冲销行表体（带出冲销行生效日期）
			if (bxvo.getContrastVO() != null && bxvo.getContrastVO().length > 0) {
				Collection<BxcontrastVO> contrasts = queryContrasts(bxvo.getParentVO());
				bxvo.setContrastVO(contrasts.toArray(new BxcontrastVO[] {}));
			}
			afterActInf(bxvo, MESSAGE_AUDIT);

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		}

	}

	/**
	 * 
	 * @param vos
	 * @param isCmpUnEffectiveCall
	 *            是否结算反生效调用
	 * @return
	 * @throws BusinessException
	 */
	public JKBXVO[] unSettle(JKBXVO[] vos, boolean isCmpUnEffectiveCall) throws BusinessException {

		compareTs(vos);

		try {
			String[] keys = new String[vos.length];
			JKBXHeaderVO[] headers = new JKBXHeaderVO[vos.length];

			for (int i = 0; i < vos.length; i++) {
				JKBXHeaderVO parentVO = vos[i].getParentVO();
				keys[i] = parentVO.getPk_jkbx();

				vos[i].setBxoldvo((JKBXVO) vos[i].clone());
				headers[i] = parentVO;

				// 补充信息
				addBxExtralInfo(vos[i]);
				
				fillUpMapf(vos[i]);
			}

			beforeActInf(vos, MESSAGE_UNSETTLE);
			
			for (int i = 0; i < vos.length; i++) {
				headers[i].setJsrq(null);
				headers[i].setJsr(null);
				headers[i].setDjzt(BXStatusConst.DJZT_Verified);
				headers[i].setSxbz(BXStatusConst.SXBZ_NO);
			}

			String inStr = SqlUtils.getInStr("er_jsconstras.pk_bxd", keys);

			String sqlJS = " er_jsconstras  where " + inStr;

			// 校验反签字信息
			checkUnSettle(keys, headers);

			Collection<JsConstrasVO> jsContrasVOs = getJKBXDAO().queryJsContrastByWhereSql(sqlJS);

			if (jsContrasVOs != null) {

				// 处理生成的收付往来单据，分应收应付单独处理0表示应收 1表示应付
				List<String> vouchid1 = new ArrayList<String>();
				List<String> vouchid0 = new ArrayList<String>();
				JsConstrasVO jsconvo = null;
				for (Iterator<JsConstrasVO> iter = jsContrasVOs.iterator(); iter.hasNext();) {
					jsconvo = iter.next();

					String vouchid = jsconvo.getPk_jsd();
					Integer billflag = jsconvo.getBillflag();
					if (billflag.intValue() == 1) {
						vouchid1.add(vouchid);
					} else {
						vouchid0.add(vouchid);
					}
				}

				if (vouchid1 != null && vouchid1.size() != 0) {
					// 通过对应的应付单的pk找到对应的聚合VO，之后删除对应的应付单
					IArapPayableBillPubService apBillService = (IArapPayableBillPubService) NCLocator.getInstance()
							.lookup(IArapPayableBillPubService.class.getName());
					IArapPayableBillPubQueryService apQryService = (IArapPayableBillPubQueryService) NCLocator
							.getInstance().lookup(IArapPayableBillPubQueryService.class.getName());
					AggPayableBillVO[] payablevo = apQryService.findBillByPrimaryKey(vouchid1.toArray(new String[] {}));
					if (payablevo != null) {
						for (AggPayableBillVO ss : payablevo) {
							BaseBillVO svo = (BaseBillVO) ss.getParentVO();
							if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE.intValue()
									&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE.intValue()) {
								throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("expensepub_0", "02011002-0061")/*
																					 * @
																					 * res
																					 * "下游往来单据已经不是暂存或保存态， 不能进行反生效操作"
																					 */);
							}
						}
						apBillService.delete(payablevo);
					}
				}
				if (vouchid0 != null && vouchid0.size() != 0) {
					// 通过对应的应收单的pk找到对应的聚合VO，之后删除对应的应收单
					IArapReceivableBillPubService billBo0 = (IArapReceivableBillPubService) NCLocator.getInstance()
							.lookup(IArapReceivableBillPubService.class.getName());
					IArapReceivableBillPubQueryService billquery0 = (IArapReceivableBillPubQueryService) NCLocator
							.getInstance().lookup(IArapReceivableBillPubQueryService.class.getName());
					AggReceivableBillVO[] recvo = billquery0.findBillByPrimaryKey(vouchid0.toArray(new String[] {}));
					if (recvo != null) {
						for (AggReceivableBillVO ss : recvo) {
							BaseBillVO svo = (BaseBillVO) ss.getParentVO();
							if (svo.getBillstatus().intValue() != ARAPBillStatus.TEMPSAVE.VALUE.intValue()
									&& svo.getBillstatus().intValue() != ARAPBillStatus.SAVE.VALUE.intValue()) {
								throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes()
										.getStrByID("expensepub_0", "02011002-0061")/*
																					 * @
																					 * res
																					 * "下游往来单据已经不是暂存或保存态， 不能进行反生效操作"
																					 */);
							}
						}
						billBo0.delete(recvo);
					}
				}

				getJKBXDAO().delete(jsContrasVOs.toArray(new JsConstrasVO[] {}));
			}

			updateHeaders(headers, new String[] { JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ, JKBXHeaderVO.DJZT,
					JKBXHeaderVO.SXBZ });

			afterActInf(vos, MESSAGE_UNSETTLE);

			return vos;

		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
		return null;
	}

	/**
	 * @param head
	 * @throws BusinessException
	 * 
	 *             后台进行反结算操作
	 */
	public void unSettleBack(JKBXHeaderVO head) throws BusinessException {

		VOStatusChecker.checkUnSettleStatus(head);

		JKBXVO bxvo = VOFactory.createVO(head);

		head.setDjzt(BXStatusConst.DJZT_Verified);
		head.setSxbz(BXStatusConst.SXBZ_NO);
		unSettle(new JKBXVO[] { bxvo });

	}

	/**
	 * 此方法处理生效，借款控制，预算控制，传收付
	 * 
	 * @param jsr
	 * @param jsrq
	 * @param vo
	 * @throws BusinessException
	 * 
	 */
	public void settle(String jsr, UFDate jsrq, JKBXVO vo) throws BusinessException {

		// 校验ts
		compareTs(new JKBXVO[] { vo });

		JKBXHeaderVO head = vo.getParentVO();

		VOStatusChecker.checkSettleStatus(head, jsrq);

		// 补充信息
		addBxExtralInfo(vo);
		fillUpMapf(vo);
		// begin--added by chendya 下列代码块一定放在预算控制之前，
		// 避免预算控制设置控制规则中如果设置控制日期为“生效日期”时查不到控制方案
		{
			head.setJsr(jsr);
			head.setJsrq(jsrq);
			head.setDjzt(BXStatusConst.DJZT_Sign);
			head.setSxbz(BXStatusConst.SXBZ_VALID);
		}
		// --end

		// 处理前插件动作(预算控制)
		beforeActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// 更新vo信息
		updateHeaders(new JKBXHeaderVO[] { head }, new String[] { JKBXHeaderVO.JSR, JKBXHeaderVO.JSRQ,
				JKBXHeaderVO.DJZT, JKBXHeaderVO.SXBZ });

		// 处理后插件动作
		afterActInf(new JKBXVO[] { vo }, MESSAGE_SETTLE);

		// 传收付
		transferArap(vo);
	}

	/**
	 * 
	 * 传收付 往来单据
	 */
	private void transferArap(JKBXVO vo) throws BusinessException {
		// 判断是否安装应收应付产品，否则不进行转往来操作
		boolean isARused = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(), BXConstans.FI_AR_FUNCODE);
		boolean isAPused = BXUtil.isProductInstalled(vo.getParentVO().getPk_group(), BXConstans.FI_AP_FUNCODE);

		// 进行转收付操作
		if (isARused && isAPused) {
			new ErPFUtil().doTransferArap(vo);
		}
	}

	/**
	 * @param vo
	 * @throws BusinessException
	 * 
	 *             增加冲借款的信息和对应的借款单的信息
	 */
	private void addBxExtralInfo(JKBXVO vo) throws BusinessException {
		if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
			BxcontrastVO[] contrast = vo.getContrastVO();
			if (vo.getContrastVO() == null || vo.getContrastVO().length == 0) {
				// 补充冲销信息
				Collection<BxcontrastVO> contrasts = queryContrasts(vo.getParentVO());
				contrast = contrasts.toArray(new BxcontrastVO[] {});
				vo.setContrastVO(contrast);
			}

			if (vo.getcShareDetailVo() == null || vo.getcShareDetailVo().length == 0) {
				// 补充分摊明细
				Collection<CShareDetailVO> cShares = queryCSharesVOS(new JKBXHeaderVO[] { vo.getParentVO() });
				vo.setcShareDetailVo(cShares.toArray(new CShareDetailVO[] {}));
			}

			// 补充对应的借款单信息
			List<String> jkdKeys = new ArrayList<String>();
			if (contrast != null && contrast.length != 0) {
				for (BxcontrastVO contr : contrast) {
					jkdKeys.add(contr.getPk_jkd());
				}
			}

			if (jkdKeys.size() > 0) {
				List<JKBXHeaderVO> jkds = new BXZbBO().queryHeadersByPrimaryKeys(jkdKeys.toArray(new String[] {}),
						BXConstans.JK_DJDL);
				Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
				if (jkds != null) {
					for (JKBXHeaderVO jkd : jkds) {
						jkdMap.put(jkd.getPrimaryKey(), jkd);
					}
				}
				vo.setJkdMap(jkdMap);
			}
		}
	}

	/**
	 * 回退单据号
	 * 
	 * @param bxvo
	 * @throws BusinessException
	 */

	public void returnBillCode(JKBXVO bxvo) throws BusinessException {
		FinanceBillCodeUtils utils = new FinanceBillCodeUtils(new FinanceBillCodeInfo(JKBXHeaderVO.DJDL,
				JKBXHeaderVO.DJBH, JKBXHeaderVO.PK_GROUP, JKBXHeaderVO.PK_ORG, bxvo.getParentVO().getTableName()));
		utils.returnBillCode(new AggregatedValueObject[] { bxvo });

	}

	/**
	 * @param headers
	 * @throws DAOException
	 * @throws SQLException
	 * 
	 *             由于DAO不能同时更新借款单和报销单，这里需要分开处理.
	 */
	public void updateHeaders(JKBXHeaderVO[] headers, String[] fields) throws BusinessException {
		Map<String, List<JKBXHeaderVO>> voMap = splitJkbx(headers);
		Collection<List<JKBXHeaderVO>> values = voMap.values();
		List<JKBXHeaderVO> list = new LinkedList<JKBXHeaderVO>();
		for (Iterator<List<JKBXHeaderVO>> iter = values.iterator(); iter.hasNext();) {
			List<JKBXHeaderVO> lvos = iter.next();
			list.addAll(lvos);
		}
		try {
			getJKBXDAO().update(list.toArray(new JKBXHeaderVO[list.size()]), fields);
		} catch (SQLException e) {
			throw new BusinessException(e.getMessage(), e);
		}
	}

	/**
	 * @param headers
	 * @return 按单据大类分离VO数组
	 */
	private Map<String, List<JKBXHeaderVO>> splitJkbx(JKBXHeaderVO[] headers) {
		Map<String, List<JKBXHeaderVO>> map = new HashMap<String, List<JKBXHeaderVO>>();
		for (int i = 0; i < headers.length; i++) {
			JKBXHeaderVO headerVO = headers[i];
			String djdl = headerVO.getDjdl();
			if (map.containsKey(djdl)) {
				map.get(djdl).add(headerVO);
			} else {
				List<JKBXHeaderVO> list = new ArrayList<JKBXHeaderVO>();
				list.add(headerVO);
				map.put(djdl, list);
			}
		}
		return map;
	}

	/**
	 * @param keys
	 *            //所有单据主键
	 * @param jkdKeys
	 *            //借款单据主键
	 * @param headers
	 *            //借款单据表头
	 * @throws SQLException
	 * @throws DAOException
	 * @throws BusinessException
	 */
	private void checkUnSettle(String[] keys, JKBXHeaderVO[] headers) throws BusinessException {

		try {
			if (keys != null) {// 校验借款单是否已经进行了冲销

				String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_JKD, keys);

				Collection<BxcontrastVO> jkContrasVOs = getJKBXDAO().retrieveContrastByClause(sqlJK);

				if (jkContrasVOs != null && jkContrasVOs.size() > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000384")/*
											 * @res "借款单已进行了冲销操作,无法进行反签字!"
											 */);
				}

				for (int i = 0; i < headers.length; i++) {
					JKBXHeaderVO bxHeaderVO = headers[i];
					if (BXConstans.BX_DJDL.equals(bxHeaderVO.getDjdl())) {
						CostShareVO shareVo = NCLocator.getInstance().lookup(IErmCostShareBillQuery.class)
								.queryCShareVOByBxVoHead(bxHeaderVO, UFBoolean.FALSE);
						if (shareVo != null) {
							throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID(
									"upp2012v575_0", "0upp2012V575-0063")/*
																		 * @res
																		 * "报销单已结转，无法进行反签字!"
																		 */);
						}
					}
				}

			}
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public static String MESSAGE_TEMP_SAVE = "bx-temp-save";

	public static String MESSAGE_SAVE = "bx-save";

	public static String MESSAGE_UPDATE = "bx-update";

	public static String MESSAGE_AUDIT = "bx-audit";

	public static String MESSAGE_UNAUDIT = "bx-unaudit";

	public static String MESSAGE_DELETE = "bx-delete";

	public static String MESSAGE_SETTLE = "bx-settle";

	public static String MESSAGE_UNSETTLE = "bx-unsettle";

	public static String MESSAGE_DTSET = "bx-dtset";

	public static String MESSAGE_UNDTSET = "bx-unset";

	public static int MESSAGE_NOTSEND = -1;

	/**
	 * @ 与收付以及会计平台交互 @
	 * */

	private void sendMessage(JKBXVO vo, int message) throws BusinessException {
		try {
			JKBXHeaderVO headVO = VOFactory.createHeadVO(vo.getParentVO().getDjdl());
			headVO = vo.getParentVO();

			AggregatedValueObject object = createJkbxToFIPVO(vo, message);

			sendMessageToFip(headVO, vo, object, message);
		} catch (BusinessException e) {

			ExceptionHandler.handleException(e);

		}
	}

	public static AggregatedValueObject createJkbxToFIPVO(JKBXVO vo, int message) throws BusinessException {
		AggregatedValueObject object = null;

		JKBXVO bxvo = ErVOUtils.prepareBxvoHeaderToItemClone(vo);

		if (message != FipMessageVO.MESSAGETYPE_DEL) {
			object = new FipUtil().addOtherInfo(bxvo);
		}
		return object;
	}

	/**
	 * 传入会计平台的数据： 集团，组织，来源系统，业务日期，单据PK，单据类型 自定义项：报销管理在会计平台需要展示的项目
	 * 
	 * @param message
	 * */
	private void sendMessageToFip(JKBXHeaderVO headVO, JKBXVO bxvo, Object object, int message)
			throws BusinessException {

		FipRelationInfoVO reVO = new FipRelationInfoVO();
		// 具体设置信息应用会计平台
		reVO.setPk_group(headVO.getPk_group());

		// 63后传会计凭证按支付单位来进行处理
		reVO.setPk_org(headVO.getPk_payorg());
		reVO.setRelationID(headVO.getPk());
		reVO.setPk_system(BXConstans.ERM_PRODUCT_CODE_Lower);
		reVO.setBusidate(headVO.getJsrq() == null ? new UFDate() : headVO.getJsrq());
		reVO.setPk_billtype(headVO.getDjlxbm());
		reVO.setPk_operator(headVO.getOperator());
		reVO.setFreedef1(headVO.getDjbh());
		reVO.setFreedef2(headVO.getZy());
		UFDouble total = headVO.getYbje();

		// added by chendya 设置金额字段的精度
		total = total.setScale(Currency.getCurrDigit(headVO.getBzbm()), UFDouble.ROUND_HALF_UP);
		reVO.setFreedef3(String.valueOf(total));

		FipMessageVO messageVO = new FipMessageVO();
		messageVO.setBillVO(object);
		messageVO.setMessagetype(message);
		messageVO.setMessageinfo(reVO);
		try {
			new FipCallFacade().sendMessage(messageVO);
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);

		}
	}

	void beforeActInf(JKBXVO djzb, String message) throws BusinessException {
		beforeActInf(new JKBXVO[] { djzb }, message);
	}

	void afterActInf(JKBXVO djzb, String message) throws BusinessException {
		afterActInf(new JKBXVO[] { djzb }, message);
	}

	/**
	 * 动作后事件处理
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void afterActInf(JKBXVO[] vos, String message) throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_AFTER;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_AFTER;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_AFTER;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_AFTER;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_AFTER;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_AFTER;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_AFTER;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_AFTER;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_AFTER;
		} else {
			return;
		}
		// 非常用单据发送事件
		if (!vos[0].getParentVO().isInit()) {
			EventDispatcher.fireEvent(new ErmBusinessEvent(BXConstans.ERM_MDID_BX, eventType, vos));
		}
		// v6.1新增项目预算控制
		// projectBudgetCtrl(vos, message);

		// 生成报销业务日志
		for (JKBXVO bxvo : vos) {
			if (message.equals(MESSAGE_UPDATE) || message.equals(MESSAGE_DELETE)) {
				BxBusiLogUtils.insertSmartBusiLogs(message.equals(MESSAGE_UPDATE) ? true : false, bxvo);
			}
		}
	}

	// /**
	// *
	// * v6.1新增 项目预算执行VO实现
	// *
	// * @author chendya
	// */
	//
	// private static List<IBudgetExecVO> getBudgetExecVOs(final JKBXVO vo) {
	// List<IBudgetExecVO> resultVOs = new ArrayList<IBudgetExecVO>();
	//
	// for (final BXBusItemVO busItemVO : vo.getChildrenVO()) {
	// resultVOs.add(new IBudgetExecVO() {
	// public String getPk_wbs_exec() {
	// return null;
	// }
	//
	// public String getPk_wbs() {
	// return vo.getParentVO().getProjecttask();
	// }
	//
	// public String getPk_project() {
	// return vo.getParentVO().getJobid();
	// }
	//
	// public String getPk_factor() {
	// return vo.getParentVO().getPk_checkele();
	// }
	//
	// public String getPk_currtype() {
	// return vo.getParentVO().getBzbm();
	// }
	//
	// public UFDouble getNmoney() {
	// return busItemVO.getYbje();
	// }
	//
	// public String getBill_type() {
	// final String djdl = vo.getParentVO().getDjdl();
	// if (BXConstans.JK_DJDL.equals(djdl)) {
	// return BXConstans.JK_DJLXBM;
	// }
	// return BXConstans.BX_DJLXBM;
	// }
	//
	// public String getBill_transitype() {
	// return vo.getParentVO().getDjlxbm();
	// }
	//
	// public String getBill_id() {
	// return vo.getParentVO().getPk_jkbx();
	// }
	//
	// public String getBill_code() {
	// return vo.getParentVO().getDjbh();
	// }
	//
	// public String getBill_bid() {
	// return null;
	// }
	//
	// public Map getUserDefMap() {
	// Map<String, Object> map = new HashMap<String, Object>();
	// map.put(JKBXHeaderVO.SZXMID, busItemVO.getSzxmid());
	// map.put(BXBusItemVO.PK_REIMTYPE, busItemVO.getPk_reimtype());
	// return map;
	// }
	// });
	// }
	//
	// return resultVOs;
	// }
	//
	// private static IBudgetExecVO[] getBudgetExecVOs(JKBXVO[] vos) {
	// List<IBudgetExecVO> voList = new ArrayList<IBudgetExecVO>();
	// for (JKBXVO vo : vos) {
	// if (vo == null || vo.getParentVO() == null) {
	// continue;
	// }
	// // 没有项目任务
	// if (StringUtil.isEmpty(vo.getParentVO().getJobid())) {
	// continue;
	// }
	//
	// voList.addAll(getBudgetExecVOs(vo));
	// }
	// return (IBudgetExecVO[]) voList.toArray(new IBudgetExecVO[0]);
	// }

	// private static JKBXVO[] getOldVOArray(JKBXVO[] vos) {
	// List<JKBXVO> oldVOList = new ArrayList<JKBXVO>();
	// for (JKBXVO vo : vos) {
	// oldVOList.add(vo.getBxoldvo());
	// }
	// return oldVOList.toArray(new JKBXVO[0]);
	// }

	// /**
	// * v6.1 新增报销管理支持项目预算控制
	// *
	// * @author chendya
	// */
	// private void projectBudgetCtrl(JKBXVO[] vos, String message) throws
	// BusinessException {
	// // 项目管理是否安装
	// boolean installed =
	// BXUtil.isProductInstalled(vos[0].getParentVO().getPk_group(),
	// BXConstans.PM_MODULEID);
	// if (!installed) {
	// return;
	// }
	// List<String> PROJECT_CTRL_MESSAGE = Arrays.asList(new String[] {
	// MESSAGE_SAVE,
	// MESSAGE_DELETE, MESSAGE_UPDATE, MESSAGE_SETTLE, MESSAGE_UNSETTLE });
	// if (!PROJECT_CTRL_MESSAGE.contains(message)) {
	// return;
	// }
	//
	// // 当前项目预算执行vo
	// IBudgetExecVO[] currBudgetExecVOs = getBudgetExecVOs(vos);
	// if (currBudgetExecVOs.length == 0) {
	// // 没有需要执行项目预算的单据
	// return;
	// }
	// // 修改前项目预算执行vo
	// IBudgetExecVO[] oldBudgetExecVOs = getBudgetExecVOs(getOldVOArray(vos));
	//
	// // 反生效或删除操作时old vo为空
	// if (MESSAGE_DELETE.equals(message) || MESSAGE_UNSETTLE.equals(message)) {
	// oldBudgetExecVOs = null;
	// }
	//
	// // 调用项目预算接口执行项目预算
	// /*BudgetCtlInfoMSG retMsg =
	// NCLocator.getInstance().lookup(IBudgetExecute.class)
	// .executeBudgetWithCheck(currBudgetExecVOs, oldBudgetExecVOs,
	// getBudgetCtrlPoint(message), getBudgetOperTypeENum(message),
	// getBudgetCtrlMap(message));
	// //if (retMsg != null && retMsg.getDetailList() != null) {
	// List<BudgetCtlInfoVO> ctrlInfos = retMsg.getDetailList();
	// StringBuffer buffer = new StringBuffer();
	// for (BudgetCtlInfoVO info : ctrlInfos) {
	// if (info != null) {
	// buffer.append(info.toString());
	// }
	// }
	// Log.getInstance(getClass()).debug(buffer.toString());
	// }*/
	// }
	//
	// private int getBudgetCtrlPoint(final String message) {
	// if (MESSAGE_SAVE.equals(message) || MESSAGE_UPDATE.equals(message)
	// || MESSAGE_DELETE.equals(message)) {
	// return nc.vo.pmbd.budgetctrl.BudgetCtrlPoint.save_control;
	// } else if (MESSAGE_AUDIT.equals(message) ||
	// MESSAGE_UNAUDIT.equals(message)) {
	// return nc.vo.pmbd.budgetctrl.BudgetCtrlPoint.check_control;
	// }
	// return nc.vo.pmbd.budgetctrl.BudgetCtrlPoint.effext_control;
	// }

	// private nc.itf.pim.budget.pub.BudgetOperTypeENum
	// getBudgetOperTypeENum(String message) {
	// if (MESSAGE_SAVE.equals(message) || MESSAGE_SETTLE.equals(message)) {
	// return nc.itf.pim.budget.pub.BudgetOperTypeENum.OPER_ADD_NEW;
	// } else if (MESSAGE_DELETE.equals(message) ||
	// MESSAGE_UNSETTLE.equals(message)) {
	// return nc.itf.pim.budget.pub.BudgetOperTypeENum.OPER_CANCEL;
	// }
	// return nc.itf.pim.budget.pub.BudgetOperTypeENum.OPER_UPDATE;
	// }

	// private Map<Integer, Boolean> getBudgetCtrlMap(final String message) {
	// if (MESSAGE_SAVE.equals(message) || MESSAGE_UPDATE.equals(message)
	// || MESSAGE_DELETE.equals(message)) {
	// return nc.vo.pmbd.budgetctrl.BudgetCtrlMapConst.getBudget_map_save();
	// }
	// return nc.vo.pmbd.budgetctrl.BudgetCtrlMapConst.getBudget_map_approve();
	// }

	/**
	 * 单据生效传会计平台
	 * 
	 * @throws BusinessException
	 */
	private void effectToFip(JKBXVO bxvo, String message) throws BusinessException {
		if (getDapMessage(bxvo.getParentVO(), message) != MESSAGE_NOTSEND) {
			sendMessage(bxvo, getDapMessage(bxvo.getParentVO(), message));
		}
	}

	/**
	 * 单据生效传会计平台
	 * 
	 * @throws BusinessException
	 */
	public void effectToFip(List<JKBXVO> listVOs, String message) throws BusinessException {
		// 发送会计平台
		for (Iterator<JKBXVO> iter = listVOs.iterator(); iter.hasNext();) {
			effectToFip(iter.next(), message);
		}
	}

	/**
	 * Action触发之前的动作
	 * 
	 * @param vos
	 * @param message
	 * @throws BusinessException
	 */
	private void beforeActInf(JKBXVO[] vos, String message) throws BusinessException {
		List<JKBXVO> listVOs = new ArrayList<JKBXVO>();
		for (JKBXVO vo : vos) {
			if (!vo.getParentVO().isNoOtherEffectItf()) {
				listVOs.add(vo);
			}
		}

		// 事件类型
		String eventType = null;
		if (message.equals(MESSAGE_SAVE)) {
			eventType = ErmEventType.TYPE_INSERT_BEFORE;
		} else if (message.equals(MESSAGE_UPDATE)) {
			if (vos[0].getParentVO().getDjzt() == BXStatusConst.DJZT_TempSaved) {
				eventType = ErmEventType.TYPE_TEMPUPDATE_BEFORE;
			} else {
				eventType = ErmEventType.TYPE_UPDATE_BEFORE;
			}
		} else if (message.equals(MESSAGE_AUDIT)) {
			eventType = ErmEventType.TYPE_APPROVE_BEFORE;
		} else if (message.equals(MESSAGE_UNAUDIT)) {
			eventType = ErmEventType.TYPE_UNAPPROVE_BEFORE;
		} else if (message.equals(MESSAGE_DELETE)) {
			eventType = ErmEventType.TYPE_DELETE_BEFORE;
		} else if (message.equals(MESSAGE_SETTLE)) {
			eventType = ErmEventType.TYPE_SIGN_BEFORE;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			eventType = ErmEventType.TYPE_UNSIGN_BEFORE;
		} else if (message.equals(MESSAGE_TEMP_SAVE)) {
			eventType = ErmEventType.TYPE_TEMPSAVE_BEFORE;
		} else {
			return;
		}

		// 非常用单据发送事件
		if (!vos[0].getParentVO().isInit()) {
			EventDispatcher.fireEvent(new ErmBusinessEvent(BXConstans.ERM_MDID_BX, eventType, vos));
		}
	}

	// 判断签字时候传会计平台
	private int getDapMessage(JKBXHeaderVO head, String message) {
		int msg = MESSAGE_NOTSEND;

		if (message.equals(MESSAGE_SETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (message.equals(MESSAGE_UNSETTLE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		} else if (head.getQcbz().equals(UFBoolean.TRUE) && message.equals(MESSAGE_SAVE)) {
			msg = FipMessageVO.MESSAGETYPE_ADD;
		} else if (head.getQcbz().equals(UFBoolean.TRUE) && message.equals(MESSAGE_DELETE)) {
			msg = FipMessageVO.MESSAGETYPE_DEL;
		}
		return msg;
	}

	public List<JKBXHeaderVO> queryHeaders(Integer start, Integer count, DjCondVO condVO) throws BusinessException {
		try {
			return getJKBXDAO().queryHeaders(start, count, condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public List<JKBXHeaderVO> queryHeadersByWhereSql(String sql, String djdl) throws BusinessException {
		try {
			return getJKBXDAO().queryHeadersByWhereSql(sql, djdl);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private static void lockDJ(String pk) throws BusinessException {
		String lock = KeyLock.dynamicLock(pk);
		if (lock != null) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000359"));
		}
	}

	public void compareTs(JKBXHeaderVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();
		for (JKBXHeaderVO vo : vos) {
			if (vo.isInit()) {
				ts3.put(vo.getPk_jkbx(), vo.getTs().toString());
			} else if (vo.getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(vo.getPk_jkbx(), vo.getTs().toString());
			} else {
				ts2.put(vo.getPk_jkbx(), vo.getTs().toString());
			}
		}
		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public void compareTs(JKBXVO[] vos) throws BusinessException {
		Hashtable<String, String> ts = new Hashtable<String, String>();
		Hashtable<String, String> ts2 = new Hashtable<String, String>();
		Hashtable<String, String> ts3 = new Hashtable<String, String>();

		for (JKBXVO vo : vos) {
			UFDateTime ts4 = vo.getParentVO().getTs();
			String pk_jkbx = vo.getParentVO().getPk_jkbx();

			if (ts4 == null || pk_jkbx == null)
				continue;

			if (vo.getParentVO().isInit()) {
				ts3.put(pk_jkbx, ts4.toString());
			} else if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
				ts.put(pk_jkbx, ts4.toString());
			} else {
				ts2.put(pk_jkbx, ts4.toString());
			}
		}

		if (ts.size() != 0)
			compareTS(ts, "er_bxzb", JKBXHeaderVO.PK_JKBX);
		if (ts2.size() != 0)
			compareTS(ts2, "er_jkzb", JKBXHeaderVO.PK_JKBX);
		if (ts3.size() != 0)
			compareTS(ts3, "er_jkbx_init", JKBXHeaderVO.PK_JKBX);
	}

	public static void compareTS(Hashtable<String, String> ts, String tableName, String pkField)
			throws BusinessException {
		String tname = null;
		Connection con = null;
		PreparedStatement stat = null;
		ResultSet rs = null;

		try {
			con = ConnectionFactory.getConnection();

			nc.bs.mw.sqltrans.TempTable tmptab = new nc.bs.mw.sqltrans.TempTable();

			tname = tmptab.createTempTable(con, "ErmTsTemp", "pk char(20),ts1 char(19)", "pk");

			String sql = "insert into " + tname + " (pk,ts1) values(?,?)";
			((CrossDBConnection) con).setAddTimeStamp(false);
			stat = con.prepareStatement(sql);

			for (Map.Entry<String, String> entry : ts.entrySet()) {
				stat.setString(1, entry.getKey());
				stat.setString(2, entry.getValue());
				stat.addBatch();
				lockDJ(entry.getKey());
			}

			stat.executeBatch();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			closeDBCon(con, stat, rs);
		}

		try {
			final String sql_n = "select count(temp.pk) from " + tname + " temp inner join " + tableName
					+ " tab on temp.pk=tab." + pkField + " where temp.ts1=tab.ts";
			con = ConnectionFactory.getConnection();
			stat = con.prepareStatement(sql_n);
			rs = stat.executeQuery();
			int result = 0;
			if (rs.next())
				result = rs.getInt(1);
			
			if (result != ts.size())
				throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
						"UPP2011-000359")/*
										 * @res "并发异常，数据已经更新，请重新查询数据后操作"
										 */);
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		} finally {
			closeDBCon(con, stat, rs);
		}
	}

	private static void closeDBCon(Connection con, PreparedStatement stat, ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}

		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
		if (stat != null) {
			try {
				stat.close();
			} catch (SQLException e) {
				ExceptionHandler.consume(e);
			}
		}
	}

	public Collection<JsConstrasVO> queryJsContrasts(JKBXHeaderVO header) throws BusinessException {

		String pk_jkbx = header.getPk_jkbx();

		Collection<JsConstrasVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(JsConstrasVO.PK_BXD + "='" + pk_jkbx + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return vos;
	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO parentVO) throws BusinessException {

		String pk_jkbx = parentVO.getPk_jkbx();
		String key = null;

		if (parentVO.getDjdl().equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(key + "='" + pk_jkbx + "'");
			fillUpHkJe(vos);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;

	}

	public Collection<BxcontrastVO> queryContrasts(JKBXHeaderVO[] parentVO, String djdl) throws BusinessException {
		String key = null;

		if (djdl.equals(BXConstans.BX_DJDL)) {
			key = BxcontrastVO.PK_BXD;
		} else {
			key = BxcontrastVO.PK_JKD;
		}

		Collection<BxcontrastVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveContrastByClause(SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX));
			fillUpHkJe(vos);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	private void fillUpHkJe(Collection<BxcontrastVO> vos) {
		if (vos != null && vos.size() > 0) {// 冲销信息的还款金额属于计算属性，所以需要在这里补充
			for (BxcontrastVO contrastVo : vos) {
				contrastVo.setHkybje(contrastVo.getCjkybje().sub(
						contrastVo.getFyybje() == null ? UFDouble.ZERO_DBL : contrastVo.getFyybje()));
				
				UFDouble cjkbbje = contrastVo.getCjkbbje() == null ? UFDouble.ZERO_DBL : contrastVo.getCjkbbje();
				UFDouble fybbje = contrastVo.getFybbje() == null ? UFDouble.ZERO_DBL : contrastVo.getFybbje();
				
				contrastVo.setHkbbje(cjkbbje.sub(fybbje));
			}
		}
	}

	public List<JKBXHeaderVO> queryHeadersByPrimaryKeys(String[] keys, String djdl) throws BusinessException {
		List<JKBXHeaderVO> headVos = null;
		try {

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, keys);
			inStr += " order by djrq desc, djbh desc ";
			headVos = getJKBXDAO().queryHeadersByWhereSql(" where zb.dr=0 and " + inStr, djdl);

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return headVos;
	}

	public int querySize(DjCondVO condVO) throws BusinessException {
		int querySize = -99;
		try {
			querySize = getJKBXDAO().querySize(condVO);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		return querySize;
	}

	public void updateQzzt(JKBXVO[] vos) throws BusinessException {
		JKBXHeaderVO[] heads = new JKBXHeaderVO[vos.length];
		for (int i = 0; i < heads.length; i++) {
			heads[i] = vos[i].getParentVO();
		}
		try {
			getJKBXDAO().update(heads, new String[] { JKBXHeaderVO.QZZT });
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public Collection<BxcontrastVO> queryJkContrast(JKBXVO[] selBxvos, boolean isBatch) throws BusinessException {
		Collection<BxcontrastVO> jkContrasVOs = null;
		try {
			String[] bxdKeys = new String[selBxvos.length];
			for (int i = 0; i < bxdKeys.length; i++) {
				bxdKeys[i] = selBxvos[i].getParentVO().getPk_jkbx();
			}
			String sqlJK = SqlUtils.getInStr(BxcontrastVO.PK_BXD, bxdKeys);
			jkContrasVOs = getJKBXDAO().retrieveContrastByClause(sqlJK);

			if (isBatch) {
				List<String> cjk_pc = new ArrayList<String>();
				for (BxcontrastVO vo : jkContrasVOs) {
					if (!StringUtils.isNullWithTrim(vo.getPk_pc())) {
						cjk_pc.add(vo.getPk_pc());
					}
				}

				Collection<BxcontrastVO> batchVos = getJKBXDAO().retrieveContrastByClause(
						SqlUtils.getInStr(BxcontrastVO.PK_PC, cjk_pc.toArray(new String[] {})));

				jkContrasVOs.addAll(batchVos);
			}

		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}
		
		fillUpHkJe(jkContrasVOs);
		return jkContrasVOs;

	}

	/**
	 * 查询分摊信息（表体）
	 * 
	 * @param parentVO
	 * @return
	 * @throws BusinessException
	 */
	public Collection<CShareDetailVO> queryCSharesVOS(JKBXHeaderVO[] parentVO) throws BusinessException {

		String key = CShareDetailVO.SRC_ID;

		Collection<CShareDetailVO> vos = null;
		try {
			vos = getJKBXDAO().retrieveCShareVoByClause(
					SqlUtils.getInStr(key, parentVO, JKBXHeaderVO.PK_JKBX) + " and " + CShareDetailVO.SRC_TYPE + "="
							+ IErmCostShareConst.CostShare_Bill_SCRTYPE_BX + " and dr=0 ");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	public void saveSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<SqdlrVO> insertVos = new ArrayList<SqdlrVO>();
		List<SqdlrVO> updateVos = new ArrayList<SqdlrVO>();
		for (SqdlrVO sqdlrVO : sqdlrVOs) {
			for (String role : roles) {
				if (sqdlrVO.getPk_authorize() != null) {
					if (!role.equals(sqdlrVO.getPk_roler())) {
						addNewSqdlrVO(insertVos, sqdlrVO, role);
					} else {
						updateVos.add(sqdlrVO);
					}
				} else {
					addNewSqdlrVO(insertVos, sqdlrVO, role);
				}

			}
		}
		try {
			dao.insertVOArray(insertVos.toArray(new SqdlrVO[insertVos.size()]));
			dao.updateVOArray(updateVos.toArray(new SqdlrVO[0]));
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private void addNewSqdlrVO(List<SqdlrVO> insertVos, SqdlrVO sqdlrVO, String role) throws BusinessException {
		if (!isAsigned(role, sqdlrVO.getPk_user(), (sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword()
				: ISqdlrKeyword.KEYWORD_BUSIUSER))) {
			SqdlrVO o = new SqdlrVO();
			o.setPk_roler(role);
			o.setPk_org(sqdlrVO.getPk_org());
			o.setPk_user(sqdlrVO.getPk_user());
			o.setKeyword(sqdlrVO.getKeyword() != null ? sqdlrVO.getKeyword() : ISqdlrKeyword.KEYWORD_BUSIUSER);
			o.setType(0);
			insertVos.add(o);
		}
	}

	public boolean isAsigned(String pk_role, String user, String keyword) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '" + user + "' and keyword = '" + keyword + "'";

		@SuppressWarnings("rawtypes")
		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	public boolean isAsigned(String pk_role, String user) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and pk_user = '" + user + "'";

		@SuppressWarnings("rawtypes")
		Collection c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
		if (c != null && c.size() > 0)
			return true;
		return false;
	}

	public Map<String, List<SqdlrVO>> querySqdlr(String[] pk_roles, String... ywy_corps) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		StringBuffer condition = null;
		try {
			condition = new StringBuffer(SqlUtils.getInStr("pk_roler", pk_roles));
			if (ywy_corps != null && ywy_corps.length > 0) {
				condition.append(" and ");
				condition.append(SqlUtils.getInStr("pk_corp", ywy_corps));
			}
			condition.append(" and type=0 ");

			@SuppressWarnings("rawtypes")
			Collection c = dao.retrieveByClause(SqdlrVO.class, condition.toString());
			Map<String, List<SqdlrVO>> vos = new HashMap<String, List<SqdlrVO>>();
			if (c != null) {
				for (Object o : c) {
					if (vos.get(((SqdlrVO) o).getPk_roler()) == null) {
						vos.put(((SqdlrVO) o).getPk_roler(), new ArrayList<SqdlrVO>());
					}
					vos.get(((SqdlrVO) o).getPk_roler()).add((SqdlrVO) o);
				}
			}
			return vos;
		} catch (Exception e1) {
			throw ExceptionHandler.handleException(e1);
		}

	}

	public void delSqdlrs(List<String> roles, SqdlrVO[] sqdlrVOs) throws BusinessException {
		try {
			batchDelete(roles, sqdlrVOs);
		} catch (DAOException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	private int batchDelete(List<String> roles, SqdlrVO[] sqdlrVOs) throws DAOException {
		PersistenceManager manager = null;
		int result = 0;
		try {
			manager = PersistenceManager.getInstance();
			manager.setMaxRows(100000);
			manager.setAddTimeStamp(true);
			JdbcSession session = manager.getJdbcSession();
			List<SQLParameter> listPara = new LinkedList<SQLParameter>();
			String sql = SQLHelper.getDeleteSQL(sqdlrVOs[0].getTableName(), new String[] { "pk_roler", "pk_user" });
			for (String role : roles) {
				for (SqdlrVO sqdlrVO : sqdlrVOs) {
					SQLParameter para = new SQLParameter();
					para.addParam(role);
					para.addParam(sqdlrVO.getPk_user());
					listPara.add(para);
				}
			}
			session.addBatch(sql, listPara.toArray(new SQLParameter[listPara.size()]));
			result = session.executeBatch();
		} catch (DbException e) {
			Logger.error(e.getMessage(), e);
			throw new DAOException(e.getMessage());
		} finally {
			if (manager != null)
				manager.release();
		}
		return result;
	}

	public Collection<JsConstrasVO> queryJsContrastsByJsd(String pk_jsd) throws BusinessException {

		Collection<JsConstrasVO> vos;
		try {
			vos = getJKBXDAO().retrieveJsContrastByClause(JsConstrasVO.PK_JSD + "='" + pk_jsd + "'");
		} catch (Exception e) {
			throw ExceptionHandler.handleException(e);
		}

		return vos;
	}

	public void unSettleFromArap(JKBXVO[] vos) throws BusinessException {
		unSettle(vos, false);
	}

	public JKBXVO[] unSettle(JKBXVO[] vos) throws BusinessException {
		return unSettle(vos, true);
	}

	public List<SqdlrVO> querySqdlr(String pk_user, String user_corp, String ywy_corp) throws BusinessException {
		nc.pubitf.rbac.IRolePubService service = NCLocator.getInstance().lookup(nc.pubitf.rbac.IRolePubService.class);
		try {
			RoleVO[] roleVOs = service.queryRoleByUserID(pk_user, user_corp);
			List<String> roles = new ArrayList<String>();
			for (RoleVO roleVO : roleVOs) {
				roles.add(roleVO.getPk_role());
			}
			Map<String, List<SqdlrVO>> sqdlrMap = querySqdlr(roles.toArray(new String[roles.size()]), ywy_corp);
			List<SqdlrVO> result = new ArrayList<SqdlrVO>();

			for (Map.Entry<String, List<SqdlrVO>> entry : sqdlrMap.entrySet()) {
				result.addAll(entry.getValue());
			}
			return result;
		} catch (BusinessException e) {
			throw ExceptionHandler.handleException(e);
		}
	}

	public void savedefSqdlrs(List<String> roles, Map<String, String[]> defMap) throws BusinessException {
		List<SqdlrVO> vos = new ArrayList<SqdlrVO>();

		for (Map.Entry<String, String[]> entry : defMap.entrySet()) {
			if (entry.getKey() != null) {
				deldefSqdlrs(roles, entry.getKey(), entry.getValue());
				for (String def : entry.getValue()) {
					SqdlrVO vo = new SqdlrVO();
					vo.setKeyword(entry.getKey());
					vo.setPk_user(def);
					vo.setType(0);
					vos.add(vo);
				}
			} else {
				deldefSqdlrs(roles, entry.getKey(), new String[] {});
			}
		}

		saveSqdlrs(roles, vos.toArray(new SqdlrVO[vos.size()]));

	}

	private void deldefSqdlrs(List<String> roles, String key, String[] pk_defs) throws BusinessException {
		BaseDAO dao = new BaseDAO();
		List<String> curDefls = new ArrayList<String>();
		for (String pk_def : pk_defs) {
			curDefls.add(pk_def);
		}
		List<SqdlrVO> list = new ArrayList<SqdlrVO>();
		for (String role : roles) {
			SqdlrVO[] sqdlrVOs = getAssigned(role, key);
			if (sqdlrVOs == null)
				break;
			for (SqdlrVO o : sqdlrVOs) {
				if (!curDefls.contains(o.getPk_user())) {
					list.add(o);
				}
			}
		}
		dao.deleteVOList(list);
	}

	private SqdlrVO[] getAssigned(String pk_role, String keyword) {
		BaseDAO dao = new BaseDAO();
		String condition = " pk_roler = '" + pk_role + "' and keyword = '" + keyword + "'";
		Collection<?> c = null;
		try {
			c = dao.retrieveByClause(SqdlrVO.class, condition);
		} catch (DAOException e) {
			nc.bs.logging.Log.getInstance(this.getClass()).error(e);
			;
		}
		if (c != null && c.size() > 0)
			return c.toArray(new SqdlrVO[c.size()]);
		return null;
	}

	public JKBXHeaderVO updateHeader(JKBXHeaderVO header, String[] fields) throws BusinessException {
		compareTs(new JKBXHeaderVO[] { header });
		updateHeaders(new JKBXHeaderVO[] { header }, fields);
		return header;
	}

	public JKBXDAO getJKBXDAO() throws SQLException {
		if (null == jkbxDAO) {
			try {
				jkbxDAO = new JKBXDAO();
			} catch (NamingException e) {
				Log.getInstance(this.getClass()).error(e.getMessage(), e);
				throw new SQLException(e.getMessage());
			}
		}
		return jkbxDAO;
	}

	private static boolean isCmpInstall(JKBXHeaderVO parentVO) throws BusinessException {
		boolean flag = BXUtil.isProductInstalled(parentVO.getPk_group(), BXConstans.TM_CMP_FUNCODE);
		if (!flag) {
			return false;
		}
		// 组织是否启用了现金管理
		String periord = NCLocator.getInstance().lookup(IOrgUnitPubService.class)
				.getOrgModulePeriodByOrgIDAndModuleID(parentVO.getPk_org(), BXConstans.TM_CMP_FUNCODE);
		if (periord == null) {
			Logger.debug("当前单据所属组织（借款报销单位)未启用现金管理");
			flag = false;
		}
		return flag;
	}

	private static String getBSLoginUser() {
		return InvocationInfoProxy.getInstance().getUserId();
	}

	public Map<String, String> getTsByPrimaryKey(String[] key, String tableName, String pkfield)
			throws BusinessException {
		try {
			return getJKBXDAO().getTsByPrimaryKeys(key, tableName, pkfield);
		} catch (Exception e) {
			throw ExceptionHandler.handleException(this.getClass(), e);

		}
	}

	private void checkDelete(JKBXVO vo) throws DataValidateException, BusinessException {
		// 校验单据状态
		VOStatusChecker.checkDeleteStatus(vo.getParentVO());

		// 非常用单据单据校验是否关帐
		if (!vo.getParentVO().isInit()) {
			VOChecker.checkErmIsCloseAcc(vo);
		}
	}

	@SuppressWarnings("unused")
	private void checkDataPermission(JKBXVO[] vos) throws BusinessException {
		List<JKBXVO> bxList = new ArrayList<JKBXVO>();
		List<JKBXVO> jkList = new ArrayList<JKBXVO>();
		boolean isNCClient=false;
		for (JKBXVO vo : vos) {
			// 是否期初单据
			boolean isQc = vo.getParentVO().getQcbz().booleanValue();
			// 是否常用单据
			boolean isInit = vo.getParentVO().isInit();
			
			//是否从NC客户端
			isNCClient=vo.isNCClient();
			// 期初常用单据不校验数据权限
			if (isQc || isInit) {
				continue;
			} else {
				if (vo.getParentVO().getDjdl().equals(BXConstans.BX_DJDL)) {
					bxList.add(vo);
				} else if (vo.getParentVO().getDjdl().equals(BXConstans.JK_DJDL)) {
					jkList.add(vo);
				}
			}
		}
		// 验证报销权限
		if(isNCClient){
			BXDataPermissionChkUtil.process(bxList.toArray(new JKBXVO[0]), BXConstans.ERMEXPRESOURCECODE,
					BXConstans.EXPDELOPTCODE, getBSLoginUser());
			
			// 验证借款权限
			BXDataPermissionChkUtil.process(jkList.toArray(new JKBXVO[0]), BXConstans.ERMLOANRESOURCECODE,
					BXConstans.LOANDELOPTCODE, getBSLoginUser());
		}
	}
}