package nc.bs.arap.bx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.itf.fi.pub.Currency;
import nc.pubitf.accperiod.AccountCalendar;
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
				 //FIXME 确认正确否
//				 vo.setCxqj(periodVO.getMonthVO().getMonth());
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
		}

		if(items.length==1){ //单行财务信息
			UFDouble hkSum=UFDouble.ZERO_DBL;
			for (BxcontrastVO vo:bxcontrastVOs) {
				vo.setPk_finitem(items[0].getPrimaryKey());
				contrastVos.add(vo);
				hkSum=hkSum.add(vo.getHkybje());
			}
			
			//单行财务信息，财务信息内容和冲销信息不一致的调整
			dealSingleModify(items, contrastVos, bbhl, globalbbhl, groupbbhl, bzbm, djrq, zfdwbm, pk_group, hkSum);
			
			//合并相同来源和目的的冲销信息
			List<BxcontrastVO> contrastVosNew = combineSameContrasts(contrastVos);
			
			return contrastVosNew;
		}else{ //多行财务信息
			Map<String,BXBusItemVO> finitemMap=new HashMap<String, BXBusItemVO>();
			for (BXBusItemVO item:items) {
				finitemMap.put(item.getPrimaryKey(), (BXBusItemVO) item.clone());
			}

			//多行财务信息，财务信息内容和冲销信息不一致的调整
			dealMultipModify(bxcontrastVOs, items, pk_jkbx);
			
			//多行财务信息，财务信息内容和冲销信息的分配
			List<BxcontrastVO> contrastVosNew = dealMultiDistribute(bxcontrastVOs, items, contrastVos, finitemMap);

			//重新设置冲借款信息的本币金额
			resetCjkJeToBB(bbhl, globalbbhl, groupbbhl, bzbm, djrq, zfdwbm, pk_group,contrastVosNew);

			return contrastVosNew;
		}
	}

	private static List<BxcontrastVO> dealMultiDistribute(BxcontrastVO[] bxcontrastVOs, BXBusItemVO[] finitems, List<BxcontrastVO> contrastVos, Map<String, BXBusItemVO> finitemMap) {
		for (BXBusItemVO itemVO:finitemMap.values()) {

			UFDouble cjkybje = itemVO.getCjkybje();
			UFDouble hkybje = itemVO.getHkybje();

			if(cjkybje.compareTo(UFDouble.ZERO_DBL)==0)
				continue;

			if(hkybje.compareTo(UFDouble.ZERO_DBL)!=0){ //带还款的行
				for (int i=0;i<bxcontrastVOs.length;i++) {

					cjkybje = itemVO.getCjkybje();
					hkybje = itemVO.getHkybje();

					if(itemVO.getHkybje().compareTo(UFDouble.ZERO_DBL)==0){
						break;
					}

					BxcontrastVO bxcontrastVO = bxcontrastVOs[i];
					UFDouble cjkybje2 = bxcontrastVO.getCjkybje();
					UFDouble hkybje2 = bxcontrastVO.getHkybje();
					if(cjkybje2.compareTo(UFDouble.ZERO_DBL)==0)
						continue;
					if(hkybje2.compareTo(UFDouble.ZERO_DBL)!=0){
						UFDouble clje=hkybje.compareTo(hkybje2)>0?hkybje2:hkybje;
						BxcontrastVO vonew = (BxcontrastVO) bxcontrastVO.clone();
						vonew.setHkybje(clje);
						vonew.setCjkybje(clje);
						vonew.setPk_finitem(itemVO.getPrimaryKey());
						vonew.setPk_bxd(itemVO.getPk_jkbx());
						vonew.setFyybje(UFDouble.ZERO_DBL);
						contrastVos.add(vonew);
						//扣减金额
						itemVO.setHkybje(itemVO.getHkybje().sub(clje));
						itemVO.setCjkybje(itemVO.getCjkybje().sub(clje));
						bxcontrastVO.setHkybje(bxcontrastVO.getHkybje().sub(clje));
						bxcontrastVO.setCjkybje(bxcontrastVO.getCjkybje().sub(clje));
					}

				}
			}
		}
		for (BXBusItemVO itemVO:finitemMap.values()) {

			for (int i=0;i<bxcontrastVOs.length;i++) {

				UFDouble cjkybje = itemVO.getCjkybje();

				if(cjkybje.compareTo(UFDouble.ZERO_DBL)==0)
					break;

				BxcontrastVO bxcontrastVO = bxcontrastVOs[i];
				UFDouble cjkybje2 = bxcontrastVO.getCjkybje();

				if(cjkybje2.compareTo(UFDouble.ZERO_DBL)==0)
					continue;

				UFDouble clje=cjkybje.compareTo(cjkybje2)>0?cjkybje2:cjkybje;
				BxcontrastVO vonew = (BxcontrastVO) bxcontrastVO.clone();
				vonew.setHkybje(UFDouble.ZERO_DBL);
				vonew.setCjkybje(clje);
				vonew.setPk_finitem(itemVO.getPrimaryKey());
				vonew.setPk_bxd(itemVO.getPk_jkbx());
				vonew.setFyybje(clje);
				contrastVos.add(vonew);
				//扣减金额
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
			String key=cvo.getPk_finitem()+cvo.getPk_jkd();
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

	private static void dealMultipModify(BxcontrastVO[] bxcontrastVOs, BXBusItemVO[] finitems, String pk_jkbx) {
		UFDouble hkSum=UFDouble.ZERO_DBL;
		for (BxcontrastVO vo:bxcontrastVOs) {
			hkSum=hkSum.add(vo.getHkybje());
		}
		UFDouble hkybSum = UFDouble.ZERO_DBL;
		for (BXBusItemVO vo:finitems) {
			hkybSum=hkybSum.add(vo.getHkybje());
		}
		if(hkybSum.equals(hkSum))
			return;
		
		if(hkybSum.compareTo(hkSum)<0){ //hk金额变小
			UFDouble clje=hkSum.sub(hkybSum);
			for(BxcontrastVO vo:bxcontrastVOs){
				if(vo.getHkybje().equals(UFDouble.ZERO_DBL)){
					continue;
				}else{
					UFDouble hkybje = vo.getHkybje();
					if(hkybje.compareTo(clje)>=0){
						vo.setHkybje(vo.getHkybje().sub(clje));
						vo.setFyybje(vo.getCjkybje().sub(vo.getHkybje()));
						break;
					}else{
						vo.setHkybje(UFDouble.ZERO_DBL);
						vo.setFyybje(vo.getCjkybje());
						clje=clje.sub(hkybje);
					}
				}
			}
		}else{ //还款金额增大
			UFDouble clje=hkybSum.sub(hkSum);
			
			for(BxcontrastVO vo:bxcontrastVOs){
				UFDouble fyybje = vo.getFyybje();
				if(fyybje.compareTo(clje)>=0){
					vo.setHkybje(vo.getHkybje().add(clje));
					vo.setFyybje(vo.getCjkybje().sub(vo.getHkybje()));
					break;
				}else{
					vo.setHkybje(vo.getCjkybje());
					vo.setFyybje(UFDouble.ZERO_DBL);
					clje=clje.sub(fyybje);
				}
			}
		}
		
	}

	private static void dealSingleModify(BXBusItemVO[] finitems, List<BxcontrastVO> contrastVos, UFDouble bbhl, UFDouble globalhl, UFDouble grouphl, String bzbm, UFDate djrq, String zfdwbm, String pk_group, UFDouble hkSum) {
		UFDouble hkybje = finitems[0].getHkybje();
		
		if(!hkSum.equals(hkybje)){  //冲销信息和财务信息的还款金额不一致
			if(contrastVos.size()==1){ //单行冲销信息
				BxcontrastVO bxcontrastVO = contrastVos.get(0);
				bxcontrastVO.setHkybje(hkybje);
				bxcontrastVO.setFyybje(bxcontrastVO.getCjkybje().sub(bxcontrastVO.getHkybje()));
			}else{ //多行冲销信息
				for(BxcontrastVO vo:contrastVos){
					UFDouble thisHkje=UFDouble.ZERO_DBL;
					if(hkybje.compareTo(UFDouble.ZERO_DBL)>0){
						if(hkybje.compareTo(vo.getCjkybje())>0){
							thisHkje=vo.getCjkybje();
						}else{
							thisHkje=hkybje;
						}
						hkybje=hkybje.sub(thisHkje);
					}
					vo.setHkybje(thisHkje);
					vo.setFyybje(vo.getCjkybje().sub(vo.getHkybje()));
				}
			}
			resetCjkJeToBB(bbhl, globalhl, grouphl, bzbm, djrq, zfdwbm, pk_group,contrastVos);
		}
	}

	private static void resetCjkJeToBB(UFDouble bbhl, UFDouble globalhl, UFDouble grouphl, String bzbm, UFDate djrq, String zfdwbm, String pk_group, List<BxcontrastVO> contrastVosNew ) {
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