package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.fi.pub.Currency;
import nc.pubitf.accperiod.AccountCalendar;
import nc.vo.arap.bx.util.BXStatusConst;
import nc.vo.ep.bx.BXBusItemVO;
import nc.vo.ep.bx.BxcontrastVO;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.er.exception.ExceptionHandler;
import nc.vo.pub.BusinessRuntimeException;
import nc.vo.pub.lang.UFDate;
import nc.vo.pub.lang.UFDouble;

public class ErContrastUtil {
	//补充会计年度等信息
	public static void addinfotoContrastVos(List<BxcontrastVO> saveContrast) {
		for(BxcontrastVO vo :saveContrast){
			 try {
				 AccountCalendar periodVO = AccountCalendar.getInstance();
				 periodVO.setDate(vo.getCxrq());
				 vo.setCxnd(periodVO.getYearVO().getPeriodyear()); 
				 vo.setCxqj(periodVO.getMonthVO().getAccperiodmth());
			 } catch ( Exception e3) {
				  ExceptionHandler.consume( e3);
			 }
			 
		}
		
	}
	
	public static List<BxcontrastVO> dealContrastForNew(JKBXHeaderVO parentVO,BxcontrastVO[] bxcontrastVOs,BXBusItemVO[] items) {
		List<BxcontrastVO> contrastVos=new ArrayList<BxcontrastVO>();
		UFDouble bbhl = parentVO.getBbhl();
		UFDouble globalbbhl = parentVO.getGlobalbbhl();
		UFDouble groupbbhl = parentVO.getGroupbbhl();
		String bzbm = parentVO.getBzbm();
		UFDate djrq = parentVO.getDjrq();
		String zfdwbm = parentVO.getPk_org();
		String pk_group = parentVO.getPk_group();

		if(items==null || items.length==0 || bxcontrastVOs==null || bxcontrastVOs.length==0)
			return contrastVos;

		String pk_jkbx = items[0].getPk_jkbx();
		
		for (BxcontrastVO vo:bxcontrastVOs) {
			vo.setPk_bxd(pk_jkbx);
			if(vo.getHkybje()==null)
				vo.setHkybje(vo.getCjkybje().sub(vo.getFyybje()));
			vo.setBxdjbh(parentVO.getDjbh());
			if(parentVO.getDjzt() == BXStatusConst.DJZT_TempSaved){
				vo.setSxbz(BXStatusConst.SXBZ_TEMP);
			}else {
				vo.setSxbz(BXStatusConst.SXBZ_NO);
			}
		}
		
		//处理业务行还款金额与冲借款行还款金额不一致问题
		dealMultipModify(bxcontrastVOs, items);
		
		List<BxcontrastVO> contrastVosNew = null;
		if(items.length==1){ //单行业务信息
			for (BxcontrastVO vo:bxcontrastVOs) {
				vo.setPk_finitem(items[0].getPrimaryKey());
				contrastVos.add(vo);
			}
						
			//合并相同来源和目的的冲销信息
			contrastVosNew = combineSameContrasts(contrastVos);
		}else{ //多行财务信息
			List<BXBusItemVO> busItemList=new ArrayList<BXBusItemVO>();
			for (BXBusItemVO item : items) {
				busItemList.add((BXBusItemVO) item.clone());
			}
			
			//多行财务信息，财务信息内容和冲销信息的分配
			contrastVosNew = dealMultiDistribute(bxcontrastVOs, contrastVos, busItemList);
		}
		
		//重新设置冲借款信息的本币金额
		resetCjkJeToBB(bbhl, globalbbhl, groupbbhl, bzbm, djrq, zfdwbm, pk_group,contrastVosNew);
		return contrastVosNew;
	}
	
	/**
	 * 本段代码整体思路<br>
	 * 1、将带还款金额的业务行重新分配，生成带还款金额的冲销行集合（处理后业务行中的还款和对应的冲借款均减掉）<br>
	 * 2、将带冲借款金额的业务行重新分配，生成包含冲销金额和原币金额的冲销行集合
	 * @param bxcontrastVOs
	 * @param contrastVos
	 * @param busitems 报销财务行
	 * @return
	 */
	private static List<BxcontrastVO> dealMultiDistribute(BxcontrastVO[] bxcontrastVOs, List<BxcontrastVO> contrastVos,
			List<BXBusItemVO> busitems) {
		for (BXBusItemVO itemVO : busitems) {
			UFDouble cjkybje = itemVO.getCjkybje();
			UFDouble hkybje = itemVO.getHkybje();

			if (cjkybje.compareTo(UFDouble.ZERO_DBL) == 0)
				continue;

			if (hkybje.compareTo(UFDouble.ZERO_DBL) != 0) { // 带还款的行
				//将业务行中的还款金额平分，并生成新的冲借款集合 ，集合中未包含还款金额的行（原币为0，冲借款等于还款）
				for (int i = 0; i < bxcontrastVOs.length; i++) {

					cjkybje = itemVO.getCjkybje();
					hkybje = itemVO.getHkybje();

					if (itemVO.getHkybje().compareTo(UFDouble.ZERO_DBL) == 0) {
						break;
					}

					BxcontrastVO bxcontrastVO = bxcontrastVOs[i];
					UFDouble cjkybje2 = bxcontrastVO.getCjkybje();
					UFDouble hkybje2 = bxcontrastVO.getHkybje();
					if (cjkybje2.compareTo(UFDouble.ZERO_DBL) == 0)
						continue;
					if (hkybje2.compareTo(UFDouble.ZERO_DBL) != 0) {
						UFDouble clje = hkybje.compareTo(hkybje2) > 0 ? hkybje2 : hkybje;
						BxcontrastVO vonew = (BxcontrastVO) bxcontrastVO.clone();
						vonew.setHkybje(clje);
						vonew.setCjkybje(clje);
						vonew.setPk_finitem(itemVO.getPrimaryKey());
						vonew.setPk_bxd(itemVO.getPk_jkbx());
						vonew.setFyybje(UFDouble.ZERO_DBL);
						contrastVos.add(vonew);
						
						// 扣减金额
						itemVO.setHkybje(itemVO.getHkybje().sub(clje));
						itemVO.setCjkybje(itemVO.getCjkybje().sub(clje));
						bxcontrastVO.setHkybje(bxcontrastVO.getHkybje().sub(clje));
						bxcontrastVO.setCjkybje(bxcontrastVO.getCjkybje().sub(clje));
					}

				}
			}
		}
		
		//将冲借款的行计算出来
		for (BXBusItemVO itemVO : busitems) {
			for (int i = 0; i < bxcontrastVOs.length; i++) {

				UFDouble cjkybje = itemVO.getCjkybje();

				if (cjkybje.compareTo(UFDouble.ZERO_DBL) == 0)
					break;

				BxcontrastVO bxcontrastVO = bxcontrastVOs[i];
				UFDouble cjkybje2 = bxcontrastVO.getCjkybje();

				if (cjkybje2.compareTo(UFDouble.ZERO_DBL) == 0)
					continue;

				UFDouble clje = cjkybje.compareTo(cjkybje2) > 0 ? cjkybje2 : cjkybje;
				BxcontrastVO vonew = (BxcontrastVO) bxcontrastVO.clone();
				vonew.setHkybje(UFDouble.ZERO_DBL);
				vonew.setCjkybje(clje);
				vonew.setPk_finitem(itemVO.getPrimaryKey());
				vonew.setPk_bxd(itemVO.getPk_jkbx());
				vonew.setFyybje(clje);
				contrastVos.add(vonew);
				// 扣减金额
				itemVO.setCjkybje(itemVO.getCjkybje().sub(clje));
				bxcontrastVO.setCjkybje(bxcontrastVO.getCjkybje().sub(clje));
			}
		}

		List<BxcontrastVO> contrastVosNew = combineSameContrasts(contrastVos);

		return contrastVosNew;
	}

	private static List<BxcontrastVO> combineSameContrasts(List<BxcontrastVO> contrastVos) {
		Map<String,BxcontrastVO> map=new HashMap<String, BxcontrastVO>();
		for(BxcontrastVO cvo:contrastVos){
			String key=cvo.getPk_finitem()+cvo.getPk_jkd() + cvo.getPk_busitem();
			if(map.containsKey(key)){
				BxcontrastVO bxcontrastVO = map.get(key);
				bxcontrastVO.setCjkybje(bxcontrastVO.getCjkybje().add(cvo.getCjkybje()));
				bxcontrastVO.setHkybje(bxcontrastVO.getHkybje().add(cvo.getHkybje()));
				bxcontrastVO.setFyybje(bxcontrastVO.getFyybje().add(cvo.getFyybje()));
				map.put(key, bxcontrastVO);
			}else{
				map.put(key, cvo);
			}
		}

		List<BxcontrastVO> contrastVosNew=new ArrayList<BxcontrastVO>();
		contrastVosNew.addAll(map.values());
		return contrastVosNew;
	}
	
	/**
	 * 处理冲销行总还款金额与业务行总还款金额不一致问题<br>
	 * 处理原则为：以业务行还款金额为准
	 * @param bxcontrastVOs
	 * @param busItems
	 */
	private static void dealMultipModify(BxcontrastVO[] bxcontrastVOs, BXBusItemVO[] busItems) {
		UFDouble contrastHkSum = UFDouble.ZERO_DBL;//冲借款信息中还款总金额
		UFDouble busItemHkYbSum = UFDouble.ZERO_DBL;//业务行还款总金额
		
		for (BxcontrastVO vo : bxcontrastVOs) {
			contrastHkSum = contrastHkSum.add(vo.getHkybje());
		}
		for (BXBusItemVO vo : busItems) {
			busItemHkYbSum = busItemHkYbSum.add(vo.getHkybje());
		}
		
		if (busItemHkYbSum.equals(contrastHkSum)){
			return;
		}
		
		for (BxcontrastVO vo : bxcontrastVOs) {
			UFDouble contrastHkje = UFDouble.ZERO_DBL;
			if (busItemHkYbSum.compareTo(UFDouble.ZERO_DBL) > 0) {
				if (busItemHkYbSum.compareTo(vo.getCjkybje()) > 0) {
					contrastHkje = vo.getCjkybje();
				} else {
					contrastHkje = busItemHkYbSum;
				}
				busItemHkYbSum = busItemHkYbSum.sub(contrastHkje);
			}
			vo.setHkybje(contrastHkje);
			vo.setFyybje(vo.getCjkybje().sub(vo.getHkybje()));
		}
	}

	public static void resetCjkJeToBB(UFDouble bbhl, UFDouble globalhl, UFDouble grouphl, String bzbm, UFDate djrq, String zfdwbm, String pk_group, List<BxcontrastVO> contrastVosNew ) {
		for(BxcontrastVO cvo:contrastVosNew){
			try{
				UFDouble cjkbbje = Currency.computeYFB(zfdwbm,Currency.Change_YBJE, bzbm, cvo.getCjkybje(), null, null, null, bbhl, djrq)[2];
				cvo.setCjkbbje(cjkbbje);
				UFDouble[] money = Currency.computeGroupGlobalAmount(cvo.getCjkybje(), cjkbbje, bzbm, djrq, cvo.getPk_org(), pk_group, globalhl, grouphl);
				cvo.setGroupcjkbbje(money[0]);
				cvo.setGlobalcjkbbje(money[1]);
				UFDouble fybbje = Currency.computeYFB(zfdwbm,Currency.Change_YBJE, bzbm, cvo.getFyybje(), null, null, null, bbhl, djrq)[2];
				cvo.setFybbje(fybbje);
				UFDouble[] money2 = Currency.computeGroupGlobalAmount(cvo.getFyybje(), fybbje, bzbm, djrq, cvo.getPk_org(),pk_group, globalhl, grouphl);
				cvo.setGroupfybbje(money2[0]);
				cvo.setGlobalfybbje(money2[1]);
			}catch (Exception e) {
				throw new BusinessRuntimeException(nc.vo.ml.NCLangRes4VoTransl.getNCLangRes().getStrByID("2011","UPP2011-000387")/*@res "按照汇率计算金额失败!"*/);
			}
			cvo.setYbje(cvo.getCjkybje());
			cvo.setBbje(cvo.getCjkbbje());
			//增加集团和全局的本币金额的处理 
			cvo.setGlobalbbje(cvo.getGlobalcjkbbje());
			cvo.setGroupbbje(cvo.getGroupcjkbbje());
		}
	}
}