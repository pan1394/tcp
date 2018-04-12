package com.nvwa.framework.communication.tcp2;

import java.util.List;

public interface IMessage {

	boolean SendToClient(List<String> ipList, String replyMessage);

    boolean SendToClient(String ip, String replyMessage);
}
