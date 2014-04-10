// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2013-2-5 10:08:18
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   SecurityAspect.java

package nc.bs.framework.server;

import java.lang.reflect.Method;
import nc.bs.framework.aop.Behavior;
import nc.bs.framework.aop.ProceedingJoinpoint;
import nc.bs.framework.common.NoProtect;
import nc.bs.framework.comn.NetStreamContext;
import nc.bs.framework.exception.FrameworkSecurityException;

// Referenced classes of package nc.bs.framework.server:
//            ITokenProcessor

public class SecurityAspect
{

    public SecurityAspect(ITokenProcessor tp)
    {
        this.tp = tp;
    }

    public boolean needProcess(Method m)
    {
        return false;
    }

    public Object aroundMethod1(ProceedingJoinpoint pjp)
        throws Throwable
    {
        if(NetStreamContext.getToken() == null)
            throw new FrameworkSecurityException("invalid secrity token(null)");
        if(tp.verifyToken(NetStreamContext.getToken()) == null)
            throw new FrameworkSecurityException("invalid secrity token");
        else
            return pjp.proceed();
    }

    private ITokenProcessor tp;
}