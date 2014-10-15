/**
 * @(#)IArapBillTypePublic.java	V5.0 2005-11-9
 *
 * Copyright 1988-2005 UFIDA, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of UFSoft, Inc.
 * Use is subject to license terms.
 *
 */

package nc.itf.er.pub;

import nc.vo.er.djlx.BillTypeVO;
import nc.vo.er.djlx.DjLXVO;
import nc.vo.pub.BusinessException;

/**
 * <p>
 *   �ո���ϵͳ�������͹����ӿڡ�
 * </p>
 * <p>
 * <Strong>��Ҫ����ʹ�ã�</Strong>
 *  <ul>
 * 		<li>���ʹ�ø���</li>
 *      <li>�Ƿ��̰߳�ȫ</li>
 * 		<li>������Ҫ��</li>
 * 		<li>ʹ��Լ��</li>
 * 		<li>����</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>�޸���:</strong>st</li>
 * 			<li><strong>�޸����ڣ�</strong>2005-11-9</li>
 * 			<li><strong>�޸����ݣ�<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 */


public interface IArapBillTypePublic {

/**
 * <p>
 *   ���ܣ����ݹ�˾������ѯ���е������͡������˾����Ϊnull��ѯ���й�˾�ĵ�������
 * </p>
 * <p>
 *    ʹ��ǰ��
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>�޸���:</strong>st</li>
 * 			<li><strong>�޸����ڣ�</strong>2005-11-9</li>
 * 			<li><strong>�޸����ݣ�<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 * 
 * @param pk_corp ��˾����
 * @return ����ָ����˾�����е�������vo����
 */
DjLXVO[] queryAllBillTypeByCorp(String pk_corp) throws BusinessException;
/**
 * <p>
 *   ����:ɾ��ָ���������ͣ�ɾ��ʧ�ܣ����絥�������Ѿ�¼���˵��ݣ��׳��쳣��
 * 		  �����ߴ����쳣
 * </p>
 * <p>
 *    ʹ��ǰ��
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>�޸���:</strong>st</li>
 * 			<li><strong>�޸����ڣ�</strong>2005-11-9</li>
 * 			<li><strong>�޸����ݣ�<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 * 
 * @param billtypeVo ��������vo
 * @throws BusinessException
 */
void deleteBillType(BillTypeVO billtypeVo) throws BusinessException;
/**
 * <p>
 *   ����:���뵥�������Լ�������������Ӧϵͳ�����ģ����Ϣ
 * </p>
 * <p>
 *    ʹ��ǰ��
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>�޸���:</strong>st</li>
 * 			<li><strong>�޸����ڣ�</strong>2005-11-9</li>
 * 			<li><strong>�޸����ݣ�<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 * @param billtypeVo ��������vo
 * @return �����뵥������vo
 * @throws BusinessException
 */
BillTypeVO insertBillType(BillTypeVO billtypevo) throws BusinessException;
/**
 * <p>
 *   ���ܣ����ݵ������ͱ���͹�˾pk���õ����Ӧ�ĵ�������vo,
 * 			�����������û�з��䵽ָ����˾������null
 * </p>
 * <p>
 *    ʹ��ǰ��
 * </p>
 * <p>
 * <Strong>��֪��BUG��</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>�޸���ʷ��</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>�޸���:</strong>st</li>
 * 			<li><strong>�޸����ڣ�</strong>2005-11-9</li>
 * 			<li><strong>�޸����ݣ�<strong></li>
 * 			</ul>
 * 		</li>
 * 		<li>
 * 		</li>
 *  </ul>
 * </p>
 * 
 * @author st
 * @version V5.0
 * @since V3.1
 * 
 * @param billTypeCode �������ͱ���
 * @param pk_corp ��˾pk
 * @return ��������vo
 * @throws BusinessException
 */
DjLXVO getDjlxvoByDjlxbm(String billTypeCode,String pk_corp) throws BusinessException;
public DjLXVO[] queryByWhereStr(String where) throws BusinessException;
}



