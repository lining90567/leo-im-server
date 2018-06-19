package org.leo.im.http.controller;

import java.util.ArrayList;
import java.util.List;

import org.leo.im.api.dto.UserChannelDTO;
import org.leo.im.api.provider.ServiceFactory;
import org.leo.im.api.service.UserChannelService;
import org.leo.im.http.vo.UserChannelVO;
import org.leo.im.service.support.ServiceProxy;
import org.leo.im.util.BeanUtils;
import org.leo.web.annotation.GetMapping;
import org.leo.web.annotation.PatchMapping;
import org.leo.web.annotation.PathVariable;
import org.leo.web.annotation.PostMapping;
import org.leo.web.annotation.RequestBody;
import org.leo.web.annotation.RequestHeader;
import org.leo.web.annotation.RequestMapping;
import org.leo.web.annotation.RequestParam;
import org.leo.web.annotation.RestController;
import org.leo.web.rest.ResponseEntity;

@RestController
@RequestMapping("/userChannels")
public final class UserChannelController extends BaseController {
    
    @GetMapping("/{userId}")
    public ResponseEntity<List<UserChannelVO>> listUserChannel(@PathVariable("userId") String userId, 
            @RequestParam("limit") int limit) {
        UserChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserChannelService());
        List<UserChannelDTO> dtoList = serviceProxy.listUserChannel(userId, null, limit);
        if(dtoList.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<UserChannelVO>());
        }
        List<UserChannelVO> voList = new ArrayList<>(dtoList.size());
        for(UserChannelDTO dto : dtoList) {
            UserChannelVO vo = new UserChannelVO();
            BeanUtils.copyProperties(dto, vo);
            voList.add(vo);
        }
        return ResponseEntity.ok(voList);
    }
    
    @GetMapping("")
    public ResponseEntity<UserChannelVO> getUserChannel(@RequestParam("userId") String userId, 
            @RequestParam("channelId") String channelId) {
        UserChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserChannelService());
        UserChannelDTO dto = serviceProxy.get(userId, channelId);
        if(dto == null) {
            return ResponseEntity.notFound().build();
        }
        UserChannelVO vo = new UserChannelVO();
        BeanUtils.copyProperties(dto, vo);
        return ResponseEntity.ok(vo);
    }
    
    /**
     * 修改频道显示名称
     * @param body
     * @return
     */
    @PatchMapping("/{channelId}")
    public ResponseEntity<Integer> updateDisplayName(@PathVariable("channelId") String channelId, @RequestBody String displayName, 
            @RequestHeader("X-Token") String token) {
        String userId = this.getSubjectFromJwt(token, "userId");
        UserChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserChannelService());
        int count = serviceProxy.updateDisplayName(channelId, userId, displayName);
        return ResponseEntity.created(count);
    }
    
    /**
     * 隐藏频道
     * @param channelId
     * @param token
     * @return
     */
    @PostMapping("/{channelId}/hiding")
    public ResponseEntity<Integer> hideChannel(@PathVariable("channelId") String channelId, @RequestHeader("X-Token") String token) {
        String userId = this.getSubjectFromJwt(token, "userId");
        UserChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserChannelService());
        int count = serviceProxy.hideChannel(userId, channelId);
        return ResponseEntity.created(count);
    }
    
    /**
     * 根据名称搜索用户频道
     * @param userId
     * @param name
     * @return
     */
    @GetMapping("/{userId}/search")
    public ResponseEntity<List<UserChannelVO>> listUserChannelByName(@PathVariable("userId") String userId, 
            @RequestParam("name") String name) {
        UserChannelService serviceProxy = ServiceProxy.newProxyInstance(ServiceFactory.createUserChannelService());
        List<UserChannelDTO> dtoList = serviceProxy.listByName(userId, name, null);
        if(dtoList.isEmpty()) {
            return ResponseEntity.ok(new ArrayList<UserChannelVO>());
        }
        List<UserChannelVO> voList = new ArrayList<>(dtoList.size());
        for(UserChannelDTO dto : dtoList) {
            UserChannelVO vo = new UserChannelVO();
            BeanUtils.copyProperties(dto, vo);
            voList.add(vo);
        }
        return ResponseEntity.ok(voList);
    }

}
