package nc.bs.arap.bx;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import nc.bs.er.pub.ArapResultSetProcessor;
import nc.bs.er.pub.IRSChecker;
import nc.bs.framework.common.NCLocator;
import nc.pubitf.fip.service.IFipBillQueryService;
import nc.pubitf.fip.service.IFipRelationQueryService;
import nc.vo.arap.bx.util.BXConstans;
import nc.vo.arap.bx.util.BXQueryUtil;
import nc.vo.ep.bx.JKBXHeaderVO;
import nc.vo.fip.external.FipExtendAggVO;
import nc.vo.fip.service.FipBasicRelationVO;
import nc.vo.fip.service.FipRelationInfoVO;
import nc.vo.gl.pubvoucher.VoucherVO;
import nc.vo.pub.BusinessException;
import nc.vo.pub.CircularlyAccessibleValueObject;

/**
 * @author twei
 *
 * nc.bs.arap.bx.VoucherRsChecker
 * 
 * 
 * ResultSet 检查类， 配合ArapResultSetProcessor对查询数据进行二次的过滤或修改
 * 
 * 包含逻辑
 * 1. 是否显示凭证号
 * 2. 根据选择的凭证状态对查询结果进行二次过滤
 * 
 * @see IRSChecker
 * @see ArapResultSetProcessor
 */
public class VoucherRsChecker implements IRSChecker {
	/**
	 * 未生成凭证标记
	 */
	public static int VOUCHER_NOTEXIST_FLAG = 0;
	/**
	 *已经生成凭证标记 
	 */
	public static int VOUCHER_EXIST_FLAG = 1;
	/**
	 * 生成凭证且记帐标记
	 */
	public static int VOUCHER_EXIST_JZ_FLAG = 2;

	/**
	 * 是否显示凭证号
	 */
	boolean linkPz = false;
	
	Integer[] voucherFlag;

	public VoucherRsChecker(boolean linkPz, Integer[] flag) {
		this.linkPz = linkPz;
		this.voucherFlag = flag;
	}

	public boolean isReslut(Object obj) throws SQLException {

		if (!(obj instanceof JKBXHeaderVO))
			return false;

		return checkGLInfo((JKBXHeaderVO) obj, linkPz, voucherFlag);

	}

	private boolean checkGLInfo(JKBXHeaderVO vHeaderVO, boolean linkPz, Integer[] voucherFlag2) throws SQLException {
		return false;
	}
	
	/**
	 * 封装成集合类型，便于contains方法调用
	 * 
	 * @author chendya
	 * @return
	 */
	private List<Integer> getVoucherFlags() {
		if (voucherFlag == null || voucherFlag.length == 0) {
			return Arrays.asList(new Integer[0]);
		}
		return Arrays.asList(voucherFlag);
	}

	public CircularlyAccessibleValueObject[] getReslut(
			CircularlyAccessibleValueObject[] vos) throws SQLException {
		int size = getVoucherFlags().size();
		try {
			if (size == 0 || size == 3) {
				// 查询时没有选择凭证状态或全选了凭证状态，直接返回
				return vos;
			} else if (size == 1) {
				// 勾选了一个凭证状态

				int flag = getVoucherFlags().get(0);
				if (flag == VOUCHER_NOTEXIST_FLAG) {
					// 未生成凭证
					return getNotExistVoucherBXVO(vos);
				} else if (flag == VOUCHER_EXIST_FLAG) {
					// 已生成凭证
					return getExistVoucherBXVO(vos);
				} else if (flag == VOUCHER_EXIST_JZ_FLAG) {
					// 已经生成凭证，且记账（pk_manager不为空）
					return getExistJZVoucherBXVO(vos);
				}
			} else if (size == 2) {
				List<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();
				// 勾选了两个凭证状态
				List<Integer> flagList = getVoucherFlags();
				if (flagList.contains(VOUCHER_NOTEXIST_FLAG)
						&& flagList.contains(VOUCHER_EXIST_FLAG)) {
					list.addAll(Arrays.asList(getNotExistVoucherBXVO(vos)));
					list.addAll(Arrays.asList(getExistVoucherBXVO(vos)));
				} else if (flagList.contains(VOUCHER_EXIST_FLAG)
						&& flagList.contains(VOUCHER_EXIST_JZ_FLAG)) {
					list.addAll(Arrays.asList(getExistVoucherBXVO(vos)));
					list.addAll(Arrays.asList(getExistJZVoucherBXVO(vos)));
				} else if (flagList.contains(VOUCHER_NOTEXIST_FLAG)
						&& flagList.contains(VOUCHER_EXIST_JZ_FLAG)) {
					list.addAll(Arrays.asList(getExistVoucherBXVO(vos)));
					list.addAll(Arrays.asList(getExistJZVoucherBXVO(vos)));
				}
				return (CircularlyAccessibleValueObject[]) list
						.toArray(new CircularlyAccessibleValueObject[0]);
			}
		} catch (BusinessException e) {
			throw new SQLException(e);
		}
		return vos;
	}

	/**
	 * 返回未生成凭证的单据VO
	 * 
	 * @author chendya
	 * @return
	 */
	private JKBXHeaderVO[] getNotExistVoucherBXVO(
			CircularlyAccessibleValueObject[] vos) throws BusinessException {
		List<CircularlyAccessibleValueObject> retList = new ArrayList<CircularlyAccessibleValueObject>();
		IFipRelationQueryService service = NCLocator.getInstance().lookup(
				IFipRelationQueryService.class);
		for (CircularlyAccessibleValueObject vo : vos) {
			FipRelationInfoVO[] fipRelVOs = service
					.queryDesBill(preBXLinkFipCondVo((JKBXHeaderVO) vo));
			if (fipRelVOs == null || fipRelVOs.length == 0) {
				retList.add(vo);
			}
		}
		return (JKBXHeaderVO[]) retList.toArray(new JKBXHeaderVO[0]);
	}

	/**
	 * 返回已生成凭证的单据VO
	 * 
	 * @author chendya
	 * @return
	 */
	private JKBXHeaderVO[] getExistVoucherBXVO(
			CircularlyAccessibleValueObject[] vos) throws BusinessException {
		List<CircularlyAccessibleValueObject> retList = new ArrayList<CircularlyAccessibleValueObject>();
		IFipRelationQueryService service = NCLocator.getInstance().lookup(
				IFipRelationQueryService.class);
		IFipBillQueryService billQryService = NCLocator.getInstance().lookup(
				IFipBillQueryService.class);
		for (CircularlyAccessibleValueObject vo : vos) {
			FipRelationInfoVO[] fipRelVOs = service
					.queryDesBill(preBXLinkFipCondVo((JKBXHeaderVO) vo));
			if (fipRelVOs != null && fipRelVOs.length > 0) {
				if(linkPz){
					//如果需要显示凭证号
					StringBuffer buf = new StringBuffer();
					FipExtendAggVO[] aggVO = billQryService.queryDesBill(fipRelVOs);
					if (aggVO != null && aggVO.length > 0) {
						for (int i = 0; i < aggVO.length; i++) {
							Object value = aggVO[i].getBillVO();
							if (value != null && value instanceof VoucherVO
									&&((VoucherVO) value).getNo()!=null) {
								if(buf.length()>0){
									buf.append(",").append(((VoucherVO) value).getNo());
								}else{
									buf.append(((VoucherVO) value).getNo());
								}
							}
						}
					}
					if(buf.length()>0){
						//设置凭证号
						((JKBXHeaderVO)vo).setVoucher(buf.toString());
					}
				}
				retList.add(vo);
			}
		}
		return (JKBXHeaderVO[]) retList.toArray(new JKBXHeaderVO[0]);
	}

	/**
	 * 返回已生成记账凭证的单据VO
	 * 
	 * @author chendya
	 * @return
	 */
	private JKBXHeaderVO[] getExistJZVoucherBXVO(
			CircularlyAccessibleValueObject[] vos) throws BusinessException {
		List<CircularlyAccessibleValueObject> retList = new ArrayList<CircularlyAccessibleValueObject>();
		IFipRelationQueryService service = NCLocator.getInstance().lookup(
				IFipRelationQueryService.class);
		IFipBillQueryService billQryService = NCLocator.getInstance().lookup(
				IFipBillQueryService.class);
		for (CircularlyAccessibleValueObject vo : vos) {
			FipRelationInfoVO[] fipRelVOs = service
					.queryDesBill(preBXLinkFipCondVo((JKBXHeaderVO) vo));
			List<VoucherVO> voucherList = new ArrayList<VoucherVO>();
			if (fipRelVOs != null && fipRelVOs.length > 0) {
				FipExtendAggVO[] aggVO = billQryService.queryDesBill(fipRelVOs);
				if (aggVO != null && aggVO.length > 0) {
					for (int i = 0; i < aggVO.length; i++) {
						Object value = aggVO[i].getBillVO();
						if (value != null && value instanceof VoucherVO) {
							boolean flag = ((VoucherVO) value).getPk_manager() != null ? true : false;
							if (flag) {
								voucherList.add((VoucherVO) value);
							}
						}
					}
				}
			}
			if (voucherList.size()>0) {
				if(linkPz){
					//记录凭证号
					StringBuffer buf = new StringBuffer();
					for (VoucherVO voucherVO : voucherList) {
						if (voucherVO.getNo() != null) {
							if (buf.length() > 0) {
								buf.append(",").append(voucherVO.getNo());
							} else {
								buf.append(voucherVO.getNo());
							}
						}
					}
					if(buf.length()>0){
						((JKBXHeaderVO)vo).setVoucher(buf.toString());
					}
				}
				retList.add(vo);
			}
		}
		return (JKBXHeaderVO[]) retList.toArray(new JKBXHeaderVO[0]);
	}

	/**
	 * 为报销联查凭证准备关联信息
	 * @author chendya
	 * @param headerVO
	 * @return
	 */
	private FipRelationInfoVO preBXLinkFipCondVo(JKBXHeaderVO headerVO) {
		FipRelationInfoVO vo = new FipRelationInfoVO();
		vo.setPk_group(headerVO.getPk_group());
		vo.setPk_org(headerVO.getPk_payorg());
		vo.setRelationID(headerVO.getPk());
		vo.setPk_billtype(headerVO.getDjlxbm());
		return vo;
	}
}
