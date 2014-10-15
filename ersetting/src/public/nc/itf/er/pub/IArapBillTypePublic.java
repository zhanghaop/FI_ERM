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
 *   收付报系统单据类型公共接口。
 * </p>
 * <p>
 * <Strong>主要的类使用：</Strong>
 *  <ul>
 * 		<li>如何使用该类</li>
 *      <li>是否线程安全</li>
 * 		<li>并发性要求</li>
 * 		<li>使用约束</li>
 * 		<li>其他</li>
 * </ul>
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2005-11-9</li>
 * 			<li><strong>修改内容：<strong></li>
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
 *   功能：根据公司主键查询所有单据类型。如果公司主键为null查询所有公司的单据类型
 * </p>
 * <p>
 *    使用前提
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2005-11-9</li>
 * 			<li><strong>修改内容：<strong></li>
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
 * @param pk_corp 公司主键
 * @return 返回指定公司的所有单据类型vo数组
 */
DjLXVO[] queryAllBillTypeByCorp(String pk_corp) throws BusinessException;
/**
 * <p>
 *   功能:删除指定单据类型，删除失败（例如单据类型已经录入了单据）抛出异常，
 * 		  调用者处理异常
 * </p>
 * <p>
 *    使用前提
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2005-11-9</li>
 * 			<li><strong>修改内容：<strong></li>
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
 * @param billtypeVo 单据类型vo
 * @throws BusinessException
 */
void deleteBillType(BillTypeVO billtypeVo) throws BusinessException;
/**
 * <p>
 *   功能:插入单据类型以及单据类型在相应系统分配的模板信息
 * </p>
 * <p>
 *    使用前提
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2005-11-9</li>
 * 			<li><strong>修改内容：<strong></li>
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
 * @param billtypeVo 单据类型vo
 * @return 所插入单据类型vo
 * @throws BusinessException
 */
BillTypeVO insertBillType(BillTypeVO billtypevo) throws BusinessException;
/**
 * <p>
 *   功能：根据单据类型编码和公司pk，得到相对应的单据类型vo,
 * 			如果单据类型没有分配到指定公司，返回null
 * </p>
 * <p>
 *    使用前提
 * </p>
 * <p>
 * <Strong>已知的BUG：</Strong>
 * 	<ul>
 * 		<li></li>
 *  </ul>
 * </p>
 * 
 * <p>
 * <strong>修改历史：</strong>
 * 	<ul>
 * 		<li><ul>
 * 			<li><strong>修改人:</strong>st</li>
 * 			<li><strong>修改日期：</strong>2005-11-9</li>
 * 			<li><strong>修改内容：<strong></li>
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
 * @param billTypeCode 单据类型编码
 * @param pk_corp 公司pk
 * @return 单据类型vo
 * @throws BusinessException
 */
DjLXVO getDjlxvoByDjlxbm(String billTypeCode,String pk_corp) throws BusinessException;
public DjLXVO[] queryByWhereStr(String where) throws BusinessException;
}



