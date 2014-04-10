// Decompiled by DJ v2.9.9.60 Copyright 2000 Atanas Neshkov  Date: 2013-4-17 11:23:59
// Home Page : http://members.fortunecity.com/neshkov/dj.html  - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TokenProcessorImpl.java

package nc.bs.framework.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import nc.bs.framework.common.InvocationInfoProxy;
import nc.bs.framework.component.ContextAwareComponent;
import nc.bs.framework.core.*;
import nc.bs.framework.core.conf.Configuration;
import nc.bs.framework.exception.FrameworkRuntimeException;
import nc.bs.framework.exception.FrameworkSecurityException;
import nc.bs.framework.server.util.KeyUtil;
import org.granite.lang.util.HexEncoder;

// Referenced classes of package nc.bs.framework.server:
//            ITokenProcessor

public class TokenProcessorImpl
    implements ContextAwareComponent, ITokenProcessor
{

    private byte[] getTokenSeed()
    {
        if(tokenSeed == null)
        {
            String s = ((Container)ctx.getContainer()).getServer().getConfiguration().getTokenSeed();
            if(s == null)
                throw new FrameworkRuntimeException("no token seed");
            HexEncoder e = new HexEncoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            try
            {
                e.decode(s, out);
            }
            catch(IOException e1)
            {
                throw new FrameworkRuntimeException("error tokend seed", e1);
            }
            tokenSeed = out.toByteArray();
        }
        return tokenSeed;
    }

    public void setComponentContext(ComponentContext context)
    {
        ctx = context;
    }

    public TokenProcessorImpl()
    {
    }

    public byte[] genToken(byte sys[], byte origin[])
    {
        if(sys == null)
            throw new FrameworkSecurityException("invalid system(null)");
        if(origin == null)
        {
            throw new FrameworkSecurityException("invalid orginal token(null)");
        } else
        {
            String userCode = InvocationInfoProxy.getInstance().getUserCode() != null ? InvocationInfoProxy.getInstance().getUserCode() : "#UAP#";
            byte userid[] = userCode.getBytes();
            long now = System.currentTimeMillis();
            byte tokenBytes[] = new byte[8 + origin.length + userid.length];
            writeLong(tokenBytes, now);
            System.arraycopy(origin, 0, tokenBytes, 8, origin.length);
            System.arraycopy(userid, 0, tokenBytes, 8 + origin.length, userid.length);
            byte md5[] = KeyUtil.md5(getTokenSeed(), tokenBytes);
            byte nbytes[] = new byte[origin.length + md5.length + 8];
            System.arraycopy(tokenBytes, 0, nbytes, 0, origin.length + 8);
            System.arraycopy(md5, 0, nbytes, 8 + origin.length, md5.length);
            return nbytes;
        }
    }

    public byte[] verifyToken(byte token[])
    {
        if(token == null || token.length < 20)
            return null;
        byte md5[] = new byte[20];
        int encryptLength = token.length - 20;
        System.arraycopy(token, encryptLength, md5, 0, 20);
        String userCode = InvocationInfoProxy.getInstance().getUserCode() != null ? InvocationInfoProxy.getInstance().getUserCode() : "#UAP#";
        byte tbytes[] = getExactToken(userCode, token);
        if(KeyUtil.verifyMD5(getTokenSeed(), tbytes, md5))
            return token;
        String runAsUser = InvocationInfoProxy.getInstance().getRunAs();
        if(runAsUser != null)
        {
            byte tokenRunAs[] = getExactToken(runAsUser, token);
            if(KeyUtil.verifyMD5(getTokenSeed(), tokenRunAs, md5))
                return token;
        }
        byte tokendefaultUser[] = getExactToken("#UAP#", token);
        if(KeyUtil.verifyMD5(getTokenSeed(), tokendefaultUser, md5))
            return token;
        else
            return null;
    }

    private byte[] getExactToken(String user, byte token[])
    {
        int encryptLength = token.length - 20;
        byte userbyte[] = user.getBytes();
        byte tokenBytes[] = new byte[encryptLength + userbyte.length];
        System.arraycopy(token, 0, tokenBytes, 0, encryptLength);
        System.arraycopy(userbyte, 0, tokenBytes, encryptLength, userbyte.length);
        return tokenBytes;
    }

    private final void writeLong(byte writeBuffer[], long v)
    {
        writeBuffer[0] = (byte)(int)(v >>> 56);
        writeBuffer[1] = (byte)(int)(v >>> 48);
        writeBuffer[2] = (byte)(int)(v >>> 40);
        writeBuffer[3] = (byte)(int)(v >>> 32);
        writeBuffer[4] = (byte)(int)(v >>> 24);
        writeBuffer[5] = (byte)(int)(v >>> 16);
        writeBuffer[6] = (byte)(int)(v >>> 8);
        writeBuffer[7] = (byte)(int)(v >>> 0);
    }

    byte tokenSeed[];
    private ComponentContext ctx;
}