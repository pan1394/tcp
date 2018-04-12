package com.nvwa.framework.communication.tcp2.client;

import java.util.List;

public interface IMessage {
 
     void Send(String sendMssage);

     List<Object> SendRequest(String key, String sendMessage, String result, String resultDetail, String errorMessage, String replyMessage);
    
}
