package org.leo.im.socket;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.ImmediateEventExecutor;

/**
 * 通道持有者类
 * 
 * @author Leo
 * @date 2018/3/29
 */
public class ChannelsHolder {
    
    private static final Map<String, Channel> CHANNELS = new ConcurrentHashMap<>(1024);
    
    /**
     * ChannelGroup
     * group会自动监测里面的channel，当channel断开时，会主动踢出该channel，永远保留当前可用的channel列表 。
     */
    private static final Map<String, ChannelGroup> CHANNEL_GROUPS = new ConcurrentHashMap<>(100);
   
    /**
     * 用户和Channel对应关系哈希Map
     */
    private static final Map<String, ChannelIdSet> USER_CHANNEL_IDS = new ConcurrentHashMap<>(1024);
    
    /**
     * Channel和用户对应关系哈希Map
     */
    private static final Map<String, String> CHANNEL_USER_ID = new ConcurrentHashMap<>(1024);
    
    /**
     * Channel和IM频道的的对应关系哈希Map
     */
//    private static final Map<String, String> CHANNEL_IM_CHANNELS = new ConcurrentHashMap<>(1024);
    
    public static Map<String, ChannelGroup> getChannelGroups() {
        return CHANNEL_GROUPS;
    }
    
    /**
     * 添加Channel
     * @param userId
     * @param channel
     */
    public static void addChannel(String userId, Channel channel) {
        if(CHANNEL_GROUPS.containsKey("all")) {
            CHANNEL_GROUPS.get("all").add(channel);
        }
        String channelId = channel.id().asShortText();
        CHANNELS.putIfAbsent(channelId, channel);
        ChannelIdSet channels = new ChannelIdSet();
        channels.add(channelId);
        ChannelIdSet returnSet = USER_CHANNEL_IDS.putIfAbsent(userId, channels);
        if(returnSet != null) {
            returnSet.add(channelId);
        }
        
        CHANNEL_USER_ID.putIfAbsent(channelId, userId);
    }
    
    /**
     * 删除Channel
     * @param channelId
     */
    public static void removeChannel(String channelId) {
        CHANNELS.remove(channelId);
        String currentUserId = CHANNEL_USER_ID.get(channelId);
        if(currentUserId != null) {
            ChannelIdSet userIds = USER_CHANNEL_IDS.get(currentUserId);
            if(userIds != null) {
                if(userIds.remove(channelId)) {
                    if(userIds.size() == 0) {
                        USER_CHANNEL_IDS.remove(currentUserId);
                    }
                }
            }
        }
        CHANNEL_USER_ID.remove(channelId);  
    }
    
    /**
     * 根据频道id得到用户id
     * @param channelId
     * @return
     */
    public static String getUserIdByChannelId(String channelId) {
        return CHANNEL_USER_ID.get(channelId);
    }
    
    /**
     * 根据用户id得到channel集合
     * @param userId
     * @return
     */
    public static ChannelIdSet getChannelsByUserId(String userId) {
        return USER_CHANNEL_IDS.get(userId);
    }
    
    /**
     * 根据channel id得到channel
     * @param channelId
     * @return
     */
    public static Channel getChannelById(String channelId) {
        return CHANNELS.get(channelId);
    }
    
    /**
     * 将channel添加到GroupChannel中
     * @param groupId
     * @param channel
     */
    public static void addChannelToGroup(String groupId, Channel channel) {
        DefaultChannelGroup channelGroup = new DefaultChannelGroup(ImmediateEventExecutor.INSTANCE);
        ChannelGroup returnChannelGroup = CHANNEL_GROUPS.putIfAbsent(groupId, channelGroup);
        if(returnChannelGroup == null) {
            // 不存在该ChannelGroup，第一次添加。
            channelGroup.add(channel);
            return;
        }
        // ChannelGroup已经存在
        returnChannelGroup.add(channel);
    }
    
    /**
     * 添加IM Channel Id
     * @param channelId
     * @param IMChannelId
     */
//    public static void addIMChannel(String channelId, String IMChannelId) {
//        CHANNEL_IM_CHANNELS.put(channelId, IMChannelId);
//    }
    
    /**
     * 移除IM Channel Id
     * @param channelId
     */
//    public static void removeIMChannel(String channelId) {
//        CHANNEL_IM_CHANNELS.remove(channelId);
//    }
    
    /**
     * 得到所用用户id
     * @return
     */
    public static Set<String> getUserIds() {
        return USER_CHANNEL_IDS.keySet();
    }

}
