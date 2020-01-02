package com.iwanol.paypal.third.hangtian;

/**
 * Created by lifeng on 2018/11/20.
 */
public class MySecurity {
    //我司公钥
    private static final String paypalmPublicCertPath = ContextUtil.getValue("paypalmPublicCertPath");
    //商户私钥
    private static final String merPrivateCertPath = ContextUtil.getValue("merPrivateCertPath");
    //商户私钥别名
    private static final String merPrivateCertAlias = ContextUtil.getValue("merPrivateCertAlias");
    //商户私钥密码
    private static final String merPrivateCertPass = ContextUtil.getValue("merPrivateCertPass");
    //敏感信息加密key
    private static final String prekey = ContextUtil.getValue("prekey");
    //批量文件加密key
    private static final String filekey = ContextUtil.getValue("filekey");
    private static PPSecurity ppSecurity = null;
    private MySecurity(){}

    /**只初始化一次,减少内存开销*/
    public static PPSecurity getPPSecurity() throws Exception {
        if(ppSecurity == null){
            synchronized (MySecurity.class){
                if(ppSecurity == null){
                    ppSecurity = new PPSecurity(paypalmPublicCertPath, merPrivateCertPath,merPrivateCertAlias,merPrivateCertPass, prekey, filekey);
                }
            }
        }
        return ppSecurity;
    }
}
