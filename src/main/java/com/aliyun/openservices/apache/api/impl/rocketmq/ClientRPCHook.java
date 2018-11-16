package com.aliyun.openservices.apache.api.impl.rocketmq;

import org.apache.rocketmq.remoting.protocol.RemotingCommand;
import com.aliyun.openservices.apache.api.impl.authority.AuthUtil;
import com.aliyun.openservices.apache.api.impl.authority.SessionCredentials;

import static com.aliyun.openservices.apache.api.impl.authority.SessionCredentials.AccessKey;
import static com.aliyun.openservices.apache.api.impl.authority.SessionCredentials.ONSChannelKey;
import static com.aliyun.openservices.apache.api.impl.authority.SessionCredentials.SecurityToken;
import static com.aliyun.openservices.apache.api.impl.authority.SessionCredentials.Signature;

public class ClientRPCHook extends AbstractRPCHook {
    private SessionCredentials sessionCredentials;

    public ClientRPCHook(SessionCredentials sessionCredentials) {
        this.sessionCredentials = sessionCredentials;
    }

    public void doBeforeRequest(String remoteAddr, RemotingCommand request) {
        byte[] total = AuthUtil.combineRequestContent(request,
            parseRequestContent(request, sessionCredentials.getAccessKey(),
                sessionCredentials.getSecurityToken(), sessionCredentials.getOnsChannel().name()));
        String signature = AuthUtil.calSignature(total, sessionCredentials.getSecretKey());
        request.addExtField(Signature, signature);
        request.addExtField(AccessKey, sessionCredentials.getAccessKey());
        request.addExtField(ONSChannelKey, sessionCredentials.getOnsChannel().name());

        if (sessionCredentials.getSecurityToken() != null) {
            request.addExtField(SecurityToken, sessionCredentials.getSecurityToken());
        }
    }

    public void doAfterResponse(String remoteAddr, RemotingCommand request, RemotingCommand response) {

    }

}
