package org.leo.im.socket.subscription.handler;

import java.util.ArrayList;
import java.util.List;

import org.leo.im.api.dto.MessageDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.MessageService;
import org.leo.im.service.support.ServiceProxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * 加入频道消息处理器
 * 
 * @author Leo
 * @date 2018/5/28
 */
public class JoinChannelHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        JSONObject json = JSON.parseObject(message);
        JSONArray userIdArray = json.getJSONArray("userIds");
        String[] userIds = new String[userIdArray.size()];
        for (int i = 0; i < userIds.length; i++) {
            userIds[i] = userIdArray.getJSONObject(i).getString("id");
        }
        super.setUsers(userIds);

        JSONObject sendMessage = new JSONObject();
        sendMessage.put("action", json.getString("action"));
        sendMessage.put("channelId", json.getString("channelId"));
        super.handleMessage(sendMessage.toJSONString());

        if (json.getString("channelType").equals("G")) {
            // 发送系统消息
            JSONArray userNicknames = json.getJSONArray("userNicknames");
            List<MessageDTO> dtos = new ArrayList<>(userNicknames.size());
            long createAt = new java.util.Date().getTime();
            for (int i = 0; i < userNicknames.size(); i++) {
                MessageDTO dto = new MessageDTO();
                dto.setId(System.nanoTime() + ((long) (Math.random() * 9 + 1) * 10000));
                dto.setChannelType("G");
                dto.setType("system_add_to_channel");
                dto.setContent(userNicknames.getJSONObject(i).getString("nickname") + " 被 " + json.getString("admin") + " 加入到该频道");
                dto.setChannelId(json.getString("channelId"));
                dto.setSenderFirstLetterOfName("L");
                dto.setSenderNickname("系统用户");
                dto.setSenderId("00000000000000000000000000000000");
                dto.setCreateAt(createAt);
                dtos.add(dto);
            }

            MessageService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createMessageService());
            serviceProxy.saveMessage(dtos);
        }
    }

}
