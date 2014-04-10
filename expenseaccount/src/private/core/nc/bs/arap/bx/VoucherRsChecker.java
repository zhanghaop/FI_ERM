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
 * ResultSet ����࣬ ���ArapResultSetProcessor�Բ�ѯ���ݽ��ж��εĹ��˻��޸�
 * 
 * �����߼�
 * 1. �Ƿ���ʾƾ֤��
 * 2. ����ѡ���ƾ֤״̬�Բ�ѯ������ж��ι���
 * 
 * @see IRSChecker
 * @see ArapResultSetProcessor
 */
public class VoucherRsChecker implements IRSChecker {
	/**
	 * δ����ƾ֤���
	 */
	public static int VOUCHER_NOTEXIST_FLAG = 0;
	/**
	 *�Ѿ�����ƾ֤��� 
	 */
	public static int VOUCHER_EXIST_FLAG = 1;
	/**
	 * ����ƾ֤�Ҽ��ʱ��
	 */
	public static int VOUCHER_EXIST_JZ_FLAG = 2;

	/**
	 * �Ƿ���ʾƾ֤��
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
	 * ��װ�ɼ������ͣ�����contains��������
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
				// ��ѯʱû��ѡ��ƾ֤״̬��ȫѡ��ƾ֤״̬��ֱ�ӷ���
				return vos;
			} else if (size == 1) {
				// ��ѡ��һ��ƾ֤״̬

				int flag = getVoucherFlags().get(0);
				if (flag == VOUCHER_NOTEXIST_FLAG) {
					// δ����ƾ֤
					return getNotExistVoucherBXVO(vos);
				} else if (flag == VOUCHER_EXIST_FLAG) {
					// ������ƾ֤
					return getExistVoucherBXVO(vos);
				} else if (flag == VOUCHER_EXIST_JZ_FLAG) {
					// �Ѿ�����ƾ֤���Ҽ��ˣ�pk_manager��Ϊ�գ�
					return getExistJZVoucherBXVO(vos);
				}
			} else if (size == 2) {
				List<CircularlyAccessibleValueObject> list = new ArrayList<CircularlyAccessibleValueObject>();
				// ��ѡ������ƾ֤״̬
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
	 * ����δ����ƾ֤�ĵ���VO
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
	 * ����������ƾ֤�ĵ���VO
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
					//�����Ҫ��ʾƾ֤��
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
						//����ƾ֤��
						((JKBXHeaderVO)vo).setVoucher(buf.toString());
					}
				}
				retList.add(vo);
			}
		}
		return (JKBXHeaderVO[]) retList.toArray(new JKBXHeaderVO[0]);
	}

	/**
	 * ���������ɼ���ƾ֤�ĵ���VO
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
					//��¼ƾ֤��
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
	 * Ϊ��������ƾ֤׼��������Ϣ
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
