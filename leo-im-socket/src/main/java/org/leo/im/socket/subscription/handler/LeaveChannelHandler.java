package org.leo.im.socket.subscription.handler;

import org.leo.im.api.dto.MessageDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.MessageService;
import org.leo.im.service.support.ServiceProxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 * 离开频道处理器
 * 
 * @author Leo
 * @date 2018/6/12
 */
public class LeaveChannelHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject data = JSON.parseObject(message);
        super.setGroups(data.getString("channelId"));

        JSONObject sendMessage = new JSONObject();
        sendMessage.put("action", data.getString("action"));
        sendMessage.put("channelId", data.getString("channelId"));
        sendMessage.put("userId", data.getString("userId"));
        sendMessage.put("userNickname", data.getString("userNickname"));
        super.handleMessage(sendMessage.toJSONString());

        if (data.getString("channelType").equals("G")) {
            // 发送系统消息
            long createAt = new java.util.Date().getTime();
            MessageDTO dto = new MessageDTO();
            dto.setId(System.nanoTime() + ((long) (Math.random() * 9 + 1) * 10000));
            dto.setChannelType("G");
            dto.setType("system_add_to_channel");
            dto.setContent(data.getString("userNickname") + " 离开该频道");
            dto.setChannelId(data.getString("channelId"));
            dto.setSenderFirstLetterOfName("L");
            dto.setSenderNickname("系统用户");
            dto.setSenderId("00000000000000000000000000000000");
            dto.setCreateAt(createAt);

            MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
            serviceProxy.saveMessage(dto);
        }
    }

}
