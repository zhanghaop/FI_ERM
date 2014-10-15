/**
 * @(#)IBtnManager.java	V5.0 2006-2-23
 *
 * Copyright 1988-2005 UFIDA, Inc. All Rights Reserved.
 *
 * This software is the proprietary information of UFSoft, Inc.
 * Use is subject to license terms.
 *
 */

package nc.ui.er.plugin;

import nc.ui.er.component.ExButtonObject;

/**
 * <p>
 *   类的主要说明。类设计的目标，完成什么样的功能。
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
 * 			<li><strong>修改日期：</strong>2006-2-23</li>
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

public interface IBtnManager {
    /**按钮被触发事件监听*/
 void onBtnClicked(ExButtonObject btn);
}



