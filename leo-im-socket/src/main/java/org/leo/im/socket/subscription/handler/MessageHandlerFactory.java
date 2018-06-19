package org.leo.im.socket.subscription.handler;

import org.leo.im.notification.ActionNames;

/**
 * 消息处理器工厂类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class MessageHandlerFactory {

    /**
     * 创建消息处理器
     * 
     * @param messageType
     * @return
     */
    public static MessageHandler createMessageHandler(String messageType) {
        switch (messageType) {
        case ActionNames.ONLINE_STATUS_CHANGED:
            return new OnlineStatusChangedHandler();
        case ActionNames.NICKNAME_CHANGED:
            return new NicknameChangedHandler();
        case ActionNames.AVATAR_CHANGED:
            return new AvatarChangedHandler();
        case ActionNames.NEW_MESSAGE:
            return new NewMessageHandler();
        case ActionNames.READ_MESSAGE:
            return new ReadMessageHandler();
        case ActionNames.CREATE_CHANNEL:
            return new ChannelCreatedHandler();
        case ActionNames.JOIN_CHANNEL:
            return new JoinChannelHandler();
        case ActionNames.MESSAGE_REMOVED:
            return new RemoveMessageHandler();
        case ActionNames.CHANNEL_NAME_CHANGED:
            return new ChannelNameChangedHandler();
        case ActionNames.MEMBERS_COUNT_CHANGED:
            return new MembersCountChangedHandler();
        case ActionNames.REMOVE_FROM_CHANNEL:
            return new RemoveFromChannelHandler();
        case ActionNames.LEAVE_CHANNEL:
            return new LeaveChannelHandler();
        case ActionNames.CHANNEL_REMOVED:
            return new ChannelRemovedHandler();
        }
        return null;
    }

}
