package org.leo.im.store.factory;

import org.leo.im.store.dao.ChannelDAO;
import org.leo.im.store.dao.ChannelMemberDAO;
import org.leo.im.store.dao.FileDAO;
import org.leo.im.store.dao.HideChannelDAO;
import org.leo.im.store.dao.MessageDAO;
import org.leo.im.store.dao.UnreadMessageCountDAO;
import org.leo.im.store.dao.UserChannelDAO;
import org.leo.im.store.dao.UserDAO;
import org.leo.im.store.dao.impl.JdbcChannelDAOImpl;
import org.leo.im.store.dao.impl.JdbcChannelMemberDAOImpl;
import org.leo.im.store.dao.impl.JdbcFileDAOImpl;
import org.leo.im.store.dao.impl.JdbcHideChannelDAOImpl;
import org.leo.im.store.dao.impl.JdbcMessageDAOImpl;
import org.leo.im.store.dao.impl.JdbcUnreadMessageCountDAOImpl;
import org.leo.im.store.dao.impl.JdbcUserChannelDAOImpl;
import org.leo.im.store.dao.impl.JdbcUserDAOImpl;

/**
 * dao工厂类
 * 
 * @author Leo
 * @date 2018/3/30
 */
public final class DAOFactory {

    /**
     * 创建UserDAO的实例
     * @return
     */
    public static UserDAO createUserDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcUserDAOImpl();
        default:
            return new JdbcUserDAOImpl();
        }
    }
    
    /**
     * 创建ChannelDAO的实例
     * @return
     */
    public static ChannelDAO createChannelDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcChannelDAOImpl();
        default:
            return new JdbcChannelDAOImpl();
        }
    }
    
    /**
     * 创建ChannelMemberDAO实例
     * @return
     */
    public static ChannelMemberDAO createChannelMemberDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcChannelMemberDAOImpl();
        default:
            return new JdbcChannelMemberDAOImpl();
        }
    }
    
    /**
     * 创建UserChannelDAO的实例
     * @return
     */
    public static UserChannelDAO createUserChannelDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcUserChannelDAOImpl();
        default:
            return new JdbcUserChannelDAOImpl();
        }
    }
    
    /**
     * 创建UnreadMessageCountDAO的实例
     * @return
     */
    public static UnreadMessageCountDAO createUnreadMessageCountDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcUnreadMessageCountDAOImpl();
        default:
            return new JdbcUnreadMessageCountDAOImpl();
        }
    }
    
    /**
     * 创建MessageDAO的实例
     * @return
     */
    public static MessageDAO createMessageDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcMessageDAOImpl();
        default:
            return new JdbcMessageDAOImpl();
        }
    }
    
    /**
     * 创建HideChannelDAO的实例
     * @return
     */
    public static HideChannelDAO createHideChannelDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcHideChannelDAOImpl();
        default:
            return new JdbcHideChannelDAOImpl();
        }
    }
    
    /**
     * 创建FileDAO的实例
     * @return
     */
    public static FileDAO createFileDAO() {
        switch (System.getProperty("dao.type")) {
        case "jdbc":
            return new JdbcFileDAOImpl();
        default:
            return new JdbcFileDAOImpl();
        }
    }

}
