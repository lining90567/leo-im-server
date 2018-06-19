package org.leo.im.socket.subscription.handler;

/**
 * 用户在线状态改变动作处理器
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class OnlineStatusChangedHandler extends AbstractMessageHandler implements MessageHandler {

    /**
     * 处理消息
     * 
     * @param channel
     * @param message
     */
    @Override
    public void doHandle(String channel, String message) {
        super.setGroups("all");
        super.handleMessage(message);
    }

}
