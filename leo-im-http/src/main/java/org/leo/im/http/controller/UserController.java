package org.leo.im.http.controller;

import org.leo.im.api.service.UserService;
import org.leo.im.common.data.Page;
import org.leo.im.http.cache.CacheManagerFactory;
import org.leo.im.http.constant.CacheKeys;
import org.leo.im.http.file.AvatarStorage;
import org.leo.im.http.file.AvatarStorageFactory;

import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.Set;

import org.leo.im.api.dto.UserDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.service.support.ServiceProxy;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.PatchMapping;
import org.leo.web.annotation.PathVariable;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.PutMapping;
import org.leo.web.annotation.RequestBody;
import org.leo.web.annotation.RequestHeader;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RequestParam;
import org.leo.web.annotation.RestController;
import org.leo.web.annotation.UploadFile;
import org.leo.web.multipart.MultipartFile;
import org.leo.web.rest.HttpStatus;
import org.leo.web.rest.ResponseEntity;

import com.alibaba.fastjson.JSONObject;

import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;

/**
 * 用户控制器
 * 
 * @author Leo
 * @date 2018/4/9
 */
@RestController()
@RequestMapping("/users")
public final class UserController extends BaseController {
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable() String id) {
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        UserDTO dto = serviceProxy.getById(id);
        return ResponseEntity.ok(dto);
    }
    
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("X-Token") String token) {
        String userId = this.getSubjectFromJwt(token, "userId");
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        UserDTO dto = serviceProxy.getById(userId);
        return ResponseEntity.ok(dto);
    }
    
    @PostMapping("")
    public ResponseEntity<?> register(FullHttpRequest request, @RequestBody String body) {
        // 检查验证码
        JSONObject json = JSONObject.parseObject(body);
        String verificationCode = json.getString("verificationCode");
        if(verificationCode == null || verificationCode.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("验证码为空");
        }
        String sessionId = getJSessionId(request);
        if(sessionId == null || sessionId.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("无效的请求");
        }
        Object verificationCodeInCache = CacheManagerFactory.getCacheManager().get(CacheKeys.VERIFICATION_CODE_PREFIX + sessionId);
        if(verificationCodeInCache == null || verificationCodeInCache.toString().trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("无效的验证码");
        }
        if(!verificationCode.equals(verificationCodeInCache)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build("无效的验证码");
        }
        
        // 注册用户
        UserDTO dto = new UserDTO();
        dto.setName(json.getString("name"));
        dto.setNickname(json.getString("nickname"));
        dto.setPassword(json.getString("password"));
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        serviceProxy.saveUser(dto);
        return ResponseEntity.created().build();
    }
    
    @GetMapping("")
    public ResponseEntity<Page<UserDTO>> listUser(@RequestParam("name") String name, @RequestParam("limit") int limit,
            @RequestParam("offset") int offset) {
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        Page<UserDTO> result = serviceProxy.listByNameOrNickname(name, limit, offset);
        return ResponseEntity.ok(result);
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> patchUser(@PathVariable() String id, @RequestBody String body) {
        JSONObject json = JSONObject.parseObject(body);
        UserDTO dto = new UserDTO();
        dto.setId(id);
        if(json.containsKey("nickname")) {
            dto.setNickname(json.getString("nickname"));
        }
        if(json.containsKey("password")) {
            dto.setPassword(json.getString("password"));
        }
        if(json.containsKey("locked")) {
            dto.setLocked(json.getBoolean("locked"));
        }
        if(json.containsKey("avatarUrl") && !json.getString("avatarUrl").trim().isEmpty()) {
            dto.setAvatarUrl(json.getString("avatarUrl"));
        }
        if(json.containsKey("lastPostAt")) {
            dto.setLastPostAt(json.getLong("lastPostAt"));
        }
        if(json.containsKey("onlineStatus")) {
            dto.setOnlineStatus(json.getString("onlineStatus"));
        }
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        UserDTO returnDTO = serviceProxy.updateUser(dto, false);
        return ResponseEntity.created(returnDTO);
    }
    
    @PostMapping("/me/avatar")
    public ResponseEntity<String> uploadAvatar(@UploadFile MultipartFile avatar, @RequestHeader("X-Token") String token) {
        String userId = this.getSubjectFromJwt(token, "userId");
        String imageType = "png";
        if("image/jpeg".equalsIgnoreCase(avatar.getFileType())) {
            imageType = "jpg";
        }
        
        // 生成两张图片，尺寸分别为 32x32、36x36、80x80
        AvatarStorage as = AvatarStorageFactory.newInstance();
        as.save(userId, imageType, avatar.getFileData(), 32, 32);
        as.save(userId, imageType, avatar.getFileData(), 36, 36);
        as.save(userId, imageType, avatar.getFileData(), 80, 80);
        return ResponseEntity.created("avatar." + imageType);
    }
    
    @GetMapping("/{id}/avatar")
    public ResponseEntity<RandomAccessFile> getMyAvatar(@PathVariable("id") String userId, @RequestParam("width") int width,
            @RequestParam("height") int height) {
        // 得到用户头像
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        UserDTO dto = serviceProxy.getById(userId);
        if(dto != null) {
            String avatarUrl = dto.getAvatarUrl();
            if(avatarUrl != null && !avatarUrl.trim().isEmpty()) {
                AvatarStorage as = AvatarStorageFactory.newInstance();
                RandomAccessFile raf = as.read(userId, avatarUrl, width, height);
                String mimetype = avatarUrl.toLowerCase().trim().endsWith("png") ? "image/png" : "image/jpeg";
                return ResponseEntity.ok(raf, mimetype);
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/nonChannelMembers")
    public ResponseEntity<Page<UserDTO>> listNonChannelMember(@RequestParam("channelId") String channelId,
            @RequestParam("username") String username, @RequestParam("limit") int limit, @RequestParam("offset") int offset) {
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        Page<UserDTO> result = serviceProxy.listNonMembers(channelId, username, limit, offset);
        return ResponseEntity.ok(result);
    }
    
    @PutMapping("/{userId}/password")
    public ResponseEntity<Integer> changePassword(@PathVariable("userId") String userId, @RequestHeader("X-Token") String token, 
            @RequestBody String body) {
        String currentUserId = this.getSubjectFromJwt(token, "userId");
        if(!userId.equals(currentUserId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        JSONObject data = JSONObject.parseObject(body);
        UserService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserService());
        int count = serviceProxy.updatePassword(userId, data.getString("username"), data.getString("oldPassword"), data.getString("newPassword"));
        return ResponseEntity.ok(count);
    }
    
    /**
     * 从cookie中得到Session Id
     * @return
     */
    private String getJSessionId(FullHttpRequest request) {
        try {
            String cookieStr = request.headers().get("Cookie");
            if(cookieStr == null || cookieStr.trim().isEmpty()) {
                return null;
            }
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            Iterator<Cookie> it = cookies.iterator();

            while (it.hasNext()) {
                Cookie cookie = it.next();
                if (cookie.name().equals(CacheKeys.JSESSIONID)) {
                    if (CacheManagerFactory.getCacheManager().get(cookie.value()) != null) {
                        return cookie.value();
                    }
                }
            }
        } catch (Exception e1) {
            return null;
        }
        return null;
    }

}
