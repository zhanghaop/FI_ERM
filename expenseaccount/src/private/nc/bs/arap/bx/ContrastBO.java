package nc.bs.arap.bx;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingException;

import nc.bs.er.util.SqlUtils;
import nc.bs.logging.Log;
import nc.impl.arap.bx.ArapBXBillPrivateImp;
import nc.itf.fi.pub.Currency;
import nc.jdbc.framework.ConnectionFactory;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.arap.bx.util.BxUIControlUtil;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BatchContratParam;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.ep.bx.JKBXVO;
import nc.vo.ep.bx.JKHeaderVO;
import nc.vo.er.check.VOChecker;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.erm.util.VOUtils;
import nc.vo.fipub.utils.KeyLock;
import nc.vo.pub.BusinessException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDateTime;
import nc.vo.pub.lang.UFDouble;

public class ContrastBO {

	private BXZbBO bxzbBO;

	public BXZbBO getBXZbBO() {
		if (null == bxzbBO) {
			bxzbBO = new BXZbBO();
		}
		return bxzbBO;
	}

	private JKBXDAO jkbxDAO;

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

	private BXBusItemDAO busItemDao = null;

	public BXBusItemDAO getBusItemDAO() throws BusinessException {
		if (null == busItemDao) {
			try {
				busItemDao = new BXBusItemDAO();
			} catch (NamingException e) {
				ExceptionHandler.handleException(e);
			}
		}
		return busItemDao;
	}

	/**
	 * 保存冲借款信息
	 * 回写借款单预计金额（报销单冲借款时用到）
	 * @param saveContrast
	 *            冲借款信息
	 * @param bxdvos
	 *            报销单信息
	 * @throws BusinessException
	 */
	public void saveContrast(List<BxcontrastVO> saveContrast, Vector<JKBXHeaderVO> bxdvos) throws BusinessException {
		if((saveContrast == null || saveContrast.size() == 0) && (bxdvos == null || bxdvos.size() == 0)){
			return;
		}

		// 增加会计期间一些信息
		ErContrastUtil.addinfotoContrastVos(saveContrast);
		Collection<BxcontrastVO> delContrasts = new ArrayList<BxcontrastVO>();//要删除的冲销信息

		try {
			if (bxdvos != null && bxdvos.size() != 0) {
				String[] bxPks = VOUtils.getAttributeValues(bxdvos.toArray(new JKBXHeaderVO[]{}), JKBXHeaderVO.PK_JKBX);
				delContrasts = getJKBXDAO().retrieveContrastByClause(SqlUtils.getInStr(BxcontrastVO.PK_BXD, bxPks));
			}
			
			if ((delContrasts == null || delContrasts.size() == 0)
					&& (saveContrast == null || saveContrast.size() == 0)) {
				return;
			}
			
			// 删除以前的冲销信息
			getJKBXDAO().delete(delContrasts.toArray(new BxcontrastVO[] {}));
			
			//保存现有的冲借款信息
			getJKBXDAO().save(saveContrast.toArray(new BxcontrastVO[] {}));
			
			if (saveContrast != null && saveContrast.size() > 0 && saveContrast.get(0).getSxbz() != null
					&& saveContrast.get(0).getSxbz() == BXStatusConst.SXBZ_TEMP) {
				return;// 报销单暂存，不回写借款单余额
			}

			// 借款单pk集合
			Set<String> jkPkSet = new HashSet<String>();
			
			for (BxcontrastVO contras : delContrasts) {
				jkPkSet.add(contras.getPk_jkd());
			}
			
			for(BxcontrastVO contras : saveContrast){
				jkPkSet.add(contras.getPk_jkd());
			}
			
			List<String> jkPkList = new ArrayList<String>();
			jkPkList.addAll(jkPkSet);
			
			if(jkPkSet.size() > 0){
				// 起校验作用，校验ts,并发处理
				KeyLock.dynamicLockWithException(jkPkList);
			}

			List<JKBXHeaderVO> jkVoList = getJKBXDAO().queryHeadersByWhereSql(
					" where " + SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, jkPkList.toArray(new String[] {})), BXConstans.JK_DJDL);
			
			BXBusItemVO[] busitems = getBusItemDAO().queryByBXVOPks(jkPkList.toArray(new String[] {}),true);

			HashMap<String, JKHeaderVO> jkdMap = new HashMap<String, JKHeaderVO>();
			HashMap<String, BXBusItemVO> busitemMap = new HashMap<String, BXBusItemVO>();

			for (JKBXHeaderVO vo : jkVoList) {
				jkdMap.put(vo.getPk_jkbx(), (JKHeaderVO) vo);
			}

			for (BXBusItemVO vo : busitems) {
				busitemMap.put(vo.getPk_busitem(), (BXBusItemVO) vo);
			}

			for (BxcontrastVO vo : delContrasts) {// 将删除的余额回写
				if(vo.getSxbz() == BXStatusConst.SXBZ_TEMP){
					continue;
				}
				
				JKHeaderVO headerVO = jkdMap.get(vo.getPk_jkd());
				headerVO.setYjye(headerVO.getYjye().add(vo.getYbje()));

				BXBusItemVO busitem = busitemMap.get(vo.getPk_busitem());
				busitem.setYjye(busitem.getYjye().add(vo.getYbje()));
			}
			
			List<String> adjuestBxds = new ArrayList<String>();
			for (BxcontrastVO vo : saveContrast) {// 将增加的冲借款的金额删掉
				JKHeaderVO jkHeadVo = jkdMap.get(vo.getPk_jkd());
				jkHeadVo.setYjye(jkHeadVo.getYjye().sub(vo.getYbje()));
				
				if(jkHeadVo.getYjye().compareTo(UFDouble.ZERO_DBL) < 0){//预计余额小于0的情况下
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0132")/* @res "借款单余额不足，请重新冲借款!" */);
				}

				BXBusItemVO busitem = busitemMap.get(vo.getPk_busitem());
				busitem.setYjye(busitem.getYjye().sub(vo.getYbje()));
				
				if(busitem.getYjye().compareTo(UFDouble.ZERO_DBL) < 0){//预计余额小于0的情况下
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("upp2012v575_0",
					"0upp2012V575-0132")/* @res "借款单余额不足，请重新冲借款!" */);
				}

				// 处理外币业务时，借款单原币被冲为0时，本币没冲为0 的特殊情况
				adjuestBxds.addAll(dealWbSpecialCjk(saveContrast, jkHeadVo, vo));

				if (jkHeadVo.getYjye().compareTo(jkHeadVo.getYbje()) > 0) {
					throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011",
							"UPP2011-000386")/*
											 * 保存冲借款金额失败，冲借款的金额超出借款单的余额！
											 */);
				}
			}

			getJKBXDAO().update(jkdMap.values().toArray(new JKHeaderVO[] {}), new String[] { JKBXHeaderVO.YJYE });//借款单表头预计余额
			getJKBXDAO().update(busitemMap.values().toArray(new BXBusItemVO[] {}), new String[] { JKBXHeaderVO.YJYE });//借款单表体预计余额更新

			// 根据冲销信息调整报销单中冲借款信息
			for (String pk_bxd : adjuestBxds) {
				adjuestBXHeadContrastInfo(pk_bxd);
			}
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
	}

	/**
	 * @param saveContrast
	 * @param headerVO
	 * @param bxcontrastVO
	 * @return
	 * @return
	 * @throws BusinessException
	 * 
	 *             处理特殊情况下的冲借款本币数据 外币借款，外币报销，两次汇率不一致，进行冲借款时会发生汇兑的问题， 按照如下原则进行调整：
	 *             当借款单的原币金额被冲减为0时，将本币金额也冲减为0
	 * 
	 *             方法：调整冲借款的本币金额 不调整冲借款的费用本币金额
	 * 
	 */
	public List<String> dealWbSpecialCjk(List<BxcontrastVO> saveContrast, JKBXHeaderVO headerVO, BxcontrastVO bxcontrastVO) throws BusinessException {

		List<String> adjuestBxds = new ArrayList<String>();

		if (headerVO.getYjye().compareTo(new UFDouble(0)) == 0 && !headerVO.getBzbm().equals(Currency.getOrgLocalCurrPK(headerVO.getPk_group()))) {
			Collection<BxcontrastVO> contrasts = getBXZbBO().queryContrasts(headerVO);
			JKBXHeaderVO headervotemp = (JKBXHeaderVO) headerVO.clone();
			for (BxcontrastVO contrast : contrasts) {
				if (contrast.getSxbz().intValue() == 0) {
					headervotemp.setBbye(headervotemp.getBbye().sub(contrast.getCjkbbje()));
				}
			}
			for (BxcontrastVO contrast : saveContrast) {
				if (contrast.getPk_jkd().equals(headerVO.getPk_jkbx())) {
					headervotemp.setBbye(headervotemp.getBbye().sub(contrast.getCjkbbje()));
				}
			}
			if (!headervotemp.getBbye().equals(new UFDouble(0))) {
				bxcontrastVO.setCjkbbje(bxcontrastVO.getCjkbbje().add(headervotemp.getBbye()));
				bxcontrastVO.setBbje(bxcontrastVO.getCjkbbje());

				adjuestBxds.add(bxcontrastVO.getPk_bxd());
			}
		}

		return adjuestBxds;
	}

	private void adjuestBXHeadContrastInfo(String pk_bxd) throws BusinessException {
		// 调整受影响的报销单的冲借款本币金额，支付本币金额，还款本币金额
		List<JKBXVO> bxds = new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(new String[] { pk_bxd }, BXConstans.BX_DJDL);
		if (bxds == null || bxds.size() != 1) {
			throw new BusinessException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011", "UPP2011-000422")/*
																														 * @res
																														 * "查询报销单信息失败!"
																														 */);
		}
		JKBXVO bxvo = bxds.get(0);

		UFDouble cjkybje = new UFDouble(0);
		UFDouble cjkbbje = new UFDouble(0);
		UFDouble groupcjkbbje = new UFDouble(0);
		UFDouble globalcjkbbje = new UFDouble(0);

		BxcontrastVO[] bxcontrastVOs = bxvo.getContrastVO();
		if (bxcontrastVOs != null) {
			for (BxcontrastVO vo : bxcontrastVOs) {// 将冲销信息中的冲销金额累加
				cjkybje = cjkybje.add(vo.getCjkybje());
				cjkbbje = cjkbbje.add(vo.getCjkbbje());
				groupcjkbbje = groupcjkbbje.add(vo.getGroupcjkbbje());
				globalcjkbbje = globalcjkbbje.add(vo.getGlobalcjkbbje());
			}
		}

//		VOChecker vochecker = new VOChecker();
//		vochecker.adjuestCjkje(bxvo.getParentVO(), cjkybje, cjkbbje, groupcjkbbje, globalcjkbbje);
		VOChecker.adjuestCjkje(bxvo.getParentVO(), cjkybje, cjkbbje, groupcjkbbje, globalcjkbbje);

		getBXZbBO().updateHeader(bxvo.getParentVO(), new String[] { JKBXHeaderVO.CJKBBJE, JKBXHeaderVO.HKBBJE, JKBXHeaderVO.ZFBBJE });
	}

	public void effectContrast(JKBXVO bxVo) throws BusinessException {
		String pk_jkbx = bxVo.getParentVO().getPk_jkbx();// 报销单pk

		try {// 冲借款关联关系VO
			Collection<BxcontrastVO> vos = getJKBXDAO().retrieveContrastByClause(BxcontrastVO.PK_BXD + "='" + pk_jkbx + "'");

			if (vos == null || vos.size() == 0)
				return;

			String jkdPks[] = new String[vos.size()];
			String[] pk_busitems = new String[vos.size()];
			HashMap<String, UFDouble[]> jkdMap = new HashMap<String, UFDouble[]>();
			HashMap<String, UFDouble[]> busitemMap = new HashMap<String, UFDouble[]>();
			
			BxcontrastVO[] contrastVos = vos.toArray(new BxcontrastVO[]{});
			for (int i = 0;i < contrastVos.length; i++) {
				BxcontrastVO vo = contrastVos[i];
				vo.setSxrq(bxVo.getParentVO().getShrq().getDate());
				vo.setSxbz(Integer.valueOf(BXStatusConst.SXBZ_VALID));
				
				jkdPks[i] = vo.getPk_jkd();
				pk_busitems[i] = vo.getPk_busitem();
				
				setKeyValueMap(jkdMap, vo, jkdPks[i]);
				setKeyValueMap(busitemMap, vo, pk_busitems[i]);
			}

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, jkdPks);
			// 借款单集合
			Collection<JKBXHeaderVO> headVos = getJKBXDAO().queryHeadersByWhereSql(" where " + inStr, BXConstans.JK_DJDL);
			for (Iterator<JKBXHeaderVO> iter = headVos.iterator(); iter.hasNext();) {
				JKBXHeaderVO headvo = iter.next();
				headvo.setYbye(headvo.getYbye().sub(jkdMap.get(headvo.getPk_jkbx())[0]));
				headvo.setBbye(headvo.getBbye().sub(jkdMap.get(headvo.getPk_jkbx())[1]));
				headvo.setGroupbbye(headvo.getGroupbbye().sub(jkdMap.get(headvo.getPk_jkbx())[2]));
				headvo.setGlobalbbye(headvo.getGlobalbbye().sub(jkdMap.get(headvo.getPk_jkbx())[3]));
				
				if (headvo.getYbye().doubleValue() == 0D) {
					headvo.setContrastenddate(bxVo.getParentVO().getShrq().getDate());
					headvo.setQzzt(Integer.valueOf(1));
				} else {
					headvo.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));
					headvo.setQzzt(Integer.valueOf(0));
				}
			}
			
			BXBusItemVO[] busitems = getBusItemDAO().queryByPks(pk_busitems,true);
			for (int i = 0; i < busitems.length; i++) {
				BXBusItemVO vo = busitems[i];
				String pk_busitem = vo.getPk_busitem();
				if(busitemMap.get(pk_busitem) != null){
					vo.setYbye(vo.getYbye().sub(busitemMap.get(pk_busitem)[0]));
					vo.setBbye(vo.getBbye().sub(busitemMap.get(pk_busitem)[1]));
					vo.setGroupbbye(vo.getGroupbbye().sub(busitemMap.get(pk_busitem)[2]));
					vo.setGlobalbbye(vo.getGlobalbbye().sub(busitemMap.get(pk_busitem)[3]));
				}
			}
			
			//更新借款单表头
			getJKBXDAO().update(headVos.toArray(new JKBXHeaderVO[] {}),
					new String[] { JKBXHeaderVO.YBYE, JKBXHeaderVO.BBYE, JKBXHeaderVO.GROUPBBYE, JKBXHeaderVO.GLOBALBBYE,
					JKBXHeaderVO.CONTRASTENDDATE, JKBXHeaderVO.QZZT });
			
			//更新借款单业务行余额字段
			getBusItemDAO().update(busitems, new String[]{BXBusItemVO.YBYE , JKBXHeaderVO.BBYE, 
					JKBXHeaderVO.GROUPBBYE, JKBXHeaderVO.GLOBALBBYE});
			//更新冲借款
			getJKBXDAO().update(vos.toArray(new BxcontrastVO[] {}));
			
			//设置冲销信息（冲销生效日期）
			bxVo.setContrastVO(vos.toArray(new BxcontrastVO[] {}));
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * 取消冲借款生效
	 * 将借款单中余额回写
	 * @param bxVo 借款单表头vo
	 * @throws BusinessException
	 */
	public void unEffectContrast(JKBXVO bxVo) throws BusinessException {
		String pk_jkbx = bxVo.getParentVO().getPk_jkbx();

		try {
			Collection<BxcontrastVO> vos = getJKBXDAO().retrieveContrastByClause(BxcontrastVO.PK_BXD + "='" + pk_jkbx + "'");

			if (vos == null || vos.size() == 0)
				return;

			String pks[] = new String[vos.size()];
			String[] pk_busitems = new String[vos.size()];
			
			//借款单表头对照金额
			HashMap<String, UFDouble[]> jkdMap = new HashMap<String, UFDouble[]>();
			
			//借款单业务行对照金额
			HashMap<String, UFDouble[]> busitemMap = new HashMap<String, UFDouble[]>();
			
			BxcontrastVO[] contrastVos = vos.toArray(new BxcontrastVO[]{});
			for (int i = 0;i < contrastVos.length; i++) {
				BxcontrastVO vo = contrastVos[i];
				vo.setSxrq(null);
				vo.setSxbz(Integer.valueOf(BXStatusConst.SXBZ_NO));
				
				pks[i] = vo.getPk_jkd();
				pk_busitems[i] = vo.getPk_busitem();
				
				setKeyValueMap(jkdMap, vo, pks[i]);//借款单可能对应多个冲销行，借款单业务行同样可能对应多个冲销行
				setKeyValueMap(busitemMap, vo, pk_busitems[i]);
			}

			String inStr = SqlUtils.getInStr(JKBXHeaderVO.PK_JKBX, pks);
			Collection<JKBXHeaderVO> headVos = getJKBXDAO().queryHeadersByWhereSql(" where " + inStr, BXConstans.JK_DJDL);
			
			//设置借款单表头余额各字段值
			for (Iterator<JKBXHeaderVO> iter = headVos.iterator(); iter.hasNext();) {
				JKBXHeaderVO headvo = iter.next();
				headvo.setYbye(headvo.getYbye().add(jkdMap.get(headvo.getPk_jkbx())[0]));
				headvo.setBbye(headvo.getBbye().add(jkdMap.get(headvo.getPk_jkbx())[1]));
				headvo.setGroupbbye(headvo.getGroupbbye().add(jkdMap.get(headvo.getPk_jkbx())[2]));
				headvo.setGlobalbbye(headvo.getGlobalbbye().add(jkdMap.get(headvo.getPk_jkbx())[3]));
				headvo.setContrastenddate(new UFDate(BXConstans.DEFAULT_CONTRASTENDDATE));
				headvo.setQzzt(Integer.valueOf(0));
			}
			
			//设置借款单表体余额各字段
			BXBusItemVO[] busitems = getBusItemDAO().queryByPks(pk_busitems,true);
			for (int i = 0; i < busitems.length; i++) {
				BXBusItemVO vo = busitems[i];
				String pk_busitem = vo.getPk_busitem();
				
				if(busitemMap.get(pk_busitem) != null){
					vo.setYbye(vo.getYbye().add(busitemMap.get(pk_busitem)[0]));
					vo.setBbye(vo.getBbye().add(busitemMap.get(pk_busitem)[1]));
					vo.setGroupbbye(vo.getGroupbbye().add(busitemMap.get(pk_busitem)[2]));
					vo.setGlobalbbye(vo.getGlobalbbye().add(busitemMap.get(pk_busitem)[3]));
				}
			}
			
			//更新借款单表头
			getJKBXDAO().update(headVos.toArray(new JKBXHeaderVO[] {}),
					new String[] { JKBXHeaderVO.YBYE, JKBXHeaderVO.BBYE, JKBXHeaderVO.GROUPBBYE, JKBXHeaderVO.GLOBALBBYE,
					JKBXHeaderVO.CONTRASTENDDATE, JKBXHeaderVO.QZZT });
			
			//更新借款单业务行余额字段
			getBusItemDAO().update(busitems, new String[]{BXBusItemVO.YBYE , JKBXHeaderVO.BBYE, 
					JKBXHeaderVO.GROUPBBYE, JKBXHeaderVO.GLOBALBBYE});
			
			getJKBXDAO().update(vos.toArray(new BxcontrastVO[] {}));
			
			bxVo.setContrastVO(vos.toArray(new BxcontrastVO[] {}));
		} catch (BusinessException e) {
			ExceptionHandler.handleException(e);
		} catch (SQLException e) {
			ExceptionHandler.handleException(e);
		}
	}
	
	/**
	 * map中对应的金额为
	 * <li>[0] 原币金额
	 * <li>[1] 本币金额
	 * <li>[2] 集团冲借款本币金额
	 * <li>[3] 全集冲借款本币金额
	 * @param map 
	 * @param vo 冲销vo
	 * @param key map中对应的key
	 */
	private void setKeyValueMap(HashMap<String, UFDouble[]> map, BxcontrastVO vo, String key) {
		if (map.containsKey(key)) {
			UFDouble[] values = map.get(key);
			values[0] = values[0].add(vo.getYbje());
			values[1] = values[1].add(vo.getBbje());
			values[2] = values[2].add(vo.getGroupcjkbbje());
			values[3] = values[3].add(vo.getGlobalcjkbbje());
			map.put(key, values);
		} else {
			map.put(key, new UFDouble[] { vo.getYbje(), vo.getBbje(), vo.getGroupcjkbbje(), vo.getGlobalcjkbbje()});
		}
	}
	
	

	public void deleteByPK_bxd(String[] pk_bxd) throws BusinessException, SQLException {
		Collection<BxcontrastVO> contrastVos = getJKBXDAO().retrieveContrastByClause(SqlUtils.getInStr(BxcontrastVO.PK_BXD, pk_bxd));
		
		if(contrastVos != null && contrastVos.size() > 0){
			
			getJKBXDAO().delete(contrastVos.toArray(new BxcontrastVO[] {}));
			
			for (BxcontrastVO contrastVo : contrastVos) {
				if (contrastVo.getSxbz() != null && contrastVo.getSxbz() == BXStatusConst.SXBZ_TEMP) {
					return;
				} else {
					break;
				}
			}
			
			//更新借款单余额
			HashMap<String, UFDouble> jkMap = new HashMap<String, UFDouble>();
			HashMap<String, UFDouble> busItemMap = new HashMap<String, UFDouble>();
			for (BxcontrastVO vo : contrastVos) {
				String key = vo.getPk_jkd();
				UFDouble cjkybje = vo.getCjkybje();
				setKeyValueMap(jkMap, key, cjkybje);
				
				String pk_busitem = vo.getPk_busitem();
				setKeyValueMap(busItemMap, pk_busitem, cjkybje);
			}
			
			updateJkdYjye(jkMap);
			updateJkdBusItemYjye(busItemMap);// 更新业务行
		}
	}

	private UFDouble setKeyValueMap(HashMap<String, UFDouble> map, String key, UFDouble value) {
		if (map.containsKey(key)) {
			UFDouble temp = map.get(key);
			value = value.add(temp);
			map.put(key, value);
		} else {
			map.put(key, value);
		}
		return value;
	}

	// 更新借款单的可用余额
	private void updateJkdYjye(HashMap<String, UFDouble> jkYeMap) throws BusinessException {
		Connection con = null;
		PreparedStatement stat = null;
		String sql = "update er_jkzb set yjye=yjye+? where pk_jkbx=?";
		try {
			con = ConnectionFactory.getConnection();
			stat = con.prepareStatement(sql);
			for (Map.Entry<String, UFDouble> entry : jkYeMap.entrySet()) {
				stat.setDouble(1, entry.getValue().doubleValue());
				stat.setString(2, entry.getKey());
				stat.addBatch();
			}
			stat.executeBatch();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}finally{
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
	}
	
	/**
	 * 更新借款单业务行的预计余额
	 * @param busItemMap
	 * @throws BusinessException
	 */
	private void updateJkdBusItemYjye(HashMap<String, UFDouble> busItemMap) throws BusinessException {
		Connection con = null;
		PreparedStatement stat = null;
		String sql = "update er_busitem set yjye=yjye+? where pk_busitem = ?";
		try {
			con = ConnectionFactory.getConnection();
			stat = con.prepareStatement(sql);
			for (Map.Entry<String, UFDouble> entry : busItemMap.entrySet()) {
				stat.setDouble(1, entry.getValue().doubleValue());
				stat.setString(2, entry.getKey());
				stat.addBatch();
			}
			stat.executeBatch();
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
		}finally{
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
	}

	public List<BxcontrastVO> batchContrast(JKBXVO[] selBxvos, List<String> mode_data, BatchContratParam param) throws BusinessException {

		UFDate cxrq = param.getCxrq();
		//过滤掉拉单的借款单
		String sql = " where zb.yjye>0 and zb.dr=0 and zb.djzt=3 and zb.djrq<='" + cxrq + "' and (pk_item is null or pk_item='~')" ;
		
		
		List<JKBXHeaderVO> jkds = getBXZbBO().queryHeadersByWhereSql(sql, BXConstans.JK_DJDL);

		List<JKBXHeaderVO> bxds = new ArrayList<JKBXHeaderVO>();

		for (JKBXVO vo : selBxvos) {
			bxds.add(vo.getParentVO());
		}

		JKBXHeaderVO[] bxdArray = bxds.toArray(new JKBXHeaderVO[] {});

		JKBXHeaderVO[] jkdArray = jkds.toArray(new JKBXHeaderVO[] {});

		getBXZbBO().compareTs(bxdArray);
		getBXZbBO().compareTs(jkdArray);

		List<BxcontrastVO> results = new BatchContrastBo().batchContrast(bxdArray, jkdArray, mode_data.toArray(new String[] {}), param);
		
		return results;
	}
	
	
	public void saveBatchContrastVO(List<BxcontrastVO> selectedData, boolean delete) throws BusinessException {


		List<String> adjuestBxds = new ArrayList<String>();

		Set<String> jkdSet = new HashSet<String>();
		Set<String> bxdSet = new HashSet<String>();
		ErContrastUtil.addinfotoContrastVos(selectedData);
		Map<String,BxcontrastVO> jkContrastMap = new HashMap<String, BxcontrastVO>();

		for (BxcontrastVO vo : selectedData) {
			String pk_bxd = vo.getPk_bxd();
			String pk_jkd = vo.getPk_jkd();

			jkdSet.add(pk_jkd);
			bxdSet.add(pk_bxd);
			jkContrastMap.put(pk_jkd, vo);
		}

		List<JKBXHeaderVO> jkds = getBXZbBO().queryHeadersByPrimaryKeys(jkdSet.toArray(new String[] {}), BXConstans.JK_DJDL);
		List<JKBXVO> bxds = new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(bxdSet.toArray(new String[] {}), BXConstans.BX_DJDL);

		Map<String, JKBXVO> bxdMap = new HashMap<String, JKBXVO>();
		Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
		HashMap<String, BXBusItemVO> busitemMap = new HashMap<String, BXBusItemVO>();
		
		for (JKBXHeaderVO vo : jkds) {
			jkdMap.put(vo.getPk_jkbx(), vo);
		}
		
		//modify 将借款单的业务行先放入缓存
		try {
			BXBusItemVO[] busitems = getBusItemDAO().queryByBXVOPks(
					jkdSet.toArray(new String[0]), true);
			for (BXBusItemVO busitem : busitems) {
				busitemMap.put(busitem.getPk_busitem(), busitem);
			}
		} catch (SQLException e) {
			ExceptionHandler.consume(e);
		}
		
		//新增借款单业务行
		List<BxcontrastVO> newSelectedData = new ArrayList<BxcontrastVO>();
		if(delete){
			newSelectedData.addAll(selectedData);
		}
		else{
			for(BxcontrastVO contrastvo : selectedData){
				UFDouble cjkybje = contrastvo.getCjkybje();
				for (Map.Entry<String, BXBusItemVO> busitem : busitemMap.entrySet()) {
					BXBusItemVO vo=busitem.getValue();
					
					if(vo.getYjye().compareTo(UFDouble.ZERO_DBL)>0){
						if(cjkybje.compareTo(UFDouble.ZERO_DBL)>0){
							//busitemMap.put(vo.getPk_busitem(), (BXBusItemVO) vo);
							// 转换借款单主表冲借款到借款单业务子表
							BxcontrastVO newvo = (BxcontrastVO) contrastvo.clone();
							newvo.setPk_busitem(vo.getPk_busitem());
							//modify 20130814:对于有多行的借款单时，应需要重新设置借款单的冲借款金额和原币金额
							if(vo.getYjye().compareTo(cjkybje)>=0){
								newvo.setCjkybje(cjkybje);
								newvo.setYbje(cjkybje);
								vo.setYjye(vo.getYjye().sub(cjkybje));
								cjkybje=UFDouble.ZERO_DBL;
							}else{
								newvo.setCjkybje(vo.getYjye());
								newvo.setYbje(vo.getYjye());
								cjkybje=cjkybje.sub(vo.getYjye());
								vo.setYjye(UFDouble.ZERO_DBL);
							}
							newSelectedData.add(newvo);
						}
					}
				}
		}
		}
		

		for (JKBXVO vo : bxds) {
			vo.setBxoldvo((JKBXVO) vo.clone());
			vo.getBxoldvo().setContrastVO(vo.getContrastVO());

			if (!delete) {
				vo.setContrastVO(null);
			}
			bxdMap.put(vo.getParentVO().getPk_jkbx(), vo);
		}
		
		for (BxcontrastVO vo : newSelectedData) {

			String pk_bxd = vo.getPk_bxd();
			String pk_jkd = vo.getPk_jkd();
			UFDouble cjkybje = vo.getCjkybje();

			JKBXVO bxd = bxdMap.get(pk_bxd);

			if (delete) {
				removeContrastForBatch(bxd, vo);
			} else {
				addContrastForBatch(bxd, vo);
			}

			JKBXHeaderVO jkd = jkdMap.get(pk_jkd);

			if (delete) {
				jkd.setYjye(jkd.getYjye().add(cjkybje));
				
				//新增业务行
				BXBusItemVO busitem = busitemMap.get(vo.getPk_busitem());
				busitem.setYjye(busitem.getYjye().add(vo.getYbje()));				
			} else {
				jkd.setYjye(jkd.getYjye().sub(cjkybje));
				
				//新增业务行
				BXBusItemVO busitem = busitemMap.get(vo.getPk_busitem());
				busitem.setYjye(busitem.getYjye().sub(vo.getYbje()));
				
				// 处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
				adjuestBxds.addAll(dealWbSpecialCjk(selectedData, jkd, vo));
			}
		}

		// 处理多张报销单冲销同一笔借款单的情况，ts校验会失败，后面的冲销不进行ts的校验
		Set<String> oldJkdPks = new HashSet<String>();
		for (JKBXVO bxvo2 : bxdMap.values()) {

			bxvo2.setHasCrossCheck(true);
			bxvo2.setHasJkCheck(true);
			bxvo2.setHasNtbCheck(true);
			bxvo2.setHasZjjhCheck(true);

			boolean isVerify = bxvo2.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Verified);
			String shr = bxvo2.getParentVO().getApprover();
			// FIXME 审核日期改动
			UFDateTime shrq = bxvo2.getParentVO().getShrq();
			BxcontrastVO[] tempBxcontrastVO = bxvo2.getContrastVO();
			if (isVerify) {
				getBXZbBO().beforeActInf(bxvo2, BXZbBO.MESSAGE_UNAUDIT);
				getBXZbBO().unAudit(new JKBXVO[] { bxvo2 });
				getBXZbBO().afterActInf(bxvo2, BXZbBO.MESSAGE_UNAUDIT);
			}
			bxvo2.setContrastVO(tempBxcontrastVO);
			bxvo2.setContrastUpdate(true);
			List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();
			BxcontrastVO[] contrastVO = bxvo2.getContrastVO();
			for (BxcontrastVO bxcontrastvo : contrastVO) {
				if (oldJkdPks.contains(bxcontrastvo.getPk_jkd())) {
					bxcontrastvo.setTs(null);
				}
				list.add(bxcontrastvo);
				oldJkdPks.add(bxcontrastvo.getPk_jkd());
			}
			bxvo2 = new BxUIControlUtil().doContrast(bxvo2, list);
			getBXZbBO().update(new JKBXVO[] { bxvo2 });

			if (isVerify) {
				bxvo2.getParentVO().setApprover(shr);
				bxvo2.getParentVO().setShrq(shrq);

				getBXZbBO().beforeActInf(bxvo2, BXZbBO.MESSAGE_AUDIT);
				getBXZbBO().audit(new JKBXVO[] { bxvo2 });
				getBXZbBO().afterActInf(bxvo2, BXZbBO.MESSAGE_AUDIT);
			}
		}

		// 处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
		for (String pk_bxd : adjuestBxds) {
			adjuestBXHeadContrastInfo(pk_bxd);
		}
	}
	
	public void saveBatchContrast(List<BxcontrastVO> selectedData, boolean delete) throws BusinessException {

		List<String> adjuestBxds = new ArrayList<String>();

		Set<String> jkdSet = new HashSet<String>();
		Set<String> bxdSet = new HashSet<String>();
		ErContrastUtil.addinfotoContrastVos(selectedData);
		Map<String,BxcontrastVO> jkContrastMap = new HashMap<String, BxcontrastVO>();

		for (BxcontrastVO vo : selectedData) {
			String pk_bxd = vo.getPk_bxd();
			String pk_jkd = vo.getPk_jkd();

			jkdSet.add(pk_jkd);
			bxdSet.add(pk_bxd);
			jkContrastMap.put(pk_jkd, vo);
		}

		List<JKBXHeaderVO> jkds = getBXZbBO().queryHeadersByPrimaryKeys(jkdSet.toArray(new String[] {}), BXConstans.JK_DJDL);
		List<JKBXVO> bxds = new ArapBXBillPrivateImp().queryVOsByPrimaryKeys(bxdSet.toArray(new String[] {}), BXConstans.BX_DJDL);

		Map<String, JKBXVO> bxdMap = new HashMap<String, JKBXVO>();
		Map<String, JKBXHeaderVO> jkdMap = new HashMap<String, JKBXHeaderVO>();
		HashMap<String, BXBusItemVO> busitemMap = new HashMap<String, BXBusItemVO>();
		
		for (JKBXHeaderVO vo : jkds) {
			jkdMap.put(vo.getPk_jkbx(), vo);
		}

		for (JKBXVO vo : bxds) {
			vo.setBxoldvo((JKBXVO) vo.clone());
			vo.getBxoldvo().setContrastVO(vo.getContrastVO());

			if (!delete) {
				vo.setContrastVO(null);
			}
			bxdMap.put(vo.getParentVO().getPk_jkbx(), vo);
		}
		List<BxcontrastVO> newSelectedData = new ArrayList<BxcontrastVO>();
		//新增借款单业务行
		try {
			BXBusItemVO[] busitems = getBusItemDAO().queryByBXVOPks(jkdSet.toArray(new String[] {}),true);
			for (BXBusItemVO vo : busitems) {
				busitemMap.put(vo.getPk_busitem(), (BXBusItemVO) vo);
				// 转换借款单主表冲借款到借款单业务子表
				BxcontrastVO newvo = (BxcontrastVO) jkContrastMap.get(vo.getPk_jkbx()).clone();
				newvo.setPk_busitem(vo.getPk_busitem());
				newSelectedData.add(newvo);
			}
		} catch (SQLException e) {
			ExceptionHandler.consume(e);
		}

		
		for (BxcontrastVO vo : newSelectedData) {

			String pk_bxd = vo.getPk_bxd();
			String pk_jkd = vo.getPk_jkd();
			UFDouble cjkybje = vo.getCjkybje();

			JKBXVO bxd = bxdMap.get(pk_bxd);

			if (delete) {
				removeContrastForBatch(bxd, vo);
			} else {
				addContrastForBatch(bxd, vo);
			}

			JKBXHeaderVO jkd = jkdMap.get(pk_jkd);

			if (delete) {
				jkd.setYjye(jkd.getYjye().add(cjkybje));
				
				//新增业务行
				BXBusItemVO busitem = busitemMap.get(vo.getPk_busitem());
				busitem.setYjye(busitem.getYjye().add(vo.getYbje()));				
			} else {
				jkd.setYjye(jkd.getYjye().sub(cjkybje));
				
				//新增业务行
				BXBusItemVO busitem = busitemMap.get(vo.getPk_busitem());
				busitem.setYjye(busitem.getYjye().sub(vo.getYbje()));
				
				// 处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
				adjuestBxds.addAll(dealWbSpecialCjk(selectedData, jkd, vo));
			}
		}

		// 处理多张报销单冲销同一笔借款单的情况，ts校验会失败，后面的冲销不进行ts的校验
		Set<String> oldJkdPks = new HashSet<String>();
		for (JKBXVO bxvo2 : bxdMap.values()) {

			bxvo2.setHasCrossCheck(true);
			bxvo2.setHasJkCheck(true);
			bxvo2.setHasNtbCheck(true);
			bxvo2.setHasZjjhCheck(true);

			boolean isVerify = bxvo2.getParentVO().getDjzt().equals(BXStatusConst.DJZT_Verified);
			String shr = bxvo2.getParentVO().getApprover();
			// FIXME 审核日期改动
			UFDateTime shrq = bxvo2.getParentVO().getShrq();
			BxcontrastVO[] tempBxcontrastVO = bxvo2.getContrastVO();
			if (isVerify) {
				getBXZbBO().beforeActInf(bxvo2, BXZbBO.MESSAGE_UNAUDIT);
				getBXZbBO().unAudit(new JKBXVO[] { bxvo2 });
				getBXZbBO().afterActInf(bxvo2, BXZbBO.MESSAGE_UNAUDIT);
			}
			bxvo2.setContrastVO(tempBxcontrastVO);
			bxvo2.setContrastUpdate(true);
			List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();
			BxcontrastVO[] contrastVO = bxvo2.getContrastVO();
			for (BxcontrastVO bxcontrastvo : contrastVO) {
				if (oldJkdPks.contains(bxcontrastvo.getPk_jkd())) {
					bxcontrastvo.setTs(null);
				}
				list.add(bxcontrastvo);
				oldJkdPks.add(bxcontrastvo.getPk_jkd());
			}
			bxvo2 = new BxUIControlUtil().doContrast(bxvo2, list);
			getBXZbBO().update(new JKBXVO[] { bxvo2 });

			if (isVerify) {
				bxvo2.getParentVO().setApprover(shr);
				bxvo2.getParentVO().setShrq(shrq);

				getBXZbBO().beforeActInf(bxvo2, BXZbBO.MESSAGE_AUDIT);
				getBXZbBO().audit(new JKBXVO[] { bxvo2 });
				getBXZbBO().afterActInf(bxvo2, BXZbBO.MESSAGE_AUDIT);
			}
		}

		// 处理外币业务时，借款单原币被冲为0时，本币不能冲为0 的特殊情况
		for (String pk_bxd : adjuestBxds) {
			adjuestBXHeadContrastInfo(pk_bxd);
		}
	}

	private void addContrastForBatch(JKBXVO bxd, BxcontrastVO vo) {
		BxcontrastVO[] contrastVO = bxd.getContrastVO();
		List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();
		if (contrastVO != null) {
			for (BxcontrastVO convo : contrastVO) {
				list.add(convo);
			}
		}
		list.add(vo);
		bxd.setContrastVO(list.toArray(new BxcontrastVO[] {}));
	}

	private void removeContrastForBatch(JKBXVO bxd, BxcontrastVO vo) {

		BxcontrastVO[] contrastVO = bxd.getContrastVO();
		List<BxcontrastVO> list = new ArrayList<BxcontrastVO>();
		if (contrastVO != null) {
			for (BxcontrastVO convo : contrastVO) {
				if (!convo.getPk_bxcontrast().equals(vo.getPk_bxcontrast()))
					list.add(convo);
			}
		}
		bxd.setContrastVO(list.toArray(new BxcontrastVO[] {}));
	}

}