package org.leo.im.starter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.UserService;
import org.leo.im.http.HttpServer;
import org.leo.im.migration.FlywayMigration;
import org.leo.im.service.support.ServiceProxy;
import org.leo.im.socket.ChannelsHolder;
import org.leo.im.socket.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Leo IM Starter
 * 
 * @author Leo
 * @date 2018/3/30
 */
public class App {
    
    private final static Logger logger = LoggerFactory.getLogger(App.class);
    
    public static void main(String[] args) {
        // 加载配置文件
        String confHome = System.getProperty("conf.home");
        if(confHome == null || confHome.trim().isEmpty()) {
            logger.error("请在启动参数中设置配置文件所在路径， -Dconf.home");
            return;
        }
        
        // 加载配置文件
        if(confHome.endsWith("/") || confHome.endsWith("\\")) {
            confHome = confHome.substring(0, confHome.length() - 1);
        }
        try {
            loadConfigFile(confHome);
        } catch(IOException e) {
            logger.error("加载配置文件失败，{}", e.getMessage());
            return;
        }
        
        //初始化数据库
        FlywayMigration dbMigration = new FlywayMigration();
        dbMigration.migrate(System.getProperty("db.pool.url"), System.getProperty("db.pool.username"), System.getProperty("db.pool.password"));
        
        String serverTypeConfig = System.getProperty("server.type");
        if(canStartServer(serverTypeConfig, "http")) {
            // 启动Http服务
            Thread httpServerThread = new Thread(() -> {
                try {
                    new HttpServer().start();
                } catch (InterruptedException e) {
                    logger.error("启动Http Server失败，{}", e.getMessage());
                }
            });
            httpServerThread.setName("httpServerThread");
            httpServerThread.start();
        }
        if(canStartServer(serverTypeConfig, "websocket")) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("Clean resources...");
                try {
                    cleanResources();
                    logger.info("Clean resources finished");
                } catch(Exception e) {
                    logger.error(e.getMessage());
                }
            }));  
            
            WebSocketServer wsServer = new WebSocketServer(Integer.getInteger("websocket.port"));
            wsServer.setBossThreads(Integer.getInteger("websocket.boss.threads"));
            wsServer.setWorkerThreads(Integer.getInteger("websocket.worker.threads"));
            // 启动WebSocket服务
            Thread websocketServerThread = new Thread(() -> {
                try {
                    wsServer.start();
                } catch (InterruptedException e) {
                    logger.error("启动WebSocket Server失败，{}", e.getMessage());
                }
            });
            websocketServerThread.setName("websocketServerThread");
            websocketServerThread.start();
        }
    }
    
    /**
     * 加载配置文件
     * @param confHome
     * @throws IOException
     */
    private static void loadConfigFile(String confHome) throws IOException {
        InputStream is = new FileInputStream(confHome + "/app.conf");
        System.getProperties().load(is);
    }
    
    /**
     * 是否可以启动指定类型的服务器
     * @param serverTypeConfig
     * @param targerServer
     * @return
     */
    private static boolean canStartServer(String serverTypeConfig, String targerServer) {
        String[] serverTypes = serverTypeConfig.split(",");
        for(String serverType : serverTypes) {
            if(serverType.equalsIgnoreCase(targerServer)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 清理资源
     */
    private static void cleanResources() {
        Set<String> userIds = ChannelsHolder.getUserIds();
        if(userIds != null && userIds.size() > 0) {
            UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
            serviceProxy.batchOffline(userIds);
        }
    }
    
}
